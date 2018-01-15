package system;

public enum StatusCodes {
    OK,                             // successful
    TARGET_AGENT_DOES_NOT_EXIST,    // caused by user
    MESSAGE_LENGTH_EXCEEDED,        // caused by user
    BOTH_AGENT_QUOTAS_EXCEEDED,     // caused by exceeded quota
    SOURCE_AGENT_QUOTA_EXCEEDED,    // caused by exceeded quota
    TARGET_AGENT_QUOTA_EXCEEDED,    // caused by exceeded quota
    SOURCE_AGENT_DOES_NOT_EXIST,    // caused by system error
    SOURCE_AGENT_NOT_LOGGED_IN,     // caused by system error
    SESSION_KEY_UNRECOGNIZED,       // caused by system error
    FAILED_TO_ADD_TO_MAILBOX,       // caused by system error
    GENERIC_ERROR                   // represents an error
}
