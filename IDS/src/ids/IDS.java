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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
    static int files = 1, filesTesting = 1;
    String line;
    String[] ket;
    BufferedReader br;
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    static Map<Integer, Double[]> sumTrainTcp = new HashMap<>();
    static Map<Integer, Double[]> sDeviasiTrainTcp = new HashMap<>();
    static Map<Integer, Double[]> sumTrainUdp = new HashMap<>();
    static Map<Integer, Double[]> sDeviasiTrainUdp = new HashMap<>();
    
    String getDatasetDir() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String dataTraining = null;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("DataTraining")) {
                dataTraining = ket[1];
            }
        }
        return dataTraining;
    }
    
    int getTypePacket() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        int typePacket = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("TypePacket")) {
                typePacket = Integer.parseInt(ket[1]);
            }
        }
        return typePacket;
    }
    
    String getPcapTest() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String pcapTest = null;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("DataTesting")) {
                pcapTest = ket[1];
            }
        }
        return pcapTest;
    }
    
    double getThreshold() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double threshold = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Threshold")) {
                threshold = Double.parseDouble(ket[1]);
            }
        }
        return threshold;
    }
    
    double getSFactor() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double sFactor = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("SmoothingFactor")) {
                sFactor = Double.parseDouble(ket[1]);
            }
        }
        return sFactor;
    }
    
    int getCounterPacket() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        int counterPacket = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("LimitPacket")) {
                counterPacket = Integer.parseInt(ket[1]);
            }
        }
        return counterPacket;
    }
    
    private void datasetReader(String dirPath, int input, int packetType, List<Thread> threadFile){
        File filePath = new File(dirPath);
        File[] listFile = filePath.listFiles();         
        
        for (File file : listFile) {
            if (file.isFile()) {
                System.out.println("Processing dataset ke-"+files+" "+file.getAbsolutePath());
                try {
                    JpcapCaptor captor = JpcapCaptor.openFile(file.getAbsolutePath());
                    PacketReader pr = new PacketReader(files, captor, input, datasetTcp, datasetUdp, dataTest, packetType);
                    Thread threadFiles = new Thread(pr);
                    threadFiles.start();
                    threadFile.add(threadFiles);                        
                } catch (IOException ex) {
                    Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                }               
                files++;
            } 
            
            else if (file.isDirectory()) {
                datasetReader(file.getAbsolutePath(), input, packetType, threadFile);
            }
        }
    }
    
    private void pcapTesting(String dirPath, int input, int packetType){
        File filePath = new File(dirPath);
        File[] listFile = filePath.listFiles();
        double threshold, sFactor, mDist;
        long start, end;
        String[] fileName;
        File fileFree, fileAttack;
        FileWriter fwFree, fwAttack;
        Mahalanobis mahalanobis;
        ArrayList<Double> valAttack;
        ArrayList<Double> valFree;
        IDS idsTesting = new IDS();
        
        for (File file : listFile) {
            if (file.isFile()) {
                System.out.println("Processing data testing ke-"+filesTesting+" "+file.getAbsolutePath());
                try {
                    JpcapCaptor captor = JpcapCaptor.openFile(file.getAbsolutePath());
                    PacketReader pr = new PacketReader(filesTesting, captor, input, datasetTcp, datasetUdp, dataTest, packetType);
                    Thread threadTesting = new Thread(pr);
                    threadTesting.start();
                    try {
                        threadTesting.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    
                    threshold = idsTesting.getThreshold(); 
                    sFactor = idsTesting.getSFactor();
                    fileName = file.getAbsolutePath().replace('/', '-').split("-", 0);
                    fileFree = new File("PcapTesting_free_packet"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                    fileAttack = new File("PcapTesting_attack_packet"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                    fwFree = new FileWriter(fileFree,true);
                    fwAttack = new FileWriter(fileAttack,true);
                    valAttack = new ArrayList<>();
                    valFree = new ArrayList<>();

                    System.out.println("Threshold: "+threshold);
                    System.out.println("Smoothing Factor: "+sFactor);
                    System.out.println("Calculating Mahalanobis Distance...");                         

                    start = System.currentTimeMillis();                         

                    for (DataPacket dataPacketTes : dataTest) {     

                         if ("tcp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                            if (sumTrainTcp.containsKey(dataPacketTes.getDstPort())) {
                                mahalanobis = new Mahalanobis();
                                mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainTcp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainTcp.get(dataPacketTes.getDstPort())), sFactor);
                                if (mDist > threshold) {
                                    valAttack.add(mDist);
                                    fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                } else {
                                    valFree.add(mDist);
                                    fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                }                                
                            }

                        }

                        else if ("udp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                            if (sumTrainUdp.containsKey(dataPacketTes.getDstPort())) {
                                mahalanobis = new Mahalanobis();
                                mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainUdp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainUdp.get(dataPacketTes.getDstPort())), sFactor);
                                if (mDist > threshold) {
                                    valAttack.add(mDist);
                                    fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                } else {
                                    valFree.add(mDist);
                                    fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                }                                
                            }
                        }
                    }
                    dataTest.clear();

                    fwAttack.close();
                    fwFree.close();
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    System.out.println("Total attack packet: "+valAttack.size());
                    System.out.println("Total free packet: "+valFree.size());   

                } catch (IOException ex) {
                    Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                }                
                filesTesting++;
            } 
            
            else if (file.isDirectory()) {
                pcapTesting(file.getAbsolutePath(), input, packetType);
            }
        }
    }
    
    public static void main(String[] args) throws IOException {        
        int input, packetCounter, packetType, portPacket, ascii = 256;
        double mDist, threshold, sFactor;
        double[] sumData, meanData, deviasiData;
        long start, end;
        String[] header, fileName;
        List<Thread> threadTraining;
        List<Thread> threadFile;
        ArrayList<Double> valAttack;
        ArrayList<Double> valFree;
        ArrayList<Double[]> dataTraining;
        Map<Integer, Integer> portTrainingTcp;
        Map<Integer, Integer> portTrainingUdp;
        String dirPath, filePath;
        File file, fileFree, fileAttack;
        FileWriter fw, fwFree, fwAttack;
        Mahalanobis mahalanobis;
        IDS ids;
        DataTraining dt;
        Scanner sc = new Scanner(System.in);
        
        OUTER:           
        while (true) {
            System.out.println("1) Training Dataset\n"+"2) Sniffer Testing\n"+"3) Pcap Testing\n"+"Type(1 or 2 or 3): ?");
            input = sc.nextInt();
            switch (input) {
                case 1:
                    System.out.println("Processing Dataset...");
                    ids = new IDS();
                    dirPath = ids.getDatasetDir();
                    packetType = ids.getTypePacket();
                    threadFile = new ArrayList<>();
                    System.out.println("Dataset directory: "+dirPath);
                    start = System.currentTimeMillis();
                    ids.datasetReader(dirPath, input, packetType, threadFile);
                    for (Thread threads : threadFile) {
                        try {
                            threads.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    end = System.currentTimeMillis();
                    System.out.println("Total time of processing dateset is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    
                    System.out.println("Training Dataset...");
                    start = System.currentTimeMillis();
                    portTrainingTcp = new HashMap<>();
                    if (datasetTcp.size() != 0) {
                        for (DataPacket dpTcp : datasetTcp) {
                            portPacket = dpTcp.getDstPort();
                            if (portPacket < 1024) {
                                if (portTrainingTcp.containsKey(portPacket)) continue;
                                portTrainingTcp.put(portPacket, portPacket);
                            }                            
                        }                        

                        threadTraining = new ArrayList<>();
                        for (Map.Entry<Integer, Integer> entry : portTrainingTcp.entrySet()) {
                            Integer port = entry.getKey();
                            dt = new DataTraining("tcp", datasetTcp, datasetUdp, sumTrainTcp, sDeviasiTrainTcp, sumTrainUdp, sDeviasiTrainUdp, port);
                            Thread threadDt = new Thread(dt);
                            threadDt.start();
                            threadTraining.add(threadDt);                            
                        }
                        
                        for (Thread thread : threadTraining) {
                            try {
                                thread.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
//                        System.out.println("tcp: "+sumTrainTcp.size()+" "+sDeviasiTrainTcp.size());                                               
                    }  
                    
                    if (datasetUdp.size() != 0) {
                        portTrainingUdp = new HashMap<>();
                        for (DataPacket dpUdp : datasetUdp) {
                            portPacket = dpUdp.getDstPort();
                            if (portPacket < 1024) {
                                if (portTrainingUdp.containsKey(portPacket)) continue;
                                portTrainingUdp.put(portPacket, portPacket);
                            }                            
                        }
                        
                        threadTraining = new ArrayList<>();
                        for (Map.Entry<Integer, Integer> entry : portTrainingUdp.entrySet()) {
                            Integer port = entry.getKey();
                            dt = new DataTraining("udp", datasetTcp, datasetUdp, sumTrainTcp, sDeviasiTrainTcp, sumTrainUdp, sDeviasiTrainUdp, port);
                            Thread threadDt = new Thread(dt);
                            threadDt.start();
                            threadTraining.add(threadDt); 
                        }
                        
                        for (Thread thread : threadTraining) {
                            try {
                                thread.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
//                        System.out.println("udp: "+sumTrainUdp.size()+" "+sDeviasiTrainUdp.size()); 
                    }
                    
                    end = System.currentTimeMillis();
                    System.out.println("Total time of training dataset is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    datasetTcp.clear();
                    datasetUdp.clear();
                    
                    break;
                
                case 2:
                    if (sumTrainTcp.size() != 0 | sumTrainUdp.size() != 0) {
                        System.out.println("Sniffer Testing");
                        ids = new IDS();           
                        filePath = ids.getPcapTest();
                        packetCounter = ids.getCounterPacket();
                        NetworkInterface[] device = JpcapCaptor.getDeviceList();
                        fileFree = new File("SnifferTesting");
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
                        PacketSniffer ps = new PacketSniffer(device[input], input, dataTest, packetCounter);
                        Thread threadPs = new Thread(ps);
                        threadPs.start();
                        try {
                            threadPs.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                        
                        threshold = ids.getThreshold(); 
                        sFactor = ids.getSFactor();
                        fileName = filePath.replace('/', '-').split("-", 0);
                        fileFree = new File("SnifferTesting_tcp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fileAttack = new File("SnifferTesting_udp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fwFree = new FileWriter(fileFree,true);
                        fwAttack = new FileWriter(fileAttack,true);
                        valAttack = new ArrayList<>();
                        valFree = new ArrayList<>();
                        System.out.println("Calculating Mahalanobis Distance...");

                        start = System.currentTimeMillis();
                        
                        for (DataPacket dataPacketTes : dataTest) {     

                            if ("tcp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                                if (sumTrainTcp.containsKey(dataPacketTes.getDstPort())) {
                                    mahalanobis = new Mahalanobis();
                                    mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainTcp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainTcp.get(dataPacketTes.getDstPort())), sFactor);
                                    fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                }

                            }

                            else if ("udp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                                if (sumTrainUdp.containsKey(dataPacketTes.getDstPort())) {
                                    mahalanobis = new Mahalanobis();
                                    mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainUdp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainUdp.get(dataPacketTes.getDstPort())), sFactor);
                                    fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                }
                            }
                        }

                        fwFree.close();
                        end = System.currentTimeMillis();
                        System.out.println("Total time of sniffer testing is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    }          
                    
                    System.out.println("Please choose Training Dataset First!");
                    
                    break;
                
                case 3:
                    if (sumTrainTcp.size() != 0 | sumTrainUdp.size() != 0) {
                        System.out.println("Pcap Testing");
                        ids = new IDS();
                        packetType = ids.getTypePacket();
                        filePath = ids.getPcapTest();
                        file = new File(filePath);
                        if (file.isFile()) {
                            System.out.println("Open File: "+filePath);
                            JpcapCaptor captor = JpcapCaptor.openFile(filePath);                        
                            PacketReader prTest = new PacketReader(0, captor, input, datasetTcp, datasetUdp, dataTest, packetType);
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
                            fileFree = new File("PcapTesting_free_packet"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                            fileAttack = new File("PcapTesting_attack_packet"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                            fwFree = new FileWriter(fileFree,true);
                            fwAttack = new FileWriter(fileAttack,true);
                            valAttack = new ArrayList<>();
                            valFree = new ArrayList<>();

                            System.out.println("Threshold: "+threshold);
                            System.out.println("Smoothing Factor: "+sFactor);
                            System.out.println("Calculating Mahalanobis Distance...");                         

                            start = System.currentTimeMillis();                         

                            for (DataPacket dataPacketTes : dataTest) {     

                                if ("tcp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                                    if (sumTrainTcp.containsKey(dataPacketTes.getDstPort())) {
                                        mahalanobis = new Mahalanobis();
                                        mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainTcp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainTcp.get(dataPacketTes.getDstPort())), sFactor);
                                        if (mDist > threshold) {
                                            valAttack.add(mDist);
                                            fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                        } else {
                                            valFree.add(mDist);
                                            fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                        }                                        
                                    }
                                }

                                else if ("udp".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                                    if (sumTrainUdp.containsKey(dataPacketTes.getDstPort())) {
                                        mahalanobis = new Mahalanobis();
                                        mDist = mahalanobis.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrainUdp.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrainUdp.get(dataPacketTes.getDstPort())), sFactor);
                                        if (mDist > threshold) {
                                            valAttack.add(mDist);
                                            fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                        } else {
                                            valFree.add(mDist);
                                            fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+mDist+"\n");
                                        }                                        
                                    }
                                }
                            }

                            fwAttack.close();
                            fwFree.close();
                            end = System.currentTimeMillis();
                            System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                            System.out.println("Total attack packet: "+valAttack.size());
                            System.out.println("Total free packet: "+valFree.size());    
                            dataTest.clear();
                        } 
                        
                        else if (file.isDirectory()) {
                            ids.pcapTesting(filePath, input, packetType);
                        }
                            
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
