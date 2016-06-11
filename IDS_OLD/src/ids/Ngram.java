/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

<<<<<<< HEAD
import java.io.IOException;
=======
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Arrays;
>>>>>>> remotes/origin/exp
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Agus
 */
public class Ngram {
<<<<<<< HEAD
    private int len, i, max;
    private char ch;
    private Map<Character, Integer> numChars;
=======
    private int len, i, max, ascii;
    private char ch;
    private Map<Integer, Integer> numChars;
    private double[] n;
>>>>>>> remotes/origin/exp

    Ngram(String str) {
        
    }

    Ngram() {
        
    }
    
<<<<<<< HEAD
    public String Ngram(String data) throws IOException {
        if(data != null){
            len = data.length();        
            numChars = new HashMap<Character, Integer>(Math.min(len, 256));
            for(i=0; i<len; i++){
                ch = data.charAt(i);
                if(!numChars.containsKey(ch)){
                    numChars.put(ch, 1);
                }
                else{
                    numChars.put(ch, numChars.get(ch)+1);
                }            
            }            
        }
        System.out.println(len);
        return new String("Jumlah: "+len+"\n"+numChars);
    }
    
=======
    public double[] Ngram(String data) throws IOException {
        if(data != null){
            len = data.length();
            n = new double[257];
            for(i=0; i<len; i++){
                ascii = (int) data.charAt(i);
                if(ascii < 256)
                    n[ascii] += 1;
                else
                    n[256] += 1;
            } 
        }
        return n;
    }    
>>>>>>> remotes/origin/exp
}
