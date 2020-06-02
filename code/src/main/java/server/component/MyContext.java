package server.component;

import java.util.Map;

/**
 * \* User: ZhuFangTao
 * \* Date: 2020/6/2 7:18 下午
 * \
 */
public class MyContext {

    private Map<String,ServletWrapper> servletWrapperMap;

    public Map<String, ServletWrapper> getServletWrapperMap() {
        return servletWrapperMap;
    }

    public MyContext setServletWrapperMap(Map<String, ServletWrapper> servletWrapperMap) {
        this.servletWrapperMap = servletWrapperMap;
        return this;
    }

    public MyContext(Map<String,ServletWrapper> servletWrapperMap){
        this.servletWrapperMap = servletWrapperMap;
    }
}