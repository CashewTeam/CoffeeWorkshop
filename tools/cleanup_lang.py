#!/usr/bin/env python3
"""
Remove language keys that do not correspond to currently registered content.
Keeps only keys needed by current registrations + UI + JEI + record descriptions.

The removed keys can be regenerated from old 1.12.2 translations when content is ported.
"""

import json
import re
import sys
from pathlib import Path

ROOT = Path(__file__).resolve().parent.parent
LANG_DIR = ROOT / "src" / "main" / "resources" / "assets" / "coffeework" / "lang"
JAVA = ROOT / "src" / "main" / "java" / "net" / "langball" / "coffee" / "init"


def parse_registry_ids():
    """Extract all registered IDs from Java init files."""
    ids = {"item": set(), "block": set(), "effect": set(), "profession": set()}
    
    for file, pattern, cat in [
        ("ModItems.java", r'ITEMS\.register\("([^"]+)"', "item"),
        ("ModBlocks.java", r'BLOCKS\.register\("([^"]+)"', "block"),
        ("ModEffects.java", r'EFFECTS\.register\("([^"]+)"', "effect"),
        ("ModVillagers.java", r'PROFESSIONS\.register\("([^"]+)"', "profession"),
    ]:
        path = JAVA / file
        if path.exists():
            text = path.read_text(encoding="utf-8")
            for m in re.finditer(pattern, text):
                ids[cat].add(m.group(1))
    
    return ids


def build_required_keys(ids):
    """Build the set of lang keys that MUST be present."""
    required = set()
    
    # Items
    for id_ in ids["item"]:
        required.add(f"item.coffeework.{id_}")
        # Record description keys
        if id_.startswith("record_") and id_ not in ("record_blank",):
            required.add(f"item.coffeework.{id_}.desc")
    
    # Blocks
    for id_ in ids["block"]:
        required.add(f"block.coffeework.{id_}")
    
    # Effects
    for id_ in ids["effect"]:
        required.add(f"effect.coffeework.{id_}")
    
    # Professions
    for id_ in ids["profession"]:
        required.add(f"entity.minecraft.villager.coffeework.{id_}")
    
    # Creative tab
    required.add("itemGroup.coffee_workshop")
    
    # GUI labels (defined in code via Component.translatable)
    required.add("gui.coffeework.coffee_machine.base")
    required.add("gui.coffeework.coffee_machine.modifier")
    required.add("gui.coffeework.coffee_machine.additive")
    required.add("gui.coffeework.coffee_machine.container")
    
    # Container titles
    required.add("container.coffeework.grinder")
    required.add("container.coffeework.coffee_machine")
    required.add("container.coffeework.icecream_machine")
    required.add("container.coffeework.roller")
    required.add("container.coffeework.oven")
    
    # JEI (referenced in JEI compat code)
    required.add("jei.coffeework.category.grinder")
    required.add("jei.coffeework.category.coffee_machine")
    required.add("jei.coffeework.category.icecream_machine")
    required.add("jei.coffeework.category.roller")
    required.add("jei.coffeework.category.oven")
    required.add("jei.coffeework.category.cooling")
    required.add("jei.coffeework.cooling.tip")
    
    return required


def cleanup_lang(lang_file, required_keys):
    """Remove unneeded keys from a language file. Returns count of removals."""
    path = LANG_DIR / lang_file
    if not path.exists():
        return 0
    
    data = json.loads(path.read_text(encoding="utf-8"))
    original_count = len(data)
    
    # Track what's being removed for audit
    removed = {}
    kept = {}
    
    for key, value in data.items():
        if key in required_keys:
            kept[key] = value
        else:
            removed[key] = value
    
    # Save cleaned version
    cleaned = dict(sorted(kept.items()))
    path.write_text(json.dumps(cleaned, indent=2, ensure_ascii=False) + "\n", encoding="utf-8")
    
    removed_count = len(removed)
    
    # Save removed keys to archive
    if removed:
        archive_path = ROOT / "reference" / "reports" / f"removed_keys_{lang_file}"
        archive_path.write_text(
            json.dumps(dict(sorted(removed.items())), indent=2, ensure_ascii=False) + "\n",
            encoding="utf-8"
        )
    
    return original_count, len(kept), removed_count


def main():
    ids = parse_registry_ids()
    required = build_required_keys(ids)
    
    print(f"Registered: {len(ids['item'])} items, {len(ids['block'])} blocks, "
          f"{len(ids['effect'])} effects, {len(ids['profession'])} professions")
    print(f"Required lang keys: {len(required)}")
    print()
    
    for lang_file in ["zh_cn.json", "ja_jp.json"]:
        orig, kept, removed = cleanup_lang(lang_file, required)
        print(f"{lang_file}: {orig} → {kept} keys (removed {removed})")
        if removed > 0:
            print(f"  Removed keys archived to reference/reports/removed_keys_{lang_file}")
    
    print("\nDone. Removed keys can be restored from archive when content is ported.")
    return 0


if __name__ == "__main__":
    sys.exit(main())
