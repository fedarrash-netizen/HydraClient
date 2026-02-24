package win.winlocker.DropDown;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import win.winlocker.config.ConfigManager;
import win.winlocker.module.ModuleManager;

import java.awt.*;
import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ConfigGui extends Screen {
    private final Screen parent;
    private EditBox fileNameBox;
    private Button importButton;
    private Button saveButton;
    private Button configButton;
    private List<String> configFiles = new ArrayList<>();
    private int selectedConfig = 0;
    
    public ConfigGui(Screen parent) {
        super(Component.literal("Config Manager"));
        this.parent = parent;
    }
    
    @Override
    protected void init() {
        // Загружаем список конфигов
        loadConfigFiles();
        
        // Поле для имени файла
        try {
            EditBox.wrapDefaultNarrationMessage(Component.literal("Config Name"))
                .wait();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        fileNameBox.setResponder(this::onFileNameChanged);
        
        // Кнопка Import
        importButton = Button.builder(Component.literal("Import"), button -> {
            importConfig();
        }).bounds(this.width / 2 - 50, 50, 80, 20).build();
        
        // Кнопка Save
        saveButton = Button.builder(Component.literal("Save"), button -> {
            saveCurrentConfig();
        }).bounds(this.width / 2 + 50, 50, 80, 20).build();
        
        // Кнопка Config (открыть в папке)
        configButton = Button.builder(Component.literal("Config"), button -> {
            openConfigFolder();
        }).bounds(this.width / 2 - 50, 80, 80, 20).build();
        
        // Кнопки навигации по конфигам
        Button prevButton = Button.builder(Component.literal("<"), button -> {
            if (selectedConfig > 0) {
                selectedConfig--;
                fileNameBox.setValue(configFiles.get(selectedConfig));
            }
        }).bounds(20, 50, 40, 20).build();
        
        Button nextButton = Button.builder(Component.literal(">"), button -> {
            if (selectedConfig < configFiles.size() - 1) {
                selectedConfig++;
                fileNameBox.setValue(configFiles.get(selectedConfig));
            }
        }).bounds(this.width - 60, 50, 40, 20).build();
        
        // Добавляем виджеты
        this.addRenderableWidget(fileNameBox);
        this.addRenderableWidget(importButton);
        this.addRenderableWidget(saveButton);
        this.addRenderableWidget(configButton);
        this.addRenderableWidget(prevButton);
        this.addRenderableWidget(nextButton);
        
        // Кнопка закрытия
        this.addRenderableWidget(Button.builder(Component.literal("Close"), button -> {
            this.minecraft.setScreen(parent);
        }).bounds(this.width / 2 - 50, this.height - 30, 100, 20).build());
    }
    
    private void loadConfigFiles() {
        configFiles.clear();
        File configDir = new File(Minecraft.getInstance().gameDirectory, "tloader");
        if (configDir.exists()) {
            File[] files = configDir.listFiles((dir, name) -> 
                name.toLowerCase().endsWith(".json") || name.toLowerCase().endsWith(".cfg"));
            if (files != null) {
                for (File file : files) {
                    configFiles.add(file.getName());
                }
            }
        }
    }
    
    private void onFileNameChanged(String newName) {
        // Находим выбранный конфиг
        for (int i = 0; i < configFiles.size(); i++) {
            if (configFiles.get(i).equals(newName)) {
                selectedConfig = i;
                break;
            }
        }
    }
    
    private void importConfig() {
        String fileName = fileNameBox.getValue();
        if (fileName != null && !fileName.isEmpty()) {
            try {
                File configFile = new File(Minecraft.getInstance().gameDirectory, "tloader/" + fileName);
                if (configFile.exists()) {
                    ConfigManager.loadConfig();
                    if (this.minecraft.player != null) {
                        this.minecraft.player.setCustomName(Component.literal("§aConfig '" + fileName + "' imported successfully!"));
                    } else {
                        System.out.println("Config '" + fileName + "' imported successfully!");
                    }
                } else {
                    if (this.minecraft.player != null) {
                        this.minecraft.player.setCustomName(Component.literal("§cConfig '" + fileName + "' not found!"));
                    } else {
                        System.out.println("Config '" + fileName + "' not found!");
                    }
                }
            } catch (Exception e) {
                if (this.minecraft.player != null) {
                    this.minecraft.player.setCustomName(Component.literal("§cFailed to import config: " + e.getMessage()));
                } else {
                    System.out.println("Failed to import config: " + e.getMessage());
                }
            }
        }
    }
    
    private void saveCurrentConfig() {
        String fileName = fileNameBox.getValue();
        if (fileName != null && !fileName.isEmpty()) {
            try {
                // Сохраняем текущие настройки модулей в конфиг
                saveModuleSettings();
                
                // Сохраняем конфиг
                ConfigManager.set("config_name", fileName);
                ConfigManager.saveConfig();
                
                if (this.minecraft.player != null) {
                    this.minecraft.player.setCustomName(Component.literal("§aConfig '" + fileName + "' saved successfully!"));
                } else {
                    System.out.println("Config '" + fileName + "' saved successfully!");
                }
            } catch (Exception e) {
                if (this.minecraft.player != null) {
                    this.minecraft.player.setCustomName(Component.literal("§cFailed to save config: " + e.getMessage()));
                } else {
                    System.out.println("Failed to save config: " + e.getMessage());
                }
            }
        }
    }
    
    private void saveModuleSettings() {
        // Сохраняем настройки всех модулей
        for (win.winlocker.module.Module module : ModuleManager.getModules()) {
            if (module.isEnabled()) {
                ConfigManager.set(module.getName() + "_enabled", true);
            } else {
                ConfigManager.set(module.getName() + "_enabled", false);
            }
            
            // Сохраняем настройки модуля
            for (win.winlocker.DropDown.settings.Setting setting : module.getSettings()) {
                if (setting instanceof win.winlocker.DropDown.settings.BooleanSetting) {
                    ConfigManager.set(module.getName() + "_" + setting.getName(), 
                        ((win.winlocker.DropDown.settings.BooleanSetting) setting).get());
                } else if (setting instanceof win.winlocker.DropDown.settings.ModeSetting) {
                    ConfigManager.set(module.getName() + "_" + setting.getName(), 
                        ((win.winlocker.DropDown.settings.ModeSetting) setting).get());
                } else if (setting instanceof win.winlocker.DropDown.settings.SliderSetting) {
                    ConfigManager.set(module.getName() + "_" + setting.getName(), 
                        ((win.winlocker.DropDown.settings.SliderSetting) setting).get());
                }
            }
        }
    }
    
    private void openConfigFolder() {
        try {
            File configDir = new File(Minecraft.getInstance().gameDirectory, "tloader");
            if (configDir.exists()) {
                // Открываем папку с конфигами в системе
                Desktop.getDesktop().open(configDir);
            }
        } catch (Exception e) {
            if (this.minecraft.player != null) {
                this.minecraft.player.setCustomName(Component.literal("§cFailed to open config folder: " + e.getMessage()));
            } else {
                System.out.println("Failed to open config folder: " + e.getMessage());
            }
        }
    }
    
    @Override
    public void render(GuiGraphics context, int mouseX, int mouseY, float delta) {
        super.render(context, mouseX, mouseY, delta);
        
        // Заголовок
        context.drawCenteredString(this.minecraft.font, Component.literal("§6§lConfig Manager"), this.width / 2, 20, 0xFFFFFF);
        
        // Информация
        context.drawString(this.minecraft.font, "§7Files: " + configFiles.size(), 20, 40, 0xFFFFFF);
        context.drawString(this.minecraft.font, "§9Selected: §a" + (selectedConfig < configFiles.size() ? configFiles.get(selectedConfig) : "None"), 20, 55, 0xFFFFFF);
        
        // Инструкции
        context.drawString(this.minecraft.font, "§8Type config name and press Import", 20, 80, 0xAAAAAA);
        context.drawString(this.minecraft.font, "§8Press Save to save current settings", 20, 95, 0xAAAAAA);
        context.drawString(this.minecraft.font, "§8Press Config to open folder", 20, 110, 0xAAAAAA);
        context.drawString(this.minecraft.font, "§8Press < > to navigate configs", 20, 125, 0xAAAAAA);
    }
}
