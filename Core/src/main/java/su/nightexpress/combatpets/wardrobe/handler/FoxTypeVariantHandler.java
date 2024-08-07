package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Fox;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class FoxTypeVariantHandler extends EnumVariantHandler<Fox.Type> {

    public FoxTypeVariantHandler() {
        super(Fox.Type.class);
    }

    @Override
    @Nullable
    public Fox.Type read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Fox fox)) return null;

        return fox.getFoxType();
    }

    @Override
    @NotNull
    public String getLocalized(Fox.@NotNull Type value) {
        return Lang.FOX_TYPE.getLocalized(value);
    }

    @Override

    public boolean apply(@NotNull LivingEntity entity, @Nullable Fox.Type value) {
        if (value == null) return false;
        if (!(entity instanceof Fox fox)) return false;

        fox.setFoxType(value);
        return true;
    }
}
