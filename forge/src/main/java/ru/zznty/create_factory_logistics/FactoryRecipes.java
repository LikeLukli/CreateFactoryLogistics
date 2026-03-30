package ru.zznty.create_factory_logistics;

import net.minecraft.world.item.crafting.RecipeSerializer;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.ForgeRegistries;
import net.neoforged.neoforge.registries.RegistryObject;
import ru.zznty.create_factory_logistics.logistics.networkLink.NetworkLinkQualificationRecipe;

public class FactoryRecipes {
    public static final DeferredRegister<RecipeSerializer<?>> REGISTER = DeferredRegister.create(ForgeRegistries.RECIPE_SERIALIZERS, CreateFactoryLogistics.MODID);

    public static final RegistryObject<NetworkLinkQualificationRecipe.Serializer> NETWORK_LINK_QUALIFICATION =
            REGISTER.register("network_link_qualification", NetworkLinkQualificationRecipe.Serializer::new);
}
