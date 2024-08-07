package su.nightexpress.combatpets.api.pet.event.capture;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;

public abstract class PetCaptureEvent extends Event {

    protected final Player       player;
    protected final LivingEntity entity;
    protected final Template     template;
    protected final Tier         tier;

    public PetCaptureEvent(@NotNull Player player, @NotNull LivingEntity entity, @NotNull Template template, @NotNull Tier tier) {
        this.player = player;
        this.entity = entity;
        this.template = template;
        this.tier = tier;
    }

    @NotNull
    public Player getPlayer() {
        return player;
    }

    @NotNull
    public LivingEntity getEntity() {
        return entity;
    }

    @NotNull
    public Template getTemplate() {
        return template;
    }

    @NotNull
    public Tier getTier() {
        return tier;
    }
}
