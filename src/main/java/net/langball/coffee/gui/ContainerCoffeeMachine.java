package net.langball.coffee.gui;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.gui.slot.SlotMachineResult;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.CoffeeBrewingRecipe;
import net.minecraft.core.BlockPos;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.ContainerData;
import net.minecraft.world.inventory.SimpleContainerData;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.RecipeType;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

/**
 * Coffee Machine container — v2 5-slot layout with role-based shift-click.
 *
 * <pre>
 * [Base]        [Additive]
 * [Modifier]    [Container]
 *            →  [Output]
 * </pre>
 *
 * <h3>Shift-click routing</h3>
 * Items from the player inventory are routed to the correct machine slot
 * based on their role in coffee brewing recipes:
 * <ul>
 *   <li>Base ingredient → Slot 0</li>
 *   <li>Modifier ingredient → Slot 1</li>
 *   <li>Additive ingredient → Slot 2</li>
 *   <li>Container item → Slot 3</li>
 * </ul>
 */
public class ContainerCoffeeMachine extends AbstractMachineMenu {

    /** Client-side constructor. */
    public ContainerCoffeeMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 5),
                new SimpleContainerData(4),
                getMachineAt(inv, pos));
    }

    /** Server-side constructor. */
    public ContainerCoffeeMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, MachineBlockEntity be) {
        super(ModMenuTypes.COFFEE_MACHINE.get(), id, inv, handler, data, be, 5);
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

    // ── Role-based shift-click routing ─────────────────────────────────

    /**
     * Returns the Coffee Machine slot index that {@code stack} should be
     * routed to, or -1 if no specific role is identified.
     *
     * <p>Searches all coffee brewing recipes for the first recipe where
     * {@code stack} matches the base, modifier, additive, or container
     * ingredient.  Returns the corresponding slot.  If a stack matches
     * multiple roles, the first role in priority order (container, base,
     * modifier, additive) is chosen.
     */
    private int findSlotForStack(ItemStack stack) {
        if (level == null) return -1;

        var recipes = level.getRecipeManager().getAllRecipesFor(
                (RecipeType<CoffeeBrewingRecipe>) getRecipeType());

        for (var recipe : recipes) {
            // Container check first (cup/glass is the most specific role)
            if (recipe.container().ingredient().test(stack)) {
                return CoffeeMachineBlockEntity.SLOT_CONTAINER;
            }
            // Base ingredient
            if (recipe.base().ingredient().test(stack)) {
                return CoffeeMachineBlockEntity.SLOT_BASE;
            }
            // Modifier ingredient
            if (recipe.modifier() != null && recipe.modifier().ingredient().test(stack)) {
                return CoffeeMachineBlockEntity.SLOT_MODIFIER;
            }
            // Additive ingredient
            if (recipe.additive() != null && recipe.additive().ingredient().test(stack)) {
                return CoffeeMachineBlockEntity.SLOT_ADDITIVE;
            }
        }
        return -1;
    }

    @Override
    public ItemStack quickMoveStack(net.minecraft.world.entity.player.Player player, int index) {
        java.util.List<net.minecraft.world.inventory.Slot> slots = this.slots;
        net.minecraft.world.inventory.Slot slot = slots.get(index);

        if (slot == null || !slot.hasItem()) {
            return ItemStack.EMPTY;
        }

        ItemStack stack = slot.getItem();
        ItemStack original = stack.copy();

        // Output slot → player inventory
        if (index == getOutputSlotIndex()) {
            if (!moveItemStackTo(stack, playerInvStartIndex, slots.size(), true)) {
                return ItemStack.EMPTY;
            }
            slot.onQuickCraft(stack, original);
        }
        // Machine slot → player inventory
        else if (index < playerInvStartIndex) {
            if (!moveItemStackTo(stack, playerInvStartIndex, slots.size(), false)) {
                return ItemStack.EMPTY;
            }
        }
        // Player inventory → machine slot (role-based routing)
        else {
            int targetSlot = findSlotForStack(stack);
            if (targetSlot >= 0) {
                if (!moveItemStackTo(stack, targetSlot, targetSlot + 1, false)) {
                    return ItemStack.EMPTY;
                }
            } else {
                // Standard hotbar/inventory swap
                if (index >= playerInvStartIndex && index < hotbarStartIndex) {
                    if (!moveItemStackTo(stack, hotbarStartIndex, slots.size(), false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (index >= hotbarStartIndex && index < slots.size()) {
                    if (!moveItemStackTo(stack, playerInvStartIndex, hotbarStartIndex, false)) {
                        return ItemStack.EMPTY;
                    }
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
