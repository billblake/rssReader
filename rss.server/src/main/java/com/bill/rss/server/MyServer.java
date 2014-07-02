package com.bill.rss.server;

import java.io.IOException;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

import com.bill.rss.dataProvider.CategoryProvider;
import com.bill.rss.domain.Category;
import com.bill.rss.mongodb.CategoriesRetriever;

public class MyServer extends HttpServlet {
	
	private CategoryProvider categoryProvider = new CategoriesRetriever();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
    	List<Category> categories = categoryProvider.retrieveCategories();
    	resp.getWriter().print(JsonUtils.convertObjectToJson(categories));
    }
    
	public static void main(String[] args) throws Exception {
		Server server = new Server(Integer.valueOf(System.getenv("PORT")));
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new MyServer()),"/*");
        server.start();
        server.join();
	}
}
