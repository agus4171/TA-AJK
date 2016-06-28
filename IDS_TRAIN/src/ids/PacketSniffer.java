/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import java.util.logging.Level;
import java.util.logging.Logger;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

/**
 *
 * @author agus
 */
public class PacketSniffer implements Runnable {
    private int input, counter, count = 1;
    private double[] numChars, temp;
    private String tuples, timeFormat, startTime;
    private String[] header, time;
    private Date date;
    private DateFormat format;
    private JpcapCaptor captor;
    private NetworkInterface device;
    private Packet packet;
    private TCPPacket tcp;
    private UDPPacket udp;        
    private ArrayList<DataPacket> dataTest;
    private Map<String, BodyPacket> packetBody = new HashMap<>();
    private Map<String, String> packetTime = new HashMap<>();
    Ngram ng = new Ngram();
    BodyPacket bp;
    
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
                    
                    if (packet instanceof TCPPacket && packet.data.length != 0){
                        tcp = (TCPPacket) packet;
                        if (tcp.dst_port < 1024) {
                            System.out.println("TCP "+tcp + new String(tcp.data, StandardCharsets.US_ASCII));
                            time = new String(tcp.toString()).split(":");
                            date = new Date(Long.parseLong(time[0])*1000L);
                            format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                            timeFormat = format.format(date);
                            tuples = "TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                            if (packetBody.containsKey(tuples)){
                                bp = packetBody.get(tuples);
                                bp.addBytes(tcp.data);

                            } else { 
                                bp = new BodyPacket(tcp.data); 
                                packetBody.put(tuples, bp); 
                                packetTime.put(tuples, timeFormat);
                            } 
//                            count++;
                        }
                        count++;
                    }
                    
                    else if(packet instanceof UDPPacket && packet.data.length != 0){
                        udp = (UDPPacket) packet;
                        if (udp.dst_port < 1024) {
                            System.out.println("UDP "+udp + new String(udp.data, StandardCharsets.US_ASCII));
                            time = new String(udp.toString()).split(":");
                            date = new Date(Long.parseLong(time[0])*1000L);
                            format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
                            format.setTimeZone(TimeZone.getTimeZone("Asia/Jakarta"));
                            timeFormat = format.format(date);
                            tuples = "UDP-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                            if(packetBody.containsKey(tuples)){
                                bp = packetBody.get(tuples);
                                bp.addBytes(udp.data);
                            } else { 
                                bp = new BodyPacket(udp.data); 
                                packetBody.put(tuples, bp); 
                                packetTime.put(tuples, timeFormat);
                            } 
//                            count++;
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
            numChars = ng.Ngram(value.getBytes());
            startTime = packetTime.get(header[0]+"-"+header[1]+"-"+header[2]+"-"+header[3]+"-"+header[4]);
            dataTest.add(new DataPacket(startTime, header[0], header[1], Integer.parseInt(header[2]), header[3], Integer.parseInt(header[4]), value.getBytes(), numChars, 0));
        }
        
        packetBody.clear();
        packetTime.clear();
    }
    
}
