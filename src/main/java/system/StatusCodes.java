package system;

public enum StatusCodes {
    OK("OK"),
    AGENT_DOES_NOT_EXIST("AGENT DOES NOT EXIST"),
    AGENT_NOT_LOGGED_IN("AGENT NOT LOGGED IN"),
    SESSION_KEY_INVALID_LENGTH("SESSION KEY INVALID LENGTH"),
    SESSION_KEY_UNRECOGNIZED("SESSION KEY UNRECOGNIZED"),
    MESSAGE_LENGTH_EXCEEDED("MESSAGE LENGTH EXCEEDED"),
    FAILED_TO_ADD_TO_MAILBOX("FAILED TO ADD TO MAILBOX"),
    GENERIC_ERROR("GENERIC ERROR"); // intended for use in testing

    private final String value;

    StatusCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


