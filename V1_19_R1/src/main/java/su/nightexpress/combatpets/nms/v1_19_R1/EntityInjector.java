package su.nightexpress.combatpets.nms.v1_19_R1;

import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.LivingEntity;
import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.jetbrains.annotations.NotNull;
import su.nightexpress.combatpets.nms.v1_19_R1.pets.animal.*;
import su.nightexpress.combatpets.nms.v1_19_R1.pets.brained.*;
import su.nightexpress.combatpets.nms.v1_19_R1.pets.monster.*;

import java.util.HashMap;
import java.util.Map;

public class EntityInjector {

    static final Map<EntityType, Class<? extends LivingEntity>> PET_TYPES = new HashMap<>();

    public static void setup() {

        PET_TYPES.put(EntityType.BEE, BeePet.class);
        PET_TYPES.put(EntityType.BLAZE, BlazePet.class);

        PET_TYPES.put(EntityType.CAT, CatPet.class);
        PET_TYPES.put(EntityType.CAVE_SPIDER, CaveSpiderPet.class);
        PET_TYPES.put(EntityType.CHICKEN, ChickenPet.class);
        PET_TYPES.put(EntityType.COW, CowPet.class);
        PET_TYPES.put(EntityType.CREEPER, CreeperPet.class);

        PET_TYPES.put(EntityType.DONKEY, DonkeyPet.class);

        //mobs.put(EntityType.ENDER_DRAGON, EntityEnderDragon.class);
        PET_TYPES.put(EntityType.ENDERMITE, EndermitePet.class);
        PET_TYPES.put(EntityType.ENDERMAN, EndermanPet.class);
        PET_TYPES.put(EntityType.EVOKER, EvokerPet.class);

        PET_TYPES.put(EntityType.GHAST, GhastPet.class);
        PET_TYPES.put(EntityType.GOAT, GoatPet.class);

        PET_TYPES.put(EntityType.HORSE, HorsePet.class);
        PET_TYPES.put(EntityType.HOGLIN, HoglinPet.class);
        PET_TYPES.put(EntityType.HUSK, HuskPet.class);

        PET_TYPES.put(EntityType.IRON_GOLEM, IronGolemPet.class);
        PET_TYPES.put(EntityType.ILLUSIONER, IllusionerPet.class);

        PET_TYPES.put(EntityType.LLAMA, LlamaPet.class);

        PET_TYPES.put(EntityType.MAGMA_CUBE, MagmaCubePet.class);
        PET_TYPES.put(EntityType.MULE, MulePet.class);
        PET_TYPES.put(EntityType.MUSHROOM_COW, MushroomCowPet.class);

        PET_TYPES.put(EntityType.OCELOT, OcelotPet.class);

        PET_TYPES.put(EntityType.PIG, PigPet.class);
        PET_TYPES.put(EntityType.PIGLIN, PiglinPet.class);
        PET_TYPES.put(EntityType.PIGLIN_BRUTE, PiglinBrutePet.class);
        PET_TYPES.put(EntityType.POLAR_BEAR, PolarBearPet.class);

        PET_TYPES.put(EntityType.RABBIT, RabbitPet.class);

        PET_TYPES.put(EntityType.SHEEP, SheepPet.class);
        PET_TYPES.put(EntityType.SILVERFISH, SilverfishPet.class);
        PET_TYPES.put(EntityType.SKELETON, SkeletonPet.class);
        PET_TYPES.put(EntityType.SKELETON_HORSE, SkeletonHorsePet.class);
        PET_TYPES.put(EntityType.SLIME, SlimePet.class);
        PET_TYPES.put(EntityType.SNOWMAN, SnowGolemPet.class);
        PET_TYPES.put(EntityType.SPIDER, SpiderPet.class);
        PET_TYPES.put(EntityType.STRAY, StrayPet.class);

        PET_TYPES.put(EntityType.VEX, VexPet.class);
        PET_TYPES.put(EntityType.VILLAGER, VillagerPet.class);
        PET_TYPES.put(EntityType.VINDICATOR, VindicatorPet.class);

        PET_TYPES.put(EntityType.WITCH, WitchPet.class);
        PET_TYPES.put(EntityType.WITHER, WitherPet.class);
        PET_TYPES.put(EntityType.WITHER_SKELETON, WitherSkeletonPet.class);
        PET_TYPES.put(EntityType.WOLF, WolfPet.class);

        PET_TYPES.put(EntityType.ZOGLIN, ZoglinPet.class);
        PET_TYPES.put(EntityType.ZOMBIE, ZombiePet.class);
        PET_TYPES.put(EntityType.ZOMBIE_HORSE, ZombieHorsePet.class);
        PET_TYPES.put(EntityType.ZOMBIE_VILLAGER, ZombieVillagerPet.class);
        PET_TYPES.put(EntityType.ZOMBIFIED_PIGLIN, ZombifiedPiglinPet.class);

        // 1.13
        PET_TYPES.put(EntityType.TURTLE, TurtlePet.class);
        PET_TYPES.put(EntityType.DROWNED, DrownedPet.class);
        PET_TYPES.put(EntityType.PHANTOM, PhantomPet.class);

        // 1.14

        PET_TYPES.put(EntityType.FOX, FoxPet.class);
        PET_TYPES.put(EntityType.PANDA, PandaPet.class);
        PET_TYPES.put(EntityType.PILLAGER, PillagerPet.class);
        PET_TYPES.put(EntityType.RAVAGER, RavagerPet.class);
        PET_TYPES.put(EntityType.WANDERING_TRADER, WanderingTraderPet.class);
        PET_TYPES.put(EntityType.TRADER_LLAMA, TraderLlamaPet.class);

        // 1.19
        PET_TYPES.put(EntityType.WARDEN, WardenPet.class);
        PET_TYPES.put(EntityType.FROG, FrogPet.class);
        PET_TYPES.put(EntityType.ALLAY, AllayPet.class);
    }

    private static LivingEntity createEntity(@NotNull EntityType type, @NotNull ServerLevel world) {
        LivingEntity entity = null;
        Class<? extends LivingEntity> clazz = PET_TYPES.get(type);
        if (clazz != null) {
            try {
                entity = clazz.getConstructor(ServerLevel.class).newInstance(world);
            }
            catch (Exception ex) {
                ex.printStackTrace();
            }
        }
        return entity;
    }

    public static LivingEntity spawnEntity(EntityType type, ServerLevel world, Location location) {
        LivingEntity entity = createEntity(type, world);
        if (entity == null) return null;
    	
        /*entity.getType().spawn(world,
                null,
                null,
                null,
                new BlockPos(location.getX(), location.getY(), location.getZ()),
                null, false, false);*/
        // EnumMobSpawn.COMMAND

        //world.addFreshEntity(entity, CreatureSpawnEvent.SpawnReason.CUSTOM);
        //world.entityManager.addNewEntity(entity);
        world.addFreshEntity(entity, null);
        entity.getBukkitEntity().teleport(location);

        return entity;
    }
}
