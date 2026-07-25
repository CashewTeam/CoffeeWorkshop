package net.langball.coffee.gui;

import net.langball.coffee.block.entity.MachineBlockEntity;
import net.langball.coffee.block.entity.RollerBlockEntity;
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
import net.minecraftforge.common.ForgeHooks;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public class ContainerRoller extends AbstractMachineMenu {

    /** Client-side constructor. */
    public ContainerRoller(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 3),
                new SimpleContainerData(4),
                getMachineAt(inv, pos));
    }

    /** Server-side constructor. */
    public ContainerRoller(int id, Inventory inv, IItemHandler handler, ContainerData data, MachineBlockEntity be) {
        super(ModMenuTypes.ROLLER.get(), id, inv, handler, data, be, 3);
    }

    @Override
    protected void addMachineSlots() {
        this.addSlot(new SlotItemHandler(itemHandler, RollerBlockEntity.SLOT_INPUT, 56, 17));
        this.addSlot(new SlotItemHandler(itemHandler, RollerBlockEntity.SLOT_FUEL, 56, 53));
        this.addSlot(new SlotMachineResult(itemHandler, RollerBlockEntity.SLOT_OUTPUT, 116, 35,
                blockEntity, level));
    }

    @Override
    protected RecipeType<MachineRecipe> getRecipeType() {
        return ModRecipeTypes.ROLLING;
    }

    @Override
    protected Set<Block> getValidBlocks() {
        return Set.of(ModBlocks.ROLLER.get());
    }

    @Override
    protected int getOutputSlotIndex() {
        return RollerBlockEntity.SLOT_OUTPUT;
    }

    @Override
    protected boolean isFuelItem(ItemStack stack) {
        return ForgeHooks.getBurnTime(stack, null) > 0;
    }

    @Override
    protected int getFuelSlotIndex() {
        return RollerBlockEntity.SLOT_FUEL;
    }
}
