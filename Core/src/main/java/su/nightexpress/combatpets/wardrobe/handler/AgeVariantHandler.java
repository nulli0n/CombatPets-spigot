package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Ageable;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class AgeVariantHandler extends EnumVariantHandler<AgeVariantHandler.Type> {

    public AgeVariantHandler() {
        super(Type.class);
    }

    @Override
    @Nullable
    public AgeVariantHandler.Type read(@NotNull LivingEntity entity) {
        if (entity instanceof Ageable ageable) return ageable.isAdult() ? Type.ADULT : Type.BABY;

        return null;
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull Type value) {
        return Lang.AGE_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Ageable ageable)) return false;

        if (value == Type.ADULT) ageable.setAdult();
        else ageable.setBaby();

        return true;
    }

    public enum Type {
        ADULT, BABY
    }
}
