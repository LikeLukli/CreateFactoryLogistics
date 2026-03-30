package ru.zznty.create_factory_abstractions.api.generic.capability;

import com.simibubi.create.content.logistics.packager.IdentifiedInventory;
import com.simibubi.create.content.logistics.packager.PackagerBlockEntity;
import com.simibubi.create.content.logistics.stockTicker.PackageOrderWithCrafts;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import ru.zznty.create_factory_abstractions.CreateFactoryAbstractions;
import ru.zznty.create_factory_abstractions.api.generic.AbstractionsCapabilities;
import ru.zznty.create_factory_abstractions.api.generic.key.GenericKeyRegistration;
import ru.zznty.create_factory_abstractions.api.generic.stack.GenericStack;
import ru.zznty.create_factory_abstractions.generic.impl.BuiltInPackagerAttachedHandler;

public interface PackagerAttachedHandler {
    int slotCount();

    GenericStack extract(int slot, int amount, boolean simulate);

    boolean unwrap(Level level, BlockPos pos, BlockState state, Direction side,
                   @Nullable PackageOrderWithCrafts orderContext, ItemStack box, boolean simulate);

    PackageBuilder newPackage();

    GenericKeyRegistration supportedKey();

    Block supportedGauge();

    @Nullable IdentifiedInventory identifiedInventory();

    @Nullable
    static PackagerAttachedHandler get(PackagerBlockEntity blockEntity) {
        if (CreateFactoryAbstractions.EXTENSIBILITY_AVAILABLE) {
            Level level = blockEntity.getLevel();
            if (level == null) return null;
            return level.getCapability(AbstractionsCapabilities.PACKAGER_ATTACHED,
                    blockEntity.getBlockPos(), blockEntity.getBlockState(), blockEntity, null);
        }
        return new BuiltInPackagerAttachedHandler(blockEntity);
    }
}
