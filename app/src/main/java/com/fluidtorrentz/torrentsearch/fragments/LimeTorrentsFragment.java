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
import com.fluidtorrentz.torrentsearch.parsing.Url;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Objects;

public class LimeTorrentsFragment extends Fragment implements TorrentAdapter.RecyclerviewListener, MagnetLinkDialogFragment.MagnetLinkListener {

    private TorrentAdapter torrentAdapter;
    private final ArrayList<SearchResult> searchResults = new ArrayList<>();
    private String link, magnet;
    private MagnetLinkDialogFragment magnetLinkDialogFragment;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_search_results, container, false);

        RecyclerView recyclerView = rootView.findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        torrentAdapter = new TorrentAdapter(this, searchResults);

        String keyword = MainActivity.getKeyword();
        String sortItem = MainActivity.getSortItem();
        new loadSearchResult(this).execute(new SortList(sortItem).urlLimeTorrents(keyword));
        recyclerView.setAdapter(torrentAdapter);

        return rootView;
    }

    @Override
    public void onItemClick(int position) {
        magnetLinkDialogFragment = new MagnetLinkDialogFragment(this);
        Bundle args = new Bundle();
        args.putString("dialog_title", searchResults.get(position).getTitle());
        magnetLinkDialogFragment.setArguments(args);
        magnetLinkDialogFragment.show(requireActivity().getSupportFragmentManager().beginTransaction(), "Magnet Link Dialog");

        link = searchResults.get(position).getLink();
        new loadMagnetLink(this).execute(link);
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
        private final WeakReference<LimeTorrentsFragment> fragmentWeakReference;

        loadSearchResult(LimeTorrentsFragment limeTorrentsFragment) {
            fragmentWeakReference = new WeakReference<>(limeTorrentsFragment);
        }

        @Override
        protected ArrayList<SearchResult> doInBackground(String... strings) {
            ArrayList<SearchResult> searchResultsTemp = new ArrayList<>();
            try {
                String website = "www.limetorrents.info";
                Document document = Jsoup.connect(strings[0]).timeout(90000).userAgent("Mozilla/5.0").timeout(70000).ignoreHttpErrors(true).get();
                Elements table2 = document.getElementsByClass("table2");
                Elements tBody = table2.select("tbody");
                tBody.select("tr").get(0).remove();

                for (Element tr : tBody.select("tr")) {

                    String title = tr.select("td").get(0).text();
                    String size = tr.select("td").get(2).text();
                    String seeds = tr.select("td").get(3).text() + " Seeds";
                    String leeches = tr.select("td").get(4).text() + " Leeches";

                    // send link for dialog
                    String link = Url.UrlLimeTorrent + tr.select("td").get(0)
                            .select("a").last().attr("href");
                    Log.d("link", link);

                    searchResultsTemp.add(new SearchResult(title, seeds, leeches, size, website, link));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            return searchResultsTemp;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            LimeTorrentsFragment limeTorrentsFragment = fragmentWeakReference.get();
            if (limeTorrentsFragment == null || limeTorrentsFragment.isDetached()) return;

            limeTorrentsFragment.searchResults.clear();
            limeTorrentsFragment.torrentAdapter.showShimmer = true;
            limeTorrentsFragment.torrentAdapter.notifyDataSetChanged();
        }

        @Override
        protected void onPostExecute(ArrayList<SearchResult> searchResults) {
            super.onPostExecute(searchResults);
            LimeTorrentsFragment limeTorrentsFragment = fragmentWeakReference.get();
            if (limeTorrentsFragment == null || limeTorrentsFragment.isDetached()) return;
            if (searchResults.isEmpty()) limeTorrentsFragment.torrentAdapter.noResult = true;

            limeTorrentsFragment.searchResults.addAll(searchResults);
            limeTorrentsFragment.torrentAdapter.showShimmer = false;
            limeTorrentsFragment.torrentAdapter.notifyDataSetChanged();
        }
    }

    private static class loadMagnetLink extends AsyncTask<String, Void, String> {
        private WeakReference<LimeTorrentsFragment> fragmentWeakReference;

        loadMagnetLink(LimeTorrentsFragment limeTorrentsFragment) {
            fragmentWeakReference = new WeakReference<>(limeTorrentsFragment);
        }

        @Override
        protected String doInBackground(String... strings) {
            try {
                Document document = Jsoup.connect(strings[0]).userAgent("Mozilla/5.0").timeout(70000).ignoreHttpErrors(true).get();
                Elements tBody = document.select("tbody");
                Element element = tBody.select("tr").get(0).select("td").get(1);
                return "magnet:?xt=urn:btih:" + element.text();
            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String magnet) {
            super.onPostExecute(magnet);
            LimeTorrentsFragment limeTorrentsFragment = fragmentWeakReference.get();
            if (limeTorrentsFragment == null || limeTorrentsFragment.isDetached()) return;

            limeTorrentsFragment.magnet = magnet;
            limeTorrentsFragment.magnetLinkDialogFragment.showDialog();
        }
    }
}