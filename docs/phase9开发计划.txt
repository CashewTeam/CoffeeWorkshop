# Phase 9 开发目标计划：器具、机器与家具恢复

## 1. 阶段目标

Phase 9 的目标是恢复此前被标记为 `REMOVED`、`REDESIGN`、`TO_PORT_MACHINE` 和 `TO_PORT_DECOR` 的器具、机器与家具内容，使相关遗留资源重新获得真实的运行时用途。

本阶段主要恢复：

```text
Moka Pot
Turkish Coffee Pot
Coffee Pot
Soda Machine
Phonograph
Stone Bar Counter
Wooden Bar Counter
```

恢复标准不是简单注册一批只能摆放的空方块，也不是机械照搬 1.12.2 的类结构，而是保留旧版玩家能够观察到的核心行为：

```text
器具有明确的输入、处理和输出流程
容器在使用后正确保留或返还
机器状态会反映到模型
多杯器具能够保存剩余份数
破坏、取回和区块重载不会复制或吞掉内容
旧模型、贴图和唱片重新成为可见、可玩的内容
```

Phase 9 不要求兼容旧版存档或 metadata，但必须对旧资源逐项建立运行时所有权。所有 `moka_*`、`turkey_*`、`coffee_pot_*`、`soda_machine_*`、`phonograph` 和 `bar_*` 资源最终都必须处于以下状态之一：

```text
ACTIVE_RUNTIME_ASSET
MERGED_RUNTIME_VARIANT
EXCLUDED_LEGACY_ASSET
```

对于本阶段明确要求恢复的资源，原则上不得使用 `EXCLUDED_LEGACY_ASSET` 规避开发。

---

## 2. 旧版行为参照原则

Phase 9 的旧版参照优先级如下：

```text
旧版实际注册和交互代码
→ 旧版配方、机器输入输出和容器返还
→ 旧版 Blockstate、模型层级与资源命名
→ 当前版本的现代化设计
```

若旧版代码与遗留模型存在冲突，应优先保留玩家实际能够使用的行为；若某个资源在旧版只有模型而没有注册入口，则需要根据同系列器具的用途，将其合并到通用状态系统中。

现代化允许：

```text
使用 BlockEntity 代替多个独立状态方块
使用枚举 BlockState 代替多个旧 Registry ID
使用数据驱动 RecipeType 代替硬编码配方
使用统一容器接口处理份数和 NBT
```

现代化不得：

```text
删除旧版产出
改变器具是否可重复使用
通过直接工作台配方绕过原机器
让空杯、瓶子、壶或原料发生复制
只恢复模型而不恢复玩法入口
```

---

# 3. 公共架构

建议先建立 Phase 9 共用能力，避免 Moka Pot、Turkish Pot 和 Coffee Pot 各自实现一套杯数与液体逻辑。

## 3.1 ServingContainer

用于表示可保存多份饮品的器具：

```text
getStoredDrink()
getServings()
getCapacity()
fill()
pourOneServing()
clear()
```

建议保存：

```text
ItemStack drink
int servings
int capacity
int dataVersion
```

`ItemStack drink` 保存饮品类型和必要 NBT，但 Stack 数量始终固定为 1。实际容量由 `servings` 表示。

## 3.2 HeatSourceRegistry

新增方块标签：

```text
coffeework:moka_heat_sources
coffeework:turkish_pot_heat_sources
```

默认可包含：

```text
Campfire
Soul Campfire
Fire
未来恢复的 Stove 或其他炉具
```

是否接受普通熔炉顶部应通过标签决定，不能硬编码多个方块判断。

## 3.3 BrewingState

Moka Pot 和 Turkish Pot 可共用处理状态定义：

```text
EMPTY
LOADED
HEATING
READY
```

BlockState 只保存需要影响模型或红石查询的低频状态；原料、进度、饮品和份数保存在 BlockEntity。

## 3.4 数据保存要求

所有器具 BlockEntity 必须：

```text
保存完整输入
保存处理进度
保存剩余份数
保存朝向
支持区块卸载和服务器重启
校验非法或过期 NBT
```

非法数据不能导致区块加载崩溃。无法恢复时应掉落原料和器具，或恢复为空器具。

---

# 4. Phase 9.0：传统咖啡器具

## 4.1 Moka Pot

遗留资源：

```text
moka_bottom
moka_top
moka_pot_unheated
moka_pot_heated
```

推荐实现：

```text
MokaPotBlock
MokaPotBlockEntity
MokaPotItem
MokaBrewingRecipe
```

### 结构设计

`moka_bottom` 和 `moka_top` 应恢复为可获得的器具组件，而不是仅作为无法使用的旧模型保留。

推荐流程：

```text
制作 Moka Bottom
制作 Moka Top
Moka Bottom + Moka Top
→ Moka Pot
```

放置后的 Moka Pot 使用一个 Block 和一个 BlockEntity，不为加热前后分别注册方块。

BlockState：

```text
FACING
ASSEMBLED
HEATED
```

BlockEntity：

```text
ItemStack coffeeInput
int waterAmount
int brewProgress
int servings
boolean ready
```

### 玩家交互

推荐操作：

```text
放置 Moka Pot
→ 加入 Water
→ 加入 Coffee Powder
→ 确认上壶已装配
→ 放到有效热源上方
→ 开始加热
→ 完成后获得多份咖啡
```

倒取时：

```text
手持空 Cup 右键
→ 消耗一个 Cup
→ 获得一杯对应咖啡
→ servings - 1
```

最后一杯倒出后，Moka Pot 回到空状态，但器具本身保留。

不建议让 Moka Pot 直接吐出四个饮品物品，否则会失去“多杯服务壶”的用途。

### 状态与模型

```text
未装配或组件状态 → moka_bottom / moka_top
已装配未加热       → moka_pot_unheated
正在加热或已完成   → moka_pot_heated
```

`HEATED` 只用于模型和粒子，不直接代表一定存在饮品；真实完成状态以 BlockEntity 为准。

### 旧版一致性目标

- Moka Top 和 Bottom 都有生存获取方式；
- 水和 Coffee Powder 是必要输入；
- 必须经过热源处理；
- 壶不会在冲泡后消失；
- 一次冲泡产生多份咖啡；
- 不允许用普通工作台直接得到最终咖啡。

---

## 4.2 Turkish Coffee Pot

遗留资源：

```text
turkey_coffee_pot
turkey_coffee_pot_unheated
turkey_coffee_pot_heated
```

推荐实现：

```text
TurkishCoffeePotBlock
TurkishCoffeePotBlockEntity
TurkishBrewingRecipe
```

它与 Moka Pot 共用热源检测和份数接口，但保留独立冲泡流程。

输入：

```text
Water
Coffee Powder
可选 Sugar
```

如果旧版配方明确要求 Sugar，应作为必要输入；如果旧版允许无糖版本，则建立两条数据驱动配方。

流程：

```text
放置 Turkish Coffee Pot
→ 加水
→ 加 Coffee Powder
→ 可选加入 Sugar
→ 放到有效热源上
→ 手动等待加热
→ 使用 Cup 倒出 Turkish Coffee
```

状态：

```text
FACING
HEATED
```

BlockEntity：

```text
ingredients
brewProgress
servings
sweetened
```

Moka Pot 与 Turkish Pot 必须产出不同饮品，不能只使用相同咖啡物品并更换模型。

### 验收重点

- 加热中移除热源会暂停而不是重置；
- 重新放回热源后继续；
- 破坏时返还器具和尚未消耗的原料；
- 完成后破坏应保留饮品份数，或按明确规则掉落饮品；
- 不得同时掉落已装内容和完整满壶，避免复制。

---

## 4.3 Coffee Pot

遗留资源：

```text
coffee_pot
coffee_pot_1
coffee_pot_2
coffee_pot_3
coffee_pot_4
```

Coffee Pot 定位为通用多杯服务壶，而不是新的咖啡制作机器。

推荐实现：

```text
CoffeePotBlock
CoffeePotBlockEntity
CoffeePotItem
```

状态：

```text
FACING
LEVEL=0..4
```

BlockEntity：

```text
ItemStack storedDrink
int servings
int capacity = 4
```

模型对应：

```text
LEVEL=0 → coffee_pot
LEVEL=1 → coffee_pot_1
LEVEL=2 → coffee_pot_2
LEVEL=3 → coffee_pot_3
LEVEL=4 → coffee_pot_4
```

### 填充方式

Phase 9 初版至少支持：

```text
Moka Pot → Coffee Pot
Coffee Machine → Coffee Pot
手持多杯咖啡容器 → Coffee Pot
```

填充时必须校验饮品一致：

```text
空壶可以接收任意允许饮品
已有 Americano 的壶只能继续加入 Americano
不同饮品不能混合
```

### 倒取方式

```text
手持空 Cup 右键
→ 获得一杯 storedDrink
→ LEVEL 和 servings 同步减少
```

潜行空手右键可取回 Coffee Pot Item，并将内容写入 Item NBT。

直接破坏：

```text
掉落一个带完整 NBT 的 Coffee Pot
```

不得同时掉落壶内全部饮品，否则玩家可以通过放置和破坏进行复制。

---

# 5. Phase 9.1：Soda Machine

遗留资源：

```text
soda_machine_bottom
soda_machine_top
```

推荐实现双高方块：

```text
SodaMachineBlock
SodaMachineBlockEntity
SodaMachineMenu
SodaMachineRecipe
```

BlockState：

```text
HALF=LOWER/UPPER
FACING
ACTIVE
```

BlockEntity 只存在于 LOWER。

## 5.1 多方块规则

放置时：

```text
检查上方空间
→ 放置 LOWER
→ 自动放置 UPPER
```

破坏任意一半：

```text
移除另一半
机器物品只掉落一次
内部物品只掉落一次
```

活塞推动、爆炸和结构加载也必须维持上下半一致，不能留下幽灵上半或复制机器。

## 5.2 机器槽位

建议：

```text
Slot 0：空 Bottle
Slot 1：Soda Material
Slot 2：Flavor
Slot 3：输出
```

配方类型：

```text
coffeework:soda_making
```

示例：

```json
{
  "type": "coffeework:soda_making",
  "container": "minecraft:glass_bottle",
  "base": "coffeework:soda_material",
  "flavor": "coffeework:syrup_berry",
  "result": "coffeework:soda_berry",
  "processing_time": 200
}
```

必须恢复旧版全部苏打口味，不允许只恢复一个基础 Soda 用于通过资源审计。

## 5.3 容器规则

空 Bottle 是饮品容器的一部分：

```text
Machine 消耗 Empty Bottle
→ 产出 Filled Soda
→ 玩家饮用完成
→ 返还 Empty Bottle
```

机器不能同时返还空瓶并输出瓶装 Soda。

取消处理或破坏机器时，应返还尚未完成的空瓶和原料。

## 5.4 视觉与反馈

`ACTIVE=true` 时可提供：

```text
气泡粒子
机器声
少量光效
动画或模型状态
```

客户端效果不能参与配方判定。

---

# 6. Phase 9.2：Phonograph

恢复：

```text
phonograph
```

推荐实现：

```text
PhonographBlock
PhonographBlockEntity
PhonographRenderer（仅在需要旋转唱片时）
```

功能参照 Jukebox：

```text
手持唱片右键 → 插入并播放
空手右键     → 取出并停止
破坏         → 掉落唱片并停止
```

至少支持当前模组已有三张唱片，并兼容标准 `RecordItem`。

BlockState：

```text
FACING
HAS_RECORD
```

BlockEntity：

```text
ItemStack record
long playbackStart
```

可以复用 Minecraft 唱片播放事件，不重新实现完整音频系统。

应支持：

```text
Comparator 输出
红石或观察者状态变化
资源重载后正常播放
多人客户端同步
区块卸载后停止或恢复到合理状态
```

新增 Advancement：

```text
在 Phonograph 中播放任意 Coffee Workshop 唱片
```

Phonograph 同时作为 Barista 建筑和家具系统的装饰节点。

---

# 7. Bar Furniture

遗留资源：

```text
bar_stone_normal
bar_stone_inner
bar_wooden_normal
bar_wooden_inner
```

注册两个方块：

```text
stone_bar_counter
wooden_bar_counter
```

BlockState：

```text
FACING
SHAPE=STRAIGHT/INNER
```

不需要 BlockEntity。

## 7.1 连接规则

放置和邻居更新时，检测同材质柜台：

```text
无拐角连接 → STRAIGHT
存在垂直方向连接 → INNER
```

`FACING` 决定 Inner 模型旋转方向。

只有旧资源支持 `NORMAL` 和 `INNER`，因此初版不应自动生成没有模型依据的 `OUTER` 状态。

石质和木质柜台默认不自动互连，避免不同材质角落产生错误模型。跨材质连接可作为后续可选扩展。

## 7.2 方块行为

- 使用对应材质音效和硬度；
- 支持精准采集与正常掉落；
- 碰撞箱与柜台高度一致；
- 不阻挡柜台后方机器交互；
- 模型和粒子贴图完整；
- Creative Tab 和生存配方均可获取。

可增加木质柜台可燃、石质柜台不可燃的材质差异。

---

# 8. 开发拆分

## Phase 9.0A：公共容器与热源

- 实现 `ServingContainer`；
- 建立热源标签；
- 建立多份饮品 NBT 规范；
- 完成非法数据恢复和数量守恒工具。

## Phase 9.0B：Moka Pot 与 Turkish Pot

- 注册组件和器具；
- 实现加水、加粉、加热与倒取；
- 接入所有遗留模型；
- 完成热源暂停和继续；
- 增加对应饮品与配方。

## Phase 9.0C：Coffee Pot

- 实现容量模型；
- 支持 Moka Pot 和 Coffee Machine 填充；
- 支持倒取与带 NBT 取回；
- 验证 LEVEL 与 servings 一致。

## Phase 9.1：Soda Machine

- 注册双高机器；
- 实现菜单、槽位和 RecipeType；
- 恢复全部 Soda；
- 完成上下半完整性和瓶子返还。

## Phase 9.2A：Phonograph

- 实现唱片插入、播放和取出；
- 接入三张唱片；
- 增加 Advancement 与 Comparator。

## Phase 9.2B：Bar Furniture

- 注册两种柜台；
- 实现 Straight/Inner 自动连接；
- 完成配方、掉落和模型。

---

# 9. 自动化测试

至少新增以下 GameTest：

```text
mokaRequiresWaterAndCoffeePowder
mokaDoesNotBrewWithoutHeat
mokaPausesWhenHeatRemoved
mokaProducesExpectedServings
mokaPourPreservesPot

turkishPotProducesCorrectDrink
turkishPotReturnsReusablePot

coffeePotLevelMatchesServings
coffeePotRejectsMixedDrinks
coffeePotPickupPreservesNbt
coffeePotBreakDoesNotDuplicateContents

sodaMachinePlacesBothHalves
sodaMachineBreakingUpperDropsOnce
sodaMachineBreakingLowerDropsOnce
sodaRecipeConsumesBottleAndIngredients
sodaDrinkReturnsEmptyBottle

phonographAcceptsValidRecord
phonographRejectsInvalidItem
phonographReturnsRecordOnBreak

barCounterSelectsStraightShape
barCounterSelectsInnerShape
barCounterUpdatesAfterNeighborRemoval
```

测试必须统计世界中的 `ItemEntity`，不能只检查方块是否消失。

---

# 10. CI 与资源审计

新增：

```text
tools/audit_phase9_assets.py
```

检查：

```text
全部 Phase 9 遗留模型拥有 runtime owner
Moka 状态模型完整
Coffee Pot LEVEL=0..4 模型完整
Soda Machine 上下半模型完整
Phonograph 模型和唱片映射完整
Bar Counter 所有状态有模型
所有 Soda 配方输出唯一
所有机器 BlockEntity 只存在于正确方块
```

以下情况必须使 CI 失败：

```text
遗留 Phase 9 资源仍处于 REMOVED/REDESIGN
机器模型状态缺失
同输入对应多个机器输出
多方块上下半 Loot 重复
器具缺少生存获取来源
饮品或容器数量不守恒
客户端类被 Dedicated Server 加载
Legacy Matrix 未更新
```

---

# 11. 最终验收标准

Phase 9 封板必须满足：

1. Moka Top、Bottom、Unheated 和 Heated 资源全部进入游戏；
2. Moka Pot 必须通过水、咖啡粉和热源完成冲泡；
3. Turkish Pot 可手动加热并产出独立土耳其咖啡；
4. Coffee Pot 支持 0–4 份容量模型；
5. Coffee Pot 取回和破坏保留饮品及份数；
6. Soda Machine 为稳定的双高方块；
7. 全部旧版 Soda 口味恢复；
8. Soda 饮用后正确返还空瓶；
9. Phonograph 支持全部现有唱片；
10. 石质和木质柜台可自动形成直线与内角；
11. 所有器具和机器均有生存配方；
12. 所有容器、饮品和原料数量守恒；
13. 服务器重启后状态、进度和内容不丢失；
14. Dedicated Server 可正常启动；
15. Legacy Matrix 中 Phase 9 的 Machine 和 Decor 待处理数量归零；
16. Build、DataGen、GameTest、资源审计和 Reachability 全部通过；
17. 客户端实机确认模型、方向、声音和动画正常。

最终阶段状态：

```text
Phase 9:
traditional brewing tools restored
multi-serving coffee pot restored
soda machine and all flavors restored
phonograph playback restored
bar furniture restored
all legacy machine and decor assets runtime-owned
container economy verified
```