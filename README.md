# SpotifyStreamer
Udacity Android Developer Nanodegree Project 1 and 2

## Spotify Streamer, Stage 1: Implementation Guide
This project will utilize two key Android libraries, which we’ve included to reduce unnecessary extra work and help you focus on applying your app development skills.

1. Picasso - A powerful library that will handle image loading and caching on your behalf.
2. Spotify-Web-Api-Wrapper - A clever Java wrapper for the Spotify Web API that provides java methods that map directly to Web API endpoints. In other words, this wrapper handles making the HTTP request to a desired API endpoint, and deserializes the JSON response so you don’t need to write any parsing code by hand. When you need to make a particular Web API call, you simply call the corresponding method in the library and act on the return data, already converted to Java objects that you can interact with programmatically.

### Build the UI: Search for an Artist, then Return Their Top Tracks
#### Task 1: UI to Search for an Artist
Design and build the layout for an Activity that allows the user to search for an artist and then return the results in a ListView.

#### Task 2: UI to Display the top 10 tracks for a selected artist
Design and build the layout for an Activity that displays the top tracks for a select artist.

#### Task 3: Query the Spotify Web API

##### Overview

For the two user interface flows described in tasks 1 & 2, you will need to fetch artist and track data from the Spotify Web API to populate your list views. As mentioned earlier, you will use the Spotify Web Api Wrapper to simplify this task since it handles making the HTTP request and deserializing the JSON response for you. You will be able to extract the artist and track metadata you need directly as java objects. 

You will issue the following requests to the Spotify Web API:

For task 1, you will need to request artist data via the Search for an Item web endpoint.

* Be sure to restrict the search to artists only by including the item type=”artist”.
* For each artist result you should extract the following data:
..* artist name
..* SpotifyId* - This is required by the Get an Artist’s Top Tracks query which will use afterwards.
..* artist thumbnail image

For task 2, you will need to request track data via the Get an Artist’s Top Tracks web endpoint.
* Specify a country code for the search (the API requires this). You can either set a hardcoded String in the query call, or make a preference screen to make the country code user-modifiable.
* For each track result you should extract the following data:
..* track name
..* album name
..* Album art thumbnail (large (640px for Now Playing screen) and small (200px for list items)). If the image size does not exist in the API response, you are free to choose whatever size is available.)
..* preview url* - This is an HTTP url that you use to stream audio. You won’t need to use this until Stage 2.

Programming Notes:
* Populate the ListView using a ListView Adapter.
* Fetch data from Spotify in the background using AsyncTask and The Spotify Web API Wrapper

## Spotify Streamer, Stage 2: Implementation Guide
### Build a Track Player and Optimize for Tablet
#### Task 1: Build a Simple Player UI
This player UI should display the following information:
* artist name
* album name
* album artwork
* track name
* track duration

This player UI should display the following playback controls:
* Play/Pause Button - (Displays “Pause” when a track is currently playing & displays “Play” when a playback is stopped)
* Next Track - advances forward to the next track in the top track list.
* Previous Track - advances to backward to the previous track in the top track list.
* (Scrub Bar) - This shows the current playback position in time and is scrubbable.

To get the icons for the playback controls, you can use the ones that are built-in on Android as drawables. Check out http://androiddrawables.com/ to see all the built-in drawables. The ones used in the mockup are found in the Other section of the site:
* ic_media_play
* ic_media_pause
* ic_media_next
* ic_media_previous

Referencing built-in Android drawables involve using the syntax @android:drawable/{drawable_id}. For example, @android:drawable/ic_media_play refers to the play button drawable.

#### Task 2: Implement playback for a selected track
You will use Android’s MediaPlayer API to stream the track preview of a currently selected track.

Please consult the guide on using MediaPlayer on developer.android.com
* Remember that you will be streaming tracks, not playing local audio files.
* Don’t forget to add the necessary permissions to your app manifest.

#### Task 3: Optimize the entire end to end experience for a tablet
Migrate the existing UI flow to use a Master-Detail Structure for tablet. If you haven’t done so already, you will want to implement three Fragments for your tablet UI: one for artist search, one for top track results, and another for Playback.
* If you need a review of how to build for tablet, please refer back to Lesson 5 of Developing Android Apps, where the instructors discuss a Master-Detail layout.
* To display the Now Playing screen in a dialog on the tablet and in a normal activity on the phone, you can use a DialogFragment, which can act as a normal fragment (for the phone) or show in a dialog (for the tablet). See the documentation on Dialogs for more information—the section called “Showing a Dialog Fullscreen or as an Embedded Fragment” is particularly helpful.
