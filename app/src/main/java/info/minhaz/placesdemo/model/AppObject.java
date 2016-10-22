package info.minhaz.placesdemo.model;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.android.gms.location.places.Place;

/**
 * Created by mc7101 on 10/21/2016.
 */

public class AppObject {
    private String phone;
    private String receivedPlace;
    private String sendPlace;
    private long time;


    AppObject(){
        super();
    }
    AppObject(Cursor cursor){
        phone = cursor.getString(cursor.getColumnIndex(Columns.PHONE));
        receivedPlace=cursor.getString(cursor.getColumnIndex(Columns.RECEIVED));
        sendPlace=cursor.getString(cursor.getColumnIndex(Columns.SEND));
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getReceivedPlace() {
        return receivedPlace;
    }

    public void setReceivedPlace(String receivedPlace) {
        this.receivedPlace = receivedPlace;
    }

    public String getSendPlace() {
        return sendPlace;
    }

    public void setSendPlace(String sendPlace) {
        this.sendPlace = sendPlace;
    }

    public long getTime() {
        return time;
    }

    public void setTime(long time) {
        this.time = time;
    }
    public interface Columns extends BaseColumns {
        String PHONE="phone";
        String SEND="send";
        String RECEIVED="received";
        String TIME="time";
    }
}
