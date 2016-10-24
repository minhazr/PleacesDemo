package info.minhaz.placesdemo.model;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.google.android.gms.location.places.Place;

import org.json.JSONObject;

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

public class AppObject {
    private String phone;
    private String receivedPlace;
    private String sendPlace;
    private long time;


    public AppObject(){
        super();
    }
    public AppObject(Cursor cursor){
        phone = cursor.getString(cursor.getColumnIndex(Columns.PHONE));
        receivedPlace=cursor.getString(cursor.getColumnIndex(Columns.RECEIVED));
        sendPlace=cursor.getString(cursor.getColumnIndex(Columns.SEND));
        time=cursor.getLong(cursor.getColumnIndex(Columns.TIME));
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
