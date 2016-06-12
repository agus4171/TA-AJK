/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.Arrays;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class FindDistance implements Runnable {
    
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;
    private ArrayList<DataPacket> dataTest;
    int ascii = 256, port;
    String proto, tuples;
    double dist, threshold, sFactor;
    double[] sumData, meanData, sDeviasi, ngram;
    ArrayList<Double[]> dataTraining;
    File file, fileFree, fileAttack;
    FileWriter fw, fwFree, fwAttack;
    Mahalanobis sd;
    
    public FindDistance(String proto, String tuples, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, double[] ngram, int port){
        this.proto = proto;
        this.tuples = tuples;
        this.datasetTcp = datasetTcp;
        this.datasetUdp = datasetUdp;
        this.ngram = ngram;
        this.port = port;
        dataTraining = new ArrayList<>();
        sumData = new double[ascii];
        meanData = new double[ascii];
        sDeviasi = new double[ascii];
        sd = new Mahalanobis();
    }
    
    public void run(){
        synchronized(dataTraining){
            if (proto.equals("tcp")) {
//                dataTraining = new ArrayList<>();                                

                for (DataPacket dataSetA : datasetTcp) {
                    if (dataSetA.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                    }                                    
                }               

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

                dist = sd.distance(ngram, sumData, sDeviasi, sFactor);
                System.out.println(Arrays.toString(sumData));
                System.out.println(Arrays.toString(meanData));
                System.out.println(Arrays.toString(sDeviasi));
            }

            else if (proto.equals("udp")) {
//                dataTraining = new ArrayList<>();                                

                for (DataPacket dataSetA : datasetUdp) {
                    if (dataSetA.getDstPort() == port) {                                        
                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                    }                                    
                }

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
                
                dist = sd.distance(ngram, sumData, sDeviasi, sFactor);
                System.out.println(Arrays.toString(sumData));
                System.out.println(Arrays.toString(meanData));
                System.out.println(Arrays.toString(sDeviasi));
            }            
        }
        print(proto+" -> "+tuples, dist);
    }
    
    public void print(String tuples, double d){
//        String dist = d;
        System.out.println(tuples+" -> "+Double.toString(d));
    }
    
}
