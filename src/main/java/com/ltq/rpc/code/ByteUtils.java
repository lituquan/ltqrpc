package com.ltq.rpc.code;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class ByteUtils {
    public static Object BytetoArray (byte[] obj) {      
        try {        
        	ByteArrayInputStream bos = new ByteArrayInputStream(obj);      
        	ObjectInputStream oos = new ObjectInputStream(bos);         
            Object readObject = oos.readObject();                   
            oos.close();         
            bos.close();     
            return readObject; 
        } catch (Exception ex) {        
            ex.printStackTrace();   
        }      
        return null;
    }
    
    
	public static byte[] toByteArray (Object obj) {      
        byte[] bytes = null;      
        ByteArrayOutputStream bos = new ByteArrayOutputStream();      
        try {        
            ObjectOutputStream oos = new ObjectOutputStream(bos);         
            oos.writeObject(obj);        
            oos.flush();         
            bytes = bos.toByteArray ();      
            oos.close();         
            bos.close();        
        } catch (IOException ex) {        
            ex.printStackTrace();   
        }      
        return bytes;    
    } 
}