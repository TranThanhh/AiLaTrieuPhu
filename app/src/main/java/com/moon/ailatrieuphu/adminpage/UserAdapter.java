package com.moon.ailatrieuphu.adminpage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.ProgressDialogF;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.User;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private List<User> userList;
    private Context context;

    private static ClickListener clickListener;

    private APIService apiService = APIConnect.getServer();

    public UserAdapter(List<User> userList, Context context) {
        this.userList = userList;
        this.context = context;
    }

    @NonNull
    @Override
    public UserAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull UserAdapter.ViewHolder holder, int position) {
        try {
            holder.bindData(position);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return userList.size();
    }

    public void refresh(List<User> cauHoiList) {
        this.userList = cauHoiList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnLongClickListener {

        private TextView tvEmail, tvIdUser, tvCreateTime, tvUpdateTimeTitle, tvUpdateTime, tvNickname, tvDiemCao, tvSoCauHoi;
        private CardView cardViewUser;
        private ImageView imgvAdmin;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvEmail = itemView.findViewById(R.id.textViewEmail);
            tvIdUser = itemView.findViewById(R.id.textviewIdUser);
            tvCreateTime = itemView.findViewById(R.id.textviewCreateTime);
            tvUpdateTimeTitle = itemView.findViewById(R.id.textviewUpdateTimeTitle);
            tvUpdateTime = itemView.findViewById(R.id.textviewUpdateTime);
            tvNickname = itemView.findViewById(R.id.textviewNickname);
            tvDiemCao = itemView.findViewById(R.id.textviewDiemCao);
            tvSoCauHoi = itemView.findViewById(R.id.textviewSoCauHoi);
            cardViewUser = itemView.findViewById(R.id.cardviewUser);
            imgvAdmin = itemView.findViewById(R.id.imageviewAdmin);

            itemView.setOnLongClickListener(this);
        }

        public void bindData(int position) throws ParseException {
            User user = userList.get(position);

            SimpleDateFormat dateIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            if (user.getRoleLevel() == 0) {
                imgvAdmin.setVisibility(View.INVISIBLE);
                tvDiemCao.setVisibility(View.VISIBLE);
                tvDiemCao.setText("Điểm cao: " + user.getDiemCao());
            } else {
                imgvAdmin.setVisibility(View.VISIBLE);
                tvDiemCao.setVisibility(View.INVISIBLE);
            }

            tvIdUser.setText("#" + user.getIdUser() + ":");
            tvCreateTime.setText(dateOut.format(dateIn.parse(user.getCreateTime())));

            if (user.getUpdateTime() != null) {
                tvUpdateTimeTitle.setVisibility(View.VISIBLE);
                tvUpdateTime.setVisibility(View.VISIBLE);
                tvUpdateTime.setText(dateOut.format(dateIn.parse(user.getUpdateTime())));
            } else {
                tvUpdateTimeTitle.setVisibility(View.GONE);
                tvUpdateTime.setVisibility(View.GONE);
            }

            tvEmail.setText("Email: " + user.getEmail());
            tvNickname.setText("Nickname: " + user.getNickname());

            apiService.countCauHoiOfUser(user.getIdUser()).enqueue(new Callback<Integer>() {
                @Override
                public void onResponse(Call<Integer> call, Response<Integer> response) {
                    if (response.body().intValue() == 0) {
                        tvSoCauHoi.setVisibility(View.GONE);
                    } else {
                        tvSoCauHoi.setVisibility(View.VISIBLE);
                        tvSoCauHoi.setText("Số câu hỏi đã tạo: " + response.body());
                    }
                }

                @Override
                public void onFailure(Call<Integer> call, Throwable t) {
                    ProgressDialogF.hideLoading();
                    Toast.makeText(context, R.string.err_connect, Toast.LENGTH_SHORT).show();
                }
            });

            if (position % 2 == 1) {
                cardViewUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lightGray2));
            } else {
                cardViewUser.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

        }

        @Override
        public boolean onLongClick(View view) {
            clickListener.onItemCLick(getAdapterPosition(), view);
            return false;
        }
    }

    public void setOnItemClick(ClickListener clickListener) {
        UserAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemCLick(int position, View v);
    }
}
