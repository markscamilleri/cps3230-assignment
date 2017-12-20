package system;

public enum MessagingSystemStatusCodes {
    OK("OK"),
    AGENT_NOT_LOGGED_IN("AGENT NOT LOGGED IN"),
    SESSION_KEY_UNRECOGNIZED("SESSION KEY UNRECOGNIZED"),
    MESSAGE_LENGTH_EXCEEDED("MESSAGE LENGTH EXCEEDED"),
    MESSAGE_CONTAINS_BLOCKED_WORD("MESSAGE CONTAINS BLOCKED WORD"),
    AGENT_DOES_NOT_EXIST("AGENT DOES NOT EXIST"),
    INVALID_MESSAGE("INVALID MESSAGE"),
    GENERIC_ERROR("GENERIC ERROR"); // todo: remove?

    private final String value;

    MessagingSystemStatusCodes(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }
}


