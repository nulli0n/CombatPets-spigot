package su.nightexpress.combatpets.hook.impl;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.combatpets.api.pet.Stat;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.pet.AttributeRegistry;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.text.NightMessage;

public class PlaceholderHook {

    private static Expansion expansion;

    public static void setup(@NotNull PetsPlugin plugin) {
        if (expansion == null) {
            expansion = new Expansion(plugin);
            expansion.register();
        }
    }

    public static void shutdown() {
        if (expansion != null) {
            expansion.unregister();
            expansion = null;
        }
    }

    private static class Expansion extends PlaceholderExpansion {

        private final PetsPlugin plugin;

        public Expansion(@NotNull PetsPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        @NotNull
        public String getAuthor() {
            return plugin.getDescription().getAuthors().getFirst();
        }

        @Override
        @NotNull
        public String getIdentifier() {
            return plugin.getName().toLowerCase();
        }

        @Override
        @NotNull
        public String getVersion() {
            return plugin.getDescription().getVersion();
        }

        @Override
        public boolean persist() {
            return true;
        }

        @Override
        public String onPlaceholderRequest(@NotNull Player player, @NotNull String params) {
            ActivePet pet = this.plugin.getPetManager().getPlayerPet(player);
            if (pet == null) return "-";

            if (params.equalsIgnoreCase("health")) {
                return NumberUtil.format(pet.getEntity().getHealth());
            }
            if (params.equalsIgnoreCase("max_health")) {
                return NumberUtil.format(pet.getMaxHealth());
            }
            if (params.equalsIgnoreCase("xp")) {
                return NumberUtil.format(pet.getXP());
            }
            if (params.equalsIgnoreCase("level")) {
                return String.valueOf(pet.getLevel());
            }
            if (params.equalsIgnoreCase("tier_name")) {
                return NightMessage.asLegacy(pet.getTier().getName());
            }
            if (params.equalsIgnoreCase("required_xp")) {
                return NumberUtil.format(pet.getRequiredXP());
            }
            if (params.equalsIgnoreCase("name")) {
                return pet.getName();
            }
            if (params.equalsIgnoreCase("aspect_points")) {
                return NumberUtil.format(pet.getAspectPoints());
            }
            if (params.equalsIgnoreCase("combat_mode")) {
                return Lang.COMBAT_MODE.getLocalized(pet.getCombatMode());
            }
            if (params.startsWith("attribute_")) {
                String name = params.substring("attribute_".length());
                Stat stat = AttributeRegistry.getById(name);
                return stat == null ? "?" : NumberUtil.format(pet.getAttribute(stat));
            }
            if (params.startsWith("aspect_")) {
                String name = params.substring("aspect_".length());
                Aspect aspect = plugin.getPetManager().getAspect(name);
                return aspect == null ? "?" : NumberUtil.format(pet.getAspectValue(aspect));
            }

            return null;
        }

    }

}
