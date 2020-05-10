import javax.swing.*;
import java.util.ArrayList;

public class Request {
    private RequestPanel requestPanel;
    private ResponsePanel responsePanel;
//    private JSplitPane reqAndResponseSplit;
    private String requestName;
    private String url;
    private String option;
    private ArrayList<HeaderInfo> headers;
//    private ArrayList
    public Request(String requestName,String option){
        requestPanel=new RequestPanel(this);

        this.requestName=requestName;
        this.option=option;
        headers=new ArrayList<>();

    }

//    public JSplitPane getReqAndResponseSplit() {
//        return reqAndResponseSplit;
//    }

    public RequestPanel getRequestPanel() {
        return requestPanel;
    }
    public ResponsePanel getResponsePanel() {
        return responsePanel;
    }
    public String getRequestName() {
        return requestName;
    }
    public void setUrl(String url) {
        this.url = url;
    }
    public void setOption(String option) {
        this.option = option;
    }
    public void addHeaderInfo(HeaderInfo headerToAdd){
        if(!headers.contains(headerToAdd))
              headers.add(headerToAdd);
    }
    public String getOption() {
        return option;
    }

    public ArrayList<HeaderInfo> getHeaders() {
        return headers;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return requestName;
    }
}
