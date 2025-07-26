package su.nightexpress.combatpets.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.nightcore.db.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<PetsPlugin, PetUser> {

    public UserManager(@NotNull PetsPlugin plugin, @NotNull DataHandler dataHandler) {
        super(plugin, dataHandler);
    }

    @Override
    @NotNull
    public PetUser create(@NotNull UUID uuid, @NotNull String name) {
        return PetUser.create(uuid, name);
    }
}
