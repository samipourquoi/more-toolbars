package io.github.samipourquoi.moretoolbars.mixin;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;
import net.minecraft.client.options.HotbarStorage;
import net.minecraft.client.options.HotbarStorageEntry;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Environment(EnvType.CLIENT)
@Mixin(HotbarStorage.class)
public class MixinHotbarStorage {
    @Final public HotbarStorageEntry[] entries = new HotbarStorageEntry[27];

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 9))
    private int constructor(int number) {
        return 27;
    }

    @ModifyConstant(method = "load", constant = @Constant(intValue = 9))
    private int load(int number) {
        return 27;
    }

    @ModifyConstant(method = "save", constant = @Constant(intValue = 9))
    private int save(int number) {
        return 27;
    }
}
