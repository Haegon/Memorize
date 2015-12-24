package com.gohn.memorize.adapter;

import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.gohn.memorize.R;
import com.gohn.memorize.common.CommonData;

import java.io.File;
import java.util.ArrayList;

public class FindFileAdapter extends BaseAdapter {

	Context context = null;
	public ArrayList<File> items = null;
	LayoutInflater layout = null;

	class ViewHolder {
		TextView tvName;
		ImageView imgIcon;
	}

	public FindFileAdapter(Context context, ArrayList<File> items) {
		this.context = context;
		this.items = items;
		this.layout = LayoutInflater.from(this.context);
	}

	public void add(int index, File addData) {
		items.add(index, addData);
		notifyDataSetChanged();
	}

	public void delete(int index) {
		items.remove(index);
		notifyDataSetChanged();
	}

	public void clear() {
		items.clear();
		notifyDataSetChanged();
	}

	@Override
	public int getCount() {
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {

		View itemLayout = convertView;
		ViewHolder viewHolder = null;

		if (itemLayout == null) {
			itemLayout = layout.inflate(R.layout.list_file, null);

			viewHolder = new ViewHolder();

			viewHolder.tvName = (TextView) itemLayout.findViewById(R.id.txt_file_name);
			viewHolder.imgIcon = (ImageView) itemLayout.findViewById(R.id.txt_file_image);

			itemLayout.setTag(viewHolder);
		} else {
			viewHolder = (ViewHolder) itemLayout.getTag();
		}

		if (position == 0) {
			viewHolder.tvName.setText(R.string.list_parent_folder);
		} else {
			viewHolder.tvName.setText(items.get(position).getName());
		}
		if (items.get(position).isDirectory()) {
			viewHolder.tvName.setTextColor(Color.BLACK);
			viewHolder.imgIcon.setImageBitmap(CommonData.getBitmap(context, R.drawable.ic_directory));
		}
		if (items.get(position).isFile()) {
			File file = items.get(position);
			String ext = file.getName().substring(file.getName().lastIndexOf("."));
			Log.d("gohn", ext);
			if (ext.contains(".xls") || ext.contains(".xlsx") || ext.contains(".csv")) {
				viewHolder.tvName.setTextColor(Color.BLACK);
				viewHolder.imgIcon.setImageBitmap(CommonData.getBitmap(context, R.drawable.ic_excel));
			} else {
				viewHolder.tvName.setTextColor(Color.GRAY);
			}
		}

		return itemLayout;
	}
}
