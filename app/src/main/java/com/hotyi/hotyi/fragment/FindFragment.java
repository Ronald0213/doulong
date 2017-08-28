package com.hotyi.hotyi.fragment;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.hotyi.hotyi.R;

public class FindFragment extends Fragment implements View.OnClickListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER

    private LinearLayout gameCircle;
    private LinearLayout gameTalent;
    private LinearLayout gameCode;

    public FindFragment(){}

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view  = inflater.inflate(R.layout.fragment_find,container,false);
        gameCircle = (LinearLayout)view.findViewById(R.id.find_frg_game_circle);
        gameTalent = (LinearLayout)view.findViewById(R.id.find_frg_game_talent);
        gameCode = (LinearLayout)view.findViewById(R.id.find_frg_game_code);
        gameCode.setOnClickListener(this);
        gameTalent.setOnClickListener(this);
        gameCircle.setOnClickListener(this);

        return view;

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.find_frg_game_circle:
                break;
            case R.id.find_frg_game_talent:
                break;
            case R.id.find_frg_game_code:
                break;
        }
    }
}
