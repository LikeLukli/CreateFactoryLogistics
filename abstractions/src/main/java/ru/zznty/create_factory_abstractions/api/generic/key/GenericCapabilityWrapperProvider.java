package ru.zznty.create_factory_abstractions.api.generic.key;

import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import ru.zznty.create_factory_abstractions.api.generic.capability.GenericInventorySummaryProvider;
import org.jetbrains.annotations.Nullable;

public interface GenericCapabilityWrapperProvider<Cap> {
    BlockCapability<Cap, @Nullable Direction> blockCapability();

    Cap wrap(GenericInventorySummaryProvider summaryProvider);

    GenericInventorySummaryProvider unwrap(Cap capability);
}
