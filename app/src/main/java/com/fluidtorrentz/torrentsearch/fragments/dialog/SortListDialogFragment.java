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

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.fluidtorrentz.torrentsearch.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.Objects;

public class SortListDialogFragment extends BottomSheetDialogFragment {

    private SortListListener sortListListener;
    private RadioButton radioButton;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            sortListListener = (SortListListener) context;
        } catch (Exception e) {
            throw new ClassCastException(requireActivity() + " sortListListener must implemented.");
        }
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.sort_list_dialog, container, false);

        // get button checkedId or get default as 1
        SharedPreferences preferences = requireActivity()
                .getSharedPreferences(requireActivity().getPackageName() + ".my.pref_file", Context.MODE_PRIVATE);
        int checkedButton = preferences.getInt("checked id", 2131296366);

        radioButton = rootView.findViewById(checkedButton);
        radioButton.setChecked(true);

        RadioGroup radioGroupSort = rootView.findViewById(R.id.radio_group_sort);
        radioGroupSort.setOnCheckedChangeListener((group, checkedId) -> {

//            Log.d("radioID", String.valueOf(checkedId));

            //save button checkedId
            SharedPreferences sharedPreferences = requireActivity()
                    .getSharedPreferences(requireActivity().getPackageName() + ".my.pref_file", Context.MODE_PRIVATE);
            sharedPreferences.edit().putInt("checked id", checkedId).apply();

            radioButton = group.findViewById(checkedId);
            Log.d("radioID", String.valueOf(checkedId));
            sortListListener.onSortButtonClick(radioButton.getText().toString());
            dismiss();
        });

        return rootView;
    }

    public interface SortListListener {
        void onSortButtonClick(String sort);
    }
}
