package lasting.wifimusicdemo.server;

/**
 * 本文件涉及作为后台服务，涉及WebServer类，及service类基本的
 * onCreate,onStartCommand和onDestroy方法
 */

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class WebService extends Service {

    public static final int PORT = 6666;
    public static final String WEBROOT = "/";

    private WebServer webServer;

    /**
     * 本APP不使用bindService方式启动服务，但是由于onBind
     * 是抽象方法必须继承，其内容为空
     */

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /**
     * 为成员变量webServer实例化
     * 内容详见WebServer类
     */

    @Override
    public void onCreate() {
        super.onCreate();
        webServer = new WebServer(PORT, WEBROOT);
    }

    /**
     * onStartCommand方法：启动服务后执行的内容，为webServer增加守护进程
     * 并启动webServer对象的功能
     */

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        webServer.setDaemon(true);
        webServer.start();
        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * onDestroy方法：销毁服务
     * 在销毁服务之前关闭webServer对象
     */

    @Override
    public void onDestroy() {
        webServer.close();
        super.onDestroy();
    }
}