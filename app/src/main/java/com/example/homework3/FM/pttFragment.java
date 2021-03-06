package com.example.homework3.FM;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.fragment.app.Fragment;
import androidx.preference.PreferenceManager;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.example.homework3.R;
import com.example.homework3.adapter.CardAdapter;
import com.example.homework3.object.CardData;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class pttFragment extends Fragment {

    String search;
    public pttFragment(String search) {
        this.search = search;
    }

    ProgressBar progressBar;
    RecyclerView recyclerView;
    CardAdapter cardAdapter;
    private List<CardData> GroupCardData = new ArrayList<CardData>();
    private CardData cardData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_ptt, container, false);
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        recyclerView = getView().findViewById(R.id.rvSearch);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL));
        progressBar = getView().findViewById(R.id.progressBarSearch);

        System.out.println("ptt_onViewStateRestored");
    }

    @Override
    public void onStart() {
        super.onStart();

        System.out.println("ptt_onStart");

        //????????????????????????????????????????????????
        if(GroupCardData.size() > 0) {
            cardAdapter.notifyItemRangeRemoved(0, GroupCardData.size());
            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
            cardAdapter = new CardAdapter(GroupCardData, getContext());
            recyclerView.setAdapter(cardAdapter);
        }

        if (SearchFragment.isNetworkAvailable(getActivity())) {
            Runnable runnable = () -> {
                try {
                    //????????????
                    String url = "https://www.ptt.cc/bbs/NTUST_STUDY/search?q="+search;
                    final Document document = SearchFragment.analyzeHTML(url);

                    //????????????Class
                    Elements elements = document.getElementsByClass("r-ent");
                    GroupCardData = findThree(elements);

                    //??????????????????
//                                elements = document.select("div.title > a");
//                                GroupCardData = findContent(elements);

                    getActivity().runOnUiThread(() -> {
                        cardAdapter = new CardAdapter(GroupCardData, getActivity());
                        recyclerView.setAdapter(cardAdapter);
                        progressBar.setVisibility(View.GONE);
                    });

                } catch (IOException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }
            };
            new Thread(runnable).start();
        } else {
            Toast.makeText(getActivity(), "???????????????", Toast.LENGTH_SHORT).show();
            progressBar.setVisibility(View.GONE);
        }
    }

    //?????????class?????????????????????
    protected List<CardData> findThree(Elements ThemeContext)
    {
        List<CardData> GroupCardData = new ArrayList<CardData>();
        for (Element domElement: ThemeContext) {
            //??????
            String Title = domElement.getElementsByClass("title").text();
            //?????????
            String AuthorName = "??????: " + domElement.getElementsByClass("author").text();
            //??????
            String Date = "??????: " + domElement.getElementsByClass("date").text();
            //??????
            String href = "https://www.ptt.cc" + domElement.select("div.title > a").attr("href");


            cardData = new CardData(Title, AuthorName, Date, href, false, "ptt");
            GroupCardData.add(cardData);
        }
        return  GroupCardData;
    }

    //?????????RV
    private void resetAdapter() {
        if(GroupCardData.size() > 0) {
            cardAdapter.notifyItemRangeRemoved(0, GroupCardData.size());
            cardAdapter = new CardAdapter(GroupCardData, getActivity());
            recyclerView.setAdapter(cardAdapter);
        }
    }
}