package com.gohn.memorize;

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

public class BaseAdapterEx extends BaseAdapter {

	Context mContext = null;
	ArrayList<File> mData = null;
	LayoutInflater mLayout = null;

	class ViewHolder {
		TextView mNameTv;
		TextView mNumberTv;
		TextView mDepartmentTv;
		ImageView mIcon;
	}

	public BaseAdapterEx(Context context, ArrayList<File> data) {
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
			itemLayout = mLayout.inflate(R.layout.list_view_item_layout, null);

			viewHolder = new ViewHolder();

			viewHolder.mNameTv = (TextView) itemLayout
					.findViewById(R.id.name_text);
			// viewHolder.mNumberTv = (TextView) itemLayout
			// .findViewById(R.id.number_text);
			// viewHolder.mDepartmentTv = (TextView) itemLayout
			// .findViewById(R.id.department_text);
			viewHolder.mIcon = (ImageView) itemLayout.findViewById(R.id.dir);

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
			viewHolder.mNameTv.setTextColor(Color.BLACK);
			viewHolder.mIcon.setImageResource(R.raw.go);
		}

		// viewHolder.mNumberTv.setText(mData.get(position).mNumber);
		// viewHolder.mDepartmentTv.setText(mData.get(position).mDepartment);

		return itemLayout;
	}
}
