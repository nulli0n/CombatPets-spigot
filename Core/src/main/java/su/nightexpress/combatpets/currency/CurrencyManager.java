package su.nightexpress.combatpets.currency;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.currency.Currency;
import su.nightexpress.combatpets.api.currency.CurrencyHandler;
import su.nightexpress.combatpets.currency.handler.VaultEconomyHandler;
import su.nightexpress.combatpets.currency.impl.CoinsEngineCurrency;
import su.nightexpress.combatpets.currency.impl.ConfigCurrency;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.integration.VaultHook;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.Plugins;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Supplier;

public class CurrencyManager extends AbstractManager<PetsPlugin> {

    private final Map<String, Currency> currencyMap;

    public CurrencyManager(@NotNull PetsPlugin plugin) {
        super(plugin);
        this.currencyMap = new HashMap<>();
    }

    @Override
    protected void onLoad() {
        if (Plugins.hasVault() && VaultHook.hasEconomy()) {
            this.registerCurrency(VaultEconomyHandler.ID, VaultEconomyHandler::new);
        }

        if (Plugins.isLoaded(HookId.COINS_ENGINE)) {
            CoinsEngineCurrency.getCurrencies().forEach(this::registerCurrency);
        }
    }

    @Override
    protected void onShutdown() {
        this.currencyMap.clear();
    }

    public boolean registerCurrency(@NotNull String id, @NotNull Supplier<CurrencyHandler> supplier) {
        FileConfig config = this.plugin.getConfig();
        ConfigCurrency currency = ConfigCurrency.read(id, supplier.get(), config, "Currency." + id);

        return this.registerCurrency(currency);
    }

    public boolean registerCurrency(@NotNull Currency currency) {
        this.currencyMap.put(currency.getId(), currency);
        this.plugin.info("Registered currency: " + currency.getId());
        return true;
    }

    public boolean hasCurrency() {
        return !this.getCurrencyMap().isEmpty();
    }

    @NotNull
    public Map<String, Currency> getCurrencyMap() {
        return this.currencyMap;
    }

    @NotNull
    public Collection<Currency> getCurrencies() {
        return this.getCurrencyMap().values();
    }

    @NotNull
    public Set<String> getCurrencyIds() {
        return this.getCurrencyMap().keySet();
    }

    @Nullable
    public Currency getCurrency(@NotNull String id) {
        return this.getCurrencyMap().get(id.toLowerCase());
    }
}
