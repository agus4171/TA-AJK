/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.util.ArrayList;
import java.util.Arrays;
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
        System.out.println(Math.round(3.019961948*100.0)/100.0);
        String str = "tcp-10.151.36.100-80-202.46.129.70-9000";
        String strName = "senin_inside.tcpdump";
        System.out.println(strName.replaceAll("\\.", ""));
        String strDua = "selamat datang di dunia jaringan tcp-10.151.36.100-80-202.46.129.70-9000";
        ArrayList<Double> val = new ArrayList<>();
        val.add(2.0);
        val.add(34.90);
        val.add(2.30);
        //String delimiter = "-";
        ArrayList<Double[]> tes = new ArrayList<>();
        double[] value = new double[]{172};
        double[] value1 = new double[]{167};
        double[] value2 = new double[]{180};
        double[] value3 = new double[]{170};
        double[] value4 = new double[]{169};
        double[] value5 = new double[]{160};
        double[] value6 = new double[]{175};
        double[] value7 = new double[]{165};
        double[] value8 = new double[]{173};
        double[] value9 = new double[]{170};        
        
        
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
        double[] mean = new double[value.length];
        double[] data = new double[value.length];   
        double[] sDeviasi = new double[value.length];
        double[] pow = new double[value.length];
      
        for (int i = 0; i < tes.size(); i++) {
//            System.out.println(tes.get(i)[2]);
            for (int j = 0; j < value.length; j++) {
                data[j] += tes.get(i)[j];
//                System.out.println(j);
            }

        }
        System.out.println(Arrays.toString(data));
//        System.out.println(Arrays.toString(data));
        for (int i = 0; i < tes.size(); i++) {
//            System.out.println(tes.get(i)[2]);
            for (int j = 0; j < value.length; j++) {
                pow[j] += Math.pow(tes.get(i)[j], 2);
//                System.out.println(j);
            }

        }
        System.out.println(Arrays.toString(pow));
        for (int i = 0; i < data.length; i++) {
            mean[i] = data[i]/tes.size();
        }
//        System.out.println(Arrays.toString(mean));
//
////                                    
//        for (int i = 0; i < tes.size(); i++) {
//            for (int j = 0; j < value.length; j++) {
//                sDeviasi[j] += Math.pow(tes.get(i)[j]-mean[j], 2);
//            }
//
//        } 
//        System.out.println(Arrays.toString(sDeviasi));
        for (int i = 0; i < value.length; i++) {
            System.out.println(Math.round(Math.sqrt((tes.size()*pow[i]-Math.pow(data[i], 2))/(tes.size()*(tes.size()-1)))*100.0)/100.0);
            sDeviasi[i] = Math.sqrt((tes.size()*pow[i] - Math.pow(data[i], 2))/tes.size()*(tes.size()-1));

        } 
//        System.out.println(Arrays.toString(sDeviasi));
        
        for (int i = 0; i < data.length; i++) {
            sDeviasi[i] = Math.sqrt(sDeviasi[i]/(tes.size()-1));
        }
//        System.out.println(Arrays.toString(sDeviasi));
//        1.1.1
//        2.3.4
//        System.out.println(Arrays.toString(data));
        
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
