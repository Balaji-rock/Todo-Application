package com.example.newtodo;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;

public class NoteAdapter extends FirestoreRecyclerAdapter<Note, NoteAdapter.NoteViewHolder> {

    private final Context context;

    /**
     * Create a new RecyclerView adapter that listens to a Firestore Query.  See {@link
     * FirestoreRecyclerOptions} for configuration options.
     *
     * @param options FirestoreRecyclerOptions for configuring the adapter
     * @param context Context of the activity or fragment
     */
    public NoteAdapter(@NonNull FirestoreRecyclerOptions<Note> options, Context context) {
        super(options);
        this.context = context;
    }

    @Override
    protected void onBindViewHolder(@NonNull NoteViewHolder holder, int position, @NonNull Note note) {
        holder.titleview.setText(note.getTitle());
        holder.contentview.setText(note.getContent());
        String priority = note.getPriority();

        // Set the priority icon based on the priority value
        switch (priority) {
            case "Low":
                holder.priicon.setImageResource(R.drawable.low);
                break;
            case "Medium":
                holder.priicon.setImageResource(R.drawable.medum);
                break;
            case "High":
                holder.priicon.setImageResource(R.drawable.high1);
                break;
            default:
                holder.priicon.setImageResource(R.drawable.low); // Handle unknown priority
                break;
        }

        holder.priitem.setText(priority);
        holder.timestampview.setText(utility.timeStamptoString(note.getTimestamp()));

        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, NoteDetails.class);
            intent.putExtra("title", note.getTitle());
            intent.putExtra("content", note.getContent());
            intent.putExtra("priority", note.getPriority());
            String docId = getSnapshots().getSnapshot(position).getId();
            intent.putExtra("docId", docId);
            context.startActivity(intent);
        });
    }

    @NonNull
    @Override
    public NoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_note, parent, false);
        return new NoteViewHolder(view);
    }

    class NoteViewHolder extends RecyclerView.ViewHolder {
        TextView titleview, contentview, timestampview, priitem;
        ImageView priicon;

        public NoteViewHolder(@NonNull View itemView) {
            super(itemView);
            titleview = itemView.findViewById(R.id.note_title);
            contentview = itemView.findViewById(R.id.note_content);
            timestampview = itemView.findViewById(R.id.timestamp);
            priitem = itemView.findViewById(R.id.pritext);
            priicon = itemView.findViewById(R.id.priorityitem);
        }
    }
}
