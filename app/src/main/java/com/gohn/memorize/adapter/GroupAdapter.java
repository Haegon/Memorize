package com.gohn.memorize.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.model.VocaGroup;

import java.util.ArrayList;

public class GroupAdapter extends BaseAdapter {

	Context mContext = null;
	public ArrayList<VocaGroup> mData = null;
	LayoutInflater mLayout = null;
	String word;

	class ViewHolder {
		TextView mNameTv;
		TextView mNumbersTv;
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
			itemLayout = mLayout.inflate(R.layout.list_group, null);

			viewHolder = new ViewHolder();

			viewHolder.mNameTv = (TextView) itemLayout.findViewById(R.id.group_name_text);
			viewHolder.mNumbersTv = (TextView) itemLayout.findViewById(R.id.group_numbers_text);

			itemLayout.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) itemLayout.getTag();
		}

		viewHolder.mNameTv.setText(mData.get(position).getName());
		viewHolder.mNumbersTv.setText(mData.get(position).getNumbers() + " " + word);
		return itemLayout;
	}
}
