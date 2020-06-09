package model;
import view.*;
import view.ResponsePanel;

import java.util.ArrayList;

public class Response {
    private String statusCode;
    private String statusMessage;
    private String elapsedTime;
    private String responseSize;
    private String responseText;
    private ArrayList<Info> headers;
    private ResponsePanel responsePanel;
    private String response;

    /**
     * constructs a response with list of headers from request
//     * @param headers the list of headers
     */
    public Response(String response) throws ErrorException {
        //responsePanel use Strings field
        //it should be initialized after fields
        this.response=response;
        this.headers=new ArrayList<>();
        responseText="";
        responseSize="";
        generateDetails();
        Response responseObj=this;
        this.responsePanel=new ResponsePanel(responseObj);


    }
    private void generateDetails() throws ErrorException {
//        System.out.println(response.isEmpty());
        //handles situation connection couldn't be established
        if(response.isEmpty())
            throw new ErrorException("Program ran into a problem while establishing connection");
        String[] responseParts=response.trim().split("->");
        // first part contains status code and massage
        String[] status=responseParts[0].split("-");
        statusCode=status[0];
        statusMessage=status[1];
//        if(checkStatus()){
            //second parts are headers
            for(int i=1;i<responseParts.length-1;i++) {
                String[] headerParts=responseParts[i].trim().split(":",2);
                headers.add(new Info(headerParts[0].trim(),headerParts[1].trim()));

            }
            //the last part is response body
            String[] responseBody=responseParts[responseParts.length-1].trim().split(":",2 );
            responseText=responseBody[1];
//        }else
//            ErrorException.showError("Error occurred in generating response details .code 4XX");


    }
    private boolean checkStatus(){
        int statusCode=Integer.parseInt(this.statusCode.trim());
        if((statusCode/100)==4 || (statusCode/100)==5)
            return false;
        return true;
    }
    /**
     *
     * @return the response panel
     */
    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }

    public void setElapsedTime(String elapsedTime) {
        this.elapsedTime = elapsedTime;
    }

    public void setResponseSize(String responseSize) {
        this.responseSize = responseSize;
    }

    public void setResponseText(String responseText) {
        this.responseText = responseText;
    }

    public void setHeaders(ArrayList<Info> headers) {
        this.headers = headers;
    }

    public String getStatusCode() {
        return statusCode;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public String getElapsedTime() {
        return elapsedTime;
    }

    public String getResponseSize() {
        return responseSize;
    }

    /**
     *
     * @return the response text
     */
    public String getResponseText() {
        return responseText;
    }

    /**
     *
     * @return the header array
     */
    public ArrayList<Info> getHeaders() {
        return headers;
    }
}
