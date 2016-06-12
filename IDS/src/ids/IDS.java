/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class IDS {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    int files = 1, count;  
    String line;
    String[] ket;
    BufferedReader br;
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    
    private void fileReader(String dir, int inputCmd, List<Thread> threadFile, int type){
        File filePath = new File(dir);
        File[] listFile = filePath.listFiles(); 
        long startFile, endFile;
        
        for (File file : listFile) {
            if (file.isFile()) {
                System.out.println("Dataset ke-"+files+" "+file.getAbsolutePath());
                try {
                    startFile = System.currentTimeMillis();
                    JpcapCaptor captor = JpcapCaptor.openFile(file.getAbsolutePath());
                    PacketReader pr = new PacketReader(files, captor, inputCmd, datasetTcp, datasetUdp, dataTest, type);
                    Thread threadFiles = new Thread(pr);
                    threadFiles.start();
                    try {
                        threadFiles.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    endFile = System.currentTimeMillis();
                    System.out.println("Time Read Dataset is: "+(endFile-startFile)/60000+" minutes "+((endFile-startFile)%60000)/1000+" seconds");
                
                } catch (IOException ex) {
                    Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                } 
                files++;
            } 
            
            else if (file.isDirectory()) {
                fileReader(file.getAbsolutePath(), inputCmd, threadFile, type);
            }
        }
    }
    
    String getDatasetDir() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String dataTraining = null;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("dataTraining")) {
                dataTraining = ket[1];
            }
            count++;
        }
        return dataTraining;
    }
    
    int getTypePacket() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        int typePacket = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("typePacket")) {
                typePacket = Integer.parseInt(ket[1]);
            }
            count++;
        }
        return typePacket;
    }
    
    String getPcapTest() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String pcapTest = null;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("dataTesting")) {
                pcapTest = ket[1];
            }
            count++;
        }
        return pcapTest;
    }
    
    double getThreshold() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double threshold = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("threshold")) {
                threshold = Double.parseDouble(ket[1]);
            }
            count++;
        }
        return threshold;
    }
    
    double getSFactor() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double sFactor = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("sFactor")) {
                sFactor = Double.parseDouble(ket[1]);
            }
            count++;
        }
        return sFactor;
    }
    
    int getCounterPacket() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        int counterPacket = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("limitPacket")) {
                counterPacket = Integer.parseInt(ket[1]);
            }
            count++;
        }
        return counterPacket;
    }
    
    public static void main(String[] args) throws IOException {        
        int input, counter, type, ascii = 256;
        double dist, threshold, sFactor;
        double[] sumData, meanData, sDeviasi;
        long start, end;
        String[] header, fileName;
        List<Thread> threads;
        ArrayList<Double> valAttack;
        ArrayList<Double> valFree;
        ArrayList<Double[]> dataTraining;
        String dirPath, filePath;
        File file, fileFree, fileAttack;
        FileWriter fw, fwFree, fwAttack;
        Mahalanobis sd;
        IDS ids;
        Scanner sc = new Scanner(System.in);
        
        OUTER:           
        while (true) {
            System.out.println("1. Training Dataset\n"+"2. Packet Sniffer Test\n"+"3. Pcap Test\n"+"Choose(1 or 2 or 3): ");
            input = sc.nextInt();
            switch (input) {
                case 1:
                    System.out.println("Trainig Dataset");
                    ids = new IDS();
                    dirPath = ids.getDatasetDir();
                    type = ids.getTypePacket();
                    threads = new ArrayList<Thread>();
                    System.out.println("Dataset Directory: "+dirPath);
                    start = System.currentTimeMillis();
                    ids.fileReader(dirPath, input, threads, type);
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");               
                    
                    break;
                
                case 2:
                    if (datasetTcp.size() != 0 | datasetUdp.size() != 0) {
                        System.out.println("Packet Sniffer Test");
                        ids = new IDS();
                        counter = ids.getCounterPacket();
                        NetworkInterface[] device = JpcapCaptor.getDeviceList();
                        fileFree = new File("PacketSnifferTest");
                        fwFree = new FileWriter(fileFree,true);

                        for (int i = 0; i < device.length; i++) {
                            System.out.println(i+": "+device[i].name + "(" + device[i].description+")");

                            //print out its datalink name and description
                            System.out.println("   Datalink: "+device[i].datalink_name + "(" + device[i].datalink_description+")");

                            //print out its MAC address
                            System.out.print("   MAC address: ");
                            for (byte b : device[i].mac_address)
                                System.out.print(Integer.toHexString(b&0xff) + ":");
                            System.out.println();

                            //print out its IP address, subnet mask and broadcast address
                            for (NetworkInterfaceAddress a : device[i].addresses)
                                System.out.println("   Address: "+a.address + " " + a.subnet + " "+ a.broadcast);
                            System.out.println("\n");
                        }

                        sc = new Scanner(System.in);
                        input = sc.nextInt();
                        System.out.println(device[input].name);
                        PacketSniffer ps = new PacketSniffer(device[input], input, dataTest, counter);
                        Thread u = new Thread(ps);
                        u.start();
                        try {
                            u.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        threshold = ids.getThreshold();
                        sFactor = ids.getSFactor();
                        System.out.println("Calculating Mahalanobis Distance...");

                        start = System.currentTimeMillis();
//                        for (DataPacket dataPacketTes : dataTes) {     
//
//                            if ("tcp".equals(dataPacketTes.getProto())) {                                
////                                double maxt = 0.0, mint = 0.0;
////                                ArrayList<Double> val = new ArrayList<>();
////
////                                for (DataPacket dataSetA : datasetTcp) {
////                                    if (dataSetA.getTuples().equals(dataPacketTes.getTuples())) {
////                                        Mahalanobis sd = new Mahalanobis(dataSetA.getNgram());                                        
////                                        dist = sd.distance(dataPacketTes.getNgram(), dataSetA.getNgram(), sFactorSniff);
////                                        val.add(dist);
////                                    }                                    
////                                }
////
////                                if (!val.isEmpty()) {
////                                    maxt = val.stream().max(Double::compare).get();
////                                    mint = val.stream().min(Double::compare).get();
////                                    fwFree.append("tcp -> "+dataPacketTes.getTuples()+" -> "+val.size()+" "+maxt+" "+mint);
////                                    fwFree.append("\n");
////                                }                                                                
//                            }
//
//                            else if ("udp".equals(dataPacketTes.getProto())) {                                 
////                                double maxu = 0.0, minu = 0.0;
////                                ArrayList<Double> val = new ArrayList<>();
////
////                                for (DataPacket dataSetB : datasetUdp) {   
////                                    if (dataSetB.getTuples().equals(dataPacketTes.getTuples())) {
////                                        Mahalanobis sd = new Mahalanobis(dataSetB.getNgram());
////                                        dist = sd.distance(dataPacketTes.getNgram(), dataSetB.getNgram(), sFactorSniff);
////                                        val.add(dist);
////                                    }                                     
////                                }
////
////                                if (!val.isEmpty()) {
////                                    maxu = val.stream().max(Double::compare).get();
////                                    minu = val.stream().min(Double::compare).get();
////                                    fwFree.append("udp -> "+dataPacketTes.getTuples()+" -> "+val.size()+" "+maxu+" "+minu);
////                                    fwFree.append("\n");
////                                }                                                              
//                            }
//                        }

                        fwFree.close();
                        end = System.currentTimeMillis();
                        System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    }          
                    
                    System.out.println("Please choose Training Dataset First!");
                    
                    break;
                
                case 3:
                    if (datasetTcp.size() != 0 | datasetUdp.size() != 0) {
                        System.out.println("Pcap Test");
                        ids = new IDS();
                        type = ids.getTypePacket();
                        filePath = ids.getPcapTest();
                        System.out.println("Open File: "+filePath);
                        JpcapCaptor captor = JpcapCaptor.openFile(ids.getPcapTest());                        
                        PacketReader prTest = new PacketReader(0, captor, input, datasetTcp, datasetUdp, dataTest, type);
                        Thread threadTest = new Thread(prTest);
                        threadTest.start();
                        try {
                            threadTest.join();                        
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }

                        threshold = ids.getThreshold(); 
                        sFactor = ids.getSFactor();
                        fileName = filePath.replace('/', '-').split("-", 0);
                        fileFree = new File("PcapTest_tcp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fileAttack = new File("PcapTest_udp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fwFree = new FileWriter(fileFree,true);
                        fwAttack = new FileWriter(fileAttack,true);
//                        valAttack = new ArrayList<>();
//                        valFree = new ArrayList<>();

                        System.out.println("Threshold: "+threshold);
                        System.out.println("Smoothing Factor: "+sFactor);
                        System.out.println("Calculating Mahalanobis Distance...");                         

                        start = System.currentTimeMillis();                        
                        
                        for (DataPacket dataPacketTes : dataTest) {     

                            if ("tcp".equals(dataPacketTes.getProto())) {      
                                dataTraining = new ArrayList<>();                                
                                
                                for (DataPacket dataSetA : datasetTcp) {
                                    if (dataSetA.getDstPort() == dataPacketTes.getDstPort()) {                                        
                                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                                    }                                    
                                }
                                
                                System.out.println(dataTraining.size());
                                sumData = new double[ascii];
                                meanData = new double[ascii];
                                sDeviasi = new double[ascii];

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
                                
                                sd = new Mahalanobis();
                                dist = sd.distance(dataPacketTes.getNgram(), sumData, sDeviasi, sFactor);
                                
                                fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
//                                System.out.println("tcp-"+dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist);
                            }

                            else if ("udp".equals(dataPacketTes.getProto())) {
                                dataTraining = new ArrayList<>();                                
                                
                                for (DataPacket dataSetB : datasetUdp) {
                                    if (dataSetB.getDstPort() == dataPacketTes.getDstPort()) {                                        
                                        dataTraining.add(ArrayUtils.toObject(dataSetB.getNgram()));                                        
                                    }                                    
                                }
                                
                                System.out.println(dataTraining.size());
                                sumData = new double[ascii];
                                meanData = new double[ascii];
                                sDeviasi = new double[ascii];

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
                                sd = new Mahalanobis();
                                dist = sd.distance(dataPacketTes.getNgram(), sumData, sDeviasi, sFactor);
                                
                                fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
//                                System.out.println("udp-"+dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist);                                
                            }
                        }
                        
                        fwAttack.close();
                        fwFree.close();
                        end = System.currentTimeMillis();
                        System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
//                        System.out.println("Total attack packet: "+valAttack.size());
//                        System.out.println("Total free packet: "+valFree.size());
                    }
                    
                    else
                        System.out.println("Please choose Training Dataset First!");
                    
                    break;
                
                default:
                    break OUTER;
            }
        } 
    }
    
}
