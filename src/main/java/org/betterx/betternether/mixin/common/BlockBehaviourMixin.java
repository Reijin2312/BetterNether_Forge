package org.betterx.betternether.mixin.common;

import org.betterx.betternether.enchantments.RubyFire;

import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Block.class)
public class BlockBehaviourMixin {


    @Inject(method = "playerDestroy", at = @At("HEAD"), cancellable = true)
    private void bn_playerDestroy(
            Level level,
            Player player,
            BlockPos blockPos,
            BlockState brokenBlock,
            BlockEntity blockEntity,
            ItemStack breakingItem,
            CallbackInfo ci
    ) {
        if (level instanceof ServerLevel server) {
            if (RubyFire.getDrops(brokenBlock, server, blockPos, player, breakingItem)) {
                player.awardStat(Stats.BLOCK_MINED.get(brokenBlock.getBlock()));
                player.causeFoodExhaustion(0.005f);
                ci.cancel();
            }
        }
    }
}
