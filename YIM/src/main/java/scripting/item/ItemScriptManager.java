package scripting.item;

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
import tools.FileoutputUtil;

public class ItemScriptManager {

    private static ItemScriptManager instance = new ItemScriptManager();
    private Map<String, ItemScript> scripts = new HashMap();
    private ScriptEngineFactory sef;

    private ItemScriptManager() {
        ScriptEngineManager sem = new ScriptEngineManager();
        this.sef = sem.getEngineByName("javascript").getFactory();
    }

    public static ItemScriptManager getInstance() {
        return instance;
    }

    public boolean scriptExists(String scriptName) {
        File scriptFile = new File(ServerConstants.resourcePath+"scripts/item/" + scriptName + ".js");
        return scriptFile.exists();
    }

    public void getItemScript(MapleClient c, String scriptName) {
        if (this.scripts.containsKey(scriptName)) {
            ((ItemScript) this.scripts.get(scriptName)).start(new ItemScriptMethods(c));
            return;
        }
        File scriptFile = new File(ServerConstants.resourcePath+"scripts/item/" + scriptName + ".js");
        if (!scriptFile.exists()) {
            if (c.getPlayer().isAdmin()) {
                c.getPlayer().dropMessage(5, "未找到 " + scriptName + ".js 请到服务端Item内添加");
            }
            return;
        }
        FileReader fr = null;
        ScriptEngine portal = this.sef.getScriptEngine();
        try {
            fr = new FileReader(scriptFile);
            CompiledScript compiled = ((Compilable) portal).compile(fr);
            compiled.eval();
        } catch (Exception e) {
            System.err.println("请检查Item为:(" + scriptName + ".js)的文件." + e);
            FileoutputUtil.log("log\\Script_Except.log", "请检查Item为:(" + scriptName + ".js)的文件. " + e);
        } finally {
            if (fr != null) {
                try {
                    fr.close();
                } catch (IOException e) {
                    System.err.println("ERROR CLOSING" + e);
                }
            }
        }
        ItemScript script = (ItemScript) ((Invocable) portal).getInterface(ItemScript.class);
        this.scripts.put(scriptName, script);
        script.start(new ItemScriptMethods(c));
    }

    public void clearScripts() {
        this.scripts.clear();
    }
}
