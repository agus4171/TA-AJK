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
public class ConfussionMatrix {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String dir = System.getProperty("user.dir")+System.getProperty("file.separator")+"UJI_COBA";
//        File filePath = new File(dir);
//        File[] listFile = filePath.listFiles();         
        BufferedReader brTh;
        Map<String, String> dataString = new HashMap<>();
        String line;
        String[] str;
        try {
            brTh = new BufferedReader(new FileReader(dir+"\\monday"));
                while ((line = brTh.readLine()) != null) {
                str = line.split(" ");
                dataString.put(str[5], str[6]);
//                System.out.println(str[5]+" "+str[6]);
            }
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
        }        
        

        double akurasi, tpr, fnr, fpr, tnr, p, dt=0.0, tp=0.0, tn=0.0, fp=0.0, fn=0.0;
        BufferedReader br;
        String[] data;
//        String[] ipSrc;
        String[] ipDst;
        try {
            br = new BufferedReader(new FileReader(dir+"\\Pcap_Record_log_monday_outside_tcpdump"));
            while ((line = br.readLine()) != null) {
                data = line.split(" ");
    //            ipSrc = data[5].split(":");            
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
            System.out.println("Total connection: "+dt+" | True positive: "+tp+" | False negative"+fn+" | False positive: "+fp+" | True negative: "+tn);
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
//        for (File file : listFile) {
//            if (file.isFile()) {
//                System.out.println(file.getAbsolutePath());
//                BufferedReader brTh = new BufferedReader(new FileReader(file.getAbsolutePath()));
//                Map<String, String> dataString = new HashMap<>();
//                String line;
//                String[] str;
//                while ((line = brTh.readLine()) != null) {
//                    str = line.split(" ");
//                    dataString.put(str[5], str[6]);
//                    System.out.println(str[5]+" "+str[6]);
//                }
//                double akurasi, tpr, fnr, fpr, tnr, p, dt=0.0, tp=0.0, tn=0.0, fp=0.0, fn=0.0;
//                BufferedReader br = new BufferedReader(new FileReader(file.getAbsolutePath()));
//                String[] data;
//        //        String[] ipSrc;
//                String[] ipDst;
//                while ((line = br.readLine()) != null) {
//                    data = line.split(" ");
//        //            ipSrc = data[5].split(":");            
//                    ipDst = data[7].split(":");
//                    if (dataString.containsKey(ipDst[0]) && data[9].equals("Attack")) {
//                        tp++;
//                    } else if (dataString.containsKey(ipDst[0]) && data[9].equals("Normal")) {
//                        fn++;
//                    } else if (data[9].equals("Attack")) {
//                        fp++;
//                    } else {
//                        tn++;
//                    }
//                    dt++;
//                }
//                System.out.println(dt+" "+tp+" "+fn+" "+fp+" "+tn);
//                akurasi = (tp+tn)/dt;
//                tpr = tp/(tp+fn);
//                fnr = fn/(tp+fn);
//                fpr = fp/(fp+tn);
//                tnr = tn/(tn+fp);
//                p = tp/(tp+fp);
//                System.out.println("Akurasi: "+Math.round(akurasi*10000.0)/10000.0+" and in Percentage "+Math.round(akurasi*10000.0)/100.0+" %");
//                System.out.println("True positive: "+Math.round(tpr*10000.0)/10000.0+" and in Percentage "+Math.round(tpr*10000.0)/100.0+" %");
//                System.out.println("False negative: "+Math.round(fnr*10000.0)/10000.0+" and in Percentage "+Math.round(fnr*10000.0)/100.0+" %");
//                System.out.println("False positive: "+Math.round(fpr*10000.0)/10000.0+" and in Percentage "+Math.round(fpr*10000.0)/100.0+" %");
//                System.out.println("True negative: "+Math.round(tnr*10000.0)/10000.0+" and in Percentage "+Math.round(tnr*10000.0)/100.0+" %");
//                System.out.println("Presisi: "+Math.round(p*10000.0)/10000.0+ "and in Percentage "+Math.round(p*10000.0)/100.0+" %");
//            }
//        }
    }
    
}
