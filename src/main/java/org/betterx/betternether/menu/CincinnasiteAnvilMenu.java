package org.betterx.betternether.menu;

import org.betterx.betternether.registry.NetherBlocks;

import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.AnvilMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;

public class CincinnasiteAnvilMenu extends AnvilMenu {
    private final ContainerLevelAccess access;

    public CincinnasiteAnvilMenu(int id, Inventory inventory, ContainerLevelAccess access) {
        super(id, inventory, access);
        this.access = access;
    }

    @Override
    public boolean stillValid(Player player) {
        return AbstractContainerMenu.stillValid(access, player, NetherBlocks.CINCINNASITE_ANVIL);
    }
}
