import controller.Utils;
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
    private static HashMap<String, String> addedHeaders;
    private static String jsonBody;
    private static byte[] responseBody;

    public static void main(String[] args) {
        requestsFile = new File(SAVING_DIRECTORY + "requestList.txt");
        Scanner input = new Scanner(System.in);
        String[] commandLine = input.nextLine()
                .replaceAll("\\s{2,}", " ")//this code replaces 2 or more spaces with one space
                .trim()
                .split(" ");


        //checking command line first entry
        if (commandLine[0].equals("list")) {
            printList();
        } else if (commandLine[0].equals("fire")) { //handling fire command
            fireRequest(commandLine);
        } else {
            createHTTPConnection(commandLine);
        }
    }

    private static void printList() {
        if (requestsFile.exists()) {
            String list = Utils.createRequestList(requestsFile);
            System.out.println(list);
        } else {
            System.out.println("no file containing request's detected");
        }
    }

    private static void fireRequest(String[] commandLine) {
        if (requestsFile.exists()) {
            String[] requests = Utils.createRequestArray(requestsFile);
            if (commandLine.length > 1) {
                for (int index = 1; index < commandLine.length; index++) {
                    try {
                        String requestDetail = requests[Integer.parseInt(commandLine[index]) - 1];//chooses the request from request array
                        String requestInCommandForm = Utils.createCommandLine(requestDetail.replaceAll("#$", ""));
                        createHTTPConnection(requestInCommandForm.replaceAll("\\s{2,}", " ")
                                .trim()
                                .split(" "));
                    } catch (IndexOutOfBoundsException e) {
//                        e.printStackTrace();
                        System.out.println("there is no request with that index");
                    } catch (NumberFormatException e) {
                        System.out.println("You didn't enter number");
                    }
                }
            } else
                System.out.println("You didn't enter the number(s) of requests you want to fire");

        } else {
            System.out.println("there is no list to select request from");
        }
    }



    private static void setDefaults() {
        showResponseHeader = false;
        isRedirectAllowed = false;
        saveRequestPermission = false;
        saveResponsePermission = false;
        responseName = null;
        jsonBody = null;
        boundary = "-----------" + System.currentTimeMillis();
        formDataMap = new HashMap<>();
        addedHeaders = new HashMap<>();
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
            if (saveRequestPermission)
                saveRequest();

            //TODO handle different connection methods
            if (connection.getRequestMethod().equals("HEAD")) {
                showResponseHeader = true;
            }
            //firing request
            getResponse();

        } catch (MalformedURLException e) {
//            e.printStackTrace();
            System.out.println("entered url is malformed");

        } catch (IOException | NullPointerException e) {
            e.printStackTrace();
            System.out.println("Connection couldn't be established");
        } catch (WrongUserInputException e) {
            connection.disconnect();
        }

    }

    private static void getResponse() throws WrongUserInputException {
        try {
            System.out.println(connection.getResponseCode() + " - " + connection.getResponseMessage());
            //todo :idk yet but this is wrong
            int status = connection.getResponseCode();
            if (status / 100 == 2)
                inputStream = connection.getInputStream();
            //TODO sometimes the server returns a page as an error ...so u should fix that condition which it only make
            // input stream when connection is ok
            //printing response headers
            if (showResponseHeader)
                printResponseHeaders();

            if (!(connection.getRequestMethod().equals("HEAD")) &&
                    connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                receiveResponseBody();
                System.out.println(new String(responseBody)); // converts the byte of response body to string and prints it

            }

            //saving response based on boolean value
            if (saveResponsePermission && status == HttpURLConnection.HTTP_OK)
                Utils.saveResponseToFile(SAVING_DIRECTORY, responseName, getResponseBody(),
                        connection.getHeaderField("Content-Type"));

            if (isRedirectAllowed && status / 100 == 3) {
                String newUrl = connection.getHeaderField("Location");
                System.out.println("Redirecting to : " + newUrl + "... \n");
                String[] newCommandLine = convertToString(commandLine).replaceAll(url.toString(), newUrl).split(" ");
                System.out.println(convertToString(newCommandLine));
                createHTTPConnection(newCommandLine);
                throw new WrongUserInputException();
            }
        } catch (UnknownHostException e) {
            System.out.println("Unknown Host ");
            throw new WrongUserInputException();
        } catch (IOException e) {
            e.printStackTrace();
            throw new WrongUserInputException();
        } finally {
//            inputStream.close();
        }
    }

    private static String convertToString(String[] stringArray) {
        StringBuilder newString = new StringBuilder();
        for (String string : stringArray) {
            newString.append(string).append(" ");
        }
        return newString.toString().trim();
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

    /**
     * set headers which user specified with -H or --header argument
     */
    private static void setHeaders() {
        for (int i = 0; i < commandLine.length; i++) {
            if (commandLine[i].equals("-H") || commandLine[i].equals("--header")) {
                try {
                    String header = commandLine[i + 1];
                    if (header.startsWith("\"")) {
                        header = header.replaceAll("^\"|\"$", "");
                        String[] headerParts = header.split(":");
                        connection.setRequestProperty(headerParts[0], headerParts[1]);
                        addedHeaders.put(headerParts[0], headerParts[1]);
                    } else throw new NullPointerException();
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

    /**
     * sets the redirect permission true if user entered -f argument
     */
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

    /**
     * creates a customized string representing the request and save it to the request list file
     */
    private static void saveRequest() {
        /*
        requests are divided by '$' symbol (used in split method) and each part of string can be divided by '#' character
         */
        StringBuilder requestString = new StringBuilder();
        //preparing requests details
        requestString.append("&url:").append(url).append("#");//adding url to string
        requestString.append("method:").append(connection.getRequestMethod()).append("#");//adding request method to string
        requestString.append("header:");
        if (addedHeaders.size() > 0)
            for (String key : addedHeaders.keySet()) {
                requestString.append(key).append("=").append(addedHeaders.get(key)).append(";");
            }
        requestString.append("#");
        requestString.append("body:");
        if (formDataMap.size() > 0)
            for (String key : formDataMap.keySet()) {
                requestString.append("form-data=").append(key).append("=").append(formDataMap.get(key))
                        .append(";");
            }
        if (jsonBody != null)
            requestString.append("json=").append(jsonBody);
        requestString.append("#");
        requestString.append("redirect:").append(isRedirectAllowed).append("#");
        requestString.append("showResponseHeader:").append(showResponseHeader).append("#");
        requestString.append("\n");
        Utils.saveRequestToFile(requestString.toString().getBytes(), requestsFile);
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
        } else if (isBodyForm || isBodyJson) {
            System.out.println("You are specifying a request bod with a non POST method");
            throw new WrongUserInputException();
        }
    }

    //method for setting message body in form Data structure -d --data
    //TODO  : u should handle urlencoded tor styles
    private static void generateFormDataBody() throws WrongUserInputException {
        connection.setRequestProperty("Content-Type", "multipart/form-data; boundary=" + boundary);
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-d") || commandLine[commandIndex].equals("--data")) {
                try {
                    String nextCommand = commandLine[commandIndex + 1];

                    if (!(nextCommand.startsWith("-")) && nextCommand.startsWith("\"")) {
                        String formData = commandLine[commandIndex + 1];
                        formData = formData.replaceAll("^\"|\"$", "");
                        String[] formDataParts = formData.split("=");
                        formDataMap.put(formDataParts[0], formDataParts[1]);
                    } else throw new Exception();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("You didn't enter form-data");
                    throw new WrongUserInputException();
                } catch (Exception e) {
//                    e.printStackTrace();
                    System.out.println("error in form data body . Syntax -> -d/--data \"key=value\"");
                    throw new WrongUserInputException();
                }

            }
        }

    }

    //method for setting message body in JSON structure -j --json   (bounce)

    /**
     * looks for json body and validate it and if it was valid assign it to field variable
     *
     * @throws WrongUserInputException exception indicating user didn't entered or entered wrong format . ends program
     */
    private static void generateJSONBody() throws WrongUserInputException {
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-j") || commandLine[commandIndex].equals("--json")) {
                connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                connection.setRequestProperty("Accept", "application/json");
                try {
                    String nextCommandValue = commandLine[commandIndex + 1];
                    //if next argument not be valid it throws a JSON exception
                    if (!nextCommandValue.startsWith("-")) {
                        nextCommandValue = nextCommandValue.replaceAll("^\"|\"$", "");
                        JSONObject jsonObj = new JSONObject(nextCommandValue);
                        jsonBody = nextCommandValue;
                    } else
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
