package su.nightexpress.combatpets.data;

import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.data.serialize.PetDataSerializer;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.database.AbstractUserDataHandler;
import su.nightexpress.nightcore.database.sql.SQLColumn;
import su.nightexpress.nightcore.database.sql.SQLCondition;
import su.nightexpress.nightcore.database.sql.SQLValue;
import su.nightexpress.nightcore.database.sql.column.ColumnType;
import su.nightexpress.nightcore.util.Lists;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataHandler<PetsPlugin, PetUser> {

    private static final SQLColumn COLUMN_PETS = SQLColumn.of("pets", ColumnType.STRING);

    private final Function<ResultSet, PetUser> userFunction;

    public DataHandler(@NotNull PetsPlugin plugin) {
        super(plugin);

        this.userFunction = (resultSet) -> {
            try {
                UUID uuid = UUID.fromString(resultSet.getString(COLUMN_USER_ID.getName()));
                String name = resultSet.getString(COLUMN_USER_NAME.getName());
                long dateCreated = resultSet.getLong(COLUMN_USER_DATE_CREATED.getName());
                long lastOnline = resultSet.getLong(COLUMN_USER_LAST_ONLINE.getName());

                PetUser user = new PetUser(plugin, uuid, name, dateCreated, lastOnline, new HashMap<>());

                this.plugin.info("Start loading pets for " + user.getName());
                this.plugin.runTaskAsync(task -> this.loadPets(user));

                return user;
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(this::synchronizePets);
    }

    public void loadPets(@NotNull PetUser user) {
        Function<ResultSet, Map<String, PetData>> function = (resultSet) -> {
            try {
                return this.gson.fromJson(resultSet.getString(COLUMN_PETS.getName()), new TypeToken<Map<String, PetData>>(){}.getType());
            }
            catch (SQLException exception) {
                exception.printStackTrace();
                return null;
            }
        };

        this.load(this.tableUsers, function, Lists.newList(COLUMN_PETS), Lists.newList(SQLCondition.equal(COLUMN_USER_ID.toValue(user.getId())))).ifPresent(user::load);

        this.plugin.info("Pets loaded for " + user.getName());
    }

    private void synchronizePets(@NotNull PetUser user) {
        if (plugin.getUserManager().isScheduledToSave(user)) return;

        PetUser fresh = this.getUser(user.getId());
        if (fresh == null) return;
        if (!user.isSyncReady()) return;

        String activeId = null;
        Player player = user.getPlayer();
        if (player != null) {
            ActivePet pet = this.plugin.getPetManager().getPlayerPet(player);
            if (pet != null) activeId = pet.getTemplate().getId();
        }

        for (PetData petData : fresh.getPets().values()) {
            if (petData.getTemplate().getId().equalsIgnoreCase(activeId)) continue;

            user.getPets().put(PetUtils.getPetKey(petData.getTier(), petData.getTemplate()), petData);
        }
    }

    @Override
    @NotNull
    protected GsonBuilder registerAdapters(@NotNull GsonBuilder builder) {
        return super.registerAdapters(builder).registerTypeAdapter(PetData.class, new PetDataSerializer());
    }

    @Override
    @NotNull
    protected List<SQLColumn> getExtraColumns() {
        return Lists.newList(COLUMN_PETS);
    }

    @Override
    @NotNull
    protected List<SQLValue> getSaveColumns(@NotNull PetUser user) {
        return Lists.newList(COLUMN_PETS.toValue(this.gson.toJson(user.getPets())));
    }

    @Override
    @NotNull
    protected Function<ResultSet, PetUser> getUserFunction() {
        return this.userFunction;
    }
}
