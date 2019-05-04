/*
 * Copyright 2019 kuma. https://github.com/litangyu
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.kuma.autopay;

import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.service.quicksettings.TileService;
import android.text.TextUtils;

import java.net.URISyntaxException;

/**
 * @author kuma <a href="mailto:lty81372860@gmail.com">Contact me.</a>
 */
public class AutoPay {

    /**
     * Pay client package name
     */
    public static final String ALIPAY_PACKAGE_NAME = "com.eg.android.AlipayGphone";
    public static final String WECHAT_PACKAGE_NAME = "com.tencent.wechat";
    /**
     * Pay client scheme
     */
    private static final String ALIPAY_SCHEME = "alipayqr://";
    private static final String WECHAT_SCHEME = "wechat://";
    private static final String ALIPAY_SA_SCHEME = ALIPAY_SCHEME + "platformapi/startapp?saId={saId}";
    //private static final String WECHAT_SA_SCHEME = WECHAT_SCHEME + "";

    /**
     * Alipay sub assembly id
     */
    public static final String ALIPAY_SCAN_QR_CODE_SAID = "10000007";
    public static final String ALIPAY_BAR_CODE_SAID = "20000056";

    //private static final String WECHAT_SCAN_QR_CODE_SAID = "";
    //private static final String WECHAT_SCAN_BAR_CODE = "";

    private static final String ALIPAY_INTENT_URL_FORMAT = "intent://platformapi/startapp?saId=10000007&" +
        "clientVersion=3.7.0.0718&qrcode=https%3A%2F%2Fqr.alipay.com%2F{urlCode}%3F_s" +
        "%3Dweb-other&_t=1472443966571#Intent;" +
        "scheme=alipayqr;package=com.eg.android.AlipayGphone;end";

    /**
     * Open pay client
     *
     * @param context Context
     * @return If return true is mean activity open success.
     */
    public static boolean openPayClient(Context context, String packageName) {
        if (TextUtils.equals(ALIPAY_PACKAGE_NAME, packageName)) {
            return startIntentUrl(context, ALIPAY_SCHEME);
        } else if (TextUtils.equals(WECHAT_PACKAGE_NAME, packageName)) {
            return startIntentUrl(context, WECHAT_SCHEME);
        } else {
            return false;
        }
    }

    /**
     * Open alipay transfer
     *
     * @param context Context
     * @param urlCode The QRCode for Collection money extraction from your Alipay account.
     *                For example in my case: QRCode HTTPS://QR.ALIPAY.COM/FKX03108APKA1JNP5ZCE5D?t=1531799314639.
     *                You just need 'FKX03108APKA1JNP5ZCE5D?t=1531799314639'.
     * @return If return true is mean activity open success.
     */
    public static boolean openAlipayTransfer(Context context, String urlCode) {
        return startIntentUrl(context, ALIPAY_INTENT_URL_FORMAT.replace("{urlCode}", urlCode));
    }

    /**
     * Open scan QRCode activity
     *
     * @param context Context
     * @return If return true is mean activity open success.
     */
    @SuppressWarnings("WrongConstant")
    public static boolean openScanQRCode(Context context, String packageName) {
        if (TextUtils.equals(ALIPAY_PACKAGE_NAME, packageName)) {
            return openAliPaySA(context, ALIPAY_SCAN_QR_CODE_SAID);
        } else if (TextUtils.equals(WECHAT_PACKAGE_NAME, packageName)) {
            try {
                Intent intent = new Intent();
                intent.setComponent(new ComponentName(WECHAT_PACKAGE_NAME, "com.tencent.mm.ui.LauncherUI"));
                intent.putExtra("LauncherUI.From.Scaner.Shortcut", true);
                intent.setFlags(335544320);
                intent.setAction("android.intent.action.VIEW");
                context.startActivity(intent);
                return true;
            } catch (Exception e) {
                return false;
            }
        } else {
            return false;
        }
    }

    /**
     * @param context Context
     * @param saId    Alipay sub assembly id.
     * @return If return true is mean activity open success.
     */
    public static boolean openAliPaySA(Context context, String saId) {
        try {
            Uri uri = Uri.parse(ALIPAY_SA_SCHEME.replace("{saId}", saId));
            Intent intent = new Intent(Intent.ACTION_VIEW, uri);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N && context instanceof TileService) {
                ((TileService) context).startActivityAndCollapse(intent);
            } else {
                context.startActivity(intent);
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Open uri scheme
     *
     * @param context Context
     * @param uri     Intent uri
     * @return If return true is mean activity open success.
     */
    public static boolean startIntentUrl(Context context, String uri) {
        try {
            Intent intent = Intent.parseUri(uri, Intent.URI_INTENT_SCHEME);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
            return true;
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return false;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * To determine whether the pay client has been installed, it is recommended to check before do transfer.
     *
     * @param context Context
     * @return If return true is mean pay client already install.
     */
    public static boolean isPayClientInstalled(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            return info != null;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Get pay client version code
     *
     * @param context Context
     * @return Pay client version code
     */
    public static String getPayClientVersion(Context context, String packageName) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo info = pm.getPackageInfo(packageName, 0);
            return info.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

}
