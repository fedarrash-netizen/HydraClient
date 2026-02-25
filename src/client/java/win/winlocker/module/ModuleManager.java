package win.winlocker.module;

import win.winlocker.module.render.*;
import win.winlocker.module.movement.*;
import win.winlocker.module.combat.*;
import win.winlocker.module.misc.*;
import win.winlocker.module.misc.Friend;
import win.winlocker.render.ItemRadius;
import win.winlocker.module.render.BlockOverlay;
import win.winlocker.module.render.ChinaHat;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ModuleManager {
    public static final List<Module> modules = new ArrayList<>();

    public static void init() {
        // Combat
        modules.add(new Aura());
        modules.add(new Targeting());
        modules.add(new AutoTotem());
        modules.add(new ElytraTarget());
        modules.add(new ClickFriend());
        modules.add(new NoFriendDamage());
        modules.add(new BotAim());
        modules.add(new AntiBot());

        // Movement
        modules.add(new Flight());
        modules.add(new Speed());
        modules.add(new WaterSpeed());
        modules.add(new NoFall());
        modules.add(new NoSlow());
        modules.add(new KeepSprint());
        modules.add(new Spider());

        // Render
        modules.add(new NoRender());
        modules.add(new ParticlesModule());
        modules.add(new TargetESP());
        modules.add(new CustomFog());
        modules.add(new HitColor());
        modules.add(new ESP());
        modules.add(new NameTags());

        // Misc
        modules.add(new RemoveVisual());
        modules.add(new DiscordRPC());
        modules.add(new Ambience());
        modules.add(new GodMode());
        modules.add(new AntiAnyDesk());
        modules.add(new AutoMessage());
        modules.add(new ClickGuiSettings());
        modules.add(new Friend());
        modules.add(new CreeperFarm());
        modules.add(new NoHungry());
        modules.add(new NameProtect());
        modules.add(new Notifications());
    }

    public static List<Module> getModules() {
        return modules;
    }

    public static List<Module> getModulesByCategory(Module.Category category) {
        return modules.stream()
                .filter(m -> m.getCategory() == category)
                .collect(Collectors.toList());
    }

    public static Module getModule(Class<? extends Module> clazz) {
        return modules.stream()
                .filter(m -> m.getClass() == clazz)
                .findFirst()
                .orElse(null);
    }

    public static Module getModule(String autoAttack) {
        if (autoAttack == null) {
            return null;
        }
        return modules.stream()
                .filter(m -> m.getName().equalsIgnoreCase(autoAttack))
                .findFirst()
                .orElse(null);
    }
}
