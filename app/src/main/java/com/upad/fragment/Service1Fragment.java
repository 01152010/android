package com.upad.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.upad.activity.BaseFragment;
import com.upad.R;
import com.upad.utils.Constants;

public class Service1Fragment extends BaseFragment {
    private LinearLayout mLlServiceQJ;
    private LinearLayout mLlServiceXY;
    private LinearLayout mLlServiceFP;
    private LinearLayout mLlServiceTF;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_service1, container, false);
        initView(view);
        return view;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    private void initView(View view){
        mLlServiceQJ = view.findViewById(R.id.ll_service_qj);
        mLlServiceXY = view.findViewById(R.id.ll_service_xy);
        mLlServiceFP = view.findViewById(R.id.ll_service_fp);
        mLlServiceTF = view.findViewById(R.id.ll_service_tf);
        mLlServiceQJ.setOnClickListener(v -> onClickServiceQJ());
        mLlServiceXY.setOnClickListener(v -> onClickServiceXY());
        mLlServiceFP.setOnClickListener(v -> onClickServiceFP());
        mLlServiceTF.setOnClickListener(v -> onClickServiceTF());

    }

    private void onClickServiceQJ(){
    }

    private void onClickServiceXY(){
    }

    private void onClickServiceFP(){
    }

    private void onClickServiceTF(){
    }


}
