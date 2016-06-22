/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class DataTraining implements Runnable {    
    private int ascii = 256, port;
    private String proto;
    private double[] sumData = new double[ascii], meanData = new double[ascii], deviasiData = new double[ascii], quadraticData = new double[ascii], standardData = new double[ascii];
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;  
    private ArrayList<Double[]> dataTraining = new ArrayList<>();
    private ArrayList<DataModel> modelTcp;
    private ArrayList<DataModel> modelUdp;
    
    public DataTraining(String proto, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, ArrayList<DataModel> modelTcp, ArrayList<DataModel> modelUdp, int port){
        this.proto = proto;
        this.datasetTcp = datasetTcp;
        this.datasetUdp = datasetUdp;
        this.modelTcp = modelTcp;
        this.modelUdp = modelUdp;
        this.port = port;
    }
    
    public void run(){
        synchronized(dataTraining){
            if (proto.equals("TCP")) {
                for (DataPacket dataSetTcp : datasetTcp) {
                    if (dataSetTcp.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetTcp.getNgram()));
                    }                                    
                }               

                for (int i = 0; i < dataTraining.size(); i++) {
                    for (int j = 0; j < ascii; j++) {
                        sumData[j] += dataTraining.get(i)[j];
                        quadraticData[j] += Math.pow(dataTraining.get(i)[j], 2);
                    }                                                                        
                }

                for (int i = 0; i < meanData.length; i++) {
                    meanData[i] = sumData[i]/dataTraining.size();
                }

                for (int i = 0; i < deviasiData.length; i++) {
                    deviasiData[i] = Math.sqrt((dataTraining.size()*quadraticData[i]-Math.pow(sumData[i], 2))/(dataTraining.size()*(dataTraining.size()-1)));
                }
                
                modelTcp.add(new DataModel(port, meanData, deviasiData, quadraticData, standardData, dataTraining.size()));
                dataTraining.clear();
            }

            else if (proto.equals("UDP")) {
                for (DataPacket dataSetUdp : datasetUdp) {
                    if (dataSetUdp.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetUdp.getNgram()));                                        
                    }                                    
                }

                for (int i = 0; i < dataTraining.size(); i++) {
                    for (int j = 0; j < ascii; j++) {
                        sumData[j] += dataTraining.get(i)[j];
                        quadraticData[j] += Math.pow(dataTraining.get(i)[j], 2);
                    }                                                                        
                }

                for (int i = 0; i < meanData.length; i++) {
                    meanData[i] = sumData[i]/dataTraining.size();
                }

                for (int i = 0; i < deviasiData.length; i++) {
                    deviasiData[i] = Math.sqrt((dataTraining.size()*quadraticData[i]-Math.pow(sumData[i], 2))/(dataTraining.size()*(dataTraining.size()-1)));
                }

                modelUdp.add(new DataModel(port, meanData, deviasiData, quadraticData, standardData, dataTraining.size()));
                dataTraining.clear();
            }            
        }                
    }
    
}
