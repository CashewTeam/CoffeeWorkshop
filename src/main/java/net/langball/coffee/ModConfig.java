package net.langball.coffee;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue COFFEE_TREE_RARITY;
    public static final ForgeConfigSpec.IntValue BLUEBERRY_RARITY;

    static {
        BUILDER.push("worldgen");

        COFFEE_TREE_RARITY = BUILDER
                .comment("Rarity of coffee tree generation (1-20)")
                .defineInRange("coffee_tree_rarity", 2, 1, 20);

        BLUEBERRY_RARITY = BUILDER
                .comment("Rarity of blueberry bush generation (1-20)")
                .defineInRange("blueberry_rarity", 2, 1, 20);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
