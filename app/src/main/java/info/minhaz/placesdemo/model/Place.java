package info.minhaz.placesdemo.model;

import android.os.Parcel;
import android.os.Parcelable;
import android.provider.BaseColumns;

import org.json.JSONException;
import org.json.JSONObject;



public class Place implements Parcelable {
    private String name;
    private double latitude;
    private double longitude;


    public Place(){
        super();
    }
    public Place(JSONObject object){
        name=object.optString(JsonKey.NAME);
        latitude=object.optDouble(JsonKey.LATITUDE);
        longitude=object.optDouble(JsonKey.LONGITUDE);
    }
    public JSONObject toJson() throws JSONException{
        JSONObject object=new JSONObject();
        object.put(JsonKey.NAME, name);
        object.put(JsonKey.LATITUDE, latitude);
        object.put(JsonKey.LONGITUDE, longitude);
        return object;
    }
    public static Place create(String input){
        JSONObject object= null;
        try {
            object = new JSONObject(input);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (object==null) return null;
        Place place=new Place(object);
        return place;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
    public interface JsonKey extends BaseColumns {
        String NAME="name";
        String LATITUDE ="latitude";
        String LONGITUDE="longitude";
    }


    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeDouble(this.latitude);
        dest.writeDouble(this.longitude);
    }

    protected Place(Parcel in) {
        this.name = in.readString();
        this.latitude = in.readDouble();
        this.longitude = in.readDouble();
    }

    public static final Parcelable.Creator<Place> CREATOR = new Parcelable.Creator<Place>() {
        @Override
        public Place createFromParcel(Parcel source) {
            return new Place(source);
        }

        @Override
        public Place[] newArray(int size) {
            return new Place[size];
        }
    };
}
