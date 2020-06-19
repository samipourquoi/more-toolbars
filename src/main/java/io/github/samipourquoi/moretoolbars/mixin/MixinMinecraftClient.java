package io.github.samipourquoi.moretoolbars.mixin;

import io.github.samipourquoi.moretoolbars.Keybinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.hud.InGameHud;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.GameOptions;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.ArrayList;

/**
 * @author samipourquoi
 */
@Mixin(MinecraftClient.class)
@Environment(EnvType.CLIENT)
public class MixinMinecraftClient {

    @Shadow @Final public GameOptions options;
    @Shadow public ClientPlayerEntity player;
    @Shadow @Final public InGameHud inGameHud;
    @Shadow public Screen currentScreen;

    /**
     * Cancels the loop which checks if a key has been pressed to save/restore a toolbar.
     */
    @ModifyConstant(method = "handleInputEvents", constant = @Constant(intValue = 9))
    private int noLoop(int i) {
        return 0;
    }

    /**
     * Reinject the loop cancelled before as we can't "part-overwrite" a method with mixin.
     */
    @Inject(method = "handleInputEvents", at = @At("HEAD"))
    private void saveOrRestoreMoreToolbars(CallbackInfo info) {
        for(int i = 0; i < 9; i++) {
            boolean bl = this.options.keySaveToolbarActivator.isPressed();
            boolean bl2 = this.options.keyLoadToolbarActivator.isPressed();
            if (this.options.keysHotbar[i].wasPressed()) {
                if (this.player.isSpectator()) {
                    this.inGameHud.getSpectatorHud().selectSlot(i);
                } else if (!this.player.isCreative() || this.currentScreen != null || !bl2 && !bl) {
                    this.player.inventory.selectedSlot = i;
                } else {
                    boolean[] pressedModifiers = {Keybinds.group1.isPressed(), Keybinds.group2.isPressed(), Keybinds.group3.isPressed()};

                    if (pressedModifiers[0] || Keybinds.group1.isNotBound() && !(pressedModifiers[1] || pressedModifiers[2])) {
                        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), i+0*9, bl2, bl);
                        System.out.println(1);
                    } else if (pressedModifiers[1] || Keybinds.group2.isNotBound() && !(pressedModifiers[0] || pressedModifiers[2])) {
                        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), i+1*9, bl2, bl);
                        System.out.println(2);
                    } else if (pressedModifiers[2] || Keybinds.group3.isNotBound() && !(pressedModifiers[0] || pressedModifiers[1])) {
                        CreativeInventoryScreen.onHotbarKeyPress(MinecraftClient.getInstance(), i+2*9, bl2, bl);
                        System.out.println(3);
                    }
                }
            }
        }
    }
}