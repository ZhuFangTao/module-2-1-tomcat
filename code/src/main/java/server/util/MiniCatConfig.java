package server.util;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;
import server.Request;
import server.Servlet;
import server.component.MyContext;
import server.component.MyHost;
import server.component.ServletWrapper;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * \* User: ZhuFangTao
 * \* Date: 2020/6/2 7:29 下午
 * \
 */
public class MiniCatConfig {

    private MiniCatConfig() {
    }

    private int port;
    private MyHost host;

    public int getPort() {
        return port;
    }

    private static MiniCatConfig config = new MiniCatConfig();

    public static MiniCatConfig getInstance() {
        return config;
    }

    public void loadServerConfig() {
        InputStream resourceAsStream = this.getClass().getClassLoader().getResourceAsStream("server.xml");
        SAXReader saxReader = new SAXReader();
        try {
            Document document = saxReader.read(resourceAsStream);
            Element rootElement = document.getRootElement();
            //解析配置的端口信息
            List<Element> connectorNodes = rootElement.selectNodes("//Server//Service//Connector");
            Element connectEle = connectorNodes.get(0);
            port = Integer.parseInt(connectEle.attributeValue("port"));

            List<Element> selectNodes = rootElement.selectNodes("//Server//Service//Engine//Host");
            Element element = selectNodes.get(0);
            String appLocation = element.attributeValue("appBase");
            loadHostConfig(appLocation);
            System.out.println(appLocation);
            System.out.println(port);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

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

    /**
     * 加载解析web.xml，初始化Servlet
     */
    private Map<String, ServletWrapper> loadServlet(String webXmlLocation) throws Exception {
        SAXReader saxReader = new SAXReader();
        Map<String, ServletWrapper> servletWrapperMap = new HashMap<>();
        try {
            Document document = saxReader.read(new FileReader(webXmlLocation));
            Element rootElement = document.getRootElement();
            List<Element> selectNodes = rootElement.selectNodes("//servlet");
            for (int i = 0; i < selectNodes.size(); i++) {
                Element element = selectNodes.get(i);
                // <servlet-name>lagou</servlet-name>
                Element servletnameElement = (Element) element.selectSingleNode("servlet-name");
                String servletName = servletnameElement.getStringValue();
                // <servlet-class>servlets.LagouServlet</servlet-class>
                Element servletclassElement = (Element) element.selectSingleNode("servlet-class");
                String servletClass = servletclassElement.getStringValue();
                // 根据servlet-name的值找到url-pattern
                Element servletMapping = (Element) rootElement.selectSingleNode("/web-app/servlet-mapping[servlet-name='" + servletName + "']");
                // /lagou
                String urlPattern = servletMapping.selectSingleNode("url-pattern").getStringValue();
                String substring = webXmlLocation.substring(0, webXmlLocation.lastIndexOf(File.separator) + 1);
                String clazzFilePath = substring + "servlets" + File.separator + servletClass.substring(servletClass.lastIndexOf(".") + 1);
                Class clazz = new MyClassLoader(servletClass).findClass(clazzFilePath + ".class");
                servletWrapperMap.put(urlPattern, new ServletWrapper().setServlet((Servlet) clazz.newInstance()));
            }
        } catch (DocumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return servletWrapperMap;
    }

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
}