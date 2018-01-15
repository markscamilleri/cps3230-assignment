package system;

public enum StatusCodes {
    OK,                             // successful
    AGENT_DOES_NOT_EXIST,           // caused by user
    AGENT_NOT_LOGGED_IN,            // caused by system error
    SESSION_KEY_INVALID_LENGTH,     // caused by system error
    SESSION_KEY_UNRECOGNIZED,       // caused by system error
    MESSAGE_LENGTH_EXCEEDED,        // caused by user
    SOURCE_AGENT_QUOTA_EXCEEDED,    // caused by user
    TARGET_AGENT_QUOTA_EXCEEDED,    // caused by user
    BOTH_AGENT_QUOTAS_EXCEEDED,     // caused by user
    FAILED_TO_ADD_TO_MAILBOX,       // caused by system error
    GENERIC_ERROR                   // represents an error
}
