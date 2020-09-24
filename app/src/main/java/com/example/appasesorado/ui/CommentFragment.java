package com.example.appasesorado.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.ViewModelProviders;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.appasesorado.Adaptadores.MyCommentAdapter;
import com.example.appasesorado.Callback.ICommentCallbackListener;
import com.example.appasesorado.Comun.Comun;
import com.example.appasesorado.Modelos.CommentModel;
import com.example.appasesorado.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import dmax.dialog.SpotsDialog;

public class CommentFragment  extends BottomSheetDialogFragment implements ICommentCallbackListener {

    private CommentViewModel commentViewModel;

    private Unbinder unbinder;
    @BindView(R.id.recycler_comment)
    RecyclerView recycler_comment;

    @BindView(R.id.txtnocomment)
    TextView txtnocomment;



    AlertDialog dialog;
    ICommentCallbackListener listener;

    public CommentFragment() {
        listener = this;
    }

    private static CommentFragment instance;

    public static CommentFragment getInstance(){
        if (instance == null)
            instance = new CommentFragment();
        return instance;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View itemView = LayoutInflater.from(getContext()).inflate(R.layout.bottom_sheet_comment_fragment,container,false);

        unbinder = ButterKnife.bind(this,itemView);
        initViews();
        loadCommentsFromFirebase();




        commentViewModel.getMutableLiveDataAsesorList().observe(this, commentModels -> {
            MyCommentAdapter adapter = new MyCommentAdapter(getContext(),commentModels);
            recycler_comment.setAdapter(adapter);
        });
        return itemView;
    }


    private void loadCommentsFromFirebase() {
        dialog.show();
        List<CommentModel> commentModels = new ArrayList<>();
        FirebaseDatabase.getInstance().getReference(Comun.COMMENT_REF)
                .child(Comun.asesorseleccionado.getUid()) // con esto selecciono al asesor seleccionado
                .orderByChild("commentTimeStamp")
                .limitToLast(100)
                .addValueEventListener((new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        for (DataSnapshot commentSnapShot:dataSnapshot.getChildren()){
                            CommentModel commentModel = commentSnapShot.getValue(CommentModel.class);

                            commentModels.add(commentModel);
                        }

                        //detecta si el recycler tiene dato o no, y si no, muestra el texto de que no tiene comentarios
                        if (!commentModels.isEmpty()){
                            listener.onCommentLoadSuccess(commentModels);
                        }else{
                            dialog.dismiss();
                            txtnocomment.setVisibility(View.VISIBLE);
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {
                        listener.onCommentLoadFailed(databaseError.getMessage());
                    }
                }));

    }

    private void initViews() {
        commentViewModel = ViewModelProviders.of(this).get(CommentViewModel.class);
        dialog = new SpotsDialog.Builder().setContext(getContext()).setCancelable(false).build();
        recycler_comment.setHasFixedSize(true);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext(),RecyclerView.VERTICAL,true);
        recycler_comment.setLayoutManager(layoutManager);
        recycler_comment.addItemDecoration(new DividerItemDecoration(getContext(),layoutManager.getOrientation()));
    }

    @Override
    public void onCommentLoadSuccess(List<CommentModel> commentModels) {
        dialog.dismiss();
        commentViewModel.setCommentList(commentModels);
    }

    @Override
    public void onCommentLoadFailed(String message) {
        dialog.dismiss();
        Toast.makeText(getContext(), message, Toast.LENGTH_SHORT).show();
    }
}