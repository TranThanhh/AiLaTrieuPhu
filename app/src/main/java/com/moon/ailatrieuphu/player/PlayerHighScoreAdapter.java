package com.moon.ailatrieuphu.player;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.model.User;

import java.text.ParseException;
import java.util.List;

public class PlayerHighScoreAdapter extends  RecyclerView.Adapter<PlayerHighScoreAdapter.ViewHolder> {

    private List<User> playerListHighScore;
    private Context context;

    public PlayerHighScoreAdapter(List<User> playerListHighScore, Context context) {
        this.playerListHighScore = playerListHighScore;
        this.context = context;
    }

    @NonNull
    @Override
    public PlayerHighScoreAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_player_high_score, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull PlayerHighScoreAdapter.ViewHolder holder, int position) {
        try {
            holder.bindData(position);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return playerListHighScore.size();
    }

    public void refresh(List<User> cauHoiList) {
        this.playerListHighScore = cauHoiList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder  {

        private TextView tvRank, tvNickname, tvDiemCao;
        private ImageView imgTop;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvRank = itemView.findViewById(R.id.textviewRank);
            tvNickname = itemView.findViewById(R.id.textviewNickname);
            tvDiemCao = itemView.findViewById(R.id.textviewDiemCao);
            imgTop = itemView.findViewById(R.id.imageviewTop);
        }

        public void bindData(int position) throws ParseException {
            User user = playerListHighScore.get(position);
            tvRank.setText(position+1+"");
            tvNickname.setText(user.getNickname()+"");
            tvDiemCao.setText(user.getDiemCao()+"");
            switch (position){
                case 0:
                        imgTop.setImageResource(R.drawable.ic_top1);
                        tvNickname.setBackgroundResource(R.drawable.background_top_diem_cao_nickname);
                        break;
                case 1:
                        imgTop.setImageResource(R.drawable.ic_top2);
                        tvNickname.setBackgroundColor(Color.TRANSPARENT);
                        break;
                default:
                        imgTop.setImageResource(R.drawable.ic_top);
                        tvNickname.setBackgroundColor(Color.TRANSPARENT);
                        break;
            }
        }

    }
}
