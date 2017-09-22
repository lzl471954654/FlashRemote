package com.lp.flashremote.fragments;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;

import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.SocketUtil;

import java.util.ArrayList;
import java.util.List;


public class SearchFragment   extends Fragment implements View.OnClickListener {

    private SocketUtil mSearchSocket=SocketUtil.getInstance(UserInfo.getUsername(),
            UserInfo.getPassword());
    private View rootView;
    private List<String> mList;
    private ArrayAdapter<String> adapter;

    private TextView mSearch;
    private TextView mRmoveList;

    private SearchView mSearchView;
    private ListView mListView;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        inintData();
    }

    private void inintData() {
        mList=new ArrayList<>();
        for(int i=0;i<20;i++){
            mList.add(i+"gjhghj");
        }
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.search_fragment,container,false);

        mSearchView=rootView.findViewById(R.id.searchView);
        mListView=rootView.findViewById(R.id.listView);
        mRmoveList=rootView.findViewById(R.id.rmovelist);
        mSearch=rootView.findViewById(R.id.search);

        adapter=new ArrayAdapter<>(getActivity(),android.R.layout.simple_expandable_list_item_1,mList);
        mListView.setAdapter(adapter);
        mListView.setTextFilterEnabled(true);
        mSearchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if (TextUtils.isEmpty(newText)){
                    mListView.setFilterText(newText);
                }else{
                    adapter.getFilter().filter(newText);
                }
                return true;
            }
        });
        return rootView;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        onBind();
    }

    private void onBind() {
        mSearch.setOnClickListener(this);
        mRmoveList.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search:

                break;
            case R.id.rmovelist:
                mList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
