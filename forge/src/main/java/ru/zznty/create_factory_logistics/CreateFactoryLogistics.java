package ru.zznty.create_factory_logistics;

import com.mojang.logging.LogUtils;
import com.simibubi.create.foundation.data.CreateRegistrate;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import org.slf4j.Logger;
import ru.zznty.create_factory_abstractions.compat.computercraft.AbstractionsComputerCraftCompat;
import ru.zznty.create_factory_logistics.compat.computercraft.ComputerCraftCompat;
import ru.zznty.create_factory_logistics.data.FactoryDataGen;

@Mod(CreateFactoryLogistics.MODID)
public class CreateFactoryLogistics {
    public static final String MODID = "create_factory_logistics";

    public static final Logger LOGGER = LogUtils.getLogger();

    public static final CreateRegistrate REGISTRATE = CreateRegistrate.create(MODID)
            .defaultCreativeTab("create_factory_logistics_tab",
                                t -> t.icon(() -> FactoryItems.REGULAR_JAR.get().getDefaultInstance()))
            .build();

    public CreateFactoryLogistics(FMLJavaModLoadingContext context) {
        FactoryGenericExtension.register();

        IEventBus modEventBus = context.getModEventBus();

        REGISTRATE.registerEventListeners(modEventBus);
        FactoryRecipes.REGISTER.register(modEventBus);
        FactoryArmInteractionPointTypes.ARM_INTERACTION_POINT_TYPES.register(modEventBus);
        FactoryGenericAttributeTypes.REGISTER.register(modEventBus);

        modEventBus.addListener(FactoryEntities::registerEntityAttributes);
        modEventBus.addListener(FactoryDataGen::gatherData);
        modEventBus.addListener(CreateFactoryLogistics::init);

        FactoryModels.register();
        FactoryItems.register();
        FactoryEntities.register();
        FactoryBlockEntities.register();
        FactoryBlocks.register();
        FactoryMenus.register();

        context.registerConfig(ModConfig.Type.SERVER, Config.SPEC);
        context.registerConfig(ModConfig.Type.CLIENT, ClientConfig.SPEC);
    }

    public static void init(final FMLCommonSetupEvent event) {
        event.enqueueWork(FactoryInventoryIdentifiers::register);
        event.enqueueWork(FactoryJarUnpackingHandlers::register);
        if (ModList.get().isLoaded(AbstractionsComputerCraftCompat.MOD_ID))
            event.enqueueWork(ComputerCraftCompat::register);
    }

    public static ResourceLocation resource(String path) {
        return ResourceLocation.fromNamespaceAndPath(MODID, path);
    }
}
