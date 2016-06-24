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
public class DataModel {
    private int dstPort;
    private int totalModel;
    private double[] sumData;
    private double[] meanData;
    private double[] deviasiData;
    private double[] quadraticData;
    
    public DataModel(int dstPort, double[] sumData, double[] meanData, double[] deviasiData, double[] quadraticData, int totalModel){
        this.dstPort = dstPort;
        this.sumData = sumData;
        this.meanData = meanData;
        this.deviasiData = deviasiData;
        this.quadraticData = quadraticData;
        this.totalModel = totalModel;
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
     * @return the totalModel
     */
    public int getTotalModel() {
        return totalModel;
    }

    /**
     * @param totalModel the totalModel to set
     */
    public void setTotalModel(int totalModel) {
        this.totalModel = totalModel;
    }

    /**
     * @return the sumData
     */
    public double[] getSumData() {
        return sumData;
    }

    /**
     * @param sumData the sumData to set
     */
    public void setSumData(double[] sumData) {
        this.sumData = sumData;
    }

    /**
     * @return the meanData
     */
    public double[] getMeanData() {
        return meanData;
    }

    /**
     * @param meanData the meanData to set
     */
    public void setMeanData(double[] meanData) {
        this.meanData = meanData;
    }

    /**
     * @return the deviasiData
     */
    public double[] getDeviasiData() {
        return deviasiData;
    }

    /**
     * @param deviasiData the deviasiData to set
     */
    public void setDeviasiData(double[] deviasiData) {
        this.deviasiData = deviasiData;
    }

    /**
     * @return the quadraticData
     */
    public double[] getQuadraticData() {
        return quadraticData;
    }

    /**
     * @param quadraticData the quadraticData to set
     */
    public void setQuadraticData(double[] quadraticData) {
        this.quadraticData = quadraticData;
    }
    
}
