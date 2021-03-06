package me.bw.fastcraft;

import java.io.File;

import me.bw.fastcraft.util.PlayerUtil;
import me.bw.fastcraft.util.Util;
import net.gravitydevelopment.updater.Updater;
import net.gravitydevelopment.updater.Updater.UpdateResult;
import net.gravitydevelopment.updater.Updater.UpdateType;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;


public class PluginUpdater {
	public static int taskID = -1;
	public static File file;
	
	
	public static void setup(final Plugin plugin, File pluginFile){
		if (taskID != -1){
			Bukkit.getScheduler().cancelTask(taskID);
			taskID = -1;
		}
		file = pluginFile;
		
		if (FastCraft.config.getBoolean("autoUpdate.enabled", true)){
			taskID = Bukkit.getScheduler().scheduleSyncRepeatingTask(plugin, new Runnable(){
				public void run() {
					Bukkit.getScheduler().runTaskAsynchronously(plugin, new Runnable(){
						public void run() {
							update(Bukkit.getConsoleSender());
						}
					});
				}
			}, 60 ,FastCraft.config.getInt("autoUpdate.checkInterval-Mins") * 1200);
		}
	}
	
	public static Updater update(CommandSender sender){
		Util.sendLang(sender, "outFcAdminUpdate-Checking");
		Updater updater = new Updater(FastCraft.plugin, 63587, file, UpdateType.DEFAULT, true);
		
		switch (updater.getResult()){
		case DISABLED:
			break;
		case FAIL_APIKEY:
		case FAIL_BADID:
		case FAIL_DBO:
		case FAIL_NOVERSION:
			Util.sendLang(sender, "outFcAdminUpdate-ErrFetch");
			break;
		case FAIL_DOWNLOAD:
			Util.sendLang(sender, "outFcAdminUpdate-DlErr");
			break;
		case NO_UPDATE:
			Util.sendLang(sender, "outFcAdminUpdate-None");
			break;
		case SUCCESS:
			Util.sendLang(sender, "outFcAdminUpdate-Complete");
			break;
		case UPDATE_AVAILABLE:
			Util.sendLang(sender, "outFcAdminUpdate-Found");
			break;
		}
		
		if (updater.getResult() == UpdateResult.SUCCESS){
			Bukkit.getScheduler().cancelTask(taskID);
			taskID = -1;
		}
		return updater;
	}
	
	public static void notifyPlayers(CommandSender sender){
		for (String s : FastCraft.config.getStringList("notifyPlayersOnUpdate")){
			Player p = PlayerUtil.getOnlinePlayer(s);
			if (p == null){
				FastCraft.playerPrefsConfig.set(s, true);
			}else if (p != sender){
				Util.sendLang(sender, "updateNotification");
			}
		}
	}
	
	public static void notifyOfUpdateIfNeeded(Player p){
		String key = p.getName() + ".notifyUpdate";
		if (FastCraft.config.getStringList("notifyPlayersOnUpdate").contains(p.getName()) &&
				FastCraft.playerPrefsConfig.getBoolean(key, false)){
			Util.sendLang(p, "updateNotification");
			FastCraft.playerPrefsConfig.set(key, null);
		}
	}
}