package cn.com.caronwer.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.volley.VolleyError;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.util.Map;

import cn.com.caronwer.R;
import cn.com.caronwer.base.BaseActivity;
import cn.com.caronwer.base.Contants;
import cn.com.caronwer.util.EncryptUtil;
import cn.com.caronwer.util.HttpUtil;
import cn.com.caronwer.util.VolleyInterface;

public class XiugaimmActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_left_white;
    private EditText mEt_account;
    private EditText mEt_yuanpassword;
    private EditText mEt_xinpassword;
    private EditText mEt_zaipassword;
    private Button mBt_exit_xiugai;
    private String mAccount;
    private String mMAccount;
    private String mYuanpassword;
    private String mXinpassword;
    private String mZaipassword;


    @Override
    protected int getLayout() {
        return R.layout.activity_xiugaimm;
    }

    @Override
    protected void findById() {
        RelativeLayout rl_head = (RelativeLayout) findViewById(R.id.in_head);
        assert rl_head != null;
        tv_title = (TextView) rl_head.findViewById(R.id.tv_title);
        tv_right = (TextView) rl_head.findViewById(R.id.tv_right);
        iv_left_white = (ImageView) rl_head.findViewById(R.id.iv_left_white);

        mEt_account = (EditText) findViewById(R.id.et_account);
        mEt_yuanpassword = (EditText) findViewById(R.id.et_yuanpassword);
        mEt_xinpassword = (EditText) findViewById(R.id.et_xinpassword);
        mEt_zaipassword = (EditText) findViewById(R.id.et_zaipassword);
        mBt_exit_xiugai = (Button) findViewById(R.id.bt_exit_xiugai);


    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
//        tv_title.setText("修改密码");
        tv_title.setText(R.string.fee_scale);
        tv_right.setVisibility(View.GONE);
        mBt_exit_xiugai.setOnClickListener(this);
        iv_left_white.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {


        switch (v.getId()) {
            case R.id.iv_left_white:
                finish();
                break;
            case R.id.bt_exit_xiugai:
                mMAccount = mEt_account.getText().toString();
                mYuanpassword = mEt_yuanpassword.getText().toString();
                mXinpassword = mEt_xinpassword.getText().toString();
                mZaipassword = mEt_zaipassword.getText().toString();
                if (mMAccount.isEmpty() || mYuanpassword.isEmpty() || mXinpassword.isEmpty() || mZaipassword.isEmpty()) {
                    showShortToastByString("信息不完整");
                    return;
                } else if (!mZaipassword.equals(mXinpassword)) {
                    showShortToastByString("两次输入密码不同");
                } else {
                    JsonObject jsonObject = new JsonObject();

                    jsonObject.addProperty("Tel", mMAccount);

                    jsonObject.addProperty("PassWord", mYuanpassword);

                    jsonObject.addProperty("UserType", 2);

                    jsonObject.addProperty("NewPwd", mXinpassword);


                    Map<String, String> map = EncryptUtil.encryptDES(jsonObject.toString());


                    HttpUtil.doPost(XiugaimmActivity.this, Contants.url_editPassWord, "editPassWord", map, new VolleyInterface(XiugaimmActivity.this, VolleyInterface.mListener, VolleyInterface.mErrorListener) {
                        @Override
                        public void onSuccess(JsonElement result) {
                            showShortToastByString("修改成功");
                            /**
                             * 修改成功  退出程序登录
                             */
//                            finish();
                            XiugaimmActivity.super.removeAll();

                            SharedPreferences sp = getSharedPreferences(Contants.SHARED_NAME, MODE_PRIVATE);
                            sp.edit().putString("password", "").commit();
                            startActivity(new Intent(XiugaimmActivity.this, LoginActivity.class));

                        }

                        @Override
                        public void onError(VolleyError error) {
                            showShortToastByString(getString(R.string.timeoutError));
                        }

                        @Override
                        public void onStateError(int sta, String msg) {
                            if (!TextUtils.isEmpty(msg)) {
                                showShortToastByString(msg);
                            }
                        }
                    });
                    break;
                }

        }
    }
}
