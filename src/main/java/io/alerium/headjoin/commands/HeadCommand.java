package io.alerium.headjoin.commands;

import io.alerium.headjoin.HeadJoin;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;

public class HeadCommand implements CommandExecutor {

    private final HeadJoin plugin;

    public HeadCommand(HeadJoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if(!sender.hasPermission("headmotd.admin")) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cYou cannot use this command."));
            return true;
        }

        if(args.length == 0) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a/headmotd reload &8- &eReload the configuration."));
            return true;
        }

        if(args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aThe configuration has been reloaded."));
            return true;
        }

        return true;
    }

}
