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
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;

/**
 *
 * @author agus
 */
public class IDS {

    /**
     * @param args the command line arguments
     * @throws java.io.IOException
     */
    static int ascii = 256, core = Runtime.getRuntime().availableProcessors();
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    static ArrayList<DataModel> modelTcp = new ArrayList<>();
    static ArrayList<DataModel> modelUdp = new ArrayList<>();
    
    private String getData(String data) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        String dataString = null;
        String line;
        String[] ket;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals(data)) {
                if (ket[0].equals("Threshold ")) {
                    dataString = Arrays.toString(ket).replace("Threshold ", "");
                } else
                    dataString = ket[1].substring(1);
            }
        }
        return dataString;
    }
    
    private Map<String, String> getTruth(String data) throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader(data));
        Map<String, String> dataTruth = new HashMap<>();
        String line;
        String[] ip;
        while ((line = br.readLine()) != null) {
            ip = line.split(" ");
            dataTruth.put(line.substring(10, 29), ip[5]);
        }
        return dataTruth;
    }
    
    private double[] getThreshold() throws FileNotFoundException, IOException{
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        double threshold[] = new double[1024];
        String line;
        String[] ket, valPort;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Threshold ")) {
                for (int i = 1; i < ket.length; i++) {
                    valPort = ket[i].substring(1).split("-",0);
                    threshold[Integer.parseInt(valPort[0])] = Double.parseDouble(valPort[1]);
                }
            }
        }
        return threshold;
    }
    
    private void getListFile(String dirPath, List<File> files){
        File filePath = new File(dirPath);
        File[] listFile = filePath.listFiles();         
        
        for (File file : listFile) {
            if (file.isFile()) {
                files.add(file);
            } 
            
            else if (file.isDirectory()) {
                getListFile(file.getAbsolutePath(), files);
            }
        }
    }
    
    private String getDateTime(){
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MMMM_EEEE-dd_hha");
        Date dateNow = new Date();
        return date.format(dateNow);    
    }
    
    private void incremental(String proto, double[] ngram, int port){
        if (proto.equals("TCP")) {
            for (DataModel dataTcp : modelTcp) {
                if (dataTcp.getDstPort() == port) {
                    int numNew = dataTcp.getTotalModel();
                    double[] sumNew = dataTcp.getSumData();
//                    System.out.println("old sum "+Arrays.toString(sumNew));
                    for (int i = 0; i < sumNew.length; i++) {
                        sumNew[i] = sumNew[i]+ngram[i];
                    }                    
//                    System.out.println("new sum "+Arrays.toString(sumNew));
                    double[] quadraticNew = dataTcp.getQuadraticData();
//                    System.out.println("old kuadrat "+Arrays.toString(quadraticNew));
                    for (int i = 0; i < quadraticNew.length; i++) {
                        quadraticNew[i] = quadraticNew[i]+Math.pow(ngram[i], 2);
                    }
//                    System.out.println("new kuadrat "+Arrays.toString(quadraticNew));
                    double[] meanNew = dataTcp.getMeanData();
//                    System.out.println("old mean "+Arrays.toString(meanNew));
                    for (int i = 0; i < meanNew.length; i++) {
                        meanNew[i] = (meanNew[i]*numNew+ngram[i])/(numNew+1);
                    }
//                    System.out.println("new mean"+Arrays.toString(meanNew));
                    numNew = numNew + 1;
                    double[] deviasiNew = dataTcp.getDeviasiData();
//                    System.out.println("old deviasi"+Arrays.toString(deviasiNew));
                    for (int i = 0; i < deviasiNew.length; i++) {
                        deviasiNew[i] = Math.sqrt((numNew*quadraticNew[i]-Math.pow(sumNew[i], 2))/(numNew*(numNew-1)));
                    }
//                    System.out.println("new deviasi"+Arrays.toString(deviasiNew));
                    dataTcp.setSumData(sumNew);
                    dataTcp.setDeviasiData(deviasiNew);
                    dataTcp.setMeanData(meanNew);
                    dataTcp.setQuadraticData(quadraticNew);
                    dataTcp.setTotalModel(numNew);
                }
            }
        }
        
        else if (proto.equals("UDP")) {
            for (DataModel dataUdp : modelUdp) {
                if (dataUdp.getDstPort() == port) {
                    int numNew = dataUdp.getTotalModel();
                    double[] sumNew = dataUdp.getSumData();
                    for (int i = 0; i < sumNew.length; i++) {
                        sumNew[i] = sumNew[i]+ngram[i];
                    }
                    double[] quadraticNew = dataUdp.getQuadraticData();
                    for (int i = 0; i < quadraticNew.length; i++) {
                        quadraticNew[i] = quadraticNew[i]+Math.pow(ngram[i], 2);
                    }
                    double[] meanNew = dataUdp.getMeanData();
                    for (int i = 0; i < meanNew.length; i++) {
                        meanNew[i] = (meanNew[i]*numNew+ngram[i])/(numNew+1);
                    }
                    numNew = numNew + 1;
                    double[] deviasiNew = dataUdp.getDeviasiData();
                    for (int i = 0; i < deviasiNew.length; i++) {
                        deviasiNew[i] = Math.sqrt((numNew*quadraticNew[i]-Math.pow(sumNew[i], 2))/(numNew*(numNew-1)));
                    }
                    dataUdp.setSumData(sumNew);
                    dataUdp.setDeviasiData(deviasiNew);
                    dataUdp.setMeanData(meanNew);
                    dataUdp.setQuadraticData(quadraticNew);
                    dataUdp.setTotalModel(numNew);
                }
            }
        }
    }
    
    public static void main(String[] args) throws IOException {
        boolean snifferStatus;
        int input, totalPacket, windowSize, packetType, portPacket, portTest, countFile = 1;
        double mDist, threshold, sFactor;
        double[] portTh;
        long start, end;
        String time, thresholdAll, dirPath, filePath, fileSave;
        String[] fileName, dateTime;
        File fileLog, fileRecord, fileRunning;
        FileWriter fwLog, fwRecord, fwRunning;
        List<Thread> threadTraining, threadFile;
        List<File> fileData;
        ArrayList<Double> attackPacket, freePacket;
        ArrayList<Double[]> dataTraining;
        Map<Integer, Integer> portTrainingTcp, portTrainingUdp;
        Map<String, String> dataTruth;
        Mahalanobis mahalanobis;
        IDS ids;
        PacketReader pr;
        DataTraining dt;
        ids = new IDS();
        time = ids.getDateTime();
        dateTime = time.split("_");
        fileRunning = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]);
        if (!fileRunning.exists()) {
            fileRunning.mkdirs();
        }
        fileRunning = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Running_Test_log");
        if (!fileRunning.exists()) {
            fileRunning.createNewFile();
        }
        fwRunning = new FileWriter(fileRunning,true);
        Scanner sc = new Scanner(System.in);
        
        OUTER:           
        while (true) {
            System.out.println("1) Training Dataset\n"+"2) Sniffer Testing\n"+"3) Pcap Testing\n"+"Type(1 or 2 or 3): ?");
            input = sc.nextInt();
            switch (input) {
                case 1:
                    System.out.println("Training Dataset...");
                    fwRunning.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                    fwRunning.append("Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a").format(Calendar.getInstance().getTime()))+"\n");
                    fwRunning.append("Training Dataset...\n");
                    ids = new IDS();
                    dirPath = ids.getData("Data Training ");
                    packetType = Integer.parseInt(ids.getData("Packet Type "));
                    threadFile = new ArrayList<>();
                    fileData = new ArrayList<>();
                    System.out.println("Dataset directory : "+dirPath);
                    fwRunning.append("Dataset directory : "+dirPath+"\n");
                    start = System.currentTimeMillis();
                    ids.getListFile(dirPath, fileData);
                    System.out.println("Total dataset : "+fileData.size()+" file");
                    fwRunning.append("Total dataset : "+fileData.size()+" file\n");
                    for (File fileDataset : fileData) {
//                        if (threadFile.size() == core) {
//                            for (Thread threads : threadFile) {
//                                try {
//                                    threads.join();
//                                } catch (InterruptedException ex) {
//                                    Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
//                                }
//                            }
//                            threadFile.clear();
//                        } 
                        JpcapCaptor captor = JpcapCaptor.openFile(fileDataset.toString());
                        pr = new PacketReader(countFile, captor, input, datasetTcp, datasetUdp, dataTest, packetType);
                        Thread threadFiles = new Thread(pr);
                        threadFiles.start();
                        System.out.println("Processing dataset ke-"+countFile+" "+fileDataset.toString());
                        fwRunning.append("Processing dataset ke-"+countFile+" "+fileDataset.toString()+"\n");
                        threadFile.add(threadFiles);
                        countFile++;
                    }
                    
                    for (Thread threads : threadFile) {
                        try {
                            threads.join();
                        } catch (InterruptedException ex) {
                            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    countFile = 1;
                    end = System.currentTimeMillis();
                    pr = new PacketReader();
                    totalPacket = pr.getCountPacket();
                    System.out.println("Total Packet : "+totalPacket+" packet");
                    fwRunning.append("Total Packet : "+totalPacket+" packet"+"\n");
                    System.out.println("Total TCP Connection : "+datasetTcp.size()+" connections");
                    fwRunning.append("Total TCP Connection : "+datasetTcp.size()+" connections\n");
                    System.out.println("Total UDP Connection : "+datasetUdp.size()+" connections"); 
                    fwRunning.append("Total UDP Connection : "+datasetUdp.size()+" connections\n");
                    System.out.println("Total time of processing dateset : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    fwRunning.append("Total time of processing dateset : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds\n");
                    System.out.println("Create Model Data Training...");
                    fwRunning.append("Create Model Data Training...\n");
                    start = System.currentTimeMillis();
                    if (!datasetTcp.isEmpty()) {
                        portTrainingTcp = new HashMap<>();
                        for (DataPacket dpTcp : datasetTcp) {
                            portPacket = dpTcp.getDstPort();
                            if (portTrainingTcp.containsKey(portPacket)) continue;
                            portTrainingTcp.put(portPacket, portPacket);                            
                        }                        

                        threadTraining = new ArrayList<>();
                        for (Map.Entry<Integer, Integer> entry : portTrainingTcp.entrySet()) {
                            Integer port = entry.getKey();
                            dt = new DataTraining("TCP", datasetTcp, datasetUdp, modelTcp, modelUdp, port);
                            Thread threadDt = new Thread(dt);
                            threadDt.start();
                            try {
                                threadDt.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }                                           
                    }  
                    
                    if (!datasetUdp.isEmpty()) {
                        portTrainingUdp = new HashMap<>();
                        for (DataPacket dpUdp : datasetUdp) {
                            portPacket = dpUdp.getDstPort();
                            if (portTrainingUdp.containsKey(portPacket)) continue;
                            portTrainingUdp.put(portPacket, portPacket);                            
                        }
                        
                        threadTraining = new ArrayList<>();
                        for (Map.Entry<Integer, Integer> entry : portTrainingUdp.entrySet()) {
                            Integer port = entry.getKey();
                            dt = new DataTraining("UDP", datasetTcp, datasetUdp, modelTcp, modelUdp, port);
                            Thread threadDt = new Thread(dt);
                            threadDt.start();
                            try {
                                threadDt.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                        }
                    }
                    
//                    for (DataModel modelTcp : modelTcp) {
//                        System.out.println(modelTcp.getDstPort());
//                        System.out.println(modelTcp.getTotalModel());
//                        System.out.println(Arrays.toString(modelTcp.getSumData()));
//                        System.out.println(Arrays.toString(modelTcp.getMeanData()));
//                        System.out.println(Arrays.toString(modelTcp.getDeviasiData()));
//                        System.out.println(Arrays.toString(modelTcp.getQuadraticData()));
//                    }
                    
                    end = System.currentTimeMillis();
                    System.out.println("Total time of training dataset : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                    fwRunning.append("Total time of training dataset : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds\n");
                    fwRunning.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                    datasetTcp.clear();
                    datasetUdp.clear();
                    
                    break;
                
                case 2:
                    if (!modelTcp.isEmpty() | !modelUdp.isEmpty()) {
                        System.out.println("Sniffer Testing...");
                        fwRunning.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                        fwRunning.append("Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a").format(Calendar.getInstance().getTime()))+"\n");
                        fwRunning.append("Sniffer Testing...\n");
//                        ids = new IDS();
//                        time = ids.getDateTime();
//                        windowSize = ids.getWindowSize();
                        NetworkInterface[] device = JpcapCaptor.getDeviceList();
                        
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
//                        System.out.println("Window Size : "+windowSize);
                        System.out.println("Choose active network interface...(0,1,2)!");
                        sc = new Scanner(System.in);
                        input = sc.nextInt();
                        System.out.println("Selected network interface name : "+device[input].name);
                        fwRunning.append("Selected network interface name : "+device[input].name+"\n");
                        snifferStatus = true;
                        while (snifferStatus) {                            
                            System.out.println("Start Sniffing...");
                            ids = new IDS();
                            time = ids.getDateTime();
                            windowSize = Integer.parseInt(ids.getData("Window Size "));
                            thresholdAll = ids.getData("Threshold "); 
                            portTh = ids.getThreshold();
                            sFactor = Double.parseDouble(ids.getData("Smoothing Factor "));
                            dateTime = time.split("_");
                            fileLog = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]);
                            if (!fileLog.exists()) {
                                fileLog.mkdirs();
                            }
                            fileLog = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Sniff_Result_log_"+dateTime[3]);
                            fileRecord = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Sniff_Record_log_"+dateTime[3]);
                            fileRunning = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Running_Test_log");
                            if (!fileLog.exists() | !fileRecord.exists()) {
                                fileLog.createNewFile();
                                fileRecord.createNewFile();
                                fileRunning.createNewFile();
                            }
                            fwLog = new FileWriter(fileLog,true);
                            fwRecord = new FileWriter(fileRecord, true);
                            freePacket = new ArrayList<>();
                            attackPacket = new ArrayList<>();
                            System.out.println("Window Size : "+windowSize);
                            fwRunning.append("Window Size : "+windowSize+"\n");
                            PacketSniffer ps = new PacketSniffer(device[input], input, dataTest, windowSize);
                            Thread threadPs = new Thread(ps);
                            threadPs.start();
                            try {
                                threadPs.join();
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }

                            System.out.println("Total Paket Testing : "+dataTest.size()+" connections");
                            fwRunning.append("Total Paket Testing : "+dataTest.size()+" connections\n");
                            fwLog.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwLog.append("#  Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a  #").format(Calendar.getInstance().getTime()))+"\n");
                            fwLog.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwLog.append("Protokol | Date | Source | Destination | Distance\n");
                            fwLog.append("-------------------------------------------------\n");
                            fwRecord.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwRecord.append("#  Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a  #").format(Calendar.getInstance().getTime()))+"\n");
                            fwRecord.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwRecord.append("Protokol | Date | Source | Destination | Keterangan\n");
                            fwRecord.append("---------------------------------------------------\n");
                            System.out.println("Threshold : "+thresholdAll.substring(4, ids.getData("Threshold ").length()-1));
                            fwRunning.append("Threshold : "+thresholdAll.substring(4, ids.getData("Threshold ").length()-1)+"\n");
                            System.out.println("Smoothing Factor : "+sFactor);
                            fwRunning.append("Smoothing Factor : "+sFactor+"\n");
                            System.out.println("Calculating Mahalanobis Distance...");

                            start = System.currentTimeMillis();

                            for (DataPacket dataPacketTes : dataTest) {     

                                if ("TCP".equals(dataPacketTes.getProtokol())) {
                                    for (DataModel dataTcp : modelTcp) {
                                        if (dataTcp.getDstPort() == dataPacketTes.getDstPort()) {
                                            mahalanobis = new Mahalanobis();
                                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataTcp.getMeanData(), dataTcp.getDeviasiData(),sFactor);
                                            if (mDist > portTh[dataPacketTes.getDstPort()]) {
                                                fwLog.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
                                                fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
                                                fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Attack"+"\n");
                                                attackPacket.add(mDist);
                                            } else {                                            
                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Normal"+"\n");
                                                ids.incremental("TCP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
                                                freePacket.add(mDist);
                                            }
                                        }
                                    }
                                }

                                else if ("UDP".equals(dataPacketTes.getProtokol())) {
                                    for (DataModel dataUdp : modelUdp) {
                                        if (dataUdp.getDstPort() == dataPacketTes.getDstPort()) {
                                            mahalanobis = new Mahalanobis();
                                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataUdp.getMeanData(), dataUdp.getDeviasiData(),sFactor);
                                            if (mDist > portTh[dataPacketTes.getDstPort()]) {
                                                fwLog.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
                                                fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
                                                fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                                                fwRecord.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Attack"+"\n");
                                                attackPacket.add(mDist);
                                            } else {
                                                fwRecord.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Normal"+"\n");
                                                ids.incremental("UDP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
                                                freePacket.add(mDist);
                                            } 
                                        }
                                    }
                                }
                            }

                            fwLog.close();
                            fwRecord.close();
                            end = System.currentTimeMillis();
                            System.out.println("Total time of sniffer testing :  "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
                            System.out.println("Total attack packet: "+attackPacket.size());
                            fwRunning.append("Total attack packet: "+attackPacket.size()+"\n");
                            System.out.println("Total free packet: "+freePacket.size()); 
                            fwRunning.append("Total free packet: "+freePacket.size()+"\n");
                            dataTest.clear();
                            snifferStatus = Integer.parseInt(ids.getData("Sniffer Status ")) != 0;
                            if (!snifferStatus) {
                                fwRunning.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                            }
                        }          
                    }
                    System.out.println("Please choose Training Dataset First!");
                    
                    break;
                
                case 3:
                    if (!modelTcp.isEmpty() | !modelUdp.isEmpty()) {
                        System.out.println("Pcap Testing...");
                        fwRunning.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                        fwRunning.append("Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a").format(Calendar.getInstance().getTime()))+"\n");
                        fwRunning.append("Pcap Testing...\n");
                        ids = new IDS();
                        time = ids.getDateTime();
                        thresholdAll = ids.getData("Threshold "); 
                        portTh = ids.getThreshold();
                        portTest = Integer.parseInt(ids.getData("Port "));
                        sFactor = Double.parseDouble(ids.getData("Smoothing Factor "));
                        packetType = Integer.parseInt(ids.getData("Packet Type "));
                        filePath = ids.getData("Data Testing ");
                        System.out.println("Data testing directory : "+filePath);
                        fwRunning.append("Data testing directory : "+filePath+"\n");
                        fileData = new ArrayList<>();
                        ids.getListFile(filePath, fileData);
                        for (File fileDataTesting : fileData) {
                            dateTime = time.split("_");
                            fileName = fileDataTesting.toString().replace("/", "-").split("-");
                            fileSave = fileName[fileName.length-2]+"_"+fileName[fileName.length-1].replace(".", "_");
                            fileLog = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]);
                            if (!fileLog.exists()) {
                                fileLog.mkdirs();
                            }
                            fileLog = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Pcap_Result_log_"+fileSave);
                            fileRecord = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Pcap_Record_log_"+fileSave);
                            fileRunning = new File(dateTime[0]+"/"+dateTime[1]+"/"+dateTime[2]+"/Running_Test_log");
                            if (!fileLog.exists() | !fileRecord.exists()) {
                                fileLog.createNewFile();
                                fileRecord.createNewFile();
                                fileRunning.createNewFile();
                            }
                            fwLog = new FileWriter(fileLog,true);
                            fwRecord = new FileWriter(fileRecord, true);
                            freePacket = new ArrayList<>();
                            attackPacket = new ArrayList<>();
                            dataTruth = new HashMap<>();
                            dataTruth = ids.getTruth(fileName[fileName.length-2]);
                            JpcapCaptor captor = JpcapCaptor.openFile(fileDataTesting.toString());                        
                            pr = new PacketReader(countFile, captor, input, datasetTcp, datasetUdp, dataTest, packetType);
                            Thread threadTest = new Thread(pr);
                            threadTest.start();
                            System.out.println("Processing data testing ke-"+countFile+" "+fileDataTesting.toString());
                            fwRunning.append("Processing data testing ke-"+countFile+" "+fileDataTesting.toString()+"\n");
                            try {
                                threadTest.join();                        
                            } catch (InterruptedException ex) {
                                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                            }
                            
                            System.out.println("Total Paket Testing : "+dataTest.size()+" connections");
                            fwRunning.append("Total Paket Testing : "+dataTest.size()+" connections\n");
                            countFile++;                            
                            
                            fwLog.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwLog.append("#  Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a  #").format(Calendar.getInstance().getTime()))+"\n");
                            fwLog.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwLog.append("Protokol | Date | Source | Destination | Distance\n");
                            fwLog.append("-------------------------------------------------\n");
                            fwRecord.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwRecord.append("#  Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a  #").format(Calendar.getInstance().getTime()))+"\n");
                            fwRecord.append("+++++++++++++++++++++++++++++++++++++++++++\n");
                            fwRecord.append("Protokol | Date | Source | Destination | Keterangan\n");
                            fwRecord.append("---------------------------------------------------\n");
                            System.out.println("Threshold : "+thresholdAll.substring(4, ids.getData("Threshold ").length()-1));
                            fwRunning.append("Threshold : "+thresholdAll.substring(4, ids.getData("Threshold ").length()-1)+"\n");
                            System.out.println("Smoothing Factor : "+sFactor);
                            fwRunning.append("Smoothing Factor : "+sFactor+"\n");
                            System.out.println("Calculating Mahalanobis Distance...");

                            start = System.currentTimeMillis();                         

                            for (DataPacket dataPacketTes : dataTest) {     
                                if ("TCP".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() == portTest) {
                                    for (DataModel dataTcp : modelTcp) {
                                        if (dataTcp.getDstPort() == dataPacketTes.getDstPort()) {
                                            mahalanobis = new Mahalanobis();
                                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataTcp.getMeanData(), dataTcp.getDeviasiData(),sFactor);
                                            if (dataTruth.containsKey(dataPacketTes.getTimePacket())) {
                                                fwLog.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
    //                                            fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
    //                                            fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
                                                attackPacket.add(mDist);
                                            }
                                            
//                                            System.out.println("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
//                                            if (mDist > portTh[dataPacketTes.getDstPort()]) {
//                                                fwLog.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
//                                                fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
//                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
//                                                fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
//                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Attack\n");
//                                                attackPacket.add(mDist);
//                                            } else {
//                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Normal\n");
//                                                //ids.incremental("TCP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
//                                                freePacket.add(mDist);
//                                            }                                           
                                        }
                                    }
                                }

                                else if ("UDP".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() == portTest) {
                                    for (DataModel dataUdp : modelUdp) {
                                        if (dataUdp.getDstPort() == dataPacketTes.getDstPort()) {
                                            mahalanobis = new Mahalanobis();
                                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataUdp.getMeanData(), dataUdp.getDeviasiData(),sFactor);
                                            if (dataTruth.containsKey(dataPacketTes.getTimePacket())) {
                                                fwLog.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
    //                                            fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
    //                                            fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                                                fwRecord.append("TCP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
                                                attackPacket.add(mDist);
                                            }
//                                            System.out.println("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
//                                            if (mDist > portTh[dataPacketTes.getDstPort()]) {
//                                                fwLog.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | "+Math.round(mDist*100.0)/100.0+"\n");
//                                                fwLog.append("+++++++++++++++++++++++++++++++++++++START++++++++++++++++++++++++++++++++++++\n");
//                                                fwLog.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
//                                                fwLog.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
//                                                fwRecord.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Attack\n");
//                                                attackPacket.add(mDist);
//                                            } else {
//                                                fwRecord.append("UDP | "+dataPacketTes.getTimePacket()+" | "+dataPacketTes.getSrcIP()+":"+dataPacketTes.getSrcPort()+" | "+dataPacketTes.getDstIP()+":"+dataPacketTes.getDstPort()+" | Normal\n");
//                                                //ids.incremental("UDP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
//                                                freePacket.add(mDist);
//                                            }                                            
                                        }
                                    }
                                }
                            }
                            if (!attackPacket.isEmpty()) {
                                System.out.println("Max "+portTest+" : "+Collections.max(attackPacket));
                                System.out.println("Min "+portTest+" : "+Collections.min(attackPacket));
                                fwLog.append("Max "+portTest+" : "+Collections.max(attackPacket)+"\n");
                                fwLog.append("Min "+portTest+" : "+Collections.min(attackPacket)+"\n");
                            }                           
                            fwLog.close();
                            fwRecord.close();
                            end = System.currentTimeMillis();
                            System.out.println("Total time is : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");  
                            fwRunning.append("Total time is : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds\n");
                            System.out.println("Total attack packet: "+attackPacket.size());
                            fwRunning.append("Total attack packet: "+attackPacket.size()+"\n");
                            System.out.println("Total free packet: "+freePacket.size()); 
                            fwRunning.append("Total free packet: "+freePacket.size()+"\n");
                            dataTest.clear();
                        }
                        countFile = 1;
                        fwRunning.append("++++++++++++++++++++++++++++++++++++++END+++++++++++++++++++++++++++++++++++++\n");
                    }
                    
                    else
                        System.out.println("Please choose Training Dataset First!");
                    
                    break;
                
                default:
                    break OUTER;
            }
        }
        fwRunning.close();
    }
    
}
