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
        super(name, null, aliases);
        this.plugin = plugin;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (!sender.hasPermission("agacommands.admin")) {
            String msg = plugin.getConfig().getString("messages.no-permission", "&cVoce não tem permissão.");
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));
            return;
        }

        if (args.length == 0) {
            sendHelp(sender);
            return;
        }

        String action = args[0].toLowerCase();

        if (action.equals("reload")) {
            plugin.reloadPlugin(sender);
            return;
        }

        if (args.length < 2) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Utilize /agacommands <comando_original> <novo_atalho>."));
            return;
        }

        String original = args[0];
        String alias = args[1];

        createAlias(sender, original, alias);
    }

    private void createAlias(CommandSender sender, String original, String alias) {
        if (original.startsWith("/")) original = original.substring(1);
        if (alias.startsWith("/")) alias = alias.substring(1);

        if (alias.trim().isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Erro: O nome do atalho não pode ser vazio ou apenas uma barra."));
            return;
        }

        if (original.trim().isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Erro: O comando original não pode ser vazio."));
            return;
        }

        plugin.addAliasToConfig(alias, original);

        plugin.getProxy().getPluginManager().registerCommand(plugin, new AliasCommand(alias, original));

        sender.sendMessage(new TextComponent(ChatColor.GREEN + "Sucesso! O comando " + ChatColor.WHITE + "/" + alias +
                ChatColor.GREEN + " agora também executa: " + ChatColor.WHITE + "/" + original + ChatColor.GREEN + "."));
    }

    private void sendHelp(CommandSender sender) {
        List<String> helpLines = plugin.getConfig().getStringList("messages.help");

        if (helpLines == null || helpLines.isEmpty()) {
            sender.sendMessage(new TextComponent(ChatColor.RED + "Ajuda nao configurada."));
            return;
        }

        for (String line : helpLines) {
            sender.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', line)));
        }
    }
}