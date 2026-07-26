#!/usr/bin/env python3
"""
Coffee Workshop — Content Surface Audit

Cross-references Java registries against resources (models, textures, lang,
blockstates, recipes, loot tables, trades) to detect:
  - Registered items without item models
  - Registered blocks without blockstates
  - Missing textures (referenced by models but not on disk)
  - Missing language keys (en_us, zh_cn, ja_jp)
  - English placeholder values (key == value)
  - Items/blocks without any survival source
  - Creative tab gaps

Output:
  build/reports/content-surface.md   — human-readable
  build/reports/content-surface.json — machine-readable
"""

import json
import os
import re
import sys
from pathlib import Path
from collections import defaultdict

ROOT = Path(__file__).resolve().parent.parent
SRC = ROOT / "src"
MAIN = SRC / "main"
RESOURCES = MAIN / "resources" / "assets" / "coffeework"
DATA = MAIN / "resources" / "data" / "coffeework"
GEN_DATA = SRC / "generated" / "resources" / "data" / "coffeework"
JAVA = MAIN / "java" / "net" / "langball" / "coffee"

MODELS_ITEM = RESOURCES / "models" / "item"
MODELS_BLOCK = RESOURCES / "models" / "block"
BLOCKSTATES = RESOURCES / "blockstates"
TEXTURES_ITEM = RESOURCES / "textures" / "item"
TEXTURES_BLOCK = RESOURCES / "textures" / "block"
TEXTURES_EFFECT = RESOURCES / "textures" / "mob_effect"
TEXTURES_VILLAGER = RESOURCES / "textures" / "entity" / "villager" / "profession"
SOUNDS_DIR = RESOURCES / "sounds"
LANG_DIR = RESOURCES / "lang"

LANG_FILES = ["en_us.json", "zh_cn.json", "ja_jp.json"]

REPORT_DIR = ROOT / "build" / "reports"
REPORT_DIR.mkdir(parents=True, exist_ok=True)


def parse_registry_entries():
    """Parse Java init files to extract all registered IDs."""
    entries = []
    
    # Parse ModItems.java
    items_file = JAVA / "init" / "ModItems.java"
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        # Match: public static final RegistryObject<Item> NAME = ITEMS.register("id", ...);
        pattern = r'ITEMS\.register\("([^"]+)"'
        for m in re.finditer(pattern, text):
            entries.append({"id": m.group(1), "type": "item", "file": "ModItems.java"})
    
    # Parse ModBlocks.java
    blocks_file = JAVA / "init" / "ModBlocks.java"
    if blocks_file.exists():
        text = blocks_file.read_text(encoding="utf-8")
        pattern = r'BLOCKS\.register\("([^"]+)"'
        for m in re.finditer(pattern, text):
            entries.append({"id": m.group(1), "type": "block", "file": "ModBlocks.java"})
    
    # Parse ModEffects.java
    effects_file = JAVA / "init" / "ModEffects.java"
    if effects_file.exists():
        text = effects_file.read_text(encoding="utf-8")
        pattern = r'EFFECTS\.register\("([^"]+)"'
        for m in re.finditer(pattern, text):
            entries.append({"id": m.group(1), "type": "mob_effect", "file": "ModEffects.java"})
    
    # Parse ModVillagers.java
    villager_file = JAVA / "init" / "ModVillagers.java"
    if villager_file.exists():
        text = villager_file.read_text(encoding="utf-8")
        for m in re.finditer(r'PROFESSIONS\.register\("([^"]+)"', text):
            entries.append({"id": m.group(1), "type": "villager_profession", "file": "ModVillagers.java"})
        for m in re.finditer(r'POI_TYPES\.register\("([^"]+)"', text):
            entries.append({"id": m.group(1), "type": "poi_type", "file": "ModVillagers.java"})
    
    # Parse ModSounds.java
    sounds_file = JAVA / "init" / "ModSounds.java"
    if sounds_file.exists():
        text = sounds_file.read_text(encoding="utf-8")
        pattern = r'SOUNDS\.register\("([^"]+)"'
        for m in re.finditer(pattern, text):
            entries.append({"id": m.group(1), "type": "sound_event", "file": "ModSounds.java"})
    
    # Parse ModCreativeTabs.java for tab items
    tab_file = JAVA / "init" / "ModCreativeTabs.java"
    creative_items = set()
    if tab_file.exists():
        text = tab_file.read_text(encoding="utf-8")
        # Extract all item references in the tab's display items
        pattern = r'output\.accept\((\w+)\.get\(\)\)'
        for m in re.finditer(pattern, text):
            var_name = m.group(1)
            creative_items.add(var_name)
    
    return entries, creative_items


def extract_item_ids_from_tab():
    """Extract actual item IDs that are in the creative tab, using field→registry mapping."""
    field_to_id = build_field_to_id_map()
    
    tab_file = JAVA / "init" / "ModCreativeTabs.java"
    item_ids = set()
    if not tab_file.exists():
        return item_ids
    
    text = tab_file.read_text(encoding="utf-8")
    pattern = r'output\.accept\(ModItems\.(\w+)\.get\(\)\)'
    for m in re.finditer(pattern, text):
        var_name = m.group(1)
        item_id = field_to_id.get(var_name.lower(), var_name.lower())
        item_ids.add(item_id)
    
    return item_ids


def build_field_to_id_map():
    """Build Java field name → registry ID mapping from ModItems.java."""
    field_to_id = {}
    items_file = JAVA / "init" / "ModItems.java"
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        pattern = r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"'
        for m in re.finditer(pattern, text):
            field_to_id[m.group(1).lower()] = m.group(2)
    return field_to_id


def scan_models():
    """Scan all item and block models, return sets of IDs that have models."""
    item_models = set()
    block_models = set()
    blockstate_ids = set()
    
    if MODELS_ITEM.exists():
        for f in MODELS_ITEM.glob("*.json"):
            item_models.add(f.stem)
    
    if MODELS_BLOCK.exists():
        for f in MODELS_BLOCK.glob("*.json"):
            block_models.add(f.stem)
    
    if BLOCKSTATES.exists():
        for f in BLOCKSTATES.glob("*.json"):
            blockstate_ids.add(f.stem)
    
    return item_models, block_models, blockstate_ids


def scan_textures():
    """Scan texture directories."""
    item_textures = set()
    block_textures = set()
    effect_textures = set()
    villager_textures = set()
    
    if TEXTURES_ITEM.exists():
        for f in TEXTURES_ITEM.glob("*.png"):
            item_textures.add(f.stem)
    
    if TEXTURES_BLOCK.exists():
        for f in TEXTURES_BLOCK.glob("*.png"):
            block_textures.add(f.stem)
    
    if TEXTURES_EFFECT.exists():
        for f in TEXTURES_EFFECT.glob("*.png"):
            effect_textures.add(f.stem)
    
    if TEXTURES_VILLAGER.exists():
        for f in TEXTURES_VILLAGER.glob("*.png"):
            villager_textures.add(f.stem)
    
    return item_textures, block_textures, effect_textures, villager_textures


def scan_lang():
    """Load all language files and return key sets."""
    lang_keys = {}
    for lang_file in LANG_FILES:
        path = LANG_DIR / lang_file
        if path.exists():
            with open(path, "r", encoding="utf-8") as f:
                lang_keys[lang_file] = json.load(f)
        else:
            lang_keys[lang_file] = {}
    return lang_keys


def find_placeholder_values(lang_data):
    """Find keys where the value equals the last segment of the key (registry path placeholder)."""
    placeholders = []
    for key, value in lang_data.items():
        # Extract the item/block/effect name from the key
        parts = key.split(".")
        if len(parts) >= 2:
            last = parts[-1]
            if value == last:
                placeholders.append({"key": key, "value": value})
    return placeholders


def check_model_texture_refs():
    """Check if textures referenced by item models actually exist."""
    missing = []
    if not MODELS_ITEM.exists():
        return missing
    
    for model_file in MODELS_ITEM.glob("*.json"):
        try:
            with open(model_file, "r", encoding="utf-8") as f:
                data = json.load(f)
        except (json.JSONDecodeError, Exception):
            continue
        
        textures = data.get("textures", {})
        for tex_key, tex_path in textures.items():
            if isinstance(tex_path, str):
                # Resolve texture path: "coffeework:item/foo" -> textures/item/foo.png
                if ":" in tex_path:
                    ns, path = tex_path.split(":", 1)
                    if ns == "coffeework":
                        if path.startswith("item/"):
                            tex_name = path[5:]  # remove "item/" prefix
                            tex_file = TEXTURES_ITEM / f"{tex_name}.png"
                            if not tex_file.exists():
                                missing.append({
                                    "model": model_file.stem,
                                    "texture_ref": tex_path,
                                    "expected_file": str(tex_file.relative_to(ROOT))
                                })
                        elif path.startswith("block/"):
                            tex_name = path[6:]  # remove "block/" prefix
                            tex_file = TEXTURES_BLOCK / f"{tex_name}.png"
                            if not tex_file.exists():
                                missing.append({
                                    "model": model_file.stem,
                                    "texture_ref": tex_path,
                                    "expected_file": str(tex_file.relative_to(ROOT))
                                })
                    # For minecraft namespace, skip (vanilla textures)
    return missing


def scan_recipes():
    """Collect all recipe output item IDs from both main and generated data."""
    recipe_outputs = set()
    
    for data_dir in [DATA / "recipes", GEN_DATA / "recipes"]:
        if not data_dir.exists():
            continue
        for recipe_file in data_dir.rglob("*.json"):
            try:
                with open(recipe_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
            except (json.JSONDecodeError, Exception):
                continue
            
            # Standard "result" field (crafting, smelting, machine recipes)
            item_id = ""
            result = data.get("result", {})
            if isinstance(result, dict):
                item_id = result.get("item", "")
            elif isinstance(result, str):
                item_id = result
            
            # Cooling recipes use "iced" field for output
            if not item_id:
                item_id = data.get("iced", "")
            
            if item_id and ":" in item_id:
                ns, name = item_id.split(":", 1)
                if ns == "coffeework":
                    recipe_outputs.add(name)
    
    return recipe_outputs


def scan_loot_tables():
    """Collect items from loot tables."""
    loot_items = set()
    
    for data_dir in [DATA / "loot_tables", GEN_DATA / "loot_tables"]:
        if not data_dir.exists():
            continue
        for lt_file in data_dir.rglob("*.json"):
            try:
                with open(lt_file, "r", encoding="utf-8") as f:
                    data = json.load(f)
            except (json.JSONDecodeError, Exception):
                continue
            
            # Recursively find all "name" fields in loot table entries
            def find_names(obj):
                if isinstance(obj, dict):
                    if "name" in obj:
                        name = obj["name"]
                        if ":" in name:
                            ns, id_ = name.split(":", 1)
                            if ns == "coffeework":
                                loot_items.add(id_)
                    for v in obj.values():
                        find_names(v)
                elif isinstance(obj, list):
                    for item in obj:
                        find_names(item)
            
            find_names(data)
    
    return loot_items


def scan_trades():
    """Extract items traded by villagers from ModVillagers.java."""
    traded_items = set()
    villager_file = JAVA / "init" / "ModVillagers.java"
    if not villager_file.exists():
        return traded_items
    
    text = villager_file.read_text(encoding="utf-8")
    # Match item references in trades
    pattern = r'ModItems\.(\w+)\.get\(\)'
    for m in re.finditer(pattern, text):
        var_name = m.group(1)
        item_id = var_name.lower()
        traded_items.add(item_id)
    
    return traded_items


def check_worldgen_items():
    """Extract items obtained from world generation."""
    worldgen_items = set()
    
    # Check features for placed items
    features_file = JAVA / "init" / "ModFeatures.java"
    if features_file.exists():
        text = features_file.read_text(encoding="utf-8")
        # Coffee tree provides coffee_seeds
        if "coffee_tree" in text.lower():
            worldgen_items.add("coffee_seeds")
        if "blueberry" in text.lower():
            worldgen_items.add("blueberry")
        if "soda_ore" in text.lower():
            worldgen_items.add("soda")
    
    # Check crop drops
    for block_file in (JAVA / "block").glob("Block*.java"):
        text = block_file.read_text(encoding="utf-8")
        # Simple heuristic: look for item drops in getCloneItemStack or onRemove
        for m in re.finditer(r'ModItems\.(\w+)\.get\(\)', text):
            var_name = m.group(1)
            worldgen_items.add(var_name.lower())
    
    return worldgen_items


def build_report():
    print("Coffee Workshop Content Surface Audit")
    print("=" * 60)
    
    # Parse registries
    entries, creative_vars = parse_registry_entries()
    creative_item_ids = extract_item_ids_from_tab()
    
    # Group entries by type
    registry_items = set(e["id"] for e in entries if e["type"] == "item")
    registry_blocks = set(e["id"] for e in entries if e["type"] == "block")
    registry_effects = set(e["id"] for e in entries if e["type"] == "mob_effect")
    registry_professions = set(e["id"] for e in entries if e["type"] == "villager_profession")
    registry_sounds = set(e["id"] for e in entries if e["type"] == "sound_event")
    
    # Scan resources
    item_models, block_models, blockstate_ids = scan_models()
    item_textures, block_textures, effect_textures, villager_textures = scan_textures()
    lang_keys = scan_lang()
    
    # Scan sources
    recipe_outputs = scan_recipes()
    loot_items = scan_loot_tables()
    traded_items = scan_trades()
    worldgen_items = check_worldgen_items()
    
    # Build issues list
    issues = []
    
    # 1. Items without item model
    for item_id in sorted(registry_items):
        if item_id not in item_models:
            # Check if it might be a block-item that uses the block model implicitly
            # (BlockItems auto-generate model from blockstate in 1.20.1, but still need item model)
            issues.append({
                "severity": "P0",
                "type": "missing_item_model",
                "id": item_id,
                "detail": f"Registered item 'coffeework:{item_id}' has no item model JSON"
            })
    
    # 2. Blocks without blockstate
    for block_id in sorted(registry_blocks):
        if block_id not in blockstate_ids:
            issues.append({
                "severity": "P0",
                "type": "missing_blockstate",
                "id": block_id,
                "detail": f"Registered block 'coffeework:{block_id}' has no blockstate JSON"
            })
    
    # 3. Model texture references that don't exist
    tex_issues = check_model_texture_refs()
    for ti in tex_issues:
        issues.append({
            "severity": "P0",
            "type": "missing_texture",
            "id": ti["model"],
            "detail": f"Model '{ti['model']}' references texture '{ti['texture_ref']}' ({ti['expected_file']}) which does not exist"
        })
    
    # 4. Mob effects without icons
    for effect_id in sorted(registry_effects):
        if effect_id not in effect_textures:
            issues.append({
                "severity": "P0",
                "type": "missing_effect_icon",
                "id": effect_id,
                "detail": f"Mob effect 'coffeework:{effect_id}' has no icon PNG in textures/mob_effect/"
            })
    
    # 5. Villager professions without textures
    for prof_id in sorted(registry_professions):
        if prof_id not in villager_textures:
            issues.append({
                "severity": "P1",
                "type": "missing_villager_texture",
                "id": prof_id,
                "detail": f"Villager profession 'coffeework:{prof_id}' has no texture in textures/entity/villager/profession/"
            })
    
    # 6. Language key coverage
    required_keys = []
    # Items
    for item_id in sorted(registry_items):
        required_keys.append(f"item.coffeework.{item_id}")
    # Blocks
    for block_id in sorted(registry_blocks):
        required_keys.append(f"block.coffeework.{block_id}")
    # Effects
    for effect_id in sorted(registry_effects):
        required_keys.append(f"effect.coffeework.{effect_id}")
    # Professions
    for prof_id in sorted(registry_professions):
        required_keys.append(f"entity.minecraft.villager.coffeework.{prof_id}")
    # Creative tab
    required_keys.append("itemGroup.coffee_workshop")
    # GUI hints
    gui_keys = ["gui.coffeework.coffee_machine.base", "gui.coffeework.coffee_machine.modifier",
                "gui.coffeework.coffee_machine.additive", "gui.coffeework.coffee_machine.container"]
    required_keys.extend(gui_keys)
    # JEI keys
    required_keys.extend(["jei.coffeework.category.grinder", "jei.coffeework.category.coffee_machine",
                          "jei.coffeework.category.icecream_machine", "jei.coffeework.category.roller",
                          "jei.coffeework.category.oven", "jei.coffeework.category.cooling"])
    required_keys.append("jei.coffeework.cooling.tip")
    # Container names
    for block_id in ["grinder", "coffee_machine", "icecream_machine", "roller", "oven"]:
        required_keys.append(f"container.coffeework.{block_id}")
    # Record descriptions
    for rec in ["record_kusa_noshi_to_ne", "record_lazy_lady_kaguya", "record_the_grimoire_of_marisa"]:
        required_keys.append(f"item.coffeework.{rec}.desc")
    
    for lang_file in LANG_FILES:
        data = lang_keys.get(lang_file, {})
        for key in required_keys:
            if key not in data:
                issues.append({
                    "severity": "P0",
                    "type": "missing_lang_key",
                    "id": key,
                    "detail": f"Required lang key '{key}' missing from {lang_file}"
                })
    
    # 7. English placeholder values
    en_data = lang_keys.get("en_us.json", {})
    placeholders = find_placeholder_values(en_data)
    for ph in placeholders:
        issues.append({
            "severity": "P0",
            "type": "placeholder_english",
            "id": ph["key"],
            "detail": f"English value '{ph['value']}' equals the registry key name (no proper translation)"
        })
    
    # 8. Creative tab gaps
    for item_id in sorted(registry_items):
        if item_id not in creative_item_ids:
            # Exclude internal/technical items
            if item_id in ("icecream_mix_vanilla",):
                continue
            issues.append({
                "severity": "P1",
                "type": "missing_from_creative_tab",
                "id": item_id,
                "detail": f"Item 'coffeework:{item_id}' is registered but not in creative tab"
            })
    
    # 9. Survival source check
    all_sources = recipe_outputs | loot_items | traded_items | worldgen_items
    # Special items that are tools/molds (no "source" needed but should be craftable)
    tool_like = {"cake_model", "cake_model_square", "cake_model_plate", "small_model",
                 "mooncake_model", "mixing_bowl", "iron_bowl"}
    internal_items = {"vanilla", "bag", "syrup_empty"}
    
    for item_id in sorted(registry_items):
        if item_id in all_sources or item_id in tool_like or item_id in internal_items:
            continue
        if item_id in recipe_outputs:
            continue
        # Check if this is a BlockItem (derives from block)
        if item_id in registry_blocks:
            continue
        issues.append({
            "severity": "P1",
            "type": "no_survival_source",
            "id": item_id,
            "detail": f"Item 'coffeework:{item_id}' has no recipe, loot, trade, or worldgen source found"
        })
    
    # 10. Duplicate / legacy models (old naming)
    legacy_models = []
    for model_id in sorted(item_models):
        # Check for old _on/_off naming for machines
        if model_id in ("coffeemachine_off", "coffeemachine_on", "grinder_off", "grinder_on",
                        "oven_off", "oven_on", "roller_off", "roller_on",
                        "icecreammachine_off", "icecreammachine_on"):
            if model_id not in registry_items:
                legacy_models.append(model_id)
    
    # Build report
    lines = []
    lines.append("# Coffee Workshop — Content Surface Audit Report")
    lines.append("")
    lines.append(f"**Registry totals:** {len(registry_items)} items, {len(registry_blocks)} blocks, "
                 f"{len(registry_effects)} effects, {len(registry_professions)} professions, "
                 f"{len(registry_sounds)} sounds")
    lines.append(f"**Resource totals:** {len(item_models)} item models, {len(block_models)} block models, "
                 f"{len(blockstate_ids)} blockstates, {len(item_textures)} item textures, "
                 f"{len(block_textures)} block textures")
    lines.append(f"**Issues found:** {len(issues)}")
    lines.append("")
    
    # Group by severity
    for severity in ["P0", "P1", "P2"]:
        sev_issues = [i for i in issues if i["severity"] == severity]
        if not sev_issues:
            continue
        
        lines.append(f"## {severity} Issues ({len(sev_issues)})")
        lines.append("")
        
        # Group by type
        by_type = defaultdict(list)
        for i in sev_issues:
            by_type[i["type"]].append(i)
        
        for itype, items in sorted(by_type.items()):
            lines.append(f"### {itype} ({len(items)})")
            lines.append("")
            for item in items:
                lines.append(f"- `{item['id']}` — {item['detail']}")
            lines.append("")
    
    # Legacy model report
    if legacy_models:
        lines.append("## Legacy/Orphan Item Models")
        lines.append("")
        lines.append("These item model files exist but correspond to old naming conventions:")
        lines.append("")
        for m in sorted(legacy_models):
            lines.append(f"- `{m}`")
        lines.append("")
    
    # Orphan assets summary
    orphan_models = sorted(item_models - registry_items - set(legacy_models))
    if orphan_models:
        lines.append("## Orphan Item Models (exist but no registered item)")
        lines.append("")
        lines.append(f"Total: {len(orphan_models)}")
        lines.append("")
        lines.append("These model files exist in `models/item/` but have no corresponding registered item:")
        lines.append("")
        lines.append("```")
        for m in orphan_models[:30]:
            lines.append(f"  {m}")
        if len(orphan_models) > 30:
            lines.append(f"  ... and {len(orphan_models) - 30} more")
        lines.append("```")
        lines.append("")
    
    # Write report
    md_path = REPORT_DIR / "content-surface.md"
    md_path.write_text("\n".join(lines), encoding="utf-8")
    
    # Write JSON
    json_path = REPORT_DIR / "content-surface.json"
    report_data = {
        "registry_counts": {
            "items": len(registry_items),
            "blocks": len(registry_blocks),
            "effects": len(registry_effects),
            "professions": len(registry_professions),
            "sounds": len(registry_sounds),
        },
        "resource_counts": {
            "item_models": len(item_models),
            "block_models": len(block_models),
            "blockstates": len(blockstate_ids),
            "item_textures": len(item_textures),
            "block_textures": len(block_textures),
            "effect_textures": len(effect_textures),
            "villager_textures": len(villager_textures),
        },
        "issues": issues,
        "orphan_models": list(orphan_models),
        "legacy_models": legacy_models,
    }
    json_path.write_text(json.dumps(report_data, indent=2, ensure_ascii=False), encoding="utf-8")
    
    print(f"\nReport written to {md_path}")
    print(f"JSON written to {json_path}")
    
    # Summary
    p0_count = len([i for i in issues if i["severity"] == "P0"])
    p1_count = len([i for i in issues if i["severity"] == "P1"])
    print(f"\nIssues: {p0_count} P0, {p1_count} P1, {len(issues)} total")
    
    if p0_count > 0:
        print("\n⚠ P0 issues detected!")
        return 1
    else:
        print("\n✅ No P0 issues!")
        return 0


if __name__ == "__main__":
    sys.exit(build_report())
