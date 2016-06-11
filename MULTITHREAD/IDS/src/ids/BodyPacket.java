/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package ids;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author agus
 */
public class BodyPacket {
    byte[] bytes = null;
        
    public BodyPacket(byte[] bytes){
        this.bytes = bytes;      
    }

    public void addBytes(byte[] bytes){
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream(); 
        try { 
            outputStream.write(this.bytes);
            outputStream.write(bytes); 
            this.bytes = outputStream.toByteArray(); 
        } catch (IOException ex) {
            Logger.getLogger(PacketReader.class.getName()).log(Level.SEVERE, null, ex);
        }            
    }

    public byte[] getBytes(){
        return bytes;
    }
    
}
