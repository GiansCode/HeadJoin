package io.alerium.headjoin;

import io.alerium.headjoin.commands.HeadCommand;
import io.alerium.headjoin.util.HeadMessage;
import me.clip.placeholderapi.PlaceholderAPI;
import net.kyori.adventure.text.Component;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class HeadJoin extends JavaPlugin implements Listener {

    @Override
    public void onEnable() {
        saveDefaultConfig();

        HeadMessage.init(this, new File("cached_heads"));

        getServer().getPluginManager().registerEvents(this, this);

        getCommand("headmotd").setExecutor(new HeadCommand(this));
    }

    @Override
    public void onDisable() {

    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        event.joinMessage(Component.empty());

        if (!event.getPlayer().hasPermission("headmotd.join")) {
            return;
        }

        List<String> messages = new ArrayList<>();

        getConfig().getStringList("message").forEach(message -> {
            messages.add(PlaceholderAPI.setPlaceholders(event.getPlayer(), message.replace("{player}", event.getPlayer().getName())));
        });

        HeadMessage.sendHead(event.getPlayer(), event.getPlayer().getUniqueId(), getConfig().getInt("head-size"), getConfig().getBoolean("center-messages"), messages);
    }

}
