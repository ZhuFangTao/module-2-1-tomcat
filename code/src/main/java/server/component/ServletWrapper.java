package server.component;

import server.Servlet;

/**
 * \* User: ZhuFangTao
 * \* Date: 2020/6/2 7:19 下午
 * \
 */
public class ServletWrapper {

    private Servlet servlet;

    public Servlet getServlet() {
        return servlet;
    }

    public ServletWrapper setServlet(Servlet servlet) {
        this.servlet = servlet;
        return this;
    }
}