/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

/**
 *
 * @author Agus
 */
public class DataPacket {
    private String srcIP;
    private String dstIP;
    private int srcPort;
    private int dstPort;
<<<<<<< HEAD
    private byte[] packetData;
=======
    private double[] packetData;
>>>>>>> remotes/origin/exp
    private long packetLength;    

    /**
     * @return the dstIP
     */
    public String getDstIP() {
        return dstIP;
    }

    /**
     * @return the dstPort
     */
    public int getDstPort() {
        return dstPort;
    }

    /**
     * @return the packetData
     */
<<<<<<< HEAD
    public byte[] getPacketData() {
=======
    public double[] getPacketData() {
>>>>>>> remotes/origin/exp
        return packetData;
    }

    /**
     * @return the packetLength
     */
    public long getPacketLength() {
        return packetLength;
    }

    /**
     * @return the srcIP
     */
    public String getSrcIP() {
        return srcIP;
    }

    /**
     * @return the srcPort
     */
    public int getSrcPort() {
        return srcPort;
    }

    /**
     * @param dstIP the dstIP to set
     */
    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    /**
     * @param dstPort the dstPort to set
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * @param packetData the packetData to set
     */
<<<<<<< HEAD
    public void setPacketData(byte[] packetData) {
=======
    public void setPacketData(double[] packetData) {
>>>>>>> remotes/origin/exp
        this.packetData = packetData;
    }

    /**
     * @param packetLength the packetLength to set
     */
    public void setPacketLength(long packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * @param srcIP the srcIP to set
     */
    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    /**
     * @param srcPort the srcPort to set
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }
}
