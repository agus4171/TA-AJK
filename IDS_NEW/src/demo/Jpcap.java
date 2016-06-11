/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import ids.Mahalanobis;
import ids.Ngram;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import jpcap.JpcapCaptor;
import jpcap.NetworkInterface;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class Jpcap {

    /**
     * @param args the command line arguments
     */        
    public static void main(String[] args) {
        Map<String, Double[]> datasetPacketUdp = new HashMap<>();
        Ngram ng = new Ngram();
        double[] numChars, temp;
        Jpcap tes = new Jpcap();
        // TODO code application logic here
        NetworkInterface[] device;
        device = JpcapCaptor.getDeviceList();
//        System.out.println(device[0].name);
        
        double[] a = {1,2,3,4,1,6};
        double[] b = {6,1,5,8,3,6};
        double[] c = {1,1,3,4,2,6};
        double[] d = {6,1,2,8,4,6};
        //Double[] ab = new Double(a);
//        double[] coba = tes.Sum(a, b);
//        System.out.println(Arrays.toString(coba));
//        Mahalanobis x = new Mahalanobis(a);
//        Mahalanobis y = new Mahalanobis(b);
//        double z = x.distanceTo(y);
//        System.out.println();
        datasetPacketUdp.put("agus", ArrayUtils.toObject(a));
        datasetPacketUdp.put("adi", ArrayUtils.toObject(b));
        datasetPacketUdp.put("wirawan", ArrayUtils.toObject(c));
        datasetPacketUdp.put("imade", ArrayUtils.toObject(d));
//        for (Map.Entry<String, Double[]> entry : datasetPacketUdp.entrySet()) {
//            String key = entry.getKey();
//            Double[] value = entry.getValue();
//            System.out.println(key+" "+Arrays.toString(ArrayUtils.toPrimitive(value)));
//        }
        numChars = ng.Ngram("selamat menunaikan ibadah puasa");  
//        System.out.println(Arrays.toString(numChars));
        if (datasetPacketUdp.containsKey("agus")) {
            temp = ArrayUtils.toPrimitive(datasetPacketUdp.get("agus"));
            System.out.println(Arrays.toString(temp));
            datasetPacketUdp.put("agus", ArrayUtils.toObject(tes.Sum(numChars, temp)));
            temp = ArrayUtils.toPrimitive(datasetPacketUdp.get("agus"));
            System.out.println(Arrays.toString(temp));
        }
        else{
            datasetPacketUdp.put("agus", ArrayUtils.toObject(numChars));
        }
        
    }
    
    public double[] Sum(double[] one, double[] two){
        for (int i = 0; i < two.length; i++) {
            two[i] = one[i] + two[i];
        }
        return one;
    }
    
}
