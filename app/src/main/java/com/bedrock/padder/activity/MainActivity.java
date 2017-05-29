package com.bedrock.padder.activity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.SoundPool;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.view.animation.FastOutSlowInInterpolator;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.widget.Toast;

import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.bedrock.padder.R;
import com.bedrock.padder.fragment.AboutFragment;
import com.bedrock.padder.fragment.SettingsFragment;
import com.bedrock.padder.helper.AdmobService;
import com.bedrock.padder.helper.AnimService;
import com.bedrock.padder.helper.FabService;
import com.bedrock.padder.helper.IntentService;
import com.bedrock.padder.helper.SoundService;
import com.bedrock.padder.helper.ThemeService;
import com.bedrock.padder.helper.ToolbarService;
import com.bedrock.padder.helper.TutorialService;
import com.bedrock.padder.helper.WindowService;
import com.bedrock.padder.model.about.About;
import com.bedrock.padder.model.about.Bio;
import com.bedrock.padder.model.about.Detail;
import com.bedrock.padder.model.about.Item;
import com.bedrock.padder.model.app.theme.ColorData;
import com.bedrock.padder.model.preset.Deck;
import com.bedrock.padder.model.preset.Music;
import com.bedrock.padder.model.preset.Pad;
import com.bedrock.padder.model.preset.Preset;
import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.util.ArrayList;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.bedrock.padder.helper.WindowService.APPLICATION_ID;

@TargetApi(14)
@SuppressWarnings("deprecation")

public class MainActivity
        extends AppCompatActivity
        implements AboutFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {

    final AppCompatActivity a = this;
    final String qs = "quickstart";
    public static final String TAG = "MainActivity";

    public boolean tgl1 = false;
    public boolean tgl2 = false;
    public boolean tgl3 = false;
    public boolean tgl4 = false;
    public boolean tgl5 = false;
    public boolean tgl6 = false;
    public boolean tgl7 = false;
    public boolean tgl8 = false;

    private SharedPreferences prefs = null;

    int currentVersionCode;
    int themeColor = R.color.hello;
    // TODO color
    int color = R.color.cyan_400;

    private MaterialDialog PresetDialog;

    int toggleSoundId = 0;
    int togglePatternId = 0;

    private AnimService anim = new AnimService();
    private ThemeService t = new ThemeService();
    private SoundService sound = new SoundService();
    private WindowService w = new WindowService();
    private FabService fab = new FabService();
    private ToolbarService toolbar = new ToolbarService();
    private TutorialService tut = new TutorialService();
    private IntentService intent = new IntentService();
    private AdmobService ad = new AdmobService();

    private boolean doubleBackToExitPressedOnce = false;
    private boolean isToolbarVisible = false;
    private boolean isSettingsFromMenu = false;
    public static boolean isPresetLoading = false;
    public static boolean isPresetVisible = false;
    public static boolean isTutorialVisible = false;
    public static boolean isAboutVisible = false;
    public static boolean isSettingVisible = false;
    public static boolean isDeckShouldCleared = false;

    private int circularRevealDuration = 400;
    private int fadeAnimDuration = 200;

    private MaterialTapTargetPrompt promptToggle;   // 1
    private MaterialTapTargetPrompt promptButton;   // 2
    private MaterialTapTargetPrompt promptSwipe;    // 3
    private MaterialTapTargetPrompt promptLoop;     // 4
    private MaterialTapTargetPrompt promptPattern;  // 5
    private MaterialTapTargetPrompt promptFab;      // 6
    private MaterialTapTargetPrompt promptPreset;   // 7
    private MaterialTapTargetPrompt promptInfo;     // 8
    private MaterialTapTargetPrompt promptTutorial; // 9

    // TODO SET ON INTENT
    private Gson gson = new Gson();
    public static Preset presets[];
    public static Preset currentPreset = null;

    // TODO TAP launch
    //IabHelper mHelper;
    //IabBroadcastReceiver mBroadcastReceiver;

    // Used for circularReveal
    // End two is for settings coordination
    public static int coord[] = {0, 0, 0, 0};

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    // TODO this is a quickfix
    String fadedJson = "{\"about\":{\"actionbar_color\":\"#00D3BE\",\"bio\":{\"image\":\"about_bio_faded\",\"name\":\"Alan Olav Walker\",\"source\":\"Powered by Wikipedia X last.fm\",\"text\":\"Alan Walker (Alan Olav Walker) is a British-Norwegian record producer who was born in Northampton, England. He recorded electronic dance music single \\\"Faded\\\" and his song released on NoCopyrightSounds, \\\"Fade\\\".\",\"title\":\"Alan Walker\\u0027s biography\"},\"details\":[{\"items\":[{\"hint\":\"https://facebook.com/alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_facebook\",\"runnable_is_with_anim\":false,\"text_id\":\"facebook\"},{\"hint\":\"https://twitter.com/IAmAlanWalker\",\"hint_is_visible\":true,\"image_id\":\"about_detail_twitter\",\"runnable_is_with_anim\":false,\"text_id\":\"twitter\"},{\"hint\":\"https://soundcloud.com/alanwalker\",\"hint_is_visible\":true,\"image_id\":\"about_detail_soundcloud\",\"runnable_is_with_anim\":false,\"text_id\":\"soundcloud\"},{\"hint\":\"https://instagram.com/alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_instagram\",\"runnable_is_with_anim\":false,\"text_id\":\"instagram\"},{\"hint\":\"https://plus.google.com/u/0/+Alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_google_plus\",\"runnable_is_with_anim\":false,\"text_id\":\"google_plus\"},{\"hint\":\"https://youtube.com/user/DjWalkzz\",\"hint_is_visible\":true,\"image_id\":\"about_detail_youtube\",\"runnable_is_with_anim\":false,\"text_id\":\"youtube\"},{\"hint\":\"http://alanwalkermusic.no\",\"hint_is_visible\":true,\"image_id\":\"about_detail_web\",\"runnable_is_with_anim\":false,\"text_id\":\"web\"}],\"title\":\"About Alan Walker\"},{\"items\":[{\"hint\":\"https://soundcloud.com/alanwalker/faded-1\",\"hint_is_visible\":false,\"image_id\":\"about_detail_soundcloud\",\"runnable_is_with_anim\":false,\"text_id\":\"soundcloud\"},{\"hint\":\"https://youtu.be/60ItHLz5WEA\",\"hint_is_visible\":false,\"image_id\":\"about_detail_youtube\",\"runnable_is_with_anim\":false,\"text_id\":\"youtube\"},{\"hint\":\"https://open.spotify.com/track/1brwdYwjltrJo7WHpIvbYt\",\"hint_is_visible\":false,\"image_id\":\"about_detail_spotify\",\"runnable_is_with_anim\":false,\"text_id\":\"spotify\"},{\"hint\":\"https://play.google.com/store/music/album/Alan_Walker_Faded?id\\u003dBgdyyljvf7b624pbv5ylcrfevte\",\"hint_is_visible\":false,\"image_id\":\"about_detail_google_play_music\",\"runnable_is_with_anim\":false,\"text_id\":\"google_play_music\"},{\"hint\":\"https://itunes.apple.com/us/album/faded/id1196294554?i\\u003d1196294581\",\"hint_is_visible\":false,\"image_id\":\"about_detail_apple\",\"runnable_is_with_anim\":false,\"text_id\":\"apple\"},{\"hint\":\"https://amazon.com/Faded/dp/B01NBYNKWJ\",\"hint_is_visible\":false,\"image_id\":\"about_detail_amazon\",\"runnable_is_with_anim\":false,\"text_id\":\"amazon\"},{\"hint\":\"https://pandora.com/alan-walker/faded-single/faded\",\"hint_is_visible\":false,\"image_id\":\"about_detail_pandora\",\"runnable_is_with_anim\":false,\"text_id\":\"pandora\"}],\"title\":\"About this track\"}],\"image\":\"about_album_faded\",\"preset_creator\":\"Studio Berict\",\"title\":\"Alan Walker - Faded\",\"tutorial_link\":\"null\"},\"id\":2,\"music\":{\"bpm\":90,\"file_name\":\"alan_walker_faded\",\"is_gesture\":true,\"name\":\"preset_faded\",\"sound_count\":246}}";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // TODO IAP launch
        //String base64EncodePublicKey = constructBase64Key();

        //mHelper = new IabHelper(this, base64EncodePublicKey);
        //mHelper.enableDebugLogging(true);

        // Start setup. This is asynchronous and the specified listener
        // will be called once setup completes.
        //Log.d(TAG, "Starting setup.");
        //mHelper.startSetup(new IabHelper.OnIabSetupFinishedListener() {
        //    public void onIabSetupFinished(IabResult result) {
        //        Log.d(TAG, "Setup finished.");

        //        if (!result.isSuccess()) {
        //            // Oh noes, there was a problem.
        //            complain("Problem setting up in-app billing: " + result);
        //            return;
        //        }

        //        // Have we been disposed of in the meantime? If so, quit.
        //        if (mHelper == null) return;

        //        // Important: Dynamically register for broadcast messages about updated purchases.
        //        // We register the receiver here instead of as a <receiver> in the Manifest
        //        // because we always call getPurchases() at startup, so therefore we can ignore
        //        // any broadcasts sent while the app isn't running.
        //        // Note: registering this listener in an Activity is a bad idea, but is done here
        //        // because this is a SAMPLE. Regardless, the receiver must be registered after
        //        // IabHelper is setup, but before first call to getPurchases().
        //        mBroadcastReceiver = new IabBroadcastReceiver(MainActivity.this);
        //        IntentFilter broadcastFilter = new IntentFilter(IabBroadcastReceiver.ACTION);
        //        registerReceiver(mBroadcastReceiver, broadcastFilter);

        //        // IAB is fully set up. Now, let's get an inventory of stuff we own.
        //        //Log.d(TAG, "Setup successful. Querying inventory.");
        //        //try {
        //        //    mHelper.queryInventoryAsync(mGotInventoryListener);
        //        //} catch (IabAsyncInProgressException e) {
        //        //    complain("Error querying inventory. Another async operation in progress.");
        //        //}
        //    }
        //});

        presets = new Preset[] {
                gson.fromJson(fadedJson, Preset.class),
                gson.fromJson(fadedJson, Preset.class),
                gson.fromJson(fadedJson, Preset.class)
        };

        //TODO needs fix

        // sharedPrefs
        Log.d(TAG, "Sharedprefs initialized");
        prefs = this.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);

        // for quickstart test
        //prefs.edit().putInt(qs, 0).apply();

        if (prefs.getBoolean("welcome", true)) {
            prefs.edit().putBoolean("welcome", false).apply();
        }

        color = prefs.getInt("color", R.color.cyan_400);
        toolbar.setActionBar(this);
        toolbar.setStatusBarTint(this);

        if (prefs.getString("colorData", null) == null) {
            // First run colorData json set
            ColorData placeHolderColorData = new ColorData(color);
            String colorDataJson = gson.toJson(placeHolderColorData);
            prefs.edit().putString("colorData", colorDataJson).apply();
            Log.d("ColorData pref", prefs.getString("colorData", null));
        }

        a.setVolumeControlStream(AudioManager.STREAM_MUSIC);

        // Set UI
        clearToggleButton();
        setFab();
        setToolbar();
        setSchemeInfo();
        setToggleButton(R.color.colorAccent);
        enterAnim();
        setButtonLayout();

        // Request ads
        ad.requestLoadNativeAd(ad.getNativeAdView(R.id.adView_main, a));

        // Set transparent nav bar
        w.setStatusBar(R.color.transparent, a);
        w.setNavigationBar(R.color.transparent, a);

        //ab.setStatusHeight(a);
        clearDeck(a);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.actionbar_item, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id) {
            case R.id.action_about:
                intent.intentWithExtra(a, "activity.AboutActivity", "about", "tapad", 0);
                break;
            case R.id.action_settings:
                Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        showSettingsFragment(a);
                        isSettingsFromMenu = true;
                    }
                }, 400);
                break;
            case R.id.action_help:
                //TODO this is for a test
                //intent.intent(a, "activity.HelpActivity", 0);
                runFileTest();
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    String string = "Hello";

    private void runFileTest() {

        string = "{\"about\":{\"actionbar_color\":\"#00D3BE\",\"bio\":{\"image\":\"about_bio_faded\",\"name\":\"Alan Olav Walker\",\"source\":\"Powered by Wikipedia X last.fm\",\"text\":\"Alan Walker (Alan Olav Walker) is a British-Norwegian record producer who was born in Northampton, England. He recorded electronic dance music single \\\"Faded\\\" and his song released on NoCopyrightSounds, \\\"Fade\\\".\",\"title\":\"Alan Walker\\u0027s biography\"},\"details\":[{\"items\":[{\"hint\":\"https://facebook.com/alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_facebook\",\"runnable_is_with_anim\":false,\"text_id\":\"facebook\"},{\"hint\":\"https://twitter.com/IAmAlanWalker\",\"hint_is_visible\":true,\"image_id\":\"about_detail_twitter\",\"runnable_is_with_anim\":false,\"text_id\":\"twitter\"},{\"hint\":\"https://soundcloud.com/alanwalker\",\"hint_is_visible\":true,\"image_id\":\"about_detail_soundcloud\",\"runnable_is_with_anim\":false,\"text_id\":\"soundcloud\"},{\"hint\":\"https://instagram.com/alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_instagram\",\"runnable_is_with_anim\":false,\"text_id\":\"instagram\"},{\"hint\":\"https://plus.google.com/u/0/+Alanwalkermusic\",\"hint_is_visible\":true,\"image_id\":\"about_detail_google_plus\",\"runnable_is_with_anim\":false,\"text_id\":\"google_plus\"},{\"hint\":\"https://youtube.com/user/DjWalkzz\",\"hint_is_visible\":true,\"image_id\":\"about_detail_youtube\",\"runnable_is_with_anim\":false,\"text_id\":\"youtube\"},{\"hint\":\"http://alanwalkermusic.no\",\"hint_is_visible\":true,\"image_id\":\"about_detail_web\",\"runnable_is_with_anim\":false,\"text_id\":\"web\"}],\"title\":\"About Alan Walker\"},{\"items\":[{\"hint\":\"https://soundcloud.com/alanwalker/faded-1\",\"hint_is_visible\":false,\"image_id\":\"about_detail_soundcloud\",\"runnable_is_with_anim\":false,\"text_id\":\"soundcloud\"},{\"hint\":\"https://youtu.be/60ItHLz5WEA\",\"hint_is_visible\":false,\"image_id\":\"about_detail_youtube\",\"runnable_is_with_anim\":false,\"text_id\":\"youtube\"},{\"hint\":\"https://open.spotify.com/track/1brwdYwjltrJo7WHpIvbYt\",\"hint_is_visible\":false,\"image_id\":\"about_detail_spotify\",\"runnable_is_with_anim\":false,\"text_id\":\"spotify\"},{\"hint\":\"https://play.google.com/store/music/album/Alan_Walker_Faded?id\\u003dBgdyyljvf7b624pbv5ylcrfevte\",\"hint_is_visible\":false,\"image_id\":\"about_detail_google_play_music\",\"runnable_is_with_anim\":false,\"text_id\":\"google_play_music\"},{\"hint\":\"https://itunes.apple.com/us/album/faded/id1196294554?i\\u003d1196294581\",\"hint_is_visible\":false,\"image_id\":\"about_detail_apple\",\"runnable_is_with_anim\":false,\"text_id\":\"apple\"},{\"hint\":\"https://amazon.com/Faded/dp/B01NBYNKWJ\",\"hint_is_visible\":false,\"image_id\":\"about_detail_amazon\",\"runnable_is_with_anim\":false,\"text_id\":\"amazon\"},{\"hint\":\"https://pandora.com/alan-walker/faded-single/faded\",\"hint_is_visible\":false,\"image_id\":\"about_detail_pandora\",\"runnable_is_with_anim\":false,\"text_id\":\"pandora\"}],\"title\":\"About this track\"}],\"image\":\"about_album_faded\",\"preset_creator\":\"Studio Berict\",\"title\":\"Alan Walker - Faded\",\"tutorial_link\":\"null\"},\"id\":2,\"music\":{\"bpm\":90,\"file_name\":\"alan_walker_faded\",\"is_gesture\":true,\"name\":\"preset_faded\",\"sound_count\":246}}";

        FileOutputStream outputStream;

        try {
            outputStream = openFileOutput("test.txt", MODE_PRIVATE);
            outputStream.write(string.getBytes());
            outputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FileInputStream inputStream;

        try {
            inputStream = openFileInput("test.txt");
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
            BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                stringBuilder.append(line).append("\n");
            }
            Log.i(TAG, stringBuilder.toString());
            inputStream.close();
            inputStreamReader.close();
            bufferedReader.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        // file input output
    }

    @Override
    public void onFragmentInteraction(Uri uri){
        // leave it empty
        // used for fragments
    }

    @Override
    public void onBackPressed() {
        Log.i("BackPressed", "isAboutVisible " + String.valueOf(isAboutVisible));
        Log.i("BackPressed", "isSettingVisible " + String.valueOf(isSettingVisible));
        if (isToolbarVisible == true) {
            if (prefs.getInt(qs, 0) > 0 && isAboutVisible == false && isSettingVisible == false) {
                Log.i("BackPressed", String.valueOf(prefs.getInt(qs, 0)));
                Log.i("BackPressed", "Quickstart tap target prompt is visible, backpress ignored.");
            } else {
                // new structure
                if (isAboutVisible && isSettingVisible) {
                    // Setting is visible above about
                    closeSettings();
                } else if (isSettingVisible) {
                    // Setting visible alone
                    closeSettings();
                } else if (isAboutVisible) {
                    // About visible alone
                    closeAbout();
                } else {
                    // Toolbar visible alone
                    closeToolbar(a);
                }
            }
        } else if (isSettingVisible == true) {
            // Setting is visible above about
            closeSettings();
        } else {
            if (prefs.getInt(qs, 0) > 0) {
                Log.i("BackPressed", "Tutorial prompt is visible, backpress ignored.");
            } else {
                Log.d("BackPressed", "Down");
                if (doubleBackToExitPressedOnce) {
                    super.onBackPressed();
                    finish();
                }

                doubleBackToExitPressedOnce = true;

                Toast.makeText(this, R.string.confirm_exit, Toast.LENGTH_SHORT).show();

                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {
                        doubleBackToExitPressedOnce = false;
                    }
                }, 2000);
            }
        }
    }

    public void onWindowFocusChanged(boolean hasFocus) {
        Log.i("MainActivity", "onWindowFocusChanged");
        sound.soundAllStop();

        int tutorial[] = {
                R.id.btn00_tutorial,
                R.id.tgl1_tutorial,
                R.id.tgl2_tutorial,
                R.id.tgl3_tutorial,
                R.id.tgl4_tutorial,
                R.id.tgl5_tutorial,
                R.id.tgl6_tutorial,
                R.id.tgl7_tutorial,
                R.id.tgl8_tutorial,
                R.id.btn11_tutorial,
                R.id.btn12_tutorial,
                R.id.btn13_tutorial,
                R.id.btn14_tutorial,
                R.id.btn21_tutorial,
                R.id.btn22_tutorial,
                R.id.btn23_tutorial,
                R.id.btn24_tutorial,
                R.id.btn31_tutorial,
                R.id.btn32_tutorial,
                R.id.btn33_tutorial,
                R.id.btn34_tutorial,
                R.id.btn41_tutorial,
                R.id.btn42_tutorial,
                R.id.btn43_tutorial,
                R.id.btn44_tutorial};

        for (int i = 0; i < tutorial.length; i++) {
            t.setInvisible(tutorial[i], 10, a);
        }

        color = prefs.getInt("color", R.color.cyan_400);

        clearToggleButton();
        super.onWindowFocusChanged(hasFocus);
    }
    
    @Override
    public void onPause() {
        ad.pauseNativeAdView(R.id.adView_main, a);
        super.onPause();
    }

    @Override
    public void onResume() {
        super.onResume();

        if (isTutorialVisible == true) {
            tut.tutorialStop(a);
        }

        Log.d("MainActivity", "onResume");
        sound.soundAllStop();
        int tutorial[] = {
                R.id.btn00_tutorial,
                R.id.tgl1_tutorial,
                R.id.tgl2_tutorial,
                R.id.tgl3_tutorial,
                R.id.tgl4_tutorial,
                R.id.tgl5_tutorial,
                R.id.tgl6_tutorial,
                R.id.tgl7_tutorial,
                R.id.tgl8_tutorial,
                R.id.btn11_tutorial,
                R.id.btn12_tutorial,
                R.id.btn13_tutorial,
                R.id.btn14_tutorial,
                R.id.btn21_tutorial,
                R.id.btn22_tutorial,
                R.id.btn23_tutorial,
                R.id.btn24_tutorial,
                R.id.btn31_tutorial,
                R.id.btn32_tutorial,
                R.id.btn33_tutorial,
                R.id.btn34_tutorial,
                R.id.btn41_tutorial,
                R.id.btn42_tutorial,
                R.id.btn43_tutorial,
                R.id.btn44_tutorial};

        for (int i = 0; i < tutorial.length; i++) {
            t.setInvisible(tutorial[i], 10, a);
        }

        ad.resumeNativeAdView(R.id.adView_main, a);
    }

    @Override
    protected void onDestroy() {
        Log.d(TAG, "onDestroy");

        sound.soundAllStop();
        sound.cancelLoading();

        ad.destroyNativeAdView(R.id.adView_main, a);
        
        super.onDestroy();
    }

    private void showAboutFragment() {
        getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_about_container, new AboutFragment())
                .commit();
        WindowService w = new WindowService();
        w.getView(R.id.fragment_about_container, a).setVisibility(View.VISIBLE);
        setAboutVisible(true);
        w.setRecentColor(R.string.about, 0, themeColor, a);
    }

    public static void showSettingsFragment(AppCompatActivity a) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_settings_container, new SettingsFragment())
                .commit();
        WindowService w = new WindowService();
        w.getView(R.id.fragment_settings_container, a).setVisibility(View.VISIBLE);
        setSettingVisible(true);
        w.setRecentColor(R.string.settings, 0, R.color.colorAccent, a);
    }

    // TODO iap launch
    //    IabHelper.QueryInventoryFinishedListener mGotInventoryListener = new IabHelper.QueryInventoryFinishedListener() {
    //        public void onQueryInventoryFinished(IabResult result, Inventory inventory) {
    //            Log.d(TAG, "Query inventory finished.");
    //
    //            // Have we been disposed of in the meantime? If so, quit.
    //            if (mHelper == null) return;
    //
    //            // Is it a failure?
    //            if (result.isFailure()) {
    //                complain("Failed to query inventory: " + result);
    //                return;
    //            }
    //
    //            Log.d(TAG, "Query inventory was successful.");
    //            Log.d(TAG, "Initial inventory query finished; enabling main UI.");
    //        }
    //    };
    //
    //    @NonNull
    //    private String constructBase64Key() {
    //        // TODO work on iap processes
    //        String encodedString = getResources().getString(R.string.base64_rsa_key);
    //        int base64Length = encodedString.length();
    //        char[] encodedStringArray = encodedString.toCharArray();
    //        char temp;
    //
    //        for(int i = 0; i < base64Length / 2; i++) {
    //            if (i % 2 == 0) {
    //                // ******   E P I C   D E C O D I N G   M E C H A N I S M   ****** //
    //                temp = encodedStringArray[i];
    //                encodedStringArray[i] = encodedStringArray[base64Length - 1 - i];
    //                encodedStringArray[base64Length - 1 - i] = temp;
    //            }
    //        }
    //
    //        return String.valueOf(encodedStringArray);
    //    }
    //
    //    private void complain(String message) {
    //        Log.e(TAG, "**** Purchase Error: " + message);
    //        alert("Error: " + message);
    //    }
    //
    //    private void alert(String message) {
    //        AlertDialog.Builder bld = new AlertDialog.Builder(this);
    //        bld.setMessage(message);
    //        bld.setNeutralButton("OK", null);
    //        Log.d(TAG, "Showing alert dialog: " + message);
    //        bld.create().show();
    //    }

    private void enterAnim() {
        anim.fadeIn(R.id.actionbar_layout, 0, 200, "background", a);
        anim.fadeIn(R.id.actionbar_image, 200, 200, "image", a);
        //TODO: Remove this to not load preset
        //loadPreset(400);
        makeJson();
        isPresetLoading = true;
    }

    private void setButtonLayout() {
        int screenWidthPx = (int)(w.getWindowWidthPx(a) * (0.8));
        int marginPx = w.getWindowWidthPx(a) / 160;
        int newWidthPx;
        int newHeightPx;
        int buttons[][] = {
                // first row is root view
                {R.id.ver0, R.id.tgl1, R.id.tgl2, R.id.tgl3, R.id.tgl4, R.id.btn00},
                {R.id.ver1, R.id.btn11, R.id.btn12, R.id.btn13, R.id.btn14, R.id.tgl5},
                {R.id.ver2, R.id.btn21, R.id.btn22, R.id.btn23, R.id.btn24, R.id.tgl6},
                {R.id.ver3, R.id.btn31, R.id.btn32, R.id.btn33, R.id.btn34, R.id.tgl7},
                {R.id.ver4, R.id.btn41, R.id.btn42, R.id.btn43, R.id.btn44, R.id.tgl8},
        };

        int tutorialButtons[][] = {
                // first row is root view
                {R.id.ver0_tutorial, R.id.tgl1_tutorial, R.id.tgl2_tutorial, R.id.tgl3_tutorial, R.id.tgl4_tutorial, R.id.btn00_tutorial},
                {R.id.ver1_tutorial, R.id.btn11_tutorial, R.id.btn12_tutorial, R.id.btn13_tutorial, R.id.btn14_tutorial, R.id.tgl5_tutorial},
                {R.id.ver2_tutorial, R.id.btn21_tutorial, R.id.btn22_tutorial, R.id.btn23_tutorial, R.id.btn24_tutorial, R.id.tgl6_tutorial},
                {R.id.ver3_tutorial, R.id.btn31_tutorial, R.id.btn32_tutorial, R.id.btn33_tutorial, R.id.btn34_tutorial, R.id.tgl7_tutorial},
                {R.id.ver4_tutorial, R.id.btn41_tutorial, R.id.btn42_tutorial, R.id.btn43_tutorial, R.id.btn44_tutorial, R.id.tgl8_tutorial},
        };

        for (int i = 0; i < 5; i++) {
            if (i == 0) {
                newHeightPx = screenWidthPx / 9;
            } else {
                newHeightPx = (screenWidthPx / 9) * 2;
            }
            for (int j = 0; j < 6; j++) {
                if (j == 0) {
                    resizeView(tutorialButtons[i][j], 0, newHeightPx);
                    resizeView(buttons[i][j], 0, newHeightPx);
                } else if (j == 5) {
                    newWidthPx = screenWidthPx / 9;
                    resizeView(tutorialButtons[i][j], newWidthPx, newHeightPx);
                    resizeView(buttons[i][j], newWidthPx - (marginPx * 2), newHeightPx - (marginPx * 2));
                    w.setMarginLinearPX(buttons[i][j], marginPx, marginPx, marginPx, marginPx, a);
                    if (i != 0) {
                        w.getTextView(buttons[i][j], a).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (newWidthPx / 3));
                    }
                } else {
                    newWidthPx = (screenWidthPx / 9) * 2;
                    resizeView(tutorialButtons[i][j], newWidthPx, newHeightPx);
                    resizeView(buttons[i][j], newWidthPx - (marginPx * 2), newHeightPx - (marginPx * 2));
                    w.setMarginLinearPX(buttons[i][j], marginPx, marginPx, marginPx, marginPx, a);
                    if (i == 0) {
                        w.getTextView(buttons[i][j], a).setTextSize(TypedValue.COMPLEX_UNIT_PX, (float) (newHeightPx / 3));
                    }
                }
            }
        }
    }

    private void resizeView(int viewId, int newWidth, int newHeight) {
        View view = a.findViewById(viewId);
        Log.d("resizeView", "width " + newWidth + " X height " + newHeight);
        if (newHeight > 0) {
            view.getLayoutParams().height = newHeight;
        }
        if (newWidth > 0) {
            view.getLayoutParams().width = newWidth;
        }
        view.setLayoutParams(view.getLayoutParams());
    }

    public void setQuickstart(final Activity activity) {
        final SharedPreferences pref = activity.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);
        try {
            currentVersionCode = activity.getPackageManager().getPackageInfo(activity.getPackageName(), 0).versionCode;
            Log.i("versionCode", "versionCode retrieved = " + String.valueOf(currentVersionCode));
        } catch (android.content.pm.PackageManager.NameNotFoundException e) {
            // handle exception
            currentVersionCode = -1;
            Log.e("NameNotFound", "NNFE, currentVersionCode = -1");
        }

        try {
            Log.d("VersionCode", "sharedPrefs versionCode = " + String.valueOf(pref.getInt("versionCode", -1))
                    + " , currentVersionCode = " + String.valueOf(currentVersionCode));
            Log.d("VersionCode", "Updated, show changelog");

            if (currentVersionCode > pref.getInt("versionCode", -1)) {
                // new app and updates
                new MaterialDialog.Builder(activity)
                        .title(w.getStringId("info_tapad_info_changelog"))
                        .content(w.getStringId("info_tapad_info_changelog_text"))
                        .contentColorRes(R.color.dark_primary)
                        .positiveText(R.string.dialog_close)
                        .positiveColorRes(R.color.colorAccent)
                        .dismissListener(new DialogInterface.OnDismissListener() {
                            @Override
                            public void onDismiss(DialogInterface dialogInterface) {
                                // Dialog
                                if (pref.getInt(qs, 0) == 0) {
                                    closeToolbar(activity);
                                    w.setVisible(R.id.fab, 300, activity);
                                    fab.setFab(activity);
                                    new MaterialDialog.Builder(activity)
                                            .title(R.string.dialog_quickstart_welcome_title)
                                            .content(R.string.dialog_quickstart_welcome_text)
                                            .positiveText(R.string.dialog_quickstart_welcome_positive)
                                            .positiveColorRes(R.color.colorAccent)
                                            .negativeText(R.string.dialog_quickstart_welcome_negative)
                                            .negativeColorRes(R.color.dark_secondary)
                                            .onPositive(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    pref.edit().putInt(qs, 0).apply();
                                                    Log.i("sharedPrefs", "quickstart edited to 0");
                                                }
                                            })
                                            .onNegative(new MaterialDialog.SingleButtonCallback() {
                                                @Override
                                                public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                                                    pref.edit().putInt(qs, -1).apply();
                                                    Log.i("sharedPrefs", "quickstart edited to -1");
                                                }
                                            })
                                            .dismissListener(new DialogInterface.OnDismissListener() {
                                                @Override
                                                public void onDismiss(DialogInterface dialogInterface) {
                                                    if (pref.getInt(qs, 0) == 0) {
                                                        Log.i("setQuickstart", "Quickstart started");
                                                        if (promptFab != null) {
                                                            return;
                                                        }
                                                        promptToggle = new MaterialTapTargetPrompt.Builder(activity)
                                                                .setTarget(activity.findViewById(R.id.tgl1))
                                                                .setPrimaryText(R.string.dialog_tap_target_toggle_primary)
                                                                .setSecondaryText(R.string.dialog_tap_target_toggle_secondary)
                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                .setAutoDismiss(false)
                                                                .setAutoFinish(false)
                                                                .setFocalColourFromRes(R.color.white)
                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                    @Override
                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                        if (tappedTarget) {
                                                                            promptToggle.finish();
                                                                            promptToggle = null;
                                                                            pref.edit().putInt(qs, 1).apply();
                                                                            Log.i("sharedPrefs", "quickstart edited to 1");
                                                                        }
                                                                    }

                                                                    @Override
                                                                    public void onHidePromptComplete() {
                                                                        promptButton = new MaterialTapTargetPrompt.Builder(activity)
                                                                                .setTarget(activity.findViewById(R.id.btn31))
                                                                                .setPrimaryText(R.string.dialog_tap_target_button_primary)
                                                                                .setSecondaryText(R.string.dialog_tap_target_button_secondary)
                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                .setAutoDismiss(false)
                                                                                .setAutoFinish(false)
                                                                                .setFocalColourFromRes(R.color.white)
                                                                                .setFocalRadius((float) w.convertDPtoPX(80, activity))
                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                    @Override
                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                        if (tappedTarget) {
                                                                                            promptButton.finish();
                                                                                            promptButton = null;
                                                                                            pref.edit().putInt(qs, 3).apply();
                                                                                            Log.i("sharedPrefs", "quickstart edited to 3");
                                                                                        }
                                                                                    }

                                                                                    @Override
                                                                                    public void onHidePromptComplete() {
                                                                                        promptSwipe = new MaterialTapTargetPrompt.Builder(activity)
                                                                                                .setTarget(activity.findViewById(R.id.btn23))
                                                                                                .setPrimaryText(R.string.dialog_tap_target_swipe_primary)
                                                                                                .setSecondaryText(R.string.dialog_tap_target_swipe_secondary)
                                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                                .setAutoDismiss(false)
                                                                                                .setAutoFinish(false)
                                                                                                .setFocalColourFromRes(R.color.white)
                                                                                                .setFocalRadius((float) w.convertDPtoPX(80, activity))
                                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                                    @Override
                                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                                        if (tappedTarget) {
                                                                                                            promptSwipe.finish();
                                                                                                            promptSwipe = null;
                                                                                                            pref.edit().putInt(qs, 4).apply();
                                                                                                            Log.i("sharedPrefs", "quickstart edited to 4");
                                                                                                        }
                                                                                                    }

                                                                                                    @Override
                                                                                                    public void onHidePromptComplete() {
                                                                                                        promptLoop = new MaterialTapTargetPrompt.Builder(activity)
                                                                                                                .setTarget(activity.findViewById(R.id.btn42))
                                                                                                                .setPrimaryText(R.string.dialog_tap_target_loop_primary)
                                                                                                                .setSecondaryText(R.string.dialog_tap_target_loop_secondary)
                                                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                                                .setAutoDismiss(false)
                                                                                                                .setAutoFinish(false)
                                                                                                                .setFocalColourFromRes(R.color.white)
                                                                                                                .setFocalRadius((float) w.convertDPtoPX(80, activity))
                                                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                                                    @Override
                                                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                                                        if (tappedTarget) {
                                                                                                                            promptLoop.finish();
                                                                                                                            promptLoop = null;
                                                                                                                            pref.edit().putInt(qs, 5).apply();
                                                                                                                            Log.i("sharedPrefs", "quickstart edited to 5");
                                                                                                                        }
                                                                                                                    }

                                                                                                                    @Override
                                                                                                                    public void onHidePromptComplete() {
                                                                                                                        promptPattern = new MaterialTapTargetPrompt.Builder(activity)
                                                                                                                                .setTarget(activity.findViewById(R.id.tgl7))
                                                                                                                                .setPrimaryText(R.string.dialog_tap_target_pattern_primary)
                                                                                                                                .setSecondaryText(R.string.dialog_tap_target_pattern_secondary)
                                                                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                                                                .setAutoDismiss(false)
                                                                                                                                .setAutoFinish(false)
                                                                                                                                .setFocalColourFromRes(R.color.white)
                                                                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                                                                    @Override
                                                                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                                                                        if (tappedTarget) {
                                                                                                                                            promptPattern.finish();
                                                                                                                                            promptPattern = null;
                                                                                                                                            pref.edit().putInt(qs, 5).apply();
                                                                                                                                            Log.i("sharedPrefs", "quickstart edited to 5");
                                                                                                                                        }
                                                                                                                                    }

                                                                                                                                    @Override
                                                                                                                                    public void onHidePromptComplete() {
                                                                                                                                        promptFab = new MaterialTapTargetPrompt.Builder(activity)
                                                                                                                                                .setTarget(activity.findViewById(R.id.fab))
                                                                                                                                                .setPrimaryText(R.string.dialog_tap_target_fab_primary)
                                                                                                                                                .setSecondaryText(R.string.dialog_tap_target_fab_secondary)
                                                                                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                                                                                .setAutoDismiss(false)
                                                                                                                                                .setAutoFinish(false)
                                                                                                                                                .setFocalColourFromRes(R.color.white)
                                                                                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                                                                                    @Override
                                                                                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                                                                                        if (tappedTarget) {
                                                                                                                                                            promptFab.finish();
                                                                                                                                                            promptFab = null;
                                                                                                                                                            pref.edit().putInt(qs, 6).apply();
                                                                                                                                                            Log.i("sharedPrefs", "quickstart edited to 6");
                                                                                                                                                        }
                                                                                                                                                    }

                                                                                                                                                    @Override
                                                                                                                                                    public void onHidePromptComplete() {
                                                                                                                                                        promptPreset = new MaterialTapTargetPrompt.Builder(activity)
                                                                                                                                                                .setTarget(activity.findViewById(R.id.toolbar_preset))
                                                                                                                                                                .setPrimaryText(R.string.dialog_tap_target_preset_primary)
                                                                                                                                                                .setSecondaryText(R.string.dialog_tap_target_preset_secondary)
                                                                                                                                                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                                                                                                                                                .setAutoDismiss(false)
                                                                                                                                                                .setAutoFinish(false)
                                                                                                                                                                .setFocalColourFromRes(R.color.blue_500)
                                                                                                                                                                .setCaptureTouchEventOutsidePrompt(true)
                                                                                                                                                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                                                                                                                                                    @Override
                                                                                                                                                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                                                                                                                                                        if (tappedTarget) {
                                                                                                                                                                            promptPreset.finish();
                                                                                                                                                                            promptPreset = null;
                                                                                                                                                                            pref.edit().putInt(qs, 7).apply();
                                                                                                                                                                            Log.i("sharedPrefs", "quickstart edited to 7");
                                                                                                                                                                        }
                                                                                                                                                                    }

                                                                                                                                                                    @Override
                                                                                                                                                                    public void onHidePromptComplete() {
                                                                                                                                                                    }
                                                                                                                                                                })
                                                                                                                                                                .show();
                                                                                                                                                    }
                                                                                                                                                })
                                                                                                                                                .show();
                                                                                                                                    }
                                                                                                                                })
                                                                                                                                .show();
                                                                                                                    }
                                                                                                                })
                                                                                                                .show();
                                                                                                    }
                                                                                                })
                                                                                                .show();
                                                                                    }
                                                                                })
                                                                                .show();
                                                                    }
                                                                })
                                                                .show();
                                                    } else {
                                                        Log.i("setQuickstart", "Quickstart canceled");
                                                        pref.edit().putInt(qs, -1).apply();
                                                    }
                                                }
                                            })
                                            .show();
                                }
                                pref.edit().putInt("versionCode", currentVersionCode).apply(); // Change this
                                Log.d("VersionCode", "putInt " + String.valueOf(pref.getInt("versionCode", -1)));
                            }
                        })
                        .show();
            }
        } catch (Exception e) {
            Log.e("QuickstartException", e.getMessage());
        }
    }

    private void setFab() {
        fab.setFab(a);
        fab.show();
        fab.onClick(new Runnable() {
            @Override
            public void run() {
                if (isToolbarVisible == false) {
                    fab.hide(0, 200);
                    anim.fadeIn(R.id.toolbar, 200, 100, "toolbarIn", a);
                    if (prefs.getInt(qs, 0) == 7) {
                        Log.i("setQuickstart", "Quickstart started");
                        if (promptInfo != null) {
                            return;
                        }
                        promptInfo = new MaterialTapTargetPrompt.Builder(a)
                                .setTarget(a.findViewById(R.id.toolbar_info))
                                .setPrimaryText(R.string.dialog_tap_target_info_primary)
                                .setSecondaryText(R.string.dialog_tap_target_info_secondary)
                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
                                .setFocalColourFromRes(R.color.blue_500)
                                .setAutoDismiss(false)
                                .setAutoFinish(false)
                                .setCaptureTouchEventOutsidePrompt(true)
                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                                    @Override
                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                                        if (tappedTarget) {
                                            promptInfo.finish();
                                            promptInfo = null;
                                            prefs.edit().putInt(qs, 8).apply();
                                            Log.i("sharedPrefs", "quickstart edited to 8");
                                        }
                                    }

                                    @Override
                                    public void onHidePromptComplete() {
                                    }
                                })
                                .show();
                    }
                    isToolbarVisible = true;
                }
            }
        });

        // set bottom margin
        w.setMarginRelativePX(R.id.fab, 0, 0, w.convertDPtoPX(20, a), w.getNavigationBarFromPrefs(a) + w.convertDPtoPX(20, a), a);
    }

    private void setToolbar() {
        // set bottom margin
        w.setMarginRelativePX(R.id.toolbar, 0, 0, 0, w.getNavigationBarFromPrefs(a), a);

        View info = findViewById(R.id.toolbar_info);
        View tutorial = findViewById(R.id.toolbar_tutorial);
        View preset = findViewById(R.id.toolbar_preset);
        View settings = findViewById(R.id.toolbar_settings);

        assert info != null;
        info.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coord[0] = (int) event.getRawX();
                coord[1] = (int) event.getRawY();

                return false;
            }
        });

        info.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isAboutVisible == false) {
                    anim.circularRevealInPx(R.id.placeholder,
                            coord[0], coord[1],
                            0, (int) Math.hypot(coord[0], coord[1]) + 200, new AccelerateDecelerateInterpolator(),
                            circularRevealDuration, 0, a);

                    Handler about = new Handler();
                    about.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showAboutFragment();
                        }
                    }, circularRevealDuration);

                    anim.fadeOut(R.id.placeholder, circularRevealDuration, fadeAnimDuration, a);

                    isAboutVisible = true;
                }
            }
        });

        assert preset != null;
        preset.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coord[0] = (int) event.getRawX();
                coord[1] = (int) event.getRawY();

                return false;
            }
        });

        preset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isPresetVisible == false) {
                    anim.circularRevealInPx(R.id.placeholder,
                            coord[0], coord[1],
                            0, (int) Math.hypot(coord[0], coord[1]) + 200, new AccelerateDecelerateInterpolator(),
                            circularRevealDuration, 0, a);

                    Handler preset = new Handler();
                    preset.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showPresetDialog(a);
                        }
                    }, circularRevealDuration);

                    isPresetVisible = true;
                }
            }
        });

        assert tutorial != null;
        tutorial.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coord[0] = (int) event.getRawX();
                coord[1] = (int) event.getRawY();

                return false;
            }
        });

        tutorial.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("isTutVisible", String.valueOf(isTutorialVisible));
                if (isTutorialVisible == false) {
                    toggleTutorial();
                    isTutorialVisible = true;
                }
            }
        });

        assert settings != null;
        settings.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                coord[2] = (int) event.getRawX();
                coord[3] = (int) event.getRawY();

                return false;
            }
        });

        settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isSettingVisible == false) {
                    w.setRecentColor(R.string.settings, 0, R.color.colorAccent, a);
                    anim.circularRevealInPx(R.id.placeholder,
                            coord[2], coord[3],
                            0, (int) Math.hypot(coord[2], coord[3]) + 200, new AccelerateDecelerateInterpolator(),
                            circularRevealDuration, 0, a);

                    Handler about = new Handler();
                    about.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            showSettingsFragment(a);
                        }
                    }, circularRevealDuration);

                    anim.fadeOut(R.id.placeholder, circularRevealDuration, fadeAnimDuration, a);

                    setSettingVisible(true);
                }
            }
        });
    }

    private void closeToolbar(Activity activity) {
        anim.fadeOut(R.id.toolbar, 0, 100, activity);
        fab.show(100, 200);
        isToolbarVisible = false;
    }

    private void closeAbout() {
        Log.d("closeAbout", "triggered");
        anim.circularRevealInPx(R.id.placeholder,
                coord[0], coord[1],
                (int) Math.hypot(coord[0], coord[1]) + 200, 0, new AccelerateDecelerateInterpolator(),
                circularRevealDuration, fadeAnimDuration, a);

        anim.fadeIn(R.id.placeholder, 0, fadeAnimDuration, "aboutOut", a);

        setAboutVisible(false);

        Handler closeAbout = new Handler();
        closeAbout.postDelayed(new Runnable() {
            @Override
            public void run() {
                setSchemeInfo();
                w.getView(R.id.fragment_about_container, a).setVisibility(View.GONE);
            }
        }, fadeAnimDuration);

        // Firstrun tutorial
        if (prefs.getInt(qs, 0) == 8) {
            promptPreset = new MaterialTapTargetPrompt.Builder(a)
                    .setTarget(a.findViewById(R.id.toolbar_preset))
                    .setPrimaryText(R.string.dialog_tap_target_preset_primary)
                    .setSecondaryText(R.string.dialog_tap_target_preset_secondary)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setFocalColourFromRes(R.color.blue_500)
                    .setAutoDismiss(false)
                    .setAutoFinish(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            if (tappedTarget) {
                                promptPreset.finish();
                                promptPreset = null;
                                prefs.edit().putInt(qs, 9).apply();
                                Log.i("sharedPrefs", "quickstart edited to 9");
                            }
                        }

                        @Override
                        public void onHidePromptComplete() {
                            // idk why is this here
                            //isTutorialVisible = false;
                        }
                    })
                    .show();
        }
    }

    public void toggleTutorial() {
        // TODO add 2gb ram limit if statement
        if (w.getView(R.id.progress_bar_layout, a).getVisibility() == View.GONE) {
            // on loading finished
//            if (isTutorialVisible == false) {
//                new MaterialDialog.Builder(a)
//                        .title(R.string.dialog_tutorial_warning_title)
//                        .content(R.string.dialog_tutorial_warning_text)
//                        .positiveText(R.string.dialog_tutorial_warning_positive)
//                        .positiveColorRes(R.color.red_500)
//                        .negativeText(R.string.dialog_tutorial_warning_negative)
//                        .negativeColorRes(R.color.dark_secondary)
//                        .onPositive(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                // TODO TUTORIAL
//                                //tut.tutorialStart(a);
//                                tut.initCurrentTiming();
//                                tut.startTutorial(tut.getCurrentTutorialDeckId(), a);
//                                isTutorialVisible = true;
//                                setTutorialUI();
//                                if (isSettingVisible == true) {
//                                    closeSettings();
//                                }
//                            }
//                        })
//                        .onNegative(new MaterialDialog.SingleButtonCallback() {
//                            @Override
//                            public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
//                                isTutorialVisible = false;
//                                setTutorialUI();
//                            }
//                        })
//                        .show();
//            } else {
//                tut.tutorialStop(a);
//                isTutorialVisible = false;
//                setTutorialUI();
//            }
            String tutorialText;
            if (presets[getScheme()].getAbout().getTutorialLink() == null) {
                tutorialText = w.getStringFromId("dialog_tutorial_text_error", a);
            } else {
                tutorialText = presets[getScheme()].getAbout().getTutorialLink();
            }

            new MaterialDialog.Builder(a)
                    .title(R.string.dialog_tutorial_title)
                    .content(tutorialText)
                    .neutralText(R.string.dialog_close)
                    .dismissListener(new DialogInterface.OnDismissListener() {
                        @Override
                        public void onDismiss(DialogInterface dialog) {
                            isTutorialVisible = false;
                        }
                    })
                    .show();
        } else {
            // still loading preset
            Toast.makeText(a, R.string.tutorial_loading, Toast.LENGTH_LONG).show();
            isTutorialVisible = false;
        }
    }

    private void setTutorialUI() {
        if (isTutorialVisible == true) {
            w.getImageView(R.id.toolbar_tutorial_icon, a).setImageResource(R.drawable.icon_tutorial_quit);
            w.getImageView(R.id.layout_settings_tutorial_icon, a).setImageResource(R.drawable.settings_tutorial_quit);
            w.getSwitchCompat(R.id.layout_settings_tutorial_switch, a).setChecked(true);
        } else {
            w.getImageView(R.id.toolbar_tutorial_icon, a).setImageResource(R.drawable.icon_tutorial);
            w.getImageView(R.id.layout_settings_tutorial_icon, a).setImageResource(R.drawable.settings_tutorial);
            w.getSwitchCompat(R.id.layout_settings_tutorial_switch, a).setChecked(false);
        }
    }

    private void closeSettings() {
        Log.d("closeSettings", "triggered");
        if (isToolbarVisible && !isSettingsFromMenu) {
            anim.circularRevealInPx(R.id.placeholder,
                    coord[2], coord[3],
                    (int) Math.hypot(coord[2], coord[3]) + 200, 0, new AccelerateDecelerateInterpolator(),
                    circularRevealDuration, fadeAnimDuration, a);

            anim.fadeIn(R.id.placeholder, 0, fadeAnimDuration, "settingOut", a);
        } else {
            w.getView(R.id.fragment_settings_container, a).setVisibility(View.GONE);
            isSettingsFromMenu = false;
        }

        color = prefs.getInt("color", R.color.cyan_400);
        clearToggleButton();

        setSettingVisible(false);

        Handler closeSettings = new Handler();
        closeSettings.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (isAboutVisible) {
                    // about visible set taskdesc
                    w.setRecentColor(R.string.about, 0, themeColor, a);
                } else {
                    setSchemeInfo();
                }
                w.getView(R.id.fragment_settings_container, a).setVisibility(View.GONE);
            }
        }, fadeAnimDuration);
    }

    private void showPresetDialog(final Activity a) {
        tut.tutorialStop(a);
        sound.soundAllStop();

        final int defaultPreset = getScheme();
        int color = currentPreset.getAbout().getActionbarColor();

        anim.fade(R.id.placeholder, 1.0f, 0.5f, 0, 200, "phIN", a);

        PresetDialog = new MaterialDialog.Builder(a)
                .title(R.string.dialog_preset_title)
                .items(R.array.presets)
                .autoDismiss(false)
                .itemsCallbackSingleChoice(defaultPreset, new MaterialDialog.ListCallbackSingleChoice() {
                    @Override
                    public boolean onSelection(MaterialDialog dialog, View view, int which, CharSequence text) {
                        setScheme(which);
                        int selectedPresetColor = presets[which].getAbout().getActionbarColor();
                        PresetDialog.getBuilder()
                                .widgetColorRes(selectedPresetColor)
                                .positiveColorRes(selectedPresetColor);
                        setSchemeInfo();

                        return true;
                    }
                })
                .alwaysCallSingleChoiceCallback()
                .widgetColorRes(color)
                .positiveText(R.string.dialog_preset_positive)
                .positiveColorRes(R.color.colorAccent)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        PresetDialog.dismiss();
                    }
                })
                .negativeText(R.string.dialog_preset_negative)
                .negativeColorRes(R.color.dark_secondary)
                .onNegative(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        setScheme(defaultPreset);
                        PresetDialog.dismiss();
                    }
                })
                .dismissListener(new DialogInterface.OnDismissListener() {
                    @Override
                    public void onDismiss(DialogInterface dialogInterface) {
                        if (defaultPreset != getScheme()) {
                            // preset changed
                            loadPreset(circularRevealDuration);
                            // deck should be cleared after the preset is cleaned
                            isDeckShouldCleared = true;
                            clearToggleButton();
                        }
                        anim.fade(R.id.placeholder, 0.5f, 1.0f, 0, 200, "phOUT", a);
                        closeDialogPreset();
                        setSchemeInfo();
                        isPresetVisible = false;
                    }
                })
                .show();
    }

    private void closeDialogPreset() {
        anim.circularRevealInPx(R.id.placeholder,
                coord[0], coord[1],
                (int) Math.hypot(coord[0], coord[1]) + 200, 0, new AccelerateDecelerateInterpolator(),
                circularRevealDuration, 200, a);

        if (prefs.getInt(qs, 0) == 7) {
            promptTutorial = new MaterialTapTargetPrompt.Builder(a)
                    .setTarget(a.findViewById(R.id.toolbar_tutorial))
                    .setPrimaryText(R.string.dialog_tap_target_tutorial_primary)
                    .setSecondaryText(R.string.dialog_tap_target_tutorial_secondary)
                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
                    .setFocalColourFromRes(R.color.blue_500)
                    .setAutoDismiss(false)
                    .setAutoFinish(false)
                    .setCaptureTouchEventOutsidePrompt(true)
                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
                        @Override
                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
                            if (tappedTarget) {
                                promptTutorial.finish();
                                promptTutorial = null;
                                prefs.edit().putInt(qs, -1).apply();
                                Log.i("sharedPrefs", "quickstart edited to -1, completed");
                            }
                        }

                        @Override
                        public void onHidePromptComplete() {
                        }
                    })
                    .show();
        }
    }

    private void loadPreset(int delay) {
        Handler preset = new Handler();
        preset.postDelayed(new Runnable() {
            @Override
            public void run() {
                sound.loadSchemeSound(presets[getScheme()], a);
            }
        }, delay);
    }

    private void setToggleButton(final int color_id) {
        // 1 - 4
        w.setOnTouch(R.id.tgl1, new Runnable() {
            @Override
            public void run() {
                clearDeck(a);
                if (tgl1 == false) {
                    toggleSoundId = 1;
                    if (tgl5 || tgl6 || tgl7 || tgl8) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    } else {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl1, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl2, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl3, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl4, R.color.grey, a);
                    if (tgl2 || tgl3 || tgl4) {
                        sound.playToggleButtonSound(1);
                    }
                } else {
                    w.setViewBackgroundColor(R.id.tgl1, R.color.grey, a);
                    toggleSoundId = 0;
                    sound.setButton(R.color.grey_dark, a);
                    sound.soundAllStop();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl1 == false) {
                    tgl1 = true;
                    tgl2 = false;
                    tgl3 = false;
                    tgl4 = false;
                } else {
                    tgl1 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl2, new Runnable() {
            @Override
            public void run() {
                clearDeck(a);
                if (tgl2 == false) {
                    toggleSoundId = 2;
                    if (tgl5 || tgl6 || tgl7 || tgl8) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    } else {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl2, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl1, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl3, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl4, R.color.grey, a);
                    sound.playToggleButtonSound(2);
                } else {
                    w.setViewBackgroundColor(R.id.tgl2, R.color.grey, a);
                    toggleSoundId = 0;
                    sound.setButton(R.color.grey_dark, a);
                    sound.soundAllStop();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl2 == false) {
                    tgl2 = true;
                    tgl1 = false;
                    tgl3 = false;
                    tgl4 = false;
                } else {
                    tgl2 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl3, new Runnable() {
            @Override
            public void run() {
                clearDeck(a);
                if (tgl3 == false) {
                    toggleSoundId = 3;
                    if (tgl5 || tgl6 || tgl7 || tgl8) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    } else {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl3, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl2, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl1, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl4, R.color.grey, a);
                    sound.playToggleButtonSound(3);
                } else {
                    w.setViewBackgroundColor(R.id.tgl3, R.color.grey, a);
                    toggleSoundId = 0;
                    sound.setButton(R.color.grey_dark, a);
                    sound.soundAllStop();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl3 == false) {
                    tgl3 = true;
                    tgl2 = false;
                    tgl1 = false;
                    tgl4 = false;
                } else {
                    tgl3 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl4, new Runnable() {
            @Override
            public void run() {
                clearDeck(a);
                if (tgl4 == false) {
                    toggleSoundId = 4;
                    if (tgl5 || tgl6 || tgl7 || tgl8) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    } else {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl4, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl2, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl3, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl1, R.color.grey, a);
                    sound.playToggleButtonSound(4);
                } else {
                    w.setViewBackgroundColor(R.id.tgl4, R.color.grey, a);
                    toggleSoundId = 0;
                    sound.setButton(R.color.grey_dark, a);
                    sound.soundAllStop();
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl4 == false) {
                    tgl4 = true;
                    tgl2 = false;
                    tgl3 = false;
                    tgl1 = false;
                } else {
                    tgl4 = false;
                }
            }
        }, a);

        // 5 - 8

        w.setOnTouch(R.id.tgl5, new Runnable() {
            @Override
            public void run() {
                if (tgl5 == false) {
                    togglePatternId = 1;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl5, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl6, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl7, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl8, R.color.grey, a);
                } else {
                    w.setViewBackgroundColor(R.id.tgl5, R.color.grey, a);
                    togglePatternId = 0;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl5 == false) {
                    tgl5 = true;
                    tgl6 = false;
                    tgl7 = false;
                    tgl8 = false;
                } else {
                    tgl5 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl6, new Runnable() {
            @Override
            public void run() {
                if (tgl6 == false) {
                    togglePatternId = 2;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl6, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl5, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl7, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl8, R.color.grey, a);
                } else {
                    w.setViewBackgroundColor(R.id.tgl6, R.color.grey, a);
                    togglePatternId = 0;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl6 == false) {
                    tgl6 = true;
                    tgl5 = false;
                    tgl7 = false;
                    tgl8 = false;
                } else {
                    tgl6 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl7, new Runnable() {
            @Override
            public void run() {
                if (tgl7 == false) {
                    togglePatternId = 3;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl7, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl6, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl5, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl8, R.color.grey, a);
                } else {
                    w.setViewBackgroundColor(R.id.tgl7, R.color.grey, a);
                    togglePatternId = 0;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl7 == false) {
                    tgl7 = true;
                    tgl6 = false;
                    tgl5 = false;
                    tgl8 = false;
                } else {
                    tgl7 = false;
                }
            }
        }, a);

        w.setOnTouch(R.id.tgl8, new Runnable() {
            @Override
            public void run() {
                if (tgl8 == false) {
                    togglePatternId = 4;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, togglePatternId, a);
                    }
                    w.setViewBackgroundColor(R.id.tgl8, color_id, a);
                    w.setViewBackgroundColor(R.id.tgl6, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl7, R.color.grey, a);
                    w.setViewBackgroundColor(R.id.tgl5, R.color.grey, a);
                } else {
                    w.setViewBackgroundColor(R.id.tgl8, R.color.grey, a);
                    togglePatternId = 0;
                    if (tgl1 || tgl2 || tgl3 || tgl4) {
                        sound.setButtonToggle(toggleSoundId, color, a);
                    }
                }
            }
        }, new Runnable() {
            @Override
            public void run() {
                if (tgl8 == false) {
                    tgl8 = true;
                    tgl6 = false;
                    tgl7 = false;
                    tgl5 = false;
                } else {
                    tgl8 = false;
                }
            }
        }, a);
    }

    public void clearDeck(Activity activity) {
        // clear button colors
        int buttonIds[] = {
                R.id.btn00,
                R.id.btn11,
                R.id.btn12,
                R.id.btn13,
                R.id.btn14,
                R.id.btn21,
                R.id.btn22,
                R.id.btn23,
                R.id.btn24,
                R.id.btn31,
                R.id.btn32,
                R.id.btn33,
                R.id.btn34,
                R.id.btn41,
                R.id.btn42,
                R.id.btn43,
                R.id.btn44
        };
        for (int buttonId : buttonIds) {
            View pad = activity.findViewById(buttonId);
            pad.setBackgroundColor(activity.getResources().getColor(R.color.grey));
        }

        // stop all looping sounds
        Integer streamIds[] = w.getLoopStreamIds();
        SoundPool soundPool = sound.getSoundPool();
        try {
            for (Integer streamId : streamIds) {
                soundPool.stop(streamId);
            }
        } finally {
            w.clearLoopStreamId();
        }
    }

    private void clearToggleButton() {
        if (isDeckShouldCleared) {
            w.setViewBackgroundColor(R.id.tgl1, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl2, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl3, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl4, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl5, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl6, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl7, R.color.grey, a);
            w.setViewBackgroundColor(R.id.tgl8, R.color.grey, a);

            tgl1 = false;
            tgl2 = false;
            tgl3 = false;
            tgl4 = false;
            tgl5 = false;
            tgl6 = false;
            tgl7 = false;
            tgl8 = false;
            sound.setButton(R.color.grey_dark, a);

            toggleSoundId = 0;
            sound.soundAllStop();

            isDeckShouldCleared = false;
        }
    }

    private void setSchemeInfo() {
        if (isSettingVisible == false && isAboutVisible == false) {
            currentPreset = presets[getScheme()];
            themeColor = currentPreset.getAbout().getActionbarColor();
            toolbar.setActionBarTitle(0, this);
            toolbar.setActionBarColor(themeColor, this);
            toolbar.setActionBarPadding(this);
            toolbar.setActionBarImage(
                    w.getDrawableId("logo_" + currentPreset.getMusic().getName().replace("preset_", "")),
                    this);
            w.setRecentColor(0, 0, themeColor, a);
        }
    }

    int getScheme() {
        if (prefs.getInt("scheme", 0) > 3 && prefs.getInt("scheme", 0) < 0) {
            // TOOD needs fix, why this happens?
            return 0;
        } else {
            return prefs.getInt("scheme", 0);
        }
    }

    private void setScheme(int scheme) {
        prefs.edit().putInt("scheme", scheme).apply();
    }

    public static void setSettingVisible(boolean isVisible) {
        isSettingVisible = isVisible;
        Log.d("SettingVisible", String.valueOf(isSettingVisible));
    }

    public static void setAboutVisible(boolean isVisible) {
        isAboutVisible = isVisible;
        Log.d("AboutVisible", String.valueOf(isAboutVisible));
    }

    private void makeJson() {
        Item fadedItems[] = {
                new Item("facebook", w.getStringFromId("preset_faded_detail_facebook", a)),
                new Item("twitter", w.getStringFromId("preset_faded_detail_twitter", a)),
                new Item("soundcloud", w.getStringFromId("preset_faded_detail_soundcloud", a)),
                new Item("instagram", w.getStringFromId("preset_faded_detail_instagram", a)),
                new Item("google_plus", w.getStringFromId("preset_faded_detail_google_plus", a)),
                new Item("youtube", w.getStringFromId("preset_faded_detail_youtube", a)),
                //new Item("twitch", w.getStringFromId("preset_faded_detail_twitch", a)), // only omfg
                new Item("web", w.getStringFromId("preset_faded_detail_web", a))
        };

        Detail fadedDetail = new Detail(w.getStringFromId("preset_faded_detail_title", a), fadedItems);

        Item fadedSongItems[] = {
                new Item("soundcloud", w.getStringFromId("preset_faded_song_detail_soundcloud", a), false),
                new Item("youtube", w.getStringFromId("preset_faded_song_detail_youtube", a), false),
                new Item("spotify", w.getStringFromId("preset_faded_song_detail_spotify", a), false),
                new Item("google_play_music", w.getStringFromId("preset_faded_song_detail_google_play_music", a), false),
                new Item("apple", w.getStringFromId("preset_faded_song_detail_apple", a), false),
                new Item("amazon", w.getStringFromId("preset_faded_song_detail_amazon", a), false),
                new Item("pandora", w.getStringFromId("preset_faded_song_detail_pandora", a), false)
        };

        Detail fadedSongDetail = new Detail(w.getStringFromId("preset_faded_song_detail_title", a), fadedSongItems);

        Bio fadedBio = new Bio(
                w.getStringFromId("preset_faded_bio_title", a),
                "about_bio_faded",
                w.getStringFromId("preset_faded_bio_name", a),
                w.getStringFromId("preset_faded_bio_text", a),
                w.getStringFromId("preset_faded_bio_source", a)
        );

        Detail fadedDetails[] = {
                fadedDetail,
                fadedSongDetail
        };

        About fadedAbout = new About(
                w.getStringFromId("preset_faded_title", a),
                "about_album_faded",
                w.getStringFromId("preset_faded_tutorial_link", a),
                "Studio Berict",
                "#00D3BE",
                fadedBio, fadedDetails
        );

        Music fadedMusic = new Music(
                "preset_faded",
                "alan_walker_faded",
                true,
                246,
                90,
                null
        );

        Preset fadedPreset = new Preset(2, fadedMusic, fadedAbout);

        largeLog("JSON", gson.toJson(fadedPreset));
        Bio tapadBio = new Bio(
                w.getStringFromId("info_tapad_bio_title", a),
                "about_bio_tapad",
                w.getStringFromId("info_tapad_bio_name", a),
                w.getStringFromId("info_tapad_bio_text", a),
                w.getStringFromId("info_tapad_bio_source", a)
        );

        Item tapadInfo[] = {
                new Item("info_tapad_info_check_update", w.getStringFromId("info_tapad_info_check_update_hint", a), "about_detail_google_play", true),
                new Item("info_tapad_info_tester", w.getStringFromId("info_tapad_info_tester_hint", a), "about_detail_tester", true),
                new Item("info_tapad_info_legal", null, "about_detail_info", false),
                new Item("info_tapad_info_version", w.getStringFromId("info_tapad_info_version_hint", a), ""),
                new Item("info_tapad_info_build_date", w.getStringFromId("info_tapad_info_build_date_hint", a), ""),
                new Item("info_tapad_info_changelog", null, "about_detail_changelog", false),
                new Item("info_tapad_info_thanks", null, "about_detail_thanks", false),
                new Item("info_tapad_info_dev", w.getStringFromId("info_tapad_info_dev_hint", a), "about_detail_dev", false)
                // TODO ADD ITEMS
        };

        Item tapadOthers[] = {
                new Item("info_tapad_others_song", w.getStringFromId("info_tapad_others_song_hint", a), "about_detail_others_song", true),
                new Item("info_tapad_others_feedback", w.getStringFromId("info_tapad_others_feedback_hint", a), "about_detail_others_feedback", true),
                new Item("info_tapad_others_report_bug", w.getStringFromId("info_tapad_others_report_bug_hint", a), "about_detail_others_report_bug", true),
                new Item("info_tapad_others_rate", w.getStringFromId("info_tapad_others_rate_hint", a), "about_detail_others_rate", true),
                new Item("info_tapad_others_translate", w.getStringFromId("info_tapad_others_translate_hint", a), "about_detail_web", false),
                new Item("info_tapad_others_recommend", w.getStringFromId("info_tapad_others_recommend_hint", a), "about_detail_others_recommend", true)
        };

        Detail tapadDetails[] = {
                new Detail(w.getStringFromId("info_tapad_info_title", a), tapadInfo),
                new Detail(w.getStringFromId("info_tapad_others_title", a), tapadOthers)
        };

        About tapadAbout = new About(
                w.getStringFromId("info_tapad_title", a),
                "about_image_tapad",
                "#9C27B0",
                tapadBio, tapadDetails
        );

        largeLog("tapadAboutJSON", gson.toJson(tapadAbout));

        Bio berictBio = new Bio(
                w.getStringFromId("info_berict_bio_title", a),
                null,
                w.getStringFromId("info_berict_bio_name", a),
                w.getStringFromId("info_berict_bio_text", a),
                w.getStringFromId("info_berict_bio_source", a)
        );

        Item devItems[] = {
                new Item("facebook", w.getStringFromId("info_berict_detail_facebook", a)),
                new Item("twitter", w.getStringFromId("info_berict_detail_twitter", a)),
                new Item("google_plus", w.getStringFromId("info_berict_detail_google_plus", a)),
                new Item("youtube", w.getStringFromId("info_berict_detail_youtube", a)),
                new Item("discord", w.getStringFromId("info_berict_detail_discord", a)),
                new Item("web", w.getStringFromId("info_berict_detail_web", a))
        };

        Item devSupport[] = {
                new Item("info_berict_action_report_bug", w.getStringFromId("info_berict_action_report_bug_hint", a), "about_detail_others_report_bug", true),
                new Item("info_berict_action_rate", w.getStringFromId("info_berict_action_rate_hint", a), "about_detail_others_rate", true),
                new Item("info_berict_action_translate", w.getStringFromId("info_berict_action_translate_hint", a), "about_detail_others_translate", false),
                new Item("info_berict_action_donate", w.getStringFromId("info_berict_action_donate_hint", a), "about_detail_others_donate", false)
        };

        Detail berictDetails[] = {
                new Detail(w.getStringFromId("info_berict_detail_title", a), devItems),
                new Detail(w.getStringFromId("info_berict_action_title", a), devSupport)
        };

        About berictAbout = new About(
                w.getStringFromId("info_berict_title", a),
                "about_image_berict",
                "#607D8B",
                berictBio, berictDetails
        );

        largeLog("berictAboutJSON", gson.toJson(berictAbout));
    }

    Deck[] getDeckFromFileName(String fileTag) {
        Pad part1[] = {
                getPadsFromFile(fileTag, 0, 0),
                getPadsFromFile(fileTag, 0, 1),
                getPadsFromFile(fileTag, 0, 2),
                getPadsFromFile(fileTag, 0, 3),
                getPadsFromFile(fileTag, 0, 4),
                getPadsFromFile(fileTag, 0, 5),
                getPadsFromFile(fileTag, 0, 6),
                getPadsFromFile(fileTag, 0, 7),
                getPadsFromFile(fileTag, 0, 8),
                getPadsFromFile(fileTag, 0, 9),
                getPadsFromFile(fileTag, 0, 10),
                getPadsFromFile(fileTag, 0, 11),
                getPadsFromFile(fileTag, 0, 12),
                getPadsFromFile(fileTag, 0, 13),
                getPadsFromFile(fileTag, 0, 14),
                getPadsFromFile(fileTag, 0, 15),
                getPadsFromFile(fileTag, 0, 16),
                getPadsFromFile(fileTag, 0, 17),
                getPadsFromFile(fileTag, 0, 18),
                getPadsFromFile(fileTag, 0, 19),
                getPadsFromFile(fileTag, 0, 20)
        };
        Pad part2[] = {
                getPadsFromFile(fileTag, 1, 0),
                getPadsFromFile(fileTag, 1, 1),
                getPadsFromFile(fileTag, 1, 2),
                getPadsFromFile(fileTag, 1, 3),
                getPadsFromFile(fileTag, 1, 4),
                getPadsFromFile(fileTag, 1, 5),
                getPadsFromFile(fileTag, 1, 6),
                getPadsFromFile(fileTag, 1, 7),
                getPadsFromFile(fileTag, 1, 8),
                getPadsFromFile(fileTag, 1, 9),
                getPadsFromFile(fileTag, 1, 10),
                getPadsFromFile(fileTag, 1, 11),
                getPadsFromFile(fileTag, 1, 12),
                getPadsFromFile(fileTag, 1, 13),
                getPadsFromFile(fileTag, 1, 14),
                getPadsFromFile(fileTag, 1, 15),
                getPadsFromFile(fileTag, 1, 16),
                getPadsFromFile(fileTag, 1, 17),
                getPadsFromFile(fileTag, 1, 18),
                getPadsFromFile(fileTag, 1, 19),
                getPadsFromFile(fileTag, 1, 20)
        };
        Pad part3[] = {
                getPadsFromFile(fileTag, 2, 0),
                getPadsFromFile(fileTag, 2, 1),
                getPadsFromFile(fileTag, 2, 2),
                getPadsFromFile(fileTag, 2, 3),
                getPadsFromFile(fileTag, 2, 4),
                getPadsFromFile(fileTag, 2, 5),
                getPadsFromFile(fileTag, 2, 6),
                getPadsFromFile(fileTag, 2, 7),
                getPadsFromFile(fileTag, 2, 8),
                getPadsFromFile(fileTag, 2, 9),
                getPadsFromFile(fileTag, 2, 10),
                getPadsFromFile(fileTag, 2, 11),
                getPadsFromFile(fileTag, 2, 12),
                getPadsFromFile(fileTag, 2, 13),
                getPadsFromFile(fileTag, 2, 14),
                getPadsFromFile(fileTag, 2, 15),
                getPadsFromFile(fileTag, 2, 16),
                getPadsFromFile(fileTag, 2, 17),
                getPadsFromFile(fileTag, 2, 18),
                getPadsFromFile(fileTag, 2, 19),
                getPadsFromFile(fileTag, 2, 20)
        };
        Pad part4[] = {
                getPadsFromFile(fileTag, 3, 0),
                getPadsFromFile(fileTag, 3, 1),
                getPadsFromFile(fileTag, 3, 2),
                getPadsFromFile(fileTag, 3, 3),
                getPadsFromFile(fileTag, 3, 4),
                getPadsFromFile(fileTag, 3, 5),
                getPadsFromFile(fileTag, 3, 6),
                getPadsFromFile(fileTag, 3, 7),
                getPadsFromFile(fileTag, 3, 8),
                getPadsFromFile(fileTag, 3, 9),
                getPadsFromFile(fileTag, 3, 10),
                getPadsFromFile(fileTag, 3, 11),
                getPadsFromFile(fileTag, 3, 12),
                getPadsFromFile(fileTag, 3, 13),
                getPadsFromFile(fileTag, 3, 14),
                getPadsFromFile(fileTag, 3, 15),
                getPadsFromFile(fileTag, 3, 16),
                getPadsFromFile(fileTag, 3, 17),
                getPadsFromFile(fileTag, 3, 18),
                getPadsFromFile(fileTag, 3, 19),
                getPadsFromFile(fileTag, 3, 20)
        };

        return new Deck[]{new Deck(part1), new Deck(part2), new Deck(part3), new Deck(part4)};
    }

    String getPadStringFromId(int padId) {
        switch (padId) {
            case 0:
                return "00";
            case 1:
                return "01";
            case 2:
                return "02";
            case 3:
                return "03";
            case 4:
                return "04";
            case 5:
                return "11";
            case 6:
                return "12";
            case 7:
                return "13";
            case 8:
                return "14";
            case 9:
                return "21";
            case 10:
                return "22";
            case 11:
                return "23";
            case 12:
                return "24";
            case 13:
                return "31";
            case 14:
                return "32";
            case 15:
                return "33";
            case 16:
                return "34";
            case 17:
                return "41";
            case 18:
                return "42";
            case 19:
                return "43";
            case 20:
                return "44";
            default:
                return null;
        }
    }

    Pad getPadsFromFile(String fileTag, int deck, int pad) {
//        if (validateFileName(
//                fileTag,
//                Integer.toString(deck + 1),
//                getPadStringFromId(pad),
//                Integer.toString(0)
//        ) == null) {
//            // the pad is empty from the first gesture == empty
//            return new Pad("a0_00");
//        } else {
            String fileNameArray[] = {
                    validateFileName(
                            fileTag,
                            Integer.toString(deck + 1),
                            getPadStringFromId(pad),
                            Integer.toString(0)
                    ),
                    validateFileName(
                            fileTag,
                            Integer.toString(deck + 1),
                            getPadStringFromId(pad),
                            Integer.toString(1)
                    ),
                    validateFileName(
                            fileTag,
                            Integer.toString(deck + 1),
                            getPadStringFromId(pad),
                            Integer.toString(2)
                    ),
                    validateFileName(
                            fileTag,
                            Integer.toString(deck + 1),
                            getPadStringFromId(pad),
                            Integer.toString(3)
                    ),
                    validateFileName(
                            fileTag,
                            Integer.toString(deck + 1),
                            getPadStringFromId(pad),
                            Integer.toString(4)
                    )
            };
            return getPadFromStringArray(fileNameArray);
//        }
    }

    Pad getPadFromStringArray(String fileName[]) {
        ArrayList<String> stringArray = new ArrayList<>();
        for (int i = 0; i < fileName.length; i++) {
            if (fileName[i] != null) {
                stringArray.add(fileName[i]);
            } else if (i == 0) {
                stringArray.add("a_null");
            }
        }

        String padStringArray[] = stringArray.toArray(new String[stringArray.size()]);

        switch (padStringArray.length) {
            case 1:
                return new Pad(padStringArray[0]);
            case 2:
                return new Pad(padStringArray[0], padStringArray[1]);
            case 3:
                return new Pad(padStringArray[0], padStringArray[1], padStringArray[2]);
            case 4:
                return new Pad(padStringArray[0], padStringArray[1], padStringArray[2], padStringArray[3]);
            case 5:
                return new Pad(padStringArray[0], padStringArray[1], padStringArray[2], padStringArray[3], padStringArray[4]);
            default:
                Log.d(TAG, "getPadFromStringArray : null array");
                return null;
        }
    }

    String validateFileName(String fileTag, String realPart, String realPad, String realGesture) {
        String fileName;
        if (realGesture.equals("0")) {
            fileName = fileTag + realPart + "_" + realPad;
        } else {
            fileName = fileTag + realPart + "_" + realPad + "_" + realGesture;
        }
        try {
            Class res = R.raw.class;
            Field field = res.getField(fileName);
            // legit
            if (field != null) {
                return fileName;
            } else {
                return null;
            }
        } catch (Exception e) {
            Log.e("getColorId", "Failure to get raw id.", e);
            // fail
            return null;
        }
    }
}