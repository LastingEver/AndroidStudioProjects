package lasting.wifimusicdemo.util;

/**
 * 本文件用于将有用工具copy至本机
 */

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class CopyUtil {

    private AssetManager manager;

    public CopyUtil(Context context) {
        manager = context.getAssets();
    }

    /**
     * 将assets下wfs文件夹copy至本机根目录.wfs文件夹（默认隐藏）
     */

    public boolean assetsCopy() {
        try {
            assetsCopy("wfs", Environment.getExternalStorageDirectory() + "/.wfs");
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public void assetsCopy(String assetsPath, String dirPath) throws IOException {
        String[] list = manager.list(assetsPath);
        if (list.length == 0) {
            InputStream in = manager.open(assetsPath);
            File file = new File(dirPath);
            file.getParentFile().mkdirs();
            file.createNewFile();
            FileOutputStream fout = new FileOutputStream(file);
            byte[] buf = new byte[1024];
            int count;
            while ((count = in.read(buf)) != -1) {
                fout.write(buf, 0, count);
                fout.flush();
            }
            in.close();
            fout.close();
        } else {
            for (String path : list) {
                assetsCopy(assetsPath + "/" + path, dirPath + "/" + path);
            }
        }
    }
}