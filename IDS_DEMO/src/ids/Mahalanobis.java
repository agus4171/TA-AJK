/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

/**
 *
 * @author agus
 */
public class Mahalanobis {
    
    public double distance(double[] x, double[] y, double[] sd, double sf){
        double sum = 0;
        
        for (int i = 0; i < x.length; i++) {
            sum += Math.abs(x[i] - y[i])/(sd[i]+sf);
        }
        return sum;
    }
    
}
