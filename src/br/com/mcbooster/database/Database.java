package br.com.mcbooster.database;

import java.io.File;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.bukkit.Bukkit;

import br.com.mcbooster.MCBooster;
import br.com.mcbooster.models.PlayerBooster;

public class Database {

	private String user, host, database, password;
	private Connection connection;
	private Statement statement;
	private SQLType sqlType;
	private File db;
	private MCBooster pl;

	public Database(String user, String password, String host, String database, SQLType sqlType, MCBooster pl) {
		this.user = user;
		this.password = password;
		this.host = host;
		this.database = database;
		this.sqlType = sqlType;
		this.pl = pl;
	}

	public Database(String database, File folder, SQLType sqlType, MCBooster plugin) {
		this.db = folder;
		this.database = database;
		this.sqlType = sqlType;
		this.pl = plugin;
	}

	public void startConnection() {
		if (getType()) {
			try {
				Class.forName("com.mysql.jdbc.Driver");
				this.connection = DriverManager.getConnection("jdbc:mysql://" + host + "/" + database + "", user, password);
				this.statement = this.connection.createStatement();
				this.statement.execute("CREATE TABLE IF NOT EXISTS booster (nick VARCHAR(16) NOT NULL, skill VARCHAR(50) NOT NULL, time BIGINT NOT NULL, rest BIGINT NOT NULL)");
			} catch (SQLException | ClassNotFoundException e) {
				e.printStackTrace();
			}
		} else {
			try {
				Class.forName("org.sqlite.JDBC");
				this.connection = DriverManager.getConnection("jdbc:sqlite:" + this.db.getAbsolutePath() + File.separator + database + ".db");
				this.statement = this.connection.createStatement();
				this.statement.execute("CREATE TABLE IF NOT EXISTS booster (nick VARCHAR(16) NOT NULL, skill VARCHAR(50) NOT NULL, time BIGINT NOT NULL, rest BIGINT NOT NULL)");
			} catch (ClassNotFoundException | SQLException e) {
				e.printStackTrace();
			}
		}
	}

	public void closeConnection() {
		try {
			this.statement.close();
			this.connection.close();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setBooster(String nick, String skill, long time) {
		try {
			PreparedStatement ps = this.connection.prepareStatement("INSERT INTO booster (nick, skill, time, rest) VALUES (?,?,?,?)");
			ps.setString(1, nick);
			ps.setString(2, skill);
			ps.setLong(3, time);
			ps.setLong(4, 0);
			ps.execute();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void removeBooster(String nick) {
		try {
			PreparedStatement ps = this.connection.prepareStatement("DELETE FROM booster WHERE nick=?");
			ps.setString(1, nick);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void setRest(String nick, long rest) {
		try {
			PreparedStatement ps = this.connection.prepareStatement("UPDATE booster SET rest=? WHERE nick=?");
			ps.setString(2, nick);
			ps.setLong(1, rest);
			ps.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public void loadBoosters() {
		try {
			ResultSet rs = this.statement.executeQuery("SELECT * FROM booster");
			PlayerBooster playerBooster;
			int i = 0;
			while (rs.next()) {
				playerBooster = new PlayerBooster(rs.getString("nick"), rs.getString("skill"), rs.getLong("time"));
				playerBooster.setRest(rs.getLong("rest"));
				playerBooster.setCheck(false);
				pl.playerBoosterManager.boosters.put(rs.getString("nick"), playerBooster);
				i++;
			}
			Bukkit.getConsoleSender().sendMessage("§f[MCBooster] * Foram carregados §f" + i + " §7boosters.");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public boolean getType() {
		if (sqlType == SQLType.MySQL)
			return true;
		else 
			return false;
	}

	public enum SQLType {
		MySQL, SQLite;
	}

}