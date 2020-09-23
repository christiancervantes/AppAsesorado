package com.example.appasesorado.ui.asesor;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appasesorado.Comun.Comun;
import com.example.appasesorado.Modelos.Asesor;
import com.example.appasesorado.Modelos.CommentModel;

public class AsesorViewModel extends ViewModel {
    private MutableLiveData<Asesor> mutableLiveDataAsesor;
    private MutableLiveData<CommentModel> mutableLiveDataComment;

    public void setCommentModel( CommentModel commentModel){
        if (mutableLiveDataComment !=null)
            mutableLiveDataComment.setValue(commentModel);
    }

    public MutableLiveData<CommentModel> getMutableLiveDataComment() {
        return mutableLiveDataComment;
    }

    public AsesorViewModel() {
        mutableLiveDataComment = new MutableLiveData<>();
    }

    public MutableLiveData<Asesor> getMutableLiveDataAsesor() {
        if (mutableLiveDataAsesor ==null)
            mutableLiveDataAsesor = new MutableLiveData<>();
        mutableLiveDataAsesor.setValue(Comun.asesorseleccionado);
        return mutableLiveDataAsesor;
    }

    public void setAsesor(Asesor asesor) {
        if (mutableLiveDataAsesor != null)
            mutableLiveDataAsesor.setValue(asesor);
    }
}