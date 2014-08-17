package com.bill.rss.mongodb;

import org.junit.Test;

import com.bill.rss.domain.User;
import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.WriteConcern;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;

import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class UserRetrieverTest {

    @Test
    public void checkIfUserNameExists() {
        DB db = MockUtils.createDbMock();
        MockUtils.createUsersCollectionMock(db);
        UserRetriever userRetriever = new UserRetriever();
        assertTrue(userRetriever.checkIfUserNameExists("billblake"));
    }


    @Test
    public void createNewUser() {
        DB db = MockUtils.createDbMock();
        DBCollection usersCollection = MockUtils.createUsersCollectionMock(db);

        UserRetriever userRetriever = new UserRetriever();
        User user = new User();
        userRetriever.createNewUser(user);
        verify(usersCollection, times(1)).insert(any(BasicDBObject.class), any(WriteConcern.class));
    }
}
