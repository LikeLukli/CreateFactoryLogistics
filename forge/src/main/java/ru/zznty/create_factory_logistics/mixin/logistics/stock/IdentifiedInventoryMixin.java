package ru.zznty.create_factory_logistics.mixin.logistics.stock;

import com.simibubi.create.api.packager.InventoryIdentifier;
import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import net.minecraft.core.Direction;
import net.neoforged.neoforge.capabilities.BlockCapability;
import net.neoforged.neoforge.capabilities.Capabilities;
import net.neoforged.neoforge.items.IItemHandler;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import ru.zznty.create_factory_abstractions.generic.support.GenericIdentifiedInventory;

@Mixin(IdentifiedInventory.class)
public class IdentifiedInventoryMixin implements GenericIdentifiedInventory {
    @Final
    @Shadow(remap = false)
    @Nullable
    private IItemHandler handler;

    @Final
    @Shadow(remap = false)
    @Nullable
    private InventoryIdentifier identifier;

    @Unique
    @Nullable
    private BlockCapability<?, @Nullable Direction> createFactoryLogistics$capability;

    @Unique
    @Nullable
    private Object createFactoryLogistics$handler;

    @Override
    public @Nullable InventoryIdentifier identifier() {
        return identifier;
    }

    @Override
    public BlockCapability<?, @Nullable Direction> blockCapability() {
        return handler != null ? Capabilities.ItemHandler.BLOCK : createFactoryLogistics$capability;
    }

    @Override
    public Object handler() {
        return handler != null ? handler : createFactoryLogistics$handler;
    }

    @Override
    public <T> void setCapability(BlockCapability<T, @Nullable Direction> capability, T handler) {
        if (this.handler != null)
            throw new IllegalArgumentException("Handler must be null");
        createFactoryLogistics$capability = capability;
        createFactoryLogistics$handler = handler;
    }
}
