package net.langball.coffee.gui;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.gui.slot.SlotMachineResult;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;
import java.util.HashSet;

/**
 * Coffee Machine container — v2 5-slot layout with role-based shift-click.
 *
 * <h3>Shift-click routing</h3>
 * Items from the player inventory are routed to the correct machine slot
 * based on a globally-computed role set (independent of recipe iteration
 * order).  Each role set is built by iterating all recipes ONCE during
 * container creation and accumulating ingredient items.
 *
 * <p>Slot priority for ambiguous items: container > base > modifier >
 * additive.  If a slot is already occupied, the next valid slot is tried.
 */
public class ContainerCoffeeMachine extends AbstractMachineMenu {

    private final java.util.Map<net.minecraft.world.item.Item, java.util.Set<java.util.function.IntSupplier>> itemToSlots =
            new java.util.HashMap<>();

    public ContainerCoffeeMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 5),
                new SimpleContainerData(4),
                getMachineAt(inv, pos));
    }

    public ContainerCoffeeMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, MachineBlockEntity be) {
        super(ModMenuTypes.COFFEE_MACHINE.get(), id, inv, handler, data, be, 5);
        buildItemRouting();
    }

    @Override
    protected void addMachineSlots() {
        this.addSlot(new SlotItemHandler(itemHandler, CoffeeMachineBlockEntity.SLOT_BASE, 30, 20));
        this.addSlot(new SlotItemHandler(itemHandler, CoffeeMachineBlockEntity.SLOT_MODIFIER, 50, 44));
        this.addSlot(new SlotItemHandler(itemHandler, CoffeeMachineBlockEntity.SLOT_ADDITIVE, 70, 20));
        this.addSlot(new SlotItemHandler(itemHandler, CoffeeMachineBlockEntity.SLOT_CONTAINER, 90, 44));
        this.addSlot(new SlotMachineResult(itemHandler, CoffeeMachineBlockEntity.SLOT_OUTPUT, 130, 32,
                blockEntity, level));
    }

    @Override
    protected RecipeType<?> getRecipeType() {
        return ModRecipeTypes.COFFEE_BREWING;
    }

    @Override
    protected Set<Block> getValidBlocks() {
        return Set.of(ModBlocks.COFFEE_MACHINE.get());
    }

    @Override
    protected int getOutputSlotIndex() {
        return CoffeeMachineBlockEntity.SLOT_OUTPUT;
    }

    @Override
    protected boolean isFuelItem(ItemStack stack) {
        return false;
    }

    @Override
    protected int getFuelSlotIndex() {
        return -1;
    }

    // ── Global role routing (independent of recipe iteration order) ──────

    /**
     * Build a multi-set mapping each item to all valid role slots.
     * Slot priority: container > base > modifier > additive.
     */
    private void buildItemRouting() {
        if (level == null) return;
        for (CoffeeBrewingRecipe r : level.getRecipeManager()
                .getAllRecipesFor((RecipeType<CoffeeBrewingRecipe>) getRecipeType())) {
            // Order matters: later role adds are LOWER priority
            if (r.additive() != null) addRoleItems(r, r.additive().ingredient(), CoffeeMachineBlockEntity.SLOT_ADDITIVE);
            addRoleItems(r, r.modifier() != null ? r.modifier().ingredient() : null, CoffeeMachineBlockEntity.SLOT_MODIFIER);
            addRoleItems(r, r.base().ingredient(), CoffeeMachineBlockEntity.SLOT_BASE);
            // Container is highest priority — placed first
            addRoleItems(r, r.container().ingredient(), CoffeeMachineBlockEntity.SLOT_CONTAINER);
        }
    }

    private void addRoleItems(CoffeeBrewingRecipe r, net.minecraft.world.item.crafting.Ingredient ing, int slot) {
        if (ing == null) return;
        for (ItemStack s : ing.getItems()) {
            itemToSlots.computeIfAbsent(s.getItem(), k -> new HashSet<>())
                    .add(() -> slot);
        }
    }

    /**
     * Resolves the target slot for a shift-clicked item.
     * Returns the highest-priority valid empty slot, or -1 if none.
     */
    private int findSlotForStack(ItemStack stack) {
        if (stack.isEmpty()) return -1;
        // Priority order: container > base > modifier > additive
        int[] prioritySlots = {CoffeeMachineBlockEntity.SLOT_CONTAINER,
                CoffeeMachineBlockEntity.SLOT_BASE,
                CoffeeMachineBlockEntity.SLOT_MODIFIER,
                CoffeeMachineBlockEntity.SLOT_ADDITIVE};

        for (int slot : prioritySlots) {
            ItemStack current = itemHandler.getStackInSlot(slot);
            if (!current.isEmpty()) continue; // slot already occupied
            // Check if this item is valid for this slot via the global role set
            if (itemToSlots.containsKey(stack.getItem())) return slot;
        }
        // Fallback: just slot 0
        return CoffeeMachineBlockEntity.SLOT_BASE;
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        java.util.List<net.minecraft.world.inventory.Slot> slots = this.slots;
        net.minecraft.world.inventory.Slot slot = slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        if (index == getOutputSlotIndex()) {
            if (!moveItemStackTo(stack, playerInvStartIndex, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        } else if (index < playerInvStartIndex) {
            if (!moveItemStackTo(stack, playerInvStartIndex, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        } else {
            int targetSlot = findSlotForStack(stack);
            if (targetSlot >= 0) {
                if (!moveItemStackTo(stack, targetSlot, targetSlot + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= playerInvStartIndex && index < hotbarStartIndex) {
                if (!moveItemStackTo(stack, hotbarStartIndex, slots.size(), false)) {
                    return ItemStack.EMPTY;
                }
            } else if (index >= hotbarStartIndex && index < slots.size()) {
                if (!moveItemStackTo(stack, playerInvStartIndex, hotbarStartIndex, false)) {
                    return ItemStack.EMPTY;
                }
            }
        }

        if (stack.isEmpty()) {
            slot.set(ItemStack.EMPTY);
        } else {
            slot.setChanged();
        }

        if (stack.getCount() == original.getCount()) {
            return ItemStack.EMPTY;
        }

        slot.onTake(player, stack);
        return original;
    }
}
