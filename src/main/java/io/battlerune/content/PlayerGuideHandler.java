package io.battlerune.content;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.packet.out.SendString;

/**
 * 
 * @author Adam_#6723
 *
 */

public class PlayerGuideHandler {
	
	/**
	 * Main Method, anything that is needed to appear on the interface should be called on here.
	 * @param player
	 */
         public void open(Player player) {
             player.inventory.refresh();
             player.equipment.refresh();
             sendGuideNames(player);
        	 player.interfaceManager.open(56000);
        	 
         }
	
    /**
     * Send's the strings to the interface.
     * @param player
     */
    public void sendGuideNames(Player player) {
        player.send(new SendString("Economy Info", 56051, true));
        player.send(new SendString("Money making", 56052, true));
        player.send(new SendString("Becoming meradj", 56053, true));

    }
    
}
