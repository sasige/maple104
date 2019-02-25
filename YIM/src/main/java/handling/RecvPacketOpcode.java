package handling;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

public enum RecvPacketOpcode
        implements WritableIntValueHolder {

    PONG(false),
    CLIENT_HELLO(false),
    LOGIN_PASSWORD(false),
    CHARLIST_REQUEST,
    CHAR_SELECT,
    PLAYER_LOGGEDIN(false),
    CHECK_CHAR_NAME,
    CREATE_CHAR,
    DELETE_CHAR,
    CREATE_ULTIMATE,
    CLIENT_ERROR(false),
    CLIENT_ERROR1(false),
    STRANGE_DATA,
    AUTH_SECOND_PASSWORD,
    LICENSE_REQUEST,
    SET_GENDER,
    SERVERSTATUS_REQUEST,
    SERVERLIST_REQUEST,
    SEND_ENCRYPTED(false),
    REDISPLAY_SERVERLIST,
    VIEW_ALL_CHAR,
    VIEW_REGISTER_PIC,
    VIEW_SELECT_PIC,
    PICK_ALL_CHAR,
    CHAR_SELECT_NO_PIC,
    VIEW_SERVERLIST,
    RSA_KEY(false),
    CLIENT_START(false),
    CLIENT_FAILED(false),
    CHANGE_MAP,
    CHANGE_CHANNEL,
    ENTER_CASH_SHOP,
    MOVE_PLAYER,
    CANCEL_CHAIR,
    USE_CHAIR,
    CLOSE_RANGE_ATTACK,
    RANGED_ATTACK,
    MAGIC_ATTACK,
    PASSIVE_ENERGY,
    TAKE_DAMAGE,
    GENERAL_CHAT,
    CLOSE_CHALKBOARD,
    FACE_EXPRESSION,
    FACE_ANDROID,
    USE_ITEM_EFFECT,
    WHEEL_OF_FORTUNE,
    USE_TITLE_EFFECT,
    USE_UNK_EFFECT,
    NPC_TALK,
    REMOTE_STORE,
    NPC_TALK_MORE,
    NPC_SHOP,
    STORAGE,
    USE_HIRED_MERCHANT,
    MERCH_ITEM_STORE,
    DUEY_ACTION,
    MECH_CANCEL,
    OWL,
    OWL_WARP,
    ITEM_SORT,
    ITEM_GATHER,
    ITEM_MOVE,
    MOVE_BAG,
    SWITCH_BAG,
    USE_ITEM,
    CANCEL_ITEM_EFFECT,
    USE_SUMMON_BAG,
    PET_FOOD,
    USE_MOUNT_FOOD,
    USE_SCRIPTED_NPC_ITEM,
    USE_RECIPE,
    USE_NEBULITE,
    USE_ALIEN_SOCKET,
    USE_ALIEN_SOCKET_RESPONSE,
    USE_NEBULITE_FUSION,
    USE_CASH_ITEM,
    USE_CATCH_ITEM,
    USE_SKILL_BOOK,
    USE_SP_RESET,
    USE_AP_RESET,
    USE_OWL_MINERVA,
    USE_TELE_ROCK,
    USE_RETURN_SCROLL,
    USE_UPGRADE_SCROLL,
    USE_FLAG_SCROLL,
    USE_EQUIP_SCROLL,
    USE_POTENTIAL_SCROLL,
    USE_BAG,
    USE_MAGNIFY_GLASS,
    DISTRIBUTE_AP,
    AUTO_ASSIGN_AP,
    HEAL_OVER_TIME,
    TEACH_SKILL,
    DISTRIBUTE_SP,
    SPECIAL_MOVE,
    CANCEL_BUFF,
    SKILL_EFFECT,
    MESO_DROP,
    GIVE_FAME,
    CHAR_INFO_REQUEST,
    SPAWN_PET,
    PET_AUTO_BUFF,
    CANCEL_DEBUFF,
    CHANGE_MAP_SPECIAL,
    UNK0A3,
    USE_INNER_PORTAL,
    TROCK_ADD_MAP,
    LIE_DETECTOR,
    LIE_DETECTOR_SKILL,
    LIE_DETECTOR_RESPONSE,
    LIE_DETECTOR_REFRESH,
    QUEST_ACTION,
    MEDAL_QUEST_ACTION,
    SKILL_MACRO,
    REWARD_ITEM,
    ITEM_MAKER,
    REPAIR_ALL,
    REPAIR,
    SOLOMON,
    GACH_EXP,
    FOLLOW_REQUEST,
    FOLLOW_REPLY,
    AUTO_FOLLOW_REPLY,
    REPORT,
    PROFESSION_INFO,
    USE_POT,
    CLEAR_POT,
    FEED_POT,
    CURE_POT,
    REWARD_POT,
    USE_COSMETIC,
    PARTYCHAT,
    WHISPER,
    MESSENGER,
    PLAYER_INTERACTION,
    PARTY_OPERATION,
    DENY_PARTY_REQUEST,
    EXPEDITION_OPERATION,
    EXPEDITION_LISTING,
    GUILD_OPERATION,
    DENY_GUILD_REQUEST,
    ADMIN_COMMAND,
    ADMIN_LOG,
    BUDDYLIST_MODIFY,
    NOTE_ACTION,
    USE_DOOR,
    USE_MECH_DOOR,
    CHANGE_KEYMAP,
    RPS_GAME,
    ENTER_MTS,
    RING_ACTION,
    ALLIANCE_OPERATION,
    DENY_ALLIANCE_REQUEST,
    REQUEST_FAMILY,
    OPEN_FAMILY,
    FAMILY_OPERATION,
    DELETE_JUNIOR,
    DELETE_SENIOR,
    ACCEPT_FAMILY,
    USE_FAMILY,
    FAMILY_PRECEPT,
    FAMILY_SUMMON,
    CYGNUS_SUMMON,
    ARAN_COMBO,
    CRAFT_DONE,
    CRAFT_EFFECT,
    CRAFT_MAKE,
    BBS_OPERATION,
    CHANGE_MARKET_MAP,
    TRANSFORM_PLAYER,
    GAME_POLL,
    MOVE_PET,
    PET_CHAT,
    PET_COMMAND,
    PET_LOOT,
    PET_AUTO_POT,
    PET_EXCEPTION_LIST,
    MOVE_SUMMON,
    SUMMON_ATTACK,
    DAMAGE_SUMMON,
    SUB_SUMMON,
    REMOVE_SUMMON,
    MOVE_DRAGON,
    DRAGON_FLY,
    MOVE_ANDROID,
    QUICK_SLOT,
    USE_TREASUER_CHEST,
    SHIKONGJUAN,
    PAM_SONG,
    MOVE_LIFE,
    AUTO_AGGRO,
    FRIENDLY_DAMAGE,
    MONSTER_BOMB,
    HYPNOTIZE_DMG,
    MOB_BOMB,
    MOB_NODE,
    DISPLAY_NODE,
    NPC_ACTION,
    ITEM_PICKUP,
    DAMAGE_REACTOR,
    TOUCH_REACTOR,
    MAKE_EXTRACTOR,
    SNOWBALL,
    LEFT_KNOCK_BACK,
    COCONUT,
    MONSTER_CARNIVAL,
    SHIP_OBJECT,
    PLAYER_UPDATE,
    PARTY_SEARCH_START,
    PARTY_SEARCH_STOP,
    START_HARVEST,
    STOP_HARVEST,
    QUICK_MOVE,
    CS_UPDATE,
    BUY_CS_ITEM,
    COUPON_CODE,
    SEND_CS_GIFI,
    SEND_CS_HOT,
    MAPLETV,
    UPDATE_QUEST,
    QUEST_ITEM,
    USE_ITEM_QUEST,
    TOUCHING_MTS,
    MTS_TAB,
    CHANGE_SET,
    GET_BOOK_INFO,
    REISSUE_MEDAL,
    CLICK_REACTOR,
    USE_FAMILIAR,
    SPAWN_FAMILIAR,
    RENAME_FAMILIAR,
    MOVE_FAMILIAR,
    TOUCH_FAMILIAR,
    ATTACK_FAMILIAR,
    SIDEKICK_OPERATION,
    DENY_SIDEKICK_REQUEST,
    ALLOW_PARTY_INVITE,
    PHANTOM_EQUIP_RECV,//幻影偷技能系列
    PHANTOM_VIEW_RECV,//幻影偷技能系列
    PHANTOM_SKILL_RECV,//幻影偷技能系列
    PVP_INFO,
    ENTER_PVP,
    ENTER_PVP_PARTY,
    LEAVE_PVP,
    PVP_RESPAWN,
    PVP_ATTACK,
    PVP_SUMMON,
    USE_HAMMER;
    private short code = -2;
    private boolean CheckState;

    @Override
    public void setValue(short code) {
        this.code = code;
    }

    @Override
    public short getValue() {
        return this.code;
    }

    private RecvPacketOpcode() {
        this.CheckState = true;
    }

    private RecvPacketOpcode(boolean CheckState) {
        this.CheckState = CheckState;
    }

    public boolean NeedsChecking() {
        return this.CheckState;
    }

    public static Properties getDefaultProperties() throws FileNotFoundException, IOException {
        Properties props = new Properties();
        File file = new File("recvops.properties");
        if(file.exists()){
            FileInputStream fileInputStream = new FileInputStream("recvops.properties");
            props.load(fileInputStream);
            fileInputStream.close();
        }
        return props;
    }

    public static void reloadValues() {
        boolean leibu = true;
        try {
            if (leibu) {
                Properties props = new Properties();
                props.load(RecvPacketOpcode.class.getClassLoader().getResourceAsStream("recvops.properties"));
                ExternalCodeTableGetter.populateValues(props, values());
            } else {
                ExternalCodeTableGetter.populateValues(getDefaultProperties(), values());
            }
        } catch (IOException e) {
            throw new RuntimeException("Failed to load recvops", e);
        }
    }

    static {
        reloadValues();
    }
}