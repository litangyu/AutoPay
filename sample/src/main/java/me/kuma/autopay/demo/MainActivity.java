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

package me.kuma.autopay.demo;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import me.kuma.autopay.AutoPay;

/**
 * @author kuma <a href="mailto:lty81372860@gmail.com">Contact me.</a>
 */
public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = MainActivity.class.getSimpleName();

    private EditText etQRCode;
    private EditText etSAId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initView();
    }

    private void initView() {
        etQRCode = findViewById(R.id.et_qrcode);
        etSAId = findViewById(R.id.et_said);

        final Button btnAlipayTransfer = findViewById(R.id.btn_alipay_transfer);
        final Button btnAlipayBarcode = findViewById(R.id.btn_alipay_barcode);
        final Button btnAlipayScanner = findViewById(R.id.btn_alipay_scanner);
        final Button btnAlipaySubAssembly = findViewById(R.id.btn_alipay_sub_assembly);

        final Button btnWeChatScanner = findViewById(R.id.btn_wechat_scanner);

        btnAlipayTransfer.setOnClickListener(this);
        btnAlipayBarcode.setOnClickListener(this);
        btnAlipayScanner.setOnClickListener(this);
        btnAlipaySubAssembly.setOnClickListener(this);

        btnWeChatScanner.setOnClickListener(this);
    }

    @Override
    protected void onResume() {
        super.onResume();

        boolean alipayInstalled = AutoPay.isPayClientInstalled(this, AutoPay.ALIPAY_PACKAGE_NAME);
        boolean wechatInstalled = AutoPay.isPayClientInstalled(this, AutoPay.WECHAT_PACKAGE_NAME);
        String alipayVersion = AutoPay.getPayClientVersion(this, AutoPay.ALIPAY_PACKAGE_NAME);
        String wechatVersion = AutoPay.getPayClientVersion(this, AutoPay.WECHAT_PACKAGE_NAME);

        String alipayStatus = getString(R.string.client_status)
            .replace("{name}", AutoPay.ALIPAY_PACKAGE_NAME)
            .replace("{installed}", String.valueOf(alipayInstalled))
            .replace("{version}", alipayVersion == null ? "nil" : alipayVersion);

        String wechatStatus = getString(R.string.client_status)
            .replace("{name}", AutoPay.WECHAT_PACKAGE_NAME)
            .replace("{installed}", String.valueOf(wechatInstalled))
            .replace("{version}", wechatVersion == null ? "nil" : wechatVersion);

        final TextView tvAlipay = findViewById(R.id.tv_alipay_status);
        final TextView tvWeChat = findViewById(R.id.tv_wechat_status);
        tvAlipay.setText(alipayStatus);
        tvWeChat.setText(wechatStatus);
    }

    /**
     * Called when a view has been clicked.
     *
     * @param v The view that was clicked.
     */
    @Override
    public void onClick(View v) {
        boolean result;
        switch (v.getId()) {
            case R.id.btn_alipay_transfer:
                result = AutoPay.openAlipayTransfer(this, etQRCode.getText().toString().trim());
                Log.d(TAG, "Call method: openAlipayTransfer " + result);
                break;
            case R.id.btn_alipay_barcode:
                result = AutoPay.openAliPaySA(this, AutoPay.ALIPAY_BAR_CODE_SAID);
                Log.d(TAG, "Call method: openAliPaySA " + result);
                break;
            case R.id.btn_alipay_scanner:
                result = AutoPay.openScanQRCode(this, AutoPay.ALIPAY_PACKAGE_NAME);
                Log.d(TAG, "Call method: openScanQRCode " + result);
                break;
            case R.id.btn_alipay_sub_assembly:
                result = AutoPay.openAliPaySA(this, etSAId.getText().toString().trim());
                Log.d(TAG, "Call method: openAliPaySA " + result);
                break;
            case R.id.btn_wechat_scanner:
                result = AutoPay.openScanQRCode(this, AutoPay.WECHAT_PACKAGE_NAME);
                Log.d(TAG, "Call method: openScanQRCode " + result);
                break;
            default:
                break;
        }
    }

}
