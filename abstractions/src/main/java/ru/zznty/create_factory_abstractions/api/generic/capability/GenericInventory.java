package ru.zznty.create_factory_abstractions.api.generic.capability;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import ru.zznty.create_factory_abstractions.api.generic.AbstractionsCapabilities;
import ru.zznty.create_factory_abstractions.api.generic.key.GenericCapabilityWrapperProvider;
import ru.zznty.create_factory_abstractions.api.generic.key.GenericKeyRegistration;

public interface GenericInventory {
    @Nullable GenericInventorySummaryProvider get(GenericKeyRegistration registration);

//    maybe in the future
//    GenericStack insert(GenericStack stack);

    static GenericInventory of(BlockEntity be) {
        Level level = be.getLevel();
        BlockPos pos = be.getBlockPos();
        if (level == null) return registration -> null;

        GenericInventory inv = level.getCapability(AbstractionsCapabilities.GENERIC_INVENTORY, pos,
                be.getBlockState(), be, null);
        if (inv != null) return inv;

        return registration -> {
            @Nullable GenericCapabilityWrapperProvider<Object> provider = registration.provider().capabilityWrapperProvider();
            if (provider == null) return null;
            Object cap = level.getCapability(provider.blockCapability(), pos, be.getBlockState(), be, null);
            return cap != null ? provider.unwrap(cap) : null;
        };
    }
}
