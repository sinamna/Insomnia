import java.util.ArrayList;

public class Request {
    private RequestPanel requestPanel;
    private ResponsePanel responsePanel;
    private String requestName;
    private String url;
    private String option;
    private ArrayList<HeaderInfo> headers;
    public Request(String requestName,String option){
        requestPanel=new RequestPanel(this);
        responsePanel=new ResponsePanel();
        this.requestName=requestName;
        this.option=option;
        headers=new ArrayList<>();

    }
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
        headers.add(headerToAdd );
    }
    public String getOption() {
        return option;
    }

    @Override
    public String toString() {
        return requestName;
    }
}
