package com.gohn.memorize.adpator;

import java.io.File;
import java.util.ArrayList;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.extention.TextViewEx;

public class FindFileAdapter extends BaseAdapter {

	Context mContext = null;
	public ArrayList<File> mData = null;
	LayoutInflater mLayout = null;

	class ViewHolder {
		TextViewEx mNameTv;
		TextViewEx mNumberTv;
		TextViewEx mDepartmentTv;
		ImageView mIcon;
	}

	public FindFileAdapter(Context context, ArrayList<File> data) {
		mContext = context;
		mData = data;
		mLayout = LayoutInflater.from(mContext);
	}

	public void add(int index, File addData) {
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
			itemLayout = mLayout.inflate(R.layout.find_file_list_layout, null);

			viewHolder = new ViewHolder();

			viewHolder.mNameTv = (TextViewEx) itemLayout.findViewById(R.id.find_file_name_text);
			viewHolder.mIcon = (ImageView) itemLayout.findViewById(R.id.find_file_dir);

			itemLayout.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) itemLayout.getTag();
		}

		if (position == 0) {
			viewHolder.mNameTv.setText(".. 상위 폴더로");
		} else {
			viewHolder.mNameTv.setText(mData.get(position).getName());
		}
		if (mData.get(position).isDirectory()) {
			viewHolder.mNameTv.setTextColor(Color.BLACK);
			viewHolder.mIcon.setImageResource(R.raw.dir);
		} 
		if (mData.get(position).isFile()) {
			File file = mData.get(position);
			String ext = file.getName().substring(file.getName().lastIndexOf("."));
			if (ext.contains(".xls") && !ext.contains(".xlsx")) {
				viewHolder.mNameTv.setTextColor(Color.BLACK);
				viewHolder.mIcon.setImageResource(R.raw.excel);
			} else {
				viewHolder.mNameTv.setTextColor(Color.GRAY);
			}
		}

		return itemLayout;
	}
}
