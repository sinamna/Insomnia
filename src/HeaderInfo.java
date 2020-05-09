public class HeaderInfo {
    private String key;
    private String value;
    private boolean enabled;

    public void setKey(String key) {
        this.key = key;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getKey() {
        return key;
    }
    public boolean getState(){
        return enabled;
    }
}
