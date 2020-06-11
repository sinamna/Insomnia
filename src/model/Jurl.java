package model;

import model.ModelUtils;
import org.json.JSONException;
import org.json.JSONObject;
import view.ErrorException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLHandshakeException;
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
    private static StringBuilder response;
    private static boolean isGUIAsking;

    public static void main(String[] args) {
        requestsFile = new File(SAVING_DIRECTORY + "requestList.txt");
        Scanner input = new Scanner(System.in);
        String[] commandLine = input.nextLine()
                .replaceAll("\\s{2,}", " ")//this code replaces 2 or more spaces with one space
                .trim()
                .split(" ");


        //checking command line first entry
        if (commandLine[0].equals("-H") || commandLine[0].equals("--help")) {
            printHelp();
        } else if (commandLine[0].equals("list")) {
            printList();
        } else if (commandLine[0].equals("fire")) { //handling fire command
            fireRequest(commandLine);
        } else {
            createHTTPConnection(commandLine, false);
        }
    }

    /**
     * prints the list of requests saved in the file
     */
    private static void printList() {
        if (requestsFile.exists()) {
            String list = ModelUtils.createRequestList(requestsFile);
            System.out.println(list);
        } else {
            System.out.println("no file containing request's detected");
        }
    }

    /**
     * fires the specified requests
     *
     * @param commandLine the line to get the request numbers from
     */
    private static void fireRequest(String[] commandLine) {
        if (requestsFile.exists()) {
            String[] requests = ModelUtils.createRequestArray(requestsFile);
            if (commandLine.length > 1) {
                for (int index = 1; index < commandLine.length; index++) {
                    try {
                        String requestDetail = requests[Integer.parseInt(commandLine[index]) - 1];//chooses the request from request array
                        String requestInCommandForm = ModelUtils.createCommandLine(requestDetail.replaceAll("#$", ""));
                        createHTTPConnection(requestInCommandForm.replaceAll("\\s{2,}", " ")
                                .trim()
                                .split(" "), false);
                    } catch (IndexOutOfBoundsException e) {
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

    /**
     * sets the default variables when http connection want to be established
     */
    private static void setDefaults() {
        response = new StringBuilder();
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

    /**
     * opens an http connection with given parameters
     *
     * @param args   the parameters given by user
     * @param guiArg whether this method initiated by terminal or graphical interface
     * @return returns an string representing response details
     */
    public static String createHTTPConnection(String[] args, boolean guiArg) {
        isGUIAsking = guiArg;
        System.out.println("connecting ...");
        setDefaults();
        commandLine = args;

        try {
            setUrl();
            if (url.getProtocol().equals("http"))
                connection = (HttpURLConnection) url.openConnection();
            else if (url.getProtocol().equals("https"))
                connection = (HttpsURLConnection) url.openConnection();
            connection.setInstanceFollowRedirects(false);
            //set variables based on user Inputs
            setMethod();
            setHeaders();
            setShowResponseHeader();
            setSaveRequestPermission();
            setSaveResponsePermission();
            setRedirectPermission();
            bodyCheck();
            if (saveRequestPermission)
                saveRequest();

            if (connection.getRequestMethod().equals("HEAD")) {
                showResponseHeader = true;
            }

            //firing request
            getResponse();

        } catch (MalformedURLException e) {
//            e.printStackTrace()
            String error = "entered url is malformed\nplease enter url starting with http or https\n";
            if (isGUIAsking)
                ErrorException.showError(error);
            else
                System.out.println(error + "url/fire/list parameters ");
        } catch (IOException | NullPointerException e) {
//            e.printStackTrace();
            System.out.println("Connection couldn't be established");
        } catch (WrongUserInputException e) {
            connection.disconnect();
        }

        return response.toString();
    }

    /**
     * generating and printing response
     *
     * @throws WrongUserInputException is thrown when program wants to close connection(in order to create a new one or
     *                                 because of an exception)
     */
    private static void getResponse() throws WrongUserInputException {
        try {

            /*
            isGUIAsking is used to not print details in console when using gui
             */
            //getting the response
            String statusStr = connection.getResponseCode() + " - " + connection.getResponseMessage();
            System.out.println(statusStr);

            //adding to response string (phase 3)
            response.append(statusStr).append("\n");

            int status = connection.getResponseCode();
            //getting input stream if connection is ok
            if (status / 100 == 2)
                inputStream = connection.getInputStream();

            //printing response headers
            String headers = getResponseHeaders();
            response.append(headers);
            if (showResponseHeader) {
                if (!isGUIAsking)
                    System.out.println(headers);
            }
            response.append("--->body:");
            if (!(connection.getRequestMethod().equals("HEAD")) &&
                    connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                receiveResponseBody();
                response.append(new String(responseBody, "UTF-8"));
                if (!isGUIAsking)
                    System.out.println("-->body:\n" + new String(responseBody)); // converts the byte of response body to string and prints it

            }

            //saving response based on boolean value -> when user used -O argument
            if (saveResponsePermission && status == HttpURLConnection.HTTP_OK)
                ModelUtils.saveResponseToFile(SAVING_DIRECTORY, responseName, getResponseBody(),
                        connection.getHeaderField("Content-Type"));

            if (isRedirectAllowed && status / 100 == 3) {
                String newUrl = connection.getHeaderField("Location");
                System.out.println("Redirecting to : " + newUrl + "... \n");
                String[] newCommandLine = convertToString(commandLine).replaceAll(url.toString(), newUrl).split(" ");
                System.out.println(convertToString(newCommandLine));
                createHTTPConnection(newCommandLine, isGUIAsking);
                throw new WrongUserInputException();
            }
        } catch (UnknownHostException e) {
            if (isGUIAsking)
                ErrorException.showError("Unknown Host ");
            else
                System.out.println("Unknown Host ");
            throw new WrongUserInputException();
        } catch (IOException e) {
//            e.printStackTrace();
            String error = "";
            if (e instanceof SocketException) {
                if (e.getMessage().contains("timed out"))
                    error += "connection timed out";
                else
                    error += "check out url protocol";

            } else if (e instanceof SSLHandshakeException)
                error += "No subject alternative DNS name found";
            error += "\nIO error occurred";
            if (isGUIAsking)
                ErrorException.showError(error);
            else
                System.out.println(error);
            throw new WrongUserInputException();
        } finally {
//            inputStream.close();
        }
    }

    /**
     * takes array of string and returns an string including all of array's elements
     *
     * @param stringArray the array of string
     * @return the single string summing elements
     */
    private static String convertToString(String[] stringArray) {
        StringBuilder newString = new StringBuilder();
        for (String string : stringArray) {
            newString.append(string).append(" ");
        }
        return newString.toString().trim();
    }

    /**
     * recieves respond body using connection input stream
     *
     * @throws IOException the IOException
     */
    private static void receiveResponseBody() throws IOException {
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        int byteRead = -1;
        byte[] data = new byte[4096];
        while ((byteRead = inputStream.read(data, 0, data.length)) != -1) {
            buffer.write(data, 0, byteRead);
        }
        responseBody = buffer.toByteArray();
    }

    /**
     * @return returns the respond body
     */
    public static byte[] getResponseBody() {
        return responseBody;
    }

    /**
     * getting respond's header
     *
     * @return string representing headers
     */
    private static String getResponseHeaders() {
        StringBuilder responseHeaders = new StringBuilder();
        Map<String, List<String>> map = connection.getHeaderFields();
        for (String key : map.keySet()) {
            responseHeaders.append("---> ").append(key).append(": ");
            List<String> values = map.get(key);
            for (String value : values) {
                responseHeaders.append(value).append("\n");
            }
        }
        return responseHeaders.toString();
    }

    /**
     * sets the url using commandline array
     *
     * @throws MalformedURLException
     */
    private static void setUrl() throws MalformedURLException {
        try {
            url = new URL(commandLine[0]);
        } catch (ArrayIndexOutOfBoundsException e) {
            System.out.println("note that url should be placed at the first place . syntax -> model.Jurl (url) ...");
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

    /**
     * sets request's method which user specified with -M or --method
     *
     * @throws WrongUserInputException throws this exception when entered method isn't allowed
     */
    private static void setMethod() throws WrongUserInputException {
        String[] validMethods = {"GET", "POST", "DELETE", "PUT", "HEAD"};
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
//                    e.printStackTrace();
                } catch (IndexOutOfBoundsException e) {
                    System.out.println("You didn't enter the request's method");
                }
            }
        }
    }

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

    /**
     * search for -S or --save and sets related boolean value true if find it
     */
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
        ModelUtils.saveRequestToFile(requestString.toString().getBytes(), requestsFile);
    }

    /**
     * search commandline for json or form-data body
     *
     * @throws WrongUserInputException
     * @throws IOException
     */
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
        // checks if user creating body only for post and put methods
        if (connection.getRequestMethod().equals("POST") || connection.getRequestMethod().equals("PUT")) {
            connection.setDoOutput(true);
            BufferedOutputStream dataOutput;
            if (isBodyForm && isBodyJson) {
                System.out.println("You can only use one form of body");
                throw new WrongUserInputException();
            } else if (isBodyForm) {
                generateFormDataBody();
                dataOutput = new BufferedOutputStream(connection.getOutputStream());
                ModelUtils.bufferOutFormData(formDataMap, boundary, dataOutput);
            } else if (isBodyJson) {
                generateJSONBody();
                dataOutput = new BufferedOutputStream(connection.getOutputStream());
                ModelUtils.bufferOutJSON(jsonBody, dataOutput);
            }
        } else if (isBodyForm || isBodyJson) {
            String error = "You are specifying a request body with a non POST method";
            if (isGUIAsking)
                ErrorException.showError(error);
            else
                System.out.println("You are specifying a request body with a non POST method");
            throw new WrongUserInputException();
        }
    }

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
                        String[] forms = formData.split("&"); //handles urlencoded
                        for (String form : forms) {
                            String[] formDataParts = form.split("=");
                            formDataMap.put(formDataParts[0], formDataParts[1]);
                        }
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

    /**
     * looks for json body and validate it and if it was valid assign it to field variable
     *
     * @throws WrongUserInputException exception indicating user didn't entered or entered wrong format . ends program
     */
    private static void generateJSONBody() throws WrongUserInputException {
        for (int commandIndex = 0; commandIndex < commandLine.length; commandIndex++) {
            if (commandLine[commandIndex].equals("-j") || commandLine[commandIndex].equals("--json")) {
                connection.setRequestProperty("Content-Type", "application/json");
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

    //method to use proxy (phase 4) --proxy & --ip  (emtiazi)

    /**
     * this exception is used as a marker to end connection
     */
    private static class WrongUserInputException extends Exception {
    }

    /**
     * downloads the pic and return its byte to be used in preview panel
     *
     * @param url
     * @return
     */
    public static byte[] downloadPic(String url) {
        try {
            URL urlToDownload = new URL(url);
            HttpURLConnection newConnection = (HttpURLConnection) urlToDownload.openConnection();
            BufferedInputStream reader = new BufferedInputStream(newConnection.getInputStream());
            ByteArrayOutputStream buffer = new ByteArrayOutputStream();
            byte[] data = new byte[2048];
            int nRead = -1;
            while ((nRead = reader.read(data, 0, data.length)) != -1) {
                buffer.write(data, 0, nRead);
            }
            buffer.flush();
            return buffer.toByteArray();
        } catch (IOException e) {
//            e.printStackTrace();
            ErrorException.showError("Can't download pic");
        }
        return null;
    }

    /**
     * prints help
     */
    private static void printHelp() {
        String[] commandHelp = {
                "-M, --method <method> sets requests method (by default GET) ",
                "-H, --header <Header> adds header to the request",
                "-i shows response header",
                "-H, --help this text help",
                "-f enables program to follow redirect response ",
                "-O, --output <fileName> saves the response body in specified filePath using given name",
                "-S, --save saves request details and can be reused later using fire command",
                "-d, --data <form-data> specifying form-data body",
                "-J, --json <json body> specifying json body",
                "list  prints the list of saved requests",
                "fire request's index ... fires requests in given order"
        };
        for (String helpPart : commandHelp) {
            System.out.println(helpPart);
        }
    }
}
