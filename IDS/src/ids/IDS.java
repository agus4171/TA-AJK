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
    int files = 1, count;  
    String line;
    String[] ket;
    BufferedReader br;
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    static Map<Integer, Double[]> sumTrain = new HashMap<>();
    static Map<Integer, Double[]> sDeviasiTrain = new HashMap<>();
    
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
                    
                    System.out.println("Trainig Dataset...");
                    start = System.currentTimeMillis();
                    
                    for (DataPacket dataSetA : datasetTcp) {
                        if (dataSetA.getDstPort() < 1024) {
                            dataTraining = new ArrayList<>(); 
                            for (DataPacket dataSetTcp : datasetTcp) {
                                if (dataSetA.getDstPort() == dataSetTcp.getDstPort()) {                                        
                                    dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                                }                                    
                            }
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
                            
                            sumTrain.put(dataSetA.getDstPort(), ArrayUtils.toObject(sumData));
                            sDeviasiTrain.put(dataSetA.getDstPort(), ArrayUtils.toObject(sDeviasi));
                        }                             
                    }
                    
                    for (DataPacket dataSetB : datasetUdp) {
                        if (dataSetB.getDstPort() < 1024) {
                            dataTraining = new ArrayList<>(); 
                            for (DataPacket dataSetUdp : datasetUdp) {
                                if (dataSetB.getDstPort() == dataSetUdp.getDstPort()) {                                        
                                    dataTraining.add(ArrayUtils.toObject(dataSetB.getNgram()));                                        
                                }                                    
                            }
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
                            
                            sumTrain.put(dataSetB.getDstPort(), ArrayUtils.toObject(sumData));
                            sDeviasiTrain.put(dataSetB.getDstPort(), ArrayUtils.toObject(sDeviasi));
                        }                             
                    }
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");               
                    
                    break;
                
                case 2:
                    if (datasetTcp.size() != 0 | datasetUdp.size() != 0) {
                        System.out.println("Packet Sniffer Test");
                        ids = new IDS();           
                        filePath = ids.getPcapTest();
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
                        fileFree = new File("PcapTest_tcp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fileAttack = new File("PcapTest_udp_dist_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
                        fwFree = new FileWriter(fileFree,true);
                        fwAttack = new FileWriter(fileAttack,true);
                        valAttack = new ArrayList<>();
                        valFree = new ArrayList<>();
                        System.out.println("Calculating Mahalanobis Distance...");

                        start = System.currentTimeMillis();
                        for (DataPacket dataPacketTes : dataTest) {     

                            if ("tcp".equals(dataPacketTes.getProto()) && dataPacketTes.getDstPort() < 1024) {
                                if (sumTrain.containsKey(dataPacketTes.getDstPort())) {
                                    sd = new Mahalanobis();
                                    dist = sd.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrain.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrain.get(dataPacketTes.getDstPort())), sFactor);
                                    fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
                                }

                            }

                            else if ("udp".equals(dataPacketTes.getProto()) && dataPacketTes.getDstPort() < 1024) {
                                if (sumTrain.containsKey(dataPacketTes.getDstPort())) {
                                    sd = new Mahalanobis();
                                    dist = sd.distance(dataPacketTes.getNgram(), ArrayUtils.toPrimitive(sumTrain.get(dataPacketTes.getDstPort())), ArrayUtils.toPrimitive(sDeviasiTrain.get(dataPacketTes.getDstPort())), sFactor);
                                    fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
                                }
                            }
                        }

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
                        valAttack = new ArrayList<>();
                        valFree = new ArrayList<>();

                        System.out.println("Threshold: "+threshold);
                        System.out.println("Smoothing Factor: "+sFactor);
                        System.out.println("Calculating Mahalanobis Distance...");                         

                        start = System.currentTimeMillis();                        
                        
                        for (DataPacket dataPacketTes : dataTest) {     

                            if ("tcp".equals(dataPacketTes.getProto()) && dataPacketTes.getDstPort() < 1024) {
//                                String tuples = new String(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort());
//                                FindDistance fd = new FindDistance("tcp", tuples, datasetTcp, datasetUdp, dataPacketTes.getNgram(), dataPacketTes.getDstPort());
//                                Thread threadFd = new Thread(fd);
//                                threadFd.start();
                                dataTraining = new ArrayList<>();                                
                                
                                for (DataPacket dataSetA : datasetTcp) {
                                    if (dataSetA.getDstPort() == dataPacketTes.getDstPort()) {                                        
                                        dataTraining.add(ArrayUtils.toObject(dataSetA.getNgram()));                                        
                                    }                                    
                                }

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
                                
                                if (dataPacketTes.getType() == 1) {
                                    valAttack.add(dist);
                                } else
                                    valFree.add(dist);

                                fwFree.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
                            }

                            else if ("udp".equals(dataPacketTes.getProto()) && dataPacketTes.getDstPort() < 1024) {
//                                String tuples = new String(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort());
//                                FindDistance fd = new FindDistance("udp", tuples, datasetTcp, datasetUdp, dataPacketTes.getNgram(), dataPacketTes.getDstPort());
//                                Thread threadFd = new Thread(fd);
//                                threadFd.start();
                                dataTraining = new ArrayList<>();                                
                                
                                for (DataPacket dataSetB : datasetUdp) {
                                    if (dataSetB.getDstPort() == dataPacketTes.getDstPort()) {                                        
                                        dataTraining.add(ArrayUtils.toObject(dataSetB.getNgram()));                                        
                                    }                                    
                                }

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
                                
//                                if (dataPacketTes.getType() == 1) {
//                                    valAttack.add(dist);
//                                } else
//                                    valFree.add(dist);

                                fwAttack.append(dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+" -> "+dist+"\n");
                            }
                        }
                        
                        fwAttack.close();
                        fwFree.close();
                        end = System.currentTimeMillis();
                        System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                        System.out.println("Total attack packet: "+valAttack.size());
                        System.out.println("Total free packet: "+valFree.size());
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
