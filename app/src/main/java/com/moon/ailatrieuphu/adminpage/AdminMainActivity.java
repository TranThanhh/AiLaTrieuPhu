package com.moon.ailatrieuphu.adminpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moon.ailatrieuphu.MainActivity;
import com.moon.ailatrieuphu.Program;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.adminpage.cauhoi.AddCauHoiFragment;
import com.moon.ailatrieuphu.adminpage.cauhoi.CauHoiFragment;
import com.moon.ailatrieuphu.adminpage.moderator.ModeratorFragment;
import com.moon.ailatrieuphu.adminpage.player.PlayerFragment;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;
import com.moon.ailatrieuphu.player.ProfileActivity;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

    private APIService apiService;
    private FragmentManager fragmentManager;

    private boolean isToasted = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setControl();

        toolbar.setTitle("Ai Là Triệu Phú ??");
        if (Program.user.getRoleLevel() == 2)
            bottomNav.getMenu().findItem(R.id.navigationModerator).setVisible(false);
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerAdmin, new CauHoiFragment(), "CauHoiFrag").commit();

        setEvent();
    }

    private void setEvent() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()) {
                    case R.id.navigationQuestion:
                        if (fragmentManager.findFragmentByTag("CauHoiFrag") == null) {
                            fragmentManager.beginTransaction().replace(R.id.mainFragmentContainerAdmin, new CauHoiFragment(), "CauHoiFrag").commit();
                        }
                        toolbar.getMenu().findItem(R.id.menuAddQuestion).setVisible(true);
                        Log.d("TestPosition", "CauHoi: " + Program.positionCauHoi);
                        break;
                    case R.id.navigationPlayer:
                        if (fragmentManager.findFragmentByTag("PlayerFrag") == null) {
                            fragmentManager.beginTransaction().replace(R.id.mainFragmentContainerAdmin, new PlayerFragment(), "PlayerFrag").commit();
                        }
                        toolbar.getMenu().findItem(R.id.menuAddQuestion).setVisible(false);
                        Log.d("TestPosition", "Player: " + Program.positionPlayer);
                        break;
                    case R.id.navigationModerator:
                        if (fragmentManager.findFragmentByTag("ModeratorFrag") == null) {
                            fragmentManager.beginTransaction().replace(R.id.mainFragmentContainerAdmin, new ModeratorFragment(), "ModeratorFrag").commit();
                        }
                        toolbar.getMenu().findItem(R.id.menuAddQuestion).setVisible(false);
                        Log.d("TestPosition", "Moderator: " + Program.positionModerator);
                        break;

                }
                return true;
            }
        });


    }

    private void setControl() {
        bottomNav = findViewById(R.id.bottomNavigationAdmin);
        toolbar = findViewById(R.id.toolbarAdmin);

        setSupportActionBar(toolbar);
        fragmentManager = getSupportFragmentManager();
        apiService = APIConnect.getServer();
    }

    public boolean onCreateOptionsMenu(Menu menu) {
        if (menu instanceof MenuBuilder) {
            ((MenuBuilder) menu).setOptionalIconsVisible(true);
        }
        getMenuInflater().inflate(R.menu.main_option_menu_admin, menu);
        return super.onCreateOptionsMenu(menu);
    }

    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.menuAddQuestion:
                fragmentManager.beginTransaction().add(R.id.fullscreenFragmentContainerAdmin, new AddCauHoiFragment()).addToBackStack(null).commit();
                break;

            case R.id.menuProfile:
                startActivity(new Intent(AdminMainActivity.this, ProfileActivity.class));
                break;

            case R.id.menuLogout:
                Program.user = null;
                finish();
                startActivity(new Intent(AdminMainActivity.this, MainActivity.class));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        isToasted = false;
        Program.user = null;
    }

    @Override
    public void onBackPressed() {
        if (isToasted == false) {
            Toast.makeText(this, "Nhấn lần nữa để thoát!", Toast.LENGTH_SHORT).show();
            isToasted = true;
        } else {
            isToasted = false;
            super.onBackPressed();
            finishAffinity();
        }
    }
}
