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
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
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

        reloadPlugin(null);

        getProxy().getPluginManager().registerListener(this, new CommandBlockerListener(this));
        getProxy().getPluginManager().registerListener(this, new TabBlockerListener(this));

        sendConsoleLogo();
    }

    private void sendConsoleLogo() {
        String border = "&a&l========================================";
        String title  = "&7      AgaCommands &8(Proxy) &a&lATIVADO";
        String author = "&7      Autor: &f" + getDescription().getAuthor();
        String ver    = "&7      Versao: &f" + getDescription().getVersion();

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

        getProxy().getPluginManager().unregisterCommands(this);

        registerAdminCommand();

        int count = loadAliasesFromConfig();

        if (sender != null) {
            String msg = config.getString("messages.reload", "&a[AgaCommands] Plugin recarregado!");
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));
            sender.sendMessage(new TextComponent(ChatColor.GRAY + "Carregados " + count + " atalhos."));
        } else {
            getLogger().info("Carregados " + count + " atalhos da config.");
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
            getLogger().severe("ERRO AO CARREGAR CONFIG.YML! Verifique a sintaxe.");
            e.printStackTrace();
        }
    }

    private void createLogFile() {
        logFile = new File(getDataFolder(), "blocked_commands.log");
        if (!logFile.exists()) {
            try {
                logFile.createNewFile();
            } catch (IOException e) {
                getLogger().warning("Não foi possível criar o arquivo de log.");
            }
        }
    }

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

    private int loadAliasesFromConfig() {
        Configuration section = config.getSection("aliases");
        if (section == null) return 0;

        int count = 0;
        for (String alias : section.getKeys()) {
            String target = section.getString(alias);
            getProxy().getPluginManager().registerCommand(this, new AliasCommand(alias, target));
            count++;
        }
        return count;
    }

    public void addAliasToConfig(String alias, String target) {
        Path path = configFile.toPath();
        try {
            List<String> lines = Files.readAllLines(path, StandardCharsets.UTF_8);

            int aliasesLineIndex = -1;
            int insertionIndex = -1;

            for (int i = 0; i < lines.size(); i++) {
                if (lines.get(i).trim().equals("aliases:")) {
                    aliasesLineIndex = i;
                    break;
                }
            }

            if (aliasesLineIndex != -1) {
                insertionIndex = aliasesLineIndex + 1;

                for (int i = aliasesLineIndex + 1; i < lines.size(); i++) {
                    String line = lines.get(i);

                    if (line.startsWith("  ") || (line.trim().startsWith("#") && line.startsWith("  "))) {
                        insertionIndex = i + 1;
                    }

                    else if (!line.trim().isEmpty() && !line.startsWith(" ")) {
                        break;
                    }
                }

                lines.add(insertionIndex, "  " + alias + ": " + target);
            } else {
                lines.add("aliases:");
                lines.add("  " + alias + ": " + target);
            }

            Files.write(path, lines, StandardCharsets.UTF_8);

            loadConfig();

        } catch (IOException e) {
            e.printStackTrace();
            getLogger().severe("Erro ao salvar o novo alias no arquivo config.yml preservando comentarios.");
        }
    }
}