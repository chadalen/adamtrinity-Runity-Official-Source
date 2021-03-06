package io.battlerune.net.packet.out;

import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.OutgoingPacket;

public class SendInterfaceConfig extends OutgoingPacket {

	private final int main;
	private final int sub1;
	private final int sub2;

	public SendInterfaceConfig(int main, int sub1, int sub2) {
		super(246, 6);
		this.main = main;
		this.sub1 = sub1;
		this.sub2 = sub2;
	}

	@Override
	public boolean encode(Player player) {
		builder.writeShort(main, ByteOrder.LE).writeShort(sub1).writeShort(sub2);
		return true;
	}

}
