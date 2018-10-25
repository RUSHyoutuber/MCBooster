package br.com.mcbooster.managers;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.mcbooster.MCBooster;
import br.com.mcbooster.models.PlayerBooster;

public class PlayerBoosterManager {

	public ConcurrentHashMap<String, PlayerBooster> boosters;
	private MCBooster pl;

	public PlayerBoosterManager(MCBooster pl) {
		this.pl = pl;
		this.boosters = new ConcurrentHashMap<>();
	}

	public void setBooster(String nick, SkillType skillType) {
		PlayerBooster playerBooster = new PlayerBooster(nick, skillType);
		this.boosters.put(nick, playerBooster);
		this.pl.sql.setBooster(nick, skillType.getName(), System.currentTimeMillis() + TimeUnit.MINUTES.toMillis(60));
	}

	public void removeBooster(String nick) {
		this.boosters.remove(nick);
		this.pl.sql.removeBooster(nick);
	}

	public boolean hasBoosterActived(String nick) {
		return this.boosters.containsKey(nick);
	}

	public boolean hasBoosterActived(String nick, SkillType skillType) {
		if (hasBoosterActived(nick) && this.boosters.get(nick).getSkillType().equals(skillType)) {
			return true;
		} else {
			return false;
		}
	}

	public PlayerBooster getPlayerBooster(String nick) {
		return this.boosters.get(nick);
	}

}