package com.upad.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.upad.R;

import java.util.List;

public class SpinnerAdapt<T> extends BaseAdapter
{

    class ViewHolder {
        TextView name;
    }


    private List<T> mList;
    private final Context mContext;
    private final LayoutInflater mInflater;


    public void setmList(List<T> mList) {
        this.mList = mList;
    }

    public SpinnerAdapt(List<T> mList, Context mContext) {
        this.mList = mList;
        this.mContext = mContext;
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public T getItem(int i) {
        return mList.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        // TODO Auto-generated method stub
        ViewHolder viewHolder = null;
        if (view == null) {
            viewHolder = new ViewHolder();
            view = mInflater.inflate(R.layout.list_item_ltes, null);
            viewHolder.name = view.findViewById(R.id.ltes_textview_name);

            view.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) view.getTag();
        }
        viewHolder.name.setText(mList.get(i).toString());

        return view;
    }
}
