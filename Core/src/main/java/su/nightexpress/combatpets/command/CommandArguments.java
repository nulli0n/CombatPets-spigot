package su.nightexpress.combatpets.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.nightcore.commands.Commands;
import su.nightexpress.nightcore.commands.builder.ArgumentNodeBuilder;
import su.nightexpress.nightcore.commands.exceptions.CommandSyntaxException;
import su.nightexpress.nightcore.core.config.CoreLang;

import java.util.Optional;

public class CommandArguments {

    public static final String PLAYER = "player";
    public static final String TIER   = "tier";
    public static final String PET    = "pet";
    public static final String AMOUNT = "amount";
    public static final String NAME   = "name";
    public static final String TYPE   = "type";

    @NotNull
    public static ArgumentNodeBuilder<Tier> tierArgument(@NotNull PetsPlugin plugin) {
        return Commands.argument(CommandArguments.TIER, (context, string) -> Optional.ofNullable(plugin.getPetManager().getTier(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_TIER_ARGUMENT))
            )
            .localized(Lang.COMMAND_ARGUMENT_NAME_TIER)
            .suggestions((reader, context) -> plugin.getPetManager().getTierIds());
    }

    @NotNull
    public static ArgumentNodeBuilder<Template> templateArgument(@NotNull PetsPlugin plugin) {
        return Commands.argument(CommandArguments.PET, (context, string) -> Optional.ofNullable(plugin.getPetManager().getTemplate(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_PET_ARGUMENT))
            )
            .localized(Lang.COMMAND_ARGUMENT_NAME_PET)
            .suggestions((reader, context) -> plugin.getPetManager().getTemplateIds());
    }

    @NotNull
    public static ArgumentNodeBuilder<FoodCategory> foodCategoryArgument(@NotNull PetsPlugin plugin) {
        return Commands.argument(CommandArguments.TYPE, (context, string) -> Optional.ofNullable(plugin.getPetManager().getFoodCategory(string))
                .orElseThrow(() -> CommandSyntaxException.custom(Lang.ERROR_COMMAND_INVALID_FOOD_CATEGORY_ARGUMENT))
            )
            .localized(CoreLang.COMMAND_ARGUMENT_NAME_TYPE)
            .suggestions((reader, context) -> plugin.getPetManager().getFoodCategoryNames());
    }
}
