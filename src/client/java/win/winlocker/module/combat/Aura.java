package win.winlocker.module.combat;

import net.minecraft.client.Minecraft;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.entity.player.Player;
import win.winlocker.DropDown.settings.*;
import win.winlocker.event.EventAttack;
import win.winlocker.event.EventManager;
import win.winlocker.event.EventTarget;
import win.winlocker.module.Module;
import win.winlocker.utils.friends.FriendStorage;
import win.winlocker.utils.math.RotationUtil;

import java.util.List;

public class Aura extends Module {
    // Основные настройки
    private final ModeSetting sortMode = new ModeSetting("Sort", "Closest", List.of("Closest", "Lowest HP", "Highest HP"));
    private final SliderSetting range = new SliderSetting("Range", 4.5, 1.0, 6.0);
    private final SliderSetting cps = new SliderSetting("CPS", 12.0, 1.0, 20.0);
    
    // Настройки таргета
    private final BooleanSetting targetPlayers = new BooleanSetting("Players", true);
    private final BooleanSetting targetMobs = new BooleanSetting("Mobs", false);
    private final BooleanSetting targetAnimals = new BooleanSetting("Animals", false);
    private final BooleanSetting targetInvisible = new BooleanSetting("Invisible", false);
    private final BooleanSetting targetDead = new BooleanSetting("Dead", false);
    private final BooleanSetting ignoreFriends = new BooleanSetting("Ignore Friends", true);
    private final BooleanSetting ignoreSpectator = new BooleanSetting("Ignore Spectator", true);
    
    // Настройки ротаций
    private final ModeSetting rotateMode = new ModeSetting("Rotate", "Smooth", List.of("None", "Snap", "Smooth", "Silent"));
    private final SliderSetting rotateSpeed = new SliderSetting("Rotate Speed", 0.15, 0.01, 1.0);
    private final SliderSetting fov = new SliderSetting("FOV", 180, 0, 180);
    private final BooleanSetting lockYaw = new BooleanSetting("Lock Yaw", false);
    private final BooleanSetting lockPitch = new BooleanSetting("Lock Pitch", false);
    
    // Настройки таймингов
    private final SliderSetting minCps = new SliderSetting("Min CPS", 8.0, 1.0, 20.0);
    private final SliderSetting maxCps = new SliderSetting("Max CPS", 12.0, 1.0, 20.0);
    private final SliderSetting randomDelay = new SliderSetting("Random Delay", 0, 0, 10);
    
    // Настройки атаки
    private final BooleanSetting onlyCriticals = new BooleanSetting("Only Criticals", false);
    private final BooleanSetting autoCrit = new BooleanSetting("Auto Crit", true);
    private final BooleanSetting throughWalls = new BooleanSetting("Through Walls", false);
    private final SliderSetting wallRange = new SliderSetting("Wall Range", 3.0, 1.0, 5.0);
    private final BooleanSetting checkKillTime = new BooleanSetting("Check Kill Time", false);
    private final SliderSetting killTime = new SliderSetting("Kill Time (ms)", 500, 0, 2000);
    
    // Presets для серверов
    private final ModeSetting serverPreset = new ModeSetting("Server Preset", "None", List.of(
        "None", "FunTime", "HolyWorld", "Vanilla"
    ));

    private final FriendStorage friendStorage = FriendStorage.getInstance();

    private LivingEntity target;
    private float[] lastRotations;
    private int attackDelayTicks = 0;
    private int randomDelayTicks = 0;
    private long lastAttackTime = 0;
    private boolean wasRotating = false;

    public Aura() {
        super("Aura", Category.COMBAT);
        addSetting(serverPreset);
        addSetting(sortMode);
        addSetting(range);
        addSetting(cps);
        addSetting(minCps);
        addSetting(maxCps);
        addSetting(rotateMode);
        addSetting(rotateSpeed);
        addSetting(fov);
        addSetting(targetPlayers);
        addSetting(targetMobs);
        addSetting(targetAnimals);
        addSetting(targetInvisible);
        addSetting(ignoreFriends);
        addSetting(ignoreSpectator);
        addSetting(onlyCriticals);
        addSetting(autoCrit);
        addSetting(throughWalls);
        addSetting(wallRange);
        addSetting(lockYaw);
        addSetting(lockPitch);
    }

    @Override
    public void onEnable() {
        EventManager.register(this);
        applyServerPreset();
        lastRotations = null;
        wasRotating = false;
    }

    @Override
    public void onDisable() {
        EventManager.unregister(this);
        target = null;
        attackDelayTicks = 0;
        randomDelayTicks = 0;
        
        // Возвращаем ротацию если была silent
        if ("Silent".equals(rotateMode.get()) && lastRotations != null && Minecraft.getInstance().player != null) {
            Minecraft.getInstance().player.setYRot(lastRotations[0]);
            Minecraft.getInstance().player.setXRot(lastRotations[1]);
        }
        lastRotations = null;
        wasRotating = false;
    }

    @EventTarget
    public void onAttack(EventAttack event) {
        // Отменяем ванильную атаку если мы уже атаковали в этом тике
        if (target != null && event.getTarget() == target) {
            // event.setCancelled(true); // Можно использовать для silent атаки
        }
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null || mc.gameMode == null) {
            target = null;
            return;
        }

        // Применяем пресет сервера при переключении
        applyServerPreset();

        // Обработка случайной задержки
        if (randomDelayTicks > 0) {
            randomDelayTicks--;
        }

        // Обработка задержки атаки
        if (attackDelayTicks > 0) {
            attackDelayTicks--;
            return;
        }

        // Проверка CPS
        long currentTime = System.currentTimeMillis();
        int currentCps = getCurrentCps();
        if (currentTime - lastAttackTime < 1000L / currentCps) {
            return;
        }

        // Поиск цели
        target = findTarget(mc);
        if (target == null) {
            // Возвращаем ротацию если была silent и цели нет
            if ("Silent".equals(rotateMode.get()) && wasRotating && lastRotations != null) {
                mc.player.setYRot(lastRotations[0]);
                mc.player.setXRot(lastRotations[1]);
                wasRotating = false;
            }
            return;
        }

        // Проверка видимости
        boolean hasLineOfSight = mc.player.hasLineOfSight(target);
        if (!throughWalls.get() && !hasLineOfSight) {
            return;
        }

        // Проверка дистанции
        double distance = mc.player.distanceTo(target);
        double maxRange = throughWalls.get() ? wallRange.get() : range.get();
        if (distance > maxRange) {
            return;
        }

        // Проверка FOV
        if (fov.get() < 180) {
            double angle = RotationUtil.getAngleToTarget(target);
            if (angle > fov.get()) {
                return;
            }
        }

        // Проверка критов
        if (onlyCriticals.get() && !isInCriticalState(mc)) {
            return;
        }

        // Авто криты (прыжок перед атакой)
        if (autoCrit.get() && !isInCriticalState(mc) && mc.player.onGround()) {
            mc.player.jumpFromGround();
            return; // Пропускаем тик для прыжка
        }

        // Ротация
        performRotation(target);

        // Атака
        mc.gameMode.attack(mc.player, target);
        mc.player.swing(InteractionHand.MAIN_HAND);

        // Расчет задержки
        int baseDelay = (int) Math.round(20.0 / currentCps);
        if (randomDelay.get() > 0) {
            attackDelayTicks = baseDelay + (int) (Math.random() * randomDelay.get());
        } else {
            attackDelayTicks = baseDelay;
        }

        lastAttackTime = currentTime;
    }

    /**
     * Получение текущего CPS (рандом между min и max)
     */
    private int getCurrentCps() {
        if (minCps.get() >= maxCps.get()) {
            return (int) minCps.get();
        }
        return (int) (minCps.get() + Math.random() * (maxCps.get() - minCps.get()));
    }

    /**
     * Выполнение ротации к цели
     */
    private void performRotation(LivingEntity target) {
        String mode = rotateMode.get();
        
        if ("None".equals(mode)) {
            return;
        }

        float[] rotations = RotationUtil.getRotations(target);
        float targetYaw = rotations[0];
        float targetPitch = rotations[1];

        if ("Snap".equals(mode)) {
            if (lockYaw.get()) {
                Minecraft.getInstance().player.setYRot(targetYaw);
            }
            if (lockPitch.get()) {
                Minecraft.getInstance().player.setXRot(targetPitch);
            }
            wasRotating = false;
        } else if ("Smooth".equals(mode)) {
            float speed = (float) rotateSpeed.get();
            if (lockYaw.get()) {
                RotationUtil.smoothRotate(targetYaw, Minecraft.getInstance().player.getXRot(), speed);
            }
            if (lockPitch.get()) {
                RotationUtil.smoothRotate(Minecraft.getInstance().player.getYRot(), targetPitch, speed);
            }
            wasRotating = true;
        } else if ("Silent".equals(mode)) {
            if (lastRotations == null) {
                lastRotations = new float[] {
                    Minecraft.getInstance().player.getYRot(),
                    Minecraft.getInstance().player.getXRot()
                };
            }
            if (lockYaw.get()) {
                Minecraft.getInstance().player.setYRot(targetYaw);
            }
            if (lockPitch.get()) {
                Minecraft.getInstance().player.setXRot(targetPitch);
            }
            wasRotating = true;
        }
    }

    /**
     * Поиск лучшей цели
     */
    private LivingEntity findTarget(Minecraft mc) {
        LivingEntity best = null;
        double bestDistance = Double.MAX_VALUE;
        float bestHealth = Float.MAX_VALUE;
        float worstHealth = 0;

        double maxRange = throughWalls.get() ? wallRange.get() : range.get();

        for (Entity entity : mc.level.entitiesForRendering()) {
            if (!(entity instanceof LivingEntity)) continue;

            LivingEntity living = (LivingEntity) entity;
            if (!isValidTarget(mc, living)) continue;

            double distance = mc.player.distanceTo(living);
            if (distance > maxRange) continue;

            if ("Closest".equals(sortMode.get())) {
                if (distance < bestDistance) {
                    best = living;
                    bestDistance = distance;
                }
            } else if ("Lowest HP".equals(sortMode.get())) {
                float health = living.getHealth() + living.getAbsorptionAmount();
                if (health < bestHealth) {
                    best = living;
                    bestHealth = health;
                    bestDistance = distance;
                }
            } else if ("Highest HP".equals(sortMode.get())) {
                float health = living.getHealth() + living.getAbsorptionAmount();
                if (health > worstHealth) {
                    best = living;
                    worstHealth = health;
                    bestDistance = distance;
                }
            }
        }

        return best;
    }

    /**
     * Проверка валидности цели
     */
    private boolean isValidTarget(Minecraft mc, LivingEntity living) {
        if (living == mc.player) return false;
        if (!living.isAlive()) return !targetDead.get();
        if (living.isRemoved()) return false;

        // Проверка на невидимость
        if (living.isInvisible() && !targetInvisible.get()) return false;

        // Проверка на спектатора
        if (ignoreSpectator.get() && living.isSpectator()) return false;

        // Игрок
        if (living instanceof Player) {
            if (!targetPlayers.get()) return false;
            if (ignoreFriends.get() && friendStorage.isFriend((Player) living)) return false;
            return true;
        }

        // Моб
        if (living instanceof Mob) {
            return targetMobs.get();
        }

        // Животное (проверка по классу)
        String className = living.getClass().getSimpleName().toLowerCase();
        if (className.contains("animal") || className.contains("cow") || 
            className.contains("pig") || className.contains("sheep") ||
            className.contains("chicken")) {
            return targetAnimals.get();
        }

        return false;
    }

    /**
     * Проверка состояния критического удара
     */
    private boolean isInCriticalState(Minecraft mc) {
        return !mc.player.onGround()
                && mc.player.fallDistance > 0.0F
                && !mc.player.isInWater()
                && !mc.player.isInLava()
                && !mc.player.onClimbable()
                && !mc.player.isPassenger()
                && !mc.player.hasEffect(net.minecraft.world.effect.MobEffects.BLINDNESS);
    }

    /**
     * Применение пресета сервера
     */
    private void applyServerPreset() {
        String preset = serverPreset.get();

        switch (preset) {
            case "FunTime":
                // FunTime - Polar античит
                range.set(3.5);
                cps.set(10.0);
                minCps.set(8.0);
                maxCps.set(12.0);
                rotateMode.set("Smooth");
                rotateSpeed.set(0.12);
                fov.set(180);
                throughWalls.set(false);
                onlyCriticals.set(false);
                autoCrit.set(false);
                break;

            case "HolyWorld":
                // HolyWorld - Grim 2.3.72
                range.set(3.0);
                cps.set(8.0);
                minCps.set(6.0);
                maxCps.set(10.0);
                rotateMode.set("Smooth");
                rotateSpeed.set(0.08);
                fov.set(120);
                throughWalls.set(false);
                onlyCriticals.set(true);
                autoCrit.set(true);
                break;

            case "Vanilla":
                // Ванилла / без античита
                range.set(5.0);
                cps.set(12.0);
                minCps.set(10.0);
                maxCps.set(14.0);
                rotateMode.set("Snap");
                rotateSpeed.set(1.0);
                fov.set(180);
                throughWalls.set(true);
                onlyCriticals.set(false);
                autoCrit.set(false);
                break;

            case "None":
            default:
                // Пользовательские настройки - не меняем
                break;
        }
    }

    public LivingEntity getTarget() {
        return target;
    }

    public LivingEntity getCombatTarget() {
        return target;
    }
}
