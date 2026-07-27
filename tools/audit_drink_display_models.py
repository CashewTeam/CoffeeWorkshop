#!/usr/bin/env python3
"""Audit drink_display_models.json for completeness and correctness.

Checks:
  - Every mapped drink ID is a registered item
  - Every mapped model path resolves to an existing JSON file
  - No duplicate drink→model mappings (aliases are INFO)
  - Unmapped _plate models must be in the approved-exclusion allowlist

Exit: 0=PASS, 2=FAIL
"""
from __future__ import annotations

import json, os, sys
from glob import glob

MOD_DIR = "src/main/resources/assets/coffeework"
DATA_DIR = "src/main/resources/data/coffeework"
MAPPING_FILE = os.path.join(DATA_DIR, "drink_display_models.json")
DECISIONS_FILE = os.path.join(DATA_DIR, "drink_display_legacy_decisions.json")
MANIFEST_FILE = "docs/content_manifest.json"

def _load_approved_orphans() -> set[str]:
    """Load allowed orphan model names from the shared decisions file."""
    try:
        with open(DECISIONS_FILE, encoding="utf-8") as f:
            data = json.load(f)
        result = set(data.get("excluded", {}).keys())
        result.update(data.get("aliases", {}).keys())
        return result
    except Exception:
        return set()

# ── Approved orphan _plate models (loaded from shared JSON, unified with legacy audit) ──
APPROVED_ORPHANS = _load_approved_orphans()


def load_manifest_items() -> set[str]:
    """Return set of all registered item IDs from content manifest."""
    with open(MANIFEST_FILE, encoding="utf-8") as f:
        manifest = json.load(f)
    return {e["id"] for e in manifest.get("registry", [])
            if e.get("registered") and e.get("type") == "item"}


def load_mappings() -> dict:
    """Return {drink_id: model_resource_location}."""
    if not os.path.exists(MAPPING_FILE):
        print(f"FAIL: {MAPPING_FILE} not found")
        sys.exit(2)
    with open(MAPPING_FILE, encoding="utf-8") as f:
        root = json.load(f)
    return root.get("drinks", {})


def model_exists(model_rl: str) -> tuple[bool, str | None]:
    """Check model file exists. Returns (ok, error_message)."""
    parts = model_rl.split(":", 1)
    if len(parts) != 2 or parts[0] != "coffeework":
        return False, f"bad namespace: {model_rl}"
    path = parts[1]
    full = os.path.join(MOD_DIR, "models", f"{path}.json")
    if not os.path.exists(full):
        return False, f"missing: {full}"
    return True, None


def list_block_plate_models() -> set[str]:
    """Return set of _plate block model filenames (without .json)."""
    return {os.path.splitext(os.path.basename(f))[0]
            for f in glob(os.path.join(MOD_DIR, "models/block/*_plate.json"))}


def main() -> int:
    exit_code = 0
    manifest_items = load_manifest_items()
    mappings = load_mappings()

    print(f"Manifest items: {len(manifest_items)}")
    print(f"Drink mappings: {len(mappings)}")

    # Exclude coffee_plate (base model, not a specific drink)
    base_plate = "coffee_plate"

    wired_names = set()
    for drink_id, model_rl in mappings.items():
        wired_names.add(model_rl.rsplit("/", 1)[-1])

    # ── Check 1: mapped drinks must be registered ──
    for drink_id in sorted(mappings):
        short = drink_id.split(":", 1)[-1] if ":" in drink_id else drink_id
        if short not in manifest_items:
            # Also check if the full qualified name matches
            if drink_id.split(":", 1)[-1] not in manifest_items:
                print(f"FAIL: {drink_id} not registered")
                exit_code = 2

    # ── Check 2: mapped models must exist ──
    for drink_id, model_rl in sorted(mappings.items()):
        ok, err = model_exists(model_rl)
        if not ok:
            print(f"FAIL: {drink_id} → {err}")
            exit_code = 2

    # ── Check 3: duplicate model refs (non-blocking: aliased models are valid) ──
    seen = {}
    for drink_id, model_rl in mappings.items():
        if model_rl in seen:
            print(f"INFO: {model_rl} shared by both {seen[model_rl]} and {drink_id} (alias)")
        seen[model_rl] = drink_id

    # ── Check 4: orphan plate block models ──
    all_block_plates = list_block_plate_models()
    unmapped = all_block_plates - wired_names - {base_plate}
    unapproved = unmapped - APPROVED_ORPHANS
    if unapproved:
        print(f"FAIL: {len(unapproved)} unapproved orphan _plate block model(s):")
        for o in sorted(unapproved):
            print(f"  - {o}")
        exit_code = 2
    if unmapped:
        approved_count = len(unmapped & APPROVED_ORPHANS)
        print(f"INFO: {len(unmapped)} unmapped _plate block model(s) "
              f"({approved_count} approved exclusions, {len(unapproved)} unapproved)")
    else:
        print("PASS: all _plate block models are mapped")

    # ── Summary ──
    exit_msg = "PASS" if exit_code == 0 else "FAIL"
    unapproved_count = len(unapproved)
    print(f"\n--- {exit_msg}: {len(mappings)} maps, {len(unmapped)} orphans "
          f"({unapproved_count} unapproved) ---")
    return exit_code


if __name__ == "__main__":
    sys.exit(main())
