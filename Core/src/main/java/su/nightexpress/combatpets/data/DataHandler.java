package su.nightexpress.combatpets.data;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;
import su.nightexpress.combatpets.data.serialize.PetDataSerializer;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.db.AbstractUserDataManager;
import su.nightexpress.nightcore.db.sql.column.Column;
import su.nightexpress.nightcore.db.sql.column.ColumnType;
import su.nightexpress.nightcore.db.sql.query.impl.SelectQuery;
import su.nightexpress.nightcore.db.sql.query.type.ValuedQuery;

import java.sql.ResultSet;
import java.util.List;
import java.util.function.Function;

public class DataHandler extends AbstractUserDataManager<PetsPlugin, PetUser> {

    public static final Gson GSON = new GsonBuilder().registerTypeAdapter(PetData.class, new PetDataSerializer()).create();

    static final Column COLUMN_PETS = Column.of("pets", ColumnType.STRING);

    public DataHandler(@NotNull PetsPlugin plugin) {
        super(plugin);
    }

    @Override
    @NotNull
    protected Function<ResultSet, PetUser> createUserFunction() {
        return DataQueries.USER_LOAD;
    }

    @Override
    public void onSynchronize() {
        this.plugin.getUserManager().getLoaded().forEach(this::synchronizePets);
    }

    /*public void loadPets(@NotNull PetUser user) {
        var query = new SelectQuery<>(DataQueries.PETS_LOADER).column(COLUMN_PETS).where(COLUMN_USER_ID, WhereOperator.EQUAL, user.getId().toString());
        var pets = this.selectFirst(this.tableUsers, query);
        if (pets == null) return;

        user.load(pets);

        this.plugin.info("Pets loaded for " + user.getName());
    }*/

    private void synchronizePets(@NotNull PetUser user) {
        if (user.isAutoSavePlanned() || !user.isAutoSyncReady()) return;

        PetUser fresh = this.getUser(user.getId());
        if (fresh == null) return;

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
        return builder;
    }

    @Override
    protected void addTableColumns(@NotNull List<Column> columns) {
        columns.add(COLUMN_PETS);
    }

    @Override
    protected void addUpsertQueryData(@NotNull ValuedQuery<?, PetUser> query) {
        query.setValue(COLUMN_PETS, user -> GSON.toJson(user.getPets()));
    }

    @Override
    protected void addSelectQueryData(@NotNull SelectQuery<PetUser> query) {
        query.column(COLUMN_PETS);
    }
}
