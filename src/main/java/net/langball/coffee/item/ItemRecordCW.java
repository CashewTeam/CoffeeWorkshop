package net.langball.coffee.item;

import net.minecraft.network.chat.Component;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.RecordItem;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.RegistryObject;

import java.util.List;

public class ItemRecordCW extends RecordItem {
    private final String titleKey;

    public ItemRecordCW(int analogOutput, RegistryObject<SoundEvent> sound, Item.Properties properties, String titleKey, int lengthInTicks) {
        super(analogOutput, sound, properties.stacksTo(1), lengthInTicks);
        this.titleKey = titleKey;
    }

    @Override
    @OnlyIn(Dist.CLIENT)
    public void appendHoverText(ItemStack stack, Level level, List<Component> tooltip, TooltipFlag flag) {
        tooltip.add(Component.translatable(this.titleKey));
    }
}
