/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package demo;

import java.util.Random;
import java.util.Scanner;

/**
 *
 * @author agus
 */
public class StandarDeviasi {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
//        int n,i,jlh=0,bsr,kcl;
//        float sd,sigma=0,rata;
//        Random x=new Random();
//        Scanner read=new Scanner(System.in);
////        System.out.print("Input banyaknya data : ");
//        n=read.nextInt();
//        int[]A=new int[n];
//        
//        for(i=0;i<n;i++){
//            A[i]=x.nextInt(200);
//            jlh=jlh+A[i];
////            System.out.println("Data "+(i+1)+ " : "+A[i]);
//        }
//        rata=jlh/n;
//        bsr=A[0]; kcl=A[0];
//        
//        for(i=0;i<n;i++){
//            sigma += ((A[i]-rata)*(A[i]-rata));
//            if(A[i]>bsr)
//                bsr=A[i];
//            if(A[i]<kcl)
//                kcl=A[i];
//        }
//        sd=(sigma/n)/2;
//        System.out.println("Jumlah "+ n +" buah data random = "+jlh);
//        System.out.println("Rata-rata = "+rata);
//        System.out.println("Bilangan terbesar = "+bsr);
//        System.out.println("Bilangan terkecil = "+kcl);
//        System.out.println("Standar Deviasi   = "+sd);
        
        int [] numbers = {10, 20, 30, 40, 50};

        for(int xx : numbers ) {
            if( xx == 30 ) continue;
            System.out.print( xx );
            System.out.print("\n");
        }
    }
    
}
