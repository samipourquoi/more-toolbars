package io.github.samipourquoi.moretoolbars.mixin;

import io.github.samipourquoi.moretoolbars.Keybinds;
import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.screen.ingame.AbstractInventoryScreen;
import net.minecraft.client.gui.screen.ingame.CreativeInventoryScreen;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import net.minecraft.client.options.KeyBinding;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.OrderedText;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeScreenHandler> {

    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeScreenHandler container, PlayerInventory playerInventory, Text text) {
        super(container, playerInventory, text);
    }

    /**
     * @author samipourquoi
     */
    @Overwrite
    public static void onHotbarKeyPress(MinecraftClient client, int index, boolean restore, boolean save) {
        ClientPlayerEntity clientPlayerEntity = client.player;
        HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
        HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(index);
        int j;
        if (clientPlayerEntity == null) return;
        if (restore) {
            for(j = 0; j < PlayerInventory.getHotbarSize(); j++) {
                ItemStack itemStack = ((ItemStack)hotbarStorageEntry.get(j)).copy();
                clientPlayerEntity.inventory.setStack(j, itemStack);
                client.interactionManager.clickCreativeStack(itemStack, 36 + j);
            }

            clientPlayerEntity.playerScreenHandler.sendContentUpdates();
        } else if (save) {
            for(j = 0; j < PlayerInventory.getHotbarSize(); j++) {
                hotbarStorageEntry.set(j, clientPlayerEntity.inventory.getStack(j).copy());
            }

            String hotbarKey = getTranslationName(client.options.keysHotbar[index % 9]);
            String restoreKey = getTranslationName(client.options.keyLoadToolbarActivator);
            String[] modifiers = {
                    (Keybinds.group1.isUnbound()) ? "" : getTranslationName(Keybinds.group1) + "+",
                    (Keybinds.group2.isUnbound()) ? "" : getTranslationName(Keybinds.group2) + "+",
                    (Keybinds.group3.isUnbound()) ? "" : getTranslationName(Keybinds.group3) + "+",
            };

            TranslatableText originalText = new TranslatableText("inventory.hotbarSaved", modifiers[index/9] + restoreKey, hotbarKey);

            client.inGameHud.setOverlayMessage((Text) originalText, false);
            hotbarStorage.save();
        }
    }

    private static String getTranslationName(KeyBinding key) {
        String a = new TranslatableText(key.getBoundKeyTranslationKey()).asString();
        String b = key.getBoundKeyLocalizedText().getString();
        return (b.equals("")) ? a : b;
    }

    /**
     * Cancels the render of the saved toolbars.
     */
    @ModifyConstant(
        method = "setSelectedTab",
        slice = @Slice(
            from = @At(
                value = "HEAD"
            ),
            to = @At(
                value = "INVOKE",
                target = "Lnet/minecraft/client/options/HotbarStorage;getSavedHotbar(I)Lnet/minecraft/client/options/HotbarStorageEntry;"
            )
        ),
        constant = @Constant(intValue = 9)
    )
    private int noOldRenderSavedToolbars(int number) {
        return 0;
    }

    /**
     * "Overwrites" the render of the saved toolbars.
     */
    @Inject(
            method = "setSelectedTab",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/client/MinecraftClient;getCreativeHotbarStorage()Lnet/minecraft/client/options/HotbarStorage;"
            )
    )
    private void renderSavedToolbars(ItemGroup group, CallbackInfo info) {
        MinecraftClient client = MinecraftClient.getInstance();
        HotbarStorage hotbarStorage = client.getCreativeHotbarStorage();
        Item[] displayItems = {Items.PAPER, Items.MAP, Items.FILLED_MAP};
        int displayItemIndex = 0;
        String[] modifiers = {
                (Keybinds.group1.isUnbound()) ? "" : getTranslationName(Keybinds.group1) + "+",
                (Keybinds.group2.isUnbound()) ? "" : getTranslationName(Keybinds.group2) + "+",
                (Keybinds.group3.isUnbound()) ? "" : getTranslationName(Keybinds.group3) + "+",
        };

        for(int i = 0; i < 27; i++) {
            HotbarStorageEntry hotbarStorageEntry = hotbarStorage.getSavedHotbar(i);
            String hotbarKey;
            String saveKey;

            if (hotbarStorageEntry.isEmpty()) {
                if ((i % 9 == 0) && i != 0) displayItemIndex++;

                for(int j = 0; j < 9; j++) {
                    if (i % 9 == j) {
                        ItemStack itemStack = new ItemStack(displayItems[displayItemIndex]);
                        itemStack.getOrCreateSubTag("CustomCreativeLock");

                        hotbarKey = getTranslationName(client.options.keysHotbar[j]);
                        saveKey = getTranslationName(client.options.keySaveToolbarActivator);
                        itemStack.setCustomName(new TranslatableText(
                                "inventory.hotbarInfo",
                                modifiers[displayItemIndex]+saveKey,
                                hotbarKey
                        ));
                        this.getScreenHandler().itemList.add(itemStack);
                    } else {
                        this.getScreenHandler().itemList.add(ItemStack.EMPTY);
                    }
                }
            } else {
                this.getScreenHandler().itemList.addAll(hotbarStorageEntry);
            }
        }
    }
}
