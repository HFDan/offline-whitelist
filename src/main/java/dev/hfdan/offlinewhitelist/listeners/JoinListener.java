package dev.hfdan.offlinewhitelist.listeners;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import dev.hfdan.offlinewhitelist.Offlinewhitelist;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.TextComponent;
import net.kyori.adventure.text.format.TextColor;
public class JoinListener implements Listener {

	private Offlinewhitelist plugin;
	public JoinListener(Offlinewhitelist plugin) {
		this.plugin = plugin;
	}

	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent ev) {
		if (plugin.enabled) {
			Player plr = ev.getPlayer();

			plugin.getLogger().info("Player name is " + plr.getName());

			boolean wl = plugin.checkPlayerInWhitelist(plr.getName());
			plugin.getLogger().info("Player is " + (wl ? "" : "not ") + "whitelisted");
			if (!wl) {
				final TextComponent tc = Component.text("You are ")
					.append(Component.text("NOT")
					.color(TextColor.color(0xb80000)))
					.append(Component.text(" whitelisted!")); 
				plr.kick(tc);
			}

		}
	}
}
