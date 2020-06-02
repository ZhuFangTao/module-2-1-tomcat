package server;

import server.util.MiniCatConfig;

import java.io.InputStream;
import java.net.Socket;
import java.util.Map;

public class RequestProcessor extends Thread {

    private Socket socket;
    private Map<String,HttpServlet> servletMap;

    public RequestProcessor(Socket socket, Map<String, HttpServlet> servletMap) {
        this.socket = socket;
        this.servletMap = servletMap;
    }

    @Override
    public void run() {
        try{
            InputStream inputStream = socket.getInputStream();

            // 封装Request对象和Response对象
            Request request = new Request(inputStream);
            Response response = new Response(socket.getOutputStream());

            findServletAndService(request,response);
            socket.close();

        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void findServletAndService(Request request,Response response) throws Exception {
        Servlet servlet = MiniCatConfig.getInstance().matchRequest(request);
        if(servlet != null){
            servlet.service(request,response);
        }else{
            response.outputHtml(request.getUrl());
        }
    }
}
