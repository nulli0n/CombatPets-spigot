package su.nightexpress.combatpets.api.pet;

import org.bukkit.Keyed;
import org.bukkit.attribute.Attribute;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Stat extends Keyed {

    @NotNull String getId();

    @NotNull String getDisplayName();

    void setDisplayName(@NotNull String displayName);

    @Nullable Attribute getVanillaMirror();
}
