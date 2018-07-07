package plugin.command.impl.player;

import io.battlerune.content.command.Command;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.position.Area;

public class DonatorBankCommand implements Command {

	@Override
	public void execute(Player player, String[] command) {
		if(Area.inWilderness(player) && (!PlayerRight.isDeveloper(player))) {
    		player.message("You cannot open the bank in the wilderness.");
    	} else {
        player.bank.open();
    	}
    }
	

	@Override
	public boolean canUse(Player player) {
		if(!PlayerRight.isDonator(player)) {
			player.speak("Hey everyone, I just tried to do something silly!");
			return false;
		} else {
		return true;
	}

}
	
}