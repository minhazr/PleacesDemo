package info.minhaz.placesdemo;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.rey.material.widget.Button;

/*
 * ----------------------------------------------------------------------------
 * "THE BEER-WARE LICENSE" (Revision 42):
 * <minhaz@minhaz.info> wrote this file.  As long as you retain this notice you
 * can do whatever you want with this stuff. If we meet some day, and you think
 * this stuff is worth it, you can buy me a beer in return.   Minhaz Rafi Chowdhury
 *
 * You may obtain a copy of the License at
 *
 * https://fedoraproject.org/wiki/Licensing/Beerware
 * ----------------------------------------------------------------------------
 */

public class  RecycleViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
    TextView phone;
    TextView send;
    TextView receive;
    TextView time;
    LinearLayout sentLayout;
    LinearLayout receivedLayout;
    private ItemClick mListener;

    public RecycleViewHolder(View view, ItemClick listener) {
        super(view);
        this.mListener=listener;
        phone=(TextView) view.findViewById(R.id.textViewPhone);
        send=(TextView) view.findViewById(R.id.textViewLocationSent);
        send.setOnClickListener(this);
        time=(TextView) view.findViewById(R.id.textViewTime);
        receive=(TextView) view.findViewById(R.id.textViewLocationReceived);
        receive.setOnClickListener(this);

        sentLayout=(LinearLayout)view.findViewById(R.id.linearLayoutLocationSent);
        receivedLayout=(LinearLayout)view.findViewById(R.id.linearLayoutLocationReceived);
    }

    @Override
    public void onClick(View view) {
        int id=view.getId();
        switch (id){
            case R.id.textViewLocationSent:
                mListener.linkClick(getAdapterPosition(), true);
                break;
            case R.id.textViewLocationReceived:
                mListener.linkClick(getAdapterPosition(), false);
                break;

        }

    }

    public interface ItemClick{
        void linkClick(int position, boolean sent);
    }
}
