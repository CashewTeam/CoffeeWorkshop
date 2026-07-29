# CoffeeWorkshop Phase 10 开发文档

# 遗留内容闭环与引导

**目标分支：** `1.20.1`  
**建议文档路径：** `docs/PHASE10_LEGACY_CONTENT_AND_GUIDANCE.md`  
**阶段定位：** Phase 9 核心系统稳定后的内容闭环、玩家引导与发布候选准备  
**适用版本：** Minecraft 1.20.1 / Forge 47.4.x / JEI 15.20.x  
**文档性质：** 开发实施规范，不是愿望清单；编码 Agent 应按本文的任务边界、代码结构和验收门禁执行。

---

# 1. 阶段目标

Phase 10 不再扩展新的底层大型机器框架，主要完成以下工作：

1. 将过度拥挤的单一创造模式页拆分成三个明确分类；
2. 补齐所有机器和自定义交互的 JEI 引导；
3. 建立完整的 Coffee Workshop Advancement 教程树；
4. 重构三个村民职业的工作站、交易职责和价格安全；
5. 补齐 Soda、三明治和冰淇淋剩余内容；
6. 将内容完整性、JEI、Advancement、交易套利检查纳入 CI；
7. 冻结内容后进入 Release Candidate 稳定期。

Phase 10 完成后，玩家应当能够在不阅读外部 Wiki 的情况下，通过：

```text
创造模式分类
→ JEI
→ Advancement
→ 村民交易
```

理解并访问模组绝大多数核心内容。

---

# 2. 当前代码基线

## 2.1 创造模式页

当前：

```text
src/main/java/net/langball/coffee/init/ModCreativeTabs.java
```

只注册一个：

```java
COFFEE_TAB
```

并在一个 `displayItems` lambda 中逐项加入全部内容。

当前内容清单统计：

```text
406 items
58 blocks
3 villager professions
2 POI types
```

所有 406 个物品都已进入当前单一 Tab。

Phase 10 不得降低这一覆盖率。

---

## 2.2 JEI

当前已注册：

```text
Grinding
Coffee Brewing
Icecream Making
Rolling
Oven Baking
Cooling
Drink Transform
```

当前缺失：

```text
Moka Brewing
Turkish Pot Brewing
Soda Machine
Display Conversion
```

其中：

- Drink Transform 已存在专用 Recipe Category；
- Moka 和 Turkish Pot 的行为仍由 BlockEntity 硬编码；
- Soda 已有真正的 `SodaMachineRecipe`；
- Drink Display 是世界交互，不是真正工作台配方。

---

## 2.3 Advancement

当前主要 Advancement 只有：

```text
coffeework:root
coffeework:phonograph_play
```

自动生成的 `advancements/recipes/...` 仅用于解锁配方，不构成玩家教程树。

Phase 10 必须建立独立的功能引导树。

---

## 2.4 村民职业

当前职业：

```text
coffee_barista
coffee_materials_trader
food_trader
```

当前 POI：

```text
coffee_poi
oven_poi
```

Barista 和 Materials Trader 都使用 Coffee Machine 作为工作站，职业职责和生成条件不够清晰。

当前交易直接以 Java 数组方式硬编码，缺少自动套利审计。

---

## 2.5 剩余内容

当前已经存在：

```text
6 种 Soda
7 种普通 Ice Cream
7 种 Cookie Ice Cream
7 个 Sandwich 成品
```

但仍存在：

- legacy Soda 口味没有全部完成语义映射；
- 三明治配方仍是零散的直接合成；
- 冰淇淋口味尚未覆盖当前糖浆体系；
- 内容注册、资源、配方、JEI、Advancement、交易之间仍靠人工同步。

---

# 3. Phase 10 总体工程原则

## 3.1 单一事实来源

同一业务事实不得在多个系统中重复硬编码。

错误示例：

```text
Moka Brew Time:
BlockEntity 写 400
JEI 再写 400
Advancement 文案再写 20 秒
```

正确方式：

```java
TraditionalBrewingDefinition.MOKA.brewTicks()
```

由 Runtime 和 JEI 同时读取。

---

## 3.2 开发阶段不处理旧内部存档迁移

Phase 10 不为 Phase 9 中间 Fix 版本维护迁移逻辑。

允许：

```text
删除开发测试世界
重新生成 DataGen
重建 Advancement 进度
```

不允许：

```text
为了内部开发存档增加永久 Chunk 扫描
保留废弃枚举
新增一次性迁移器
```

---

## 3.3 新内容的完整面

每个新注册物品至少必须具备：

```text
注册
模型
纹理
en_us
zh_cn
ja_jp
至少一个 Creative Tab
生存获取来源
JEI 可见路径
合理的 Advancement 归属
```

方块物品还必须具备：

```text
Block
BlockItem
Blockstate
Block Model
Item Model
Loot / 自定义掉落
碰撞与放置测试
```

---

## 3.4 DataGen 是源，生成资源不是源

对于由 DataGen 管理的内容：

```text
src/main/java/.../datagen
```

是唯一事实来源。

不得只编辑：

```text
src/generated/resources
```

Phase 10 必须增加“清空生成目录后重新 DataGen 无差异”的 CI 门禁。

---

# 4. Phase 10 实施分期

建议分为以下提交阶段：

```text
P10-A  Creative Tabs
P10-B  JEI Coverage
P10-C  Advancement Tree
P10-D  Villager Rebalance
P10-E  Soda Expansion
P10-F  Sandwich & Ice Cream Closure
P10-RC Final Audit
```

每批必须独立构建、独立测试，不建议将整个 Phase 10 压缩成一个巨型提交。

---

# 5. 12.1 创造模式页扩容

## 5.1 目标分类

注册三个 CreativeModeTab：

```text
Coffee Workshop — Drinks & Machines
Coffee Workshop — Bakery & Ingredients
Coffee Workshop — Decor & Equipment
```

推荐注册 ID：

```text
coffeework:drinks_and_machines
coffeework:bakery_and_ingredients
coffeework:decor_and_equipment
```

推荐语言键：

```text
itemGroup.coffeework.drinks_and_machines
itemGroup.coffeework.bakery_and_ingredients
itemGroup.coffeework.decor_and_equipment
```

---

## 5.2 内容归属规则

### Drinks & Machines

包括：

```text
Grinder
Coffee Machine
Icecream Machine
Roller
Oven
Soda Machine
Moka Pot
Turkish Coffee Pot
Coffee Pot
Cold Brew Pot
Moka Bottom / Top

所有咖啡饮品
所有茶饮
所有 Cocoa 饮品
所有 Soda
Espresso
Turkish Coffee
Cold Brew
Instant Coffee 系列
Cup
Glass Cup
Cold Brew Bottle
```

Phonograph 和唱片不放在这里，归入 Decor & Equipment。

### Bakery & Ingredients

包括：

```text
所有面包
三明治
蛋糕
切片
Cake Roll
Jiggy Cake
Mousse
Pie
Muffin
Pastry
Mooncake
Soufflé
普通与 Cookie Ice Cream

咖啡豆与咖啡粉
可可原料
面粉
糖浆
奶油
面团
酵母
香料
明胶
水果
茶叶
Vanilla
Soda 原料
所有 Raw / Model / Base 中间件
```

### Decor & Equipment

包括：

```text
Plate
Drink Display 相关可获得物
Wooden / Stone Bar Counter
Phonograph
Records
Xmas Tree
Ginger House
Bags / Double Bags
Coffee Tree / Blueberry Bush
Seeds
Soda Ore

Iron Bowl
Mixing Bowl
Cake Models
Mooncake Model
Small Model
Molds
Empty Cold Brew Pot
其他工具和设备
```

允许少量物品重复出现在两个 Tab，例如：

```text
Cup
Mixing Bowl
Plate
```

但每个物品必须有一个“主归属”。

---

## 5.3 Java 实现结构

不要继续使用一个 400 行 lambda。

修改：

```text
src/main/java/net/langball/coffee/init/ModCreativeTabs.java
```

推荐结构：

```java
public final class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(
                    Registries.CREATIVE_MODE_TAB,
                    CoffeeWork.MODID);

    public static final RegistryObject<CreativeModeTab>
            DRINKS_AND_MACHINES = TABS.register(
                    "drinks_and_machines",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable(
                                    "itemGroup.coffeework.drinks_and_machines"))
                            .icon(() -> new ItemStack(
                                    ModItems.COFFEE_MACHINE.get()))
                            .displayItems((parameters, output) ->
                                    addDrinksAndMachines(output))
                            .build());

    public static final RegistryObject<CreativeModeTab>
            BAKERY_AND_INGREDIENTS = TABS.register(
                    "bakery_and_ingredients",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable(
                                    "itemGroup.coffeework.bakery_and_ingredients"))
                            .icon(() -> new ItemStack(
                                    ModItems.CAKE_BERRY.get()))
                            .displayItems((parameters, output) ->
                                    addBakeryAndIngredients(output))
                            .build());

    public static final RegistryObject<CreativeModeTab>
            DECOR_AND_EQUIPMENT = TABS.register(
                    "decor_and_equipment",
                    () -> CreativeModeTab.builder()
                            .title(Component.translatable(
                                    "itemGroup.coffeework.decor_and_equipment"))
                            .icon(() -> new ItemStack(
                                    ModItems.WOODEN_BAR_COUNTER.get()))
                            .displayItems((parameters, output) ->
                                    addDecorAndEquipment(output))
                            .build());

    private static void addDrinksAndMachines(
            CreativeModeTab.Output output) {

        output.accept(ModItems.GRINDER.get());
        output.accept(ModItems.COFFEE_MACHINE.get());
        output.accept(ModItems.ICECREAM_MACHINE.get());

        // 按生产流程继续排列，不按注册文件顺序堆放。
    }

    private static void addBakeryAndIngredients(
            CreativeModeTab.Output output) {
        // ingredients -> raw -> processed -> finished
    }

    private static void addDecorAndEquipment(
            CreativeModeTab.Output output) {
        // blocks -> equipment -> records
    }

    private ModCreativeTabs() {}
}
```

---

## 5.4 排序规范

Tab 内应按玩家流程排序：

```text
获取原料
→ 初级加工
→ 高级加工
→ 成品
```

示例：

```text
Coffee Seeds
Coffee Bean Raw
Coffee Bean
Coffee Powder
Grinder
Coffee Machine
Espresso
Americano
Latte
Flavored Latte
```

禁止仅按字母排序，因为生产链会被打散。

---

## 5.5 Creative Tab 审计脚本

当前：

```text
tools/build_content_manifest.py
```

只判断物品是否在任意 `output.accept(...)` 中。

Phase 10 建议保留这项兼容，同时增加：

```text
tools/audit_creative_tabs.py
```

输出：

```json
{
  "coffee_americano": [
    "drinks_and_machines"
  ],
  "cup": [
    "drinks_and_machines",
    "decor_and_equipment"
  ]
}
```

核心校验：

```python
missing = registered_item_ids - set(item_to_tabs)
unknown = set(item_to_tabs) - registered_item_ids

assert not missing
assert not unknown
```

再增加：

```python
ALLOWED_DUPLICATES = {
    "cup",
    "cup_glass",
    "mixing_bowl",
    "plate",
}
```

非允许物品若出现在多个 Tab，应报告警告，避免随意重复。

---

## 5.6 12.1 验收标准

```text
[ ] 三个 Tab 全部可见
[ ] 三语言标题存在
[ ] 每个注册物品至少进入一个 Tab
[ ] 无未注册 ID 被加入 Tab
[ ] Tab 图标在注册完成后可安全创建
[ ] 原有 406 项覆盖率不下降
[ ] 新增 Phase 10 内容自动进入正确 Tab
[ ] Content Manifest 可以显示每个物品的 Tab 归属
```

---

# 6. 12.2 JEI 完整覆盖

## 6.1 最终分类

Phase 10 要求：

```text
Grinding
Coffee Brewing
Icecream Making
Rolling
Oven Baking
Cooling
Drink Transform
Moka Brewing
Turkish Pot Brewing
Soda Machine
Display Conversion
```

---

## 6.2 包结构

建议整理为：

```text
compat/jei/
├── JEICompat.java
├── JEIRecipeTypes.java
├── MachineRecipeCategory.java
├── CoffeeBrewingRecipeCategory.java
├── CoolingRecipeCategory.java
├── DrinkTransformRecipeCategory.java
├── SodaMachineRecipeCategory.java
├── TraditionalBrewingRecipeCategory.java
├── DisplayConversionRecipeCategory.java
└── view/
    ├── TraditionalBrewingJeiRecipe.java
    └── DisplayConversionJeiRecipe.java
```

Moka、Turkish 和 Display 可以使用 JEI-only DTO，不需要伪装成 Minecraft 工作台配方。

---

## 6.3 Soda Machine

Soda 已有真正的：

```java
SodaMachineRecipe
```

因此 JEI 必须直接使用 RecipeManager 中的数据。

### JEIRecipeTypes

新增：

```java
public static final RecipeType<SodaMachineRecipe>
        SODA_MAKING = RecipeType.create(
                CoffeeWork.MODID,
                "soda_making",
                SodaMachineRecipe.class);
```

### 获取配方

```java
public static List<SodaMachineRecipe> getSodaRecipes() {
    ClientLevel level = Minecraft.getInstance().level;

    if (level == null) {
        return List.of();
    }

    return new ArrayList<>(
            level.getRecipeManager()
                    .getAllRecipesFor(
                            ModRecipeTypes.SODA_MAKING));
}
```

### Category 布局

当前 GUI 槽位语义：

```text
Bottle
Soda Base
Flavor
Output
```

JEI Category 应明确显示三个输入：

```java
@Override
public void setRecipe(
        IRecipeLayoutBuilder builder,
        SodaMachineRecipe recipe,
        IFocusGroup focuses) {

    builder.addSlot(
                    RecipeIngredientRole.INPUT,
                    4,
                    8)
            .addIngredients(recipe.container());

    builder.addSlot(
                    RecipeIngredientRole.INPUT,
                    26,
                    8)
            .addIngredients(recipe.base());

    builder.addSlot(
                    RecipeIngredientRole.INPUT,
                    48,
                    8)
            .addIngredients(recipe.flavor());

    builder.addSlot(
                    RecipeIngredientRole.OUTPUT,
                    100,
                    22)
            .addItemStack(recipe.result());
}
```

### JEICompat 注册

```java
registration.addRecipeCategories(
        new SodaMachineRecipeCategory(
                guiHelper,
                new ItemStack(
                        ModBlocks.SODA_MACHINE.get())));
```

```java
registration.addRecipes(
        JEIRecipeTypes.SODA_MAKING,
        JEIRecipeTypes.getSodaRecipes());
```

```java
registration.addRecipeCatalyst(
        new ItemStack(
                ModBlocks.SODA_MACHINE.get()),
        JEIRecipeTypes.SODA_MAKING);
```

### GUI 点击区域

```java
@Override
public void registerGuiHandlers(
        IGuiHandlerRegistration registration) {

    registration.addRecipeClickArea(
            GuiSodaMachine.class,
            92,
            18,
            38,
            40,
            JEIRecipeTypes.SODA_MAKING);
}
```

坐标必须结合最终 Soda GUI 贴图人工校准。

---

## 6.4 Soda RecipeType 必须收敛为一个实例

当前不得同时保留：

```text
ModRecipeTypes.SODA_MAKING
SodaMachineRecipeSerializer.TYPE
```

只保留：

```java
ModRecipeTypes.SODA_MAKING
```

修改：

```java
@Override
public RecipeType<?> getType() {
    return ModRecipeTypes.SODA_MAKING;
}
```

删除 Serializer 中重复创建的 `TYPE`。

这项修复应在 Phase 10 JEI 提交中完成，避免 RecipeManager 与 JEI 查询使用不同对象。

---

## 6.5 Moka 与 Turkish Brewing

当前两个 BlockEntity 分别硬编码：

```text
Moka:
400 ticks
4 servings
Coffee Powder + Water
Espresso

Turkish:
500 ticks
4 servings
Coffee Powder + Water
Turkish Coffee
```

JEI 不应再次复制这些数字。

### 建立共享定义

新增：

```text
src/main/java/net/langball/coffee/brewing/TraditionalBrewingDefinition.java
```

```java
public record TraditionalBrewingDefinition(
        ResourceLocation id,
        Supplier<? extends ItemLike> coffeeInput,
        Supplier<? extends Item> result,
        Supplier<? extends ItemLike> servingContainer,
        TagKey<Block> heatSources,
        int brewTicks,
        int servings) {

    public Ingredient coffeeIngredient() {
        return Ingredient.of(coffeeInput.get());
    }

    public ItemStack createResult() {
        ItemStack stack =
                new ItemStack(result.get());

        if (stack.getItem() instanceof DrinkCoffee drink) {
            drink.initializeFreshStack(stack);
            DrinkCoffee.setRemainingCups(stack, 1);
            stack.getOrCreateTag()
                    .putInt("max_cups", 1);
        }

        return stack;
    }
}
```

新增：

```text
TraditionalBrewingDefinitions.java
```

```java
public final class TraditionalBrewingDefinitions {

    public static final TraditionalBrewingDefinition MOKA =
            new TraditionalBrewingDefinition(
                    CoffeeWork.id("moka_brewing"),
                    () -> ModItems.COFFEE_POWDER.get(),
                    () -> ModItems.ESPRESSO.get(),
                    () -> ModItems.CUP.get(),
                    ModBlockTags.MOKA_HEAT_SOURCES,
                    400,
                    4);

    public static final TraditionalBrewingDefinition TURKISH =
            new TraditionalBrewingDefinition(
                    CoffeeWork.id("turkish_brewing"),
                    () -> ModItems.COFFEE_POWDER.get(),
                    () -> ModItems.COFFEE_TURKISH.get(),
                    () -> ModItems.CUP.get(),
                    ModBlockTags.TURKISH_HEAT_SOURCES,
                    500,
                    4);

    private TraditionalBrewingDefinitions() {}
}
```

### 修改 BlockEntity

Moka：

```java
private static final TraditionalBrewingDefinition DEFINITION =
        TraditionalBrewingDefinitions.MOKA;
```

替换：

```java
MAX_BREW_TIME
MAX_SERVINGS
ModItems.ESPRESSO
HEAT_SOURCE_TAG
```

为：

```java
DEFINITION.brewTicks()
DEFINITION.servings()
DEFINITION.createResult()
DEFINITION.heatSources()
```

Turkish 同理。

这样 Runtime 与 JEI 使用同一份定义。

---

## 6.6 Traditional Brewing JEI DTO

新增：

```java
public record TraditionalBrewingJeiRecipe(
        TraditionalBrewingDefinition definition) {

    public ResourceLocation id() {
        return definition.id();
    }
}
```

注册两个 JEI RecipeType：

```java
public static final RecipeType<TraditionalBrewingJeiRecipe>
        MOKA_BREWING = RecipeType.create(
                CoffeeWork.MODID,
                "moka_brewing",
                TraditionalBrewingJeiRecipe.class);

public static final RecipeType<TraditionalBrewingJeiRecipe>
        TURKISH_BREWING = RecipeType.create(
                CoffeeWork.MODID,
                "turkish_brewing",
                TraditionalBrewingJeiRecipe.class);
```

两个分类可以复用同一个：

```java
TraditionalBrewingRecipeCategory
```

构造参数传入：

```text
RecipeType
标题
机器图标
```

### 建议布局

```text
Coffee Powder + Water Bucket
            ↓ Heat
     4 servings of Drink
            + Cup
```

Category 中：

- Coffee Powder：INPUT；
- Water Bucket：INPUT；
- Heat Source：CATALYST；
- Cup：CATALYST；
- Espresso / Turkish Coffee：OUTPUT；
- 文本显示 `20s · 4 servings` 或 `25s · 4 servings`。

不要把 4 servings 表示成结果物品堆叠 4 个，因为运行时是内部 ServingContainer，而不是一次产出 4 个独立物品。

---

## 6.7 Drink Transform

现有 Category 保留。

调整：

1. 分类图标不要固定使用 `SYRUP_FRUIT` 表示所有转换；
2. Catalyst 不应使用某一个糖浆；
3. 该操作发生在工作台，应使用 Crafting Table 作为 Catalyst；
4. JEI 应显示糖浆返还空瓶提示；
5. 应显示“保留剩余杯数”的说明。

推荐：

```java
registration.addRecipeCatalyst(
        new ItemStack(Items.CRAFTING_TABLE),
        JEIRecipeTypes.DRINK_TRANSFORM);
```

Category 文本：

```text
Preserves remaining servings
Returns empty syrup bottle
```

三语言键：

```text
jei.coffeework.drink_transform.preserve_servings
jei.coffeework.drink_transform.return_container
```

---

## 6.8 Display Conversion

Drink Display 不是工作台配方。

不要创建假的可合成输出。

### DTO

```java
public record DisplayConversionJeiRecipe(
        ItemStack plate,
        ItemStack drink) {
}
```

### 数据来源

使用：

```java
DrinkDisplayRegistry.getRegisteredDrinks()
```

转换：

```java
public static List<DisplayConversionJeiRecipe>
        getDisplayConversions() {

    return DrinkDisplayRegistry
            .getRegisteredDrinks()
            .stream()
            .sorted()
            .map(ForgeRegistries.ITEMS::getValue)
            .filter(Objects::nonNull)
            .map(item ->
                    new DisplayConversionJeiRecipe(
                            new ItemStack(
                                    ModItems.PLATE.get()),
                            new ItemStack(item)))
            .toList();
}
```

### Category 语义

使用：

```text
Plate：CATALYST
Drink：INPUT
```

不设置 OUTPUT。

绘制提示：

```text
Right-click the plate with this drink
右键盘子摆放该饮品
この飲み物を持って皿を右クリック
```

---

## 6.9 JEI 覆盖审计

新增：

```text
tools/audit_jei_coverage.py
```

必须验证：

```text
ModRecipeTypes 中所有机器 RecipeType 均有 JEI 类型
Soda Category 已注册
Moka Category 已注册
Turkish Category 已注册
Display Category 已注册
所有自定义 Category 有语言键
所有机器 Category 有 Catalyst
Soda GUI 有 click area
```

推荐硬性列表：

```python
REQUIRED_JEI_CATEGORIES = {
    "grinding",
    "coffee_brewing",
    "icecream_making",
    "rolling",
    "oven_baking",
    "cooling",
    "drink_transform",
    "moka_brewing",
    "turkish_brewing",
    "soda_making",
    "display_conversion",
}
```

---

## 6.10 12.2 验收标准

```text
[ ] 对 Soda 成品按 R 能看到三输入机器配方
[ ] 对 Soda 输入按 U 能看到用途
[ ] Soda Machine 可作为 Catalyst 打开分类
[ ] Moka 和 Turkish 的时间、结果、份数来自共享定义
[ ] Display Conversion 不显示为工作台配方
[ ] Drink Transform 提示保留剩余杯数和空瓶返还
[ ] 数据包添加的新 Soda 配方在重载后可被 JEI 发现
[ ] 所有 JEI 文本有三语言
```

---

# 7. 12.3 Advancement 完整引导

## 7.1 目标树

```text
Coffee Root
├─ Coffee Farming
├─ Machine Processing
├─ Drink Collection
├─ Cold Brew
├─ Ice Cream
├─ Bakery
│  └─ Cake Master
├─ Traditional Brewing
├─ Soda Shop
└─ Café Decoration
```

Advancement ID：

```text
coffeework:root
coffeework:coffee_farming
coffeework:machine_processing
coffeework:drink_collection
coffeework:cold_brew
coffeework:ice_cream
coffeework:bakery
coffeework:cake_master
coffeework:traditional_brewing
coffeework:soda_shop
coffeework:cafe_decoration
```

已有：

```text
coffeework:phonograph_play
```

建议将其 parent 改为：

```text
coffeework:cafe_decoration
```

---

## 7.2 使用 DataGen 生成主引导树

新增：

```text
src/main/java/net/langball/coffee/datagen/ModAdvancementProvider.java
```

结构：

```java
public final class ModAdvancementProvider
        implements AdvancementSubProvider {

    @Override
    public void generate(
            HolderLookup.Provider registries,
            Consumer<Advancement> output) {

        Advancement root =
                createRoot(output);

        Advancement farming =
                createCoffeeFarming(root, output);

        Advancement processing =
                createMachineProcessing(root, output);

        createDrinkCollection(root, output);
        createColdBrew(root, output);
        createIceCream(root, output);

        Advancement bakery =
                createBakery(root, output);

        createCakeMaster(bakery, output);
        createTraditionalBrewing(root, output);
        createSodaShop(root, output);
        createCafeDecoration(root, output);
    }
}
```

在 `DataGenerators` 注册：

```java
generator.addProvider(
        event.includeServer(),
        new AdvancementProvider(
                output,
                event.getLookupProvider(),
                List.of(
                        new ModAdvancementProvider())));
```

---

## 7.3 Root

触发：

```text
获得任意 Coffee Bean Tag 物品
```

继续使用：

```text
coffeework:coffee_beans
```

显示：

```text
icon: coffee_bean
background: brown_concrete
frame: task
toast: false
chat: false
```

---

## 7.4 Coffee Farming

触发建议：

```text
获得 coffee_bean_raw
或
获得 blueberry
```

如果定位为完整农业入门，可要求：

```text
coffee_bean_raw AND blueberry
```

推荐先用 OR，降低初期门槛。

奖励：

```text
25 XP
```

---

## 7.5 Machine Processing

触发：

```text
获得 coffee_powder
```

因为当前 Coffee Powder 的主要来源是 Grinder，能够代表玩家完成第一次机器加工。

如果未来 Coffee Powder 增加其他直接来源，应改用自定义机器完成 Trigger，而不是继续依赖 inventory_changed。

---

## 7.6 Drink Collection

建议作为 `challenge`。

要求同时获得代表性饮品：

```text
espresso
coffee_latte
coffee_black_tea
coffee_coldbrew
cocoa
任意 soda
```

为避免硬编码全部 Soda，可以新增：

```text
coffeework:soda_drinks
```

Item Tag。

Reward：

```text
100 XP
```

---

## 7.7 Cold Brew

满足其一：

```text
获得 coldbrew_bottle
获得 coffee_coldbrew
```

使用 OR requirements。

---

## 7.8 Ice Cream

新增 Item Tag：

```text
coffeework:icecreams
```

包括全部普通 Ice Cream。

触发：

```text
inventory_changed + tag coffeework:icecreams
```

---

## 7.9 Bakery

新增 Item Tag：

```text
coffeework:bakery_products
```

建议只包含最终可食用烘焙品，不包含 raw/model/base。

触发任意：

```text
bread
sandwich
pie
muffin
croissant
cake slice
```

---

## 7.10 Cake Master

作为 Bakery 子节点，frame 使用：

```text
challenge
```

要求获得代表性成品：

```text
cake_berry
cake_coffee
cake_lemon
cake_schwarzwald
cake_redvelvet
tiramisu
```

不要要求所有数十个蛋糕变体，避免 Advancement 变成纯清单劳动。

Reward：

```text
150 XP
```

---

## 7.11 Traditional Brewing

要求同时获得：

```text
espresso
coffee_turkish
```

这能够引导玩家完成：

```text
Moka Pot
Turkish Coffee Pot
```

两条传统冲煮路径。

---

## 7.12 Soda Shop

第一阶段只要求获得任意：

```text
#coffeework:soda_drinks
```

如果后续需要挑战节点，可另加：

```text
soda_collector
```

要求收集全部 Soda，但不属于 Phase 10 最低范围。

---

## 7.13 Café Decoration

建议通过放置动作触发，而不是只获取物品。

要求：

```text
放置 Wooden 或 Stone Bar Counter
放置 Plate
放置 Phonograph
```

三项全部完成。

可使用：

```text
minecraft:placed_block
```

为每个物品建立独立 Criterion。

已有 `phonograph_play` 作为其子节点。

---

## 7.14 Advancement 公共标签

新增：

```text
coffeework:soda_drinks
coffeework:icecreams
coffeework:cookie_icecreams
coffeework:sandwiches
coffeework:bakery_products
coffeework:cakes
coffeework:coffee_drinks
```

这些 Tag 同时可被：

```text
Advancement
JEI
村民交易分类
内容审计
```

复用。

---

## 7.15 Advancement 审计

新增：

```text
tools/audit_advancements.py
```

校验：

```text
所有 parent 存在
无循环 parent
所有 icon item 已注册
所有 title/description 有三语言
所有必需节点存在
root 有 background
非 root 不应重复 background
requirements 引用的 criteria 存在
无孤立节点
```

---

## 7.16 12.3 验收标准

```text
[ ] 新世界首次获得咖啡豆时打开 Root
[ ] 10 个功能节点全部显示
[ ] Cake Master 位于 Bakery 下
[ ] phonograph_play 位于 Café Decoration 下
[ ] 所有任务可在纯生存模式完成
[ ] 不依赖创造模式物品
[ ] 三语言完整
[ ] Advancement JSON 审计通过
```

---

# 8. 12.4 村民职业重构

## 8.1 职责

### Barista

负责：

```text
Coffee Drinks
Coffee Pots
Moka Pot
Turkish Pot
Coffee Pot
Cups
Glass Cups
Cold Brew equipment
```

不再出售：

```text
Cocoa Powder
Spices
通用材料
```

### Materials Trader

负责：

```text
Coffee crops
Blueberry
Vanilla
Lemon
Tea leaves
Cocoa material
Flour
Yeast
Spices
Gelatin
Soda base
Syrup containers
Syrup materials
```

### Food Trader

负责：

```text
Bread
Sandwiches
Pastries
Muffins
Pies
Ice Cream
Cake slices
少量完整 Cake
```

---

## 8.2 独立 POI

当前 Barista 和 Materials Trader 共用 Coffee Machine。

Phase 10 改为：

```text
Barista -> Coffee Machine
Materials Trader -> Grinder
Food Trader -> Oven
```

新增：

```java
public static final RegistryObject<PoiType>
        MATERIALS_POI = POI_TYPES.register(
                "materials_poi",
                () -> new PoiType(
                        ImmutableSet.copyOf(
                                ModBlocks.GRINDER.get()
                                        .getStateDefinition()
                                        .getPossibleStates()),
                        1,
                        1));
```

修改 Materials Trader：

```java
holder -> holder.value() ==
        MATERIALS_POI.get()
```

最终 POI 数量：

```text
3
```

---

## 8.3 交易规格结构

不要继续直接在 `registerTrades()` 中拼大量匿名数组。

新增：

```text
src/main/java/net/langball/coffee/villager/TradeSpec.java
```

```java
public record TradeSpec(
        Direction direction,
        Supplier<? extends ItemLike> item,
        int itemCount,
        int emeraldCount,
        int maxUses,
        int villagerXp,
        float priceMultiplier) {

    public enum Direction {
        PLAYER_BUYS,
        VILLAGER_BUYS
    }

    public VillagerTrades.ItemListing toListing() {
        return switch (direction) {
            case PLAYER_BUYS ->
                    new ModVillagers.ItemsForEmeralds(
                            new ItemStack(
                                    item.get(),
                                    itemCount),
                            emeraldCount,
                            maxUses,
                            villagerXp,
                            priceMultiplier);

            case VILLAGER_BUYS ->
                    new ModVillagers.EmeraldsForItems(
                            item.get(),
                            itemCount,
                            emeraldCount,
                            maxUses,
                            villagerXp);
        };
    }
}
```

新增：

```text
TradeCatalog.java
```

存放三个职业、五个等级的规格。

---

## 8.4 推荐交易等级

### Barista

| 等级 | 内容 |
|---|---|
| 1 | 买入 Raw Coffee Bean；出售 Cup、Glass Cup |
| 2 | 出售 Espresso、Americano、Latte、Coffee Pot |
| 3 | 出售 Cappuccino、Macchiato、Moka Pot |
| 4 | 出售 Cold Brew、Turkish Pot、Flavored Latte |
| 5 | 出售高级冷萃、Nitro、稀有饮品 |

### Materials Trader

| 等级 | 内容 |
|---|---|
| 1 | 买入 Coffee Bean Raw、Blueberry；出售 Seeds |
| 2 | 出售 Flour、Yeast、Syrup Empty |
| 3 | 出售 Vanilla、Lemon、Tea Leaf、Spices |
| 4 | 出售 Cocoa Powder、Gelatin、Soda Base |
| 5 | 出售稀有糖浆材料、Sakura 相关材料 |

### Food Trader

| 等级 | 内容 |
|---|---|
| 1 | Bread、Bagel、Toast |
| 2 | 基础 Sandwich |
| 3 | Croissant、Muffin、Pie |
| 4 | Large Sandwich、Mooncake、Ice Cream |
| 5 | Cake Slice、Tiramisu、少量完整 Cake |

---

## 8.5 推荐基础价格

价格只是初始值，最终需实测：

### Barista

```text
8 Cup              -> 1 Emerald
4 Glass Cup        -> 1 Emerald
Espresso           -> 2 Emerald
Americano          -> 2 Emerald
Latte              -> 3 Emerald
Coffee Pot         -> 6 Emerald
Moka Pot           -> 8 Emerald
Turkish Pot        -> 10 Emerald
Advanced Drink     -> 5–7 Emerald
```

### Materials Trader

```text
12 Coffee Bean Raw -> 1 Emerald
16 Blueberry       -> 1 Emerald
8 Flour            -> 1 Emerald
4 Yeast            -> 1 Emerald
8 Syrup Empty      -> 2 Emerald
4 Vanilla          -> 2 Emerald
4 Lemon            -> 2 Emerald
4 Soda Base        -> 3 Emerald
```

### Food Trader

```text
Bread / Toast      -> 1 Emerald
Basic Sandwich     -> 3 Emerald
Large Sandwich     -> 5 Emerald
Croissant / Muffin -> 3–4 Emerald
Pie                -> 5 Emerald
Ice Cream          -> 4 Emerald
Cake Slice         -> 4–6 Emerald
Whole Cake         -> 9–12 Emerald
```

---

## 8.6 防套利规则

全职业范围内，对同一物品：

```text
玩家从村民买入的每件价格
必须高于
村民从玩家收购的每件支付
```

推荐最低安全边际：

```text
sellCostPerItem >= buyPayoutPerItem × 1.5
```

示例：

```text
村民以 1 Emerald 收 8 Coffee Bean
buyPayoutPerItem = 0.125

若村民出售 Coffee Bean：
至少 1 Emerald / 4 个
sellCostPerItem = 0.25
```

但更推荐：

```text
同一物品不要同时存在买入和卖出
```

特别检查：

```text
Coffee Powder
Cocoa Powder
Instant Coffee Box
Cake Slice
Flour
Blueberry
```

---

## 8.7 自动套利测试

在 `TradeCatalog` 暴露 package-private 只读规格：

```java
static Map<ResourceLocation,
        Map<Integer, List<TradeSpec>>>
        specsForTesting()
```

新增 GameTest：

```java
@GameTest(template = "empty")
public static void villagerTradesHaveNoDirectArbitrage(
        GameTestHelper helper) {

    Map<Item, Double> highestPayout =
            new HashMap<>();

    Map<Item, Double> lowestSellCost =
            new HashMap<>();

    // 遍历所有职业和等级
    // VILLAGER_BUYS:
    // emeraldCount / itemCount
    //
    // PLAYER_BUYS:
    // emeraldCount / itemCount

    for (Item item : intersection) {
        double payout =
                highestPayout.get(item);

        double cost =
                lowestSellCost.get(item);

        helper.assertTrue(
                cost >= payout * 1.5,
                "Direct arbitrage for "
                        + ForgeRegistries.ITEMS
                                .getKey(item)
                        + ": payout="
                        + payout
                        + ", cost="
                        + cost);
    }

    helper.succeed();
}
```

还应检查：

```text
itemCount > 0
emeraldCount > 0
maxUses > 0
villagerXp >= 0
等级位于 1..5
```

---

## 8.8 12.4 验收标准

```text
[ ] 三个职业使用三个不同 POI
[ ] 三职业职责无明显重叠
[ ] 每个职业至少有 3 个等级，建议 5 个
[ ] 无同物品直接套利
[ ] 无零价格或负数配置
[ ] 所有交易物品存在生存获取路径
[ ] 交易内容在 Content Manifest 中可识别
[ ] 三种职业纹理和三语言名称正确
```

---

# 9. 12.5 剩余物品查漏补缺

# 9.1 Soda 饮料内容扩展

## 9.1.1 当前内容

当前：

```text
soda_caramel
soda_chocolate
soda_fruit
soda_mint
soda_vanilla
soda_sakura
```

Phase 10 最低扩展建议：

```text
soda_cola
soda_apple
soda_berry
soda_lemon
```

Legacy `cherry` 建议默认合并进：

```text
soda_sakura
```

除非项目明确新增独立 Cherry 原料。

最终最低目标：

```text
10 种 Soda
```

---

## 9.1.2 注册辅助方法

当前 Soda 注册重复大量 `DrinkCoffee` 构造代码。

在 `ModCoffeeItems` 增加：

```java
private static RegistryObject<Item> registerSoda(
        DeferredRegister<Item> items,
        String id,
        int nutrition,
        float saturation,
        Supplier<MobEffectInstance[]> effects) {

    return items.register(
            id,
            () -> new DrinkCoffee(
                    new Item.Properties()
                            .food(new FoodProperties.Builder()
                                    .nutrition(nutrition)
                                    .saturationMod(saturation)
                                    .alwaysEat()
                                    .build()),
                    effects.get(),
                    1,
                    () -> Items.GLASS_BOTTLE));
}
```

使用：

```java
ModItems.SODA_APPLE = registerSoda(
        items,
        "soda_apple",
        3,
        0.4F,
        () -> new MobEffectInstance[]{
                new MobEffectInstance(
                        MobEffects.REGENERATION,
                        160,
                        0)
        });
```

不要在 Supplier 外复用 `MobEffectInstance` 对象。

---

## 9.1.3 Soda 配方

目录：

```text
data/coffeework/recipes/soda_making/
```

Apple 示例：

```json
{
  "type": "coffeework:soda_making",
  "container": {
    "item": "minecraft:glass_bottle"
  },
  "base": {
    "item": "coffeework:soda"
  },
  "flavor": {
    "tag": "coffeework:fruits/apples"
  },
  "result": {
    "item": "coffeework:soda_apple"
  },
  "experience": 0.2,
  "processing_time": 200
}
```

Berry：

```json
"flavor": {
  "tag": "coffeework:fruits/berries"
}
```

Lemon：

```json
"flavor": {
  "item": "coffeework:lemon"
}
```

Cola 建议新增：

```text
coffeework:syrup_cola
```

配方：

```text
syrup_empty
caramel
cocoa_powder
sugar
```

并让 `syrup_cola` 返回 `syrup_empty`。

---

## 9.1.4 Soda 完整面

每个新 Soda 必须同步：

```text
ModItems 字段
ModCoffeeItems 注册
item model
item texture
三语言
soda_making JSON
Creative Tab
soda_drinks Tag
Drink Display mapping
JEI 自动发现
Advancement 自动识别
Content Manifest
Recipe Reachability
```

---

# 9.2 三明治生产链

## 9.2.1 当前内容

当前成品：

```text
sandwich_blt
sandwich_bacon_egg
sandwich_beef_cheese
sandwich_blt_large
sandwich_club
sandwich_club_large
sandwich_ham_cheese
```

Phase 10 不需要立即增加更多 ID，优先重构生产关系。

---

## 9.2.2 生产链目标

```text
Bread / Toast
+ Meat
+ Cheese / Egg / Vegetable
→ Basic Sandwich
→ Club / Large Sandwich
```

Large 版本应从小型成品升级，而不是重新重复全部原料。

推荐：

```text
2 × sandwich_blt
→ sandwich_blt_large

sandwich_bacon_egg
+ toast
+ cooked_chicken
→ sandwich_club

2 × sandwich_club
→ sandwich_club_large
```

---

## 9.2.3 食材 Tag

新增：

```text
coffeework:breads
coffeework:cooked_pork
coffeework:cooked_beef
coffeework:cooked_chicken
coffeework:sandwich_vegetables
coffeework:sandwiches
```

默认：

```json
{
  "replace": false,
  "values": [
    "minecraft:bread",
    "coffeework:toast",
    "coffeework:bread_round",
    "coffeework:baguette"
  ]
}
```

其他模组可以通过数据包追加兼容食材。

---

## 9.2.4 配方选择

普通 Sandwich 不需要自定义 RecipeSerializer。

使用：

```text
Shaped Recipe
Shapeless Recipe
Item Tag Ingredient
```

即可。

只有当需要：

```text
复制 NBT
条件返还多个容器
动态输出
```

时才创建 CustomRecipe。

不要为了“看起来高级”增加没有必要的 Serializer。

---

## 9.2.5 FoodProperties 统一

新增：

```text
src/main/java/net/langball/coffee/init/ModFoodProperties.java
```

```java
public final class ModFoodProperties {

    public static final FoodProperties
            SANDWICH_STANDARD =
            new FoodProperties.Builder()
                    .nutrition(10)
                    .saturationMod(0.9F)
                    .build();

    public static final FoodProperties
            SANDWICH_LARGE =
            new FoodProperties.Builder()
                    .nutrition(14)
                    .saturationMod(1.0F)
                    .build();

    private ModFoodProperties() {}
}
```

注册：

```java
new Item(
        new Item.Properties()
                .food(
                        ModFoodProperties
                                .SANDWICH_STANDARD))
```

避免每个注册点复制数值。

---

## 9.2.6 容器返还原则

Sandwich 本身没有合理的可重复使用容器。

不要让 Sandwich 食用后凭空返回 Plate。

容器返还只用于：

```text
配方明确消耗了 Mixing Bowl
配方明确消耗了 Mold
食物明确装在 Cup / Bottle / Bowl 中
```

如果未来引入：

```text
sandwich_assembly_plate
```

则该物品必须实现：

```java
hasCraftingRemainingItem()
getCraftingRemainingItem()
```

当前 Phase 10 最低范围无需新增。

---

## 9.2.7 三明治测试

测试：

```text
[ ] 所有 Basic Sandwich 可通过配方获得
[ ] Large Sandwich 必须依赖 Basic 成品
[ ] Tag 中的替代 Bread 可匹配
[ ] 非 Bread 物品不可匹配
[ ] 配方不会复制 Cheese / Meat
[ ] FoodProperties 与设计表一致
[ ] 所有 Sandwich 进入 sandwiches Tag
[ ] Bakery Advancement 能识别 Sandwich
```

---

# 9.3 剩余冰淇淋口味

## 9.3.1 当前内容

普通：

```text
vanilla
apple
berry
chocolate
coffee
lemon
melon
```

Cookie Ice Cream 同样有七种。

Phase 10 最低新增：

```text
icecream_caramel
icecream_mint
icecream_sakura
icecream_tea
```

对应 Cream：

```text
cream_caramel
cream_mint
cream_sakura
cream_tea
```

---

## 9.3.2 Cream 配方

Caramel：

```text
Mixing Bowl
Cream Milk
Syrup Caramel
→ Cream Caramel
```

Mint：

```text
Mixing Bowl
Cream Milk
Syrup Mint
→ Cream Mint
```

Sakura：

```text
Mixing Bowl
Cream Milk
Syrup Sakura
→ Cream Sakura
```

Tea：

```text
Mixing Bowl
Cream Milk
Tea Leaf
→ Cream Tea
```

现有：

```text
Mixing Bowl
Syrup Item
```

都已具有 crafting remainder 时，标准 Crafting 系统会同时返回容器。

必须写 GameTest 或配方级测试验证：

```text
Mixing Bowl 返回
Syrup Empty 返回
```

---

## 9.3.3 Icecream Machine 配方

保持当前单输入架构：

```json
{
  "type": "coffeework:icecream_making",
  "cookingtime": 400,
  "experience": 0.2,
  "ingredient": {
    "item": "coffeework:cream_caramel"
  },
  "result": {
    "item": "coffeework:icecream_caramel"
  }
}
```

不要为了四个新口味修改 Icecream Machine BlockEntity。

---

## 9.3.4 食用容器

Phase 10 默认假设当前 Ice Cream 是独立可食用成品，不返回 Cup。

只有在最终美术明确表示物品装在可重复使用杯中时，才执行以下扩展：

1. Icecream Machine 增加 Cup 输入槽；
2. 新增多输入 `IcecreamMakingRecipe`；
3. 消耗 Cream + Cup；
4. 食用后返回 Empty Cup。

这属于可选扩展，不是 Phase 10 最低验收项。

禁止只在 `finishUsingItem()` 返回 Cup，却不在生产时消耗 Cup，这会造成物品凭空生成。

---

## 9.3.5 Ice Cream Tag

新增：

```text
coffeework:icecreams
coffeework:cookie_icecreams
```

用途：

```text
Advancement
Creative Tab 审计
Villager Trade 分类
内容完整性审计
```

---

# 10. 内容定义矩阵

建议新增：

```text
docs/phase10_content_matrix.json
```

结构：

```json
{
  "soda_apple": {
    "family": "soda",
    "creative_tab": "drinks_and_machines",
    "recipe_type": "coffeework:soda_making",
    "jei_category": "soda_making",
    "advancement_tag": "coffeework:soda_drinks",
    "trade_profession": null,
    "displayable": true
  }
}
```

该文件不替代 Java 注册，而是作为 Phase 10 范围清单和审计输入。

允许状态：

```text
PLANNED
REGISTERED
RESOURCE_COMPLETE
RECIPE_COMPLETE
GUIDE_COMPLETE
DONE
```

所有 Phase 10 内容最终必须为：

```text
DONE
```

---

# 11. CI 与自动化门禁

Phase 10 最终 CI 顺序：

```text
1. Compile
2. DataGen clean rebuild
3. Content Manifest
4. Legacy Matrix
5. Content Surface Audit
6. Recipe Reachability
7. Creative Tab Audit
8. JEI Coverage Audit
9. Advancement Tree Audit
10. Trade Arbitrage Audit
11. GameTest Server
12. Build JAR
```

---

## 11.1 Clean DataGen

建议 CI 在临时副本中：

```bash
rm -rf src/generated/resources/data/coffeework/recipes
rm -rf src/generated/resources/data/coffeework/tags
rm -rf src/generated/resources/data/coffeework/advancements

./gradlew runData

git diff --exit-code
```

如果 DataGen 后有 diff，说明仓库生成资源不是最新状态。

---

## 11.2 Creative Tab 门禁

```text
registered items == union(all creative tabs)
missing == 0
unknown == 0
```

---

## 11.3 JEI 门禁

```text
required categories == registered categories
missing catalysts == 0
missing lang == 0
```

---

## 11.4 Advancement 门禁

```text
required nodes == 11
missing parent == 0
cycle == 0
missing translation == 0
unregistered icon == 0
```

---

## 11.5 Trade 门禁

```text
invalid amount == 0
invalid level == 0
direct arbitrage == 0
missing trade item == 0
```

---

# 12. GameTest 计划

新增或扩展：

```text
Phase10GameTests.java
```

建议不要继续把所有阶段测试堆入 `Phase9GameTests.java`。

测试分组：

## Creative

创造模式页属于客户端注册，不适合 GameTest，使用 Python 静态审计。

## JEI

JEI 属于客户端集成，使用：

```text
编译检查
静态覆盖审计
人工 Client Smoke Test
```

## Advancement

测试：

```text
Advancement JSON 静态审计
必要时测试自定义 Trigger
```

## Villager

GameTest：

```text
tradeCatalogHasNoDirectArbitrage
tradeSpecsHaveValidAmounts
professionsUseDistinctPois
```

## Soda

GameTest：

```text
allSodaRecipesMatchExpectedInputs
sodaFlavorTagsAcceptAlternatives
sodaReturnsFlavorContainerWhenPresent
sodaProducesExactlyOneBottle
```

## Sandwich

GameTest：

```text
largeSandwichDependsOnBaseSandwich
breadTagAlternativesMatch
invalidIngredientRejected
```

## Ice Cream

GameTest：

```text
newCreamRecipesReturnMixingBowl
syrupCreamRecipeReturnsEmptySyrupBottle
newIcecreamMachineRecipesComplete
```

---

# 13. 人工测试清单

## 13.1 Creative Tabs

```text
[ ] 三个 Tab 均显示
[ ] 搜索模式仍可找到全部物品
[ ] 顺序符合生产流程
[ ] 无明显重复和错分
```

## 13.2 JEI

```text
[ ] Soda R/U
[ ] Moka R/U
[ ] Turkish R/U
[ ] Drink Transform R/U
[ ] Display Conversion 提示
[ ] Catalyst 点击
[ ] Soda GUI 点击区域
```

## 13.3 Advancement

```text
[ ] 新世界 Root
[ ] 各功能节点自然触发
[ ] Toast / Chat 设置合理
[ ] 三语言文本不截断
[ ] 图标正确
```

## 13.4 Villagers

```text
[ ] 三个工作站生成正确职业
[ ] 拆除工作站后行为正常
[ ] 升级到 5 级时交易池合理
[ ] 无可重复套利循环
```

## 13.5 内容

```text
[ ] 10 种 Soda
[ ] 7 个 Sandwich 全部可达
[ ] 4 个新增 Ice Cream 口味
[ ] 容器返还不复制
[ ] Drink Display 可展示新增饮品
```

---

# 14. 建议提交拆分

## Commit 1

```text
Phase 10A: split creative tabs and add coverage audit
```

修改：

```text
ModCreativeTabs.java
三语言
build_content_manifest.py
audit_creative_tabs.py
```

## Commit 2

```text
Phase 10B: complete JEI machine and interaction coverage
```

修改：

```text
JEIRecipeTypes
JEICompat
Soda Category
Traditional Brewing Definition
Moka/Turkish BE
Display Category
语言
audit_jei_coverage.py
```

## Commit 3

```text
Phase 10C: add full advancement guidance tree
```

修改：

```text
ModAdvancementProvider
DataGenerators
Item Tags
三语言
audit_advancements.py
```

## Commit 4

```text
Phase 10D: rebalance villagers and prevent trade arbitrage
```

修改：

```text
ModVillagers
TradeSpec
TradeCatalog
Materials POI
GameTests
语言/纹理检查
```

## Commit 5

```text
Phase 10E: expand soda content
```

修改：

```text
ModItems
ModCoffeeItems
Soda recipes
models/textures/lang
display mapping
tags
GameTests
```

## Commit 6

```text
Phase 10F: close sandwich and ice cream content
```

修改：

```text
ModBakeryItems
ModFoodProperties
RecipeProvider
Item Tags
models/textures/lang
GameTests
```

## Commit 7

```text
Phase 10 RC: final content and guidance audit
```

只允许：

```text
测试
审计
资源缺失修复
配方可达性修复
文档
```

不再增加新功能。

---

# 15. Phase 10 Definition of Done

Phase 10 只有在以下全部满足时才能关闭：

```text
[ ] 三个 Creative Tab 完成
[ ] 所有注册物品至少进入一个 Tab
[ ] JEI 11 个目标分类全部可用
[ ] Soda RecipeType 只有一个 Runtime 实例
[ ] Moka/Turkish Runtime 与 JEI 共用定义
[ ] Advancement 11 节点完整
[ ] 三职业使用三独立 POI
[ ] 村民交易无直接套利
[ ] Soda 扩展完成
[ ] Sandwich 生产链完成
[ ] 新 Ice Cream 口味完成
[ ] 所有新增内容三语言完整
[ ] 所有新增内容有模型与纹理
[ ] 所有新增内容有生存获取来源
[ ] Recipe Reachability = 0 unreachable
[ ] Content Surface = 0 issues
[ ] GameTest 全通过
[ ] Clean DataGen 无 diff
[ ] 人工客户端 Smoke Test 通过
```

---

# 16. 编码 Agent 执行约束

编码 Agent 每次开始任务前必须：

1. 阅读本文；
2. 只执行一个 Commit 阶段；
3. 列出将修改的文件；
4. 修改后执行对应审计；
5. 不因测试失败而删除测试；
6. 不通过扩大兼容层掩盖开发阶段问题；
7. 不直接编辑生成 JSON 而忽略 DataGen；
8. 不把客户端 JEI 类引用进服务端公共加载路径；
9. 不在 Creative Tab 中遗漏新增 Item；
10. 不在交易表中同时添加未经审计的买卖对。

每个阶段完成后输出：

```text
实施内容
变更文件
新增测试
运行命令
测试结果
已知限制
下一阶段前置条件
```

---

# 17. 最终路线

```text
Phase 9 人工问题修复
→ Phase 10A Creative Tabs
→ Phase 10B JEI
→ Phase 10C Advancement
→ Phase 10D Villagers
→ Phase 10E Soda
→ Phase 10F Sandwich & Ice Cream
→ Phase 10 RC
→ 首个正式存档兼容版本
```

从 Phase 10 RC 发布以后，才开始对真实玩家存档建立正式兼容承诺。
