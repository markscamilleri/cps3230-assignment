package system;

public enum StatusCodes {
    OK,
    AGENT_DOES_NOT_EXIST,
    AGENT_NOT_LOGGED_IN,
    SESSION_KEY_INVALID_LENGTH,
    SESSION_KEY_UNRECOGNIZED,
    MESSAGE_LENGTH_EXCEEDED,
    FAILED_TO_ADD_TO_MAILBOX,
    GENERIC_ERROR // intended for use in testing
}
