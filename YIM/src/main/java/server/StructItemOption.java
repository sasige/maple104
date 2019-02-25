package server;

import java.util.HashMap;
import java.util.Map;

public class StructItemOption {

    public static String[] types = {"incSTR", "incDEX", "incINT", "incLUK", "incACC", "incEVA", "incSpeed", "incJump", "incPAD", "incMAD", "incPDD", "incMDD", "prop", "time", "incSTRr", "incDEXr", "incINTr", "incLUKr", "incMHPr", "incMMPr", "incACCr", "incEVAr", "incPADr", "incMADr", "incPDDr", "incMDDr", "incCr", "incDAMr", "RecoveryHP", "RecoveryMP", "HP", "MP", "level", "ignoreTargetDEF", "ignoreDAM", "incAllskill", "ignoreDAMr", "RecoveryUP", "incCriticaldamageMin", "incCriticaldamageMax", "incTerR", "incAsrR", "DAMreflect", "mpconReduce", "reduceCooltime", "incMesoProp", "incRewardProp", "boss", "incMHP", "incMMP", "attackType"};
    public int optionType;
    public int reqLevel;
    public int opID;
    public String face;
    public Map<String, Integer> data = new HashMap();

    public int get(String type) {
        return this.data.get(type) != null ? ((Integer) this.data.get(type)).intValue() : 0;
    }

    @Override
    public String toString() {
        return "Under Construction";
    }
}