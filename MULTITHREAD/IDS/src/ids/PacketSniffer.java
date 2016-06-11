/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
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
    private Map<String, Double[]> dataTestPacket;  
    
    private ArrayList<DataPacket> dataTest;
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
                synchronized(captor){                            
                    if (count == counter) break;
                    
                    if (packet instanceof TCPPacket && packet.data.length != 0){
                        tcp = (TCPPacket) packet;
                        tuples = "tcp-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                        numChars = ng.Ngram(new String(tcp.data, StandardCharsets.US_ASCII));  
                        if (dataTestPacket.containsKey(tuples)){
                            temp = ArrayUtils.toPrimitive(dataTestPacket.get(tuples));
                            dataTestPacket.put(tuples, ArrayUtils.toObject(Sum(numChars, temp)));

                        } else { 
                            dataTestPacket.put(tuples, ArrayUtils.toObject(numChars));
                        } 
                        count++;
                    }
                    
                    else if(packet instanceof UDPPacket && packet.data.length != 0){
                        udp = (UDPPacket) packet;
                        tuples = "udp-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                        numChars = ng.Ngram(new String(udp.data, StandardCharsets.US_ASCII)); 
                        if(dataTestPacket.containsKey(tuples)){
                            temp = ArrayUtils.toPrimitive(dataTestPacket.get(tuples));
                            dataTestPacket.put(tuples, ArrayUtils.toObject(Sum(numChars, temp)));

                        } else { 
                            dataTestPacket.put(tuples, ArrayUtils.toObject(numChars)); 
                        }
                        count++;
                    }
                }     
            }
        } catch (IOException ex) {
            Logger.getLogger(PacketSniffer.class.getName()).log(Level.SEVERE, null, ex);
        }        
        System.out.println("dataTes: "+dataTestPacket.size());         
    }
    
    double[] Sum(double[] one, double[] two){
        for (int i = 0; i < one.length; i++) {
            one[i] = one[i] + two[i];
        }
        return one;
    }
    
}
