# Coffee Workshop 1.20.1 — Phase 2 机器核心重构执行计划

> 目标分支：`1.20.1`  
> 前置条件：Phase 1 已完成，CI、DataGen、资源审计和基础构建可稳定通过  
> Phase 2 定位：统一五台机器的加工核心，解决重复逻辑、配方重载、自动化、经验与测试问题  
> 本阶段不负责：大规模新增饮品/食品内容、多输入配方重设计、完整旧版内容恢复

---

## 1. 阶段目标

Phase 2 完成后，Grinder、Coffee Machine、Icecream Machine、Roller、Oven 应共享同一套可测试的加工框架，并满足：

1. 加工逻辑仅在逻辑服务端运行；
2. 五台机器不再复制完整 tick、配方查询、输出检查和成品生成代码；
3. 输入或配方发生变化时，加工进度按统一规则处理；
4. `/reload` 后不会继续使用旧的配方对象；
5. 输出满、输入改变、燃料耗尽、容器返还等边界行为一致；
6. 顶部、侧面和底部的自动化访问受到明确限制；
7. 玩家手动取出成品时正确获得配方经验；
8. shift-click、漏斗和普通点击不会重复发放经验；
9. 五台机器均有 GameTest 或可自动运行的集成测试；
10. CI 会执行 `runGameTestServer`，机器回归会阻止合并。

---

## 2. 当前代码基线

### 已有基础

当前已经具备：

- 统一 `MachineBlockEntity`，保存物品栏、加工时间、燃烧时间和同步数据；
- 单方块 `LIT` 状态；
- 服务端 ticker 保护；
- 通用输入容器返还方法；
- 五类 `RecipeType` 与 `RecipeSerializer`；
- JEI 机器分类；
- 五台机器菜单；
- Forge `IItemHandler` Capability；
- Java 17、Forge 1.20.1 构建环境。

### 本阶段要解决的结构性问题

当前仍存在：

- Grinder、Roller、Oven、Icecream Machine 等重复实现几乎相同的 tick；
- 每台机器分别维护 `cachedRecipe`；
- 缓存只在输入槽变化时失效，数据包重载后可能持有旧对象；
- `totalCookTime` 主要在开始消耗燃料时设置，配方改变时可能沿用旧加工时间；
- 所有方向都返回同一个完整 `IItemHandler`；
- 输出槽只禁止插入，没有经验和配方使用记录；
- `MachineRecipe.experience` 已存在，但没有真正结算；
- Serializer 没有系统校验非法加工时间、经验和输出数量；
- Coffee Machine 中仍留有未使用的旧 `smeltItem()`；
- Icecream Machine 使用可变 `ItemStack` 作为静态 Map 的键；
- `build.gradle` 启用了 GameTest namespace，但尚未建立完整的 `gameTestServer` 执行链。

---

## 3. 范围边界

### Phase 2 必须完成

- 公共加工状态机；
- 通用配方解析与缓存；
- 配方变化处理；
- 通用燃料处理；
- 原子化输入消费和输出生成；
- Sided Capability；
- 通用结果槽和经验；
- 菜单公共逻辑整理；
- Serializer 校验；
- GameTest 与 CI；
- NBT 向后兼容；
- 五台机器全部迁移。

### Phase 2 暂不展开

以下内容放到 Phase 3：

- Coffee Machine 多原料槽；
- 饮品杯具、牛奶、糖浆等多输入 JSON schema；
- 大批量机器配方 DataGen；
- 完整咖啡生产内容；
- Roller、Oven 和 Icecream Machine 的全部食品配方；
- 动态餐盘展示；
- 大规模 JEI 页面重做。

Phase 2 只为这些功能提供可扩展基础。

---

## 4. 目标类结构

建议调整为：

```text
MachineBlockEntity
└── AbstractProcessingBlockEntity
    ├── AbstractFueledProcessingBlockEntity
    │   ├── GrinderBlockEntity
    │   ├── RollerBlockEntity
    │   ├── OvenBlockEntity
    │   └── IcecreamMachineBlockEntity
    └── CoffeeMachineBlockEntity
```

### 4.1 `MachineBlockEntity`

继续负责通用基础设施：

- `ItemStackHandler`；
- `ContainerData`；
- NBT 保存和加载；
- Update Tag / Update Packet；
- Capability 生命周期；
- 比较器公共接口；
- `markChangedAndSync()`；
- 输入容器返还；
- Sided Handler 创建与销毁。

不再负责具体加工规则。

### 4.2 `AbstractProcessingBlockEntity`

负责所有加工机器共有的逻辑：

```java
protected abstract RecipeType<MachineRecipe> getRecipeType();
protected abstract int getInputSlot();
protected abstract int getOutputSlot();

protected MachineRecipe resolveRecipe();
protected boolean canProcess(MachineRecipe recipe);
protected ItemStack createResult(MachineRecipe recipe);
protected void processRecipe(MachineRecipe recipe);
protected void resetProgress();
protected void onRecipeChanged(MachineRecipe oldRecipe, MachineRecipe newRecipe);
protected boolean shouldRun();
```

它统一处理：

- 配方解析；
- 活跃配方 ID；
- 加工进度；
- 输出兼容；
- 成品生成；
- 输入消费；
- 配方使用记录；
- LIT 状态；
- 数据同步。

### 4.3 `AbstractFueledProcessingBlockEntity`

负责需要燃料或冷却剂的机器：

```java
protected abstract int getFuelSlot();
protected abstract int getFuelTime(ItemStack stack);
protected abstract boolean isValidFuel(ItemStack stack);
```

统一实现：

- 开始燃烧；
- 燃料容器返还；
- `burnTime` 和 `burnTimeTotal`；
- 燃烧状态；
- 不在无法加工时消耗新燃料。

### 4.4 Coffee Machine

Coffee Machine 继续作为自供能机器：

- 没有燃料槽；
- 只在存在合法配方且输出可接受时开始；
- `LIT` 表示实际加工状态；
- 不再将 `burnTime` 伪装成燃料时间；
- GUI 不需要显示燃料进度时，相关数据固定为零。

### 4.5 Icecream Machine Fuel Policy

删除：

```java
Map<ItemStack, Integer> ICE_FUEL_REGISTRY
```

替换为明确的 Fuel Policy，例如：

```java
public interface MachineFuelPolicy {
    boolean isFuel(ItemStack stack);
    int getFuelTime(ItemStack stack);
}
```

实现：

```text
VanillaFuelPolicy
IcecreamCoolingFuelPolicy
```

冷却剂可先使用：

- `Map<Item, Integer>`；
- 或固定 Tag + 统一冷却时间；
- 后续再升级为数据包驱动。

不要继续使用可变 `ItemStack` 作为 HashMap key。

---

## 5. 加工状态规则

统一状态语义，避免五台机器各自解释。

### 5.1 状态

建议使用内部枚举：

```java
public enum MachineStatus {
    IDLE,
    WAITING_FOR_FUEL,
    PROCESSING,
    OUTPUT_BLOCKED
}
```

该状态不一定需要写入 NBT，但应可由当前数据推导，并对测试开放。

### 5.2 输入为空或无配方

行为：

- 清除活动配方 ID；
- `cookTime = 0`；
- `totalCookTime = 0`；
- 不消耗新燃料；
- 已经点燃的燃料可继续自然减少；
- Coffee Machine 立即关闭 LIT。

### 5.3 输入切换到不同配方

当 recipe ID 变化：

```text
cookTime = 0
totalCookTime = 新配方 cookingTime
activeRecipeId = 新配方 ID
```

不能沿用旧配方进度。

如果只是同一个配方的输入数量改变，可以保留当前进度。

### 5.4 输出槽阻塞

建议规则：

- 暂停 `cookTime`，不立即归零；
- 不消耗新的燃料；
- 已点燃的燃料继续减少；
- Coffee Machine 暂停并关闭 LIT；
- 输出腾空后从原进度继续。

该规则必须写入 `PORTING_DECISIONS.md`，避免以后改动时产生歧义。

### 5.5 加工完成

必须按原子顺序执行：

1. 再次确认配方仍匹配；
2. 再次确认输出能容纳完整结果；
3. 调用 `recipe.assemble(...)` 生成结果；
4. 消耗一份输入；
5. 处理 remainder；
6. 合并或写入输出；
7. 记录配方使用次数；
8. 重置本轮进度；
9. `setChanged()` 并同步。

不要直接使用 `recipe.result().copy()`，以便后续为饮品结果初始化 NBT。

---

## 6. 配方缓存与 `/reload`

### 6.1 不再缓存 Recipe 对象

当前 `cachedRecipe` 会在数据包重载后成为旧对象。

建议只缓存：

```java
@Nullable
private ResourceLocation activeRecipeId;
```

解析流程：

```java
if (activeRecipeId != null) {
    Recipe<?> byId = recipeManager.byKey(activeRecipeId).orElse(null);
    if (byId instanceof MachineRecipe recipe
            && recipe.getType() == getRecipeType()
            && recipe.matches(container, level)) {
        return recipe;
    }
    activeRecipeId = null;
}

MachineRecipe recipe = recipeManager
        .getRecipeFor(getRecipeType(), container, level)
        .orElse(null);

activeRecipeId = recipe == null ? null : recipe.getId();
return recipe;
```

收益：

- `/reload` 后通过 RecipeManager 重新拿到新对象；
- 删除配方后自动失效；
- 修改加工时间或结果后立即生效；
- 常规 tick 优先按 ID 查询，避免扫描全部配方。

### 6.2 NBT

保存：

```text
ActiveRecipe
RecipesUsed
```

加载旧世界时字段不存在应正常回退。

现有字段继续兼容：

```text
BurnTime
BurnTimeTotal
CookTime
CookTimeTotal
Items
```

本阶段不要改变 Registry ID 或菜单槽位编号。

---

## 7. 输出检查与结果生成

新增公共方法：

```java
protected boolean canAcceptResult(ItemStack result);
protected void insertResult(ItemStack result);
```

检查必须包含：

- 输出为空；
- Item 相同；
- NBT/组件相同；
- 总数量不超过 Item 最大堆叠；
- 总数量不超过槽位限制；
- 结果不是空 Stack；
- 结果数量大于零。

生成结果统一使用：

```java
recipe.assemble(container, level.registryAccess());
```

这样 Phase 3 可以通过自定义 Recipe 为咖啡初始化：

```text
remaining_cups
max_cups
```

而不需要再次重写机器核心。

---

## 8. Serializer 安全校验

`MachineRecipeSerializer#fromJson` 增加明确校验：

```text
ingredient：必须存在且非空
result.item：必须存在
result.count：1 到物品最大堆叠数
experience：有限数值且 >= 0
cookingtime：1 到合理上限
group：可选
```

建议上限：

```text
cookingtime <= 72000
```

发现非法 JSON 时抛出包含 Recipe ID 和字段名的 `JsonSyntaxException`。

Network 解码后也做防御性检查：

- 负经验归零或拒绝；
- 非法时间拒绝；
- 空结果拒绝；
- 过大数量拒绝。

建议优先拒绝非法数据，不静默修正，以便 DataGen 和 CI 尽早暴露错误。

---

## 9. Sided Capability

### 9.1 目标规则

对燃料机器：

| 方向 | 插入 | 提取 |
|---|---|---|
| UP | 输入槽 | 禁止 |
| 水平方向 | 燃料槽 | 禁止 |
| DOWN | 禁止 | 输出槽 |
| `side == null` | 完整 Handler | 完整 Handler |

Coffee Machine：

| 方向 | 插入 | 提取 |
|---|---|---|
| UP / 水平方向 | 输入槽 | 禁止 |
| DOWN | 禁止 | 输出槽 |
| `side == null` | 完整 Handler | 完整 Handler |

### 9.2 实现

为每台机器建立：

```java
LazyOptional<IItemHandler> inputHandler;
LazyOptional<IItemHandler> fuelHandler;
LazyOptional<IItemHandler> outputHandler;
```

可以基于 `RangedWrapper`，但需要额外限制：

- 输入 wrapper 禁止 extract；
- 燃料 wrapper 禁止 extract；
- 输出 wrapper 禁止 insert。

新增：

```text
InsertOnlyItemHandler
ExtractOnlyItemHandler
```

所有 Wrapper 都必须在：

```text
onLoad
invalidateCaps
reviveCaps
```

中同步管理。

### 9.3 测试要求

- 顶部漏斗不能插入燃料；
- 侧面漏斗不能插入原料；
- 底部漏斗只能取输出；
- 任何方向都不能把物品插入输出槽；
- 自动化不能抽走正在加工的输入；
- GUI 通过 `side == null` 仍能访问完整物品栏；
- 区块卸载后旧 LazyOptional 失效。

---

## 10. 输出槽与经验

新增公共：

```text
SlotMachineResult
```

替换 `SlotGrinderOutput` 以及其他机器的简单输出槽。

职责：

- 禁止放入；
- 记录本次取出数量；
- 调用 Item 的 crafted callback；
- 手动取出时结算经验；
- shift-click 时只结算一次；
- 自动化提取不直接生成经验球。

### 10.1 配方使用记录

BlockEntity 保存：

```java
Map<ResourceLocation, Integer> recipesUsed;
```

每完成一次加工：

```java
recipesUsed.merge(recipe.getId(), 1, Integer::sum);
```

玩家从结果槽取出时：

1. 根据 RecipeManager 重新解析 recipe ID；
2. 计算 `次数 × experience`；
3. 小数部分按概率产生额外 1 XP；
4. 生成经验球；
5. 清除已结算记录；
6. 触发 Recipe Award / Advancement 回调。

行为建议参照原版熔炉：

- 漏斗取出产物时，经验继续存储在机器中；
- 玩家之后手动取出任意成品时领取累计经验；
- 破坏机器不额外掉出储存经验。

该决策需要记录。

---

## 11. 菜单公共逻辑

五台菜单保留各自 GUI 坐标，但抽取：

```text
AbstractMachineMenu
```

公共内容：

- 玩家背包和快捷栏添加；
- `stillValid`；
- `getItemHandlerAt`；
- `hasRecipe`；
- shift-click 主流程；
- 结果槽处理；
- slot index 定义。

子类只声明：

```java
protected RecipeType<MachineRecipe> getRecipeType();
protected int getMachineSlotCount();
protected int getInputSlot();
protected OptionalInt getFuelSlot();
protected int getOutputSlot();
protected Block getValidBlock();
protected void addMachineSlots();
```

迁移时保持现有 GUI 坐标和槽位编号，避免 Screen 同步回归。

删除 Coffee Machine 中未使用的旧 `smeltItem()` 和其他死代码。

---

## 12. 比较器行为

当前比较器表示：

```text
cookTime / totalCookTime
```

本阶段必须明确最终设计。

推荐继续使用“加工进度输出”：

- 空闲：0；
- 加工中：1–14；
- 即将完成：15；
- 输出阻塞时保持当前进度；
- `totalCookTime <= 0` 时固定 0。

如果更希望兼容原版容器比较器语义，应改成物品栏充满度，但不要同时混用两个语义。

在 `PORTING_DECISIONS.md` 记录选择，并加入 GameTest。

---

## 13. 同步和脏标记

新增统一方法：

```java
protected void markChangedAndSync() {
    setChanged();
    if (level != null) {
        level.sendBlockUpdated(
            worldPosition,
            getBlockState(),
            getBlockState(),
            Block.UPDATE_CLIENTS
        );
        level.updateNeighbourForOutputSignal(
            worldPosition,
            getBlockState().getBlock()
        );
    }
}
```

调用时机：

- 物品栏变化；
- 进度变化到需要客户端显示时；
- LIT 变化；
- 加工完成；
- 配方变化；
- 比较器输出变化。

避免每 tick 无条件发送完整 BlockEntity update。GUI 高频数据继续通过 `ContainerData` 同步。

---

## 14. GameTest 执行计划

### 14.1 Gradle

在 `minecraft.runs` 中加入：

```groovy
gameTestServer {
    workingDirectory project.file('run')
    property 'forge.enabledGameTestNamespaces', 'coffeework'
    mods {
        coffeework {
            source sourceSets.main
        }
    }
}
```

CI 新增：

```bash
./gradlew runGameTestServer --no-daemon --stacktrace
```

Forge GameTest Server 会以失败的必需测试数量作为退出码。

### 14.2 测试目录

```text
src/main/java/net/langball/coffee/gametest/
src/main/resources/data/coffeework/structures/
```

建议：

```text
MachineGameTests.java
MachineCapabilityGameTests.java
MachinePersistenceGameTests.java
```

### 14.3 通用机器测试

为五台机器参数化或生成以下测试：

1. 有配方、有输出空间时在正确 tick 完成；
2. 无配方时不加工；
3. 输出满时不生成额外物品；
4. 输入改变为另一配方时进度归零；
5. 同配方堆叠数量改变时行为稳定；
6. 燃料机器无燃料时不加工；
7. Coffee Machine 不需要燃料；
8. LIT 在加工开始和结束时正确切换；
9. 破坏机器后库存按设计掉落；
10. 保存和重新载入后物品栏、燃料和进度一致；
11. remainder 不吞掉输入堆叠；
12. 输出数量永不超过堆叠上限；
13. 非法或删除的 recipe ID 能自动恢复；
14. 比较器输出符合决策。

### 14.4 Capability 测试

- UP 只能插入输入；
- SIDE 只能插入燃料；
- DOWN 只能提取输出；
- 输出不能通过自动化插入；
- 输入不能通过自动化提取；
- Coffee Machine 没有燃料暴露；
- Capability invalidate 后旧引用不再可用。

### 14.5 经验测试

- 普通点击取出产生正确 XP；
- shift-click 只发一次；
- 多次加工累计经验；
- 小数经验概率逻辑可通过固定 Random 或纯单元测试验证；
- 漏斗提取不直接生成经验；
- Recipe 被 `/reload` 删除后不会崩溃。

---

## 15. 实施顺序与 PR 拆分

### PR 2.1：测试基础和行为决策

内容：

- 新增 `gameTestServer`；
- 建立空测试结构；
- 添加机器测试辅助工具；
- 在 `PORTING_DECISIONS.md` 写明：
  - 输出阻塞策略；
  - 比较器语义；
  - 自动化方向；
  - 经验存储策略。

验收：

- 空 GameTest 可在 CI 运行；
- 不改现有机器行为。

### PR 2.2：Recipe Resolver 与 Serializer

内容：

- 基于 recipe ID 的解析器；
- 删除五份 `cachedRecipe`；
- 配方变化检测；
- Serializer 参数校验；
- `recipe.assemble()`；
- 单元测试。

验收：

- 输入切换更新加工时间；
- 删除或修改配方后不使用旧对象；
- 非法 JSON 在加载时清晰失败。

### PR 2.3：公共加工状态机

内容：

- `AbstractProcessingBlockEntity`；
- `AbstractFueledProcessingBlockEntity`；
- 通用输出检查；
- 通用输入消费；
- 通用 LIT；
- 通用同步；
- NBT 兼容。

先只迁移 Grinder，作为参考实现。

验收：

- Grinder 全部测试通过；
- 旧 Grinder NBT 可加载；
- 代码中不再保留 Grinder 自有完整 tick。

### PR 2.4：迁移其余四台机器

顺序：

1. Roller；
2. Oven；
3. Icecream Machine；
4. Coffee Machine。

额外处理：

- 删除 Coffee Machine 死代码；
- 替换 Icecream `Map<ItemStack, Integer>`；
- 确保 Coffee Machine 自供能状态正确。

验收：

- 五台机器共享公共 tick；
- 子类只剩槽位、RecipeType、燃料策略和菜单工厂等差异。

### PR 2.5：Sided Capability

内容：

- insert-only / extract-only wrappers；
- Direction 映射；
- Capability 生命周期；
- 漏斗 GameTest。

验收：

- 自动化规则全部通过；
- GUI 和菜单无回归。

### PR 2.6：输出经验与菜单公共层

内容：

- `SlotMachineResult`；
- `recipesUsed` NBT；
- XP 结算；
- `AbstractMachineMenu`；
- shift-click 统一；
- Advancement/Recipe Award。

验收：

- 五台机器手动取出经验正确；
- 自动化和 shift-click 不刷经验；
- 菜单槽位保持兼容。

### PR 2.7：全量回归与清理

内容：

- 五台 GameTest 全覆盖；
- 删除重复方法和未使用 imports；
- 删除旧输出槽；
- 更新架构文档；
- 更新内容矩阵；
- CI 加入 `runGameTestServer`。

验收：

- `compileJava`；
- `runData`；
- `audit_resources.py`；
- `runGameTestServer`；
- `build`；
- 全部通过。

---

## 16. 每个 PR 的审查清单

- [ ] 没有新增客户端侧机器逻辑；
- [ ] 没有直接缓存跨 reload 的 Recipe 对象；
- [ ] 没有修改 Registry ID；
- [ ] 没有改变菜单槽位编号；
- [ ] 没有无条件每 tick 发完整同步包；
- [ ] 不使用可变 ItemStack 作为 Map key；
- [ ] 所有 ItemStack 输出都使用 copy/assemble；
- [ ] 所有数量计算都检查最大堆叠；
- [ ] 所有 Capability 都正确 invalidate/revive；
- [ ] 所有新行为都有测试；
- [ ] DataGen 运行后工作区无变化；
- [ ] Dedicated Server 构建不引用客户端类。

---

## 17. Phase 2 完成定义

Phase 2 只有在以下条件全部满足时才算完成：

### 架构

- [ ] 五台机器共享同一加工状态机；
- [ ] 燃料机器共享同一燃料流程；
- [ ] Coffee Machine 只保留自供能差异；
- [ ] 不存在五份复制 tick；
- [ ] 不存在跨 `/reload` 的 Recipe 对象缓存；
- [ ] 不存在旧的 `smeltItem()` 死代码；
- [ ] Icecream Machine 不使用 `ItemStack` Map key。

### 行为

- [ ] 配方变化会正确重置加工时间；
- [ ] 输出阻塞不会吞物或复制；
- [ ] remainder 不会覆盖整个输入堆叠；
- [ ] 结果通过 `assemble()` 生成；
- [ ] 五台 LIT 状态正确；
- [ ] NBT 保存加载正确；
- [ ] 比较器行为一致。

### 自动化

- [ ] 顶部输入；
- [ ] 侧面燃料；
- [ ] 底部输出；
- [ ] 无法向输出槽插入；
- [ ] 无法从输入槽自动抽取；
- [ ] Capability 生命周期无泄漏。

### 经验和菜单

- [ ] `experience` 字段真正生效；
- [ ] 普通点击和 shift-click 经验正确；
- [ ] 自动化不直接刷经验；
- [ ] 五台菜单共享公共逻辑；
- [ ] GUI 坐标和槽位编号无回归。

### 测试

- [ ] 每台机器至少有基础加工 GameTest；
- [ ] 配方变化测试；
- [ ] 输出阻塞测试；
- [ ] 容器返还测试；
- [ ] Capability 测试；
- [ ] 保存加载测试；
- [ ] XP 测试；
- [ ] CI `runGameTestServer` 通过。

---

## 18. Phase 2 结束后的接口要求

Phase 3 应能只通过以下操作添加内容：

1. 编写机器配方 JSON；
2. 使用 DataGen Builder 生成 JSON；
3. 为结果 Item 提供必要初始化；
4. 在 JEI 中自动出现；
5. 不修改任何 BlockEntity tick；
6. 不复制菜单或经验逻辑；
7. 不为新配方添加硬编码判断。

满足这一点，才能说明 Phase 2 真正建立了可扩展的机器核心。
