/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConfussionMatrix;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Agus
 */
public class Save {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dir = System.getProperty("user.dir")+System.getProperty("file.separator")+"UJI_COBA";
        BufferedReader brTh;
        Map<String, String> dataString = new HashMap<>();
        String line, newIP;
        String[] str;
        String[] ip;
        try {
            brTh = new BufferedReader(new FileReader(dir+"\\monday"));
            while ((line = brTh.readLine()) != null) {
                str = line.split(" ");
                if (dataString.containsKey(str[5])) continue;
                else if (str[5].contains("*")) {
                    ip = str[5].split("\\.");
                    for (int i = 1; i < 255; i++) {
                        newIP = ip[0]+"."+ip[1]+"."+ip[2]+"."+i;
                        if (dataString.containsKey(newIP)) continue;
                        else
                            dataString.put(newIP, str[6]);
                    }
                }  
                else
                    dataString.put(str[5], str[6]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }        
        

        double akurasi, tpr, fnr, fpr, tnr, p, dt=0.0, tp=0.0, tn=0.0, fp=0.0, fn=0.0;
        BufferedReader br;
        String[] data, ipDst;
        try {
            br = new BufferedReader(new FileReader(dir+"\\Pcap_Record_log_monday_inside_tcpdump"));
            while ((line = br.readLine()) != null) {
                data = line.split(" ");            
                ipDst = data[7].split(":");
                if (dataString.containsKey(ipDst[0]) && data[9].equals("Attack")) {
                    tp++;
                } else if (dataString.containsKey(ipDst[0]) && data[9].equals("Normal")) {
                    fn++;
                } else if (data[9].equals("Attack")) {
                    fp++;
                } else {
                    tn++;
                }
                dt++;
            }
            System.out.println("Total connection: "+dt+" | True positive: "+tp+" | False negative: "+fn+" | False positive: "+fp+" | True negative: "+tn);
            akurasi = (tp+tn)/dt;
            tpr = tp/(tp+fn);
            fnr = fn/(tp+fn);
            fpr = fp/(fp+tn);
            tnr = tn/(tn+fp);
            p = tp/(tp+fp);
            System.out.println("Akurasi         : "+Math.round(akurasi*10000.0)/10000.0+" and in Percentage "+Math.round(akurasi*10000.0)/100.0+"%");
            System.out.println("True positive   : "+Math.round(tpr*10000.0)/10000.0+" and in Percentage "+Math.round(tpr*10000.0)/100.0+"%");
            System.out.println("False negative  : "+Math.round(fnr*10000.0)/10000.0+" and in Percentage "+Math.round(fnr*10000.0)/100.0+"%");
            System.out.println("False positive  : "+Math.round(fpr*10000.0)/10000.0+" and in Percentage "+Math.round(fpr*10000.0)/100.0+"%");
            System.out.println("True negative   : "+Math.round(tnr*10000.0)/10000.0+" and in Percentage "+Math.round(tnr*10000.0)/100.0+"%");
            System.out.println("Presisi         : "+Math.round(p*10000.0)/10000.0+ " and in Percentage "+Math.round(p*10000.0)/100.0+"%");
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }  
    }
    
}
