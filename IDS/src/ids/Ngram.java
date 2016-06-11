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
    private int len;
    private byte[] ascii;
    private double[] n;
    
    public double[] Ngram(String data){
        if (data != null) {
            n = new double[256];
            ascii = data.getBytes(StandardCharsets.US_ASCII);
            
            for (byte b : ascii) {
                if (b < 256) {
                    n[b] += 1;
                }
            }
        }
        return n;
    }
    
}
