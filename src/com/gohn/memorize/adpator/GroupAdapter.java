package com.gohn.memorize.adpator;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.gohn.memorize.R;
import com.gohn.memorize.extention.TextViewEx;
import com.gohn.memorize.model.VocaGroup;

public class GroupAdapter extends BaseAdapter {

	Context mContext = null;
	public ArrayList<VocaGroup> mData = null;
	LayoutInflater mLayout = null;
	String word;

	class ViewHolder {
		TextViewEx mNameTv;
		TextViewEx mNumbersTv;
	}

	public GroupAdapter(Context context, ArrayList<VocaGroup> data, String w) {
		mContext = context;
		mData = data;
		word = w;
		mLayout = LayoutInflater.from(mContext);
	}

	public void add(int index, VocaGroup addData) {
		mData.add(index, addData);
		notifyDataSetChanged();
	}

	public void delete(int index) {
		mData.remove(index);
		notifyDataSetChanged();
	}

	public void clear() {
		mData.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return mData.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return mData.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub

		View itemLayout = convertView;
		ViewHolder viewHolder = null;

		if (itemLayout == null) {
			itemLayout = mLayout.inflate(R.layout.group_list_layout, null);

			viewHolder = new ViewHolder();

			viewHolder.mNameTv = (TextViewEx) itemLayout.findViewById(R.id.group_name_text);
			viewHolder.mNumbersTv = (TextViewEx) itemLayout.findViewById(R.id.group_numbers_text);

			itemLayout.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) itemLayout.getTag();
		}

		viewHolder.mNameTv.setText(mData.get(position).Name);
		viewHolder.mNumbersTv.setText(mData.get(position).Numbers + " " + word);
		return itemLayout;
	}
}
