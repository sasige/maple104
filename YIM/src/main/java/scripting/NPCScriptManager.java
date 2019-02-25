package scripting;

import client.MapleClient;

import java.util.Map;
import java.util.WeakHashMap;
import java.util.concurrent.locks.Lock;
import javax.script.Invocable;
import javax.script.ScriptEngine;

import constants.ServerConstants;
import server.quest.MapleQuest;
import tools.FileoutputUtil;

public class NPCScriptManager extends AbstractScriptManager {

    private final Map<MapleClient, NPCConversationManager> cms = new WeakHashMap();
    private static final NPCScriptManager instance = new NPCScriptManager();
    private static String resourcePath = System.getProperty("resourcePath") + "/";

    public static NPCScriptManager getInstance() {
        return instance;
    }

    public void start(MapleClient c, int npc) {
        start(c, npc, 0);
    }

    public void start(MapleClient c, int npc, int npcMode) {
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if ((!this.cms.containsKey(c)) && (c.canClickNPC())) {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "您已经建立与NPC ID: " + npc + "   型号：" + npcMode + " 的对话。");
                }
                Invocable iv;
                if (npcMode == 0) {
                    iv = getInvocable(ServerConstants.resourcePath + "scripts/npc/" + npc + ".js", c, true);
                } else {
                    iv = getInvocable(ServerConstants.resourcePath + "scripts/npc/" + npc + "_" + npcMode + ".js", c, true);
                }
                ScriptEngine scriptengine = (ScriptEngine) iv;
                NPCConversationManager cm = new NPCConversationManager(c, npc, -1, -1, (byte) -1, npcMode, iv);
                if ((iv == null) || (getInstance() == null)) {
                    if (iv == null) {
                        cm.sendOk("对不起，我并没有被管理员设置可使用，如果您觉得我应该工作的，那就请您汇报给管理员.\r\n我的ID编号: #r" + npc + "#k 型号：" + npcMode + ".");
                    }
                    dispose(c);
                    return;
                }
                this.cms.put(c, cm);
                scriptengine.put("cm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start", new Object[0]);
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                dispose(c);
            }
        } catch (Exception e) {
            System.err.println("执行NPC脚本出错 NPC ID : " + npc + " 模式: " + npcMode + " 错误信息: " + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行NPC脚本出错 NPC ID : " + npc + " 模式: " + npcMode + ".\r\n错误信息: " + e);
            dispose(c);
            notice(c, npc);
        } finally {
            lock.unlock();
        }
    }

    public void action(MapleClient c, byte mode, byte type, int selection) {
        if (mode != -1) {
            NPCConversationManager cm = (NPCConversationManager) this.cms.get(c);
            if ((cm == null) || (cm.getLastMsg() > -1)) {
                return;
            }
            Lock lock = c.getNPCLock();
            lock.lock();
            try {
                if (cm.pendingDisposal) {
                    dispose(c);
                } else {
                    c.setClickedNPC();
                    cm.getIv().invokeFunction("action", new Object[]{Byte.valueOf(mode), Byte.valueOf(type), Integer.valueOf(selection)});
                }
            } catch (Exception e) {
                System.err.println("执行NPC脚本出错 NPC ID : " + cm.getNpc() + " 模式: " + cm.getNpcMode() + " 错误信息: " + e);
                FileoutputUtil.log("log\\Script_Except.log", "执行NPC脚本出错 NPC ID : " + cm.getNpc() + " 模式: " + cm.getNpcMode() + ". \r\n错误信息: " + e);
                dispose(c);
                notice(c, getCM(c).getNpc());
            } finally {
                lock.unlock();
            }
        }
    }

    public void startQuest(MapleClient c, int npc, int quest) {
        if (c.getPlayer().isInJailMap()) {
            c.getPlayer().dropMessage(1, "在这个地方无法进行任务操作.");
            return;
        }
        if (!MapleQuest.getInstance(quest).canStart(c.getPlayer(), null)) {
            if (c.getPlayer().isAdmin()) ;
            return;
        }
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if ((!this.cms.containsKey(c)) && (c.canClickNPC())) {
                Invocable iv = getInvocable(ServerConstants.resourcePath +"quest/" + quest + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isAdmin()) {
                        c.getPlayer().dropMessage(5, "任务脚本不存在 NPC：" + npc + " Quest：" + quest);
                    }
                    dispose(c);
                    return;
                }
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "开始脚本任务 NPC：" + npc + " Quest：" + quest);
                }
                ScriptEngine scriptengine = (ScriptEngine) iv;
                NPCConversationManager cm = new NPCConversationManager(c, npc, quest, -1, (byte) 0, 0, iv);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();

                iv.invokeFunction("start", (byte) 1, (byte) 0, 0);
            } else {
                dispose(c);
            }
        } catch (Exception e) {
            System.err.println("执行任务脚本失败 任务ID: (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行任务脚本失败 任务ID: (" + quest + ")..NPCID: " + npc + ". \r\n错误信息: " + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void startQuest(MapleClient c, byte mode, byte type, int selection) {
        Lock lock = c.getNPCLock();
        NPCConversationManager cm = (NPCConversationManager) this.cms.get(c);
        if ((cm == null) || (cm.getLastMsg() > -1)) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("start", new Object[]{Byte.valueOf(mode), Byte.valueOf(type), Integer.valueOf(selection)});
            }
        } catch (Exception e) {
            System.err.println("执行任务脚本失败 任务ID: (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行任务脚本失败 任务ID: (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ". \r\n错误信息: " + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void endQuest(MapleClient c, int npc, int quest, boolean customEnd) {
        if (c.getPlayer().isInJailMap()) {
            c.getPlayer().dropMessage(1, "在这个地方无法进行任务操作.");
            return;
        }
        if ((!customEnd) && (!MapleQuest.getInstance(quest).canComplete(c.getPlayer(), null))) {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage(6, "不能完成这个任务 NPC：" + npc + " Quest：" + quest);
            }
            return;
        }
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if ((!this.cms.containsKey(c)) && (c.canClickNPC())) {
                Invocable iv = getInvocable(ServerConstants.resourcePath +"quest/" + quest + ".js", c, true);
                if (iv == null) {
                    if (c.getPlayer().isAdmin()) {
                        c.getPlayer().dropMessage(5, "完成任务脚本不存在 NPC：" + npc + " Quest：" + quest);
                    }
                    dispose(c);
                    return;
                }
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "完成脚本任务 NPC：" + npc + " Quest：" + quest);
                }
                ScriptEngine scriptengine = (ScriptEngine) iv;
                NPCConversationManager cm = new NPCConversationManager(c, npc, quest, -1, (byte) 1, 0, iv);
                this.cms.put(c, cm);
                scriptengine.put("qm", cm);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();

                iv.invokeFunction("end", (byte) 1, (byte) 0, 0);
            } else {
                dispose(c);
            }
        } catch (Exception e) {
            System.err.println("执行任务脚本失败 任务ID: (" + quest + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行任务脚本失败 任务ID: (" + quest + ")..NPCID: " + npc + ". \r\n错误信息: " + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void endQuest(MapleClient c, byte mode, byte type, int selection) {
        Lock lock = c.getNPCLock();
        NPCConversationManager cm = (NPCConversationManager) this.cms.get(c);
        if ((cm == null) || (cm.getLastMsg() > -1)) {
            return;
        }
        lock.lock();
        try {
            if (cm.pendingDisposal) {
                dispose(c);
            } else {
                c.setClickedNPC();
                cm.getIv().invokeFunction("end", new Object[]{Byte.valueOf(mode), Byte.valueOf(type), Integer.valueOf(selection)});
            }
        } catch (Exception e) {
            System.err.println("完成任务脚本失败 任务ID (" + cm.getQuest() + ")...NPC: " + cm.getNpc() + ":" + e);
            FileoutputUtil.log("log\\Script_Except.log", "完成任务脚本失败 任务ID (" + cm.getQuest() + ")..NPCID: " + cm.getNpc() + ". \r\n错误信息: " + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void startItem(MapleClient c, int npc, int itemid) {
        Lock lock = c.getNPCLock();
        lock.lock();
        try {
            if ((!this.cms.containsKey(c)) && (c.canClickNPC())) {
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "开始道具脚本 NPC：" + npc + " Itemid：" + itemid);
                }
                Invocable iv = getInvocable(ServerConstants.resourcePath +"item/" + itemid + ".js", c, true);
                ScriptEngine scriptengine = (ScriptEngine) iv;
                NPCConversationManager im = new NPCConversationManager(c, npc, -1, itemid, (byte) -1, 0, iv);
                if ((iv == null) || (getInstance() == null)) {
                    if (iv == null) {
                        im.sendOk("对不起，我并没有被管理员设置可使用，如果您觉得我应该工作的，那就请您汇报给管理员.\r\n我的信息: #b#i" + itemid + ":##t" + itemid + "##k ID: " + itemid);
                    }
                    dispose(c);
                    return;
                }
                this.cms.put(c, im);
                scriptengine.put("im", im);

                c.getPlayer().setConversation(1);
                c.setClickedNPC();
                try {
                    iv.invokeFunction("start", new Object[0]);
                } catch (NoSuchMethodException nsme) {
                    iv.invokeFunction("action", (byte) 1, (byte) 0, 0);
                }
            } else {
                dispose(c);
            }
        } catch (Exception e) {
            System.err.println("执行道具脚本失败 道具ID: (" + itemid + ")..NPCID: " + npc + ":" + e);
            FileoutputUtil.log("log\\Script_Except.log", "执行道具脚本失败 道具ID: (" + itemid + ")..NPCID: " + npc + ". \r\n错误信息: " + e);
            dispose(c);
        } finally {
            lock.unlock();
        }
    }

    public void dispose(MapleClient c) {
        NPCConversationManager npccm = (NPCConversationManager) this.cms.get(c);
        if (npccm != null) {
            this.cms.remove(c);
            if (npccm.getType() == -1) {
                if (npccm.getNpcMode() == 0) {
                    c.removeScriptEngine(ServerConstants.resourcePath +"scripts/npc/" + npccm.getNpc() + ".js");
                } else {
                    c.removeScriptEngine(ServerConstants.resourcePath +"scripts/npc/" + npccm.getNpc() + "_" + npccm.getNpcMode() + ".js");
                }
            } else {
                c.removeScriptEngine(ServerConstants.resourcePath +"scripts/quest/" + npccm.getQuest() + ".js");
            }
            if (npccm.getItem() > 0) {
                c.removeScriptEngine(ServerConstants.resourcePath +"scripts/item/" + npccm.getItem() + ".js");
            }
        }
        if ((c.getPlayer() != null) && (c.getPlayer().getConversation() == 1)) {
            c.getPlayer().setConversation(0);
        }
    }

    public NPCConversationManager getCM(MapleClient c) {
        return (NPCConversationManager) this.cms.get(c);
    }

    private void notice(MapleClient c, int id) {
        c.getPlayer().dropMessage(1, "这个NPC脚本是错误的，请联系管理员修复它.NPCID: " + id);
    }
}