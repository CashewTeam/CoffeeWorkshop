# Coffee Workshop — Porting Decisions (1.20.1)

This file records decisions taken during the 1.12.2 → 1.20.1 port. It is the
canonical place to record changes to registry IDs, data formats, or play
semantics. Add a new dated entry whenever such a decision is made so future
maintainers can trace why a name or behaviour is the way it is.

For the high-level audit and phased plan see
`CoffeeWorkshop_1.20.1_移植审计与开发计划.md`.

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
