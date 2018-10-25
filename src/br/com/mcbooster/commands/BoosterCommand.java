package br.com.mcbooster.commands;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import br.com.mcbooster.MCBooster;
import br.com.mcbooster.models.PlayerBooster;

public class BoosterCommand implements CommandExecutor {

	public ItemStack item;
	private MCBooster pl;

	public BoosterCommand(MCBooster pl) {
		this.pl = pl;
		this.item = new ItemStack(Material.EXP_BOTTLE);
		ItemMeta meta = item.getItemMeta();
		List<String> lore = new ArrayList<>();
		String nome = pl.config.getString("Nome-Do-Booster").replace('&', '§');
		for (String str : pl.config.getStringList("Lore-Do-Booster"))
			lore.add(str.replace('&', '§'));
		meta.setLore(lore);
		meta.setDisplayName(nome);
		this.item.setItemMeta(meta);
		pl.getCommand("givebooster").setExecutor(this);
		pl.getCommand("booster").setExecutor(this);
	}

	@SuppressWarnings("deprecation")
	@Override
	public boolean onCommand(CommandSender s, Command cmd, String lbl, String[] args) {

		/**
		 * @command /givebooster
		 */
		if (cmd.getName().equalsIgnoreCase("givebooster")) {
			if (args.length < 1 || args.length > 2) {
				s.sendMessage(pl.config.getString("Givebooster-Comando-Incorreto").replace('&', '§'));
				return true;
			}
			
			if (!s.hasPermission("booster.admin")) {
				s.sendMessage(pl.config.getString("Givebooster-Sem-Permissao").replace('&', '§').replace("{player}", s.getName()));
				return true;
			}
			
			Player target = Bukkit.getPlayer(args[0]);
			if (target == null) {
				s.sendMessage(pl.config.getString("Givebooster-Player-Offline").replace('&', '§').replace("{player}", args[0]));
				return true;
			}
			
			int quantia = 1;
			if (args.length > 1) {
				try {
					quantia = Integer.valueOf(args[1]);
				} catch (Exception e) {
					s.sendMessage(pl.config.getString("Givebooster-Quantia-Invalida").replace('&', '§').replace("{numero}", args[1]));
					return true;
				}
			}
			
			s.sendMessage(pl.config.getString("Givebooster-Sucesso").replace('&', '§').replace("{player}", target.getName()));
			addItem(target, quantia);
			return true;
		}

		/**
		 * @command /booster
		 */
		if (cmd.getName().equalsIgnoreCase("booster")) {
			if (!(s instanceof Player)) {
				s.sendMessage(pl.config.getString("Booster-Console-Nao-Pode").replace('&', '§'));
				return true;
			}
			
			Player p = (Player) s;
			if (this.pl.playerBoosterManager.hasBoosterActived(p.getName())) {
				PlayerBooster playerBooster = this.pl.playerBoosterManager.getPlayerBooster(p.getName());
				long time = playerBooster.getEndTime() - System.currentTimeMillis();
				String timer = this.pl.formatDifference(time);
				String skill = playerBooster.getSkillType().getName();
				if (!timer.equals("agora")) {
					s.sendMessage(pl.config.getString("Booster-Ativado").replace('&', '§').replace("{skill}", skill).replace("{tempo}", timer));
					return true;
				} else {
					this.pl.playerBoosterManager.removeBooster(p.getName());
				}
			}
			
			s.sendMessage(pl.config.getString("Booster-Nao-Possui").replace('&', '§'));
			return true;
		}
		return true;
	}
	
	private void addItem(Player p, int quantia) {
		this.item.setAmount(quantia);
		if (p.getInventory().firstEmpty() == -1) 
			p.getWorld().dropItem(p.getLocation(), this.item);
		else
			p.getInventory().addItem(this.item);
	}
}