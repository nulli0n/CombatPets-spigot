package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.DyeColor;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Sheep;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class SheepColorVariantHandler extends EnumVariantHandler<DyeColor> {

    public SheepColorVariantHandler() {
        super(DyeColor.class);
    }

    @Override
    @Nullable
    public DyeColor read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Sheep sheep)) return null;

        return sheep.getColor();
    }

    @Override
    @NotNull
    public String getLocalized(@NotNull DyeColor value) {
        return Lang.DYE_COLOR.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable DyeColor value) {
        if (!(entity instanceof Sheep sheep)) return false;

        sheep.setColor(value);
        return true;
    }
}
