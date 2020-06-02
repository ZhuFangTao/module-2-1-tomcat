package servlets;

import server.HttpProtocolUtil;
import server.HttpServlet;
import server.Request;
import server.Response;

import java.io.IOException;

public class MyServlet extends HttpServlet {
    @Override
    public void doGet(Request request, Response response) {
        String content = "<h1>app4 servlet get</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void doPost(Request request, Response response) {
        String content = "<h1>app4 servlet post</h1>";
        try {
            response.output((HttpProtocolUtil.getHttpHeader200(content.getBytes().length) + content));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void init() throws Exception {

    }

    @Override
    public void destory() throws Exception {

    }
}
