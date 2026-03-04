package org.betterx.betternether.integrations.jei;

import mezz.jei.api.constants.VanillaTypes;
import mezz.jei.api.gui.builder.IRecipeLayoutBuilder;
import mezz.jei.api.gui.drawable.IDrawable;
import mezz.jei.api.gui.drawable.IDrawableAnimated;
import mezz.jei.api.gui.ingredient.IRecipeSlotsView;
import mezz.jei.api.helpers.IGuiHelper;
import mezz.jei.api.recipe.IFocusGroup;
import mezz.jei.api.recipe.RecipeIngredientRole;
import mezz.jei.api.recipe.RecipeType;
import mezz.jei.api.recipe.category.IRecipeCategory;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.crafting.AbstractCookingRecipe;
import org.betterx.betternether.blockentities.BlockEntityForge;
import org.betterx.betternether.registry.NetherBlocks;
import org.jetbrains.annotations.NotNull;

public class JEIForgeCategory implements IRecipeCategory<AbstractCookingRecipe> {
    private final IDrawable background;
    private final IDrawable icon;
    private final IDrawableAnimated arrow;
    private final IDrawableAnimated flame;
    private final Component title;

    public JEIForgeCategory(IGuiHelper guiHelper) {
        ResourceLocation vanillaFurnace = new ResourceLocation("minecraft", "textures/gui/container/furnace.png");

        this.background = guiHelper.createDrawable(vanillaFurnace, 55, 16, 82, 54);

        this.icon = guiHelper.createDrawableIngredient(VanillaTypes.ITEM_STACK, new ItemStack(NetherBlocks.CINCINNASITE_FORGE));
        this.title = Component.translatable("betternether.jei.forge.category");

        this.flame = guiHelper.drawableBuilder(vanillaFurnace, 176, 0, 14, 14)
                .buildAnimated(200, IDrawableAnimated.StartDirection.TOP, true);
        this.arrow = guiHelper.drawableBuilder(vanillaFurnace, 176, 14, 24, 17)
                .buildAnimated(200, IDrawableAnimated.StartDirection.LEFT, false);
    }

    @Override
    public @NotNull RecipeType<AbstractCookingRecipe> getRecipeType() {
        return JEIPlugin.FORGE_RECIPE_TYPE;
    }

    @Override
    public @NotNull Component getTitle() { return title; }

    @Override
    public @NotNull IDrawable getBackground() { return background; }

    @Override
    public @NotNull IDrawable getIcon() { return icon; }

    @Override
    public void setRecipe(IRecipeLayoutBuilder builder, AbstractCookingRecipe recipe, IFocusGroup focuses) {
        builder.addSlot(RecipeIngredientRole.INPUT, 1, 1).addIngredients(recipe.getIngredients().get(0));
        builder.addSlot(RecipeIngredientRole.OUTPUT, 61, 19).addItemStack(recipe.getResultItem(Minecraft.getInstance().level.registryAccess()));
    }

    @Override
    public void draw(AbstractCookingRecipe recipe, IRecipeSlotsView recipeSlotsView, GuiGraphics guiGraphics, double mouseX, double mouseY) {
        flame.draw(guiGraphics, 1, 20);
        arrow.draw(guiGraphics, 24, 18);

        float experience = recipe.getExperience();
        if (experience > 0) {
            Component experienceString = Component.translatable("betternether.jei.cooking.experience", experience);
            int xPos = 82 - Minecraft.getInstance().font.width(experienceString);
            guiGraphics.drawString(Minecraft.getInstance().font, experienceString, xPos, 0, 0xFF808080, false);
        }

        float cookTimeSeconds = (recipe.getCookingTime() / (float) BlockEntityForge.SPEEDUP) / 20.0F;
        String timeText = String.format("%.1fs", cookTimeSeconds);

        int timeXPos = 70 - (Minecraft.getInstance().font.width(timeText) / 2);
        guiGraphics.drawString(Minecraft.getInstance().font, timeText, timeXPos, 46, 0xFF404040, false);
    }
}
