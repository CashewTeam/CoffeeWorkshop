package net.langball.coffee.client.renderer;

import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Axis;
import net.langball.coffee.block.entity.DrinkDisplayBlockEntity;
import net.langball.coffee.client.DrinkDisplayModelRegistry;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.BlockEntityRenderer;
import net.minecraft.client.renderer.blockentity.BlockEntityRendererProvider;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.block.HorizontalDirectionalBlock;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.client.model.data.ModelData;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;

@OnlyIn(Dist.CLIENT)
public class DrinkDisplayRenderer implements BlockEntityRenderer<DrinkDisplayBlockEntity> {
    private static final Logger LOGGER = LogManager.getLogger();

    public DrinkDisplayRenderer(BlockEntityRendererProvider.Context ctx) {
    }

    @Override
    public void render(DrinkDisplayBlockEntity be, float partialTick, PoseStack poseStack,
                       MultiBufferSource buffer, int packedLight, int packedOverlay) {
        ResourceLocation drinkId = be.getDrinkId();
        if (drinkId == null) return;

        BakedModel model = DrinkDisplayModelRegistry.getModel(drinkId);
        if (model == null) return;

        Direction facing = be.getBlockState().getValue(HorizontalDirectionalBlock.FACING);
        float rotation = switch (facing) {
            case SOUTH -> 180.0F;
            case WEST -> 270.0F;
            case EAST -> 90.0F;
            default -> 0.0F;
        };

        poseStack.pushPose();
        poseStack.translate(0.5, 0.0, 0.5);
        poseStack.mulPose(Axis.YP.rotationDegrees(rotation));
        poseStack.translate(-0.5, 0.0, -0.5);

        VertexConsumer vertexConsumer = buffer.getBuffer(RenderType.cutout());
        RandomSource random = RandomSource.create(42L);

        for (BakedModel nestedModel : model.getRenderPasses(be.getDrinkRaw(), false)) {
            for (Direction direction : Direction.values()) {
                for (var quad : nestedModel.getQuads(null, direction, random, ModelData.EMPTY, RenderType.cutout())) {
                    vertexConsumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay);
                }
            }
            for (var quad : nestedModel.getQuads(null, null, random, ModelData.EMPTY, RenderType.cutout())) {
                vertexConsumer.putBulkData(poseStack.last(), quad, 1.0F, 1.0F, 1.0F, packedLight, packedOverlay);
            }
        }

        poseStack.popPose();
    }
}
