package lasting.wifimusicdemo.server;

import android.util.Log;

import org.apache.http.ConnectionClosedException;
import org.apache.http.HttpException;
import org.apache.http.HttpServerConnection;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpService;

import java.io.IOException;

public class WorkerThread extends Thread {

    private final HttpService httpservice;
    private final HttpServerConnection conn;

    public WorkerThread(final HttpService httpservice, final HttpServerConnection conn) {
        super();
        this.httpservice = httpservice;
        this.conn = conn;
    }

    /**
     * 工作线程的设置和运行
     */

    @Override
    public void run() {
        HttpContext context = new BasicHttpContext();
        try {
            while (!Thread.interrupted() && this.conn.isOpen()) {
                this.httpservice.handleRequest(this.conn, context);
            }
        } catch (ConnectionClosedException e) {
            System.err.println("Client closed connection");
        } catch (IOException e) {
            System.err.println("I/O error: " + e.getMessage());
        } catch (HttpException e) {
            System.err.println("Unrecoverable HTTP protocol violation: " + e.getMessage());
        } finally {
            try {
                this.conn.shutdown();
            } catch (IOException e) {
                Log.i("info", "error");
            }
        }
    }
}