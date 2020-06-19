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
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.Item;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.text.TranslatableText;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Overwrite;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.*;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Environment(EnvType.CLIENT)
@Mixin(CreativeInventoryScreen.class)
public abstract class MixinCreativeInventoryScreen extends AbstractInventoryScreen<CreativeInventoryScreen.CreativeContainer> {

    public MixinCreativeInventoryScreen(CreativeInventoryScreen.CreativeContainer container, PlayerInventory playerInventory, Text text) {
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
        if (restore) {
            for(j = 0; j < PlayerInventory.getHotbarSize(); j++) {
                ItemStack itemStack = ((ItemStack)hotbarStorageEntry.get(j)).copy();
                clientPlayerEntity.inventory.setInvStack(j, itemStack);
                client.interactionManager.clickCreativeStack(itemStack, 36 + j);
            }

            clientPlayerEntity.playerContainer.sendContentUpdates();
        } else if (save) {
            for(j = 0; j < PlayerInventory.getHotbarSize(); j++) {
                hotbarStorageEntry.set(j, clientPlayerEntity.inventory.getInvStack(j).copy());
            }

            String hotbarKey = client.options.keysHotbar[index % 9].getLocalizedName();
            String restoreKey = client.options.keyLoadToolbarActivator.getLocalizedName();
            String[] modifiers = {
                    (Keybinds.group1.isNotBound()) ? "" : Keybinds.group1.getLocalizedName() + "+",
                    (Keybinds.group2.isNotBound()) ? "" : Keybinds.group2.getLocalizedName() + "+",
                    (Keybinds.group3.isNotBound()) ? "" : Keybinds.group3.getLocalizedName() + "+",
            };

            TranslatableText originalText = new TranslatableText("inventory.hotbarSaved", modifiers[index/9] + restoreKey, hotbarKey);

            client.inGameHud.setOverlayMessage((Text) originalText, false);
            hotbarStorage.save();
        }
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
                (Keybinds.group1.isNotBound()) ? "" : Keybinds.group1.getLocalizedName() + "+",
                (Keybinds.group2.isNotBound()) ? "" : Keybinds.group2.getLocalizedName() + "+",
                (Keybinds.group3.isNotBound()) ? "" : Keybinds.group3.getLocalizedName() + "+",
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

                        hotbarKey = client.options.keysHotbar[j].getLocalizedName();
                        saveKey = client.options.keySaveToolbarActivator.getLocalizedName();
                        itemStack.setCustomName(new TranslatableText(
                                "inventory.hotbarInfo",
                                modifiers[displayItemIndex]+saveKey,
                                hotbarKey
                        ));
                        ((CreativeInventoryScreen.CreativeContainer) this.container).itemList.add(itemStack);
                    } else {
                        ((CreativeInventoryScreen.CreativeContainer) this.container).itemList.add(ItemStack.EMPTY);
                    }
                }
            } else {
                ((CreativeInventoryScreen.CreativeContainer) this.container).itemList.addAll(hotbarStorageEntry);
            }
        }
    }
}
