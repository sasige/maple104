/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package client;

import java.io.Serializable;

/**
 *
 * @author Mary
 */
public class attactset implements Serializable  {

    private static final long serialVersionUID = 9179541993413738569L;
    public int minmapid;
    public int maxmapid;
    public int attack;

    public attactset(int attack, int minmapid, int maxmapid) {
        this.minmapid = minmapid;
        this.maxmapid = maxmapid;
        this.attack = attack;
    }

}
