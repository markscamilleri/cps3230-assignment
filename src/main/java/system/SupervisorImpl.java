package system;

import util.Utils;

import static system.MessagingSystem.LOGIN_KEY_LENGTH;

public class SupervisorImpl implements Supervisor {

    private final MessagingSystem messagingSystem;

    public SupervisorImpl(MessagingSystem messagingSystem) {
        this.messagingSystem = messagingSystem;
    }

    public String getLoginKey(String agentId) {

        if (agentId.startsWith("spy-")) {
            return null;
        } else {
            final String loginKey = Utils.getNRandomCharacters(LOGIN_KEY_LENGTH);
            messagingSystem.registerLoginKey(agentId, loginKey);
            return loginKey;
        }
    }
}
