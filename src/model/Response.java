package model;

import view.*;
import view.ResponsePanel;

import javax.swing.*;
import java.io.Serializable;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;

public class Response {
    private String statusCode;
    private String statusMessage;
    private String elapsedTime;
    private String responseText;
    private ArrayList<Info> headers;
    private ResponsePanel responsePanel;
    private String response;
    private boolean isImage;
    private boolean isJSON;
    private String requestURL;

    /**
     * constructs a response with list of headers from request
     * //     * @param headers the list of headers
     */
    public Response(String response,String requestURL) throws ErrorException {
        //responsePanel use Strings field
        //it should be initialized after fields
        this.requestURL=requestURL;
        isImage = false;
        isJSON = false;
        this.response = response;
        this.headers = new ArrayList<>();
        responseText = "";
        generateDetails();
        Response responseObj = this;
        this.responsePanel = new ResponsePanel(responseObj);


    }

    /**
     * extracts response details from customized and formatted response string
     * @throws ErrorException
     */
    private void generateDetails() throws ErrorException {
        if (response.isEmpty())
            throw new ErrorException("Program ran into a problem while establishing connection");
        String[] responseParts = response.trim().split("--->");
        // first part contains status code and massage
        String[] status = responseParts[0].split("-");
        statusCode = status[0];
        statusMessage = status[1];


        for (int i = 1; i < responseParts.length; i++) {
            String resPart = responseParts[i].trim();
            if (resPart.startsWith("body")) {
                String[] responseBody = responseParts[i].trim().split(":", 2);
                responseText = responseBody[1];
                break;
            }
            String[] headerParts = responseParts[i].trim().split(":", 2);
            headers.add(new Info(headerParts[0].trim(), headerParts[1].trim()));
            if (headerParts[0].contains("Content"))
                if (headerParts[1].contains("image"))
                    isImage = true;
            if (headerParts[1].contains("json"))
                isJSON = true;
        }

    }

    /**
     * @return the response panel
     */
    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }

    /**
     *
     * @return  the url of request
     */
    public String getRequestURL() {
        return requestURL;
    }

    /**
     *
     * @return  whether body is image or not
     */
    public boolean isImage() {
        return isImage;
    }

    /**
     *
     * @return  whether body is JSON or not
     */
    public boolean isJSON() {
        return isJSON;
    }

    /**
     *
     * @return  the status code of response
     */
    public String getStatusCode() {
        return statusCode;
    }

    /**
     *
     * @return  the status message of the request
     */
    public String getStatusMessage() {
        return statusMessage;
    }


    /**
     * @return the response text
     */
    public String getResponseText() {
        return responseText;
    }

    /**
     * @return the header array
     */
    public ArrayList<Info> getHeaders() {
        return headers;
    }
}
