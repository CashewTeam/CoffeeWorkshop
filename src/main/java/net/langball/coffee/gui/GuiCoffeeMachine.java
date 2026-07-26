package net.langball.coffee.gui;

import net.langball.coffee.CoffeeWork;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

@OnlyIn(Dist.CLIENT)
public class GuiCoffeeMachine extends AbstractContainerScreen<ContainerCoffeeMachine> {
    private static final ResourceLocation TEXTURE =
            new ResourceLocation(CoffeeWork.MODID, "textures/gui/coffee_machine.png");

    public GuiCoffeeMachine(ContainerCoffeeMachine menu, Inventory inv, Component title) {
        super(menu, inv, title);
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        int i = this.leftPos;
        int j = this.topPos;
        guiGraphics.blit(TEXTURE, i, j, 0, 0, this.imageWidth, this.imageHeight);

        // Burn time indicator (flame) — positioned near the arrow
        int burnTime = menu.data.get(2);
        int currentBurnTime = menu.data.get(3);
        if (burnTime > 0) {
            int k = currentBurnTime != 0 ? burnTime * 13 / currentBurnTime : 0;
            guiGraphics.blit(TEXTURE, i + 72, j + 36 + 12 - k, 176, 12 - k, 14, k + 1);
        }

        // Cook progress arrow — between container and output
        int cookTime = menu.data.get(0);
        int totalCookTime = menu.data.get(1);
        if (totalCookTime > 0) {
            int l = cookTime * 24 / totalCookTime;
            guiGraphics.blit(TEXTURE, i + 106, j + 33, 176, 14, l + 1, 16);
        }
    }

    @Override
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderBackground(guiGraphics);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        this.renderTooltip(guiGraphics, mouseX, mouseY);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        guiGraphics.drawString(this.font, this.title, this.titleLabelX, this.titleLabelY, 0x404040, false);
        guiGraphics.drawString(this.font, this.playerInventoryTitle, this.inventoryLabelX, this.inventoryLabelY, 0x404040, false);
    }
}
