package su.nightexpress.combatpets.data;

import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.nightcore.database.AbstractUserManager;

import java.util.UUID;

public class UserManager extends AbstractUserManager<PetsPlugin, PetUser> {

    public UserManager(@NotNull PetsPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    public PetUser createUserData(@NotNull UUID uuid, @NotNull String name) {
        return PetUser.create(this.plugin, uuid, name);
    }
}
