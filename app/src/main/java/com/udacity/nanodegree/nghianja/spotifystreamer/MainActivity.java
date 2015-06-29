package com.udacity.nanodegree.nghianja.spotifystreamer;

import android.app.Activity;
import android.app.FragmentManager;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import kaaes.spotify.webapi.android.SpotifyApi;
import kaaes.spotify.webapi.android.SpotifyService;
import kaaes.spotify.webapi.android.models.Artist;
import kaaes.spotify.webapi.android.models.ArtistsPager;

/**
 * References:
 * [1] https://github.com/googlesamples/android-BasicContactables
 * [2] http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 * [3] http://stackoverflow.com/questions/9157504/put-a-progressbar-on-actionbar
 * [4] http://stackoverflow.com/questions/25730163/how-to-save-custom-listfragment-state-with-orientation-change
 * [5] http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
 */
public class MainActivity extends Activity {

    private static final String TAG = "MainActivity";
    private MainActivityFragment mainFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_main);

        FragmentManager manager = getFragmentManager();
        mainFragment = (MainActivityFragment) manager.findFragmentById(R.id.main_fragment);

        if (getIntent() != null) {
            handleIntent(getIntent());
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        handleIntent(intent);
    }

    /**
     * Assuming this activity was started with a new intent, process the incoming information and
     * react accordingly.
     */
    private void handleIntent(Intent intent) {
        // Special processing of the incoming intent only occurs if the if the action specified
        // by the intent is ACTION_SEARCH.
        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            setProgressBarIndeterminateVisibility(true);

            // SearchManager.QUERY is the key that a SearchManager will use to send a query string
            // to an Activity.
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "query=" + query);

            SearchArtistTask task = new SearchArtistTask();
            task.execute(query);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        // Get the SearchView and set the searchable configuration
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.search).getActionView();
        // Assumes current activity is the searchable activity
        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent intent = new Intent(this, SettingsActivity.class);
            startActivity(intent);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) this.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public void updateAdapter(List<Artist> items) {
        ArtistArrayAdapter adapter = (ArtistArrayAdapter) mainFragment.getListAdapter();

        if (adapter == null) {
            Log.w(TAG, "adapter should not be null");
        } else {
            adapter.clear();
            adapter.addAll(items);
            adapter.notifyDataSetChanged();
        }

        setProgressBarIndeterminateVisibility(false);
    }

    public void toastNoNetwork() {
        Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
    }

    private class SearchArtistTask extends AsyncTask<String, Void, ArtistsPager> {
        @Override
        protected ArtistsPager doInBackground(String... queries) {
            ArtistsPager results = new ArtistsPager();

            if (isNetworkAvailable()) {
                SpotifyApi api = new SpotifyApi();
                SpotifyService spotify = api.getService();

                for (String query : queries) {
                    results = spotify.searchArtists(query);
                }
            } else {
                results.artists.items = new ArrayList<>();
                toastNoNetwork();
            }

            return results;
        }

        @Override
        protected void onPostExecute(ArtistsPager results) {
            Log.i(TAG, "searchArtists() returned ArtistPager");
            updateAdapter(results.artists.items);
        }
    }

}
