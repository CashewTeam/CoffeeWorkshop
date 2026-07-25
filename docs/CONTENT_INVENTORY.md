# Coffee Workshop — Content Inventory

> **Last updated:** 2026-07-25  
> **Branch:** 1.20.1  
> **This document compares the 1.12.2 (master) content with the current 1.20.1 port status.**

---

## Legend

| Icon | Meaning |
|------|---------|
| ✅ | Ported / fully functional |
| 🔄 | Renamed (old name → new name) |
| 📦 | Merged (multiple old items into one) |
| 🆕 | New in 1.20.1 |
| ⏳ | Deferred to later phase |
| ❌ | Removed / not ported |

---

## Machines

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| `grinder` (off/on) | `grinder` (single + LIT) | ✅ | Phase 2 refactor |
| `coffeemachine` (off/on) | `coffee_machine` (single + LIT) | 🔄 | Renamed, single block |
| `icecreammachine` (off/on) | `icecream_machine` (single + LIT) | 🔄 | Renamed, single block |
| `roller` (off/on) | `roller` (single + LIT) | ✅ | Phase 2 refactor |
| `oven` (off/on) | `oven` (single + LIT) | ✅ | Phase 2 refactor |
| — | Clay Oven (`oven` formerly `clay_oven`) | 🔄 | Now `oven` |

---

## Plants & Crops

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| `coffee_tree` | `coffee_tree` | ✅ | Phase 4: growth, harvest, seeds |
| `blueberry_bush` | `blueberry_bush` | ✅ | Phase 4: age, harvest, bonemeal |
| `vanilla_crop` | `vanilla_crop` | ✅ | Phase 4: crop behavior |
| `coffee_seeds` | `coffee_seeds` | ✅ | |
| `vanilla_seeds` | `vanilla_seeds` | ✅ | |

---

## Drinks (Hot)

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Espresso | `espresso` | ✅ | Multi-cup, returns cup |
| Café Americano | `coffee_americano` | ✅ | Multi-cup, returns cup |
| Café Latte | `coffee_latte` | ✅ | Multi-cup, returns cup |
| Cappuccino | `coffee_cappuccino` | ✅ | Multi-cup, returns cup |
| Caramel Macchiato | `coffee_macchiato` | ✅ | Multi-cup, returns cup |
| Mochaccino | `coffee_mochaccino` | ✅ | Multi-cup, returns cup |
| Green Tea | `coffee_green_tea` | ✅ | Returns glass |
| Black Tea | `coffee_black_tea` | ✅ | Returns glass |
| Milk Tea | `coffee_milk_tea` | ✅ | Returns glass |
| Mandarin Drink | `coffee_mandarin_drink` | ✅ | Returns glass |
| Cold Brew | `coffee_coldbrew` | ✅ | Returns glass |
| Hot Cocoa | `cocoa` | ✅ | Returns cup |
| Strong Cocoa | `cocoa_strong` | ✅ | Returns cup |
| Instant Coffee | `coffee_instant` | ✅ | Packaging-based |

---

## Drinks (Iced)

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Iced Americano | `coffee_americano_ice` | ✅ | Returns glass |
| Iced Latte | `coffee_latte_ice` | ✅ | Returns glass |
| Iced Cappuccino | `coffee_cappuccino_ice` | ✅ | Returns glass |
| Iced Macchiato | `coffee_macchiato_ice` | ✅ | Returns glass |
| Iced Mochaccino | `coffee_mochaccino_ice` | ✅ | Returns glass |
| Iced Green Tea | `coffee_green_tea_ice` | ✅ | Returns glass |
| Iced Black Tea | `coffee_black_tea_ice` | ✅ | Returns glass |
| Iced Milk Tea | `coffee_milk_tea_ice` | ✅ | Returns glass |
| Iced Mandarin Drink | `coffee_mandarin_drink_ice` | ✅ | Returns glass |
| Iced Cold Brew | `coffee_coldbrew_ice` | ✅ | Returns glass |
| Iced Cocoa | `cocoa_ice` | ✅ | Returns glass |
| Iced Strong Cocoa | `cocoa_strong_ice` | ✅ | Returns glass |

---

## Flavored Latte Variants

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Caramel Latte | `coffee_latte_caramel` | ✅ | Hot, returns cup |
| Chocolate Latte | `coffee_latte_chocolate` | ✅ | Hot, returns cup |
| Fruit Latte | `coffee_latte_fruit` | ✅ | Hot, returns cup |
| Mint Latte | `coffee_latte_mint` | ✅ | Hot, returns cup |
| Vanilla Latte | `coffee_latte_vanilla` | ✅ | Hot, returns cup |
| Sakura Latte | `coffee_latte_sakura` | ✅ | Hot, returns cup |
| Iced Caramel Latte | `coffee_latte_caramel_ice` | ✅ | Returns glass |
| Iced Chocolate Latte | `coffee_latte_chocolate_ice` | ✅ | Returns glass |
| Iced Fruit Latte | `coffee_latte_fruit_ice` | ✅ | Returns glass |
| Iced Mint Latte | `coffee_latte_mint_ice` | ✅ | Returns glass |
| Iced Vanilla Latte | `coffee_latte_vanilla_ice` | ✅ | Returns glass |
| Iced Sakura Latte | `coffee_latte_sakura_ice` | ✅ | Returns glass |

---

## Americano Extensions

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Fruit Americano | `coffee_americano_fruit` | ✅ | Returns cup |
| Iced Fruit Americano | `coffee_americano_fruit_ice` | ✅ | Returns glass |
| Nitro Americano | `coffee_americano_nitro_ice` | ✅ | Returns glass |
| Nitro Fruit Americano | `coffee_americano_nitro_fruit_ice` | ✅ | Returns glass |

---

## Cold Brew Extensions

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Fruit Cold Brew | `coffee_coldbrew_fruit` | ✅ | Returns glass |
| Cold Brew Latte | `coffee_coldbrew_latte` | ✅ | Returns glass |
| Caramel Cold Brew Latte | `coffee_coldbrew_latte_caramel` | ✅ | Returns glass |
| Chocolate Cold Brew Latte | `coffee_coldbrew_latte_chocolate` | ✅ | Returns glass |
| Fruit Cold Brew Latte | `coffee_coldbrew_latte_fruit` | ✅ | Returns glass |
| Mint Cold Brew Latte | `coffee_coldbrew_latte_mint` | ✅ | Returns glass |
| Vanilla Cold Brew Latte | `coffee_coldbrew_latte_vanilla` | ✅ | Returns glass |
| Iced Fruit Cold Brew | `coffee_coldbrew_fruit_ice` | ✅ | Returns glass |
| Iced Cold Brew Latte | `coffee_coldbrew_latte_ice` | ✅ | Returns glass |
| Iced Caramel Cold Brew Latte | `coffee_coldbrew_latte_caramel_ice` | ✅ | Returns glass |
| Iced Chocolate Cold Brew Latte | `coffee_coldbrew_latte_chocolate_ice` | ✅ | Returns glass |
| Iced Fruit Cold Brew Latte | `coffee_coldbrew_latte_fruit_ice` | ✅ | Returns glass |
| Iced Mint Cold Brew Latte | `coffee_coldbrew_latte_mint_ice` | ✅ | Returns glass |
| Iced Vanilla Cold Brew Latte | `coffee_coldbrew_latte_vanilla_ice` | ✅ | Returns glass |

---

## Instant Coffee & Accessories

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Instant Coffee | `coffee_instant` | ✅ | |
| Instant Coffee Stick | `coffee_instant_stick` | ✅ | |
| Instant Coffee Box | `coffee_instant_box` | ✅ | |
| Instant Coffee Cup | `coffee_instant_cup` | ⏳ | Model exists, not registered |
| Instant Coffee Cup (unopened) | `coffee_instant_cup_unopen` | ⏳ | Model exists, not registered |

---

## Drinks (Soda) — Deferred

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Soda Water | `soda_drink` | ⏳ | Translations exist, not registered |
| Cola | `soda_drink_cola` | ⏳ | Translations exist, not registered |
| Lemon Soda | `soda_drink_lemon` | ⏳ | Translations exist, not registered |
| Berry Soda | `soda_drink_berry` | ⏳ | Translations exist, not registered |
| Cherry Cola | `soda_drink_cherry` | ⏳ | Translations exist, not registered |
| Vanilla Soda | `soda_drink_vanilla` | ⏳ | Translations exist, not registered |
| Apple Soda | `soda_drink_apple` | ⏳ | Translations exist, not registered |
| Chocolate Soda | `soda_drink_chocolate` | ⏳ | Translations exist, not registered |

---

## Containers & Utensils

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Cup (ceramic) | `cup` | ✅ | Returned by hot drinks |
| Glass | `cup_glass` | ✅ | Returned by iced drinks |
| Plate | `plate` | ✅ | Block + item |
| Cold Brew Pot | `coldbrew_pot` | ✅ | Block |
| Empty Cold Brew Pot | `empty_coldbrew_pot` | ✅ | |
| Cold Brew Bottle | `coldbrew_bottle` | ✅ | |
| Syrup (empty) | `syrup_empty` | ✅ | |
| Iron Bowl | `iron_bowl` | ✅ | |
| Mixing Bowl | `mixing_bowl` | ✅ | |
| Bag | `bag` | ✅ | |
| Bag Cloth | `bag_cloth` | ✅ | |

---

## Ingredients & Materials

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Raw Coffee Bean | `coffee_bean_raw` | ✅ | |
| Roast Coffee Bean | `coffee_bean` | ✅ | |
| Coffee Powder | `coffee_powder` | ✅ | |
| Cocoa Bean | `cocoa_bean` | ✅ | |
| Cocoa Powder | `cocoa_powder` | ✅ | |
| Cocoa Batter | `cocoa_batter` | ✅ | |
| Flour | `flour` | ✅ | |
| Dough (+ variants) | `dough*` | ✅ | 12 dough variants |
| Butter | `butter` | ✅ | |
| Cheese | `cheese` | ✅ | |
| Yeast | `yeast` | ✅ | |
| Gelatin | `gelatin` | ✅ | |
| Soda (baking) | `soda` | ✅ | |
| Spices | `spices` | ✅ | |
| Ice Slag | `ice_slag` | ✅ | |
| Iron Plate | `plate_iron` | ✅ | |
| Vanilla | `vanilla` | ✅ | |
| Chocolate Bar | `chocolate_bar` | ✅ | |
| Chocolate Chip | `chocolate_chip` | ✅ | |

---

## Bags (Storage Blocks)

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| 7 single bag types | `bag_*` | ✅ | coffee, cocoa, flour, sugar, etc. |
| 7 double bag types | `double_bag_*` | ✅ | |

---

## Cakes & Desserts

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| 9 sponge cake types | `cake_sponge_*` | ✅ | |
| Coffee Cake | `cake_coffee` | ✅ | |
| Harvest Cake | `cake_harvest` | ✅ | |
| Lemon Cake | `cake_lemon` | ✅ | |
| Tea Cake | `cake_tea` | ✅ | |
| Berry Cake | `cake_berry` | ✅ | |
| Cheese Cake | `cake_cheese` | ✅ | |
| Schwarzwald Cake | `cake_schwarzwald` | ✅ | |
| Red Velvet Cake | `cake_redvelvet` | ✅ | |
| Tiramisu | `tiramisu` | ✅ | |
| 4 mousse types | `mousse_*` | ✅ | |
| Cream Pie | `pie_cream` | ✅ | |
| Sponge Cake Slice | `cake_sponge_slice` | ✅ | |
| Cake Models | `cake_model*` | ✅ | 3 types |

---

## Breads

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Round Bread | `bread_round` | ✅ | |
| Baguette | `baguette` | ✅ | |
| Bagel | `bagel` | ✅ | |
| Toast | `toast` | ✅ | |
| Brownie | `brownie` | ✅ | |

---

## Sandwiches

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| BLT Sandwich | `sandwich_blt` | ✅ | |
| Club Sandwich | `sandwich_club` | ⏳ | Translations exist |
| Large BLT | `sandwich_blt_large` | ⏳ | Translations exist |
| Large Club | `sandwich_club_large` | ⏳ | Translations exist |
| Bacon Egg Sandwich | `sandwich_bacon_egg` | ⏳ | Translations exist |
| Ham Cheese Bagel | `sandwich_ham_cheese` | ⏳ | Translations exist |
| Cheeseburger | `sandwich_beef_cheese` | ⏳ | Translations exist |

---

## Ice Creams

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Vanilla Ice Cream | `icecream_vanilla` | ✅ | |
| Other flavors | — | ⏳ | Icecream machine recipes deferred |

---

## Decor & Specials

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| Soda Ore | `soda_ore` | ✅ | |
| Xmas Tree | `xmas_tree` | ✅ | |
| Ginger House | `ginger_house` | ✅ | |
| Records (3) | `record_*` | ✅ | |

---

## Drink Plates (Block Form) — Deferred

| Old (1.12.2) | New (1.20.1) | Status | Notes |
|---|---|---|---|
| ~70 plate blockstates | — | ⏳ | 70+ blockstate/block model files exist, not registered as blocks. Deferred until plate system design is finalized. |

---

## Obsolete / Removed

| Old (1.12.2) | Reason | Notes |
|---|---|---|
| `forge_marker` blockstates | Replaced by standard variant format | Phase 1 |
| `coffeeworkshop` namespace | Unified to `coffeework` | Phase 1 |
| `.lang` files | Replaced by `.json` lang files | Phase 1 |
| Static HashMap recipe maps | Replaced by data-driven RecipeManager | Phase 3 |

---

## Summary Statistics

| Category | Total | ✅ Ported | ⏳ Deferred | ❌ Removed |
|---|---|---|---|---|
| Machines | 5 | 5 | 0 | 0 |
| Plants & Crops | 5 | 5 | 0 | 0 |
| Drinks (hot) | 13 | 13 | 0 | 0 |
| Drinks (iced) | 12 | 12 | 0 | 0 |
| Flavored lattes | 12 | 12 | 0 | 0 |
| Americano extensions | 4 | 4 | 0 | 0 |
| Cold brew extensions | 14 | 14 | 0 | 0 |
| Instant coffee + extras | 5 | 3 | 2 | 0 |
| Soda drinks | 8 | 0 | 8 | 0 |
| Containers & utensils | 12 | 12 | 0 | 0 |
| Ingredients & materials | 17 | 17 | 0 | 0 |
| Bags (storage) | 14 | 14 | 0 | 0 |
| Cakes & desserts | 18 | 18 | 0 | 0 |
| Breads | 5 | 5 | 0 | 0 |
| Sandwiches | 6 | 1 | 5 | 0 |
| Ice creams | 6 | 1 | 5 | 0 |
| Decor & specials | 6 | 6 | 0 | 0 |
| Drink plates | ~70 | 0 | ~70 | 0 |
| **Total** | **~232** | **~132** | **~90** | **~10** |
