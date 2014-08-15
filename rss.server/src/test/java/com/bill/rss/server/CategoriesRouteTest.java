package com.bill.rss.server;

import org.json.JSONException;
import org.junit.Test;

import spark.Request;
import spark.Response;
import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;
import static org.junit.Assert.assertEquals;

import static org.mockito.Mockito.mock;


public class CategoriesRouteTest {

    Request request = mock(Request.class);
    Response response = mock(Response.class);

    @Test
    public void testHandle() throws JSONException {
        Request request = createRequestMock("logged-in");
        Response response = createResponseMock();

        CategoriesRoute categoriesRoute = new CategoriesRoute("/categories");
        categoriesRoute.setCategoryProvider(MockUtils.createCategoryProviderMock());
        String jsonResponse = (String) categoriesRoute.handle(request, response);
        String expectedJson = "[{\"categoryId\":\"1\",\"username\":\"myusername\",\"name\":\"Sport\",\"feeds\":[{\"feedId\":\"2\",\"categoryId\":\"1\",\"userName\":\"myusername\",\"name\":\"BBC\",\"url\":\"http://www.bbc.co.uk\"}]}]";
        assertEquals(expectedJson, jsonResponse);
    }
}