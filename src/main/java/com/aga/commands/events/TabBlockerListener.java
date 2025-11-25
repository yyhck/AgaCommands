package com.aga.commands.events;

import com.aga.commands.AgaCommands; // Import necessário

import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.TabCompleteEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.util.List;

public class TabBlockerListener implements Listener {

    private final AgaCommands plugin;

    public TabBlockerListener(AgaCommands plugin) {
        this.plugin = plugin;
    }

    @EventHandler
    public void onTab(TabCompleteEvent event) {
        // Se a função estiver desativada na config, ignora
        if (!plugin.getConfig().getBoolean("tab-settings.enabled")) return;

        if (!(event.getSender() instanceof ProxiedPlayer)) return;

        ProxiedPlayer player = (ProxiedPlayer) event.getSender();

        if (player.hasPermission("agacommands.admin")) return;

        String cursor = event.getCursor().toLowerCase();

        // Se não começa com barra, é chat normal, deixa passar
        if (!cursor.startsWith("/")) return;

        List<String> allowed = plugin.getConfig().getStringList("tab-settings.allowed-commands");
        boolean isAllowed = false;

        for (String cmd : allowed) {
            // Verifica se o que o player digitou começa com algum comando permitido
            if (cursor.startsWith(cmd.toLowerCase())) {
                isAllowed = true;
                break;
            }
        }

        if (!isAllowed) {
            event.setCancelled(true);
        }
    }
}