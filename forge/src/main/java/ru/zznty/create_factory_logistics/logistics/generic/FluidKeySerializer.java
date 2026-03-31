package ru.zznty.create_factory_logistics.logistics.generic;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.material.Fluid;
import net.minecraft.world.level.material.Fluids;
import ru.zznty.create_factory_abstractions.api.generic.key.GenericKeySerializer;

public class FluidKeySerializer implements GenericKeySerializer<FluidKey> {
    @Override
    public FluidKey read(CompoundTag tag) {
        String key = tag.getString("id");
        return key.isEmpty() ? new FluidKey(Fluids.EMPTY, null) :
               new FluidKey(BuiltInRegistries.FLUID.get(ResourceLocation.parse(key)),
                            tag.contains("Tag") ? tag.getCompound("Tag") : null);
    }

    @Override
    public void write(FluidKey key, CompoundTag tag) {
        ResourceLocation resourceLocation = BuiltInRegistries.FLUID.getKey(key.fluid());
        tag.putString("id", resourceLocation == null ? "minecraft:empty" : resourceLocation.toString());
        if (key.nbt() != null)
            tag.put("Tag", key.nbt().copy());
    }

    @Override
    public FluidKey read(FriendlyByteBuf buf) {
        ResourceLocation id = buf.readResourceLocation();
        Fluid fluid = BuiltInRegistries.FLUID.get(id);
        return new FluidKey(fluid != null ? fluid : Fluids.EMPTY, buf.readNbt());
    }

    @Override
    public void write(FluidKey key, FriendlyByteBuf buf) {
        ResourceLocation fluidKey = BuiltInRegistries.FLUID.getKey(key.fluid());
        buf.writeResourceLocation(fluidKey != null ? fluidKey : BuiltInRegistries.FLUID.getKey(Fluids.EMPTY));
        buf.writeNbt(key.nbt());
    }
}
