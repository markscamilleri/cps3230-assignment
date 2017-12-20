package system;

import org.junit.*;
import util.Utils;

import java.lang.reflect.Field;

public class MessagingSystemTest {

    private MessagingSystem msgSystem;

    @Before
    public void setUp() throws NoSuchFieldException, IllegalAccessException {
        Field instance = MessagingSystem.class.getDeclaredField("INSTANCE");
        instance.setAccessible(true);
        instance.set(null, null);
        msgSystem = MessagingSystem.getInstance();
    }

    @After
    public void tearDown() {
        msgSystem = null;
    }

    @Test
    public void registerFailsIfLoginKeyIncorrectLength() {
        final String loginKey = Utils.getNRandomCharacters(MessagingSystem.LOGIN_KEY_LENGTH - 1);
        final String agentId = "1234xy";

        Assert.assertFalse(msgSystem.registerLoginKey(agentId, loginKey));
    }

    @Test
    public void registerFailsIfLoginKeyNotUnique() {
        final String loginKey = Utils.getNRandomCharacters(MessagingSystem.LOGIN_KEY_LENGTH);
        final String agentId1 = "1234xy";
        final String agentId2 = "5678ab";

        Assume.assumeTrue(msgSystem.registerLoginKey(agentId1, loginKey));
        Assert.assertFalse(msgSystem.registerLoginKey(agentId2, loginKey));
    }

    @Test
    public void registerSucceedsIfLoginKeyValid() {
        final String loginKey = Utils.getNRandomCharacters(MessagingSystem.LOGIN_KEY_LENGTH);
        final String agentId = "1234xy";

        Assert.assertTrue(msgSystem.registerLoginKey(agentId, loginKey));
    }
}