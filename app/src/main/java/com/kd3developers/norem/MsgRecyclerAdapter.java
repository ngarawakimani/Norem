package com.kd3developers.norem;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * Created by wa kimani on 11/29/2016.
 */

public class MsgRecyclerAdapter extends RecyclerView.Adapter<MsgRowHolder>{
    List<MsgItems> msgItem;
    Context mContext;

    public MsgRecyclerAdapter(Context mContext , List<MsgItems> msgItem) {
        this.mContext = mContext;
        this.msgItem = msgItem;
    }

    @Override
    public MsgRowHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.msg_recycler_layout,null);
        MsgRowHolder rowHolder = new MsgRowHolder(view);
        return rowHolder;
    }

    @Override
    public void onBindViewHolder(MsgRowHolder holder, int position) {
        final MsgItems msgItems = msgItem.get(position);

        holder.headline.setText(msgItems.getHeadline());
        holder.message.setText(msgItems.getMessage());
        holder.time.setText(msgItems.getTime());

    }

    @Override
    public int getItemCount() {
        return msgItem.size();
    }
}
class MsgRowHolder extends RecyclerView.ViewHolder{

    protected TextView headline;
    protected TextView message;
    protected TextView time;


    public MsgRowHolder(View itemView) {
        super(itemView);

        headline = (TextView)itemView.findViewById(R.id.msg_headline);
        message = (TextView)itemView.findViewById(R.id.msg_message);
        time = (TextView)itemView.findViewById(R.id.msg_time);

    }
}

