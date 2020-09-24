package com.example.appasesorado.Services;

import androidx.annotation.NonNull;

import com.example.appasesorado.Comun.Comun;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import java.util.Map;
import java.util.Random;

public
class MyFCMServices extends FirebaseMessagingService {
    @Override
    public void onMessageReceived(@NonNull RemoteMessage remoteMessage) {
        Map<String,String> dataRecv = remoteMessage.getData();
        if (dataRecv!=null){
            Comun.showNotification(this,new Random().nextInt(),
                    dataRecv.get(Comun.NOTI_TITLE),
                    dataRecv.get(Comun.NOTI_CONTENT),
                    null);
        }
    }

    @Override
    public void onNewToken(@NonNull String s) {
        super.onNewToken(s);
        if (FirebaseAuth.getInstance().getCurrentUser()!=null)
            Comun.updateToken(this,s);
    }
}
