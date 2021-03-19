package com.atlasplugins.atlastemplate.apis;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import com.atlasplugins.atlastemplate.AtlasTemplate;

public enum Configs {

    CAIXAS, CATEGORIAS, ;

    private RawConfig config;

    Configs() {
        this.config = new RawConfig(AtlasTemplate.getPlugin(AtlasTemplate.class), this.name().toLowerCase() + ".yml");
    }

    /**
     * @return the config
     */

    public static void setup() {
        for (Configs cfg : Configs.values()) {
            cfg.getRawConfig().saveDefaultConfig();
        }
    }

    public RawConfig getRawConfig() {
        return config;
    }

    public FileConfiguration getConfig() {
        return config.getConfig();
    }

    public void saveConfig() {
        this.config.saveConfig();
    }

}