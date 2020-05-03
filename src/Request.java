public class Request {
    private RequestPanel requestPanel;
    private ResponsePanel responsePanel;
    private String requestName;
    private String url;
    private String Option;
    public Request(String requestName){
        requestPanel=new RequestPanel(this);
        responsePanel=new ResponsePanel();
        this.requestName=requestName;
        this.Option="GET";
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
        Option = option;

    }

    public String getOption() {
        return Option;
    }

    @Override
    public String toString() {
        return requestName;
    }
}
