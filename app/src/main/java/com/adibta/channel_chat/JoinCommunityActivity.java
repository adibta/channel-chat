package com.adibta.channel_chat;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class JoinCommunityActivity extends AppCompatActivity {

    @BindView(R.id.edit_text_input) TextView tvInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_join_community);
        ButterKnife.bind(this);
    }

    @OnClick(R.id.button_submit)
    public void submit(){

    }
}
