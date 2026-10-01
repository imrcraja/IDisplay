package com.rcempire.idisplay.mixin;

import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.PlayerEntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerEntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    @Unique
    public static final ThreadLocal<AbstractClientPlayerEntity> IDISPLAY_ENTITY = new ThreadLocal<>();

    @Inject(method = "render", at = @At("HEAD"))
    private void idisplay$capture(AbstractClientPlayerEntity entity, float entityYaw, float tickDelta,
                                   MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   int light, CallbackInfo ci) {
        IDISPLAY_ENTITY.set(entity);
    }

    @Inject(method = "render", at = @At("TAIL"))
    private void idisplay$release(AbstractClientPlayerEntity entity, float entityYaw, float tickDelta,
                                   MatrixStack matrices, VertexConsumerProvider vertexConsumers,
                                   int light, CallbackInfo ci) {
        IDISPLAY_ENTITY.remove();
    }
}
