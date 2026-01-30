package org.betterx.betternether.blocks;

import org.betterx.bclib.behaviours.interfaces.BehaviourMetal;
import org.betterx.bclib.util.LootUtil;
import org.betterx.betternether.menu.CincinnasiteAnvilMenu;
import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.item.ItemStack;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.Level;
import net.minecraft.core.BlockPos;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.network.chat.Component;

import net.minecraft.world.level.block.state.BlockBehaviour;

import com.google.common.collect.Lists;

import java.util.List;

public class BlockCincinnasiteAnvil extends AnvilBlock implements BehaviourMetal {
    public BlockCincinnasiteAnvil() {
        super(BlockBehaviour.Properties.copy(NetherBlocks.CINCINNASITE_BLOCK).noOcclusion());
    }

    @Override
    public MenuProvider getMenuProvider(BlockState state, Level level, BlockPos pos) {
        return new SimpleMenuProvider(
                (id, inventory, player) -> new CincinnasiteAnvilMenu(
                        id,
                        inventory,
                        ContainerLevelAccess.create(level, pos)
                ),
                Component.translatable("container.repair")
        );
    }

    @Override
    public List<ItemStack> getDrops(BlockState state, LootParams.Builder builder) {
        ItemStack tool = builder.getParameter(LootContextParams.TOOL);
        if (LootUtil.isCorrectTool(this, state, tool)) {
            return Lists.newArrayList(new ItemStack(this));
        } else {
            return Lists.newArrayList();
        }
    }
}
