package su.nightexpress.combatpets.data.impl;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.db.AbstractUser;

import java.util.*;

public class PetUser extends AbstractUser {

    private final Map<String, PetData> petMap;

    private boolean loaded;

    @NotNull
    public static PetUser create(@NotNull UUID uuid, @NotNull String name) {
        long dateCreated = System.currentTimeMillis();
        long lastOnline = System.currentTimeMillis();
        Map<String, PetData> petMap = new HashMap<>();

        PetUser user = new PetUser(uuid, name, dateCreated, lastOnline, petMap);
        user.load(new HashMap<>());
        return user;
    }

    public PetUser(@NotNull UUID uuid,
                   @NotNull String name,
                   long dateCreated,
                   long lastOnline,
                   @NotNull Map<String, PetData> petMap
    ) {
        super(uuid, name, dateCreated, lastOnline);
        this.petMap = new HashMap<>(petMap);
        this.loaded = true;
    }

    public boolean isLoaded() {
        return this.loaded;
    }

    public void load(@NotNull Map<String, PetData> petMap) {
        this.petMap.clear();
        this.petMap.putAll(petMap);
        this.petMap.values().removeIf(Objects::isNull);
        this.petMap.values().forEach(PetData::refresh);
        this.loaded = true;
    }

    @NotNull
    public Map<String, PetData> getPets() {
        return this.petMap;
    }

    public void addPet(@NotNull PetData petData) {
        this.petMap.putIfAbsent(PetUtils.getPetKey(petData.getTier(), petData.getConfig()), petData);
        petData.refresh();
    }

    public boolean removePet(@NotNull Template config, @NotNull Tier tier) {
        return this.petMap.remove(PetUtils.getPetKey(tier, config)) != null;
    }

    public boolean hasPet(@NotNull Template config, @NotNull Tier tier) {
        return this.getPet(config, tier) != null;
    }

    @Nullable
    public PetData getPet(@NotNull Template config, @NotNull Tier tier) {
        return this.getPet(PetUtils.getPetKey(tier, config));
    }

    @NotNull
    public Collection<PetData> getPets(@NotNull Tier tier) {
        Set<PetData> pets = new HashSet<>();
        this.getPets().forEach((key, data) -> {
            if (key.endsWith(tier.getId())) {
                pets.add(data);
                data.tryRevive();
            }
        });
        return pets;
    }

    @Nullable
    public PetData getPet(@NotNull String key) {
        return this.petMap.get(key);
    }
}
