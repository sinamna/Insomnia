import java.util.ArrayList;

public class Request {
    private RequestPanel requestPanel;
    private Response response;
    private String requestName;
    private String url;
    private String option;
    //Info type are key-valued types
    private ArrayList<Info> headers;
    private ArrayList<Info> formData;
    public Request(String requestName, String option) {
        this.requestName = requestName;
        this.option = option;
        headers = new ArrayList<>();
        formData=new ArrayList<>();
        requestPanel = new RequestPanel(this);


    }

    public void setResponse(Response response) {
        this.response = response;
    }

    public Response getResponse() {
        return response;
    }

    public RequestPanel getRequestPanel() {
        return requestPanel;
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

    public void addHeaderInfo(Info headerToAdd) {
        if (!headers.contains(headerToAdd))
            headers.add(headerToAdd);
    }
    public void addDataInfo(Info dataToAdd){
        if(!formData.contains(dataToAdd))
            formData.add(dataToAdd);
    }
    public String getOption() {
        return option;
    }

    public ArrayList<Info> getHeaders() {
        return headers;
    }

    public ArrayList<Info> getFormData() {
        return formData;
    }

    public String getUrl() {
        return url;
    }

    @Override
    public String toString() {
        return requestName;
    }
}
