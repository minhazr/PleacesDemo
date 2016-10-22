package info.minhaz.placesdemo;

import android.content.Context;
import android.database.Cursor;
import android.view.ViewGroup;

/**
 * Created by mc7101 on 10/21/2016.
 */

public class RecyclerAdapter extends CursorRecyclerViewAdapter<RecycleViewHolder> {

    public RecyclerAdapter(Context context, Cursor cursor){
        super(context, cursor);
    }

    @Override
    public void onBindViewHolder(RecycleViewHolder viewHolder, Cursor cursor) {
        if (cursor==null && cursor.getCount()==0) return;

    }

    @Override
    public RecycleViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        return null;
    }


}
