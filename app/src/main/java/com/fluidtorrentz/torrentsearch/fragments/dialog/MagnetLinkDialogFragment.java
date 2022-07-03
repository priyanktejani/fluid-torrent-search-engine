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

package com.fluidtorrentz.torrentsearch.fragments.dialog;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.DialogFragment;

import com.fluidtorrentz.torrentsearch.R;

public class MagnetLinkDialogFragment extends DialogFragment implements View.OnClickListener {

    private final MagnetLinkListener magnetLinkListener;
    private String dialogTitle;
    private int position;
    private ConstraintLayout dialogLayout;
    private LinearLayout alertLayout;
    private ProgressBar progressDialog;

    public MagnetLinkDialogFragment(MagnetLinkListener magnetLinkListener) {
        this.magnetLinkListener = magnetLinkListener;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            dialogTitle = getArguments().getString("dialog_title");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.magnet_link_dialog, container, false);
        dialogLayout = rootView.findViewById(R.id.dialog_layout);
        progressDialog = rootView.findViewById(R.id.progress_dialog);
        alertLayout = rootView.findViewById(R.id.alert_layout);
        dialogLayout.setVisibility(View.GONE);

        TextView textDialogTitle = rootView.findViewById(R.id.text_dialog_title);
        Button buttonOpenLink = rootView.findViewById(R.id.button_open_link);
        Button buttonCopyLink = rootView.findViewById(R.id.button_copy_link);
        Button buttonShareLink = rootView.findViewById(R.id.button_share_link);
        Button buttonOpenWebsite = rootView.findViewById(R.id.button_open_website);
        Button buttonInstallTorrent = rootView.findViewById(R.id.button_install_torrent);
        Button buttonCancel = rootView.findViewById(R.id.button_cancel);

        textDialogTitle.setText(dialogTitle);
        buttonOpenLink.setOnClickListener(this);
        buttonCopyLink.setOnClickListener(this);
        buttonShareLink.setOnClickListener(this);
        buttonOpenWebsite.setOnClickListener(this);

        buttonInstallTorrent.setOnClickListener(this);
        buttonCancel.setOnClickListener(this);

        return rootView;
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.button_open_link) {
            position = 0;
        } else if (id == R.id.button_copy_link) {
            position = 1;
        } else if (id == R.id.button_share_link) {
            position = 2;
        } else if (id == R.id.button_open_website) {
            position = 3;
        } else if (id == R.id.button_install_torrent) {
            Intent intentOpenLink = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=free.full.torrent.moviedownloader"));
            startActivity(intentOpenLink);
        } else if (id == R.id.button_cancel) {
            dismiss();
        }
        magnetLinkListener.onMagnetButtonClick(position);
    }

    public void showInstallAlert() {
        dialogLayout.setVisibility(View.GONE);
        alertLayout.setVisibility(View.VISIBLE);
    }

    public void showDialog() {
        dialogLayout.setVisibility(View.VISIBLE);
        progressDialog.setVisibility(View.GONE);

    }

    public interface MagnetLinkListener {
        void onMagnetButtonClick(int position);
    }
}
