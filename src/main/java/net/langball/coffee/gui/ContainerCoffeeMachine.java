package net.langball.coffee.gui;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.gui.slot.SlotMachineResult;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.init.ModRecipeTypes;
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
 * Coffee Machine container — v2 5-slot layout.
 *
 * <pre>
 * [Base]        [Additive]
 * [Modifier]    [Container]
 *            →  [Output]
 * </pre>
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
        // 5-slot layout:
        // 0: Base (30, 20)
        // 1: Modifier (50, 44)
        // 2: Additive (70, 20)
        // 3: Container (90, 44)
        // 4: Output (130, 32)
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
}
