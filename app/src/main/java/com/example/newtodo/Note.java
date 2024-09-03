package com.example.newtodo;

import com.google.firebase.Timestamp;

public class Note {

    String Title;
    private String priority;
    String Content;
        Timestamp timestamp;

    @Override
    public String toString() {
        return "Note{" +
                "Title='" + Title + '\'' +
                ", priority='" + priority + '\'' +
                ", Content='" + Content + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }

    public String getPriority() {
        return priority;
    }

    public void setPriority(String priority) {
        this.priority = priority;
    }

    public String getTitle() {
        return Title;
    }

    public void setTitle(String title) {
        Title = title;
    }

    public String getContent() {
        return Content;
    }

    public void setContent(String content) {
        Content = content;
    }

    public Timestamp getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Timestamp timestamp) {
        this.timestamp = timestamp;
    }

    public Note(String title, String priority, String content, Timestamp timestamp) {
        Title = title;
        this.priority = priority;
        Content = content;
        this.timestamp = timestamp;
    }

    public Note() {

    }
}
