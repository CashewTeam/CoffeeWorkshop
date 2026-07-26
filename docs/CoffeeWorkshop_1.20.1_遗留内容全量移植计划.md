# Coffee Workshop 1.20.1 遗留内容全量移植计划

> **新基线提交：** `4a752b854e26865325ad8d9a0397b7a3c834be7e`  
> **目标：** 将当前仓库中的全部遗留模型、贴图、Blockstate 和旧内容设计转化为实际可玩的 1.20.1 游戏内容。  
> **原则：** 不再以“归档、删除、长期搁置”为完成状态。允许合并旧 ID，但旧资源所表达的视觉、状态或玩法能力必须被现代系统实际使用。  
> **测试策略：** 新增自动测试继续后移；每批内容必须通过 DataGen、Build、资源审计、Manifest 同步和配方可达性检查，并进行必要的人工视觉验收。

---

## 1. 最新提交审计

提交 `4a752b8` 完成了两项来源补齐：

1. 为 `coffee_americano_nitro_fruit_ice` 增加：
   ```text
   coffee_americano_nitro_ice + syrup_fruit
   → coffee_americano_nitro_fruit_ice
   ```
2. 咖啡树增加精准采集与剪刀获取方块本体的 Loot Pool。

Manifest 因此从：

```text
Survival Source 175/176
```

更新为：

```text
Survival Source 176/176
```

### 1.1 必须先修：氮气水果美式会重置杯数

当前使用普通 `minecraft:crafting_shapeless`：

```text
已喝过的 Nitro Americano
+ Fruit Syrup
→ 新的 Nitro Fruit Americano
```

普通无序合成不会复制输入饮品的 NBT。`DrinkCoffee` 会在首次使用或显示 Tooltip 时，将没有 NBT 的结果初始化为最大杯数。

因此可能发生：

```text
剩余 1/4 杯 Nitro Americano
→ 合成
→ 得到 4/4 杯 Nitro Fruit Americano
```

这会形成饮品复制漏洞。

#### 修复要求

新增通用的：

```text
DrinkTransformRecipe
```

职责：

- 匹配一个源 `DrinkCoffee` 和一个变换材料；
- 复制源饮品的 `remaining_cups`；
- 将杯数限制在目标饮品的 `max_cups`；
- 保留允许继承的其他 NBT；
- 正常返回 `syrup_empty`；
- `getResultItem()` 返回初始化过的展示 Stack；
- 可以继续用于未来的糖浆、奶泡、苏打或咖啡器具变换。

Nitro Fruit 配方应迁移到这一 Serializer，不使用普通 Shapeless Recipe。

---

### 1.2 必须先修：咖啡树特殊工具掉落会与正常掉落叠加

当前 Loot Table 包含四个相互独立的 Pool：

```text
Silk Touch → coffee_tree
Shears → coffee_tree
成熟 → coffee_bean_raw
所有阶段 → coffee_seeds
```

特殊工具 Pool 没有排除普通成熟掉落和种子掉落。

因此：

```text
剪刀破坏成熟咖啡树
→ coffee_tree
+ coffee_bean_raw
+ 可能的 coffee_seeds
```

如果剪刀同时满足 Silk Touch 条件，还可能触发两个方块本体 Pool。

#### 修复要求

建立互斥分支：

```text
Silk Touch OR Shears
→ 只掉 coffee_tree

否则：
  成熟 → coffee_bean_raw + 概率种子
  未成熟 → 概率种子
```

推荐在 Loot Provider 中复用：

```text
HAS_SILK_TOUCH_OR_SHEARS
NOT_HAS_SILK_TOUCH_OR_SHEARS
```

普通收获 Pool 必须添加反向条件。

---

### 1.3 当前提交判定

可以确认：

- 两个内容 ID 已经有形式上的来源；
- Manifest 已同步更新；
- 新内容扩展可以以该提交为结构基线。

但在上述两个语义问题修复前，不应把它作为最终玩法基线。

建议状态：

```text
Registered resource coverage: complete
Registered source coverage: nominally complete
Gameplay semantics: two closure fixes pending
```

---

# 2. 全量移植目标的重新定义

当前 Manifest 将 639 个资源标为：

```text
ASSET_ARCHIVE 521
PORT_NOW       14
PORT_LATER     52
REMOVED        33
MERGED         14
REDESIGN        5
```

这些名称不再符合项目目标。

## 2.1 新状态体系

替换为：

| 新状态 | 含义 |
|---|---|
| `ACTIVE_RUNTIME_ASSET` | 已经被 Blockstate、模型父级、Renderer 或现有物品使用 |
| `TO_PORT_STANDALONE` | 需要注册为独立物品或方块 |
| `TO_PORT_INTERMEDIATE` | 需要成为原料、半成品、模具态或生坯 |
| `TO_WIRE_STATE_VARIANT` | 需要接入现有 Block 的状态、食用阶段或工作状态 |
| `TO_WIRE_DISPLAY_VARIANT` | 需要进入通用摆放/展示系统 |
| `TO_PORT_MACHINE` | 需要恢复机器、器具或交互功能 |
| `TO_PORT_DECOR` | 需要成为可放置装饰或家具 |
| `MERGED_RUNTIME_VARIANT` | 旧 ID 合并到现代状态，但资源能力仍被使用 |
| `UNASSIGNED` | 尚未决定运行时归属，最终必须为 0 |

不再使用以下终态：

```text
ASSET_ARCHIVE
REMOVED
PORT_LATER
```

它们只能作为历史标签，不能作为移植完成结果。

---

## 2.2 639 个资源不等于 639 个新注册对象

许多资源本来就是现有内容的组成部分。

例如：

```text
cake_coffee_uneaten
cake_coffee_slice1
...
cake_coffee_slice6
```

它们应属于：

```text
BlockCakeBasic(cake_coffee)
→ BITES=0..6 的模型状态
```

这类资源不应单独注册七个方块，但必须由现有方块的 Blockstate 实际引用。

同理：

- `*_raw`：应成为真实生坯或原料阶段；
- `*_model`：应成为装入模具的中间物；
- `*_base`：应成为蛋糕底或组装步骤；
- `*_plate`：应进入饮品/食物摆放系统；
- `*_on`：应成为机器 `LIT=true` 的视觉；
- crop stage：应成为作物年龄状态；
- slice models：应成为食用阶段状态。

最终验收不是“所有文件都有独立 Registry ID”，而是：

> 每个遗留资源都能追溯到一个正在运行的物品、方块、状态、配方阶段、渲染器、GUI 或实体。

---

# 3. Phase 5.0：移植基线与资源所有权图

这是下一步最优先工作。

## 3.1 修复最新提交的两个语义问题

- `DrinkTransformRecipe`
- 咖啡树特殊工具互斥 Loot Table

## 3.2 重构内容清单

新增：

```text
docs/legacy_content_matrix.json
docs/LEGACY_CONTENT_MATRIX.md
```

每个遗留资源至少记录：

```json
{
  "asset_id": "cake_coffee_slice1",
  "asset_type": "block_model",
  "content_family": "cake_coffee",
  "runtime_owner": "coffeework:cake_coffee",
  "runtime_role": "blockstate:bites=1",
  "target_phase": "5.4",
  "status": "ACTIVE_RUNTIME_ASSET",
  "source": "legacy_1_12",
  "survival_chain": "cake assembly",
  "notes": ""
}
```

独立物品示例：

```json
{
  "asset_id": "icecream_chocolate",
  "asset_type": "item_model",
  "content_family": "icecream",
  "runtime_owner": "coffeework:icecream_chocolate",
  "runtime_role": "finished_food",
  "target_phase": "5.2",
  "status": "TO_PORT_STANDALONE"
}
```

## 3.3 审计器升级

Manifest 生成器必须递归解析：

```text
Blockstate
→ Block Model
→ Parent Model
→ Texture

Item Model
→ Parent Model
→ Texture

Renderer model map
→ Additional baked model

Recipe
→ Intermediate item
→ Finished output
```

新增指标：

```text
Legacy assets assigned: X/639
Legacy assets active in runtime: X/639
Standalone legacy content registered: X/Y
Unassigned assets: 0
```

### 验收

- 当前已经被 Blockstate 引用的蛋糕切片、作物阶段不能再被列为 Orphan；
- 639 个资源全部拥有 `runtime_owner` 或明确的待实现 Owner；
- `UNASSIGNED = 0`；
- 不移动或删除资源。

---

# 4. Phase 5.1：注册体系扩容准备

当前 `ModItems` 和 `ModBlocks` 已经较大。在恢复数十到上百内容前，应先拆分。

建议结构：

```text
init/
  ModCoffeeItems.java
  ModBakeryItems.java
  ModIngredientItems.java
  ModEquipmentItems.java
  ModFoodBlocks.java
  ModDecorBlocks.java
  ModItems.java            // 统一 DeferredRegister 出口
```

公共注册辅助：

```java
registerFood(id, nutrition, saturation)
registerFastFood(id, nutrition, saturation)
registerReusableContainer(id, remainder)
registerBlockItem(id, block)
registerRawAndFinished(rawId, finishedId)
```

DataGen Provider 分组：

```text
CoffeeRecipeGroup
IcecreamRecipeGroup
SandwichRecipeGroup
PastryRecipeGroup
CakeRecipeGroup
ConfectioneryRecipeGroup
EquipmentRecipeGroup
```

目标不是运行时动态注册，而是降低静态 DeferredRegister 的重复代码和漏项概率。

---

# 5. Phase 5.2：第一批高价值内容恢复

第一批直接恢复当前 14 个 `PORT_NOW` 内容。

## 5.1 杯装速溶咖啡

恢复：

```text
coffee_instant_cup_unopen
coffee_instant_cup
```

建议链路：

```text
coffee_instant_stick + cup
→ coffee_instant_cup_unopen

未开启杯装速溶咖啡
+ water bottle / water bucket interaction
→ coffee_instant_cup
```

行为：

- 未开启杯不能饮用；
- 开启后使用 `DrinkCoffeeInstant`；
- 饮完返回 `cup`；
- 水瓶/桶正常返还容器；
- 模型分别使用两个遗留资源。

## 5.2 六种冰淇淋

恢复：

```text
icecream_apple
icecream_berry
icecream_chocolate
icecream_coffee
icecream_lemon
icecream_melon
```

与现有：

```text
icecream_vanilla
```

组成七种口味。

推荐 Icecream Machine 输入结构：

```text
Base Mix + Flavor + Container
→ Ice Cream
```

必要时新增：

```text
icecream_mix_apple
...
```

若没有对应遗留混合物贴图，可继续使用统一 `icecream_mix`，由 Flavor 决定输出。

## 5.3 六种三明治

恢复：

```text
sandwich_bacon_egg
sandwich_beef_cheese
sandwich_blt_large
sandwich_club
sandwich_club_large
sandwich_ham_cheese
```

与现有：

```text
sandwich_blt
```

形成完整家族。

建议：

- 普通版由面包与馅料工作台合成；
- Large 版消耗双份面包和馅料；
- 不新增机器；
- 食物值按成本分层；
- 村民 Food Trader 可在后续出售低等级三明治，但不能形成交易套利。

### Phase 5.2 验收

- 新增 14 个 Item 注册；
- 三语文本；
- 模型与贴图；
- 创造模式页；
- Recipe/机器来源；
- JEI 可见；
- Manifest Source 100%；
- 遗留矩阵中 14 项变为 `ACTIVE_RUNTIME_ASSET`。

---

# 6. Phase 5.3：烘焙与甜品基础原料

在恢复长链食品前，先恢复被旧清单标为 `REMOVED` 的基础内容。

## 6.1 糖果与烘焙原料

恢复：

```text
caramel
caramel_apple
custard
milk_form → 建议显示名“奶泡”，ID 可保留或现代化为 milk_foam
hardtack
cookie_black
cookie_oreo
marshmallow
marshmallow_roast
marshmallow_chocolate
smore
pot
```

链路建议：

```text
Sugar → Caramel
Apple + Caramel → Caramel Apple

Milk + Egg + Sugar + Mixing Bowl
→ Custard

Milk + processing
→ Milk Foam

Marshmallow
→ Campfire/Smoker/Oven
→ Roasted Marshmallow

Roasted Marshmallow + Chocolate + Cookie
→ S'more
```

## 6.2 生坯与模具态的统一原则

所有遗留：

```text
*_raw
*_model
*_base
*_plate_raw
*_plate_model
```

都必须获得真实用途。

推荐语义：

| 后缀 | 运行时意义 |
|---|---|
| `_raw` | 未烘焙生坯 |
| `_model` | 装入模具的成型生坯 |
| `_base` | 蛋糕底/组装基底 |
| `_plate_raw` | 盘装未烘焙状态 |
| `_plate_model` | 使用盘式模具成型的状态 |

禁止仅注册最终食品而让这些中间资源继续闲置。

---

# 7. Phase 5.4：蛋糕资源完整接入

当前大量 `ASSET_ARCHIVE` 实际上属于蛋糕状态和制作链。

## 7.1 食用阶段模型

现有 `BlockCakeBasic` 使用 `BITES=0..6`。

每种蛋糕必须检查：

```text
uneaten
slice1
slice2
slice3
slice4
slice5
slice6
```

是否全部由 Blockstate 引用。

已有引用的资源改标：

```text
ACTIVE_RUNTIME_ASSET
```

## 7.2 切片物品

恢复：

```text
cake_<flavor>_slices
cake_slices
```

新增“切蛋糕”玩法：

```text
手持 plate / knife-like tool
右键蛋糕
→ 获得对应 cake slice item
→ BITES + 1
```

切片不直接增加饱食度，玩家可携带或用于其他甜点配方。

没有专用刀具时，可先使用：

```text
plate
```

作为取片容器；后续再恢复刀具资源。

## 7.3 蛋糕卷

恢复：

```text
cake_roll
cake_berry_roll
cake_carrot_roll
cake_chocolate_roll
cake_coffee_roll
cake_lemon_roll
cake_pumpkin_roll
cake_redvelvet_roll
cake_tea_roll
```

链路：

```text
Sponge Base
→ Roller
→ Cake Roll Sheet
+ Cream/Flavor
→ Finished Roll
```

这样可同时使用 Roller、Cream 家族和旧卷蛋糕模型。

## 7.4 原料态

将所有：

```text
cake_*_raw
cake_*_model
cake_*_base
```

接入：

```text
Mixing Bowl
→ Raw Batter
→ Mold
→ Oven
→ Sponge/Base
→ Final Cake
```

### Phase 5.4 验收

- 所有蛋糕 slice/uneaten 模型都有 Blockstate Owner；
- 所有 raw/model/base 物品有配方；
- 所有 roll 物品可获得；
- 无蛋糕家族资源保持“仅存在文件”。

---

# 8. Phase 6：完整冰淇淋、奶油和曲奇圣代

恢复：

```text
cream_milk
cream_apple
cream_berry
cream_chocolate
cream_coffee
cream_lemon
cream_melon
```

以及：

```text
cookie_icecream_vanilla
cookie_icecream_apple
cookie_icecream_berry
cookie_icecream_chocolate
cookie_icecream_coffee
cookie_icecream_lemon
cookie_icecream_melon
```

链路：

```text
Milk + Sugar + Flavor
→ Icecream Machine
→ Cream / Ice Cream

Cookie + Ice Cream
→ Cookie Ice Cream
```

建议将 `cream_*` 作为：

- 蛋糕卷配方；
- 蛋糕组装；
- 圣代；
- 泡芙与千层酥；

的公共中间材料。

---

# 9. Phase 7：糕点、派、玛芬与节日食品

## 9.1 糕点

恢复：

```text
croissant
croissant_chocolate
ginger_bread
ginger_bread_man
puff
mille_feuille
```

使用：

```text
Roller + Oven
```

完整链路：

```text
Dough
→ Roller
→ Raw Shape
→ Oven
→ Finished Pastry
```

## 9.2 派

恢复：

```text
pie_apple
pie_berry
pie_caramel
pie_chocolate
pie_coffee
pie_lemon
pie_melon
pie_tea
```

与现有 `pie_cream` 组成九种派。

每种派：

```text
plate_dough / plate_dough_pastry
+ filling
→ raw pie
→ Oven
→ finished pie
```

## 9.3 玛芬

恢复：

```text
muffin
muffin_berry
muffin_carrot
muffin_chocolate
muffin_coffee
muffin_lemon
muffin_pumpkin
muffin_redvelvet
muffin_tea
```

并使用全部：

```text
muffin_*_raw
```

## 9.4 摇晃蛋糕

恢复遗留拼写 ID：

```text
jiggy_cake*
```

显示名可以使用：

```text
Jiggly Cake / 摇晃蛋糕
```

不强制修改 Registry ID，避免无意义 ID 迁移。

恢复九种口味和对应：

```text
*_raw
*_model
```

## 9.5 月饼

恢复：

```text
mooncake
mooncake_egg
mooncake_fruit
mooncake_ham
```

使用现有：

```text
mooncake_model
```

和各类：

```text
mooncake_*_raw
```

## 9.6 舒芙蕾

恢复：

```text
souffle
souffle_chocolate
souffle_raw
souffle_chocolate_raw
```

---

# 10. Phase 8：全部饮品摆放模型接入游戏

遗留资源中存在大量：

```text
<drink_id>_plate
```

目标不是注册几十个重复方块，而是实现通用摆放系统。

## 10.1 推荐架构

```text
DrinkDisplayBlock
DrinkDisplayBlockEntity
DrinkDisplayItem / Plate interaction
DrinkDisplayRenderer
DrinkDisplayModelRegistry
```

BlockEntity 保存：

```text
ItemStack drink
remaining_cups
rotation
```

## 10.2 玩家交互

推荐流程：

```text
放置空 Plate
手持饮品右键 Plate
→ 转换为摆放饮品

右键摆放饮品
→ 饮用一杯
→ 更新 remaining_cups

最后一杯
→ 只留下空 Plate / Cup

破坏
→ 掉落保留杯数的原饮品
```

## 10.3 模型使用

新增：

```text
assets/coffeework/drink_display_models.json
```

示例：

```json
{
  "coffeework:coffee_americano": "coffeework:item/coffee_americano_plate",
  "coffeework:coffee_latte": "coffeework:item/coffee_latte_plate"
}
```

客户端通过：

```text
ModelEvent.RegisterAdditional
+ BlockEntityRenderer
```

加载并渲染这些旧 `_plate` 模型。

这样可以真正使用每一个摆放饮品资源，同时避免注册七十多个几乎相同的 Block。

### 验收

- 每个 `_plate` 模型都存在映射；
- 每种现有饮品都能放到桌面；
- 杯数 NBT 不丢失；
- 破坏和饮用不复制饮品；
- `DrinkDisplayModelRegistry` 缺项时 CI 失败。

---

# 11. Phase 9：器具、机器与家具恢复

此前标为 `REMOVED` 或 `REDESIGN` 的资源全部恢复。

## 11.1 Moka Pot

资源：

```text
moka_bottom
moka_top
moka_pot_unheated
moka_pot_heated
```

推荐实现：

```text
MokaPotBlock + BlockEntity
```

输入：

```text
Water + Coffee Powder
```

置于：

```text
Campfire / Stove-like heat source
```

完成后可倒入杯子。

状态：

```text
ASSEMBLED
HEATED
BREW_PROGRESS
SERVINGS
```

## 11.2 Turkish Coffee Pot

资源：

```text
turkey_coffee_pot
turkey_coffee_pot_unheated
turkey_coffee_pot_heated
```

实现手动火源加热，产出土耳其咖啡。

## 11.3 Coffee Pot

恢复：

```text
coffee_pot
coffee_pot_1
coffee_pot_2
coffee_pot_3
coffee_pot_4
```

建议实现为多杯服务壶：

- 状态表示剩余份数；
- 右键杯子倒出咖啡；
- 模型随容量变化；
- 可由 Coffee Machine 或 Moka Pot 填充。

## 11.4 Soda Machine

资源：

```text
soda_machine_bottom
soda_machine_top
```

实现双高方块：

```text
HALF=LOWER/UPPER
FACING
LIT/ACTIVE
```

机器输入：

```text
Bottle + Soda Material + Flavor
→ Soda Drink
```

同时恢复全部原版苏打口味和空瓶返还。

## 11.5 Phonograph

恢复：

```text
phonograph
```

实现为 Jukebox 风格方块：

- 支持现有三张唱片；
- 可取出唱片；
- 使用遗留模型；
- 可作为 Barista 装饰和 Advancement 节点。

## 11.6 Bar Furniture

资源：

```text
bar_stone_normal
bar_stone_inner
bar_wooden_normal
bar_wooden_inner
```

注册：

```text
stone_bar_counter
wooden_bar_counter
```

使用状态：

```text
SHAPE=STRAIGHT/INNER
FACING
```

可在放置或邻居更新时自动选择形状。

---

# 12. Phase 10：遗留内容闭环与引导

## 12.1 创造模式页扩容

内容全部恢复后，单一 Tab 会过于拥挤。

建议拆成：

```text
Coffee Workshop — Drinks & Machines
Coffee Workshop — Bakery & Ingredients
Coffee Workshop — Decor & Equipment
```

每个内容必须至少进入一个 Tab。

## 12.2 JEI

所有新机器与自定义配方加入 JEI：

```text
Drink Transform
Moka Brewing
Turkish Pot Brewing
Soda Machine
Display conversion（可只显示提示）
```

普通烘焙链由现有 Oven、Roller、Icecream Machine 分类承载。

## 12.3 Advancement

完整引导：

```text
Coffee Root
├─ Coffee Farming
├─ Machine Processing
├─ Drink Collection
├─ Cold Brew
├─ Ice Cream
├─ Bakery
├─ Cake Master
├─ Traditional Brewing
├─ Soda Shop
└─ Café Decoration
```

## 12.4 村民职业

内容扩展后重新分配：

- Barista：咖啡、咖啡器具、杯子；
- Materials Trader：作物、香料、糖浆材料；
- Food Trader：面包、三明治、糕点；
- 避免同物品买卖套利。

---

# 13. PR 拆分建议

## PR 5.0：基线语义修复

- `DrinkTransformRecipe`
- Nitro Fruit 迁移
- Coffee Tree 互斥 Loot
- Manifest 来源修正

## PR 5.1：遗留资源所有权矩阵

- 新状态体系
- 递归资源引用审计
- 639 项全部分配 Owner
- 修正“活跃资源被列为 Orphan”

## PR 5.2：注册与 DataGen 结构拆分

- Items/Blocks 分类文件
- Recipe Group Helper
- 公共 Food/Container 注册方法

## PR 5.3：速溶杯与七种冰淇淋

- 2 个速溶杯
- 6 个新冰淇淋
- 现有香草冰淇淋链路统一

## PR 5.4：完整三明治

- 6 个新三明治
- 食物数值与交易平衡

## PR 6.0：甜品公共原料

- caramel/custard/milk foam
- cookies/marshmallow/smore
- 生坯与模具语义

## PR 6.1：Cream 与 Cookie Ice Cream

- 7 Cream
- 7 Cookie Ice Cream

## PR 7.0：派与糕点

- 8 Pie
- Croissant/Ginger Bread/Puff/Mille-feuille

## PR 7.1：Muffin/Jiggly Cake/Mooncake/Soufflé

- 所有 finished/raw/model 资源

## PR 7.2：蛋糕中间物、卷蛋糕与切片玩法

- raw/model/base/roll/slices
- BlockCakeBasic 状态资源完整接入

## PR 8.0：通用饮品摆放系统

- BlockEntity + Renderer
- 全部 `_plate` 模型映射

## PR 9.0：传统咖啡器具

- Moka Pot
- Turkish Coffee Pot
- Coffee Pot

## PR 9.1：Soda Machine 与苏打饮料

- 双方块机器
- 完整苏打口味

## PR 9.2：Phonograph 与 Bar Furniture

- 唱片机
- 木制/石制吧台

## PR 10.0：全量闭环

- 三个 Creative Tab
- JEI
- Advancement
- Villager Trade
- `Legacy assets active = 639/639`
- `UNASSIGNED = 0`

---

# 14. 每批内容的强制交付面

每一个新内容家族必须在同一个 PR 中提交：

```text
Java Registry
+ Language
+ Item/Block Model
+ Texture
+ Blockstate（如适用）
+ Recipe / Machine Source / Interaction Source
+ Creative Tab
+ JEI Visibility
+ Loot / Remainder
+ Legacy Content Matrix Owner
+ Manifest Update
```

禁止只提交：

```text
模型和贴图
```

或只提交：

```text
Java 注册
```

而把获取方式、文本或配方留到不确定的未来。

---

# 15. CI 与完成门禁

自动测试继续后移，但以下门禁始终执行：

```bash
python tools/audit_resources.py
python tools/audit_content_surface.py
python tools/build_content_manifest.py
python tools/audit_legacy_runtime_coverage.py
./gradlew runData build --no-daemon --stacktrace
python tools/report_recipe_reachability.py
```

新增 Release 指标：

```text
Registered items with resources: 100%
Registered items with source: 100%
Legacy assets assigned: 639/639
Legacy assets runtime-active: 639/639
Unassigned legacy assets: 0
Unmapped drink plate models: 0
Unused raw/model/base assets: 0
```

允许旧 ID 合并，但必须在矩阵中指向实际 Owner，例如：

```text
coffeemachine_on
→ coffee_machine
→ blockstate lit=true
```

---

# 16. 下一步建议

立即开始的顺序：

1. 修复 Nitro Fruit 的 NBT 杯数复制问题；
2. 修复 Coffee Tree 特殊工具掉落叠加；
3. 将 Manifest 的 639 项重建为运行时所有权矩阵；
4. 恢复速溶杯；
5. 恢复六种冰淇淋；
6. 恢复六种三明治。

这一步完成后，项目将从“现有核心内容资源完整”正式进入：

```text
Phase 5:
Legacy content full restoration
```

最终目标不是减少遗留资源数量，而是：

> 让遗留资源从静态文件转化为玩家能够制作、摆放、食用、操作和收集的完整游戏内容。
