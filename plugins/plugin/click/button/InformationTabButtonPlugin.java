package plugin.click.button;

import io.battlerune.Config;
import io.battlerune.content.DropDisplay;
import io.battlerune.content.DropSimulator;
import io.battlerune.content.Skillguides.AttackApp;
import io.battlerune.content.achievement.AchievementInterface;
import io.battlerune.content.activity.ActivityType;
import io.battlerune.content.famehall.FameHandler;
import io.battlerune.content.presetInterface.PresetInterfaceHandler;
import io.battlerune.content.skill.impl.magic.teleport.TeleportType;
import io.battlerune.content.skill.impl.slayer.SlayerTab;
import io.battlerune.content.staff.PanelType;
import io.battlerune.content.staff.StaffPanel;
import io.battlerune.content.teleport.TeleportHandler;
import io.battlerune.content.tittle.TitleManager;
import io.battlerune.content.writer.InterfaceWriter;
import io.battlerune.content.writer.impl.InformationWriter;
import io.battlerune.content.writer.impl.QuestWriter;
import io.battlerune.game.plugin.PluginContext;
import io.battlerune.game.world.entity.mob.player.Player;
import io.battlerune.game.world.entity.mob.player.PlayerRight;
import io.battlerune.net.packet.out.SendURL;

public class InformationTabButtonPlugin extends PluginContext {
    
    /**
     * 
     * @author Adam_#6723
     */
     
    @Override
    protected boolean onClick(Player player, int button) {
        switch (button) {
            case 29404:
              //  if (PlayerRight.isManagement(player)) {
            	if(PlayerRight.isDeveloper(player)) {
                    StaffPanel.open(player, PanelType.INFORMATION_PANEL);
                    return true;
                }
                player.send(new SendURL("http://www.runity.io"));
                return true;
                
                case 29440:
                InterfaceWriter.write(new InformationWriter(player));
                return true;
                
            case 29421:
            case 29420:
            case 29419:
            	 player.dialogueFactory.sendOption("Hall of Fame", () -> {
                     player.dialogueFactory.onAction(() -> player.interfaceManager.open(58500));
                 }, "Royalty program", () -> {
                     player.dialogueFactory.onAction(() -> AttackApp.open(player));
                 }, "Activity Logger", () -> {
                     player.dialogueFactory.onAction(player.activityLogger::open);
                 }, "Game Records", () -> {
                     player.dialogueFactory.onAction(() -> player.gameRecord.display(ActivityType.getFirst()));
                 }, "Title Manager", () -> {
                     player.dialogueFactory.onAction(() -> TitleManager.open(player));
                     }).execute();
            	break;
            case 29429:
                PresetInterfaceHandler presets = new PresetInterfaceHandler();
                presets.open(player);
            	break;
            	/** here**/
            case 29423:
            	InterfaceWriter.write(new AchievementInterface(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_000);
            //	AchievementInterface.open(player);
            	break;    
            case 29426:
            	DropDisplay.open(player);
            	break;
            case 29432:
                TeleportHandler.open(player, TeleportType.CITIES);
            	break;
            case 29411:
            	
        
                InterfaceWriter.write(new InformationWriter(player));
                return true;
            case -30531:
            case -28219:
            case -30131:
                InterfaceWriter.write(new InformationWriter(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 29_400);
                return true;
            case 29413:
            case 29405:
            case -30528:
                InterfaceWriter.write(new QuestWriter(player));
                player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_400);
                return true;
            case 29408:
            case -30128:
            	InterfaceWriter.write(new AchievementInterface(player));
               player.interfaceManager.setSidebar(Config.QUEST_TAB, 35_000);
                return true;
            case 29410:
            	player.interfaceManager.setSidebar(Config.QUEST_TAB, 37_300);
            	break;
            case 29414:
            case -30525:
            case -30125:
                player.dialogueFactory.sendOption("Drop display", () -> {
                    player.dialogueFactory.onAction(() -> DropDisplay.open(player));
                },
                		
                		"Drop Simulator", () -> {
                    player.dialogueFactory.onAction(() -> DropSimulator.open(player));
                }, "Activity Logger", () -> {
                    player.dialogueFactory.onAction(player.activityLogger::open);
                }, "Game Records", () -> {
                    player.dialogueFactory.onAction(() -> player.gameRecord.display(ActivityType.getFirst()));
                }, "Title Manager", () -> {
                    player.dialogueFactory.onAction(() -> TitleManager.open(player));
                }).execute();
                return true;
        }
        return false;
    }
}