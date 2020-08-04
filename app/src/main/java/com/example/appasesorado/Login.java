package com.example.appasesorado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appasesorado.Comun.Comun;
import com.example.appasesorado.Modelos.Usuario;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.OnClick;
import dmax.dialog.SpotsDialog;

public class Login extends AppCompatActivity {

    private static final int REQUEST_CODE = 7171;

    //variables
    private String fechadenac;
    private String nombre;
    Button btn_celular;
    AlertDialog alertDialog;
    TextView fechacreacion;
    TextView fechaactualizacion;

    //firebase
    FirebaseDatabase database;
    DatabaseReference users;

    //autenficacion
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

    @OnClick(R.id.btn_celular)
    void loginUser(){
        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),REQUEST_CODE);
    }

    @Override
    protected void onStart(){
        super.onStart();
        firebaseAuth.addAuthStateListener(listener);
    }

    @Override
    protected void onStop(){
        if (listener !=null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        firebaseAuth = FirebaseAuth.getInstance();
        //autenficacion
        providers = Arrays.asList(new AuthUI.IdpConfig.PhoneBuilder().build());
        listener = firebaseAuthLocal -> {
            FirebaseUser user = firebaseAuthLocal.getCurrentUser();
            if (user != null){
                checkUserFromFirebase(user);
            }

        };
        setContentView(R.layout.activity_login);

        //init firebase
        database=FirebaseDatabase.getInstance();
        users=database.getReference("Usuario");

        btn_celular=(Button)findViewById(R.id.btn_celular);



        if (Comun.isConnectedToInternet(getBaseContext())){

            btn_celular.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    starLoginSystem();
                }
            });
        }
        else{
            Toast.makeText(Login.this,"Revise su conexion a internet !!",Toast.LENGTH_LONG).show();
            return;
        }
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        alertDialog = new SpotsDialog.Builder().setContext(this).setCancelable(false).build();
        alertDialog.setMessage("Entrando...");
        alertDialog.show();


        users.child(user.getPhoneNumber()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    Usuario usuario = dataSnapshot.getValue(Usuario.class);
                    goToHomeActivity(usuario);
                    alertDialog.dismiss();
                    Toast.makeText(Login.this, "Se logeo correctamente!! :) ", Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(Login.this,Dashboard.class);
                    //Comun.actualUsuario = localUser;
                    startActivity(homeIntent);
                    finish();

                }else{
                    showRegisterDialog(user);
                }
                alertDialog.dismiss();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void showRegisterDialog(FirebaseUser user) {

    }

    private void goToHomeActivity(Usuario usuario) {
        Comun.actualUsuario = usuario;
    }

    private void starLoginSystem() {
        startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder()
                .setAvailableProviders(providers).build(),REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }else{
                Toast.makeText(this, "No se pudo ingresar :(", Toast.LENGTH_SHORT).show();
            }

        }
    }

    }


