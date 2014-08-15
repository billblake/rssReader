package com.bill.rss.server;

import org.junit.Test;

import com.bill.rss.domain.User;

import static org.junit.Assert.assertEquals;

public class SecurityUtilsTest {


    @Test
    public void testEncrypt() {
        String stringToEncrypt = "myPassword";
        String encryptedString = SecurityUtils.encrypt(stringToEncrypt );
        assertEquals("deb1536f480475f7d593219aa1afd74c", encryptedString);
    }


    @Test
    public void testClearPassword() {
        User user = MockUtils.createUserMock();
        user = SecurityUtils.clearPassword(user);
        assertEquals("", user.getPassword());
    }
}
