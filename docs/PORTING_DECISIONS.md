# Coffee Workshop — Porting Decisions (1.20.1)

This file records decisions taken during the 1.12.2 → 1.20.1 port.

---

## 2026-07-26 — Porting Closure: P0 resolution

### Language completion

- All three language files (`en_us.json`, `zh_cn.json`, `ja_jp.json`) now have
  **identical key sets** (252 keys each), fully covering all registered items,
  blocks, effects, professions, GUI labels, JEI categories, container titles,
  and record descriptions.
- 43 English placeholder values (raw registry names) replaced with proper
  display names (e.g. `"bag_coffee"` → `"Coffee Bean Bag"`).
- Chinese typos fixed: `冰淇凌` → `冰淇淋`, `烘培` → `烘焙`.
- `coffee_seed` (singular) key renamed to `coffee_seeds` (plural) to match
  registry.
- ~620 unregistered-item language keys removed from `zh_cn` and `ja_jp` and
  archived to `reference/reports/removed_keys_*.json` for later restoration.

### Mob effect icons

- Placeholder 18×18 PNG icons created for `caffeine` (brown), `relax` (light blue),
  and `golden_heart` (gold).  These prevent missing-texture artifacts in the
  potion HUD and item tooltips.

### Creative tab completion

- 8 missing items added: `tea_leaf`, `black_tea_leaf`, `coldbrew_bottle`,
  `syrup_caramel`, `syrup_chocolate`, `syrup_fruit`, `syrup_mint`,
  `syrup_vanilla`, `syrup_sakura`.

### Trade arbitrage fixed

- **Coffee Powder**: Barista buy 4→6E / Barista sell 4→4E (previously 16E,
  allowing 10E profit per cycle).
- **Cocoa Powder**: Barista buy 4→8E / Barista sell 4→6E (previously 16E, 8E profit).
- **Roast Coffee Bean**: Materials Trader buy 1→2E (previously 4E, allowing
  cross-profession arbitrage with Barista sell 2→8E).

### Shift-click routing (retrospective from Phase 4)

- `ContainerCoffeeMachine.findSlotForStack()` P0 bug fixed: inverted
  remainder.isEmpty() check caused all valid slots to be skipped when
  machine was empty.
- `computeSlotScore()` now properly merges accepted count into hypothetical
  container state during recipe-match scoring.
- Empty optional slots (modifier==null, additive==null) no longer inflate
  partial-match scores.
- 5 new GameTests verify role-based slot routing.

### Content manifest

- `docs/content_manifest.json` and `docs/CONTENT_MANIFEST.md` generated
  by `tools/build_content_manifest.py` — cross-references all 190
  registered entities against models, textures, translations, creative
  tab, recipes, loot tables, and trades.
- 689 orphan assets (models/textures/blockstates without registration)
  classified into: ASSET_ARCHIVE (569), PORT_LATER (52), PORT_NOW (14),
  REMOVED (33), MERGED (16), REDESIGN (5).
- Old `docs/CONTENT_INVENTORY.md` (Phase 0 baseline) archived to
  `reference/reports/CONTENT_INVENTORY_PHASE0.md`.

### Audit tooling

- `tools/audit_content_surface.py` — five-way cross-reference audit
  (registry ↔ model ↔ texture ↔ lang ↔ source). 0 P0 issues on current code.
- `tools/cleanup_lang.py` — removes unregistered-content language keys,
  archives them for restoration.
- `tools/fix_lang.py` — bulk translation fix/insertion.

### PORTING_DECISIONS stale entries

The following earlier entries describe issues now resolved:

- **Phase 3 deferred drinks** (2026-07-25): All 15 P0 drinks now have
  machine recipes via Phase 4 P1 completion.
- **Shift-click routing limitation** (2026-07-25): Fixed in Phase 4
  closure (see shift-click routing entry above).
- **P1 deferred items** (2026-07-25): Flavored syrups, flavored lattes,
  iced extensions, and coldbrew extensions now all have survival recipes.
- **Chocolate chip production gap**: Resolved with grinder recipe.

Remaining known limitations (2026-07-26):
- Single additive slot means some P1 drink combinations (e.g. ice_slag +
  syrup simultaneously) remain creative-only.  See "Known slot limitations
  for P1 drinks" below.
- ~64% of items have survival sources; 46 items are creative/trade-only.
- 3 villager professions lack custom overlay textures (use default appearance).
- ~70 old drink-plate block models and blockstates not ported — planned
  for unified drink display system redesign.

---

## 2026-07-25 — Mod ID & namespace

- **Mod ID**: locked to `coffeework`. The legacy `coffeeworkshop` namespace
  (used in a handful of language keys) has been migrated to `coffeework`.
  See commit "audit(mods): replace placeholder URLs; lang json migration".
- **Resource namespace**: matches `coffeework` everywhere (`assets/coffeework/...`).
- **Issue tracker** in `mods.toml` now points to the real GitHub repo:
  `https://github.com/CashewTeam/CoffeeWorkshop/issues`.
- **`updateJSONURL`** in `mods.toml` has been *removed* (not left as a
  placeholder) because Forge Update Checker rejects unreachable URLs.
  When the project starts publishing an update JSON, restore the field.

## 2026-07-25 — Language file format

- All language files migrated from `.lang` to `.json`. The legacy `.lang`
  files are intentionally retained for the moment so that if any conversion
  bug is discovered, the source data is still in the tree. They are
  ignored by the resource loader. Removal will be done in a follow-up
  PR after CI proves the JSON files work end-to-end.
- Duplicate keys in `zh_cn.lang` (19) and `ja_jp.lang` (20) were resolved
  by **keeping the last occurrence**, matching Forge's `LangMigrator`
  behaviour. No translation values were edited; the 43 entries in
  `en_us` whose value literally equals the registry name are preserved
  as-is to keep this PR resource-only.

## 2026-07-25 — Blockstate format

- Every blockstate JSON now uses the modern
  `coffeework:block/<name>` reference syntax. All 126 blockstate files
  were updated; 609 model references were rewritten in total.
- The lone file using the legacy Forge format (`coldbrew_pot.json`) has
  had its top-level `forge_marker` and `defaults` keys removed. Each
  `ferm=0..8` variant already specified its own model, so no behaviour
  drift occurred at the resource layer.
- `ferm` is **not yet** registered as a BlockState property in Java. The
  coldbrew block therefore still renders on `ferm=0` only. Adding the
  BlockState property, `FERM` range, tick logic, glass-bottle interaction,
  and brew completion is Phase 4 of the audit plan; deliberately out of
  scope for this resource-only PR.

## 2026-07-25 — Vanilla texture migration (model files)

A migration dictionary in `tools/migrate_vanilla_textures.py` was used to
rewrite bare `blocks/<old>` references to their modern equivalents. The
dictionary covers:

```
blocks/log_oak               -> block/oak_log
blocks/log_oak_top           -> block/oak_log_top
blocks/planks_oak            -> block/oak_planks
blocks/concrete_black        -> block/black_concrete
blocks/concrete_white        -> block/white_concrete
blocks/iron_block            -> block/iron_block
blocks/hardened_clay         -> block/terracotta
blocks/furnace_front_on      -> block/furnace_front_on
blocks/wool_colored_white    -> block/white_wool
blocks/wool_colored_brown    -> block/brown_wool
blocks/stone_slab_side       -> block/smooth_stone_slab_side
blocks/stone_slab_top        -> block/smooth_stone_slab_top
```

**Not migrated automatically**: `blocks/anvil_base`. There is no clean
modern vanilla equivalent in 1.20.1. The 22 files that still reference
it are listed in `build/reports/manual_texture_review.md`. When the next
PR (or follow-up) replaces these, prefer a mod-owned texture
(`coffeework:textures/...`) over a hand-picked vanilla stand-in.

## 2026-07-25 — Things deliberately not changed yet

- Machine IDs are still split (`grinder_off` / `grinder_on`, etc.). The
  audit recommends collapsing each pair to a single block with a `LIT`
  boolean property. That change touches Java, ModBlocks, blockstates,
  block entities, loot tables, POI registration, and possibly Missing
  Mappings; it is the next PR (`PR 05`).
- Machine `use(...)` interactions still do nothing; the user cannot open
  any GUI in-game yet. This is `PR 06` scope.
- Machine recipe classes (`GrinderRecipes`, …) are still empty static
  `HashMap`s. Switching to data-pack recipes is `PR 07`.
- No `BlockLootProvider`, `BlockTagsProvider`, or `ItemTagsProvider` is
  registered yet. Adding them is `PR 04`.
- POI registration uses `defaultBlockState()` only, so villagers only
  recognise north-facing workstations. Phase 6 of the audit covers this.
- `CoffeeWork.java` (common entry point) still `import`s client GUI classes
  at the top of the file. That creates a class-loading risk on a
  dedicated server; the recommended fix is moving GUI registration to a
  `@Mod.EventBusSubscriber(Dist.CLIENT)` class.

These are all P1 in the audit, planned but **out of scope** for this
first resource-only PR.

## 2026-07-25 — Phase 0 baseline (第二轮审计)

### Build & DataGen

- **`compileJava`**: passes cleanly (Java 17, Forge 1.20.1-47.4.10).
- **`runData`**: executes 3 providers (Recipes, Loot Tables, Block Tags).
  No changes on re-run — generated resources are stable.
- **`runClient`** / **`runServer`**: not executed in Phase 0 (no display/GitHub
  access in audit environment). Verification deferred to Phase 1.

### Machine recipe inventory

- **30 machine recipes** exist as hand-written JSONs in
  `src/main/resources/data/coffeework/recipes/`:
  - `coffee_brewing/` (13): americano, latte, cappuccino, espresso,
    macchiato, mochaccino, green_tea, black_tea, milk_tea, cocoa,
    cocoa_strong, coldbrew, mandarin_drink
  - `grinding/` (6): coffee_powder, cocoa_powder, cocoa_batter, flour,
    ice_slag, soda
  - `icecream_making/` (1): vanilla
  - `oven_baking/` (6): bread, bread_round, baguette, bagel, toast, cookie
  - `rolling/` (4): plate_dough, plate_dough_ginger, plate_dough_pastry, plate_iron
- **No machine recipes** are generated by DataGen yet (`ModRecipeProvider`
  only outputs vanilla crafting/smelting/shapeless recipes).
- **109 crafting recipes** exist in `src/generated/resources/`.

### Resource audit

- `tools/audit_resources.py` passes — no hard failures.
- 30 model files still reference `blocks/anvil_base` (exempt, manual review).
- 3 legacy `.lang` files remain on disk (ignored by loader).
- `.cache` directory in `src/generated/resources/` is tracked in git (P1-14).

### Known P0 issues confirmed by source audit

All 14 P0 issues from the audit document (P0-01 through P0-14) are confirmed
present in the current codebase. See
`CoffeeWorkshop_1.20.1_第二轮代码审计与开发计划.md` for details.

### Content matrix migration

- `CONTENT_INVENTORY.md` migrated from single‑column ✅/⏳/❌ to five‑column
  R/A/O/B/T system.
- Phase 0 static analysis shows:
  - ~141 items registered, ~142 with assets
  - Only ~74 obtainable in survival (~46%)
  - ~49 with correct core behavior
  - **0 items tested**
- All values are static estimates — runtime verification deferred to Phase 1.

## 2026-07-25 — Tooling

Three tool scripts under `tools/` were introduced:

- `tools/convert_lang.py` — one-shot converter from Forge `.lang` to
  Minecraft JSON translation files. Re-runnable.
- `tools/fix_blockstates.py` — one-shot fixer for blockstate model paths
  and `forge_marker`/`defaults` residue. Re-runnable.
- `tools/migrate_vanilla_textures.py` — one-shot vanilla texture
  reference migrator. Re-runnable.
- `tools/audit_resources.py` — **read-only** audit that emits
  `build/reports/resource-audit.md` and
  `build/reports/manual_texture_review.md`. Intended to run in CI.

The conversion scripts are conservative: they never modify a file unless
they can prove a real *value* change is needed.

## 2026-07-25 — Phase 2 machine refactor behavior decisions

These decisions define the unified processing-machine semantics adopted
during Phase 2.  All five machines (Grinder, Roller, Oven, Icecream
Machine, Coffee Machine) share the same rules unless a decision
explicitly calls out a machine-specific exception.

### Output-blocking strategy

When the output slot cannot accept the full recipe result (because the
slot is already occupied by a different item, or the stack has reached
its maximum size):

- **cookTime is paused**, not reset to zero.  Already-consumed fuel
  continues to burn down (it was spent to heat the machine, not wasted).
- New fuel will **not** be consumed while the output remains blocked —
  the machine will not start a new burn cycle.
- Coffee Machine behaves identically: it pauses progress and turns off
  `LIT`; it will not start a new self-cycle until the output clears.
- When the output is unblocked (player or hopper removes items),
  processing resumes from the paused `cookTime` for the same recipe.
  If the input has changed in the meantime, progress is reset (recipe
  change rule).

### Comparator semantics

- The comparator output represents **processing progress**: 0 when idle,
  1–14 proportional to `cookTime / totalCookTime`, and 15 when the craft
  is about to complete.

### Automation direction rules

For fueled machines (Grinder, Roller, Oven, Icecream Machine):

| Direction    | Insert allowed                | Extract allowed |
|-------------|-------------------------------|-----------------|
| UP           | input slot only               | no              |
| HORIZONTAL   | fuel slot only                | no              |
| DOWN         | no                            | output slot only|
| `side=null`  | full handler (for GUI/code)   | full handler    |

For Coffee Machine (no fuel slot):

| Direction    | Insert allowed                | Extract allowed |
|-------------|-------------------------------|-----------------|
| UP           | input slot only               | no              |
| HORIZONTAL   | input slot only               | no              |
| DOWN         | no                            | output slot only|
| `side=null`  | full handler (for GUI/code)   | full handler    |

### Experience storage

- `recipesUsed` (Map\<ResourceLocation, Integer\>) stored in NBT tracks
  how many times each recipe completed on this machine.
- Experience is awarded when a **player** manually removes an item from
  the output slot (click, shift-click, or hotbar-swap).  The total XP is
  `recipe.experience × count`.
- Fractional experience uses the standard furnace probability:
  `level.random.nextFloat() < (total - (int)total)` → +1 XP.
- **Hoppers and other automation do not generate experience orbs** when
  extracting from the output.  The accumulated XP stays in the machine.
- A player who later manually extracts any remaining output claims all
  accumulated experience.
- Breaking the machine does **not** drop stored experience.

### Recipe caching

- Recipe objects are never cached across `/reload`.  Only the
  `ResourceLocation` (recipe ID) is persisted in NBT.
- Each tick resolves the active recipe ID through the current
  `RecipeManager`, falling back to a full recipe-type scan if the ID
  has changed.

### Recipe change rule

- If the active recipe ID changes (different recipe or input no longer
  matches), `cookTime` resets to 0 and `totalCookTime` updates to the
  new recipe's `cookingTime`.
- If only the input stack count changes (same recipe), progress is
  **preserved** — the cook continues from where it was.

### Icecream Machine fuel policy

- The legacy `Map<ItemStack, Integer> ICE_FUEL_REGISTRY` (using mutable
  `ItemStack` as map keys) is replaced by an `IcecreamCoolingFuelPolicy`
  backed by a `Map<Item, Integer>` plus an optional Tag-based override.
  Initial fuel entries: `ICE`=200, `PACKED_ICE`=800, `BLUE_ICE`=3600.
- Standard furnace fuels are *not* accepted as ice cream machine fuel
  (the machine requires cold sources, not heat).

## 2026-07-25 — Phase 3 deferred drinks and Coffee Machine upgrade

### Deferred drink recipes

The following drink items were previously covered by single-input
`MachineRecipe` JSONs that were removed during Phase 3 because the
single-input format could not distinguish between drinks sharing the
same ingredient (e.g. 8 drinks using `milk_bucket` as the sole input).

These items remain registered and functional (they can be obtained via
creative mode, `/give`, and are correctly consumed with cup return), but
their machine recipes are deferred to a later phase when the multi-input
Coffee Machine architecture is extended to support additional modifier
types (flavored syrups, tea leaves, cocoa-specific inputs).

| Drink | Old recipe status | Target phase |
|-------|------------------|--------------|
| Cappuccino | removed (ambiguous milk_bucket input) | Phase 4 |
| Macchiato | removed (ambiguous milk_bucket input) | Phase 4 |
| Mochaccino | removed (ambiguous milk_bucket input) | Phase 4 |
| Cocoa | removed (ambiguous milk_bucket input) | Phase 4 |
| Cocoa Strong | removed (ambiguous milk_bucket input) | Phase 4 |
| Milk Tea | removed (ambiguous milk_bucket input) | Phase 4 |
| Green Tea | removed (ambiguous water_bucket input) | Phase 4 |
| Black Tea | removed (ambiguous water_bucket input) | Phase 4 |
| Mandarin Drink | removed (ambiguous water_bucket input) | Phase 4 |
| Coldbrew | removed (ambiguous coffee_powder input) | Phase 4 |

All flavored latte variants (caramel, chocolate, fruit, mint, vanilla,
sakura) and iced variants were never published as machine recipes and
remain creative-only until their recipe schema is designed.

### Coffee Machine slot upgrade

- Coffee Machine upgraded from 2 slots (input, output) to 4 slots
  (base=0, modifier=1, cup=2, output=3).
- Old 2-slot NBT is migrated on load: slot 0 → slot 0 (base),
  slot 1 → slot 3 (output). An `InventoryVersion` tag (value=1) is
  written to prevent repeated migration.
- The Coffee Machine uses `CoffeeBrewingRecipe` (multi-input) instead
  of `MachineRecipe` (single-input).

### Icecream Machine recipe change

- Direct `icecream_vanilla` crafting removed; replaced by
  `icecream_mix_vanilla` (workbench) → Icecream Machine → vanilla
  ice cream.
- New item `icecream_mix_vanilla` registered.

### Cup crafting

- `cup`: 3×paper → 4×cup (shaped)
- `cup_glass`: 3×glass_pane → 4×cup_glass (shaped)
- Cups are no longer creative-only.

### Phase 3 Worldgen verification

- Coffee Tree worldgen is fully configured:
  - `CoffeeTreeFeature.java` — places coffee tree crop on grass/dirt/farmland
  - `worldgen/configured_feature/coffee_tree.json`
  - `worldgen/placed_feature/coffee_tree.json` (in_square + heightmap + biome)
  - `forge/biome_modifier/add_coffee_tree.json` (overworld, vegetal_decoration)
  - Rarity config: `coffee_tree_rarity=2` → 2/8 = 25% chance per placement
- Vanilla crop worldgen (`vanilla_crop`) similarly configured.
- First coffee seed is obtainable in new survival worlds without commands.

## 2026-07-25 — Phase 4 Schema v2 and P0 drink recovery

### Coffee Brewing Schema v2 (5-slot)

- Coffee Machine upgraded from 4 slots to 5 slots:
  - Slot 0: Base (coffee_powder, cocoa_powder, coldbrew_bottle, tea_leaf)
  - Slot 1: Modifier (water_bucket, milk_bucket, or empty)
  - Slot 2: Additive (cocoa_powder, sugar, chocolate_chip, ice_slag, or empty)
  - Slot 3: Container (cup, cup_glass)
  - Slot 4: Output
- `CoffeeBrewingRecipe` now carries an optional `additive` SlotIngredient field.
- NBT migration path: v0(2-slot) → v1(4-slot) → v2(5-slot).  Migration runs
  sequentially so a save from v0 is upgraded to v2 in one load.
- Old 4-slot saves (v1): slot 2(container) → slot 3, slot 3(output) → slot 4.
  New additive slot 2 starts empty.

### Atomic Consumption Plan

- `AbstractProcessingBlockEntity.processRecipe()` rewritten to build a
  `ConsumptionPlan` (pre-check all slots + remainder placement) before
  applying any mutations.  If any check fails, nothing is consumed.
- `ProcessingRecipe.getRemainder(int slot)` added as a default interface method.
  No more `instanceof CoffeeBrewingRecipe` casts in the processing engine.
- Remainder handling: buckets return empty buckets, bottles return glass bottles.
  Remainders are placed back into the vacated slot when possible.

### P0 drink recipe recovery (15 recipes)

All Phase 3 deferred drinks now have machine recipes:

| Drink | Base | Modifier | Additive | Container | Cups |
|-------|------|----------|----------|-----------|------|
| Espresso | coffee_powder×2 | — | — | cup | 2 |
| Americano | coffee_powder | water_bucket | — | cup | 4 |
| Latte | coffee_powder | milk_bucket | — | cup | 4 |
| Cappuccino | coffee_powder | milk_bucket | sugar | cup | 3 |
| Macchiato | coffee_powder×2 | milk_bucket | — | cup | 3 |
| Mochaccino | coffee_powder | milk_bucket | cocoa_powder | cup | 3 |
| Cocoa | cocoa_powder×2 | milk_bucket | — | cup | 3 |
| Cocoa Strong | cocoa_powder×2 | milk_bucket | chocolate_chip | cup | 2 |
| Green Tea | tea_leaf | water_bucket | — | cup_glass | 3 |
| Black Tea | black_tea_leaf | water_bucket | — | cup_glass | 3 |
| Milk Tea | black_tea_leaf | milk_bucket | sugar | cup_glass | 3 |
| Mandarin Drink | coffee_powder | milk_bucket | tea_leaf | cup_glass | 3 |
| Coldbrew | coldbrew_bottle | — | — | cup_glass | 3 |
| Iced Americano | coffee_powder | water_bucket | ice_slag | cup_glass | 4 |
| Iced Latte | coffee_powder | milk_bucket | ice_slag | cup_glass | 4 |
| Iced Coldbrew | coldbrew_bottle | — | ice_slag | cup_glass | 3 |

### Recipe ambiguity resolution

- Cappuccino uses `sugar` as additive to distinguish from Latte
  (which has no additive).  Without this, both would match the
  same input combination (coffee_powder + milk_bucket + cup).
- Macchiato uses 2×coffee_powder to distinguish from Latte (1×).
- All other drink recipes have unique input signatures.

### Tea leaf items

- `tea_leaf`: crafted from 4×oak_leaves (2×2 shaped).  Represents
  generic green tea material.
- `black_tea_leaf`: obtained by smelting `tea_leaf`.
- No world-gen tea bush; leaves crafting is accessible early-game.

### Dough intermediate closures

- `plate_dough` → Oven → bread_round (200 ticks).  Closes the
  dough→roller→plate_dough→oven chain.
- `plate_dough_pastry` → used in pie_cream workbench recipe (existing).
- `plate_dough_ginger` → 2×2 workbench → ginger_house block.
  More efficient than the 3×3 dough_ginger recipe.

### P1 deferred items

Flavored latte variants (caramel, chocolate, fruit, mint, vanilla, sakura),
their iced versions, iced tea/cocoa variants, coldbrew extensions, and
flavored syrup system remain creative-only pending Phase 4 P1.

### Phase 4 P1 additions

See above for Phase 4 P1 items that were implemented alongside P0.
All 6 syrup items, flavored lattes, iced extensions, coldbrew
extensions, and Americano extensions have survival recipes.

### Chocolate chip production gap (Phase 4 review)

- `chocolate_chip` was registered as an item and used in `cocoa_strong`,
  `dough_cookie`, and `brownie` recipes, but had no production recipe.
  **Fix**: added Grinder recipe `chocolate_bar` → `chocolate_chip` × 2.
- This matches the old 1.12.2 behavior where chocolate chips were
  ground from chocolate bars.

### Known slot limitations for P1 drinks

The 5-slot Coffee Machine (base/modifier/additive/container/output) cannot
express recipes that need both `ice_slag` AND a flavored syrup simultaneously
(only one additive slot).  The following registered items therefore have no
survival recipe and remain creative-only:

- `cocoa_strong_ice` — needs `chocolate_chip` + `ice_slag`
- `coffee_coldbrew_fruit_ice` — ambiguous signature with `coldbrew_fruit`
- `coffee_coldbrew_latte_*_ice` (6 items) — need milk + syrup + ice_slag
- `coffee_americano_nitro_fruit_ice` — soda + syrup can't coexist

These will be resolved in a future slot-extension phase.

### Shift-click routing limitation

- Coffee Machine shift-click sends all recipe-matching items to slot 0
  (base) rather than routing to the appropriate slot (buckets → modifier,
  cups → container, syrups → additive, sugar → additive, etc.).
- The machine remains functional because the recipe system checks all
  slots; items in wrong slots simply don't match a recipe.
- Enhanced shift-click routing is deferred to a future UX improvement.
