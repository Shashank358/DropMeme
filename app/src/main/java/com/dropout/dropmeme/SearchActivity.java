package com.dropout.dropmeme;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.Toast;

import com.dropout.dropmeme.Adapters.AllUsersAdapter;
import com.dropout.dropmeme.Adapters.HomeContentAdapter;
import com.dropout.dropmeme.Models.HomeMeme;
import com.dropout.dropmeme.Models.Users;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class SearchActivity extends AppCompatActivity {

    private Toolbar toolbar;
    private RecyclerView allUsersList;
    private LinearLayoutManager layoutManager;

    private FirebaseFirestore db;
    private AllUsersAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        toolbar = findViewById(R.id.search_user_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(toolbar.getTitle());

        allUsersList = findViewById(R.id.all_users_list);
        layoutManager = new LinearLayoutManager(this);
        allUsersList.setLayoutManager(layoutManager);
        allUsersList.setItemAnimator(new DefaultItemAnimator());

        db = FirebaseFirestore.getInstance();

        Query query = db.collection("Users");
        final FirestoreRecyclerOptions<Users> options = new FirestoreRecyclerOptions.Builder<Users>()
                .setQuery(query, Users.class)
                .build();

        adapter = new AllUsersAdapter(getApplicationContext(), options);
        allUsersList.setAdapter(adapter);

        adapter.setOnItemClick(new AllUsersAdapter.OnItemClick() {
            @Override
            public void getPosition(String userId) {
                Intent profileIntent = new Intent(SearchActivity.this, ProfileActivity.class);
                profileIntent.putExtra("other_userId", userId);
                startActivity(profileIntent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.search_user_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.app_bar_search:
                Toast.makeText(this, "clicked", Toast.LENGTH_SHORT).show();
        }

        return true;
    }
}
