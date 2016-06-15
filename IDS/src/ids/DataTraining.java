/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.util.ArrayList;
import java.util.Map;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class DataTraining implements Runnable {
    
    private int ascii = 256, port;
    private String proto;
    private double[] sumData = new double[ascii], meanData = new double[ascii], sDeviasi = new double[ascii];
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;  
    private ArrayList<Double[]> dataTraining = new ArrayList<>();
    private Map<Integer, Double[]> sumTrainTcp;
    private Map<Integer, Double[]> sDeviasiTrainTcp;
    private Map<Integer, Double[]> sumTrainUdp;
    private Map<Integer, Double[]> sDeviasiTrainUdp;
    
    
    public DataTraining(String proto, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, Map<Integer, Double[]> sumTrainTcp, Map<Integer, Double[]> sDeviasiTrainTcp, Map<Integer, Double[]> sumTrainUdp, Map<Integer, Double[]> sDeviasiTrainUdp, int port){
        this.proto = proto;
        this.datasetTcp = datasetTcp;
        this.datasetUdp = datasetUdp;
        this.sumTrainTcp = sumTrainTcp;
        this.sDeviasiTrainTcp = sDeviasiTrainTcp;
        this.sumTrainUdp = sumTrainUdp;
        this.sDeviasiTrainUdp = sDeviasiTrainUdp;
        this.port = port;
    }
    
    public void run(){
        synchronized(dataTraining){
            if (proto.equals("tcp")) {
                for (DataPacket dataSetA : datasetTcp) {
                    if (dataSetA.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                    }                                    
                }               

                if (dataTraining.size() != 0) {
                    for (int i = 0; i < dataTraining.size(); i++) {
                        for (int j = 0; j < ascii; j++) {
                            sumData[j] += dataTraining.get(i)[j];
                        }                                                                        
                    }

                    for (int i = 0; i < ascii; i++) {
                        meanData[i] = sumData[i]/dataTraining.size();
                    }  

                    for (int i = 0; i < ascii; i++) {
                        sDeviasi[i] = Math.sqrt(Math.pow(sumData[i]-meanData[i], 2)/dataTraining.size());
                    }

                    sumTrainTcp.put(port, ArrayUtils.toObject(sumData));
                    sDeviasiTrainTcp.put(port, ArrayUtils.toObject(sDeviasi));
                }            
            }

            else if (proto.equals("udp")) {
                for (DataPacket dataSetA : datasetUdp) {
                    if (dataSetA.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                    }                                    
                }

                if (dataTraining.size() != 0) {
                    for (int i = 0; i < dataTraining.size(); i++) {
                        for (int j = 0; j < ascii; j++) {
                            sumData[j] += dataTraining.get(i)[j];
                        }                                                                        
                    }

                    for (int i = 0; i < ascii; i++) {
                        meanData[i] = sumData[i]/dataTraining.size();
                    }  

                    for (int i = 0; i < ascii; i++) {
                        sDeviasi[i] = Math.sqrt(Math.pow(sumData[i]-meanData[i], 2)/dataTraining.size());
                    }

                    sumTrainUdp.put(port, ArrayUtils.toObject(sumData));
                    sDeviasiTrainUdp.put(port, ArrayUtils.toObject(sDeviasi));              
                }            
            }            
        }                
    }
    
}
