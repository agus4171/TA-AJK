/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Agus
 */
public class FileReader implements Runnable{
    private BufferedReader br = null;
    private List<String> list;
    private String ngram;
    private Ngram ng = new Ngram();
    
    public FileReader(String file, List<String> list){
        try {
            this.list = list;
            this.br = new BufferedReader(new java.io.FileReader(file));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void run(){
        String line = null;
        while(true) {
            //System.out.println(Thread.currentThread().getName());
            this.list = new ArrayList<String>();
            synchronized(br) {
                try {
                    while((line = br.readLine()) != null) {
                        list.add(line);
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                Thread.sleep(1);
                display(this.list);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            if(line == null)
                break;
        }
    }
    
    public void display(List<String> list) {
        for(String str:list) {
            ngram = String.join(" ", list);
        }
        try {
            ng.Ngram(ngram);
        } catch (IOException ex) {
            Logger.getLogger(FileReader.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
