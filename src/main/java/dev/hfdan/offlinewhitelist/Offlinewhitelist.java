package dev.hfdan.offlinewhitelist;

import dev.hfdan.offlinewhitelist.listeners.JoinListener;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;

public final class Offlinewhitelist extends JavaPlugin {

	private static Connection conn = null;
	public boolean enabled = true;

	@Override
	public void onEnable() {
		// Plugin startup logic
		getLogger().info("Offline Whitelist enabled!");
		getServer().getPluginManager().registerEvents(new JoinListener(this), this);
		try {
			Class.forName("org.sqlite.JDBC");
			conn = DriverManager.getConnection("jdbc:sqlite:offlinewhitelist.db");
			String sql = "CREATE TABLE IF NOT EXISTS WHITELIST " +
					"(ID INTEGER PRIMARY KEY AUTOINCREMENT, USERNAME TEXT NOT NULL);";
			Statement st = conn.createStatement();
			st.executeUpdate(sql);
			st.close();
		} catch (Exception e) {
			getLogger().warning("Could not open database!\n" + e.getClass().getName() + ": " + e.getMessage());
		}
	}

	@Override
	public void onDisable() {
		// Plugin shutdown logic
		getLogger().info("Offline Whitelist disabled!");
		try {
			conn.close();
		} catch (Exception ex) {
			getLogger().warning("Could not close connection: " + ex.getMessage());
		}
	}

	public boolean checkPlayerInWhitelist(String PlayerName) {
		try {
			String sql = "SELECT (count(*) > 0) as found FROM WHITELIST WHERE USERNAME = ?;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, PlayerName);
			ResultSet rs = st.executeQuery();

			getLogger().info("Query: " + rs.getStatement().toString());

			boolean ret = rs.getBoolean(1);

			st.close();
			rs.close();
			return ret;
		} catch (Exception ex) {
			getLogger().warning("Could not check if player is in whitelist: " + ex.getMessage());
			return false;
		}
	}

	private void addToWhitelist(String PlayerName) {
		try {
			String sql = "INSERT INTO WHITELIST (USERNAME)\n" + "VALUES(?);";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, PlayerName);
			st.executeUpdate();
			st.close();
		} catch (Exception ex) {
			getLogger().warning("Could not add user to whitelist: " + ex.getMessage());
		}
	}

	private void removeFromWhitelist(String PlayerName) {
		try {
			String sql = "DELETE FROM WHITELIST WHERE USERNAME = ?;";
			PreparedStatement st = conn.prepareStatement(sql);
			st.setString(1, PlayerName);
			st.executeUpdate();
			st.close();
		} catch (Exception ex) {
			getLogger().warning("Could not remove user from whitelist: " + ex.getMessage());
		}
	}

	private String getWhitelisted() {
		StringBuilder ret = new StringBuilder();
		try {
			String sql = "SELECT * FROM WHITELIST;";
			Statement st = conn.createStatement();
			ResultSet rs = st.executeQuery(sql);

			while (rs.next()) {
				ret.append(rs.getString("USERNAME"));
				ret.append(" ");
			}

			st.close();
			rs.close();
		} catch (Exception ex) {
			getLogger().warning("Could not get whitelisted players: " + ex.getMessage());
		}
		return ret.toString();
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if (cmd.getName().equalsIgnoreCase("offlinewhitelist")) {
			if (sender.isOp()) {
				switch (args[0]) {
					case "on" -> {
						enabled = true;
						return true;
					}
					case "off" -> {
						enabled = false;
						return true;
					}
					case "add" -> {
						addToWhitelist(args[1]);
						return true;
					}
					case "remove" -> {
						removeFromWhitelist(args[1]);
						return true;
					}
					case "list" -> {
						sender.sendMessage(getWhitelisted());
						return true;
					}
				}
			}
		}

		return false;
	}
}
