# Coffee Workshop 1.20.1 第二轮代码审计与后续开发计划

> 仓库：`CashewTeam/CoffeeWorkshop`  
> 旧版基线：`master`（Minecraft 1.12.2）  
> 目标分支：`1.20.1`（Minecraft 1.20.1 / Forge 47.4.10）  
> 审计日期：2026-07-25  
> 审计性质：在第一轮移植计划执行后的增量代码审计  
> 审计限制：本轮通过 GitHub 当前分支源码与资源进行静态审计；本地环境无法解析 GitHub 域名，因此没有重新执行 Gradle 构建、`runData`、客户端或专用服务器。所有运行时结论都必须在 Phase 0 中复验。

---

## 1. 本轮结论

与第一轮审计相比，`1.20.1` 分支已经从“可编译移植骨架”推进到“主要系统已经重建的 Alpha 版本”。

已经完成的重要工作包括：

- 机器统一为单方块 ID，并使用 `LIT` 方块状态；
- 五台机器恢复右键菜单、库存掉落、比较器和运行粒子；
- 客户端 Screen 注册从通用入口类中隔离；
- 五类自定义机器 `RecipeType`、`RecipeSerializer` 和 JEI 分类已经建立；
- 咖啡树、蓝莓、香草、蛋糕、冷萃壶和餐盘的基础交互已恢复；
- 语言文件、blockstate 路径和部分旧纹理引用已经迁移；
- 普通工作台/熔炉配方、方块掉落表和基础挖掘标签开始由 DataGen 生成；
- 大量旧版饮品和食品已重新注册；
- 村民 POI 已覆盖机器全部状态；
- 内容清单与移植决策文档已经建立。

但当前版本仍不能按“主要内容已完成”评估。最核心的问题已经从“类和资源缺失”转变为：

1. **生存模式进度链路未闭环**；
2. **机器通用加工代码存在会同时影响五台机器的边界缺陷**；
3. **自定义机器配方框架存在，但没有形成可靠、完整、可验证的配方内容层**；
4. **多杯饮品系统很可能无法按设计工作**；
5. **当前内容清单把“注册完成”误标为“完全可用”**；
6. **资源审计工具仍允许已知坏资源通过**；
7. **没有自动化测试和构建门禁证明当前版本可运行**。

因此，下一阶段不应继续优先扩大物品注册数量，而应先完成：

```text
种植咖啡树
→ 获得生咖啡豆
→ 烘焙/加工
→ 磨粉
→ 咖啡机制作饮品
→ 多次饮用
→ 返还杯具
```

这一条完整生存闭环，并为五台机器建立可重复测试。

---

## 2. 完成度定义需要重新调整

当前 `docs/CONTENT_INVENTORY.md` 主要使用“✅ 已移植”表示状态，但其中混合了多种完全不同的完成度：

- 只注册了 RegistryObject；
- 有模型和翻译；
- 可以从创造栏取得；
- 有普通合成配方；
- 机器能够加工；
- 旧版行为等价；
- 已通过客户端和服务端测试。

第二轮开始，建议改为以下五列：

| 标记 | 含义 |
|---|---|
| R | Registry 已注册 |
| A | Assets 完整：模型、贴图、语言、音效 |
| O | Obtainable：生存模式可获得 |
| B | Behavior：核心行为与设计一致 |
| T | Tested：客户端、服务端和自动化测试通过 |

只有同时达到：

```text
R + A + O + B + T
```

才能标记为“完成”。

例如，当前大量饮品可能已经满足 `R + A`，但尚未证明 `O + B + T`；不能继续显示为单一的绿色“✅”。

---

## 3. 本轮问题总表

## 3.1 P0：阻断可玩闭环或存在明显漏洞

| ID | 问题 | 影响 |
|---|---|---|
| P0-01 | 自定义机器配方未纳入 DataGen；生成缓存中只有普通配方 | 五台机器框架存在，但可能没有实际可加载配方 |
| P0-02 | 普通配方 Provider 中未建立主要咖啡饮品的生产链 | 大量饮品只能从创造栏获取 |
| P0-03 | 咖啡树成熟采摘和掉落表都只产出 `coffee_seeds` | `coffee_bean_raw` 的核心生存来源断裂 |
| P0-04 | 咖啡树使用剪刀会掉落方块物品，但不会移除原方块 | 可无限复制咖啡树 |
| P0-05 | `DrinkCoffee.finishUsingItem()` 先调用父类食物消费，再执行多杯逻辑 | 单物品堆叠可能先被消耗，导致多杯逻辑失效 |
| P0-06 | 未在已审计的注册/配方路径看到 `initCupCount()` 被调用 | 多杯饮品默认可能只记录 1 杯 |
| P0-07 | 五台机器客户端也会递减 `burnTime` | GUI 进度和 LIT 状态可能出现客户端漂移 |
| P0-08 | 输入有 crafting remainder 且堆叠数大于 1 时，整个输入槽被替换成一个容器 | 会吞掉剩余输入物品 |
| P0-09 | 机器 blockstate 忽略 `lit`，只按 `facing` 选择模型 | 机器运行时不会切换到 on 模型 |
| P0-10 | 至少 22 个模型仍引用 `blocks/anvil_base`，审计脚本将其豁免 | 可出现紫黑贴图，但审计仍返回成功 |
| P0-11 | 所有蛋糕 Loot Table 都使用 `dropSelf` | 吃掉大部分后破坏可获得完整蛋糕 |
| P0-12 | 搅拌碗和所有模具都是普通 Item，配方会直接消耗它们 | 工具循环断裂，玩法成本异常 |
| P0-13 | 配方解锁条件普遍检查“是否拥有配方结果” | Recipe Book 中大量配方无法自然解锁 |
| P0-14 | 苏打矿当前掉落自身，而旧版掉落 4–8 个苏打材料 | 矿石进度和经济行为不等价 |

## 3.2 P1：架构和稳定性问题

| ID | 问题 |
|---|---|
| P1-01 | Grinder、Roller、Oven、Icecream Machine 的 tick 代码大量复制 |
| P1-02 | 配方缓存不会在 `/reload` 后自动失效 |
| P1-03 | 输入切换到加工时间不同的新配方时，`totalCookTime` 未必同步更新 |
| P1-04 | `MachineRecipe.experience` 已序列化，但输出槽没有使用 |
| P1-05 | 所有方向都暴露同一个完整 `IItemHandler`，没有侧面输入/燃料/输出规则 |
| P1-06 | 机器配方只支持一个 Ingredient、一个 Result，扩展性不足 |
| P1-07 | Recipe Serializer 没有验证负数经验、零/负加工时间和非法输出数量 |
| P1-08 | 比较器目前表示加工进度，而不是库存充满度；需要明确这是设计修改还是错误 |
| P1-09 | 自定义机器配方没有专用 DataGen Builder 和 Provider |
| P1-10 | DataGen 没有 BlockState、ItemModel、Language、ItemTags Provider |
| P1-11 | 资源审计只检查路径和 JSON，没有检查 Registry → 资源/掉落/翻译的覆盖关系 |
| P1-12 | `docs/PORTING_DECISIONS.md` 和内容清单没有同步最新 20 个左右的开发提交 |
| P1-13 | 没有发现 CI 工作流和自动化 GameTest 基线 |
| P1-14 | 生成目录的 `.cache` 文件进入版本控制，增加无意义 diff |

## 3.3 P2：内容完整性和可延期功能

当前内容清单仍明确延期：

- 即溶咖啡杯、未开封即溶咖啡杯；
- 8 种苏打饮品；
- 5 种三明治扩展；
- 其他冰淇淋口味；
- 约 70 个旧版饮品餐盘方块；
- 可选 Mod 兼容；
- 完整语言润色；
- 旧版所有饮品和食品生产链等价恢复。

这些项目不应与 P0/P1 修复混在同一个 PR 中。应先决定：

```text
恢复
合并
重新设计
明确删除
```

然后再进入内容制作。

---

## 4. 机器系统详细审计

## 4.1 已完成部分

当前 `MachineBlock` 已经统一实现：

- `FACING`；
- `LIT`；
- 动态亮度；
- 右键打开菜单；
- 方块被替换时掉落内部物品；
- 比较器输出；
- 运行粒子和声音；
- BlockEntity 和 ticker 抽象入口。

这是正确方向，应继续保留。

## 4.2 必须改为服务端单一状态源

当前各 BlockEntity 的代码结构是：

```java
if (burnTime > 0) {
    burnTime--;
}

if (!level.isClientSide) {
    // 服务器加工逻辑
}
```

由于 Block ticker 同时在客户端和服务端注册，客户端也会自行递减 `burnTime`。

推荐两层保护同时使用：

### Block 层

```java
@Override
protected <T extends BlockEntity> BlockEntityTicker<T> createTicker(
        Level level, BlockEntityType<T> type) {
    if (level.isClientSide) {
        return null;
    }
    return createTickerHelper(...);
}
```

### BlockEntity 层

```java
public void tick(Level level, BlockPos pos, BlockState state) {
    if (level.isClientSide) {
        return;
    }
    ...
}
```

服务端通过 `ContainerData` 和 BlockState 更新同步客户端，客户端绝不自行修改加工状态。

---

## 4.3 抽取统一加工引擎

目前至少四台机器复制了相同流程：

- 消耗燃料；
- 查找配方；
- 检查输出；
- 增加 cookTime；
- 制作结果；
- 消耗输入；
- 切换 LIT；
- 保存状态。

建议新增：

```text
AbstractProcessingBlockEntity
AbstractFueledProcessingBlockEntity
```

或组合式组件：

```text
MachineProcessController
FuelPolicy
InputConsumptionPolicy
OutputPolicy
```

推荐最小结构：

```java
public abstract class AbstractProcessingBlockEntity extends MachineBlockEntity {
    protected abstract RecipeType<MachineRecipe> recipeType();
    protected abstract int inputSlot();
    protected abstract int outputSlot();
    protected abstract boolean consumesFuel();
    protected abstract int fuelSlot();

    protected boolean canProcess(MachineRecipe recipe);
    protected void process(MachineRecipe recipe);
    protected void updateLitState(boolean lit);
    protected void resetProgress();
}
```

收益：

- 五台机器共享同一套修复；
- 防止新的复制 bug；
- 更容易添加 GameTest；
- 更容易统一数据包重载；
- 更容易支持经验和侧面自动化。

---

## 4.4 修复容器返还和堆叠输入

当前模式：

```java
ItemStack container = stack.getCraftingRemainingItem();
if (!container.isEmpty()) {
    setStackInSlot(slot, container);
} else {
    stack.shrink(1);
}
```

如果输入是 16 个带容器返还的物品，会把全部 16 个替换成 1 个空容器。

正确流程应是：

1. 复制一份 consumed item；
2. 先从输入堆叠减少 1；
3. 计算一个 remainder；
4. 输入槽为空时放入 remainder；
5. 输入槽不为空时：
   - 尝试放入专用容器槽；
   - 尝试玩家/机器输出；
   - 或作为方块掉落；
6. 不允许静默吞掉任何物品。

建议提供统一方法：

```java
protected void consumeOneWithRemainder(int slot) {
    ItemStack input = itemHandler.getStackInSlot(slot);
    ItemStack one = input.copyWithCount(1);
    ItemStack remainder = one.getCraftingRemainingItem();

    input.shrink(1);

    if (!remainder.isEmpty()) {
        if (input.isEmpty()) {
            itemHandler.setStackInSlot(slot, remainder);
        } else {
            ejectRemainder(remainder);
        }
    }
}
```

---

## 4.5 配方缓存和资源重载

当前缓存只在输入槽变化时失效。

问题：

- `/reload` 删除某个配方后，只要输入不变，旧缓存仍可能继续加工；
- `/reload` 修改加工时间或结果后，旧对象仍可能被使用；
- 输入 NBT/组件变化但 Item 类型不变时，缓存判断未必充分；
- 当前加工中的配方和新配方之间没有明确切换策略。

建议：

- 最简单且安全的版本：每次准备加工时从 RecipeManager 查询；
- 或缓存 `recipeId`，每次加工开始通过 RecipeManager 重新解析；
- 监听 Tags/Recipes 更新事件，统一清空所有机器缓存；
- 保存加工中 recipe ID，输入不再匹配时重置进度；
- 数据包重载后强制重新验证。

---

## 4.6 配方改变时的加工时间

当前 `totalCookTime` 通常只在消耗燃料或开始一个自供能周期时赋值。

场景：

1. 机器仍有剩余燃料；
2. 玩家取出 200 tick 配方输入；
3. 换入 400 tick 配方；
4. 缓存失效，但 `totalCookTime` 仍可能是旧值。

应在当前配方 ID 变化时：

```java
cookTime = 0;
totalCookTime = recipe.cookingTime();
currentRecipeId = recipe.getId();
```

并在没有配方时统一重置。

---

## 4.7 输出经验

`MachineRecipe` 已经包含 `experience`，JEI 也会显示 XP，但输出槽只禁止玩家放入物品，没有实际发经验。

新增通用：

```text
SlotMachineResult
```

应负责：

- 记录取出数量；
- 按配方经验计算 XP；
- 处理小数经验概率；
- 调用 `stack.onCraftedBy`；
- 触发适当的 Forge/玩家事件；
- shift-click 时也正确发放；
- 不允许自动化提取时无条件刷玩家 XP。

需要决定：

- 漏斗提取是否不产生经验；
- 经验是否保存在机器中，玩家稍后领取；
- 还是与原版熔炉一致，在玩家取出时给予。

推荐参照原版 Furnace Result Slot 行为。

---

## 4.8 侧面自动化

当前 Capability 对所有方向返回同一个完整 Handler。

推荐：

| 方向 | 行为 |
|---|---|
| 顶部 | 输入 |
| 侧面 | 燃料或辅助输入 |
| 底部 | 输出与容器 |
| `null` | GUI 使用完整 Handler |

可使用：

```text
RangedWrapper
CombinedInvWrapper
自定义 IItemHandlerModifiable wrapper
```

必须测试：

- 漏斗不能把物品塞进输出槽；
- 漏斗不能从输入槽任意抽走正在加工的物品；
- 燃料不会进入输入槽；
- 冰淇淋机只接受冰类燃料；
- Coffee Machine 没有燃料槽时侧面规则仍正确。

---

## 5. 机器配方和生产链

## 5.1 当前框架的实际状态

当前已经有五类 RecipeType：

```text
coffeework:grinding
coffeework:coffee_brewing
coffeework:icecream_making
coffeework:rolling
coffeework:oven_baking
```

并有通用 Serializer、JEI 分类和 BlockEntity 查询逻辑。

但是当前 DataGenerators 只添加：

- 普通 RecipeProvider；
- LootTableProvider；
- BlockTagsProvider。

`ModRecipeProvider` 只生成工作台和普通熔炉配方，没有专门输出上述五类机器 JSON。生成缓存中也没有观察到这些配方。

因此 Phase 0 必须执行完整目录审计：

```bash
find src/main/resources src/generated/resources \
  -path '*recipes*' -name '*.json' \
  -exec grep -H '"type": "coffeework:' {} \;
```

如果没有手工 JSON，则当前五台机器没有任何实际配方。

---

## 5.2 新增机器配方 DataGen

新增：

```text
ModMachineRecipeProvider
MachineRecipeBuilder
```

示例：

```java
MachineRecipeBuilder.grinding(
        Ingredient.of(ModItems.COFFEE_BEAN.get()),
        new ItemStack(ModItems.COFFEE_POWDER.get()),
        0.2F,
        200
).save(writer, CoffeeWork.id("grinding/coffee_powder"));
```

目录建议：

```text
data/coffeework/recipes/machine/grinding/
data/coffeework/recipes/machine/coffee_brewing/
data/coffeework/recipes/machine/icecream_making/
data/coffeework/recipes/machine/rolling/
data/coffeework/recipes/machine/oven_baking/
```

Recipe ID 可带目录，RecipeType 仍由 JSON 中 `type` 决定。

---

## 5.3 配方 Schema 扩展

当前通用配方只有：

```json
{
  "ingredient": {},
  "result": {},
  "experience": 0.0,
  "cookingtime": 200
}
```

对于 Grinder 和 Roller 基本足够，但完整饮品生产可能需要：

- 容器；
- 基础原料；
- 调味原料；
- 水或奶；
- 多输入；
- 不消耗的工具；
- 返回容器；
- NBT 初始化。

推荐 Schema：

```json
{
  "type": "coffeework:coffee_brewing",
  "ingredients": [
    { "slot": "base", "ingredient": { "item": "coffeework:coffee_powder" }, "count": 1 },
    { "slot": "container", "ingredient": { "item": "coffeework:cup" }, "count": 1 },
    { "slot": "additive", "ingredient": { "tag": "c:milks" }, "count": 1 }
  ],
  "result": {
    "item": "coffeework:coffee_latte",
    "count": 1,
    "initialize_cups": true
  },
  "remainders": [],
  "experience": 0.2,
  "cookingtime": 200
}
```

如果坚持旧版 Coffee Machine 的单输入槽设计，也必须恢复“预组合输入物品”这一层，而不是让所有饮品只存在于 Registry 中。

建议先做设计决策：

### 路线 A：忠于旧版单输入机器

- 工作台先组合杯具与原料；
- 机器只处理一个预制输入；
- GUI 与旧版接近；
- Schema 简单。

### 路线 B：重新设计现代多槽机器

- 原料直接投入机器；
- 更符合玩家预期；
- JEI 展示更直观；
- 需要修改 GUI、菜单和 BlockEntity。

在继续制作几十种饮品前必须先冻结路线。

---

## 5.4 建立配方可达性审计

新增脚本：

```text
tools/audit_progression.py
```

输入：

- 所有注册 Item/Block；
- 普通配方；
- 机器配方；
- Loot Table；
- 作物收获；
- 世界生成；
- 村民交易；
- 初始可获得原版物品集合。

输出：

- 无任何生存来源的物品；
- 只有创造栏来源的物品；
- 循环依赖；
- 配方需要自身结果解锁；
- 工具被意外消耗；
- 机器产出没有用途；
- 注册但无模型/翻译的内容；
- 旧版有而新版缺失的内容。

该脚本至少应查出：

```text
coffee_bean_raw
大量饮品
延期的苏打/三明治/冰淇淋
```

---

## 6. 咖啡核心进度链

## 6.1 咖啡树掉落修复

当前成熟采摘只掉落 `coffee_seeds`，Loot Table 也把作物和种子都配置成 `coffee_seeds`。

推荐：

### 右键成熟采摘

```text
1–3 个 coffee_bean_raw
0–1 个 coffee_seeds
AGE 3 → AGE 1
```

受 Fortune 影响的内容仅在破坏掉落中处理，右键采摘可使用固定或少量随机产量。

### 破坏成熟树

```text
coffee_bean_raw：1–3，受 Fortune 影响
coffee_seeds：1，额外种子有概率
```

### 非成熟树

```text
coffee_seeds：0–1
```

不要再使用：

```java
createCropDrops(block, coffee_seeds, coffee_seeds, ...)
```

---

## 6.2 修复剪刀复制

当前剪刀右键：

- 掉落咖啡树 Item；
- 损耗剪刀；
- 原方块仍保留。

选择一种明确行为：

### 推荐行为

剪刀采集整个植株：

```java
level.removeBlock(pos, false);
popResource(level, pos, coffeeTreeItem);
```

或者只允许剪叶并重置年龄，但绝不能同时保留原方块和掉落完整方块。

增加 GameTest：

```text
使用剪刀一次后，世界中的咖啡树数量 + 玩家获得数量总和不增加
```

---

## 6.3 建立最小咖啡闭环

第一批机器配方只需要覆盖：

```text
coffee_bean_raw
→ oven 或普通烘焙
→ coffee_bean
→ grinder
→ coffee_powder
→ coffee_machine + cup
→ coffee_americano
→ 饮用 4 次
→ cup
```

这条链全部通过后，再添加：

- Espresso；
- Latte；
- Cappuccino；
- Macchiato；
- Mochaccino；
- 调味拿铁；
- 冰饮；
- 冷萃扩展。

---

## 7. 饮品系统

## 7.1 `finishUsingItem` 的消费顺序

当前代码先调用：

```java
super.finishUsingItem(stack, level, livingEntity);
```

随后才处理多杯 NBT。

对于带 FoodProperties 的 Item，父类流程可能已经：

- 增加饥饿值；
- 减少 ItemStack；
- 返回消费后的 Stack。

当前代码忽略父类返回值，却继续操作原 Stack，存在单件物品先被清空的高风险。

推荐不要依赖父类自动缩减：

1. 自己调用食物效果；
2. 自己触发统计和 Advancement；
3. 自己处理杯数；
4. 只有最后一杯才返回空杯；
5. Creative 不减少；
6. 服务端修改状态，客户端只播放动画。

或完整接收父类返回值，并在多杯模式下使用独立的“不可默认 shrink”消费路径。

---

## 7.2 NBT 初始化

`initCupCount()` 已存在，但在本轮审计到的物品注册和普通配方中没有看到调用。

当前无 NBT 时：

```text
remaining = 1
max = 1
```

这会使声明为 3–4 杯的饮品像单杯物品一样工作。

推荐三层防护：

### 物品惰性初始化

```java
private void ensureCupData(ItemStack stack) {
    CompoundTag tag = stack.getOrCreateTag();
    if (!tag.contains(TAG_MAX_CUPS)) {
        tag.putInt(TAG_MAX_CUPS, maxCups);
    }
    if (!tag.contains(TAG_REMAINING_CUPS)) {
        tag.putInt(TAG_REMAINING_CUPS, maxCups);
    }
}
```

在：

- `use`；
- `finishUsingItem`；
- tooltip；
- bar；
- Craft/机器输出

之前调用。

### 配方输出初始化

机器在装配结果时：

```java
ItemStack result = recipe.assemble(...);
if (result.getItem() instanceof DrinkCoffee drink) {
    DrinkCoffee.initCupCount(result, drink.getConfiguredMaxCups());
}
```

### 旧存档兼容

无 NBT 的旧 Stack 应默认恢复为该 Item 配置的 `maxCups`，而不是 1。

---

## 7.3 饮品测试矩阵

每个饮品类别至少测试：

- 第一次饮用；
- 中间一杯；
- 最后一杯；
- 空杯/玻璃返还；
- Creative 模式；
- 背包满时；
- 丢弃后重新捡起；
- 保存重进世界；
- `/give` 得到的无 NBT Stack；
- 机器制作结果；
- 配方制作结果；
- 配置关闭多杯；
- 配置关闭空杯返还；
- 效果持续时间和等级；
- 已有效果叠加；
- tooltip 与进度条。

---

## 8. 普通配方和工具返还

## 8.1 模具和搅拌碗

当前以下物品均为普通 Item：

- `mixing_bowl`；
- `cake_model`；
- `cake_model_square`；
- `cake_model_plate`；
- `small_model`；
- `mooncake_model`；
- `iron_bowl`。

而大量配方会把它们作为 Ingredient，默认会被消耗。

选择一种统一设计：

### 方案 A：永久工具

使用自定义 Item：

```java
@Override
public boolean hasCraftingRemainingItem(ItemStack stack) {
    return true;
}

@Override
public ItemStack getCraftingRemainingItem(ItemStack stack) {
    return stack.copyWithCount(1);
}
```

### 方案 B：耐久工具

每次合成损耗 1 点耐久，耐久耗尽后消失。

这更适合：

- 刀具；
- 模具；
- 搅拌碗。

但 vanilla recipe remainder 不便直接损耗耐久，可通过自定义 Recipe Serializer 实现。

### 方案 C：产物包含工具返还

使用自定义配方，在 `getRemainingItems()` 中返回工具。

推荐：

- 搅拌碗：永久或高耐久；
- 铁碗：永久；
- 蛋糕模具：耐久；
- 杯和玻璃：被饮品占用，饮尽返还。

---

## 8.2 修复 Recipe Book 解锁

当前通用 helper 使用：

```java
.unlockedBy("has_item", has(result))
```

玩家必须先拥有结果才能解锁配方。

应改为基于一个主要输入：

```java
shapeless(..., result)
    .requires(primaryIngredient)
    .unlockedBy("has_primary_ingredient", has(primaryIngredient));
```

不要在 helper 中自动使用 result。

推荐让每个配方显式声明解锁条件；或者 helper 接收：

```java
ItemLike unlockItem
```

增加 DataGen 校验：

- advancement criterion 不能只引用 recipe result；
- 每个 Recipe 必须至少有一个合理解锁输入；
- 对机器配方不需要 Recipe Book advancement，但 JEI 必须可查看。

---

## 9. 方块和 Loot 行为

## 9.1 蛋糕

当前所有蛋糕 `dropSelf`，会产生吃剩蛋糕复制完整蛋糕的问题。

推荐：

- 普通破坏不掉落；
- 或仅 `BITES=0` 时掉落；
- 若保留 Silk Touch，明确是否允许完整回收；
- `BITES>0` 时不掉落；
- 加入 Cake Slice Eaten 统计或自定义 Advancement；
- 客户端不要重复执行进食的世界修改。

还需审查 `use()` 当前客户端和服务端都调用 `eat()`。客户端预测可能修改本地方块和 FoodData，再由服务端同步纠正。推荐遵循原版 CakeBlock 的交互模式，服务端负责真实修改。

---

## 9.2 苏打矿

旧版苏打矿：

```text
掉落苏打材料
数量 4–8
支持 Fortune
```

当前：

```text
普通 Block
Loot Table dropSelf
```

推荐 Loot Table：

```text
soda：4–8
Fortune bonus
Silk Touch：掉落 soda_ore
Explosion decay
```

可以不再需要自定义 `BlockOreSoda` 类，使用 Loot Table 表达即可。

---

## 9.3 冷萃壶

基础状态已恢复，但需要补测：

- random tick 是否过快；
- `ferm=7` 完成；
- `ferm=8` 空壶；
- 破坏和 Loot Table 不重复掉落；
- 爆炸、活塞和命令替换是否触发正确掉落；
- Creative 破坏是否不应掉落；
- 水桶在填充配方中的返还；
- 完成冷萃的后续饮品获取路径；
- 保存加载；
- 客户端模型切换。

目前 Loot Table 为 noDrop，真实掉落由 `onRemove()` 处理。需要验证 Creative 和 `/setblock` 等场景，长期建议把可表达的掉落尽量放回 Loot Table。

---

## 10. 资源和模型

## 10.1 机器 LIT 模型

机器 Java 状态已经包含：

```text
facing × lit
```

共 8 个组合。

当前机器 blockstate 只匹配 facing，因此 `lit=true` 与 `lit=false` 使用同一个模型。

应生成：

```json
{
  "variants": {
    "facing=north,lit=false": { "model": "coffeework:block/grinder_off" },
    "facing=north,lit=true":  { "model": "coffeework:block/grinder_on" },
    ...
  }
}
```

如果某台机器没有独立 on 模型：

- 至少明确使用同一个模型；
- 或通过 emissive texture/overlay；
- 不能让 Java 状态与资源定义无意脱节。

新增 `ModBlockStateProvider`，不要继续手工维护五份类似 JSON。

---

## 10.2 `anvil_base`

至少一批模型仍使用：

```text
blocks/anvil_base
```

当前审计脚本把它当作人工审查项，不作为失败。

这不应继续保留到测试版。

推荐：

1. 从原始材质或旧 Minecraft 合法资源中制作 Mod 自有纹理；
2. 放入：
   ```text
   assets/coffeework/textures/block/machine_metal_dark.png
   ```
3. 批量替换全部 `anvil_base`；
4. 删除审计豁免；
5. CI 中出现任何 `blocks/...` 旧路径直接失败。

---

## 10.3 删除旧 `.lang`

`convert_lang.py` 已经完成迁移，旧 `.lang` 继续保留只会制造双源维护风险。

执行：

- 对比 JSON key 数量；
- 检查三种语言；
- 删除 `.lang`；
- 修改审计脚本，不再容忍 `.lang`；
- `orig_en_us.lang` 移入 `reference/legacy/` 或删除；
- 只维护 JSON。

---

## 10.4 扩展资源审计

当前 `audit_resources.py` 应新增：

- 每个注册 Block 是否有 blockstate；
- blockstate 是否覆盖 Java 全部可能状态；
- 每个 BlockItem 是否有 item model；
- 每个 Item 是否有 item model；
- 每个 Block/Item 是否有 `en_us` key；
- `zh_cn`、`ja_jp` 缺失 key；
- 每个 Block 是否有 Loot Table 或明确 no-drop；
- `requiresCorrectToolForDrops` 是否有 mineable tag；
- 每个模型 parent 是否存在；
- 所有自定义 texture 是否存在；
- 所有 GUI texture 是否存在；
- 所有 SoundEvent 是否在 sounds.json；
- 未引用的孤儿模型/贴图；
- 旧 drink plate 资源单独分类；
- 所有 `lit`、`age`、`bites`、`ferm` 状态覆盖；
- 任何豁免都会使报告返回 warning，发布构建中 warning 也视为失败。

---

## 11. 世界生成

当前 Soda Ore 使用自定义随机散点算法模拟矿脉。

建议改用原生数据驱动 OreFeature：

```text
OreConfiguration
ConfiguredFeature
PlacedFeature
CountPlacement
HeightRangePlacement
BiomeModifier
```

好处：

- 与现代世界生成兼容；
- 数据包可调整；
- 矿脉形状更稳定；
- 不需要手写随机聚类；
- 更容易测试和配置。

咖啡树和蓝莓也需要验证：

- 群系标签；
- 配置的“rarity”数值是否真正与描述一致；
- 生成后是否能存活；
- 世界生成不会在不合法地面放置；
- `/place feature` 可稳定测试；
- 不加载额外区块。

---

## 12. JEI

JEI 已经为五类机器建立分类，这是明显进展。

仍需补齐：

- 确认五类机器各自至少有一个实际 JSON 配方；
- 显示燃料或冷却剂；
- 显示容器返还；
- 多输入 Schema 后更新布局；
- 点击机器 GUI 箭头/配方按钮跳转；
- 确认无 JEI 时 Mod 正常启动；
- 确认专用服务器不会加载 `net.minecraft.client` 或 JEI 客户端类；
- 将 XP 文本与真实输出经验一致；
- 本地化单位和文本；
- JEI 版本放入 gradle property，避免 build.gradle 硬编码多处。

---

## 13. 文档和内容清单

## 13.1 更新 `PORTING_DECISIONS.md`

补记已经完成的决策：

- 机器 ID 合并；
- LIT 单方块；
- 客户端事件隔离；
- 自定义 RecipeType；
- 饮品采用“一种饮品一个 Item”；
- 多杯 NBT；
- Loot/Tags DataGen；
- POI 全状态；
- JEI 接入；
- 延期内容的处理原则。

## 13.2 重写 `CONTENT_INVENTORY.md`

采用：

| 内容 | R | A | O | B | T | 备注 |
|---|---:|---:|---:|---:|---:|---|

示例：

| 内容 | R | A | O | B | T | 备注 |
|---|---:|---:|---:|---:|---:|---|
| coffee_americano | ✅ | ✅ | ❌ | ⚠️ | ❌ | 无已验证机器配方，多杯待修 |
| coffee_tree | ✅ | ✅ | ⚠️ | ❌ | ❌ | 成熟产出错误，剪刀复制 |
| grinder | ✅ | ⚠️ | ✅ | ⚠️ | ❌ | LIT 模型和 tick 待修 |
| soda_ore | ✅ | ✅ | ✅ | ❌ | ❌ | 掉落行为错误 |

这比单一“✅”更能指导后续工作。

---

## 14. 测试与 CI

## 14.1 新增 Gradle 运行配置

增加：

```text
gameTestServer
```

并保留：

```text
client
server
data
```

## 14.2 GameTest

优先编写：

### 机器

- 每台机器能打开菜单；
- 服务端 tick；
- 客户端不自行 tick；
- 输入和输出；
- 燃料；
- 配方时间；
- 配方切换；
- 数据包重载；
- 保存加载；
- LIT；
- 容器返还；
- 自动化侧面；
- 输出经验；
- 破坏掉落。

### 咖啡树

- 成熟采摘产出生豆；
- 种子掉落；
- 剪刀不复制；
- 生长和骨粉；
- 非法地面自动破坏。

### 饮品

- `/give` 无 NBT 饮品初始化；
- 机器输出初始化；
- 多杯；
- 空杯；
- Creative；
- 保存加载；
- 配置开关。

### 蛋糕和冷萃

- 七口进食；
- 吃剩破坏不掉完整蛋糕；
- 发酵 0–8；
- 完成后取瓶；
- 空壶掉落。

## 14.3 CI 门禁

建议 GitHub Actions：

```bash
./gradlew compileJava
python tools/audit_resources.py
python tools/audit_progression.py
./gradlew runData
git diff --exit-code
./gradlew test
./gradlew runGameTestServer
./gradlew build
```

额外检查：

- Java 17；
- 无 JEI运行测试；
- 有 JEI运行测试；
- 专用服务器启动；
- 生成资源没有未提交变化；
- 资源 warning 为 0；
- 不提交 `src/generated/resources/.cache`。

---

## 15. 新阶段执行计划

## Phase 0：重新建立真实基线

**目标：** 证明当前分支究竟能否构建和运行，并把文档状态改成真实完成度。

任务：

- 本地执行 `compileJava`、`runData`、`runClient`、`runServer`；
- 导出日志；
- 列出全部机器配方 JSON；
- 检查五类 RecipeType 配方数量；
- 更新内容清单为 R/A/O/B/T；
- 更新 Porting Decisions；
- 建立 CI；
- 清理 generated cache；
- 保存资源审计和进度审计报告。

验收：

- 可重复构建；
- 专用服务器启动；
- CI 绿色；
- 文档不再把“已注册”写成“完全完成”。

优先级：P0  
工作量：M

---

## Phase 1：修复当前 P0 漏洞

任务：

- 修复客户端 tick；
- 抽取统一输入消费方法；
- 修复容器堆叠吞物；
- 修复咖啡树成熟掉落；
- 修复剪刀复制；
- 修复多杯消费顺序；
- 添加饮品惰性 NBT 初始化；
- 修复蛋糕掉落；
- 修复苏打矿掉落；
- 修复配方解锁条件；
- 修复模具/搅拌碗返还；
- 修复机器 LIT blockstate；
- 替换全部 `anvil_base`；
- 删除旧 `.lang`。

验收：

- 无物品复制；
- 无静默吞物；
- 咖啡核心原料可生存获得；
- 多杯饮品工作；
- 机器 on/off 视觉正确；
- 无已知缺失纹理。

优先级：P0  
工作量：L  
依赖：Phase 0

---

## Phase 2：机器核心重构

任务：

- 抽取通用 Processing Engine；
- server-only ticker；
- 统一 canProcess/process；
- Recipe ID 跟踪；
- 配方重载失效；
- 配方切换重置；
- 通用结果槽与 XP；
- 侧面 Capability；
- comparator 设计；
- 统一燃料策略；
- Serializer 参数验证；
- 机器 GameTest。

验收：

- 五台机器不再复制核心 tick；
- 同一套测试覆盖全部机器；
- `/reload` 安全；
- 自动化稳定；
- 输出经验正确。

优先级：P1  
工作量：L  
依赖：Phase 1

---

## Phase 3：机器配方与咖啡闭环

任务：

- 新增 MachineRecipeBuilder/DataProvider；
- 生成五类基础配方；
- 完成生豆→熟豆→粉→饮品；
- 饮品结果初始化 cups；
- JEI 显示；
- 移除核心流程的 direct crafting fallback；
- 建立配方可达性报告。

第一批最小内容：

- Grinder：咖啡豆、可可豆、小麦/相关材料；
- Oven：烘焙咖啡豆和基础面包；
- Roller：基础面团；
- Icecream Machine：香草冰淇淋；
- Coffee Machine：Americano、Espresso、Latte。

验收：

- 新世界不使用创造模式即可制作第一杯咖啡；
- 每台机器至少有可验证配方；
- RecipeManager 和 JEI 数量一致；
- 所有结果有用途。

优先级：P0/P1  
工作量：L  
依赖：Phase 2

---

## Phase 4：恢复食品生产语义

任务：

- 决定模具耐久；
- 恢复 Roller 流程；
- 恢复 Oven 流程；
- 恢复 Icecream Machine 流程；
- 逐步替换“直接工作台合成 fallback”；
- 检查奶桶、碗和模具返还；
- 食物营养平衡；
- 蛋糕、慕斯、提拉米苏的正确生产链。

验收：

- 机器不再只是装饰；
- 工具循环正确；
- 主要旧版食品通过对应机器生产；
- 普通配方只保留合理的基础预加工。

优先级：P1  
工作量：XL  
依赖：Phase 3

---

## Phase 5：资源和数据生成全覆盖

任务：

- BlockStateProvider；
- ItemModelProvider；
- LanguageProvider；
- ItemTagsProvider；
- MachineRecipeProvider；
- 扩展 Loot Provider；
- 扩展 audit_resources；
- 删除手工可生成资源；
- 清理孤儿 drink plate 资源；
- 处理 LIT/emissive 模型。

验收：

- 注册内容和资源 1:1；
- runData 后仓库无 diff；
- 无资源豁免；
- 无孤儿或未知用途资源。

优先级：P1  
工作量：L  
依赖：Phase 1

---

## Phase 6：延期内容决策与恢复

建议顺序：

1. 即溶咖啡杯 2 项；
2. 冰淇淋其他口味；
3. 三明治 5 项；
4. 苏打饮品 8 项；
5. 冷萃和调味饮品生产链；
6. 饮品餐盘展示系统；
7. 可选 Mod 兼容。

饮品餐盘不建议恢复为 70 个独立方块。推荐：

```text
一个 Plate Block
+ BlockEntity 保存展示 ItemStack
+ 动态渲染或 BakedModel/BER
```

这样可以展示任意饮品，并避免 70 个 Registry、Blockstate 和模型的维护负担。

优先级：P2  
工作量：XL  
依赖：核心稳定

---

## Phase 7：发布准备

任务：

- 专用服务器压力测试；
- 多人测试；
- 世界保存兼容；
- Missing Mappings；
- 旧开发存档迁移；
- JEI 可选性；
- 配置说明；
- README；
- Changelog；
- License/credits；
- 版本号；
- 发布构建；
- CurseForge/Modrinth 元数据。

验收：

- 新世界完整游玩；
- 无 P0/P1；
- CI 全绿；
- 内容矩阵中发布内容全部 R/A/O/B/T；
- 延期内容有明确路线，不伪装成完成。

---

## 16. 推荐 PR 拆分

### PR 01：第二轮基线与 CI

- 新状态矩阵；
- Porting Decisions；
- CI；
- GameTest run 配置；
- 审计脚本基线。

### PR 02：咖啡树、掉落和复制漏洞

- raw bean；
- seeds；
- shears；
- soda ore；
- cake loot。

### PR 03：饮品多杯修复

- finishUsingItem；
- NBT 初始化；
- cup return；
- config；
- tests。

### PR 04：机器 Server Tick 和输入消费

- server-only；
- remainder；
- recipe change；
- cache reload。

### PR 05：机器通用加工基类

- 去重；
- process controller；
- sided capability；
- comparator。

### PR 06：结果槽和经验

- SlotMachineResult；
- XP；
- shift-click；
- automation policy。

### PR 07：机器配方 DataGen

- Builder；
- Provider；
- JSON；
- Serializer validation；
- JEI。

### PR 08：最小咖啡闭环

- bean roast；
- grinding；
- americano/espresso/latte；
- progression audit。

### PR 09：资源 P0 清理

- LIT blockstates；
- anvil texture；
- legacy lang；
- audit strict mode。

### PR 10：工具返还和食品流程

- mixing bowl；
- molds；
- roller；
- oven；
- icecream。

### PR 11+：延期内容批次

- instant；
- icecream；
- sandwich；
- soda；
- plate display。

---

## 17. 发布前硬性验收清单

### 构建

- [ ] Java 17 构建；
- [ ] `runData`；
- [ ] `runGameTestServer`；
- [ ] `build`；
- [ ] 专用服务器启动；
- [ ] 无 JEI 启动；
- [ ] 有 JEI 启动。

### 资源

- [ ] 0 missing model；
- [ ] 0 missing texture；
- [ ] 0 `blocks/anvil_base`；
- [ ] 0 `.lang`；
- [ ] 所有 LIT 状态有模型；
- [ ] 所有 Item 有模型和 en_us；
- [ ] zh_cn/ja_jp 缺项有报告；
- [ ] 所有 Block 有 Loot 或明确 no-drop。

### 进度

- [ ] 生咖啡豆可获得；
- [ ] 熟咖啡豆可获得；
- [ ] 咖啡粉可获得；
- [ ] 杯具可获得；
- [ ] Coffee Machine 有配方；
- [ ] 第一杯咖啡可生存制作；
- [ ] 多杯饮用；
- [ ] 返还杯具；
- [ ] 无循环依赖。

### 机器

- [ ] 五台菜单；
- [ ] 五台配方；
- [ ] 五台保存加载；
- [ ] 五台 LIT；
- [ ] 五台自动化；
- [ ] 五台配方重载；
- [ ] 无客户端状态漂移；
- [ ] 无吞物；
- [ ] 无复制；
- [ ] XP 正确。

### 内容

- [ ] 内容矩阵使用 R/A/O/B/T；
- [ ] 所有发布内容达到五项；
- [ ] 所有延期内容明确标记；
- [ ] 不再使用“注册完成=移植完成”。

---

## 18. 建议立即执行的顺序

下一步不建议继续注册苏打、三明治或更多饮品。建议严格按以下顺序推进：

1. 在可联网开发机上执行完整 Gradle 基线；
2. 统计五类机器实际配方数量；
3. 修咖啡树 raw bean 和剪刀复制；
4. 修饮品多杯初始化与消费；
5. 修机器 server-only tick；
6. 修输入 remainder 吞物；
7. 修 LIT blockstate 和 anvil 贴图；
8. 修蛋糕、苏打矿和工具返还；
9. 抽取统一机器加工核心；
10. 建立机器配方 DataGen；
11. 打通第一杯 Americano；
12. 建立 GameTest 和 progression audit；
13. 再恢复完整食品和延期内容。

第二轮完成的目标应定义为：

> 在新世界中，玩家能够自然找到咖啡树，获得生咖啡豆，经过机器完成烘焙、研磨和冲泡，得到具有正确多杯状态的咖啡，饮尽后返还杯具；五台机器在客户端、专用服务器、存档加载、数据包重载和自动化环境中均不会丢物、复制或状态漂移。

达到这个标准后，Coffee Workshop 1.20.1 才真正从“内容已注册”进入“核心玩法移植完成”。

---

## 19. 本轮关键证据路径

### 机器与配方

- `src/main/java/net/langball/coffee/block/MachineBlock.java`
- `src/main/java/net/langball/coffee/block/entity/MachineBlockEntity.java`
- `src/main/java/net/langball/coffee/block/entity/GrinderBlockEntity.java`
- `src/main/java/net/langball/coffee/block/entity/CoffeeMachineBlockEntity.java`
- `src/main/java/net/langball/coffee/block/entity/IcecreamMachineBlockEntity.java`
- `src/main/java/net/langball/coffee/block/entity/RollerBlockEntity.java`
- `src/main/java/net/langball/coffee/block/entity/OvenBlockEntity.java`
- `src/main/java/net/langball/coffee/recipes/MachineRecipe.java`
- `src/main/java/net/langball/coffee/recipes/MachineRecipeSerializer.java`
- `src/main/java/net/langball/coffee/init/ModRecipeTypes.java`
- `src/main/java/net/langball/coffee/datagen/DataGenerators.java`
- `src/main/java/net/langball/coffee/datagen/ModRecipeProvider.java`

### 作物、食品和饮品

- `src/main/java/net/langball/coffee/block/BlockCoffeeTree.java`
- `src/main/java/net/langball/coffee/block/BlockVanilla.java`
- `src/main/java/net/langball/coffee/block/BlockCakeBasic.java`
- `src/main/java/net/langball/coffee/block/BlockColdBrewPot.java`
- `src/main/java/net/langball/coffee/block/BlockOreSoda.java`
- `src/main/java/net/langball/coffee/item/DrinkCoffee.java`
- `src/main/java/net/langball/coffee/init/ModItems.java`
- `src/main/java/net/langball/coffee/datagen/ModBlockLootProvider.java`

### 资源和工具

- `src/main/resources/assets/coffeework/blockstates/grinder.json`
- `src/main/resources/assets/coffeework/blockstates/coffee_machine.json`
- `src/main/resources/assets/coffeework/blockstates/icecream_machine.json`
- `src/main/resources/assets/coffeework/blockstates/roller.json`
- `src/main/resources/assets/coffeework/blockstates/oven.json`
- `src/main/resources/assets/coffeework/models/block/grinder_off.json`
- `tools/audit_resources.py`
- `docs/CONTENT_INVENTORY.md`
- `docs/PORTING_DECISIONS.md`

### JEI、客户端和世界生成

- `src/main/java/net/langball/coffee/client/ClientModEvents.java`
- `src/main/java/net/langball/coffee/compat/jei/JEICompat.java`
- `src/main/java/net/langball/coffee/compat/jei/JEIRecipeTypes.java`
- `src/main/java/net/langball/coffee/compat/jei/MachineRecipeCategory.java`
- `src/main/java/net/langball/coffee/world/SodaOreFeature.java`
- `build.gradle`
