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
    int files = 1; 
//    static Map<String, Double[]> datasetPacketTcp = new HashMap<>();
//    static Map<String, Double[]> datasetPacketUdp = new HashMap<>();
//    static Map<String, Double[]> dataTestPacket = new HashMap<>();
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();       
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    static DataPacket[] dataTrain = new DataPacket[]{};
    
    public void fileReader(String dir, int inputCmd, List<Thread> threadFile){
        File filePath = new File(dir);
        File[] listFile = filePath.listFiles();    
        
        for (File file : listFile) {
            if (file.isFile()) {
                System.out.println("Dataset ke-"+files+" "+file.getAbsolutePath());
                try {
                    JpcapCaptor captor = JpcapCaptor.openFile(file.getAbsolutePath());
                    PacketReader pr = new PacketReader(files, captor, inputCmd, datasetTcp, datasetUdp, dataTest, 1);
                    Thread threadFiles = new Thread(pr);
                    threadFiles.start();
                    threadFile.add(threadFiles);                
                } catch (IOException ex) {
                    Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                } 
                files++;
            } 
            
            else if (file.isDirectory()) {
                fileReader(file.getAbsolutePath(), inputCmd, threadFile);
            }
        }
    }
    
    public String getDatasetDir() throws FileNotFoundException, IOException{
        int count = 0;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        while ((line = br.readLine()) != null) {
            if (count == 0) {
                return line;
            }
            count++;
        }
        return line;
    }
    
    public String getPcapTest() throws FileNotFoundException, IOException{
        int count = 0;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        while ((line = br.readLine()) != null) {
            if (count == 1) {
                return line;
            }
            count++;
        }
        return line;
    }
    
    public double getThreshold() throws FileNotFoundException, IOException{
        double threshold = 0;
        int count = 0;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        while ((line = br.readLine()) != null) {
            if (count == 2) {
                threshold = Double.parseDouble(line);
            }
            count++;
        }
        return threshold;
    }
    
    public double getSFactor() throws FileNotFoundException, IOException{
        double sFactor = 0;
        int count = 0;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        while ((line = br.readLine()) != null) {
            if (count == 3) {
                sFactor = Double.parseDouble(line);
            }
            count++;
        }
        return sFactor;
    }
    
    public int getCounterPacket() throws FileNotFoundException, IOException{
        int count = 0;
        String line;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        while ((line = br.readLine()) != null) {
            if (count == 4) {
                return Integer.parseInt(line);
            }
            count++;
        }
        return Integer.parseInt(line);
    }
    
    public static void main(String[] args) throws IOException {        
        int input, counter;
        double dist, threshold, sFactor;
        long start, end;
        String[] header, fileName;
        List<Thread> threads;
        ArrayList<Double> valAttack;
        ArrayList<Double> valFree;
        String dirPath, filePath;
        File fileFree, fileAttack;
        FileWriter fwFree, fwAttack;
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
                    threads = new ArrayList<>();
                    System.out.println("Dataset Directory: "+dirPath);
                    start = System.currentTimeMillis();
                    ids.fileReader(dirPath, input, threads);
                    for (Thread thread : threads) {
                        try {
                            thread.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");               
                    
                    break;
                
                case 2:
                    if ( datasetTcp.size() != 0 | datasetUdp.size() != 0) {
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
                        filePath = ids.getPcapTest();                       
                        System.out.println("Open File: "+filePath);                        
                        JpcapCaptor captor = JpcapCaptor.openFile(filePath);                        
                        PacketReader prTest = new PacketReader(0, captor, input, datasetTcp, datasetUdp, dataTest, input);
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
                        fileFree = new File("PcapTest_free_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]+"_"+threshold);
                        fileAttack = new File("PcapTest_attack_"+fileName[fileName.length-2]+"_"+fileName[fileName.length-1]+"_"+threshold);
                        fwFree = new FileWriter(fileFree,true);
                        fwAttack = new FileWriter(fileAttack,true);
                        valAttack = new ArrayList<>();
                        valFree = new ArrayList<>();

                        System.out.println("Threshold: "+threshold);
                        System.out.println("Smoothing Factor: "+sFactor);
                        System.out.println("Calculating Mahalanobis Distance...");                        

                        start = System.currentTimeMillis();

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
