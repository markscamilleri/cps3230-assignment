package system;

public enum MessagingSystemStatusCodes {
    OK("OK"),
    ERROR("ERROR");

    private String value;

    MessagingSystemStatusCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


