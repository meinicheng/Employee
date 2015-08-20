package com.sdbnet.hywy.employee.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

public class MyBaseAdapter<E> extends BaseAdapter {
	private List<E> mList = new ArrayList<E>();

	public MyBaseAdapter(List<E> list) {
		this.mList = list;
	}
	

	@Override
	public int getCount() {
		if (mList != null)
			return mList.size();
		else
			return 0;
	}

	@Override
	public Object getItem(int position) {
		if (mList != null)
			return mList.get(position);
		else
			return null;

	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		return null;
	}

}
