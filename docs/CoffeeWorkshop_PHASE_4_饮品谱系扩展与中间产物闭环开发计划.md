# Coffee Workshop 1.20.1
# Phase 4：饮品谱系扩展与中间产物闭环开发计划

> 目标分支：`1.20.1`  
> 前置阶段：`Phase 3: verified core complete`  
> 优先级：P0 / P1  
> 工作量：XL  
> 阶段定位：在 Phase 3 第一杯咖啡闭环基础上，恢复旧版主要饮品谱系，并关闭已知无用途中间产物。

---

## 1. 阶段背景

Phase 3 已完成并自动验证以下核心链路：

```text
Coffee Seeds
→ Raw Coffee Bean
→ Oven
→ Roasted Coffee Bean
→ Grinder
→ Coffee Powder
→ Coffee Machine
→ Americano / Espresso / Latte
→ cups 消耗
→ 空杯返还
```

当前机器 Recipe、DataGen、JEI、GameTest 和可达性报告框架已经成立。

Phase 4 不再重做机器核心，而是在现有基础上解决以下问题：

1. Phase 3 延期的热饮、茶饮、可可和 Coldbrew 仍只能通过创造模式获得；
2. 当前 Coffee Machine 只有“基础原料 + 一个 Modifier + 容器”三个输入角色，不能表示咖啡粉、牛奶、糖浆和杯子同时参与的配方；
3. `ice_slag` 和 `cup_glass` 尚未进入正式冰饮生产链；
4. `plate_dough`、`plate_dough_ginger` 缺少正式下游用途；
5. 可达性报告能够验证核心咖啡链，但还不能严格验证世界来源、Tag、机器前置和终端用途；
6. 大量已经注册的饮品 Item 尚未有正式生存配方。

---

## 2. 阶段总目标

Phase 4 的核心目标是建立可扩展的完整饮品配方体系：

```text
基础原料
+ 液体
+ 附加原料
+ 饮品容器
→ Coffee Machine
→ 热饮 / 茶饮 / 可可 / 冰饮 / 风味饮品
```

同时建立以下内容闭环：

```text
Cold Brew Pot
→ Coldbrew Bottle
→ Coffee Machine
→ Coldbrew Drink

Ice
→ Grinder
→ Ice Slag
→ Iced Drinks

Glass Pane
→ Glass Cup
→ Tea / Coldbrew / Iced Drinks

Dough
→ Roller
→ Plate Dough
→ Pie / Pastry

Ginger Dough
→ Roller
→ Plate Ginger Dough
→ Gingerbread / Ginger House
```

---

## 3. Phase 4 完成定义

Phase 4 只有在以下 P0 条件全部完成后才能封板。

### 3.1 Coffee Machine 架构

- [ ] Coffee Machine 支持四个输入角色和一个输出槽；
- [ ] Recipe 可以分别声明基础原料、液体、附加原料和容器；
- [ ] 缺失的可选原料意味着对应槽必须为空；
- [ ] 多输入消费使用真正的原子化消费计划；
- [ ] 所有 remainder 在消费前完成放置验证；
- [ ] 输出阻塞时任何输入都不会被消费；
- [ ] GUI、shift-click 和 Sided Capability 与新槽位一致；
- [ ] Phase 3 的 Americano、Espresso、Latte 行为保持不变。

### 3.2 延期饮品恢复

至少恢复以下正式配方：

- [ ] Cappuccino；
- [ ] Macchiato；
- [ ] Mochaccino；
- [ ] Cocoa；
- [ ] Cocoa Strong；
- [ ] Milk Tea；
- [ ] Green Tea；
- [ ] Black Tea；
- [ ] Mandarin Drink；
- [ ] Coldbrew。

### 3.3 冰饮与容器用途

- [ ] `cup_glass` 被正式茶饮或冰饮消耗；
- [ ] `ice_slag` 被至少三条冰饮配方消耗；
- [ ] Iced Americano；
- [ ] Iced Latte；
- [ ] Iced Coldbrew；
- [ ] 冰饮最后一次饮用后返还 `cup_glass`。

### 3.4 中间产物闭环

- [ ] `plate_dough` 有正式下游；
- [ ] `plate_dough_ginger` 有正式下游；
- [ ] `ice_slag` 有正式下游；
- [ ] `cup_glass` 有正式下游；
- [ ] 新增的茶叶、糖浆或饮品基底均有获得方式和用途；
- [ ] 不增加新的无用途中间物。

### 3.5 数据、JEI 与测试

- [ ] 所有新配方由 DataGen 生成；
- [ ] JEI 显示四输入角色、数量、时间、经验和初始化后的 cups；
- [ ] RecipeManager 与 JEI 数量一致；
- [ ] 所有 P0 饮品均有真实 GameTest；
- [ ] Reachability v2 对 P0 链路无错误；
- [ ] `runData` 幂等；
- [ ] CI 全绿。

---

## 4. 范围边界

## 4.1 Phase 4 P0

Phase 4 P0 包含：

- Coffee Machine 五槽升级；
- Coffee Brewing Recipe Schema v2；
- 原子化 Consumption Plan；
- Phase 3 延期的十种基础饮品；
- Cold Brew Pot 基础闭环；
- 三种基础冰饮；
- `cup_glass` 和 `ice_slag` 用途；
- `plate_dough` 和 `plate_dough_ginger` 至少各一条用途；
- JEI 五槽展示；
- Reachability v2；
- 自动化测试与生存验收。

## 4.2 Phase 4 P1

Phase 4 P1 包含：

- 焦糖、巧克力、果味、薄荷、香草、樱花 Latte；
- 上述风味 Latte 的冰饮版本；
- Iced Cappuccino、Macchiato、Mochaccino；
- Iced Green Tea、Black Tea、Milk Tea；
- Iced Cocoa、Cocoa Strong；
- Fruit Americano；
- Nitro Americano；
- Coldbrew Latte 和风味 Coldbrew；
- 更完整的数值平衡；
- JEI 配方分组与搜索别名。

## 4.3 延期内容

以下内容不作为 Phase 4 P0：

- 动态水箱和牛奶储罐；
- Fluid Capability；
- 自动补水管道；
- 完整咖啡师村民交易；
- 所有蛋糕和复杂甜点恢复；
- 动态饮品颜色或统一 NBT 饮品 Item；
- 旧开发存档的 Coffee Machine 槽位迁移。

---

## 5. 开发前旧版审计

建立：

```text
docs/PHASE_4_PORTING_MATRIX.md
```

对 `master` 分支逐项记录：

| 内容 | 旧版输入 | 输出 | 数量 | 时间 | 容器 | cups | 效果 | 备注 |
|---|---|---|---:|---:|---|---:|---|---|
| Cappuccino | | | | | | | | |
| Macchiato | | | | | | | | |
| Mochaccino | | | | | | | | |
| Cocoa | | | | | | | | |
| Cocoa Strong | | | | | | | | |
| Green Tea | | | | | | | | |
| Black Tea | | | | | | | | |
| Milk Tea | | | | | | | | |
| Mandarin Drink | | | | | | | | |
| Coldbrew | | | | | | | | |
| Iced Americano | | | | | | | | |
| Iced Latte | | | | | | | | |
| Iced Coldbrew | | | | | | | | |

必须额外审计：

- 旧版茶叶或茶粉对应的 Registry ID；
- 旧版糖浆 Item 和容器返还；
- Cocoa 和 Mochaccino 的原料差异；
- Mandarin Drink 的真实配方语义；
- Coldbrew Pot 发酵时间和产量；
- 冰饮是否直接使用冰、碎冰或旧 metadata；
- `plate_dough` 和 `plate_dough_ginger` 的旧用途。

旧版行为无法确认时，应在 `PORTING_DECISIONS.md` 记录明确决策，不得静默使用染料代替食品原料。

---

## 6. P0 架构决策：Coffee Brewing Recipe Schema v2

## 6.1 推荐槽位

Coffee Machine 升级为五槽：

| 槽位 | 角色 | 示例 |
|---:|---|---|
| 0 | Base | coffee_powder、tea、cocoa_powder、coldbrew_bottle |
| 1 | Modifier | water_bucket、milk_bucket |
| 2 | Additive | cocoa_powder、syrup、ice_slag |
| 3 | Container | cup、cup_glass |
| 4 | Output | DrinkCoffee |

保留现有 JSON 字段 `modifier`，避免无意义的数据格式破坏；新增 `additive`：

```json
{
  "type": "coffeework:coffee_brewing",
  "base": {
    "ingredient": {
      "item": "coffeework:coffee_powder"
    },
    "count": 1
  },
  "modifier": {
    "ingredient": {
      "item": "minecraft:milk_bucket"
    },
    "count": 1
  },
  "additive": {
    "ingredient": {
      "item": "coffeework:cocoa_powder"
    },
    "count": 1
  },
  "container": {
    "ingredient": {
      "item": "coffeework:cup"
    },
    "count": 1
  },
  "result": {
    "item": "coffeework:coffee_mochaccino"
  },
  "experience": 0.3,
  "cookingtime": 140
}
```

## 6.2 可选槽位规则

- 未声明 `modifier`：槽 1 必须为空；
- 未声明 `additive`：槽 2 必须为空；
- 声明了 Ingredient：对应槽必须匹配且数量足够；
- 输出槽不得参与 Recipe 匹配；
- 未声明的额外物品必须阻止配方匹配；
- Recipe ID 变化时继续使用 Phase 2 的进度重置规则。

## 6.3 ProcessingRecipe 扩展

现有 `ProcessingRecipe` 已能返回：

```java
int[] getConsumedSlots();
int getRequiredCount(int slot);
```

Phase 4 增加统一 remainder 接口：

```java
ItemStack getRemainder(int slot, ItemStack consumedStack);
```

不要在公共加工引擎中继续使用：

```java
recipe instanceof CoffeeBrewingRecipe
```

公共加工引擎只依赖接口。

---

## 7. 原子化 Consumption Plan

多输入配方扩展前必须替换当前“边遍历边 shrink”的处理方式。

新增：

```text
ConsumptionPlan
ConsumptionEntry
RemainderDestination
```

建议结构：

```java
record ConsumptionEntry(
        int slot,
        int count,
        ItemStack before,
        ItemStack after,
        ItemStack remainder
) {}
```

处理流程：

1. 重新解析当前 Recipe；
2. 构造最终 Result Stack；
3. 检查输出完整容量；
4. 检查每个输入槽数量；
5. 计算每个槽消费后的 Stack；
6. 计算所有 crafting remainder；
7. 确定 remainder 的目标槽；
8. 任何一步失败则返回不可处理；
9. 全部检查通过后一次性应用；
10. 写入结果；
11. 记录 Recipe 使用和经验；
12. 同步 BlockEntity。

原则：

- 不允许在正常机器配方中将 Bucket 或 Syrup Container 直接掉到世界；
- remainder 无法放回时，机器必须暂停；
- 输出被堵塞时不得消耗任何输入；
- 多数量输入必须正确处理 count；
- 消费期间不能产生复制或吞物。

---

## 8. Coffee Machine 菜单、GUI 与自动化

## 8.1 Menu

新增槽位：

```text
Base
Modifier
Additive
Container
Output
```

每个槽必须有专门的 `SlotIngredient` 校验。

shift-click 路由：

- Coffee Powder、Tea、Cocoa、Coldbrew Bottle → Base；
- Water Bucket、Milk Bucket → Modifier；
- Syrup、Cocoa Powder、Ice Slag → Additive；
- Cup、Glass Cup → Container；
- 其他物品仅在玩家背包和快捷栏移动。

不能只依赖“任意 Recipe 是否匹配这个单独 Stack”，否则同一 Item 可能被错误送入多个槽。

## 8.2 Capability

建议规则：

| 方向 | 暴露槽位 |
|---|---|
| UP | Base |
| 水平面 | Modifier、Additive、Container |
| DOWN | Output |
| null | 完整 Handler |

水平 Handler 必须依赖各槽 `isItemValid()`，禁止自动化插入输出。

## 8.3 Screen

调整 GUI 纹理和箭头布局，至少显示：

```text
Base + Modifier + Additive + Container → Output
```

可选槽为空时，不应出现幽灵物品或错误提示。

---

## 9. 第一批正式饮品配方

实际原料和数量以旧版审计为准。以下是 Phase 4 的目标语义，不是最终平衡数值。

## 9.1 咖啡热饮

### Cappuccino

```text
coffee_powder
+ milk_bucket
+ cup
→ coffee_cappuccino
```

### Macchiato

```text
coffee_powder × 2
+ milk_bucket
+ cup
→ coffee_macchiato
```

### Mochaccino

```text
coffee_powder
+ milk_bucket
+ cocoa_powder
+ cup
→ coffee_mochaccino
```

Mochaccino 是验证 Additive 槽的第一条 P0 配方。

## 9.2 Cocoa

### Cocoa

```text
cocoa_powder
+ milk_bucket
+ cup
→ cocoa
```

### Cocoa Strong

```text
cocoa_powder × 2
+ milk_bucket
+ sugar 或 chocolate
+ cup
→ cocoa_strong
```

## 9.3 Tea

必须根据旧版审计恢复或新增明确的茶原料，不允许使用颜色染料代替。

### Green Tea

```text
green_tea_material
+ water_bucket
+ cup_glass
→ coffee_green_tea
```

### Black Tea

```text
black_tea_material
+ water_bucket
+ cup_glass
→ coffee_black_tea
```

### Milk Tea

```text
tea_material
+ milk_bucket
+ sugar
+ cup_glass
→ coffee_milk_tea
```

### Mandarin Drink

按旧版语义确定。建议仅在旧版无法恢复时采用：

```text
coffee_or_tea_base
+ milk_bucket
+ tea_material
+ cup_glass
→ coffee_mandarin_drink
```

---

## 10. Cold Brew Pot 闭环

当前 Cold Brew Pot 已有：

- `FERM` 状态；
- 随机发酵；
- Glass Bottle 提取；
- `coldbrew_bottle` 输出；
- Empty Pot 和 Full Pot 掉落逻辑。

Phase 4 需要将其正式纳入生产链。

## 10.1 制作链

```text
plate_iron
→ empty_coldbrew_pot

empty_coldbrew_pot
+ coffee_powder × 4
+ water_bucket
→ coldbrew_pot

coldbrew_pot
→ 发酵
→ coldbrew_bottle

coldbrew_bottle
+ cup_glass
→ Coffee Machine
→ coffee_coldbrew
```

Coffee Machine 配方建议：

```text
Base: coldbrew_bottle
Modifier: empty
Additive: empty
Container: cup_glass
Output: coffee_coldbrew
Remainder: glass_bottle
```

## 10.2 Cold Brew GameTest

至少覆盖：

```text
coldbrewPot_advancesFermentation
coldbrewPot_onlyExtractsWhenFinished
coldbrewPot_returnsBottleProduct
coldbrewBottle_returnsGlassBottle
coldbrewPot_breakDropsCorrectVariant
coldbrewDrink_initializesCups
```

随机发酵测试应提供测试专用加速 Hook，避免依赖随机概率。

---

## 11. 冰饮与 Phase 3 中间物闭环

## 11.1 Ice Slag

正式用途：

```text
ice
→ Grinder
→ ice_slag
```

P0 冰饮：

```text
Americano ingredients + ice_slag + cup_glass
→ coffee_americano_ice

Latte ingredients + ice_slag + cup_glass
→ coffee_latte_ice

coldbrew_bottle + ice_slag + cup_glass
→ coffee_coldbrew_ice
```

这样 `ice_slag` 和 `cup_glass` 同时进入正式下游。

## 11.2 Glass Cup

`cup_glass` 至少用于：

- Green Tea；
- Black Tea；
- Milk Tea；
- Mandarin Drink；
- Coldbrew；
- 三种基础冰饮。

最后一杯饮用后必须返还 `cup_glass`，不能返还纸杯。

---

## 12. 风味糖浆系统

风味 Latte 属于 P1，但架构应在 P0 阶段定型。

当前已有：

```text
syrup_empty
```

建议引入数据驱动或独立 Item 的风味糖浆：

```text
syrup_caramel
syrup_chocolate
syrup_fruit
syrup_mint
syrup_vanilla
syrup_sakura
```

选择规则：

- 如果旧版存在稳定 Registry ID，优先沿用；
- 如果旧版依赖 metadata，1.20.1 推荐拆为独立 Item；
- 不建议使用一个 NBT Flavor Item，除非已有完整资源和 DataGen 方案；
- 使用后返还 `syrup_empty`；
- Syrup 必须有生存制作方式；
- JEI 必须显示容器返还。

P1 配方：

```text
coffee_powder
+ milk_bucket
+ flavored_syrup
+ cup
→ flavored_latte
```

冰版本将容器切换为 `cup_glass`，并需要 `ice_slag`。由于只有一个 Additive 槽，风味冰 Latte 可能需要以下方案之一：

1. 新增 `iced_syrup` 或预混 Syrup；
2. 增加第二 Additive 槽；
3. 先制作热风味 Latte，再通过独立冷却 Recipe 转换；
4. 延期到 Phase 5。

Phase 4 P0 不应为了全部风味冰饮继续扩大 Coffee Machine 槽位。

---

## 13. Plate Dough 下游

## 13.1 Plate Dough

优先复用已有 Pie 或 Pastry 内容。

目标链：

```text
dough
→ Roller
→ plate_dough
→ pie / pastry base
→ finished food
```

至少完成一条：

```text
plate_dough
+ cream/filling
+ mold
→ pie_cream 或其他现有 Pie
```

若旧版要求 Oven，则使用：

```text
plate_dough_filled
→ Oven
→ finished pie
```

## 13.2 Plate Dough Ginger

优先目标：

```text
dough_ginger
→ Roller
→ plate_dough_ginger
→ Oven
→ gingerbread
```

如果当前没有 Gingerbread Item，可选：

- 恢复旧版 Gingerbread Registry ID；
- 将 Ginger House 配方改为明确消耗 `plate_dough_ginger`；
- 新增一个最小可食用 Gingerbread Item。

不得只为清除报告警告而添加无意义的循环配方。

---

## 14. DataGen

扩展：

```text
ModMachineRecipeProvider
CoffeeBrewingRecipeBuilder
ModRecipeProvider
```

建议拆分：

```java
buildBasicCoffeeRecipes(writer);
buildTeaRecipes(writer);
buildCocoaRecipes(writer);
buildColdBrewRecipes(writer);
buildIcedDrinkRecipes(writer);
buildFlavoredDrinkRecipes(writer);
buildPastryClosureRecipes(writer);
```

Builder 增加：

```java
.additive(Ingredient ingredient, int count)
.requireEmptyModifier()
.requireEmptyAdditive()
```

校验：

- Base、Container 必填；
- Modifier 和 Additive 可选；
- 可选字段省略时，对应槽必须为空；
- Result 必须是合法 ItemStack；
- DrinkCoffee 输出必须能初始化 cups；
- 同一输入组合不得生成多个结果；
- Recipe ID 不得重复；
- 所有 count 必须大于零；
- 不允许输出超过最大堆叠。

---

## 15. JEI

Coffee Brewing Category 调整为五槽：

```text
Base
Modifier
Additive
Container
Output
```

要求：

- 显示 Ingredient Count；
- 可选槽为空时显示明确的空槽；
- 输出使用 `getResultItem()`，保证 cups NBT；
- Tooltip 显示剩余杯数和最大杯数；
- 显示 Experience 和 Cooking Time；
- Syrup、Bucket、Bottle remainder 可通过 Tooltip 或额外图标提示；
- RecipeManager 和 JEI 使用同一 Recipe 列表。

Cold Brew Pot 建议新增 JEI Category：

```text
Cold Brew Fermentation
```

显示：

```text
Filled Pot
→ fermentation time/stages
→ Coldbrew Bottle
```

若本阶段不新增 Category，至少通过信息页说明完整流程。

---

## 16. Reachability v2

升级：

```text
tools/report_recipe_reachability.py
```

## 16.1 世界来源清单

不要只在 Python Set 中人工声明物品可达。

新增：

```text
data/coffeework/reachability/world_sources.json
```

示例：

```json
{
  "coffeework:coffee_seeds": {
    "type": "placed_feature",
    "id": "coffeework:coffee_tree"
  },
  "coffeework:vanilla": {
    "type": "placed_feature",
    "id": "coffeework:vanilla_crop"
  }
}
```

脚本必须验证引用的：

- Configured Feature；
- Placed Feature；
- Biome Modifier；
- Loot Table；
- Trade；
- Drop Modifier；

至少有一个真实资源存在。

## 16.2 Tag 解析

脚本必须读取：

```text
data/*/tags/items/*.json
```

将 Tag Ingredient 展开为具体物品集合。

## 16.3 机器前置

机器 Recipe 的隐式输入包含机器本身：

```text
grinding → grinder
oven_baking → oven
rolling → roller
icecream_making → icecream_machine
coffee_brewing → coffee_machine
```

只有机器可制作时，其输出才能被标记为可达。

## 16.4 终端用途分类

新增：

```text
CONSUMABLE
PLACEABLE
CRAFTING_INPUT
MACHINE_INPUT
CONTAINER
DEFERRED
UNUSED_INTERMEDIATE
```

`DrinkCoffee`、Food Item 和 BlockItem 可以作为合法终端结果。

普通无功能 Item 若没有下游，应标记：

```text
UNUSED_INTERMEDIATE
```

## 16.5 CI 失败条件

- P0 饮品不可达；
- 新增普通中间物无用途；
- 世界来源引用不存在；
- 同输入组合出现模糊配方；
- DataGen 产生重复 Recipe ID；
- 已移除的 direct fallback 重新出现；
- P0 机器配方数量低于目标；
- JSON 解析失败。

---

## 17. GameTest 计划

## 17.1 Recipe Schema

```text
coffeeRecipe_matchesFourInputs
missingModifier_requiresEmptySlot
missingAdditive_requiresEmptySlot
wrongAdditive_doesNotMatch
recipeSerializer_roundTripsAdditive
ambiguousRecipe_isRejected
```

## 17.2 原子消费

```text
blockedOutput_consumesNothing
missingRemainderSpace_consumesNothing
bucketReturnsToModifierSlot
syrupBottleReturnsToAdditiveSlot
multipleRequiredItems_consumeExactCount
```

## 17.3 热饮

```text
cappuccino_brews
macchiato_brews
mochaccino_brewsWithCocoa
cocoa_brews
cocoaStrong_brews
greenTea_usesGlassCup
blackTea_usesGlassCup
milkTea_brews
mandarinDrink_brews
coldbrew_brewsFromBottle
```

## 17.4 冰饮

```text
icedAmericano_consumesIceSlag
icedLatte_consumesIceSlag
icedColdbrew_consumesIceSlag
icedDrink_returnsGlassCup
```

## 17.5 cups

每种 P0 饮品至少验证：

- Result Item；
- `remaining_cups`；
- `max_cups`；
- 正确空杯类型；
- 最后一次饮用返还容器；
- 单杯配置关闭时不会无限饮用。

## 17.6 中间产物

```text
plateDough_hasReachableOutput
plateGingerDough_hasReachableOutput
iceSlag_hasIcedDrinkUse
glassCup_hasDrinkUse
```

---

## 18. PR 拆分

## PR 4.0：旧版审计与 Schema 决策

内容：

- `PHASE_4_PORTING_MATRIX.md`；
- 茶、可可、糖浆、冷萃和冰饮旧版审计；
- 确认五槽布局；
- 确认 P0/P1 饮品清单；
- 更新 `PORTING_DECISIONS.md`。

验收：

- 不新增正式配方；
- 所有 P0 饮品都有明确原料来源；
- 不使用临时染料替代品。

## PR 4.1：Coffee Brewing Schema v2

内容：

- Additive Slot；
- Serializer；
- Builder；
- Menu；
- Screen；
- Capability；
- JEI；
- Recipe 兼容；
- Phase 3 三条配方迁移；
- Schema GameTest。

验收：

- Americano、Espresso、Latte 回归通过；
- 五槽配方无歧义；
- DataGen 幂等。

## PR 4.2：原子化 Consumption Plan

内容：

- ConsumptionPlan；
- 通用 remainder 接口；
- Bucket、Bottle、Syrup Container；
- 阻塞处理；
- 精确数量消费；
- 复制/吞物回归测试。

验收：

- 无世界掉落式正常 remainder；
- 任一目标槽不可用时完全不消费。

## PR 4.3：基础咖啡与可可饮品

内容：

- Cappuccino；
- Macchiato；
- Mochaccino；
- Cocoa；
- Cocoa Strong；
- DataGen；
- JEI；
- GameTest。

验收：

- 五种饮品生存可达；
- cups 和纸杯返还正确。

## PR 4.4：茶饮与 Glass Cup

内容：

- Green Tea；
- Black Tea；
- Milk Tea；
- Mandarin Drink；
- 茶原料获取；
- Glass Cup；
- GameTest。

验收：

- `cup_glass` 不再是无用中间物；
- 四种饮品均可达。

## PR 4.5：Cold Brew Pot

内容：

- 发酵行为审计；
- Coldbrew Bottle；
- Coffee Machine Coldbrew；
- Bottle remainder；
- Cold Brew JEI 信息；
- GameTest。

验收：

- 从 Empty Pot 到 Coldbrew Drink 的完整闭环可达。

## PR 4.6：基础冰饮

内容：

- Iced Americano；
- Iced Latte；
- Iced Coldbrew；
- Ice Slag；
- Glass Cup；
- GameTest。

验收：

- `ice_slag` 有正式用途；
- 三种冰饮可达；
- Glass Cup 正确返还。

## PR 4.7：Dough 中间物闭环

内容：

- Plate Dough 下游；
- Plate Ginger Dough 下游；
- Oven 或工作台配方；
- 资源和语言；
- GameTest。

验收：

- 两个中间物不再被 Reachability v2 标记为 unused。

## PR 4.8：风味糖浆与 P1 饮品

内容：

- Syrup Items；
- Syrup Container Return；
- Hot Flavored Latte；
- 可选冰饮方案；
- JEI 与测试。

验收：

- 所有新增 Syrup 都有制作方式和用途；
- 不增加新的创造模式专属核心材料。

## PR 4.9：Reachability v2 与最终封板

内容：

- 世界来源验证；
- Tag 展开；
- 机器前置；
- 终端用途分类；
- CI 门禁；
- 生存模式验收；
- 文档更新。

验收：

- P0 无不可达；
- P0 无无用中间物；
- CI 全绿；
- 新世界可制作至少一种茶、一种可可、一种冷萃和一种冰饮。

---

## 19. CI

最终 CI 顺序：

```bash
python tools/audit_resources.py
./gradlew runData build --no-daemon --stacktrace
git status --porcelain --untracked-files=all -- src/generated/resources
python tools/audit_resources.py
python tools/report_recipe_reachability.py
./gradlew runGameTestServer --no-daemon --stacktrace
```

上传：

```text
build/reports/coffeework/**
build/test-results/**
build/libs/*.jar
```

Phase 4 最终封板工作流必须强制运行 `runData`，不能只依赖变更检测。

---

## 20. 手工验收流程

创建全新生存世界，不使用：

```text
Creative
/give
/recipe give
手工数据包注入
```

完成：

1. 制作 Coffee Machine；
2. 制作 Cappuccino；
3. 制作 Mochaccino；
4. 制作 Cocoa；
5. 获得茶原料；
6. 制作 Green Tea；
7. 制作 Milk Tea；
8. 制作并发酵 Cold Brew Pot；
9. 提取 Coldbrew Bottle；
10. 制作 Coldbrew；
11. 研磨 Ice Slag；
12. 制作 Iced Americano；
13. 确认纸杯与玻璃杯返还；
14. 验证 cups；
15. 验证 JEI 全链路；
16. 验证 Plate Dough 下游；
17. 重启世界确认机器和饮品状态。

记录到：

```text
docs/PHASE_4_SURVIVAL_ACCEPTANCE.md
```

---

## 21. 最终验收表

### P0

- [ ] Coffee Machine 五槽架构；
- [ ] Additive Recipe Schema；
- [ ] 原子化消费；
- [ ] Cappuccino；
- [ ] Macchiato；
- [ ] Mochaccino；
- [ ] Cocoa；
- [ ] Cocoa Strong；
- [ ] Green Tea；
- [ ] Black Tea；
- [ ] Milk Tea；
- [ ] Mandarin Drink；
- [ ] Coldbrew；
- [ ] Iced Americano；
- [ ] Iced Latte；
- [ ] Iced Coldbrew；
- [ ] Glass Cup 用途；
- [ ] Ice Slag 用途；
- [ ] Plate Dough 用途；
- [ ] Plate Ginger Dough 用途；
- [ ] JEI；
- [ ] Reachability v2；
- [ ] GameTest；
- [ ] DataGen 幂等；
- [ ] 新世界生存验收；
- [ ] CI 全绿。

### P1

- [ ] 六种风味 Syrup；
- [ ] 六种 Hot Flavored Latte；
- [ ] 风味冰 Latte；
- [ ] Iced Cappuccino；
- [ ] Iced Macchiato；
- [ ] Iced Mochaccino；
- [ ] Iced Tea；
- [ ] Iced Cocoa；
- [ ] Fruit Americano；
- [ ] Nitro Americano；
- [ ] Coldbrew Latte 系列；
- [ ] 数值和平衡调整。

---

## 22. Phase 4 最终原则

Phase 4 的完成标准不是“让更多饮品出现在 JEI 中”，而是：

> 所有 P0 饮品都拥有明确、无歧义、可在新生存世界中到达的生产链；Coffee Machine 能安全处理基础原料、液体、附加原料和容器；Bucket、Bottle、Syrup Container 与 Cup 均正确返还；Phase 3 遗留中间物进入真实下游；JEI、RecipeManager、Reachability 和 GameTest 对同一玩法给出一致结论。
