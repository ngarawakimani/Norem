package com.kd3developers.norem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by wa kimani on 11/6/2016.
 */

public class DataRecyclerAdapter extends RecyclerView.Adapter<DataRowHolder>{

    List<DataItems> listItem;
    Context mContext;

    public DataRecyclerAdapter(Context mContext , List<DataItems> listItem) {
        this.mContext = mContext;
        this.listItem = listItem;
    }

    @Override
    public DataRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.container_layout,null);
        DataRowHolder rowHolder = new DataRowHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(DataRowHolder holder, int position) {
        final DataItems dataItems = listItem.get(position);
        Picasso.with(mContext)
                .load("http://192.168.137.1/android/norem/photos/" + dataItems.getImage())
                .error(R.drawable.hostel)
                .placeholder(R.drawable.hostel)
                .into(holder.image);

        //holder.image.setImageResource(dataItems.getImage());
        holder.heading.setText(dataItems.getHeading());
        holder.description.setText(dataItems.getDescription());
        holder.price.setText(dataItems.getPrice());
        holder.bed_space.setText("Bed Spaces "+dataItems.getBed_space());

    }

    @Override
    public int getItemCount() {
        return listItem.size();
    }
}
class DataRowHolder extends RecyclerView.ViewHolder{

    protected ImageView image;
    protected TextView heading;
    protected TextView description;
    protected TextView price;
    protected TextView bed_space;


    public DataRowHolder(View itemView) {
        super(itemView);

        image = (ImageView)itemView.findViewById(R.id.container_image);
        heading = (TextView)itemView.findViewById(R.id.container_heading);
        description = (TextView)itemView.findViewById(R.id.container_description);
        price = (TextView)itemView.findViewById(R.id.container_price);
        bed_space = (TextView)itemView.findViewById(R.id.bed_spaces);

    }
}

