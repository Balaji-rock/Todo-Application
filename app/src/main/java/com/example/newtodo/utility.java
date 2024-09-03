package com.example.newtodo;

import android.content.Context;
import android.widget.Toast;

import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;

public class utility {
    static void showToast(Context context, String msg){
        Toast.makeText(context, msg, Toast.LENGTH_SHORT).show();
    }
    static CollectionReference getCollectionReference(){
        FirebaseUser currentUser= FirebaseAuth.getInstance().getCurrentUser();
      return   FirebaseFirestore.getInstance().collection("notes").document(currentUser.getUid()).collection("my_notes");
    }
    static String timeStamptoString(Timestamp timestamp){
        return new SimpleDateFormat("MM/dd//yyyy").format(timestamp.toDate());
    }
}
