/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import ids.Mahalanobis;
import ids.Ngram;
import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class Jpcap {

    /**
     * @param args the command line arguments
     */        
    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader("tuesday"));
        Map<String, Integer> dataTruth = new HashMap<>();
        String line;
        String[] ip;
        while ((line = br.readLine()) != null) {
            ip = line.split(" ");
            System.out.println(ip[6]);
//            System.out.println(line.substring(10, 29));
//            dataTruth.put(line.substring(10, 20), 1);
        }
            
//        ArrayList<Double> tes = new ArrayList<>();
//        tes.add(0.0);
//        tes.add(4.1);
//        tes.add(4.2);
//        tes.add(4.3);
//        tes.add(5.0);
//        tes.add(1.0);
//        tes.add(3.0);
//        tes.add(2.0);
//        tes.add(7.0);
//        tes.add(100.0);
//        System.out.println(Collections.max(tes));
//        System.out.println(Collections.min(tes));
//        boolean a = true;
//        System.out.println(a);
//        int b = 0;
//        a = b != 0;
//        System.out.println(a);
//        int core = Runtime.getRuntime().availableProcessors();
//        System.out.println(core);
//        String str = "/home/agus/dataset/inside/1/tuesday/inside.tcpdump";
//        String[] s = str.replace("/", "-").split("-");
//        System.out.println(s[s.length-2]+"_"+s[s.length-1].replace(".", "-"));
//        try {
//            JpcapCaptor captor = JpcapCaptor.openFile("/media/agus/4DD244C0638395F0/dataset/1/inside/friday/inside.tcpdump");
//            int i =1;
//            String regex = "\\r?\\n";
//            String[] head;
//            while (true) {                
//                Packet packet = captor.getPacket();
//                if (packet == null || packet == Packet.EOF) break;
//                if (packet instanceof UDPPacket) {
//                    UDPPacket tcp = (UDPPacket) packet;
//                    if (tcp.dst_port == 53) {
//                        System.out.println(i+" "+ new String(tcp.data, StandardCharsets.US_ASCII));
//                        head = new String(tcp.data, StandardCharsets.US_ASCII).split(regex);
//                        System.out.println(i+" "+head[0]);
//                    }
//                    i++;
//                }
////                System.out.println(new String(packet.data, StandardCharsets.US_ASCII));
//            }
//        Map<String, Double[]> datasetPacketUdp = new HashMap<>();
//        Ngram ng = new Ngram();
//        double[] numChars, temp;
//        Jpcap tes = new Jpcap();
//        // TODO code application logic here
//        NetworkInterface[] device;
//        device = JpcapCaptor.getDeviceList();
////        System.out.println(device[0].name);
//        
//        double[] a = {1,2,3,4,1,6};
//        double[] b = {6,1,5,8,3,6};
//        double[] c = {1,1,3,4,2,6};
//        double[] d = {6,1,2,8,4,6};
//Double[] ab = new Double(a);
//        double[] coba = tes.Sum(a, b);
//        System.out.println(Arrays.toString(coba));
//        Mahalanobis x = new Mahalanobis(a);
//        Mahalanobis y = new Mahalanobis(b);
//        double z = x.distanceTo(y);
//        System.out.println();
//        datasetPacketUdp.put("agus", ArrayUtils.toObject(a));
//        datasetPacketUdp.put("adi", ArrayUtils.toObject(b));
//        datasetPacketUdp.put("wirawan", ArrayUtils.toObject(c));
//        datasetPacketUdp.put("imade", ArrayUtils.toObject(d));
//        for (Map.Entry<String, Double[]> entry : datasetPacketUdp.entrySet()) {
//            String key = entry.getKey();
//            Double[] value = entry.getValue();
//            System.out.println(key+" "+Arrays.toString(ArrayUtils.toPrimitive(value)));
//        }
//        numChars = ng.Ngram("selamat menunaikan ibadah puasa");  
//        System.out.println(Arrays.toString(numChars));
//        if (datasetPacketUdp.containsKey("agus")) {
//            temp = ArrayUtils.toPrimitive(datasetPacketUdp.get("agus"));
//            System.out.println(Arrays.toString(temp));
//            datasetPacketUdp.put("agus", ArrayUtils.toObject(tes.Sum(numChars, temp)));
//            temp = ArrayUtils.toPrimitive(datasetPacketUdp.get("agus"));
//            System.out.println(Arrays.toString(temp));
//        }
//        else{
//            datasetPacketUdp.put("agus", ArrayUtils.toObject(numChars));
//        }
//        } catch (IOException ex) {
//            Logger.getLogger(Jpcap.class.getName()).log(Level.SEVERE, null, ex);
//        }
//        
    }
//    
//    public double[] Sum(double[] one, double[] two){
//        for (int i = 0; i < two.length; i++) {
//            two[i] = one[i] + two[i];
//        }
//        return one;
//    }
    
}
