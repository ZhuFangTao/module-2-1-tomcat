package server.component;

import java.util.Map;

/**
 * \* User: ZhuFangTao
 * \* Date: 2020/6/2 7:18 下午
 * \
 */
public class MyHost {

    private Map<String, MyContext> contextMap;

    public Map<String, MyContext> getContextMap() {
        return contextMap;
    }

    public MyHost setContextMap(Map<String, MyContext> contextMap) {
        this.contextMap = contextMap;
        return this;
    }

    public MyHost(Map<String, MyContext> contextMap) {
        this.contextMap = contextMap;
    }
}