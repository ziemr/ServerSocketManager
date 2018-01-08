package com.android.serversocket.socket;

import java.util.List;

import com.android.serversocket.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ShowIPtAdapter extends BaseAdapter {
	private Context context;
	private List<String> listData;

	public ShowIPtAdapter(Context context) {
		this.context = context;
	}

	public void bindData(List<String> listData) {
		this.listData = listData;
	}
	public void addData(String data){
		this.listData.add(data);
	}
	public void removeData(String data){
		this.listData.remove(data);
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return this.listData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return this.listData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View converView, ViewGroup arg2) {
		Holder mHolder = null;
		if (converView == null) {
			LayoutInflater inflater = (LayoutInflater) context
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
			converView = inflater.inflate(R.layout.item, arg2, false);
			mHolder = new Holder();
			
			mHolder.tv_content = (TextView) converView
					.findViewById(R.id.tv_content);
			converView.setTag(mHolder);
		} else {
			mHolder = (Holder) converView.getTag();
		}
	
		mHolder.tv_content.setText(listData.get(position));
		return converView;
	}

	static class Holder {	
		public TextView tv_content;// Ã· æƒ⁄»›1
	}
}
