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

package com.fluidtorrentz.torrentsearch.activities;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.fluidtorrentz.torrentsearch.R;
import com.fluidtorrentz.torrentsearch.fragments.MainFragment;
import com.fluidtorrentz.torrentsearch.fragments.dialog.SortListDialogFragment;

import java.util.Objects;

public class MainActivity extends AppCompatActivity implements SortListDialogFragment.SortListListener {

    private static String keyword;
    private static String sortItem = "Seeds DESC";
    public static int current;
    private EditText inputSearch;
    private final TextView.OnEditorActionListener editorActionListener = new TextView.OnEditorActionListener() {
        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

            if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                keyword = inputSearch.getText().toString();
                if (!keyword.isEmpty()) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MainFragment()).commit();
                }
                current = 0;
            }
            closeKeyboard();

            return false;
        }
    };

    public static String getKeyword() {
        return keyword;
    }

    public static String getSortItem() {
        return sortItem;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // clear checked button for fresh new run
        SharedPreferences preferences = getSharedPreferences(getPackageName() + ".my.pref_file", Context.MODE_PRIVATE);
        preferences.edit().clear().apply();

        // get text from user
        inputSearch = findViewById(R.id.edit_text_search);
        inputSearch.setOnEditorActionListener(editorActionListener);

        // sort list
        ImageButton imageButton = findViewById(R.id.button_sort_list);
        imageButton.setOnClickListener(v -> {
            SortListDialogFragment sortListDialogFragment = new SortListDialogFragment();
            sortListDialogFragment.show(getSupportFragmentManager(), "Sort List Dialog");
        });
    }

    private void closeKeyboard() {
        View view = this.getCurrentFocus();
        if (view != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            Objects.requireNonNull(inputMethodManager).hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    @Override
    public void onSortButtonClick(String sort) {
        sortItem = sort;
        if (keyword != null) {
            current = MainFragment.viewPager.getCurrentItem();
            getSupportFragmentManager().beginTransaction().replace(R.id.frame_layout, new MainFragment()).commit();
        }
    }
}