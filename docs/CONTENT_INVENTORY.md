# Coffee Workshop — Content Inventory

> **Last updated:** 2026-07-25 (Phase 0 baseline)  
> **Branch:** 1.20.1  
> **This document uses the R/A/O/B/T completion matrix defined in the Phase 0 audit.**

---

## Legend

| Column | Meaning |
|--------|---------|
| **R** | Registry — block/item/effect/etc. is registered in Java code |
| **A** | Assets — model, texture, language key (en_us) present |
| **O** | Obtainable — available in survival mode without `/give` or creative tab |
| **B** | Behavior — core behavior matches original design intent |
| **T** | Tested — verified via GameTest, manual test, or CI pass |

**Phase 0 status** reflects static analysis and `compileJava`/`runData` verification only.
Items marked ❌ in O/B/T need Phase 1+ attention.

---

## Machines

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| grinder | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | LIT blockstate missing (P0-09); client-side burnTime (P0-07); input remainder bug (P0-08) |
| coffee_machine | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | Same LIT + tick + remainder issues |
| icecream_machine | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | Same LIT + tick + remainder issues |
| roller | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | Same LIT + tick + remainder issues |
| oven | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | Same LIT + tick + remainder issues |

> Assets note: all 5 machines use `blocks/anvil_base` texture (P0-10). Blockstates ignore `lit` property (P0-09).

---

## Plants & Crops

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_tree | ✅ | ✅ | ⚠️ | ❌ | ❌ | Mature harvest drops seeds not raw beans (P0-03); shears don't remove block (P0-04) |
| blueberry_bush | ✅ | ✅ | ✅ | ✅ | ❌ | Basic harvest works |
| vanilla_crop | ✅ | ✅ | ✅ | ✅ | ❌ | Basic crop behavior |
| coffee_seeds | ✅ | ✅ | ✅ | ❌ | ❌ | Only source is coffee_tree (which drops seeds not raw beans) |
| vanilla_seeds | ✅ | ✅ | ✅ | ✅ | ❌ | |

---

## Drinks (Hot)

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| espresso | ✅ | ✅ | ❌ | ⚠️ | ❌ | Machine recipe exists but no survival path to cup + powder |
| coffee_americano | ✅ | ✅ | ❌ | ⚠️ | ❌ | Machine recipe exists; multi-cup NBT init not guaranteed (P0-06) |
| coffee_latte | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same as americano |
| coffee_cappuccino | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_macchiato | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_mochaccino | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_green_tea | ✅ | ✅ | ❌ | ✅ | ❌ | Machine recipe exists |
| coffee_black_tea | ✅ | ✅ | ❌ | ✅ | ❌ | Machine recipe exists |
| coffee_milk_tea | ✅ | ✅ | ❌ | ✅ | ❌ | Machine recipe exists |
| coffee_mandarin_drink | ✅ | ✅ | ❌ | ✅ | ❌ | Machine recipe exists |
| coffee_coldbrew | ✅ | ✅ | ❌ | ✅ | ❌ | Machine recipe exists |
| cocoa | ✅ | ✅ | ❌ | ⚠️ | ❌ | Machine recipe exists; multi-cup NBT |
| cocoa_strong | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_instant | ✅ | ✅ | ❌ | ✅ | ❌ | Packaging-based, has crafting recipe |

---

## Drinks (Iced)

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_americano_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | No verified survival path; multi-cup NBT |
| coffee_latte_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_cappuccino_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_macchiato_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_mochaccino_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_green_tea_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_black_tea_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_milk_tea_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_mandarin_drink_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| cocoa_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| cocoa_strong_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |

---

## Flavored Latte Variants

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_latte_caramel | ✅ | ✅ | ❌ | ⚠️ | ❌ | No verified machine recipe or survival path |
| coffee_latte_chocolate | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_fruit | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_mint | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_vanilla | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_sakura | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_caramel_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_chocolate_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_fruit_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_mint_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_vanilla_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_latte_sakura_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |

---

## Americano Extensions

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_americano_fruit | ✅ | ✅ | ❌ | ⚠️ | ❌ | No verified machine recipe |
| coffee_americano_fruit_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_americano_nitro_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_americano_nitro_fruit_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |

---

## Cold Brew Extensions

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_coldbrew_fruit | ✅ | ✅ | ❌ | ⚠️ | ❌ | No verified machine recipe |
| coffee_coldbrew_latte | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_caramel | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_chocolate | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_fruit | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_mint | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_vanilla | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_fruit_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_caramel_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_chocolate_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_fruit_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_mint_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |
| coffee_coldbrew_latte_vanilla_ice | ✅ | ✅ | ❌ | ⚠️ | ❌ | Same |

---

## Instant Coffee & Accessories

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_instant | ✅ | ✅ | ✅ | ✅ | ❌ | Crafting recipe exists |
| coffee_instant_stick | ✅ | ✅ | ✅ | ✅ | ❌ | |
| coffee_instant_box | ✅ | ✅ | ✅ | ✅ | ❌ | |
| coffee_instant_cup | ❌ | ⚠️ | ❌ | ❌ | ❌ | Model exists, not registered |
| coffee_instant_cup_unopen | ❌ | ⚠️ | ❌ | ❌ | ❌ | Model exists, not registered |

---

## Drinks (Soda) — Deferred

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| soda_drink | ❌ | ⚠️ | ❌ | ❌ | ❌ | Translations exist, not registered |
| soda_drink_cola | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_lemon | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_berry | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_cherry | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_vanilla | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_apple | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |
| soda_drink_chocolate | ❌ | ⚠️ | ❌ | ❌ | ❌ | Same |

---

## Containers & Utensils

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| cup | ✅ | ✅ | ✅ | ✅ | ❌ | Returned by hot drinks |
| cup_glass | ✅ | ✅ | ✅ | ✅ | ❌ | Returned by iced drinks |
| plate | ✅ | ✅ | ✅ | ✅ | ❌ | Block + item |
| coldbrew_pot | ✅ | ✅ | ✅ | ⚠️ | ❌ | ferm state, Loot Table review pending (P0-11) |
| empty_coldbrew_pot | ✅ | ✅ | ✅ | ✅ | ❌ | |
| coldbrew_bottle | ✅ | ✅ | ❌ | ❌ | ❌ | No production recipe in survival |
| syrup_empty | ✅ | ✅ | ✅ | ✅ | ❌ | |
| iron_bowl | ✅ | ✅ | ✅ | ❌ | ❌ | Plain Item, consumed by recipes (P0-12) |
| mixing_bowl | ✅ | ✅ | ✅ | ❌ | ❌ | Plain Item, consumed by recipes (P0-12) |
| bag | ✅ | ✅ | ✅ | ✅ | ❌ | |
| bag_cloth | ✅ | ✅ | ✅ | ✅ | ❌ | |

---

## Ingredients & Materials

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| coffee_bean_raw | ✅ | ✅ | ❌ | ❌ | ❌ | Coffee tree drops seeds, not raw beans (P0-03) |
| coffee_bean | ✅ | ✅ | ❌ | ✅ | ❌ | Requires raw bean via furnace recipe |
| coffee_powder | ✅ | ✅ | ❌ | ✅ | ❌ | Requires coffee_bean via grinder recipe |
| cocoa_bean | ✅ | ✅ | ✅ | ✅ | ❌ | |
| cocoa_powder | ✅ | ✅ | ✅ | ✅ | ❌ | Furnace recipe from cocoa beans |
| cocoa_batter | ✅ | ✅ | ❌ | ✅ | ❌ | Requires grinder recipe |
| flour | ✅ | ✅ | ❌ | ✅ | ❌ | Requires grinder recipe |
| dough* (12 variants) | ✅ | ✅ | ⚠️ | ⚠️ | ❌ | Require mixing_bowl (consumed) and flour |
| butter | ✅ | ✅ | ✅ | ⚠️ | ❌ | Consumes mixing_bowl |
| cheese | ✅ | ✅ | ✅ | ⚠️ | ❌ | Consumes mixing_bowl |
| yeast | ✅ | ✅ | ✅ | ⚠️ | ❌ | Consumes mixing_bowl |
| gelatin | ✅ | ✅ | ✅ | ✅ | ❌ | |
| soda | ✅ | ✅ | ❌ | ❌ | ❌ | Soda ore drops self, not soda material (P0-14) |
| spices | ✅ | ✅ | ✅ | ✅ | ❌ | |
| ice_slag | ✅ | ✅ | ❌ | ✅ | ❌ | Requires grinder recipe |
| plate_iron | ✅ | ✅ | ❌ | ✅ | ❌ | Requires roller recipe |
| vanilla | ✅ | ✅ | ❌ | ✅ | ❌ | Requires vanilla crop |
| chocolate_bar | ✅ | ✅ | ✅ | ✅ | ❌ | |
| chocolate_chip | ✅ | ✅ | ❌ | ⚠️ | ❌ | Chocolate → chip recipe? |

---

## Bags (Storage Blocks)

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| bag_* (7 types) | ✅ | ✅ | ✅ | ✅ | ❌ | |
| double_bag_* (7 types) | ✅ | ✅ | ✅ | ✅ | ❌ | |

---

## Cakes & Desserts

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| cake_sponge_* (9 types) | ✅ | ✅ | ✅ | ⚠️ | ❌ | Consume mixing_bowl |
| cake_coffee | ✅ | ✅ | ✅ | ⚠️ | ❌ | Loot table dropSelf (P0-11) |
| cake_harvest | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_lemon | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_tea | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_berry | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_cheese | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_schwarzwald | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_redvelvet | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| tiramisu | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| mousse_* (4 types) | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| pie_cream | ✅ | ✅ | ✅ | ⚠️ | ❌ | Same |
| cake_sponge_slice | ✅ | ✅ | ✅ | ✅ | ❌ | |
| cake_model* (3 types) | ✅ | ✅ | ✅ | ❌ | ❌ | Plain Items, consumed (P0-12) |

---

## Breads

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| bread_round | ✅ | ✅ | ✅ | ✅ | ❌ | |
| baguette | ✅ | ✅ | ✅ | ✅ | ❌ | |
| bagel | ✅ | ✅ | ✅ | ✅ | ❌ | |
| toast | ✅ | ✅ | ✅ | ✅ | ❌ | |
| brownie | ✅ | ✅ | ✅ | ✅ | ❌ | |

---

## Sandwiches

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| sandwich_blt | ✅ | ✅ | ✅ | ✅ | ❌ | |
| sandwich_club | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |
| sandwich_blt_large | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |
| sandwich_club_large | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |
| sandwich_bacon_egg | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |
| sandwich_ham_cheese | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |
| sandwich_beef_cheese | ❌ | ⚠️ | ❌ | ❌ | ❌ | Deferred |

---

## Ice Creams

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| icecream_vanilla | ✅ | ✅ | ✅ | ⚠️ | ❌ | Has direct crafting fallback + machine recipe |
| Other flavors | ❌ | ❌ | ❌ | ❌ | ❌ | Deferred |

---

## Decor & Specials

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| soda_ore | ✅ | ✅ | ✅ | ❌ | ❌ | Drops self, should drop soda material 4-8 (P0-14) |
| xmas_tree | ✅ | ✅ | ✅ | ✅ | ❌ | |
| ginger_house | ✅ | ✅ | ✅ | ✅ | ❌ | |
| record_* (3 types) | ✅ | ✅ | ✅ | ✅ | ❌ | |

---

## Drink Plates (Block Form) — Deferred

| Content | R | A | O | B | T | Notes |
|---|---|---|---:|---:|---:|---:|-------|
| ~70 plate types | ❌ | ⚠️ | ❌ | ❌ | ❌ | 70+ blockstate/model files exist but no registration |

---

## Obsolete / Removed

| Old (1.12.2) | Reason | Notes |
|---|---|---|
| `forge_marker` blockstates | Replaced by standard variant format | Phase 1 ✅ |
| `coffeeworkshop` namespace | Unified to `coffeework` | Phase 1 ✅ |
| `.lang` files | Replaced by `.json` lang files | Files still present, Phase 1 planned removal |
| Static HashMap recipe maps | Replaced by data-driven RecipeManager | Phase 3 |

---

## Summary Statistics (R/A/O/B/T)

| Category | Total | R ✅ | A ✅ | O ✅ | B ✅ | T ✅ |
|---|---|---|---:|---:|---:|---:|---:|
| Machines | 5 | 5 | 5 | 5 | 0 | 0 |
| Plants & Crops | 5 | 5 | 5 | 4 | 3 | 0 |
| Drinks (hot) | 14 | 14 | 14 | 0 | 0 | 0 |
| Drinks (iced) | 12 | 12 | 12 | 0 | 0 | 0 |
| Flavored lattes | 12 | 12 | 12 | 0 | 0 | 0 |
| Americano extensions | 4 | 4 | 4 | 0 | 0 | 0 |
| Cold brew extensions | 14 | 14 | 14 | 0 | 0 | 0 |
| Instant coffee | 5 | 3 | 4 | 3 | 3 | 0 |
| Soda drinks | 8 | 0 | 0 | 0 | 0 | 0 |
| Containers & utensils | 10 | 10 | 10 | 9 | 6 | 0 |
| Ingredients & materials | 17 | 17 | 17 | 8 | 13 | 0 |
| Bags (storage) | 14 | 14 | 14 | 14 | 14 | 0 |
| Cakes & desserts | 18 | 18 | 18 | 18 | 0 | 0 |
| Breads | 5 | 5 | 5 | 5 | 5 | 0 |
| Sandwiches | 7 | 1 | 1 | 1 | 1 | 0 |
| Ice creams | 6 | 1 | 1 | 1 | 1 | 0 |
| Decor & specials | 6 | 6 | 6 | 6 | 3 | 0 |
| Drink plates | ~70 | 0 | 0 | 0 | 0 | 0 |
| **Total (excl. deferred)** | **~160** | **~141** | **~142** | **~74** | **~49** | **0** |
| **Total (all)** | **~232** | **~141** | **~142** | **~74** | **~49** | **0** |

> **Key insight:** Only ~46% of registered items are obtainable in survival. Zero items are tested.
