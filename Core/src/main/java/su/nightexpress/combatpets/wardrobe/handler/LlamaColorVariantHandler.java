package su.nightexpress.combatpets.wardrobe.handler;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Llama;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.config.Lang;

public class LlamaColorVariantHandler extends EnumVariantHandler<Llama.Color> {

    public LlamaColorVariantHandler() {
        super(Llama.Color.class);
    }

    @Override
    @Nullable
    public Llama.Color read(@NotNull LivingEntity entity) {
        if (!(entity instanceof Llama llama)) return null;

        return llama.getColor();
    }

    @Override
    @NotNull
    public String getLocalized(Llama.@NotNull Color value) {
        return Lang.LLAMA_COLOR.getLocalized(value);
    }

    @Override
    public boolean apply(@NotNull LivingEntity entity, @Nullable Llama.Color value) {
        if (value == null) return false;
        if (!(entity instanceof Llama llama)) return false;

        llama.setColor(value);
        return true;
    }
}
