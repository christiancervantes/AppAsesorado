package com.example.appasesorado.Callback;

import com.example.appasesorado.Modelos.CommentModel;

import java.util.List;

public
interface ICommentCallbackListener {
    void onCommentLoadSuccess(List<CommentModel> commentModels);
    void onCommentLoadFailed(String message);
}
