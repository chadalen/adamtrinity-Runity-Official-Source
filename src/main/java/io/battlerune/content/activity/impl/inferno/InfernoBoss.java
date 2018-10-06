package io.battlerune.content.activity.impl.inferno;

import io.battlerune.game.Animation;
import io.battlerune.game.task.Task;
import io.battlerune.game.task.TaskManager;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.object.CustomGameObject;
import io.battlerune.game.world.position.Position;

public class InfernoBoss extends Task {
	
	
	private int tick;

	public InfernoBoss() {
		super(1);
		this.tick = 0;
		// TODO Auto-generated constructor stub
	}

	public void spawnbosswave(Player player) {

		Npc mager = new Npc(7703, new Position(2264, 5351, 0));
		Npc ranger = new Npc(7702, new Position(2271, 5350, 0));
		Npc Jad = new Npc(7700, new Position(2269, 5345, 0));
		Npc Healer = new Npc(7750, new Position(2276, 5363, 0));
		Npc Healer1 = new Npc(7750, new Position(2280, 5363, 0));
		Npc Healer2 = new Npc(7750, new Position(2266, 5363, 0));
		Npc Healer3 = new Npc(7750, new Position(2262, 5363, 0));
		Npc Glyph = new Npc(7707, new Position(2270, 5363, 0));
		Npc TzKalZuk = new Npc(7706, new Position(2268, 5362, 0));

		player.sendMessage("@red@ Inferno Boss Wave has begun!");
		World.sendMessage("@red@" + player.getName() + " has just Started the Inferno!!");
		TaskManager.schedule(new Task(1) {

		@Override
		protected void execute() {
		
	 switch(tick) {
	 case 0:
		     Glyph.register();
			 Glyph.face(new Position(2271, 1));
		 break;
	 case 1:
            TzKalZuk.register();
			TzKalZuk.animate(new Animation(7563));
			TzKalZuk.face(new Position(2271, 1));
			break;
	 case 2:

		 Glyph.walkExactlyTo(new Position(2257, 5361, 0), () -> {
			System.err.println("WALKING GLYPH TO SET POSITION!"); 
		 });

			// for(int i = 0; i > Glyph.getCurrentHealth(); i++) {

		 //}
		 break;
	 case 3:
		 mager.register();
		 ranger.register();
		 if(Glyph.getCurrentHealth() <= 10) {
         mager.attack(Glyph);
         ranger.attack(Glyph);
		 } else {
			 mager.attack(player);
			 ranger.attack(player);
		 }
		 break;		 
	  }
	 tick++;
	 }
  });
}

	
	protected void execute() {
		// TODO Auto-generated method stub
		
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
}
