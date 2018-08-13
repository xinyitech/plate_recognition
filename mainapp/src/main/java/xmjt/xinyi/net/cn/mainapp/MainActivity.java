package xmjt.xinyi.net.cn.mainapp;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.morgoo.droidplugin.pm.PluginManager;
import com.morgoo.helper.compat.PackageManagerCompat;

import java.io.File;

public class MainActivity extends AppCompatActivity {

    private Button buttonInstall;
    private Button buttonTest;
    private Button buttonTest1;
    private MyBroadCaster mMyBroadCaster;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        buttonInstall = findViewById(R.id.buttonInstall);
        buttonTest = findViewById(R.id.buttonTest);
        buttonTest1 = findViewById(R.id.buttonTest1);

        IntentFilter filter = new IntentFilter();
        filter.addAction("xinyi.recognition.sdk");
        mMyBroadCaster = new MyBroadCaster();
        this.registerReceiver(mMyBroadCaster, filter);


        buttonInstall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                File file = new File(Environment.getExternalStorageDirectory(), "/plugin");
                if (!file.exists()) {
                    try {
                        file.mkdir();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                File[] plugins = file.listFiles();
                //没有插件
                if (plugins == null || plugins.length == 0) {
                    Log.d("sss", "插件安装失败==没有找到插件");
                    return;
                }
                //1.先卸载apk，插件apk的包名是“com.plugin”,不管有没有安装，先卸载了再说
                try {
                    PluginManager.getInstance().deletePackage("cn.net.xinyi.xmjt", 0);
                } catch (RemoteException e) {
                    Log.d("sss", "插件卸载失败==" + e.getMessage().toString());
                    e.printStackTrace();
                }
                for (File apk : plugins) {
                    if (!apk.getAbsolutePath().contains("apk")) {
                        Log.d("sss", "不是apk文件啊==" + apk.getName());
                        continue;
                    }
                    //开始进行插件apk的安装
                    try {
                        Log.d("sss", "即将安装的apk==" + apk.getAbsolutePath());
                        int a = PluginManager.getInstance().installPackage(apk.getAbsolutePath(), PackageManagerCompat.INSTALL_REPLACE_EXISTING);
                        getResult(a);
                    } catch (RemoteException e) {
                        Log.d("sss", "插件安装失败==" + e.getMessage().toString());
                        e.printStackTrace();
                    }
                }
            }
        });

        buttonTest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage("cn.net.xinyi.xmjt");
                    if (intent == null) {
                        Log.d("sss", "intent是空的，没法使用啊");
                    }
                    intent.putExtra("type", 1);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 1000);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sss", "插件启动失败==" + e.toString());
                }
            }
        });


        buttonTest1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                try {
                    PackageManager pm = getPackageManager();
                    Intent intent = pm.getLaunchIntentForPackage("cn.net.xinyi.xmjt");
                    if (intent == null) {
                        Log.d("sss", "intent是空的，没法使用啊");
                    }
                    intent.putExtra("type", 2);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivityForResult(intent, 1001);
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.d("sss", "插件启动失败==" + e.toString());
                }
            }
        });
    }

    private void getResult(int a) {
        switch (a) {
            case -1:
                Toast.makeText(MainActivity.this, "安装或卸载失败", Toast.LENGTH_SHORT).show();
                break;
            case 1:
                Toast.makeText(MainActivity.this, "安装成功", Toast.LENGTH_SHORT).show();
                break;
            case -110:
                Toast.makeText(MainActivity.this, "安装程序内部错误", Toast.LENGTH_SHORT).show();
                break;
            case -2:
                Toast.makeText(MainActivity.this, "无效的Apk", Toast.LENGTH_SHORT).show();
                break;
            case 0x00000002:
                Toast.makeText(MainActivity.this, "安装更新", Toast.LENGTH_SHORT).show();
                break;
            case -3:
                Toast.makeText(MainActivity.this, "不支持的ABI", Toast.LENGTH_SHORT).show();
                break;
            default:
                Toast.makeText(MainActivity.this, "未知错误", Toast.LENGTH_SHORT).show();
                break;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mMyBroadCaster);
    }

    //接收拍照结果的广播
    public class MyBroadCaster extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            //type==0 表示照识别车牌号码
            if (intent.getIntExtra("type", -1) == 1) {
                Toast.makeText(MainActivity.this, "1", Toast.LENGTH_SHORT).show();
            } else if (intent.getIntExtra("type", -1) == 2) {//type==2 表示识别身份证
                Toast.makeText(MainActivity.this, "2", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
