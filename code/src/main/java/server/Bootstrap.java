package server;

import server.util.MiniCatConfig;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.*;

/**
 * Minicat的主类
 */
public class Bootstrap {

    private Map<String, HttpServlet> servletMap = new HashMap<>();

    /**
     * Minicat 的程序启动入口
     *
     * @param args
     */
    public static void main(String[] args) {
        Bootstrap bootstrap = new Bootstrap();
        try {
            // 启动Minicat
            bootstrap.start();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Minicat启动需要初始化展开的一些操作
     */
    public void start() throws Exception {

        //加载解析Server.xml 并且解析web.xml
        MiniCatConfig.getInstance().loadServerConfig();

        //启动socket监听
        startHost();

    }

    private void startHost() throws IOException {
        int corePoolSize = 10;
        int maximumPoolSize = 50;
        long keepAliveTime = 100L;
        TimeUnit unit = TimeUnit.SECONDS;
        BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(50);
        ThreadFactory threadFactory = Executors.defaultThreadFactory();
        RejectedExecutionHandler handler = new ThreadPoolExecutor.AbortPolicy();

        ThreadPoolExecutor threadPoolExecutor = new ThreadPoolExecutor(
                corePoolSize,
                maximumPoolSize,
                keepAliveTime,
                unit,
                workQueue,
                threadFactory,
                handler
        );

        ServerSocket serverSocket = new ServerSocket(MiniCatConfig.getInstance().getPort());
        System.out.println("=====>>>Minicat start on port：" + MiniCatConfig.getInstance().getPort());

        while (true) {

            Socket socket = serverSocket.accept();
            RequestProcessor requestProcessor = new RequestProcessor(socket, servletMap);
            threadPoolExecutor.execute(requestProcessor);
        }
    }

}
