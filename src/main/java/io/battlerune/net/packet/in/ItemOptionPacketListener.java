package io.battlerune.net.packet.in;

import static com.google.common.base.Preconditions.checkState;
import static io.battlerune.net.packet.ClientPackets.FIRST_ITEM_OPTION;
import static io.battlerune.net.packet.ClientPackets.SECOND_ITEM_OPTION;
import static io.battlerune.net.packet.ClientPackets.THIRD_ITEM_OPTION;

import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.impl.FirstItemClickInteractionEvent;
import io.battlerune.content.event.impl.SecondItemClickInteractionEvent;
import io.battlerune.content.event.impl.ThirdItemClickInteractionEvent;
import io.battlerune.content.itemaction.ItemActionRepository;
import io.battlerune.content.mysterybox.MysteryBoxExecuter;
import io.battlerune.game.event.impl.ItemClickEvent;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.world.InterfaceConstants;
import io.battlerune.game.world.entity.mob.data.PacketType;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.game.world.items.Item;
import io.battlerune.net.codec.ByteModification;
import io.battlerune.net.codec.ByteOrder;
import io.battlerune.net.packet.GamePacket;
import io.battlerune.net.packet.PacketListener;
import io.battlerune.net.packet.PacketListenerMeta;
import io.battlerune.net.packet.out.SendMessage;

/**
 * The {@code GamePacket} responsible for clicking the actions of an {@code
 * Item}.
 *
 * @author Daniel | Obey
 */
@PacketListenerMeta({ FIRST_ITEM_OPTION, SECOND_ITEM_OPTION, THIRD_ITEM_OPTION })
public class ItemOptionPacketListener implements PacketListener {

	@Override
	public void handlePacket(Player player, GamePacket packet) {
		checkState(player != null, "Player does not exist.");

		if (player.isTeleporting() || player.isDead() || player.locking.locked(PacketType.CLICK_ITEM)) {
			return;
		}

		if (player.isDead())
			return;
		

		switch (packet.getOpcode()) {

		case FIRST_ITEM_OPTION:
			handleFirstOption(player, packet);
			break;

		case SECOND_ITEM_OPTION:
			handleSecondOption(player, packet);
			break;

		case THIRD_ITEM_OPTION:
			handleThirdOption(player, packet);
			break;

		}
	}

	private static void handleFirstOption(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int slot = packet.readShort(false, ByteModification.ADD);
		final int id = packet.readShort(ByteOrder.LE);
		
		switch (interfaceId) {
		case 57716:
			player.forClan(channel -> channel.getShowcase().select(player, id, slot));
			break;
		case InterfaceConstants.INVENTORY_INTERFACE:
			final Item item = player.inventory.get(slot);

			if (item == null || item.getId() != id) {//
			//	System.out.println("exploit appeared in firstoption itemsoption packet..");
				return;
			}

			if (EventDispatcher.execute(player, new FirstItemClickInteractionEvent(item, slot))) {
				return;
			}

			if (ItemActionRepository.inventory(player, item, 1)) {
				return;
			}

			if (MysteryBoxExecuter.execute(player, item.getId())) {
				return;
			}
			 if (player.mysteryBox.click(item)) {
                 return;
             }

			PluginManager.getDataBus().publish(player, new ItemClickEvent(1, item, slot));
			break;
		}
	}

	private static void handleSecondOption(Player player, GamePacket packet) {
		final int itemId = packet.readShort(ByteModification.ADD);
		final int slot = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int interfaceId = packet.readShort(ByteOrder.LE, ByteModification.ADD);

		switch (interfaceId) {
		case InterfaceConstants.INVENTORY_INTERFACE:
			final Item item = player.inventory.get(slot);

			if (item == null || item.getId() != itemId) {
				System.out.println("appeared in item option 2... packetlisten exploit..");
				return;
			}

			if (EventDispatcher.execute(player, new SecondItemClickInteractionEvent(item, slot))) {
				return;
			}

			if (ItemActionRepository.inventory(player, item, 2)) {
				return;
			}

			PluginManager.getDataBus().publish(player, new ItemClickEvent(2, item, slot));
			break;
		}
	}

	private static void handleThirdOption(Player player, GamePacket packet) {
		final int interfaceId = packet.readShort(ByteOrder.LE, ByteModification.ADD);
		final int slot = packet.readShort(ByteOrder.LE);
		final int itemId = packet.readShort(ByteModification.ADD);

		if (PlayerRight.isDeveloper(player))
			player.send(new SendMessage("InterfaceId="+interfaceId+" Slot="+slot+" ItemId="+itemId));
		
		switch (interfaceId) {
		case InterfaceConstants.INVENTORY_INTERFACE:
			Item item = player.inventory.get(slot);

			if (item == null || item.getId() != itemId) {
				System.out.println("appeared in items option 3... exploit");
				return;
			}
			
			if (item.getName().toLowerCase().contains("glory")) {
				player.handleGloryTeleport(player, item.getId());
				return;
			}

			if (EventDispatcher.execute(player, new ThirdItemClickInteractionEvent(item, slot))) {
				return;
			}

			if (ItemActionRepository.inventory(player, item, 3)) {
				return;
			}

			PluginManager.getDataBus().publish(player, new ItemClickEvent(3, item, slot));
			break;
		}
	}

}
