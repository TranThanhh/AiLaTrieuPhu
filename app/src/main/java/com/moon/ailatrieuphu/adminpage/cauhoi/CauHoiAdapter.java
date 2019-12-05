package com.moon.ailatrieuphu.adminpage.cauhoi;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.model.CauHoi;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.List;

public class CauHoiAdapter extends RecyclerView.Adapter<CauHoiAdapter.ViewHolder> {

    private List<CauHoi> cauHoiList;
    private Context context;

    private static ClickListener clickListener;

    public CauHoiAdapter(List<CauHoi> cauHoiList, Context context) {
        this.cauHoiList = cauHoiList;
        this.context = context;
    }

    @NonNull
    @Override
    public CauHoiAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_cau_hoi, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CauHoiAdapter.ViewHolder holder, int position) {
        try {
            holder.bindData(position);
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return cauHoiList.size();
    }

    public void refresh(List<CauHoi> cauHoiList) {
        this.cauHoiList = cauHoiList;
        notifyDataSetChanged();
    }

    public class ViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView tvIdCauHoi, tvNoiDung, tvUpdateTime, tvCreateTime, tvUpdateTimeTitle, tvDapAnDung;
        private CardView cardViewCauHoi;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            tvIdCauHoi = itemView.findViewById(R.id.textviewIdCauHoi);
            tvNoiDung = itemView.findViewById(R.id.textviewNoiDung);
            cardViewCauHoi = itemView.findViewById(R.id.cardviewCauHoi);
            tvCreateTime = itemView.findViewById(R.id.textviewCreateTime);
            tvUpdateTime = itemView.findViewById(R.id.textviewUpdateTime);
            tvUpdateTimeTitle = itemView.findViewById(R.id.textviewUpdateTimeTitle);
            tvDapAnDung = itemView.findViewById(R.id.textviewDapAnDung);

            itemView.setOnClickListener(this);
        }

        public void bindData(int position) throws ParseException {
            CauHoi cauHoi = cauHoiList.get(position);

            SimpleDateFormat dateIn = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            SimpleDateFormat dateOut = new SimpleDateFormat("dd/MM/yyyy HH:mm");

            tvIdCauHoi.setText("#" + cauHoi.getIdCauHoi() + ":");
            tvNoiDung.setText(cauHoi.getNoiDung() + "");

            tvCreateTime.setText(dateOut.format(dateIn.parse(cauHoi.getCreateTime())));

            if (cauHoi.getUpdateTime() != null) {
                tvUpdateTimeTitle.setVisibility(View.VISIBLE);
                tvUpdateTime.setText(dateOut.format(dateIn.parse(cauHoi.getUpdateTime())));
            } else {
                tvUpdateTimeTitle.setVisibility(View.GONE);
                tvUpdateTime.setVisibility(View.GONE);
            }

            switch (cauHoi.getDapAnDung()) {
                case "A":
                    tvDapAnDung.setText("A. " + cauHoi.getCauA());
                    break;
                case "B":
                    tvDapAnDung.setText("B. " + cauHoi.getCauB());
                    break;
                case "C":
                    tvDapAnDung.setText("C. " + cauHoi.getCauC());
                    break;
                case "D":
                    tvDapAnDung.setText("D. " + cauHoi.getCauD());
                    break;
            }

            if (position % 2 == 1) {
                cardViewCauHoi.setCardBackgroundColor(ContextCompat.getColor(context, R.color.lightGray2));
            } else {
                cardViewCauHoi.setCardBackgroundColor(ContextCompat.getColor(context, R.color.white));
            }

        }

        @Override
        public void onClick(View view) {
            clickListener.onItemCLick(getAdapterPosition(), view);
        }
    }

    public void setOnItemClick(ClickListener clickListener) {
        CauHoiAdapter.clickListener = clickListener;
    }

    public interface ClickListener {
        void onItemCLick(int position, View v);
    }
}
