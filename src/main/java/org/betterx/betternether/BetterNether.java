package org.betterx.betternether;

import org.betterx.bclib.BCLib;
import org.betterx.bclib.api.v2.dataexchange.DataExchangeAPI;
import org.betterx.bclib.registry.RegistryBootstrap;
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
import net.minecraft.core.registries.Registries;

import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.RegisterEvent;

@Mod(BetterNether.MOD_ID)
public class BetterNether {
    public static final String MOD_ID = "betternether";
    public static final Logger LOGGER = new Logger(MOD_ID);
    private static boolean thinArmor = true;
    private static boolean lavafallParticles = true;
    private static float fogStart = 0.05F;
    private static float fogEnd = 0.5F;

    public BetterNether() {
        IEventBus modBus = FMLJavaModLoadingContext.get().getModEventBus();
        // Avoid eager block/item class loading here; it can trip registry freeze in datagen.
        modBus.addListener(EventPriority.HIGHEST, this::ensureStructuresLoaded);
        modBus.addListener(NetherEntities::onRegister);
        modBus.addListener(NetherParticles::onRegister);
        modBus.addListener(NetherPoiTypes::onRegister);
        modBus.addListener(NetherFeatures::onRegister);
        modBus.addListener((RegisterEvent event) -> BlockEntitiesRegistry.register(event));
        modBus.addListener(NetherTemplates::register);
        modBus.addListener(EventPriority.HIGHEST, this::ensureBlocksLoaded);
        modBus.addListener(EventPriority.HIGHEST, this::ensureItemsLoaded);
        modBus.addListener(EventPriority.LOWEST, RegistryBootstrap::register);
        modBus.addListener(this::onCommonSetup);
    }

    private void onDatagen() {
    }

    public void onInitialize() {
        LOGGER.info("=^..^=    BetterNether for 1.20    =^..^=");
        //MigrationProfile.fixCustomFolder(new File("/Users/frank/Entwicklung/BetterNether/src/main/resources/data/betternether/structures/lava"));
        initOptions();
        SoundsRegistry.ensureStaticallyLoaded();
        NetherEntities.register();
        BNWorldGenerator.onModInit();
        NetherStructures.register();
        NetherBiomes.register();
        BrewingRegistry.register();
        CommandRegistry.register();
        ObsidianBreaker.register();
        Config.save();

        BNLoot.register();
        BNCriterion.register();

        Configs.saveConfigs();
        WorldConfig.registerModCache(MOD_ID);
        DataExchangeAPI.registerMod(BetterNether.MOD_ID);
        Patcher.register();

        if (BCLib.isDatagen()) {
            onDatagen();
        }
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        onInitialize();
    }

    private void ensureBlocksLoaded(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.BLOCK)) {
            return;
        }
        try {
            Class.forName("org.betterx.betternether.registry.NetherBlocks");
        } catch (ClassNotFoundException ignored) {
        }
        NetherTags.register();
        // Ensure features/structure types are queued before BCLib's registry events fire.
        NetherStructures.ensureStaticLoad();
        NetherFeatures.register();
    }

    private void ensureStructuresLoaded(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.STRUCTURE_TYPE)) {
            return;
        }
        NetherStructures.ensureStaticLoad();
    }

    private void ensureItemsLoaded(RegisterEvent event) {
        if (!event.getRegistryKey().equals(Registries.ITEM)) {
            return;
        }
        try {
            Class.forName("org.betterx.betternether.registry.NetherItems");
        } catch (ClassNotFoundException ignored) {
        }
        // Spawn eggs must be queued during the item registry event (entities register later).
        NetherEntities.registerSpawnEggs();
        IntegrationRecipes.register();
        CreativeTabs.register();
    }

    private void initOptions() {
        thinArmor = Configs.MAIN.getBoolean("improvement", "smaller_armor_offset", true);
        lavafallParticles = Configs.MAIN.getBoolean("improvement", "lavafall_particles", true);
        float density = Configs.MAIN.getFloat("improvement", "fog_density[vanilla: 1.0]", 0.75F);
        changeFogDensity(density);
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

    public static void changeFogDensity(float density) {
        fogStart = -0.45F * density + 0.5F;
        fogEnd = -0.5F * density + 1;
    }

    public static float getFogStart() {
        return fogStart;
    }

    public static float getFogEnd() {
        return fogEnd;
    }

    public static ResourceLocation makeID(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}

