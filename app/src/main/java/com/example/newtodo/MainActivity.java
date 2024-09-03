package com.example.newtodo;


import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.ImageButton;
import android.widget.PopupMenu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.Query;

public class MainActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ImageButton menu;
    FloatingActionButton add;
    NoteAdapter noteAdapter; // Adapter for Firebase Firestore
    //////////////////////
    String[] priorityItems = {"All", "Low", "Medium", "High"};
    AutoCompleteTextView autoCompleteTextView;
    ArrayAdapter<String> arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize views and set listeners
        add = findViewById(R.id.addnotebtn);
        add.setOnClickListener(v -> startActivity(new Intent(MainActivity.this, NoteDetails.class)));

        recyclerView = findViewById(R.id.recyclerView);
        menu = findViewById(R.id.menu);
        menu.setOnClickListener(v -> showMenu());
        ////////
        autoCompleteTextView = findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_items, priorityItems);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener((adapterView, view, i, l) -> {
            String selectedPriority = adapterView.getItemAtPosition(i).toString();
            setupRecyclerView(selectedPriority); // Set up RecyclerView with the selected priority
        });


        setupRecyclerView("All");
    }
    private void setupRecyclerView(String priority) {
        Query query;

        if (priority.equals("All")) {
            query = utility.getCollectionReference()
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        } else {
            query = utility.getCollectionReference()
                    .whereEqualTo("priority", priority)
                    .orderBy("timestamp", Query.Direction.DESCENDING);
        }

        FirestoreRecyclerOptions<Note> options = new FirestoreRecyclerOptions.Builder<Note>()
                .setQuery(query, Note.class)
                .build();

        if (noteAdapter != null) {
            noteAdapter.stopListening();
        }

        noteAdapter = new NoteAdapter(options, this);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(noteAdapter);
        noteAdapter.startListening();
    }


    private void showMenu() {
        PopupMenu popupMenu = new PopupMenu(MainActivity.this, menu);

        // Get the current user's email from FirebaseAuth
        String userEmail = FirebaseAuth.getInstance().getCurrentUser().getEmail();

        // Add the user's email to the menu
        if (userEmail != null && !userEmail.isEmpty()) {
            popupMenu.getMenu().add(userEmail); // Display user's email at the top
        }
        popupMenu.getMenu().add("Logout");
        popupMenu.show();
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem menuItem) {
                if (menuItem.getTitle().equals("Logout")) {
                    FirebaseAuth.getInstance().signOut();
                    startActivity(new Intent(MainActivity.this, LoginActivity.class));
                    finish();
                    return true;
                }
                return false;
            }
        });
    }
    @Override
    protected void onStart() {
        super.onStart();
        noteAdapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        noteAdapter.stopListening();
    }

    @Override
    protected void onResume() {
        super.onResume();
        noteAdapter.notifyDataSetChanged();
    }
}
