package com.example.appasesorado;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.appasesorado.Comun.Comun;

import java.security.Principal;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        ImageView logo = findViewById(R.id.logo);

        if (Comun.isConnectedToInternet(getBaseContext())){
            Animation myanim = AnimationUtils.loadAnimation(this,R.anim.animacion1);

            logo.startAnimation(myanim);

            final Intent intent = new Intent(Splash.this, Login.class);
            Thread timer = new Thread(){
                public void run (){
                    try{
                        sleep(2000);
                    }catch (InterruptedException e){
                        e.printStackTrace();
                    }
                    finally {
                        startActivity(intent);
                        finish();
                    }
                }
            };
            timer.start();

        }
        else{
            Toast.makeText(Splash.this,"Revise su conexion a internet !!",Toast.LENGTH_LONG).show();
            return;
        }
    }


}