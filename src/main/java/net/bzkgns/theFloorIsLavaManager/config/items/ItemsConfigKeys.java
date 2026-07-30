package net.bzkgns.theFloorIsLavaManager.config.items;

import net.bzkgns.theFloorIsLavaManager.config.ConfigKey;

public final class ItemsConfigKeys {
    private ItemsConfigKeys() {
    }

    public static final ConfigKey<ItemsConfig, Integer> FIREBALL_COOLDOWN =
            new ConfigKey<>(
                    "fireball.cooldown",
                    "config.items.fireball.cooldown",
                    ItemsConfig::getFireballCooldown,
                    ItemsConfig::setFireballCooldown,
                    Integer::parseInt
            );
    public static final ConfigKey<ItemsConfig, Double> FIREBALL_POWER =
            new ConfigKey<>(
                    "fireball.power",
                    "config.items.fireball.power",
                    ItemsConfig::getFireballPower,
                    ItemsConfig::setFireballPower,
                    Double::parseDouble
            );
    public static final ConfigKey<ItemsConfig, Double> FIREBALL_SPEED =
            new ConfigKey<>(
                    "fireball.speed",
                    "config.items.fireball.speed",
                    ItemsConfig::getFireballSpeed,
                    ItemsConfig::setFireballSpeed,
                    Double::parseDouble
            );
    public static final ConfigKey<ItemsConfig, Double> FIREBALL_DAMAGE_REDUCTION =
            new ConfigKey<>(
                    "fireball.damage_reduction",
                    "config.items.fireball.damage_reduction",
                    ItemsConfig::getFireballDamageReduction,
                    ItemsConfig::setFireballDamageReduction,
                    Double::parseDouble
            );
    public static final ConfigKey<ItemsConfig, Boolean> FIREBALL_PLACE_FIRE =
            new ConfigKey<>(
                    "fireball.place_fire",
                    "config.items.fireball.place_fire",
                    ItemsConfig::isFireballPlaceFire,
                    ItemsConfig::setFireballPlaceFire,
                    Boolean::parseBoolean
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_KNOCKBACK_ENCHANTMENT_MULTIPLIER =
            new ConfigKey<>(
                    "tnt.knockback_enchantment_multiplier",
                    "config.items.tnt.knockback_enchantment_multiplier",
                    ItemsConfig::getTntKnockbackEnchantmentMultiplier,
                    ItemsConfig::setTntKnockbackEnchantmentMultiplier,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_KNOCKBACK_MULTIPLIER =
            new ConfigKey<>(
                    "tnt.knockback_multiplier",
                    "config.items.tnt.knockback_multiplier",
                    ItemsConfig::getTntKnockbackMultiplier,
                    ItemsConfig::setTntKnockbackMultiplier,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_DAMAGE_REDUCTION =
            new ConfigKey<>(
                    "tnt.damage_reduction",
                    "config.items.tnt.damage_reduction",
                    ItemsConfig::getTntDamageReduction,
                    ItemsConfig::setTntDamageReduction,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_RAYCAST_DISTANCE =
            new ConfigKey<>(
                    "tnt.raycast_distance",
                    "config.items.tnt.raycast_distance",
                    ItemsConfig::getTntRaycastDistance,
                    ItemsConfig::setTntRaycastDistance,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Integer> TNT_IMMUNE_DELAY_TICK =
            new ConfigKey<>(
                    "tnt.immune_delay_tick",
                    "config.items.tnt.immune_delay_tick",
                    ItemsConfig::getTntImmuneDelayTick,
                    ItemsConfig::setTntImmuneDelayTick,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_SPAWN_Y_VELOCITY =
            new ConfigKey<>(
                    "tnt.spawn_y_velocity",
                    "config.items.tnt.spawn_y_velocity",
                    ItemsConfig::getTntSpawnYVelocity,
                    ItemsConfig::setTntSpawnYVelocity,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> TNT_POWER =
            new ConfigKey<>(
                    "tnt.power",
                    "config.items.tnt.power",
                    ItemsConfig::getTntPower,
                    ItemsConfig::setTntPower,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Integer> PARACHUTE_COOLDOWN =
            new ConfigKey<>(
                    "parachute.cooldown",
                    "config.items.parachute.cooldown",
                    ItemsConfig::getParachuteCooldown,
                    ItemsConfig::setParachuteCooldown,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> PARACHUTE_EFFECT_DURATION =
            new ConfigKey<>(
                    "parachute.effect_duration",
                    "config.items.parachute.effect_duration",
                    ItemsConfig::getParachuteEffectDuration,
                    ItemsConfig::setParachuteEffectDuration,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> HEAL_CAMP_MAX_ALIVE_TICKS =
            new ConfigKey<>(
                    "heal_camp.max_alive_ticks",
                    "config.items.heal_camp.max_alive_ticks",
                    ItemsConfig::getHealCampMaxAliveTicks,
                    ItemsConfig::setHealCampMaxAliveTicks,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> HEAL_CAMP_APPLICATION_DELAY =
            new ConfigKey<>(
                    "heal_camp.application_delay",
                    "config.items.heal_camp.application_delay",
                    ItemsConfig::getHealCampApplicationDelay,
                    ItemsConfig::setHealCampApplicationDelay,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Double> THROWABLE_IRON_GOLEM_DAMAGE_PER_TICK =
            new ConfigKey<>(
                    "throwable_iron_golem.damage_per_tick",
                    "config.items.throwable_iron_golem.damage_per_tick",
                    ItemsConfig::getThrowableIronGolemDamagePerTick,
                    ItemsConfig::setThrowableIronGolemDamagePerTick,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Integer> THROWABLE_IRON_GOLEM_MAX_DISTANCE =
            new ConfigKey<>(
                    "throwable_iron_golem.max_distance",
                    "config.items.throwable_iron_golem.max_distance",
                    ItemsConfig::getThrowableIronGolemMaxDistance,
                    ItemsConfig::setThrowableIronGolemMaxDistance,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Double> THROWABLE_IRON_GOLEM_ATTACK_DISTANCE =
            new ConfigKey<>(
                    "throwable_iron_golem.attack_distance",
                    "config.items.throwable_iron_golem.attack_distance",
                    ItemsConfig::getThrowableIronGolemAttackDistance,
                    ItemsConfig::setThrowableIronGolemAttackDistance,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Integer> THROWABLE_IRON_GOLEM_ATTACK_COOLDOWN =
            new ConfigKey<>(
                    "throwable_iron_golem.attack_cooldown",
                    "config.items.throwable_iron_golem.attack_cooldown",
                    ItemsConfig::getThrowableIronGolemAttackCooldown,
                    ItemsConfig::setThrowableIronGolemAttackCooldown,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> TEAM_INVENTORY_ROW_COUNT =
            new ConfigKey<>(
                    "team_inventory.row_count",
                    "config.items.team_inventory.row_count",
                    ItemsConfig::getTeamInventoryRowCount,
                    ItemsConfig::setTeamInventoryRowCount,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> SNOW_BALL_PLATE_FILL_RADIUS =
            new ConfigKey<>(
                    "snow_ball_plate.fill_radius",
                    "config.items.snow_ball_plate.fill_radius",
                    ItemsConfig::getSnowBallPlateFillRadius,
                    ItemsConfig::setSnowBallPlateFillRadius,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> BATTE_KNOCKBACK_LEVEL =
            new ConfigKey<>(
                    "batte.knockback_level",
                    "config.items.batte.knockback_level",
                    ItemsConfig::getBatteKnockbackLevel,
                    ItemsConfig::setBatteKnockbackLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Integer> SHEARS_EFFICIENCY_LEVEL =
            new ConfigKey<>(
                    "shears.efficiency_level",
                    "config.items.shears.efficiency_level",
                    ItemsConfig::getShearsEfficiencyLevel,
                    ItemsConfig::setShearsEfficiencyLevel,
                    Integer::parseInt
            );

    public static final ConfigKey<ItemsConfig, Double> FEATHER_FALLING_BOOTS_GRAVITY =
            new ConfigKey<>(
                    "feather_falling_boots.gravity",
                    "config.items.feather_falling_boots.gravity",
                    ItemsConfig::getFeatherFallingBootsGravity,
                    ItemsConfig::setFeatherFallingBootsGravity,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> FEATHER_FALLING_BOOTS_JUMP_STRENGTH =
            new ConfigKey<>(
                    "feather_falling_boots.jump_strength",
                    "config.items.feather_falling_boots.jump_strength",
                    ItemsConfig::getFeatherFallingBootsJumpStrength,
                    ItemsConfig::setFeatherFallingBootsJumpStrength,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Double> FEATHER_FALLING_BOOTS_SAFE_FALL_DISTANCE =
            new ConfigKey<>(
                    "feather_falling_boots.safe_fall_distance",
                    "config.items.feather_falling_boots.safe_fall_distance",
                    ItemsConfig::getFeatherFallingBootsSafeFallDistance,
                    ItemsConfig::setFeatherFallingBootsSafeFallDistance,
                    Double::parseDouble
            );

    public static final ConfigKey<ItemsConfig, Integer> FEATHER_FALLING_BOOTS_ENCHANTMENT_LEVEL =
            new ConfigKey<>(
                    "feather_falling_boots.enchantment_level",
                    "config.items.feather_falling_boots.enchantment_level",
                    ItemsConfig::getFeatherFallingBootsEnchantmentLevel,
                    ItemsConfig::setFeatherFallingBootsEnchantmentLevel,
                    Integer::parseInt
            );



}
