/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

<<<<<<< HEAD
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

=======
>>>>>>> remotes/origin/exp
/**
 *
 * @author Agus
 */
<<<<<<< HEAD
public class PacketReconst implements Runnable{
    private byte[] packet;
    private String tuples, os, filePath, dirPath = System.getProperty("user.dir");
    private Map<String, BodyData> packetBody;
    BodyData packetData;
    ArrayList<Thread> threads;
    
    public PacketReconst(ArrayList<Thread> threads, String tuples, byte[] newPacket, Map<String, BodyData> packetBody){
        this.threads = threads; 
        this.tuples = tuples;
        this.packet = newPacket;
        this.packetBody = packetBody;
    }
    
    public void run(){
        System.out.println(Thread.currentThread().getName());
        synchronized(threads){
            BodyData bd = new BodyData(tuples, packet);
            bd.addBytes(packet);
            packetBody.put(tuples, bd);
        }
    }
    
    public class BodyData{
        byte[] bytes = null;
        File file;
        FileWriter fw;
        
        public BodyData(String dir, byte[] bytes){
            this.bytes = bytes;
            os = System.getProperty("os.name").toLowerCase();
            if(os.indexOf("win") >= 0){
                filePath = dirPath+"\\result\\"+dir+".txt";        
            }else if(os.indexOf("nix") >= 0 || os.indexOf("nux") >= 0 || os.indexOf("mac") >0 ){
                filePath = dirPath+"/result/"+dir+".txt";
            }
            this.file = new File(filePath);
            if(!this.file.exists()){
                try {
                    this.file.createNewFile();
                } catch (IOException ex) {
                    Logger.getLogger(PacketReconst.class.getName()).log(Level.SEVERE, null, ex);
                }
            }            
        }
        
        public void addBytes(byte[] bytes){
            try {
                fw = new FileWriter(file, true);
                fw.write(new String(bytes, StandardCharsets.UTF_8));
                fw.flush();
            } catch (IOException ex) {
                Logger.getLogger(PacketReconst.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        public FileWriter getBytes(){
            return fw;
        }
    }
=======
public class PacketReconst {
//    private byte[] packet;
//    private String tuples, os, filePath, dirPath = System.getProperty("user.dir");
//    private Map<String, BodyData> packetBody;
//    private ArrayList<String> capturedPackets = new ArrayList<String>();
//    BodyData packetData;
//    ArrayList<Thread> threads;
//    
//    public PacketReconst(ArrayList<Thread> threads, String tuples, byte[] newPacket, Map<String, BodyData> packetBody){
//        this.threads = threads; 
//        this.tuples = tuples;
//        this.packet = newPacket;
//        this.packetBody = packetBody;
//    }
//    
//    public void run(){
//        System.out.println(Thread.currentThread().getName());
//        synchronized(threads){
//            BodyData bd = new BodyData(tuples, packet);
//            bd.addBytes(packet);
//            packetBody.put(tuples, bd);
//        }
//    }
    
    
>>>>>>> remotes/origin/exp
}
