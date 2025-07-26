package su.nightexpress.combatpets.data;

import com.google.gson.reflect.TypeToken;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.data.impl.PetUser;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Function;

public class DataQueries {

    public static final Function<ResultSet, PetUser> USER_LOAD = (resultSet) -> {
        try {
            UUID uuid = UUID.fromString(resultSet.getString(DataHandler.COLUMN_USER_ID.getName()));
            String name = resultSet.getString(DataHandler.COLUMN_USER_NAME.getName());
            long dateCreated = resultSet.getLong(DataHandler.COLUMN_USER_DATE_CREATED.getName());
            long lastOnline = resultSet.getLong(DataHandler.COLUMN_USER_LAST_ONLINE.getName());
            Map<String, PetData> petData = DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_PETS.getName()), new TypeToken<Map<String, PetData>>(){}.getType());

            return new PetUser(uuid, name, dateCreated, lastOnline, new HashMap<>(petData));

            //this.plugin.info("Start loading pets for " + user.getName());
            //this.plugin.runTaskAsync(task -> this.loadPets(user));
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };

    /*public static final Function<ResultSet, Map<String, PetData>> PETS_LOADER = (resultSet) -> {
        try {
            return DataHandler.GSON.fromJson(resultSet.getString(DataHandler.COLUMN_PETS.getName()), new TypeToken<Map<String, PetData>>(){}.getType());
        }
        catch (SQLException exception) {
            exception.printStackTrace();
            return null;
        }
    };*/
}
