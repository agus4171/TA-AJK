/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.util.Vector;

/**
 *
 * @author Agus
 */
public class Mahalanobis {
    private int d;
    private double[] data;
    
    public Mahalanobis(int d){
        this.d = d;
        data = new double[d];
    }
    public Mahalanobis(double[] a){
        d = a.length;
        data = new double[d];
        for (int i = 0; i < d; i++) {
            data[i] = a[i];
        }
    }
    
    public int length(){
        return d;
    }
    
    public int dimension(){
        return d;
    }
    
    public double dot(Mahalanobis that){
        if (this.d != that.d) throw new IllegalArgumentException("Dimension don't agree");
        double sum = 0.0;
        for (int i = 0; i < d; i++) {
            sum = sum + (this.data[i] * that.data[i]);
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
        for (int i = 0; i < d; i++)
            c.data[i] = this.data[i] - that.data[i];
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
    
    public String toString() {
        StringBuilder s = new StringBuilder();
        for (int i = 0; i < d; i++)
            s.append(data[i] + " ");
        return s.toString();
    }
}
