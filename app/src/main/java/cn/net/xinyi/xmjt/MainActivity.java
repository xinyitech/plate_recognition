package cn.net.xinyi.xmjt;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.hardware.Camera;
import android.os.Build;
import android.os.Bundle;

import com.kernal.passport.sdk.utils.CheckPermission;
import com.kernal.passport.sdk.utils.Devcode;
import com.kernal.passport.sdk.utils.SharedPreferencesHelper;
import com.kernal.passportreader.sdk.CameraActivity;
import com.kernal.plateid.MemoryCameraActivity;
import com.kernal.plateid.PermissionActivity;

import java.util.List;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_main);
        getCameraInformation();

   //type ==1 表示进行拍照识别车牌号码
        if (getIntent().getIntExtra("type", 0) == 1) {
            Intent cameraintent = new Intent(MainActivity.this, MemoryCameraActivity.class);
            if (Build.VERSION.SDK_INT >= 23) {
                CheckPermission checkPermission = new CheckPermission(MainActivity.this);
                if (checkPermission.permissionSet(PERMISSION)) {
                    PermissionActivity.startActivityForResult(MainActivity.this, 0, "false", PERMISSION);
                } else {
                    cameraintent.putExtra("camera", false);
                    startActivity(cameraintent);
                    finish();
                }
            } else {
                cameraintent.putExtra("camera", false);
                startActivity(cameraintent);
                finish();
            }
        } else if (getIntent().getIntExtra("type", 0) == 2) {//type==2 表示识别身份证
            Intent intent = new Intent(MainActivity.this, CameraActivity.class);
            if (Build.VERSION.SDK_INT >= 23) {
                com.kernal.passport.sdk.utils.CheckPermission checkPermission = new com.kernal.passport.sdk.utils.CheckPermission(this);
                if (checkPermission.permissionSet(PERMISSION)) {
                    com.kernal.passport.sdk.utils.PermissionActivity.startActivityForResult(this, 0,
                            SharedPreferencesHelper.getInt(
                                    getApplicationContext(), "nMainId", 2),
                            Devcode.devcode, 0, 0, 0, PERMISSION);
                } else {
                    intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                            getApplicationContext(), "nMainId", 2));
                    intent.putExtra("devcode", Devcode.devcode);
                    intent.putExtra("flag", 0);
                    intent.putExtra("nCropType", 0);
                    startActivity(intent);
                    finish();
                }
            } else {
                intent.putExtra("nMainId", SharedPreferencesHelper.getInt(
                        getApplicationContext(), "nMainId", 2));
                intent.putExtra("devcode", Devcode.devcode);
                intent.putExtra("flag", 0);
                intent.putExtra("nCropType", 0);
                startActivity(intent);
                finish();
            }
        }
    }

    private void getCameraInformation() {
        if(readIntPreferences("PlateService","picWidth") == 0 || readIntPreferences("PlateService","picHeight") == 0
                || readIntPreferences("PlateService","preWidth") == 0 || readIntPreferences("PlateService","preHeight") == 0
                || readIntPreferences("PlateService","preMaxWidth") == 0 || readIntPreferences("PlateService","preMaxHeight") == 0){

            Camera camera = null;
            int pre_Max_Width = 640;
            int pre_Max_Height = 480;
            final int Max_Width = 2048;
            final int Max_Height = 1536;
            boolean isCatchPicture = false;
            int picWidth = 2048;
            int picHeight = 1536;
            int preWidth = 320;
            int preHeight = 240;
            try {
                camera = Camera.open();
                if (camera != null) {
                    Camera.Parameters parameters = camera.getParameters();
                    List<Camera.Size> previewSizes = parameters.getSupportedPreviewSizes();
                    Camera.Size size;
                    int second_Pre_Width = 0,second_Pre_Height = 0;
                    int length = previewSizes.size();
                    if (length == 1) {
                        size = previewSizes.get(0);
                        pre_Max_Width = size.width;
                        pre_Max_Height = size.height;
                    }else {
                        for (int i = 0; i < length; i++) {
                            size = previewSizes.get(i);
                            if (size.width <= Max_Width && size.height <= Max_Height) {
                                second_Pre_Width = size.width;
                                second_Pre_Height = size.height;
                                if (pre_Max_Width < second_Pre_Width) {
                                    pre_Max_Width = second_Pre_Width;
                                    pre_Max_Height = second_Pre_Height;
                                }
                            }
                        }
                    }

                    for(int i=0;i<previewSizes.size();i++){
                        if(previewSizes.get(i).width == 640 && previewSizes.get(i).height == 480){
                            preWidth = 640;
                            preHeight = 480;
                            break;
                        }
                        if(previewSizes.get(i).width == 320 && previewSizes.get(i).height == 240) {
                            preWidth = 320;
                            preHeight = 240;
                        }
                    }
                    if(preWidth == 0 || preHeight == 0){
                        if(previewSizes.size() == 1){
                            preWidth = previewSizes.get(0).width;
                            preHeight = previewSizes.get(0).height;
                        }else{
                            preWidth = previewSizes.get(previewSizes.size()/2).width;
                            preHeight = previewSizes.get(previewSizes.size()/2).height;
                        }
                    }

                    List<Camera.Size> PictureSizes = parameters.getSupportedPictureSizes();
                    for(int i=0;i<PictureSizes.size();i++){
                        if(PictureSizes.get(i).width == 2048 && PictureSizes.get(i).height == 1536){
                            if(isCatchPicture == true) {
                                break;
                            }
                            isCatchPicture = true;
                            picWidth = 2048;
                            picHeight = 1536;
                        }
                        if(PictureSizes.get(i).width == 1600 && PictureSizes.get(i).height == 1200){
                            isCatchPicture = true;
                            picWidth = 1600;
                            picHeight = 1200;
                        }
                        if(PictureSizes.get(i).width == 1280 && PictureSizes.get(i).height == 960) {
                            isCatchPicture = true;
                            picWidth = 1280;
                            picHeight = 960;
                            break;
                        }
                    }
                }

                writeIntPreferences("PlateService","picWidth",picWidth);
                writeIntPreferences("PlateService","picHeight",picHeight);
                writeIntPreferences("PlateService","preWidth",preWidth);
                writeIntPreferences("PlateService","preHeight",preHeight);
                writeIntPreferences("PlateService","preMaxWidth",pre_Max_Width);
                writeIntPreferences("PlateService","preMaxHeight",pre_Max_Height);
            } catch (Exception e) {

            } finally {
                if (camera != null) {
                    try {
                        camera.release();
                        camera = null;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }

    }
    protected int readIntPreferences(String perferencesName, String key) {
        SharedPreferences preferences = getSharedPreferences(perferencesName, MODE_PRIVATE);
        int result = preferences.getInt(key, 0);
        return result;
    }
    protected void writeIntPreferences(String perferencesName, String key, int value) {
        SharedPreferences preferences = getSharedPreferences(perferencesName, MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putInt(key, value);
        editor.commit();
    }
    static final String[] PERMISSION = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,// 写入权限
            Manifest.permission.READ_EXTERNAL_STORAGE, // 读取权限
            Manifest.permission.READ_PHONE_STATE,
            Manifest.permission.VIBRATE, Manifest.permission.INTERNET};
}
