package su.nightexpress.combatpets.wardrobe.util;

import org.bukkit.Material;
import org.bukkit.NamespacedKey;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetAPI;

public class EntityVariant<T> {

    private final String            name;
    private final NamespacedKey     key;
    private final VariantHandler<T> handler;

    private String    displayName;
    private ItemStack icon;

    public EntityVariant(@NotNull String name, @NotNull VariantHandler<T> handler) {
        this.name = name.toLowerCase();
        this.key = new NamespacedKey(PetAPI.plugin, "accessorydata." + this.name);
        this.handler = handler;

        this.setDisplayName(this.getName());
        this.setIcon(new ItemStack(Material.PAPER));
    }

    @NotNull
    public String getName() {
        return name;
    }

    @NotNull
    public NamespacedKey getKey() {
        return this.key;
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
