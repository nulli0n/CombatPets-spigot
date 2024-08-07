package su.nightexpress.combatpets.command;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.FoodCategory;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.nightcore.command.experimental.argument.CommandArgument;
import su.nightexpress.nightcore.command.experimental.builder.ArgumentBuilder;

public class CommandArguments {

    public static final String PLAYER = "player";
    public static final String TIER   = "tier";
    public static final String PET    = "pet";
    public static final String AMOUNT = "amount";
    public static final String NAME   = "name";
    public static final String TYPE   = "type";

    @NotNull
    public static ArgumentBuilder<Tier> tierArgument(@NotNull PetsPlugin plugin) {
        return CommandArgument.builder(CommandArguments.TIER, (string, context) -> plugin.getPetManager().getTier(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_TIER)
            .customFailure(Lang.ERROR_COMMAND_INVALID_TIER_ARGUMENT)
            .withSamples(context -> plugin.getPetManager().getTierIds());
    }

    @NotNull
    public static ArgumentBuilder<Template> templateArgument(@NotNull PetsPlugin plugin) {
        return CommandArgument.builder(CommandArguments.PET, (string, context) -> plugin.getPetManager().getTemplate(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_PET)
            .customFailure(Lang.ERROR_COMMAND_INVALID_PET_ARGUMENT)
            .withSamples(context -> plugin.getPetManager().getTemplateIds());
    }

    @NotNull
    public static ArgumentBuilder<FoodCategory> foodCategoryArgument(@NotNull PetsPlugin plugin) {
        return CommandArgument.builder(CommandArguments.TYPE, (string, context) -> plugin.getPetManager().getFoodCategory(string))
            .localized(Lang.COMMAND_ARGUMENT_NAME_TYPE)
            .customFailure(Lang.ERROR_COMMAND_INVALID_FOOD_CATEGORY_ARGUMENT)
            .withSamples(context -> plugin.getPetManager().getFoodCategoryNames());
    }
}
