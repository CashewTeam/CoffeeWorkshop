package net.langball.coffee.gui;

import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.langball.coffee.block.entity.MachineBlockEntity;
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
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;
import net.minecraftforge.items.SlotItemHandler;

import java.util.Set;

public class ContainerCoffeeMachine extends AbstractMachineMenu {

    /** Client-side constructor. */
    public ContainerCoffeeMachine(int id, Inventory inv, BlockPos pos) {
        this(id, inv,
                getItemHandlerAt(inv, pos, 2),
                new SimpleContainerData(4),
                (MachineBlockEntity) inv.player.level().getBlockEntity(pos));
    }

    /** Server-side constructor. */
    public ContainerCoffeeMachine(int id, Inventory inv, IItemHandler handler, ContainerData data, MachineBlockEntity be) {
        super(ModMenuTypes.COFFEE_MACHINE.get(), id, inv, handler, data, be, 2);
    }

    @Override
    protected void addMachineSlots() {
        this.addSlot(new SlotItemHandler(itemHandler, CoffeeMachineBlockEntity.SLOT_INPUT, 36, 35));
        this.addSlot(new SlotMachineResult(itemHandler, CoffeeMachineBlockEntity.SLOT_OUTPUT, 116, 35,
                blockEntity, level));
    }

    @Override
    protected RecipeType<MachineRecipe> getRecipeType() {
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
        return false; // Coffee Machine has no fuel
    }

    @Override
    protected int getFuelSlotIndex() {
        return -1; // no fuel slot
    }

    private static IItemHandler getItemHandlerAt(Inventory inv, BlockPos pos, int size) {
        BlockEntity be = inv.player.level().getBlockEntity(pos);
        if (be != null) {
            return be.getCapability(ForgeCapabilities.ITEM_HANDLER)
                    .resolve()
                    .orElseGet(() -> new ItemStackHandler(size));
        }
        return new ItemStackHandler(size);
    }
}
