package com.example.newtodo;

import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;

public class NoteDetails extends AppCompatActivity {

    private EditText title, content;
    private ImageButton saveBtn;
    private TextView pageTitle, deleteBtn;
    private String titleD, contentD, docId;
    private boolean isEditMode;
    private String[] priorityItems = {"Low", "Medium", "High"};
    private AutoCompleteTextView autoCompleteTextView;
    private ArrayAdapter<String> arrayAdapter;
    private String specificItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_note_details);

        title = findViewById(R.id.notesTitletxt);
        content = findViewById(R.id.notesContenttxt);
        saveBtn = findViewById(R.id.savenote);
        pageTitle = findViewById(R.id.pageTitle);
        deleteBtn = findViewById(R.id.deleteBtn);
        autoCompleteTextView = findViewById(R.id.autoComplete);
        arrayAdapter = new ArrayAdapter<>(this, R.layout.dropdown_items, priorityItems);
        autoCompleteTextView.setAdapter(arrayAdapter);
        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                specificItem = adapterView.getItemAtPosition(i).toString();
                Toast.makeText(NoteDetails.this, specificItem, Toast.LENGTH_SHORT).show();
            }
        });

        // Retrieve note details if in edit mode
        titleD = getIntent().getStringExtra("title");
        contentD = getIntent().getStringExtra("content");
        specificItem = getIntent().getStringExtra("priority");
        docId = getIntent().getStringExtra("docId");
        title.setText(titleD);
        content.setText(contentD);
        autoCompleteTextView.setText(specificItem, false);

        if (docId != null && !docId.isEmpty()) {
            isEditMode = true;
        }

        if (isEditMode) {
            deleteBtn.setVisibility(View.VISIBLE);
            pageTitle.setText("Edit Note");
        }

        saveBtn.setOnClickListener(v -> saveNote());

        deleteBtn.setOnClickListener(v -> deleteNoteFromFirebase());
    }

    private void deleteNoteFromFirebase() {
        if (docId != null && !docId.isEmpty()) {
            DocumentReference documentReference = utility.getCollectionReference().document(docId);

            documentReference.delete().addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    utility.showToast(NoteDetails.this, "Note Deleted");
                    finish();
                } else {
                    utility.showToast(NoteDetails.this, "Failed While Deleting");
                }
            });
        }
    }

    private void saveNote() {
        String tit = title.getText().toString();
        String con = content.getText().toString();

        if (tit == null || tit.isEmpty()) {
            title.setError("Title Required");
            return;
        }

        if (specificItem == null) {
            Toast.makeText(NoteDetails.this, "Please select a priority", Toast.LENGTH_SHORT).show();
            return;
        }

        Note note = new Note();
        note.setTitle(tit);
        note.setContent(con);
        note.setPriority(specificItem);
        note.setTimestamp(Timestamp.now());

        saveNoteToFirebase(note);
    }

    private void saveNoteToFirebase(Note note) {
        DocumentReference documentReference;

        if (isEditMode) {
            documentReference = utility.getCollectionReference().document(docId);
        } else {
            documentReference = utility.getCollectionReference().document();
        }

        documentReference.set(note).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                utility.showToast(NoteDetails.this, "Note Saved");
                finish();
            } else {
                utility.showToast(NoteDetails.this, "Failed While Saving");
            }
        });
    }
}
