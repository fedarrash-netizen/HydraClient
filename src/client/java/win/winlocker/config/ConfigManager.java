package win.winlocker.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import net.minecraft.client.Minecraft;
import win.winlocker.TLoaderClient;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class ConfigManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_DIR = new File(Minecraft.getInstance().gameDirectory, "tloader");
    private static final File CONFIG_FILE = new File(CONFIG_DIR, "config.json");
    
    private static Map<String, Object> config = new HashMap<>();
    private static boolean needsSave = false;
    
    public static void loadConfig() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }
            
            if (CONFIG_FILE.exists()) {
                FileReader reader = new FileReader(CONFIG_FILE);
                JsonObject json = GSON.fromJson(reader, JsonObject.class);
                reader.close();
                
                // Загружаем настройки из JSON
                if (json != null) {
                    for (Map.Entry<String, JsonElement> entry : json.entrySet()) {
                        String key = entry.getKey();
                        JsonElement value = entry.getValue();
                        
                        if (value.isJsonPrimitive()) {
                            String strValue = value.getAsString();
                            if (strValue.equals("true") || strValue.equals("false")) {
                                config.put(key, Boolean.parseBoolean(strValue));
                            } else {
                                try {
                                    config.put(key, Double.parseDouble(strValue));
                                } catch (NumberFormatException e) {
                                    config.put(key, strValue);
                                }
                            }
                        }
                    }
                }
            }
        } catch (Exception e) {
            System.err.println("Failed to load config: " + e.getMessage());
        }
    }
    
    public static void saveConfig() {
        try {
            if (!CONFIG_DIR.exists()) {
                CONFIG_DIR.mkdirs();
            }
            
            JsonObject json = new JsonObject();
            for (Map.Entry<String, Object> entry : config.entrySet()) {
                Object value = entry.getValue();
                if (value instanceof Boolean) {
                    json.addProperty(entry.getKey(), (Boolean) value);
                } else if (value instanceof Double) {
                    json.addProperty(entry.getKey(), (Double) value);
                } else {
                    json.addProperty(entry.getKey(), value.toString());
                }
            }
            
            FileWriter writer = new FileWriter(CONFIG_FILE);
            GSON.toJson(json, writer);
            writer.close();
            
            needsSave = false;
        } catch (IOException e) {
            System.err.println("Failed to save config: " + e.getMessage());
        }
    }
    
    public static void set(String key, Object value) {
        config.put(key, value);
        needsSave = true;
    }
    
    public static Object get(String key) {
        return config.get(key);
    }
    
    public static boolean getBoolean(String key, boolean defaultValue) {
        Object value = config.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        }
        return defaultValue;
    }
    
    public static double getDouble(String key, double defaultValue) {
        Object value = config.get(key);
        if (value instanceof Double) {
            return (Double) value;
        }
        return defaultValue;
    }
    
    public static String getString(String key, String defaultValue) {
        Object value = config.get(key);
        if (value instanceof String) {
            return (String) value;
        }
        return defaultValue;
    }
    
    public static boolean needsSave() {
        return needsSave;
    }
    
    public static void markForSave() {
        needsSave = true;
    }
}
