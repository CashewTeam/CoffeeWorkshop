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

def _scan_java_files(glob_pattern: str) -> list:
    """Find all Java files matching a pattern in the init/ directory.
    Supports both single-file and multi-file layouts.
    """
    init_dir = JAVA / "init"
    if not init_dir.exists():
        return []
    matches = list(init_dir.glob(glob_pattern))
    if not matches:
        # Fallback: try the old single-file name
        return []
    return sorted(matches)


def _read_all_java_text(glob_pattern: str) -> str:
    """Concatenate text from all matching Java files in init/."""
    files = _scan_java_files(glob_pattern)
    return "\n".join(f.read_text(encoding="utf-8") for f in files)


def parse_registry():
    """Parse all Java registry files, keeping items and blocks separate to avoid overwrite.
    Supports both single-file (ModItems.java) and multi-file (ModCoffeeItems.java, etc.) layouts.
    """
    items = {}   # registry_id → {type: "item", java_field, block_item, ...}
    blocks = {}  # registry_id → {type: "block", java_field, ...}
    effects = {}
    professions = {}
    poi_types = {}
    sounds = {}
    
    # Build field_name → registry_id lookup
    field_to_id = {}
    
    # Scan all Mod*Items*.java files
    items_text = _read_all_java_text("Mod*Items*.java")
    if items_text:
        # Pattern 1: old-style inline declarations
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', items_text):
            field, rid = m.group(1), m.group(2)
            items[rid] = {"type": "item", "java_field": field, "block_item": False}
            field_to_id[field.lower()] = rid
        # Pattern 2: new-style delegated registrations (ModItems.X = items.register("id", ...))
        for m in re.finditer(r'ModItems\.(\w+)\s*=\s*items\.register\("([^"]+)"', items_text):
            field, rid = m.group(1), m.group(2)
            if rid not in items:  # don't overwrite
                items[rid] = {"type": "item", "java_field": field, "block_item": False}
                field_to_id[field.lower()] = rid
    
    # Scan all Mod*Blocks*.java files
    blocks_text = _read_all_java_text("Mod*Blocks*.java")
    block_field_to_id = {}
    if blocks_text:
        for m in re.finditer(r'public static final RegistryObject<Block>\s+(\w+)\s*=\s*BLOCKS\.register\("([^"]+)"', blocks_text):
            field, rid = m.group(1), m.group(2)
            blocks[rid] = {"type": "block", "java_field": field}
            block_field_to_id[field.lower()] = rid
            field_to_id[field.lower()] = rid
        # New-style delegated: ModBlocks.X = blocks.register("id", ...)
        for m in re.finditer(r'ModBlocks\.(\w+)\s*=\s*blocks\.register\("([^"]+)"', blocks_text):
            field, rid = m.group(1), m.group(2)
            if rid not in blocks:
                blocks[rid] = {"type": "block", "java_field": field}
                block_field_to_id[field.lower()] = rid
                field_to_id[field.lower()] = rid
    
    # Detect BlockItems: items whose registry ID matches a block ID
    for rid in items:
        if rid in blocks:
            items[rid]["block_item"] = True
            items[rid]["block_of"] = rid
    
    # Also check BlockItem constructor patterns for ID mismatches (e.g. grinder_off → grinder)
    # Process each file separately so DOTALL regex doesn't cross file boundaries.
    items_files = _scan_java_files("Mod*Items*.java")
    for items_file in items_files:
        text = items_file.read_text(encoding="utf-8")
        for m in re.finditer(r'(?:ITEMS|items)\.register\("([^"]+)".*?new\s+BlockItem\(ModBlocks\.(\w+)\.get\(\)', text, re.DOTALL):
            item_rid, block_field = m.group(1), m.group(2)
            if item_rid in items:
                block_rid = block_field_to_id.get(block_field.lower(), block_field.lower())
                items[item_rid]["block_item"] = True
                items[item_rid]["block_of"] = block_rid
    
    # ModEffects.java
    effects_file = JAVA / "init" / "ModEffects.java"
    if effects_file.exists():
        text = effects_file.read_text(encoding="utf-8")
        for m in re.finditer(r'EFFECTS\.register\("([^"]+)"', text):
            effects[m.group(1)] = {"type": "mob_effect", "java_field": ""}
    
    # ModVillagers.java
    villager_file = JAVA / "init" / "ModVillagers.java"
    if villager_file.exists():
        text = villager_file.read_text(encoding="utf-8")
        for m in re.finditer(r'PROFESSIONS\.register\("([^"]+)"', text):
            professions[m.group(1)] = {"type": "villager_profession", "java_field": ""}
        for m in re.finditer(r'POI_TYPES\.register\("([^"]+)"', text):
            poi_types[m.group(1)] = {"type": "poi_type", "java_field": ""}
    
    # ModSounds.java
    sounds_file = JAVA / "init" / "ModSounds.java"
    if sounds_file.exists():
        text = sounds_file.read_text(encoding="utf-8")
        for m in re.finditer(r'SOUNDS\.register\("([^"]+)"', text):
            sounds[m.group(1)] = {"type": "sound_event", "java_field": ""}
    
    return items, blocks, effects, professions, poi_types, sounds, field_to_id


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
    """Returns dict: recipe_id → {type, result_id, ...}. Parses all recipe formats."""
    recipes = {}
    for d in data_dirs:
        if not d.exists():
            continue
        for rf in d.rglob("*.json"):
            # Only JSONs in a "recipes" directory (not advancements, loot_tables, etc.)
            parts = set(rf.parts)
            if "recipes" not in parts and not any(p == "recipes" for p in rf.parents if p != rf):
                continue
            # Skip if parent has "advancements" (advancement JSONs have same name as recipes)
            if "advancements" in parts or any("advancements" in str(p) for p in rf.parents):
                continue
            try:
                with open(rf, "r", encoding="utf-8") as f:
                    data = json.load(f)
            except (json.JSONDecodeError, Exception):
                continue
            rid = rf.stem
            rtype = data.get("type", "")
            
            # Only process files that look like actual recipes (have a type field)
            if not rtype:
                continue
            
            # Standard "result" field (crafting, machine recipes)
            result = data.get("result", {})
            if isinstance(result, dict):
                item = result.get("item", "")
            elif isinstance(result, str):
                item = result
            else:
                item = ""
            
            # Cooling recipes use "iced" field for output
            if not item:
                item = data.get("iced", "")
            
            if item and ":" in item:
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
    
    # Build field → id mapping from all Mod*Items*.java files
    field_to_id = {}
    items_text = _read_all_java_text("Mod*Items*.java")
    if items_text:
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', items_text):
            field_to_id[m.group(1).lower()] = m.group(2)
        for m in re.finditer(r'ModItems\.(\w+)\s*=\s*items\.register\("([^"]+)"', items_text):
            if m.group(1).lower() not in field_to_id:
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
    
    # Resolve via field_to_id from all Mod*Items*.java files
    field_to_id = {}
    items_text = _read_all_java_text("Mod*Items*.java")
    if items_text:
        for m in re.finditer(r'public static final RegistryObject<Item>\s+(\w+)\s*=\s*ITEMS\.register\("([^"]+)"', items_text):
            field_to_id[m.group(1).lower()] = m.group(2)
        for m in re.finditer(r'ModItems\.(\w+)\s*=\s*items\.register\("([^"]+)"', items_text):
            if m.group(1).lower() not in field_to_id:
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
    
    # Parse registries (separate types, no overwrite)
    items, blocks, effects, professions, poi_types, sounds, field_to_id = parse_registry()
    print(f"Registry entries: {len(items)} items, {len(blocks)} blocks, "
          f"{len(effects)} effects, {len(professions)} professions, "
          f"{len(poi_types)} POIs, {len(sounds)} sounds")
    
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
    tex_missing = check_model_texture_refs()
    
    # Parse blockstate → block model references for accurate model detection
    blockstate_models = {}  # block_id → set of model names referenced
    for bid in blocks:
        bs_path = BLOCKSTATES / f"{bid}.json"
        if bs_path.exists():
            try:
                bs_data = json.loads(bs_path.read_text(encoding="utf-8"))
                models = set()
                for variant_val in bs_data.get("variants", {}).values():
                    entries = variant_val if isinstance(variant_val, list) else [variant_val]
                    for e in entries:
                        mdl = e.get("model", "")
                        if mdl and ":" in mdl:
                            ns, mp = mdl.split(":", 1)
                            if ns == "coffeework" and "/" in mp:
                                models.add(mp.split("/")[-1])
                blockstate_models[bid] = models
            except (json.JSONDecodeError, Exception):
                pass
    
    # Scan villager trades with direction (ItemsForEmeralds vs EmeraldsForItems)
    trade_sells = set()   # villager sells item → player gets it (source)
    trade_buys = set()    # player gives item → villager buys (not a source)
    villager_file = JAVA / "init" / "ModVillagers.java"
    if villager_file.exists():
        vtext = villager_file.read_text(encoding="utf-8")
        for m in re.finditer(r'new ItemsForEmeralds\(ModItems\.(\w+)\.get\(\)', vtext):
            fid = m.group(1).lower()
            trade_sells.add(field_to_id.get(fid, fid))
        for m in re.finditer(r'new EmeraldsForItems\(ModItems\.(\w+)\.get\(\)', vtext):
            fid = m.group(1).lower()
            trade_buys.add(field_to_id.get(fid, fid))
    
    # Worldgen items (from features)
    worldgen_items = {"coffee_tree", "blueberry_bush", "soda_ore"}
    # Block interaction items (obtained via right-click / use on blocks)
    block_interact_items = {"coldbrew_bottle",
        "cake_slices", "cake_berry_slices", "cake_cheese_slices", "cake_coffee_slices",
        "cake_harvest_slices", "cake_lemon_slices", "cake_redvelvet_slices",
        "cake_schwarzwald_slices", "cake_tea_slices", "cake_sponge_berry_slices",
        "cake_sponge_carrot_slices", "cake_sponge_chocolate_slices", "cake_sponge_coffee_slices",
        "cake_sponge_lemon_slices", "cake_sponge_pumpkin_slices", "cake_sponge_redvelvet_slices",
        "cake_sponge_tea_slices", "cake_sponge_slice",
    }
    
    # Recipe outputs
    recipe_outputs = set()
    for r in recipes.values():
        if r["result"]:
            recipe_outputs.add(r["result"])
    
    # Actual sounds.json parsing (not fake)
    sounds_json = {}
    sounds_json_path = RES / "sounds.json"
    if sounds_json_path.exists():
        with open(sounds_json_path, "r", encoding="utf-8") as f:
            sounds_json = json.load(f)
    
    en_lang = langs.get("en_us.json", {})
    zh_lang = langs.get("zh_cn.json", {})
    ja_lang = langs.get("ja_jp.json", {})
    
    # Build manifest entries
    manifest = []
    registered_item_ids = set()
    registered_block_ids = set()
    
    # Items
    for rid, info in sorted(items.items()):
        registered_item_ids.add(rid)
        entry = {"id": rid, "type": "item", "registered": True}
        entry["model"] = rid in item_models
        entry["texture_ok"] = rid not in tex_missing
        entry["texture_issues"] = tex_missing.get(rid, [])
        key = f"item.coffeework.{rid}"
        entry["lang_en"] = key in en_lang
        entry["lang_zh"] = key in zh_lang
        entry["lang_ja"] = key in ja_lang
        entry["creative_tab"] = rid in creative_items
        sources = []
        if rid in recipe_outputs: sources.append("recipe")
        if rid in loot_items: sources.append("loot")
        if rid in trade_sells: sources.append("trade")
        if rid in worldgen_items: sources.append("worldgen")
        if rid in block_interact_items: sources.append("interact")
        entry["sources"] = sources
        entry["block_item"] = info.get("block_item", False)
        if info.get("block_of"):
            entry["block_of"] = info["block_of"]
        manifest.append(entry)
    
    # Blocks
    for rid, info in sorted(blocks.items()):
        registered_block_ids.add(rid)
        entry = {"id": rid, "type": "block", "registered": True}
        entry["blockstate"] = rid in blockstates
        # Use actual blockstate model references instead of name guessing
        bs_models = blockstate_models.get(rid, set())
        if bs_models:
            entry["block_model"] = all(m in block_models for m in bs_models)
            missing_models = [m for m in bs_models if m not in block_models]
            if missing_models:
                entry["block_model_missing"] = missing_models
        else:
            entry["block_model"] = False
        key = f"block.coffeework.{rid}"
        entry["lang_en"] = key in en_lang
        entry["lang_zh"] = key in zh_lang
        entry["lang_ja"] = key in ja_lang
        entry["loot_table"] = rid in loot_items
        manifest.append(entry)
    
    # Effects
    for rid, info in sorted(effects.items()):
        entry = {"id": rid, "type": "mob_effect", "registered": True}
        entry["icon"] = rid in effect_textures
        key = f"effect.coffeework.{rid}"
        entry["lang_en"] = key in en_lang
        entry["lang_zh"] = key in zh_lang
        entry["lang_ja"] = key in ja_lang
        manifest.append(entry)
    
    # Professions
    for rid, info in sorted(professions.items()):
        entry = {"id": rid, "type": "villager_profession", "registered": True}
        entry["texture"] = rid in villager_textures
        key = f"entity.minecraft.villager.coffeework.{rid}"
        entry["lang_en"] = key in en_lang
        entry["lang_zh"] = key in zh_lang
        entry["lang_ja"] = key in ja_lang
        manifest.append(entry)
    
    # POIs
    for rid, info in sorted(poi_types.items()):
        manifest.append({"id": rid, "type": "poi_type", "registered": True})
    
    # Sounds
    for rid, info in sorted(sounds.items()):
        entry = {"id": rid, "type": "sound_event", "registered": True}
        # Actually check sounds.json
        entry["in_sounds_json"] = rid in sounds_json
        if entry["in_sounds_json"]:
            snd_data = sounds_json[rid]
            snd_file = snd_data.get("sounds", [None])[0]
            if isinstance(snd_file, dict):
                snd_file = snd_file.get("name", "")
            if snd_file and isinstance(snd_file, str) and ":" in snd_file:
                ns, path = snd_file.split(":", 1)
                ogg_path = RES / "sounds" / f"{path}.ogg"
                entry["ogg_exists"] = ogg_path.exists()
                entry["ogg_size"] = ogg_path.stat().st_size if ogg_path.exists() else 0
            else:
                entry["ogg_exists"] = False
        else:
            entry["ogg_exists"] = False
        manifest.append(entry)
    
    # Classify orphan assets
    orphan_assets = []
    
    orphan_item_models = item_models - registered_item_ids
    for im in sorted(orphan_item_models):
        c = classify_orphan(im, "item_model", registered_block_ids)
        orphan_assets.append({"id": im, "type": "item_model", "classification": c})
    
    orphan_block_models = block_models - registered_block_ids
    for bm in sorted(orphan_block_models):
        c = classify_orphan(bm, "block_model", registered_block_ids)
        orphan_assets.append({"id": bm, "type": "block_model", "classification": c})
    
    orphan_blockstates = blockstates - registered_block_ids
    for bs in sorted(orphan_blockstates):
        c = classify_orphan(bs, "blockstate", registered_block_ids)
        orphan_assets.append({"id": bs, "type": "blockstate", "classification": c})
    
    # Orphan textures
    all_reg_tex = registered_item_ids | registered_block_ids
    orphan_item_textures = item_textures - all_reg_tex
    for ot in sorted(orphan_item_textures):
        if ot not in item_models and ot not in block_models:
            c = classify_orphan(ot, "texture", registered_block_ids)
            if c != "UNCLASSIFIED":
                orphan_assets.append({"id": ot, "type": "item_texture", "classification": c})
    
    # Counts
    stats = {
        "registered": len(manifest),
        "by_type": defaultdict(int),
        "orphan_assets": len(orphan_assets),
        "orphan_by_classification": defaultdict(int),
        "completeness": {
            "model_ok": 0, "texture_ok": 0,
            "lang_en_ok": 0, "lang_zh_ok": 0, "lang_ja_ok": 0,
            "creative_tab_ok": 0, "has_source": 0,
            "total_items": len(items),
        }
    }
    
    for e in manifest:
        stats["by_type"][e["type"]] += 1
        if e["type"] == "item":
            if e.get("model"): stats["completeness"]["model_ok"] += 1
            if e.get("texture_ok", True): stats["completeness"]["texture_ok"] += 1
            if e.get("lang_en"): stats["completeness"]["lang_en_ok"] += 1
            if e.get("lang_zh"): stats["completeness"]["lang_zh_ok"] += 1
            if e.get("lang_ja"): stats["completeness"]["lang_ja_ok"] += 1
            if e.get("creative_tab"): stats["completeness"]["creative_tab_ok"] += 1
            if e.get("sources"): stats["completeness"]["has_source"] += 1
    
    for oa in orphan_assets:
        stats["orphan_by_classification"][oa["classification"]] += 1
    
    output = {
        "stats": {k: dict(v) if isinstance(v, defaultdict) else v for k, v in stats.items()},
        "registry": manifest,
        "orphan_assets": orphan_assets,
    }
    
    # Write JSON
    json_path = ROOT / "docs" / "content_manifest.json"
    with open(json_path, "w", encoding="utf-8") as f:
        json.dump(output, f, indent=2, ensure_ascii=False)
    
    # Write Markdown
    md = build_markdown_report(output)
    md_path = ROOT / "docs" / "CONTENT_MANIFEST.md"
    md_path.write_text(md, encoding="utf-8")
    
    # Print summary
    comp = stats["completeness"]
    ti = comp["total_items"]
    print(f"\nItem Completeness ({ti} items):")
    for label, key in [("Model", "model_ok"), ("Texture", "texture_ok"),
                        ("en_us", "lang_en_ok"), ("zh_cn", "lang_zh_ok"),
                        ("ja_jp", "lang_ja_ok"), ("Creative Tab", "creative_tab_ok"),
                        ("Source", "has_source")]:
        val = comp.get(key, 0)
        print(f"  {label}: {val}/{ti} ({val*100//ti}%)" if ti > 0 else f"  {label}: {val}/0")
    
    print(f"\nOrphan Assets: {stats['orphan_assets']}")
    for cls, count in sorted(stats["orphan_by_classification"].items()):
        print(f"  {cls}: {count}")
    
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
            ("en_us Key Coverage", "lang_en_ok"),
            ("zh_cn Key Coverage", "lang_zh_ok"),
            ("ja_jp Key Coverage", "lang_ja_ok"),
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
