package com.moon.ailatrieuphu.player;


import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.DialogFragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.model.User;
import com.moon.ailatrieuphu.utility.LoadingDialog;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PlayerHighScoreDialogFragment extends DialogFragment {

    private RecyclerView rvPlayerHighScore;
    private TextView tvEmpty;

    private APIService apiService;
    private PlayerHighScoreAdapter playerHighScoreAdapter;

    private List<User> playerHighScoreList;
    private boolean isLoaded=false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view= inflater.inflate(R.layout.dialog_fragment_player_high_score, container, false);

        setControl(view);

        return view;
    }

    private void setControl(View view) {
        rvPlayerHighScore =view.findViewById(R.id.recyclerviewPlayerHighScore);
        tvEmpty=view.findViewById(R.id.textviewEmpty);

        apiService= APIConnect.getServer();
        playerHighScoreList =new ArrayList<>();

        getAllPlayerHighScore();
    }

    private void getAllPlayerHighScore() {
        LoadingDialog.show(getContext());
        apiService.getAllPlayerHighScoreActive().enqueue(new Callback<List<User>>() {
            @Override
            public void onResponse(Call<List<User>> call, Response<List<User>> response) {
                LoadingDialog.hide();
                if (response.code() == 200) {
                    playerHighScoreList = response.body();
                    //playerList.sort(Comparator.comparing(CauHoi::getIdCauHoi).reversed());
                    tvEmpty.setVisibility(View.GONE);
                    rvPlayerHighScore.setVisibility(View.VISIBLE);
                } else {
                    tvEmpty.setVisibility(View.VISIBLE);
                    rvPlayerHighScore.setVisibility(View.GONE);
                }
                if (!isLoaded) {
                    playerHighScoreAdapter = new PlayerHighScoreAdapter(playerHighScoreList, getContext());
                    rvPlayerHighScore.setLayoutManager(new LinearLayoutManager(getContext()));
                    rvPlayerHighScore.setAdapter(playerHighScoreAdapter);
                    isLoaded = true;
                } else {
                    playerHighScoreAdapter.refresh(playerHighScoreList);
                }
            }

            @Override
            public void onFailure(Call<List<User>> call, Throwable t) {
                LoadingDialog.hide();
                Toast.makeText(getContext(), R.string.err_connect, Toast.LENGTH_SHORT).show();
            }
        });
    }
}
