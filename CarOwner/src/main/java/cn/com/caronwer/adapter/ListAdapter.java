package cn.com.caronwer.adapter;

import android.annotation.SuppressLint;
import android.app.AlertDialog.Builder;
import android.content.DialogInterface;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

import cn.com.caronwer.R;
import cn.com.caronwer.activity.WithdrawActivity;

@SuppressLint({ "ViewHolder", "InflateParams" })
public class ListAdapter extends BaseAdapter{

	private TextView list_text;
	private ImageView list_image;
	private List<String> listStr;
	private String str ; 
	public ListAdapter(List<String> listStr) {
		this.listStr = listStr;
	}
	
	@Override
	public int getCount() {
		return listStr.size();
	}

	@Override
	public Object getItem(int arg0) {
		return null;
	}

	@Override
	public long getItemId(int arg0) {
		return 0;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		convertView = LayoutInflater.from(WithdrawActivity.sWithdrawActivity).inflate(
					R.layout.listview_list, null);
		list_text = (TextView)convertView.findViewById(R.id.list_text);
		list_image = (ImageView)convertView.findViewById(R.id.list_image);
		list_text.setText(listStr.get(position));
		list_image.setTag(list_text.getText());
		list_image.setOnClickListener(new OnClickListener() {
			@Override
			public void onClick(View view) {
				str = view.getTag().toString();
				Builder dialog = new Builder(WithdrawActivity.sWithdrawActivity);
				dialog.setMessage("确认删除"+str+"吗？");
				dialog.setTitle("提示");
				dialog.setPositiveButton("确认", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						Toast.makeText(WithdrawActivity.sWithdrawActivity, "删除成功 ", Toast.LENGTH_LONG).show();
						WithdrawActivity.sWithdrawActivity.deleteRecord(str);
						WithdrawActivity.sWithdrawActivity.popupWindow.dismiss();
					}
				});
				dialog.setNegativeButton("取消", new DialogInterface.OnClickListener() {
					@Override
					public void onClick(DialogInterface arg0, int arg1) {
						arg0.dismiss();
					}
				});
				dialog.create().show();
			}
		});
		return convertView;
		
	}

}
