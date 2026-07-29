# CoffeeWorkshop 人工测试问题代码级修复方案

**审计分支：** `1.20.1`  
**审计基线：** `4383b69bf0374e8f3f227f8028d9bdb448c5e979`（Phase 9 Fix10 Cleanup）  
**范围：** 人工测试发现的 5 组问题，以及审计过程中发现的直接关联缺陷。  
**原则：** 当前仍处开发阶段，不引入旧开发存档迁移逻辑。

---

# 一、结论概览

| 编号 | 问题 | 严重度 | 根因 |
|---|---|---:|---|
| 1 | 木质/石质吧台拐角不生成或方向错误 | P1 | 邻居朝向条件错误；`INNER_LEFT` 被当作 `INNER_RIGHT` 旋转 180°，碰撞和 blockstate 同时错；现有测试复刻了错误假设 |
| 2 | 苏打饮品没有 JEI 合成表 | P1 | JEI 插件完全未注册 `soda_making` 类型、分类、配方和催化剂；同时存在两个不同的 Minecraft `RecipeType` 实例 |
| 3 | 水果配方只接受原版甜浆果 | P2 | 多个配方直接硬编码 `minecraft:sweet_berries`；项目没有水果/浆果 Item Tag，也没有 Item Tag DataProvider |
| 4 | 空冷萃壶无法右键放置 | P1 | `empty_coldbrew_pot` 注册成普通 `Item`，不是指向 `coldbrew_pot` 方块的 `BlockItem` |
| 5 | 圣诞树和姜饼屋模型错误 | P1 | 圣诞树无属性却使用旧式 `"normal"` variant；姜饼屋 blockstate 要求 `facing`，但 Java 方块没有注册 `FACING` |

另外发现：

- `SodaMachineRecipe#getType()` 与机器查询使用的 `ModRecipeTypes.SODA_MAKING` 不是同一个对象，应合并。
- `cake_vanilla.json` 使用甜浆果作为“香草蛋糕”原料，属于配方语义错误，不应简单替换为通用水果 Tag。
- 圣诞树和姜饼屋的 item model 完整复制 block model，容易再次出现资源漂移，可改为直接继承 block model。

---

# 二、问题 1：吧台拐角连接错误

## 2.1 当前根因 A：邻居朝向判断不符合实际 L 形摆放

文件：

```text
src/main/java/net/langball/coffee/block/BarCounterBlock.java
```

当前逻辑：

```java
Direction right = facing.getClockWise();
if (hasMatchingNeighbour(
        level,
        pos.relative(right),
        thisBlock,
        facing.getOpposite())) {
    return Shape.INNER_RIGHT;
}

Direction left = facing.getCounterClockWise();
if (hasMatchingNeighbour(
        level,
        pos.relative(left),
        thisBlock,
        facing.getOpposite())) {
    return Shape.INNER_LEFT;
}
```

问题在于左右侧邻居都被要求：

```java
neighbourFacing == facing.getOpposite()
```

这要求相邻吧台与当前吧台平行、但朝向相反，而不是形成 90° 转角。

以当前吧台 `FACING=NORTH` 为例：

| 邻居位置 | 实际形成 L 形时邻居应朝向 | 当前代码要求 |
|---|---|---|
| EAST（右侧） | EAST | SOUTH |
| WEST（左侧） | WEST | SOUTH |

所以玩家按正常方式绕着拐角摆放时，状态通常不会自动变为 `INNER_LEFT/RIGHT`。

### 修复

将期望朝向改为邻居所在的侧向：

```java
public static Shape determineShape(
        BlockGetter level,
        BlockPos pos,
        Direction facing,
        Block thisBlock) {

    Direction right = facing.getClockWise();
    Direction left = facing.getCounterClockWise();

    boolean connectsRight = hasMatchingNeighbour(
            level,
            pos.relative(right),
            thisBlock,
            right);

    boolean connectsLeft = hasMatchingNeighbour(
            level,
            pos.relative(left),
            thisBlock,
            left);

    // 当前模型没有 T 型状态。两侧同时匹配时不要随机偏向右侧。
    if (connectsRight == connectsLeft) {
        return Shape.STRAIGHT;
    }

    return connectsRight
            ? Shape.INNER_RIGHT
            : Shape.INNER_LEFT;
}
```

同时把参数类型从 `Level` 收窄成 `BlockGetter`，便于测试且避免无意义的写权限依赖：

```java
private static boolean hasMatchingNeighbour(
        BlockGetter level,
        BlockPos neighbourPos,
        Block thisBlock,
        Direction expectedFacing) {

    BlockState neighbour = level.getBlockState(neighbourPos);
    return neighbour.is(thisBlock)
            && neighbour.getValue(FACING) == expectedFacing;
}
```

---

## 2.2 当前根因 B：`INNER_LEFT` 不是 `INNER_RIGHT` 旋转 180°

基础 inner 模型的主体位于：

```text
+x, +z
```

即模型元素主体是：

```json
"from": [4, 0, 4],
"to":   [16, 14, 16]
```

`INNER_RIGHT` 当前映射是正确的：

| FACING | 应占据的后方+右侧象限 | 当前旋转 |
|---|---|---:|
| NORTH | +x +z | 0 |
| EAST  | -x +z | 90 |
| SOUTH | -x -z | 180 |
| WEST  | +x -z | 270 |

但左转角应是“后方+左侧”，不是右转角的对角线：

| FACING | 正确 `INNER_LEFT` 象限 | 当前代码象限 | 结果 |
|---|---|---|---|
| NORTH | -x +z | -x -z | 错 |
| EAST  | -x -z | +x -z | 错 |
| SOUTH | +x -z | +x +z | 错 |
| WEST  | +x +z | -x +z | 错 |

### 修复碰撞表

将 `INNER_LEFT` 改为：

```java
EnumMap<Direction, VoxelShape> innerLeft = new EnumMap<>(Direction.class);

innerLeft.put(Direction.NORTH,
        Shapes.or(
                Shapes.box(0, 0, 0.25, 0.75, Y_TOP, 1),
                Shapes.box(0, Y_TOP, 0, 1, 1, 1)));

innerLeft.put(Direction.EAST,
        Shapes.or(
                Shapes.box(0, 0, 0, 0.75, Y_TOP, 0.75),
                Shapes.box(0, Y_TOP, 0, 1, 1, 1)));

innerLeft.put(Direction.SOUTH,
        Shapes.or(
                Shapes.box(0.25, 0, 0, 1, Y_TOP, 0.75),
                Shapes.box(0, Y_TOP, 0, 1, 1, 1)));

innerLeft.put(Direction.WEST,
        Shapes.or(
                Shapes.box(0.25, 0, 0.25, 1, Y_TOP, 1),
                Shapes.box(0, Y_TOP, 0, 1, 1, 1)));
```

### 修复两个 blockstate 文件

文件：

```text
src/main/resources/assets/coffeework/blockstates/wooden_bar_counter.json
src/main/resources/assets/coffeework/blockstates/stone_bar_counter.json
```

保留 `INNER_RIGHT` 不变，将 `INNER_LEFT` 旋转改为：

```json
{
  "facing=north,shape=inner_left": {
    "model": "coffeework:block/bar_wooden_inner",
    "y": 90
  },
  "facing=east,shape=inner_left": {
    "model": "coffeework:block/bar_wooden_inner",
    "y": 180
  },
  "facing=south,shape=inner_left": {
    "model": "coffeework:block/bar_wooden_inner",
    "y": 270
  },
  "facing=west,shape=inner_left": {
    "model": "coffeework:block/bar_wooden_inner",
    "y": 0
  }
}
```

石质吧台使用同一旋转表，只替换模型路径。

---

## 2.3 当前 GameTest 为什么没有发现

当前测试辅助方法主动把邻居设置成：

```java
primaryFacing.getOpposite()
```

这与错误的生产代码完全一致，所以测试是在验证错误假设，而不是模拟玩家实际摆放。

当前测试还把错误的 `INNER_LEFT` 碰撞象限表复制到断言中，因此 Java 碰撞表和测试一起错时仍会全绿。

### 测试重写

修改：

```text
src/main/java/net/langball/coffee/gametest/Phase9GameTests.java
```

辅助方法应改成：

```java
helper.setBlock(neighbour,
        block.defaultBlockState()
                .setValue(BarCounterBlock.FACING, neighbourOffset));
```

需要覆盖：

1. 木质吧台：4 个朝向 × 左右两侧，共 8 个。
2. 石质吧台：4 个朝向 × 左右两侧，共 8 个。
3. 错误朝向邻居不得形成拐角。
4. 删除邻居后 `INNER_* -> STRAIGHT`。
5. 左侧邻居移除、右侧邻居加入时 `INNER_LEFT -> INNER_RIGHT`。
6. 两侧同时匹配时使用明确策略，目前建议回退 `STRAIGHT`。
7. 断言碰撞的“后方+左/右”象限，而不是复制旧表。

建议额外增加一个 Python 资源审计，读取：

```text
bar_*_inner.json
wooden_bar_counter.json
stone_bar_counter.json
```

根据基础模型主体象限计算旋转后的象限，避免 Java 碰撞和 JSON 模型再次各自维护一套错误表。

---

# 三、问题 2：苏打饮品没有 JEI 合成表

## 3.1 JEI 插件完全漏掉 Soda Machine

文件：

```text
src/main/java/net/langball/coffee/compat/jei/JEICompat.java
```

当前只注册：

- Grinder
- Coffee Machine
- Icecream Machine
- Roller
- Oven
- Cooling
- Drink Transform

缺少：

```text
SODA_MAKING
```

以下三个阶段全部缺失：

```java
registerCategories(...)
registerRecipes(...)
registerRecipeCatalysts(...)
```

`JEIRecipeTypes.java` 也没有 `RecipeType<SodaMachineRecipe>`。

---

## 3.2 关联缺陷：同一个 ID 创建了两个不同 RecipeType 对象

当前存在：

```java
ModRecipeTypes.SODA_MAKING
```

以及：

```java
SodaMachineRecipeSerializer.TYPE
```

两者都调用：

```java
RecipeType.simple("coffeework:soda_making")
```

但这是两个不同对象。

机器侧查询使用：

```java
ModRecipeTypes.SODA_MAKING
```

而配方自身返回：

```java
SodaMachineRecipeSerializer.TYPE
```

这会让 RecipeManager 的分类和查询依赖两个不同实例，属于必须同时修掉的类型一致性问题。

### 修复

删除：

```java
SodaMachineRecipeSerializer.TYPE
```

修改 `SodaMachineRecipe`：

```java
@Override
public RecipeType<?> getType() {
    return ModRecipeTypes.SODA_MAKING;
}
```

整个项目只允许：

```java
ModRecipeTypes.SODA_MAKING
```

作为唯一 Minecraft RecipeType。

---

## 3.3 新增 JEI RecipeType

修改：

```text
src/main/java/net/langball/coffee/compat/jei/JEIRecipeTypes.java
```

新增：

```java
public static final mezz.jei.api.recipe.RecipeType<SodaMachineRecipe> SODA_MAKING =
        mezz.jei.api.recipe.RecipeType.create(
                CoffeeWork.MODID,
                "soda_making",
                SodaMachineRecipe.class);
```

新增获取方法：

```java
public static List<SodaMachineRecipe> getSodaRecipes() {
    ClientLevel level = Minecraft.getInstance().level;
    if (level == null) {
        return List.of();
    }

    return new ArrayList<>(
            level.getRecipeManager()
                    .getAllRecipesFor(ModRecipeTypes.SODA_MAKING));
}
```

---

## 3.4 新增专用 JEI Category

新增文件：

```text
src/main/java/net/langball/coffee/compat/jei/SodaMachineRecipeCategory.java
```

不要直接复用 `MachineRecipeCategory`，因为 Soda 配方是：

```text
container + base + flavor -> result
```

而通用 MachineCategory 只有一个输入。

建议布局：

```java
public final class SodaMachineRecipeCategory
        extends AbstractRecipeCategory<SodaMachineRecipe> {

    private final IDrawableAnimated arrow;

    public SodaMachineRecipeCategory(
            IGuiHelper guiHelper,
            ItemStack icon) {

        super(
                JEIRecipeTypes.SODA_MAKING,
                Component.translatable(
                        "jei.coffeework.category.soda_machine"),
                guiHelper.createDrawableIngredient(
                        VanillaTypes.ITEM_STACK,
                        icon),
                122,
                58);

        IDrawableStatic staticArrow =
                guiHelper.createDrawable(
                        CoffeeWork.id("textures/gui/jei_machine.png"),
                        0, 0, 22, 16);

        arrow = guiHelper.createAnimatedDrawable(
                staticArrow,
                200,
                IDrawableAnimated.StartDirection.LEFT,
                false);
    }

    @Override
    public void setRecipe(
            IRecipeLayoutBuilder builder,
            SodaMachineRecipe recipe,
            IFocusGroup focuses) {

        builder.addSlot(RecipeIngredientRole.INPUT, 4, 7)
                .addIngredients(recipe.container());

        builder.addSlot(RecipeIngredientRole.INPUT, 26, 7)
                .addIngredients(recipe.base());

        builder.addSlot(RecipeIngredientRole.INPUT, 48, 7)
                .addIngredients(recipe.flavor());

        builder.addSlot(RecipeIngredientRole.OUTPUT, 100, 21)
                .addItemStack(recipe.result());
    }

    @Override
    public void draw(
            SodaMachineRecipe recipe,
            IRecipeSlotsView slots,
            GuiGraphics graphics,
            double mouseX,
            double mouseY) {

        arrow.draw(graphics, 72, 21);

        String seconds = recipe.cookingTime() / 20.0F + "s";
        graphics.drawString(
                Minecraft.getInstance().font,
                seconds,
                76,
                43,
                0xFF808080,
                false);
    }
}
```

---

## 3.5 在 JEICompat 注册完整链路

在 `registerCategories`：

```java
new SodaMachineRecipeCategory(
        guiHelper,
        new ItemStack(ModBlocks.SODA_MACHINE.get()))
```

在 `registerRecipes`：

```java
registration.addRecipes(
        JEIRecipeTypes.SODA_MAKING,
        JEIRecipeTypes.getSodaRecipes());
```

在 `registerRecipeCatalysts`：

```java
registration.addRecipeCatalyst(
        new ItemStack(ModBlocks.SODA_MACHINE.get()),
        JEIRecipeTypes.SODA_MAKING);
```

可选但推荐：

```java
@Override
public void registerGuiHandlers(IGuiHandlerRegistration registration) {
    registration.addRecipeClickArea(
            GuiSodaMachine.class,
            94, 20,
            34, 36,
            JEIRecipeTypes.SODA_MAKING);
}
```

---

## 3.6 语言文件

增加：

```json
"jei.coffeework.category.soda_machine": "Soda Machine"
```

并同步：

```text
en_us.json
zh_cn.json
ja_jp.json
```

---

## 3.7 JEI 验收

1. 对任意 `soda_*` 成品按 `R`，显示 Soda Machine 分类。
2. 对玻璃瓶、Soda 原料或糖浆按 `U`，显示对应配方用途。
3. 点击 Soda Machine 催化剂可进入分类。
4. 当前 6 个 `soda_making` JSON 全部显示。
5. `/reload` 后新增 datapack Soda 配方能够进入 JEI 列表。
6. JEI 输入轮播正确展示 Tag/Ingredient 中的多个可替代物。

---

# 四、问题 3：水果配方只支持原版甜浆果

## 4.1 根因

当前多个配方直接硬编码：

```json
{
  "item": "minecraft:sweet_berries"
}
```

已确认涉及：

```text
src/generated/resources/data/coffeework/recipes/
├── iron_bowl_batter_berry.json
├── mousse_berry_raw.json
├── cream_berry.json
├── cake_berry.json
├── cake_vanilla.json
├── syrup_fruit.json
├── pie_berry.json
└── mooncake_fruit_raw.json
```

而模组本身已经注册：

```text
coffeework:blueberry
coffeework:lemon
```

项目当前只有 `ModBlockTagsProvider`，没有 Item Tag Provider，因此配方没有统一的水果抽象。

---

## 4.2 不要把所有水果混进所有“berry”配方

应区分两个 Tag：

### 浆果

```text
coffeework:fruits/berries
```

默认包含：

```text
minecraft:sweet_berries
minecraft:glow_berries
coffeework:blueberry
```

### 通用水果

```text
coffeework:fruits
```

默认包含：

```text
#coffeework:fruits/berries
minecraft:apple
minecraft:melon_slice
coffeework:lemon
```

这样其他模组或数据包可以向 Tag 追加水果，而不需要修改 CoffeeWorkshop Java 代码。

不建议依赖不存在或语义不稳定的通用 Forge fruits Tag；本模组应声明自己的数据协议。

---

## 4.3 新增 TagKey

新增：

```text
src/main/java/net/langball/coffee/init/ModItemTags.java
```

```java
public final class ModItemTags {
    public static final TagKey<Item> BERRIES =
            create("fruits/berries");

    public static final TagKey<Item> FRUITS =
            create("fruits");

    private static TagKey<Item> create(String path) {
        return TagKey.create(
                Registries.ITEM,
                CoffeeWork.id(path));
    }

    private ModItemTags() {}
}
```

---

## 4.4 新增 Item Tag DataProvider

新增：

```text
src/main/java/net/langball/coffee/datagen/ModItemTagsProvider.java
```

```java
public final class ModItemTagsProvider extends ItemTagsProvider {

    public ModItemTagsProvider(
            PackOutput output,
            CompletableFuture<HolderLookup.Provider> lookup,
            CompletableFuture<TagLookup<Block>> blockTags,
            ExistingFileHelper existingFileHelper) {

        super(
                output,
                lookup,
                blockTags,
                CoffeeWork.MODID,
                existingFileHelper);
    }

    @Override
    protected void addTags(HolderLookup.Provider provider) {
        tag(ModItemTags.BERRIES)
                .add(Items.SWEET_BERRIES)
                .add(Items.GLOW_BERRIES)
                .add(ModItems.BLUEBERRY.get());

        tag(ModItemTags.FRUITS)
                .addTag(ModItemTags.BERRIES)
                .add(Items.APPLE)
                .add(Items.MELON_SLICE)
                .add(ModItems.LEMON.get());
    }
}
```

在 `DataGenerators` 注册：

```java
ModBlockTagsProvider blockTags =
        new ModBlockTagsProvider(
                output,
                event.getLookupProvider(),
                existingFileHelper);

generator.addProvider(event.includeServer(), blockTags);

generator.addProvider(
        event.includeServer(),
        new ModItemTagsProvider(
                output,
                event.getLookupProvider(),
                blockTags.contentsGetter(),
                existingFileHelper));
```

---

## 4.5 配方替换规则

使用 `ModItemTags.BERRIES`：

```text
iron_bowl_batter_berry
mousse_berry_raw
cream_berry
cake_berry
pie_berry
```

使用 `ModItemTags.FRUITS`：

```text
syrup_fruit
mooncake_fruit_raw
```

示例：

```java
shapeless(
        RecipeCategory.MISC,
        ModItems.SYRUP_FRUIT.get(),
        ModItems.SYRUP_EMPTY.get())
    .requires(ModItems.SYRUP_EMPTY.get())
    .requires(ModItemTags.FRUITS)
    .requires(Items.SUGAR)
    .save(writer, modLoc("syrup_fruit"));
```

Mooncake 需要两个水果时：

```java
.requires(ModItemTags.FRUITS)
.requires(ModItemTags.FRUITS)
```

---

## 4.6 `cake_vanilla` 不应直接套水果 Tag

当前：

```text
cake_vanilla -> minecraft:cake
```

却要求：

```text
minecraft:sweet_berries
```

从名称和模组已有原料看，更合理的是：

```java
.requires(ModItems.VANILLA.get())
```

应将它作为独立配方语义错误修复，而不是换成 `FRUITS`。

---

## 4.7 生成资源漂移问题

当前 `ModRecipeProvider` 中明确出现的 `SWEET_BERRIES` 只有：

```text
iron_bowl_batter_berry
syrup_fruit
```

但 `src/generated/resources` 还有多个旧的硬编码输出。

这意味着至少存在以下情况之一：

- 生成目录保留了旧 DataGen 残留；
- 一部分配方生成源已经从 Java 中删除；
- 当前构建依赖历史生成文件。

修复时不能只手改 JSON。应：

1. 把所有仍应存在的配方重新纳入 `ModRecipeProvider`；
2. 清空目标生成目录中的 CoffeeWorkshop recipe 输出；
3. 重新执行 `runData`；
4. 检查 Git diff，确保没有依赖历史残留；
5. 给 CI 增加“干净目录重新 DataGen 后无 diff”的检查。

---

## 4.8 水果配方验收

- `sweet_berries` 可制作 berry 类物品。
- `glow_berries` 可制作 berry 类物品。
- `coffeework:blueberry` 可制作 berry 类物品。
- `coffeework:lemon` 只能用于通用 fruit 配方和 lemon 专用配方，不应制作 berry cake。
- `apple`、`melon_slice` 可进入通用 fruit 配方。
- 胡萝卜、马铃薯等非水果不得通过。
- JEI 应轮播显示同一 Tag 中所有允许物品。

---

# 五、问题 4：空冷萃壶不能右键放置

## 5.1 根因

当前：

```java
ModItems.EMPTY_COLDBREW_POT =
        items.register(
                "empty_coldbrew_pot",
                () -> new Item(new Item.Properties()));
```

它只是普通 Item，没有任何放置行为。

与此同时：

```java
ModItems.COLD_BREW_POT
```

才是：

```java
new BlockItem(ModBlocks.COLD_BREW_POT.get(), ...)
```

`BlockColdBrewPot` 已经定义：

```text
ferm=8
```

作为空壶状态，blockstate 也已经将：

```text
ferm=8 -> empty_coldbrew_pot model
```

所以方块状态层已经支持空壶，缺的是物品到方块状态的放置桥接。

---

## 5.2 新增 EmptyColdBrewPotItem

新增：

```text
src/main/java/net/langball/coffee/item/EmptyColdBrewPotItem.java
```

```java
public final class EmptyColdBrewPotItem extends BlockItem {

    public EmptyColdBrewPotItem(
            Block block,
            Properties properties) {
        super(block, properties);
    }

    @Override
    @Nullable
    protected BlockState getPlacementState(
            BlockPlaceContext context) {

        BlockState state = super.getPlacementState(context);

        if (state == null) {
            return null;
        }

        return state.setValue(
                BlockColdBrewPot.FERM,
                8);
    }
}
```

---

## 5.3 修改注册位置

从：

```text
ModIngredientItems
```

删除普通 Item 注册。

在：

```text
ModEquipmentItems
```

注册：

```java
ModItems.EMPTY_COLDBREW_POT =
        items.register(
                "empty_coldbrew_pot",
                () -> new EmptyColdBrewPotItem(
                        ModBlocks.COLD_BREW_POT.get(),
                        new Item.Properties().stacksTo(1)));
```

建议同时把满冷萃壶改成：

```java
new Item.Properties().stacksTo(1)
```

避免带状态含义的壶类物品堆叠 64 个。

同一个方块可以拥有两个不同注册 ID 的 BlockItem：

```text
coffeework:coldbrew_pot
coffeework:empty_coldbrew_pot
```

两者只需在放置状态上不同。

---

## 5.4 修复精准选取

当前 `getCloneItemStack()` 无论 `ferm` 状态如何都返回满壶。

修改：

```java
@Override
public ItemStack getCloneItemStack(
        BlockGetter level,
        BlockPos pos,
        BlockState state) {

    return new ItemStack(
            state.getValue(FERM) == 8
                    ? ModItems.EMPTY_COLDBREW_POT.get()
                    : ModItems.COLD_BREW_POT.get());
}
```

---

## 5.5 冷萃壶测试

新增 GameTest：

1. 玩家手持 `empty_coldbrew_pot` 右键地面。
2. 生成 `coldbrew_pot` 方块。
3. 状态必须为 `ferm=8`。
4. 生存模式物品数量减少 1。
5. 破坏后只掉落一个 `empty_coldbrew_pot`。
6. 精准选取返回空壶。
7. `coldbrew_pot` 满壶物品放置后仍为 `ferm=0`。
8. 满壶与空壶不能因为 `onRemove` 与 loot table 同时掉落而复制。

当前 loot table 没有物品池，掉落仍由 `onRemove` 管理，因此修复时不要再给该方块追加普通 `dropSelf()`，否则会双重掉落。

---

# 六、问题 5：圣诞树和姜饼屋模型错误

## 6.1 圣诞树：无属性方块使用了不存在的 `normal` variant

`BlockXmasTree` 没有注册任何 BlockState property。

但 blockstate JSON 是：

```json
{
  "variants": {
    "normal": {
      "model": "coffeework:block/xmas_tree"
    }
  }
}
```

`normal` 是旧版本 blockstate 写法。当前无属性方块的 variant key 应为：

```json
""
```

### 修复

```json
{
  "variants": {
    "": {
      "model": "coffeework:block/xmas_tree"
    }
  }
}
```

文件：

```text
src/main/resources/assets/coffeework/blockstates/xmas_tree.json
```

---

## 6.2 姜饼屋：JSON 使用 facing，但 Java 方块没有 FACING

当前 blockstate JSON 声明：

```text
facing=north
facing=east
facing=south
facing=west
```

但是 `BlockGingerHouse`：

- 没有 `DirectionProperty FACING`；
- 没有注册默认 Facing；
- 没有 `createBlockStateDefinition()`；
- 没有 `getStateForPlacement()`；
- 没有 rotate/mirror。

因此实际 blockstate 不可能匹配 JSON 中的任何 variant。

姜饼屋模型四侧纹理不同，现有 JSON 也明确希望支持旋转，所以不建议把 JSON 简化成无方向状态；应补齐 Java 朝向。

### 修复

参考项目中已经正确实现的 `BlockPlate`：

```java
public final class BlockGingerHouse extends Block {

    public static final DirectionProperty FACING =
            HorizontalDirectionalBlock.FACING;

    public BlockGingerHouse(
            BlockBehaviour.Properties properties) {

        super(properties);

        registerDefaultState(
                stateDefinition.any()
                        .setValue(FACING, Direction.NORTH));
    }

    @Override
    protected void createBlockStateDefinition(
            StateDefinition.Builder<Block, BlockState> builder) {

        builder.add(FACING);
    }

    @Override
    public BlockState getStateForPlacement(
            BlockPlaceContext context) {

        return defaultBlockState().setValue(
                FACING,
                context.getHorizontalDirection()
                        .getOpposite());
    }

    @Override
    public BlockState rotate(
            BlockState state,
            Rotation rotation) {

        return state.setValue(
                FACING,
                rotation.rotate(state.getValue(FACING)));
    }

    @Override
    public BlockState mirror(
            BlockState state,
            Mirror mirror) {

        return state.rotate(
                mirror.getRotation(
                        state.getValue(FACING)));
    }

    // 原有 Shape 方法保留
}
```

现有 `ginger_house.json` 的四向 rotation 可以继续使用。

---

## 6.3 精简 item model，避免 block/item 资源再次漂移

当前：

```text
models/item/xmas_tree.json
models/item/ginger_house.json
```

都完整复制了数百行 block model。

而两个 block model 已经包含 `display` 配置，所以 item model 可以直接继承：

```json
{
  "parent": "coffeework:block/xmas_tree"
}
```

```json
{
  "parent": "coffeework:block/ginger_house"
}
```

这样之后修改 Blockbench 模型时只维护一份。

---

## 6.4 模型验收

- 世界中的圣诞树不再显示 missing model。
- 圣诞树透明部分按 `cutout` 正常显示。
- 姜饼屋放置时正面朝向玩家。
- 四个朝向分别旋转正确。
- 结构旋转/镜像命令后 Facing 正确。
- 物品栏、手持、地面掉落模型与方块模型一致。
- F3 调试状态中姜饼屋存在 `facing=...`。
- 圣诞树状态不出现多余 property。

---

# 七、建议实施顺序

## 第 1 批：确定性资源/物品修复

1. 修复 `xmas_tree.json` 空 variant。
2. 给 `BlockGingerHouse` 增加 FACING。
3. 将两个 item model 改成 parent。
4. 新增 `EmptyColdBrewPotItem`。
5. 修复空壶注册、精准选取和 GameTest。

这些修改局部、风险最低，适合作为第一提交。

## 第 2 批：吧台连接系统

1. 修正邻居 Facing 判定。
2. 修正 `INNER_LEFT` Java 碰撞象限。
3. 修正木质/石质 blockstate rotations。
4. 重写测试，不再使用 `primaryFacing.getOpposite()`。
5. 增加两种材质和负向用例。

吧台代码、碰撞、JSON 与测试必须在同一个提交中修改，不能拆开。

## 第 3 批：Soda JEI 与 RecipeType 收敛

1. 删除 `SodaMachineRecipeSerializer.TYPE`。
2. `SodaMachineRecipe#getType()` 返回唯一的 `ModRecipeTypes.SODA_MAKING`。
3. 新增 `JEIRecipeTypes.SODA_MAKING`。
4. 新增 `SodaMachineRecipeCategory`。
5. 注册 recipes/category/catalyst/click area。
6. 增加三语言 key。

## 第 4 批：水果 Tag 与配方重生成

1. 新增 `ModItemTags`。
2. 新增 `ModItemTagsProvider`。
3. 区分 BERRIES 和 FRUITS。
4. 修复 `cake_vanilla`。
5. 将所有相关配方重新纳入 DataGen 源。
6. 清理并重新生成 `src/generated/resources`。
7. 增加 clean DataGen CI 检查。

---

# 八、最终自动化验收清单

## Java / 构建

```bash
./gradlew clean build
./gradlew runGameTestServer
```

## DataGen

```bash
rm -rf src/generated/resources/data/coffeework/recipes
rm -rf src/generated/resources/data/coffeework/tags/items

./gradlew runData

git diff --exit-code
```

CI 实际执行时可以在临时 worktree 中做 clean DataGen，避免直接破坏工作区。

## 游戏内人工测试

### 吧台

- 木质、石质各测试 8 个左右拐角组合。
- 反向朝向不会错误吸附。
- 删除/新增邻居实时更新。
- 模型和碰撞完全一致。

### Soda/JEI

- `R` 查看成品配方。
- `U` 查看输入用途。
- 催化剂与 GUI 点击区正常。
- datapack `/reload` 后配方更新。

### 水果

- Blueberry、Sweet Berries、Glow Berries。
- Apple、Melon、Lemon 只进入通用 fruit 配方。
- 非水果被拒绝。

### 冷萃壶

- 空壶可放置且 `ferm=8`。
- 满壶为 `ferm=0`。
- 破坏、精准选取、掉落均保持空/满身份。

### 装饰模型

- 圣诞树不再 missing model。
- 姜饼屋四向旋转正确。
- 物品模型和方块模型一致。

---

# 九、预计改动文件

## Java

```text
BarCounterBlock.java
Phase9GameTests.java
SodaMachineRecipe.java
SodaMachineRecipeSerializer.java
JEIRecipeTypes.java
JEICompat.java
SodaMachineRecipeCategory.java              [新增]
ModItemTags.java                            [新增]
ModItemTagsProvider.java                    [新增]
DataGenerators.java
ModRecipeProvider.java
EmptyColdBrewPotItem.java                   [新增]
ModIngredientItems.java
ModEquipmentItems.java
BlockColdBrewPot.java
BlockGingerHouse.java
```

## Resources

```text
blockstates/wooden_bar_counter.json
blockstates/stone_bar_counter.json
blockstates/xmas_tree.json
blockstates/ginger_house.json
models/item/xmas_tree.json
models/item/ginger_house.json
lang/en_us.json
lang/zh_cn.json
lang/ja_jp.json
data/coffeework/tags/items/fruits.json       [生成]
data/coffeework/tags/items/fruits/berries.json [生成]
相关 recipe JSON                              [重新生成]
```

## 工具/CI

建议新增：

```text
tools/audit_bar_counter_geometry.py
```

并增加 clean DataGen 一致性检查。

---

# 十、完成标准

本轮修复完成后应满足：

- 吧台逻辑不再依赖错误的“相反朝向”测试假设。
- `INNER_LEFT` 的模型、碰撞和状态完全一致。
- Soda RecipeType 只有一个 Minecraft 实例。
- 所有 Soda 配方出现在 JEI。
- 水果配方由可扩展 Tag 驱动。
- 空冷萃壶是可放置物品，并正确生成 `ferm=8`。
- 圣诞树与姜饼屋 blockstate 和 Java 状态定义一致。
- clean DataGen 后仓库无历史生成文件依赖。
