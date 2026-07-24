# Coffee Workshop — 移植计划 (1.12.2 → 1.20.1)

> 基于 Forge 官方 API 文档编写  
> 参考: [Forge Porting Guides](https://docs.minecraftforge.net/en/latest/legacy/porting/)  
> 文档日期: 2026-07-24

---

## 目录

1. [总体变更概览](#1-总体变更概览)
2. [构建系统 (build.gradle)](#2-构建系统-buildgradle)
3. [Mod 初始化体系](#3-mod-初始化体系)
4. [注册系统 (DeferredRegister)](#4-注册系统-deferredregister)
5. [Blocks (方块)](#5-blocks-方块)
6. [Items (物品) & 食物系统](#6-items-物品--食物系统)
7. [BlockEntities (旧 TileEntity)](#7-blockentities-旧-tileentity)
8. [GUI/Menu 系统](#8-guimenu-系统)
9. [Potions → Effects (药水/效果)](#9-potions--effects-药水效果)
10. [Recipes (配方)](#10-recipes-配方)
11. [Config (配置)](#11-config-配置)
12. [WorldGen (世界生成)](#12-worldgen-世界生成)
13. [Villagers (村民)](#13-villagers-村民)
14. [SidedProxy → DistExecutor](#14-sidedproxy--distexecutor)
15. [资源文件 (Assets)](#15-资源文件-assets)
16. [JEI 兼容](#16-jei-兼容)
17. [与其他 Mod 的兼容](#17-与其他-mod-的兼容)
18. [阶段化执行计划](#18-阶段化执行计划)
19. [逐文件工作量统计](#19-逐文件工作量统计)
20. [推荐的参考文档链接](#20-推荐的参考文档链接)

---

## 1. 总体变更概览

| 系统 | 1.12.2 | 1.20.1 | 改动级别 |
|---|---|---|---|
| 构建工具 | ForgeGradle 2.3 + MCP | ForgeGradle 6+ + Parchment/Official | 🔴 重写 |
| Mod 注解 | `@EventHandler` + `FML*Event` | `@SubscribeEvent` + ModLifecycle | 🔴 重写 |
| 代理模式 | `@SidedProxy` | `DistExecutor` (完全移除) | 🔴 重写 |
| 注册方式 | `ForgeRegistries.*.register()` + static fields | `DeferredRegister<T>` + `RegistryObject<T>` | 🔴 重写 |
| 物品值 | 基类/元数据 (meta/damage) 实现子类型 | `ItemStack` components + `CreativeModeTab` | 🔴 重写 |
| 食物 | `extends ItemFood` | `Item.Properties().food(FoodProperties)` | 🔴 重写 |
| 三方信息 | `NBTTagCompound` | `CompoundTag` | 🟡 改名 |
| 格子实体 | `TileEntity` + `IInventory` + `ISidedInventory` | `BlockEntity` + `IItemHandler` (Capability) | 🔴 重写 |
| GUI | `Container` + `GuiContainer` + `IGuiHandler` | `AbstractContainerMenu` + `AbstractContainerScreen` + `MenuType` | 🔴 重写 |
| 药水效果 | `extends Potion` + `PotionEffect` | `extends MobEffect` + `MobEffectInstance` | 🟡 改名/重写 |
| 配方 | `GameRegistry.addShapedRecipe()` (代码中) | JSON 文件 + `RecipeProvider` 数据生成 | 🔴 重写 |
| 配置 | `Configuration` + `event.getSuggestedConfigurationFile()` | `ForgeConfigSpec` + TOML | 🔴 重写 |
| 世界生成 | `GameRegistry.registerWorldGenerator()` + `OreGenEvent` | `Feature`/`PlacedFeature` + `BiomeModifier` | 🔴 重写 |
| 村民 | `VillagerRegistry` + `VillagerCareer` | `PoiType` + `VillagerProfession` + `VillagerTrades` | 🔴 重写 |
| 子类型 | 通过 meta/damage 值 + `getSubItems` | 每种变体独立 registry 对象或 `CreativeModeTab` 自定义 | 🔴 重写 |
| 模型注册 | `ModelLoader.setCustomModelResourceLocation()` 代码注册 | JSON 驱动 (`assets/<modid>/models/`) | 🟢 移除 |

> **级别说明**: 🔴 = 需要完全重写 / 🟡 = 需要较大修改 / 🟢 = 小改或不变

---

## 2. 构建系统 (build.gradle)

### 2.1 ForgeGradle 版本

```gradle
// 1.12.2 (当前)
buildscript {
    repositories { jcenter(); maven { url = "http://files.minecraftforge.net/maven" } }
    dependencies { classpath 'net.minecraftforge.gradle:ForgeGradle:2.3-SNAPSHOT' }
}
apply plugin: 'net.minecraftforge.gradle.forge'

minecraft {
    version = "1.12.2-14.23.4.2764"
    mappings = "snapshot_20171003"
}
```

```gradle
// 1.20.1 (目标)
plugins {
    id 'eclipse'
    id 'maven-publish'
    id 'net.minecraftforge.gradle' version '[6.0,6.2)'
}

minecraft {
    mappings channel: 'official', version: '1.20.1'  // 或 parchment
    // 或使用 Parchment: mappings channel: 'parchment', version: '2023.09.03-1.20.1'
}
```

### 2.2 关键构建变化

| 项目 | 1.12.2 | 1.20.1 |
|---|---|---|
| Java 版本 | Java 8 | Java 17 |
| Gradle 版本 | Gradle 4.x | Gradle 8.x |
| MCP → Official | MCP 映射 | Mojang 官方映射或 Parchment |
| 资源处理 | `processResources` + `mcmod.info` | `mods.toml` (META-INF) |
| Forge Maven | `files.minecraftforge.net/maven` | `maven.minecraftforge.net` |

### 2.3 MCModInfo → Mods.toml

**移除** `mcmod.info`，改为 `META-INF/mods.toml`:

```toml
modLoader="javafml"
loaderVersion="[47,)"
license="..."
[[mods]]
    modId="coffeeworkshop"
    version="1.2.8.1"
    displayName="Coffee Workshop"
    logoFile="logo.png"
    credits="..."
    authors="..."
    description='''
        Bring me a coffee. Get a move on!
    '''
    [[dependencies.coffeeworkshop]]
        modId="forge"
        mandatory=true
        versionRange="[47,)"
        ordering="NONE"
        side="BOTH"
```

---

## 3. Mod 初始化体系

### 3.1 主类改动 (CoffeeWork.java)

**1.12.2:**
```java
@Mod(modid = CoffeeWork.MODID, name = CoffeeWork.NAME, version = CoffeeWork.VERSION)
public class CoffeeWork {
    @Instance public static CoffeeWork instance;
    @SidedProxy(clientSide = "...ClientProxy", serverSide = "...CommonProxy")
    public static CommonProxy proxy;

    @EventHandler
    public void preInit(FMLPreInitializationEvent event) { proxy.preInit(event); }
    @EventHandler
    public void init(FMLInitializationEvent event) { proxy.init(event); }
    @EventHandler
    public void postInit(FMLPostInitializationEvent event) { proxy.postInit(event); }
}
```

**1.20.1:**
```java
@Mod(CoffeeWork.MODID)
public class CoffeeWork {
    public static final String MODID = "coffeeworkshop";
    public static CoffeeWork instance;

    public CoffeeWork() {
        instance = this;
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();

        // 所有 DeferredRegister 在此注入 mod 事件总线
        ModBlocks.BLOCKS.register(modBus);
        ModItems.ITEMS.register(modBus);
        ModBlockEntities.BLOCK_ENTITIES.register(modBus);
        ModMenuTypes.MENUS.register(modBus);
        ModEffects.EFFECTS.register(modBus);
        ModCreativeTabs.TABS.register(modBus);
        // ... 其他注册

        // 注册生命周期事件
        modBus.addListener(this::commonSetup);
        modBus.addListener(this::clientSetup);
    }

    private void commonSetup(FMLCommonSetupEvent event) {
        event.enqueueWork(() -> {
            // 线程安全的后初始化逻辑
            // 配方注册、世界生成注册、能力注册等
        });
    }

    private void clientSetup(FMLClientSetupEvent event) {
        // 客户端专用初始化
        // 如 Menu 和 Screen 的绑定: ScreenManager.register(...)
    }
}
```

### 3.2 Lifecycle 事件映射

| 1.12.2 | 1.20.1 | 说明 |
|---|---|---|
| `FMLPreInitializationEvent` | `RegisterEvent` (自动由 DeferredRegister 触发) + `FMLCommonSetupEvent` | 注册在 DeferredRegister 中自动完成 |
| `FMLInitializationEvent` | `FMLCommonSetupEvent` + `InterModEnqueueEvent` + `InterModProcessEvent` | 配方、兼容、networking |
| `FMLPostInitializationEvent` | `FMLLoadCompleteEvent` | 很少需要 |
| `FMLClientSetupEvent` (无) | `FMLClientSetupEvent` | 客户端 GUI 绑定等 |

### 3.3 注册类规划

将 1.12.2 中分散的 `XxxLoader(event)` 模式改为 **DeferredRegister 类**:

| 1.12.2 类 | 1.20.1 目标类 | 注册类型 |
|---|---|---|
| `BlockLoader` | `ModBlocks` | `DeferredRegister<Block>` |
| `ItemLoader` | `ModItems` | `DeferredRegister<Item>` |
| `DrinksLoader` | 合并到 `ModItems` | `DeferredRegister<Item>` |
| `TileEntityLoader` | `ModBlockEntities` | `DeferredRegister<BlockEntityType<?>>` |
| `GuiLoader` | `ModMenuTypes` | `DeferredRegister<MenuType<?>>` |
| `PotionLoader` | `ModEffects` | `DeferredRegister<MobEffect>` |
| — | `ModCreativeTabs` | `DeferredRegister<CreativeModeTab>` |
| `VillagerLoader` | `ModVillagers` | `DeferredRegister<VillagerProfession>` + `DeferredRegister<PoiType>` |

---

## 4. 注册系统 (DeferredRegister)

### 4.1 模板模式

所有注册对象使用 `DeferredRegister<T>` + `RegistryObject<T>` 静态字段模式:

```java
public class ModBlocks {
    public static final DeferredRegister<Block> BLOCKS =
            DeferredRegister.create(ForgeRegistries.BLOCKS, CoffeeWork.MODID);

    public static final RegistryObject<Block> GRINDER = BLOCKS.register("grinder",
            () -> new BlockGrinder(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.STONE)
                    .requiresCorrectToolForDrops()
            ));

    public static final RegistryObject<Block> GRINDER_ON = BLOCKS.register("grinder_on",
            () -> new BlockGrinder(BlockBehaviour.Properties.of()
                    .mapColor(MapColor.STONE)
                    .strength(3.0f, 5.0f)
                    .sound(SoundType.STONE)
                    .lightLevel(state -> 14)
                    .requiresCorrectToolForDrops()
            ));
}
```

### 4.2 BlockItem 注册

BlockItem 通过 `DeferredRegister<Item>` 单独注册:

```java
public class ModItems {
    public static final DeferredRegister<Item> ITEMS =
            DeferredRegister.create(ForgeRegistries.ITEMS, CoffeeWork.MODID);

    // BlockItem: registry name 必须与 Block 一致
    public static final RegistryObject<Item> GRINDER_ITEM = ITEMS.register("grinder",
            () -> new BlockItem(ModBlocks.GRINDER.get(), new Item.Properties()));
}
```

### 4.3 BlockEntityType 注册

```java
public class ModBlockEntities {
    public static final DeferredRegister<BlockEntityType<?>> BLOCK_ENTITIES =
            DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<BlockEntityType<TileEntityGrinder>> GRINDER =
            BLOCK_ENTITIES.register("grinder",
                    () -> BlockEntityType.Builder.of(
                            TileEntityGrinder::new,
                            ModBlocks.GRINDER.get(),
                            ModBlocks.GRINDER_ON.get()
                    ).build(null));
}
```

### 4.4 MenuType 注册

```java
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<MenuType<ContainerGrinder>> GRINDER =
            MENUS.register("grinder",
                    () -> IForgeMenuType.create(ContainerGrinder::new));
}
```

### 4.5 从 meta/damage 子类型拆分

1.12.2 大量使用 meta 值实现子类型（如 `ItemBase` 和 `ItemFoodBasic` 支持几十种 meta 变体）。

**1.20.1 不推荐使用 meta**。建议策略:
- **简单变体**（如面包风味): 每个变体一个独立的 `RegistryObject<Item>`，汇总到同个 CreativeModeTab
- **复杂变体**（如饮品有药水效果差异): 使用 `ItemStack` components (1.20.5+) 或保留少量可控子类
- **或**继续使用 `CreativeModeTab` 的 `displayItems` 填充生成带 NBT 的 ItemStack

> **注意**: 1.20.1 中 `Item` 的 `getSubItems(CreativeTabs, NonNullList)` 已被 `fillItemCategory(CreativeModeTab, NonNullList)` 取代，且 Forge 更推荐用 `BuildCreativeModeTabContentsEvent`。

---

## 5. Blocks (方块)

### 5.1 Block 类 API 映射

| 1.12.2 | 1.20.1 |
|---|---|
| `extends Block` | `extends Block` (同) |
| `Material` (构造参数) | `BlockBehaviour.Properties.of().mapColor(...)` |
| `Block.setHardness(float)` | `BlockBehaviour.Properties.of().strength(float)` |
| `Block.setResistance(float)` | `BlockBehaviour.Properties.of().strength(float hardness, float resistance)` |
| `Block.setLightLevel(float)` | `BlockBehaviour.Properties.of().lightLevel(state -> int)` |
| `Block.setHarvestLevel("pickaxe", 0)` | `BlockBehaviour.Properties.of().requiresCorrectToolForDrops()` + 标签系统 |
| `Block.setSoundType(SoundType)` | `BlockBehaviour.Properties.of().sound(SoundType)` |
| `Block.setCreativeTab(CreativeTabs)` | 通过 `BlockItem` 和 `BuildCreativeModeTabContentsEvent` 添加 |
| `Block.setUnlocalizedName(String)` | 移除，由 `getDescriptionId()` 自动从注册名生成 |
| `IBlockState` | `BlockState` |
| `EnumFacing` | `Direction` |
| `AxisAlignedBB` | `AABB` (net.minecraft.world.phys) |
| `EntityPlayer` | `Player` (net.minecraft.world.entity.player) |
| `World` | `Level` (net.minecraft.world.level) |
| `BlockPos` | `BlockPos` (net.minecraft.core) |
| `EnumHand` | `InteractionHand` |
| `ItemStack` | `ItemStack` (同) |

### 5.2 BlockState 系统变更

```java
// 1.12.2
public static final PropertyDirection FACING = BlockHorizontal.FACING;
protected BlockStateContainer createBlockState() {
    return new BlockStateContainer(this, FACING);
}
public IBlockState getStateFromMeta(int meta) { ... }
public int getMetaFromState(IBlockState state) { ... }

// 1.20.1
public static final Property<Direction> FACING = HorizontalDirectionalBlock.FACING;
protected void createBlockStateDefinition(StateDefinition.Builder<Block, BlockState> builder) {
    builder.add(FACING);
}
// 元数据不存在了, 直接使用 BlockState 属性
```

### 5.3 Block 方法映射

| 1.12.2 | 1.20.1 |
|---|---|
| `onBlockActivated(World, BlockPos, IBlockState, EntityPlayer, EnumHand, EnumFacing, float, float, float)` | `use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)` |
| `onBlockAdded(World, BlockPos, IBlockState)` | `onPlace(BlockState, Level, BlockPos, BlockState, boolean)` |
| `breakBlock(World, BlockPos, IBlockState)` | `onRemove(BlockState, Level, BlockPos, BlockState, boolean)` |
| `neighborChanged(IBlockState, World, BlockPos, Block, BlockPos)` | `neighborChanged(BlockState, Level, BlockPos, Block, BlockPos, boolean)` |
| `getBoundingBox(IBlockState, IBlockAccess, BlockPos)` | `getShape(BlockState, BlockGetter, BlockPos, CollisionContext)` |
| `getCollisionBoundingBox(IBlockState, IBlockAccess, BlockPos)` | `getCollisionShape(BlockState, BlockGetter, BlockPos, CollisionContext)` |
| `isOpaqueCube(IBlockState)` | 移除，使用 `BlockBehaviour.Properties.noOcclusion()` |
| `isFullCube(IBlockState)` | 移除 |
| `isFullBlock(IBlockState)` | `BlockState.isSolid()` |
| `getBlockLayer()` → `BlockRenderLayer.CUTOUT` | 移除（JSON 模型定义 `render_type`） |
| `getItem(World, BlockPos, IBlockState)` | `getCloneItemStack(BlockState, HitResult, BlockGetter, BlockPos)` |
| `getStateForPlacement(...)` | `getStateForPlacement(BlockPlaceContext)` |
| `withRotation(...)` / `withMirror(...)` | 同 |

### 5.4 BlockCoffee.java 改动要点

该 Block 是咖啡杯碟方块，有以下关键变更:
- `Material.ROCK` → `BlockBehaviour.Properties.of().mapColor(MapColor.STONE)`
- `BlockHorizontal.FACING` → `HorizontalDirectionalBlock.FACING`
- `AxisAlignedBB` → `AABB`
- `getBlockLayer()` → 移除，JSON 模型处理渲染层
- `isOpaqueCube()` / `isNormalCube()` / `isBlockNormalCube()` → 全部用 `.noOcclusion()` 替代
- `canPlaceBlockAt(World, BlockPos)` → `canSurvive(BlockState, Level, BlockPos)`
- `onBlockActivated` → `use`
- `EntityItem.dropItem(...)` → `ItemEntity` + `popResource(Level, BlockPos, ItemStack)` 或 `Containers.dropItemStack`

---

## 6. Items (物品) & 食物系统

### 6.1 Item 基本 API 映射

| 1.12.2 | 1.20.1 |
|---|---|
| `Item.setUnlocalizedName(String)` | 移除，`getDescriptionId()` 自动从 RegistryName 生成 |
| `Item.setRegistryName(String)` | 由 `DeferredRegister` 自动处理 |
| `Item.setCreativeTab(CreativeTabs)` | 通过 `BuildCreativeModeTabContentsEvent` 或 CreativeModeTab 构建器 |
| `Item.setMaxStackSize(int)` | `Item.Properties().stacksTo(int)` |
| `Item.setContainerItem(Item)` | `Item.Properties().craftRemainder(Item)` |
| `Item.getSubItems(CreativeTabs, NonNullList)` | `fillItemCategory(CreativeModeTab, NonNullList)` / `BuildCreativeModeTabContentsEvent` |
| `ModelLoader.setCustomModelResourceLocation(...)` | 移除，JSON 模型驱动 |

### 6.2 食物系统 (ItemFood → FoodProperties)

**1.12.2:**
```java
public class DrinkCoffee extends ItemFood {
    public DrinkCoffee(...) {
        super(amounts[0], saturations[0], false);
        this.setAlwaysEdible();
    }
    @Override
    public int getHealAmount(ItemStack stack) { ... }
    @Override
    public float getSaturationModifier(ItemStack stack) { ... }
}
```

**1.20.1:**
```java
public class DrinkCoffee extends Item {
    public DrinkCoffee(Properties properties) {
        super(properties);
    }

    // 使用 FoodProperties 组件
    public static final FoodProperties COFFEE_FOOD = new FoodProperties.Builder()
            .nutrition(2)
            .saturationMod(0.2f)
            .alwaysEat()
            .effect(() -> new MobEffectInstance(ModEffects.CAFFEINE.get(), 3600, 0), 1.0f)
            .effect(() -> new MobEffectInstance(ModEffects.RELAX.get(), 600, 1), 1.0f)
            .build();

    // 注册时:
    public static final RegistryObject<Item> COFFEE_AMERICANO = ITEMS.register("coffee_americano",
            () -> new DrinkCoffee(new Item.Properties()
                    .stacksTo(1)
                    .food(DrinkCoffee.AMERICANO_FOOD)));
}
```

### 6.3 ItemDrink (饮品) 的特殊处理

原 `DrinkCoffee` 继承 `ItemFood` 并实现:
- `getHealAmount` 和 `getSaturationModifier` 返回基于 meta 的值
- `onItemUseFinish` (原 `onFoodEaten`) 饮用逻辑
- `getSubItems` 填充子类型变体
- 额外药水效果逻辑
- TAN (ToughAsNails) 兼容接口

**移植策略**: 所有饮品种类改为独立的 `RegistryObject<Item>`。

### 6.4 原 ItemLoader 中大量子类型项的处理

`ItemLoader.java` (25KB) 中有大量 `ItemBase` 和 `ItemFoodBasic` 实例:
- `materials` (27 种meta): 每种拆为独立 `RegistryObject<Item>`
- `bread` (4 种meta): 拆为独立面包
- `cake_slices` (18 种meta): 拆为独立切片
- `dessert_1` (18 种meta): 拆为独立甜品
- 等等

**建议**: 写一个工具类或直接逐项注册，避免 meta 依赖。可以用 `EnumMap` 或静态内部类组织同系列。

### 6.5 CreativeModeTab 配置

```java
public class ModCreativeTabs {
    public static final DeferredRegister<CreativeModeTab> TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, CoffeeWork.MODID);

    public static final RegistryObject<CreativeModeTab> COFFEE_TAB = TABS.register("coffee_workshop",
            () -> CreativeModeTab.builder()
                    .title(Component.translatable("itemGroup.coffee_workshop"))
                    .icon(() -> new ItemStack(ModItems.CUP.get()))
                    .displayItems((params, output) -> {
                        output.accept(ModBlocks.GRINDER.get());
                        output.accept(ModItems.COFFEE_BEAN.get());
                        // ... 所有物品和方块
                    })
                    .build());
}
```

---

## 7. BlockEntities (旧 TileEntity)

### 7.1 核心类映射

| 1.12.2 | 1.20.1 |
|---|---|
| `extends TileEntityLockable implements ITickable, ISidedInventory` | `extends BlockEntity` + 实现 tick 方法 + `IItemHandler` 能力 |
| `ITickable` → `void update()` | `BlockEntity` 静态 `tick(Level, BlockPos, BlockState, BlockEntity)` 方法 |
| `IInventory` | 移除 → `IItemHandler` (能力系统) |
| `ISidedInventory` | 移除 → `SidedInvWrapper` 或自定义 `IItemHandler` |
| `NonNullList<ItemStack>` | `NonNullList<ItemStack>` (同) |
| `ItemStackHelper` | `ContainerHelper` |
| `NBTTagCompound` | `CompoundTag` |
| `NBTTagList` | `ListTag` |
| `writeToNBT(NBTTagCompound)` | `saveAdditional(CompoundTag)` |
| `readFromNBT(NBTTagCompound)` | `load(CompoundTag)` |
| `getUpdateTag()` / `handleUpdateTag()` | 基本同 |
| `getUpdatePacket()` / `onDataPacket()` | 基本同，包类型变为 `ClientboundBlockEntityDataPacket` |

### 7.2 注册样式变更

```java
// 1.12.2
GameRegistry.registerTileEntity(TileEntityGrinder.class, new ResourceLocation(MODID, "Grinder"));

// 1.20.1 (DeferredRegister)
public static final RegistryObject<BlockEntityType<TileEntityGrinder>> GRINDER =
        BLOCK_ENTITIES.register("grinder",
                () -> BlockEntityType.Builder.of(TileEntityGrinder::new,
                        ModBlocks.GRINDER.get(),
                        ModBlocks.GRINDER_ON.get()
                ).build(null));
```

### 7.3 方块关联 BlockEntity

```java
// 1.20.1 Block 类需要实现 EntityBlock 接口
public class BlockGrinder extends Block implements EntityBlock {
    @Nullable
    @Override
    public BlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new TileEntityGrinder(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(Level level, BlockState state,
            BlockEntityType<T> type) {
        return type == ModBlockEntities.GRINDER.get()
                ? TileEntityGrinder::tick
                : null;
    }
}
```

### 7.4 Tick 方法变更

```java
// 1.12.2 → 实现 ITickable
public void update() {
    // tick logic (非静态)
}

// 1.20.1 → 静态工厂方法
public static void tick(Level level, BlockPos pos, BlockState state, TileEntityGrinder blockEntity) {
    // tick logic (需要传入实例)
}
```

### 7.5 库存系统 IInventory → IItemHandler

**1.12.2** 使用 `IInventory`、`ISidedInventory`、`SlotFurnaceFuel` 等接口。

**1.20.1** 使用 Forge Capability 系统:

```java
// BlockEntity 中
private final IItemHandler itemHandler = new ItemStackHandler(3) {
    @Override
    protected void onContentsChanged(int slot) {
        setChanged();
    }
};
private final LazyOptional<IItemHandler> lazyHandler = LazyOptional.of(() -> itemHandler);

// 暴露 Capability
@Override
public <T> LazyOptional<T> getCapability(Capability<T> cap, Direction side) {
    if (cap == ForgeCapabilities.ITEM_HANDLER) {
        if (side == null) return lazyHandler.cast();
        // 根据 side 返回侧面/顶底等不同访问权限的 handler
        return lazyHandler.cast();
    }
    return super.getCapability(cap, side);
}
```

**Container 中使用**:
```java
// 原: Slot(inputInventory, index, x, y)
// 新:
this.addSlot(new SlotItemHandler(itemHandler, index, x, y));
```

### 7.6 数据同步

```java
// 1.20.1 BlockEntity 数据同步

@Override
public CompoundTag getUpdateTag() {
    CompoundTag tag = new CompoundTag();
    saveAdditional(tag);
    return tag;
}

@Override
public Packet<ClientGamePacketListener> getUpdatePacket() {
    return ClientboundBlockEntityDataPacket.create(this);
}
```

---

## 8. GUI/Menu 系统

### 8.1 架构变更

| 1.12.2 | 1.20.1 |
|---|---|
| `Container` | `AbstractContainerMenu` |
| `GuiContainer` | `AbstractContainerScreen` |
| `IGuiHandler` + `NetworkRegistry.registerGuiHandler()` | 完全移除 → `MenuType` + `MenuProvider` + `NetworkHooks.openScreen()` |
| `Slot` 直接使用 | `SlotItemHandler` (配合 IItemHandler) |
| `player.inventory` (IInventory) | `Inventory` 参数同 |

### 8.2 MenuType 注册

```java
public class ModMenuTypes {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(ForgeRegistries.MENU_TYPES, CoffeeWork.MODID);

    public static final RegistryObject<MenuType<ContainerGrinder>> GRINDER =
            MENUS.register("grinder",
                    () -> IForgeMenuType.create((windowId, inv, data) ->
                            new ContainerGrinder(windowId, inv, data.readBlockPos())));
}
```

### 8.3 Container → AbstractContainerMenu

```java
// 1.20.1 模板
public class ContainerGrinder extends AbstractContainerMenu {
    private final IItemHandler itemHandler;

    // 客户端构造 (从 MenuType factory 调用)
    public ContainerGrinder(int containerId, Inventory playerInv, BlockPos pos) {
        this(containerId, playerInv,
                playerInv.player.level().getBlockEntity(pos) != null
                        ? ((TileEntityGrinder) playerInv.player.level().getBlockEntity(pos)).getItemHandler()
                        : new ItemStackHandler(3));
    }

    // 服务端构造
    public ContainerGrinder(int containerId, Inventory playerInv, IItemHandler handler) {
        super(ModMenuTypes.GRINDER.get(), containerId);
        this.itemHandler = handler;

        // 机器槽位
        this.addSlot(new SlotItemHandler(handler, 0, x, y)); // 输入
        this.addSlot(new SlotItemHandler(handler, 1, x, y)); // 燃料
        this.addSlot(new SlotItemHandler(handler, 2, x, y)); // 输出 (只读)

        // 玩家背包
        for (int i = 0; i < 3; i++)
            for (int j = 0; j < 9; j++)
                this.addSlot(new Slot(playerInv, j + i * 9 + 9, x, y));

        // 玩家快捷栏
        for (int k = 0; k < 9; k++)
            this.addSlot(new Slot(playerInv, k, x, y));
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) { ... }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(ContainerLevelAccess.NULL, player, ModBlocks.GRINDER.get());
    }
}
```

### 8.4 GuiContainer → AbstractContainerScreen

```java
public class GuiGrinder extends AbstractContainerScreen<ContainerGrinder> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CoffeeWork.MODID, "textures/gui/grinder.png");

    public GuiGrinder(ContainerGrinder container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        // 1.12.2: drawGuiContainerBackgroundLayer → renderBg
        // 1.12.2: mc.getTextureManager() → guiGraphics.blit()
        guiGraphics.blit(TEXTURE, leftPos, topPos, 0, 0, imageWidth, imageHeight);
    }
}
```

### 8.5 打开 GUI

```java
// 1.12.2
player.openGui(CoffeeWork.instance, GuiLoader.GUI_BLAST, world, x, y, z);

// 1.20.1 (在 Block.use 方法中)
if (!level.isClientSide) {
    BlockEntity be = level.getBlockEntity(pos);
    if (be instanceof MenuProvider menuProvider) {
        NetworkHooks.openScreen((ServerPlayer) player, menuProvider, pos);
    }
}
return InteractionResult.sidedSuccess(level.isClientSide);
```

### 8.6 注册 Screen

```java
// 在 FMLClientSetupEvent 中
@SubscribeEvent
public static void onClientSetup(FMLClientSetupEvent event) {
    event.enqueueWork(() -> {
        ScreenManager.register(ModMenuTypes.GRINDER.get(), GuiGrinder::new);
    });
}
```

### 8.7 ContainerData 同步进度条

```java
// 替代原 Container 中直接使用 inventory fields 的方式
// 使用 ContainerData 接口:
public class TileEntityGrinder extends BlockEntity {
    protected final ContainerData data = new ContainerData() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> cookTime;
                case 1 -> totalCookTime;
                case 2 -> burnTime;
                default -> 0;
            };
        }
        @Override
        public void set(int index, int value) { ... }
        @Override
        public int getCount() { return 3; }
    };
}

// 在 Container 中:
this.addDataSlots(((TileEntityGrinder) be).data);
// Client 端用 SimpleContainerData(3)
```

---

## 9. Potions → Effects (药水/效果)

### 9.1 API 映射

| 1.12.2 | 1.20.1 |
|---|---|
| `extends Potion` | `extends MobEffect` |
| `PotionEffect` | `MobEffectInstance` |
| `new PotionEffect(potion, duration, amplifier)` | `new MobEffectInstance(mobEffect, duration, amplifier)` |
| `Potion.isBadEffect()` | `MobEffect.getCategory()` → `MobEffectCategory.BENEFICIAL/HARMFUL/NEUTRAL` |
| `Potion.setIconIndex(...)` | 移除，使用 JSON 纹理 |
| `Potion.setLiquidColor(...)` | 通过 JSON 或构造参数 |
| `ForgeRegistries.POTIONS` | `ForgeRegistries.MOB_EFFECTS` (1.20.1) |
| `PotionType` (用于酿造) | 移除，改用数据驱动 |

### 9.2 注册

```java
public class ModEffects {
    public static final DeferredRegister<MobEffect> EFFECTS =
            DeferredRegister.create(ForgeRegistries.MOB_EFFECTS, CoffeeWork.MODID);

    public static final RegistryObject<MobEffect> CAFFEINE = EFFECTS.register("caffeine",
            () -> new PotionCaffeine(MobEffectCategory.BENEFICIAL, 0x8B4513));

    public static final RegistryObject<MobEffect> RELAX = EFFECTS.register("relax",
            () -> new PotionRelax(MobEffectCategory.BENEFICIAL, 0x98FB98));

    public static final RegistryObject<MobEffect> GOLDEN_HEART = EFFECTS.register("golden_heart",
            () -> new PotionGoldenHeart(MobEffectCategory.BENEFICIAL, 0xFFD700));
}
```

### 9.3 效果类改写

```java
// 1.12.2
public class PotionCaffeine extends Potion {
    protected PotionCaffeine(boolean isBad, int color) {
        super(isBad, color);
    }
}

// 1.20.1
public class PotionCaffeine extends MobEffect {
    public PotionCaffeine(MobEffectCategory category, int color) {
        super(category, color);
    }

    @Override
    public void applyEffectTick(LivingEntity entity, int amplifier) {
        // 原 performEffect 逻辑
    }

    @Override
    public boolean isDurationEffectTick(int duration, int amplifier) {
        // 原 isReady 逻辑
        return true;
    }
}
```

---

## 10. Recipes (配方)

### 10.1 大变革

| 1.12.2 | 1.20.1 |
|---|---|
| `GameRegistry.addShapedRecipe(...)` | 移除 → JSON 文件 `data/<modid>/recipes/*.json` |
| `GameRegistry.addShapelessRecipe(...)` | 移除 → JSON 文件 |
| `GameRegistry.addSmelting(...)` | 移除 → JSON 文件 |
| 自定义 IRecipe | `Recipe` 接口 (修改) |
| ShapedOreRecipe | Forge 配方默认支持标签作为输入 |

### 10.2 配方类型映射

```json
// 1.12.2 代码: GameRegistry.addShapedRecipe(...)
// 1.20.1 JSON: data/coffeeworkshop/recipes/coffee_americano.json
{
    "type": "minecraft:crafting_shaped",
    "pattern": [
        "###",
        " # ",
        " # "
    ],
    "key": {
        "#": {
            "tag": "coffeeworkshop:coffee_bean_roasted"
        }
    },
    "result": {
        "item": "coffeeworkshop:coffee_americano"
    }
}
```

```json
// 熔炉配方
{
    "type": "minecraft:smelting",
    "ingredient": {
        "item": "coffeeworkshop:coffee_bean_raw"
    },
    "result": "coffeeworkshop:coffee_bean",
    "experience": 0.3,
    "cookingtime": 200
}
```

### 10.3 数据生成 (推荐替代手动写 JSON)

写一个 `RecipeProvider` 类来自动生成所有配方 JSON:

```java
public class ModRecipeProvider extends RecipeProvider {
    @Override
    protected void buildRecipes(RecipeOutput output) {
        ShapedRecipeBuilder.shaped(RecipeCategory.FOOD, ModItems.COFFEE_AMERICANO.get())
                .pattern("###")
                .define('#', ModItems.COFFEE_BEAN.get())
                .unlockedBy("has_coffee_bean", has(ModItems.COFFEE_BEAN.get()))
                .save(output);

        SimpleCookingRecipeBuilder.smelting(Ingredient.of(ModItems.COFFEE_BEAN_RAW.get()),
                        RecipeCategory.FOOD, ModItems.COFFEE_BEAN.get(), 0.3f, 200)
                .unlockedBy("has_coffee_bean_raw", has(ModItems.COFFEE_BEAN_RAW.get()))
                .save(output);
    }
}
```

### 10.4 自定义机器配方

原 `GrinderRecipes`、`CoffeeMachineRecipes` 等使用简单 `HashMap` + 静态 getter 模式。移植后同样可行:

```java
public class GrinderRecipes {
    private static final Map<Ingredient, ItemStack> RECIPES = new HashMap<>();

    public static void addRecipe(Ingredient input, ItemStack output) {
        RECIPES.put(input, output);
    }

    public static ItemStack getResult(ItemStack input) {
        for (var entry : RECIPES.entrySet()) {
            if (entry.getKey().test(input))
                return entry.getValue().copy();
        }
        return ItemStack.EMPTY;
    }
}
```

配方注册逻辑移动到 `FMLCommonSetupEvent` 的 `enqueueWork` 中。

---

## 11. Config (配置)

### 11.1 API 变更

| 1.12.2 | 1.20.1 |
|---|---|
| `net.minecraftforge.common.config.Configuration` | 完全移除 |
| `event.getSuggestedConfigurationFile()` | 移除 |
| `new Configuration(file)` | `ForgeConfigSpec.Builder` |
| `.get(Configuration.CATEGORY_GENERAL, key, default)` | `.defineInRange(key, default, min, max)` 或 `.define(key, default)` |
| `.getInt()` / `.getString()` | `ConfigValue::get()` |

### 11.2 新样式

```java
public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue COFFEE_TREE_RARITY;
    public static final ForgeConfigSpec.IntValue BLUEBERRY_RARITY;

    static {
        BUILDER.push("worldgen");
        COFFEE_TREE_RARITY = BUILDER
                .comment("Coffee tree rarity (1-20)")
                .defineInRange("coffee_tree_rarity", 2, 1, 20);
        BLUEBERRY_RARITY = BUILDER
                .comment("Blueberry bush rarity (1-20)")
                .defineInRange("blueberry_rarity", 2, 1, 20);
        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
```

### 11.3 注册 Config

```java
// 在 Mod 构造器中
ModLoadingContext.get().registerConfig(net.minecraftforge.fml.config.ModConfig.Type.COMMON, ModConfig.SPEC);
```

---

## 12. WorldGen (世界生成)

### 12.1 架构变更

| 1.12.2 | 1.20.1 |
|---|---|
| `GameRegistry.registerWorldGenerator(...)` | 移除 → `Feature`/`ConfiguredFeature`/`PlacedFeature` |
| `extends WorldGenerator` | `extends Feature<NoneFeatureConfiguration>` |
| `MinecraftForge.ORE_GEN_BUS` + `OreGenEvent` | 完全移除 → `BiomeModifier` 系统 |
| `@SubscribeEvent onOreGenPost` | `BiomeModifier` 或 `BiomeLoadingEvent` |

### 12.2 新世界生成架构

Forge 1.20.1 世界生成分成三层:

1. **Feature**: 生成本身逻辑 (`Feature<Configuration>`)
2. **ConfiguredFeature**: Feature + 配置（JSON 文件 `data/<modid>/worldgen/configured_feature/`）
3. **PlacedFeature**: ConfiguredFeature + 放置规则（JSON 或 BiomeModifier）

### 12.3 代码实现

```java
// 1. Feature 类
public class CoffeeTreeFeature extends Feature<NoneFeatureConfiguration> {
    public CoffeeTreeFeature() {
        super(NoneFeatureConfiguration.CODEC);
    }

    @Override
    public boolean place(FeaturePlaceContext<NoneFeatureConfiguration> context) {
        // 放置逻辑 (原 CoffeeTreeWorldGen.generate 移植过来)
        // Context 包含: level(), random(), origin(), config()
        return true;
    }
}
```

```java
// 2. 注册 Feature
public static final RegistryObject<Feature<NoneFeatureConfiguration>> COFFEE_TREE =
        ModFeatures.FEATURES.register("coffee_tree", CoffeeTreeFeature::new);
```

```json
// 3. configured_feature JSON:
// data/coffeeworkshop/worldgen/configured_feature/coffee_tree.json
{
    "type": "coffeeworkshop:coffee_tree",
    "config": {}
}
```

```json
// 4. placed_feature JSON:
// data/coffeeworkshop/worldgen/placed_feature/coffee_tree.json
{
    "feature": "coffeeworkshop:coffee_tree",
    "placement": [
        { "type": "minecraft:rarity_filter", "chance": 12 },
        { "type": "minecraft:in_square" },
        { "type": "minecraft:heightmap", "heightmap": "WORLD_SURFACE_WG" },
        { "type": "minecraft:biome" }
    ]
}
```

```json
// 5. biome_modifier JSON:
// data/coffeeworkshop/forge/biome_modifier/add_coffee_tree.json
{
    "type": "forge:add_features",
    "biomes": "#minecraft:is_overworld",
    "features": "coffeeworkshop:coffee_tree",
    "step": "vegetal_decoration"
}
```

### 12.4 Soda Ore 的变更

原使用 `OreGenEvent.Post` 以 ANDESITE 位置生成苏打矿石。1.20.1 不再需要 hack，改为正规 ore feature:

```json
// configured_feature/soda_ore.json
{
    "type": "minecraft:ore",
    "config": {
        "size": 8,
        "discard_chance_on_air_exposure": 0.0,
        "targets": [
            {
                "target": {
                    "block": "coffeeworkshop:soda_ore",
                    "predicate_type": "minecraft:block_match",
                    "predicate": "minecraft:stone"
                },
                "state": { "Name": "coffeeworkshop:soda_ore" }
            }
        ]
    }
}
```

---

## 13. Villagers (村民)

### 13.1 架构变更

| 1.12.2 | 1.20.1 |
|---|---|
| `VillagerRegistry` (静态) | `DeferredRegister<VillagerProfession>` + `DeferredRegister<PoiType>` |
| `VillagerCareer` | 完全移除 |
| `List<VillagerTradeList>` | `VillagerTrades` 接口 + `ItemListing` |
| `VillagerRegistry.setVillagerTypeForProfession(...)` | 移除，所有职业可被任何生物群系使用 |

### 13.2 注册

```java
// PoiType (兴趣点)
public static final RegistryObject<PoiType> COFFEE_POI = POI_TYPES.register("coffee_poi",
        () -> new PoiType(
                Set.of(ModBlocks.COFFEE_MACHINE.get().defaultBlockState()),
                1, 1));

// VillagerProfession
public static final RegistryObject<VillagerProfession> COFFEE_BARISTA =
        PROFESSIONS.register("coffee_barista",
                () -> new VillagerProfession("coffee_barista",
                        holder -> holder.value() == COFFEE_POI.get(),
                        holder -> holder.value() == COFFEE_POI.get(),
                        Set.of(), Set.of(),
                        // 交易声音
                        SoundEvents.VILLAGER_WORK_WEAPONSMITH));
```

### 13.3 交易注册

```java
// 在 FMLCommonSetupEvent 中
event.enqueueWork(() -> {
    VillagerTrades.ItemListing[] baristaTrades = {
            new BasicItemListing(new ItemStack(Items.EMERALD, 2),
                    new ItemStack(ModItems.COFFEE_AMERICANO.get()), 12, 2, 0.05f),
            // ...
    };
    VillagerTrades.TRADES.put(ModVillagers.COFFEE_BARISTA.get(),
            VillagerTrades.getFor(1, baristaTrades));
});
```

---

## 14. SidedProxy → DistExecutor

### 14.1 移除 SidedProxy

1.12.2 使用 `@SidedProxy` 加载不同代理类。1.20.1 使用 `DistExecutor`。

**CommonProxy 的 preInit/init/postInit** 方法分散到:
- `DeferredRegister` → 注册事件
- `FMLCommonSetupEvent` → 线程安全初始化
- `FMLClientSetupEvent` → 客户端初始化
- `@EventBusSubscriber` → 事件监听

### 14.2 DistExecutor 用法

```java
// 需要客户端专用操作时
DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
    ScreenManager.register(ModMenuTypes.GRINDER.get(), GuiGrinder::new);
});
```

### 14.3 ClientProxy 方法映射

```java
// 原 ClientProxy:
preInit: ItemLoader.registerRenders() + DrinksLoader.registerRenders() + BlockLoader.registerRenders()
→ 1.20.1: 完全移除。模型已由 JSON 驱动。

// 原客户端 ModelLoader.setCustomModelResourceLocation()
// → 1.20.1: 移除，由 JSON 模型文件 + 纹理对应决定
```

---

## 15. 资源文件 (Assets)

### 15.1 Blockstates

1.12.2 variant 格式 (大部分可用):
```json
{
    "variants": {
        "normal": { "model": "coffeework:grinder_off" },
        "facing=north": { "model": "coffeework:grinder_off" }
    }
}
```

1.20.1 推荐 multipart 或保留 variant 格式（仍支持）:
```json
{
    "variants": {
        "facing=north": { "model": "coffeework:block/grinder_off" },
        "facing=south": { "model": "coffeework:block/grinder_off", "y": 180 },
        "facing=east":  { "model": "coffeework:block/grinder_off", "y": 90 },
        "facing=west":  { "model": "coffeework:block/grinder_off", "y": 270 }
    }
}
```

### 15.2 模型文件路径

```
// 1.12.2:
assets/coffeework/models/item/xxx.json
assets/coffeework/models/block/xxx.json

// 1.20.1: 推荐加 block/item 子目录:
assets/coffeework/models/item/xxx.json
assets/coffeework/models/block/xxx.json
```

### 15.3 语言文件

`.lang` 格式在 1.20.1 仍受支持，但也支持 JSON:
```json
// assets/coffeework/lang/zh_cn.json
{
    "item.coffeeworkshop.coffee_americano": "美式咖啡",
    "block.coffeeworkshop.grinder": "研磨机",
    "itemGroup.coffee_workshop": "咖啡工坊"
}
```

### 15.4 渲染层 (RenderLayer)

1.12.2 代码中 `getBlockLayer()` 返回 `BlockRenderLayer.CUTOUT` → 1.20.1 在 JSON 模型文件中指定:
```json
{
    "parent": "block/cross",
    "textures": {
        "cross": "coffeeworkshop:block/coffee_tree"
    },
    "render_type": "cutout"
}
```

### 15.5 音效文件

`sounds.json` 格式基本不变，路径不变:
```json
{
    "records.kusa_noshi_to_ne": {
        "category": "record",
        "sounds": [{
            "name": "coffeeworkshop:records/kusa_noshi_to_ne",
            "stream": true
        }]
    }
}
```

### 15.6 JSON_Creator.java 的处理

这是一个1.12.2运行时在 `json_create` 目录下生成 JSON 模型的工具类。**1.20.1 完全不需要**:
- 模型 JSON 直接存在 `assets/` 目录下
- 不需要运行时生成
- 整个 `JSON_Creator.java` 文件可以删除

---

## 16. JEI 兼容

### 16.1 主要变更

| 1.12.2 JEI | 1.20.1 JEI |
|---|---|
| `mezz.jei.api.IModPlugin` | `mezz.jei.api.IModPlugin` (包名同) |
| `@mezz.jei.api.JEIPlugin` | `@mezz.jei.api.JeiPlugin` |
| `IModRegistry` | `IRecipeRegistration` |
| `IGuiHelper` | 同 |
| `IRecipeCategory` | 接口同，但方法签名有变 |
| `ISubtypeRegistry` | `ISubtypeRegistration` (注册 NBT 相关) |

### 16.2 移植要点

```java
@JeiPlugin
public class JEICompat implements IModPlugin {
    @Override
    public ResourceLocation getPluginUid() {
        return new ResourceLocation(CoffeeWork.MODID, "jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        IGuiHelper guiHelper = registration.getJeiHelpers().getGuiHelper();
        registration.addRecipeCategories(new CategoryGrinder(guiHelper));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        registration.addRecipes(GrinderRecipeMaker.getRecipes().getRecipeCategory(),
                GrinderRecipeMaker.getRecipes().getRecipes());
    }
}
```

---

## 17. 与其他 Mod 的兼容

### 17.1 变更汇总

| API | 1.12.2 | 1.20.1 |
|---|---|---|
| Mod 加载检测 | `Loader.isModLoaded(modId)` | `ModList.get().isLoaded(modId)` |
| @Optional 注解 | `@Optional.Interface` / `@Optional.Method` | 移除，需手动检查 |
| TeaStory | 直接引用 `roito.teastory.TeaStory.MODID` | 1.20.1已经停更 |
| HAC (HeatAndClimate) | `defeatedcrow.hac.main.ClimateMain.MOD_ID` | 1.20.1已经停更 |
| IC2 | `ic2.core.IC2.MODID` | 需确认新版 modID |1.20.1已经停更
| ToughAsNails | `toughasnails.api.*` | ToughAsNails-forge-1.20.1-9.2.0.171 可以使用 |

### 17.2 建议策略

- 所有 `@Optional.Interface` / `@Optional.Method` 注解移除
- 改为运行期用 `ModList.get().isLoaded(modId)` 检查
- 接口分离: 创建 `CompatXxx` 类，在 `FMLCommonSetupEvent` 中条件性地调用
- ToughAsNails (温度/口渴系统) 在 1.20.1 可能不可用，预留接口但标记为可选

---

## 18. 阶段化执行计划

### Sprint 1: 项目骨架 & 注册体系

| 任务 | 影响文件 | 预估复杂度 |
|---|---|---|
| 重建 `build.gradle` (ForgeGradle 6+) | `build.gradle` | ⭐⭐ |
| 新建 `mods.toml` | `META-INF/mods.toml` | ⭐ |
| 重写 `CoffeeWork.java` (移除 proxy, 改为 mod event bus + DistExecutor) | `CoffeeWork.java` | ⭐⭐⭐ |
| 建立所有 `DeferredRegister` 类: `ModBlocks`, `ModItems`, `ModBlockEntities`, `ModMenuTypes`, `ModEffects`, `ModCreativeTabs` | 6 个新文件 | ⭐⭐⭐ |
| 删除 `CommonProxy.java`、`ClientProxy.java` | 删除 | ⭐ |
| 删除 `JSON_Creator.java` | 删除 | ⭐ |

### Sprint 2: Items & Blocks (无方块实体)

| 任务 | 影响文件 | 预估复杂度 |
|---|---|---|
| 改写所有 Block 类 (BlockState, Direction, Properties) | ~17 个文件 | ⭐⭐⭐⭐⭐ |
| 重写 `BlockLoader` → `ModBlocks` | `BlockLoader.java` 删除 | ⭐⭐⭐ |
| 重写 `ItemLoader` → `ModItems` (拆分 meta 子类型) | `ItemLoader.java` 删除 | ⭐⭐⭐⭐⭐ |
| 重写 `DrinksLoader` → 合并到 `ModItems` | `DrinksLoader.java` 删除 | ⭐⭐⭐⭐ |
| 重写食物系统 (FoodProperties) | `ItemFoodBasic.java`, `ItemFoodContain.java`, `ItemBase.java` 等 | ⭐⭐⭐ |
| 重写 `ItemRecordCW.java` | `ItemRecordCW.java` | ⭐ |
| 重写 `OreDicRegister.java` → tag 系统 | `OreDicRegister.java` | ⭐⭐ |
| 配置 `CreativeModeTab` + `BuildCreativeModeTabContentsEvent` | `ModCreativeTabs.java` | ⭐⭐ |
| 迁移所有模型 JSON + 纹理 | `assets/` 目录 | ⭐⭐⭐ |

### Sprint 3: BlockEntities & GUI

| 任务 | 影响文件 | 预估复杂度 |
|---|---|---|
| 重写 5 个 BlockEntity (TileEntity → BlockEntity + IItemHandler) | 5 个文件 | ⭐⭐⭐⭐⭐ |
| 重写 `TileEntityLoader` → `ModBlockEntities` | `TileEntityLoader.java` 删除 | ⭐ |
| 改写 5 个 `Container` → `AbstractContainerMenu` | 5 个文件 | ⭐⭐⭐⭐ |
| 改写 5 个 `Gui` → `AbstractContainerScreen` | 5 个文件 | ⭐⭐⭐ |
| 改写 5 个 `Slot` → `SlotItemHandler` | 5 个文件 | ⭐⭐ |
| 重写 `GuiLoader` → `ModMenuTypes` + Screen 注册 | `GuiLoader.java` 删除 | ⭐⭐⭐ |

### Sprint 4: 功能系统

| 任务 | 影响文件 | 预估复杂度 |
|---|---|---|
| 重写 `ConfigLoader` → `ForgeConfigSpec` | `ConfigLoader.java` | ⭐⭐ |
| 重写 Potion → MobEffect | 4 个文件 | ⭐⭐ |
| 重写 `PotionLoader` → `ModEffects` | `PotionLoader.java` 删除 | ⭐ |
| 重写所有 Recipe 类 → JSON (`data/`) + RecipeProvider | 5 个 + data 文件 | ⭐⭐⭐ |
| 重写 WorldGen (Feature + JSON + BiomeModifier) | 4 个文件 + JSON | ⭐⭐⭐⭐ |
| 重写 Villager (PoiType + Profession + Trades) | 4 个文件 | ⭐⭐⭐ |

### Sprint 5: 兼容层 & 收尾

| 任务 | 影响文件 | 预估复杂度 |
|---|---|---|
| 更新 JEI 兼容 | 9 个文件 | ⭐⭐⭐ |
| 删除 `Util` 中不再需要的类 | `RecipesUtil.java`, `TagPropertyAccessor.java` | ⭐ |
| 测试 + 修复编译错误 | — | ⭐⭐⭐⭐⭐ |

---

## 19. 逐文件工作量统计

### 🔴 完全重写 (25 个文件)

| 文件 | 大小 | 原因 |
|---|---|---|
| `build.gradle` | 3.9KB | ForgeGradle 6+ / Java 17 / Official mappings |
| `CoffeeWork.java` | 1.5KB | 移除 @EventHandler/@SidedProxy/@Instance |
| `CommonProxy.java` | 2.6KB | 完全移除 proxy 模式 |
| `ClientProxy.java` | 1.2KB | 完全移除 proxy 模式 |
| `BlockLoader.java` | 13.9KB | → ModBlocks (DeferredRegister) |
| `ItemLoader.java` | 25.2KB | → ModItems (DeferredRegister + 拆分 meta) |
| `DrinksLoader.java` | 42.2KB | → ModItems 合并 |
| `ConfigLoader.java` | 1.1KB | → ForgeConfigSpec |
| `GuiLoader.java` | 2.6KB | → ModMenuTypes + ScreenManager |
| `PotionLoader.java` | 0.8KB | → ModEffects |
| `TileEntityLoader.java` | 1.0KB | → ModBlockEntities |
| `VillagerLoader.java` | 0.2KB | → ModVillagers |
| `WorldGenLoader.java` | 1.7KB | → Feature/PlacedFeature/BiomeModifier |
| `JSON_Creator.java` | 24.4KB | 不再需要 (模型 JSON 直接存放) |
| `RecipesUtil.java` | 1.8KB | NBT 工具 → CompoundTag 重写 |
| `TagPropertyAccessor.java` | 4.7KB | Tag API 变更 → CompoundTag 方法 |
| `RecipeShaplessOreNbt.java` | 1.2KB | 配方系统不再需要自定义 IRecipe |
| ~~`mcmod.info`~~ | ~~0.6KB~~ | → `mods.toml` |

### 🟡 较大修改 (20+ 个文件)

| 文件 | 大小 | 主要变更 |
|---|---|---|
| `BlockCoffee.java` | 8.1KB | BlockState/Direction/AABB/EntityPlayer→Player |
| `BlockGrinder.java` | 12.0KB | EntityBlock 接口 + TE 关联 |
| `BlockCoffeeMachine.java` | 12.1KB | 同上 |
| `BlockIcecreamMachine.java` | 9.0KB | 同上 |
| `BlockRoller.java` | 12.0KB | 同上 |
| `BlockClayOven.java` | 11.7KB | 同上 |
| `BlockBag.java` | 5.3KB | BlockState 系统 |
| `BlockBag2.java` | 4.2KB | 同上 |
| `BlockBlueBerryBush.java` | 7.7KB | Crop 系统 + BlockState |
| `BlockCoffeeTree.java` | 7.9KB | 同上 |
| `BlockCakeBasic.java` | 2.8KB | BlockState + 食用逻辑 |
| `BlockPlate.java` | 7.3KB | BlockState |
| `BlockColdBrewPot.java` | 6.5KB | BlockState |
| `DrinkCoffee.java` | 8.9KB | FoodProperties + effect 系统 |
| `DrinkCoffeeIce.java` | 3.4KB | 同上 |
| `DrinkCoffeeInstant.java` | 3.8KB | 同上 |
| `DrinkEspresso.java` | 2.7KB | 同上 |
| `ItemRecordCW.java` | 0.9KB | SoundEvent 注册变化 |
| 5 个 TileEntity | 各 14-20KB | BlockEntity + IItemHandler + ContainerData |
| 5 个 Container | 各 5-6KB | AbstractContainerMenu + SlotItemHandler |
| 5 个 Gui*Machine | 各 2.8-3KB | AbstractContainerScreen + GuiGraphics |
| 5 个 Slot 输出 | 各 3.2KB | SlotItemHandler |
| 4 个 Potion 类 | 各 1-2.5KB | MobEffect 改名 |
| 3 个 Recipe 注册 | 各 3.2-3.3KB | 接口基本不变 |
| 4 个 WorldGen 类 | 各 1.5-2.7KB | Feature 架构重写 |
| 3 个 Villager 类 | 各 2.4-2.9KB | PoiType + Profession |
| JEI 兼容 (9 个文件) | 各 1-3KB | JEI API 更新 |

### 🟢 基本不变 (少数文件)

| 文件 | 说明 |
|---|---|
| `DrinksCraftingRecipes.java` (18KB) | 配方内容 → 改为 JSON + RecipeProvider |
| `FoodCraftingRecipes.java` (28KB) | 同上 |
| `MaterialCraftingRecipes.java` (21KB) | 同上 |
| `blockstates/*.json` | 小改 (模型路径前缀) |
| 纹理文件 `textures/` | 不变 |
| 音频文件 `sounds/` | 不变 |
| 语言文件 `lang/*` | 可保留 .lang 或改为 .json |

---

## 20. 推荐的参考文档链接

### Forge 官方文档 (1.20.1)
- **Getting Started**: <https://docs.minecraftforge.net/en/1.20.1/gettingstarted/>
- **Registries (DeferredRegister)**: <https://docs.minecraftforge.net/en/1.20.1/concepts/registries/>
- **Sides & DistExecutor**: <https://docs.minecraftforge.net/en/1.20.1/concepts/sides/>
- **Mod Lifecycle**: <https://docs.minecraftforge.net/en/1.20.1/concepts/lifecycle/>
- **Blocks**: <https://docs.minecraftforge.net/en/1.20.1/blocks/>
- **Block States**: <https://docs.minecraftforge.net/en/1.20.1/blocks/states/>
- **Items**: <https://docs.minecraftforge.net/en/1.20.1/items/>
- **BlockEntities**: <https://docs.minecraftforge.net/en/1.20.1/blockentities/>
- **Menus**: <https://docs.minecraftforge.net/en/1.20.1/gui/menus/>
- **Screens**: <https://docs.minecraftforge.net/en/1.20.1/gui/screens/>
- **Configuration (ForgeConfigSpec)**: <https://docs.minecraftforge.net/en/1.20.1/misc/config/>
- **Networking**: <https://docs.minecraftforge.net/en/1.20.1/networking/>
- **Capabilities**: <https://docs.minecraftforge.net/en/1.20.1/datastorage/capabilities/>
- **Recipes**: <https://docs.minecraftforge.net/en/1.20.1/resources/server/recipes/>
- **Data Generation**: <https://docs.minecraftforge.net/en/1.20.1/datagen/>
- **Forge Porting Guides Index**: <https://docs.minecraftforge.net/en/latest/legacy/porting/>

### 社区资源
- **1.12 → 1.13/1.14 Primer (williewillus)**: <https://gist.github.com/williewillus/353c872bcf1a6ace9921189f6100d09a>
- **1.16 → 1.17 Primer (50ap5ud5)**: <https://gist.github.com/50ap5ud5/beebcf056cbdd3c922cc8993689428f4>
- **1.19.4 → 1.20 Primer (ChampionAsh5357)**: <https://gist.github.com/ChampionAsh5357/cf818acc53ffea6f4387fe28c2977d56>
- **McJty 1.20 Tutorials**: <https://mcjty.eu/docs/1.20/ep1>
- **ModdingTutorials.org**: <https://moddingtutorials.org/>

---

> **说明**: 本计划基于对源码的完整分析及 Forge 官方 1.20.1 API 文档编写。实际移植中可能遇到文档未覆盖的边缘情况，届时需要参考 vanilla 反编译代码或 Forge GitHub 源码。
