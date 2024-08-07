package su.nightexpress.combatpets.pet.impl;

import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Aspect;
import su.nightexpress.nightcore.config.ConfigValue;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.util.StringUtil;
import su.nightexpress.nightcore.util.placeholder.PlaceholderMap;

import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class PetAspect implements Aspect {

    private final String         id;
    private final String         name;
    private final ItemStack      icon;
    private final Set<String>    attributes;
    private final PlaceholderMap placeholderMap;

    public PetAspect(@NotNull String id, @NotNull String name, @NotNull ItemStack icon, @NotNull Set<String> attributes) {
        this.id = id.toLowerCase();
        this.name = name;
        this.icon = icon;
        this.attributes = attributes;
        this.placeholderMap = Placeholders.forAspect(this);
    }

    @NotNull
    public static PetAspect read(@NotNull FileConfig config, @NotNull String path, @NotNull String id) {
        String name = config.getString(path + ".Name", StringUtil.capitalizeUnderscored(id));

        ItemStack icon = ConfigValue.create(path + ".Icon", new ItemStack(Material.LIME_DYE),
            "Sets aspect icon.",
            Placeholders.WIKI_ITEMS_URL
        ).read(config);

        Set<String> attributes = ConfigValue.create(path + ".Attached_Attributes",
                new HashSet<>(),
            "Sets pet's attributes affected by this aspect.",
            Placeholders.WIKI_ATTRIBUTES_URL,
            Placeholders.WIKI_ASPECTS_URL
        ).onRead(set -> set.stream().map(String::toLowerCase).collect(Collectors.toSet())).read(config);

        return new PetAspect(id, name, icon, attributes);
    }

    public void write(@NotNull FileConfig config, @NotNull String path) {
        config.set(path + ".Name", this.getName());
        config.setItem(path + ".Icon", this.getIcon());
        config.set(path + ".Attached_Attributes", this.getAttributes());
    }

    @Override
    @NotNull
    public PlaceholderMap getPlaceholders() {
        return this.placeholderMap;
    }

    @NotNull
    public String getId() {
        return this.id;
    }

    @NotNull
    public String getName() {
        return this.name;
    }

    @NotNull
    @Override
    public ItemStack getIcon() {
        return new ItemStack(this.icon);
    }

    @NotNull
    public Set<String> getAttributes() {
        return this.attributes;
    }
}
