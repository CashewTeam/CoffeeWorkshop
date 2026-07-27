# Phase 9 Fix1 代码审计

审计范围：

```text
基线：8e98d30c4ec298b1acc38202035a9e03a3509de7
当前：4a989255755315dbb5c6aa3b56721831257bfb9a
```

## 总体判定

```text
CI / Build：PASS
上一轮 P0：部分关闭
新增 P0：1
当前 P0：2
P1：5
P2：4

Phase 9 Fix1：不通过
进入下一批功能开发：NO-GO
```

本轮 CI 已完整通过资源审计、Build、DataGen、GameTest、Reachability 和 JAR 上传，目标 SHA 也与 `4a989255` 一致。 

三个 Artifact 均已生成。

但静态代码审查发现三种 Pot 的 NBT 保存存在无限递归，现有 GameTest 没有覆盖任何 Phase 9 器具实际序列化路径。因此本次“全绿”不能作为运行时安全证明。

---

# 一、上一轮问题关闭情况

## 1. 新增饮品摆放映射已补齐

现在已经加入：

```text
coffee_turkish
soda_caramel
soda_chocolate
soda_fruit
soda_mint
soda_vanilla
soda_sakura
```

对应的 Drink Display 映射和模型。

这关闭了上一轮导致 GameTest 失败的七个 `DrinkCoffee` 缺映射问题。

**判定：✅ 关闭。**

---

## 2. Moka 和 Turkish Pot 的直接倍增已修复

Moka Pot 完成冲泡后，现在会把储存的 Espresso 调整为：

```text
remaining_cups = 1
pot servings = 4
```



Turkish Pot 也采用同样规则。

因此原来的：

```text
Moka：4 份 × 2 杯 = 8 次饮用
Turkish：4 份 × 4 杯 = 16 次饮用
```

已改为每倒出一次只得到一个剩余份数为 1 的饮品。

**判定：✅ 两种冲泡壶自身的倍增问题已关闭。**

---

## 3. 热源状态检查已修复

现在热源判断会：

```text
先检查方块是否属于热源标签
若具有 LIT 属性，则要求 LIT=true
没有 LIT 属性的 Fire、Magma 等保持有效
```

 

熄灭 Campfire 和未工作的 Furnace 不再冲泡咖啡。

**判定：✅ 关闭。**

---

## 4. Soda Machine 已返还糖浆空容器

机器现在会获取 Flavor 的 crafting remainder，并优先放回 Flavor 槽；槽位无法接受时掉落到机器附近。

因此：

```text
Flavor Syrup → syrup_empty
```

不再被机器吞掉。

**判定：✅ 关闭。**

---

## 5. Bar Counter 碰撞箱已按朝向旋转

四个方向现在拥有对应的 VoxelShape。

**判定：✅ 固定北向碰撞问题关闭。**

---

## 6. Coffee Pot 已增加基础填充入口

当前可以手持 `DrinkCoffee` 右键 Coffee Pot，将饮品剩余杯数写入壶内。

这关闭了“Coffee Pot 完全无法填充”的纯可达性问题，但当前传输算法仍有严重复制与损耗问题，不能视为完整关闭。

---

# 二、P0：三种 Pot 的 NBT 保存会无限递归

这是本轮最严重的新问题。

## Moka Pot

当前公开方法：

```java
public void saveToTag(CompoundTag tag) {
    saveAdditional(tag);
}
```

而 `saveAdditional()` 内部又调用：

```java
saveToTag(tag);
```



实际调用链：

```text
saveAdditional
→ saveToTag
→ saveAdditional
→ saveToTag
→ ...
→ StackOverflowError
```

Turkish Pot 存在完全相同的问题。

Coffee Pot 也存在同样递归。

## 触发范围

以下行为都可能触发：

```text
区块保存
服务器自动存档
BlockEntity 更新包
getUpdateTag()
破坏 Pot
潜行取回 Pot
将 Pot 内容写入 BlockEntityTag
```

三个 Pot 的 `onRemove()` 都会调用这个递归方法。例如 Moka Pot 破坏时会调用 `moka.saveToTag(beTag)`。

这意味着当前三种器具一旦实际参与保存、同步或破坏，可能直接导致服务端栈溢出。

## 正确修法

不要让类方法覆盖 `ServingContainer.saveToTag()` 后再调用 `saveAdditional()`。

推荐拆成：

```java
private void writeContents(CompoundTag tag) {
    tag.putBoolean("HasCoffee", hasCoffee);
    tag.putBoolean("HasWater", hasWater);
    tag.putInt("BrewProgress", brewProgress);
    ServingContainer.super.saveToTag(tag);
}

@Override
protected void saveAdditional(CompoundTag tag) {
    super.saveAdditional(tag);
    writeContents(tag);
}

public CompoundTag saveForItem() {
    CompoundTag tag = new CompoundTag();
    writeContents(tag);
    return tag;
}
```

Block 中改为：

```java
CompoundTag beTag = moka.saveForItem();
```

Coffee Pot、Moka Pot、Turkish Pot 必须同时修复。

**判定：P0，阻止 Fix1 通过。**

---

# 三、P0：Coffee Pot 仍会复制多杯饮品

虽然 Moka/Turkish 输出已经规范化为单份，Coffee Pot 填充时仍直接复制原始饮品 NBT。

Coffee Pot 当前执行：

```java
pot.fillFrom(held, DrinkCoffee.getRemainingCups(held));
held.shrink(1);
```



`ServingContainer.fill()` 会保存完整的 `drink.copy()`，并单独记录 servings。

倒出时又复制保存的完整饮品 Stack，只减少 Pot 的 servings，不减少输出饮品内部的 `remaining_cups`。

## 实际复制示例

将一个满杯 Americano 放入 Coffee Pot：

```text
原饮品 NBT：
remaining_cups = 4

Coffee Pot：
storedDrink.remaining_cups = 4
servings = 4
```

每次倒出得到：

```text
1 个 remaining_cups=4 的 Americano
```

总计：

```text
4 次倒出 × 每个饮品 4 次饮用
= 16 次饮用
```

## 同时存在过量损耗

假设 Coffee Pot 已有 3/4 份，再放入一个剩余 4 杯的饮品：

```text
Pot 只增加 1 份
整个 4 杯饮品却被 held.shrink(1) 删除
```

因此同一实现既可能复制，也可能吞掉多余份数。

## 正确实现

`fill()` 应返回实际接受数量：

```java
int moved = Math.min(
    DrinkCoffee.getRemainingCups(held),
    pot.getCapacity() - pot.getServings()
);
```

壶内模板必须标准化为：

```text
remaining_cups = 1
```

源饮品则只扣除 `moved`：

```text
源饮品剩余 > 0
→ 保留原 ItemStack 并更新 remaining_cups

源饮品剩余 = 0
→ 移除饮品并返还其空容器
```

不能直接 `held.shrink(1)`。

**判定：P0，饮品经济仍可复制。**

---

# 四、P1：Moka 与 Turkish Pot 第二批会瞬间完成

完成第一批冲泡后：

```text
brewProgress = MAX_BREW_TIME
```

倒完最后一份时，`ServingContainer.clear()` 只清除：

```text
storedDrink
servings
```

不会重置 `brewProgress`。

Moka 的 `pourServing()` 也没有在最后一份后重置进度。

之后重新加入水和咖啡粉：

```text
brewProgress 已经是 400
serverTick 执行 brewProgress++
brewProgress >= 400
→ 约 1 Tick 完成下一批
```

对应判断与完成逻辑位于： 

Turkish Pot 同样会从 500 继续增长。

## 修复

最后一份被倒出时：

```java
if (isEmpty()) {
    brewProgress = 0;
    hasCoffee = false;
    hasWater = false;
}
```

也可以覆盖 `clear()`，统一重置冲泡状态。

**判定：P1，后续批次绕过加热时间。**

---

# 五、P1：Coffee Pot 的容器语义不正确

Coffee Pot 当前接受所有 `DrinkCoffee`，但倒出时只接受普通陶瓷 `CUP`。

因此它也可以接受：

```text
玻璃杯茶饮
冰咖啡
瓶装 Soda
Cold Brew
```

但倒出这些饮品仍会消耗普通 Cup。

例如：

```text
Soda Bottle 放入 Coffee Pot
→ Soda Item 被删除
→ 使用普通 Cup 倒出
→ 得到瓶装 Soda
```

这会额外吞掉普通 Cup。

手持饮品填充时，也没有返还源饮品原本的：

```text
Cup
Glass Cup
Glass Bottle
```

## 推荐规则

Coffee Pot 应二选一：

1. 只允许指定的热咖啡 Tag，例如：

```text
coffeework:coffee_pot_drinks
```

并统一使用普通 Cup；

2. 根据 `DrinkCoffee#getEmptyCupItem()` 判断倒出时要求的容器类型。

此外，填充完成后必须返还源饮品容器。

当前也没有直接实现计划中的：

```text
Moka Pot → Coffee Pot
Coffee Machine → Coffee Pot
```

只有手持成品饮品转移。

**判定：P1。**

---

# 六、P1：Phonograph 仍无法正确区分唱片声音

Phonograph 现在增加了播放和停止事件，这是进步。

但播放代码为：

```java
level.levelEvent(1010, worldPosition, 0);
```

并且局部变量 `recordItem` 没有用于事件数据。

所有唱片都发送相同的 `data=0`，客户端没有获得当前唱片对应的 Item ID，因此无法可靠地区分三张唱片并播放正确音乐。

`tickCount` 虽然被保存，但没有任何 Ticker 增加它，也没有：

- 唱片时长管理；
- 播放完成；
- 区块加载后恢复；
- 自动停止逻辑。

 

## 修复

播放事件应传递当前 Record Item：

```java
level.levelEvent(
    1010,
    worldPosition,
    Item.getId(record.getItem())
);
```

并决定：

```text
区块重载后恢复播放
或明确停止播放
```

当前实现只达到了“保存唱片并发出 Jukebox 事件”，还没有完成三个曲目的真实播放闭环。

**判定：P1。**

---

# 七、P1：带内容 Coffee Pot 重新放置后模型仍显示空壶

取回 Coffee Pot 时会尝试把内容写入 `BlockEntityTag`。

重新放置时，方块状态始终从：

```text
LEVEL=0
```

开始。

BlockEntity 加载 NBT 后只调用 `loadFromTag()`，没有把 BlockState 的 `LEVEL` 更新为保存的 servings。

所以修复递归后仍会出现：

```text
壶内实际有 4 份饮品
模型显示 LEVEL=0 空壶
第一次倒出后突然跳到 LEVEL=3
```

## 修复

在 `onLoad()` 或首次服务端 Tick 中同步：

```java
state.setValue(CoffeePotBlock.LEVEL, getFillLevel())
```

也可以在 Item 中额外保存 `BlockStateTag`，但 BlockEntity 加载后统一同步更可靠。

**判定：P1。**

---

# 八、P1：CI 仍没有 Phase 9 行为测试

本轮 GameTest 已转绿，但 Fix1 没有新增 Moka、Turkish、Coffee Pot、Soda Machine、Phonograph 或 Bar Counter 的专用测试。

因此 CI 没有执行：

```text
Pot NBT 序列化
Pot 破坏与取回
Coffee Pot 填充和倒出
第二轮冲泡
Phonograph 不同唱片播放
Soda syrup_empty 返还
```

这也是 NBT 无限递归能够在 CI 全绿状态下进入代码的原因。

至少需要新增：

```text
potNbtSerializationDoesNotRecurse
mokaProducesFourSingleServings
turkishProducesFourSingleServings
secondMokaBatchRequiresFullBrewTime
coffeePotTransferConservesServings
coffeePotPartialFillPreservesSourceDrink
coffeePotPickupRestoresLevel
sodaMachineReturnsSyrupEmpty
phonographPassesCorrectRecordId
```

**判定：P1，自动化门禁仍未建立。**

---

# 九、P2：单份饮品 UI 会显示为“已喝过”

Moka 输出的 Espresso 配置仍然是 `max_cups=2`，但被写成 `remaining_cups=1`。

Turkish Coffee 则是：

```text
max_cups=4
remaining_cups=1
```

因此耐久条会显示为：

```text
Espresso：剩一半
Turkish Coffee：剩四分之一
```

虽然实际只能饮用一次，但视觉上像是已经被喝过。

可增加单份标志，或让单份输出拥有一致的：

```text
remaining_cups=1
max_cups=1
```

并相应调整 Drink Display 合法性验证。

---

# 十、P2：Soda Machine 输入和 ACTIVE 状态仍较粗糙

Soda Machine 的三个输入槽仍允许任意物品：

```java
case SLOT_BOTTLE, SLOT_SODA, SLOT_FLAVOR -> true;
```



还存在：

- Shift-click 会尝试将任意物品塞入输入槽；
- `experience` 字段未使用；
- 只更新下半方块的 `ACTIVE`；
- 上半方块的 ACTIVE 不同步；
- ACTIVE=true/false 暂无视觉差异。

这些不影响基础配方完成，但不适合 Phase 9 封板。

---

# 十一、P2：Bar Counter 的 Inner 判断仍可能误判

碰撞方向已修复，但 Inner 状态只检查右侧邻居，并且条件只是：

```java
rightState.getValue(FACING) != facing
```



邻居朝向只要“不同”就会形成 Inner，包括某些相反或不构成拐角的摆放。

应明确检查邻居是否是正确的正交方向，并测试左右两类拐角。

---

# 十二、P2：NBT 校验仍不完整

`ServingContainer.loadFromTag()` 已增加 servings 范围截断，这是进步。

但仍未验证：

- `StoredDrink` 是否是 `DrinkCoffee`；
- StoredDrink 是否允许进入该容器；
- NBT 的 max/remaining 是否合理；
- 没有 StoredDrink 标签时是否清除旧客户端缓存；
- Coffee Pot 中 storedDrink 是否已规范化为单份模板。

---

# 十三、审计报告仍无法识别机器产出

Content Manifest 当前为：

```text
406 个 Item
405/406 有 Survival Source
```



唯一缺失项仍是：

```text
coffee_turkish → Source：—
```



它实际上可以由 Turkish Pot 产出，说明 Manifest 尚未识别 BlockEntity/Machine 输出。

Legacy Matrix 同时显示：

```text
Runtime covered：611/639
TO_PORT_MACHINE：0
TO_PORT_DECOR：0
```



这两个报告目前只能证明资源和注册所有权完成，不能证明 Phase 9 行为完成。

---

# 十四、Phase 9 完成度更新

| 子系统 | 第一轮 | Fix1 | 判定 |
|---|---:|---:|---|
| 新饮品摆放 | 0% | 100% | 已关闭 |
| Moka Pot | 45% | 65% | 单份修复，但 NBT 与二次冲泡阻塞 |
| Turkish Pot | 40% | 65% | 同上 |
| Coffee Pot | 10% | 45% | 可填充，但存在严重倍增 |
| Soda Machine | 65% | 78% | 容器返还完成 |
| Phonograph | 20% | 45% | 有事件，但未正确指定唱片 |
| Bar Furniture | 65% | 78% | 碰撞修复，角落判断待完善 |
| Phase 9 GameTest | 10% | 15% | 几乎没有专用测试 |

建议阶段状态：

```text
Phase 9 Fix1:
CI GREEN
RUNTIME UNSAFE

Phase 9 implementation:
BLOCKED BY POT SERIALIZATION

Phase 9 economy:
BLOCKED BY COFFEE POT DUPLICATION
```

# 下一轮修复顺序

## P0

1. 修复三种 Pot 的 `saveToTag()` / `saveAdditional()` 无限递归；
2. 增加真实 Pot NBT 保存与重载 GameTest；
3. 让 Coffee Pot 只保存单份模板；
4. 改为按实际转移份数扣除源饮品；
5. 禁止过量填充时吞掉完整饮品。

## P1

6. 最后一份倒出后重置 Moka/Turkish `brewProgress`；
7. Coffee Pot 根据饮品类型校验空容器；
8. 填充 Coffee Pot 时返还源杯子或瓶子；
9. 实现 Moka/Coffee Machine 到 Coffee Pot 的直接转移；
10. Phonograph 使用真实 Record Item ID；
11. Coffee Pot 放置后同步 LEVEL；
12. 建立完整 Phase 9 GameTest。

## P2

13. 改进 Soda Machine 槽位过滤和 ACTIVE 双半同步；
14. 修正 Bar Counter 正交角判断；
15. 强化 ServingContainer NBT 校验；
16. 扩展 Manifest 对机器产出的识别。

# 最终结论

**Phase 9 Fix1 不通过。**

本轮确实关闭了饮品映射、Moka/Turkish 直接倍增、静态热源和糖浆容器等上一轮问题，但新增的三种 Pot NBT 无限递归属于更严重的运行时 P0；Coffee Pot 也仍能将一个四杯饮品扩增为四个四杯饮品。

当前不应继续新增 Phase 9 功能，应先提交一次专门的 `Phase 9 Fix2`，修复序列化与份数转移，再以 `4a989255` 为基线复审。
