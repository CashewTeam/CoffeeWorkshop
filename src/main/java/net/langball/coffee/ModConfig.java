package net.langball.coffee;

import net.minecraftforge.common.ForgeConfigSpec;

public class ModConfig {
    public static final ForgeConfigSpec.Builder BUILDER = new ForgeConfigSpec.Builder();
    public static final ForgeConfigSpec SPEC;

    public static final ForgeConfigSpec.IntValue COFFEE_TREE_RARITY;
    public static final ForgeConfigSpec.IntValue BLUEBERRY_RARITY;

    // Drinks
    public static final ForgeConfigSpec.BooleanValue ENABLE_EMPTY_CUP_RETURN;
    public static final ForgeConfigSpec.BooleanValue ENABLE_MULTI_CUP;

    static {
        BUILDER.push("worldgen");

        COFFEE_TREE_RARITY = BUILDER
                .comment("Rarity of coffee tree generation (1-20)")
                .defineInRange("coffee_tree_rarity", 2, 1, 20);

        BLUEBERRY_RARITY = BUILDER
                .comment("Rarity of blueberry bush generation (1-20)")
                .defineInRange("blueberry_rarity", 2, 1, 20);

        BUILDER.pop();

        BUILDER.push("drinks");

        ENABLE_EMPTY_CUP_RETURN = BUILDER
                .comment("Whether finishing a drink returns an empty cup or glass")
                .define("enable_empty_cup_return", true);

        ENABLE_MULTI_CUP = BUILDER
                .comment("Whether drinks track remaining servings (multi-cup NBT system)")
                .define("enable_multi_cup", true);

        BUILDER.pop();
        SPEC = BUILDER.build();
    }
}
