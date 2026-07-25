package net.langball.coffee.gui;

import net.langball.coffee.block.entity.IcecreamMachineBlockEntity;
import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.gui.slot.SlotMachineICE;
import net.langball.coffee.gui.slot.SlotMachineResult;
import net.langball.coffee.init.ModBlocks;
import net.langball.coffee.init.ModMenuTypes;
import net.langball.coffee.init.ModRecipeTypes;
import net.langball.coffee.recipes.MachineRecipe;
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

public class ContainerIcecreamMachine extends AbstractMachineMenu {

    /** Client-side constructor. */
    public ContainerIcecreamMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 3),
                new SimpleContainerData(4),
                getMachineAt(inv, pos));
    }

    /** Server-side constructor. */
    public ContainerIcecreamMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, MachineBlockEntity be) {
        super(ModMenuTypes.ICECREAM_MACHINE.get(), id, inv, handler, data, be, 3);
    }

    @Override
    protected void addMachineSlots() {
        this.addSlot(new SlotItemHandler(itemHandler, IcecreamMachineBlockEntity.SLOT_INPUT, 56, 17));
        this.addSlot(new SlotMachineICE(itemHandler, IcecreamMachineBlockEntity.SLOT_FUEL, 112, 17));
        this.addSlot(new SlotMachineResult(itemHandler, IcecreamMachineBlockEntity.SLOT_OUTPUT, 83, 57,
                blockEntity, level));
    }

    @Override
    protected RecipeType<MachineRecipe> getRecipeType() {
        return ModRecipeTypes.ICECREAM_MAKING;
    }

    @Override
    protected Set<Block> getValidBlocks() {
        return Set.of(ModBlocks.ICECREAM_MACHINE.get());
    }

    @Override
    protected int getOutputSlotIndex() {
        return IcecreamMachineBlockEntity.SLOT_OUTPUT;
    }

    @Override
    protected boolean isFuelItem(ItemStack stack) {
        return SlotMachineICE.isItemFuel(stack);
    }

    @Override
    protected int getFuelSlotIndex() {
        return IcecreamMachineBlockEntity.SLOT_FUEL;
    }
}
