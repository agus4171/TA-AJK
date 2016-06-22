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
    private int input, files, type, countPacket;
    private double[] numChars;
    private String tuples, regex = "\\r?\\n";
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
        countPacket = new Integer(1);
    }
    
    @Override
    public void run(){
        while (true) {
            Packet packet = captor.getPacket(); 
            
            synchronized(packetBody){
                if (packet == null || packet == Packet.EOF) break;

                if (packet instanceof TCPPacket && packet.data.length != 0){
                    tcp = (TCPPacket) packet;
                    BodyPacket tcpBodyData;
                    if (tcp.dst_port < 1024) {
//                        if (tcp.dst_port == 80) {
//                            System.out.println(countPacket+" "+tcp.data.length+" "+new String(tcp.data, StandardCharsets.US_ASCII));
//                        }
//                        if (tcp.dst_port == 80) {
//                            String[] split = new String(tcp.data, StandardCharsets.US_ASCII).split(regex);
//                            byte[] data = split[0].getBytes(StandardCharsets.US_ASCII);
//                            tuples = input+"-TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
//                            if (packetBody.containsKey(tuples)){
//                                tcpBodyData = packetBody.get(tuples);
//                                tcpBodyData.addBytes(data);
//                            } else { 
//                                tcpBodyData = new BodyPacket(data); 
//                                packetBody.put(tuples, tcpBodyData); 
//                            }
//                        } else {
                            tuples = input+"-TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                            if (packetBody.containsKey(tuples)){
                                tcpBodyData = packetBody.get(tuples);
                                tcpBodyData.addBytes(tcp.data);
                            } else { 
                                tcpBodyData = new BodyPacket(tcp.data); 
                                packetBody.put(tuples, tcpBodyData); 
                            }
//                        }
                        countPacket++;
                    }
//                    countPacket++;
                }

                else if(packet instanceof UDPPacket && packet.data.length != 0){
                    udp = (UDPPacket) packet;
                    BodyPacket udpBodyData; 
                    if (udp.dst_port < 1024) {
                        tuples = input+"-UDP-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                        if(packetBody.containsKey(tuples)){
                            udpBodyData = packetBody.get(tuples);
                            udpBodyData.addBytes(udp.data);
                        } else { 
                            udpBodyData = new BodyPacket(udp.data); 
                            packetBody.put(tuples, udpBodyData); 
                        }
                        countPacket++;
                    }
//                    countPacket++;
                }                
            }            
        }            
        System.out.println("Total Packet: "+countPacket);
        if (input == 1) {
            System.out.println("Storing dataset ke-"+files);
        } else if (input == 3) {
            System.out.println("Storing data testing ke-"+files);
        }
        
        for (Map.Entry<String, BodyPacket> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            header = key.split("-", 0);
            BodyPacket value = entry.getValue();
            numChars = ng.Ngram(new String(value.getBytes(), StandardCharsets.US_ASCII));
                
            if (header[0].equals("1") && header[1].equals("TCP")) {
                datasetTcp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), null, numChars, type));
            } 

            else if (header[0].equals("1") && header[1].equals("UDP")) {
                datasetUdp.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), null, numChars, type));
            }

            else if (header[0].equals("3")) {    
                dataTest.add(new DataPacket(header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), value.getBytes(), numChars, type));
            }                
        }
        
        if (input == 1) {
            System.out.println("Total Dataset of Tcp: "+datasetTcp.size());
            System.out.println("Total Dataset of Udp: "+datasetUdp.size());            
        } else if (input == 3) {
            System.out.println("Total Data Testing: "+dataTest.size());
        }        
         
        packetBody.clear();
    }
    
}
