package su.nightexpress.combatpets.shop.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.bridge.currency.Currency;

public class EggPrice {

    private final Currency currency;
    private final double   price;

    public EggPrice(@NotNull Currency currency, double price) {
        this.currency = currency;
        this.price = price;
    }

    @NotNull
    public Currency getCurrency() {
        return currency;
    }

    public double getPrice() {
        return price;
    }
}
