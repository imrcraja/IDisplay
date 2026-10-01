package com.rcempire.idisplay.mixin;

import com.rcempire.idisplay.IDisplayClient;
import net.minecraft.client.render.VertexConsumerProvider;
import net.minecraft.client.render.entity.EntityRenderer;
import net.minecraft.client.util.math.MatrixStack;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyArgs;
import org.spongepowered.asm.mixin.injection.invoke.arg.Args;

@Mixin(EntityRenderer.class)
public abstract class PlayerEntityRendererMixin {
    /*
     * Minecraft 1.20.1 calls renderLabelIfPresent from EntityRenderer.render().
     * Modify the call arguments instead of modifying a method parameter directly.
     * This avoids the fragile ModifyVariable target that caused startup crashes
     * on some Fabric/Yarn mappings and modded launchers.
     */
    @ModifyArgs(
            method = "render",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/render/entity/EntityRenderer;renderLabelIfPresent(Lnet/minecraft/entity/Entity;Lnet/minecraft/text/Text;Lnet/minecraft/client/util/math/MatrixStack;Lnet/minecraft/client/render/VertexConsumerProvider;I)V"
            )
    )
    private void idisplay$customLabel(Args args) {
        Entity entity = args.get(0);
        if (!(entity instanceof net.minecraft.client.network.AbstractClientPlayerEntity)) {
            return;
        }

        Text original = args.get(1);
        String fallback = original.getString();
        String custom = IDisplayClient.getDisplayName(entity.getUuid(), fallback);

        if (!custom.equals(fallback)) {
            args.set(1, Text.literal(custom));
        }
    }
}
