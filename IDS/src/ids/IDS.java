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
    static int ascii = 256, packetCounter = 1, core = Runtime.getRuntime().availableProcessors();
    String line;
    String[] ket;
    BufferedReader br;
    static ArrayList<DataPacket> datasetTcp = new ArrayList<>();
    static ArrayList<DataPacket> datasetUdp = new ArrayList<>();
    static ArrayList<DataPacket> dataTest = new ArrayList<>();
    static ArrayList<DataModel> modelTcp = new ArrayList<>();
    static ArrayList<DataModel> modelUdp = new ArrayList<>();
    
    String getDatasetDir() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String dataTraining = null;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Data Training ")) {
                dataTraining = ket[1].substring(1);
            }
        }
        return dataTraining;
    }
    
    double getThreshold(int port) throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double threshold = 0;
        String[] valPort;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Threshold ")) {
                for (int i = 1; i < ket.length; i++) {
                    valPort = ket[i].substring(1).split("-",0);
                    if (Integer.parseInt(valPort[0]) == port) {
                        threshold = Double.parseDouble(valPort[1]);
                    }
                }
            }
        }
        return threshold;
    }
    
    String getTh() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        String th = null;
        String[] valPort;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Threshold ")) {
                th = Arrays.toString(ket).replace("Threshold ", "");
            }
        }
        return th;
    }
    
    double getSFactor() throws FileNotFoundException, IOException{
        br = new BufferedReader(new FileReader("conf"));
        double sFactor = 0;
        while ((line = br.readLine()) != null) {
            ket = line.split(":", 0);
            if (ket[0].equals("Smoothing Factor ")) {
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
            if (ket[0].equals("Limit Packet ")) {
                counterPacket = Integer.parseInt(ket[1].substring(1));
            }
        }
        return counterPacket;
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
    
    String getDateTime(){
        SimpleDateFormat date = new SimpleDateFormat("E_yyyy-MM-dd_hha");
        Date dateNow = new Date();
        return date.format(dateNow);    
    }
    
    private void incremental(String proto, double[] ngram, int port){
        if (proto.equals("TCP")) {
            for (DataModel dataTcp : modelTcp) {
                if (dataTcp.getDstPort() == port) {
                    int numNew = dataTcp.getTotalModel();
                    double[] quadraticNew = dataTcp.getQuadraticData();
                    for (int i = 0; i < quadraticNew.length; i++) {
                        quadraticNew[i] = quadraticNew[i]+Math.pow(ngram[i], 2);
                    }
                    double[] meanNew = dataTcp.getMeanData();
                    for (int i = 0; i < meanNew.length; i++) {
                        meanNew[i] = (meanNew[i]*numNew+ngram[i])/(numNew+1);
                    }
                    numNew = numNew + 1;
                    double[] deviasiNew = dataTcp.getDeviasiData();
                    for (int i = 0; i < deviasiNew.length; i++) {
                        deviasiNew[i] = Math.sqrt((numNew*quadraticNew[i]-Math.pow(meanNew[i]*numNew, 2))/(numNew*(numNew-1)));
                    }
                    dataTcp.setDeviasiData(deviasiNew);
                    dataTcp.setMeanData(meanNew);
                    dataTcp.setTotalModel(numNew);
                }
            }
        }
        
        else if (proto.equals("UDP")) {
            for (DataModel dataUdp : modelUdp) {
                if (dataUdp.getDstPort() == port) {
                    int numNew = dataUdp.getTotalModel();
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
                        deviasiNew[i] = Math.sqrt((numNew*quadraticNew[i]-Math.pow(meanNew[i]*numNew, 2))/(numNew*(numNew-1)));
                    }
                    dataUdp.setDeviasiData(deviasiNew);
                    dataUdp.setMeanData(meanNew);
                    dataUdp.setTotalModel(numNew);
                }
            }
        }
    }
    
    public static void main(String[] args) throws IOException {    
        int input, packetLimit, packetType, portPacket, portTesting, countFile = 1;
        double mDist, threshold, sFactor;
        double[] sumData, meanData, deviasiData;
        long start, end;
        String time, thresholdAll;
        String[] header, fileName, dateTime;
        List<Thread> threadTraining;
        List<Thread> threadFile;
        List<File> fileData;
        ArrayList<Double> valAttack;
        ArrayList<Double> valFree;
        ArrayList<Double[]> dataTraining;
        Map<Integer, Integer> portTrainingTcp;
        Map<Integer, Integer> portTrainingUdp;
        String dirPath, filePath;
        File file, fileLog, fileDir;
        FileWriter fw, fwFree, fwAttack;
        Mahalanobis mahalanobis;
        IDS ids;
        DataTraining dt;
        Scanner sc;
        
        System.out.println("Processing Dataset...");
        ids = new IDS();
        dirPath = ids.getDatasetDir();
        threadFile = new ArrayList<>();
        fileData = new ArrayList<>();
        System.out.println("Dataset directory : "+dirPath);
        start = System.currentTimeMillis();
        ids.getListFile(dirPath, fileData);
        System.out.println("Total dataset is : "+fileData.size()+" file");
        for (File fileDataset : fileData) {
            JpcapCaptor captor = JpcapCaptor.openFile(fileDataset.toString());
            PacketReader pr = new PacketReader(countFile, captor, datasetTcp, datasetUdp, dataTest, packetCounter);
            Thread threadFiles = new Thread(pr);
            threadFiles.start();
            System.out.println("Processing dataset ke-"+countFile+" "+fileDataset.toString());
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
        System.out.println("Total time of processing dateset is : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");

        System.out.println("Training Dataset...");
        start = System.currentTimeMillis();
        if (datasetTcp.size() != 0) {
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

        if (datasetUdp.size() != 0) {
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

        end = System.currentTimeMillis();
        System.out.println("Total time of training dataset is : "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
        datasetTcp.clear();
        datasetUdp.clear();
        
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
        
        System.out.println("Choose active network interface...(0,1,2)!");
        sc = new Scanner(System.in);
        input = sc.nextInt();
                   
        while (true) {
            ids = new IDS();           
            packetLimit = ids.getCounterPacket();
            time = ids.getDateTime();
            System.out.println(device[input].name);
            System.out.println("Start Sniffing...");
            PacketSniffer ps = new PacketSniffer(device[input], 1, dataTest, packetLimit);
            Thread threadPs = new Thread(ps);
            threadPs.start();
            try {
                threadPs.join();
            } catch (InterruptedException ex) {
                Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
            }

            thresholdAll = ids.getTh(); 
            sFactor = ids.getSFactor();
            dateTime = time.split("_");
            fileDir = new File(dateTime[0]+"_"+dateTime[1]);
            if (!fileDir.exists()) {
                fileDir.mkdir();
            }
            fileLog = new File(dateTime[0]+"_"+dateTime[1]+"/Sniffing_log_"+dateTime[2]);
            if (!fileLog.exists()) {
                fileLog.createNewFile();
            }
            fw = new FileWriter(fileLog,true);
            fw.append(time+"\n");
            valAttack = new ArrayList<>();
            valFree = new ArrayList<>();
            System.out.println("Threshold : "+thresholdAll.substring(4, ids.getTh().length()-1));
            System.out.println("Smoothing Factor : "+sFactor);
            System.out.println("Calculating Mahalanobis Distance...");

            start = System.currentTimeMillis();

            for (DataPacket dataPacketTes : dataTest) {     

                if ("TCP".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                    for (DataModel dataTcp : modelTcp) {
                        if (dataTcp.getDstPort() == dataPacketTes.getDstPort()) {
                            mahalanobis = new Mahalanobis();
                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataTcp.getMeanData(), dataTcp.getDeviasiData(),sFactor);
                            if (mDist > ids.getThreshold(dataPacketTes.getDstPort())) {
                                valAttack.add(mDist);
                                fw.append(Math.round(mDist*100.0)/100.0+" -> "+dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+"\n");
                                fw.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
                            } else {
                                valFree.add(mDist);
                                ids.incremental("TCP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
                            }                                
                        }
                    }
                }

                else if ("UDP".equals(dataPacketTes.getProtokol()) && dataPacketTes.getDstPort() < 1024) {
                    for (DataModel dataUdp : modelUdp) {
                        if (dataUdp.getDstPort() == dataPacketTes.getDstPort()) {
                            mahalanobis = new Mahalanobis();
                            mDist = mahalanobis.distance(dataPacketTes.getNgram(), dataUdp.getMeanData(), dataUdp.getDeviasiData(),sFactor);
                            if (mDist > ids.getThreshold(dataPacketTes.getDstPort())) {
                                valAttack.add(mDist);
                                fw.append(Math.round(mDist*100.0)/100.0+" -> "+dataPacketTes.getSrcIP()+"-"+dataPacketTes.getSrcPort()+"-"+dataPacketTes.getDstIP()+"-"+dataPacketTes.getDstPort()+"\n");
                                fw.append(new String(dataPacketTes.getPacketData(), StandardCharsets.US_ASCII)+"\n");
                            } else {
                                valFree.add(mDist);
                                ids.incremental("UDP", dataPacketTes.getNgram(), dataPacketTes.getDstPort());
                            }                                
                        }
                    }
                }
            }

            fw.close();
            end = System.currentTimeMillis();
            System.out.println("Total time of sniffer testing is: "+(end-start)/3600000+" hour "+((end-start)%3600000)/60000+" minutes "+(((end-start)%3600000)%60000)/1000+" seconds");
            System.out.println("Total attack packet: "+valAttack.size());
            System.out.println("Total free packet: "+valFree.size());
            dataTest.clear();
        } 
    }
    
}
