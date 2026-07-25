# Coffee Workshop 1.20.1

# Phase 3：机器配方与咖啡闭环执行计划

> 目标分支：`1.20.1`
> 前置阶段：Phase 2 核心加工架构已通过 CI 和 GameTest
> 优先级：P0 / P1
> 工作量：L
> 阶段定位：将现有机器框架转化为可在生存模式中完整使用的生产系统

---

## 1. 阶段目标

Phase 3 的核心目标是建立第一条真正完整、数据驱动、可在生存模式中到达的咖啡生产链：

```text
咖啡种子或咖啡树
→ 生咖啡豆
→ Oven 烘焙
→ 熟咖啡豆
→ Grinder 研磨
→ 咖啡粉
→ Coffee Machine 冲泡
→ Americano / Espresso / Latte
→ 多杯饮用
→ 空杯返还
```

同时为五类机器建立首批正式配方：

```text
Grinder
Oven
Roller
Icecream Machine
Coffee Machine
```

本阶段结束后，机器不再只是“能够运行的框架”，而是形成至少一条完整、可验证的玩法闭环。

---

## 2. Phase 3 完成定义

只有以下条件全部满足，Phase 3 才能标记完成：

### 数据生成

* [ ] 新增 `MachineRecipeBuilder`；
* [ ] 新增 `ModMachineRecipeProvider`；
* [ ] 五种 RecipeType 的配方均由 DataGen 生成；
* [ ] 不再手工维护正式机器配方 JSON；
* [ ] Provider 和生成结果在同一提交；
* [ ] 连续执行两次 `runData` 后工作区无变化。

### 咖啡闭环

* [ ] 生咖啡豆在生存模式可获得；
* [ ] 生豆只能通过 Oven 核心路径烘焙；
* [ ] 熟豆只能通过 Grinder 核心路径研磨；
* [ ] Coffee Machine 可以生产 Americano；
* [ ] Coffee Machine 可以生产 Espresso；
* [ ] Coffee Machine 可以生产 Latte；
* [ ] 饮品结果初始化正确杯数；
* [ ] 最后一杯饮用后正确返还空杯；
* [ ] 玩家不使用创造模式即可完成第一杯咖啡。

### 五台机器

* [ ] Grinder 至少有 3 条正式配方；
* [ ] Oven 至少有 2 条正式配方；
* [ ] Roller 至少有 2 条正式配方；
* [ ] Icecream Machine 至少有 1 条正式配方；
* [ ] Coffee Machine 至少有 3 条正式配方；
* [ ] 五台机器均有真实 GameTest。

### JEI

* [ ] 五类机器配方全部显示；
* [ ] 输入槽、修饰槽、容器槽和输出槽显示正确；
* [ ] 饮品输出显示初始化后的杯数；
* [ ] 每台机器有 Catalyst；
* [ ] JEI 数量与 RecipeManager 数量一致；
* [ ] JEI 不再显示已删除的 direct crafting fallback。

### 可达性

* [ ] 生成配方可达性报告；
* [ ] 咖啡闭环不存在循环依赖；
* [ ] 所有新增中间产物都有下游用途；
* [ ] 所有最终产物是可食用、可饮用、可放置或被其他配方消耗；
* [ ] 报告中没有 P0 不可达项。

---

## 3. 范围边界

### Phase 3 必须完成

* Machine Recipe DataGen；
* 首批五类机器配方；
* 生豆到饮品完整链路；
* Coffee Machine 多输入配方；
* 饮品杯数初始化；
* JEI 配方展示；
* 核心 direct fallback 移除；
* 配方可达性报告；
* 五台机器的首条真实加工测试。

### Phase 3 暂不完成

以下内容延期到后续阶段：

* 全部咖啡、茶、可可饮品；
* 冰饮系列；
* 糖浆和风味 Latte；
* 全部面包、蛋糕和甜点；
* 动态流体储罐；
* Coffee Machine 水箱或牛奶箱；
* 完整村民交易；
* 生产数值最终平衡；
* 全部旧版机器配方恢复。

Phase 3 只建立第一条纵向闭环以及后续扩展所需的数据生成能力。

---

## 4. 开始开发前的旧版行为审计

正式实现前，必须从 `master` 分支记录首批内容的旧版行为。

建立：

```text
docs/PHASE_3_PORTING_MATRIX.md
```

至少记录：

| 内容        | 旧版输入 | 旧版输出 | 数量 | 时间 | 燃料 | 容器返还 | 备注 |
| --------- | ---- | ---- | -: | -: | -- | ---- | -- |
| 生豆烘焙      |      |      |    |    |    |      |    |
| 咖啡研磨      |      |      |    |    |    |      |    |
| Americano |      |      |    |    |    |      |    |
| Espresso  |      |      |    |    |    |      |    |
| Latte     |      |      |    |    |    |      |    |
| 香草冰淇淋     |      |      |    |    |    |      |    |
| 基础面团压片    |      |      |    |    |    |      |    |
| 基础面包烘焙    |      |      |    |    |    |      |    |

如果旧版实现不明确：

1. 检查旧 TileEntity；
2. 检查旧 GUI 槽位；
3. 检查旧 JEI 集成；
4. 检查旧配方注册；
5. 检查旧 metadata 对应关系；
6. 将推断写入 `PORTING_DECISIONS.md`。

未经审计，不得只按照现实咖啡配方自行设计游戏规则。

---

## 5. P0 架构决策：Coffee Machine 多输入配方

## 5.1 当前问题

普通 `MachineRecipe` 是单输入单输出。

如果直接生成：

```text
coffee_powder → Americano
coffee_powder → Espresso
coffee_powder → Latte
```

RecipeManager 无法确定玩家想要哪一种结果，实际结果可能取决于数据包加载顺序。

因此禁止添加三条具有相同输入和 RecipeType 的配方。

## 5.2 推荐方案

保持四类普通机器继续使用 `MachineRecipe`：

```text
Grinder
Oven
Roller
Icecream Machine
```

Coffee Machine 使用专门的多输入配方：

```text
CoffeeBrewingRecipe
CoffeeBrewingRecipeSerializer
CoffeeBrewingRecipeBuilder
```

两种 Recipe 应共同实现一个轻量接口：

```java
public interface ProcessingRecipe {
    ResourceLocation getId();

    ItemStack assemble(
            SimpleContainer container,
            RegistryAccess registryAccess
    );

    boolean matches(
            SimpleContainer container,
            Level level
    );

    float experience();

    int cookingTime();

    int[] getConsumedSlots();

    int getRequiredCount(int slot);
}
```

公共加工引擎只依赖 `ProcessingRecipe`，不直接依赖单输入实现细节。

## 5.3 Coffee Machine 槽位设计

Coffee Machine 调整为：

| 槽位 | 作用          |
| -: | ----------- |
|  0 | 咖啡粉或饮品基础原料  |
|  1 | 水、牛奶或其他修饰原料 |
|  2 | 空杯          |
|  3 | 成品输出        |

推荐首批配方：

### Espresso

```text
槽 0：2 × coffee_powder
槽 1：必须为空
槽 2：1 × cup
输出：espresso
```

### Americano

```text
槽 0：1 × coffee_powder
槽 1：1 × water_bucket
槽 2：1 × cup
输出：coffee_americano
返还：bucket
```

### Latte

```text
槽 0：1 × coffee_powder
槽 1：1 × milk_bucket
槽 2：1 × cup
输出：coffee_latte
返还：bucket
```

未被配方声明的 Coffee Machine 输入槽必须为空，避免 Espresso 在存在水或牛奶时错误匹配。

## 5.4 存档迁移

Coffee Machine 当前只有两个槽位。升级为四槽后必须迁移旧 NBT：

```text
旧槽 0 → 新槽 0
旧槽 1 → 新槽 3
新槽 1、2 → 空
```

新增：

```text
InventoryVersion
```

建议：

```text
0：旧两槽结构
1：四槽 Coffee Machine
```

加载旧数据时进行显式迁移，不能假设 `ItemStackHandler.deserializeNBT()` 会自动把旧输出槽移动到新输出槽。

必须添加 GameTest：

```text
coffeeMachine_oldInventoryMigrates
```

---

## 6. MachineRecipeBuilder

新增：

```text
src/main/java/net/langball/coffee/datagen/recipe/
└── MachineRecipeBuilder.java
```

职责：

* 生成单输入机器配方；
* 写入正确 Serializer ID；
* 写入 Ingredient；
* 写入 Result；
* 写入 Experience；
* 写入 Cooking Time；
* 进行构建期校验；
* 防止重复 ID；
* 支持 Item 和 Tag Ingredient。

建议 API：

```java
MachineRecipeBuilder.grinding(
        Ingredient.of(ModItems.COFFEE_BEAN.get()),
        new ItemStack(ModItems.COFFEE_POWDER.get())
)
.experience(0.2F)
.cookingTime(200)
.save(writer, CoffeeWork.id("grinding/coffee_powder"));
```

提供工厂方法：

```java
grinding(...)
ovenBaking(...)
rolling(...)
icecreamMaking(...)
```

或者使用统一方法：

```java
MachineRecipeBuilder.machine(
        ModRecipeTypes.GRINDING_SERIALIZER,
        ingredient,
        result
);
```

首选前者，调用处更清晰。

## 6.1 Builder 校验

`save()` 前必须验证：

* Recipe ID 不为空；
* Ingredient 不为空；
* Result 不为空；
* Result Count 大于零；
* Result Count 不超过最大堆叠；
* Experience 有限且不小于零；
* Cooking Time 位于 1–72000；
* Serializer 与 RecipeType 对应；
* 不允许保存相同 ID；
* 不允许同类型、同输入出现多个无选择条件的结果。

## 6.2 FinishedRecipe

Builder 应实现或创建：

```java
FinishedRecipe
```

自定义机器配方不需要 Recipe Book advancement，可以让：

```java
serializeAdvancement()
```

返回 `null`，但必须明确记录原因。

---

## 7. CoffeeBrewingRecipeBuilder

新增：

```text
CoffeeBrewingRecipeBuilder.java
```

建议 API：

```java
CoffeeBrewingRecipeBuilder.brewing(
        new ItemStack(ModItems.COFFEE_AMERICANO.get())
)
.base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 1)
.modifier(Ingredient.of(Items.WATER_BUCKET), 1)
.container(Ingredient.of(ModItems.CUP.get()), 1)
.experience(0.2F)
.cookingTime(120)
.save(writer, CoffeeWork.id("coffee_brewing/americano"));
```

Espresso 允许不调用 `.modifier()`：

```java
CoffeeBrewingRecipeBuilder.brewing(
        new ItemStack(ModItems.ESPRESSO.get())
)
.base(Ingredient.of(ModItems.COFFEE_POWDER.get()), 2)
.requireEmptyModifier()
.container(Ingredient.of(ModItems.CUP.get()), 1)
...
```

JSON 示例：

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
      "item": "minecraft:water_bucket"
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
    "item": "coffeework:coffee_americano",
    "count": 1
  },
  "experience": 0.2,
  "cookingtime": 120
}
```

Espresso JSON 中可以省略 `modifier`，但 Serializer 必须将“省略”解释为该槽必须为空，而不是任意物品均可存在。

---

## 8. ModMachineRecipeProvider

新增：

```text
src/main/java/net/langball/coffee/datagen/
└── ModMachineRecipeProvider.java
```

在 `ModDataGenerators` 中注册。

Provider 分区：

```java
buildGrindingRecipes(writer);
buildOvenRecipes(writer);
buildRollingRecipes(writer);
buildIcecreamRecipes(writer);
buildCoffeeRecipes(writer);
```

不得把机器配方继续混入已有的 `ModRecipeProvider`。

普通 RecipeProvider 负责：

* 工作台；
* 熔炉；
* 存储拆装；
* 中间材料；
* 机器方块本身。

Machine Recipe Provider 负责：

* Grinder；
* Oven；
* Roller；
* Icecream Machine；
* Coffee Machine。

---

## 9. 第一批正式配方

## 9.1 Grinder

### P0

```text
熟咖啡豆 → 咖啡粉
可可豆 → 可可粉
小麦 → 面粉
```

建议 ID：

```text
grinding/coffee_powder
grinding/cocoa_powder
grinding/flour
```

其中：

```text
coffee_bean → coffee_powder
minecraft:cocoa_beans → cocoa_powder
minecraft:wheat → flour
```

### P1

根据旧版行为补充：

```text
糖类研磨
香料研磨
其他谷物
```

### 用途验证

* 咖啡粉：Coffee Machine、冷萃、咖啡蛋糕；
* 可可粉：巧克力、蛋糕、后续可可饮品；
* 面粉：面团、面包、蛋糕。

---

## 9.2 Oven

### P0

```text
生咖啡豆 → 熟咖啡豆
基础面包面团 → 面包
```

建议 ID：

```text
oven_baking/coffee_bean
oven_baking/bread
```

### P1

```text
圆面包
法棍
贝果
吐司
曲奇
```

### 关键规则

机器配方生成并通过测试后，删除普通熔炉中的：

```text
coffee_bean_raw → coffee_bean
dough_bread → bread
```

移除 fallback 和新增机器配方必须处于同一个 PR，防止提交中间状态导致物品不可达。

---

## 9.3 Roller

### P0

```text
iron_ingot → plate_iron
dough → plate_dough
```

`plate_iron` 必须进入 P0，因为 Coffee Machine 的工作台配方依赖铁板。缺少这条配方会导致第一杯咖啡链路中断。

建议 ID：

```text
rolling/plate_iron
rolling/plate_dough
```

### P1

```text
dough_pastry → plate_dough_pastry
dough_ginger → plate_dough_ginger
```

### fallback 移除

机器配方稳定后删除：

```text
2× dough → plate_dough
2× dough_pastry → plate_dough_pastry
2× dough_ginger → plate_dough_ginger
```

---

## 9.4 Icecream Machine

为了保留 Phase 2 的“原料、冷却剂、输出”三槽结构，首批不扩展 Icecream Machine 为多输入。

新增中间物品：

```text
icecream_mix_vanilla
```

普通工作台或 Mixing Bowl 配方：

```text
milk_bucket
+ sugar
+ vanilla
+ mixing_bowl
→ icecream_mix_vanilla
```

容器返还：

```text
milk_bucket → bucket
mixing_bowl → mixing_bowl
```

机器配方：

```text
icecream_mix_vanilla
+ 冷却剂
→ icecream_vanilla
```

建议 ID：

```text
icecream_making/vanilla
```

删除现有直接生成最终香草冰淇淋的工作台 fallback。

### 用途验证

* `icecream_mix_vanilla` 的唯一用途是进入 Icecream Machine；
* `icecream_vanilla` 是可食用终端产物；
* JEI 必须同时显示混合物合成和机器冻结步骤。

---

## 9.5 Coffee Machine

### P0

```text
coffee_brewing/espresso
coffee_brewing/americano
coffee_brewing/latte
```

建议初始数值：

| 饮品        | 咖啡粉 | 修饰物          | 空杯 | 输出杯数 |
| --------- | --: | ------------ | -: | ---: |
| Espresso  |   2 | 无            |  1 |    2 |
| Americano |   1 | Water Bucket |  1 |    4 |
| Latte     |   1 | Milk Bucket  |  1 |    4 |

实际投入数量和加工时间以旧版审计结果为准。

### 容器处理

* 空杯被消耗并成为饮品容器；
* Water Bucket 消耗后返还 Bucket；
* Milk Bucket 消耗后返还 Bucket；
* 饮品饮用完最后一杯后返还 Cup；
* Coffee Machine 不得凭空生成 Cup；
* 输入返还失败时不得吞掉 Bucket。

---

## 10. 饮品 cups 初始化

## 10.1 单一数据源

每种饮品的最大杯数已经由对应 `DrinkCoffee` 实例配置。

禁止在以下位置重复硬编码杯数：

* JSON Recipe；
* Coffee Machine BlockEntity；
* JEI Category；
* DataProvider；
* Screen。

为 `DrinkCoffee` 增加：

```java
public int getConfiguredMaxCups() {
    return maxCups;
}
```

再增加：

```java
public ItemStack initializeFreshStack(ItemStack stack) {
    return initCupCount(stack, maxCups);
}
```

## 10.2 Recipe 输出初始化

Coffee Brewing Recipe 统一调用：

```java
private ItemStack createResultStack() {
    ItemStack output = result.copy();

    if (output.getItem() instanceof DrinkCoffee drink) {
        drink.initializeFreshStack(output);
    }

    return output;
}
```

以下两个方法必须使用同一结果工厂：

```java
assemble(...)
getResultItem(...)
```

这样可以保证：

* Coffee Machine 实际产物有 cups；
* JEI 显示结果有 cups；
* JEI tooltip 显示正确杯数；
* 测试读取结果时 cups 正确；
* 不会出现实际产物和 JEI 示例不同。

## 10.3 验证值

第一批饮品应验证：

```text
Americano：remaining_cups = 4，max_cups = 4
Espresso：remaining_cups = 2，max_cups = 2
Latte：remaining_cups = 4，max_cups = 4
```

如果旧版审计得到不同数值，以旧版行为和已批准决策为准。

---

## 11. 原子化多输入消费

Coffee Machine 完成加工前必须同时验证：

* 基础原料数量足够；
* 修饰原料满足条件；
* 空杯存在；
* 输出能容纳完整结果；
* Water/Milk Bucket 的 remainder 可放回；
* 配方仍是当前 Recipe；
* 所有输入在同一 tick 原子消耗。

处理顺序：

1. 重新验证 Recipe；
2. 构造初始化后的 Result Stack；
3. 检查输出空间；
4. 检查所有输入数量；
5. 检查所有 remainder；
6. 消耗咖啡粉；
7. 消耗水或牛奶；
8. 处理 Bucket；
9. 消耗 Cup；
10. 写入饮品结果；
11. 记录 Recipe 使用次数；
12. 标记脏数据并同步。

任何一步无法完成时，不得消耗部分材料。

---

## 12. Coffee Machine 菜单与自动化

## 12.1 Menu

调整：

```text
ContainerCoffeeMachine
CoffeeMachineScreen
CoffeeMachineBlockEntity
```

槽位建议：

```text
咖啡粉：36, 26
修饰物：56, 44
空杯：76, 26
输出：116, 35
```

实际坐标根据现有 GUI 纹理调整。

## 12.2 shift-click

玩家背包 shift-click 时：

* 咖啡粉优先进入槽 0；
* Water/Milk Bucket 优先进入槽 1；
* Cup 优先进入槽 2；
* 其他物品在背包与快捷栏间移动；
* 输出槽继续使用 `SlotMachineResult`。

不得再使用单一 `hasRecipe(stack)` 将所有原料都移动到槽 0。

## 12.3 Capability

建议：

| 方向             | Coffee Machine 行为 |
| -------------- | ----------------- |
| UP             | 咖啡粉输入             |
| 水平方向           | 修饰物和空杯输入          |
| DOWN           | 成品输出              |
| `side == null` | 完整 Handler        |

如要让漏斗区分修饰物和空杯，可在水平 Handler 中暴露槽 1 和 2，并依赖各槽 `isItemValid()`。

必须新增真实 Capability GameTest。

---

## 13. JEI 集成

## 13.1 配方枚举

新增统一工具：

```text
MachineRecipeCatalog
```

职责：

```java
getGrindingRecipes(recipeManager)
getOvenRecipes(recipeManager)
getRollingRecipes(recipeManager)
getIcecreamRecipes(recipeManager)
getCoffeeRecipes(recipeManager)
```

JEI、调试日志和数量报告都使用这个类，不允许各自编写过滤规则。

## 13.2 Coffee Category

Coffee Machine 分类显示：

```text
咖啡粉输入
修饰物输入
空杯输入
成品输出
加工时间
经验
```

Espresso 的修饰物槽显示为空或明确标注“无需修饰物”。

## 13.3 cups 显示

JEI 输出必须使用 Recipe 的 `getResultItem()`，不能重新创建普通 ItemStack。

这样输出 tooltip 应显示：

```text
剩余杯数：4 / 4
```

## 13.4 数量一致性

启动开发客户端时记录：

```text
RecipeManager:
grinding = N
oven_baking = N
rolling = N
icecream_making = N
coffee_brewing = N

JEI registered:
grinding = N
oven_baking = N
rolling = N
icecream_making = N
coffee_brewing = N
```

JEI 注册数量必须直接来自同一 Recipe 列表。

第一批最低数量：

```text
grinding >= 3
oven_baking >= 2
rolling >= 2
icecream_making >= 1
coffee_brewing >= 3
```

---

## 14. direct crafting fallback 移除策略

只在替代机器流程已经满足以下条件后删除 fallback：

* 配方已生成；
* 机器本身可制造；
* 输入可获得；
* 输出有用途；
* JEI 可见；
* GameTest 通过；
* 可达性报告无阻断项。

### Phase 3 应删除

```text
Furnace：coffee_bean_raw → coffee_bean
Furnace：dough_bread → bread
Crafting：plate_dough
Crafting：plate_dough_pastry
Crafting：plate_dough_ginger
Crafting：icecream_vanilla
```

### Phase 3 不必删除

不属于第一批闭环、且尚未恢复机器替代路径的 fallback，可以暂时保留，但必须在报告中标记：

```text
deferred_fallback
target_phase
replacement_machine
```

不得无记录地保留。

---

## 15. 配方可达性报告

新增：

```text
tools/report_recipe_reachability.py
```

输出：

```text
build/reports/coffeework/recipe-reachability.json
build/reports/coffeework/recipe-reachability.md
```

可选提交快照：

```text
docs/RECIPE_REACHABILITY.md
```

## 15.1 图模型

节点：

```text
Item ID
Block Item ID
Tag
Machine
```

边：

```text
输入材料 → 输出材料
```

配方类型：

```text
crafting
smelting
grinding
oven_baking
rolling
icecream_making
coffee_brewing
loot
crop
worldgen
```

## 15.2 初始可获得集合

包括：

* 常见原版材料；
* 木材；
* 石头；
* 铁；
* 红石；
* 小麦；
* 可可豆；
* 牛奶；
* 水；
* 糖；
* 世界生成或掉落可获得的模组种子；
* 已确认可获得的 Coffee Workshop 作物。

禁止把创造栏内容作为初始可获得集合。

## 15.3 报告内容

报告至少包含：

### 机器数量

```text
Grinding recipes
Oven recipes
Rolling recipes
Icecream recipes
Coffee recipes
```

### 不可达结果

```text
UNREACHABLE
```

### 没有用途的结果

```text
UNUSED_OUTPUT
```

### 重复路径

```text
DIRECT_FALLBACK_DUPLICATE
```

### 配方冲突

```text
AMBIGUOUS_RECIPE
```

### 循环依赖

```text
CIRCULAR_DEPENDENCY
```

### 终端用途

```text
CONSUMABLE
PLACEABLE
INGREDIENT
CONTAINER
```

## 15.4 P0 可达链

报告必须明确打印：

```text
Coffee Seeds
→ Raw Coffee Bean
→ Oven
→ Roasted Coffee Bean
→ Grinder
→ Coffee Powder
→ Coffee Machine
→ Americano
```

并标记每一步对应 Recipe ID。

---

## 16. 生存模式可达性审计

在实现机器配方前，先确认以下基础节点可获得：

* Coffee Seeds；
* Raw Coffee Bean；
* Cup；
* Mixing Bowl；
* Vanilla；
* Plate Iron；
* 五台机器方块。

特别检查：

### Coffee Seeds

如果新世界无法自然获得 Coffee Seeds，咖啡闭环仍然不可达。

必须通过旧版行为选择至少一种正式来源：

* 世界生成咖啡树；
* 草丛掉落；
* 战利品箱；
* 村民交易；
* 其他已批准来源。

不得通过创造栏作为唯一来源。

### Cup

Cup 必须有生存配方，并且该配方不能依赖尚不可达的机器输出。

### Plate Iron

因为 Coffee Machine 配方依赖 Plate Iron，必须确保：

```text
先制造 Roller
→ Roller 压制 Iron Ingot
→ 获得 Plate Iron
→ 制造 Coffee Machine
```

不存在：

```text
Coffee Machine 需要 Plate Iron
Plate Iron 需要 Coffee Machine
```

---

## 17. 所有结果的用途

第一批输出用途矩阵：

| 输出                  | 用途                   |
| ------------------- | -------------------- |
| Roasted Coffee Bean | Grinder 输入、储存袋       |
| Coffee Powder       | 三种咖啡、冷萃、咖啡甜点         |
| Cocoa Powder        | 巧克力、蛋糕、后续可可饮品        |
| Flour               | Dough、面包和甜点          |
| Plate Iron          | Coffee Machine、工具和模具 |
| Plate Dough         | 后续烘焙或甜点              |
| Vanilla Ice Cream   | 可食用终端产物              |
| Americano           | 可饮用终端产物              |
| Espresso            | 可饮用，同时用于 Tiramisu    |
| Latte               | 可饮用终端产物              |

如果报告发现某个中间结果既不可食用、不可放置，也没有下游配方，则该项不得标记完成。

---

## 18. GameTest 计划

## 18.1 DataGen 与 Serializer

新增测试：

```text
machineRecipeBuilder_writesExpectedJson
machineRecipeSerializer_roundTrips
coffeeRecipeSerializer_roundTrips
invalidRecipeData_isRejected
duplicateMachineRecipe_isRejected
```

## 18.2 Grinder

```text
coffeeBean_grindsToCoffeePowder
cocoaBean_grindsToCocoaPowder
wheat_grindsToFlour
```

## 18.3 Oven

```text
rawCoffeeBean_roastsToCoffeeBean
breadDough_bakesToBread
```

## 18.4 Roller

```text
ironIngot_rollsToPlateIron
dough_rollsToPlateDough
```

## 18.5 Icecream Machine

```text
vanillaMix_freezesToVanillaIcecream
invalidCoolant_doesNotStart
```

## 18.6 Coffee Machine

```text
espresso_brewsWithEmptyModifierSlot
americano_brewsWithWaterAndCup
latte_brewsWithMilkAndCup
coffeeOutput_initializesCups
coffeeRecipe_consumesCup
waterBucket_returnsBucket
milkBucket_returnsBucket
blockedOutput_consumesNothing
oldTwoSlotInventory_migrates
```

## 18.7 饮用闭环

至少增加：

```text
americano_hasFourCups
espresso_hasTwoCups
latte_hasFourCups
lastServing_returnsCup
singleCupConfig_doesNotAllowInfiniteUse
```

## 18.8 可达性

增加脚本测试：

```text
reachability_report_hasNoP0UnreachableNodes
reachability_report_hasNoAmbiguousCoffeeRecipes
reachability_report_hasExpectedMachineCounts
```

---

## 19. PR 拆分

## PR 3.0：旧版行为矩阵与 Recipe Schema

内容：

* 审计 master；
* 编写 `PHASE_3_PORTING_MATRIX.md`；
* 确认三种咖啡输入；
* 确认杯数和加工时间；
* 确认 Coffee Machine 多输入槽位；
* 在 `PORTING_DECISIONS.md` 记录决定。

验收：

* 不新增正式内容；
* 所有配方行为均有明确来源或批准决策。

---

## PR 3.1：MachineRecipeBuilder 与 Provider

内容：

* `MachineRecipeBuilder`；
* `ModMachineRecipeProvider`；
* DataGenerator 注册；
* Builder 校验；
* Grinder/Oven/Roller/Icecream 首批 JSON 生成；
* 迁移现有手工机器 JSON。

验收：

* 五种 RecipeType 都有 DataGen 输出；
* `runData` 幂等；
* Serializer round-trip 通过；
* 不存在手工与生成重复资源。

---

## PR 3.2：Grinder、Oven 与 Roller 生产链

内容：

* 咖啡豆、可可豆、小麦研磨；
* 生豆烘焙；
* 基础面包烘焙；
* 铁板压制；
* 基础面团压片；
* 删除对应 direct fallback；
* 新增真实 GameTest。

验收：

```text
Raw Bean → Roasted Bean → Powder
Wheat → Flour → Dough
Iron → Plate Iron
```

全部在生存模式可达。

---

## PR 3.3：Icecream Machine 最小闭环

内容：

* 新增 Vanilla Icecream Mix；
* 混合物工作台或 Mixing Bowl 配方；
* Vanilla Icecream Machine Recipe；
* 冷却剂验证；
* 删除最终冰淇淋直接合成 fallback；
* JEI 显示；
* GameTest。

验收：

* Vanilla Ice Cream 只能通过 Icecream Machine 核心路径获得；
* Mixing Bowl 和 Bucket 正确返还。

---

## PR 3.4：Coffee Brewing 多输入架构

内容：

* `ProcessingRecipe`；
* `CoffeeBrewingRecipe`；
* Serializer；
* Builder；
* Coffee Machine 四槽布局；
* NBT 迁移；
* Menu；
* Screen；
* shift-click；
* Capability；
* 原子输入消费；
* Bucket remainder。

验收：

* 三种配方无歧义；
* 老两槽 Coffee Machine 不丢物；
* 自动化槽位规则正确；
* 输出阻塞时不消费任何输入。

---

## PR 3.5：三种咖啡与 cups 初始化

内容：

* Espresso；
* Americano；
* Latte；
* `DrinkCoffee#getConfiguredMaxCups()`；
* `initializeFreshStack()`；
* Recipe 输出初始化；
* Cup 消耗和返还；
* GameTest。

验收：

```text
Americano：4 / 4
Espresso：2 / 2
Latte：4 / 4
```

最后一次饮用返还正确 Cup。

---

## PR 3.6：JEI 与 Recipe 数量一致性

内容：

* `MachineRecipeCatalog`；
* 五类正式配方列表；
* Coffee 多输入布局；
* Catalyst；
* cups tooltip；
* 数量日志；
* 移除 fallback 展示。

验收：

* RecipeManager 与 JEI 五类数量逐项一致；
* JEI 中可以完整查看第一杯咖啡的所有步骤。

---

## PR 3.7：可达性报告与最终验收

内容：

* Reachability 脚本；
* Markdown/JSON 报告；
* CI 步骤；
* 新世界生存手工测试；
* 文档更新；
* 内容清单状态更新。

验收：

* 无 P0 `UNREACHABLE`；
* 无 P0 `AMBIGUOUS_RECIPE`；
* 无未记录 `DIRECT_FALLBACK_DUPLICATE`；
* Actions 全绿；
* 新世界成功制作第一杯咖啡。

---

## 20. CI 调整

在现有流程中加入：

```bash
./gradlew runData --no-daemon --stacktrace
python tools/audit_resources.py
python tools/report_recipe_reachability.py
./gradlew runGameTestServer --no-daemon --stacktrace
./gradlew build --no-daemon --stacktrace
```

CI 必须失败的情况：

* DataGen 产生未提交变化；
* 正式 Recipe ID 重复；
* 同输入出现歧义结果；
* P0 输出不可达；
* 核心 fallback 仍然存在；
* RecipeManager 机器配方数量低于最低值；
* 任一真实 GameTest 失败；
* JAR 未生成。

上传报告：

```text
build/reports/coffeework/**
```

---

## 21. 手工验收流程

创建一个全新生存世界，禁止：

```text
Creative Mode
/give
/datapack 手工注入
调试箱
```

完成以下流程：

1. 获得 Coffee Seeds；
2. 种植 Coffee Tree；
3. 收获 Raw Coffee Bean；
4. 制造 Grinder；
5. 制造 Oven；
6. 制造 Roller；
7. 使用 Roller 制作 Plate Iron；
8. 制造 Coffee Machine；
9. 使用 Oven 烘焙 Raw Coffee Bean；
10. 使用 Grinder 研磨 Coffee Bean；
11. 制作 Cup；
12. 准备 Water Bucket；
13. 在 Coffee Machine 中制作 Americano；
14. 确认结果为 `4 / 4 cups`；
15. 连续饮用四次；
16. 确认返还 Cup；
17. 重复验证 Espresso；
18. 重复验证 Latte；
19. 打开 JEI 检查全部生产步骤；
20. 重启世界，确认机器库存和饮品 cups 不丢失。

将过程记录到：

```text
docs/PHASE_3_SURVIVAL_ACCEPTANCE.md
```

---

## 22. Phase 3 最终验收表

### P0

* [ ] 旧版行为矩阵完成；
* [ ] MachineRecipeBuilder 完成；
* [ ] Machine Recipe Provider 完成；
* [ ] 五种 RecipeType 有正式生成配方；
* [ ] Grinder 三条基础配方完成；
* [ ] Oven 生豆和面包完成；
* [ ] Roller 铁板和基础面团完成；
* [ ] Icecream Vanilla 完成；
* [ ] Coffee Machine 多输入完成；
* [ ] Americano 完成；
* [ ] Espresso 完成；
* [ ] Latte 完成；
* [ ] cups 初始化完成；
* [ ] Cup 和 Bucket 返还完成；
* [ ] 核心 direct fallback 删除；
* [ ] JEI 数量一致；
* [ ] 可达性报告无 P0 错误；
* [ ] 新世界第一杯咖啡完成；
* [ ] CI 全绿。

### P1

* [ ] 其余基础面团压片；
* [ ] 更多基础面包；
* [ ] 额外 Grinder 材料；
* [ ] 更完整 JEI tooltip；
* [ ] 配方数值平衡；
* [ ] 可达性报告终端用途分类完善；
* [ ] 不同配置下的饮品测试；
* [ ] 自动化 Coffee Machine 完整测试。

---

## 23. 最终原则

Phase 3 的完成标准不是“生成了一批 JSON”，而是：

> 玩家能够从新世界中自然获得咖啡原料，通过五台机器中的正式生产流程制作第一杯咖啡；RecipeManager、JEI、饮品 cups、容器返还和可达性报告对这一流程给出一致结果。

任何只有创造栏入口、只有手工 JSON、依赖 direct fallback、存在同输入多结果歧义或无法返还容器的实现，都不能视为 Phase 3 完成。
