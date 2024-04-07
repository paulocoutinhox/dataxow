package com.dataxow.app

import com.dataxow.config.AppConfig
import kotlinx.serialization.json.Json
import java.io.File
import java.nio.file.Paths

object ConfigManager {
    private const val CONFIG_FILE_NAME = "config.json"

    private val OS: String = System.getProperty("os.name").lowercase()
    private val userConfigDir: String = System.getProperty("user.home")

    private val configDir: String by lazy {
        if (OS.contains("win")) {
            Paths.get(userConfigDir, "AppData", "Local", "DataXow").toString()
        } else {
            Paths.get(userConfigDir, ".config", "DataXow").toString()
        }
    }

    private val CONFIG_PATH = Paths.get(configDir, CONFIG_FILE_NAME).toString()

    private val json = Json {
        encodeDefaults = true
        ignoreUnknownKeys = true
    }

    fun loadConfig(): AppConfig {
        ensureConfigDirectoryExists()

        val configFile = File(CONFIG_PATH)

        if (configFile.exists()) {
            return json.decodeFromString(AppConfig.serializer(), configFile.readText())
        }

        return AppConfig()
    }

    fun saveConfig(config: AppConfig) {
        ensureConfigDirectoryExists()

        val configFile = File(CONFIG_PATH)
        val data = json.encodeToString(AppConfig.serializer(), config)
        configFile.writeText(data)
    }

    private fun ensureConfigDirectoryExists() {
        val configDirectory = File(configDir)
        if (!configDirectory.exists()) {
            configDirectory.mkdirs()
        }
    }
}
