package com.aga.commands.events;

import com.aga.commands.AgaCommands; // Import necessário

import net.md_5.bungee.api.ChatColor;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.ChatEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;
import net.md_5.bungee.api.chat.TextComponent; // Certifique-se deste import

import java.util.List;

public class CommandBlockerListener implements Listener {

    private final AgaCommands plugin;

    public CommandBlockerListener(AgaCommands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onChat(ChatEvent event) {
        if (!event.isCommand()) return;
        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        // Permissao de bypass
        if (player.hasPermission("agacommands.admin")) return;

        String message = event.getMessage();
        // Pega apenas a primeira parte (ex: /pl)
        String rootCommand = message.split(" ")[0].toLowerCase();

        List<String> blockedList = plugin.getConfig().getStringList("blocked-commands");

        if (blockedList.contains(rootCommand)) {
            event.setCancelled(true);

            String msg = plugin.getConfig().getString("messages.blocked-command", "&cComando desconhecido.");
            player.sendMessage(new TextComponent(ChatColor.translateAlternateColorCodes('&', msg)));

            // Log Async
            plugin.logBlockedCommand(player.getName(), message);
        }
    }
}