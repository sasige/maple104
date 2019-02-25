package scripting;

import client.MapleClient;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import javax.script.Invocable;
import javax.script.ScriptEngine;
import javax.script.ScriptEngineManager;
import javax.script.ScriptException;
import tools.FileoutputUtil;

public abstract class AbstractScriptManager {

    private static final ScriptEngineManager sem = new ScriptEngineManager();

    protected Invocable getInvocable(String path, MapleClient c) {
        return getInvocable(path, c, false);
    }

    protected Invocable getInvocable(String path, MapleClient c, boolean npc) {
        FileReader fr = null;
        try {
            //path = "scripts/" + path;
            ScriptEngine engine = null;

            if (c != null) {
                engine = c.getScriptEngine(path);
            }
            if (engine == null) {
                File  scriptFile = new File(path);
                if (!scriptFile.exists()) {

                    return null;
                }
                engine = sem.getEngineByName("javascript");
                if (c != null) {
                    c.setScriptEngine(path, engine);
                }
                fr = new FileReader(scriptFile);
                engine.eval(fr);
            } else if ((c != null) && (npc)) {
                c.getPlayer().dropMessage(-1, "您当前已经和1个NPC对话了. 如果不是请输入 @ea 命令进行解卡。");
            }
            return (Invocable) engine;
        } catch (FileNotFoundException | ScriptException e) {
            System.err.println("Error executing script. Path: " + path + "\nException " + e);
            FileoutputUtil.log("log\\Script_Except.log", "Error executing script. Path: " + path + "\nException " + e);
            return null;
        } finally {
            try {
                if (fr != null) {
                    fr.close();
                }
            } catch (IOException ignore) {
            }
        }
    }
}