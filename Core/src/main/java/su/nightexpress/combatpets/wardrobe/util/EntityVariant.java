package su.nightexpress.combatpets.wardrobe.util;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;

public class EntityVariant<T> {

    private final String            name;
    private final VariantHandler<T> handler;

    private String    displayName;
    private ItemStack icon;

    public EntityVariant(@NotNull String name, @NotNull VariantHandler<T> handler) {
        this.name = name.toLowerCase();
        this.handler = handler;

        this.setDisplayName(this.getName());
        this.setIcon(new ItemStack(Material.PAPER));
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public VariantHandler<T> getHandler() {
        return handler;
    }

    @NotNull
    public String getDisplayName() {
        return displayName;
    }

    public void setDisplayName(@NotNull String displayName) {
        this.displayName = displayName;
    }

    @NotNull
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    public void setIcon(@NotNull ItemStack icon) {
        this.icon = new ItemStack(icon);
    }
}
