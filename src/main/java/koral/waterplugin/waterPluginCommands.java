package koral.waterplugin;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitScheduler;

public class waterPluginCommands implements CommandExecutor {
    WaterPlugin plugin;
    public waterPluginCommands(final WaterPlugin plugin){
        this.plugin = plugin;
    }
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args){
            Player player = (Player) sender;
            if(cmd.getName().equalsIgnoreCase("waterreload")){
                this.plugin.saveDefaultConfig();
                this.plugin.reloadConfig();
                Bukkit.getScheduler().cancelTask(plugin.id);
                plugin.runExpChanger();
                player.sendMessage(ChatColor.GREEN + "Config zostal przeladowany");
            }

        return true;
    }


}
