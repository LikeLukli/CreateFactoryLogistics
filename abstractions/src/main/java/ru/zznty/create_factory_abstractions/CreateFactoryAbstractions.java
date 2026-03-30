package ru.zznty.create_factory_abstractions;

import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.fml.javafmlmod.FMLJavaModLoadingContext;
import ru.zznty.create_factory_abstractions.compat.computercraft.AbstractionsComputerCraftCompat;
import ru.zznty.create_factory_abstractions.generic.impl.GenericContentExtender;
import ru.zznty.create_factory_abstractions.registry.TypeRegistries;

@Mod(CreateFactoryAbstractions.ID)
public final class CreateFactoryAbstractions {
    public static final String ID = "create_factory_abstractions";

    public static final boolean EXTENSIBILITY_AVAILABLE = ModList.get().isLoaded("create_factory_logistics");

    public CreateFactoryAbstractions(FMLJavaModLoadingContext context) {
        TypeRegistries.register(context.getModEventBus());
        GenericContentExtender.register(context.getModEventBus());
        context.getModEventBus().addListener(CreateFactoryAbstractions::init);
    }

    public static void init(final FMLCommonSetupEvent event) {
        if (ModList.get().isLoaded(AbstractionsComputerCraftCompat.MOD_ID))
            event.enqueueWork(AbstractionsComputerCraftCompat::register);
    }
}
