package scripting;

import client.MapleClient;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.script.Compilable;
import javax.script.CompiledScript;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineFactory;
import javax.script.ScriptEngineManager;

import constants.ServerConstants;
import server.MaplePortal;
import tools.FileoutputUtil;

public class PortalScriptManager {

    private static final PortalScriptManager instance = new PortalScriptManager();
    private final Map<String, PortalScript> scripts = new HashMap();
    private static final ScriptEngineFactory sef = new ScriptEngineManager().getEngineByName("javascript").getFactory();

    public static PortalScriptManager getInstance() {
        return instance;
    }

    private PortalScript getPortalScript(String scriptName) {
        if (this.scripts.containsKey(scriptName)) {
            return (PortalScript) this.scripts.get(scriptName);
        }
        File scriptFile = new File(ServerConstants.resourcePath+"scripts/portal/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            return null;
        }
        FileReader fr = null;
        ScriptEngine portal = sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (Exception e) {
            System.err.println("请检查Portal为:(" + scriptName + ".js)的文件." + e);
            FileoutputUtil.log("log\\Script_Except.log", "请检查Portal为:(" + scriptName + ".js)的文件. " + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        PortalScript script = (PortalScript) ((Invocable) portal).getInterface(PortalScript.class);
        this.scripts.put(scriptName, script);
        return script;
    }

    public void executePortalScript(MaplePortal portal, MapleClient c) {
        PortalScript script = getPortalScript(portal.getScriptName());

        if (script != null) {
            try {
                script.enter(new PortalPlayerInteraction(c, portal));
                if (c.getPlayer().isAdmin()) {
                    c.getPlayer().dropMessage(5, "执行Portal为:(" + portal.getScriptName() + ".js)的文件 在地图 " + c.getPlayer().getMapId() + " - " + c.getPlayer().getMap().getMapName());
                }
            } catch (Exception e) {
                System.err.println("执行地图脚本过程中发生错误.请检查Portal为:( " + portal.getScriptName() + ".js)的文件." + e);
            }
        } else {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage(5, "未找到Portal为:(" + portal.getScriptName() + ".js)的文件 在地图 " + c.getPlayer().getMapId() + " - " + c.getPlayer().getMap().getMapName());
            }

            FileoutputUtil.log("log\\Script_Except.log", "执行地图脚本过程中发生错误.未找到Portal为:(" + portal.getScriptName() + ".js)的文件 在地图 " + c.getPlayer().getMapId() + " - " + c.getPlayer().getMap().getMapName());
        }
    }

    public void clearScripts() {
        this.scripts.clear();
    }
}
