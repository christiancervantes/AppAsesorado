package com.example.appasesorado.Modelos;

import java.util.Map;

public
class CommentModel {
    private float ratingValue;
    private String comment,name, id ,idEstudiante, avatar;
    private Map<String,Object> commentTimeStamp;

    public CommentModel() {
    }

    public float getRatingValue() {
        return ratingValue;
    }

    public void setRatingValue(float ratingValue) {
        this.ratingValue = ratingValue;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Map<String, Object> getCommentTimeStamp() {
        return commentTimeStamp;
    }

    public void setCommentTimeStamp(Map<String, Object> commentTimeStamp) {
        this.commentTimeStamp = commentTimeStamp;
    }

    public String getIdEstudiante() {
        return idEstudiante;
    }

    public void setIdEstudiante(String idEstudiante) {
        this.idEstudiante = idEstudiante;
    }

    public String getAvatar() {
        return avatar;
    }

    public void setAvatar(String avatar) {
        this.avatar = avatar;
    }
}
