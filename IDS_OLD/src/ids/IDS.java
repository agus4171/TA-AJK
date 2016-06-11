/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JFileChooser;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;

/**
 *
 * @author Agus
 */
public class IDS {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        int input, result;
        long start, end;
        File file;
        File[] listFile;
        FileReader fr;
        NetworkInterface[] device;
        ArrayList<String> list;
        ArrayList<Hasil> data = new ArrayList<Hasil>();
        device = JpcapCaptor.getDeviceList();
        Scanner sc = new Scanner(System.in);
        while (true) {           
            System.out.println("1. Training Dataset\n"+"2. Packet Sniffer Test\n"+"3. Pcap Test\n"+"Choose(1 or 2 or 3): ");
            input = sc.nextInt();
            if(input == 1){
                System.out.println("Trainig Dataset\n");
                file = new File("D:\\free");
                listFile = file.listFiles();
                list = new ArrayList<>();
                for (int i = 0; i < listFile.length; i++) {
                    System.out.println(listFile[i].getName());
                    JpcapCaptor captor = JpcapCaptor.openFile(listFile[i].getAbsolutePath());
                    start = System.currentTimeMillis();
                    PacketReader pr = new PacketReader(captor, input, data);
                    Thread t = new Thread(pr);
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/60000+" minutes");
                }
            } else if (input == 2){
                System.out.println("Packet Sniffer Test\n");
                PacketSniffer ps = new PacketSniffer();
            } else if (input == 3){
                System.out.println("Pcap Test\n");
                JFileChooser fc = new JFileChooser();
                fc.setCurrentDirectory(new File(System.getProperty("user.dir")+System.getProperty("file.separator")+"../DARPA"));
                result = fc.showOpenDialog(fc);
                if(result == JFileChooser.APPROVE_OPTION){
                    File selectedFile = fc.getSelectedFile();
                    System.out.println("Open File: "+selectedFile.getAbsolutePath());
                    JpcapCaptor captor = JpcapCaptor.openFile(selectedFile.getAbsolutePath());
                    start = System.currentTimeMillis();
                    PacketReader pr = new PacketReader(captor, input, data);
                    Thread t = new Thread(pr);
                    t.start();
                    try {
                        t.join();
                    } catch (InterruptedException ex) {
                        Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    end = System.currentTimeMillis();
                    System.out.println("Total time is: "+(end-start)/60000+" minutes");
                }
                if(result == JFileChooser.CANCEL_OPTION){
                    System.out.println("No file is selected");
                }
            }else if(input == 2){
                System.out.println("Packet Sniffer Mode\n");
                PacketSniffer ps = new PacketSniffer();
            }            
        } catch (IOException ex) {
            Logger.getLogger(IDS.class.getName()).log(Level.SEVERE, null, ex);
        } 
            } else{
                break;
            } 
        } 
    }
    
}
