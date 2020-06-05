package controller;

import java.io.BufferedOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

public class Utils {
    public static void bufferOutFormData(HashMap<String,String> formDataMap, String boundary
            , BufferedOutputStream bufferedOutputStream) throws IOException {
        try{
//            System.out.println(formDataMap);
//            StringBuilder str=new StringBuilder();
            for(String key:formDataMap.keySet()){
                bufferedOutputStream.write(("--"+boundary+"\r\n").getBytes());
//                str.append("--"+boundary+"\r\n").append("Content Disposition: form-data; name=\""+key +"\"\r\n\r\n")
//                        .append(formDataMap.get(key)+"\r\n");
                bufferedOutputStream.write(("Content-Disposition: form-data; name=\""+key +"\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((formDataMap.get(key)+"\r\n").getBytes());
//                System.out.println(str.toString());
            }
            bufferedOutputStream.write(("--"+boundary+"--\r\n").getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            //todo : form data still wont be sent
        }catch (NullPointerException e){
            System.out.println("You didn't specified form-data");
        }
    }






    public static void bufferOutJSON(String jsonBody,BufferedOutputStream bufferedOutputStream) throws IOException {
        byte[] jsonBodyBytes=jsonBody.getBytes();
        bufferedOutputStream.write(jsonBodyBytes,0,jsonBodyBytes.length);
    }
    public static void saveResponseToFile(String directory,String name,byte[] responseBody,String contentType){
        if(name==null){
            Date dNow=new Date();
            SimpleDateFormat dateFormatter=
                    new SimpleDateFormat("yyyy.MM.dd'_'hh.mm");
            name="output_"+dateFormatter.format(dNow);
            String type=contentType.split(";")[0];
            if(type.equals("text/html"))
                name+=".html";
            else if (type.equals("image/png"))
                name+=".png";
            else if(type.equals("text/plain"))
                name+=".txt";
        }

        //the current path is in src folder and we want to save data to "saved_data" app
        String filePath;
        if(System.getProperty("user.dir").endsWith("src"))
            filePath=Paths.get("..").getFileName().toString()+directory+name;
        else
            filePath=directory+name;

        try {
            BufferedOutputStream writer=new BufferedOutputStream(new FileOutputStream(filePath));
            writer.write(responseBody);
            System.out.println("\n**file saved**");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }catch (NullPointerException e){
            System.out.println("There is no response body to write to file.");
        }

    }
}
