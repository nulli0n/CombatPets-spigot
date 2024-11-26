package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.Creeper;
import org.bukkit.entity.LivingEntity;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class CreeperPowerVariantHandler extends EnumVariantHandler<CreeperPowerVariantHandler.Type> {

    public CreeperPowerVariantHandler() {
        super(Type.class);
    }

    @Override
    @Nullable
    public CreeperPowerVariantHandler.Type read(@NotNull LivingEntity entity) {
        return entity instanceof Creeper creeper ? (creeper.isPowered() ? Type.POWERED : Type.DEFAULT) : null;
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull CreeperPowerVariantHandler.Type value) {
        return Lang.POWER_TYPE.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable CreeperPowerVariantHandler.Type value) {
        if (!(entity instanceof Creeper creeper)) return false;

        creeper.setPowered(value == Type.POWERED);
        return true;
    }

    public enum Type {
        DEFAULT, POWERED
    }
}
