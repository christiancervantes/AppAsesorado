package com.example.appasesorado.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasesorado.Callback.ICommentCallbackListener;
import com.example.appasesorado.Comun.Comun;
import com.example.appasesorado.Modelos.Asesor;
import com.example.appasesorado.Modelos.CommentModel;
import com.example.appasesorado.R;
import com.example.appasesorado.ui.CommentFragment;
import com.example.appasesorado.ui.CommentViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import butterknife.BindView;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class AdapterAsesor extends RecyclerView.Adapter<AdapterAsesor.MyHolder> {
    Context context;
    List<Asesor> AsesorList;

    int position1;

    FirebaseDatabase database;
    DatabaseReference commentRef;

    @BindView(R.id.rating_bar2)
    RatingBar rating_bar2;

    public AdapterAsesor(Context context, List<Asesor> asesorList) {
        this.context = context;
        AsesorList = asesorList;
    }

    @NonNull
    @Override
    public AdapterAsesor.MyHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.row_asesor, parent, false);
        return new MyHolder(view);

    }


    @Override
    public void onBindViewHolder(@NonNull AdapterAsesor.MyHolder myHolder, int position) {

        String aseuid = AsesorList.get(position).getUid();
        String nombre = AsesorList.get(position).getNombre();
        String skill = AsesorList.get(position).getSkill();
        String celular = AsesorList.get(position).getCelular();
        String comentario = AsesorList.get(position).getComentario();
        Double ratingValue = AsesorList.get(position).getRatingValue();
        Long ratingCount = AsesorList.get(position).getRatingCount();


        //String valoracion1 = String.valueOf(AsesorList.get(position).getRatingValue()/AsesorList.get(position).getRatingCount());

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        myHolder.namease.setText(nombre);
        myHolder.skillase.setText(skill);

        //myHolder.valoracion1.setText(valoracion1);

        if (AsesorList.get(position).getRatingValue() != null)
        myHolder.rating_bar2.setRating(AsesorList.get(position).getRatingValue().floatValue() / AsesorList.get(position).getRatingCount());

        myHolder.comentase.setText(comentario);

        myHolder.mostrarcomentarios.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //inicias el fragment comment llamando con el uid del asesor
                Comun.asesorseleccionado = AsesorList.get(position);

                //para llamar a un fragmento desde una actividad recycler
                AppCompatActivity activity = (AppCompatActivity) view.getContext();
                CommentFragment commentFragment = CommentFragment.getInstance();
                commentFragment.show(activity.getSupportFragmentManager(), "CommentFragment");

            }
        });

        myHolder.celulartv.setOnClickListener(view -> {
            String timeStamp = String.valueOf(System.currentTimeMillis());

            boolean installed = appInstalledOrNot("com.whatsapp");
            if (installed) {
                //Guardar la asesoria dentro de la base de datos
                HashMap<String, Object> asesoria = new HashMap<>();
                asesoria.put("asesor", "" + aseuid);
                asesoria.put("estudiante", "" + uid);
                asesoria.put("timeStamp", "" + timeStamp);
                asesoria.put("estado", "" + "en asesoria");
                DatabaseReference referenceaseasoria = FirebaseDatabase.getInstance().getReference("asesorias");
                referenceaseasoria.child(timeStamp).setValue(asesoria).addOnSuccessListener(aVoid -> {
                    //actualizar estado del asesor
                    DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("asesores").child(aseuid);
                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("estado", "en asesoria");
                    dbref.updateChildren(hashMap);

                    //Intent para abrir whatsapp
                    Intent intentWS = new Intent(Intent.ACTION_VIEW);
                    intentWS.setData(Uri.parse("http://api.whatsapp.com/send?phone=" + "+51" + celular + "&text=" + ""));
                    context.startActivity(intentWS);



                }).addOnFailureListener(e -> {
                    Toast.makeText(context, "No se ha podido contactar al asesor", Toast.LENGTH_SHORT).show();
                });

            } else {
                Toast.makeText(context, "WhatsApp no esta instalado en su dipositivo.", Toast.LENGTH_SHORT).show();
            }

        });


        //Cargar las asesorias registradas por el usuario
        Query query = FirebaseDatabase.getInstance().getReference("asesorias").orderByChild("estudiante").equalTo(uid);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot ds : snapshot.getChildren()) {
                    String estate = "" + ds.child("estado").getValue();
                    if (AsesorList.get(position).getEstado().equals("en asesoria") && AsesorList.get(position).getEstado().equals(estate)) {
                        myHolder.completarasesoria.setVisibility(View.VISIBLE);
                        myHolder.cancelarasesoria.setVisibility(View.VISIBLE);
                        myHolder.celulartv.setVisibility(View.GONE);



                    } else {
                        myHolder.completarasesoria.setVisibility(View.GONE);
                        myHolder.cancelarasesoria.setVisibility(View.GONE);
                        myHolder.celulartv.setVisibility(View.VISIBLE);
                    }
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Accion de completar asesoria
        myHolder.completarasesoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //actulizar estado de la asesoria
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("asesores").child(aseuid);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("estado", "finish asesoria");
                dbref.updateChildren(hashMap);


                //carga vista emergente para la valoracion
                AlertDialog.Builder builder = new AlertDialog.Builder(context);
                builder.setCancelable(true);
                View itemView = LayoutInflater.from(context).inflate(R.layout.layout_valoracion, null);
                builder.setView(itemView);
                final AlertDialog dialog = builder.create();
                dialog.show();


                RatingBar ratingBar = itemView.findViewById(R.id.rating_bar_toset);
                EditText txtcomment = itemView.findViewById(R.id.commenttxt);
                TextInputLayout textfield = itemView.findViewById(R.id.textfield);
                Button btn_continuar = itemView.findViewById(R.id.btn_siguiente);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        btn_continuar.setVisibility(View.VISIBLE);
                        txtcomment.setVisibility(View.VISIBLE);
                        textfield.setHint("Ingrese su comentario...");
                        //colocar logica de suma y promedio de valoracion para el asesor(consultar antes la valoracion del asesor)

                    }
                });
                btn_continuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        database = FirebaseDatabase.getInstance();
                        commentRef = database.getReference(Comun.COMMENT_REF);


                        CommentModel commentModel = new CommentModel();
                        commentModel.setName(Comun.actualUsuario.getNombre());
                        commentModel.setComment(txtcomment.getText().toString());
                        commentModel.setRatingValue(ratingBar.getRating());
                        Map<String, Object> serverTimeStamp = new HashMap<>();
                        serverTimeStamp.put("timeStamp", ServerValue.TIMESTAMP);
                        commentModel.setCommentTimeStamp(serverTimeStamp);
                        //foodDetailViewModel.setCommentModel(commentModel);



                        FirebaseDatabase.getInstance()
                                .getReference(Comun.COMMENT_REF)
                                .child(AsesorList.get(position).getUid())
                                .push()
                                .setValue(commentModel)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()){
                                            //despues de enviar el comentario, se debe actualziar el rating
                                            dialog.dismiss();
                                            addRatingToFood(commentModel.getRatingValue());
                                        }
                                    }
                                });

                    }
                });

            }
        });
        myHolder.cancelarasesoria.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference dbref = FirebaseDatabase.getInstance().getReference("asesores").child(aseuid);
                HashMap<String, Object> hashMap = new HashMap<>();
                hashMap.put("estado", "no asesoria");
                dbref.updateChildren(hashMap);

                //falta agregar lo de remover de la base de datos al cancelar(yo lo pongo owo)
                //falta lo de y lo de la valoracion agregar comentarios, sus vistas ya las cree estan arriba

            }
        });
    }



    private void addRatingToFood(float ratingValue) {
        FirebaseDatabase.getInstance()
                .getReference(Comun.ASESOR_REF)
                .child((AsesorList.get(position1).getUid())) //selecciona asesor
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if (dataSnapshot.exists()){
                            Asesor asesor = dataSnapshot.getValue(Asesor.class);
                            asesor.setUid((AsesorList.get(position1).getUid()));

                            //aplicar rating
                            if (asesor.getRatingValue()==null)
                                asesor.setRatingValue(0d);
                            if (asesor.getRatingCount()==null)
                                asesor.setRatingCount(0l);

                            double sumRating = asesor.getRatingValue() + ratingValue;
                            long ratingCount = asesor.getRatingCount() + 1;
                            double valoracion1 = sumRating/ratingCount;


                            Map<String,Object> updateData= new HashMap<>();
                            updateData.put("ratingValue",sumRating);
                            updateData.put("ratingCount",ratingCount);
                            updateData.put("valoracion1",valoracion1);

                            //actualziar la data en la variable
                            asesor.setRatingValue(sumRating);
                            asesor.setRatingCount(ratingCount);
                            asesor.setValoracion1((long) valoracion1);

                            //waitingDialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
                            //waitingDialog.show();
                            //waitingDialog.setMessage("calificando...");

                            dataSnapshot.getRef()
                                    .updateChildren(updateData)
                                    .addOnCompleteListener(task -> {

                                        if (task.isSuccessful()){

                                            Toast.makeText(context, "Gracias por calificar ", Toast.LENGTH_SHORT).show();
                                            Comun.asesorseleccionado = asesor;
                                            //foodDetailViewModel.setFoodModel(asesor); //refrescar
                                        }
                                    });
                        }
                        else{

                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                        Toast.makeText(context, ""+databaseError.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private boolean appInstalledOrNot(String url) {
        PackageManager packageManager = context.getPackageManager();
        boolean app_installed;
        try {
            packageManager.getPackageInfo(url, PackageManager.GET_ACTIVITIES);
            app_installed = true;
        } catch (PackageManager.NameNotFoundException ex) {
            app_installed = false;
        }
        return app_installed;
    }

    @Override
    public int getItemCount() {
        return AsesorList.size();
    }



    public static class MyHolder extends RecyclerView.ViewHolder {
        TextView namease, skillase, comentase;
        RatingBar rating_bar2 ;
        ImageView celulartv;
        TextView valoracion1;
        Button completarasesoria;
        Button cancelarasesoria;
        LinearLayout mostrarcomentarios;



        public MyHolder(@NonNull View itemView) {
            super(itemView);
            namease = itemView.findViewById(R.id.txt_comment_name);
            skillase = itemView.findViewById(R.id.txt_skill);
            comentase = itemView.findViewById(R.id.txt_comment);
            rating_bar2 = itemView.findViewById(R.id.rating_bar2);
            valoracion1 = itemView.findViewById(R.id.valoracion1);
            celulartv = itemView.findViewById(R.id.tvwas);
            completarasesoria = itemView.findViewById(R.id.finishasedsoria);
            cancelarasesoria = itemView.findViewById(R.id.cancelarasesoria);
            mostrarcomentarios = itemView.findViewById(R.id.mostrarcomentarios);
        }

    }
}
