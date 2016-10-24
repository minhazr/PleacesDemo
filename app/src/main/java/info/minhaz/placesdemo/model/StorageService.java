package info.minhaz.placesdemo.model;

import android.app.IntentService;
import android.content.Intent;
import android.content.Context;
import android.text.TextUtils;

import org.json.JSONException;


public class StorageService extends IntentService {

    private static final String ACTION_SEND = "info.minhaz.placesdemo.action.SEND";
    private static final String ACTION_RECEIVE = "info.minhaz.placesdemo.action.RECEIVE";
    private static final String ACTION_DELETE = "info.minhaz.placesdemo.action.DELETE";

    private static final String EXTRA_SENDER = "info.minhaz.placesdemo.extra.SENDER";
    private static final String EXTRA_LOCATION = "info.minhaz.placesdemo.extra.LOCATION";
    private static final String EXTRA_NUMBER = "info.minhaz.placesdemo.extra.NUMBER";


    public StorageService() {
        super("StorageService");
    }


    public static void storeSendData(Context context, Place place, String sender) {
        Intent intent = new Intent(context, StorageService.class);
        intent.setAction(ACTION_SEND);
        intent.putExtra(EXTRA_LOCATION, place);
        intent.putExtra(EXTRA_SENDER, sender);
        context.startService(intent);
    }
    public static void storeReceiveData(Context context, Place place, String sender) {
        Intent intent = new Intent(context, StorageService.class);
        intent.setAction(ACTION_RECEIVE);
        intent.putExtra(EXTRA_LOCATION, place);
        intent.putExtra(EXTRA_SENDER, sender);
        context.startService(intent);
    }
    public static void delete(Context context, String sender) {
        Intent intent = new Intent(context, StorageService.class);
        intent.setAction(ACTION_DELETE);
        intent.putExtra(EXTRA_NUMBER, sender);
        context.startService(intent);
    }



    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_SEND.equals(action)) {
                final String sender = intent.getStringExtra(EXTRA_SENDER);
                final Place place = intent.getParcelableExtra(EXTRA_LOCATION);
                handleActionSend(sender, place, true);
            } else if (ACTION_RECEIVE.equals(action)) {
                final String sender = intent.getStringExtra(EXTRA_SENDER);
                final Place place = intent.getParcelableExtra(EXTRA_LOCATION);
                handleActionSend(sender, place, false);
            }else if (ACTION_DELETE.equals(action)) {
                final String sender = intent.getStringExtra(EXTRA_NUMBER);
                Cache.delete(getApplicationContext(), sender);
            }
        }
    }


    private void handleActionSend(String sender, Place place, boolean send) {
        String number= Cache.get(getApplicationContext(), sender);
        String place_string= null;
        try {
            place_string = place.toJson().toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(place_string)) return;
        if (!TextUtils.isEmpty(number)){
            Cache.update(getApplicationContext(), sender, send, place_string);
        }else{
            Cache.insert(getApplicationContext(),sender,send, place_string);
        }
    }


    private void handleActionReceive(String sender, Place place) {
        String number= Cache.get(getApplicationContext(), sender);
        if (!TextUtils.isEmpty(number)){
            try {
                Cache.update(getApplicationContext(), sender, false, place.toJson().toString());
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}
