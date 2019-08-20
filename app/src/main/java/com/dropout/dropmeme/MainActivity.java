package com.dropout.dropmeme;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.app.SearchableInfo;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.dropout.dropmeme.Fragments.CategoryFragment;
import com.dropout.dropmeme.Fragments.HomeFragment;
import com.dropout.dropmeme.Fragments.ProfileFragment;
import com.dropout.dropmeme.Fragments.TrendingFragment;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity implements BottomNavigationView.OnNavigationItemSelectedListener {

    private BottomNavigationView navigationView;
    private Toolbar toolbar;
    private FirebaseAuth mAuth;
    private ImageView searchIcon, settingIcon;
    private FloatingActionButton createMemeBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();

        toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle(toolbar.getTitle());

        searchIcon = findViewById(R.id.main_search_icon);
        settingIcon = findViewById(R.id.main_setting_icon);
        createMemeBtn = findViewById(R.id.create_meme_fab_btn);

        createMemeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent createIntent = new Intent(MainActivity.this, CreateMemeActivity.class);
                createIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                createIntent.putExtra("path", "default");
                startActivity(createIntent);
            }
        });

        navigationView =findViewById(R.id.main_bottom_navigation);
        navigationView.setOnNavigationItemSelectedListener(this);
        loadFragments(new HomeFragment());

        searchIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SearchActivity.class);
                startActivity(intent);
            }
        });

        settingIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, SettingActivity.class);
                startActivity(intent);
            }
        });
    }

    private boolean loadFragments(HomeFragment homeFragment) {

        if (homeFragment != null){
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, homeFragment)
                    .commit();
            return true;
        }
        return false;
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        if (menuItem.getItemId() == R.id.menu_home){
            HomeFragment homeFragment = new HomeFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, homeFragment)
                    .commit();
        }else if (menuItem.getItemId() == R.id.menu_trend){
            TrendingFragment trendingFragment = new TrendingFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, trendingFragment)
                    .commit();
        }else if (menuItem.getItemId() == R.id.menu_category){
            CategoryFragment categoryFragment = new CategoryFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, categoryFragment)
                    .commit();

        }else if (menuItem.getItemId() == R.id.menu_profile){
            ProfileFragment profileFragment = new ProfileFragment();
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.main_container, profileFragment)
                    .commit();
        }
        return true;
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser firebaseUser = mAuth.getCurrentUser();
        if (firebaseUser == null){
            Intent authIntent = new Intent(MainActivity.this, AuthActivity.class);
            startActivity(authIntent);
            finish();
        }
    }
}
