/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import ids.DataPacket;
import java.io.File;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class SplitString {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        String str = "tcp-10.151.36.100-80-202.46.129.70-9000";
        String strDua = "selamat datang di dunia jaringan tcp-10.151.36.100-80-202.46.129.70-9000";
        ArrayList<Double> val = new ArrayList<>();
        val.add(2.0);
        val.add(34.90);
        val.add(2.30);
        //String delimiter = "-";
        ArrayList<Double[]> tes = new ArrayList<>();
        double[] value = new double[]{1.0, 1, 1};
        double[] value1 = new double[]{2.0, 2, 2};
        double[] value2 = new double[]{3.0, 3, 3};
        double[] value3 = new double[]{6.0, 4, 4};
        double[] value4 = new double[]{6.0, 4, 4};
        double[] value5 = new double[]{6.0, 4, 4};
        double[] value6 = new double[]{6.0, 4, 4};
        double[] value7 = new double[]{6.0, 4, 4};
        double[] value8 = new double[]{6.0, 4, 4};
        double[] value9 = new double[]{6.0, 4, 4};        
        
        
//        double[][] dataTraining = new double[][256];
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < value.length; j++) {
//                dataTraining[i][j] = value[j];
//            }
//        }
        double sum = 0;
        
        tes.add(ArrayUtils.toObject(value));
        tes.add(ArrayUtils.toObject(value1));
        tes.add(ArrayUtils.toObject(value2));
        tes.add(ArrayUtils.toObject(value3));
        tes.add(ArrayUtils.toObject(value4));
        tes.add(ArrayUtils.toObject(value5));
        tes.add(ArrayUtils.toObject(value6));
        tes.add(ArrayUtils.toObject(value7));
        tes.add(ArrayUtils.toObject(value8));
        tes.add(ArrayUtils.toObject(value9));
        System.out.println(tes.size());
        double[] mean = new double[tes.size()];
        double[] data = new double[tes.size()];   
        double[] sDeviasi = new double[tes.size()];
      
        for (int i = 0; i < tes.size(); i++) {
//            System.out.println(tes.get(i)[2]);
            for (int j = 0; j < 3; j++) {
                data[j] += tes.get(i)[j];
            }

        }
        for (int i = 0; i < 3; i++) {
            mean[i] = data[i]/tes.size();
            System.out.println(mean[i]);
        }

                                    
        for (int i = 0; i < 3; i++) {
            sDeviasi[i] = Math.sqrt(Math.pow(data[i]-mean[i], 2)/tes.size());
            System.out.println(sDeviasi[i]);
        }  
        
//        double[] a = ArrayUtils.toPrimitive(value);
//        Double[] v = ArrayUtils.toObject(a);
//        System.out.println(Arrays.toString(v));
//        String[] temp;
//        temp = str.split("-", 4);
//        for (int i = 0; i < temp.length; i++) {
//            System.out.println(temp[i]);
//        }
        
//        byte[] one = str.getBytes(Charset.forName("UTF-8"));
//        byte[] two = strDua.getBytes(Charset.forName("UTF-8"));
//        byte[] combined = new byte[one.length + two.length];
//
//        System.arraycopy(one,0,combined,0         ,one.length);
//        System.arraycopy(two,0,combined,one.length,two.length);
////        System.out.println(new String(combined, StandardCharsets.UTF_8));
//        File file = new File(System.getProperty("user.dir")+"/threshold/sapi");
////        System.out.println(System.getProperty("user.dir")+"/threshold/");
//        try {
//            file.createNewFile();
//        } catch (IOException ex) {
//            Logger.getLogger(SplitString.class.getName()).log(Level.SEVERE, null, ex);
//        }
        
    }
    
}
