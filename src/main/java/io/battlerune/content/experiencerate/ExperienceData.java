package io.battlerune.content.experiencerate;

import io.battlerune.game.world.entity.mob.player.PlayerRight;

/***
 * Experience rate modifier
 * 
 * @author Nerik#8690
 *
 */
public enum ExperienceData {

	NORMAL(PlayerRight.PLAYER, 50.0), HELPER(PlayerRight.HELPER, 50.0), MODERATOR(PlayerRight.MODERATOR, 50.0),
	ADMINISTRATOR(PlayerRight.ADMINISTRATOR, 50.0), DEVELOPER(PlayerRight.DEVELOPER, 50.0), OWNER(PlayerRight.OWNER, 50.0),

	DONATOR(PlayerRight.DONATOR, 52.0), SUPER_DONATOR(PlayerRight.SUPER_DONATOR, 54.0),
	EXTREME_DONATOR(PlayerRight.EXTREME_DONATOR, 56.0), ELITE_DONATOR(PlayerRight.ELITE_DONATOR, 58.0),
	KING_DONATOR(PlayerRight.KING_DONATOR, 60.0), SUPREME_DONATOR(PlayerRight.SUPREME_DONATOR, 62.0),
	YOUTUBER(PlayerRight.YOUTUBER, 64.0), GRAPHIC(PlayerRight.GRAPHIC, 66.0),

	IRONMAN(PlayerRight.IRONMAN, 10.0), ULTIMATE_IRONMAN(PlayerRight.ULTIMATE_IRONMAN, 5.0),
	HARDCORE_IRONMAN(PlayerRight.HARDCORE_IRONMAN, 2.5), CLASSIC(PlayerRight.CLASSIC, 2.0),

	;

	private PlayerRight mode;
	private double modifier;

	ExperienceData(PlayerRight mode, double modifier) {
		this.mode = mode;
		this.modifier = modifier;
	}

	public PlayerRight getMode() {
		return mode;
	}

	public double getModifier() {
		return modifier;
	}
}
