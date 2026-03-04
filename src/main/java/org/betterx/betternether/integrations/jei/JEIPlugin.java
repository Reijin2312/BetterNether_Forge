package org.betterx.betternether.integrations.jei;

import mezz.jei.api.IModPlugin;
import mezz.jei.api.JeiPlugin;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.registration.IRecipeCatalystRegistration;
import mezz.jei.api.registration.IRecipeCategoryRegistration;
import mezz.jei.api.registration.IRecipeRegistration;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import net.minecraft.world.item.crafting.RecipeManager;
import org.betterx.betternether.BetterNether;
import org.betterx.betternether.registry.NetherBlocks;
import org.jetbrains.annotations.NotNull;

import java.util.List;

@JeiPlugin
public class JEIPlugin implements IModPlugin {
    public static final RecipeType<AbstractCookingRecipe> FORGE_RECIPE_TYPE =
            RecipeType.create(BetterNether.MOD_ID, "forge", AbstractCookingRecipe.class);

    @Override
    public @NotNull ResourceLocation getPluginUid() {
        return BetterNether.makeID("jei_plugin");
    }

    @Override
    public void registerCategories(IRecipeCategoryRegistration registration) {
        registration.addRecipeCategories(new JEIForgeCategory(registration.getJeiHelpers().getGuiHelper()));
    }

    @Override
    public void registerRecipes(IRecipeRegistration registration) {
        RecipeManager manager = net.minecraft.client.Minecraft.getInstance().level.getRecipeManager();

        List<AbstractCookingRecipe> smeltingRecipes = (List) manager.getAllRecipesFor(net.minecraft.world.item.crafting.RecipeType.SMELTING);

        registration.addRecipes(FORGE_RECIPE_TYPE, smeltingRecipes);
    }

    @Override
    public void registerRecipeCatalysts(IRecipeCatalystRegistration registration) {
        registration.addRecipeCatalyst(new ItemStack(NetherBlocks.CINCINNASITE_FORGE), FORGE_RECIPE_TYPE);
        registration.addRecipeCatalyst(new ItemStack(NetherBlocks.CINCINNASITE_FORGE), mezz.jei.api.constants.RecipeTypes.SMELTING);
    }
}
