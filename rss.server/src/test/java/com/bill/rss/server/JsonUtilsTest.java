package com.bill.rss.server;

import org.junit.Test;

import com.bill.rss.domain.User;

import static org.junit.Assert.assertEquals;

public class JsonUtilsTest {

    @Test
    public void testConvertObjectToJson() {
        User user = MockUtils.createUserMock();
        String userAsJson = JsonUtils.convertObjectToJson(user);
        assertEquals("{\"userName\":\"billblake\",\"firstName\":\"Bill\",\"lastName\":\"Blake\",\"password\":\"sdgh4e34wtgdfh\"}", userAsJson);
    }
}
