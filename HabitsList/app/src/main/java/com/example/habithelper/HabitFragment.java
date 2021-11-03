package com.example.habithelper;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link HabitFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class HabitFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    ArrayList<Habit> HabitsList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.Adapter HabitsAdapter;

    public HabitFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Habits.
     */
    // TODO: Rename and change types and number of parameters
    public static HabitFragment newInstance(String param1, String param2) {
        HabitFragment fragment = new HabitFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        if (getArguments() != null) {
//            mParam1 = getArguments().getString(ARG_PARAM1);
//            mParam2 = getArguments().getString(ARG_PARAM2);
//        }

        HabitsList.add(new Habit("Exercise"));
        HabitsList.add(new Habit("Meditation"));

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_habits, container, false);

//        initRecyclerView(view);

        recyclerView = view.findViewById(R.id.habits_recycler_view);

        if (getArguments() != null){
            ArrayList<String> habitTitles = new ArrayList<>();
            habitTitles = getArguments().getStringArrayList("habitsTitle");
            for (String eachTitle:habitTitles){
                HabitsList.add(new Habit(eachTitle));
            }

        }

        Log.d("WTF2", "onCreateView: " + HabitsList.size());

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(layoutManager);

        HabitsAdapter = new habitsCustomList(HabitsList, getContext());
        recyclerView.setAdapter(HabitsAdapter);

//        Log.d("WTF", "onCreateView: " + recyclerView.getId());

        Log.d("WTF", "onCreateView: " + recyclerView.getAdapter().getItemCount());

        return view;
    }


//    public void addHabit(Habit habit1) {
//        HabitsList.add(habit1);
//        HabitsAdapter.notifyDataSetChanged();
//        Log.d("TEXT", "onAddHabit: "+String.valueOf(HabitsList.size()));
//    }


}