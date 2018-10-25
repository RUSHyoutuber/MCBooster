package br.com.mcbooster.listeners;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Result;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemFlag;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.mcbooster.MCBooster;
import br.com.mcbooster.models.PlayerBooster;

public class BoosterListener implements Listener {

	private MCBooster pl;
	private Inventory inventory;
	private HashMap<Integer, SkillType> menu;

	public BoosterListener(MCBooster pl) {
		this.pl = pl;
		this.menu = new HashMap<>();
		this.inventory = Bukkit.createInventory(null, pl.getConfig().getInt("Linhas-Do-Menu") * 9, pl.config.getString("Titulo-Do-Menu").replace('&', '§'));
		for (String skill : pl.config.getConfigurationSection("Skills").getKeys(false)) {
			if (pl.config.getBoolean("Skills." + skill + ".Ativar")) {
				int slot = pl.config.getInt("Skills."+ skill +".Slot");
				this.inventory.setItem(slot, createItem(skill));
				this.menu.put(slot, SkillType.valueOf(skill.toUpperCase()));
			}
		}
		Bukkit.getPluginManager().registerEvents(this, pl);
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent e) {
		if (e.getAction() == Action.RIGHT_CLICK_AIR || e.getAction() == Action.RIGHT_CLICK_BLOCK) {
			Player p = e.getPlayer();
			ItemStack hand = p.getItemInHand();
			if (hand != null && hand.getType() != Material.AIR && hand.getType() == Material.EXP_BOTTLE && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().hasLore()) {
				if (hand.getItemMeta().getDisplayName().equals("§aBooster de Experiência")) {
					e.setCancelled(true);
					if (this.pl.playerBoosterManager.hasBoosterActived(p.getName())) {
						PlayerBooster playerBooster = this.pl.playerBoosterManager.getPlayerBooster(p.getName());
						long time = playerBooster.getEndTime() - System.currentTimeMillis();
						String timer = this.pl.formatDifference(time);
						String skill = playerBooster.getSkillType().getName();
						if (!timer.equals("agora")) {
							p.sendMessage(pl.config.getString("Booster-Ja-Ativado").replace('&', '§').replace("{skill}", skill).replace("{tempo}", timer));
							return;
						} else {
							this.pl.playerBoosterManager.removeBooster(p.getName());
						}
					}
					p.openInventory(this.inventory);
				}
			}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent e) {
		if (e.getCurrentItem() != null && e.getCurrentItem().getType() != Material.AIR && e.getInventory().getType() == InventoryType.CHEST) {
			if (e.getInventory().getName().equals(this.inventory.getName())) {
				e.setCancelled(true);
				e.setResult(Result.DENY);
				Player p = (Player) e.getWhoClicked();
				ItemStack hand = p.getItemInHand();
				if (hand != null && hand.getType() != Material.AIR && hand.getType() == Material.EXP_BOTTLE && hand.hasItemMeta() && hand.getItemMeta().hasDisplayName() && hand.getItemMeta().hasLore()) {
					if (!this.pl.playerBoosterManager.hasBoosterActived(p.getName())) {
						int slot = e.getSlot();
						if (menu.containsKey(slot)) {
							removeBooster(p);
							SkillType skill = menu.get(slot);
							this.pl.playerBoosterManager.setBooster(p.getName(), skill);
							p.sendMessage(pl.config.getString("Booster-Ativado-Sucesso").replace('&', '§').replace("{skill}", skill.getName()));
							p.closeInventory();
						}
					}
				} else {
					p.sendMessage(pl.config.getString("Booster-Error").replace('&', '§'));
					p.closeInventory();
				}
			}
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event) {
		Player player = event.getPlayer();
		if (this.pl.playerBoosterManager.hasBoosterActived(player.getName())) {
			PlayerBooster playerBooster = this.pl.playerBoosterManager.getPlayerBooster(player.getName());
			long rest = playerBooster.getEndTime() - System.currentTimeMillis();
			playerBooster.setRest(rest);
			playerBooster.setCheck(false);
			this.pl.sql.setRest(player.getName(), rest);
		}
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		if (this.pl.playerBoosterManager.hasBoosterActived(player.getName())) {
			PlayerBooster playerBooster = this.pl.playerBoosterManager.getPlayerBooster(player.getName());
			playerBooster.setEndTime(System.currentTimeMillis() + playerBooster.getRest());
			playerBooster.setCheck(true);
		}
	}
	
	private void removeBooster(Player p) {
		if (p.getItemInHand().getAmount() < 2) {
			p.setItemInHand(new ItemStack(Material.AIR));
		} else {
			ItemStack item = p.getItemInHand();
			item.setAmount(item.getAmount() - 1);
		}
	}

	@SuppressWarnings("deprecation")
	private ItemStack createItem(String skill) {
		int id = pl.config.getInt("Skills." + skill + ".Id");
		int data = pl.config.getInt("Skills." + skill + ".Data");
		boolean flags = pl.config.getBoolean("Skills." + skill + ".Flags");
		String nome = pl.getConfig().getString("Skills." + skill + ".Nome").replace('&', '§');
		List<String> lore = new ArrayList<>();
		ItemStack item = new ItemStack(id);
		ItemMeta meta = item.getItemMeta();
		for (String str : pl.config.getStringList("Skills." + skill + ".Lore")) 
			lore.add(str.replace('&', '§'));
		if (flags)
			meta.addItemFlags(ItemFlag.values());
		meta.setDisplayName(nome);
		meta.setLore(lore);
		item.setItemMeta(meta);
		item.setDurability((short) data);
		return item;
	}

}