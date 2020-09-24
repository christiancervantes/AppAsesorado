package com.example.appasesorado.Comun;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;

import com.example.appasesorado.Modelos.Asesor;
import com.example.appasesorado.Modelos.Celular;
import com.example.appasesorado.Modelos.TokenModel;
import com.example.appasesorado.Modelos.Usuario;
import com.example.appasesorado.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public
class Comun {

    public static final String STUDENT_INFO_REF = "estudiantes";
    public static final String ASESOR_REF = "asesores";
    public static final String COMMENT_REF = "comentarios";
    public static final String CELULARES_REF = "celulares";
    public static final String NOTI_TITLE = "title";
    public static final String NOTI_CONTENT = "content";
    public static final String TOKEN_REF = "tokens";

    public static Usuario actualUsuario;
    public static Asesor asesorseleccionado;
    public static String actualToken= "";
    public static Celular celularActual;


    public static boolean isConnectedToInternet (Context context){
        ConnectivityManager connectivityManager = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager !=null){
            NetworkInfo[] info = connectivityManager.getAllNetworkInfo();
            if (info != null){
                for (int i=0;i<info.length;i++){
                    if (info[i].getState()==NetworkInfo.State.CONNECTED)
                        return true;
                }
            }
        }
        return false;
    }

    public static void showNotification(Context context , int id, String title, String content, Intent intent) {
        PendingIntent pendingIntent = null;
        if (intent != null)
            pendingIntent = PendingIntent.getActivity(context,id,intent,PendingIntent.FLAG_UPDATE_CURRENT);
        String NOTIFICATION_CHANNEL_ID ="app_asesorado";
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel notificationChannel = new NotificationChannel(NOTIFICATION_CHANNEL_ID,
                    "App Asesorado",NotificationManager.IMPORTANCE_DEFAULT);
            notificationChannel.setDescription("Asesorado");
            notificationChannel.enableLights(true);
            notificationChannel.setLightColor(Color.RED);
            notificationChannel.setVibrationPattern(new long[]{0,100,500,1000});
            notificationChannel.enableVibration(true);

            notificationManager.createNotificationChannel(notificationChannel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context,NOTIFICATION_CHANNEL_ID);
        builder.setContentTitle(title)
                .setContentText(content)
                .setAutoCancel(true)
                .setSmallIcon(R.mipmap.ic_launcher_round)
                .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),R.drawable.rating)); //secambia de icono

        if (pendingIntent !=null)
            builder.setContentIntent(pendingIntent);
        Notification notification = builder.build();
        notificationManager.notify(id,notification);

    }

    public static void updateToken(Context context , String newToken) {

        TokenModel tokenModel = new TokenModel(newToken);
        if (FirebaseAuth.getInstance().getCurrentUser().getUid() !=null){ //arregla el bug de la localizacion al desintalar
            FirebaseDatabase.getInstance()
                    .getReference(Comun.TOKEN_REF)
                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                    .setValue(tokenModel)
                    .addOnFailureListener(e -> {
                        Toast.makeText(context, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {

                }
            });
        }
    }

    public static String createTopicOrder() {
        return new StringBuilder("/topics/Nueva_Calificación").toString();
    }
}
