package com.example.appasesorado;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.appasesorado.Modelos.Usuario;

import android.Manifest;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
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
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import butterknife.BindView;
import butterknife.ButterKnife;
import io.reactivex.Completable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Action;

public class Splash extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 7171;
    private static final int PERMISSION_REQUEST_CODE = 1;

    //variables
    private String fechadenac;
    TextView fechacreacion;
    TextView fechaactualizacion;
    TextView obtenerImei;
    String registro;

    String modelo1;

    //para el ime
    String imei;
    static final Integer PHONESTATS = 0x1;

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
    DatabaseReference celularRef;
    DatabaseReference tokenRef;

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
        celularRef = database.getReference(Comun.CELULARES_REF);
        tokenRef = database.getReference(Comun.TOKEN_REF);



        providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.FacebookBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        firebaseAuth = FirebaseAuth.getInstance();

       listener = myFirebaseAuth -> { //
          FirebaseUser user = firebaseAuth.getCurrentUser();
          if (user != null){

              //update token
              FirebaseInstanceId.getInstance()
                      .getInstanceId()
                      .addOnFailureListener(e -> Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show())
                      .addOnSuccessListener(instanceIdResult -> {
                          Log.d("Token2 --->", instanceIdResult.getToken());
                          Comun.updateToken(Splash.this,instanceIdResult.getToken());
                          
                          tokenRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                  .setValue(instanceIdResult.getToken())
                                  .addOnFailureListener(new OnFailureListener() {
                                      @Override
                                      public void onFailure(@NonNull Exception e) {
                                          
                                      }
                                  }).addOnSuccessListener(new OnSuccessListener<Void>() {
                              @Override
                              public void onSuccess(Void aVoid) {
                                  //Toast.makeText(Splash.this, "enviado el token", Toast.LENGTH_SHORT).show();
                              }
                          });

                      });
              checkUserFromFirebase(user);
          }else{
              showLogin();
          }

       };
                           
       }


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
                            //para pedir el permiso
                            consultarPermiso(Manifest.permission.READ_PHONE_STATE, PHONESTATS);
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

        //obtener imei
        obtenerImei=(TextView) itemView.findViewById(R.id.imeiPhoner);
        EditText edt_nombre = (EditText) itemView.findViewById(R.id.edt_nombre);
        EditText edt_celular = (EditText) itemView.findViewById(R.id.edt_phone);
        //TextView nroRegistro = (TextView)itemView.findViewById(R.id.nroregistro);
        TextView modelo  = (TextView)itemView.findViewById(R.id.modelo);

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


            ocultarTeclado(edt_nombre);

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


        //ya pone el celular de usuario registrado o sino lo debe de ingresar
        if(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null &&
        !TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber())){
            edt_celular.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            edt_celular.setClickable(false);
            edt_celular.setFocusable(false);
        } else{
            edt_celular.setHint("Ingrese su celular");
        }


        Button btn_continuar = itemView.findViewById(R.id.btnRegistrar);
        btn_continuar.setOnClickListener(v -> {

            Date dateAse = new Date();
            DateFormat hourdateFormataa = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
            final String uuid = UUID.randomUUID().toString().replace("-", "");

            if (TextUtils.isEmpty(edt_nombre.getText().toString())){
                Toast.makeText(this, "Ingrese su nombre y apellido por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if (TextUtils.isEmpty(edt_fechadenacimiento.getText().toString())){
                Toast.makeText(this, "Ingrese su fecha de Nacimiento por favor", Toast.LENGTH_SHORT).show();
                return;
            }else if ((!cbxterminos.isChecked())){
                    Toast.makeText(this, "Acepte los términos y cóndiciones por favor", Toast.LENGTH_SHORT).show();
                    return;
            }else {

                //obtener el imei y modelo de celular
                obtenerImei.setText(imei);
                modelo.setText(obtenerNombreDeDispositivo());

                //registro de usuario
                Usuario usuario = new Usuario();
                usuario.setIdEstudiante(user.getUid());
                usuario.setNombre(edt_nombre.getText().toString());
                usuario.setCelular(edt_celular.getText().toString());
                usuario.setFechadecumpleaños(edt_fechadenacimiento.getText().toString());
                usuario.setRatingValue(0.0);
                usuario.setRatingCount((long) 0);
                usuario.setImei(obtenerImei.getText().toString());
                usuario.setFechadecreacion(fechacreacion.getText().toString());
                usuario.setFechaactualizacion(fechaactualizacion.getText().toString());


                celularRef.child(imei)
                        .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {

                                    int nrodeveces = snapshot.child("nroRegistro").getValue(Integer.class);

                                    if (nrodeveces > 3){

                                        //Actualizar la la info del celular dentro de la base de datos "celulares"
                                        DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("celulares").child(imei);
                                        HashMap<String, Object> celular = new HashMap<>();
                                        celular.put("fechaHoraActualizacionBloqueado", hourdateFormataa.format(dateAse));
                                        celular.put("estado", "bloqueado");
                                        referenceaseasoria.updateChildren(celular);

                                        AlertDialog.Builder builder1 = new AlertDialog.Builder(Splash.this, R.style.AlertDialogCustom);
                                        builder1.setCancelable(false);

                                        View itemView1 = LayoutInflater.from(Splash.this).inflate(R.layout.layout_verificacion_imei, null);

                                        builder1.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                                            @Override
                                            public void onClick(DialogInterface dialogInterface, int i) {

                                                dialogInterface.dismiss();
                                                finish();
                                                System.exit(0);
                                            }
                                        });

                                        builder1.setView(itemView1);
                                        final AlertDialog dialog1 = builder1.create();
                                        dialog1.show();

                                    } else if (nrodeveces == 3){

                                        studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(usuario)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnCompleteListener(aVoid -> {

                                            dialog.dismiss();
                                            Toast.makeText(Splash.this, "Registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                            goToHomeActivity(usuario);

                                            //Actualizar la la info del celular dentro de la base de datos "celulares"
                                            DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("celulares").child(imei);
                                                HashMap<String, Object> celular = new HashMap<>();
                                                celular.put("fechaHoraAct3", hourdateFormataa.format(dateAse));
                                            celular.put("nroRegistro", 4);
                                            referenceaseasoria.updateChildren(celular);

                                        });

                                    }else if (nrodeveces == 2){
                                        studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(usuario)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnCompleteListener(aVoid -> {

                                            dialog.dismiss();
                                            Toast.makeText(Splash.this, "Registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                            goToHomeActivity(usuario);


                                            //Actualizar la la info del celular dentro de la base de datos "celulares"
                                            DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("celulares").child(imei);
                                            HashMap<String, Object> celular = new HashMap<>();
                                            celular.put("fechaHoraAct2", hourdateFormataa.format(dateAse));
                                            celular.put("nroRegistro", 3);
                                            referenceaseasoria.updateChildren(celular);

                                        });

                                    }else if (nrodeveces == 1){
                                        studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                                .setValue(usuario)
                                                .addOnFailureListener(new OnFailureListener() {
                                                    @Override
                                                    public void onFailure(@NonNull Exception e) {
                                                        dialog.dismiss();
                                                        Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                    }
                                                }).addOnCompleteListener(aVoid -> {

                                            dialog.dismiss();
                                            Toast.makeText(Splash.this, "Registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                            goToHomeActivity(usuario);


                                            //Actualizar la la info del celular dentro de la base de datos "celulares"
                                            DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("celulares").child(imei);
                                            HashMap<String, Object> celular = new HashMap<>();
                                            celular.put("fechaHoraAct1", hourdateFormataa.format(dateAse));
                                            celular.put("nroRegistro", 2);
                                            referenceaseasoria.updateChildren(celular);

                                        });

                                    }

                                }else {
                                    studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                            .setValue(usuario)
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    dialog.dismiss();
                                                    Toast.makeText(Splash.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                                }
                                            }).addOnCompleteListener(aVoid -> {

                                        dialog.dismiss();
                                        Toast.makeText(Splash.this, "Registrado correctamente!!", Toast.LENGTH_SHORT).show();
                                        goToHomeActivity(usuario);

                                        //Guardar la la info del celular dentro de la base de datos "celulares"
                                        HashMap<String, Object> celular = new HashMap<>();
                                        celular.put("imei", "" + imei);
                                        celular.put("modeloCelular", "" + modelo.getText().toString());
                                        celular.put("fechaHoraAct0", hourdateFormataa.format(dateAse));
                                        celular.put("nroRegistro", 1);
                                        DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("celulares");
                                        referenceaseasoria.child(imei).setValue(celular).addOnSuccessListener(aVoid1 -> {
                                            //nada

                                        }).addOnFailureListener(e -> {

                                            //nada
                                        });

                                    });
                                }
                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
            }

        });
    }

    // Con este método mostramos en un Toast con un mensaje que el usuario ha concedido los permisos a la aplic
    private void consultarPermiso(String permission, Integer requestCode) {
        if (ContextCompat.checkSelfPermission(Splash.this, permission) != PackageManager.PERMISSION_GRANTED) {

            if (ActivityCompat.shouldShowRequestPermissionRationale(Splash.this, permission)) {

                ActivityCompat.requestPermissions(Splash.this, new String[]{permission}, requestCode);

            } else {

                ActivityCompat.requestPermissions(Splash.this, new String[]{permission}, requestCode);
            }
        } else {
            imei = getDeviceId(getApplicationContext());
           // Toast.makeText(this,permission+" El permiso a la aplicación está concedido.", Toast.LENGTH_SHORT).show();
        }
    }

    // Con este método consultamos al usuario si nos puede dar acceso a leer los datos internos del móvil
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults)
    {
        switch (requestCode) {
            case 1: {

                // Validamos si el usuario acepta el permiso para que la aplicación acceda a los datos internos del equipo, si no denegamos el acceso
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    imei = getDeviceId(getApplicationContext());

                } else {

                    Toast.makeText(Splash.this, "Has negado el permiso a la aplicación", Toast.LENGTH_SHORT).show();
                }
                return;
            }
        }
    }

    // Con este método consultamos al usuario si nos puede dar acceso a leer los datos internos del móvil
    private String obtenerIMEI() {


        final TelephonyManager telephonyManager= (TelephonyManager) this.getSystemService(Context.TELEPHONY_SERVICE);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            //Hacemos la validación de métodos, ya que el método getDeviceId() ya no se admite para android Oreo en adelante, debemos usar el método getImei()
           return telephonyManager.getImei();
        }
       else {
        return telephonyManager.getDeviceId();
       }

    }

    public static String getDeviceId(Context context) {

        String deviceId;

        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            deviceId = Settings.Secure.getString(
                    context.getContentResolver(),
                    Settings.Secure.ANDROID_ID);
        } else {
            final TelephonyManager mTelephony = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            if (mTelephony.getDeviceId() != null) {
                deviceId = mTelephony.getDeviceId();
            } else {
                deviceId = Settings.Secure.getString(
                        context.getContentResolver(),
                        Settings.Secure.ANDROID_ID);
            }
        }

        return deviceId;
    }

    //con este metodo se obtiene el fabricante y modelo de ceular
    public String obtenerNombreDeDispositivo() {
        String fabricante = Build.MANUFACTURER;
        String modelo = Build.MODEL;
        if (modelo.startsWith(fabricante)) {
            return primeraLetraMayuscula(modelo);
        } else {
            return primeraLetraMayuscula(fabricante) + " " + modelo;
        }
    }

    //con este metodo la primera letra se escribe en mayuscula para darle un formato
    private String primeraLetraMayuscula(String cadena) {
        if (cadena == null || cadena.length() == 0) {
            return "";
        }
        char primeraLetra = cadena.charAt(0);
        if (Character.isUpperCase(primeraLetra)) {
            return cadena;
        } else {
            return Character.toUpperCase(primeraLetra) + cadena.substring(1);
        }
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
                    //Comun.actualToken = token;

                    Toast.makeText(this, "Bienvenido " +usuario.getNombre(), Toast.LENGTH_SHORT).show();
                    Intent homeIntent = new Intent(Splash.this,MainActivity.class);
                    //homeIntent.putExtra("user",usuario.getUid()); //*****************************************************************aqui
                    //Comun.actualUsuario = localUser;
                    startActivity(homeIntent);
                    finish();


    }

    //para ocultar teclado
    public void ocultarTeclado(EditText edt) {
        InputMethodManager imm = (InputMethodManager)this.getSystemService(Context.
                INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(edt.getWindowToken(), 0);

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