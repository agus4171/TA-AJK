/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.util.Arrays;

/**
 *
 * @author agus
 */
public class Mahalanobis {
    private int d;
    private double mean, sd;
    private double[] data;
    
    public Mahalanobis(int d){
        this.d = d;
        data = new double[d];
    }
    
    public Mahalanobis(double[] a){
        d = a.length;
        data = new double[d];
        System.arraycopy(a, 0, data, 0, d);
        mean = mean(data);
        sd = standardDeviation(data);
//        System.out.println(mean+" "+sd);
    }
    
    public int length(){
        return d;
    }
    
    public int dimension(){
        return d;
    }
    
    public double mean(double[] data){
        double sum = 0.0;
        int count = 0;
        
        for (double e : data) {
            if (e != 0) {
                sum += e;
                count++;
            }            
        }
        return sum/count;
    }
    
    public double standardDeviation(double[] data){
        double sum = 0.0;
        int count = 0;
        for (double e : data) {
            if (e != 0) {
                sum += Math.pow(e-mean, 2);
                count++;
            }            
        }
        return Math.sqrt(sum/count);
    }
    
    public double dot(Mahalanobis that){
        if (this.d != that.d) throw new IllegalArgumentException("Dimension don't agree");
        double sum = 0.0;
        for (int i = 0; i < d; i++) {
            sum += (this.data[i] * that.data[i]);
        }
        return sum;
    }
    
    public double distance(double[] x, double[] y, double sf){
        double sum = 0.0;
        
        for (int i = 0; i < x.length; i++) {
            sum += Math.abs(x[i] - y[i])/(sd+sf);
        }
        return sum;
    }
    
    public double magnitude(){
        return Math.sqrt(this.dot(this));
    }
    
    public Mahalanobis plus(Mahalanobis that){
        if (this.d != that.d) throw new IllegalArgumentException("Dimensions don't agree");
        Mahalanobis c = new Mahalanobis(d);
        for (int i = 0; i < d; i++)
            c.data[i] = this.data[i] + that.data[i];
        return c;
    }
    
    public Mahalanobis minus(Mahalanobis that){
        if (this.d != that.d) throw new IllegalArgumentException("Dimensions don't agree");
        Mahalanobis c = new Mahalanobis(d);
        for (int i = 0; i < d; i++){
            c.data[i] = this.data[i] - that.data[i];
//            System.out.println(c.data[i]+"="+this.data[i]+" - "+that.data[i]);
        }
        return c;
    }
    
    public double distanceTo(Mahalanobis that){
        if (this.d != that.d) throw new IllegalArgumentException("Dimension don't agree");
        return this.minus(that).magnitude();
    }
    
    public double cartesian(int i){
        return data[i];
    }
    
    public Mahalanobis times(double alpha) {
        Mahalanobis c = new Mahalanobis(d);
        for (int i = 0; i < d; i++)
            c.data[i] = alpha * data[i];
        return c;
    }
    
    public Mahalanobis scale(double alpha) {
        Mahalanobis c = new Mahalanobis(d);
        for (int i = 0; i < d; i++)
            c.data[i] = alpha * data[i];
        return c;
    }
    
    public Mahalanobis direction() {
        if (this.magnitude() == 0.0) throw new ArithmeticException("Zero-vector has no direction");
        return this.times(1.0 / this.magnitude());
    }
    
    @Override
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < d; i++)
            s.append(data[i]).append(" ");
        return s.toString();
    }
    
}
