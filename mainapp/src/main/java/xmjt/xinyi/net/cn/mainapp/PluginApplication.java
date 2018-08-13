package xmjt.xinyi.net.cn.mainapp;

import android.app.Application;
import android.content.Context;

import com.morgoo.droidplugin.PluginHelper;

public class PluginApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //这里必须在super.onCreate方法之后，顺序不能变
        PluginHelper.getInstance().applicationOnCreate(getBaseContext());
    }

    @Override
    protected void attachBaseContext(Context base) {
        PluginHelper.getInstance().applicationAttachBaseContext(base);
        super.attachBaseContext(base);
    }
}
