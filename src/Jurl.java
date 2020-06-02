import controller.Utils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Jurl {
    private static HttpURLConnection connection;
    private static URL url;
    private static String[] commandLine;
    private static boolean showResponseHeader;
    private static boolean isRedirectAllowed;
    private static boolean saveResponsePermission;
    private static boolean saveRequestPermission;
    private static final String SAVING_DIRECTORY="\\saved_data\\";
    private static String responseName;
    private static File requestsFile;
    private static InputStream inputStream;
    private static String boundary;
    private static HashMap<String,String> formDataMap;
    public static void main(String[] args) {
        setDefaults();
        commandLine = args;
        createHTTPConnection();
        fireRequest();
        connection.disconnect();
    }
    private static void setDefaults(){
        showResponseHeader=false;
        isRedirectAllowed=false;
        saveRequestPermission=false;
        saveResponsePermission=false;
        responseName=null;
        requestsFile=new File(SAVING_DIRECTORY+"list.requests");
        boundary="-----------"+System.currentTimeMillis();
        formDataMap=new HashMap<>();
    }
    private static void createHTTPConnection() {
        try {
            setUrl();
            connection = (HttpURLConnection) url.openConnection();
            setMethod();
            setHeaders();
            setShowResponseHeader();
            if(connection.getRequestMethod().equals("POST")){
                connection.setRequestProperty("Content-Type","multipart/form-data; boundary="+boundary);
                connection.setDoOutput(true);
                BufferedOutputStream formDataOutput=new BufferedOutputStream(connection.getOutputStream());
                generateFormDataBody();
                Utils.bufferOutFormData(formDataMap,boundary,formDataOutput);
            }else if(connection.getRequestMethod().equals("HEAD")){
                showResponseHeader=true;
            }
        } catch (MalformedURLException e) {
//            e.printStackTrace();
            System.out.println("entered url is malformed");
            System.out.println(url);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection couldn't be established");
        }

    }
    private static void fireRequest(){
        try {
//
            System.out.println(connection.getResponseCode()+" - "+connection.getResponseMessage());
            if (connection.getResponseCode()/100==2)
                inputStream=connection.getInputStream();
            if(showResponseHeader)
                printResponseHeaders();

            if(!(connection.getRequestMethod().equals("HEAD"))&&
                    connection.getResponseCode()== HttpURLConnection.HTTP_OK){
                String responseBody=getResponseBody();
                System.out.println(responseBody);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally{
//            inputStream.close();
        }
    }
    private static String getResponseBody(){
        BufferedReader input=new BufferedReader(new InputStreamReader(inputStream));
        String line;
        StringBuilder responseBody=new StringBuilder();
        try{
            while((line=input.readLine())!=null){
                responseBody.append(line);
            }
            input.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return responseBody.toString();
    }
    //method for printing response headers
    private static void printResponseHeaders(){
        Map<String, List<String>> map=connection.getHeaderFields();
        for(String key:map.keySet()){
            System.out.print(key+": ");
            List <String>values=map.get(key);
            for(String value:values){
                System.out.println(" "+value);
            }
        }
    }
    //method for setting url
    private static void setUrl() throws MalformedURLException {
        try {
            url=new URL(commandLine[0]);

        }catch (ArrayIndexOutOfBoundsException e){
            System.out.println("note that url should be placed at the first place . syntax -> Jurl (url) ...");
        }

    }
    //method for setting headers  -H --headers
    private static void setHeaders(){
        for(int i=0;i<commandLine.length;i++){
            if(commandLine[i].equals("-H")||commandLine[i].equals("--header")){
                try{
                    //taking header
                    String header=commandLine[i+1];
                    header=header.replaceAll("^\"|\"$","");
                    String[] headerParts=header.split(":");
                    connection.setRequestProperty(headerParts[0],headerParts[1]);
                }catch (IndexOutOfBoundsException e){
                    System.out.println("You didn't specified a header");
                }catch(NullPointerException e){
                    System.out.println("Use proper format for header -> \"Key: Value\"");
                }
            }
        }
    }
    //method for setting request's method -M --method
    private static void setMethod() {
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-M") || commandLine[commandIndex].equals("--method")) {
                try{
                    connection.setRequestMethod(commandLine[commandIndex+1]);
                    if(commandLine[commandIndex+1].equals("POST"))
                        connection.setDoOutput(true);
                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e){
                    System.out.println("You didn't enter the request's method");
                }
            }
        }
       
    }
    //method for printing help  -h --help

    //method for setting redirect shit  -f
    private static void setRedirectPermission(){
        for(String command:commandLine){
            if(command.equals("-f"))
                isRedirectAllowed=true;
        }
    }
    //method for showing response headers -i
    private static void setShowResponseHeader(){
        for(String command: commandLine){
            if(command.equals("-i"))
                showResponseHeader=true;
        }
    }

    /**
     * searching for -O and --output argument and looks for the preferred name
     */
    private static void saveResponse(){
        int commandIndex=-1;
        for(int i=0;i<commandLine.length;i++){
            if(commandLine[i].equals("-O")||commandLine[i].equals("--output")){
                commandIndex=i;
                saveResponsePermission=true;
                break;
            }
        }
        /*
        looking for file name in array's next element
         */
        if(saveResponsePermission){
            try{
                if(!(commandLine[commandIndex+1].startsWith("-")||
                        commandLine[commandIndex+1].equals("http"))){
                    responseName=commandLine[commandIndex+1];
                }
            } catch (IndexOutOfBoundsException e){
                // do nothing
            }
        }

    }
    // method for saving request -S --save
    private static void saveRequest(){
        StringBuilder requestString=new StringBuilder();
        //preparing requests details
        requestString.append("url: ").append(url).append("|");
        requestString.append("method: ").append(connection.getRequestMethod()).append("|");
        // TODO: 5/28/2020 remember to save both header and formData shits
        try (FileOutputStream out=new FileOutputStream(requestsFile)){

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    //method for setting message body in form Data structure -d --data
    private static void generateFormDataBody(){
        for(int commandIndex=0;commandIndex<commandLine.length;commandIndex++){
            if(commandLine[commandIndex].equals("-d")||commandLine[commandIndex].equals("--data")){
                try{
                    if(!(commandLine[commandIndex+1].startsWith("-"))){
                        String formData=commandLine[commandIndex+1];
                        formData=formData.replaceAll("^\"|\"$","");
                        String[] formDataParts=formData.split("=");
                        formDataMap.put(formDataParts[0],formDataParts[1]);
//                        System.out.println(formDataParts[0]+"="+formDataParts[1]);
                    }
                }catch (IndexOutOfBoundsException e){
                    System.out.println("You didn't enter form-data");
                }catch (Exception e){
                    e.printStackTrace();
                    System.out.println("error in form data body . Syntax -> -d/--data \"key=value\"");
                }

            }
        }

    }
    //method for setting message body in JSON structure -j --json   (emtiazi)

    //method for uploading a file (request's POST method) --upload (emtiazi)

    //method to create a menu like thing for selecting requests

    //method to use proxy (phase 4) --proxy & --ip  (emtiazi)
}
