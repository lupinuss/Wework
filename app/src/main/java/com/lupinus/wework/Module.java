package com.lupinus.wework;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

/**
 * Created by luyufa on 2019/4/10.
 */

public class Module implements IXposedHookLoadPackage {


    @Override
    public void handleLoadPackage(XC_LoadPackage.LoadPackageParam lpparam) throws Throwable {

        String packetName = lpparam.packageName;

        String processName = lpparam.processName;

        //com.tencent.wework
        if ("com.tencent.wework".equals(packetName) && "com.tencent.wework".equals(processName)){

            RootCloak rootCloak = new RootCloak();
            rootCloak.handleLoadPackage(lpparam);

            WeChatWork.hook();

        }
    }
}
