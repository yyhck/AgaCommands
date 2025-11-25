package com.aga.commands.commands;

import com.aga.commands.AgaCommands;
import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.api.plugin.Command;

import java.util.List;

public class AdminCommand extends Command {

    private final AgaCommands plugin;

    public AdminCommand(AgaCommands plugin, String name, String[] aliases) {
        // null na permissão para controle manual da mensagem
        super(name, null, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("agacommands.admin")) {
            String msg = plugin.getConfig().getString("messages.no-permission", "&cVoce nao tem permissao.");
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));
            return;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String action = args[0].toLowerCase();

        switch (action) {
            case "reload":
                plugin.reloadPlugin(sender);
                break;

            default:
                if (args.length >= 2) {
                    createAlias(sender, args[0], args[1]);
                } else {
                    sendHelp(sender);
                }
                break;
        }
    }

    private void createAlias(CommandSender sender, String original, String alias) {
        if (original.startsWith("/")) original = original.substring(1);
        if (alias.startsWith("/")) alias = alias.substring(1);

        plugin.addAliasToConfig(alias, original);

        // Registra o novo alias imediatamente
        plugin.getProxy().getPluginManager().registerCommand(plugin, new AliasCommand(alias, original));

        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Atalho " + ChatColor.YELLOW + "/" + alias +
                ChatColor.GREEN + " criado para " + ChatColor.YELLOW + "/" + original));
    }

    private void sendHelp(CommandSender sender) {
        List<String> helpLines = plugin.getConfig().getStringList("messages.help");

        if (helpLines == null || helpLines.isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ajuda nao configurada na config.yml"));
            return;
        }

        for (String line : helpLines) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', line)));
        }
    }
}