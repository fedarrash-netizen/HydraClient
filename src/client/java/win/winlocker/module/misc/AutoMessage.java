package win.winlocker.module.misc;

import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.player.Player;
import win.winlocker.module.Module;
import win.winlocker.module.ModuleManager;

import java.util.List;
import java.util.Random;

public class AutoMessage extends Module {
    
    private final List<String> phrases = List.of(
            "- %s Ебанный бездарь, на что ты надеялся?",
            "- %s На колени перед королем",
            "- %s Настрой свою килку бездарь",
            "- %s Сьел все свои яблоки и все равно проебал",
            "- %s Забыл как хотел отсосать мне хуй?",
            "- %s Валяется в ногах перед своим богом"
    );

    private Player lastTarget = null;
    private final Random random = new Random();

    public AutoMessage() {
        super("AutoMessage", Category.MISC);
    }

    @Override
    public void onTick() {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.level == null) return;

        // Ищем Aura модуль
        Module aura = null;
        try {
            // Пытаемся найти Aura через ModuleManager
            aura = ModuleManager.getModule("Aura");
            if (aura == null) {
                aura = ModuleManager.getModule("AutoAttack");
            }
        } catch (Exception e) {
            // Если Aura не найден, просто выходим
            return;
        }

        if (aura != null && aura.isEnabled()) {
            // Проверяем есть ли цель у Aura
            Player target = getAuraTarget(aura);
            if (target != null) {
                lastTarget = target;
            }
        }

        if (lastTarget != null) {
            boolean isDead = lastTarget.isDeadOrDying() || lastTarget.getHealth() <= 0.0f ||
                           !mc.level.players().contains(lastTarget);

            if (isDead) {
                if (mc.player.distanceTo(lastTarget) < 20.0) {
                    sendDeathMessage(lastTarget.getName().getString());
                }
                lastTarget = null;
            } else if (mc.player.distanceTo(lastTarget) > 30.0) {
                lastTarget = null;
            }
        }
    }

    private Player getAuraTarget(Module aura) {
        // Пытаемся получить цель из Aura
        try {
            // Это зависит от реализации Aura в твоей базе
            // Метод может называться getTarget(), getTargetPlayer() и т.д.
            return (Player) aura.getClass().getMethod("getTarget").invoke(aura);
        } catch (Exception e) {
            // Если не удалось получить цель, возвращаем null
            return null;
        }
    }

    private void sendDeathMessage(String name) {
        Minecraft mc = Minecraft.getInstance();
        if (mc.player == null || mc.getConnection() == null) return;

        String message = phrases.get(random.nextInt(phrases.size()));
        String finalMessage = message.contains("%s") ? String.format(message, name) : message + " " + name;

        mc.player.connection.sendChat(finalMessage);
    }
}
