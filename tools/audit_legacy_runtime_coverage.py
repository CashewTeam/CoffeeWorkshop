#!/usr/bin/env python3
"""
Coffee Workshop — Legacy Asset Runtime Coverage Audit

Reads the current content manifest, then recursively resolves which orphan
(legacy) assets are already in use by registered game content:
  - Blockstate → Block Model → Parent chain → Texture
  - Item Model → Parent chain → Texture (actively used by registered items)
  - Recipe intermediates (items appearing in recipe JSONs)
  - Machine state variants (_on/_off) mapped to LIT blockstate

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
import sys
from collections import defaultdict
from pathlib import Path
from typing import Any

REPO_ROOT = Path(__file__).resolve().parents[1]
ASSETS_ROOT = REPO_ROOT / "src" / "main" / "resources" / "assets" / "coffeework"
DATA_ROOT = REPO_ROOT / "src" / "main" / "resources" / "data" / "coffeework"
GEN_DATA_ROOT = REPO_ROOT / "src" / "generated" / "resources" / "data" / "coffeework"
MANIFEST_PATH = REPO_ROOT / "docs" / "content_manifest.json"
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

FAMILY_PATTERNS = [
    # (pattern, family_name, target_phase)
    ("coffee_", "coffee_drinks", "5.2"),
    ("espresso", "coffee_drinks", "5.2"),
    ("cocoa", "coffee_drinks", "5.2"),
    ("coldbrew", "coffee_drinks", "5.2"),
    ("icecream_", "icecream", "5.2"),
    ("cream_milk", "icecream_cream", "6.0"),
    ("cream_apple", "icecream_cream", "6.0"),
    ("cream_berry", "icecream_cream", "6.0"),
    ("cream_chocolate", "icecream_cream", "6.0"),
    ("cream_coffee", "icecream_cream", "6.0"),
    ("cream_lemon", "icecream_cream", "6.0"),
    ("cream_melon", "icecream_cream", "6.0"),
    ("cookie_icecream", "icecream_cookie", "6.0"),
    ("sandwich_", "sandwich", "5.2"),
    ("cake_slices", "cake_system", "5.4"),
    ("cake_sponge", "cake_system", "5.4"),
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
    ("cake_roll", "cake_roll", "5.4"),
    ("tiramisu", "cake_system", "5.4"),
    ("mousse_", "cake_mousse", "5.4"),
    ("brownie", "bakery", "5.3"),
    ("caramel", "confectionery", "5.3"),
    ("custard", "confectionery", "5.3"),
    ("milk_form", "confectionery", "5.3"),
    ("hardtack", "confectionery", "5.3"),
    ("cookie_black", "confectionery", "5.3"),
    ("cookie_oreo", "confectionery", "5.3"),
    ("marshmallow", "confectionery", "5.3"),
    ("smore", "confectionery", "5.3"),
    ("jiggy_cake", "cake_jiggy", "7.1"),
    ("mooncake", "pastry", "7.1"),
    ("souffle", "pastry", "7.1"),
    ("muffin_", "pastry", "7.1"),
    ("pie_", "pie", "7.0"),
    ("croissant", "pastry", "7.0"),
    ("ginger_bread", "pastry", "7.0"),
    ("puff", "pastry", "7.0"),
    ("mille_feuille", "pastry", "7.0"),
    ("soda_machine", "soda_machine", "9.1"),
    ("moka_", "traditional_brew", "9.0"),
    ("turkey_", "traditional_brew", "9.0"),
    ("coffee_pot_", "traditional_brew", "9.0"),
    ("phonograph", "decor_phonograph", "9.2"),
    ("bar_stone", "decor_bar", "9.2"),
    ("bar_wooden", "decor_bar", "9.2"),
    ("records_", "records", "5.2"),
    ("bag_coffee_raw", "bags", "5.1"),
    ("bag_coffee_powder", "bags", "5.1"),
    ("bag_cocoa", "bags", "5.1"),
    ("bag_cocoa_powder", "bags", "5.1"),
    ("bag_flour", "bags", "5.1"),
    ("bag_sugar", "bags", "5.1"),
    ("double_bag_", "bags", "5.1"),
    ("bag_coffee", "bags", "5.1"),
    ("syrup_", "syrups", "5.2"),
    ("plate_dough", "dough_system", "5.3"),
    ("plate_iron", "materials", "5.1"),
]

# ── Known blockstate → block mappings ──────────────────────────────────

# These orphan assets are blockstate variants already wired to registered blocks.
# Format: orphan_id → (runtime_owner, runtime_role)
BLOCKSTATE_WIRED = {
    # Grinder lit states
    "grinder_on": ("grinder", "blockstate:lit=true"),
    "grinder_off": ("grinder", "blockstate:lit=false"),
    # Oven lit states
    "oven_on": ("oven", "blockstate:lit=true"),
    "oven_off": ("oven", "blockstate:lit=false"),
    # Roller lit states
    "roller_on": ("roller", "blockstate:lit=true"),
    "roller_off": ("roller", "blockstate:lit=false"),
    # Icecream Machine lit states
    "icecream_machine_on": ("icecream_machine", "blockstate:lit=true"),
    "icecream_machine_off": ("icecream_machine", "blockstate:lit=false"),
    # Coffee Machine lit (merge old ids)
    "coffeemachine_on": ("coffee_machine", "blockstate:lit=true"),
    "coffeemachine_off": ("coffee_machine", "blockstate:lit=false"),
    "coffee_machine_on": ("coffee_machine", "blockstate:lit=true"),
    "coffee_machine_off": ("coffee_machine", "blockstate:lit=false"),
    # Cold brew pot ferm stages
    "coldbrew_pot_0": ("coldbrew_pot", "blockstate:ferm=0"),
    "coldbrew_pot_1": ("coldbrew_pot", "blockstate:ferm=1"),
    "coldbrew_pot_2": ("coldbrew_pot", "blockstate:ferm=2"),
    "coldbrew_pot_3": ("coldbrew_pot", "blockstate:ferm=3"),
    "coldbrew_pot_4": ("coldbrew_pot", "blockstate:ferm=4"),
    "coldbrew_pot_5": ("coldbrew_pot", "blockstate:ferm=5"),
    "coldbrew_pot_6": ("coldbrew_pot", "blockstate:ferm=6"),
    "coldbrew_pot_7": ("coldbrew_pot", "blockstate:ferm=7"),
    "coldbrew_pot_8": ("coldbrew_pot", "blockstate:ferm=8"),
    # Clay oven (old name merged to oven)
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


def _resolve_model_chain(model_name: str, model_dir: Path) -> set:
    """Recursively resolve a model JSON to collect all referenced model IDs."""
    seen = set()
    stack = [model_name]
    while stack:
        name = stack.pop()
        if name in seen:
            continue
        seen.add(name)
        path = model_dir / f"{name}.json"
        data = _read_json(path)
        if not data:
            continue
        # Follow parent
        parent = data.get("parent", "")
        if parent.startswith(f"{NAMESPACE}:block/"):
            pname = parent[len(f"{NAMESPACE}:block/"):]
            if pname not in seen:
                stack.append(pname)
        elif parent.startswith(f"{NAMESPACE}:item/"):
            pname = parent[len(f"{NAMESPACE}:item/"):]
            if pname not in seen:
                stack.append(pname)
        # Collect texture references
        textures = data.get("textures", {})
        for tex_ref in textures.values():
            if isinstance(tex_ref, str) and tex_ref.startswith(f"{NAMESPACE}:block/"):
                ref_name = tex_ref[len(f"{NAMESPACE}:block/"):]
                if ref_name not in seen:
                    stack.append(ref_name)
            elif isinstance(tex_ref, str) and tex_ref.startswith(f"{NAMESPACE}:item/"):
                ref_name = tex_ref[len(f"{NAMESPACE}:item/"):]
                if ref_name not in seen:
                    stack.append(ref_name)
    return seen


def _collect_active_blockstate_refs() -> dict:
    """Return {asset_id: (runtime_owner, runtime_role)} for all blockstate-referenced assets."""
    active = {}
    for bs_path in sorted(BLOCKSTATES_DIR.glob("*.json")):
        bs_name = bs_path.stem
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
                    # Mark this block model as active
                    active[model_id] = (bs_name, f"blockstate:variants={variant_key}")
                    # Recursively collect all referenced models
                    chain = _resolve_model_chain(model_id, BLOCK_MODELS_DIR)
                    for ref in chain:
                        if ref != model_id and ref not in active:
                            active[ref] = (bs_name, f"blockstate:parent_of_{model_id}")
    return active


def _collect_registered_item_models(registry: list) -> set:
    """Return set of item model names that are directly used by registered items."""
    used = set()
    for entry in registry:
        eid = entry["id"]
        if entry.get("registered") and entry.get("type") in ("item",):
            # Check if the item model exists
            if (ITEM_MODELS_DIR / f"{eid}.json").exists():
                used.add(eid)
            # Also check if a block item references a block model
            if entry.get("block_item") and entry.get("block_of"):
                bid = entry["block_of"]
                if (BLOCK_MODELS_DIR / f"{bid}.json").exists():
                    used.add(bid)
    return used


def _collect_recipe_intermediates() -> dict:
    """Find items that appear as recipe outputs. Returns {item_id: recipe_count}."""
    intermediates = defaultdict(int)
    recipe_dirs = [
        GEN_DATA_ROOT / "recipes",
        DATA_ROOT / "recipes",
    ]
    # Also check subdirectories for typed recipes
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
    """Return (family_name, target_phase) for an asset."""
    for pattern, family, phase in FAMILY_PATTERNS:
        if pattern in asset_id:
            return (family, phase)
    return ("unclassified", "TBD")


def _classify_orphan(orphan: dict, registry_ids: set,
                     active_blockstate: dict, active_item_models: set,
                     recipe_outputs: dict) -> dict:
    """Classify a single orphan asset into the new status system."""
    asset_id = orphan["id"]
    asset_type = orphan["type"]
    old_class = orphan.get("classification", "UNKNOWN")
    family, phase = _determine_family(asset_id)

    result = {
        "asset_id": asset_id,
        "asset_type": asset_type,
        "content_family": family,
        "runtime_owner": None,
        "runtime_role": None,
        "target_phase": phase,
        "status": STATUS_UNASSIGNED,
        "source": "legacy_1_12",
        "survival_chain": "",
        "old_classification": old_class,
        "notes": "",
    }

    # ── Rule 1: Already registered ──
    if asset_id in registry_ids:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_item_or_block"
        return result

    # ── Rule 2: Wired to existing blockstate ──
    if asset_id in BLOCKSTATE_WIRED:
        owner, role = BLOCKSTATE_WIRED[asset_id]
        result["status"] = STATUS_MERGED
        result["runtime_owner"] = f"coffeework:{owner}"
        result["runtime_role"] = role
        result["notes"] = "Old ID merged into modern blockstate variant"
        return result

    # ── Rule 3: Used by active blockstate chain ──
    if asset_id in active_blockstate:
        owner, role = active_blockstate[asset_id]
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{owner}"
        result["runtime_role"] = role
        return result

    # ── Rule 4: Used by registered item model ──
    if asset_id in active_item_models:
        result["status"] = STATUS_ACTIVE
        result["runtime_owner"] = f"coffeework:{asset_id}"
        result["runtime_role"] = "registered_item_model"
        return result

    # ── Rule 5: Appears as recipe output → standalone item ──
    if asset_id in recipe_outputs:
        result["status"] = STATUS_STANDALONE
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

    # Cake slice models (_slice1..6, _uneaten)
    if any(asset_id.endswith(s) for s in [f"_slice{i}" for i in range(1, 7)] + ["_uneaten"]):
        # Extract cake name: e.g., cake_coffee_slice1 → cake_coffee
        import re
        m = re.match(r"^(.+?)_(slice[1-6]|uneaten)$", asset_id)
        if m:
            cake_name = m.group(1)
            result["status"] = STATUS_STATE_VARIANT
            result["runtime_owner"] = f"coffeework:{cake_name}"
            result["runtime_role"] = f"blockstate:bites"
            result["notes"] = "Cake slice model — wire to BITES property"
            return result

    # Raw/intermediate/model/base items
    if asset_id.endswith("_raw"):
        base = asset_id[:-4]
        if base in registry_ids or base in recipe_outputs:
            result["status"] = STATUS_INTERMEDIATE
            result["runtime_owner"] = f"coffeework:{base}" if base in registry_ids else None
            result["runtime_role"] = "intermediate:raw"
            result["survival_chain"] = f"raw material for {base}"
            return result
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_role"] = "intermediate:raw"
        return result

    if asset_id.endswith("_model"):
        base = asset_id[:-6]
        if base in registry_ids:
            result["status"] = STATUS_INTERMEDIATE
            result["runtime_owner"] = f"coffeework:{base}"
            result["runtime_role"] = "intermediate:mold_form"
            result["survival_chain"] = f"mold form for {base}"
            return result
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_role"] = "intermediate:mold_form"
        return result

    if asset_id.endswith("_base"):
        base = asset_id[:-5]
        if base in registry_ids:
            result["status"] = STATUS_INTERMEDIATE
            result["runtime_owner"] = f"coffeework:{base}"
            result["runtime_role"] = "intermediate:base_layer"
            result["survival_chain"] = f"base layer for {base}"
            return result
        result["status"] = STATUS_INTERMEDIATE
        result["runtime_role"] = "intermediate:base_layer"
        return result

    # Plate models → display system
    if asset_id.endswith("_plate"):
        drink_id = asset_id[:-6]
        if drink_id in registry_ids:
            result["status"] = STATUS_DISPLAY_VARIANT
            result["runtime_owner"] = f"coffeework:{drink_id}"
            result["runtime_role"] = "drink_display:plate_model"
            result["notes"] = "Plate model — wire to DrinkDisplayBlock system"
            return result
        result["status"] = STATUS_DISPLAY_VARIANT
        result["runtime_role"] = "drink_display:plate_model"
        return result

    # Cake roll models
    if asset_id.endswith("_roll"):
        result["status"] = STATUS_STATE_VARIANT
        result["runtime_role"] = "cake_roll_item"
        return result

    # Cake slices item
    if asset_id.endswith("_slices") and not asset_id.endswith("_slice1") and not asset_id.endswith("_slice2") and not asset_id.endswith("_slice3") and not asset_id.endswith("_slice4") and not asset_id.endswith("_slice5") and not asset_id.endswith("_slice6"):
        base = asset_id[:-7]
        result["status"] = STATUS_STANDALONE
        result["runtime_owner"] = None
        result["runtime_role"] = "slice_item"
        result["survival_chain"] = f"interact with {base} cake block"
        return result

    # Coldbrew stages
    if asset_id.startswith("coldbrew_pot_"):
        result["status"] = STATUS_STATE_VARIANT
        result["runtime_owner"] = "coffeework:coldbrew_pot"
        result["runtime_role"] = "blockstate:ferm"
        return result

    # Machine-related (soda, moka, turkey, coffee pot, phonograph, bar)
    for machine_pattern in ["soda_machine", "moka_", "turkey_", "coffee_pot_"]:
        if asset_id.startswith(machine_pattern):
            result["status"] = STATUS_MACHINE
            result["runtime_role"] = "machine_component"
            return result

    # Bar furniture
    if asset_id.startswith("bar_"):
        result["status"] = STATUS_DECOR
        result["runtime_role"] = "bar_furniture"
        return result

    # Phonograph
    if "phonograph" in asset_id:
        result["status"] = STATUS_TO_PORT_MACHINE if asset_type == "blockstate" else STATUS_DECOR
        result["runtime_role"] = "phonograph"
        return result

    # Syrups
    if asset_id.startswith("syrup_") and asset_id not in registry_ids:
        if asset_id in ("syrup_brown", "syrup_full"):
            result["status"] = STATUS_ACTIVE
            result["runtime_owner"] = "coffeework:syrup_fruit"
            result["runtime_role"] = "deprecated_variant"
            result["notes"] = "Old variant — superseded by modern syrups"
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
        result["survival_chain"] = "mixing bowl intermediate for cake batter"
        result["notes"] = "Old bowl+batter system, needs redesign into modern mold system"
        return result

    # Known removed items that should be PORT_STANDALONE
    port_now_set = {
        "coffee_instant_cup", "coffee_instant_cup_unopen",
        "icecream_apple", "icecream_berry", "icecream_chocolate",
        "icecream_coffee", "icecream_lemon", "icecream_melon",
        "sandwich_bacon_egg", "sandwich_beef_cheese", "sandwich_blt_large",
        "sandwich_club", "sandwich_club_large", "sandwich_ham_cheese",
    }
    if asset_id in port_now_set:
        result["status"] = STATUS_STANDALONE
        return result

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
        return result

    # Muffin finished items
    if asset_id.startswith("muffin_") and not asset_id.endswith("_raw"):
        result["status"] = STATUS_STANDALONE
        return result

    # Pie items
    if asset_id.startswith("pie_") and not asset_id.endswith("_raw"):
        result["status"] = STATUS_STANDALONE
        return result

    # Cake finished items  
    if (asset_id.startswith("cake_") and asset_id not in registry_ids
            and not asset_id.endswith("_raw") and not asset_id.endswith("_base")
            and not asset_id.endswith("_model") and not asset_id.endswith("_plate")
            and not any(asset_id.endswith(f"_slice{i}") for i in range(1, 7))
            and not asset_id.endswith("_uneaten") and not asset_id.endswith("_slices")
            and not asset_id.endswith("_roll")):
        result["status"] = STATUS_STANDALONE
        return result

    # Jiggy cake items
    if "jiggy" in asset_id:
        if asset_id.endswith("_raw") or asset_id.endswith("_model"):
            result["status"] = STATUS_INTERMEDIATE
        else:
            result["status"] = STATUS_STANDALONE
        return result

    # Mooncake items
    if "mooncake" in asset_id:
        if asset_id.endswith("_raw"):
            result["status"] = STATUS_INTERMEDIATE
        elif asset_id == "mooncake_model":
            result["status"] = STATUS_ACTIVE
            result["runtime_owner"] = "coffeework:mooncake_model"
            result["runtime_role"] = "registered_item"
        else:
            result["status"] = STATUS_STANDALONE
        return result

    # Dessert items (old naming, now merged)
    dessert_standalone = {
        "chocolate_chip", "chocolate_bar",
        "field_ration",
        "ginger_house",
    }
    if asset_id in dessert_standalone:
        result["status"] = STATUS_STANDALONE
        return result

    # Default: based on old classification
    if old_class == "PORT_NOW":
        result["status"] = STATUS_STANDALONE
    elif old_class == "PORT_LATER":
        result["status"] = STATUS_STANDALONE
    elif old_class == "REMOVED":
        result["status"] = STATUS_STANDALONE
    elif old_class == "REDESIGN":
        result["status"] = STATUS_STANDALONE
    elif old_class == "MERGED":
        result["status"] = STATUS_MERGED
    elif old_class == "ASSET_ARCHIVE":
        # Last resort — leave as UNASSIGNED for manual review
        pass

    return result


def _build_summary(classified: list) -> dict:
    """Build summary statistics."""
    total = len(classified)
    by_status = defaultdict(int)
    by_family = defaultdict(lambda: {"total": 0, "by_status": defaultdict(int)})
    by_type = defaultdict(int)

    for entry in classified:
        by_status[entry["status"]] += 1
        by_type[entry["asset_type"]] += 1
        fam = by_family[entry["content_family"]]
        fam["total"] += 1
        fam["by_status"][entry["status"]] += 1

    return {
        "total_legacy_assets": total,
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
    }


def _format_md(summary: dict, classified: list) -> str:
    """Generate human-readable Markdown report."""
    out = []
    out.append("# Coffee Workshop — Legacy Asset Runtime Coverage")
    out.append("")
    out.append("> Auto-generated by `tools/audit_legacy_runtime_coverage.py`")
    out.append("")
    out.append("## Summary")
    out.append("")
    out.append(f"| Metric | Count |")
    out.append(f"|---|---|")
    out.append(f"| Total legacy assets | {summary['total_legacy_assets']} |")
    out.append(f"| **ACTIVE_RUNTIME_ASSET** (already in use) | {summary['active_runtime']} |")
    out.append(f"| **MERGED_RUNTIME_VARIANT** (old ID merged) | {summary['merged']} |")
    out.append(f"| TO_PORT_STANDALONE (needs registration) | {summary['to_port_standalone']} |")
    out.append(f"| TO_PORT_INTERMEDIATE (raw/model/base) | {summary['to_port_intermediate']} |")
    out.append(f"| TO_WIRE_STATE_VARIANT (block states) | {summary['to_wire_state']} |")
    out.append(f"| TO_WIRE_DISPLAY_VARIANT (display system) | {summary['to_wire_display']} |")
    out.append(f"| TO_PORT_MACHINE (machine devices) | {summary['to_port_machine']} |")
    out.append(f"| TO_PORT_DECOR (decor blocks) | {summary['to_port_decor']} |")
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


def main() -> int:
    REPORT_DIR.mkdir(parents=True, exist_ok=True)

    # Load manifest
    manifest = _load_manifest()
    orphans = manifest.get("orphan_assets", [])
    registry = manifest.get("registry", [])

    # Collect data
    registry_ids = {e["id"] for e in registry if e.get("registered")}
    active_blockstate = _collect_active_blockstate_refs()
    active_item_models = _collect_registered_item_models(registry)
    recipe_outputs = _collect_recipe_intermediates()

    print(f"Registry entries: {len(registry_ids)}")
    print(f"Active blockstate refs: {len(active_blockstate)}")
    print(f"Active item models: {len(active_item_models)}")
    print(f"Recipe outputs: {len(recipe_outputs)}")

    # Classify each orphan
    classified = []
    for orphan in orphans:
        entry = _classify_orphan(orphan, registry_ids, active_blockstate,
                                 active_item_models, recipe_outputs)
        classified.append(entry)

    # Build summary
    summary = _build_summary(classified)

    # Write reports
    REPORT_JSON.write_text(json.dumps({
        "summary": summary,
        "assets": classified,
    }, indent=2, ensure_ascii=False), encoding="utf-8")
    REPORT_MD.write_text(_format_md(summary, classified), encoding="utf-8")

    # Write legacy content matrix JSON
    matrix_json = {
        "summary": summary,
        "version": "1.0",
        "status_system": {
            "ACTIVE_RUNTIME_ASSET": "Already used by blockstate, renderer, or registered item",
            "TO_PORT_STANDALONE": "Needs registration as independent Item or Block",
            "TO_PORT_INTERMEDIATE": "Needs to become a crafting intermediate (raw/model/base)",
            "TO_WIRE_STATE_VARIANT": "Needs to attach to existing block states",
            "TO_WIRE_DISPLAY_VARIANT": "Needs display system integration (_plate models)",
            "TO_PORT_MACHINE": "Needs machine/device restoration",
            "TO_PORT_DECOR": "Needs decor block registration",
            "MERGED_RUNTIME_VARIANT": "Old ID merged into modern state, capability preserved",
            "UNASSIGNED": "Not yet determined (target: 0)",
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
    print(f"\n=== Legacy Coverage Summary ===")
    print(f"Total: {summary['total_legacy_assets']}")
    print(f"Active + Merged (already covered): {summary['active_runtime'] + summary['merged']}")
    print(f"To port (standalone + intermediate + state + display + machine + decor): "
          f"{summary['to_port_standalone'] + summary['to_port_intermediate'] + summary['to_wire_state'] + summary['to_wire_display'] + summary['to_port_machine'] + summary['to_port_decor']}")
    print(f"UNASSIGNED: {summary['unassigned']}")

    if summary['unassigned'] > 0:
        print("\nWARNING: UNASSIGNED assets remain — manual review needed.")
        return 2

    return 0


if __name__ == "__main__":
    sys.exit(main())
