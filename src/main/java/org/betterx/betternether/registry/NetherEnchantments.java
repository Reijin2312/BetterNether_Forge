package org.betterx.betternether.registry;

import org.betterx.betternether.BetterNether;
import org.betterx.betternether.enchantments.ObsidianBreaker;
import org.betterx.betternether.enchantments.RubyFire;

import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.item.enchantment.Enchantment;

public final class NetherEnchantments {
    public static final ObsidianBreaker OBSIDIAN_BREAKER = register("obsidian_breaker", new ObsidianBreaker());
    public static final RubyFire RUBY_FIRE = register("ruby_fire", new RubyFire());

    private NetherEnchantments() {
    }

    public static void ensureStaticallyLoaded() {
        // Loading this class performs the registry registrations above.
    }

    private static <T extends Enchantment> T register(String name, T enchantment) {
        return Registry.register(BuiltInRegistries.ENCHANTMENT, BetterNether.makeID(name), enchantment);
    }
}
