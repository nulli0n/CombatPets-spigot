package su.nightexpress.combatpets.capture;

import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.Placeholders;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.api.pet.event.capture.PetCaptureFailureEvent;
import su.nightexpress.combatpets.api.pet.event.capture.PetCaptureSuccessEvent;
import su.nightexpress.combatpets.api.pet.event.capture.PetEscapeCaptureEvent;
import su.nightexpress.combatpets.capture.config.CaptureConfig;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.nightcore.util.random.Rnd;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

public final class CaptureTask {

    private static final int END_COUNTER     = 100;
    private static final int ADD_COUNT       = 8;

    private final PetsPlugin plugin;
    private final Player     player;
    private final LivingEntity     entity;
    private final Template         template;
    private final Tier             tier;
    private final boolean          shouldWin;

    private int     success;
    private int     failure;
    private boolean running;

    public CaptureTask(@NotNull PetsPlugin plugin,
                       @NotNull Player player,
                       @NotNull LivingEntity entity,
                       @NotNull Template template,
                       @NotNull Tier tier) {
        this.plugin = plugin;
        this.player = player;
        this.entity = entity;
        this.template = template;
        this.tier = tier;
        this.success = CaptureManager.getSavedCaptureProgress(entity);
        this.failure = 0;
        this.shouldWin = Rnd.chance(this.template.getCaptureChance());
        this.running = false;
    }

    public boolean isRunning() {
        return running;
    }

    public void start() {
        if (!this.running) {
            this.entity.setAI(false);
            this.running = true;
        }
    }

    public void tick() {
        if (this.success >= END_COUNTER || this.failure >= END_COUNTER) {
            if (this.success > this.failure) {
                this.onSuccess();
            }
            else {
                this.onFailure();
            }
            this.stop();
            return;
        }

        if (this.player == null || this.entity.isDead() || !this.entity.isValid() || this.player.getWorld() != this.entity.getWorld()) {
            this.stop();
            return;
        }

        if (this.player.getLocation().distance(this.entity.getLocation()) > CaptureConfig.CAPTURE_MAX_DISTANCE.get()) {
            Lang.CAPTURE_FAIL_DISTANCE.message().send(this.player);
            this.stop();
            return;
        }

        if (this.tryEscape()) {
            this.stop();
            return;
        }

        if (!this.entity.isLeashed()) {
            this.entity.setLeashHolder(this.player);
        }

        Location location = this.entity.getLocation();
        location.setYaw(Rnd.get(360));
        this.entity.teleport(location);

        boolean random = this.resultPredefined() ? Rnd.nextBoolean() == this.shouldWin : Rnd.chance(this.template.getCaptureChance());

        if (random) {
            this.countSuccess();
        }
        else this.countFailure();

        UniParticle.of(Particle.SMOKE).play(this.entity.getLocation(), 0.5, 0.1, 10);

        Lang.CAPTURE_PROGRESS.message().send(this.player, replacer -> replacer
            .replace(Placeholders.GENERIC_SUCCESS, String.valueOf(this.success))
            .replace(Placeholders.GENERIC_FAILURE, String.valueOf(this.failure)));
    }

    private boolean resultPredefined() {
        return !CaptureConfig.CAPTURE_SAVE_PROGRESS.get();
    }

    private void countSuccess() {
        this.success = Math.min(END_COUNTER, this.success + Rnd.get(ADD_COUNT));
        if (this.success == END_COUNTER && this.resultPredefined() && !this.shouldWin) {
            this.success -= 1;
        }
    }

    private void countFailure() {
        this.failure = Math.min(END_COUNTER, this.failure + Rnd.get(ADD_COUNT));
        if (this.failure == END_COUNTER && this.resultPredefined() && this.shouldWin) {
            this.failure -= 1;
        }
    }

    private void onSuccess() {
        ItemStack itemStack = this.template.createEgg(this.tier);
        if (Config.isWardrobeEnabled()) {
            this.plugin.getWardrobeManager().storeAccessoryData(this.entity, itemStack);
        }

        this.entity.getWorld().dropItemNaturally(this.entity.getLocation(), itemStack);
        this.entity.remove();

        UniParticle.of(Particle.CLOUD).play(this.entity.getLocation(), 0.5, 0.1, 30);
        UniParticle.of(Particle.HEART).play(this.entity.getEyeLocation(), 0.05, 1);

        PetCaptureSuccessEvent event = new PetCaptureSuccessEvent(player, entity, template, tier);
        plugin.getPluginManager().callEvent(event);

        Lang.CAPTURE_SUCCESS.message().send(this.player, replacer -> replacer
            .replace(Placeholders.TEMPLATE_DEFAULT_NAME, this.template.getDefaultName())
            .replace(Placeholders.TIER_NAME, this.tier.getName()));
    }

    private void onFailure() {
        if (CaptureConfig.CAPTURE_SAVE_PROGRESS.get()) {
            CaptureManager.saveCaptureProgress(this.entity, this.success);
        }

        UniParticle.of(Particle.LAVA).play(this.entity.getEyeLocation(), 0.1, 15);

        PetCaptureFailureEvent event = new PetCaptureFailureEvent(player, entity, template, tier);
        plugin.getPluginManager().callEvent(event);

        Lang.CAPTURE_FAIL_UNLUCK.message().send(this.player, replacer -> replacer
            .replace(Placeholders.TEMPLATE_DEFAULT_NAME, this.template.getDefaultName())
            .replace(Placeholders.TIER_NAME, this.tier.getName()));
    }

    private boolean tryEscape() {
        if (!CaptureConfig.CAPTURE_ESCAPE_ALLOWED.get()) return false;

        if (Rnd.chance(this.template.getCaptureEscapeChance())) {
            this.entity.damage(0.1D, this.player);

            CaptureManager.setEscapedFromCatch(this.entity, true);

            UniParticle.of(Particle.ANGRY_VILLAGER).play(this.entity.getEyeLocation(), 0.1, 3);

            PetEscapeCaptureEvent event = new PetEscapeCaptureEvent(player, entity, template, tier);
            plugin.getPluginManager().callEvent(event);

            Lang.CAPTURE_FAIL_ESCAPED.message().send(this.player, replacer -> replacer
                .replace(Placeholders.TEMPLATE_DEFAULT_NAME, this.template.getDefaultName())
                .replace(Placeholders.TIER_NAME, this.tier.getName()));
            return true;
        }

        return false;
    }

    public void stop() {
        if (this.running) {
            this.entity.setLeashHolder(null);
            //this.plugin.getPetNMS().setLeashedTo(this.entity, null);
            this.entity.setAI(true);
            this.running = false;
        }
    }

    @NotNull
    public LivingEntity getEntity() {
        return this.entity;
    }
}
