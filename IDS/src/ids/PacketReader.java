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
    private double[] numChars;
    private String tuples;
    private String[] header;
    private Map<String, BodyPacket> packetBody = new HashMap<>();
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;
    private ArrayList<DataPacket> dataTest;
    private Ngram ng = new Ngram();
    
    public PacketReader(int files, JpcapCaptor captor, int input, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, ArrayList<DataPacket> dataTest, int type){
        this.files = files;
        this.captor = captor;        
        this.input = input;
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
                
                if (packet instanceof TCPPacket && packet.data.length != 0){
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
                
                else if(packet instanceof UDPPacket && packet.data.length != 0){
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
        System.out.println("Processing Packet ke-"+files);
        Proses();
    }
    
    public void Proses(){    
        dataTest.clear();
//        int free = 0, countFree = 0;
//        System.out.println(packetBody.size());
//        free = packetBody.size()/2;
        
        for (Map.Entry<String, BodyPacket> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            header = key.split("-", 0);
            BodyPacket value = entry.getValue();
            numChars = ng.Ngram(new String(value.getBytes(), StandardCharsets.US_ASCII));
                
            if (header[0].equals("1") && header[1].equals("tcp")) {
                datasetTcp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, type));
            } 

            else if (header[0].equals("1") && header[1].equals("udp")) {
                datasetUdp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, type));
            }

            else if (header[0].equals("3")) {
                
//                if (header[1].equals("tcp") && countFree < free) {
//                    datasetTcp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, type));
//                    countFree++;
//                } 
//
//                else if (header[1].equals("udp") && countFree < free) {
//                    datasetUdp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, type));
//                    countFree++;
//                }
//                
//                else
                dataTest.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), numChars, type));
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
