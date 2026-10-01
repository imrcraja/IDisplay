package com.rcempire.idisplay.mixin;

import com.rcempire.idisplay.IDisplayClient;
import net.minecraft.client.gui.hud.PlayerListHud;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.text.Text;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PlayerListHud.class)
public abstract class PlayerListHudMixin {
    @Inject(method = "getPlayerName", at = @At("HEAD"), cancellable = true)
    private void idisplay$customName(PlayerListEntry entry, CallbackInfoReturnable<Text> cir) {
        if (entry == null || entry.getProfile() == null) return;
        String real = entry.getProfile().getName();
        String custom = IDisplayClient.getDisplayName(entry.getProfile().getId(), real);
        if (!custom.equals(real)) cir.setReturnValue(Text.literal(custom));
    }
}
