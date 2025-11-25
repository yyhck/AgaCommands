package com.aga.commands.commands;

import com.aga.commands.AgaCommands;
import net.md_5.bungee.api.CommandSender;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.plugin.Command;

public class AliasCommand extends Command {

    private final String targetCommand;

    public AliasCommand(String name, String targetCommand) {
        super(name);
        this.targetCommand = targetCommand;
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        StringBuilder cmdLine = new StringBuilder(targetCommand);
        for (String arg : args) {
            cmdLine.append(" ").append(arg);
        }

        if (sender instanceof ProxiedPlayer) {
            ((ProxiedPlayer) sender).chat("/" + cmdLine.toString());
        } else {
            AgaCommands.getInstance().getProxy().getPluginManager().dispatchCommand(sender, cmdLine.toString());
        }
    }
}