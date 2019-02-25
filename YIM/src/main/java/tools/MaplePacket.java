/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package tools;

/**
 *
 * @author lvlusi
 */
public interface MaplePacket extends java.io.Serializable {

    public byte[] getBytes();

    public Runnable getOnSend();

    public void setOnSend(Runnable onSend);
}