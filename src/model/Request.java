package model;

import view.Info;
import view.RequestPanel;

import javax.swing.*;
import java.util.ArrayList;

public class Request {
    private RequestPanel requestPanel;
    private Response response;
    private String requestName;
    private String url;
    private String method;
    //view.Info type are key-valued types
    private ArrayList<Info> headers;
    private ArrayList<Info> formData;

    /**
     * constructs a request with given name and option  and the list which this requested is located in
     * @param requestName the name of the request
     * @param method the request option
     * @param listModel the list of requests being displayed in another panel
     */
    public Request(String requestName, String method, JList<Request>listModel) {
        //setting fields
        this.requestName = requestName;
        this.method = method;
        headers = new ArrayList<>();
        formData=new ArrayList<>();
        requestPanel = new RequestPanel(this,listModel);
    }

    /**
     * sets the response to this request
     * @param response the response to this request
     */
    public void setResponse(Response response) {
        this.response = response;
    }

    /**
     *
     * @return the response of this request
     */
    public Response getResponse() {
        return response;
    }

    /**
     *
     * @return the request panel
     */
    public RequestPanel getRequestPanel() {
        return requestPanel;
    }

    /**
     *
     * @return the request name
     */
    public String getRequestName() {
        return requestName;
    }

    /**
     * sets this request's url
     * @param url url to be set
     */
    public void setUrl(String url) {
        this.url = url;
    }

    /**
     * sets this request's method
     * @param method method to be set
     */
    public void setMethod(String method) {
        this.method = method;
    }

    /**
     * adds new view.Info to header list
     * @param headerToAdd the info to be added to header list
     */
    public void addHeaderInfo(Info headerToAdd) {
        if (!headers.contains(headerToAdd))
            headers.add(headerToAdd);
    }

    /**
     * add new view.Info to formData list
     * @param dataToAdd new data to be added to formData list
     */
    public void addDataInfo(Info dataToAdd){
        if(!formData.contains(dataToAdd))
            formData.add(dataToAdd);
    }

    /**
     *
     * @return the method of request
     */
    public String getMethod() {
        return method;
    }

    /**
     *
     * @return the list of headers
     */
    public ArrayList<Info> getHeaders() {
        return headers;
    }

    /**
     *
     * @return the list of formData
     */
    public ArrayList<Info> getFormData() {
        return formData;
    }

    /**
     *
     * @return the url of the request
     */
    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return requestName;
    }
}
