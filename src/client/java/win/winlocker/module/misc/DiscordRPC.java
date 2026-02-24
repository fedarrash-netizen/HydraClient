package win.winlocker.module.misc;

import club.minnced.discord.rpc.DiscordEventHandlers;
import club.minnced.discord.rpc.DiscordRichPresence;
import net.minecraft.client.Minecraft;
import win.winlocker.module.Module;

public class DiscordRPC extends Module {
    private static final club.minnced.discord.rpc.DiscordRPC discordRPC = club.minnced.discord.rpc.DiscordRPC.INSTANCE;
    private static final String discordID = "1472190589440233503";
    private static final DiscordRichPresence discordRichPresence = new DiscordRichPresence();
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
    
    public void stopRPC() {
        discordRPC.Discord_Shutdown();
    }
    
    public void startRPC() {
        DiscordEventHandlers eventHandlers = new DiscordEventHandlers();
        discordRPC.Discord_Initialize(discordID, eventHandlers, true, null);
        
        discordRichPresence.startTimestamp = System.currentTimeMillis() / 1000L;
        discordRichPresence.largeImageKey = "лучший визуальный мод.";
        discordRichPresence.largeImageText = "CrackBy InfinityTeam";
        
        updateThread = new Thread(() -> {
            while (true) {
                try {
                    Minecraft mc = Minecraft.getInstance();
                    
                    if (mc.player != null) {
                        discordRichPresence.details = "лучший визуальный мод.";
                    } else {
                        discordRichPresence.details = "CrackBy InfinityTeam";
                        discordRichPresence.state = "";
                    }
                    
                    discordRPC.Discord_UpdatePresence(discordRichPresence);
                    Thread.sleep(5000);
                } catch (InterruptedException ignored) {
                    break;
                } catch (Exception e) {
                    // Игнорируем ошибки, чтобы поток не прерывался
                }
            }
        });
        updateThread.start();
    }
}