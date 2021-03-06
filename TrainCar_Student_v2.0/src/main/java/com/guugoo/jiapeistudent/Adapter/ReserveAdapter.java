package com.guugoo.jiapeistudent.Adapter;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;

import com.guugoo.jiapeistudent.Data.Reserve;
import com.guugoo.jiapeistudent.MinorActivity.CommentActivity;
import com.guugoo.jiapeistudent.MinorActivity.CommentPayActivity;
import com.guugoo.jiapeistudent.MinorActivity.ReserveDetailsActivity;
import com.guugoo.jiapeistudent.MinorActivity.TwoDimensionalActivity;
import com.guugoo.jiapeistudent.R;
import com.guugoo.jiapeistudent.Tools.Utils;

import java.util.List;

/**
 * Created by Administrator on 2016/8/9.
 */
public class ReserveAdapter extends BaseAdapter {
    private static final String TAG = "ReserveAdapter";
    private int resourceId;
    private  Context context;
    private Activity activity;
    private List<Reserve> list;
    public ReserveAdapter(int resourceId, Context context, List<Reserve> list) {
        this.resourceId = resourceId;
        this.activity = (Activity) context;
        this.context = context;
        this.list = list;
    }
    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        ViewHolder viewHolder;
        if (null == convertView) {
            convertView = View.inflate(context, resourceId, null);
            viewHolder = new ViewHolder();
            viewHolder.BookingsTime = (TextView) convertView.findViewById(R.id.adapter_reserve_text1);
//            viewHolder.Amount = (TextView) convertView.findViewById(R.id.adapter_reserve_text2);
            viewHolder.name = (TextView) convertView.findViewById(R.id.adapter_reserve_text3);
            viewHolder.Status = (TextView) convertView.findViewById(R.id.adapter_reserve_text4);
            viewHolder.Branch = (TextView) convertView.findViewById(R.id.adapter_reserve_text5);
            viewHolder.Subject = (TextView) convertView.findViewById(R.id.adapter_reserve_text6);
            viewHolder.button1 = (Button) convertView.findViewById(R.id.adapter_reserve_button1);
            viewHolder.button2 = (Button) convertView.findViewById(R.id.adapter_reserve_button2);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        final Reserve data = list.get(position);
        if(!TextUtils.isEmpty(data.getTimeSlot())&&!TextUtils.isEmpty(data.getBookingDay())){
            String[] time = data.getBookingDay().split("-");
            viewHolder.BookingsTime.setText(time[0]+"年"+time[1]+"月"+time[2]+"日"+"\n"+data.getTimeSlot());
        }
        viewHolder.Branch.setText(data.getBranch());
        viewHolder.name.setText(data.getTeacher());
        switch (data.getStatus()) {
            case 0:
                viewHolder.Status.setText(R.string.my_reserve_content_text1);
                viewHolder.button2.setVisibility(View.VISIBLE);
                viewHolder.button2.setText(R.string.my_reserve_content_text6);
                break;
            case 1:
                viewHolder.Status.setText(R.string.my_reserve_content_text2);
                viewHolder.button2.setVisibility(View.GONE);
                /*
                if(list.get(position).getFeeItem()!=-1){
                    viewHolder.button2.setVisibility(View.VISIBLE);
                    viewHolder.button2.setText(R.string.my_reserve_content_text7);
                }
                */
                break;
            case 2:
                viewHolder.Status.setText(R.string.my_reserve_content_text4);
                viewHolder.button2.setVisibility(View.VISIBLE);
                if (data.getIsEvaluate() == 0) {
                    viewHolder.button2.setText(R.string.my_reserve_content_text7);
                } else {
                    viewHolder.button2.setText(R.string.my_reserve_content_text8);
                }
                break;
            case -1:
                viewHolder.Status.setText(R.string.my_reserve_content_text3_1);
                viewHolder.button2.setVisibility(View.GONE);
                break;
            case -2:
                viewHolder.Status.setText(R.string.my_reserve_content_text3_2);
                viewHolder.button2.setVisibility(View.GONE);
                break;
            case -3:
                viewHolder.Status.setText(R.string.my_reserve_content_text3_3);
                viewHolder.button2.setVisibility(View.GONE);
                break;
            case -4:
                viewHolder.Status.setText(R.string.my_reserve_content_text3_4);
                viewHolder.button2.setVisibility(View.GONE);
                break;

        }
        viewHolder.Subject.setText(Utils.getSubject(data.getSubject()));
        viewHolder.button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, ReserveDetailsActivity.class);
                intent.putExtra("RefId", data.getRefId());
                intent.putExtra("BookingId", data.getBookingId());
                intent.putExtra("PayType", data.getFeeItem());
                intent.putExtra("Status", data.getStatus());
                activity.startActivityForResult(intent,1);
            }
        });
        viewHolder.button2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (list.get(position).getStatus()){
                        case 0:
                            Intent intent1 = new Intent(context, TwoDimensionalActivity.class);
                            intent1.putExtra("BookingId",list.get(position).getBookingId());
                            intent1.putExtra("TeacherId",list.get(position).getTeacherId());
                            intent1.putExtra("BookingDay",list.get(position).getBookingDay());
                            intent1.putExtra("TimeSlot",list.get(position).getTimeSlot());
                            context.startActivity(intent1);
                            break;
                        case 2:
                            Intent intent = new Intent(context, CommentActivity.class);
                            intent.putExtra("BookingId",list.get(position).getBookingId());
                            intent.putExtra("FeeItem",list.get(position).getFeeItem());
                            intent.putExtra("ActMinute",list.get(position).getActMinute());
                            intent.putExtra("MinuteFee",list.get(position).getMinuteFee());
                            context.startActivity(intent);
                            break;
                    }

                }
        });
        return convertView;
    }
    class ViewHolder {
        private TextView BookingsTime;
//        private TextView Amount;
        private TextView name;
        private TextView Status;
        private TextView Branch;
        private TextView Subject;
        private Button button1;
        private Button button2;
    }

}
