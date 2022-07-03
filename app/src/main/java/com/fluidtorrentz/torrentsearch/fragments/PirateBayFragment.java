/*
 * Copyright (c) 2020 Priyank Tejani
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package com.fluidtorrentz.torrentsearch.fragments;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.fluidtorrentz.torrentsearch.R;
import com.fluidtorrentz.torrentsearch.activities.MainActivity;
import com.fluidtorrentz.torrentsearch.adapters.TorrentAdapter;
import com.fluidtorrentz.torrentsearch.fragments.dialog.MagnetLinkDialogFragment;
import com.fluidtorrentz.torrentsearch.parsing.SearchResult;
import com.fluidtorrentz.torrentsearch.parsing.SortList;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class PirateBayFragment extends Fragment implements TorrentAdapter.RecyclerviewListener, MagnetLinkDialogFragment.MagnetLinkListener {

    private TorrentAdapter torrentAdapter;
    private final ArrayList<SearchResult> searchResults = new ArrayList<>();
    private String link, magnet;
    private MagnetLinkDialogFragment magnetLinkDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView =  inflater.inflate(R.layout.fragment_search_results, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        torrentAdapter = new TorrentAdapter(this, searchResults);

        String keyword = MainActivity.getKeyword();
        String sortItem = MainActivity.getSortItem();
        new loadSearchResult(this).execute(new SortList(sortItem).urlPirateBay(keyword));
        recyclerView.setAdapter(torrentAdapter);

        return rootView;
    }

    private static String size(String str) {
        int index = str.indexOf("Size") + 5;
        StringBuilder size = new StringBuilder();
        for (int i = index; i < str.length(); i++) {
            if (str.charAt(i) == ',') break;
            size.append(str.charAt(i));
        }
        return size.toString();
    }

    @Override
    public void onMagnetButtonClick(int position) {
        switch (position) {
            case 0:
                // open link
                try {
                    Intent intentOpenLink = new Intent(Intent.ACTION_VIEW, Uri.parse(magnet));
                    startActivity(intentOpenLink);
                } catch (Exception e) {
                    magnetLinkDialogFragment.showInstallAlert();
                }
                break;
            case 1:
                // copy link
                ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("copy_link", magnet);
                assert clipboard != null;
                clipboard.setPrimaryClip(clip);
                Toast.makeText(getActivity(), "Link copied successfully", Toast.LENGTH_SHORT).show();
                break;
            case 2:
                // share link
                Intent intentShareLink = new Intent(Intent.ACTION_SEND);
                intentShareLink.setType("text/plain");
                intentShareLink.putExtra(Intent.EXTRA_TEXT, magnet);
                startActivity(Intent.createChooser(intentShareLink, "Share via"));
                break;
            case 3:
                // open webView
                Intent intentOpenWebsite = new Intent(Intent.ACTION_VIEW, Uri.parse(link));
                startActivity(intentOpenWebsite);
                break;
        }
    }

    private static class loadSearchResult extends AsyncTask<String, Void, ArrayList<SearchResult>> {
        private final WeakReference<PirateBayFragment> fragmentWeakReference;

        loadSearchResult(PirateBayFragment pirateBayFragment) {
            fragmentWeakReference = new WeakReference<>(pirateBayFragment);
        }

        @Override
        protected ArrayList<SearchResult> doInBackground(String... strings) {
            ArrayList<SearchResult> searchResultsTemp = new ArrayList<>();
            try {
                String website = "www.thepiratebay10.org";
                Document document = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0").timeout(70000).ignoreHttpErrors(true).get();
                Elements tBody = document.select("tbody");
                tBody.select("tr").last().remove();

                for (Element tr : tBody.select("tr")) {
                    String title = tr.select("td").get(1).getElementsByClass("detName").text();
                    String str = tr.select("td").get(1).getElementsByClass("detDesc").text();
                    String seeds = tr.select("td").get(2).text() + " Seeds";
                    String leeches = tr.select("td").get(3).text() + " Leeches";
                    String size = size(str);

                    // send link for dialog
                    String link = tr.select("td").get(1).getElementsByClass("detName")
                            .select("a").attr("href");
                    
                    searchResultsTemp.add(new SearchResult(title, seeds, leeches, size, website, link));
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return searchResultsTemp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            PirateBayFragment pirateBayFragment = fragmentWeakReference.get();
            if (pirateBayFragment == null || pirateBayFragment.isDetached()) return;

            pirateBayFragment.searchResults.clear();
            pirateBayFragment.torrentAdapter.showShimmer = true;
            pirateBayFragment.torrentAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> searchResults) {
            super.onPostExecute(searchResults);
            PirateBayFragment pirateBayFragment = fragmentWeakReference.get();
            if (pirateBayFragment == null || pirateBayFragment.isDetached()) return;
            if (searchResults.isEmpty()) pirateBayFragment.torrentAdapter.noResult = true;

            pirateBayFragment.searchResults.addAll(searchResults);
            pirateBayFragment.torrentAdapter.showShimmer = false;
            pirateBayFragment.torrentAdapter.notifyDataSetChanged();
        }
    }

    private static class loadMagnetLink extends AsyncTask<String, Void, String> {
        private WeakReference<PirateBayFragment> fragmentWeakReference;

        loadMagnetLink(PirateBayFragment pirateBayFragment) {
            fragmentWeakReference = new WeakReference<>(pirateBayFragment);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0").timeout(70000).ignoreHttpErrors(true).get();
                return document.getElementsByClass("download").select("a").attr("href");
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String magnet) {
            super.onPostExecute(magnet);
            PirateBayFragment pirateBayFragment = fragmentWeakReference.get();
            if (pirateBayFragment == null || pirateBayFragment.isDetached()) return;

            pirateBayFragment.magnet = magnet;
            pirateBayFragment.magnetLinkDialogFragment.showDialog();
        }
    }

    @Override
    public void onItemClick(int position) {
        magnetLinkDialogFragment = new MagnetLinkDialogFragment(this);
        Bundle args = new Bundle();
        args.putString("dialog_title", searchResults.get(position).getTitle());
        magnetLinkDialogFragment.setArguments(args);
        magnetLinkDialogFragment.show(requireActivity().getSupportFragmentManager().beginTransaction(), "Magnet Link Dialog");

        link = searchResults.get(position).getLink();
        new PirateBayFragment.loadMagnetLink(this).execute(link);
    }
}