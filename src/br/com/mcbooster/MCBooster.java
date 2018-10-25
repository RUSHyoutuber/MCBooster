package br.com.mcbooster;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import br.com.mcbooster.commands.BoosterCommand;
import br.com.mcbooster.database.Database;
import br.com.mcbooster.database.Database.SQLType;
import br.com.mcbooster.listeners.BoosterListener;
import br.com.mcbooster.listeners.GainXPListener;
import br.com.mcbooster.managers.PlayerBoosterManager;
import br.com.mcbooster.models.PlayerBooster;

public class MCBooster extends JavaPlugin {

	public PlayerBoosterManager playerBoosterManager;
	public FileConfiguration config;
	public Database sql;

	@Override
	public void onEnable() {
		saveDefaultConfig();
		
		if (!getConfig().getBoolean("MySQL.Ativar")) {
			File db = new File(getDataFolder() + File.separator + "Boosters.db");
			if (!db.exists()) {
				try {
					db.createNewFile();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		
		if (getConfig().getBoolean("MySQL.Ativar")) {
			String user, password, host, database;
			user = getConfig().getString("MySQL.User");
			password = getConfig().getString("MySQL.Password");
			host = getConfig().getString("MySQL.Host");
			database = getConfig().getString("MySQL.Database");
			this.sql = new Database(user, password, host, database, SQLType.MySQL, this);
			this.sql.startConnection();
		} else {
			this.sql = new Database("Boosters", getDataFolder(), SQLType.SQLite, this);
			this.sql.startConnection();
		}
		
		config = getConfig();
		playerBoosterManager = new PlayerBoosterManager(this);
		new BoosterCommand(this);
		new BoosterListener(this);
		new GainXPListener(this);
		sql.loadBoosters();
		start();
	}

	@Override
	public void onDisable() {
		HandlerList.unregisterAll(this);
		sql.closeConnection();
	}

	public String formatDifference(long time) {
		if (time == 0)
			return "nunca";
		long day = TimeUnit.MILLISECONDS.toDays(time);
		long hours = TimeUnit.MILLISECONDS.toHours(time) - (day * 24);
		long minutes = TimeUnit.MILLISECONDS.toMinutes(time) - (TimeUnit.MILLISECONDS.toHours(time) * 60);
		long seconds = TimeUnit.MILLISECONDS.toSeconds(time) - (TimeUnit.MILLISECONDS.toMinutes(time) * 60);
		StringBuilder sb = new StringBuilder();
		if (day > 0)
			sb.append(day).append(" ").append(day == 1 ? "dia" : "dias").append(" ");
		if (hours > 0)
			sb.append(hours).append(" ").append(hours == 1 ? "hora" : "horas").append(" ");
		if (minutes > 0)
			sb.append(minutes).append(" ").append(minutes == 1 ? "minuto" : "minutos").append(" ");
		if (seconds > 0)
			sb.append(seconds).append(" ").append(seconds == 1 ? "segundo" : "segundos");
		String diff = sb.toString();
		return diff.isEmpty() ? "agora" : diff;
	}

	@SuppressWarnings("deprecation")
	private void start() {
		new BukkitRunnable() {
			@Override
			public void run() {
				for (PlayerBooster pb : playerBoosterManager.boosters.values()) {
					if (pb.isCheck() && System.currentTimeMillis() >= pb.getEndTime()) {
						if (Bukkit.getPlayer(pb.getNick()) != null)
							Bukkit.getPlayer(pb.getNick()).sendMessage(config.getString("Acabou-Booster").replace('&', '§').replace("{skill}", pb.getSkillType().getName()));
						playerBoosterManager.removeBooster(pb.getNick());
					}
				}
			}
		}.runTaskTimerAsynchronously(this, 100L, 100L);
	}

}