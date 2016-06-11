/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.NetworkInterfaceAddress;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

/**
 *
 * @author Agus
 */
public class PacketSniffer {
<<<<<<< HEAD
    private DataPacket dp = new DataPacket();  
    private Ngram ngramData = new Ngram();
    private NetworkInterface[] device;
    private JpcapCaptor captor;    
    private PacketReconst pr;
    private TCPPacket tcp;
    private UDPPacket udp;
    private byte[] data;  
    private String input, print, str, info, ngramPrint, tuples;
    private int i, c, p=0;
    Date dateNow = new Date();
    SimpleDateFormat sdf = new SimpleDateFormat("E-yyyy.MM.dd-hha");
    File sniffer = new File("../result/sniffer_"+sdf.format(dateNow)+".txt");    
    File ngram = new File("../result/ngram_"+sdf.format(dateNow)+".txt");
    FileWriter fwp;
    FileWriter fwn;
    private Map<String, PacketReconst.BodyData> packetBody = new HashMap<String, PacketReconst.BodyData>();
    
    public PacketSniffer(){
        ArrayList<Thread> threads = new ArrayList<>();
        device = JpcapCaptor.getDeviceList();
        try {
            fwp = new FileWriter(sniffer);
            fwn = new FileWriter(ngram);
        } catch (IOException ex) {
            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        for(i=0; i<device.length; i++){
            System.out.println(i+": "+device[i].name + "(" + device[i].description+")");
            
            //print out its datalink name and description
            System.out.println("  datalink: "+device[i].datalink_name + "(" + device[i].datalink_description+")");

            //print out its MAC address
            System.out.print("  MAC address: ");
            for (byte b : device[i].mac_address)
                System.out.print(Integer.toHexString(b&0xff) + ":");
            System.out.println();

            //print out its IP address, subnet mask and broadcast address
            for (NetworkInterfaceAddress a : device[i].addresses)
                System.out.println("  address: "+a.address + " " + a.subnet + " "+ a.broadcast);
            System.out.println("\n");
        }
        
        c = Integer.parseInt(getInput("Pilih interface(0,1,2...): "));
        System.out.println(device[c].description);
        try {
            captor = JpcapCaptor.openDevice(device[c], 65535, false, 20);
        } catch (IOException ex) {
            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        if(!sniffer.exists()){
            try {
                sniffer.createNewFile();                  
            } catch (IOException ex) {
                Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        if(!ngram.exists()){
            try {
                ngram.createNewFile();                 
            } catch (IOException ex) {
                Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        while (true) {    
            Packet packet = captor.getPacket();
            if(packet !=  null){
                if(packet instanceof TCPPacket){
                tcp = (TCPPacket) packet;
                PacketReconst.BodyData tcpBodyData;
                tuples = tcp.src_ip.toString().substring(1)+"("+tcp.src_port+")"+"-"+tcp.dst_ip.toString().substring(1)+"("+tcp.dst_port+")";
                if(packetBody.containsKey(tuples)){
                    tcpBodyData = packetBody.get(tuples);
                    tcpBodyData.addBytes(tcp.data);
                    }else{
//                        pr = new PacketReconst(tuples, tcp.data, packetBody);
//                        Thread t = new Thread(pr);
//                        t.start();
//                        System.out.println("Running: "+t.getName()+"_"+t.getId());
//                        threads.add(t);
                    }
                }else if(packet instanceof UDPPacket){
                    udp = (UDPPacket) packet;
                    PacketReconst.BodyData udpBodyData;
                    tuples = udp.src_ip.toString().substring(1)+"("+udp.src_port+")"+"-"+udp.dst_ip.toString().substring(1)+"("+udp.dst_port+")";
                    if(packetBody.containsKey(tuples)){
                        udpBodyData = packetBody.get(tuples);
                        udpBodyData.addBytes(udp.data);
                    }else{
//                        pr = new PacketReconst(tuples, udp.data, packetBody);
//                        Thread t = new Thread(pr);
//                        t.start();
//                        System.out.println("Running: "+t.getName()+"_"+t.getId());
//                        threads.add(t);
                    }
                }
            }
            for(Thread thread : threads){
                try {
                    thread.join();
                } catch (InterruptedException ex) {
                    Logger.getLogger(PacketReader.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }        
    }
    
    public static String getInput(String c){
        String input = "";
        System.out.println(c);
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
        try {
            input = br.readLine();
        } catch (IOException ex) {
            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
        }
        return input;
    }
    
    String getPacket(Packet packet) throws IOException {   
        
        if(packet instanceof TCPPacket){
            tcp = (TCPPacket) packet;
            dp.setSrcIP(tcp.src_ip.toString().substring(1));
            dp.setDstIP(tcp.dst_ip.toString().substring(1));
            dp.setSrcPort(tcp.src_port);
            dp.setDstPort(tcp.dst_port);
        } 
        if(packet instanceof UDPPacket){
            udp = (UDPPacket) packet; 
            dp.setSrcIP(udp.src_ip.toString().substring(1));
            dp.setDstIP(udp.dst_ip.toString().substring(1));
            dp.setSrcPort(udp.src_port);
            dp.setDstPort(udp.dst_port);
        } 
        dp.setPacketData(packet.data);
        ngramPrint = ngramData.Ngram(new String(dp.getPacketData(), StandardCharsets.UTF_8));
        fwn.write("DataKe: "+p+" "+ngramPrint);
        fwn.write("\r\n");
        print = Integer.toString(p)+" -> "+dp.getSrcIP()+":"+dp.getSrcPort()+"-"+dp.getDstIP()+":"+dp.getDstPort()+" -> Data: "+new String(dp.getPacketData(), StandardCharsets.UTF_8);
        p++;
        return new String(print);
    }
=======
//    private DataPacket dp = new DataPacket();  
//    private Ngram ngramData = new Ngram();
//    private NetworkInterface[] device;
//    private JpcapCaptor captor;    
//    private PacketReconst pr;
//    private TCPPacket tcp;
//    private UDPPacket udp;
//    private byte[] data;  
//    private String input, print, str, info, ngramPrint, tuples;
//    private int i, c, p=0;
//    Date dateNow = new Date();
//    SimpleDateFormat sdf = new SimpleDateFormat("E-yyyy.MM.dd-hha");
//    File sniffer = new File("../result/sniffer_"+sdf.format(dateNow)+".txt");    
//    File ngram = new File("../result/ngram_"+sdf.format(dateNow)+".txt");
//    FileWriter fwp;
//    FileWriter fwn;
////    private Map<String, PacketReconst.BodyData> packetBody = new HashMap<String, PacketReconst.BodyData>();
//    
//    public PacketSniffer(){
//        ArrayList<Thread> threads = new ArrayList<>();
//        device = JpcapCaptor.getDeviceList();
//        try {
//            fwp = new FileWriter(sniffer);
//            fwn = new FileWriter(ngram);
//        } catch (IOException ex) {
//            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        for(i=0; i<device.length; i++){
//            System.out.println(i+": "+device[i].name + "(" + device[i].description+")");
//            
//            //print out its datalink name and description
//            System.out.println("  datalink: "+device[i].datalink_name + "(" + device[i].datalink_description+")");
//
//            //print out its MAC address
//            System.out.print("  MAC address: ");
//            for (byte b : device[i].mac_address)
//                System.out.print(Integer.toHexString(b&0xff) + ":");
//            System.out.println();
//
//            //print out its IP address, subnet mask and broadcast address
//            for (NetworkInterfaceAddress a : device[i].addresses)
//                System.out.println("  address: "+a.address + " " + a.subnet + " "+ a.broadcast);
//            System.out.println("\n");
//        }
//        
//        c = Integer.parseInt(getInput("Pilih interface(0,1,2...): "));
//        System.out.println(device[c].description);
//        try {
//            captor = JpcapCaptor.openDevice(device[c], 65535, false, 20);
//        } catch (IOException ex) {
//            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
//        if(!sniffer.exists()){
//            try {
//                sniffer.createNewFile();                  
//            } catch (IOException ex) {
//                Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        if(!ngram.exists()){
//            try {
//                ngram.createNewFile();                 
//            } catch (IOException ex) {
//                Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
//            }
//        }
//        
//        while (true) {    
//            Packet packet = captor.getPacket();
//            if(packet !=  null){
//                if(packet instanceof TCPPacket){
//                tcp = (TCPPacket) packet;
//                PacketReconst.BodyData tcpBodyData;
//                tuples = tcp.src_ip.toString().substring(1)+"("+tcp.src_port+")"+"-"+tcp.dst_ip.toString().substring(1)+"("+tcp.dst_port+")";
//                if(packetBody.containsKey(tuples)){
//                    tcpBodyData = packetBody.get(tuples);
//                    tcpBodyData.addBytes(tcp.data);
//                    }else{
////                        pr = new PacketReconst(tuples, tcp.data, packetBody);
////                        Thread t = new Thread(pr);
////                        t.start();
////                        System.out.println("Running: "+t.getName()+"_"+t.getId());
////                        threads.add(t);
//                    }
//                }else if(packet instanceof UDPPacket){
//                    udp = (UDPPacket) packet;
//                    PacketReconst.BodyData udpBodyData;
//                    tuples = udp.src_ip.toString().substring(1)+"("+udp.src_port+")"+"-"+udp.dst_ip.toString().substring(1)+"("+udp.dst_port+")";
//                    if(packetBody.containsKey(tuples)){
//                        udpBodyData = packetBody.get(tuples);
//                        udpBodyData.addBytes(udp.data);
//                    }else{
////                        pr = new PacketReconst(tuples, udp.data, packetBody);
////                        Thread t = new Thread(pr);
////                        t.start();
////                        System.out.println("Running: "+t.getName()+"_"+t.getId());
////                        threads.add(t);
//                    }
//                }
//            }
//            for(Thread thread : threads){
//                try {
//                    thread.join();
//                } catch (InterruptedException ex) {
//                    Logger.getLogger(PacketReader.class.getName()).log(Level.SEVERE, null, ex);
//                }
//            }
//        }        
//    }
//    
//    public static String getInput(String c){
//        String input = "";
//        System.out.println(c);
//        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
//        try {
//            input = br.readLine();
//        } catch (IOException ex) {
//            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        return input;
//    }
//    
//    String getPacket(Packet packet) throws IOException {   
//        
//        if(packet instanceof TCPPacket){
//            tcp = (TCPPacket) packet;
//            dp.setSrcIP(tcp.src_ip.toString().substring(1));
//            dp.setDstIP(tcp.dst_ip.toString().substring(1));
//            dp.setSrcPort(tcp.src_port);
//            dp.setDstPort(tcp.dst_port);
//        } 
//        if(packet instanceof UDPPacket){
//            udp = (UDPPacket) packet; 
//            dp.setSrcIP(udp.src_ip.toString().substring(1));
//            dp.setDstIP(udp.dst_ip.toString().substring(1));
//            dp.setSrcPort(udp.src_port);
//            dp.setDstPort(udp.dst_port);
//        } 
//        dp.setPacketData(packet.data);
//        ngramPrint = ngramData.Ngram(new String(dp.getPacketData(), StandardCharsets.UTF_8));
//        fwn.write("DataKe: "+p+" "+ngramPrint);
//        fwn.write("\r\n");
//        print = Integer.toString(p)+" -> "+dp.getSrcIP()+":"+dp.getSrcPort()+"-"+dp.getDstIP()+":"+dp.getDstPort()+" -> Data: "+new String(dp.getPacketData(), StandardCharsets.UTF_8);
//        p++;
//        return new String(print);
//    }
>>>>>>> remotes/origin/exp
    
}
