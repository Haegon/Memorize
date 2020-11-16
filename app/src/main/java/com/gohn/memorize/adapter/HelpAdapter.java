package com.gohn.memorize.adapter;

import android.content.Context;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.gohn.memorize.R;

import java.util.List;


public class HelpAdapter extends RecyclerView.Adapter<HelpAdapter.SimpleViewHolder> {

    private final Context context;
    private final List<Integer> items;
    private final FragmentManager fragmentManager;

    public static class SimpleViewHolder extends RecyclerView.ViewHolder {

        public final ViewGroup parentView;

        public SimpleViewHolder(View view) {
            super(view);
            parentView = (ViewGroup) view.findViewById(R.id.scroll_help_content);
        }
    }

    public HelpAdapter(Context context, List<Integer> items, FragmentManager fm) {
        this.context = context;
        this.items = items;
        this.fragmentManager = fm;
    }

    public void setItems(List<Integer> items) {
        this.items.clear();
        this.items.addAll(items);
        notifyDataSetChanged();
    }

    public void addItem(Integer item) {
        this.items.add(item);
        notifyDataSetChanged();
    }

    public void removeItem(int position) {
        items.remove(position);
        notifyItemRemoved(position);
    }

    @Override
    public SimpleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        final View view = LayoutInflater.from(context).inflate(R.layout.list_help, parent, false);
        return new SimpleViewHolder(view);
    }

    @Override
    public void onBindViewHolder(final SimpleViewHolder holder, final int position) {

        final Integer layoutId = items.get(position);

        View newView = LayoutInflater.from(context).inflate(layoutId, null);
        holder.parentView.removeAllViews();
        holder.parentView.addView(newView);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }
}
