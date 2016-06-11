/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

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
        Double[] value = new Double[]{2.0, 34.90, 2.30};
        double[] a = ArrayUtils.toPrimitive(value);
        Double[] v = ArrayUtils.toObject(a);
//        System.out.println(Arrays.toString(v));
        String[] temp;
        temp = str.split("-", 4);
        for (int i = 0; i < temp.length; i++) {
//            System.out.println(temp[i]);
        }
        
        byte[] one = str.getBytes(Charset.forName("UTF-8"));
        byte[] two = strDua.getBytes(Charset.forName("UTF-8"));
        byte[] combined = new byte[one.length + two.length];

        System.arraycopy(one,0,combined,0         ,one.length);
        System.arraycopy(two,0,combined,one.length,two.length);
        System.out.println(new String(combined, StandardCharsets.UTF_8));
        File file = new File(System.getProperty("user.dir")+"/threshold/sapi");
//        System.out.println(System.getProperty("user.dir")+"/threshold/");
        try {
            file.createNewFile();
        } catch (IOException ex) {
            Logger.getLogger(SplitString.class.getName()).log(Level.SEVERE, null, ex);
        }
        
    }
    
}
