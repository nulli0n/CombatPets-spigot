package su.nightexpress.combatpets.level;

import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.ActivePet;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.hook.HookId;
import su.nightexpress.combatpets.hook.impl.MythicMobsHook;
import su.nightexpress.combatpets.level.data.XPSource;
import su.nightexpress.combatpets.level.listener.LevelingListener;
import su.nightexpress.nightcore.config.FileConfig;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.BukkitThing;
import su.nightexpress.nightcore.util.NumberUtil;
import su.nightexpress.nightcore.util.PDCUtil;
import su.nightexpress.nightcore.util.Plugins;

import java.util.*;

public class LevelingManager extends AbstractManager<PetsPlugin> {

    private static final String FILE_NAME = "leveling.yml";

    private Map<String, XPSource> xpSourceMap;

    public LevelingManager(@NotNull PetsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        FileConfig config = this.getConfig();
        this.loadConfig(config);
        this.loadXPSources(config);
        config.saveChanges();

        this.addListener(new LevelingListener(this.plugin, this));

        this.plugin.info("Loaded Leveling module.");
    }

    @Override
    protected void onShutdown() {
        if (this.xpSourceMap != null) {
            this.xpSourceMap.clear();
            this.xpSourceMap = null;
        }
    }

    private void loadConfig(@NotNull FileConfig config) {
        config.initializeOptions(LevelingConfig.class);
    }

    private void loadXPSources(@NotNull FileConfig config) {
        if (!this.useCustomXPTable()) return;

        this.xpSourceMap = new HashMap<>();

        if (config.getSection("XPSources").isEmpty()) {
            XPSource.getDefaults().forEach((id, source) -> source.write(config, "XPSources." + id));
        }
        config.getSection("XPSources").forEach(id -> {
            XPSource source = XPSource.read(config, "XPSources." + id);
            this.xpSourceMap.put(id.toLowerCase(), source);
        });
        this.plugin.info("Loaded " + this.xpSourceMap.size() + " XP sources.");
    }

    @NotNull
    public FileConfig getConfig() {
        return FileConfig.loadOrExtract(this.plugin, FILE_NAME);
    }

    @NotNull
    public Set<XPSource> getXPSources() {
        return this.useCustomXPTable() ? new HashSet<>(this.xpSourceMap.values()) : Collections.emptySet();
    }

    public boolean shouldDropXP(@NotNull LivingEntity entity) {
        return PDCUtil.getBoolean(entity, Keys.levelingNoXP).isEmpty();
    }

    public void setDropXP(@NotNull LivingEntity entity, boolean value) {
        if (value) {
            PDCUtil.set(entity, Keys.levelingNoXP, true);
        }
        else PDCUtil.remove(entity, Keys.levelingNoXP);
    }

    public boolean useCustomXPTable() {
        return LevelingConfig.USE_CUSTOM_XP_TABLE.get();
    }

//    public boolean useDamageDealtCheck() {
//        return LevelingConfig.XP_BASED_ON_DAMAGE_DEALT.get();
//    }

    public boolean isDisabledWorld(@NotNull World world) {
        return this.isDisabledWorld(world.getName());
    }

    public boolean isDisabledWorld(@NotNull String name) {
        return LevelingConfig.DISABLED_WORLDS.get().contains(name);
    }

    @Nullable
    public XPSource getXPSource(@NotNull LivingEntity entity) {
        String name;
        if (Plugins.isInstalled(HookId.MYTHIC_MOBS) && MythicMobsHook.isMythicMob(entity)) {
            name = MythicMobsHook.getMobInternalName(entity);
        }
        else name = BukkitThing.toString(entity.getType());

        return this.getXPSources().stream().filter(xpSource -> xpSource.isMob(name)).findFirst().orElse(null);
    }

    public int rewardXP(@NotNull ActivePet activePet, @NotNull LivingEntity victim, double damagePercent, int naturalXP) {
        if (!activePet.canGainXP()) return 0;

        int xpReward = 0;

        if (this.useCustomXPTable()) {
            XPSource source = this.getXPSource(victim);
            if (source != null && source.checkChance()) {
                xpReward = source.getAmount().roll();
            }
        }
        else xpReward = naturalXP;

        xpReward = (int) ((double) xpReward * damagePercent);
        if (xpReward <= 0) return 0;

        activePet.addXP(xpReward);

        int finalXpReward = xpReward;
        Lang.LEVELING_XP_GAIN.message().send(activePet.getOwner(), replacer -> replacer
            .replace(Placeholders.GENERIC_AMOUNT, NumberUtil.format(finalXpReward))
            .replace(Placeholders.PET_NAME, activePet.getName())
        );

        return xpReward;
    }
}
