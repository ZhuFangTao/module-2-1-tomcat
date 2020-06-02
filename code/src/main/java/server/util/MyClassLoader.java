package server.util;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

/**
 * \* User: ZhuFangTao
 * \* Date: 2020/6/2 8:11 下午
 * \
 */
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