package info.minhaz.placesdemo;

import android.content.Context;
import android.database.Cursor;
import android.telephony.PhoneNumberUtils;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.Locale;

import info.minhaz.placesdemo.model.AppObject;
import info.minhaz.placesdemo.model.Cache;
import info.minhaz.placesdemo.model.Place;


public class RecyclerAdapter extends CursorRecyclerViewAdapter<RecycleViewHolder> implements RecycleViewHolder.ItemClick{
    private Context context;
    public RecyclerAdapter(Context context, Cursor cursor){
        super(context, cursor);
        this.context=context;
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder viewHolder, Cursor cursor) {
        if (cursor==null ||(cursor!=null && cursor.getCount()==0)) return;
        AppObject appObject=new AppObject(cursor);
        if (!TextUtils.isEmpty(appObject.getPhone())){
            viewHolder.phone.setText(PhoneNumberUtils.formatNumber(appObject.getPhone()));
        }

        if (!TextUtils.isEmpty(appObject.getReceivedPlace())){
            Place place=Place.create(appObject.getReceivedPlace());
            if (place!=null){
                viewHolder.receivedLayout.setVisibility(View.VISIBLE);
                viewHolder.receive.setText(place.getName());
            }

        }else{
            viewHolder.receivedLayout.setVisibility(View.GONE);
        }
        if (!TextUtils.isEmpty(appObject.getSendPlace())){
            Place place=Place.create(appObject.getSendPlace());
            if (place!=null){
                viewHolder.sentLayout.setVisibility(View.VISIBLE);
                viewHolder.send.setText(place.getName());
            }
        }else{
            viewHolder.sentLayout.setVisibility(View.GONE);
        }
        Calendar now = Calendar.getInstance();
        Calendar updateTime = Calendar.getInstance();
        updateTime.setTimeInMillis(appObject.getTime());
        if ((now.get(Calendar.YEAR)== updateTime.get(Calendar.YEAR)) && (now.get(Calendar.DAY_OF_YEAR) == updateTime.get(Calendar.DAY_OF_YEAR))){
            long diff = now.getTimeInMillis() - appObject.getTime();
            if (diff>3600000){
                diff=diff/3600000;
                if (diff==1)
                    viewHolder.time.setText("1 "+context.getString(R.string.hour));
                else{
                    viewHolder.time.setText(diff+" "+context.getString(R.string.hours));
                }
            }else if (diff>=60000 && diff<=3600000){
                diff=diff/60000;
                if (diff==1)
                    viewHolder.time.setText("1 "+context.getString(R.string.minute));
                else{
                    viewHolder.time.setText(diff+" "+context.getString(R.string.minutes));
                }
            }else if (diff>=1000 && diff<60000){
                diff=diff/1000;
                if (diff==1)
                    viewHolder.time.setText("1 "+context.getString(R.string.second));
                else{
                    viewHolder.time.setText(diff+" "+context.getString(R.string.seconds));
                }
            }
        }


    }


    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        return new RecycleViewHolder(view, this);
    }

    @Override
    public void linkClick(int position, boolean sent) {
        Cursor cursor=getCursor();
        if (cursor==null || cursor.getCount()==0) return;
        if (cursor.moveToPosition(position)){
            AppObject appObject=new AppObject(cursor);
            if (sent){
                if (!TextUtils.isEmpty(appObject.getSendPlace())){
                    Place place=Place.create(appObject.getSendPlace());
                    if (place!=null){
                        MapsActivity.startActivity(context, place);
                    }

                }
            }else{
                if (!TextUtils.isEmpty(appObject.getReceivedPlace())){
                    Place place=Place.create(appObject.getReceivedPlace());
                    if (place!=null){
                        MapsActivity.startActivity(context, place);
                    }

                }
            }

        }
    }

    @Override
    public void deleteClick(int position) {
        Cursor cursor=getCursor();
        if (cursor==null || cursor.getCount()==0) return;
        if (cursor.moveToPosition(position)){
            Cache.delete(context, cursor.getString(cursor.getColumnIndex(AppObject.Columns.PHONE)));
            notifyDataSetChanged();
        }
    }
}