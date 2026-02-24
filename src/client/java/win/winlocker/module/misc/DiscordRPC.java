package win.winlocker.module.misc;

import win.winlocker.module.Module;

public class DiscordRPC extends Module {
    private volatile boolean running;

    public DiscordRPC() {
        super("Discord RPC", Category.MISC);
    }

    @Override
    public void onEnable() {
        startRPC();
    }

    @Override
    public void onDisable() {
        stopRPC();
    }

    public synchronized void stopRPC() {
        running = false;
    }

    public synchronized void startRPC() {
        running = true;
    }
}
