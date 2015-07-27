package com.bill.rss.server;

import org.junit.Test;
import org.mockito.Mockito;

import spark.HaltException;
import spark.Request;
import spark.Response;

import com.bill.rss.dataProvider.CategoryUpdater;
import com.bill.rss.domain.Category;

import static com.bill.rss.server.MockUtils.createRequestMock;
import static com.bill.rss.server.MockUtils.createResponseMock;

import static org.mockito.Mockito.when;

public class SaveCategoryRouteTest {

    @Test
    public void testAddCategory() {
        Request request = createRequestMock("logged-in");
        addAddCategoryBodyToRequestMock(request);
        Response response = createResponseMock();
        CategoryUpdater categoryUpdater = Mockito.mock(CategoryUpdater.class);

        SaveCategoryRoute saveCategoryRoute = new SaveCategoryRoute("");
        saveCategoryRoute.setCategoryUpdater(categoryUpdater );
        saveCategoryRoute.handle(request, response);
        Mockito.verify(categoryUpdater, Mockito.times(1)).addCategory(Mockito.any(Category.class));
    }


    @Test
    public void testSaveCategory() {
        Request request = createRequestMock("logged-in");
        addSaveCategoryBodyToRequestMock(request);
        Response response = createResponseMock();
        CategoryUpdater categoryUpdater = Mockito.mock(CategoryUpdater.class);

        SaveCategoryRoute saveCategoryRoute = new SaveCategoryRoute("");
        saveCategoryRoute.setCategoryUpdater(categoryUpdater);
        saveCategoryRoute.handle(request, response);
        Mockito.verify(categoryUpdater, Mockito.times(1)).saveCategory(Mockito.any(Category.class));
    }


    @Test(expected = HaltException.class)
    public void testSaveCategoryInvalidInput() {
        Request request = createRequestMock("logged-in");
        addInvalidBodyToRequestMock(request);
        Response response = createResponseMock();
        CategoryUpdater categoryUpdater = Mockito.mock(CategoryUpdater.class);

        SaveCategoryRoute saveCategoryRoute = new SaveCategoryRoute("");
        saveCategoryRoute.setCategoryUpdater(categoryUpdater);
        saveCategoryRoute.handle(request, response);
    }



    private void addSaveCategoryBodyToRequestMock(Request request) {
        String userBody = "{\"categoryId\":\"123\",\"username\":\"bob\",\"name\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addAddCategoryBodyToRequestMock(Request request) {
        String userBody = "{\"username\":\"bob\",\"name\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }


    private void addInvalidBodyToRequestMock(Request request) {
        String userBody = "{\"usersdname\":\"bob\",\"nasdme\":\"News\"}";
        when(request.body()).thenReturn(userBody);
    }
}
