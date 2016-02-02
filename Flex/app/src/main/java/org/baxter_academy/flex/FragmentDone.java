package org.baxter_academy.flex;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class FragmentDone extends Fragment {

    LinearLayout linearLayout;
    private Activity activity;

    public FragmentDone() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_todo, container, false);

        // Gets the json string - We're using getActivity instead of this because this doesn't work in this
        SharedPreferences prefs = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
        String json = prefs.getString("tasks", "error");

        // Error will only happen if the Preference does not exist
        if (!json.equals("error")) {
            // Here we create our Gson object
            Gson gson = new Gson();
            // Here we use our Gson object to decode our json string back into our TaskStorage class
            final TaskStorage task_storage = gson.fromJson(json, TaskStorage.class);
            // Here we set tv as our text view
            TextView tv = (TextView) view.findViewById(R.id.title_todo);
            tv.setTextSize(18);
            if (tv.getText().equals("To Do")) {
                tv.setText("");
            }
            // Here we iterate through all the Task objects in our list
            for(Iterator<Task> i = task_storage.tasks.iterator(); i.hasNext();) {
                final Task task = i.next();
                if (task.getTaskStatus().equals("Done")) {
                    // Wil's Code
                    LinearLayout layout = (LinearLayout) view.findViewById(R.id.todo_layout);
                    ViewGroup.LayoutParams layoutParams = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                    linearLayout = new LinearLayout(getActivity());
                    linearLayout.setOrientation(LinearLayout.VERTICAL);
                    linearLayout.setLayoutParams(layoutParams);

                    TextView textView = new TextView(getActivity());
                    textView.setLayoutParams(layoutParams);
                    textView.setTextSize(20);
                    textView.setText(task.getTaskTitle());
                    textView.append(task.getTaskInfo());
                    textView.append(task.getTaskStatus());
                    textView.setBackgroundColor(Color.parseColor("#F8BBD0"));
                    textView.setPadding(15, 15, 20, 20);
                    textView.setTextColor(Color.parseColor("#515151"));
                    textView.setMovementMethod(new ScrollingMovementMethod());
                    linearLayout.addView(textView);

                    Button deleteButton = new Button(getActivity());
                    deleteButton.setText("Delete");
                    deleteButton.setTag(task.getTaskTitle());
                    deleteButton.setOnClickListener(new View.OnClickListener() {
                        // This is run when the Button is pressed
                        @Override
                        public void onClick(View v) {
                            SharedPreferences prefs = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            String json = prefs.getString("tasks", "error");
                            Gson gson = new Gson();
                            TaskStorage task_storage = gson.fromJson(json, TaskStorage.class);
                            List<Task> filteredTasks = new ArrayList<Task>();
                            for(Iterator<Task> i = task_storage.tasks.iterator(); i.hasNext();) {
                                Task filteredTask = i.next();
                                // TODO: Have IDs for each task because this will get rid of everything w/ the same title
                                if (!filteredTask.getTaskTitle().equals(v.getTag())) {
                                    filteredTasks.add(filteredTask);
                                }
                            }
                            task_storage.tasks = filteredTasks;
                            // Saves the updated Task Storage
                            editor.putString("tasks", gson.toJson(task_storage));
                            editor.commit();
                            Intent intent = new Intent(getContext(), FlexActivity.class);
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(deleteButton);


                    Button upgradeStatusButton = new Button(getActivity());
                    upgradeStatusButton.setText("Upgrade Status");
                    upgradeStatusButton.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            task.upgradeStatus();
                            Gson gson = new Gson();
                            SharedPreferences prefs = getActivity().getSharedPreferences("meta", Context.MODE_PRIVATE);
                            SharedPreferences.Editor editor = prefs.edit();
                            editor.putString("tasks", gson.toJson(task_storage));
                            editor.commit();
                            Intent intent = new Intent(getContext(), FlexActivity.class);
                            startActivity(intent);
                        }
                    });
                    linearLayout.addView(upgradeStatusButton);

                    layout.addView(linearLayout);
                }
            }
        } else {
            TextView tv = (TextView) view.findViewById(R.id.title_todo);
            tv.setTextSize(18);
            tv.setText(json);
        }

        return view;
    }

}
