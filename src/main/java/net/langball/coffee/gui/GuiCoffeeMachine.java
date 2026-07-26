package net.langball.coffee.gui;

import net.langball.coffee.CoffeeWork;
import net.langball.coffee.block.entity.CoffeeMachineBlockEntity;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

/**
 * Coffee Machine GUI screen — v2 five-slot layout.
 *
 * <p>Displays four input slots (base, modifier, additive, container) and
 * one output slot, plus a progress arrow and running-state flame
 * indicator.  Slot tooltips show role names when hovering over empty slots.
 */
@OnlyIn(Dist.CLIENT)
public class GuiCoffeeMachine extends AbstractContainerScreen<ContainerCoffeeMachine> {

    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CoffeeWork.MODID, "textures/gui/coffee_machine.png");

    public GuiCoffeeMachine(ContainerCoffeeMachine menu, Inventory inv, Component title) {
        super(menu, inv, title);
        this.imageWidth = CoffeeMachineGuiLayout.IMAGE_WIDTH;
        this.imageHeight = CoffeeMachineGuiLayout.IMAGE_HEIGHT;
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int guiX = this.leftPos;
        int guiY = this.topPos;
        guiGraphics.blit(TEXTURE, guiX, guiY, 0, 0, this.imageWidth, this.imageHeight);

        // Flame / running indicator
        int burnTime = menu.data.get(2);
        int currentBurnTime = menu.data.get(3);
        if (burnTime > 0) {
            int k = currentBurnTime != 0 ? burnTime * CoffeeMachineGuiLayout.FLAME_HEIGHT / currentBurnTime : 0;
            guiGraphics.blit(TEXTURE,
                    guiX + CoffeeMachineGuiLayout.FLAME_X,
                    guiY + CoffeeMachineGuiLayout.FLAME_Y + CoffeeMachineGuiLayout.FLAME_HEIGHT - k,
                    176, CoffeeMachineGuiLayout.FLAME_HEIGHT - k,
                    14, k + 1);
        }

        // Progress arrow
        int cookTime = menu.data.get(0);
        int totalCookTime = menu.data.get(1);
        if (totalCookTime > 0) {
            int l = cookTime * CoffeeMachineGuiLayout.PROGRESS_WIDTH / totalCookTime;
            guiGraphics.blit(TEXTURE,
                    guiX + CoffeeMachineGuiLayout.PROGRESS_X,
                    guiY + CoffeeMachineGuiLayout.PROGRESS_Y,
                    176, CoffeeMachineGuiLayout.FLAME_HEIGHT,
                    l + 1, CoffeeMachineGuiLayout.PROGRESS_HEIGHT);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);

        // Slot role hints — only shown when the slot is empty
        renderSlotHint(guiGraphics, mouseX, mouseY,
                CoffeeMachineGuiLayout.BASE_X, CoffeeMachineGuiLayout.BASE_Y,
                CoffeeMachineBlockEntity.SLOT_BASE,
                Component.translatable("gui.coffeework.coffee_machine.base"));
        renderSlotHint(guiGraphics, mouseX, mouseY,
                CoffeeMachineGuiLayout.MODIFIER_X, CoffeeMachineGuiLayout.MODIFIER_Y,
                CoffeeMachineBlockEntity.SLOT_MODIFIER,
                Component.translatable("gui.coffeework.coffee_machine.modifier"));
        renderSlotHint(guiGraphics, mouseX, mouseY,
                CoffeeMachineGuiLayout.ADDITIVE_X, CoffeeMachineGuiLayout.ADDITIVE_Y,
                CoffeeMachineBlockEntity.SLOT_ADDITIVE,
                Component.translatable("gui.coffeework.coffee_machine.additive"));
        renderSlotHint(guiGraphics, mouseX, mouseY,
                CoffeeMachineGuiLayout.CONTAINER_X, CoffeeMachineGuiLayout.CONTAINER_Y,
                CoffeeMachineBlockEntity.SLOT_CONTAINER,
                Component.translatable("gui.coffeework.coffee_machine.container"));
    }

    /**
     * Shows a role tooltip when the mouse hovers over an empty machine slot.
     */
    private void renderSlotHint(GuiGraphics graphics, int mouseX, int mouseY,
                                 int slotX, int slotY, int slotIndex, Component text) {
        int x = leftPos + slotX;
        int y = topPos + slotY;
        if (mouseX >= x && mouseX < x + 16 && mouseY >= y && mouseY < y + 16) {
            // Only show hint when the slot is empty
            ItemStack stack = menu.slots.get(slotIndex).getItem();
            if (stack.isEmpty()) {
                graphics.renderTooltip(this.font, text, mouseX, mouseY);
            }
        }
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
