package com.lp.flashremote.fragments;

import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.lp.flashremote.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by PUJW on 2017/8/14.
 */

public class Remote_Pc_Fragment extends Fragment implements View.OnClickListener {
    private int[] fabId = new int[]{R.id.fab1, R.id.fab2, R.id.fab3, R.id.fab4,
            R.id.fab5, R.id.fab6, R.id.fab7, R.id.fab8, R.id.fab9};
    private int[] llId = new int[]{R.id.ll01, R.id.ll02, R.id.ll03};
    private FloatingActionButton[] fab = new FloatingActionButton[fabId.length];
    private List<AnimatorSet> mAnimList = new ArrayList<>();


    private FloatingActionButton mFab_more;
    private RelativeLayout mFab_Menu;
    private TextView mHideMenuTv;

    private boolean isShow = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_pc, container, false);
        initView(view);
        setDefaultValues();
        return view;
    }

    private void setDefaultValues() {
        for (int i = 0; i < fab.length; i++) {
            mAnimList.add((AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.add_anim));
        }
        mAnimList.add((AnimatorSet) AnimatorInflater.loadAnimator(getActivity(), R.animator.add_anim));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        bindEvents();
    }

    private void bindEvents() {
        mFab_more.setOnClickListener(this);
        mHideMenuTv.setOnClickListener(this);
        for (int i = 0; i < fabId.length; i++) {
            fab[i].setOnClickListener(this);
        }
    }

    private void initView(View view) {
        mFab_more = (FloatingActionButton) view.findViewById(R.id.pcmore);
        mFab_Menu = (RelativeLayout) view.findViewById(R.id.fab_menu);
        mHideMenuTv = (TextView) view.findViewById(R.id.hide_more_menu);
        for (int i = 0; i < fabId.length; i++) {
            fab[i] = (FloatingActionButton) view.findViewById(fabId[i]);
        }

    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.pcmore:
                isShow = !isShow;
                mFab_Menu.setVisibility(isShow ? View.VISIBLE : View.GONE);
                if (isShow) {
                    AnimatorSet animator;
                    for (int i = 0; i < fab.length; i++) {
                        animator = mAnimList.get(i);
                        animator.setTarget(fab[i]);
                        animator.start();
                    }
                    animator=mAnimList.get(fab.length);
                    animator.setTarget(mHideMenuTv);
                    animator.start();
                }
                break;
            case R.id.fab1:
                hideFABMenu();
                break;
            case R.id.fab2:
                hideFABMenu();
                break;
            case R.id.fab3:
                hideFABMenu();
                break;
            case R.id.fab4:
                hideFABMenu();
                break;
            case R.id.fab5:
                hideFABMenu();
                break;
            case R.id.fab6:
                hideFABMenu();
                break;
            case R.id.fab7:
                hideFABMenu();
                break;
            case R.id.fab8:
                hideFABMenu();
                break;
            case R.id.fab9:
                hideFABMenu();
                break;
            case R.id.hide_more_menu:
                hideFABMenu();
                break;
        }
    }

    private void hideFABMenu() {
        mFab_Menu.setVisibility(View.GONE);
        isShow = false;
    }
}
