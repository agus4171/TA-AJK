/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ConfussionMatrix;

import java.io.BufferedReader;
import java.io.File;
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
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String dir = System.getProperty("user.dir")+System.getProperty("file.separator")+"UJI_COBA";
        String dirTh = System.getProperty("user.dir")+System.getProperty("file.separator")+"doc";
        File filePath = new File(dir);
        File[] listFile = filePath.listFiles();
        Map<String, String> dataString = new HashMap<>();
        BufferedReader brTh;
        String line, newIP;
        String[] str, ip, data, ipDst, fileLog, conf = null;
        double akurasi, tpr, fnr, fpr, tnr, p, dt=0.0, tp=0.0, tn=0.0, fp=0.0, fn=0.0;
        brTh = new BufferedReader(new FileReader("confussion"));
        while ((line = brTh.readLine()) != null) {
            conf = line.split(":");
        }
        System.out.println("File condensed : Week "+conf[0]+", "+conf[1]);
        brTh = new BufferedReader(new FileReader(dirTh+"/"+conf[0]+"/"+conf[1]));
        while ((line = brTh.readLine()) != null) {
            str = line.split(" ");
//            dataString.put(str[5], str[6]);
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
        for (File file : listFile) {
            if (file.isFile()) {
                fileLog = file.toString().split("_");
                if (fileLog[4].equals(conf[1])) {
                    System.out.println(file.getAbsolutePath());
                    try {
                        brTh = new BufferedReader(new FileReader(file.getAbsolutePath()));
                        while ((line = brTh.readLine()) != null) {
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
                        System.out.println(dt+" "+tp+" "+fn+" "+fp+" "+tn);
                        akurasi = (tp+tn)/dt;
                        tpr = tp/(tp+fn);
                        fnr = fn/(tp+fn);
                        fpr = fp/(fp+tn);
                        tnr = tn/(fp+tn);
                        p = tp/(tp+fp);
                        System.out.println("Akurasi: "+Math.round(akurasi*10000.0)/10000.0+" and in Percentage "+Math.round(akurasi*10000.0)/100.0+" %");
                        System.out.println("True positive: "+Math.round(tpr*10000.0)/10000.0+" and in Percentage "+Math.round(tpr*10000.0)/100.0+" %");
                        System.out.println("False negative: "+Math.round(fnr*10000.0)/10000.0+" and in Percentage "+Math.round(fnr*10000.0)/100.0+" %");
                        System.out.println("False positive: "+Math.round(fpr*10000.0)/10000.0+" and in Percentage "+Math.round(fpr*10000.0)/100.0+" %");
                        System.out.println("True negative: "+Math.round(tnr*10000.0)/10000.0+" and in Percentage "+Math.round(tnr*10000.0)/100.0+" %");
                        System.out.println("Presisi: "+Math.round(p*10000.0)/10000.0+ "and in Percentage "+Math.round(p*10000.0)/100.0+" %");
                        } catch (FileNotFoundException ex) {
                            Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
                        } catch (IOException ex) {
                        Logger.getLogger(ConfussionMatrix.class.getName()).log(Level.SEVERE, null, ex);
                    }                    
                }                                
            }
        }
    }
    
}
