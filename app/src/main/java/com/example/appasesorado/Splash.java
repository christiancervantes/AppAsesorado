package com.example.appasesorado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.appasesorado.Modelos.Usuario;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appasesorado.Comun.Comun;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class Splash extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 7171;

    //variables
    private String fechadenac;
    TextView fechacreacion;
    TextView fechaactualizacion;
    TextView txtTerminos;

    //autenficacion
    //prueba1
    private List<AuthUI.IdpConfig> providers;
    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener listener;

   @BindView(R.id.progress_bar)
    ProgressBar progress_bar;

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

        try {
            PackageInfo info = getPackageManager().getPackageInfo(
                    "com.example.appasesorado",                  //Insert your own package name.
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                MessageDigest md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
                Log.d("KeyHash:", Base64.encodeToString(md.digest(), Base64.DEFAULT));
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        setContentView(R.layout.activity_splash);

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


        firebaseAuth = FirebaseAuth.getInstance();

        listener = myFirebaseAuth -> { //
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null)
                //delaySplashScreen();
                checkUserFromFirebase(user);
            else
                showLogin();
        };
    }


    //commit

    private void checkUserFromFirebase(FirebaseUser user) {
        studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()){
                            //Toast.makeText(Splash.this, "Ingreso", Toast.LENGTH_SHORT).show();
                            Usuario usuario = snapshot.getValue(Usuario.class);
                            goToHomeActivity(usuario);

                        }else{
                           showRegisterLayout(user);
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                        Toast.makeText(Splash.this, ""+error.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void showRegisterLayout(FirebaseUser user) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.DialogTheme);
        LayoutInflater inflater = getLayoutInflater(); //-----cambio 1
        builder.setCancelable(false);
        builder.setTitle("Registro de datos");

        View itemView = LayoutInflater.from(this).inflate(R.layout.layout_register,null);

        builder.setView(itemView);
        final AlertDialog dialog = builder.create();
        dialog.show();  //-----cambio 2


        EditText edt_nombre = (EditText) itemView.findViewById(R.id.edt_nombre);
        EditText edt_celular = (EditText) itemView.findViewById(R.id.edt_phone);
        CheckBox cbxterminos = (CheckBox) itemView.findViewById(R.id.cbxterminos);

           cbxterminos.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View view) {
                   if (cbxterminos.isChecked()){
                       AlertDialog.Builder popup = new AlertDialog.Builder(Splash.this);
                       LayoutInflater Inflater2 = getLayoutInflater();
                       popup.setCancelable(false);
                       popup.setTitle("Términos y Cóndiciones");
                       View itemView2 = LayoutInflater.from(getBaseContext()).inflate(R.layout.layout_terminos,null);

                       popup.setView(itemView2);
                       final AlertDialog dialog2 = popup.create();
                       dialog2.show();


                       Button btnContinuar = (Button) itemView2.findViewById(R.id.btnContinuar);
                       btnContinuar.setOnClickListener(new View.OnClickListener() {
                           @Override
                           public void onClick(View view) {
                               Toast.makeText(Splash.this, "Términos y Cóndiciones aceptadas!!", Toast.LENGTH_SHORT).show();
                               dialog2.dismiss();

                           }
                       });
                   }else{
                       //nothing
                   }

               }
           });




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
        if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null &&
        !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
            edt_celular.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
        } else{
            edt_celular.setHint("Ingrese su celular");
        }


        Button btn_continuar = itemView.findViewById(R.id.btnRegistrar);
        btn_continuar.setOnClickListener(v -> {

            if (TextUtils.isEmpty(edt_nombre.getText().toString())){
                Toast.makeText(this, "Ingrese su nombre y apellido por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(edt_fechadenacimiento.getText().toString())){
                Toast.makeText(this, "Ingrese su fecha de Nacimiento por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if ((!cbxterminos.isChecked())){
                    Toast.makeText(this, "Acepte los términos y cóndiciones por favor", Toast.LENGTH_SHORT).show();
                    return;
            }else{

                Usuario usuario = new Usuario();
                usuario.setUid(user.getUid());
                usuario.setNombre(edt_nombre.getText().toString());
                usuario.setCelular(edt_celular.getText().toString());
                usuario.setFechadecumpleaños(edt_fechadenacimiento.getText().toString());
                usuario.setRatingValue(0.0);
                usuario.setRatingCount((long) 0);
                usuario.setFechadecreacion(fechacreacion.getText().toString());
                usuario.setFechaactualizacion(fechaactualizacion.getText().toString());

                studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(usuario)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                dialog.dismiss();
                                Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(Splash.this, "Registrado correctamente!!", Toast.LENGTH_SHORT).show();
                        dialog.dismiss();
                        goToHomeActivity(usuario);
                    }
                });
            }

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

        progress_bar.setVisibility(View.VISIBLE);

        Completable.timer(3, TimeUnit.SECONDS,
                AndroidSchedulers.mainThread())
                .subscribe(new Action() {
                    @Override
                    public void run() throws Exception {
                        firebaseAuth.addAuthStateListener(listener);
                    }
                });
    }

    private void goToHomeActivity(Usuario usuario) {
        Comun.actualUsuario = usuario; //init value
        Toast.makeText(this, "Bienvenido " +usuario.getNombre(), Toast.LENGTH_SHORT).show();
        startActivity(new Intent(Splash.this,MainActivity.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == LOGIN_REQUEST_CODE){
            IdpResponse response = IdpResponse.fromResultIntent(data);
            if (resultCode == RESULT_OK){
                FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            } else {
                Toast.makeText(this, "[Error]: " + response.getError().getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }
}