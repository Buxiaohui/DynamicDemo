package com.bxh.dynamicdemo;

import android.Manifest;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Environment;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import java.io.File;
import java.util.List;

import dalvik.system.DexClassLoader;
import dalvik.system.PathClassLoader;

import com.test.dynamic.dyinterface.IDynamic;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "BXH_dynamic";
    //动态类加载接口
    private IDynamic lib;
    private String[] permissions = {Manifest.permission.WRITE_EXTERNAL_STORAGE, Manifest.permission.READ_EXTERNAL_STORAGE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

            // 检查该权限是否已经获取
            int i0 = ContextCompat.checkSelfPermission(this, permissions[0]);
            int i1 = ContextCompat.checkSelfPermission(this, permissions[1]);
            // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
            if (i1 != PackageManager.PERMISSION_GRANTED || i0 != PackageManager.PERMISSION_GRANTED) {
                // 如果没有授予该权限，就去提示用户请求
                startRequestPermission();
            } else {
                init();
            }
        }
    }

    // 开始提交请求权限
    private void startRequestPermission() {
        ActivityCompat.requestPermissions(this, permissions, 321);
    }

    // 用户权限 申请 的回调方法
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == 321) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (grantResults[0] != PackageManager.PERMISSION_GRANTED) {
                    // 判断用户是否 点击了不再提醒。(检测该权限是否还可以申请)
                    boolean b = shouldShowRequestPermissionRationale(permissions[0]);
                    if (!b) {
                        // 用户还是想用我的 APP 的
                        // 提示用户去应用设置界面手动开启权限
                        Toast.makeText(MainActivity.this, "你不设置就玩不了阿", Toast.LENGTH_SHORT).show();
                    } else
                        finish();
                } else {
                    Toast.makeText(this, "权限获取成功", Toast.LENGTH_SHORT).show();
                    init();
                }
            }
        }
    }

    private void init() {
        boolean isPathMode = false;
        int i0 = ContextCompat.checkSelfPermission(this, permissions[0]);
        int i1 = ContextCompat.checkSelfPermission(this, permissions[1]);
        // 权限是否已经 授权 GRANTED---授权  DINIED---拒绝
        if (i1 != PackageManager.PERMISSION_GRANTED || i0 != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        //初始化组件
        Button showBannerBtn = (Button) findViewById(R.id.show_banner_btn);
        Button showDialogBtn = (Button) findViewById(R.id.show_dialog_btn);
        Button showFullScreenBtn = (Button) findViewById(R.id.show_fullscreen_btn);
        Button showAppWallBtn = (Button) findViewById(R.id.show_appwall_btn);
        /**使用DexClassLoader方式加载类*/
        //dex压缩文件的路径(可以是apk,jar,zip格式)
        String dexPath = Environment.getExternalStorageDirectory().toString() + File.separator + "dynamic_dex.jar";
        //dex解压释放后的目录
        //String dexOutputDir = getApplicationInfo().dataDir;
        String dexOutputDirs = Environment.getExternalStorageDirectory().toString();
        Log.i(TAG, "dexOutputDirs=" + dexOutputDirs);
        //定义DexClassLoader
        //第一个参数：是dex压缩文件的路径
        //第二个参数：是dex解压缩后存放的目录
        //第三个参数：是C/C++依赖的本地库文件目录,可以为null
        //第四个参数：是上一级的类加载器
        /**
         * java.lang.IllegalArgumentException: Optimized data directory /storage/emulated/0 is not owned by the current user.
         * Shared storage cannot protect your application from code injection attacks.
         * 4.1以后不能把dex文件放sd卡
         * */
        String dexFilePath = getApplicationContext().getExternalCacheDir().toString() + File.separator + "dynamic_dex.jar";
        File dexOutputDir = getApplicationContext().getDir("dex", 0);
        Log.i(TAG, "bxh dexFilePath=" + dexFilePath);
        Log.i(TAG, "bxh dexOutputDir=" + dexOutputDir);
        //DexClassLoader cl = new DexClassLoader(dexPath, dexOutputDirs, null, getClassLoader());
        DexClassLoader cl = new DexClassLoader(dexFilePath, dexOutputDir.getAbsolutePath(), null, getClassLoader());
        Log.i(TAG, "bxh cl=" + cl);
        /**使用PathClassLoader方法加载类*/
        //创建一个意图，用来找到指定的apk：这里的"com.dynamic.impl是指定apk中在AndroidMainfest.xml文件中定义的<action name="com.dynamic.impl"/>
        Intent intent = new Intent("com.dynamic.impl", null);
        //获得包管理器
        PackageManager pm = getPackageManager();
        List<ResolveInfo> resolveinfoes = pm.queryIntentActivities(intent, 0);
        if (resolveinfoes == null || resolveinfoes.size() < 1) {
            Log.e(TAG, "resolveinfoes error");
            return;
        }
        Log.i(TAG, "resolveinfoes normal");
        //获得指定的activity的信息
        ActivityInfo actInfo = resolveinfoes.get(0).activityInfo;
        //获得apk的目录或者jar的目录
        String apkPath = actInfo.applicationInfo.sourceDir;
        Log.i(TAG, "apkPath =" + apkPath);
        //native代码的目录
        String libPath = actInfo.applicationInfo.nativeLibraryDir;
        Log.i(TAG, "libPath =" + libPath);

        //创建类加载器，把dex加载到虚拟机中
        //第一个参数：是指定apk安装的路径，这个路径要注意只能是通过actInfo.applicationInfo.sourceDir来获取
        //第二个参数：是C/C++依赖的本地库文件目录,可以为null
        //第三个参数：是上一级的类加载器
        PathClassLoader pcl = new PathClassLoader(apkPath, libPath, this.getClassLoader());
        //加载类
        try {
            //com.dynamic.impl.Dynamic是动态类名
            //使用DexClassLoader加载类
            //Class libProviderClazz = cl.loadClass("com.dynamic.impl.Dynamic");
            //使用PathClassLoader加载类
            //Class libProviderClazz = pcl.loadClass("com.dynamic.impl.Dynamic");
            Class libProviderClazz = null;
            if (isPathMode) {
                //PathClassLoader 方式需要安装apk
                libProviderClazz = pcl.loadClass("com.test.dynamic.dyclass.DynamicImpl");
            } else {
                //PathClassLoader 方式不需要安装apk
                libProviderClazz = cl.loadClass("com.test.dynamic.dyclass.DynamicImpl");
            }


            lib = (IDynamic) libProviderClazz.newInstance();
            if (lib != null) {
                Log.e(TAG, "lib normal");
                lib.init(MainActivity.this);
            } else {
                Log.e(TAG, "lib null,load class error!!!!");
            }
        } catch (Exception exception) {
            exception.printStackTrace();
            Log.e(TAG, "lib init error,e=" + exception.toString());
        }
        //}
        /**下面分别调用动态类中的方法*/
        showBannerBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showBanner();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showDialogBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showDialog();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showFullScreenBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showFullScreen();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
        showAppWallBtn.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                if (lib != null) {
                    lib.showAppWall();
                } else {
                    Toast.makeText(getApplicationContext(), "类加载失败", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
