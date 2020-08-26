package com.example.appasesorado.ui;

import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.example.appasesorado.Modelos.CommentModel;

import java.util.List;

public
class CommentViewModel extends ViewModel {
    private MutableLiveData<List<CommentModel>> mutableLiveDataAsesorList;

    public CommentViewModel(){
        mutableLiveDataAsesorList = new MutableLiveData<>();
    }

    public MutableLiveData<List<CommentModel>> getMutableLiveDataAsesorList() {
        return mutableLiveDataAsesorList;
    }

    public void setCommentList(List<CommentModel>commentList){
        mutableLiveDataAsesorList.setValue(commentList);
    }
}
