package dev.hfdan.offlinewhitelist.listeners;

import dev.hfdan.offlinewhitelist.Offlinewhitelist;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import java.sql.*;

public class JoinListener implements Listener {

	private Offlinewhitelist plugin;
	public JoinListener(Offlinewhitelist plugin) {
		this.plugin = plugin;
	}

	private JoinListener() {}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		if (plugin.enabled) {
			Player plr = ev.getPlayer();

			plugin.getLogger().info("Player name is " + plr.getName());

			boolean wl = plugin.checkPlayerInWhitelist(plr.getName());
			plugin.getLogger().info("Player is " + (wl ? "" : "not ") + "whitelisted");
			if (!wl) {
				plr.kickPlayer("You are not whitelisted");
			}

		}
	}
}
