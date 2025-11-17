package su.nightexpress.combatpets.item;

import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.PetsPlugin;
import su.nightexpress.combatpets.api.item.ItemType;
import su.nightexpress.combatpets.api.pet.Template;
import su.nightexpress.combatpets.api.pet.Tier;
import su.nightexpress.combatpets.config.Config;
import su.nightexpress.combatpets.config.Keys;
import su.nightexpress.combatpets.config.Lang;
import su.nightexpress.combatpets.data.impl.PetData;
import su.nightexpress.combatpets.util.PetUtils;
import su.nightexpress.nightcore.manager.AbstractManager;
import su.nightexpress.nightcore.util.*;
import su.nightexpress.nightcore.util.wrapper.UniParticle;

public class ItemManager extends AbstractManager<PetsPlugin> {

    public ItemManager(@NotNull PetsPlugin plugin) {
        super(plugin);
    }

    @Override
    protected void onLoad() {
        this.addListener(new ItemListener(this.plugin, this));
    }

    @Override
    protected void onShutdown() {

    }

    private void setItemType(@NotNull ItemStack itemStack, @NotNull ItemType type) {
        ItemUtil.editMeta(itemStack, meta -> setItemType(meta, type));
    }

    private void setItemType(@NotNull ItemMeta meta, @NotNull ItemType type) {
        PDCUtil.set(meta, Keys.itemType, type.name());
    }

    @Nullable
    public ItemType getItemType(@NotNull ItemStack itemStack) {
        String name = PDCUtil.getString(itemStack, Keys.itemType).orElse(null);
        return name == null ? null : StringUtil.getEnum(name, ItemType.class).orElse(null);
    }

    public boolean isItemOfType(@NotNull ItemStack itemStack, @NotNull ItemType itemType) {
        return this.getItemType(itemStack) == itemType;
    }

    @NotNull
    public ItemStack createEgg(@NotNull Template template, @NotNull Tier tier) {
        ItemStack item = PetUtils.getRawEggItem(template);
        ItemUtil.editMeta(item, meta -> {
            PDCUtil.set(meta, Keys.eggPetId, template.getId());
            PDCUtil.set(meta, Keys.eggTierId, tier.getId());
            setItemType(meta, ItemType.EGG);
        });

        ItemReplacer.create(item).readMeta()
            .replace(template.getPlaceholders())
            .replace(tier.getPlaceholders())
            .writeMeta();

        return item;
    }

    @NotNull
    public ItemStack createMysteryEgg(@NotNull Template template) {
        ItemStack item = PetUtils.getRawMysteryEgg(template);
        ItemUtil.editMeta(item, meta -> {
            PDCUtil.set(meta, Keys.eggPetId, template.getId());
            setItemType(meta, ItemType.MYSTERY_EGG);
        });
        ItemReplacer.create(item).readMeta().replace(template.getPlaceholders()).writeMeta();

        return item;
    }

    public boolean isEgg(@NotNull ItemStack itemStack) {
        return this.isItemOfType(itemStack, ItemType.EGG);
    }

    public boolean isMysteryEgg(@NotNull ItemStack itemStack) {
        return this.isItemOfType(itemStack, ItemType.MYSTERY_EGG);
    }

    @Nullable
    public Template getEggTemplate(@NotNull ItemStack item) {
        String id = PDCUtil.getString(item, Keys.eggPetId).orElse(null);
        return id == null ? null : this.plugin.getPetManager().getTemplate(id);
    }

    @Nullable
    public Tier getEggTier(@NotNull ItemStack item) {
        String tierId = PDCUtil.getString(item, Keys.eggTierId).orElse(null);
        return tierId == null ? null : this.plugin.getPetManager().getTier(tierId);
    }

    public void onItemUse(@NotNull Player player, @NotNull ItemStack itemStack, @NotNull ItemType type) {
        switch (type) {
            case EGG -> this.handleEggUse(player, itemStack);
            case MYSTERY_EGG -> this.handleMysteryEggUse(player, itemStack);
            case XP_ORB -> {}
            case REBIRTH -> {}
            default -> {}
        }
    }

    private void handleEggUse(@NotNull Player player, @NotNull ItemStack itemStack) {
        Template template = this.getEggTemplate(itemStack);
        if (template == null) return;

        Tier tier = this.getEggTier(itemStack);
        if (tier == null) return;

        PetData petData = plugin.getPetManager().tryClaimPet(player, tier, template);
        if (petData == null) return;

        if (Config.isWardrobeEnabled()) {
            petData.setWardrobe(plugin.getWardrobeManager().readAccessoryData(itemStack));
        }

        itemStack.setAmount(itemStack.getAmount() - 1);
    }

    private void handleMysteryEggUse(@NotNull Player player, @NotNull ItemStack itemStack) {
        Tier tier = this.plugin.getPetManager().getTierByWeight();

        Template template = this.getEggTemplate(itemStack);
        if (template == null) return;

        itemStack.setAmount(itemStack.getAmount() - 1);

        Players.addItem(player, this.createEgg(template, tier));

        UniParticle.of(Particle.WITCH).play(player.getLocation().add(0, 0.5, 0), 0.1, 0.5, 50);

        Lang.PET_MYSTERY_EGG_HATCH.message().send(player, replacer -> replacer
            .replace(tier.replacePlaceholders())
            .replace(template.replacePlaceholders())
        );
    }
}
