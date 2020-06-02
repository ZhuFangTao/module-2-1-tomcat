1. 解析server.xml配置文件 获取设置的端口和appBase路径

```java
 port = Integer.parseInt(connectEle.attributeValue("port"));
 String appLocation = element.attributeValue("appBase");
```

2. 根据appBase获取应用并解析应用文件夹下的web.xml文件 并封装成MyHost对象

```java
 private void loadHostConfig(String appLocation) throws Exception {
        File file = new File(appLocation);
        if (file.isDirectory() || file.list() != null) {
            Map<String, MyContext> contextMap = new HashMap<>();
            for (String fileName : file.list()) {
                contextMap.put(fileName, loadContext(appLocation + File.separator + fileName));
            }
            host = new MyHost(contextMap);
        }
    }


    private MyContext loadContext(String appPath) throws Exception {
        String webXmlLocation = appPath + File.separator + "web.xml";
        return new MyContext(loadServlet(webXmlLocation));
    }

```

3. 定义MyClassLoader 

```java
public class MyClassLoader extends ClassLoader{

    private String className;

    public MyClassLoader(String className){
        this.className = className;
    }

    @Override
    protected Class<?> findClass(String myPath) {
        byte[] cLassBytes = null;
        Path path;
        try {
            path = Paths.get(new URI("file://" + myPath));
            cLassBytes = Files.readAllBytes(path);
        } catch (IOException | URISyntaxException e) {
            e.printStackTrace();
        }
        Class clazz = defineClass(className, cLassBytes, 0, cLassBytes.length);
        return clazz;
    }
}
```



4.将第二步中解析出来的servlet-class 使用自定义加载器加载 并放入servletWrapperMap中

```java
Class clazz = new MyClassLoader(servletClass).findClass(clazzFilePath + ".class");
servletWrapperMap.put(urlPattern, new ServletWrapper().setServlet((Servlet) clazz.newInstance()));
```



5. 在接受到客户端请求时，根据host-context-servlet逐级匹配url。如果能查找到则调用servlet.service() 否则返回404页面

```java
Servlet servlet = MiniCatConfig.getInstance().matchRequest(request);
if(servlet != null){
    servlet.service(request,response);
}else{
    response.outputHtml(request.getUrl());
}
```

```java
public Servlet matchRequest(Request request) {
    String url = request.getUrl().substring(1);
    MyContext context = host.getContextMap().get(url.substring(0, url.indexOf("/")));
    if (context != null) {
        ServletWrapper servletWrapper = context.getServletWrapperMap().get(url.substring(url.indexOf("/")));
        if (servletWrapper != null) {
            return servletWrapper.getServlet();
        }
    }
    return null;
}
```