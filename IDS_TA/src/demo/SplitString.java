/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.io.File;
import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;
import org.apache.commons.lang3.ArrayUtils;

/**
 *
 * @author agus
 */
public class SplitString {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws IOException {
        System.out.println("Start time : "+new String(new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss a").format(Calendar.getInstance().getTime())));
        System.out.println("-------------------------------------");
        System.out.println("Protokol | Date | Source | Destination | Distance");
        System.out.println("-------------------------------------------------");
        System.out.println("#################################################");
//        System.out.println(Math.round(3.019961948*100.0)/100.0);
        Calendar calNewYork = Calendar.getInstance(TimeZone.getTimeZone("America/New_York"));

//        calNewYork.setTimeInMillis(922712402);
//        calNewYork.setTimeZone(TimeZone.getTimeZone("America/New_York"));
//        System.out.println(calNewYork.getTime());
//        System.out.println("Time in New York: " + calNewYork.get(Calendar.HOUR_OF_DAY)+":"+calNewYork.get(Calendar.MINUTE));
        String ep = "922712402";
        Long lp = Long.parseLong(ep);
        System.out.println(lp);
        Date dateE = new Date(lp*1000L);
        DateFormat format = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
        format.setTimeZone(TimeZone.getTimeZone("America/New_York"));
        String formatted = format.format(dateE);
        System.out.println(formatted);
        
        format.setTimeZone(TimeZone.getTimeZone("Australia/Sydney"));
        formatted = format.format(dateE);
//        System.out.println(formatted);
        
        File dir = new File("new_folder/sa");
        if (!dir.exists()) {
            dir.mkdirs();
        }
        File baru = new File("new_folder/"+"coba");
        if (!baru.exists()) {
            baru.createNewFile();
        }
        SimpleDateFormat date = new SimpleDateFormat("yyyy_MMMM_EEEE-dd_hha");
        Date dateNow = new Date();
        System.out.println(date.format(dateNow));
        String[] time = date.format(dateNow).split("_");
        System.out.println(time[0]+time[1]);
        String str = "tcp-10.151.36.100-80-202.46.129.70-9000";
        String strName = "Threshold : 80-40000: 53-20000: 25-11000: 123-10000: 79-1700: 23-4400: 21-1800";
        String[] head = strName.split(":");
        System.out.println(Arrays.toString(head).replace(" ", ""));
//        System.out.println(strName.replaceAll("\\.", ""));
        String strDua = "selamat datang di dunia jaringan tcp-10.151.36.100-80-202.46.129.70-9000";
        ArrayList<Double> val = new ArrayList<>();
        val.add(2.0);
        val.add(34.90);
        val.add(2.30);
        //String delimiter = "-";
        ArrayList<Double[]> tes = new ArrayList<>();
        double[] value = new double[]{1, 2, 1, 0, 2, 1, 0, 1, 0, 0};
        double[] value1 = new double[]{0, 2, 3, 0, 2, 1, 0, 1, 0, 0};
        double[] value2 = new double[]{1, 0, 0, 2, 1, 2, 0, 0, 0, 0};
        double[] value3 = new double[]{1, 2, 1, 0, 2, 1, 0, 2, 0, 3};
        double[] value4 = new double[]{0, 2, 3, 3, 0, 0, 3, 1, 0, 0};
        double[] value5 = new double[]{0, 1, 1, 0, 2, 3, 0, 0, 0, 1};
        double[] value6 = new double[]{0, 0, 3, 0, 0, 3, 1, 2, 1, 0};
        double[] value7 = new double[]{0, 2, 0, 2, 2, 1, 0, 1, 3, 0};
        double[] value8 = new double[]{0, 0, 0, 0, 1, 1, 0, 3, 0, 2};
        double[] value9 = new double[]{0, 2, 1, 0, 2, 3, 0, 1, 0, 0};        
        
        
//        double[][] dataTraining = new double[][256];
//        for (int i = 0; i < 4; i++) {
//            for (int j = 0; j < value.length; j++) {
//                dataTraining[i][j] = value[j];
//            }
//        }
        
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
//        
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 1, 0, 2, 3, 0, 1, 0, 0}));
        tes.add(ArrayUtils.toObject(new double[]{0, 0, 0, 0, 1, 1, 0, 3, 0, 2}));
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 0, 2, 2, 3, 0, 1, 3, 0}));
        tes.add(ArrayUtils.toObject(new double[]{1, 0, 0, 2, 2, 0, 0, 0, 0, 0}));
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 2, 3, 0, 0, 3, 1, 0, 0}));
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 1, 0, 2, 3, 0, 1, 0, 0}));
        tes.add(ArrayUtils.toObject(new double[]{0, 0, 2, 0, 2, 1, 0, 3, 0, 2}));
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 0, 2, 2, 1, 0, 1, 3, 0}));
        tes.add(ArrayUtils.toObject(new double[]{1, 0, 0, 2, 1, 2, 0, 0, 0, 0}));
        tes.add(ArrayUtils.toObject(new double[]{0, 2, 3, 3, 1, 0, 3, 1, 0, 0}));
        System.out.println(tes.size());
        double[] mean = new double[value.length];
        double[] data = new double[value.length];   
        double[] sDeviasi = new double[value.length];
        double[] sDeviasiTemp = new double[value.length];
        double[] pow = new double[value.length];
      
        for (int i = 0; i < tes.size(); i++) {
//            System.out.println(tes.get(i)[2]);
            for (int j = 0; j < value.length; j++) {
                data[j] += tes.get(i)[j];
                pow[j] += Math.pow(tes.get(i)[j], 2);
//                System.out.println(j);
            }

        }
        System.out.println("sum "+Arrays.toString(data));
//        System.out.println(Arrays.toString(data));
//        for (int i = 0; i < tes.size(); i++) {
////            System.out.println(tes.get(i)[2]);
//            for (int j = 0; j < value.length; j++) {
//                
////                System.out.println(j);
//            }
//
//        }
        System.out.println("quadratic "+Arrays.toString(pow));
        for (int i = 0; i < data.length; i++) {
            mean[i] = data[i]/tes.size();
        }
        System.out.println("mean "+Arrays.toString(mean));
//
////                                    
        for (int i = 0; i < tes.size(); i++) {
            for (int j = 0; j < value.length; j++) {
                sDeviasi[j] += Math.pow(tes.get(i)[j]-mean[j], 2);
//                System.out.println(sDeviasi[j]);
            }
        } 
        System.out.println(Arrays.toString(sDeviasi));
        for (int i = 0; i < sDeviasi.length; i++) {
            sDeviasiTemp[i] = Math.round(Math.sqrt(sDeviasi[i]/(tes.size()-1))*100.0)/100.0;
        }
        System.out.println(Arrays.toString(sDeviasiTemp));
        
        for (int i = 0; i < value.length; i++) {
            sDeviasi[i] = Math.round(Math.sqrt((tes.size()*pow[i]-Math.pow(data[i], 2))/(tes.size()*(tes.size()-1)))*100.0)/100.0;
//            sDeviasi[i] = Math.sqrt((tes.size()*pow[i] - Math.pow(data[i], 2))/tes.size()*(tes.size()-1));

        } 
        System.out.println("deviasi "+Arrays.toString(sDeviasi));
        
        for (int i = 0; i < data.length; i++) {
            sDeviasi[i] = Math.sqrt(sDeviasi[i]/(tes.size()-1));
        }
        double[] newTest = new double[]{9, 2, 7, 8, 6, 7, 0, 1, 0, 0};
        double sum = 0;
        for (int i = 0; i < newTest.length; i++) {
            sum += Math.abs(newTest[i] - mean[i])/(sDeviasi[i]+0.001);
        }
        System.out.println(sum);
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
