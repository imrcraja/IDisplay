package com.rcempire.idisplay.mixin;

import com.rcempire.idisplay.IDisplayClient;
import net.minecraft.client.gui.hud.ChatHud;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

@Mixin(ChatHud.class)
public abstract class ChatHudMixin {
    @ModifyVariable(
            method = "addMessage(Lnet/minecraft/text/Text;)V",
            at = @At("HEAD"), argsOnly = true, ordinal = 0
    )
    private Text idisplay$replaceOwnName(Text message) {
        return IDisplayClient.replaceOwnName(message);
    }
}
