package com.semicolon.salonat.notification;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.media.AudioAttributes;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;
import com.semicolon.salonat.R;
import com.semicolon.salonat.activities.HomeActivity;
import com.semicolon.salonat.models.UnReadModel;
import com.semicolon.salonat.models.UserModel;
import com.semicolon.salonat.preference.Preferences;
import com.semicolon.salonat.tags.Tags;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import org.greenrobot.eventbus.EventBus;

import java.util.Map;

public class NotificationMessaging extends FirebaseMessagingService {
    Preferences preferences = Preferences.getInstance();
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        super.onMessageReceived(remoteMessage);

        Map<String,String> map = remoteMessage.getData();
        for (String key : map.keySet())
        {
            Log.e("Key: ",key+" Value: "+map.get(key));
        }


        ManageNotification(getUserData(),getSession(),map);

    }

    private void ManageNotification(final UserModel userData, final String session, final Map<String, String> map) {
        new Handler(Looper.getMainLooper()).postDelayed(new Runnable() {
            @Override
            public void run() {

                if (session.equals(Tags.session_login))
                {
                    String to_user_id = map.get("to_user_id");
                    String current_user_id = userData.getUser_id();

                    if (to_user_id.equals(current_user_id))
                    {
                        CreateNotification(map);

                    }
                }

            }
        },100);
    }

    private void CreateNotification(final Map<String, String> map) {
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.O)
        {


            Log.e("nnnnnnnn","nnnnnnnnnnn");

            final int notifyID = 1;
            String CHANNEL_ID = "my_channel_01";
            CharSequence name ="my_channel_name";
            String notPath = "android.resource://"+getPackageName()+"/"+ R.raw.not;
            int importance = NotificationManager.IMPORTANCE_HIGH;

            Intent intent = new Intent(NotificationMessaging.this, HomeActivity.class);
            android.app.TaskStackBuilder stackBuilder = android.app.TaskStackBuilder.create(this);
            stackBuilder.addNextIntent(intent);
            PendingIntent pendingIntent = stackBuilder.getPendingIntent(0,PendingIntent.FLAG_UPDATE_CURRENT);
            NotificationChannel mChannel = new NotificationChannel(CHANNEL_ID, name, importance);

            mChannel.setShowBadge(true);
            mChannel.setSound(Uri.parse(notPath),new AudioAttributes.Builder()
                    .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
                    .setLegacyStreamType(AudioManager.STREAM_NOTIFICATION)
                    .setUsage(AudioAttributes.USAGE_NOTIFICATION_EVENT)
                    .build());

            final Notification.Builder notification = new Notification.Builder(this)
                    .setContentTitle(map.get("main_title"))
                    .setContentText(map.get("message_content"))
                    .setSmallIcon(R.mipmap.icon_launcher_rounded)
                    .setChannelId(CHANNEL_ID)
                    .setContentIntent(pendingIntent);
            final NotificationManager mNotificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            mNotificationManager.createNotificationChannel(mChannel);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    notification.setLargeIcon(bitmap);
                    mNotificationManager.notify(notifyID , notification.build());

                    EventBus.getDefault().post(new UnReadModel());

                    Log.e("rr","rr");
                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };
            Picasso.with(this).load(Tags.IMAGE_URL+map.get("image")).into(target);


        }else
        {

            final NotificationManager manager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            final NotificationCompat.Builder builder = new NotificationCompat.Builder(NotificationMessaging.this);

            Target target = new Target() {
                @Override
                public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                    builder.setLargeIcon(bitmap);
                    builder.setSmallIcon(R.mipmap.icon_launcher_rounded);
                    builder.setContentTitle(map.get("main_title"));
                    builder.setContentText(map.get("message_content"));
                    Intent intent =new Intent(NotificationMessaging.this, HomeActivity.class);
                    PendingIntent pendingIntent = PendingIntent.getActivity(NotificationMessaging.this,0,intent,PendingIntent.FLAG_UPDATE_CURRENT);
                    builder.setContentIntent(pendingIntent);
                    manager.notify(0,builder.build());
                    EventBus.getDefault().post(new UnReadModel());


                }

                @Override
                public void onBitmapFailed(Drawable errorDrawable) {

                }

                @Override
                public void onPrepareLoad(Drawable placeHolderDrawable) {

                }
            };

            Picasso.with(this).load(Uri.parse(Tags.IMAGE_URL+map.get("image"))).into(target);



        }
    }


    private UserModel getUserData()
    {
        UserModel userModel = preferences.getUserData(this);
        return userModel;
    }
    private String getSession()
    {
        String session = preferences.getSession(this);
        return session;
    }




}
