/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author agus
 */
public class ReadFile {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws FileNotFoundException, IOException {
        String line;
        String[] ket;
        int countVal = 0;
        BufferedReader br = new BufferedReader(new FileReader("conf"));
        long start = System.currentTimeMillis();
        while ((line = br.readLine()) != null) {
//            switch (countVal) {
//                case 0:
//                    System.out.println(line);
//                    break;
//                case 1:
//                    System.out.println(line);
//                    break;
//                default:
//                    break;
//            }
            ket = line.split(":", 0);
            if (ket[0].equals("val")) {
                System.out.println(ket[1]);
            }
            countVal++;
        }
        long end = System.currentTimeMillis();
//        System.out.println(end - start);
    }
    
}
