package cn.guugoo.jiapeistudent.Fragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.text.TextUtils;
import android.text.format.Time;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.alibaba.fastjson.JSONObject;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import cn.guugoo.jiapeistudent.App.Constant;
import cn.guugoo.jiapeistudent.Data.Booking;
import cn.guugoo.jiapeistudent.Data.ReserveTime;
import cn.guugoo.jiapeistudent.Data.ReturnData;
import cn.guugoo.jiapeistudent.Interface.TimeRefreshListenter;
import cn.guugoo.jiapeistudent.MinorActivity.ReserveVerifyActivity;

import cn.guugoo.jiapeistudent.R;
import cn.guugoo.jiapeistudent.Tools.DES;
import cn.guugoo.jiapeistudent.Tools.MyHandler;
import cn.guugoo.jiapeistudent.Tools.MyThread;
import cn.guugoo.jiapeistudent.Tools.MyToast;
import cn.guugoo.jiapeistudent.Tools.Utils;
import cn.guugoo.jiapeistudent.Views.CHScrollView2;

/**
 * Created by Administrator on 2016/8/8.
 */
public class TimeFragment extends Fragment implements TimeRefreshListenter {
    private static final String TAG = "TimeFragment";
    private View fragmentView;
    private SharedPreferences sp;
    private LinearLayout ll_day;
    private int width;//屏幕宽度
    private String[] crossTitles; //横向标题

    private String[] bookingLists; //表格中的内容
    private List<TextView> textViews,contenttextViews;

    private Boolean day=false;
    private int Type;
    private int BranchId = 0;
    private Handler handler;
    public static Paint BLACK_PAINT = new Paint();
    static {
        BLACK_PAINT.setColor(Color.BLACK);
    }
    private ListView listView;
    public HorizontalScrollView mTouchView;
    private List<CHScrollView2> mHScrollViews = new ArrayList<CHScrollView2>();

    private String[] cols = new String[]{"6:00", "7:00", "8:00", "9:00", "10:00",
            "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00",
            "19:00", "20:00", "21:00", "22:00"};

    private ScrollAdapter mAdapter;
    private int hourWidth = 200;

    HashMap<String, ArrayList<Booking>> data;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentView = inflater.inflate(R.layout.fragment_time, container, false);
        findById();
        handler = new MyHandler(this.getActivity()) {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                Log.d(TAG, "handleMessage: " + msg.obj);
                if (msg.what == 1) {
                    ReturnData data = JSONObject.parseObject((String) msg.obj, ReturnData.class);
                    if (data.getStatus() == 0) {
                        Log.d(TAG, "handleMessage: " + data.getData());
                        ReserveTime reserveTime = JSONObject.parseObject(data.getData(), ReserveTime.class);
                        if (!TextUtils.isEmpty(reserveTime.getTimeCoordinate()) && !TextUtils.isEmpty(reserveTime.getBookingList())) {
                            Log.d(TAG, "handleMessage: " + "in");

                            crossTitles = reserveTime.getTimeCoordinate().split("#");
                            bookingLists = reserveTime.getBookingList().split("#");
                            if(day){
                                createView(); //画界面
                                AddListener(); // 添加监听
                            }
                            getBookData(bookingLists);
                        }
                    } else {
                        MyToast.makeText(getContext(), data.getMessage());
                    }
                }
            }
        };
        init();
        return fragmentView;
    }

    public void getBookData(String[] bookingLists){
        if (data == null)
            data = new HashMap<>();
        else data.clear();

        for (int i = 0; i < bookingLists.length; i++) {
            String[] items = bookingLists[i].split(",");//分割课程信息
            if (items.length < 16) {
                System.out.println(items + "数据格式错误!");
                continue;
            }
            Booking booking = new Booking();
            booking.setBookingCode(items[0]);
            booking.setSchoolCode(items[1]);
            booking.setLocationCode(items[2]);
            booking.setLocationName(items[3]);
            booking.setTeacherCode(items[4]);
            booking.setTeacherName(items[5]);
            booking.setTeacherTel(items[6]);
            booking.setCardNo(items[7]);
            booking.setCourseDate(items[8]);
            booking.setCourseTime(items[9]);
            booking.setSubject(items[10]);
            booking.setBookingSum(Integer.valueOf(items[11]));
            booking.setBookAmount(Integer.valueOf(items[12]));
            booking.setWeekStr(items[13]);
            booking.setDateStr(items[14]);
            booking.setStatus(Integer.valueOf(items[15]));

            //判断HashMap中是否存在该教练编号的key(courseItem[5]表示教练编号的值, 这里我随便写的)
            if (data.containsKey(booking.getTeacherCode())) {
                data.get(booking.getTeacherCode()).add(booking);
            } else {
                ArrayList<Booking> bookingList = new ArrayList<Booking>();
                bookingList.add(booking);
                data.put(booking.getTeacherCode(), bookingList);
            }
        }

        mAdapter = new ScrollAdapter(getActivity(), data, R.layout.common_item_my_hlistview);
        listView.setAdapter(mAdapter);

    }


    private void findById() {

        ll_day = (LinearLayout) fragmentView.findViewById(R.id.layout_time_day);

        sp = getActivity().getSharedPreferences("user", Context.MODE_PRIVATE);
        Bundle bundle = getArguments();
        if (bundle != null) {
            Type = bundle.getInt("type");
            switch (Type) {
                case 0:
                    break;
                case 1:
                    BranchId = bundle.getInt("BranchId");
                    break;
            }
        }

        CHScrollView2 headerScroll = (CHScrollView2) fragmentView.findViewById(R.id.item_scroll_title);
        LinearLayout layout = (LinearLayout) fragmentView.findViewById(R.id.item_scroll_title_content);
        //layout.removeAllViews();
        for (int i = 0; i < cols.length; i++) {
            MyTextView textView = new MyTextView(getActivity());
            textView.setText(cols[i]);
            textView.setTextColor(Color.BLACK);
            textView.setGravity(Gravity.CENTER);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    hourWidth, LinearLayout.LayoutParams.MATCH_PARENT);
            layout.addView(textView, layoutParams);
        }
        //添加头滑动事件
        mHScrollViews.add(0, headerScroll);
        listView = (ListView) fragmentView.findViewById(R.id.hlistview_scroll_list);

    }

    private void init() {
        textViews = new ArrayList<TextView>();
        contenttextViews = new ArrayList<TextView>();
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = formatter.format(d);
        getTimeTable(dateNowStr,true);
    }
    private void AddListener() {
        final Iterator<TextView> iterator = textViews.iterator();
        while (iterator.hasNext()) {
            final TextView textView = iterator.next();

            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    MyToast.makeText(getContext(), textView.getText().toString());
                    Iterator<TextView> iterator1 = textViews.iterator();
                    while (iterator1.hasNext()){
                        TextView textView = iterator1.next();
                        Log.d(TAG,textView.getText().toString());
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_black));
                    }
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.login_color));
                    String s[]= textView.getText().toString().split("\n");
                    Time t=new Time();
                    t.setToNow(); // 取得系统时间。
                    getTimeTable(t.year+"-"+s[0],false);

                }
            });
        }

    }

    public void addcontentLister(){
        final Iterator<TextView> iterator_content = contenttextViews.iterator();
        while (iterator_content.hasNext()) {
            final TextView textView = iterator_content.next();

            final Booking booking = (Booking) textView.getTag();
            textView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getActivity(),ReserveVerifyActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("BookingDay",booking.getCourseDate());
                    bundle.putString("TimeSlot",booking.getCourseTime());
                    bundle.putString("Branch",booking.getLocationName());
                    bundle.putString("Name",booking.getTeacherName());
                    bundle.putString("TeacherId",booking.getTeacherCode());
                    bundle.putString("Tel",booking.getTeacherTel());
                    bundle.putString("VehNof",booking.getCardNo());
                    intent.putExtras(bundle);
                    startActivity(intent);

                }
            });
        }
    }

    private void createView() {
        DisplayMetrics metric = new DisplayMetrics();
        getActivity().getWindowManager().getDefaultDisplay().getMetrics(metric);
        width = metric.widthPixels;

        Log.d(TAG, "createView: " + crossTitles.length);
        for (int i = 0; i <= crossTitles.length; i++) {
            TextView textView = new TextView(getContext());
            LinearLayout.LayoutParams tv_params = new LinearLayout.LayoutParams(
                    0, LinearLayout.LayoutParams.WRAP_CONTENT, 1);
            if (i == 0) {
                textView.setText("");
            } else {
                Log.d(TAG, "text: " + crossTitles[i-1]);
                textView.setGravity(Gravity.CENTER);
                textView.setText(crossTitles[i - 1].replace(",", "\n"));
                textView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                if (i==1){
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.login_color));
                }else {
                    textView.setTextColor(ContextCompat.getColor(getContext(), R.color.text_black));
                }

            }
            textView.setTag(i);
            textViews.add(textView);
            ll_day.addView(textView, tv_params);

        }
    }

    private void getTimeTable(String QueryTime,boolean today) {

        day=today;
        if (Utils.isNetworkAvailable(getContext())) {
            JSONObject json = new JSONObject(true);
            switch (Type) {
                case 0:
                    json.put("BranchId", 0);
                    json.put("Type", "SJ");
                    break;
                case 1:
                    json.put("BranchId", BranchId);
                    json.put("Type", "CD");
                    break;
            }
            json.put("StudentId", sp.getInt("Id", 0));
            json.put("Subject", sp.getInt("CurrentSubject", 0));
            json.put("SchoolId", sp.getInt("SchoolId", 0));
            json.put("TeacherId", 0);
            json.put("QueryTime", QueryTime);

            new MyThread(Constant.URL_Timetable, handler, DES.encryptDES(json.toString())).start();

        } else {
            MyToast.makeText(getContext(), R.string.Toast_internet);
        }

    }

    @Override
    public void onMainAction() {
        ll_day.removeAllViews();
        Date d = new Date();
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        String dateNowStr = formatter.format(d);
        getTimeTable(dateNowStr,true);
    }


    class ScrollAdapter extends BaseAdapter {

        private HashMap<String, ArrayList<Booking>> data;
        private List<String> listKey;
        private Context context;
        private int res;
        private String maxTime;

        public ScrollAdapter(Context context, HashMap<String, ArrayList<Booking>> data, int res) {
            this.context = context;
            this.data = data;
            this.res = res;
            listKey = new ArrayList<String>();
            Iterator<String> iterator = data.keySet().iterator();
            while (iterator.hasNext()) {
                listKey.add(iterator.next());
            }
        }

        @Override
        public int getCount() {
            if (listKey == null)
                return 0;
            return listKey.size();
        }

        @Override
        public Object getItem(int position) {
            if (data == null)
                return null;
            return data.get(listKey.get(position));
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            convertView = LayoutInflater.from(context).inflate(res, null);

            ViewHolder holder = new ViewHolder();
            holder.itemTitleV = (TextView) convertView.findViewById(R.id.item_titlev);
            holder.itemChScroll = (CHScrollView2) convertView.findViewById(R.id.item_chscroll_scroll);
            holder.itemLayout = (RelativeLayout) convertView.findViewById(R.id.item_chscroll_scroll_content);
            holder.itemLayout.removeAllViews();

            ArrayList<Booking> bookings = data.get(listKey.get(position));
            holder.itemTitleV.setText(bookings.get(0).getTeacherName());


            for (Booking booking : bookings) {
                String[] times = booking.getCourseTime().split("-");
                if (maxTime == null || times[1].compareTo(maxTime) > 0) {
                    maxTime = times[1];
                }

                MyTextView textView = new MyTextView(context);
                switch (booking.getStatus()){
                    case -1:
                        textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table));
                        textView.setGravity(Gravity.CENTER);
                        textView.setText("已过期");
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    case 0:
                        textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.table));
                        textView.setGravity(Gravity.CENTER);
                        textView.setText("已约满");
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        break;
                    default:
                        textView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.login_color));
                        textView.setGravity(Gravity.CENTER);
                        if (getWidth(times[0], times[1]) < 100) {
                            textView.setText(booking.getBookAmount()+"/"+booking.getBookingSum());
                        } else {
                            textView.setText("已约/总数"+"\n"+booking.getBookAmount()+"/"+booking.getBookingSum());
                        }
                        textView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                        textView.setTag(booking);
                        contenttextViews.add(textView);
                        break;
                }

                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        getWidth(times[0], times[1]), RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(getWidth("6:00", times[0]), 1, 1, 1);
                holder.itemLayout.addView(textView, layoutParams);
            }

            if (maxTime.compareTo("23:00") < 0) {
                TextView textView = new TextView(context);
                RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(
                        getWidth(maxTime, "23:00"), RelativeLayout.LayoutParams.MATCH_PARENT);
                layoutParams.setMargins(getWidth("6:00", maxTime), 1, 1, 1);
                holder.itemLayout.addView(textView, layoutParams);
            }

            addcontentLister();
            addHViews(holder.itemChScroll, position + 1);
            return convertView;
        }
    }

    public void addHViews(final CHScrollView2 hScrollView, int index) {
        if (!mHScrollViews.isEmpty()) {
            int size = mHScrollViews.size();
            CHScrollView2 scrollView = mHScrollViews.get(size - 1);
            final int scrollX = scrollView.getScrollX();
            //第一次满屏后，向下滑动，有一条数据在开始时未加入
            if (scrollX != 0) {
                listView.post(new Runnable() {
                    @Override
                    public void run() {
                        //当listView刷新完成之后，把该条移动到最终位置
                        hScrollView.scrollTo(scrollX, 0);
                    }
                });
            }
        }
        mHScrollViews.add(index, hScrollView);
    }

    class ViewHolder {
        public TextView itemTitleV;
        public CHScrollView2 itemChScroll;
        public RelativeLayout itemLayout;
    }

    class MyTextView extends TextView {

        public MyTextView(Context context) {
            super(context);
        }

        @Override
        protected void onDraw(Canvas canvas) {
            super.onDraw(canvas);

            canvas.drawLine(this.getWidth() - 1, 0, this.getWidth() - 1,
                    this.getHeight() - 1, BLACK_PAINT);

        }
    }

    private int getWidth(String startTime, String endTime) {
        String[] startStr = startTime.split(":");
        String[] endStr = endTime.split(":");
        int minutes = (Integer.valueOf(endStr[0]) - Integer.valueOf(startStr[0])) * 60
                + (Integer.valueOf(endStr[1]) - Integer.valueOf(startStr[1]));
        return minutes * hourWidth / 60;
    }

    public void onScrollChanged(int l, int t, int oldl, int oldt){
        for(CHScrollView2 scrollView : mHScrollViews) {
            //防止重复滑动
            if(mTouchView != scrollView)
                scrollView.smoothScrollTo(l, t);
        }
    }
}
