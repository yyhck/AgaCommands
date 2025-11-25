package com.aga.commands;

import com.aga.commands.commands.AdminCommand;
import com.aga.commands.commands.AliasCommand;
import com.aga.commands.events.CommandBlockerListener;
import com.aga.commands.events.TabBlockerListener;

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.config.Configuration;
import net.md_5.bungee.config.ConfigurationProvider;
import net.md_5.bungee.config.YamlConfiguration;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.file.Files;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class AgaCommands extends Plugin {

    private static AgaCommands instance;
    private Configuration config;
    private File configFile;
    private File logFile;

    @Override
    public void onEnable() {
        instance = this;

        // 1. Carrega configurações
        reloadPlugin(null);

        // 2. Registra os Eventos (Listeners)
        getProxy().getPluginManager().registerListener(this, new CommandBlockerListener(this));
        getProxy().getPluginManager().registerListener(this, new TabBlockerListener(this));

        // 3. Envia mensagem bonita no console
        sendConsoleLogo();
    }

    private void sendConsoleLogo() {
        String border = "&a&l========================================";
        String title  = "&f      AgaCommands &e(Proxy) &a&lATIVADO";
        String author = "&f      Autor: &a&l" + getDescription().getAuthor();
        String ver    = "&f      Versao: &a&l" + getDescription().getVersion();

        CommandSender console = getProxy().getConsole();
        console.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', border)));
        console.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', title)));
        console.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', author)));
        console.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', ver)));
        console.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', border)));
    }

    public static AgaCommands getInstance() {
        return instance;
    }

    public void reloadPlugin(CommandSender sender) {
        loadConfig();
        createLogFile();

        // Limpa comandos antigos para evitar duplicatas e registra novamente
        getProxy().getPluginManager().unregisterCommands(this);

        registerAdminCommand();
        loadAliasesFromConfig();

        if (sender != null) {
            String msg = config.getString("messages.reload", "&a[AgaCommands] Plugin recarregado!");
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));
        }
    }

    private void loadConfig() {
        if (!getDataFolder().exists()) {
            getDataFolder().mkdir();
        }

        configFile = new File(getDataFolder(), "config.yml");

        if (!configFile.exists()) {
            try (InputStream in = getResourceAsStream("config.yml")) {
                Files.copy(in, configFile.toPath());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        try {
            config = ConfigurationProvider.getProvider(YamlConfiguration.class).load(configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void createLogFile() {
        logFile = new File(getDataFolder(), "blocked_commands.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Nao foi possivel criar o arquivo de log.");
            }
        }
    }

    /**
     * Log Assíncrono para otimização
     */
    public void logBlockedCommand(String playerName, String command) {
        if (logFile == null) return;

        getProxy().getScheduler().runAsync(this, () -> {
            try (FileWriter fw = new FileWriter(logFile, true);
                 PrintWriter pw = new PrintWriter(fw)) {

                SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                String date = sdf.format(new Date());
                pw.println(playerName + ": " + command + " - " + date);

            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    public Configuration getConfig() {
        return config;
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(config, configFile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void registerAdminCommand() {
        List<String> commandNames = config.getStringList("main-command-aliases");
        if (commandNames == null || commandNames.isEmpty()) {
            getProxy().getPluginManager().registerCommand(this, new AdminCommand(this, "agacommands", null));
            return;
        }

        String mainName = commandNames.get(0);
        String[] aliases = new String[0];

        if (commandNames.size() > 1) {
            aliases = commandNames.subList(1, commandNames.size()).toArray(new String[0]);
        }

        getProxy().getPluginManager().registerCommand(this, new AdminCommand(this, mainName, aliases));
    }

    private void loadAliasesFromConfig() {
        Configuration section = config.getSection("aliases");
        if (section == null) return;

        for (String alias : section.getKeys()) {
            String target = section.getString(alias);
            getProxy().getPluginManager().registerCommand(this, new AliasCommand(alias, target));
        }
    }

    public void addAliasToConfig(String alias, String target) {
        config.set("aliases." + alias, target);
        saveConfig();
    }
}