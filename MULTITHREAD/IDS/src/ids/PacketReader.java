/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

/**
 *
 * @author agus
 */
public class PacketReader implements Runnable {
    private JpcapCaptor captor;
    private TCPPacket tcp;
    private UDPPacket udp;
    private int input, files, type;
    private double[] numChars, temp;
    private String tuples;
    private String[] header;
    private Map<String, BodyPacket> packetBody = new HashMap<>();
//    private Map<String, Double[]> datasetPacketTcp;
//    private Map<String, Double[]> datasetPacketUdp;
//    private Map<String, Double[]> dataTestPacket;
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;
    private ArrayList<DataPacket> dataTest;
    private DataPacket[] dataTrain;
    private Ngram ng = new Ngram();
    
    public PacketReader(int files, JpcapCaptor captor, int n, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, ArrayList<DataPacket> dataTest, int type){
        this.files = files;
        this.captor = captor;        
        this.input = n;
        this.datasetTcp = datasetTcp;
        this.datasetUdp = datasetUdp;
        this.dataTest = dataTest;
        this.type = type;
    }
    
    @Override
    public void run(){
        while (true) {
            Packet packet = captor.getPacket();
            synchronized(captor){                
                if (packet == null || packet == Packet.EOF) break;
                
                if (packet instanceof TCPPacket &&  packet.data != null){
                    tcp = (TCPPacket) packet;
                    BodyPacket tcpBodyData; 
                    tuples = input+"-tcp-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                    if (packetBody.containsKey(tuples)){
                        tcpBodyData = packetBody.get(tuples);
                        tcpBodyData.addBytes(tcp.data);
                    
                    } else { 
                        tcpBodyData = new BodyPacket(tcp.data); 
                        packetBody.put(tuples, tcpBodyData); 
                    } 
                }
                else if(packet instanceof UDPPacket && packet.data != null){
                    udp = (UDPPacket) packet;
                    BodyPacket udpBodyData; 
                    tuples = input+"-udp-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                    if(packetBody.containsKey(tuples)){
                        udpBodyData = packetBody.get(tuples);
                        udpBodyData.addBytes(udp.data);
                    
                    } else { 
                        udpBodyData = new BodyPacket(udp.data); 
                        packetBody.put(tuples, udpBodyData); 
                    } 
                }
            }            
        }
        Proses();
    }
    
    private void Proses(){    
        dataTest.clear();
        
        for (Map.Entry<String, BodyPacket> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            header = key.split("-", 0);
            BodyPacket value = entry.getValue();
            numChars = ng.Ngram(new String(value.getBytes(), StandardCharsets.US_ASCII));
                
            if (header[0].equals("1") && header[1].equals("tcp")) {
                datasetTcp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, 0));
            } 

            else if (header[0].equals("1") && header[1].equals("udp")) {
                datasetUdp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, 0));
            }

            else if (header[0].equals("3")) {
                dataTest.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, 0));
            }                
        }
        System.out.println("datasetTcp: "+datasetTcp.size());
        System.out.println("datasetUdp: "+datasetUdp.size());
        System.out.println("dataTes: "+dataTest.size()); 
        packetBody.clear();
    }
    
    double[] Sum(double[] one, double[] two){
        for (int i = 0; i < one.length; i++) {
            one[i] = one[i] + two[i];
        }
        return one;
    }
    
}
