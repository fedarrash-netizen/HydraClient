package win.winlocker.module.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import win.winlocker.module.Module;

public class DiscordRPC extends Module {
    private static final String DISCORD_ID = "1472190589440233503";

    private club.minnced.discord.rpc.DiscordRPC rpc;
    private DiscordRichPresence presence;
    private Thread updateThread;

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
        if (updateThread != null) {
            updateThread.interrupt();
            updateThread = null;
        }

        if (rpc != null) {
            try {
                rpc.Discord_Shutdown();
            } catch (Throwable ignored) {
            }
            rpc = null;
            presence = null;
        }
    }

    public synchronized void startRPC() {
        if (rpc != null) {
            return;
        }

        try {
            rpc = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
            presence = new DiscordRichPresence();

            DiscordEventHandlers handlers = new DiscordEventHandlers();
            rpc.Discord_Initialize(DISCORD_ID, handlers, true, null);

            presence.startTimestamp = System.currentTimeMillis() / 1000L;
            presence.largeImageKey = "best_visual_mod";
            presence.largeImageText = "CrackBy InfinityTeam";

            updateThread = new Thread(this::updateLoop, "Discord-RPC-Update");
            updateThread.setDaemon(true);
            updateThread.start();
        } catch (Throwable t) {
            System.err.println("[DiscordRPC] Failed to initialize: " + t.getMessage());
            stopRPC();
        }
    }

    private void updateLoop() {
        while (!Thread.currentThread().isInterrupted() && rpc != null && presence != null) {
            try {
                Minecraft mc = Minecraft.getInstance();
                if (mc.player != null) {
                    presence.details = "Best visual mod";
                    presence.state = "In game";
                } else {
                    presence.details = "CrackBy InfinityTeam";
                    presence.state = "Menu";
                }

                rpc.Discord_UpdatePresence(presence);
                Thread.sleep(5000L);
            } catch (InterruptedException interrupted) {
                Thread.currentThread().interrupt();
                break;
            } catch (Throwable ignored) {
                // Keep thread alive if RPC update fails intermittently.
            }
        }
    }
}
