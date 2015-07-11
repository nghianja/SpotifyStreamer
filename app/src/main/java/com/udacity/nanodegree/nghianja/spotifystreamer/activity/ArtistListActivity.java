package com.udacity.nanodegree.nghianja.spotifystreamer.activity;

import android.app.Activity;
import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.SearchView;
import android.widget.Toast;

import com.udacity.nanodegree.nghianja.spotifystreamer.R;
import com.udacity.nanodegree.nghianja.spotifystreamer.SpotifyStreamerApp;
import com.udacity.nanodegree.nghianja.spotifystreamer.task.SearchArtistTask;

/**
 * References:
 * [1] https://github.com/googlesamples/android-BasicContactables
 * [2] http://www.vogella.com/tutorials/AndroidBackgroundProcessing/article.html
 * [3] http://stackoverflow.com/questions/9157504/put-a-progressbar-on-actionbar
 * [4] http://stackoverflow.com/questions/25730163/how-to-save-custom-listfragment-state-with-orientation-change
 * [5] http://stackoverflow.com/questions/15313598/once-for-all-how-to-correctly-save-instance-state-of-fragments-in-back-stack
 */
public class ArtistListActivity extends Activity {

    private static final String TAG = "ArtistListActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        requestWindowFeature(Window.FEATURE_INDETERMINATE_PROGRESS);
        setContentView(R.layout.activity_artist_list);

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
            // SearchManager.QUERY is the key that a SearchManager will use to send a query string
            // to an Activity.
            String query = intent.getStringExtra(SearchManager.QUERY);
            Log.d(TAG, "query=" + query);

            if (SpotifyStreamerApp.isNetworkAvailable(this)) {
                setProgressBarIndeterminateVisibility(true);
                SearchArtistTask task = new SearchArtistTask();
                task.execute(query);
            } else {
                Toast.makeText(this, getResources().getString(R.string.no_network), Toast.LENGTH_SHORT).show();
            }
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

}
