package org.betterx.betternether.advancements;

import org.betterx.betternether.BetterNether;

import net.minecraft.advancements.CriteriaTriggers;
import net.minecraft.advancements.critereon.ContextAwarePredicate;
import net.minecraft.advancements.critereon.PlayerTrigger;

public class BNCriterion {
    public static PlayerTrigger BREW_BLUE;
    public static PlayerTrigger USED_FORGE;
    public static PlayerTrigger DISTURBED_WISP;
    public static PlayerTrigger BURNED_GLOOMSCULK_CRYSTAL;
    public static PlayerTrigger WISP_SHED_EXPERIENCE;
    public static ConvertByLightningTrigger CONVERT_BY_LIGHTNING;


    public static PlayerTrigger.TriggerInstance BREW_BLUE_TRIGGER;
    public static PlayerTrigger.TriggerInstance USED_FORGE_ANY_TRIGGER;


    public static void register() {
        BREW_BLUE = CriteriaTriggers.register(new PlayerTrigger(BetterNether.makeID("brew_blue")));
        USED_FORGE = CriteriaTriggers.register(new PlayerTrigger(BetterNether.makeID("used_forge")));
        DISTURBED_WISP = CriteriaTriggers.register(new PlayerTrigger(BetterNether.makeID("disturbed_wisp")));
        BURNED_GLOOMSCULK_CRYSTAL = CriteriaTriggers.register(new PlayerTrigger(BetterNether.makeID("burned_gloomsculk_crystal")));
        WISP_SHED_EXPERIENCE = CriteriaTriggers.register(new PlayerTrigger(BetterNether.makeID("wisp_shed_experience")));
        CONVERT_BY_LIGHTNING = CriteriaTriggers.register(new ConvertByLightningTrigger());

        USED_FORGE_ANY_TRIGGER = new PlayerTrigger.TriggerInstance(
                BNCriterion.USED_FORGE.getId(),
                ContextAwarePredicate.ANY
        );

        BREW_BLUE_TRIGGER = new PlayerTrigger.TriggerInstance(
                BNCriterion.BREW_BLUE.getId(),
                ContextAwarePredicate.ANY
        );
    }
}
