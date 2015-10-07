package lasting.wifimusicdemo;

/**
 * 概述：本程序用于在局域网（可以是自家路由，也可以是CMCC-EDU，手机开的热点
 * 也行）中创建一个专有地址用于网内其他终端访问，下载，和删除手机中的文件。
 * 含有一个activity一个service。
 * 使用方法：此APP打开后有一个开始按钮，按下会自动获取本机在局域网内的地址
 * （端口号暂固定为6666），在同局域网内其他终端可以使用浏览器等软件访问
 * XXX.XXX.XXX.XXX:6666，来访问本机的根分区目录，有下载文件，文件夹打包
 * 下载，文件删除的功能，经测试，CMCC-EDU中使用本APP，浏览器直接地址访问会
 * 被其屏蔽（其可能是为了安全考虑），无法直接访问，但是实际上已在局域网中创
 * 建了这个地址，可以通过其他软件访问此地址，且能正常读写本机文件。
 * <p/>
 * 本文件：继承activity类，作为本APP的唯一一个activity，主要实现了后台服务
 * 的开启，和文件列表的读取，接口只有一个即点击按钮监视器，用途即是监听用户
 * 点击按钮操作，以便切换按钮模式，更改后台服务状态。
 * 以下是具体内容：
 * 涉及到的类（不含控件类）：1.WebServer：用于启动后台服务，在局域网上注册。
 * 2.CopyUtil：将本APP中assets文件夹中的工具copy至本机内部存储器根目录下
 * 重要方法的说明见方法前面。
 * <p/>
 * 本文件：继承activity类，作为本APP的唯一一个activity，主要实现了后台服务
 * 的开启，和文件列表的读取，接口只有一个即点击按钮监视器，用途即是监听用户
 * 点击按钮操作，以便切换按钮模式，更改后台服务状态。
 * 以下是具体内容：
 * 涉及到的类（不含控件类）：1.WebServer：用于启动后台服务，在局域网上注册。
 * 2.CopyUtil：将本APP中assets文件夹中的工具copy至本机内部存储器根目录下
 * 重要方法的说明见方法前面。
 */

/**
 * 本文件：继承activity类，作为本APP的唯一一个activity，主要实现了后台服务
 * 的开启，和文件列表的读取，接口只有一个即点击按钮监视器，用途即是监听用户
 * 点击按钮操作，以便切换按钮模式，更改后台服务状态。
 * 以下是具体内容：
 * 涉及到的类（不含控件类）：1.WebServer：用于启动后台服务，在局域网上注册。
 * 2.CopyUtil：将本APP中assets文件夹中的工具copy至本机内部存储器根目录下
 * 重要方法的说明见方法前面。
 */

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import lasting.wifimusicdemo.server.WebService;
import lasting.wifimusicdemo.util.CopyUtil;

public class MainActivity extends Activity implements OnCheckedChangeListener {

    private ToggleButton toggleBtn;
    private TextView urlText;

    private Intent intent;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        initFiles();

        intent = new Intent(this, WebService.class);
    }

    /**
     * 各控件的初始化，按钮监听器的设置
     */

    private void initViews() {
        toggleBtn = (ToggleButton) findViewById(R.id.toggleBtn);
        toggleBtn.setOnCheckedChangeListener(this);
        urlText = (TextView) findViewById(R.id.urlText);
    }

    /**
     * 把有用工具copy至本机，即为有终端访问本机时展现的画面。
     */

    private void initFiles() {
        new CopyUtil(this).assetsCopy();
    }

    /**
     * 按钮模式切换类
     * 点击时检测能否自动获取本地IP，能则开启后台服务，不能则返回错误
     */

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            String ip = null;
            try {
                ip = getLocalIpAddress();
            } catch (SocketException e) {
                e.printStackTrace();
            }
            if (ip == null) {
                Toast.makeText(this, "未连接网络", Toast.LENGTH_SHORT).show();
                urlText.setText("");
            } else {
                startService(intent);
                urlText.setText("http://" + ip + ":" + WebService.PORT + "/");
            }
        } else {
            stopService(intent);
            urlText.setText("");
        }
    }

    /**
     * 获取当前IP地址
     * 原先采用通用的方法，但那个方法在2.3及之前版本没有问题，默认IPV4
     * 但由于安卓4.0以后默认获取的是IPV6的地址，但是IPV6会显示乱码，
     * 于是改进了一下，加了一个布尔值参数获取IPV4地址，方法基本是采取通用式
     */

    private String getLocalIpAddress() throws SocketException {
        for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
            NetworkInterface intf = en.nextElement();
            for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements(); ) {
                InetAddress inetAddress = enumIpAddr.nextElement();
                if (!inetAddress.isLoopbackAddress() && (inetAddress instanceof Inet4Address)) {
                    return inetAddress.getHostAddress().toString();
                }
            }
        }
        return "null";
    }
}