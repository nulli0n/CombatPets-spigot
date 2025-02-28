package su.nightexpress.combatpets.nms.mc_1_21_3;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import su.nightexpress.combatpets.nms.mc_1_21_3.pets.animal.*;
import su.nightexpress.combatpets.nms.mc_1_21_3.pets.brained.*;
import su.nightexpress.combatpets.nms.mc_1_21_3.pets.monster.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EntityInjector {

    private static final Map<EntityType, Class<? extends LivingEntity>> GOAL_MOBS = new HashMap<>();
    private static final Map<EntityType, Class<? extends LivingEntity>> BRAIN_MOBS = new HashMap<>();

    public static void register() {
        BRAIN_MOBS.put(EntityType.ALLAY, AllayPet.class);
        BRAIN_MOBS.put(EntityType.AXOLOTL, AxolotlPet.class);
        BRAIN_MOBS.put(EntityType.ARMADILLO, ArmadilloPet.class);
        BRAIN_MOBS.put(EntityType.BREEZE, BreezePet.class);
        BRAIN_MOBS.put(EntityType.FROG, FrogPet.class);
        BRAIN_MOBS.put(EntityType.GOAT, GoatPet.class);
        BRAIN_MOBS.put(EntityType.HOGLIN, HoglinPet.class);
        BRAIN_MOBS.put(EntityType.PIGLIN, PiglinPet.class);
        BRAIN_MOBS.put(EntityType.PIGLIN_BRUTE, PiglinBrutePet.class);
        BRAIN_MOBS.put(EntityType.SNIFFER, SnifferPet.class);
        BRAIN_MOBS.put(EntityType.ZOGLIN, ZoglinPet.class);
        BRAIN_MOBS.put(EntityType.WARDEN, WardenPet.class);
        BRAIN_MOBS.put(EntityType.CREAKING, CreakingPet.class);

        GOAL_MOBS.put(EntityType.BEE, BeePet.class);
        GOAL_MOBS.put(EntityType.BLAZE, BlazePet.class);
        GOAL_MOBS.put(EntityType.BOGGED, BoggedPet.class);
        GOAL_MOBS.put(EntityType.CAT, CatPet.class);
        GOAL_MOBS.put(EntityType.CAVE_SPIDER, CaveSpiderPet.class);
        GOAL_MOBS.put(EntityType.CHICKEN, ChickenPet.class);
        GOAL_MOBS.put(EntityType.COW, CowPet.class);
        GOAL_MOBS.put(EntityType.CREEPER, CreeperPet.class);
        GOAL_MOBS.put(EntityType.DONKEY, DonkeyPet.class);
        GOAL_MOBS.put(EntityType.DROWNED, DrownedPet.class);
        GOAL_MOBS.put(EntityType.ENDERMITE, EndermitePet.class);
        GOAL_MOBS.put(EntityType.ENDERMAN, EndermanPet.class);
        GOAL_MOBS.put(EntityType.EVOKER, EvokerPet.class);
        GOAL_MOBS.put(EntityType.FOX, FoxPet.class);
        GOAL_MOBS.put(EntityType.GHAST, GhastPet.class);
        GOAL_MOBS.put(EntityType.HORSE, HorsePet.class);
        GOAL_MOBS.put(EntityType.HUSK, HuskPet.class);
        GOAL_MOBS.put(EntityType.IRON_GOLEM, IronGolemPet.class);
        GOAL_MOBS.put(EntityType.ILLUSIONER, IllusionerPet.class);
        GOAL_MOBS.put(EntityType.LLAMA, LlamaPet.class);
        GOAL_MOBS.put(EntityType.MAGMA_CUBE, MagmaCubePet.class);
        GOAL_MOBS.put(EntityType.MULE, MulePet.class);
        GOAL_MOBS.put(EntityType.MOOSHROOM, MushroomCowPet.class);
        GOAL_MOBS.put(EntityType.OCELOT, OcelotPet.class);
        //PET_TYPES.put(EntityType.PARROT, ParrotPet.class);
        GOAL_MOBS.put(EntityType.PANDA, PandaPet.class);
        GOAL_MOBS.put(EntityType.PHANTOM, PhantomPet.class);
        GOAL_MOBS.put(EntityType.PIG, PigPet.class);
        GOAL_MOBS.put(EntityType.PILLAGER, PillagerPet.class);
        GOAL_MOBS.put(EntityType.POLAR_BEAR, PolarBearPet.class);
        GOAL_MOBS.put(EntityType.RABBIT, RabbitPet.class);
        GOAL_MOBS.put(EntityType.RAVAGER, RavagerPet.class);
        GOAL_MOBS.put(EntityType.SHEEP, SheepPet.class);
        GOAL_MOBS.put(EntityType.SILVERFISH, SilverfishPet.class);
        GOAL_MOBS.put(EntityType.SKELETON, SkeletonPet.class);
        GOAL_MOBS.put(EntityType.SKELETON_HORSE, SkeletonHorsePet.class);
        GOAL_MOBS.put(EntityType.SLIME, SlimePet.class);
        GOAL_MOBS.put(EntityType.SNOW_GOLEM, SnowGolemPet.class);
        GOAL_MOBS.put(EntityType.SPIDER, SpiderPet.class);
        GOAL_MOBS.put(EntityType.STRAY, StrayPet.class);
        GOAL_MOBS.put(EntityType.TRADER_LLAMA, TraderLlamaPet.class);
        GOAL_MOBS.put(EntityType.TURTLE, TurtlePet.class);
        GOAL_MOBS.put(EntityType.VEX, VexPet.class);
        GOAL_MOBS.put(EntityType.VILLAGER, VillagerPet.class);
        GOAL_MOBS.put(EntityType.VINDICATOR, VindicatorPet.class);
        GOAL_MOBS.put(EntityType.WANDERING_TRADER, WanderingTraderPet.class);
        GOAL_MOBS.put(EntityType.WITCH, WitchPet.class);
        GOAL_MOBS.put(EntityType.WITHER, WitherPet.class);
        GOAL_MOBS.put(EntityType.WITHER_SKELETON, WitherSkeletonPet.class);
        GOAL_MOBS.put(EntityType.WOLF, WolfPet.class);
        GOAL_MOBS.put(EntityType.ZOMBIE, ZombiePet.class);
        GOAL_MOBS.put(EntityType.ZOMBIE_HORSE, ZombieHorsePet.class);
        GOAL_MOBS.put(EntityType.ZOMBIE_VILLAGER, ZombieVillagerPet.class);
        GOAL_MOBS.put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinPet.class);
    }

    @NotNull
    public static Set<EntityType> getTypes() {
        Set<EntityType> types = new HashSet<>();
        types.addAll(BRAIN_MOBS.keySet());
        types.addAll(GOAL_MOBS.keySet());
        return types;
    }

    public static boolean isBrained(@NotNull EntityType type) {
        return BRAIN_MOBS.containsKey(type);
    }

    public static boolean isGoalMob(@NotNull EntityType type) {
        return GOAL_MOBS.containsKey(type);
    }

    @Nullable
    public static LivingEntity spawn(@NotNull EntityType type, @NotNull ServerLevel world, @NotNull Location location) {
        LivingEntity entity = createEntity(type, world);
        if (entity == null) return null;

        world.addFreshEntity(entity, null);
        entity.getBukkitEntity().teleport(location);

        return entity;
    }

    @Nullable
    private static LivingEntity createEntity(@NotNull EntityType type, @NotNull ServerLevel world) {
        Class<? extends LivingEntity> clazz = GOAL_MOBS.getOrDefault(type, BRAIN_MOBS.get(type));

        if (clazz != null) {
            try {
                return clazz.getConstructor(ServerLevel.class).newInstance(world);
            }
            catch (Exception exception) {
                exception.printStackTrace();
            }
        }
        return null;
    }
}
