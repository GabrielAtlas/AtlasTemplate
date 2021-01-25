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

    CAIXAS, CATEGORIAS, FABRICADA;

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

    public class RawConfig {

        private AtlasTemplate plugin;
        private String configName;
        private File configFile;
        private FileConfiguration config;

        public RawConfig(AtlasTemplate atlasTemplate, String fileName) {
            this.plugin = atlasTemplate;
            this.configName = fileName;
            File dataFolder = atlasTemplate.getDataFolder();
            this.configFile = new File(dataFolder.toString() + File.separatorChar + this.configName);
        }

    	public void reloadConfig() {
			try {
				this.config = YamlConfiguration.loadConfiguration(new InputStreamReader(new FileInputStream(this.configFile), StandardCharsets.UTF_8));
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			}
			InputStream is = this.plugin.getResource(this.configName);
			if (is != null) {
				Reader reader = new InputStreamReader(is);
				YamlConfiguration defConfig = YamlConfiguration.loadConfiguration(reader);
				this.config.setDefaults(defConfig);
			}
		}

        public FileConfiguration getConfig() {
            if (this.config == null) {
                reloadConfig();
            }
            return this.config;
        }

        public void saveConfig() {
            if ((this.config == null) || (this.configFile == null)) {
                return;
            }
            try {
                getConfig().save(this.configFile);
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        public void saveDefaultConfig() {
            if (!this.configFile.exists()) {
                this.plugin.saveResource(this.configName, false);
            }
        }
    }

}