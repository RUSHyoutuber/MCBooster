package br.com.mcbooster.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

import br.com.mcbooster.MCBooster;

public class GainXPListener implements Listener {

	private MCBooster pl;

	public GainXPListener(MCBooster pl) {
		this.pl = pl;
		Bukkit.getPluginManager().registerEvents(this, pl);
	}

	@SuppressWarnings("deprecation")
	@EventHandler
	private void onGainXp(McMMOPlayerXpGainEvent e) {
		Player player = e.getPlayer();
		if (this.pl.playerBoosterManager.hasBoosterActived(player.getName(), e.getSkill())) {
			int newXpGained = e.getXpGained() * 2;
			e.setXpGained(newXpGained);
		}
	}

}