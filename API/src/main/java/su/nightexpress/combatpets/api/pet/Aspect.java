package su.nightexpress.combatpets.api.pet;

import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.nightcore.util.placeholder.Placeholder;

import java.util.Set;

public interface Aspect extends Placeholder {

    @NotNull String getId();

    @NotNull String getName();

    @NotNull ItemStack getIcon();

    @NotNull Set<String> getAttributes();
}
