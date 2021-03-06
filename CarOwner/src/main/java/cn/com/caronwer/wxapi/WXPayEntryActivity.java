package cn.com.caronwer.wxapi;


import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.tencent.mm.sdk.constants.ConstantsAPI;
import com.tencent.mm.sdk.modelbase.BaseReq;
import com.tencent.mm.sdk.modelbase.BaseResp;
import com.tencent.mm.sdk.openapi.IWXAPI;
import com.tencent.mm.sdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.sdk.openapi.WXAPIFactory;

import cn.com.caronwer.R;
import cn.com.caronwer.base.Contants;


public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pay_result);
        api = WXAPIFactory.createWXAPI(this, Contants.weChatAPPID);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {

    }

    @Override
    public void onResp(BaseResp resp) {
        Log.d(TAG, "onPayFinish, errCode = " + resp.errCode);

        if (resp.errCode == 0) {
            Toast.makeText(WXPayEntryActivity.this, "支付成功", Toast.LENGTH_LONG).show();
            finish();
        } else if (resp.errCode == -1) {
            Toast.makeText(WXPayEntryActivity.this, "支付出错，已取消支付", Toast.LENGTH_LONG).show();
            //MyToast.makeText(WXPayEntryActivity.this, "支付出错，已取消支付");
            finish();
        } else if (resp.errCode == -2) {
            Toast.makeText(WXPayEntryActivity.this, "支付已经取消", Toast.LENGTH_LONG).show();
            //MyToast.makeText(WXPayEntryActivity.this, "支付已经取消");
            finish();
        }

        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle(R.string.app_id);
            builder.setMessage(getString(R.string.pay_result_callback_msg, String.valueOf(resp.errCode)));
            builder.show();
        }
    }
}