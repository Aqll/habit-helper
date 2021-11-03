package com.example.habithelper;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class habitsCustomList extends RecyclerView.Adapter<habitsCustomList.MyViewHolder> {

    private ArrayList<Habit> habits_list;
    private Context context;

    public habitsCustomList(ArrayList<Habit> habits_list, Context context) {
        this.habits_list = habits_list;
        this.context = context;
    }

//    public habitsCustomList(List<Habit> habits_list) {
//        this.habits_list = habits_list;
//    }

    @NonNull
    @Override
    public habitsCustomList.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.habit_row_contents, parent,false);

        View view = LayoutInflater.from(context).inflate(R.layout.habit_row_contents, parent,false);

        return new MyViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull habitsCustomList.MyViewHolder holder, int position) {
        holder.habitTitle_textView.setText(habits_list.get(position).getTitle());
    }

    @Override
    public int getItemCount() {
        return habits_list.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView habitTitle_textView;

        public MyViewHolder (View view) {
            super(view);
            habitTitle_textView = view.findViewById(R.id.habitTitleTextView);

        }
    }
}
