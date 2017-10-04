package com.lp.flashremote.fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.lp.flashremote.Model.RecordDao;
import com.lp.flashremote.R;
import com.lp.flashremote.beans.UserInfo;
import com.lp.flashremote.utils.Command2JsonUtil;
import com.lp.flashremote.utils.SocketUtil;
import com.lp.flashremote.utils.StringUtil;

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

    private int id;//editextView 的id
    private   EditText mEditText;

    private ListView mListView;

    private RecordDao mRecord;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRecord=new RecordDao(getActivity());
        inintData();
    }

    private void inintData() {
       mList=mRecord.alterData();
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable final Bundle savedInstanceState) {
        rootView=inflater.inflate(R.layout.search_fragment,container,false);
        initView();
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



    private void initView() {
        mSearchView=rootView.findViewById(R.id.searchView);
        mListView=rootView.findViewById(R.id.listView);
        mRmoveList=rootView.findViewById(R.id.rmovelist);
        mSearch=rootView.findViewById(R.id.search);
        id=mSearchView.getContext().getResources().getIdentifier("android:id/search_src_text",null,null);
        mEditText=mSearchView.findViewById(id);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        onBind();
    }

    private void onBind() {
        mSearch.setOnClickListener(this);
        mRmoveList.setOnClickListener(this);
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                mSearchSocket.addMessage(StringUtil.operateCmd(
                        Command2JsonUtil.getJson("7",mList.get(i),false)));
            }
        });
        mListView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(AdapterView<?> adapterView, View view, int i, long l) {
                final int id=i;
                new AlertDialog.Builder(getActivity())
                        .setTitle("提示")
                        .setMessage("你确定要删除--"+mList.get(i)+"--?")
                        .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                mRecord.deleteData(mList.get(id));
                                mList.remove(id);
                                adapter.notifyDataSetChanged();
                            }
                        })
                        .show();
                return false;
            }
        });
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()){
            case R.id.search:
                String str=mEditText.getText().toString();
                mSearchSocket.addMessage(StringUtil.operateCmd(
                        Command2JsonUtil.getJson("7",str,false)));
                mRecord.addData(str);
                mList.add(str);
                adapter.notifyDataSetChanged();
                break;
            case R.id.rmovelist:
                for (String s:mList){
                    mRecord.deleteData(s);
                }
                mList.clear();
                adapter.notifyDataSetChanged();
                break;
        }
    }
}
