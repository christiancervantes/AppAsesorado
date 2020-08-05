package com.example.appasesorado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appasesorado.Modelos.Usuario;
import com.facebook.FacebookSdk;
import com.facebook.appevents.AppEventsLogger;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appasesorado.Comun.Comun;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.Principal;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;
import io.reactivex.schedulers.Schedulers;

public class Splash extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 7171;

    //variables
    private String fechadenac;
    TextView fechacreacion;
    TextView fechaactualizacion;

    //autenficacion
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

   //@BindView(R.id.progress_bar)
    //ProgressBar progress_bar;

    //database
    FirebaseDatabase database;
    DatabaseReference studentInfoRef;


    @Override
    protected void onStart() {
        super.onStart();
        delaySplashScreen();
    }

    @Override
    protected void onStop() {
        if (firebaseAuth != null && listener != null)
            firebaseAuth.removeAuthStateListener(listener);
        super.onStop();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    init();

    }


    private void init() {

        ButterKnife.bind(this);

        database = FirebaseDatabase.getInstance();
        studentInfoRef = database.getReference(Comun.STUDENT_INFO_REF);

        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());

        //showSignInOptions();

        firebaseAuth = FirebaseAuth.getInstance();

        listener = myFirebaseAuth -> { //
            FirebaseUser user = myFirebaseAuth.getCurrentUser();
            if ( user != null)
                //delaySplashScreen();
               checkUserFromFirebase(user);
            else
                showLogin();
        };
    }

    private void checkUserFromFirebase(FirebaseUser user) {
        studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            Toast.makeText(Splash.this, "Ingreso", Toast.LENGTH_SHORT).show();
                        }else{
                           // showRegisterLayout(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Splash.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterLayout(FirebaseUser user) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater inflater = getLayoutInflater(); //-----cambio 1
        builder.setCancelable(false);
        builder.setTitle("Registro");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);

        builder.setView(itemView);
        final AlertDialog dialog = builder.create();
        dialog.show();  //-----cambio 2



        EditText edt_nombre = (EditText) itemView.findViewById(R.id.edt_nombre);
        EditText edt_apellido = (EditText) itemView.findViewById(R.id.edt_apellido);
        EditText edt_celular = (EditText) itemView.findViewById(R.id.edt_phone);

        //Spinner spinner = (Spinner) itemView.findViewById(R.id.spinner_registro);
        //ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,R.array.lista,android.R.layout.simple_list_item_1);
        //spinner.setAdapter(adapter);


        //MaterialEditText edt_fechadenacimiento = findViewById(R.id.edt_fechadenacimiento);
        EditText edt_fechadenacimiento = (EditText) itemView.findViewById(R.id.edt_fechadenacimiento);
        edt_fechadenacimiento.setOnClickListener(v -> {
            final Calendar calendar = Calendar.getInstance();
            DatePickerDialog datePickerDialog = new DatePickerDialog(Splash.this, (datePicker, year, mes, dia) -> {
                Calendar calendarResultado = Calendar.getInstance();
                calendarResultado.set(Calendar.YEAR,year);
                calendarResultado.set(Calendar.MONTH,mes);
                calendarResultado.set(Calendar.DAY_OF_MONTH,dia);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy", Locale.getDefault());
                Date date = calendarResultado.getTime();
                //String fechaDeNacimientoTexto = simpleDateFormat.format(date);
                fechadenac = simpleDateFormat.format(date);
                edt_fechadenacimiento.setText(fechadenac);
            },calendar.get(Calendar.YEAR)-18,calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH));
            datePickerDialog.show();
        });



        //sacar fecha y hora de la cuenta creada
        SimpleDateFormat dateFormat = new SimpleDateFormat("EEEE, dd 'de' MMMM 'del' yyyy hh:mm:ss aaa",Locale.getDefault());
        String fechacreada = dateFormat.format(new Date());
        fechacreacion =dialog.findViewById(R.id.fechacreacion);
        fechacreacion.setText(fechacreada);

        //fechadeactualizacion
        SimpleDateFormat dateFormat1 = new SimpleDateFormat("EEEE, dd-MM-yyyy hh:mm:ss aaa",Locale.getDefault());
        String fechacreada1 = dateFormat1.format(new Date());
        fechaactualizacion =dialog.findViewById(R.id.fechaactualizacion);
        fechaactualizacion.setText(fechacreada1);


        //ya pone el celular de usuario registrado
        edt_celular.setText(user.getPhoneNumber());


        TextView txt_registrar = itemView.findViewById(R.id.btn_continuar);
        txt_registrar.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edt_nombre.getText().toString())){
                Toast.makeText(this, "Ingrese su nombre por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(edt_apellido.getText().toString())){
                Toast.makeText(this, "Ingrese su apellido por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(edt_fechadenacimiento.getText().toString())){
                Toast.makeText(this, "Ingrese su fecha de Nacimiento porfavor", Toast.LENGTH_SHORT).show();
                return;
            }


            Usuario usuario = new Usuario();
            usuario.setUid(user.getUid());
            usuario.setNombre(edt_nombre.getText().toString());
            usuario.setApellido(edt_apellido.getText().toString());
            usuario.setCelular(edt_celular.getText().toString());
           // usuario.setSpinner(spinner.getAccessibilityClassName().toString());
            usuario.setFechadecumpleaños(edt_fechadenacimiento.getText().toString());
            usuario.setFechadecreacion(fechacreacion.getText().toString());
            usuario.setFechaactualizacion(fechaactualizacion.getText().toString());

            studentInfoRef.child(user.getUid()).setValue(usuario).addOnCompleteListener(task -> {  //************************** cambiar a nombre
                if (task.isSuccessful()){
                    dialog.dismiss();
                    Toast.makeText(this, "Registro Completo!! :) ", Toast.LENGTH_SHORT).show();
                    goToHomeActivity(usuario);
                    Intent homeIntent = new Intent(Splash.this,Dashboard.class);
                    //Comun.actualUsuario = localUser;
                    startActivity(homeIntent);
                    finish();
                }
            });
        });
    }

    private void showLogin() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_login)
                .setPhoneButtonId(R.id.btn_celular)
                .setGoogleButtonId(R.id.btn_google)
                .setFacebookButtonId(R.id.btn_facebook)
                .build();

        startActivityForResult(AuthUI.getInstance()
        .createSignInIntentBuilder()
        .setAuthMethodPickerLayout(authMethodPickerLayout)
        .setIsSmartLockEnabled(false)
                .setTheme(R.style.LoginTheme)
        .setAvailableProviders(providers)
        .build(),LOGIN_REQUEST_CODE);
    }

    private void delaySplashScreen() {

        //progress_bar.setVisibility(View.VISIBLE);

        Completable.timer(3, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {

                        //
                        firebaseAuth.addAuthStateListener(listener);
                    }
                });
    }

    private void goToHomeActivity(Usuario usuario) {
        Comun.actualUsuario = usuario;

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            }else{
                Toast.makeText(this, "[Error]: "+response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}