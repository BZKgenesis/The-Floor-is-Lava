package net.bzkgns.theFloorIsLava.config.items;

import net.bzkgns.theFloorIsLava.config.ConfigKey;
import net.bzkgns.theFloorIsLava.config.ConfigSection;

import java.util.List;

public class ItemsConfig implements ConfigSection<ItemsConfig> {
    private int fireballCooldown = 20;
    private double fireballPower = 2.0f;
    private double fireballSpeed = 1.0f;
    private double fireballDamageReduction = 0.25f;
    private boolean fireballPlaceFire = false;

    private double tntKnockbackEnchantmentMultiplier = 0.2f;
    private double tntKnockbackMultiplier = 1.2f;
    private double tntDamageReduction = 0.25f;
    private double tntRaycastDistance = 4.5f;
    private int tntImmuneDelayTick = 5;
    private double tntSpawnYVelocity = 0.5f;
    private double tntPower = 4.0f;

    private int parachuteCooldown = 60;
    private int parachuteEffectDuration = 60;

    private int HealCampMaxAliveTicks = 20 * 60 * 5; // 5 minutes in ticks
    private int HealCampApplicationDelay = 20 * 5; // 5 seconds in ticks

    private double throwableIronGolemDamagePerTick = 0.1f;
    private int throwableIronGolemMaxDistance = 50;
    private double throwableIronGolemAttackDistance = 2.0f;
    private int throwableIronGolemAttackCooldown = 20;

    private int teamInventoryRowCount = 3;

    private int snowBallPlateFillRadius = 4;

    private int batteKnockbackLevel = 3;

    private int shearsEfficiencyLevel = 3;

    private double featherFallingBootsGravity = -0.5f;
    private double featherFallingBootsJumpStrength = 0.3f;
    private double featherFallingBootsSafeFallDistance = 5.0f;
    private int featherFallingBootsEnchantmentLevel = 3;


    private static final List<ConfigKey<ItemsConfig, ?>> KEYS = List.of(
            ItemsConfigKeys.FIREBALL_COOLDOWN,
            ItemsConfigKeys.FIREBALL_POWER,
            ItemsConfigKeys.FIREBALL_SPEED,
            ItemsConfigKeys.FIREBALL_DAMAGE_REDUCTION,
            ItemsConfigKeys.FIREBALL_PLACE_FIRE,

            ItemsConfigKeys.TNT_KNOCKBACK_ENCHANTMENT_MULTIPLIER,
            ItemsConfigKeys.TNT_KNOCKBACK_MULTIPLIER,
            ItemsConfigKeys.TNT_DAMAGE_REDUCTION,
            ItemsConfigKeys.TNT_RAYCAST_DISTANCE,
            ItemsConfigKeys.TNT_IMMUNE_DELAY_TICK,
            ItemsConfigKeys.TNT_SPAWN_Y_VELOCITY,
            ItemsConfigKeys.TNT_POWER,

            ItemsConfigKeys.PARACHUTE_COOLDOWN,
            ItemsConfigKeys.PARACHUTE_EFFECT_DURATION,

            ItemsConfigKeys.HEAL_CAMP_MAX_ALIVE_TICKS,
            ItemsConfigKeys.HEAL_CAMP_APPLICATION_DELAY,

            ItemsConfigKeys.THROWABLE_IRON_GOLEM_DAMAGE_PER_TICK,
            ItemsConfigKeys.THROWABLE_IRON_GOLEM_MAX_DISTANCE,
            ItemsConfigKeys.THROWABLE_IRON_GOLEM_ATTACK_DISTANCE,
            ItemsConfigKeys.THROWABLE_IRON_GOLEM_ATTACK_COOLDOWN,

            ItemsConfigKeys.TEAM_INVENTORY_ROW_COUNT,

            ItemsConfigKeys.SNOW_BALL_PLATE_FILL_RADIUS,

            ItemsConfigKeys.BATTE_KNOCKBACK_LEVEL,

            ItemsConfigKeys.SHEARS_EFFICIENCY_LEVEL,

            ItemsConfigKeys.FEATHER_FALLING_BOOTS_GRAVITY,
            ItemsConfigKeys.FEATHER_FALLING_BOOTS_JUMP_STRENGTH,
            ItemsConfigKeys.FEATHER_FALLING_BOOTS_SAFE_FALL_DISTANCE,
            ItemsConfigKeys.FEATHER_FALLING_BOOTS_ENCHANTMENT_LEVEL
    );

    public int getFireballCooldown() { return fireballCooldown; }
    public void setFireballCooldown(int v) { this.fireballCooldown = v; }

    public double getFireballPower() { return fireballPower; }
    public void setFireballPower(double v) { this.fireballPower = v; }

    public double getFireballSpeed() { return fireballSpeed; }
    public void setFireballSpeed(double v) { this.fireballSpeed = v; }

    public double getFireballDamageReduction() { return fireballDamageReduction; }
    public void setFireballDamageReduction(double v) { this.fireballDamageReduction = v; }

    public boolean isFireballPlaceFire() { return fireballPlaceFire; }
    public void setFireballPlaceFire(boolean v) { this.fireballPlaceFire = v; }

    public double getTntKnockbackEnchantmentMultiplier() { return tntKnockbackEnchantmentMultiplier; }
    public void setTntKnockbackEnchantmentMultiplier(double v) { this.tntKnockbackEnchantmentMultiplier = v; }

    public double getTntKnockbackMultiplier() { return tntKnockbackMultiplier; }
    public void setTntKnockbackMultiplier(double v) { this.tntKnockbackMultiplier = v; }

    public double getTntDamageReduction() { return tntDamageReduction; }
    public void setTntDamageReduction(double v) { this.tntDamageReduction = v; }

    public double getTntRaycastDistance() { return tntRaycastDistance; }
    public void setTntRaycastDistance(double v) { this.tntRaycastDistance = v; }

    public int getTntImmuneDelayTick() { return tntImmuneDelayTick; }
    public void setTntImmuneDelayTick(int v) { this.tntImmuneDelayTick = v; }

    public double getTntSpawnYVelocity() { return tntSpawnYVelocity; }
    public void setTntSpawnYVelocity(double v) { this.tntSpawnYVelocity = v; }

    public double getTntPower() { return tntPower; }
    public void setTntPower(double v) { this.tntPower = v; }

    public int getParachuteCooldown() { return parachuteCooldown; }
    public void setParachuteCooldown(int v) { this.parachuteCooldown = v; }

    public int getParachuteEffectDuration() { return parachuteEffectDuration; }
    public void setParachuteEffectDuration(int v) { this.parachuteEffectDuration = v; }

    public int getHealCampMaxAliveTicks() { return HealCampMaxAliveTicks; }
    public void setHealCampMaxAliveTicks(int v) { this.HealCampMaxAliveTicks = v; }

    public int getHealCampApplicationDelay() { return HealCampApplicationDelay; }
    public void setHealCampApplicationDelay(int v) { this.HealCampApplicationDelay = v; }

    public double getThrowableIronGolemDamagePerTick() { return throwableIronGolemDamagePerTick; }
    public void setThrowableIronGolemDamagePerTick(double v) { this.throwableIronGolemDamagePerTick = v; }

    public int getThrowableIronGolemMaxDistance() { return throwableIronGolemMaxDistance; }
    public void setThrowableIronGolemMaxDistance(int v) { this.throwableIronGolemMaxDistance = v; }

    public double getThrowableIronGolemAttackDistance() { return throwableIronGolemAttackDistance; }
    public void setThrowableIronGolemAttackDistance(double v) { this.throwableIronGolemAttackDistance = v; }

    public int getThrowableIronGolemAttackCooldown() { return throwableIronGolemAttackCooldown; }
    public void setThrowableIronGolemAttackCooldown(int v) { this.throwableIronGolemAttackCooldown = v; }

    public int getTeamInventoryRowCount() { return teamInventoryRowCount; }
    public void setTeamInventoryRowCount(int v) { this.teamInventoryRowCount = v; }

    public int getSnowBallPlateFillRadius() { return snowBallPlateFillRadius; }
    public void setSnowBallPlateFillRadius(int v) { this.snowBallPlateFillRadius = v; }

    public int getBatteKnockbackLevel() { return batteKnockbackLevel; }
    public void setBatteKnockbackLevel(int v) { this.batteKnockbackLevel = v; }

    public int getShearsEfficiencyLevel() { return shearsEfficiencyLevel; }
    public void setShearsEfficiencyLevel(int v) { this.shearsEfficiencyLevel = v; }

    public double getFeatherFallingBootsGravity() { return featherFallingBootsGravity; }
    public void setFeatherFallingBootsGravity(double v) { this.featherFallingBootsGravity = v; }

    public double getFeatherFallingBootsJumpStrength() { return featherFallingBootsJumpStrength; }
    public void setFeatherFallingBootsJumpStrength(double v) { this.featherFallingBootsJumpStrength = v; }

    public double getFeatherFallingBootsSafeFallDistance() { return featherFallingBootsSafeFallDistance; }
    public void setFeatherFallingBootsSafeFallDistance(double v) { this.featherFallingBootsSafeFallDistance = v; }

    public int getFeatherFallingBootsEnchantmentLevel() { return featherFallingBootsEnchantmentLevel; }
    public void setFeatherFallingBootsEnchantmentLevel(int v) { this.featherFallingBootsEnchantmentLevel = v; }

    @Override
    public String getName() {
        return "items";
    }

    @Override
    public List<ConfigKey<ItemsConfig, ?>> getKeys() {
        return List.copyOf(KEYS);
    }
}
