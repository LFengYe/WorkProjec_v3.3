package cn.com.goodsowner.activity;

import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import cn.com.goodsowner.R;
import cn.com.goodsowner.base.BaseActivity;


/**
 * Created by LFeng on 2017/6/19.
 */

public class RegisterProtocolActivity extends BaseActivity {

    private TextView tv_title;
    private TextView tv_right;
    private ImageView iv_left_white;
    private TextView tv_msgtitle;
    private TextView tv_content;

    @Override
    protected int getLayout() {
        return R.layout.activity_register_protocol;
    }

    @Override
    protected void findById() {
        RelativeLayout rl_head = (RelativeLayout) findViewById(R.id.in_head);
        assert rl_head != null;
        tv_title = (TextView) rl_head.findViewById(R.id.tv_title);
        tv_right = (TextView) rl_head.findViewById(R.id.tv_right);
        iv_left_white = (ImageView) rl_head.findViewById(R.id.iv_left_white);

        tv_content = (TextView) findViewById(R.id.tv_content);
        tv_msgtitle = (TextView) findViewById(R.id.tv_msgtitle);
    }

    @Override
    protected void initData() {

    }

    @Override
    protected void initView() {
        tv_title.setText(R.string.register_protocol_title);
        tv_right.setVisibility(View.GONE);
        iv_left_white.setOnClickListener(this);

        tv_content.setText("注册前请认真阅读注册协议");
        tv_msgtitle.setText(R.string.register_protocol_title);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.iv_left_white:
                finish();
                break;
        }
    }
}
