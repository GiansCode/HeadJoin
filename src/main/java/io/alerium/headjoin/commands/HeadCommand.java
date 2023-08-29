package io.alerium.headjoin.commands;

import io.alerium.headjoin.HeadJoin;
import io.alerium.headjoin.util.HeadMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.serializer.legacy.LegacyComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class HeadCommand implements CommandExecutor {

    private final HeadJoin plugin;

    public HeadCommand(HeadJoin plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String label, @NotNull String[] args) {
        if (!sender.hasPermission("headmotd.admin")) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cYou cannot use this command."));
            return true;
        }

        if (args.length == 0) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a/headmotd reload &8- &eReload the configuration."));
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a/headmotd send <playerHeadName> <player / *> <customMessageName> &8- &eSend a custom head message."));
            return true;
        }

        if (args[0].equalsIgnoreCase("reload")) {
            plugin.reloadConfig();
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&aThe configuration has been reloaded."));
            return true;
        }

        if (args[0].equalsIgnoreCase("send")) {
            headMotdSend(sender, Arrays.copyOfRange(args, 1, args.length));
            return true;
        }

        return true;
    }

    private void headMotdSend(CommandSender sender, String[] args) {
        if (args.length != 3) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&a/headmotd send <playerHeadName> <player / *> <customMessageName> &8- &eSend a custom head message."));
            return;
        }

        String playerHeadName = args[0];
        OfflinePlayer offlinePlayer = Bukkit.getOfflinePlayer(playerHeadName);
        if (offlinePlayer.getPlayerProfile().getId() == null) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cInvalid player head."));
            return;
        }

        Collection<? extends Player> target;
        if (args[1].equals("*")) {
            target = Bukkit.getOnlinePlayers();
        } else {
            Player tempTarget = Bukkit.getPlayer(args[1]);
            if (tempTarget == null) {
                sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cTarget player not found."));
                return;
            }

            target = Collections.singleton(tempTarget);
        }

        if (!plugin.getConfig().contains("custom-messages." + args[2])) {
            sender.sendMessage(LegacyComponentSerializer.legacyAmpersand().deserialize("&cCustom message not found."));
            return;
        }

        List<String> messages = plugin.getConfig().getStringList("custom-messages." + args[2])
                .stream()
                .map(message -> PlaceholderAPI.setPlaceholders(offlinePlayer, message.replace("{player}", args[0])))
                .collect(Collectors.toList());

        List<Component> components = HeadMessage.getComponentList(offlinePlayer.getPlayerProfile().getId(), plugin.getConfig().getInt("head-size"), plugin.getConfig().getBoolean("center-messages"), messages);
        target.forEach(p -> components.forEach(p::sendMessage));
    }

}
