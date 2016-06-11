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
    private String proto;
    private String srcIP;
    private String dstIP;
    private String tuples;
    private int srcPort;
    private int dstPort;
    private byte[] packetData;
    private double[] ngram;
    private long packetLength; 
    private double mean;
    private int type;
    
    public DataPacket(String proto, String srcIP, int srcPort, String dstIP, int dstPort, double[] ngram, int type){
        this.proto = proto;
        this.srcIP = srcIP;
        this.srcPort = srcPort;
        this.dstIP = dstIP;
        this.dstPort = dstPort;
        this.ngram = ngram;
        this.type = type;
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
     * @return the packetLength
     */
    public long getPacketLength() {
        return packetLength;
    }

    /**
     * @param packetLength the packetLength to set
     */
    public void setPacketLength(long packetLength) {
        this.packetLength = packetLength;
    }

    /**
     * @return the tuples
     */
    public String getProto() {
        return proto;
    }

    /**
     * @param tuples the tuples to set
     */
    public void setProto(String tuples) {
        this.proto = tuples;
    }

    /**
     * @return the mean
     */
    public double getMean() {
        return mean;
    }

    /**
     * @param mean the mean to set
     */
    public void setMean(double mean) {
        this.mean = mean;
    }

    /**
     * @return the tuples
     */
    public String getTuples() {
        return tuples;
    }

    /**
     * @param tuples the tuples to set
     */
    public void setTuples(String tuples) {
        this.tuples = tuples;
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
     * @return the type
     */
    public int getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(int type) {
        this.type = type;
    }
    
}
