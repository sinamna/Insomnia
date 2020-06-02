package controller;

import view.Info;

import java.io.BufferedOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class Utils {
    public static void bufferOutFormData(HashMap<String,String> formDataMap, String boundary
            , BufferedOutputStream bufferedOutputStream) throws IOException {
        try{
            for(String key:formDataMap.keySet()){
                bufferedOutputStream.write(("--"+boundary+"\r\n").getBytes());
                bufferedOutputStream.write(("Content Disposition: form-data; name=\""+key +"\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((formDataMap.get(key)+"\r\n").getBytes());
            }
            bufferedOutputStream.write(("--"+boundary+"--\r\n").getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
        }catch (NullPointerException e){
            System.out.println("You didn't specified form-data");
        }

    }
}
