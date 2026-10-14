package org.betterx.betternether;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.dataexchange.DataExchangeAPI;
import org.betterx.bclib.api.v3.levelgen.features.FeatureConfigAPI;
import org.betterx.betternether.advancements.BNCriterion;
import org.betterx.betternether.commands.CommandRegistry;
import org.betterx.betternether.config.Config;
import org.betterx.betternether.config.Configs;
import org.betterx.betternether.enchantments.ObsidianBreaker;
import org.betterx.betternether.loot.BNLoot;
import org.betterx.betternether.recipes.IntegrationRecipes;
import org.betterx.betternether.registry.*;
import org.betterx.betternether.registry.features.configured.NetherVegetation;
import org.betterx.betternether.tab.CreativeTabs;
import org.betterx.betternether.world.BNWorldGenerator;
import org.betterx.worlds.together.util.Logger;
import org.betterx.worlds.together.world.WorldConfig;

import net.minecraft.resources.ResourceLocation;

import net.fabricmc.api.ModInitializer;

public class BetterNether implements ModInitializer {
    public static final String MOD_ID = "betternether";
    public static final Logger LOGGER = new Logger(MOD_ID);
    private static boolean thinArmor = true;
    private static boolean lavafallParticles = true;


    private void onDatagen() {
    }

    @Override
    public void onInitialize() {
        LOGGER.info("=^..^=    BetterNether for 1.20    =^..^=");
        FeatureConfigAPI.register(MOD_ID, Configs::isFeatureEnabled);
        //MigrationProfile.fixCustomFolder(new File("/Users/frank/Entwicklung/BetterNether/src/main/resources/data/betternether/structures/lava"));
        initOptions();
        SoundsRegistry.ensureStaticallyLoaded();
        NetherGameRules.ensureStaticallyLoaded();
        NetherEnchantments.ensureStaticallyLoaded();
        NetherBlocks.register();
        BlockEntitiesRegistry.register();
        NetherItems.register();
        NetherTemplates.ensureStaticallyLoaded();
        NetherEntities.register();
        BNWorldGenerator.onModInit();
        NetherPoiTypes.register();
        NetherFeatures.register();
        NetherStructures.register();
        NetherBiomes.register();
        BrewingRegistry.register();
        CommandRegistry.register();
        ObsidianBreaker.register();
        Config.save();

        IntegrationRecipes.register();
        NetherTags.register();
        BNLoot.register();
        BNCriterion.register();
        NetherVegetation.setupBonemealFeatures();

        Configs.saveConfigs();
        WorldConfig.registerModCache(MOD_ID);
        DataExchangeAPI.registerMod(BetterNether.MOD_ID);
        Patcher.register();
        CreativeTabs.register();

        if (BCLib.isDatagen()) {
            onDatagen();
        }
    }

    private void initOptions() {
        thinArmor = Configs.MAIN.getBoolean("improvement", "smaller_armor_offset", true);
        lavafallParticles = Configs.MAIN.getBoolean("improvement", "lavafall_particles", true);
    }

    public static boolean hasThinArmor() {
        return thinArmor;
    }

    public static void setThinArmor(boolean value) {
        thinArmor = value;
    }

    public static boolean hasLavafallParticles() {
        return lavafallParticles;
    }

    public static ResourceLocation makeID(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

