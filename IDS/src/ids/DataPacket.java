/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

/**
 *
 * @author agus
 */
public class DataPacket {
    private String protokol;
    private String srcIP;
    private String dstIP;
    private int srcPort;
    private int dstPort;
    private byte[] packetData;
    private double[] ngram;
    private int packetType;
    
    public DataPacket(String protokol, String srcIP, int srcPort, String dstIP, int dstPort, byte[] packetData, double[] ngram){
        this.protokol = protokol;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.dstIP = dstIP;
        this.dstPort = dstPort;
        this.packetData = packetData;
        this.ngram = ngram;
    }    

    /**
     * @return the protokol
     */
    public String getProtokol() {
        return protokol;
    }

    /**
     * @param protokol the protokol to set
     */
    public void setProtokol(String protokol) {
        this.protokol = protokol;
    }

    /**
     * @return the srcIP
     */
    public String getSrcIP() {
        return srcIP;
    }

    /**
     * @param srcIP the srcIP to set
     */
    public void setSrcIP(String srcIP) {
        this.srcIP = srcIP;
    }

    /**
     * @return the dstIP
     */
    public String getDstIP() {
        return dstIP;
    }

    /**
     * @param dstIP the dstIP to set
     */
    public void setDstIP(String dstIP) {
        this.dstIP = dstIP;
    }

    /**
     * @return the srcPort
     */
    public int getSrcPort() {
        return srcPort;
    }

    /**
     * @param srcPort the srcPort to set
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    /**
     * @return the dstPort
     */
    public int getDstPort() {
        return dstPort;
    }

    /**
     * @param dstPort the dstPort to set
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * @return the packetData
     */
    public byte[] getPacketData() {
        return packetData;
    }

    /**
     * @param packetData the packetData to set
     */
    public void setPacketData(byte[] packetData) {
        this.packetData = packetData;
    }

    /**
     * @return the ngram
     */
    public double[] getNgram() {
        return ngram;
    }

    /**
     * @param ngram the ngram to set
     */
    public void setNgram(double[] ngram) {
        this.ngram = ngram;
    }

    /**
     * @return the packetType
     */
    public int getPacketType() {
        return packetType;
    }

    /**
     * @param packetType the packetType to set
     */
    public void setPacketType(int packetType) {
        this.packetType = packetType;
    }
}
