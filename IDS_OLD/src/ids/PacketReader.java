/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author Agus
 */
public class PacketReader {
    private JpcapCaptor captor;
    private PacketReconst pr;    
    private TCPPacket tcp;
    private UDPPacket udp;
    private String tuples;
    Date dateNow = new Date();
    ArrayList<Thread> threads;
    SimpleDateFormat sdf = new SimpleDateFormat("E-yyyy.MM.dd-hha");
    File sniffer = new File("../result/sniffer_"+sdf.format(dateNow)+".txt");    
    File ngram = new File("../result/ngram_"+sdf.format(dateNow)+".txt");
    FileWriter fwp;
    FileWriter fwn;
    private Map<String, PacketReconst.BodyData> packetBody = new HashMap<String, PacketReconst.BodyData>();
    private List<String> list;
    
    public PacketReader(JpcapCaptor captor){
//        threads = new ArrayList<>();
//        while (true) {            
//            Packet packet = captor.getPacket();
//            if(packet == null || packet == Packet.EOF) break;
//            if(packet instanceof TCPPacket){
//                tcp = (TCPPacket) packet;
//                PacketReconst.BodyData tcpBodyData;
//                tuples = tcp.src_ip.toString().substring(1)+"("+tcp.src_port+")"+"-"+tcp.dst_ip.toString().substring(1)+"("+tcp.dst_port+")";
//                if(packetBody.containsKey(tuples)){
//                    tcpBodyData = packetBody.get(tuples);
//                    tcpBodyData.addBytes(tcp.data);
//                }else{
//                    pr = new PacketReconst(threads, tuples, tcp.data, packetBody);
//                    Thread t = new Thread(pr);
//                    t.start();
//                    //System.out.println("Running: "+t.getName());
//                    threads.add(t);
//                }
//            }else if(packet instanceof UDPPacket){
//                udp = (UDPPacket) packet;
//                PacketReconst.BodyData udpBodyData;
//                tuples = udp.src_ip.toString().substring(1)+"("+udp.src_port+")"+"-"+udp.dst_ip.toString().substring(1)+"("+udp.dst_port+")";
//                if(packetBody.containsKey(tuples)){
//                    udpBodyData = packetBody.get(tuples);
//                    udpBodyData.addBytes(udp.data);
//                }else{
//                    pr = new PacketReconst(threads, tuples, udp.data, packetBody);
//                    Thread t = new Thread(pr);
//                    t.start();
//                    //System.out.println("Running: "+t.getName());
//                    threads.add(t);
//                }
//            }
//            
//        }
//        for(Thread thread : threads){
//            try {
//                thread.join();
//            } catch (InterruptedException ex) {
//                Logger.getLogger(PacketReader.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
        //Display();
        Directory("/home/agus/AJK/IDS/result");
        captor.close();
    }
    
    public void Display(){
        for (Map.Entry<String, PacketReconst.BodyData> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            PacketReconst.BodyData value = entry.getValue();
            System.out.println(key);
        }
    }
    
    public void Directory(String folder){    
        int count = 0;
        File file = new File(folder);
        File[] listFile = file.listFiles();
        for(int i=0; i< listFile.length; i++){
            if(listFile[i].isFile()){
                //System.out.println("File: "+listFile[i].getName());
                count++;
                list = new ArrayList<>();
                FileReader fr = new FileReader(listFile[i].getAbsolutePath(), list);
                Thread tf = new Thread(fr);
                tf.start();
                //System.out.println("Running: "+tf.getName()+"_"+tf.getId());
            }else if(listFile[i].isDirectory()){
                System.out.println("Directory: "+listFile[i].getName());
            }
        }
        System.out.println("total file: "+count);
//        for(String str:list) {
//            System.out.println(str);
//        }
//        System.out.println(list.size());       
    }
    
    public void Tampilin(List<String> list) {
        
=======
public class PcapReader implements Runnable{
    private JpcapCaptor captor;  
    private TCPPacket tcp;
    private UDPPacket udp;
    private int len, i, max, ascii, n;
    private double[] numChars;
    private String tuples, os, dirPath, filePath, ngram, data;
    private Map<String, BodyData> packetBody = new HashMap<String, BodyData>();    
    Ngram ng = new Ngram();
    ArrayList<Hasil> hasil;
    
    public PcapReader(JpcapCaptor captor, int n, ArrayList<Hasil> hasil){
        this.captor = captor;        
        this.n = n;
        this.hasil = hasil;
    }
    
    public void run(){
        while (true) {
            Packet packet = captor.getPacket();
            synchronized(captor){                
                if(packet == null || packet == Packet.EOF) break;
                if(packet instanceof TCPPacket){
                    tcp = (TCPPacket) packet;
                    BodyData tcpBodyData; 
                    tuples = tcp.src_ip.toString().substring(1)+"("+tcp.src_port+")"+"-"+tcp.dst_ip.toString().substring(1)+"("+tcp.dst_port+")";
                    if(packetBody.containsKey(tuples)){
                        tcpBodyData = packetBody.get(tuples);
                        tcpBodyData.addBytes(tcp.data);
                    } 
                    else { 
                        tcpBodyData = new BodyData(tcp.data); 
                        packetBody.put(tuples, tcpBodyData); 
                    } 
                }
//                else if(packet instanceof UDPPacket){
//                    udp = (UDPPacket) packet;
//                    BodyData udpBodyData; 
//                    tuples = udp.src_ip.toString().substring(1)+"("+udp.src_port+")"+"-"+udp.dst_ip.toString().substring(1)+"("+udp.dst_port+")";
//                    if(packetBody.containsKey(tuples)){
//                        udpBodyData = packetBody.get(tuples);
//                        udpBodyData.addBytes(udp.data);
//                    } else { 
//                        udpBodyData = new BodyData(udp.data); 
//                        packetBody.put(tuples, udpBodyData); 
//                    } 
//                }
            }            
        } 
        System.out.println("Finished");
        Proses(n);
    }
    
    private class BodyData{
        byte[] bytes = null;
        
        public BodyData(byte[] bytes){
            this.bytes = bytes;      
        }
        
        public void addBytes(byte[] bytes){
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
            try { 
                outputStream.write(this.bytes);
                outputStream.write(bytes); 
                this.bytes = outputStream.toByteArray(); 
            } catch (IOException ex) {
                Logger.getLogger(PcapReader.class.getName()).log(Level.SEVERE, null, ex);
            }            
        }
        
        public byte[] getBytes(){
            return bytes;
        }
    }
    
    public void Proses(int n){
        int z = n;
        if(z == 1){
//            dirPath = System.getProperty("user.dir");
//            os = System.getProperty("os.name").toLowerCase();        
//            if(os.indexOf("win") >= 0){
//                filePath = dirPath+"\\result\\";        
//            } else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("mac") >0 ){
//                filePath = dirPath+"/result/";
//            }
//            File file = new File(filePath+"ngram.txt");
            for (Map.Entry<String, BodyData> entry : packetBody.entrySet()) {
                String key = entry.getKey();
                BodyData value = entry.getValue();            
                if(value.bytes.length != 0){
                    try {                    
                        numChars = ng.Ngram(new String(value.bytes, StandardCharsets.UTF_8));
                        //System.out.println(Arrays.toString(numChars));
                        //Collection.addAll(hasil, numChars);
                        //hasil.add(new Hasil(numChars, z));
                        packetBody.remove(value);
                    } catch (IOException ex) {
                        Logger.getLogger(PcapReader.class.getName()).log(Level.SEVERE, null, ex);
                    }           
                }
            }
            System.out.println(packetBody.size());            
        }
        else if (z == 3){
            for (Map.Entry<String, BodyData> entry : packetBody.entrySet()) {
                String key = entry.getKey();
                BodyData value = entry.getValue();            
                if(value.bytes.length != 0){
                    try {                    
                        numChars = ng.Ngram(new String(value.bytes, StandardCharsets.UTF_8));
                        //System.out.println(Arrays.toString(numChars));
                        //hasil.add(new Hasil(numChars, z));
                        packetBody.remove(value);
                    } catch (IOException ex) {
                        Logger.getLogger(PcapReader.class.getName()).log(Level.SEVERE, null, ex);
                    }           
                }
            }
            System.out.println(packetBody.size());            
        }
        
    }
    
    private double[] addDouble(double[] obj, double nObj){
        ArrayList<Double> temp = new ArrayList<Double>(Arrays.asList(ArrayUtils.toObject(obj)));
        temp.add(nObj);
        return ArrayUtils.toPrimitive(temp.toArray(new Double[]{}));
>>>>>>> remotes/origin/exp
    }
    
}
