package org.golde.bukkit.corpsereborn.listeners;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.golde.bukkit.corpsereborn.ConfigData;
import org.golde.bukkit.corpsereborn.Main;
import org.golde.bukkit.corpsereborn.PlayerInventoryClone;
import org.golde.bukkit.corpsereborn.Util;
import org.golde.bukkit.corpsereborn.CorpseAPI.events.CorpseSpawnEvent;
import org.golde.bukkit.corpsereborn.dump.ReportError;
import org.golde.bukkit.corpsereborn.nms.Corpses.CorpseData;

public class PlayerDeath implements Listener {

	@EventHandler(priority=EventPriority.HIGHEST, ignoreCancelled = true)
	public void onPlayerDeath(PlayerDeathEvent e) {
		try{
			if (ConfigData.isOnDeath() && Util.playerInCorrectWorld(e.getEntity())) {
				CorpseData data;
				PlayerInventoryClone inv = new PlayerInventoryClone(e.getEntity(), e.getDrops());
				
				data = Main.getPlugin().corpses.spawnCorpse(e.getEntity(), null, e.getEntity().getLocation(), inv.toInventory(), 0).setSelectedSlot(e.getEntity().getInventory().getHeldItemSlot());
				
				CorpseSpawnEvent cse = new CorpseSpawnEvent(data, false);
				Util.callEvent(cse);
				if(cse.isCancelled()){
					Main.getPlugin().corpses.removeCorpse(data);
				}else{
					if (ConfigData.hasLootingInventory()) {		
						e.getDrops().clear();
					}
				}
			}
			
			// For each corpse, remove player from view of that corpse.
			//in therory fixing a bug. Realy not sure
			for (CorpseData cd:Main.getPlugin().corpses.getAllCorpses()) {
				cd.removeFromMap(e.getEntity());
			}
		}catch(Exception ex){
			new ReportError(ex);
		}
	}
}
