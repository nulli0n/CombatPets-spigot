package su.nightexpress.combatpets.nms.mc_1_21_8;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Mob;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.api.pet.PetEntity;
import su.nightexpress.combatpets.nms.mc_1_21_8.pets.animal.*;
import su.nightexpress.combatpets.nms.mc_1_21_8.pets.brained.*;
import su.nightexpress.combatpets.nms.mc_1_21_8.pets.monster.*;
import su.nightexpress.nightcore.util.BukkitThing;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityInjector {

    private static final Map<EntityType, Creator<?>> CREATORS = new HashMap<>();

    public static void register() {
        addCreator(EntityType.ALLAY, AllayPet::new);
        addCreator(EntityType.AXOLOTL, AxolotlPet::new);
        addCreator(EntityType.ARMADILLO, ArmadilloPet::new);
        addCreator(EntityType.BREEZE, BreezePet::new);
        addCreator(EntityType.FROG, FrogPet::new);
        addCreator(EntityType.GOAT, GoatPet::new);
        addCreator(EntityType.HOGLIN, HoglinPet::new);
        addCreator(EntityType.PIGLIN, PiglinPet::new);
        addCreator(EntityType.PIGLIN_BRUTE, PiglinBrutePet::new);
        addCreator(EntityType.SNIFFER, SnifferPet::new);
        addCreator(EntityType.ZOGLIN, ZoglinPet::new);
        addCreator(EntityType.WARDEN, WardenPet::new);
        addCreator(EntityType.CREAKING, CreakingPet::new);

        addCreator(EntityType.BEE, BeePet::new);
        addCreator(EntityType.BLAZE, BlazePet::new);
        addCreator(EntityType.BOGGED, BoggedPet::new);
        addCreator(EntityType.CAT, CatPet::new);
        addCreator(EntityType.CAVE_SPIDER, CaveSpiderPet::new);
        addCreator(EntityType.CHICKEN, ChickenPet::new);
        addCreator(EntityType.COW, CowPet::new);
        addCreator(EntityType.CREEPER, CreeperPet::new);
        addCreator(EntityType.DONKEY, DonkeyPet::new);
        addCreator(EntityType.DROWNED, DrownedPet::new);
        addCreator(EntityType.ENDERMITE, EndermitePet::new);
        addCreator(EntityType.ENDERMAN, EndermanPet::new);
        addCreator(EntityType.EVOKER, EvokerPet::new);
        addCreator(EntityType.FOX, FoxPet::new);
        addCreator(EntityType.GHAST, GhastPet::new);
        addCreator(EntityType.HORSE, HorsePet::new);
        addCreator(EntityType.HUSK, HuskPet::new);
        addCreator(EntityType.IRON_GOLEM, IronGolemPet::new);
        addCreator(EntityType.ILLUSIONER, IllusionerPet::new);
        addCreator(EntityType.LLAMA, LlamaPet::new);
        addCreator(EntityType.MAGMA_CUBE, MagmaCubePet::new);
        addCreator(EntityType.MULE, MulePet::new);
        addCreator(EntityType.MOOSHROOM, MushroomCowPet::new);
        addCreator(EntityType.OCELOT, OcelotPet::new);
        addCreator(EntityType.PANDA, PandaPet::new);
        addCreator(EntityType.PHANTOM, PhantomPet::new);
        addCreator(EntityType.PIG, PigPet::new);
        addCreator(EntityType.PILLAGER, PillagerPet::new);
        addCreator(EntityType.POLAR_BEAR, PolarBearPet::new);
        addCreator(EntityType.RABBIT, RabbitPet::new);
        addCreator(EntityType.RAVAGER, RavagerPet::new);
        addCreator(EntityType.SHEEP, SheepPet::new);
        addCreator(EntityType.SILVERFISH, SilverfishPet::new);
        addCreator(EntityType.SKELETON, SkeletonPet::new);
        addCreator(EntityType.SKELETON_HORSE, SkeletonHorsePet::new);
        addCreator(EntityType.SLIME, SlimePet::new);
        addCreator(EntityType.SNOW_GOLEM, SnowGolemPet::new);
        addCreator(EntityType.SPIDER, SpiderPet::new);
        addCreator(EntityType.STRAY, StrayPet::new);
        addCreator(EntityType.TRADER_LLAMA, TraderLlamaPet::new);
        addCreator(EntityType.TURTLE, TurtlePet::new);
        addCreator(EntityType.VEX, VexPet::new);
        addCreator(EntityType.VILLAGER, VillagerPet::new);
        addCreator(EntityType.VINDICATOR, VindicatorPet::new);
        addCreator(EntityType.WANDERING_TRADER, WanderingTraderPet::new);
        addCreator(EntityType.WITCH, WitchPet::new);
        addCreator(EntityType.WITHER, WitherPet::new);
        addCreator(EntityType.WITHER_SKELETON, WitherSkeletonPet::new);
        addCreator(EntityType.WOLF, WolfPet::new);
        addCreator(EntityType.ZOMBIE, ZombiePet::new);
        addCreator(EntityType.ZOMBIE_HORSE, ZombieHorsePet::new);
        addCreator(EntityType.ZOMBIE_VILLAGER, ZombieVillagerPet::new);
        addCreator(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinPet::new);
    }

    private static <T extends Mob & PetEntity> void addCreator(@NotNull EntityType type, Creator<T> creator) {
        CREATORS.put(type, creator);
    }

    public interface Creator<T extends Mob & PetEntity> {

        @NotNull T create(@NotNull ServerLevel level);
    }

    @NotNull
    public static Set<EntityType> getTypes() {
        return new HashSet<>(CREATORS.keySet());
    }

    @NotNull
    public static Mob spawn(@NotNull EntityType type, @NotNull ServerLevel level) {
        Creator<?> creator = CREATORS.get(type);
        if (creator == null) throw new IllegalStateException("No mob creator exists for the '" + BukkitThing.getAsString(type) + "' entity type.");

        return creator.create(level);
    }
}
