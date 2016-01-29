/*******************************************************************************
 * Copyright 2011, 2012 Chris Banes.
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *******************************************************************************/
package com.handmark.pulltorefresh.samples;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.handmark.pulltorefresh.library.PullToRefreshBase;
import com.handmark.pulltorefresh.library.PullToRefreshBase.Mode;
import com.handmark.pulltorefresh.library.PullToRefreshBase.OnRefreshListener2;
import com.handmark.pulltorefresh.library.PullToRefreshRecyclerView;

import java.util.Arrays;
import java.util.LinkedList;

public final class PullToRefreshRecyclerViewActivity extends Activity {

    private String[] mDataItems = {"Abbaye de Belloc", "Abbaye du Mont des Cats", "Abertam", "Abondance", "Ackawi",
            "Acorn", "Adelost", "Affidelice au Chablis", "Afuega'l Pitu", "Airag", "Airedale", "Aisy Cendre",
            "Allgauer Emmentaler"};

    static final int MENU_SET_MODE = 0;

    private LinkedList<String> mListItems;
    private PullToRefreshRecyclerView mPullRefreshRecyclerView;
    private RecyclerView mRecyclerView;
    private ArrayAdapter<String> mAdapter;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ptr_recycler);
        initViews();
    }

    private void initViews() {
        mPullRefreshRecyclerView = (PullToRefreshRecyclerView) findViewById(R.id.pull_refresh_recycler);
        mRecyclerView = mPullRefreshRecyclerView.getRefreshableView();

        // Set a listener to be invoked when the list should be refreshed.
        mPullRefreshRecyclerView.setOnRefreshListener(new OnRefreshListener2<RecyclerView>() {

            @Override
            public void onPullDownToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                Toast.makeText(PullToRefreshRecyclerViewActivity.this, "Pull Down!", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();
            }

            @Override
            public void onPullUpToRefresh(PullToRefreshBase<RecyclerView> refreshView) {
                Toast.makeText(PullToRefreshRecyclerViewActivity.this, "Pull Up!", Toast.LENGTH_SHORT).show();
                new GetDataTask().execute();
            }

        });

        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(new DefaultAdapter());
    }

    /**
     * 模拟耗时操作
     */
    private class GetDataTask extends AsyncTask<Void, Void, String[]> {

        @Override
        protected String[] doInBackground(Void... params) {
            // Simulates a background job.
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
            }
            return mDataItems;
        }

        @Override
        protected void onPostExecute(String[] result) {
            mListItems.addFirst("Added after refresh...");
            mListItems.addAll(Arrays.asList(result));
            mAdapter.notifyDataSetChanged();

            // Call onRefreshComplete when the list has been refreshed.
            mPullRefreshRecyclerView.onRefreshComplete();

            super.onPostExecute(result);
        }
    }


    class DefaultAdapter extends RecyclerView.Adapter<DefaultViewHolder> {
        @Override
        public void onBindViewHolder(DefaultViewHolder holder, int position) {
            holder.textView.setText(mListItems.get(position));
        }

        @Override
        public DefaultViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View rootView = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_ptr_recycler_item, parent, false);
            return new DefaultViewHolder(rootView);
        }

        @Override
        public int getItemCount() {
            return mListItems.size();
        }
    } // end of DefaultAdapter


    class DefaultViewHolder extends RecyclerView.ViewHolder {
        public TextView textView;

        public DefaultViewHolder(View view) {
            super(view);
            textView = (TextView) view.findViewById(R.id.list_textview);
        }
    } // end of DefaultViewHolder


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.add(0, MENU_SET_MODE, 0,
                mPullRefreshRecyclerView.getMode() == Mode.BOTH ? "Change to MODE_PULL_DOWN"
                        : "Change to MODE_PULL_BOTH");
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        MenuItem setModeItem = menu.findItem(MENU_SET_MODE);
        setModeItem.setTitle(mPullRefreshRecyclerView.getMode() == Mode.BOTH ? "Change to MODE_PULL_FROM_START"
                : "Change to MODE_PULL_BOTH");

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case MENU_SET_MODE:
                mPullRefreshRecyclerView
                        .setMode(mPullRefreshRecyclerView.getMode() == Mode.BOTH ? Mode.PULL_FROM_START
                                : Mode.BOTH);
                break;
        }

        return super.onOptionsItemSelected(item);
    }

}
