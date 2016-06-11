/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import ids.*;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Agus
 */
public class FileRead {

	/**Java multithreaded reading large files
	 * @param args
	 */
    public static void main(String[] args) {
        List<String> list = new ArrayList<>();
        File file = new File("F:\\tcpResult");
        File[] listFile = file.listFiles();
        for(int i=0; i< listFile.length; i++){
            if(listFile[i].isFile()){
                System.out.println("File: "+listFile[i].getName());
                list = new ArrayList<String>();
                Thread t1 = new Thread(new MultiThread(listFile[i].getAbsolutePath(), list));
                t1.start();
                System.out.println("Running: "+t1.getName()+"_"+t1.getId());
            }else if(listFile[i].isDirectory()){
                System.out.println("Directory: "+listFile[i].getName());
            }
        }
    }        
}


 class MultiThread implements Runnable{	
    private static BufferedReader br = null;
    private List<String> list;
    private String path;

    MultiThread(String absolutePath, List<String> list) {
        this.path = absolutePath;
        this.list = list;
        try {
            br = new BufferedReader(new FileReader(path),10);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(MultiThread.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public void run() {
        String line = null;
        int count = 0;
        while(true) {
            //System.out.println(Thread.currentThread().getName());
            //this.list = new ArrayList<String>();
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
                Thread.sleep(10);
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
        }
        System.out.println(list.size());
    }
	
}
