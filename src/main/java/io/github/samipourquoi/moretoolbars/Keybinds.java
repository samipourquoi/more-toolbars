package io.github.samipourquoi.moretoolbars;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.keybinding.FabricKeyBinding;
import net.fabricmc.fabric.api.client.keybinding.KeyBindingRegistry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.client.util.InputUtil;
import net.minecraft.util.Identifier;
import org.lwjgl.glfw.GLFW;

@Environment(EnvType.CLIENT)
public class Keybinds implements ModInitializer {

    public static FabricKeyBinding group1;
    public static FabricKeyBinding group2;
    public static FabricKeyBinding group3;

    @Override
    public void onInitialize() {

        group1 = FabricKeyBinding.Builder.create(
                new Identifier("moretoolbars", "group1"),
                InputUtil.Type.KEYSYM,
                InputUtil.UNKNOWN_KEYCODE.getKeyCode(),
                "key.categories.creative"
        ).build();

        group2 = FabricKeyBinding.Builder.create(
                new Identifier("moretoolbars", "group2"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_ALT,
                "key.categories.creative"
        ).build();

        group3 = FabricKeyBinding.Builder.create(
                new Identifier("moretoolbars", "group3"),
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_LEFT_SUPER,
                "key.categories.creative"
        ).build();

        KeyBindingRegistry.INSTANCE.register(group1);
        KeyBindingRegistry.INSTANCE.register(group2);
        KeyBindingRegistry.INSTANCE.register(group3);
    }
}
