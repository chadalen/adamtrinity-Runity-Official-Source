package io.battlerune.game.world.entity.mob.player;

import io.battlerune.content.Toolkit;
import io.battlerune.content.activity.impl.battlerealm.BattleRealmNode;
import io.battlerune.content.masterminer.AdventureGUI;
import io.battlerune.content.masterminer.MasterMinerData;
import io.battlerune.content.masterminer.MasterMinerGUI;
import io.battlerune.content.masterminer.MasterMinerTaskHandler;
import io.battlerune.content.skill.impl.farming.Farming;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tritonus.share.ArraySet;

import io.battlerune.Config;
import io.battlerune.content.ActivityLog;
import io.battlerune.content.ActivityLogger;
import io.battlerune.content.achievement.AchievementKey;
import io.battlerune.content.dailyachievement.DailyAchievementKey;
import io.battlerune.content.activity.Activity;
import io.battlerune.content.activity.impl.barrows.BrotherData;
import io.battlerune.content.activity.record.PlayerRecord;
import io.battlerune.content.clanchannel.channel.ClanChannel;
import io.battlerune.content.clanchannel.channel.ClanChannelHandler;
import io.battlerune.content.clanchannel.content.ClanViewer;
import io.battlerune.content.combat.Killstreak;
import io.battlerune.content.combat.Skulling;
import io.battlerune.content.dialogue.ChatBoxItemDialogue;
import io.battlerune.content.dialogue.Dialogue;
import io.battlerune.content.dialogue.DialogueFactory;
import io.battlerune.content.dialogue.OptionDialogue;
import io.battlerune.content.donators.Donation;
import io.battlerune.content.emote.EmoteUnlockable;
import io.battlerune.content.event.EventDispatcher;
import io.battlerune.content.event.impl.LogInEvent;
import io.battlerune.content.mysterybox.MysteryBoxManager;
import io.battlerune.content.pet.PetData;
import io.battlerune.content.pet.Pets;
import io.battlerune.content.preset.PresetManager;
import io.battlerune.content.prestige.Prestige;
import io.battlerune.content.quest.QuestManager;
import io.battlerune.content.skill.impl.construction.House;
import io.battlerune.content.skill.impl.magic.RunePouch;
import io.battlerune.content.skill.impl.magic.Spellbook;
import io.battlerune.content.skill.impl.magic.spell.SpellCasting;
import io.battlerune.content.skill.impl.runecrafting.RunecraftPouch;
import io.battlerune.content.skill.impl.slayer.Slayer;
import io.battlerune.content.store.impl.PersonalStore;
import io.battlerune.content.teleport.Teleport;
import io.battlerune.content.tittle.PlayerTitle;
import io.battlerune.game.event.impl.MovementEvent;
import io.battlerune.game.plugin.PluginManager;
import io.battlerune.game.task.impl.TeleblockTask;
import io.battlerune.game.world.World;
import io.battlerune.game.world.entity.EntityType;
import io.battlerune.game.world.entity.combat.Combat;
import io.battlerune.game.world.entity.combat.CombatConstants;
import io.battlerune.game.world.entity.combat.effect.AntifireDetails;
import io.battlerune.game.world.entity.combat.magic.CombatSpell;
import io.battlerune.game.world.entity.combat.ranged.RangedAmmunition;
import io.battlerune.game.world.entity.combat.ranged.RangedWeaponDefinition;
import io.battlerune.game.world.entity.combat.strategy.CombatStrategy;
import io.battlerune.game.world.entity.combat.strategy.player.special.CombatSpecial;
import io.battlerune.game.world.entity.combat.weapon.WeaponInterface;
import io.battlerune.game.world.entity.mob.Mob;
import io.battlerune.game.world.entity.mob.UpdateFlag;
import io.battlerune.game.world.entity.mob.Viewport;
import io.battlerune.game.world.entity.mob.movement.waypoint.PickupWaypoint;
import io.battlerune.game.world.entity.mob.movement.waypoint.Waypoint;
import io.battlerune.game.world.entity.mob.npc.Npc;
import io.battlerune.game.world.entity.mob.player.appearance.Appearance;
import io.battlerune.game.world.entity.mob.player.exchange.ExchangeSessionManager;
import io.battlerune.game.world.entity.mob.player.relations.ChatMessage;
import io.battlerune.game.world.entity.mob.player.relations.PlayerRelation;
import io.battlerune.game.world.entity.mob.player.requests.PlayerPunishment;
import io.battlerune.game.world.entity.mob.player.requests.RequestManager;
import io.battlerune.game.world.entity.mob.prayer.Prayer;
import io.battlerune.game.world.entity.mob.prayer.PrayerBook;
import io.battlerune.game.world.items.Item;
import io.battlerune.game.world.items.containers.bank.Bank;
import io.battlerune.game.world.items.containers.bank.BankPin;
import io.battlerune.game.world.items.containers.bank.BankVault;
import io.battlerune.game.world.items.containers.bank.DonatorDeposit;
import io.battlerune.game.world.items.containers.equipment.Equipment;
import io.battlerune.game.world.items.containers.impl.LootingBag;
import io.battlerune.game.world.items.containers.impl.LostUntradeables;
import io.battlerune.game.world.items.containers.inventory.Inventory;
import io.battlerune.game.world.items.containers.pricechecker.PriceChecker;
import io.battlerune.game.world.position.Area;
import io.battlerune.game.world.position.Position;
import io.battlerune.game.world.region.Region;
import io.battlerune.net.packet.OutgoingPacket;
import io.battlerune.net.packet.out.*;
import io.battlerune.net.session.GameSession;
import io.battlerune.util.MessageColor;
import io.battlerune.util.MutableNumber;
import io.battlerune.util.Stopwatch;
import io.battlerune.util.Utility;

import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;

/**
 * This class represents a character controlled by a player.
 *
 * @author Daniel
 * @author Michael | Chex
 */
public class Player extends Mob {

    private static final Logger logger = LogManager.getLogger();
    private int memberId = -1;
    public final Viewport viewport = new Viewport(this);
    public boolean debug;
    public Npc pet;
    private CombatSpell autocast;
    private CombatSpell singleCast;
    public Appearance appearance = Config.DEFAULT_APPEARANCE;
    public PlayerRight right = PlayerRight.PLAYER;
    public PlayerTitle playerTitle = PlayerTitle.empty();
    public Spellbook spellbook = Spellbook.MODERN;
    public ChatBoxItemDialogue chatBoxItemDialogue;
    private Optional<ChatMessage> chatMessage = Optional.empty();
    public PrayerBook quickPrayers = new PrayerBook();
    public HashSet<Prayer> unlockedPrayers = new HashSet<>();
    public RangedWeaponDefinition rangedDefinition;
    public RangedAmmunition rangedAmmo;
    private AntifireDetails antifireDetails;
    private WeaponInterface weapon = WeaponInterface.UNARMED;
    private CombatSpecial combatSpecial;
    public int[] achievedSkills = new int[7];
    public double[] achievedExp = new double[7];
    public Optional<Dialogue> dialogue = Optional.empty();
    public Optional<OptionDialogue> optionDialogue = Optional.empty();
    public Optional<Consumer<String>> enterInputListener = Optional.empty();
    public boolean[] barrowKills = new boolean[BrotherData.values().length];
    public final PlayerRelation relations = new PlayerRelation(this);
    public final Donation donation = new Donation(this);
    public final LostUntradeables lostUntradeables = new LostUntradeables(this);
    public BrotherData hiddenBrother;
    public int barrowsKillCount;
    public int sequence;
    public int playTime;
    public int sessionPlayTime;
    public int kill;
    public int death;
    public int shop;
    public int headIcon;
    public int valueIcon = -1;
    public int pestPoints;
    public int pkPoints;
    public BattleRealmNode battleRealmNode;
    public long staminaExpireTime;

    public int getpkPoints() {
        return pkPoints;
    }

    public int skillingPoints;
    public int bossPoints;
    public int triviaPoints;

    /**
     * generates a random location for the player within the last man standing lobby.
     **/
    static Random rand = new Random();
    public static final Position DEFAULT_POSITION = new Position((2972 + rand.nextInt(1)), 2784 + rand.nextInt(1));
    public static final int SkillLevel = 0;

    public int getTriviaPoints() {
        return triviaPoints;
    }

    public void SetTriviaPoints(int triviaPoints) {
        this.triviaPoints = triviaPoints;
    }

    public int getBossPoints() {
        return bossPoints;
    }

    public void setBossPoints(int bossPoints) {
        this.bossPoints = bossPoints;
    }

    public int getpestPoints() {
        return pestPoints;
    }

    public void setpestPoints(int pestPoints) {
        this.pestPoints = pestPoints;
    }


    public int getskillingPoints() {
        return skillingPoints;
    }

    public void setskillingPoints(int skillingPoints) {
        this.skillingPoints = skillingPoints;
    }

    public int getkolodionPoints() {
        return kolodionPoints;
    }

    public void setkolodionPoints(int kolodionPoints) {
        this.kolodionPoints = kolodionPoints;
    }

    /**
     * LMS
     **/
    public int lmsCoffer = 0;

    public int getLmsCoffer() {
        return lmsCoffer;
    }

    public void setLmsCoffer(int lmsCoffer) {
        this.lmsCoffer = lmsCoffer;
    }

    public double expRate;
    public int votePoints;
    public int totalVotes;
    public int kolodionPoints;
    public int ringOfRecoil = 40;
    public int profileView;
    public int ROW = 5;
    public int wilderness;
    public int runEnergy;
    public int energyRate;
    public int royalty;
    public int skillmenu;
    public int royaltyLevel = 1;
    public int skillmenuLevel = 1;
    public int glovesTier;
    public int smallPouch;
    public int mediumPouch;
    public int largePouch;
    public int giantPouch;
    public final RunecraftPouch runecraftPouch = new RunecraftPouch(this);
    public double experienceRate = Config.COMBAT_MODIFICATION;
    public long usernameLong;
    public boolean idle;
    public boolean resting;
    public boolean newPlayer;
    public boolean needsStarter;
    public boolean venged;
    private boolean specialActivated;
    public boolean warriorGuidTask;
    public boolean isBot;
    private String username = "";
    private String password = "";
    public String uuid;
    public String lastHost;
    public String created = Utility.getDate();
    public String lastClan = "";
    public ClanChannel clanChannel;
    public String clan = "";
    public String clanTag = "";
    public String clanTagColor = "";
    public final AtomicBoolean saved = new AtomicBoolean(false);
    public Stopwatch yellDelay = Stopwatch.start();
    public Stopwatch godwarsDelay = Stopwatch.start();
    public Stopwatch buttonDelay = Stopwatch.start();
    public Stopwatch itemDelay = Stopwatch.start();
    public Stopwatch foodDelay = Stopwatch.start();
    public Stopwatch takeobj = Stopwatch.start();
    public Stopwatch takeAntiFireshieldDelay = Stopwatch.start();
    public Stopwatch takeHammersDelay = Stopwatch.start();
    public Stopwatch takeLogsDelay = Stopwatch.start();
    public Stopwatch karambwanDelay = Stopwatch.start();
    public Stopwatch potionDelay = Stopwatch.start();
    public Stopwatch restoreDelay = Stopwatch.start();
    public Stopwatch diceDelay = Stopwatch.start();
    public Stopwatch BankerPetDelay = Stopwatch.start();
    public Stopwatch aggressionTimer = Stopwatch.start();
    public Stopwatch databaseRequest = Stopwatch.start();
    public Set<PetData> petInsurance = new ArraySet<>();
    public Set<PetData> lostPets = new ArraySet<>();
    public final ClanViewer clanViewer = new ClanViewer(this);
    public final PlayerRecord gameRecord = new PlayerRecord(this);
    public final ExchangeSessionManager exchangeSession = new ExchangeSessionManager(this);
    public final Map<Integer, PersonalStore> viewing_shops = new HashMap<>();
    public final PlayerAssistant playerAssistant = new PlayerAssistant(this);
    public final InterfaceManager interfaceManager = new InterfaceManager(this);
    public final RequestManager requestManager = new RequestManager(this);
    public final MysteryBoxManager mysteryBox = new MysteryBoxManager(this);
    public final Settings settings = new Settings(this);
    private final AccountSecurity security = new AccountSecurity(this);
    public final Inventory inventory = new Inventory(this);
    public final Bank bank = new Bank(this);
    public final BankVault bankVault = new BankVault(this);
    public final BankPin bankPin = new BankPin(this);
    public final RunePouch runePouch = new RunePouch(this);
    public final Killstreak killstreak = new Killstreak(this);
    public final LootingBag lootingBag = new LootingBag(this);
    public final PlayerPunishment punishment = new PlayerPunishment(this);
    public final Equipment equipment = new Equipment(this);
    public final PresetManager presetManager = new PresetManager(this);
    public final Prestige prestige = new Prestige(this);
    public final PriceChecker priceChecker = new PriceChecker(this);
    public final DonatorDeposit donatorDeposit = new DonatorDeposit(this);
    public DialogueFactory dialogueFactory = new DialogueFactory(this);
    public final House house = new House(this);
    public Slayer slayer = new Slayer(this);
    public Skulling skulling = new Skulling(this);
    public QuestManager quest = new QuestManager(this);
    public SpellCasting spellCasting = new SpellCasting(this);
    private final Combat<Player> combat = new Combat<>(this);
    public final ActivityLogger activityLogger = new ActivityLogger(this);
    private final MutableNumber poisonImmunity = new MutableNumber();
    private final MutableNumber venomImmunity = new MutableNumber();
    private final MutableNumber specialPercentage = new MutableNumber();
    public Deque<String> lastKilled = new ArrayDeque<>(3);
    public List<EmoteUnlockable> emoteUnlockable = new LinkedList<>();
    public List<Teleport> favoriteTeleport = new ArrayList<>();
    public final Set<String> hostList = new HashSet<>();
    public MasterMinerData masterMinerData = new MasterMinerData();
    public MasterMinerTaskHandler masterMinerTask = new MasterMinerTaskHandler();
    public final MasterMinerGUI masterMiner = new MasterMinerGUI(this, masterMinerData);
    public AdventureGUI adventure = new AdventureGUI(this);
    private Farming farming = new Farming(this);
    public Toolkit toolkit = new Toolkit(this);

    public HashMap<ActivityLog, Integer> loggedActivities = new HashMap<ActivityLog, Integer>(ActivityLog.values().length) {
        {
            for (final ActivityLog achievement : ActivityLog.values())
                put(achievement, 0);
        }
    };

    public HashMap<AchievementKey, Integer> playerAchievements = new HashMap<AchievementKey, Integer>(AchievementKey.values().length) {
        {
            for (final AchievementKey achievement : AchievementKey.values())
                put(achievement, 0);
        }
    };
	
	  public HashMap<DailyAchievementKey, Integer> playerAchievements1 = new HashMap<DailyAchievementKey, Integer>(DailyAchievementKey.values().length) {
        {
            for (final DailyAchievementKey achievement : DailyAchievementKey.values())
                put(achievement, 0);
        }
    };

    public float blowpipeScales;
    public Item blowpipeDarts;

    public int tridentSeasCharges;
    public int tridentSwampCharges;

    public int serpentineHelmCharges;
    public int tanzaniteHelmCharges;
    public int magmaHelmCharges;

    public long staffOfDeadSpecial;
    private Optional<GameSession> session = Optional.empty();
    public int dragonfireCharges;
    public long dragonfireUsed;

    public Player(String username) {
        super(Config.LUMBRIDGE, false);
        this.username = username;
        this.usernameLong = Utility.nameToLong(username);
    }

    public Player(String username, Position position) {
        super(Config.DEFAULT_POSITION);
        this.setPosition(position);
        this.setUsername(username);
        this.isBot = true;
    }

    @Override
    public void setPosition(Position position) {
//        Region.removeMobOnTile(width(), getPosition());
        super.setPosition(position);
//        Region.setMobOnTile(width(), position);
    }

    public void chat(ChatMessage chatMessage) {
        if (!chatMessage.isValid()) {
            return;
        }
        this.chatMessage = Optional.of(chatMessage);
        this.updateFlags.add(UpdateFlag.CHAT);
    }

    public void setUsername(String username) {
        this.username = username;
        this.usernameLong = Utility.nameToLong(username);
    }

    public void send(OutgoingPacket packet) {
        send(packet, false);
    }

    public void send(OutgoingPacket encoder, boolean queue) {
        if (isBot) {
            return;
        }
        encoder.execute(this, queue);
    }

    private void login() {
        positionChange = true;
        regionChange = true;

        onStep();

        skills.login();

        mobAnimation.reset();

        inventory.refresh();

        equipment.login();

        settings.login();

        relations.onLogin();

        sendInitialPackets();

        playerAssistant.login();

        security.login();

        getFarming().doConfig();



        //  joinclan(Player);
    }


    /*    public static void joinclan (Player player) {


            ClanChannelHandler.connect(player, "Help");

        }*/
    private void sendInitialPackets() {
        playerAssistant.welcomeScreen();
        send(new SendRunEnergy());
        send(new SendPlayerDetails());
        send(new SendCameraReset());
        send(new SendExpCounter(skills.getExpCounter()));
        message(String.format("Welcome to %s. ", Config.SERVER_NAME + ""));
        message(String.format("There are currently %s players online.", World.getPlayerCount()));


        if (Config.DOUBLE_EXPERIENCE) {
            message("Double experience is currently active!");
        }
        if (Config.X4_EXPERIENCE) {
            //  message("X4 experience is currently active!");
        }
        
        Toolkit.TOOLS.forEach(t -> toolkit.fill(t.getId()));
    }

    private final boolean canLogout() {
        if (getCombat().inCombat()) {
            send(new SendMessage("You can not logout whilst in combat!"));
            return false;
        }

        if (!getCombat().hasPassed(CombatConstants.COMBAT_LOGOUT_COOLDOWN)) {
            send(new SendMessage("You must wait " + TimeUnit.SECONDS.convert(CombatConstants.COMBAT_LOGOUT_COOLDOWN - combat.elapsedTime(), TimeUnit.MILLISECONDS) + " seconds before you can logout as you were recently in combat."));
            return false;
        }

        if (!interfaceManager.isMainClear()) {
            send(new SendMessage("Please close what you are doing before logging out!"));
            return false;
        }

        return !Activity.evaluate(this, it -> !it.canLogout(this));
    }

    public final Set<PlayerOption> contextMenus = new HashSet<>();

    public final void logout() {
        logout(false);
    }

    public final void logout(boolean force) {
        if (!canLogout() && !force) {
            return;
        }

        send(new SendLogout());
        setVisible(false);
        World.queueLogout(this);
    }

    public void loadRegion() {
        Region[] surrounding = World.getRegions().getSurroundingRegions(getPosition());

        for (Region region : surrounding) {
            region.sendGroundItems(this);
            region.sendGameObjects(this);

            //Npc Face
            for (Npc npc : region.getNpcs(getHeight())) {
                if (!npc.getCombat().inCombat())
                    npc.face(npc.faceDirection);
            }
        }

        Activity.forActivity(this, minigame -> minigame.onRegionChange(this));

        if (debug && PlayerRight.isDeveloper(this)) {
            send(new SendMessage("[REGION] Loaded new region.", MessageColor.DEVELOPER));
        }
    }

    public void pickup(Item item, Position position) {
        Waypoint waypoint = new PickupWaypoint(this, item, position);
        if (cachedWaypoint == null || (!cachedWaypoint.isRunning() || !waypoint.equals(cachedWaypoint))) {
            resetWaypoint();
            action.clearNonWalkableActions();
            movement.reset();
            getCombat().reset();
            World.schedule(cachedWaypoint = waypoint);
        }
    }

    public void forClan(Consumer<ClanChannel> action) {
        if (clanChannel != null)
            action.accept(clanChannel);
    }

    @Override
    public void register() {
        if (!isRegistered() && !World.getPlayers().contains(this)) {
            setRegistered(World.getPlayers().add(this));
            setPosition(getPosition());

            login();

            logger.info("[REGISTERED]: " + Utility.formatName(getName()) + " [" + lastHost + "]");
            EventDispatcher.execute(this, new LogInEvent());
        }
        getFarming().load();
        getFarming().doConfig();
    }

    @Override
    public void unregister() {
        if (!isRegistered()) {
            return;
        }

        if (!World.getPlayers().contains(this)) {
            return;
        }

        send(new SendLogout());
        Activity.forActivity(this, minigame -> minigame.onLogout(this));
        relations.updateLists(false);
        house.leave();
        getFarming().save();
        Pets.onLogout(this);
        ClanChannelHandler.disconnect(this, true);
        interfaceManager.close();
        World.cancelTask(this, true);
        World.getPlayers().remove((Player) destroy());
        logger.info(String.format("[UNREGISTERED]: %s [%s]", getName(), lastHost));
    }

    @Override
    public void addToRegion(Region region) {
        region.addPlayer(this);
        aggressionTimer.reset();
    }

    @Override
    public void removeFromRegion(Region region) {
        region.removePlayer(this);
    }

    @Override
    public void sequence() {
        if (!idle) {
            playTime++;
            sessionPlayTime++;
        }
        action.sequence();
        playerAssistant.sequence();
        sequence++;
    }

    @Override
    public void onStep() {
        PluginManager.getDataBus().publish(this, new MovementEvent(getPlayer().getPosition().copy()));
        send(new SendMultiIcon(Area.inMulti(this) ? 1 : -1));

        if (brutalMode)
        {
            send(new SendPlayerOption(PlayerOption.ATTACK, true));
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
        }
        if (Area.inBattleRealm(this)) {
            send(new SendPlayerOption(PlayerOption.ATTACK, true));
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
        } // wilderness
        else if (Area.inWilderness(this)) {
            int modY = getPosition().getY() > 6400 ? getPosition().getY() - 6400 : getPosition().getY();
            wilderness = (((modY - 3521) / 8) + 1);
            send(new SendString("Level " + wilderness, 23327));
            if (interfaceManager.getWalkable() != 23400) {
                interfaceManager.openWalkable(23400);
            }

            if (!this.brutalMode) {
                send(new SendPlayerOption(PlayerOption.ATTACK, true));
                send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
            }

            // duel arena lobby
        } else if (Area.inDuelArenaLobby(this)) {
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false));
            send(new SendPlayerOption(PlayerOption.ATTACK, false, true));

            // duel arena
        } else if (Area.inDuelArena(this) || Area.inDuelObsticleArena(this)) {
            send(new SendPlayerOption(PlayerOption.ATTACK, true));
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
            if (interfaceManager.getWalkable() != 201)
                interfaceManager.openWalkable(201);

            // event arena
        } else if (Area.inEventArena(this)) {
            send(new SendPlayerOption(PlayerOption.ATTACK, true));
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
            send(new SendString("Fun PK", 23327));
            if (interfaceManager.getWalkable() != 23400)
                interfaceManager.openWalkable(23400);

            // donator
        } else if (Area.inDonatorZone(this) && !PlayerRight.isDonator(this)) {
            move(Config.DEFAULT_POSITION);
            send(new SendMessage("You're not suppose to be here! Hacker much??"));

            // clear
        } else if (!inActivity()) {
            if (!brutalMode)
            {
                send(new SendPlayerOption(PlayerOption.ATTACK, false, true));
            }
            send(new SendPlayerOption(PlayerOption.DUEL_REQUEST, false, true));
            send(new SendPlayerOption(PlayerOption.FOLLOW, false));
            send(new SendPlayerOption(PlayerOption.TRADE_REQUEST, false));
            send(new SendPlayerOption(PlayerOption.VIEW_PROFILE, false));

            if (!interfaceManager.isClear())
                interfaceManager.close();
            if (wilderness > 0)
                wilderness = 0;
        }
    }

    @Override
    public Combat<Player> getCombat() {
        return combat;
    }

    @Override
    public CombatStrategy<Player> getStrategy() {
        return playerAssistant.getStrategy();
    }

    @Override
    public void appendDeath() {
        World.schedule(new PlayerDeath(this));
    }

    @Override
    public String getName() {
        return Utility.formatName(username);
    }

    @Override
    public EntityType getType() {
        return EntityType.PLAYER;
    }

    @Override
    public boolean isValid() {
        return (isBot || session != null) && super.isValid();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        }

        if (obj == null || !(obj instanceof Player)) {
            return false;
        }

        Player other = (Player) obj;
        return Objects.equals(getIndex(), other.getIndex()) && Objects.equals(username, other.username) && Objects.equals(password, other.password) && Objects.equals(isBot, other.isBot);
    }

    @Override
    public int hashCode() {
        return Objects.hash(getIndex(), username);
    }

    @Override
    public String toString() {
        return String.format("Player[index=%d member_id=%d username=%s pos=%s bot=%b]", getIndex(), getMemberId(), getUsername(), getPosition(), isBot);
    }

    public void message(String... messages) {
        for (String message : messages) {
            send(new SendMessage(message));
        }
    }

    public boolean isAutoRetaliate() {
        return settings.autoRetaliate;
    }

    public boolean isSpecialActivated() {
        return specialActivated;
    }

    public void setSpecialActivated(boolean activated) {
        this.specialActivated = activated;
    }

    public void setCombatSpecial(CombatSpecial special) {
        this.combatSpecial = special;
    }

    public boolean isSingleCast() {
        return singleCast != null;
    }

    CombatSpell getSingleCastSpell() {
        return singleCast;
    }

    public void setSingleCast(CombatSpell singleCast) {
        this.singleCast = singleCast;
    }

    public boolean isAutocast() {
        return autocast != null;
    }

    CombatSpell getAutocastSpell() {
        return autocast;
    }

    public void setAutocast(CombatSpell autocast) {
        this.autocast = autocast;
    }

    public MutableNumber getSpecialPercentage() {
        return specialPercentage;
    }

    public final AtomicInteger teleblockTimer = new AtomicInteger(0);

    public void teleblock(int time) {
        if (time <= 0 || (teleblockTimer.get() > 0)) {
            return;
        }

        teleblockTimer.set(time);
        send(new SendMessage("A teleblock spell has been casted on you!"));
        send(new SendWidget(SendWidget.WidgetType.TELEBLOCK, (int) ((double) teleblockTimer.get() / 1000D * 600D)));
        World.schedule(new TeleblockTask(this));
    }

    public boolean isTeleblocked() {
        return teleblockTimer.get() > 0;
    }

    public CombatSpecial getCombatSpecial() {
        return combatSpecial;
    }

    public WeaponInterface getWeapon() {
        return weapon;
    }

    public void setWeapon(WeaponInterface weapon) {
        this.weapon = weapon;
    }

    public Optional<AntifireDetails> getAntifireDetails() {
        return Optional.ofNullable(antifireDetails);
    }

    public void setAntifireDetail(AntifireDetails antifireDetails) {
        this.antifireDetails = antifireDetails;
    }

    public final MutableNumber getPoisonImmunity() {
        return poisonImmunity;
    }

    public final MutableNumber getVenomImmunity() {
        return venomImmunity;
    }

    public Optional<ChatMessage> getChatMessage() {
        return chatMessage;
    }

    public String getUsername() {
        return username;
    }

    public void setMemberId(int memberId) {
        this.memberId = memberId;
    }

    public int getMemberId() {
        return memberId;
    }

    public Optional<GameSession> getSession() {
        return session;
    }

    public void setSession(GameSession session) {
        this.session = Optional.of(session);
        this.lastHost = session.getHost();
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return password;
    }

    public Object getPreviousTeleports() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setPreviousTeleports(int[] fix) {
        // TODO Auto-generated method stub

    }

    public Object getPacketSender() {
        // TODO Auto-generated method stub
        return null;
    }

    public int getPreviousTeleportsIndex(int i) {
        // TODO Auto-generated method stub
        return 0;
    }

    public Object getTeleportInterfaceData() {
        // TODO Auto-generated method stub
        return null;
    }

    public void setTeleportInterfaceData(Object object) {
        // TODO Auto-generated method stub

    }


    public Farming getFarming() {
        return farming;
    }



}