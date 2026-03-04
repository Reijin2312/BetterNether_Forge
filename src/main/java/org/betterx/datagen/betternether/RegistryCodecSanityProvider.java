package org.betterx.datagen.betternether;

import org.betterx.betternether.BetterNether;
import org.betterx.worlds.together.surfaceRules.SurfaceRuleRegistry;

import com.mojang.serialization.Codec;
import com.mojang.serialization.JsonOps;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.RegistryDataLoader;
import net.minecraft.resources.RegistryOps;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * Tries to encode every registry entry with its codec to surface the exact failing key.
 */
public class RegistryCodecSanityProvider implements DataProvider {
    private final CompletableFuture<HolderLookup.Provider> registriesFuture;
    private final NetherRegistrySupplier supplier;

    public RegistryCodecSanityProvider(
            CompletableFuture<HolderLookup.Provider> registriesFuture,
            NetherRegistrySupplier supplier
    ) {
        this.registriesFuture = registriesFuture;
        this.supplier = supplier;
    }

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return registriesFuture.thenApply(provider -> {
            Set<ResourceKey<?>> seen = new LinkedHashSet<>();

            supplier.registries().forEach(info -> {
                seen.add(info.key());
                checkRegistry(provider, info.key(), info.elementCodec());
            });

            RegistryDataLoader.WORLDGEN_REGISTRIES
                    .stream()
                    .filter(data -> seen.add(data.key()))
                    .forEach(data -> checkRegistry(provider, data.key(), data.elementCodec()));
            return null;
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void checkRegistry(HolderLookup.Provider provider, ResourceKey<? extends Registry<?>> key, Codec<?> codec) {
        provider.lookup((ResourceKey) key).ifPresent(rawLookup -> {
            HolderLookup.RegistryLookup<Object> regLookup = (HolderLookup.RegistryLookup<Object>) rawLookup;
            BetterNether.LOGGER.info("[datagen][codec-check] scanning registry {}", key.location());
            final var ops = RegistryOps.create(JsonOps.INSTANCE, provider);
            if (SurfaceRuleRegistry.SURFACE_RULES_REGISTRY.equals(key)) {
                final var holders = regLookup.listElements().toList();
                BetterNether.LOGGER.info("[datagen][surface-rules] {} entries", holders.size());
                holders.forEach(holder -> holder.unwrapKey()
                                               .map(ResourceKey::location)
                                               .ifPresent(loc -> BetterNether.LOGGER.info(
                                                       "[datagen][surface-rules] {}",
                                                       loc
                                               )));
                holders.forEach(holder -> encodeHolder(codec, ops, holder));
            } else {
                regLookup.listElements().forEach(holder -> encodeHolder(codec, ops, holder));
            }
        });
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void encodeHolder(Codec<?> codec, RegistryOps<?> ops, Holder<Object> holder) {
        final var loc = holder.unwrapKey().map(ResourceKey::location).orElse(null);
        try {
            var res = ((Codec<Object>) codec).encodeStart((RegistryOps) ops, holder.value());
            res.error().ifPresent(err ->
                    BetterNether.LOGGER.error("[datagen][codec-check] {} -> {}", loc, String.valueOf(err))
            );
        } catch (Exception e) {
            BetterNether.LOGGER.error("[datagen][codec-check] exception at {}", loc, e);
        }
    }

    @Override
    public String getName() {
        return "BetterNether Registry Codec Sanity";
    }
}
