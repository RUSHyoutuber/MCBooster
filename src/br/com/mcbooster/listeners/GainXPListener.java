package br.com.mcbooster.listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.gmail.nossr50.events.experience.McMMOPlayerXpGainEvent;

import br.com.mcbooster.MCBooster;

public class GainXPListener implements Listener{

	private MCBooster plugin;
	
	public GainXPListener(MCBooster plugin) {
		this.plugin = plugin;
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}
	
	@EventHandler
	private void onGainXp(McMMOPlayerXpGainEvent event){
		Player player = event.getPlayer();
		if(this.plugin.playerBoosterManager.hasBoosterActived(player.getName(), event.getSkill())){
			int newXpGained = event.getXpGained() * 2;
			event.setXpGained(newXpGained);
		}
	}
	
}
