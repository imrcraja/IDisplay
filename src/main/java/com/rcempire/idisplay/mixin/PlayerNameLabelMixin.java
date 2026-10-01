package com.rcempire.idisplay.mixin;

import com.rcempire.idisplay.IDisplayClient;
import net.minecraft.client.network.AbstractClientPlayerEntity;
import net.minecraft.client.render.entity.LivingEntityRenderer;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(LivingEntityRenderer.class)
public abstract class PlayerNameLabelMixin {
    @ModifyVariable(method = "renderLabelIfPresent", at = @At("HEAD"), argsOnly = true, ordinal = 0)
    private Text idisplay$customLabel(Text original) {
        AbstractClientPlayerEntity entity = PlayerEntityRendererMixin.IDISPLAY_ENTITY.get();
        if (entity == null) return original;
        return Text.literal(IDisplayClient.getDisplayName(entity.getUuid(), original.getString()));
    }
}
