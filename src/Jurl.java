import controller.Utils;
import netscape.javascript.JSObject;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Jurl {
    private static HttpURLConnection connection;
    private static URL url;
    private static String[] commandLine;
    private static boolean showResponseHeader;
    private static boolean isRedirectAllowed;
    private static boolean saveResponsePermission;
    private static boolean saveRequestPermission;
    private static final String SAVING_DIRECTORY = "saved_data\\";
    private static String responseName;
    private static File requestsFile;
    private static InputStream inputStream;
    private static String boundary;
    private static HashMap<String, String> formDataMap;
    private static String jsonBody;
    private static byte[] responseBody;

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        String[] commandLine = input.nextLine().trim().split(" ");
        createHTTPConnection(commandLine);
//        connection.disconnect();
    }

    private static void setDefaults() {
        showResponseHeader = false;
        isRedirectAllowed = false;
        saveRequestPermission = false;
        saveResponsePermission = false;
        responseName = null;
        jsonBody = null;
        requestsFile = new File(SAVING_DIRECTORY + "list.requests");
        boundary = "-----------" + System.currentTimeMillis();
        formDataMap = new HashMap<>();
//        HttpURLConnection.setFollowRedirects(false);
    }

    private static void createHTTPConnection(String[] args) {
        setDefaults();
        commandLine = args;
        try {
            setUrl();
            connection = (HttpURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            setMethod();
            setHeaders();
            setShowResponseHeader();
            setSaveRequestPermission();
            setSaveResponsePermission();
            setRedirectPermission();
            bodyCheck();
            if (connection.getRequestMethod().equals("HEAD")) {
                showResponseHeader = true;
            }
            //firing request
            fireRequest();

        } catch (MalformedURLException e) {
//            e.printStackTrace();
            System.out.println("entered url is malformed");
            System.out.println(url);

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection couldn't be established");
        } catch (WrongUserInputException e) {
            connection.disconnect();
        }

    }

    private static void fireRequest() throws WrongUserInputException {
        try {
            System.out.println(connection.getResponseCode() + " - " + connection.getResponseMessage());
            //todo :idk yet but this is wrong
            int status = connection.getResponseCode();
            if (status / 100 == 2)
             inputStream = connection.getInputStream();

            //printing response headers
            if (showResponseHeader)
                printResponseHeaders();
            if (!(connection.getRequestMethod().equals("HEAD")) &&
                    connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                receiveResponseBody();
                System.out.println(new String(responseBody));
            }

            //saving response based on boolean value
            if (saveResponsePermission)
                Utils.saveResponseToFile(SAVING_DIRECTORY, responseName, getResponseBody(),
                        connection.getHeaderField("Content-Type"));

            if (isRedirectAllowed) {

            }
        } catch(UnknownHostException e){
            System.out.println("Unknown Host ");
            throw new WrongUserInputException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrongUserInputException();
        } finally {
//            inputStream.close();
        }
    }

    private static void receiveResponseBody() throws IOException {
//        BufferedReader input = new BufferedReader(new InputStreamReader(inputStream));
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteRead = -1;
        byte[] data = new byte[4096];
        while ((byteRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, byteRead);
        }
        responseBody = buffer.toByteArray();
//        String line;
//        StringBuilder receivedResponseBody = new StringBuilder();
//        try {
//            while ((line = input.readLine()) != null) {
//                receivedResponseBody.append(line);
//            }
//            input.close();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        responseBody = receivedResponseBody.toString();
    }

    public static byte[] getResponseBody() {
        return responseBody;
    }

    //method for printing response headers
    private static void printResponseHeaders() {
        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key : map.keySet()) {
            System.out.print(" " + key + ": ");
            List<String> values = map.get(key);
            for (String value : values) {
                System.out.println(" " + value);
            }
        }
        System.out.println("\n");
    }

    //method for setting url
    private static void setUrl() throws MalformedURLException {
        try {
            url = new URL(commandLine[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
//            System.out.println("error in setURL method");
            System.out.println("note that url should be placed at the first place . syntax -> Jurl (url) ...");
        }

    }

    //method for setting headers  -H --headers
    private static void setHeaders() {
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i].equals("-H") || commandLine[i].equals("--header")) {
                try {
                    //taking header
                    String header = commandLine[i + 1];
                    header = header.replaceAll("^\"|\"$", "");
                    String[] headerParts = header.split(":");
                    connection.setRequestProperty(headerParts[0], headerParts[1]);
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("You didn't specified a header");
                } catch (NullPointerException e) {
                    System.out.println("Use proper format for header -> \"Key: Value\"");
                }
            }
        }
    }

    //method for setting request's method -M --method
    private static void setMethod() throws WrongUserInputException {
        String[] validMethods = {"GET", "POST", "DELETE", "PUT"};
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-M") || commandLine[commandIndex].equals("--method")) {
                try {
                    String method = commandLine[commandIndex + 1];
                    for (String validMethod : validMethods) {
                        if (method.equals(validMethod)) {
                            connection.setRequestMethod(method);
                            return;
                        }

                    }
                    // TODO: 6/2/2020 connection should be disconnected when user enters the wrong input
                    System.out.println("entered method isn't valid");
                    throw new WrongUserInputException();

                } catch (ProtocolException e) {
                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("You didn't enter the request's method");
                }
            }
        }
    }
    //method for printing help  -h --help

    //method for setting redirect shit  -f
    //TODO: do the redirect shit
    private static void setRedirectPermission() {
        for (String command : commandLine) {
            if (command.equals("-f"))
                isRedirectAllowed = true;
        }
    }

    /**
     * looks through the commandline for the specified argument to set the boolean value in order to show the
     * headers in response
     */
    private static void setShowResponseHeader() {
        for (String command : commandLine) {
            if (command.equals("-i"))
                showResponseHeader = true;
        }
    }

    /**
     * searching for -O and --output argument and looks for the preferred name
     */
    private static void setSaveResponsePermission() {
        int commandIndex = -1;
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i].equals("-O") || commandLine[i].equals("--output")) {
                commandIndex = i;
                saveResponsePermission = true;
//                System.out.println(saveResponsePermission);
                break;
            }
        }
        /*
        looking for file name in array's next element
         */
        if (saveResponsePermission) {
            try {
                if (!(commandLine[commandIndex + 1].startsWith("-") ||
                        commandLine[commandIndex + 1].equals("http"))) {
                    responseName = commandLine[commandIndex + 1];
                }
            } catch (IndexOutOfBoundsException e) {
                // do nothing
            }
            //error might happen for names i guess
        }

    }

    private static void setSaveRequestPermission() {
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-S") || commandLine[commandIndex].equals("--save")) {
                saveRequestPermission = true;
            }
        }
    }

    // method for saving request -S --save
    private static void saveRequest() {
        StringBuilder requestString = new StringBuilder();
        //preparing requests details
        requestString.append("url: ").append(url).append("|");
        requestString.append("method: ").append(connection.getRequestMethod()).append("|");
        // TODO: 5/28/2020 remember to save both header and formData shits
        try (FileOutputStream out = new FileOutputStream(requestsFile)) {

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private static void bodyCheck() throws WrongUserInputException,
            IOException {
        boolean isBodyJson = false;
        boolean isBodyForm = false;
        for (String command : commandLine) {
            if (command.equals("-d") || command.equals("--data"))
                isBodyForm = true;
            if (command.equals("-j") || command.equals("--json"))
                isBodyJson = true;
        }
//        System.out.println(isBodyForm + "-" + isBodyJson);
        if (connection.getRequestMethod().equals("POST")) {
            connection.setDoOutput(true);
            BufferedOutputStream dataOutput;
            if (isBodyForm && isBodyJson) {
                System.out.println("You can only use one form of body");
                throw new WrongUserInputException();
            } else if (isBodyForm) {
                generateFormDataBody();
                dataOutput = new BufferedOutputStream(connection.getOutputStream());
                Utils.bufferOutFormData(formDataMap, boundary, dataOutput);
            } else if (isBodyJson) {
                generateJSONBody();
                dataOutput = new BufferedOutputStream(connection.getOutputStream());
                Utils.bufferOutJSON(jsonBody, dataOutput);
            }
        }
    }

    //method for setting message body in form Data structure -d --data
    private static void generateFormDataBody() {
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-d") || commandLine[commandIndex].equals("--data")) {
                try {
                    if (!(commandLine[commandIndex + 1].startsWith("-"))) {
                        String formData = commandLine[commandIndex + 1];
                        formData = formData.replaceAll("^\"|\"$", "");
                        String[] formDataParts = formData.split("=");
                        formDataMap.put(formDataParts[0], formDataParts[1]);
//                        System.out.println(formDataParts[0]+"="+formDataParts[1]);
                    }
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("You didn't enter form-data");
                } catch (Exception e) {
                    e.printStackTrace();
                    System.out.println("error in form data body . Syntax -> -d/--data \"key=value\"");
                }

            }
        }

    }

    //method for setting message body in JSON structure -j --json   (bounce)
    //TODO do the json shit --------------------- do this shit nigga
    private static void generateJSONBody() throws WrongUserInputException {
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-j") || commandLine[commandIndex].equals("--json")) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                try {
                    String nextCommandValue = commandLine[commandIndex + 1];
                    //if next argument not be valid it throws a JSON exception
                    if(!nextCommandValue.startsWith("-")){
                        nextCommandValue=nextCommandValue.replaceAll("^\"|\"$", "");
                        System.out.println(nextCommandValue);
                        JSONObject jsonObj = new JSONObject(nextCommandValue);
                        jsonBody = nextCommandValue;
                    }
                    else
                        throw new IndexOutOfBoundsException();
                } catch (IndexOutOfBoundsException ex) {
                    System.out.println("You didn't enter json body");
                    throw new WrongUserInputException();
                } catch (JSONException e) {
                    System.out.println("You didn't enter valid json body");
                    throw new WrongUserInputException();
                }

            }
        }

    }
    //method to create a menu like thing for selecting requests

    //method to use proxy (phase 4) --proxy & --ip  (emtiazi)

    //exception
    private static class WrongUserInputException extends Exception {

    }
}
