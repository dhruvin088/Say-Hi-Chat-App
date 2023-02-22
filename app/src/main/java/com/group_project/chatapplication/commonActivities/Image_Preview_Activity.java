package com.group_project.chatapplication.commonActivities;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.group_project.chatapplication.R;

public class Image_Preview_Activity extends AppCompatActivity {

    ImageView selected_img, back_to_home;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_preview);

        selected_img = findViewById(R.id.selected_img);
        back_to_home = findViewById(R.id.back_to_home);

        String imgUrl = getIntent().getExtras().get("passSelectedImage").toString();
        Glide.with(selected_img.getContext()).load(imgUrl).into(selected_img);

        back_to_home.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Image_Preview_Activity.this, Settings_Activity.class));
                finish();
            }
        });

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}