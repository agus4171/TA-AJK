/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.nio.charset.StandardCharsets;

/**
 *
 * @author agus
 */
public class Ngram {
    private int ascii;
    private double[] n;
    
    public double[] Ngram(byte[] data){
        if (data != null) {
            n = new double[256];
           
            for (byte b : data) {
                ascii = b & 0xFF;
                n[ascii] += 1;                
            }
        }
        return n;
    }
    
}
