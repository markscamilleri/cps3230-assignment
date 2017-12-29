package system;

import util.Utils;

import static system.MessagingSystem.LOGIN_KEY_LENGTH;

public class SupervisorImpl implements Supervisor {

    public String getLoginKey(String agentId) {
        return Utils.getNCharacters(LOGIN_KEY_LENGTH);
    }
}
