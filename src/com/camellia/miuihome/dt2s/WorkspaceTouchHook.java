package com.camellia.miuihome.dt2s;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import de.robv.android.xposed.XC_MethodHook;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class WorkspaceTouchHook extends XC_MethodHook {
    private static DoubleTapController sController;

    @Override
    public void beforeHookedMethod(MethodHookParam param) throws Throwable {
        MotionEvent event = (MotionEvent) param.args[0];
        if (event == null) {
            return;
        }

        if (sController == null) {
            Context context = null;
            if (param.thisObject instanceof View) {
                context = ((View) param.thisObject).getContext();
            }
            if (context != null) {
                sController = new DoubleTapController(context);
                Log.i(MainHook.TAG, "Initialized DoubleTapController on first touch");
            }
        }

        if (sController == null || !sController.isDoubleTapEvent(event)) {
            return;
        }

        Log.i(MainHook.TAG, "Double tap detected on Workspace!");

        // Check if the tap was on an icon or widget
        try {
            Field screenIdxField = findFieldInHierarchy(param.thisObject.getClass(), "mCurrentScreenIndex");
            if (screenIdxField != null) {
                screenIdxField.setAccessible(true);
                int screenIndex = screenIdxField.getInt(param.thisObject);

                Method getCellLayoutMethod = findMethodInHierarchy(param.thisObject.getClass(), "getCellLayout", int.class);
                if (getCellLayoutMethod != null) {
                    Object cellLayout = getCellLayoutMethod.invoke(param.thisObject, screenIndex);
                    if (cellLayout != null) {
                        Method lastDownMethod = findMethodInHierarchy(cellLayout.getClass(), "lastDownOnOccupiedCell");
                        if (lastDownMethod != null) {
                            Boolean occupied = (Boolean) lastDownMethod.invoke(cellLayout);
                            if (occupied != null && occupied) {
                                Log.d(MainHook.TAG, "Ignored: cell occupied by icon or widget");
                                return;
                            }
                        }
                    }
                }
            }
        } catch (Throwable t) {
            Log.w(MainHook.TAG, "Cell check bypassed: " + t.getMessage());
        }

        // Check if in desktop editing mode
        try {
            Method isEditingMethod = findMethodInHierarchy(param.thisObject.getClass(), "isInNormalEditingMode");
            if (isEditingMethod != null) {
                Boolean inEditing = (Boolean) isEditingMethod.invoke(param.thisObject);
                if (inEditing != null && inEditing) {
                    Log.d(MainHook.TAG, "Ignored: launcher in editing mode");
                    return;
                }
            }
        } catch (Throwable ignored) {}

        // Trigger MIUI native screen lock broadcast
        try {
            Context context = null;
            if (param.thisObject instanceof View) {
                context = ((View) param.thisObject).getContext();
            }
            if (context != null) {
                Intent lockIntent = new Intent("com.miui.app.ExtraStatusBarManager.action_TRIGGER_TOGGLE");
                lockIntent.putExtra("com.miui.app.ExtraStatusBarManager.extra_TOGGLE_ID", 10);
                context.sendBroadcast(lockIntent);
                Log.i(MainHook.TAG, "Screen lock broadcast sent successfully!");
            }
        } catch (Throwable t) {
            Log.e(MainHook.TAG, "Failed to send lock broadcast", t);
        }
    }

    private static Field findFieldInHierarchy(Class<?> clazz, String fieldName) {
        Class<?> curr = clazz;
        while (curr != null && curr != Object.class) {
            try {
                Field f = curr.getDeclaredField(fieldName);
                f.setAccessible(true);
                return f;
            } catch (NoSuchFieldException e) {
                curr = curr.getSuperclass();
            }
        }
        return null;
    }

    private static Method findMethodInHierarchy(Class<?> clazz, String methodName, Class<?>... paramTypes) {
        Class<?> curr = clazz;
        while (curr != null && curr != Object.class) {
            try {
                Method m = curr.getDeclaredMethod(methodName, paramTypes);
                m.setAccessible(true);
                return m;
            } catch (NoSuchMethodException e) {
                curr = curr.getSuperclass();
            }
        }
        return null;
    }
}
