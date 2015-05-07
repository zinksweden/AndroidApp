package com.example.adam.tentaonline;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.Iterator;

/**
 * Created by Adam on 2015-05-04.
 */
public class CustomSpinnerAdapter extends BaseAdapter {

    Context context;
    Integer[] emoticons;

    public CustomSpinnerAdapter(Context context,Integer[] items) {
        super();
        this.context=context;
        emoticons=items;
    }

    @Override
    public int getCount() {
        return emoticons.length;
    }

    @Override
    public Object getItem(int position) {
        return emoticons[position];
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        View itemView = convertView;
        ViewHolder emoticonViewHolder;

        if(convertView==null){
            LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
            itemView =inflater.inflate(R.layout.spinner_row, parent, false);

            emoticonViewHolder = new ViewHolder();
            emoticonViewHolder.imageViewIcon = (ImageView) itemView.findViewById(R.id.spinnerImage);

            itemView.setTag(emoticonViewHolder);
        }
        else{
            emoticonViewHolder = (ViewHolder) itemView.getTag();
        }

        emoticonViewHolder.imageViewIcon.setImageDrawable(context.getResources().getDrawable(emoticons[position]));

        return itemView;
    }
}
