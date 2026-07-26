package net.langball.coffee.block.entity;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.init.ModBlockEntities;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.ProcessingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Coffee Machine block entity — brews drinks from coffee powder, liquids,
 * optional additives, and cups.
 *
 * <h3>v2 — Five-slot layout</h3>
 * <ul>
 *   <li>Slot 0 — Base (coffee powder, cocoa powder, coldbrew bottle)</li>
 *   <li>Slot 1 — Modifier (water bucket, milk bucket, or empty)</li>
 *   <li>Slot 2 — Additive (cocoa powder, ice slag, syrup, or empty)</li>
 *   <li>Slot 3 — Container (cup, cup_glass)</li>
 *   <li>Slot 4 — Output</li>
 * </ul>
 *
 * <p>Self-powered (no fuel slot).
 */
public class CoffeeMachineBlockEntity extends AbstractProcessingBlockEntity {

    public static final int SLOT_BASE = 0;
    public static final int SLOT_MODIFIER = 1;
    public static final int SLOT_ADDITIVE = 2;
    public static final int SLOT_CONTAINER = 3;
    public static final int SLOT_OUTPUT = 4;
    private static final int INVENTORY_SIZE = 5;
    /** Version tag for NBT migration from old layouts. */
    private static final int CURRENT_INVENTORY_VERSION = 2;

    public CoffeeMachineBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COFFEE_MACHINE.get(), pos, state);
        this.itemHandler = new ItemStackHandler(INVENTORY_SIZE) {
            @Override
            protected void onContentsChanged(int slot) {
                setChanged();
            }

            @Override
            public boolean isItemValid(int slot, @NotNull ItemStack stack) {
                return slot != SLOT_OUTPUT;
            }
        };
    }

    @Override
    protected RecipeType<?> getRecipeType() {
        return ModRecipeTypes.COFFEE_BREWING;
    }

    @Override
    protected int getInputSlot() { return SLOT_BASE; }

    @Override
    protected int getOutputSlot() { return SLOT_OUTPUT; }

    @Override
    protected int[] getInputSlots() { return new int[]{SLOT_BASE, SLOT_MODIFIER, SLOT_ADDITIVE, SLOT_CONTAINER}; }

    @Override
    protected int[] getTopInputSlots() { return new int[]{SLOT_BASE}; }

    @Override
    protected int[] getHorizontalInputSlots() { return new int[]{SLOT_MODIFIER, SLOT_ADDITIVE, SLOT_CONTAINER}; }

    @Override
    protected int[] getFuelSlots() { return new int[0]; }

    @Override
    protected int[] getOutputSlots() { return new int[]{SLOT_OUTPUT}; }

    public boolean isBurning() {
        return burnTime > 0;
    }

    @Override
    protected void startProcessingPower(ProcessingRecipe recipe) {
        burnTime = recipe.cookingTime();
        burnTimeTotal = recipe.cookingTime();
    }

    @Override
    protected boolean shouldBeLit(ProcessingRecipe recipe, boolean canProcess) {
        return canProcess && hasProcessingPower();
    }

    // ---- NBT migration --------------------------------------------------

    @Override
    public void load(CompoundTag tag) {
        int version = tag.getInt("InventoryVersion");

        // Migrate from old 4-slot layout (v1) to new 5-slot layout (v2)
        if (tag.contains("Items") && version < CURRENT_INVENTORY_VERSION) {
            CompoundTag items = tag.getCompound("Items");
            int oldSize = items.getList("Items", 10).size();

            if (version == 0) {
                // v0: old 2-slot → v1: 4-slot (already handled by v1 migration)
                // Re-run v1 migration first
                if (oldSize == 2) {
                    tag = migrateV0toV1(tag);
                    items = tag.getCompound("Items");
                    oldSize = items.getList("Items", 10).size();
                }
            }

            // v1 → v2: 4-slot → 5-slot
            // Old: slot 0=base, 1=modifier, 2=container, 3=output
            // New: slot 0=base, 1=modifier, 2=additive(empty), 3=container, 4=output
            if (version <= 1 && oldSize == 4) {
                tag = migrateV1toV2(tag);
            }
        }

        tag.putInt("InventoryVersion", CURRENT_INVENTORY_VERSION);
        super.load(tag);
    }

    /** Migrate from v0 (2-slot) to v1 (4-slot) layout. */
    private CompoundTag migrateV0toV1(CompoundTag tag) {
        CompoundTag items = tag.getCompound("Items");
        CompoundTag migrated = new CompoundTag();
        net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();

        // Copy old slot 0 → new slot 0 (base)
        CompoundTag oldInput = items.getList("Items", 10).getCompound(0).copy();
        oldInput.putByte("Slot", (byte) 0);
        list.add(oldInput);

        // Move old slot 1 → new slot 3 (output)
        CompoundTag oldOutput = items.getList("Items", 10).getCompound(1).copy();
        oldOutput.putByte("Slot", (byte) 3);
        list.add(oldOutput);

        migrated.put("Items", list);
        migrated.putInt("Size", 4);
        tag.put("Items", migrated);
        return tag;
    }

    /** Migrate from v1 (4-slot) to v2 (5-slot) layout. */
    private CompoundTag migrateV1toV2(CompoundTag tag) {
        CompoundTag items = tag.getCompound("Items");
        CompoundTag migrated = new CompoundTag();
        net.minecraft.nbt.ListTag list = new net.minecraft.nbt.ListTag();

        var oldList = items.getList("Items", 10);
        for (int i = 0; i < oldList.size(); i++) {
            CompoundTag entry = oldList.getCompound(i).copy();
            int oldSlot = entry.getByte("Slot");
            // Map old → new:
            // old 0→0 (base), old 1→1 (modifier), old 2→3 (container), old 3→4 (output)
            int newSlot = switch (oldSlot) {
                case 0 -> 0; // base stays
                case 1 -> 1; // modifier stays
                case 2 -> 3; // container shifts +1
                case 3 -> 4; // output shifts +1
                default -> oldSlot;
            };
            entry.putByte("Slot", (byte) newSlot);
            list.add(entry);
        }

        migrated.put("Items", list);
        migrated.putInt("Size", 5);
        tag.put("Items", migrated);
        return tag;
    }

    @Override
    public Component getDisplayName() {
        return Component.translatable("container." + CoffeeWork.MODID + ".coffee_machine");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int id, Inventory inventory, Player player) {
        return new net.langball.coffee.gui.ContainerCoffeeMachine(id, inventory, itemHandler, data, this);
    }

    @Override
    public void tick(Level level, BlockPos pos, BlockState state) {
        tickProcessing(level, pos, state);
    }
}
