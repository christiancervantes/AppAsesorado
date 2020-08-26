package com.example.appasesorado.Comun;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.example.appasesorado.Modelos.Asesor;
import com.example.appasesorado.Modelos.Usuario;

public
class Comun {

    public static final String STUDENT_INFO_REF = "estudiantes";
    public static final String ASESOR_REF = "asesores";
    public static final String COMMENT_REF = "comentarios";
    public static Usuario actualUsuario;
    public static Asesor asesorseleccionado;

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
}
