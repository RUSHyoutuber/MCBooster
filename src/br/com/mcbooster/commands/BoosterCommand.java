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

public class BoosterCommand implements CommandExecutor{
	
	public ItemStack item;
	private MCBooster plugin;
	
	public BoosterCommand(MCBooster plugin) {
		this.plugin = plugin;
		this.item = new ItemStack(Material.EXP_BOTTLE);
		ItemMeta itemMeta = item.getItemMeta();
		itemMeta.setDisplayName("§aBooster de Experiência");
		List<String> lore = new ArrayList<>();
		lore.add("§7* Receba §f1 hora §7de §fDuplo XP §7em qual quer habilidade!");
		itemMeta.setLore(lore);
		this.item.setItemMeta(itemMeta);
		plugin.getCommand("givebooster").setExecutor(this);
		plugin.getCommand("booster").setExecutor(this);
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String arg2, String[] args) {

		if(cmd.getName().equalsIgnoreCase("givebooster")){
			if(args.length == 1 && sender.hasPermission("booster.admin")){
				Player target = Bukkit.getPlayerExact(args[0]);
				if(target == null){
					sender.sendMessage("§7* O jogador(a) §f" + args[0] + " §7está offline.");
					return true;
				}
				if(target.getInventory().firstEmpty() == -1){
					sender.sendMessage("§7* O jogador(a) §f" + args[0] + " §7está com inventário §fcheio§7.");
					return true;
				}
				target.getInventory().addItem(this.item);
				target.updateInventory();
			}
		}
		
		if(cmd.getName().equalsIgnoreCase("booster")){
			if(!(sender instanceof Player)) return true;
			Player player = (Player) sender;
			if(this.plugin.playerBoosterManager.hasBoosterActived(player.getName())){
				PlayerBooster playerBooster = this.plugin.playerBoosterManager.getPlayerBooster(player.getName());
				long time = playerBooster.getEndTime() - System.currentTimeMillis();
				String timer = this.plugin.formatDifference(time);
				if(!timer.equals("agora")){
					player.sendMessage("§7* Você já possui um booster ativado na skill §f" + playerBooster.getSkillType().getName() + "§7.");
					player.sendMessage("§7* Tempo restante: §f" + timer + "§7.");
					return true;
				}else{
					this.plugin.playerBoosterManager.removeBooster(player.getName());
				}
			}else{
				player.sendMessage("§7* Você não possui nenhum booster ativo!");
			}
		}
		
		return false;
	}

	
	
}
