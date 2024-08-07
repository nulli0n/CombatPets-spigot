package su.nightexpress.combatpets.api.currency;

import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.Placeholders;

public interface CurrencyHandler {

    @NotNull String getDefaultName();

    @NotNull
    default String getDefaultFormat() {
        return Placeholders.GENERIC_AMOUNT + " " + Placeholders.GENERIC_NAME;
    }

    double getBalance(@NotNull Player player);

    void give(@NotNull Player player, double amount);

    void take(@NotNull Player player, double amount);
}
