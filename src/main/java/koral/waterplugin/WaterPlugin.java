package koral.waterplugin;

import com.destroystokyo.paper.event.player.PlayerPickupExperienceEvent;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.GameMode;
import org.bukkit.Material;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityBreedEvent;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.FurnaceExtractEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.event.player.PlayerItemConsumeEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.inventory.meta.PotionMeta;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionData;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.bukkit.potion.PotionType;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.io.File;

public final class WaterPlugin extends JavaPlugin implements Listener, CommandExecutor {

    private waterPluginCommands commandExecutor;
    boolean waterchanger = this.getConfig().getBoolean("waterchanger");

    public ItemStack getWaterBottle(int amount){
        ItemStack bottle = new ItemStack(Material.POTION, 1);
        ItemMeta itemMeta = bottle.getItemMeta();
        PotionMeta potionMeta = (PotionMeta) itemMeta;
        PotionData potionData = new PotionData(PotionType.WATER);
        potionMeta.setBasePotionData(potionData);
        bottle.setItemMeta(itemMeta);
         return bottle;
    }


    @EventHandler
    public void onItemConsumeEvent(PlayerItemConsumeEvent event){
        Player player = event.getPlayer();
        if(event.getItem().equals(getWaterBottle(1))){
            if(player.getExp() + 0.5f >1f)
                player.setExp(1f);
                else
            player.setExp(player.getExp() + 0.5f);
                confusionEffectRemove(player);
                player.sendMessage(ChatColor.BLUE + "Zaspokoiłeś pragnienie...");
        }

    }
    @EventHandler
    public void onEntityDeath(EntityDeathEvent e)
    {
        e.setDroppedExp(0);

    }
    @EventHandler
    public void onFurnaceExtract(FurnaceExtractEvent e){
        e.setExpToDrop(0);
    }
    @EventHandler
    public void onEntityBreeding(EntityBreedEvent e){
        e.setExperience(0);
    }


    @EventHandler
    public void playerJoinEvent(PlayerJoinEvent event){
        Player player = event.getPlayer();
        if(!player.hasPlayedBefore()){
            player.setExp(1f);
        }
    }

@EventHandler
public void playerPickupExp(PlayerPickupExperienceEvent e)
{
    e.setCancelled(true);
}

@EventHandler
public void onPlayerRespawnEvent(PlayerRespawnEvent e)
{
    Player player = e.getPlayer();
    runExpAddSelfCancel(player);
}

public void confusionEffectAdd(Player player, int duration){
    player.addPotionEffect(new PotionEffect(PotionEffectType.CONFUSION, duration, 1, true, true));
}

public void confusionEffectRemove(Player player){
        player.removePotionEffect(PotionEffectType.CONFUSION);
    // create a new potion effect with type Speed then set the duration to the maximum integer value
    // , and the potion's "power" level to 1, and the last two booleans disable the annoying particle  effects.
}

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        this.commandExecutor = new waterPluginCommands(this);
        this.getCommand("waterreload").setExecutor(this.commandExecutor);
        createConfig();
        if(waterchanger == true)
        runExpChanger();
        else
            return;


    }

public int id;

public void runExpChanger(){
   id = new  BukkitRunnable() {
        @Override
        public void run() {
            Bukkit.getOnlinePlayers().forEach(player -> {
                if(player.getGameMode() == GameMode.SURVIVAL || player.getGameMode() == GameMode.ADVENTURE) {
                    if(player.getHealth() > 0) {
                        if (player.getExp() <= 0.01f) {
                            player.damage(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getBaseValue());
                            if (player.getHealth() == 0)
                                player.sendMessage(ChatColor.translateAlternateColorCodes('&', ChatColor.BLUE + "&lZginąłeś z powodu pragnienia!"));
                        }
                        if (player.getExp() > 0.01f) {
                            player.setExp(player.getExp() - 0.01f);
                        }
                        if(player.getExp() <= 0.15f && player.getExp() > 0.01f){
                            confusionEffectAdd(player, Integer.MAX_VALUE);

                        }
                    }
                    else return;

                }
                else return;
            });

        }
    }.runTaskTimer(this, 0L,getConfig().getLong("timetodeath") ).getTaskId();
}

    @Override
    public void onDisable() {
        // Plugin shutdown logic
    }

    public void createConfig(){
        File file = new File(getDataFolder() + File.separator + "config.yml");
        if (!file.exists())
            saveDefaultConfig();
        else {
            saveDefaultConfig();
            reloadConfig();
        }
    }

    public void runExpAddSelfCancel(Player player){
        int id = Bukkit.getScheduler().runTaskTimer(this, new Runnable() {
            public void run() {
                    player.setExp(0.8f);
            }
        }, 0, 20).getTaskId();
        Bukkit.getScheduler().runTaskLater(this, new Runnable() {
            public void run() {
                Bukkit.getScheduler().cancelTask(id);
            }
        },20);
    }




}
