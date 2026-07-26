# Coffee Workshop

A Minecraft mod about coffee, tea, baking, and more — now ported to Minecraft 1.20.1 with Forge.

## About

Coffee Workshop adds a complete coffee production chain to Minecraft: grow coffee trees, harvest raw beans, roast and grind them, then brew drinks in a Coffee Machine. Alongside the core coffee loop, the mod includes tea brewing, cold brew fermentation, iced drinks, flavored syrups, baking (breads, cakes, mousses), ice cream making, and villager trading.

Originally created for Minecraft 1.12.2, this is the modern 1.20.1 port built with data-driven recipes, DeferredRegister, BlockEntity/Menu architecture, and full creative/survival parity for the core coffee loops.

## Features

### Machines

| Machine | Purpose | Fuel |
|---------|---------|------|
| **Grinder** | Grinds beans/seeds into powders | Heat (coal, etc.) |
| **Coffee Machine** | Brews hot and iced drinks | Self-powered |
| **Ice Cream Machine** | Freezes ice cream mixes | Cooling (ice, packed ice) |
| **Roller** | Rolls dough plates from dough | Heat |
| **Oven** | Bakes breads, roasts beans | Heat |

### Coffee Production Chain

```
Coffee Tree → Raw Coffee Beans → Roast → Grind → Coffee Powder
                                                        ↓
                                              Coffee Machine
                                              (+ water/milk + cup)
                                                        ↓
                                                  Hot Drink
                                              (+ ice_slag → Iced)
```

### Drink Menu

- **15+ hot drinks**: Espresso, Americano, Latte, Cappuccino, Macchiato, Mochaccino, Green Tea, Black Tea, Milk Tea, Mandarin Drink, Cold Brew, Hot Cocoa, Strong Cocoa, and more
- **15+ iced drinks**: Iced variants of all major drinks via ice_slag additive
- **6 flavored latte variants**: Caramel, Chocolate, Fruit, Mint, Vanilla, Sakura
- **7 cold brew latte extensions**: Fruit, Mint, Vanilla, and flavored lattes
- **Cooling recipes**: Transform hot drinks into iced drinks via workbench + ice_slag (preserves NBT/cup count)

### Food & Baking

- **Breads**: Round Bread, Baguette, Bagel, Toast, Brownie
- **Sponge Cakes (9 flavors)**: Plain, Chocolate, Coffee, Pumpkin, Carrot, Red Velvet, Lemon, Matcha, Berry
- **Full Cakes (8 types)**: Coffee, Harvest, Lemon, Matcha, Berry, Cheese, Schwarzwald, Red Velvet, Tiramisu
- **Mousses (4 types)**: Berry, Lemon, Chocolate, Coffee
- **Sandwiches**: BLT (more planned)
- **Ice Cream**: Vanilla (more flavors planned)
- **Storage**: Single and double bags for beans, powders, flour, sugar

### Villagers

- **Coffee Barista** — Trades coffee drinks, beans, powders
- **Coffee Materials Trader** — Trades instant coffee items
- **Food Trader** — Trades cakes, breads, baking supplies

### World Generation

- Coffee Trees spawn in overworld (forest/plains biomes)
- Blueberry Bushes generate naturally
- Soda Ore deposits underground

## Requirements

- **Minecraft**: 1.20.1
- **Forge**: 47.x (recommended 47.4.10+)
- **Java**: 17+
- **JEI** (optional): Recommended for recipe browsing

## Building from Source

```bash
git clone https://github.com/CashewTeam/CoffeeWorkshop.git
cd CoffeeWorkshop
git checkout 1.20.1
./gradlew build
```

The compiled JAR will be in `build/libs/`.

Run DataGen (generates recipes, loot tables, tags):
```bash
./gradlew runData
```

Run GameTests:
```bash
./gradlew runGameTestServer
```

## Development Status

**Current Phase**: Porting Closure — resource and content finalization.

| Metric | Status |
|--------|--------|
| Registered Items | 176 items, 49 blocks |
| Item Models | 100% |
| Textures | 100% |
| Translations (en/zh) | 100% |
| Japanese (ja_jp) | Community-contributed (incomplete) |
| Creative Tab Coverage | 99% (1 intermediate item excluded) |
| Survival Sources | 60% (core coffee/machine loops complete) |
| GameTests | 56 passing |

The core coffee and machine production loops are available in survival.
Some legacy variant items remain creative-only or trade-exclusive.
See `docs/CONTENT_MANIFEST.md` for the full per-item breakdown.

- ✅ **Phase 1**: Registry modernization, resource format migration
- ✅ **Phase 2**: Unified machine architecture (BlockEntity/Menu/RecipeManager)
- ✅ **Phase 3**: Core drink recipes, world generation
- ✅ **Phase 4**: Full coffee brewing schema (5-slot), P0 drink recovery, flavored syrups, cooling recipes
- 🔄 **Closure**: Language completion, content manifest, resource audit (current)
- 📋 **Planned**: Remaining ice cream flavors, sandwiches, soda drinks, advancements

## License

Original mod by the Coffee Workshop team. This port maintained by the CashewTeam.

## Credits

- **Original 1.12.2 mod**: Coffee Workshop team
- **1.20.1 port**: CashewTeam
- **Translations**: Community contributors (zh_cn, ja_jp)
