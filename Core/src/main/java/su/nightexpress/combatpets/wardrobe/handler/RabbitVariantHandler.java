package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Rabbit;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class RabbitVariantHandler extends EnumVariantHandler<Rabbit.Type> {

    public RabbitVariantHandler() {
        super(Rabbit.Type.class);
    }

    @Override
    @Nullable
    public Rabbit.Type read(@NotNull LivingEntity entity) {
        return entity instanceof Rabbit rabbit ? rabbit.getRabbitType() : null;
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull Rabbit.Type value) {
        return Lang.RABBIT_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Rabbit.Type value) {
        if (value != null && entity instanceof Rabbit rabbit) {
            rabbit.setRabbitType(value);
            return true;
        }
        return false;
    }
}
