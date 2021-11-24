package com.eziot.demo.message;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.eziot.common.http.callback.IEZIoTResultCallback;
import com.eziot.demo.message.adapter.EZIoTMessageListAdapter;
import com.eziot.demo.message.adapter.EZIoTMessageTypeListAdapter;
import com.eziot.iotsdkdemo.R;
import com.eziot.message.EZIoTMessageManager;
import com.eziot.message.model.EZIoTMsgCategoryInfo;

import org.jetbrains.annotations.Nullable;

import java.util.List;

public class EZIoTMessageListActivity extends AppCompatActivity implements View.OnClickListener {

    private View mAlarmMessage;
    private View mNoticeMessage;
    private RecyclerView mRecyclerView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_eziot_message_list);
        initView();
        initData();
    }

    private void initView(){
        mRecyclerView = findViewById(R.id.message_recyclerView);
        findViewById(R.id.message_list_go_back).setOnClickListener(this);
    }

    private void initData(){
        EZIoTMessageManager.INSTANCE.getMessageCategories(new IEZIoTResultCallback<List<EZIoTMsgCategoryInfo>>(){
            @Override
            public void onError(int errorCode, @Nullable String errorDesc) {

            }
            @Override
            public void onSuccess(List<EZIoTMsgCategoryInfo> ezIoTMsgCategoryInfos) {
                LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EZIoTMessageListActivity.this);
                EZIoTMessageListAdapter adapter = new EZIoTMessageListAdapter(EZIoTMessageListActivity.this, ezIoTMsgCategoryInfos);
                mRecyclerView.setLayoutManager(linearLayoutManager);
                mRecyclerView.setAdapter(adapter);
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.message_list_go_back:
                finish();
                break;
        }
    }
}