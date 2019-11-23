package com.moon.ailatrieuphu.adminpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.view.menu.MenuBuilder;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.moon.ailatrieuphu.R;
import com.moon.ailatrieuphu.adminpage.cauhoi.AddCauHoiFragment;
import com.moon.ailatrieuphu.adminpage.cauhoi.CauHoiFragment;
import com.moon.ailatrieuphu.adminpage.member.MemberFragment;
import com.moon.ailatrieuphu.api.APIConnect;
import com.moon.ailatrieuphu.api.APIService;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AdminMainActivity extends AppCompatActivity {

    private BottomNavigationView bottomNav;
    private Toolbar toolbar;

    private APIService apiService;
    private FragmentManager fragmentManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        setControl();

        toolbar.setTitle("Ai Là Triệu Phú ??");
        fragmentManager.beginTransaction().add(R.id.mainFragmentContainerAdmin,new CauHoiFragment(),"CauHoiFrag").commit();

        setEvent();
    }

    private void setEvent() {
        bottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
                switch (menuItem.getItemId()){
                    case R.id.navigationQuestion:
                        if(fragmentManager.findFragmentByTag("CauHoiFrag")==null){
                            fragmentManager.beginTransaction().replace(R.id.mainFragmentContainerAdmin,new CauHoiFragment(),"CauHoiFrag").commit();
                        }
                        toolbar.getMenu().findItem(R.id.menuAddQuestion).setVisible(true);
                        break;
                    case R.id.navigationMember:
                        if(fragmentManager.findFragmentByTag("MemberFrag")==null){
                            fragmentManager.beginTransaction().replace(R.id.mainFragmentContainerAdmin,new MemberFragment(),"MemberFrag").commit();
                        }
                        toolbar.getMenu().findItem(R.id.menuAddQuestion).setVisible(false);
                        break;
                }
                return true;
            }
        });


    }

    private void setControl() {
        bottomNav =findViewById(R.id.bottomNavigationAdmin);
        toolbar=findViewById(R.id.toolbarAdmin);

        setSupportActionBar(toolbar);
        fragmentManager=getSupportFragmentManager();
        apiService= APIConnect.getServer();
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
                Toast.makeText(this, "Profile", Toast.LENGTH_SHORT).show();
                break;

            case R.id.menuLogout:
                Toast.makeText(this, "Logout", Toast.LENGTH_SHORT).show();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
