/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.nio.charset.StandardCharsets;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import jpcap.JpcapCaptor;
import jpcap.packet.Packet;
import jpcap.packet.TCPPacket;
import jpcap.packet.UDPPacket;

/**
 *
 * @author agus
 */
public class PacketReader implements Runnable {
    private static int countPacket;
    private int input, files, type, counter, windowSize;
    private double[] numChars;
    private String tuples, timeFormat, startTime, regex = "\\r?\\n";
    private String[] header, time, line, split;
    private byte[] data;
    private Date date;
    private DateFormat format;
    private JpcapCaptor captor;
    private TCPPacket tcp;
    private UDPPacket udp;
    private ArrayList<DataPacket> datasetTcp;       
    private ArrayList<DataPacket> datasetUdp;
    private ArrayList<DataPacket> dataTest;
    private Map<String, BodyPacket> packetBody = new HashMap<>();
    private Map<String, String> packetTime = new HashMap<>();
    private Map<String, String> dataPort;
    Ngram ng = new Ngram();
    BodyPacket bp;
    
    public PacketReader(){
        
    }
    
    public PacketReader(int files, JpcapCaptor captor, int input, ArrayList<DataPacket> datasetTcp, ArrayList<DataPacket> datasetUdp, ArrayList<DataPacket> dataTest, Map<String, String> dataPort, int type, int windowSize){
        this.files = files;
        this.captor = captor;        
        this.input = input;
        this.datasetTcp = datasetTcp;
        this.datasetUdp = datasetUdp;
        this.dataTest = dataTest;
        this.dataPort = dataPort;
        this.type = type;
        this.windowSize = windowSize;
    }
    
    @Override
    public void run(){
        while (true) {
            Packet packet = captor.getPacket(); 
            
            synchronized(packetBody){
                if (packet == null || packet == Packet.EOF || (input == 3 && counter == windowSize)) break;
                if (packet instanceof TCPPacket && packet.data.length != 0){
                    tcp = (TCPPacket) packet;
                    if (dataPort.containsKey("TCP"+tcp.dst_port)) {
                        if (input == 3) {
                            time = new String(tcp.toString()).split(":");
                            date = new Date(Long.parseLong(time[0])*1000L);
                            format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                            timeFormat = format.format(date);
                        }
//                        if (tcp.dst_port == 80) {;
//                            System.out.println(new String(tcp.data, StandardCharsets.US_ASCII));
//                            split = new String(tcp.data, StandardCharsets.US_ASCII).split(regex);
//                            data = split[0].getBytes(StandardCharsets.US_ASCII);
//                            for (int i = 1; i < split.length; i++) {
//                                line = split[i].split(": ");
//                                data = line[1].getBytes(StandardCharsets.US_ASCII);
//                            }
//                            System.out.println(new String(data, StandardCharsets.US_ASCII));
//                            tuples = input+"-TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
//                            if (packetBody.containsKey(tuples)){
//                                bp = packetBody.get(tuples);
//                                bp.addBytes(data);
//                            } else { 
//                                bp = new BodyPacket(data); 
//                                packetBody.put(tuples, bp); 
//                                packetTime.put("TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port, timeFormat);
//                            }
//                        } else {
                        tuples = input+"-TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port;
                        if (packetBody.containsKey(tuples)){
                            bp = packetBody.get(tuples);
                            bp.addBytes(tcp.data);
                        } else { 
                            bp = new BodyPacket(tcp.data); 
                            packetBody.put(tuples, bp); 
                            packetTime.put("TCP-"+tcp.src_ip.toString().substring(1)+"-"+tcp.src_port+"-"+tcp.dst_ip.toString().substring(1)+"-"+tcp.dst_port, timeFormat);
                        }
//                        }
                        countPacket++;
                        counter++;
                    }
                }

                else if(packet instanceof UDPPacket && packet.data.length != 0){
                    udp = (UDPPacket) packet; 
                    if (dataPort.containsKey("UDP"+udp.dst_port)) {
                        if (input == 3) {
                            time = new String(udp.toString()).split(":");
                            date = new Date(Long.parseLong(time[0])*1000L);
                            format = new SimpleDateFormat("MM/dd/yyyy HH:mm:ss");
                            format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
                            timeFormat = format.format(date);
                        }
                        tuples = input+"-UDP-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port;
                        if(packetBody.containsKey(tuples)){
                            bp = packetBody.get(tuples);
                            bp.addBytes(udp.data);
                        } else { 
                            bp = new BodyPacket(udp.data); 
                            packetBody.put(tuples, bp); 
                            packetTime.put("UDP-"+udp.src_ip.toString().substring(1)+"-"+udp.src_port+"-"+udp.dst_ip.toString().substring(1)+"-"+udp.dst_port, timeFormat);
                        }
                        countPacket++;
                        counter++;
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
                datasetTcp.add(new DataPacket(startTime, header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), null, numChars, type));
            } 
            else if (header[0].equals("1") && header[1].equals("UDP")) {
                datasetUdp.add(new DataPacket(startTime, header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), null, numChars, type));
            }
            else {
                startTime = packetTime.get(header[1]+"-"+header[2]+"-"+header[3]+"-"+header[4]+"-"+header[5]);
                dataTest.add(new DataPacket(startTime, header[1], header[2], Integer.parseInt(header[3]), header[4], Integer.parseInt(header[5]), value.getBytes(), numChars, type));
            }                
        }           
        packetBody.clear();
        packetTime.clear();
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
