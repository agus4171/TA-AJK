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
        String[] fileName;
        String str = "/media/agus/4DD244C0638395F0/datates/4/wednesday/inside.tcpdump";
        fileName = str.replace('/', '-').split("-", 0);
        System.out.println(fileName[fileName.length-2]+"_"+fileName[fileName.length-1]);
        ArrayList<Double> val = new ArrayList<>();
        val.add(2.0);
        val.add(34.90);
        val.add(2.30);
        //String delimiter = "-";
        Double[] value = new Double[]{2.0, 34.90, 2.30};
        double[] a = ArrayUtils.toPrimitive(value);
        Double[] v = ArrayUtils.toObject(a);
        System.out.println(Arrays.toString(v));
        String[] temp;
        temp = str.split("-", 4);
        for (int i = 0; i < temp.length; i++) {
//            System.out.println(temp[i]);
        }
        
    }
    
}
