package com.camellia.miuihome.dt2s;

import android.util.Log;
import android.view.MotionEvent;
import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage.LoadPackageParam;

public class MainHook implements IXposedHookLoadPackage {
    public static final String TAG = "MiuiHomeDT2S";
    public static final String TARGET_PKG = "com.miui.home";

    @Override
    public void handleLoadPackage(final LoadPackageParam lpparam) throws Throwable {
        if (!TARGET_PKG.equals(lpparam.packageName)) {
            return;
        }

        Log.i(TAG, "Initializing double tap to sleep hook in " + lpparam.packageName);
        XposedBridge.log(TAG + ": Initializing double tap to sleep hook in " + lpparam.packageName);

        try {
            Class<?> workspaceClass = XposedHelpers.findClass("com.miui.home.launcher.Workspace", lpparam.classLoader);
            XposedHelpers.findAndHookMethod(workspaceClass, "dispatchTouchEvent", MotionEvent.class, new WorkspaceTouchHook());
            Log.i(TAG, "Successfully installed dispatchTouchEvent hook on Workspace");
        } catch (Throwable t) {
            Log.e(TAG, "Hook installation error: ", t);
            XposedBridge.log(TAG + " Hook installation error: " + t);
        }
    }
}
