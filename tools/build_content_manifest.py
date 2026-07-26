#!/usr/bin/env python3
"""
Coffee Workshop — Content Manifest Generator

Produces:
  docs/content_manifest.json — machine-readable registry ↔ resource cross-reference
  docs/CONTENT_MANIFEST.md   — human-readable content completion matrix

Cross-references:
  Java Registry → Item Model → Texture → Language (en/zh/ja) → Blockstate → Loot → Recipe → Trade → WorldGen → Creative Tab

Also classifies orphan assets (model/texture/blockstate without registration) into:
  PORT_NOW / PORT_LATER / REDESIGN / MERGED / REMOVED / ASSET_ARCHIVE
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
RES = MAIN / "resources" / "assets" / "coffeework"
DATA = MAIN / "resources" / "data" / "coffeework"
GEN_DATA = SRC / "generated" / "resources" / "data" / "coffeework"
JAVA = MAIN / "java" / "net" / "langball" / "coffee"

MODELS_ITEM = RES / "models" / "item"
MODELS_BLOCK = RES / "models" / "block"
BLOCKSTATES = RES / "blockstates"
TEXTURES_ITEM = RES / "textures" / "item"
TEXTURES_BLOCK = RES / "textures" / "block"
TEXTURES_EFFECT = RES / "textures" / "mob_effect"
TEXTURES_VILLAGER = RES / "textures" / "entity" / "villager" / "profession"
LANG_DIR = RES / "lang"

# ── Classification rules for known unregistered/legacy content ─────

# Items/blocks that have models/assets but no registration — classification
ORPHAN_CLASSIFICATION = {
    # PORT_NOW: high value, low effort, should be restored now
    "coffee_instant_cup": "PORT_NOW",
    "coffee_instant_cup_unopen": "PORT_NOW",
    "icecream_chocolate": "PORT_NOW",
    "icecream_coffee": "PORT_NOW",
    "icecream_apple": "PORT_NOW",
    "icecream_berry": "PORT_NOW",
    "icecream_melon": "PORT_NOW",
    "icecream_lemon": "PORT_NOW",
    "sandwich_club": "PORT_NOW",
    "sandwich_blt_large": "PORT_NOW",
    "sandwich_club_large": "PORT_NOW",
    "sandwich_bacon_egg": "PORT_NOW",
    "sandwich_beef_cheese": "PORT_NOW",
    "sandwich_ham_cheese": "PORT_NOW",
    
    # PORT_LATER: needs longer production chains
    "cream_apple": "PORT_LATER",
    "cream_berry": "PORT_LATER",
    "cream_chocolate": "PORT_LATER",
    "cream_coffee": "PORT_LATER",
    "cream_lemon": "PORT_LATER",
    "cream_melon": "PORT_LATER",
    "cream_milk": "PORT_LATER",
    "cookie_icecream_vanilla": "PORT_LATER",
    "cookie_icecream_chocolate": "PORT_LATER",
    "cookie_icecream_coffee": "PORT_LATER",
    "cookie_icecream_apple": "PORT_LATER",
    "cookie_icecream_berry": "PORT_LATER",
    "cookie_icecream_lemon": "PORT_LATER",
    "cookie_icecream_melon": "PORT_LATER",
    "souffle": "PORT_LATER",
    "souffle_chocolate": "PORT_LATER",
    "muffin": "PORT_LATER",
    "muffin_berry": "PORT_LATER",
    "muffin_carrot": "PORT_LATER",
    "muffin_chocolate": "PORT_LATER",
    "muffin_coffee": "PORT_LATER",
    "muffin_lemon": "PORT_LATER",
    "muffin_pumpkin": "PORT_LATER",
    "muffin_redvelvet": "PORT_LATER",
    "muffin_tea": "PORT_LATER",
    "mooncake": "PORT_LATER",
    "mooncake_egg": "PORT_LATER",
    "mooncake_fruit": "PORT_LATER",
    "mooncake_ham": "PORT_LATER",
    "pie_apple": "PORT_LATER",
    "pie_berry": "PORT_LATER",
    "pie_caramel": "PORT_LATER",
    "pie_chocolate": "PORT_LATER",
    "pie_coffee": "PORT_LATER",
    "pie_lemon": "PORT_LATER",
    "pie_melon": "PORT_LATER",
    "pie_tea": "PORT_LATER",
    "jiggy_cake": "PORT_LATER",
    "jiggy_cake_berry": "PORT_LATER",
    "jiggy_cake_carrot": "PORT_LATER",
    "jiggy_cake_chocolate": "PORT_LATER",
    "jiggy_cake_coffee": "PORT_LATER",
    "jiggy_cake_lemon": "PORT_LATER",
    "jiggy_cake_pumpkin": "PORT_LATER",
    "jiggy_cake_redvelvet": "PORT_LATER",
    "jiggy_cake_tea": "PORT_LATER",
    "soda_drink_cola": "PORT_LATER",
    "soda_drink_lemon": "PORT_LATER",
    "soda_drink_berry": "PORT_LATER",
    "soda_drink_cherry": "PORT_LATER",
    "soda_drink_vanilla": "PORT_LATER",
    "soda_drink_apple": "PORT_LATER",
    "soda_drink_chocolate": "PORT_LATER",
    "croissant": "PORT_LATER",
    "croissant_chocolate": "PORT_LATER",
    "ginger_bread": "PORT_LATER",
    "ginger_bread_man": "PORT_LATER",
    "puff": "PORT_LATER",
    "mille_feuille": "PORT_LATER",
    
    # REDESIGN: needs different approach (e.g. generic display block)
    # All plate block variants for placed drinks — should be a unified display system
    # Classified by prefix pattern below
    
    # MERGED: old naming absorbed into current registry
    "coffeemachine_off": "MERGED",
    "coffeemachine_on": "MERGED",
    "grinder_off": "MERGED",
    "grinder_on": "MERGED",
    "oven_off": "MERGED",
    "oven_on": "MERGED",
    "roller_off": "MERGED",
    "roller_on": "MERGED",
    "icecreammachine_off": "MERGED",
    "icecreammachine_on": "MERGED",
    "clay_oven": "MERGED",
    "clay_oven_on": "MERGED",
    
    # REMOVED: no intention to restore
    "d_bar": "REMOVED",
    "dirty_pastry_bun": "REMOVED",
    "moka_bottom": "REMOVED",
    "moka_top": "REMOVED",
    "moka_pot_heated": "REMOVED",
    "moka_pot_unheated": "REMOVED",
    "turkey_coffee_pot": "REMOVED",
    "turkey_coffee_pot_heated": "REMOVED",
    "turkey_coffee_pot_unheated": "REMOVED",
    "pot": "REMOVED",
    "phonograph": "REMOVED",
    "soda_machine_bottom": "REMOVED",
    "soda_machine_top": "REMOVED",
    "caramel": "REMOVED",
    "caramel_apple": "REMOVED",
    "marshmallow": "REMOVED",
    "marshmallow_chocolate": "REMOVED",
    "marshmallow_roast": "REMOVED",
    "smore": "REMOVED",
    "cookie_black": "REMOVED",
    "cookie_oreo": "REMOVED",
    "hardtack": "REMOVED",
    "custard": "REMOVED",
    "milk_form": "REMOVED",
    "syrup_full": "MERGED",  # replaced by multiple specific syrups
    "syrup_brown": "MERGED",  # renamed to syrup_caramel
    "coffee_pot": "REMOVED",  # old coffee pot models
    "coffee_pot_1": "REMOVED",
    "coffee_pot_2": "REMOVED",
    "coffee_pot_3": "REMOVED",
    "coffee_pot_4": "REMOVED",
}

# Patterns for plate block classifications
PLATE_PATTERNS = [
    "_plate$", "_plate.json", "_slice", "bar_stone", "bar_wooden",
    "soda_machine"
]

def is_drink_plate(name):
    """Check if a model/blockstate represents an old drink plate block."""
    for p in PLATE_PATTERNS:
        if re.search(p, name):
            return True
    return False


# ── Registry parsing ───────────────────────────────────────────────

def parse_registry():
    """Parse all Java registry files and return entries with their Java field name."""
    entries = {}  # registry_id → {type, java_field, ...}
    
    # ModItems.java — extract field name → registry id mapping
    items_file = JAVA / "init" / "ModItems.java"
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', text):
            field, rid = m.group(1), m.group(2)
            entries[rid] = {"type": "item", "java_field": field, "block_item": False}
    
    # ModBlocks.java — extract field name → registry id mapping
    blocks_file = JAVA / "init" / "ModBlocks.java"
    if blocks_file.exists():
        text = blocks_file.read_text(encoding="utf-8")
        for m in re.finditer(r'public static final RegistryObject<Block>\s+(\w+)\s*=\s*BLOCKS\.register\("([^"]+)"', text):
            field, rid = m.group(1), m.group(2)
            entries[rid] = {"type": "block", "java_field": field}
    
    # Tag block items
    for rid, entry in entries.items():
        if entry["type"] == "item":
            if rid in entries and entries[rid]["type"] == "block":
                entry["block_item"] = True
    
    # Find block item associations by looking at BlockItem constructors
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"[^)]*ModBlocks\.(\w+)', text):
            field, rid, block_field = m.group(1), m.group(2), m.group(3)
            if rid in entries:
                entries[rid]["block_item"] = True
                entries[rid]["block_field"] = block_field
    
    # ModEffects.java
    effects_file = JAVA / "init" / "ModEffects.java"
    if effects_file.exists():
        text = effects_file.read_text(encoding="utf-8")
        for m in re.finditer(r'EFFECTS\.register\("([^"]+)"', text):
            entries[m.group(1)] = {"type": "mob_effect", "java_field": ""}
    
    # ModVillagers.java
    villager_file = JAVA / "init" / "ModVillagers.java"
    if villager_file.exists():
        text = villager_file.read_text(encoding="utf-8")
        for m in re.finditer(r'PROFESSIONS\.register\("([^"]+)"', text):
            entries[m.group(1)] = {"type": "villager_profession", "java_field": ""}
        for m in re.finditer(r'POI_TYPES\.register\("([^"]+)"', text):
            entries[m.group(1)] = {"type": "poi_type", "java_field": ""}
    
    # ModSounds.java
    sounds_file = JAVA / "init" / "ModSounds.java"
    if sounds_file.exists():
        text = sounds_file.read_text(encoding="utf-8")
        for m in re.finditer(r'SOUNDS\.register\("([^"]+)"', text):
            entries[m.group(1)] = {"type": "sound_event", "java_field": ""}
    
    return entries


# ── Resource scanning ──────────────────────────────────────────────

def scan_json_dir(path):
    """Return set of filenames without extension."""
    if not path.exists():
        return set()
    return {f.stem for f in path.glob("*.json")}

def scan_png_dir(path):
    if not path.exists():
        return set()
    return {f.stem for f in path.glob("*.png")}


def scan_lang():
    langs = {}
    for lf in ["en_us.json", "zh_cn.json", "ja_jp.json"]:
        p = LANG_DIR / lf
        if p.exists():
            with open(p, "r", encoding="utf-8") as f:
                langs[lf] = json.load(f)
    return langs


def scan_recipes(data_dirs):
    """Returns dict: recipe_id → {type, result_id, ...}"""
    recipes = {}
    for d in data_dirs:
        if not d.exists():
            continue
        for rf in d.rglob("*.json"):
            try:
                with open(rf, "r", encoding="utf-8") as f:
                    data = json.load(f)
            except (json.JSONDecodeError, Exception):
                continue
            rid = rf.stem
            rtype = data.get("type", "minecraft:crafting_shaped")
            result = data.get("result", {})
            if isinstance(result, dict):
                item = result.get("item", "")
            elif isinstance(result, str):
                item = result
            else:
                item = ""
            if ":" in item:
                _, iname = item.split(":", 1)
            else:
                iname = ""
            recipes[rid] = {"type": rtype, "result": iname, "path": str(rf.relative_to(ROOT))}
    return recipes


def scan_loot_tables(data_dirs):
    """Returns set of item IDs found in loot tables."""
    items = set()
    
    def find_names(obj):
        if isinstance(obj, dict):
            if "name" in obj:
                name = obj["name"]
                if ":" in name:
                    ns, id_ = name.split(":", 1)
                    if ns == "coffeework":
                        items.add(id_)
            for v in obj.values():
                find_names(v)
        elif isinstance(obj, list):
            for item in obj:
                find_names(item)
    
    for d in data_dirs:
        lt_dir = d / "loot_tables"
        if not lt_dir.exists():
            continue
        for lf in lt_dir.rglob("*.json"):
            try:
                with open(lf, "r", encoding="utf-8") as f:
                    data = json.load(f)
            except (json.JSONDecodeError, Exception):
                continue
            find_names(data)
    return items


def scan_creative_tab():
    """Returns set of item IDs in creative tab, resolving Java field→registry mapping."""
    tab_file = JAVA / "init" / "ModCreativeTabs.java"
    if not tab_file.exists():
        return set()
    
    text = tab_file.read_text(encoding="utf-8")
    items = set()
    for m in re.finditer(r'output\.accept\(ModItems\.(\w+)\.get\(\)\)', text):
        field = m.group(1).lower()
        items.add(field)
    
    # Build field → id mapping from ModItems.java
    field_to_id = {}
    items_file = JAVA / "init" / "ModItems.java"
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', text):
            field_to_id[m.group(1).lower()] = m.group(2)
    
    # Resolve
    resolved = set()
    for field in items:
        if field in field_to_id:
            resolved.add(field_to_id[field])
        else:
            resolved.add(field)
    return resolved


def scan_trades():
    """Returns set of item IDs involved in villager trades."""
    items = set()
    villager_file = JAVA / "init" / "ModVillagers.java"
    if not villager_file.exists():
        return items
    
    text = villager_file.read_text(encoding="utf-8")
    for m in re.finditer(r'ModItems\.(\w+)\.get\(\)', text):
        field = m.group(1).lower()
        items.add(field)
    
    # Resolve via field_to_id
    field_to_id = {}
    items_file = JAVA / "init" / "ModItems.java"
    if items_file.exists():
        text = items_file.read_text(encoding="utf-8")
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', text):
            field_to_id[m.group(1).lower()] = m.group(2)
    
    resolved = set()
    for f in items:
        resolved.add(field_to_id.get(f, f))
    return resolved


def check_model_texture_refs():
    """Check texture references in item models."""
    missing = defaultdict(list)
    if not MODELS_ITEM.exists():
        return missing
    
    for mf in MODELS_ITEM.glob("*.json"):
        try:
            with open(mf, "r", encoding="utf-8") as f:
                data = json.load(f)
        except (json.JSONDecodeError, Exception):
            continue
        
        textures = data.get("textures", {})
        for tex_key, tex_path in textures.items():
            if isinstance(tex_path, str) and tex_path.startswith("coffeework:"):
                _, path = tex_path.split(":", 1)
                if path.startswith("item/"):
                    tex_name = path[5:]
                    if not (TEXTURES_ITEM / f"{tex_name}.png").exists():
                        missing[mf.stem].append(tex_path)
                elif path.startswith("block/"):
                    tex_name = path[6:]
                    if not (TEXTURES_BLOCK / f"{tex_name}.png").exists():
                        missing[mf.stem].append(tex_path)
    return missing


# ── Orphan classification ──────────────────────────────────────────

# Patterns for models that are companion/state variants of registered blocks
STATE_MODEL_PATTERNS = [
    r'_stage\d+$',           # crop stages
    r'_uneaten$',            # cake uneaten state
    r'_slice\d+$',           # cake slice states
    r'_on$',                 # lit state models
    r'_finished$',           # coldbrew pot finished state
]

def is_state_model(name, registered_blocks):
    """Check if this is a state/crop variant model for a registered block."""
    for pat in STATE_MODEL_PATTERNS:
        if re.search(pat, name):
            return True
    # Check if it's a variant of a registered block
    for bid in registered_blocks:
        if name.startswith(bid + "_") or name.endswith("_" + bid):
            return True
        # Handle crop models: vanilla_stage_X belongs to vanilla_crop
        if bid.endswith("_crop"):
            base = bid[:-5]  # remove "_crop"
            if name.startswith(base + "_stage"):
                return True
        # Handle shared base models (bag is parent of bag_coffee, etc.)
        if bid.startswith(name + "_"):
            return True
    return False


def classify_orphan(name, category, registered_blocks=None):
    """Classify an unregistered asset."""
    if name in ORPHAN_CLASSIFICATION:
        return ORPHAN_CLASSIFICATION[name]
    
    # State/crop variants of registered blocks → ASSET_ARCHIVE (active companions)
    if registered_blocks and is_state_model(name, registered_blocks):
        return "ASSET_ARCHIVE"
    
    # Block items that match registered block (these aren't really orphaned)
    if registered_blocks and name in registered_blocks:
        return "ASSET_ARCHIVE"  # item model for a block, properly used
    
    # Plate/block variants for placed drinks → redesign
    if is_drink_plate(name):
        return "REDESIGN"
    
    # Raw/model/slice variants that are companion assets → asset_archive
    if any(name.endswith(s) for s in ["_raw", "_model", "_slices", "_base", 
                                       "_plate_model", "_plate_raw", "_roll"]):
        return "ASSET_ARCHIVE"
    
    # Duplicate/alternate naming conventions
    if name.startswith("records_"):  # records_kusa_noshi_to_ne vs records.kusa_noshi_to_ne
        return "ASSET_ARCHIVE"
    
    # Old naming variants (singular, alternate)
    legacy_names = {
        "coffee_seed", "coffee_bean_light", "coffee_powder_light",
        "coffee", "coffee_ice", "crop_coffee",
        "field_ration_d", "coffee_machine", "icecream_machine",
        "roller", "coldbrew_pot", "coldbrew_pot_finished",
        "plate", "soda_ore", "xmas_tree", "ginger_house",
        "blueberry_bush", "coffee_tree",
        "plate_pastry", "plate_ginger",
        "iron_bowl_batter", "iron_bowl_cheese", "iron_bowl_egg",
        "iron_bowl_batter_berry", "iron_bowl_batter_carrot",
        "iron_bowl_batter_chocolate", "iron_bowl_batter_coffee",
        "iron_bowl_batter_lemon", "iron_bowl_batter_pumpkin",
        "iron_bowl_batter_red", "iron_bowl_batter_tea",
        "cake_carrot",  # old blockstate
    }
    if name in legacy_names:
        return "ASSET_ARCHIVE"
    
    return "UNCLASSIFIED"


# ── Main manifest builder ──────────────────────────────────────────

def build_manifest():
    print("Coffee Workshop — Content Manifest Generator")
    print("=" * 60)
    
    # Parse registries
    registry = parse_registry()
    print(f"Registry entries: {len(registry)}")
    
    # Scan resources
    item_models = scan_json_dir(MODELS_ITEM)
    block_models = scan_json_dir(MODELS_BLOCK)
    blockstates = scan_json_dir(BLOCKSTATES)
    item_textures = scan_png_dir(TEXTURES_ITEM)
    block_textures = scan_png_dir(TEXTURES_BLOCK)
    effect_textures = scan_png_dir(TEXTURES_EFFECT)
    villager_textures = scan_png_dir(TEXTURES_VILLAGER)
    langs = scan_lang()
    
    # Scan sources
    data_dirs = [DATA, GEN_DATA]
    recipes = scan_recipes(data_dirs)
    loot_items = scan_loot_tables(data_dirs)
    creative_items = scan_creative_tab()
    trade_items = scan_trades()
    tex_missing = check_model_texture_refs()
    
    # Recipe outputs
    recipe_outputs = set()
    for r in recipes.values():
        if r["result"]:
            recipe_outputs.add(r["result"])
    
    # Build manifest entries for registered content
    manifest = []
    orphan_assets = []
    
    en_lang = langs.get("en_us.json", {})
    zh_lang = langs.get("zh_cn.json", {})
    ja_lang = langs.get("ja_jp.json", {})
    
    for rid, info in sorted(registry.items()):
        entry = {
            "id": rid,
            "type": info["type"],
            "registered": True,
        }
        
        if info["type"] == "item":
            # Model
            entry["model"] = rid in item_models
            # Texture
            entry["texture_ok"] = rid not in tex_missing
            entry["texture_issues"] = tex_missing.get(rid, [])
            # Lang
            key = f"item.coffeework.{rid}"
            entry["lang_en"] = key in en_lang
            entry["lang_zh"] = key in zh_lang
            entry["lang_ja"] = key in ja_lang
            entry["lang_en_placeholder"] = (key in en_lang and en_lang.get(key) == rid)
            # Creative tab
            entry["creative_tab"] = rid in creative_items
            # Sources
            sources = []
            if rid in recipe_outputs:
                sources.append("recipe")
            if rid in loot_items:
                sources.append("loot")
            if rid in trade_items:
                sources.append("trade")
            entry["sources"] = sources
            
        elif info["type"] == "block":
            # Blockstate
            entry["blockstate"] = rid in blockstates
            # Block model (any model containing this block name)
            has_block_model = any(rid in m or m.startswith(f"{rid}_") for m in block_models)
            entry["block_model"] = has_block_model
            # Lang
            key = f"block.coffeework.{rid}"
            entry["lang_en"] = key in en_lang
            entry["lang_zh"] = key in zh_lang
            entry["lang_ja"] = key in ja_lang
            # Loot table
            entry["loot_table"] = rid in loot_items
            
        elif info["type"] == "mob_effect":
            entry["icon"] = rid in effect_textures
            key = f"effect.coffeework.{rid}"
            entry["lang_en"] = key in en_lang
            entry["lang_zh"] = key in zh_lang
            entry["lang_ja"] = key in ja_lang
            
        elif info["type"] == "villager_profession":
            entry["texture"] = rid in villager_textures
            key = f"entity.minecraft.villager.coffeework.{rid}"
            entry["lang_en"] = key in en_lang
            entry["lang_zh"] = key in zh_lang
            entry["lang_ja"] = key in ja_lang
            
        elif info["type"] == "sound_event":
            # Check sounds.json
            entry["in_sounds_json"] = True  # assume since registered
            entry["ogg_exists"] = (ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "sounds" / f"{rid.replace('.', '/')}.ogg").exists()
        
        manifest.append(entry)
    
    # Classify orphan assets
    registered_blocks = {e["id"] for e in manifest if e["type"] == "block"}
    orphan_item_models = item_models - {e["id"] for e in manifest if e["type"] == "item"}
    orphan_block_models = block_models - registered_blocks
    for bm in sorted(orphan_block_models):
        classification = classify_orphan(bm, "block_model", registered_blocks)
        orphan_assets.append({"id": bm, "type": "block_model", "classification": classification})
    
    for im in sorted(orphan_item_models):
        classification = classify_orphan(im, "item_model", registered_blocks)
        orphan_assets.append({"id": im, "type": "item_model", "classification": classification})
    
    # Orphan blockstates
    orphan_blockstates = blockstates - registered_blocks
    for bs in sorted(orphan_blockstates):
        classification = classify_orphan(bs, "blockstate", registered_blocks)
        orphan_assets.append({"id": bs, "type": "blockstate", "classification": classification})
    
    # Orphan textures (not referenced by any registered item model)
    all_registered_texture_refs = set()
    for e in manifest:
        if e.get("type") == "item" and e.get("model"):
            all_registered_texture_refs.add(e["id"])
    orphan_item_textures = item_textures - all_registered_texture_refs
    # Filter: many textures are shared (e.g. coffee_bean_light is used by coffee_bean model)
    for ot in sorted(orphan_item_textures):
        if ot not in item_models and ot not in block_models:
            classification = classify_orphan(ot, "texture", registered_blocks)
            if classification != "UNCLASSIFIED":
                orphan_assets.append({"id": ot, "type": "item_texture", "classification": classification})
    
    # Counts
    stats = {
        "registered": len(manifest),
        "by_type": defaultdict(int),
        "orphan_assets": len(orphan_assets),
        "orphan_by_classification": defaultdict(int),
        "completeness": {
            "model_ok": 0, "texture_ok": 0, "lang_en_ok": 0, "lang_zh_ok": 0, "lang_ja_ok": 0,
            "creative_tab_ok": 0, "has_source": 0,
            "total_items": 0
        }
    }
    
    for e in manifest:
        stats["by_type"][e["type"]] += 1
        if e["type"] == "item":
            stats["completeness"]["total_items"] += 1
            if e.get("model"): stats["completeness"]["model_ok"] += 1
            if e.get("texture_ok", True): stats["completeness"]["texture_ok"] += 1
            if e.get("lang_en"): stats["completeness"]["lang_en_ok"] += 1
            if e.get("lang_zh"): stats["completeness"]["lang_zh_ok"] += 1
            if e.get("lang_ja"): stats["completeness"]["lang_ja_ok"] += 1
            if e.get("creative_tab"): stats["completeness"]["creative_tab_ok"] += 1
            if e.get("sources"): stats["completeness"]["has_source"] += 1
    
    for oa in orphan_assets:
        stats["orphan_by_classification"][oa["classification"]] += 1
    
    # Build output
    output = {
        "stats": {k: dict(v) if isinstance(v, defaultdict) else v for k, v in stats.items()},
        "registry": manifest,
        "orphan_assets": orphan_assets,
    }
    
    # Write JSON
    json_path = ROOT / "docs" / "content_manifest.json"
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)
    print(f"\nJSON manifest: {json_path}")
    
    # Write Markdown
    md = build_markdown_report(output)
    md_path = ROOT / "docs" / "CONTENT_MANIFEST.md"
    md_path.write_text(md, encoding="utf-8")
    print(f"MD manifest: {md_path}")
    
    # Print summary
    comp = stats["completeness"]
    ti = comp["total_items"]
    if ti > 0:
        print(f"\nItem Completeness:")
        print(f"  Model:  {comp['model_ok']}/{ti} ({comp['model_ok']*100//ti}%)")
        print(f"  Texture:{comp['texture_ok']}/{ti} ({comp['texture_ok']*100//ti}%)")
        print(f"  en_us:  {comp['lang_en_ok']}/{ti} ({comp['lang_en_ok']*100//ti}%)")
        print(f"  zh_cn:  {comp['lang_zh_ok']}/{ti} ({comp['lang_zh_ok']*100//ti}%)")
        print(f"  ja_jp:  {comp['lang_ja_ok']}/{ti} ({comp['lang_ja_ok']*100//ti}%)")
        print(f"  Tab:    {comp['creative_tab_ok']}/{ti} ({comp['creative_tab_ok']*100//ti}%)")
        print(f"  Source: {comp['has_source']}/{ti} ({comp['has_source']*100//ti}%)")
    
    print(f"\nOrphan Assets: {stats['orphan_assets']}")
    for cls, count in sorted(stats["orphan_by_classification"].items()):
        print(f"  {cls}: {count}")
    
    unconverted = stats["orphan_by_classification"].get("UNCLASSIFIED", 0)
    if unconverted > 0:
        print(f"\n⚠ {unconverted} unclassified orphan assets — review needed")
    
    return 0


def build_markdown_report(data):
    lines = []
    lines.append("# Coffee Workshop — Content Manifest")
    lines.append("")
    lines.append(f"> **Generated:** auto-generated from registry scan")
    lines.append(f"> **Branch:** 1.20.1")
    lines.append("")
    
    # Summary stats
    stats = data["stats"]
    comp = stats["completeness"]
    ti = comp["total_items"]
    
    lines.append("## Summary Statistics")
    lines.append("")
    lines.append(f"| Registry Type | Count |")
    lines.append(f"|---|---|")
    for t, c in sorted(stats["by_type"].items()):
        lines.append(f"| {t} | {c} |")
    lines.append(f"| **Total Registered** | **{stats['registered']}** |")
    lines.append(f"| **Orphan Assets** | **{stats['orphan_assets']}** |")
    lines.append("")
    
    if ti > 0:
        lines.append("## Item Completeness Matrix")
        lines.append("")
        lines.append(f"| Metric | Count | % |")
        lines.append(f"|---|---|---|")
        for label, key in [
            ("Model", "model_ok"),
            ("Texture", "texture_ok"),
            ("en_us Translation", "lang_en_ok"),
            ("zh_cn Translation", "lang_zh_ok"),
            ("ja_jp Translation", "lang_ja_ok"),
            ("Creative Tab", "creative_tab_ok"),
            ("Survival Source", "has_source"),
        ]:
            val = comp.get(key, 0)
            pct = val * 100 // ti if ti > 0 else 0
            lines.append(f"| {label} | {val}/{ti} | {pct}% |")
        lines.append("")
    
    # Registered items table
    lines.append("## Registered Items")
    lines.append("")
    lines.append("| ID | Model | Tex | en | zh | ja | Tab | Source |")
    lines.append("|---|---|---|---|---|---|---|---|")
    
    items = [e for e in data["registry"] if e["type"] == "item"]
    for e in items:
        m = "✅" if e.get("model") else "❌"
        t = "✅" if e.get("texture_ok", True) else "❌"
        en = "✅" if e.get("lang_en") else "❌"
        zh = "✅" if e.get("lang_zh") else "❌"
        ja = "✅" if e.get("lang_ja") else "❌"
        tab = "✅" if e.get("creative_tab") else "—"
        src = ",".join(e.get("sources", [])) or "—"
        lines.append(f"| `{e['id']}` | {m} | {t} | {en} | {zh} | {ja} | {tab} | {src} |")
    
    lines.append("")
    
    # Blocks
    blocks = [e for e in data["registry"] if e["type"] == "block"]
    if blocks:
        lines.append("## Registered Blocks")
        lines.append("")
        lines.append("| ID | Blockstate | Block Model | en | zh | ja | Loot |")
        lines.append("|---|---|---|---|---|---|---|")
        for e in blocks:
            bs = "✅" if e.get("blockstate") else "❌"
            bm = "✅" if e.get("block_model") else "❌"
            en = "✅" if e.get("lang_en") else "❌"
            zh = "✅" if e.get("lang_zh") else "❌"
            ja = "✅" if e.get("lang_ja") else "❌"
            lt = "✅" if e.get("loot_table") else "—"
            lines.append(f"| `{e['id']}` | {bs} | {bm} | {en} | {zh} | {ja} | {lt} |")
        lines.append("")
    
    # Effects
    effects = [e for e in data["registry"] if e["type"] == "mob_effect"]
    if effects:
        lines.append("## Mob Effects")
        lines.append("")
        lines.append("| ID | Icon | en | zh | ja |")
        lines.append("|---|---|---|---|---|")
        for e in effects:
            ic = "✅" if e.get("icon") else "❌"
            en = "✅" if e.get("lang_en") else "❌"
            zh = "✅" if e.get("lang_zh") else "❌"
            ja = "✅" if e.get("lang_ja") else "❌"
            lines.append(f"| `{e['id']}` | {ic} | {en} | {zh} | {ja} |")
        lines.append("")
    
    # Professions
    profs = [e for e in data["registry"] if e["type"] == "villager_profession"]
    if profs:
        lines.append("## Villager Professions")
        lines.append("")
        lines.append("| ID | Texture | en | zh | ja |")
        lines.append("|---|---|---|---|---|")
        for e in profs:
            tx = "✅" if e.get("texture") else "❌"
            en = "✅" if e.get("lang_en") else "❌"
            zh = "✅" if e.get("lang_zh") else "❌"
            ja = "✅" if e.get("lang_ja") else "❌"
            lines.append(f"| `{e['id']}` | {tx} | {en} | {zh} | {ja} |")
        lines.append("")
    
    # Orphan assets summary
    lines.append("## Orphan Asset Classification")
    lines.append("")
    lines.append(f"| Classification | Count |")
    lines.append(f"|---|---|")
    for cls, count in sorted(stats["orphan_by_classification"].items()):
        lines.append(f"| {cls} | {count} |")
    lines.append("")
    
    # Detail by classification
    by_class = defaultdict(list)
    for oa in data["orphan_assets"]:
        by_class[oa["classification"]].append(oa)
    
    for cls in ["PORT_NOW", "PORT_LATER", "REDESIGN", "MERGED", "REMOVED", "ASSET_ARCHIVE", "UNCLASSIFIED"]:
        items = by_class.get(cls, [])
        if not items:
            continue
        
        lines.append(f"### {cls} ({len(items)})")
        lines.append("")
        
        if cls == "PORT_NOW":
            lines.append("High value, low effort — should be restored now.")
        elif cls == "PORT_LATER":
            lines.append("Requires longer production chains — restore in a later content phase.")
        elif cls == "REDESIGN":
            lines.append("Needs a different architectural approach (e.g. generic display block for placed drinks).")
        elif cls == "MERGED":
            lines.append("Old naming merged into current registry (e.g. _on/_off → LIT property).")
        elif cls == "REMOVED":
            lines.append("No intention to restore.")
        elif cls == "ASSET_ARCHIVE":
            lines.append("Companion assets (raw/model/slice variants) — archive to reference/legacy-assets/.")
        elif cls == "UNCLASSIFIED":
            lines.append("⚠ Needs manual review and classification.")
        
        lines.append("")
        lines.append("```")
        for item in sorted(items, key=lambda x: x["id"]):
            lines.append(f"  {item['type']}: {item['id']}")
        lines.append("```")
        lines.append("")
    
    return "\n".join(lines)


if __name__ == "__main__":
    sys.exit(build_manifest())
