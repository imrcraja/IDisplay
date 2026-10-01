package com.rcempire.idisplay.mixin;

import com.rcempire.idisplay.IDisplayClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(EntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Unique
    private static final ThreadLocal<Entity> IDISPLAY_ENTITY = new ThreadLocal<>();

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD")
    )
    private void idisplay$captureEntity(Entity entity, Text text, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        IDISPLAY_ENTITY.set(entity);
    }

    @ModifyVariable(
            method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 0
    )
    private Text idisplay$customLabel(Text original) {
        Entity entity = IDISPLAY_ENTITY.get();
        if (!(entity instanceof AbstractClientPlayerEntity)) return original;
        return Text.literal(IDisplayClient.getDisplayName(entity.getUuid(), original.getString()));
    }

    @Inject(
            method = "renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V",
            at = @At("RETURN")
    )
    private void idisplay$releaseEntity(Entity entity, Text text, MatrixStack matrices,
                                         VertexConsumerProvider vertexConsumers, int light, CallbackInfo ci) {
        IDISPLAY_ENTITY.remove();
    }
}
