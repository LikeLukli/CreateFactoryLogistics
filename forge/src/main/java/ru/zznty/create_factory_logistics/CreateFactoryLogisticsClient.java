package ru.zznty.create_factory_logistics;

import net.createmod.catnip.config.ui.BaseConfigScreen;
import net.createmod.ponder.foundation.PonderIndex;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.neoforge.client.ConfigScreenHandler;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLLoadCompleteEvent;
import ru.zznty.create_factory_logistics.ponder.PonderPlugin;

import static ru.zznty.create_factory_logistics.CreateFactoryLogistics.MODID;

@Mod.EventBusSubscriber(modid = MODID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class CreateFactoryLogisticsClient {
    @SubscribeEvent
    public static void onClientInit(FMLClientSetupEvent event) {
        BaseConfigScreen.setDefaultActionFor(MODID, base -> base
                .withButtonLabels(null, null, "Gameplay Settings")
                .withSpecs(ClientConfig.SPEC, null, Config.SPEC));

        PonderIndex.addPlugin(new PonderPlugin());
    }

    @SubscribeEvent
    static void onLoadComplete(FMLLoadCompleteEvent event) {
        ModList.get().getModContainerById(MODID).get().registerExtensionPoint(ConfigScreenHandler.ConfigScreenFactory.class,
                () -> new ConfigScreenHandler.ConfigScreenFactory(
                        (mc, previousScreen) -> new BaseConfigScreen(previousScreen, MODID)));
    }
}
