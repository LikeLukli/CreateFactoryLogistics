package ru.zznty.create_factory_logistics.logistics.packager;

import com.simibubi.create.AllBlockEntityTypes;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import net.minecraft.core.registries.Registries;
import net.minecraft.tags.TagKey;
import net.minecraft.world.level.block.Block;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.capabilities.RegisterCapabilitiesEvent;
import net.neoforged.neoforge.fluids.capability.templates.FluidHandlerItemStack;
import org.jetbrains.annotations.ApiStatus;
import ru.zznty.create_factory_abstractions.api.generic.AbstractionsCapabilities;
import ru.zznty.create_factory_abstractions.generic.impl.BuiltInPackagerAttachedHandler;
import ru.zznty.create_factory_logistics.Config;
import ru.zznty.create_factory_logistics.CreateFactoryLogistics;
import ru.zznty.create_factory_logistics.FactoryBlockEntities;
import ru.zznty.create_factory_logistics.FactoryItems;
import ru.zznty.create_factory_logistics.logistics.jar.JarPackageItem;
import ru.zznty.create_factory_logistics.logistics.jarPackager.JarPackagerAttachedHandler;
import ru.zznty.create_factory_logistics.logistics.jarPackager.JarPackagerBlockEntity;
import ru.zznty.create_factory_logistics.logistics.networkLink.NetworkLinkBlockEntity;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = CreateFactoryLogistics.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class CapabilityAttacher {
    private static final TagKey<Block> ITEM_PACKAGER = TagKey.create(Registries.BLOCK,
                                                                     CreateFactoryLogistics.resource("packager_item"));

    @SubscribeEvent
    public static void registerCapabilities(RegisterCapabilitiesEvent event) {
        // Register PACKAGER_ATTACHED for Create's built-in packager
        event.registerBlockEntity(AbstractionsCapabilities.PACKAGER_ATTACHED,
                AllBlockEntityTypes.PACKAGER.get(),
                (packagerBE, direction) -> {
                    if (packagerBE.getBlockState().is(ITEM_PACKAGER)) {
                        return new BuiltInPackagerAttachedHandler(packagerBE);
                    }
                    return null;
                });

        // Register PACKAGER_ATTACHED for jar packager
        event.registerBlockEntity(AbstractionsCapabilities.PACKAGER_ATTACHED,
                FactoryBlockEntities.JAR_PACKAGER.get(),
                (be, direction) -> {
                    if (be instanceof JarPackagerBlockEntity jarPackagerBE) {
                        return new JarPackagerAttachedHandler(jarPackagerBE);
                    }
                    return null;
                });

        // Register GENERIC_INVENTORY for network link block entity
        event.registerBlockEntity(AbstractionsCapabilities.GENERIC_INVENTORY,
                FactoryBlockEntities.NETWORK_LINK.get(),
                NetworkLinkBlockEntity::getGenericInventoryForCapability);

        // Register fluid handler for jar items
        event.registerItem(Capabilities.FluidHandler.ITEM,
                (stack, ctx) -> new FluidHandlerItemStack(stack, Config.jarCapacity),
                FactoryItems.REGULAR_JAR.get(), FactoryItems.RARE_JAR.get());
    }

    private CapabilityAttacher() {
    }
}
