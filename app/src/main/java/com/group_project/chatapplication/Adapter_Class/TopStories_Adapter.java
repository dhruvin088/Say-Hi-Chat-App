package com.group_project.chatapplication.Adapter_Class;

import android.content.Context;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devlomi.circularstatusview.CircularStatusView;
import com.group_project.chatapplication.MainActivity;
import com.group_project.chatapplication.Model_Class.Stories_Model;
import com.group_project.chatapplication.Model_Class.UserStories_Model;
import com.group_project.chatapplication.R;

import java.util.ArrayList;
import java.util.Date;

import de.hdodenhof.circleimageview.CircleImageView;
import omari.hamza.storyview.StoryView;
import omari.hamza.storyview.callback.StoryClickListeners;
import omari.hamza.storyview.model.MyStory;

public class TopStories_Adapter extends RecyclerView.Adapter<TopStories_Adapter.TopStatusViewHolder> {
    Context context;
    ArrayList<UserStories_Model> userStatuses;

    public TopStories_Adapter(Context context, ArrayList<UserStories_Model> userStatuses) {
        this.context = context;
        this.userStatuses = userStatuses;
    }

    @NonNull
    @Override
    public TopStatusViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.item_stories, parent, false);
        return new TopStatusViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull TopStatusViewHolder holder, int position) {
        UserStories_Model userStories_model3 = userStatuses.get(position);
        Stories_Model lastStory = userStories_model3.getStatuses().get(userStories_model3.getStatuses().size() - 1);
        Glide.with(context).load(lastStory.getImageUrl()).into(holder.status_outer_img);
        holder.circular_status_view.setPortionsCount(userStories_model3.getStatuses().size());
        holder.circular_status_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<MyStory> myStories = new ArrayList<>();
                for (Stories_Model story : userStories_model3.getStatuses()) {
                    myStories.add(new MyStory(story.getImageUrl()));
                }
                new StoryView.Builder(((MainActivity) context).getSupportFragmentManager())
                        .setStoriesList(myStories) // Required
                        .setStoryDuration(5000) // Default is 2000 Millis (2 Seconds)
                        .setTitleText(userStories_model3.getName()) // Default is Hidden
                        .setSubtitleText(longToDateString(lastStory.getTimestamp(), "dd-MM-yyyy hh:mm a")) // Default is Hidden
                        .setTitleLogoUrl(userStories_model3.getProfileImage()) // Default is Hidden
                        .setStoryClickListeners(new StoryClickListeners() {
                            @Override
                            public void onDescriptionClickListener(int position) {
                                //your action
                            }

                            @Override
                            public void onTitleIconClickListener(int position) {
                                //your action
                            }
                        }) // Optional Listeners
                        .build() // Must be called before calling show method
                        .show();

            }
        });

    }

    @Override
    public int getItemCount() {
        return userStatuses.size();
    }

    public static class TopStatusViewHolder extends RecyclerView.ViewHolder {
        CircularStatusView circular_status_view;
        CircleImageView status_outer_img;

        public TopStatusViewHolder(@NonNull View itemView) {
            super(itemView);
            circular_status_view = itemView.findViewById(R.id.circular_status_view);
            status_outer_img = itemView.findViewById(R.id.status_outer_img);
        }
    }

    public static String longToDateString(long timestamp, String format) {
        return DateFormat.format(format, new Date(timestamp)).toString();
    }

}
