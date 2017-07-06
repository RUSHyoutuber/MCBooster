package br.com.mcbooster.listeners;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.ClickType;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import com.gmail.nossr50.datatypes.skills.SkillType;

import br.com.mcbooster.MCBooster;
import br.com.mcbooster.models.PlayerBooster;

public class BoosterListener implements Listener {

	private MCBooster plugin;
	private Inventory inventory;

	public BoosterListener(MCBooster plugin) {
		this.plugin = plugin;
		this.inventory = Bukkit.createInventory(null, 27, "Escolha a habilidade:");
		this.inventory.setItem(12, createItem(Material.DIAMOND_PICKAXE, "§aMineração", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		this.inventory.setItem(11, createItem(Material.BOW, "§aArqueiro", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		this.inventory.setItem(10, createItem(Material.DIAMOND_SWORD, "§aEspadas", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		this.inventory.setItem(14, createItem(Material.DIAMOND_SPADE, "§aEscavação", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		this.inventory.setItem(15, createItem(Material.ANVIL, "§aReparação", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		this.inventory.setItem(16, createItem(Material.DIAMOND_BOOTS, "§aAcrobacia", new String[] { "§7* Ative o booster nessa habilidade!" }, 1, 0));
		Bukkit.getPluginManager().registerEvents(this, plugin);
	}

	@EventHandler
	private void onInteract(PlayerInteractEvent event){
		Player player = event.getPlayer();
		if(player.getItemInHand() != null && player.getItemInHand().getTypeId() != 0 && player.getItemInHand().getType() == Material.EXP_BOTTLE && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore()){
			if(player.getItemInHand().getItemMeta().getDisplayName().equals("§aBooster de Experiência")){
				event.setCancelled(true);
				if(this.plugin.playerBoosterManager.hasBoosterActived(player.getName())){
					PlayerBooster playerBooster = this.plugin.playerBoosterManager.getPlayerBooster(player.getName());
					long time = playerBooster.getEndTime() - System.currentTimeMillis();
					String timer = this.plugin.formatDifference(time);
					if(!timer.equals("agora")){
						player.sendMessage("§7* Você já possui um booster ativado na skill §f" + playerBooster.getSkillType().getName() + "§7.");
						player.sendMessage("§7* Tempo restante: §f" + timer + "§7.");
						return;
					}else{
						this.plugin.playerBoosterManager.removeBooster(player.getName());
					}
				}
				player.updateInventory();
				player.openInventory(this.inventory);
			}
		}
	}

	@EventHandler
	private void onClick(InventoryClickEvent event){
		if(event.getCurrentItem() == null || event.getCurrentItem().getType() == Material.AIR){
			return;
		}
		if(event.getInventory().getName().equals(this.inventory.getName())){
			event.setCancelled(true);
		}
		if(event.getWhoClicked() instanceof Player && event.getClickedInventory().getName().equals(this.inventory.getName())){
			Player player = (Player) event.getWhoClicked();
			ItemStack item = event.getCurrentItem();
			if(item.hasItemMeta() && item.getItemMeta().hasDisplayName() && item.getItemMeta().hasLore() && player.getItemInHand() != null && player.getItemInHand().getType() != Material.AIR && player.getItemInHand().getType() == Material.EXP_BOTTLE && player.getItemInHand().hasItemMeta() && player.getItemInHand().getItemMeta().hasDisplayName() && player.getItemInHand().getItemMeta().hasLore() && player.getItemInHand().getItemMeta().getDisplayName().equals("§aBooster de Experiência")){
				if(!this.plugin.playerBoosterManager.hasBoosterActived(player.getName())){
					boolean remove = false;
					if(item.getItemMeta().getDisplayName().equals("§aMineração")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.MINING);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fMineração§7!");
						remove = true;
					}
					if(item.getItemMeta().getDisplayName().equals("§aArqueiro")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.ARCHERY);
						player.sendMessage("§7*Parabéns, você ativou um booster na habilidade §fArqueiro§7!");
						remove = true;
					}
					if(item.getItemMeta().getDisplayName().equals("§aEspadas")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.SWORDS);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fEspadas§7!");
						remove = true;
					}
					if(item.getItemMeta().getDisplayName().equals("§aEscavação")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.EXCAVATION);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fEscavação§7!");
						remove = true;
					}
					if(item.getItemMeta().getDisplayName().equals("§aReparação")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.REPAIR);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fReparação§7!");
						remove = true;
					}
					if(item.getItemMeta().getDisplayName().equals("§aAcrobacia")){
						this.plugin.playerBoosterManager.setBooster(player.getName(), SkillType.ACROBATICS);
						player.sendMessage("§7* Parabéns, você ativou um booster na habilidade §fAcrobacia§7!");
						remove = true;
					}
					if(remove){
						if(player.getItemInHand().getAmount() - 1 >= 1){
							ItemStack is = new ItemStack(Material.EXP_BOTTLE, player.getItemInHand().getAmount() - 1);
							ItemMeta itemMeta = is.getItemMeta();
							itemMeta.setDisplayName("§aBooster de Experiência");
							List<String> lore = new ArrayList<>();
							lore.add("§7* Receba §f1 hora §7de §fDuplo XP §7em qual quer habilidade!");
							itemMeta.setLore(lore);
							is.setItemMeta(itemMeta);
							player.setItemInHand(is);
						}else{
							player.setItemInHand(null);
						}
						remove = false;
					}
					player.closeInventory();
					player.updateInventory();
				}
			}else{
				player.sendMessage("§7* Você precisa estar segurando um §fbooster §7para selecionar a habilidade.");
				player.closeInventory();
			}
		}
	}

	@EventHandler
	private void onQuit(PlayerQuitEvent event){
		Player player = event.getPlayer();
		if(this.plugin.playerBoosterManager.hasBoosterActived(player.getName())){
			PlayerBooster playerBooster = this.plugin.playerBoosterManager.getPlayerBooster(player.getName());
			long rest = playerBooster.getEndTime() - System.currentTimeMillis();
			playerBooster.setRest(rest);
			playerBooster.setCheck(false);
			this.plugin.sql.setRest(player.getName(), rest);
		}
	}

	@EventHandler
	private void onJoin(PlayerJoinEvent event){
		Player player = event.getPlayer();
		if(this.plugin.playerBoosterManager.hasBoosterActived(player.getName())){
			PlayerBooster playerBooster = this.plugin.playerBoosterManager.getPlayerBooster(player.getName());
			playerBooster.setEndTime(System.currentTimeMillis() + playerBooster.getRest());
			playerBooster.setCheck(true);
		}
	}

	private ItemStack createItem(Material material, String nome, String[] lore, int quantidade, int metadata) {
		ItemStack item = new ItemStack(material, quantidade, (byte) metadata);
		ItemMeta itemM = item.getItemMeta();
		itemM.setDisplayName(nome);
		List<String> itemLore = new ArrayList<>();
		if (lore != null) {
			for (String loree : lore) {
				itemLore.add(loree);
			}
			itemM.setLore(itemLore);
		}
		item.setItemMeta(itemM);
		return item;
	}

}
