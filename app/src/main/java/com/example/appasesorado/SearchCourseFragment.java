package com.example.appasesorado;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appasesorado.Adaptadores.AdapterAsesor;
import com.example.appasesorado.Modelos.Asesor;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;

public class SearchCourseFragment extends Fragment {
    FirebaseAuth firebaseAuth;
    RecyclerView recyclerView;
    AdapterAsesor adapterAsesor;
    List<Asesor> AsesorList;
    FirebaseDatabase firebaseDatabase;
    SearchView searchView;



    public SearchCourseFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        setHasOptionsMenu(true);
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_search_course, container, false);
        recyclerView = view.findViewById(R.id.recyclerview_asesor);
        firebaseAuth = FirebaseAuth.getInstance();
        searchView = view.findViewById(R.id.searchview);
        //propiedades del recycler view
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
//iniciar lista de asesores
        AsesorList = new ArrayList<>();
//Obtener todos los asesores
        getAllAsesors();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                //llamado  cuando el usuario presiona buscar desde el teclado
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s);
                } else {
                    getAllAsesors();
                }
                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                if (!TextUtils.isEmpty(s.trim())) {
                    searchUsers(s);

                } else {
                    getAllAsesors();
                }
                return false;
            }
        });
        return view;
    }

    private void getAllAsesors() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("asesores");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AsesorList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Asesor asesor = ds.getValue(Asesor.class);
                    assert asesor != null;
                    if (asesor.isVerificacion()) {
                        if(asesor.isCondicion()){
                            AsesorList.add(asesor);
                            adapterAsesor = new AdapterAsesor(getActivity(), AsesorList);
                            recyclerView.setAdapter(adapterAsesor);
                        }

                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }




    private void searchUsers(final String query) {
        final FirebaseUser fUser = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("asesores");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                AsesorList.clear();
                for (DataSnapshot ds : dataSnapshot.getChildren()) {
                    Asesor asesor = ds.getValue(Asesor.class);
                    //obtener toda la data
                    assert asesor != null;
                    if (asesor.isVerificacion()) {
                        if(asesor.isCondicion()){
                            if (asesor.getNombre().toLowerCase().contains(query.toLowerCase()) ||
                                    asesor.getCurso().toLowerCase().contains(query.toLowerCase()) ||
                                    asesor.getValoracion1().toString().contains(query.toLowerCase()) ||
                                    asesor.getSkill().toLowerCase().contains(query.toLowerCase())) {

                                AsesorList.add(asesor);

                            }
                        }
                    }


                    //Adapter
                    adapterAsesor = new AdapterAsesor(getActivity(), AsesorList);
                    //refresh adapter
                    adapterAsesor.notifyDataSetChanged();
                    recyclerView.setAdapter(adapterAsesor);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
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