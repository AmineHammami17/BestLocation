package com.example.findfriends;

import android.annotation.SuppressLint;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.media.AudioAttributes;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class MySmsReceiver extends BroadcastReceiver {

    @SuppressLint("MissingPermission")
    @Override
    public void onReceive(Context context, Intent intent) {

// TODO: This method is called when the BroadcastReceiver is receiving an
// Intent broadcast.
        String messageBody, phoneNumber;
        if (intent.getAction().equals("android.provider.Telephony.SMS_RECEIVED")) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                Object[] pdus = (Object[]) bundle.get("pdus");
                final SmsMessage[] messages = new SmsMessage[pdus.length];
                for (int i = 0; i < pdus.length; i++) {
                    messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                }
                if (messages.length > -1) {
                    messageBody = messages[0].getMessageBody();
                    phoneNumber = messages[0].getDisplayOriginatingAddress();

                    Toast.makeText(context,
                                    "Message : " + messageBody + "Reçu de la part de;" + phoneNumber,
                                    Toast.LENGTH_LONG)
                            .show();
                    if(messageBody.contains("FindFriends: Envoyer moi votre position")){
                        //lancer un service => capter la position gps et la renvoyer
                        Intent i=new Intent(context,MyLocationService.class);
                        i.putExtra("phone",phoneNumber);
                        context.startService(i);

                    }
                    if(messageBody.contains("FindFriends : Ma position est ")){
                        String[] t = messageBody.split("#");
                        String longitude=t[1];
                        String latitude=t[2];
                        NotificationCompat.Builder mynotif = new NotificationCompat.Builder(context,"channel");
                        mynotif.setContentTitle("");
                        mynotif.setContentText("");
                        mynotif.setSmallIcon(android.R.drawable.ic_dialog_map);
                        mynotif.setAutoCancel(true);
                        Intent i2 = new Intent(context , MapsActivity.class);
                        i2.putExtra("longitude",longitude);
                        i2.putExtra("latitude",latitude);

                        PendingIntent pi = PendingIntent.getActivity(context,0,i2,PendingIntent.FLAG_MUTABLE);
                        mynotif.setContentIntent(pi);
                        NotificationManagerCompat managerCompat = NotificationManagerCompat.from(context);
                        NotificationChannel canal = new NotificationChannel("channel","canal pour notre app",NotificationManager.IMPORTANCE_DEFAULT);
                        managerCompat.notify(1,mynotif.build());


                    }


                }
            }
        }
    }}