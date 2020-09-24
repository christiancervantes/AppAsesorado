package com.example.appasesorado.Adaptadores;

import android.content.Context;
import android.text.format.DateUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.appasesorado.Modelos.CommentModel;
import com.example.appasesorado.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;

public
class MyCommentAdapter extends RecyclerView.Adapter<MyCommentAdapter.MyViewHolder> {
    Context context;
    List<CommentModel> commentModelList;

    public MyCommentAdapter(Context context, List<CommentModel> commentModelList) {
        this.context = context;
        this.commentModelList = commentModelList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        return new MyViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_comment_item, parent, false));

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {
        long timeStamp = Long.parseLong(commentModelList.get(position).getCommentTimeStamp().get("timeStamp").toString());
        holder.txt_comment_date.setText(DateUtils.getRelativeTimeSpanString(timeStamp));
        holder.txt_comment.setText(commentModelList.get(position).getComment());
        holder.txt_comment_name.setText(commentModelList.get(position).getName());
        holder.ratingBar.setRating(commentModelList.get(position).getRatingValue());
        String tipo=commentModelList.get(position).getAvatar();
        switch (tipo) {
            case "estu1":
                holder.imgavatarcommet.setImageResource(R.drawable.man1);
                break;
            case "estu2":
                holder.imgavatarcommet.setImageResource(R.drawable.man2);
                break;
            case "estu3":
                holder.imgavatarcommet.setImageResource(R.drawable.man3);
                break;
            case "estu4":
                holder.imgavatarcommet.setImageResource(R.drawable.girl11);
                break;
            case "estu5":
                holder.imgavatarcommet.setImageResource(R.drawable.woman2);
                break;
            case "estu6":
                holder.imgavatarcommet.setImageResource(R.drawable.woman);
                break;
            default:
                holder.imgavatarcommet.setImageResource(R.drawable.man1);

        }



    }

    @Override
    public int getItemCount() {
        return commentModelList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        Unbinder unbinder;
        @BindView(R.id.txt_comment_date)
        TextView txt_comment_date;

        @BindView(R.id.txt_comment)
        TextView txt_comment;

        @BindView(R.id.txt_comment_name)
        TextView txt_comment_name;

        @BindView(R.id.rating_bar)
        RatingBar ratingBar;

        @BindView(R.id.imgavatarcommet)
        ImageView imgavatarcommet;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
            unbinder = ButterKnife.bind(this, itemView);
        }
    }
}
