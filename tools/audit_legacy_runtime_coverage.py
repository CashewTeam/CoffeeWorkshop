#!/usr/bin/env python3
"""
Coffee Workshop — Legacy Asset Runtime Coverage Audit (v2)

Reads the current content manifest, then recursively resolves which orphan
(legacy) assets are already in use by registered game content:
  - Blockstate → Block Model → Parent chain (only for registered blocks)
  - Item Model → Parent chain → Texture (actively used by registered items)
  - Recipe intermediates (items appearing in recipe JSONs)
  - Machine state variants (_on/_off) mapped to LIT blockstate

Each asset is tracked with a typed key: (asset_type, asset_id) — a block model
named "coffee" is NOT matched by a registered item "coffee".

Classifies each orphan asset into the new status system:
  ACTIVE_RUNTIME_ASSET   — used by an existing blockstate / renderer / entity
  TO_PORT_STANDALONE     — needs registration as independent Item/Block
  TO_PORT_INTERMEDIATE   — needs to be a crafting intermediate (raw/model/base)
  TO_WIRE_STATE_VARIANT  — needs to attach to existing block states
  TO_WIRE_DISPLAY_VARIANT— needs display system integration (_plate models)
  TO_PORT_MACHINE        — needs machine/device restoration
  TO_PORT_DECOR          — needs decor block registration
  MERGED_RUNTIME_VARIANT — old ID merged into modern state, capability preserved
  UNASSIGNED             — not yet determined (target: 0)

Output:
  build/reports/legacy-runtime-coverage.md
  build/reports/legacy-runtime-coverage.json
  docs/legacy_content_matrix.json
  docs/LEGACY_CONTENT_MATRIX.md
"""

import json
import re
import sys
from collections import defaultdict
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS_ROOT = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
DATA_ROOT = REPO_ROOT / "src" / "main" / "resources" / "data" / "coffeework"
GEN_DATA_ROOT = REPO_ROOT / "src" / "generated" / "resources" / "data" / "coffeework"
MANIFEST_PATH = REPO_ROOT / "docs" / "content_manifest.json"
BASELINE_PATH = REPO_ROOT / "docs" / "legacy_asset_baseline.json"
LEGACY_MATRIX_JSON = REPO_ROOT / "docs" / "legacy_content_matrix.json"
LEGACY_MATRIX_MD = REPO_ROOT / "docs" / "LEGACY_CONTENT_MATRIX.md"
REPORT_DIR = REPO_ROOT / "build" / "reports"
REPORT_MD = REPORT_DIR / "legacy-runtime-coverage.md"
REPORT_JSON = REPORT_DIR / "legacy-runtime-coverage.json"

BLOCKSTATES_DIR = ASSETS_ROOT / "blockstates"
BLOCK_MODELS_DIR = ASSETS_ROOT / "models" / "block"
ITEM_MODELS_DIR = ASSETS_ROOT / "models" / "item"
TEXTURES_BLOCK = ASSETS_ROOT / "textures" / "block"
TEXTURES_ITEM = ASSETS_ROOT / "textures" / "item"
NAMESPACE = "coffeework"


# ── New classification system ──────────────────────────────────────────

STATUS_ACTIVE = "ACTIVE_RUNTIME_ASSET"
STATUS_STANDALONE = "TO_PORT_STANDALONE"
STATUS_INTERMEDIATE = "TO_PORT_INTERMEDIATE"
STATUS_STATE_VARIANT = "TO_WIRE_STATE_VARIANT"
STATUS_DISPLAY_VARIANT = "TO_WIRE_DISPLAY_VARIANT"
STATUS_MACHINE = "TO_PORT_MACHINE"
STATUS_DECOR = "TO_PORT_DECOR"
STATUS_MERGED = "MERGED_RUNTIME_VARIANT"
STATUS_UNASSIGNED = "UNASSIGNED"


# ── Content family grouping ────────────────────────────────────────────
#
# Patterns are ordered from most specific to least specific so that
# "cookie_icecream" matches before "icecream", and "coffee_pot_" matches
# before "coffee_".

FAMILY_PATTERNS = [
    # ═══ CONTENT-STRUCTURE FIRST (prefix-based) ═══
    # These match on content structure, not keyword. Must come before any
    # generic coffee/icecream patterns to prevent cake_coffee_roll being
    # misclassified as coffee_drinks.

    # Jiggy cakes (must be before cake_ to prevent substring match)
    ("jiggy_cake", "cake_jiggy", "7.1"),

    # Cake system
    ("cake_slices", "cake_system", "5.4"),
    ("cake_sponge_", "cake_system", "5.4"),
    ("cake_cheese", "cake_system", "5.4"),
    ("cake_coffee", "cake_system", "5.4"),
    ("cake_harvest", "cake_system", "5.4"),
    ("cake_lemon", "cake_system", "5.4"),
    ("cake_tea", "cake_system", "5.4"),
    ("cake_berry", "cake_system", "5.4"),
    ("cake_chocolate", "cake_system", "5.4"),
    ("cake_carrot", "cake_system", "5.4"),
    ("cake_pumpkin", "cake_system", "5.4"),
    ("cake_redvelvet", "cake_system", "5.4"),
    ("cake_schwarzwald", "cake_system", "5.4"),
    ("cake_model", "cake_system", "5.4"),
    ("cake_roll", "cake_roll", "5.4"),
    ("cake_", "cake_system", "5.4"),
    ("tiramisu", "cake_system", "5.4"),
    ("mousse_", "cake_mousse", "5.4"),

    # Muffins
    ("muffin_", "pastry", "7.1"),
    ("muffin", "pastry", "7.1"),

    # Pies
    ("pie_", "pie", "7.0"),

    # Jiggy cakes
    ("jiggy_cake", "cake_jiggy", "7.1"),

    # Cookie ice cream (must be before icecream_)
    ("cookie_icecream", "icecream_cookie", "6.0"),

    # Creams (must be before icecream_ and coffee_)
    ("cream_milk", "icecream_cream", "6.0"),
    ("cream_apple", "icecream_cream", "6.0"),
    ("cream_berry", "icecream_cream", "6.0"),
    ("cream_chocolate", "icecream_cream", "6.0"),
    ("cream_coffee", "icecream_cream", "6.0"),
    ("cream_lemon", "icecream_cream", "6.0"),
    ("cream_melon", "icecream_cream", "6.0"),

    # Ice cream
    ("icecream_mix", "icecream", "5.2"),
    ("icecream_vanilla", "icecream", "5.2"),
    ("icecream_apple", "icecream", "5.2"),
    ("icecream_berry", "icecream", "5.2"),
    ("icecream_chocolate", "icecream", "5.2"),
    ("icecream_coffee", "icecream", "5.2"),
    ("icecream_lemon", "icecream", "5.2"),
    ("icecream_melon", "icecream", "5.2"),
    ("icecream_machine", "icecream", "5.2"),
    ("icecreammachine", "icecream", "5.2"),
    ("icecream_", "icecream", "5.2"),

    # Traditional brew (must be before coffee_)
    ("coffee_pot_", "traditional_brew", "9.0"),

    # Sandwiches
    ("sandwich_", "sandwich", "5.2"),

    # Mooncakes, soufflés
    ("mooncake", "pastry", "7.1"),
    ("souffle", "pastry", "7.1"),

    # Pastries
    ("croissant", "pastry", "7.0"),
    ("ginger_bread", "pastry", "7.0"),
    ("puff", "pastry", "7.0"),
    ("mille_feuille", "pastry", "7.0"),

    # Bakery and confectionery
    ("brownie", "bakery", "5.3"),
    ("caramel_apple", "confectionery", "5.3"),
    ("caramel", "confectionery", "5.3"),
    ("custard", "confectionery", "5.3"),
    ("milk_form", "confectionery", "5.3"),
    ("hardtack", "confectionery", "5.3"),
    ("cookie_black", "confectionery", "5.3"),
    ("cookie_oreo", "confectionery", "5.3"),
    ("marshmallow_chocolate", "confectionery", "5.3"),
    ("marshmallow_roast", "confectionery", "5.3"),
    ("marshmallow", "confectionery", "5.3"),
    ("smore", "confectionery", "5.3"),

    # Machines / decor
    ("soda_machine", "soda_machine", "9.1"),
    ("moka_", "traditional_brew", "9.0"),
    ("turkey_", "traditional_brew", "9.0"),
    ("phonograph", "decor_phonograph", "9.2"),
    ("bar_stone", "decor_bar", "9.2"),
    ("bar_wooden", "decor_bar", "9.2"),
    ("clay_oven", "machines", "5.1"),
    ("grinder_", "machines", "5.1"),
    ("roller_", "machines", "5.1"),
    ("oven_", "machines", "5.1"),
    ("coffeemachine", "machines", "5.1"),

    # Doughs
    ("plate_dough", "dough_system", "5.3"),
    ("dough_", "dough_system", "5.3"),

    # Bags
    ("double_bag_", "bags", "5.1"),
    ("bag_coffee_raw", "bags", "5.1"),
    ("bag_coffee_powder", "bags", "5.1"),
    ("bag_cocoa", "bags", "5.1"),
    ("bag_cocoa_powder", "bags", "5.1"),
    ("bag_flour", "bags", "5.1"),
    ("bag_sugar", "bags", "5.1"),
    ("bag_coffee", "bags", "5.1"),
    ("bag_cloth", "bags", "5.1"),
    ("bag", "bags", "5.1"),

    # Syrups, plates, records
    ("syrup_", "syrups", "5.2"),
    ("plate_iron", "materials", "5.1"),
    ("records_", "records", "5.2"),
    ("record_", "records", "5.2"),

    # Crops
    ("vanilla_stage", "crops", "5.1"),
    ("blueberry_stage", "crops", "5.1"),
    ("crop_coffee", "crops", "5.1"),

    # ═══ KEYWORD-BASED (LAST) ═══
    # These must be AFTER all content-structure patterns.

    # Coffee drinks (keyword — last in family list)
    ("coffee_coldbrew", "coffee_drinks", "5.2"),
    ("coffee_americano", "coffee_drinks", "5.2"),
    ("coffee_latte", "coffee_drinks", "5.2"),
    ("coffee_cappuccino", "coffee_drinks", "5.2"),
    ("coffee_macchiato", "coffee_drinks", "5.2"),
    ("coffee_mochaccino", "coffee_drinks", "5.2"),
    ("coffee_green_tea", "coffee_drinks", "5.2"),
    ("coffee_black_tea", "coffee_drinks", "5.2"),
    ("coffee_milk_tea", "coffee_drinks", "5.2"),
    ("coffee_mandarin", "coffee_drinks", "5.2"),
    ("coffee_instant", "coffee_drinks", "5.2"),
    ("coffee_bean", "coffee_drinks", "5.2"),
    ("coffee_powder", "coffee_drinks", "5.2"),
    ("coffee_seed", "coffee_drinks", "5.2"),
    ("coffee_ice", "coffee_drinks", "5.2"),
    ("coffee_", "coffee_drinks", "5.2"),
    ("coffee", "coffee_drinks", "5.2"),
    ("cocoa", "coffee_drinks", "5.2"),
    ("coldbrew", "coffee_drinks", "5.2"),
    ("espresso", "coffee_drinks", "5.2"),

    # Misc materials (keyword catch-alls, absolute last)
    ("d_bar", "materials", "5.1"),
    ("dirty_pastry_bun", "materials", "5.1"),
    ("pot", "traditional_brew", "9.0"),
    ("plate", "materials", "5.1"),
    ("iron_bowl", "materials", "5.1"),
    ("field_ration", "materials", "5.1"),
    ("ice_slag", "materials", "5.1"),
]


# ── Known blockstate → block mappings (old ID → registered owner) ──────

BLOCKSTATE_WIRED = {
    "grinder_on": ("grinder", "blockstate:lit=true"),
    "grinder_off": ("grinder", "blockstate:lit=false"),
    "oven_on": ("oven", "blockstate:lit=true"),
    "oven_off": ("oven", "blockstate:lit=false"),
    "roller_on": ("roller", "blockstate:lit=true"),
    "roller_off": ("roller", "blockstate:lit=false"),
    "icecream_machine_on": ("icecream_machine", "blockstate:lit=true"),
    "icecream_machine_off": ("icecream_machine", "blockstate:lit=false"),
    "icecreammachine_on": ("icecream_machine", "blockstate:lit=true"),
    "icecreammachine_off": ("icecream_machine", "blockstate:lit=false"),
    "coffeemachine_on": ("coffee_machine", "blockstate:lit=true"),
    "coffeemachine_off": ("coffee_machine", "blockstate:lit=false"),
    "coffee_machine_on": ("coffee_machine", "blockstate:lit=true"),
    "coffee_machine_off": ("coffee_machine", "blockstate:lit=false"),
    "clay_oven": ("oven", "blockstate:lit=false"),
    "clay_oven_on": ("oven", "blockstate:lit=true"),
}


def _read_json(path: Path) -> Any:
    if not path.exists():
        return None
    with open(path, "r", encoding="utf-8") as f:
        return json.load(f)


def _load_manifest() -> dict:
    return _read_json(MANIFEST_PATH) or {"orphan_assets": [], "registry": [], "stats": {}}


def _resolve_model_chain(model_name: str, model_dir: Path) -> dict:
    """Recursively resolve a model JSON.
    Returns {"models": set(), "textures": set()} — never mixed.
    """
    seen_models = set()
    seen_textures = set()
    stack = [(model_name, "model")]
    while stack:
        name, kind = stack.pop()
        if kind == "model" and name in seen_models:
            continue
        if kind in ("texture",) and name in seen_textures:
            continue
        if kind == "model":
            seen_models.add(name)
            path = model_dir / f"{name}.json"
            data = _read_json(path)
            if not data:
                continue
            parent = data.get("parent", "")
            if parent.startswith(f"{NAMESPACE}:block/"):
                pname = parent[len(f"{NAMESPACE}:block/"):]
                stack.append((pname, "model"))
            elif parent.startswith(f"{NAMESPACE}:item/"):
                pname = parent[len(f"{NAMESPACE}:item/"):]
                stack.append((pname, "model"))
            textures = data.get("textures", {})
            for tex_ref in textures.values():
                if isinstance(tex_ref, str) and tex_ref.startswith(f"{NAMESPACE}:"):
                    tail = tex_ref[len(f"{NAMESPACE}") + 1:]
                    if tail.startswith("block/"):
                        ref_name = tail[len("block/"):]
                        # Texture reference to a block model file — follow it
                        stack.append((ref_name, "model"))
                    elif tail.startswith("item/"):
                        ref_name = tail[len("item/"):]
                        texture_path = TEXTURES_ITEM / f"{ref_name}.png"
                        if texture_path.exists():
                            seen_textures.add(ref_name)
                        # Also try as a model parent
                        stack.append((ref_name, "model"))
        elif kind == "texture":
            seen_textures.add(name)
    return {"models": seen_models, "textures": seen_textures}


def _collect_active_blockstate_refs(registry: list) -> dict:
    """Return {asset_id: (runtime_owner, runtime_role)} for blockstate-referenced assets.
    Only scans blockstates for REGISTERED blocks — unregistered blockstates
    are NOT counted as active.
    """
    registered_block_ids = {
        e["id"] for e in registry
        if e.get("registered") and e.get("type") == "block"
    }
    active = {}
    for bs_path in sorted(BLOCKSTATES_DIR.glob("*.json")):
        bs_name = bs_path.stem
        # P0 fix: only scan blockstates for registered blocks
        if bs_name not in registered_block_ids:
            continue
        data = _read_json(bs_path)
        if not data:
            continue
        variants = data.get("variants", {})
        for variant_key, variant_val in variants.items():
            models = variant_val if isinstance(variant_val, list) else [variant_val]
            for model_entry in models:
                model_ref = model_entry.get("model", "")
                if model_ref.startswith(f"{NAMESPACE}:block/"):
                    model_id = model_ref[len(f"{NAMESPACE}:block/"):]
                    active[model_id] = (bs_name, f"blockstate:variants={variant_key}")
                    chain = _resolve_model_chain(model_id, BLOCK_MODELS_DIR)
                    for ref in chain.get("models", set()):
                        if ref != model_id and ref not in active:
                            active[ref] = (bs_name, f"blockstate:parent_of_{model_id}")
                    # Also track textures
                    for tex in chain.get("textures", set()):
                        if tex not in active:
                            active[tex] = (bs_name, f"blockstate:texture_of_{model_id}")
    return active


def _collect_registered_item_models(registry: list) -> dict:
    """Return dict of (type, asset_id) → True for asset IDs actively used
    by registered items.  Uses typed keys: ("item_model", id) and ("block_model", id).
    """
    used = {}
    for entry in registry:
        eid = entry["id"]
        if not entry.get("registered"):
            continue
        etype = entry.get("type", "")
        if etype == "item":
            if (ITEM_MODELS_DIR / f"{eid}.json").exists():
                used[("item_model", eid)] = True
            if entry.get("block_item") and entry.get("block_of"):
                bid = entry["block_of"]
                if (BLOCK_MODELS_DIR / f"{bid}.json").exists():
                    used[("block_model", bid)] = True
        elif etype == "block":
            if (BLOCKSTATES_DIR / f"{eid}.json").exists():
                used[("blockstate", eid)] = True
            if (BLOCK_MODELS_DIR / f"{eid}.json").exists():
                used[("block_model", eid)] = True
    return used


def _collect_recipe_intermediates() -> dict:
    """Find items that appear as recipe outputs. Returns {item_id: recipe_count}."""
    intermediates = defaultdict(int)
    recipe_dirs = [GEN_DATA_ROOT / "recipes", DATA_ROOT / "recipes"]
    for rd in recipe_dirs:
        if not rd.exists():
            continue
        for recipe_file in rd.rglob("*.json"):
            data = _read_json(recipe_file)
            if not data:
                continue
            result = data.get("result", {})
            if isinstance(result, dict):
                rname = result.get("item", "")
                if rname.startswith(f"{NAMESPACE}:"):
                    intermediates[rname[len(f"{NAMESPACE}:"):]] += 1
            elif isinstance(result, str) and result.startswith(f"{NAMESPACE}:"):
                intermediates[result[len(f"{NAMESPACE}:"):]] += 1
    return dict(intermediates)


def _determine_family(asset_id: str) -> tuple:
    """Return (family_name, target_phase) for an asset.
    Patterns are ordered most-specific-first to prevent misclassification.
    """
    for pattern, family, phase in FAMILY_PATTERNS:
        if pattern in asset_id:
            return (family, phase)
    return ("unclassified", "TBD")


def _classify_orphan(orphan: dict, registry_ids: set,
                     registered_item_ids: set, registered_block_ids: set,
                     active_blockstate: dict, active_typed: dict,
                     recipe_outputs: dict) -> dict:
    """Classify a single orphan asset into the new status system.
    
    Uses separated ID sets:
    - registered_item_ids: only item registry IDs
    - registered_block_ids: only block registry IDs
    """
    asset_id = orphan["id"]
    asset_type = orphan["type"]
    old_class = orphan.get("classification", "UNKNOWN")
    family, phase = _determine_family(asset_id)
    typed_key = (asset_type, asset_id)

    result = {
        "asset_id": asset_id,
        "asset_type": asset_type,
        "content_family": family,
        "runtime_owner": None,
        "runtime_role": None,
        "target_owner": None,
        "target_phase": phase,
        "status": STATUS_UNASSIGNED,
        "source": "legacy_1_12",
        "survival_chain": "",
        "old_classification": old_class,
        "notes": "",
    }

    # ── Rule 1: Already registered (typed check with separated sets) ──
    if asset_type == "item_model" and asset_id in registered_item_ids:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_item_model"
        return result
    if asset_type == "block_model" and asset_id in registered_block_ids:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_block_model"
        return result
    if asset_type == "blockstate" and asset_id in registered_block_ids:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_blockstate"
        return result

    # ── Rule 2: Wired to existing blockstate ──
    if asset_id in BLOCKSTATE_WIRED:
        owner, role = BLOCKSTATE_WIRED[asset_id]
        result["status"] = STATUS_MERGED
        result["runtime_owner"] = f"coffeework:{owner}"
        result["runtime_role"] = role
        result["notes"] = "Old ID merged into modern blockstate variant"
        return result

    # ── Rule 3: Used by active blockstate chain (only registered blocks) ──
    if asset_id in active_blockstate:
        owner, role = active_blockstate[asset_id]
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{owner}"
        result["runtime_role"] = role
        return result

    # ── Rule 4: Used by registered item model (typed check) ──
    if typed_key in active_typed:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_model_or_texture"
        return result

    # ── Rule 5: Appears as recipe output → standalone item ──
    if asset_id in recipe_outputs:
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "recipe_output"
        result["target_owner"] = f"coffeework:{asset_id}"
        result["survival_chain"] = f"recipe ({recipe_outputs[asset_id]} recipe(s))"
        return result

    # ── Heuristic rules based on naming patterns ──

    # Machine state variants (_on, _off)
    if asset_id.endswith("_on") or asset_id.endswith("_off"):
        base = asset_id[:-3] if asset_id.endswith("_on") else asset_id[:-4]
        if base in registry_ids:
            result["status"] = STATUS_MERGED
            result["runtime_owner"] = f"coffeework:{base}"
            result["runtime_role"] = f"blockstate:lit={'true' if asset_id.endswith('_on') else 'false'}"
            return result

    # Cake slice models
    if any(asset_id.endswith(s) for s in [f"_slice{i}" for i in range(1, 7)] + ["_uneaten"]):
        m = re.match(r"^(.+?)_(slice[1-6]|uneaten)$", asset_id)
        if m:
            cake_name = m.group(1)
            result["status"] = STATUS_STATE_VARIANT
            result["runtime_owner"] = f"coffeework:{cake_name}" if cake_name in registry_ids else None
            result["target_owner"] = f"coffeework:{cake_name}"
            result["runtime_role"] = "blockstate:bites"
            result["notes"] = "Cake slice model — wire to BITES property"
            return result

    # Raw intermediates
    if asset_id.endswith("_raw"):
        base = asset_id[:-4]
        owner = f"coffeework:{base}" if base in registry_ids else None
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_owner"] = owner
        result["target_owner"] = f"coffeework:{base}"
        result["runtime_role"] = "intermediate:raw"
        result["survival_chain"] = f"raw material for {base}"
        return result

    # Mold items
    if asset_id.endswith("_model"):
        base = asset_id[:-6]
        owner = f"coffeework:{base}" if base in registry_ids else None
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_owner"] = owner
        result["target_owner"] = f"coffeework:{base}"
        result["runtime_role"] = "intermediate:mold_form"
        result["survival_chain"] = f"mold form for {base}"
        return result

    # Base layers
    if asset_id.endswith("_base"):
        base = asset_id[:-5]
        owner = f"coffeework:{base}" if base in registry_ids else None
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_owner"] = owner
        result["target_owner"] = f"coffeework:{base}"
        result["runtime_role"] = "intermediate:base_layer"
        result["survival_chain"] = f"base layer for {base}"
        return result

    # Plate models → display system
    if asset_id.endswith("_plate"):
        drink_id = asset_id[:-6]
        owner = f"coffeework:{drink_id}" if drink_id in registry_ids else None
        result["status"] = STATUS_DISPLAY_VARIANT
        result["runtime_owner"] = owner
        result["target_owner"] = f"coffeework:{drink_id}"
        result["runtime_role"] = "drink_display:plate_model"
        result["notes"] = "Plate model — wire to DrinkDisplayBlock system"
        return result

    # Cake roll models
    if asset_id.endswith("_roll"):
        result["status"] = STATUS_STATE_VARIANT
        result["runtime_role"] = "cake_roll_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Cake slices item
    if asset_id.endswith("_slices") and not any(asset_id.endswith(f"_slice{i}") for i in range(1, 7)):
        base = asset_id[:-7]
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "slice_item"
        result["target_owner"] = f"coffeework:{base}"
        result["survival_chain"] = f"interact with {base} cake block"
        return result

    # Coldbrew stages
    if asset_id.startswith("coldbrew_pot_"):
        result["status"] = STATUS_STATE_VARIANT
        result["runtime_owner"] = "coffeework:coldbrew_pot"
        result["runtime_role"] = "blockstate:ferm"
        return result

    # Machine-related
    for machine_prefix in ["soda_machine", "moka_", "turkey_", "coffee_pot_"]:
        if asset_id.startswith(machine_prefix):
            result["status"] = STATUS_MACHINE
            result["runtime_role"] = "machine_component"
            result["target_owner"] = f"coffeework:{asset_id}"
            return result

    # Bar furniture
    if asset_id.startswith("bar_"):
        result["status"] = STATUS_DECOR
        result["runtime_role"] = "bar_furniture"
        result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Phonograph
    if "phonograph" in asset_id:
        result["status"] = STATUS_MACHINE if asset_type == "blockstate" else STATUS_DECOR
        result["runtime_role"] = "phonograph"
        result["target_owner"] = f"coffeework:phonograph"
        return result

    # Syrups — old naming variants
    if asset_id == "syrup_brown":
        result["status"] = STATUS_MERGED
        result["runtime_owner"] = "coffeework:syrup_caramel"
        result["runtime_role"] = "old_naming_variant"
        result["notes"] = "Renamed to syrup_caramel"
        return result
    if asset_id == "syrup_full":
        result["status"] = STATUS_MERGED
        result["runtime_owner"] = "coffeework:syrup_vanilla"
        result["runtime_role"] = "old_naming_variant"
        result["notes"] = "Old universal syrup, superseded by specific flavors"
        return result

    # ── Rule 6: Old naming variants merged into modern IDs ──
    old_name_merged = {
        "coffee": ("coffee_americano", "generic coffee old-name replaced by specific drinks"),
        "coffee_bean_light": ("coffee_bean", "old roasted bean name"),
        "coffee_ice": ("coffee_americano_ice", "old generic iced coffee name"),
        "coffee_powder_light": ("coffee_powder", "old powder name"),
        "coffee_seed": ("coffee_seeds", "singular→plural rename"),
        "crop_coffee": ("coffee_tree", "old crop model name"),
        "field_ration_d": ("field_ration", "old variant of field_ration"),
        "plate_ginger": ("plate_dough_ginger", "old name for plate_dough_ginger"),
        "plate_pastry": ("plate_dough_pastry", "old name for plate_dough_pastry"),
        "iron_bowl_cheese": ("cheese", "old bowl+cheese intermediate"),
        "iron_bowl_egg": ("dough", "old bowl+egg intermediate"),
        "records_kusa_noshi_to_ne": ("record_kusa_noshi_to_ne", "old plural→singular record name"),
        "records_lazy_lady_kaguya": ("record_lazy_lady_kaguya", "old plural→singular record name"),
        "records_the_grimoire_of_marisa": ("record_the_grimoire_of_marisa", "old plural→singular record name"),
    }
    if asset_id in old_name_merged:
        owner, note = old_name_merged[asset_id]
        result["status"] = STATUS_MERGED
        result["runtime_owner"] = f"coffeework:{owner}"
        result["runtime_role"] = "old_naming_variant"
        result["notes"] = note
        return result

    # ── Rule 7: Iron bowl batter variants → intermediates ──
    if asset_id.startswith("iron_bowl_batter"):
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_role"] = "intermediate:iron_bowl_batter"
        result["target_owner"] = f"coffeework:{asset_id}"
        result["survival_chain"] = "mixing bowl intermediate for cake batter"
        result["notes"] = "Old bowl+batter system, needs redesign into modern mold system"
        return result

    # Known PORT_NOW items → standalone
    port_now_set = {
        "coffee_instant_cup", "coffee_instant_cup_unopen",
        "icecream_apple", "icecream_berry", "icecream_chocolate",
        "icecream_coffee", "icecream_lemon", "icecream_melon",
        "sandwich_bacon_egg", "sandwich_beef_cheese", "sandwich_blt_large",
        "sandwich_club", "sandwich_club_large", "sandwich_ham_cheese",
    }
    if asset_id in port_now_set:
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        result["survival_chain"] = "phase_5_2"
        return result

    # Old PORT_LATER / REMOVED items → standalone
    port_later_standalone = {
        "cream_apple", "cream_berry", "cream_chocolate", "cream_coffee",
        "cream_lemon", "cream_melon", "cream_milk",
        "cookie_icecream_apple", "cookie_icecream_berry", "cookie_icecream_chocolate",
        "cookie_icecream_coffee", "cookie_icecream_lemon", "cookie_icecream_melon",
        "cookie_icecream_vanilla",
        "caramel", "caramel_apple", "custard", "milk_form", "hardtack",
        "cookie_black", "cookie_oreo", "marshmallow", "marshmallow_roast",
        "marshmallow_chocolate", "smore",
        "souffle", "souffle_chocolate",
        "croissant", "croissant_chocolate",
        "ginger_bread", "ginger_bread_man",
        "puff", "mille_feuille",
    }
    if asset_id in port_later_standalone:
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        result["survival_chain"] = f"phase_{family}" if family != "unclassified" else "later_phase"
        return result

    # Muffin: catch both "muffin_xxx" and standalone "muffin"
    if asset_id == "muffin" or (asset_id.startswith("muffin_") and not asset_id.endswith("_raw")):
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Pie items
    if asset_id.startswith("pie_") and not asset_id.endswith("_raw"):
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Cake finished items
    if (asset_id.startswith("cake_") and asset_id not in registry_ids
            and not asset_id.endswith("_raw") and not asset_id.endswith("_base")
            and not asset_id.endswith("_model") and not asset_id.endswith("_plate")
            and not any(asset_id.endswith(f"_slice{i}") for i in range(1, 7))
            and not asset_id.endswith("_uneaten") and not asset_id.endswith("_slices")
            and not asset_id.endswith("_roll")):
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Jiggy cake items
    if "jiggy" in asset_id:
        if asset_id.endswith("_raw") or asset_id.endswith("_model"):
            result["status"] = STATUS_INTERMEDIATE
            result["runtime_role"] = "intermediate:jiggy_raw_or_model"
        else:
            result["status"] = STATUS_STANDALONE
            result["runtime_role"] = "standalone_item"
            result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Mooncake items
    if "mooncake" in asset_id:
        if asset_id.endswith("_raw"):
            result["status"] = STATUS_INTERMEDIATE
            result["runtime_role"] = "intermediate:mooncake_raw"
        elif asset_id == "mooncake_model":
            result["status"] = STATUS_ACTIVE
            result["runtime_owner"] = "coffeework:mooncake_model"
            result["runtime_role"] = "registered_item"
        else:
            result["status"] = STATUS_STANDALONE
            result["runtime_role"] = "standalone_item"
            result["target_owner"] = f"coffeework:{asset_id}"
        return result

    # Default: fallback based on old classification
    if old_class == "PORT_NOW":
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
    elif old_class == "PORT_LATER":
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
    elif old_class == "REMOVED":
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "standalone_item"
        result["target_owner"] = f"coffeework:{asset_id}"
    elif old_class == "REDESIGN":
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "redesign_item"
        result["target_owner"] = f"coffeework:{asset_id}"
    elif old_class == "MERGED":
        result["status"] = STATUS_MERGED
        result["runtime_role"] = "merged_variant"
    elif old_class == "ASSET_ARCHIVE":
        # Last resort for unowned assets
        result["status"] = STATUS_STANDALONE
        result["runtime_role"] = "unowned_archive"
        result["target_owner"] = f"coffeework:{asset_id}"

    return result


def _build_summary(classified: list, total_baseline: int, baseline_registered: int) -> dict:
    """Build summary statistics with new quality metrics."""
    total = len(classified)
    by_status = defaultdict(int)
    by_family = defaultdict(lambda: {"total": 0, "by_status": defaultdict(int)})
    by_type = defaultdict(int)
    missing_target_owner = 0
    missing_runtime_owner = 0
    tbd_phase = 0

    # Unique display variant counting (deduplicate by drink ID, not asset file)
    display_asset_count = 0
    display_unique_ids = set()

    for entry in classified:
        by_status[entry["status"]] += 1
        by_type[entry["asset_type"]] += 1
        fam = by_family[entry["content_family"]]
        fam["total"] += 1
        fam["by_status"][entry["status"]] += 1

        st = entry["status"]

        # Quality gate: ACTIVE/MERGED must have runtime_owner
        if st in (STATUS_ACTIVE, STATUS_MERGED):
            if entry.get("runtime_owner") is None:
                missing_runtime_owner += 1
        else:
            # Non-ACTIVE must have target_owner
            if entry.get("target_owner") is None:
                missing_target_owner += 1

        if entry.get("target_phase") == "TBD":
            tbd_phase += 1

        # Unique display variant counting
        if st == STATUS_DISPLAY_VARIANT:
            display_asset_count += 1
            to = entry.get("target_owner", "")
            if to:
                display_unique_ids.add(to)

    return {
        "total_legacy_assets": total,
        "total_baseline_assets": total_baseline,
        "restored_from_baseline": baseline_registered,
        "by_status": dict(by_status),
        "by_type": dict(by_type),
        "by_family": {k: {"total": v["total"], "by_status": dict(v["by_status"])}
                      for k, v in sorted(by_family.items())},
        "active_runtime": by_status.get(STATUS_ACTIVE, 0),
        "merged": by_status.get(STATUS_MERGED, 0),
        "to_port_standalone": by_status.get(STATUS_STANDALONE, 0),
        "to_port_intermediate": by_status.get(STATUS_INTERMEDIATE, 0),
        "to_wire_state": by_status.get(STATUS_STATE_VARIANT, 0),
        "to_wire_display": by_status.get(STATUS_DISPLAY_VARIANT, 0),
        "to_port_machine": by_status.get(STATUS_MACHINE, 0),
        "to_port_decor": by_status.get(STATUS_DECOR, 0),
        "unassigned": by_status.get(STATUS_UNASSIGNED, 0),
        "missing_target_owner": missing_target_owner,
        "missing_runtime_owner": missing_runtime_owner,
        "tbd_phase": tbd_phase,
        "display_asset_files": display_asset_count,
        "display_unique_variants": len(display_unique_ids),
    }


def _format_md(summary: dict, classified: list) -> str:
    """Generate human-readable Markdown report."""
    out = []
    out.append("# Coffee Workshop — Legacy Asset Runtime Coverage (v2)")
    out.append("")
    out.append("> Auto-generated by `tools/audit_legacy_runtime_coverage.py`")
    out.append("")

    # Quality gates
    out.append("## Quality Gates")
    out.append("")
    out.append(f"| Metric | Count | Target |")
    out.append(f"|---|---|---|")
    out.append(f"| UNASSIGNED status | {summary['unassigned']} | 0 |")
    out.append(f"| Missing target_owner (non-ACTIVE) | {summary['missing_target_owner']} | 0 |")
    out.append(f"| Missing runtime_owner (ACTIVE/MERGED) | {summary['missing_runtime_owner']} | 0 |")
    out.append(f"| TBD phase | {summary['tbd_phase']} | 0 |")
    out.append("")

    # Summary
    out.append("## Summary")
    out.append("")
    out.append(f"| Metric | Count |")
    out.append(f"|---|---|")
    out.append(f"| **Fixed baseline assets** | **{summary.get('total_baseline_assets', summary['total_legacy_assets'])}** |")
    out.append(f"| Restored (now registered) | +{summary.get('restored_from_baseline', 0)} |")
    out.append(f"| Total legacy assets (tracked) | {summary['total_legacy_assets']} |")
    out.append(f"| **ACTIVE_RUNTIME_ASSET** (already in use) | {summary['active_runtime']} |")
    out.append(f"| **MERGED_RUNTIME_VARIANT** (old ID merged) | {summary['merged']} |")
    out.append(f"| **→ Runtime covered** | **{summary['active_runtime'] + summary['merged']}/{summary.get('total_baseline_assets', summary['total_legacy_assets'])}** |")
    out.append(f"| TO_PORT_STANDALONE (needs registration) | {summary['to_port_standalone']} |")
    out.append(f"| TO_PORT_INTERMEDIATE (raw/model/base) | {summary['to_port_intermediate']} |")
    out.append(f"| TO_WIRE_STATE_VARIANT (block states) | {summary['to_wire_state']} |")
    out.append(f"| TO_WIRE_DISPLAY_VARIANT (display system) | {summary['to_wire_display']} |")
    out.append(f"| TO_PORT_MACHINE (machine devices) | {summary['to_port_machine']} |")
    out.append(f"| TO_PORT_DECOR (decor blocks) | {summary['to_port_decor']} |")
    out.append("")
    out.append(f"**Display Variants**: {summary['display_asset_files']} asset files / "
               f"{summary['display_unique_variants']} unique drink variants")
    out.append("")
    out.append(f"| **UNASSIGNED** (not yet determined) | {summary['unassigned']} |")
    out.append("")

    # By family
    out.append("## By Content Family")
    out.append("")
    out.append("| Family | Total | Active | Merged | Standalone | Intermediate | State | Display | Machine | Decor | Unassigned |")
    out.append("|---|---|---|---|---|---|---|---|---|---|---|")
    for family, info in sorted(summary["by_family"].items()):
        bs = info["by_status"]
        out.append(f"| {family} | {info['total']} | "
                   f"{bs.get(STATUS_ACTIVE, 0)} | {bs.get(STATUS_MERGED, 0)} | "
                   f"{bs.get(STATUS_STANDALONE, 0)} | {bs.get(STATUS_INTERMEDIATE, 0)} | "
                   f"{bs.get(STATUS_STATE_VARIANT, 0)} | {bs.get(STATUS_DISPLAY_VARIANT, 0)} | "
                   f"{bs.get(STATUS_MACHINE, 0)} | {bs.get(STATUS_DECOR, 0)} | "
                   f"{bs.get(STATUS_UNASSIGNED, 0)} |")
    out.append("")

    # Missing target owner
    missing = [e for e in classified
               if e["status"] not in (STATUS_ACTIVE, STATUS_MERGED)
               and e.get("runtime_role") is None and e.get("runtime_owner") is None]
    if missing:
        out.append("## Missing Target Owner")
        out.append("")
        out.append("These non-active entries have no `runtime_owner` or `runtime_role`.")
        out.append("")
        out.append("| Asset ID | Type | Status | Family |")
        out.append("|---|---|---|---|")
        for e in sorted(missing, key=lambda x: x["asset_id"]):
            out.append(f"| `{e['asset_id']}` | {e['asset_type']} | {e['status']} | {e['content_family']} |")
        out.append("")

    # Unassigned detail
    unassigned = [e for e in classified if e["status"] == STATUS_UNASSIGNED]
    if unassigned:
        out.append("## UNASSIGNED Assets (Need Manual Review)")
        out.append("")
        out.append("| Asset ID | Type | Old Classification |")
        out.append("|---|---|---|")
        for e in sorted(unassigned, key=lambda x: x["asset_id"]):
            out.append(f"| `{e['asset_id']}` | {e['asset_type']} | {e['old_classification']} |")
        out.append("")

    # Active detail
    active_list = [e for e in classified if e["status"] == STATUS_ACTIVE]
    out.append(f"## ACTIVE_RUNTIME_ASSET ({len(active_list)} assets)")
    out.append("")
    out.append("These are already wired to existing blockstates, recipes, or registered items.")
    out.append("")
    out.append("| Asset ID | Runtime Owner | Role |")
    out.append("|---|---|---|")
    for e in sorted(active_list, key=lambda x: x["asset_id"]):
        out.append(f"| `{e['asset_id']}` | {e.get('runtime_owner', 'N/A')} | {e.get('runtime_role', 'N/A')} |")
    out.append("")

    out.append("---")
    return "\n".join(out)


def _load_baseline() -> dict:
    """Load the immutable 639-asset baseline."""
    return _read_json(BASELINE_PATH) or {"baseline_assets": [], "total_baseline_assets": 0}


def _get_typed_registry_keys(registry: list) -> set:
    """Build set of (type, id) for all registered items and blocks.
    Used for typed matching against baseline assets."""
    keys = set()
    for e in registry:
        if not e.get("registered"):
            continue
        eid = e["id"]
        etype = e.get("type", "")
        if etype == "item":
            keys.add(("item_model", eid))
            keys.add(("item_texture", eid))
            if e.get("block_item") and e.get("block_of"):
                keys.add(("block_model", e["block_of"]))
        elif etype == "block":
            keys.add(("block_model", eid))
            keys.add(("blockstate", eid))
    return keys


def main() -> int:
    REPORT_DIR.mkdir(parents=True, exist_ok=True)

    # Load manifest (for registry data and current state)
    manifest = _load_manifest()
    registry = manifest.get("registry", [])

    # Load immutable baseline — always 639 assets
    baseline = _load_baseline()
    baseline_assets = baseline.get("baseline_assets", [])
    total_baseline = baseline.get("total_baseline_assets", len(baseline_assets))

    # Baseline integrity gate
    if total_baseline != 639:
        print(f"FAIL: Baseline asset count is {total_baseline}, expected 639.")
        print("The baseline file docs/legacy_asset_baseline.json may be corrupted.")
        return 2
    if len(baseline_assets) != 639:
        print(f"FAIL: Baseline list length is {len(baseline_assets)}, expected 639.")
        return 2
    typed_keys = [(a.get("type", ""), a.get("id", "")) for a in baseline_assets]
    if len(set(typed_keys)) != len(typed_keys):
        dupes = [k for k in typed_keys if typed_keys.count(k) > 1]
        print(f"FAIL: Baseline has duplicate (type, id) entries: {set(dupes)}")
        return 2

    # Collect data
    registry_ids = {e["id"] for e in registry if e.get("registered")}
    registered_item_ids = {e["id"] for e in registry if e.get("registered") and e.get("type") == "item"}
    registered_block_ids = {e["id"] for e in registry if e.get("registered") and e.get("type") == "block"}
    typed_registry_keys = _get_typed_registry_keys(registry)
    active_blockstate = _collect_active_blockstate_refs(registry)
    active_typed = _collect_registered_item_models(registry)
    recipe_outputs = _collect_recipe_intermediates()

    # Count restored: baseline assets whose (type, id) is now registered
    baseline_registered = sum(
        1 for a in baseline_assets
        if (a["type"], a["id"]) in typed_registry_keys
    )

    print(f"Baseline assets: {total_baseline}")
    print(f"Baseline assets now registered: {baseline_registered}")
    print(f"Registry entries: {len(registry_ids)}")
    print(f"Registered items: {len(registered_item_ids)}, blocks: {len(registered_block_ids)}")
    print(f"Active blockstate refs (registered blocks only): {len(active_blockstate)}")
    print(f"Active typed assets: {len(active_typed)}")
    print(f"Recipe outputs: {len(recipe_outputs)}")

    # Classify each baseline asset (always 639 — never shrinks)
    classified = []
    for orphan in baseline_assets:
        entry = _classify_orphan(orphan, registry_ids,
                                 registered_item_ids, registered_block_ids,
                                 active_blockstate, active_typed, recipe_outputs)
        classified.append(entry)

    # Build summary
    summary = _build_summary(classified, total_baseline, baseline_registered)

    # Write reports
    REPORT_JSON.write_text(json.dumps({
        "summary": summary,
        "assets": classified,
    }, indent=2, ensure_ascii=False), encoding="utf-8")
    REPORT_MD.write_text(_format_md(summary, classified), encoding="utf-8")

    # Write legacy content matrix JSON
    matrix_json = {
        "summary": summary,
        "version": "2.0",
        "status_system": {
            "ACTIVE_RUNTIME_ASSET": "Already used by blockstate, renderer, or registered item (typed check)",
            "TO_PORT_STANDALONE": "Needs registration as independent Item or Block",
            "TO_PORT_INTERMEDIATE": "Needs to become a crafting intermediate (raw/model/base)",
            "TO_WIRE_STATE_VARIANT": "Needs to attach to existing block states",
            "TO_WIRE_DISPLAY_VARIANT": "Needs display system integration (_plate models)",
            "TO_PORT_MACHINE": "Needs machine/device restoration",
            "TO_PORT_DECOR": "Needs decor block registration",
            "MERGED_RUNTIME_VARIANT": "Old ID merged into modern state, capability preserved",
            "UNASSIGNED": "Not yet determined (target: 0)",
        },
        "quality_gates": {
            "UNASSIGNED_STATUS": summary["unassigned"],
            "MISSING_TARGET_OWNER": summary["missing_target_owner"],
            "TBD_PHASE": summary["tbd_phase"],
        },
        "assets": classified,
    }
    LEGACY_MATRIX_JSON.write_text(json.dumps(matrix_json, indent=2, ensure_ascii=False),
                                   encoding="utf-8")

    # Write legacy content matrix MD
    LEGACY_MATRIX_MD.write_text(_format_md(summary, classified), encoding="utf-8")

    print(f"\nWrote {REPORT_MD}")
    print(f"Wrote {REPORT_JSON}")
    print(f"Wrote {LEGACY_MATRIX_JSON}")
    print(f"Wrote {LEGACY_MATRIX_MD}")

    # Summary
    print(f"\n=== Legacy Coverage Summary v2 ===")
    print(f"Baseline: {summary.get('total_baseline_assets', 0)}")
    print(f"Restored (now registered): +{summary.get('restored_from_baseline', 0)}")
    print(f"Tracked: {summary['total_legacy_assets']}")
    print(f"Active + Merged (runtime covered): {summary['active_runtime'] + summary['merged']}/{summary.get('total_baseline_assets', summary['total_legacy_assets'])}")
    print(f"To port: {summary['to_port_standalone'] + summary['to_port_intermediate'] + summary['to_wire_state'] + summary['to_wire_display'] + summary['to_port_machine'] + summary['to_port_decor']}")
    print(f"UNASSIGNED: {summary['unassigned']}")
    print(f"Missing target owner: {summary['missing_target_owner']}")
    print(f"Missing runtime owner: {summary['missing_runtime_owner']}")
    print(f"TBD phase: {summary['tbd_phase']}")
    print(f"Display: {summary['display_asset_files']} asset files / {summary['display_unique_variants']} unique variants")

    exit_code = 0
    if summary['unassigned'] > 0:
        print("\nFAIL: UNASSIGNED assets remain.")
        exit_code = 2
    if summary['missing_target_owner'] > 0:
        print(f"FAIL: {summary['missing_target_owner']} non-ACTIVE assets missing target_owner.")
        exit_code = 2
    if summary['missing_runtime_owner'] > 0:
        print(f"FAIL: {summary['missing_runtime_owner']} ACTIVE/MERGED assets missing runtime_owner.")
        exit_code = 2
    if summary['tbd_phase'] > 0:
        print(f"FAIL: {summary['tbd_phase']} assets with TBD phase.")
        exit_code = 2

    if exit_code == 0:
        print("\nPASS: all quality gates met.")

    return exit_code


if __name__ == "__main__":
    sys.exit(main())
