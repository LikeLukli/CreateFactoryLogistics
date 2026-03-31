package ru.zznty.create_factory_abstractions.api.generic;

import net.minecraft.core.Direction;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.capabilities.BlockCapability;
import ru.zznty.create_factory_abstractions.api.generic.capability.GenericInventory;
import ru.zznty.create_factory_abstractions.api.generic.capability.PackagerAttachedHandler;
import org.jetbrains.annotations.Nullable;

public class AbstractionsCapabilities {
    public static final BlockCapability<PackagerAttachedHandler, @Nullable Direction> PACKAGER_ATTACHED =
            BlockCapability.createNullable(
                    ResourceLocation.fromNamespaceAndPath("create_factory_abstractions", "packager_attached"),
                    PackagerAttachedHandler.class);

    public static final BlockCapability<GenericInventory, @Nullable Direction> GENERIC_INVENTORY =
            BlockCapability.createNullable(
                    ResourceLocation.fromNamespaceAndPath("create_factory_abstractions", "generic_inventory"),
                    GenericInventory.class);
}
