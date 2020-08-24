package com.example.appasesorado.Adaptadores;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.media.Rating;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasesorado.Modelos.Asesor;
import com.example.appasesorado.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.List;

import butterknife.BindView;

public class AdapterAsesor extends RecyclerView.Adapter<AdapterAsesor.MyHolder> {
    Context context;
    List<Asesor> AsesorList;

    Asesor asesor;

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

        float rating =ratingValue.floatValue()/ratingCount;

        String valoracion1 = String.valueOf(rating);


        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        assert user != null;
        String uid = user.getUid();
        myHolder.namease.setText(nombre);
        myHolder.skillase.setText(skill);
        myHolder.valoracion1.setText(valoracion1);
        if (AsesorList.get(position).getRatingValue() != null)
        myHolder.rating_bar2.setRating(AsesorList.get(position).getRatingValue().floatValue()/AsesorList.get(position).getRatingCount());

        myHolder.comentase.setText(comentario);

        //myHolder.rating_bar2.setIsIndicator(true);

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

                    } else {
                        myHolder.completarasesoria.setVisibility(View.GONE);
                        myHolder.cancelarasesoria.setVisibility(View.GONE);
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
                Button btn_continuar = itemView.findViewById(R.id.btn_siguiente);
                ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
                    @Override
                    public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                        btn_continuar.setVisibility(View.VISIBLE);
                        txtcomment.setVisibility(View.VISIBLE);
                        //colocar logica de suma y promedio de valoracion para el asesor(consultar antes la valoracion del asesor)

                    }
                });
                btn_continuar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        dialog.dismiss();
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
        }

    }
}
