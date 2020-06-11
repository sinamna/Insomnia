package model;

import java.io.*;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;

/**
 * class containing static methods doing model.Jurl program operations
 */
public class ModelUtils {
    /**
     * sends form data to using given output stream
     * @param formDataMap the map of form-data body
     * @param boundary the boundary to split different parts
     * @param bufferedOutputStream the output stream of the connection
     * @throws IOException IOException
     */
    public static void bufferOutFormData(HashMap<String, String> formDataMap, String boundary
            , BufferedOutputStream bufferedOutputStream) throws IOException {
        try {

            for (String key : formDataMap.keySet()) {
                bufferedOutputStream.write(("--" + boundary + "\r\n").getBytes());
                bufferedOutputStream.write(("Content-Disposition: form-data; name=\"" + key + "\"\r\n\r\n").getBytes());
                bufferedOutputStream.write((formDataMap.get(key) + "\r\n").getBytes());
            }
            bufferedOutputStream.write(("--" + boundary + "--\r\n").getBytes());
            bufferedOutputStream.flush();
            bufferedOutputStream.close();
            //todo : form data still wont be sent
        } catch (NullPointerException e) {
            System.out.println("You didn't specified form-data");
        }
    }

    /**
     * send json body to the specified output stream
     * @param jsonBody json body to be sent
     * @param bufferedOutputStream the output stream used to send json body
     * @throws IOException IOException
     */
    public static void bufferOutJSON(String jsonBody, BufferedOutputStream bufferedOutputStream) throws IOException {
        byte[] jsonBodyBytes = jsonBody.getBytes();
        bufferedOutputStream.write(jsonBodyBytes, 0, jsonBodyBytes.length);
    }

    /**
     * saves the response with given name to pre-specified path
     * @param directory directory for the file to be saved in
     * @param name the name of file which can be empty ,if so method generates name based on type and date
     * @param responseBody the body of response to be saved
     * @param contentType the content type of response body
     */
    public static void saveResponseToFile(String directory, String name, byte[] responseBody, String contentType) {
        if (name == null) {
            Date dNow = new Date();
            SimpleDateFormat dateFormatter =
                    new SimpleDateFormat("yyyy.MM.dd'_'hh.mm");
            name = "output_" + dateFormatter.format(dNow);
            String type = contentType.split(";")[0];
            // looking for familiar types
            if (type.equals("text/html"))
                name += ".html";
            else if (type.equals("image/png"))
                name += ".png";
            else if (type.equals("text/plain"))
                name += ".txt";
        }

        //the current path is in src folder and we want to save data to "saved_data" app
        String filePath;
        if (System.getProperty("user.dir").endsWith("src"))
            filePath = Paths.get("..").getFileName().toString() + directory + name;
        else
            filePath = directory + name;

        try {
            BufferedOutputStream writer = new BufferedOutputStream(new FileOutputStream(filePath));
            writer.write(responseBody);
            System.out.println("\n**file saved**");
            writer.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            System.out.println("There is no response body to write to file.");
        }

    }


    /**
     * saves request to given file
     *
     * @param requestDetail the byte form of request string representing the request details
     * @param fileToSave    the file which requests are saved
     */
    public static void saveRequestToFile(byte[] requestDetail, File fileToSave) {
        try {
            //it appends new data to the file
            BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(fileToSave, true));
            outputStream.write(requestDetail);
            System.out.println("request saved ");
            outputStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * creates a list to be printed with given file containing formatted list of requests
     * @param fileToRead the file to read requests from
     * @return the string representing list
     */
    public static String createRequestList(File fileToRead) {
        StringBuilder requestList = new StringBuilder();
        try {

            String requests = readListFromFile(fileToRead);
            requests = requests.replaceAll("^&", "");
            String[] requestArray = requests.split("&");
            for (int i = 0; i < requestArray.length; i++) {
                String request = requestArray[i];
                if (!request.isEmpty()){
                    requestList.append(i + 1).append("- ");
                    request = request.replaceAll("#", " | ").replaceAll(":", ": ")
                            .replaceAll(";", "; ").replaceAll("form-data=", "form-data: ")
                            .replaceAll("json=", "json: ").replaceFirst(": //","://");

                    requestList.append(request);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return requestList.toString();
    }

    /**
     * reads bytes from file and creates string representing it
     * @param fileToRead file to read from
     * @return the string represented formatted requests list
     * @throws IOException the IOException
     */
    private static String readListFromFile(File fileToRead) throws IOException {
            FileInputStream inputStream=new FileInputStream(fileToRead);
            byte[] fileContent=new byte[(int) fileToRead.length()];
            inputStream.read(fileContent);
            inputStream.close();
            return new String (fileContent);

    }

    /**
     * creates array of requests from list
     * @param fileToRead file to read list of requests from
     * @return array of string representing requests
     */
    public static String[] createRequestArray(File fileToRead){
        String requestList=null;
        try {
            requestList=readListFromFile(fileToRead);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        requestList = requestList.replaceAll("^&", "");
        String[] requests = requestList.split("&");
        return requests;
    }

    /**
     * recreates request command using given requests details taken from request list
     * @param requestDetails the details of request
     * @return string representing commands of request
     */
    public static String createCommandLine(String requestDetails) {
        StringBuilder requestCommandLine = new StringBuilder();
        String[] requestParts = requestDetails.trim().split("#");
        // url is the first part of request
        String[] urlParts = requestParts[0].split(":");
        requestCommandLine.append(urlParts[1]).append(":").append(urlParts[2]).append(" ");
        // method is the second part
        requestCommandLine.append("-M ").append(requestParts[1].split(":")[1]).append(" ");
        //headers are the third parts
        try {
            String[] headers = requestParts[2].split(":")[1].split(";");
            for (String header : headers) {
                header = header.replaceAll("=", ":");
                requestCommandLine.append("-H ").append("\"").append(header).append("\" ");
            }
        } catch (IndexOutOfBoundsException e) {
            //does nothing
        }
        //request bodies are in 4th part
        try {
            String[] bodies = requestParts[3].split(":")[1].split(";");
            for (String body : bodies) {
                String[] bodyParts = body.split("=");
                if (bodyParts[0].equals("form-data")) {
                    requestCommandLine.append("-d ").append("\"").append(bodyParts[1]).append("=")
                            .append(bodyParts[2]).append("\" ");
                } else {
                    requestCommandLine.append("--json ").append(bodyParts[1]).append(" ");
                }
            }
        } catch (IndexOutOfBoundsException e) {
            //does nothing
        }

        //redirect permission is in 5th position
        if (requestParts[4].split(":")[1].equals("true"))
            requestCommandLine.append("-f ");

        //showing header option is in 6th position
        if (requestParts[5].split(":")[1].equals("true"))
            requestCommandLine.append("-i ");

        System.out.println(requestCommandLine.toString());
        return requestCommandLine.toString();
    }


}
