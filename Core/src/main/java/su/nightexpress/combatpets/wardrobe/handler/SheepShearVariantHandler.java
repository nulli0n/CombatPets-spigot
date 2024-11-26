package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class SheepShearVariantHandler extends EnumVariantHandler<SheepShearVariantHandler.Type> {

    public SheepShearVariantHandler() {
        super(Type.class);
    }

    @Override
    @Nullable
    public SheepShearVariantHandler.Type read(@NotNull LivingEntity entity) {
        return entity instanceof Sheep sheep ? (sheep.isSheared() ? Type.SHEARED : Type.NORMAL) : null;
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull Type value) {
        return Lang.SHEEP_SHEAR.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Type value) {
        if (!(entity instanceof Sheep sheep)) return false;

        sheep.setSheared(value == Type.SHEARED);
        return true;
    }

    public enum Type {
        NORMAL, SHEARED
    }
}
