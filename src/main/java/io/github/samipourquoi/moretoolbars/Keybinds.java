package io.github.samipourquoi.moretoolbars;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

/**
 * Adds the keybinds for the modifiers of each group.
 *
 * @author samipourquoi
 */
@Environment(EnvType.CLIENT)
public class Keybinds implements ModInitializer {

    public static KeyBinding group1;
    public static KeyBinding group2;
    public static KeyBinding group3;

    @Override
    public void onInitialize() {

        group1 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moretoolbars.group1",
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEY.getCode(),
                "key.categories.creative"
        ));

        group2 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moretoolbars.group2",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.creative"
        ));

        group3 = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.moretoolbars.group3",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SUPER,
                "key.categories.creative"
        ));
    }
}
