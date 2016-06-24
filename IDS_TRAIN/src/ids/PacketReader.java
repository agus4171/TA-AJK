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
    private static int countPacket = 1;
    private int input, files, type;
    private double[] numChars;
    private String tuples, regex = "\\r?\\n";
    private String[] header;
    private JpcapCaptor captor;
    private TCPPacket tcp;
    private UDPPacket udp;
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;
    private ArrayList<DataPacket> dataTest;
    private Map<String, BodyPacket> packetBody = new HashMap<>();
    private Ngram ng = new Ngram();
    
    public PacketReader(){
        
    }
    
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
            
            synchronized(packetBody){
                if (packet == null || packet == Packet.EOF) break;

                if (packet instanceof TCPPacket && packet.data.length != 0){
                    tcp = (TCPPacket) packet;
                    BodyPacket tcpBodyData;
                    if (tcp.dst_port < 1024) {
//                        if (tcp.dst_port == 80) {
//                            System.out.println(tcp+new String(tcp.data, StandardCharsets.US_ASCII));
//                        }
//                        if (tcp.dst_port == 80) {
//                            String[] split = new String(tcp.data, StandardCharsets.US_ASCII).split(regex);
//                            System.out.println(split[2]);
//                            byte[] data = split[0].getBytes(StandardCharsets.US_ASCII);
//                            tuples = input+"-TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
//                            if (packetBody.containsKey(tuples)){
//                                tcpBodyData = packetBody.get(tuples);
//                                tcpBodyData.addBytes(data);
//                            } else { 
//                                tcpBodyData = new BodyPacket(data); 
//                                packetBody.put(tuples, tcpBodyData); 
//                            }
//                        } 
//                        else {
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
                }                
            }            
        }  
        
        for (Map.Entry<String, BodyPacket> entry : packetBody.entrySet()) {
            String key = entry.getKey();
            header = key.split("-", 0);
            BodyPacket value = entry.getValue();
            numChars = ng.Ngram(value.getBytes());            
                
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
        
        packetBody.clear();
    }
    
    /**
     * @return the countPacket
     */
    public static int getCountPacket() {
        return countPacket;
    }

    /**
     * @param aCountPacket the countPacket to set
     */
    public static void setCountPacket(int aCountPacket) {
        countPacket = aCountPacket;
    }
    
}
