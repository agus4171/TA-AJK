/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

/**
 *
 * @author Agus
 */
public class Hasil {
    private double[] data;
    private int x;
    
    public Hasil(double[] val, int z){
        this.data = val;
        this.x = z;
    }

    /**
     * @return the data
     */
    public double[] getData() {
        return data;
    }

    /**
     * @return the x
     */
    public int getX() {
        return x;
    }

    /**
     * @param data the data to set
     */
    public void setData(double[] data) {
        this.data = data;
    }

    /**
     * @param x the x to set
     */
    public void setX(int x) {
        this.x = x;
    }
}
