# Coffee Workshop 1.20.1 移植代码审计与后续开发计划

> 审计对象：`CashewTeam/CoffeeWorkshop`  
> 旧版基线：`master`（Minecraft 1.12.2 / Forge 14.23.4.2764）  
> 目标分支：`1.20.1`（Minecraft 1.20.1 / Forge 47.4.10）  
> 文档日期：2026-07-25  
> 文档定位：替代当前分支中偏概念性的 `PORTING_PLAN.md`，并修正 `ASSET_PORTING_PLAN.md` 中已经过时或不准确的结论。

---

## 1. 审计范围与方法

本次审计以两个分支的注册代码、方块类、方块实体、菜单、物品、资源 JSON、语言文件、数据生成器与旧版行为实现为依据，重点检查：

1. 注册名与资源名是否形成完整映射；
2. 旧版 BlockState、模型、贴图和语言资源能否被 1.20.1 正确解析；
3. 旧版方块交互是否在新版得到等价实现；
4. 方块实体、菜单、加工状态与库存是否能够可靠保存；
5. 配方、掉落、标签、世界生成、村民和饮品系统是否具备最小可玩闭环；
6. 后续开发能否通过数据生成和自动化检查避免资源再次漂移。

本次为静态代码与资源审计，没有在本地启动客户端、专用服务器或执行 `runData`。因此，文档把“建立可重复构建基线”和“生成精确资源清单”列为第一阶段的强制任务。现有手工统计的文件数量不应继续作为唯一依据。

---

## 2. 总体结论

`1.20.1` 分支已经完成了注册系统、部分方块实体、菜单与数据配方框架的重建，但目前更接近“可编译的移植骨架”，尚未达到功能等价或稳定可玩的状态。

最主要的阻断项如下：

| 编号 | 级别 | 结论 |
|---|---|---|
| R-01 | P0 | 语言文件仍是 `.lang`，1.20.1 应使用 `en_us.json`、`zh_cn.json`、`ja_jp.json`。 |
| R-02 | P0 | 大量 blockstate 使用旧式模型引用，如 `coffeework:coffee_machine`，应解析到 `coffeework:block/coffee_machine`。 |
| R-03 | P0 | 模型内部仍引用 1.12.2 的旧版原版贴图路径和名称，如 `blocks/log_oak`、`blocks/concrete_black`。 |
| R-04 | P0 | 部分 blockstate 仍使用旧 Forge 的 `forge_marker`、`defaults` 格式。 |
| R-05 | P0 | Java 方块状态与 blockstate JSON 不一致，例如蛋糕缺少 `bites`、冷萃壶缺少 `ferm`、香草缺少 `age`。 |
| R-06 | P0 | 未发现完整的方块掉落表和挖掘标签生成流程。 |
| B-01 | P0 | 五台机器方块均缺少新版 `use(...)`，右键无法打开菜单。 |
| B-02 | P0 | 机器通过“关/开两个方块互换”表示运行状态，切换时可能移除或重建方块实体，导致库存和进度丢失。 |
| B-03 | P0 | 机器首次加工、燃料容器返还等 tick 逻辑存在明显缺陷。 |
| B-04 | P0 | 机器配方类是空的内存 Map，未形成可重载的数据包配方系统。 |
| B-05 | P1 | 输出槽位丢失旧版经验、统计与相关事件逻辑。 |
| G-01 | P1 | 蛋糕、冷萃壶、香草、餐盘、咖啡树成熟采摘等玩法被简化或完全丢失。 |
| C-01 | P1 | 饮品只移植了少量品种，多杯饮用、空杯返还和变体效果未恢复。 |
| V-01 | P1 | 村民 POI 仅包含机器默认朝向状态，其他朝向可能无法被识别为工作站。 |
| A-01 | P1 | 数据生成器仅生成普通配方，没有模型、blockstate、语言、掉落和标签 Provider。 |
| S-01 | P2 | 通用 Mod 主类直接引用客户端 GUI 类，应隔离到客户端事件订阅类。 |
| M-01 | P2 | `mods.toml` 仍包含占位 issueTracker 和 update URL。 |

推荐目标不是继续逐个修补紫黑贴图，而是先建立一套统一的资源和状态规则，再恢复核心玩法。

---

## 3. 分支和构建基线

### 3.1 旧版基线

旧版 `master` 使用：

- Minecraft 1.12.2；
- Forge 14.23.4.2764；
- ForgeGradle 2.3；
- MCP snapshot mappings；
- Java 8；
- `mcmod.info`；
- 代码注册配方、模型和 GUI。

证据路径：

- `master:build.gradle`
- `master:src/main/java/net/langball/coffee/**`

### 3.2 当前目标分支

`1.20.1` 使用：

- Minecraft 1.20.1；
- Forge 47.4.10；
- ForgeGradle 6；
- Mojang Official mappings；
- Java 17；
- `mods.toml`；
- `DeferredRegister`；
- `src/generated/resources` 作为数据生成输出目录。

证据路径：

- `1.20.1:build.gradle`
- `1.20.1:src/main/java/net/langball/coffee/CoffeeWork.java`
- `1.20.1:src/main/resources/META-INF/mods.toml`

### 3.3 应冻结的基础决定

后续开发应先确定并写入 `docs/PORTING_DECISIONS.md`：

1. Mod ID 继续使用 `coffeework`，不要混用 `coffeeworkshop`；
2. 目标 Forge 固定为 47.4.x 中经过测试的版本；
3. 开发阶段允许整理 registry ID，但整理后必须一次完成并冻结；
4. 不默认承诺把 1.12.2 世界直接升级到 1.20.1；
5. 若需要兼容当前开发分支已有世界，再为重命名项加入 Missing Mappings 迁移；
6. 资源采用现代命名空间和目录规则；
7. 机器配方采用 JSON 数据包配方，不再使用静态内存 Map。

---

## 4. 资源映射审计

## 4.1 语言文件

当前资源仍包含：

```text
assets/coffeework/lang/en_us.lang
assets/coffeework/lang/zh_cn.lang
assets/coffeework/lang/ja_jp.lang
```

目标应为：

```text
assets/coffeework/lang/en_us.json
assets/coffeework/lang/zh_cn.json
assets/coffeework/lang/ja_jp.json
```

现有语言 key 还存在命名空间混用：

```text
effect.coffeeworkshop.caffeine
entity.minecraft.villager.coffeeworkshop.coffee_barista
```

实际注册命名空间是 `coffeework`，应改为：

```text
effect.coffeework.caffeine
entity.minecraft.villager.coffeework.coffee_barista
```

此外，很多翻译值仍是 registry name 本身，例如：

```text
item.coffeework.cake_coffee=cake_coffee
item.coffeework.field_ration=field_ration
```

这些虽然不阻止加载，但属于未完成翻译。

### 处理要求

- 编写一次性转换脚本，将 `.lang` 转为 UTF-8 JSON；
- 移除旧 `.lang`，避免维护两套来源；
- 自动检查重复 key、非法 JSON、缺失 key；
- 从注册表生成必须存在的默认 key：
  - `block.coffeework.<id>`
  - `item.coffeework.<id>`
  - `container.coffeework.<id>`
  - `effect.coffeework.<id>`
  - 创造模式标签、唱片说明和村民职业；
- 以 `en_us.json` 为完整性基准，检查另外两种语言缺项；
- 未翻译项可以临时回退英文，但不能直接显示 registry name。

---

## 4.2 Blockstate 模型路径

当前部分 blockstate 写法类似：

```json
{
  "variants": {
    "facing=north": {
      "model": "coffeework:coffee_machine"
    }
  }
}
```

现代资源位置应明确为：

```json
{
  "variants": {
    "facing=north": {
      "model": "coffeework:block/coffee_machine"
    }
  }
}
```

需要批量审计所有：

```text
assets/coffeework/blockstates/*.json
```

并递归校验每个 `model` 最终是否存在于：

```text
assets/coffeework/models/block/
```

不要仅靠字符串替换。应先判断目标文件真实存在，再做改写。

---

## 4.3 旧 Forge Blockstate 格式

`coldbrew_pot.json` 等资源仍包含：

```json
"forge_marker": 1,
"defaults": {
  "model": "coffeework:coldbrew_pot"
}
```

这些属于旧版 Forge 扩展格式，应改成标准 `variants` 或 `multipart` JSON。

处理顺序：

1. 先恢复 Java 中真实存在的 BlockState 属性；
2. 从属性组合生成新的 blockstate；
3. 删除 `forge_marker` 与 `defaults`；
4. 对每个可能状态执行模型解析检查。

---

## 4.4 原版贴图路径迁移

机器和物品模型中仍存在旧引用，例如：

```text
blocks/concrete_black
blocks/iron_block
blocks/log_oak
blocks/planks_oak
blocks/anvil_base
```

这些引用至少存在两类问题：

1. 旧目录前缀为 `blocks/`，现代约定为 `block/`；
2. 旧名称发生变化，例如：
   - `log_oak` → `oak_log`
   - `planks_oak` → `oak_planks`
   - `concrete_black` → `black_concrete`
   - `anvil_base` 已不再是可直接依赖的标准纹理名。

应建立一份明确的迁移字典，而不是只把 `blocks/` 改成 `block/`。

建议脚本输出三类结果：

- 可自动迁移；
- 原版中已不存在，需要人工选择替代纹理；
- 自定义命名空间资源缺失。

自定义路径如 `coffeework:model/bag_none` 如果对应 PNG 确实存在，可以暂时保留；不需要在第一阶段把所有美术目录强制改成 `textures/block` 和 `textures/item`，以减少无意义改动。

---

## 4.5 状态定义和模型不匹配

### 咖啡树

当前 Java 使用 `AGE_7`，即 0–7 八个状态；blockstate 只提供 0–3 四个模型。

推荐恢复旧版 0–3 设计：

```java
IntegerProperty.create("age", 0, 3)
```

原因：

- 旧版玩法本来就是四阶段；
- 已有四个模型；
- 可以减少资源补作；
- 成熟采摘重置到 age 1 的旧行为更容易恢复。

另一种方案是保留 0–7，并把 4–7 都映射到成熟模型，但需要明确这是玩法调整而不是等价移植。

### 香草作物

当前 Java 类只是普通 `Block`，没有 `age`；blockstate 却定义 `age=0..7`。

推荐改为 `CropBlock`，实现：

- `getBaseSeedId()`；
- 成熟掉落；
- 农田存活；
- 随机生长；
- 骨粉；
- 0–7 模型。

### 蛋糕

当前 blockstate 使用 `bites=0..6`，Java 类没有 `BITES` 属性，也没有进食逻辑。

推荐继承或参考原版 `CakeBlock`，恢复：

- `BITES`；
- 每次右键进食；
- 不同蛋糕的营养与饱和度；
- 最后一口移除方块；
- 对 Relax 效果的旧版加成；
- 对应碰撞盒随 bites 变化。

### 冷萃壶

当前 blockstate 使用 `ferm=0..8`，Java 类没有 `FERM` 属性。

推荐恢复：

- 0–7 发酵进度；
- 8 表示已取出或空壶状态；
- random tick；
- 完成后的模型；
- 玻璃瓶取饮品；
- 取出后状态切换；
- 根据进度决定破坏掉落；
- 客户端粒子。

如果希望简化状态，可改为 BlockEntity 存储进度，但必须重新设计同步、模型和掉落，工作量反而更高。当前内容规模下继续用 BlockState 整数属性更直接。

---

## 4.6 Item Model

现有 `ASSET_PORTING_PLAN.md` 中“缺少 10 个 item model”的静态清单已经与仓库状态不完全一致，例如 `coffee_machine.json` 已存在。

不再维护手工计数，改为自动生成检查结果：

- 每个独立 Item 必须有 `models/item/<registry_id>.json`；
- 每个 BlockItem 按其 Item registry ID 查找 item model；
- BlockItem 的 ID 与 Block ID 不一致时，不能假设自动继承；
- 解析 item model 的 parent 和 texture；
- 唱片、种子、食物和特殊物品单独列出缺失纹理；
- 对模型 JSON 做语法和循环引用检查。

---

## 4.7 Loot Table 与 Tags

当前数据生成器只注册普通配方 Provider，没有看到完整的 Loot Table 和 Tags Provider。

必须补齐：

- `BlockLootSubProvider`；
- `BlockTagsProvider`；
- `ItemTagsProvider`；
- 机器和普通方块的 self-drop；
- 作物成熟/未成熟掉落；
- 蛋糕按设计掉落或不掉落；
- 冷萃壶根据状态掉落；
- `minecraft:mineable/pickaxe`；
- 需要的工具等级标签；
- 作物/种子相关标签；
- Forge 通用材料标签。

尤其是使用 `requiresCorrectToolForDrops()` 的机器和矿石，如果没有正确标签和掉落表，可能出现使用任何工具都不掉落的情况。

---

## 5. 注册名和资源名规范化

当前机器命名存在明显不一致：

| 类型 | 当前 Block ID | 当前 Item ID |
|---|---|---|
| Grinder | `grinder_off` | `grinder_off` |
| Coffee Machine | `coffeemachine_off` | `coffee_machine` |
| Ice Cream Machine | `icecreammachine_off` | `icecream_machine` |
| Roller | `roller_off` | `roller` |
| Oven | `oven_off` | `oven_off` |

推荐统一为单一 ID：

```text
grinder
coffee_machine
icecream_machine
roller
oven
```

运行状态通过 `LIT` 属性表示，不再注册 `_off` / `_on` 两个方块。

这样可以同时解决：

- Block、BlockItem、blockstate、模型和语言 key 不一致；
- 方块实体在切换开关状态时丢失；
- Loot Table 需要为两个方块重复维护；
- 村民 POI 需要枚举两个方块；
- 世界存档中机器不断被替换；
- 菜单有效性要接受两种方块；
- 创造栏和配方 ID 混乱。

若必须兼容当前开发存档，可在此次重命名 PR 中加入 Missing Mappings，将旧 ID 映射到新 ID。因为 1.20.1 分支尚在开发，建议现在一次性完成，之后冻结 registry ID。

---

## 6. 机器系统审计

## 6.1 菜单无法打开

旧版机器在 `onBlockActivated` 中调用 GUI；新版机器方块只保留：

- 朝向；
- 碰撞盒；
- 方块实体创建；
- ticker。

虽然新版方块实体已经实现 `MenuProvider`，菜单也可以从 BlockPos 构造，但方块没有调用：

```java
NetworkHooks.openScreen(serverPlayer, menuProvider, pos);
```

五台机器都应在服务端 `use(...)` 中：

1. 检查玩家是 `ServerPlayer`；
2. 获取当前位置的 BlockEntity；
3. 确认它实现 `MenuProvider`；
4. 调用 `NetworkHooks.openScreen` 并写入 BlockPos；
5. 返回 `InteractionResult.sidedSuccess(level.isClientSide)`。

该修复应先于 GUI 贴图调整，因为当前 GUI 即使绘制代码正确，玩家也无法正常打开。

---

## 6.2 开关状态切换可能丢失库存

当前 `GrinderBlockEntity` 在燃烧状态变化时选择另一个 Block 并调用 `level.setBlock(...)`。旧版代码在替换方块前保存 TileEntity，并在替换后重新挂回；新版没有等价保护。

推荐重构：

```java
public static final BooleanProperty LIT = BlockStateProperties.LIT;
```

每台机器只注册一个 Block，通过：

```java
level.setBlock(pos, state.setValue(LIT, burning), Block.UPDATE_ALL);
```

切换状态。

验收标准：

- 点燃和熄灭前后 BlockEntity 对象和库存不丢失；
- 加工进度不重置；
- 菜单保持有效；
- 保存退出后重新进入状态正确；
- 漏斗和其他 Capability 不失效；
- 方块方向不变化。

---

## 6.3 tick 逻辑缺陷

### 首次加工可能立即完成

`totalCookTime` 初始为 0，当前逻辑先执行：

```java
cookTime++;
if (cookTime >= totalCookTime) {
    totalCookTime = getCookTime(input);
    ...
}
```

第一次进入加工分支时，`1 >= 0` 成立，可能立即产出。

应在开始加工或输入变化时先设置总时长，完成后再重置：

```java
if (totalCookTime <= 0) {
    totalCookTime = recipe.getCookingTime();
}

cookTime++;
if (cookTime >= totalCookTime) {
    craft();
    cookTime = 0;
    totalCookTime = nextRecipeTimeOrDefault();
}
```

### 燃料容器返还

当前代码先 `shrink(1)`，再从可能已经为空的 stack 读取 crafting remainder。水桶、岩浆桶等容器可能无法正确返还。

应先保存 remainder：

```java
ItemStack remainder = fuel.getCraftingRemainingItem();
fuel.shrink(1);
if (fuel.isEmpty() && !remainder.isEmpty()) {
    setFuelSlot(remainder);
}
```

还要处理燃料槽已有物品和 remainder 无法放回时的掉落或拒绝逻辑。

### 其他需要统一检查

- 输入变化后 cookTime 是否重置；
- 输出 NBT/组件是否严格匹配；
- 输出数量是否会超过最大堆叠；
- 配方结果是否被安全复制；
- 只在服务端修改库存；
- 每次修改后调用 `setChanged()`；
- 必要时发送 block update；
- 燃料槽只接受合法燃料；
- 自动化侧面输入输出规则；
- chunk 卸载和重新加载；
- 多人同时打开菜单。

---

## 6.4 破坏、掉落和比较器

旧版机器包含：

- 破坏时掉落内部库存；
- 比较器输出；
- 运行粒子和声音；
- pick block 返回关闭状态机器；
- 自定义名称。

新版至少需要恢复：

```java
@Override
public void onRemove(...) {
    if (state.getBlock() != newState.getBlock()) {
        Containers.dropContents(...);
        level.updateNeighbourForOutputSignal(...);
        super.onRemove(...);
    }
}
```

如果库存使用 `ItemStackHandler`，可用辅助方法遍历并 `popResource`。

同时实现：

- `hasAnalogOutputSignal`；
- `getAnalogOutputSignal`；
- 客户端 `animateTick`；
- 运行状态的粒子和音效；
- 机器物品始终掉落统一 ID；
- 方块实体 Capability 在 `onLoad`、`invalidateCaps`、必要时 `reviveCaps` 中正确维护。

---

## 6.5 输出槽位

新版 `SlotGrinderOutput` 只禁止放入物品，旧版还负责：

- 记录取出数量；
- 给予经验；
- 调用物品制作回调；
- 触发玩家熔炼事件。

后续有两种实现路线：

1. 恢复类似 `FurnaceResultSlot` 的行为；
2. 直接参考或复用原版 `FurnaceResultSlot` 的设计，按自定义配方经验值发放。

推荐机器配方 JSON 包含：

```json
{
  "type": "coffeework:grinding",
  "ingredient": { "item": "coffeework:coffee_bean" },
  "result": {
    "item": "coffeework:coffee_powder",
    "count": 1
  },
  "experience": 0.2,
  "cookingtime": 200
}
```

---

## 7. 机器配方系统

当前五类机器配方是 singleton + `Map<ItemStack, ItemStack>`。这会导致：

- 配方默认为空；
- 不能通过数据包重载；
- 不能被 JEI 正常枚举；
- 缺少烹饪时间和经验；
- 对 NBT/组件匹配不清晰；
- 服务器和客户端同步难以保证。

推荐为以下机器注册自定义 `RecipeType` 和 `RecipeSerializer`：

```text
coffeework:grinding
coffeework:coffee_brewing
coffeework:icecream_making
coffeework:rolling
coffeework:oven_baking
```

每种配方至少支持：

- 一个或多个 Ingredient；
- 输出 ItemStack；
- 数量；
- 加工时间；
- 经验；
- 可选容器返还；
- 可选条件或标签输入。

BlockEntity 每 tick 从：

```java
level.getRecipeManager()
```

查询配方，并缓存当前 recipe ID。资源重载后缓存应能失效。

### 最小可玩配方集

第一轮不要一次恢复全部旧内容，先确保：

- 咖啡豆 → 咖啡粉；
- 可可豆 → 可可粉；
- 基础面团加工；
- 基础烘焙；
- 基础咖啡饮品；
- 基础冰淇淋；
- 至少一条完整的“种植 → 加工 → 制作 → 饮用”链路。

随后再逐步恢复旧版完整内容。

---

## 8. 非机器方块交互

## 8.1 蛋糕

恢复优先级：P1。

任务：

- 使用 `BITES` 属性；
- 每种蛋糕恢复营养值；
- 动态碰撞盒；
- 右键进食；
- 最后一口移除；
- Relax 效果加成；
- creative 和饥饿状态行为；
- 对应 0–6 模型；
- 正确的 pick block 和掉落策略。

建议将不同蛋糕参数抽成构造参数或数据定义，不要复制多份类。

---

## 8.2 冷萃壶

恢复优先级：P1。

任务：

- `FERM` 0–8；
- 仅在合法环境和随机刻下推进；
- 完成状态模型；
- 玻璃瓶交互；
- 产出冷萃饮品；
- 取出后变为空壶；
- 进度相关破坏掉落；
- 粒子；
- 明确服务器与客户端职责；
- 加入 GameTest 验证进度和取瓶。

---

## 8.3 咖啡树

当前生长存在，但旧版成熟采摘行为未恢复。

推荐：

- 状态范围恢复为 0–3；
- 成熟右键掉落咖啡豆或种子；
- 采摘后重置为 age 1；
- 明确剪刀行为是否保留；
- 实现 `canSurvive` 和邻居更新；
- 使用 Forge 作物生长钩子，便于兼容；
- 校验自然生成状态是否合理。

---

## 8.4 香草

当前不是作物。

推荐直接改为 `CropBlock`：

- 0–7 生长；
- 农田存活；
- 骨粉；
- 成熟产出香草和种子；
- 破坏掉落表；
- 对应八阶段模型；
- 种子使用原版 `ItemNameBlockItem` 或等价实现，减少自定义种植代码。

---

## 8.5 蓝莓灌木

当前已经实现年龄、生长和成熟采摘，是移植完成度较高的方块。

仍需验证：

- 存活地面；
- 掉落表；
- 碰撞与减速是否需要旧版行为；
- 骨粉；
- 模型四阶段；
- 世界生成；
- 服务器/客户端重复掉落风险。

---

## 8.6 餐盘

旧版右键会移除并返还餐盘，新版只剩朝向和碰撞盒。

需要先决定餐盘的最终设计：

1. 纯装饰方块：右键回收；
2. 饮品展示方块：保存展示物品；
3. 取消动态杯碟系统，只保留普通装饰。

如果选择第一种，恢复简单 `use` 即可；如果选择第二种，应使用 BlockEntity 存储展示物品，不要重新注册几十个饮品盘方块。

---

## 9. 饮品系统

旧版饮品系统包含：

- 多种饮品变体；
- metadata 子类型；
- 多杯容量；
- 杯数进度条；
- 饮尽后返还空杯；
- 每个变体不同效果；
- 已有效果的叠加和增强；
- Tough As Nails 兼容。

当前只移植少量饮品，且：

- `maxCups` 未参与实际杯数消耗；
- `variantCount` 未参与选择；
- 效果逻辑可能遍历所有变体；
- 没有空杯返还；
- 多次饮用状态未保存。

### 推荐设计

在 1.20.1 上不要继续依赖 metadata，选择以下之一：

#### 方案 A：每种饮品独立 Item

优点：

- 资源和翻译简单；
- JEI 兼容简单；
- 配方清晰；
- 最稳定。

缺点：

- 注册项较多。

#### 方案 B：同类饮品共享 Item，用 NBT 保存 variant 和 remaining_cups

优点：

- 接近旧版结构；
- 注册项较少。

缺点：

- 模型 override、翻译、配方、JEI 和同步更复杂。

推荐先采用方案 A 完成最小可玩版本。多杯机制仍可通过 ItemStack NBT 保存：

```text
remaining_cups
max_cups
```

饮用后：

- 减少一杯；
- 更新耐久条显示；
- 最后一杯返还空杯；
- creative 不消耗；
- 只应用当前饮品效果；
- 正确处理当前效果叠加上限。

Tough As Nails 等兼容放到核心功能稳定后再恢复。

---

## 10. 村民、世界生成与客户端隔离

## 10.1 村民 POI

当前 POI 只加入机器的默认状态。带 `FACING` 的方块有四种状态，如果使用 `LIT` 则有八种状态。

应使用：

```java
ImmutableSet.copyOf(
    ModBlocks.COFFEE_MACHINE.get()
        .getStateDefinition()
        .getPossibleStates()
)
```

保证所有朝向和亮灭状态都能被识别。

还需要决定两个咖啡职业共享同一个 POI 是否为预期设计。

---

## 10.2 世界生成

当前已存在：

- 自定义 Feature；
- Configured Feature；
- Placed Feature；
- Forge Biome Modifier。

这部分不是首要阻断项，但需要集成测试：

- 配置概率是否与旧版语义一致；
- 是否在不合适群系生成；
- 是否生成在地表正确高度；
- 咖啡树和蓝莓能否存活；
- 矿石生成数量和高度；
- `/place feature` 测试；
- 数据包重载。

---

## 10.3 客户端代码隔离

`CoffeeWork` 当前直接 import GUI 类并注册 Screen。为降低专用服务器类加载风险，建议创建：

```text
client/ClientModEvents.java
```

并使用：

```java
@Mod.EventBusSubscriber(
    modid = CoffeeWork.MODID,
    bus = Mod.EventBusSubscriber.Bus.MOD,
    value = Dist.CLIENT
)
```

在 `FMLClientSetupEvent` 中注册 Screen 和 RenderType。

作物、透贴模型需要的渲染层也应在客户端事件中按 Block 注册，而不是误以为所有有 `FACING` 的方块都需要 `cutout`。

---

## 11. 数据生成和自动化审计

扩展 `DataGenerators`，至少注册：

- `ModBlockStateProvider`；
- `ModItemModelProvider`；
- `ModLanguageProvider` 或独立语言校验器；
- `ModBlockLootProvider`；
- `ModBlockTagsProvider`；
- `ModItemTagsProvider`；
- `ModRecipeProvider`；
- 自定义机器配方 Provider；
- 必要的 WorldGen 数据 Provider。

### 资源审计脚本

建议新增：

```text
tools/audit_resources.py
```

输出：

```text
build/reports/resource-audit.json
build/reports/resource-audit.md
```

脚本应：

1. 从 Java 源码或运行时 registry dump 获取 Block/Item ID；
2. 检查 blockstate；
3. 解析所有模型 parent；
4. 解析所有 texture；
5. 验证文件存在；
6. 检查状态属性是否覆盖；
7. 检查 BlockItem 模型；
8. 检查语言 key；
9. 检查 Loot Table；
10. 检查 tags；
11. 报告未引用资源；
12. 报告旧式 `blocks/`、`items/`、`forge_marker`；
13. 报告 `coffeeworkshop` 错误命名空间；
14. 非零错误时令 CI 失败。

### CI 最低检查

```text
./gradlew compileJava
./gradlew runData
./gradlew test
./gradlew build
```

`runData` 后工作区必须保持干净。若生成输出发生变化，CI 应失败并提示提交生成资源。

另外增加一次 dedicated server 启动冒烟测试，确认客户端类没有在服务端被加载。

---

## 12. 阶段化执行计划

## Phase 0：建立可靠基线

**目标：** 得到可重复构建、可比较、可自动发现资源错误的环境。

任务：

- 冻结 Forge/Gradle/Java 版本；
- 清理 `mods.toml` 占位 URL；
- 运行并记录 `compileJava`、`runData`、`runClient`、`runServer`；
- 保存当前缺失模型、贴图、翻译和数据包错误日志；
- 新增资源审计脚本；
- 生成 registry 清单；
- 将现有两份计划标记为历史参考，本文作为主计划。

验收：

- 编译和数据生成命令稳定；
- 报告能列出所有注册项和缺失资源；
- 专用服务器至少进入完成启动状态；
- CI 可重复执行。

工作量：M

---

## Phase 1：统一 ID 与资源链

**目标：** 清除紫黑模型、缺失翻译和无掉落方块。

任务：

- 决定并应用统一机器 ID；
- `.lang` → `.json`；
- 修复 `coffeeworkshop` 命名空间；
- blockstate 模型路径加 `block/`；
- 转换旧 Forge blockstate；
- 迁移原版贴图路径和名称；
- 修复咖啡树、香草、蛋糕、冷萃壶状态定义；
- 生成 item model；
- 生成 Loot Table；
- 生成 block/item tags；
- 配置客户端 RenderType。

验收：

- 客户端日志中没有本 Mod 的 missing model/texture；
- 所有注册方块放置后有正确模型；
- 所有注册物品在创造栏有图标和名称；
- 所有方块按设计掉落；
- 所有作物状态有模型；
- F3+T 重载资源无错误。

工作量：L  
依赖：Phase 0

---

## Phase 2：机器交互与持久化

**目标：** 五台机器可打开、可保存、不会因运行状态丢失数据。

任务：

- 单方块 + `LIT`；
- 通用 `use` 打开菜单；
- `onRemove` 掉落库存；
- 比较器；
- Capability 生命周期；
- 修复首次瞬间加工；
- 修复燃料容器；
- 修复进度重置；
- 修复输出堆叠；
- 恢复运行粒子和声音；
- 测试所有朝向；
- 测试保存加载；
- 测试多人访问；
- 测试漏斗自动化。

验收：

- 五台机器右键均能打开；
- 开关状态不替换 BlockEntity；
- 加工中退出世界后进度和库存保留；
- 破坏会掉落库存；
- 漏斗输入输出符合设计；
- 不发生首次瞬间产出；
- 燃料容器正确返还。

工作量：L  
依赖：Phase 1 中的 ID 冻结

---

## Phase 3：数据包机器配方

**目标：** 所有机器依赖 RecipeManager，而不是空的静态 Map。

任务：

- 注册五类 RecipeType/Serializer；
- 定义 JSON schema；
- BlockEntity 查询并缓存配方；
- 加工时间和经验来自配方；
- 为最小可玩链路编写配方；
- 输出槽经验；
- JEI 兼容接口预留；
- 数据包重载测试。

验收：

- `/reload` 后配方可更新；
- 删除配方 JSON 后机器不再识别；
- JEI 能枚举配方或至少已有清晰接入点；
- 不再调用旧 singleton recipe map。

工作量：L  
依赖：Phase 2

---

## Phase 4：恢复核心玩法方块

**目标：** 完成旧版最核心的非机器行为。

顺序：

1. 咖啡树成熟采摘；
2. 香草作物；
3. 蛋糕 bites 和进食；
4. 冷萃壶发酵和装瓶；
5. 餐盘最终设计；
6. 蓝莓完善。

验收：

- 每个玩法都有从放置/种植到产出的闭环；
- 所有状态保存和同步；
- 所有状态模型正确；
- 有 GameTest 或最少明确的手工测试脚本。

工作量：L  
依赖：Phase 1

---

## Phase 5：饮品和内容完整性

**目标：** 从“少量示例物品”推进到明确的内容版本。

任务：

- 制作旧版内容对照表；
- 标记：已移植、改名、合并、延期、删除；
- 恢复空杯和多杯机制；
- 修复当前饮品效果选择；
- 恢复基础饮品线；
- 再恢复冰饮、拿铁、可可等扩展；
- 恢复旧版兼容功能前先做配置开关；
- 校对全部翻译和模型。

验收：

- README 中有完整内容清单；
- 每个可获得物品都有配方、模型、翻译和用途；
- 不再存在注册但无法正常获得或使用的占位内容；
- 饮品不会错误应用其他变体的效果。

工作量：XL  
依赖：Phase 3、Phase 4

---

## Phase 6：村民、世界生成、JEI 和兼容

**目标：** 完成辅助系统并准备测试版发布。

任务：

- 修复 POI 全状态；
- 验证交易经济；
- 验证 Feature 与 Biome Modifier；
- JEI 分类；
- 恢复可选 Mod 兼容；
- 客户端隔离；
- 配置文件和默认值；
- 专用服务器测试；
- 版本号、变更日志、许可证、元数据。

验收：

- 村民能识别任意朝向工作站；
- 世界生成可配置且无数据包错误；
- JEI 显示五台机器配方；
- 无可选依赖时仍能启动；
- 客户端和服务端均可稳定加载。

工作量：L  
依赖：前述阶段

---

## 13. 推荐 PR 拆分

为了便于审阅和回归，不建议把所有修改放进一个大 PR。

### PR 01：审计与构建基线

- CI；
- 资源检查脚本；
- registry 清单；
- 元数据清理；
- 文档更新。

### PR 02：语言资源迁移

- 三个 JSON 语言文件；
- 命名空间修正；
- key 完整性检查。

### PR 03：模型和 blockstate 修复

- 模型路径；
- 原版贴图映射；
- 旧 Forge blockstate；
- 状态属性对齐；
- RenderType。

### PR 04：掉落和标签

- Loot Provider；
- Block/Item Tags；
- 作物掉落；
- 机器 self-drop。

### PR 05：机器 ID 与单方块 LIT 重构

- Registry 重命名；
- Missing Mappings；
- BlockEntityType；
- blockstate；
- item model；
- POI 基础适配。

### PR 06：机器菜单和生命周期

- `use`；
- `onRemove`；
- 比较器；
- Capability；
- tick bug；
- 测试。

### PR 07：自定义机器配方

- RecipeType；
- Serializer；
- JSON 配方；
- 经验；
- JEI 接口。

### PR 08：作物、蛋糕和冷萃壶

- 咖啡树；
- 香草；
- 蛋糕；
- 冷萃；
- 餐盘。

### PR 09：饮品内容与兼容

- 多杯；
- 空杯；
- 变体；
- 旧内容恢复；
- 可选兼容。

---

## 14. 测试矩阵

## 14.1 资源

- [ ] 每个 Block 有 blockstate；
- [ ] 每个 blockstate 的每个状态可解析模型；
- [ ] 每个模型的 parent 可解析；
- [ ] 每个 texture 可解析；
- [ ] 每个 BlockItem 有 item model；
- [ ] 每个 Item/Block 有 en_us key；
- [ ] zh_cn 和 ja_jp 无非法 JSON；
- [ ] 无 `forge_marker`；
- [ ] 无旧 `blocks/` 和 `items/` 原版路径；
- [ ] 无错误的 `coffeeworkshop` 命名空间；
- [ ] 每个方块有预期 Loot Table；
- [ ] 需要工具的方块在正确 mineable tag 中。

## 14.2 机器

对五台机器分别执行：

- [ ] 四个朝向放置；
- [ ] 右键打开；
- [ ] shift-click；
- [ ] 输入、燃料、输出限制；
- [ ] 首次加工时长；
- [ ] 燃料消耗；
- [ ] 容器返还；
- [ ] 输出堆叠；
- [ ] LIT 切换；
- [ ] 库存持久化；
- [ ] 进度持久化；
- [ ] 破坏掉落；
- [ ] 比较器；
- [ ] 漏斗；
- [ ] 两个玩家同时打开；
- [ ] chunk 卸载；
- [ ] 服务器重启；
- [ ] `/reload` 配方；
- [ ] 客户端粒子和声音。

## 14.3 作物和食品

- [ ] 咖啡树全部阶段；
- [ ] 咖啡树成熟采摘；
- [ ] 香草全部阶段；
- [ ] 蓝莓采摘；
- [ ] 骨粉；
- [ ] 非法地面自动破坏；
- [ ] 蛋糕七次 bites；
- [ ] 饥饿限制；
- [ ] Relax 效果；
- [ ] 冷萃九个状态；
- [ ] 完成后装瓶；
- [ ] 状态相关掉落。

## 14.4 环境

- [ ] 单人客户端；
- [ ] LAN；
- [ ] 专用服务器；
- [ ] 无 JEI；
- [ ] 有 JEI；
- [ ] 无可选兼容 Mod；
- [ ] 有可选兼容 Mod；
- [ ] 新世界；
- [ ] 当前开发分支旧存档迁移。

---

## 15. 完成定义

一个功能只有同时满足以下条件才算“已移植”：

1. 注册成功；
2. 模型和贴图正常；
3. 翻译正常；
4. 可通过合理方式获得；
5. 交互逻辑完整；
6. 保存和同步正确；
7. 破坏和掉落正确；
8. 专用服务器可用；
9. 有明确测试；
10. 不依赖未初始化的静态 Map 或客户端类；
11. 资源重载后仍正常；
12. 文档中的内容状态已更新。

仅仅“类已创建”或“可以编译”不能标记为已移植。

---

## 16. 建议立即开始的任务

按风险和依赖排序，下一步建议直接执行：

1. 新建 `port/1.20.1-stabilization` 工作分支；
2. 加入资源审计脚本并生成第一份报告；
3. 把三份 `.lang` 转成 JSON；
4. 修复全部 blockstate 模型路径；
5. 迁移旧原版贴图引用；
6. 决定机器统一 ID；
7. 把五台机器改成单方块 `LIT`；
8. 补 `use(...)` 打开菜单；
9. 修复方块实体首次加工和燃料 remainder；
10. 添加 Loot/Tag Provider；
11. 用 Grinder 打通第一条完整机器配方；
12. 验收后再复制到其他四台机器；
13. 恢复咖啡树、香草、蛋糕和冷萃壶；
14. 最后扩大饮品和兼容内容。

第一阶段的成功标准不是“恢复所有旧内容”，而是得到一个没有缺失资源、能够在专用服务器运行，并拥有一条完整咖啡玩法链路的 1.20.1 测试版本。

---

## 17. 关键证据索引

以下路径是本次结论的主要依据，便于后续开发者复查：

### 构建与注册

- `master:build.gradle`
- `1.20.1:build.gradle`
- `1.20.1:src/main/java/net/langball/coffee/CoffeeWork.java`
- `1.20.1:src/main/java/net/langball/coffee/init/ModBlocks.java`
- `1.20.1:src/main/java/net/langball/coffee/init/ModItems.java`
- `1.20.1:src/main/java/net/langball/coffee/init/ModBlockEntities.java`
- `1.20.1:src/main/java/net/langball/coffee/init/ModMenuTypes.java`

### 资源

- `1.20.1:src/main/resources/assets/coffeework/lang/en_us.lang`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/coffeemachine_off.json`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/grinder_off.json`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/cake_coffee.json`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/coldbrew_pot.json`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/coffee_tree.json`
- `1.20.1:src/main/resources/assets/coffeework/blockstates/vanilla_crop.json`
- `1.20.1:src/main/resources/assets/coffeework/models/block/coffee_machine.json`
- `1.20.1:src/main/resources/assets/coffeework/models/item/grinder_off.json`

### 机器

- `master:src/main/java/net/langball/coffee/block/BlockGrinder.java`
- `1.20.1:src/main/java/net/langball/coffee/block/BlockGrinder.java`
- `1.20.1:src/main/java/net/langball/coffee/block/entity/GrinderBlockEntity.java`
- `1.20.1:src/main/java/net/langball/coffee/gui/ContainerGrinder.java`
- `1.20.1:src/main/java/net/langball/coffee/gui/GuiGrinder.java`
- `master:src/main/java/net/langball/coffee/gui/slot/SlotGrinderOutput.java`
- `1.20.1:src/main/java/net/langball/coffee/gui/slot/SlotGrinderOutput.java`

### 玩法行为

- `master:src/main/java/net/langball/coffee/block/BlockColdBrewPot.java`
- `1.20.1:src/main/java/net/langball/coffee/block/BlockColdBrewPot.java`
- `master:src/main/java/net/langball/coffee/block/BlockCakeBasic.java`
- `1.20.1:src/main/java/net/langball/coffee/block/BlockCakeBasic.java`
- `master:src/main/java/net/langball/coffee/block/BlockCoffeeTree.java`
- `1.20.1:src/main/java/net/langball/coffee/block/BlockCoffeeTree.java`
- `master:src/main/java/net/langball/coffee/block/BlockVanilla.java`
- `1.20.1:src/main/java/net/langball/coffee/block/BlockVanilla.java`
- `master:src/main/java/net/langball/coffee/drinks/DrinkCoffee.java`
- `1.20.1:src/main/java/net/langball/coffee/item/DrinkCoffee.java`

### 配方和数据

- `1.20.1:src/main/java/net/langball/coffee/recipes/blocks/GrinderRecipes.java`
- `1.20.1:src/main/java/net/langball/coffee/recipes/blocks/CoffeeMachineRecipes.java`
- `1.20.1:src/main/java/net/langball/coffee/datagen/DataGenerators.java`
- `1.20.1:src/main/java/net/langball/coffee/datagen/ModRecipeProvider.java`

---

## 18. 文档维护规则

每合并一个移植 PR：

1. 更新本计划对应任务状态；
2. 更新内容对照表；
3. 附上测试结果；
4. 重新生成资源审计报告；
5. 不再手工修改“资源数量统计”，以脚本结果为准；
6. 若改变 registry ID、数据格式或玩法语义，记录到 `docs/PORTING_DECISIONS.md`；
7. 所有延期或删除的旧版功能必须明确写出原因，避免未来重复审计。
