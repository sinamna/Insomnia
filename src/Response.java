import java.util.ArrayList;

public class Response {
    private String statusCode;
    private String statusMessage;
    private String elapsedTime;
    private String responseSize;
    private String responseText;
    private ArrayList<Info> headers;
    private ResponsePanel responsePanel;

    /**
     * constructs a response with list of headers from request
     * @param headers the list of headers
     */
    public Response(ArrayList<Info> headers){
        //responsePanel use Strings field
        //it should be initialized after fields
        statusCode="hi";
        statusMessage="hi";
        elapsedTime="Hi";
        responseSize="hi";
        responseText="";
        this.headers=headers;
        this.responsePanel=new ResponsePanel(this);


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
