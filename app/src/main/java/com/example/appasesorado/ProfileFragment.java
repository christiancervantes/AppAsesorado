package com.example.appasesorado;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.cardview.widget.CardView;
import androidx.core.view.MenuItemCompat;
import androidx.fragment.app.Fragment;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RatingBar;
import android.widget.TextView;

import com.example.appasesorado.Modelos.Asesor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.HashMap;

import butterknife.BindView;


public class ProfileFragment extends Fragment {

    FirebaseAuth firebaseAuth;
    FirebaseUser user;
    FirebaseDatabase firebaseDatabase;
    DatabaseReference databaseReference;
    TextView nameTv, celulartv;
    CircularImageView imgavatar;
    Asesor asesor;

    RatingBar rating_bar3;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);//to show menu
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        user = firebaseAuth.getCurrentUser();
        firebaseDatabase = FirebaseDatabase.getInstance();
        databaseReference = firebaseDatabase.getReference("estudiantes");

        ((AppCompatActivity) getActivity()).getSupportActionBar().setTitle("Mi Perfil");
        imgavatar = view.findViewById(R.id.imgavatar);
        nameTv = view.findViewById(R.id.txtnombre);
        rating_bar3 = view.findViewById(R.id.rating_bar3);
        celulartv = view.findViewById(R.id.txtcelular);

        imgavatar.setOnClickListener(view1 -> {
            showDialogSelecterAvatar();
        });
        Query query = databaseReference.orderByChild("idEstudiante").equalTo(user.getUid());
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                //revisar antes de obtener los datos
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    //get data
                    Double ratingValue = Double.valueOf("" + ds.child("ratingValue").getValue());
                    Long ratingCount = Long.valueOf("" + ds.child("ratingCount").getValue());

                    String name = "" + ds.child("nombre").getValue();
                    String celular = "Tu número es " + ds.child("celular").getValue();
                    String tipo = "" + ds.child("tipo").getValue();
                    switch (tipo){
                        case "estu1":
                            imgavatar.setImageResource(R.drawable.man1);
                            break;
                        case "estu2":
                            imgavatar.setImageResource(R.drawable.man2);
                            break;
                        case "estu3":
                            imgavatar.setImageResource(R.drawable.man3);
                            break;
                        case "estu4":
                            imgavatar.setImageResource(R.drawable.girl11);
                            break;
                        case "estu5":
                            imgavatar.setImageResource(R.drawable.woman2);
                            break;
                        case "estu6":
                            imgavatar.setImageResource(R.drawable.woman);
                            break;
                        default:
                            imgavatar.setImageResource(R.drawable.whatsapp);
                            break;
                    }

                    //set data
                    nameTv.setText(name);

                    if (ratingValue != null && ratingCount != null)
                        rating_bar3.setRating(ratingValue.floatValue() / ratingCount);

                    celulartv.setText(celular);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        return view;
    }

    private void showDialogSelecterAvatar() {

        //carga vista de selector de avatares
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(true);
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.layout_avatar_select, null);
        builder.setView(itemView);
        final AlertDialog dialog = builder.create();
        dialog.show();


        CardView estu1=itemView.findViewById(R.id.card_estu1);
        CardView estu2=itemView.findViewById(R.id.card_estu2);
        CardView estu3=itemView.findViewById(R.id.card_estu3);
        CardView estu4=itemView.findViewById(R.id.card_estu4);
        CardView estu5=itemView.findViewById(R.id.card_estu5);
        CardView estu6=itemView.findViewById(R.id.card_estu6);
        estu1.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu1");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();
        });
        estu2.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu2");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();
        });
        estu3.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu3");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();
        });
        estu4.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu4");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();
        });
        estu5.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu5");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();

        });
        estu6.setOnClickListener(view -> {
            DatabaseReference dbrefx = FirebaseDatabase.getInstance().getReference("estudiantes").child(user.getUid());
            HashMap<String, Object> hashMap3 = new HashMap<>();
            hashMap3.put("tipo", "estu6");
            dbrefx.updateChildren(hashMap3);
            dialog.dismiss();
        });




    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        //GET ITEM ID
        int id = item.getItemId();
        if (id == R.id.action_logout) {
            firebaseAuth.signOut();
            checkUserStatus();
        }
        return super.onOptionsItemSelected(item);
    }

    //Revisar el estado del usuario
    private void checkUserStatus() {
        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            //user is signed in stay here
            //set email of logged in user
            //mProfileTV.setText(user.getEmail());
        } else {
            //user is signed in stay here
            startActivity(new Intent(getActivity(), Splash.class));
            getActivity().finish();
        }
    }
}