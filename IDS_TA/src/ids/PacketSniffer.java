/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
public class PacketSniffer implements Runnable {
    private JpcapCaptor captor;
    private final NetworkInterface device;
    private Packet packet;
    private TCPPacket tcp;
    private UDPPacket udp;
    private String tuples;
    private String[] header;
    private int input, counter, count = 1;
    private double[] numChars, temp;
    private final Map<String, BodyPacket> packetBody = new HashMap<>(); 
    private final ArrayList<DataPacket> dataTest;
    private final Ngram ng = new Ngram();
    
    public PacketSniffer(NetworkInterface device, int n, ArrayList<DataPacket> dataTest, int counter){        
        this.device = device;
        this.dataTest = dataTest;
        this.input = n;
        this.counter = counter;
    }
    
    @Override
    public void run(){
        try {
            captor = JpcapCaptor.openDevice(device, 65536, true, 20);
            while (true) {                
                packet = captor.getPacket();
                synchronized(packetBody){                            
                    if (count == counter) break;
                    
                    if (packet instanceof TCPPacket){
                        tcp = (TCPPacket) packet;
                        System.out.println(count+" "+tcp);
                        BodyPacket tcpBodyData; 
                        tuples = "TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                        if (packetBody.containsKey(tuples)){
                            tcpBodyData = packetBody.get(tuples);
                            tcpBodyData.addBytes(tcp.data);

                        } else { 
                            tcpBodyData = new BodyPacket(tcp.data); 
                            packetBody.put(tuples, tcpBodyData); 
                        } 
                        count++;
                    }
                    
                    else if(packet instanceof UDPPacket){
                        udp = (UDPPacket) packet;
                        System.out.println(count+" "+udp);
                        BodyPacket udpBodyData; 
                        tuples = "UDP-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                        if(packetBody.containsKey(tuples)){
                            udpBodyData = packetBody.get(tuples);
                            udpBodyData.addBytes(udp.data);
                        } else { 
                            udpBodyData = new BodyPacket(udp.data); 
                            packetBody.put(tuples, udpBodyData); 
                        } 
                        count++;
                    }
                    
                }     
            }
        } catch (IOException ex) {
            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
        }   
        
        for (Map.Entry<String, BodyPacket> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            header = key.split("-", 0);
            BodyPacket value = entry.getValue();
            if (value.getBytes().length != 0) {
                numChars = ng.Ngram(new String(value.getBytes(), StandardCharsets.US_ASCII));
                dataTest.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), value.getBytes(), numChars, 0));
            }   
        }
        System.out.println("dataTes: "+dataTest.size()); 
    }
    
}
