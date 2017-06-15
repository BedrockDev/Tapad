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
import com.bedrock.padder.helper.AdmobHelper;
import com.bedrock.padder.helper.AnimateHelper;
import com.bedrock.padder.helper.FabHelper;
import com.bedrock.padder.helper.FileHelper;
import com.bedrock.padder.helper.IntentHelper;
import com.bedrock.padder.helper.SoundHelper;
import com.bedrock.padder.helper.ToolbarHelper;
import com.bedrock.padder.helper.TutorialHelper;
import com.bedrock.padder.helper.WindowHelper;
import com.bedrock.padder.model.FirebaseMetadata;
import com.bedrock.padder.model.about.About;
import com.bedrock.padder.model.about.Bio;
import com.bedrock.padder.model.about.Detail;
import com.bedrock.padder.model.about.Item;
import com.bedrock.padder.model.app.theme.ColorData;
import com.bedrock.padder.model.preset.Music;
import com.bedrock.padder.model.preset.Preset;
import com.google.gson.Gson;

import java.io.File;

import uk.co.samuelwall.materialtaptargetprompt.MaterialTapTargetPrompt;

import static com.bedrock.padder.helper.FirebaseHelper.PROJECT_LOCATION_PRESETS;
import static com.bedrock.padder.helper.WindowHelper.APPLICATION_ID;

@TargetApi(14)
@SuppressWarnings("deprecation")

public class MainActivity
        extends AppCompatActivity
        implements AboutFragment.OnFragmentInteractionListener, SettingsFragment.OnFragmentInteractionListener {

    public static final String TAG = "MainActivity";
    public static final String PRESET_KEY = "savedPreset";
    public static boolean isPresetLoading = false;
    public static boolean isPresetVisible = false;
    public static boolean isPresetChanged = false;
    public static boolean isTutorialVisible = false;
    public static boolean isAboutVisible = false;
    public static boolean isSettingVisible = false;
    public static boolean isDeckShouldCleared = false;
    public static Preset preset;
    public static Preset currentPreset = null;
    // Used for circularReveal
    // End two is for settings coordination
    public static int coord[] = {0, 0, 0, 0};
    final AppCompatActivity a = this;
    final String qs = "quickstart";
    public boolean tgl1 = false;
    public boolean tgl2 = false;
    public boolean tgl3 = false;
    public boolean tgl4 = false;
    public boolean tgl5 = false;
    public boolean tgl6 = false;
    public boolean tgl7 = false;
    public boolean tgl8 = false;
    int currentVersionCode;
    int themeColor = R.color.hello;
    int color = R.color.cyan_400;
    int toggleSoundId = 0;
    int togglePatternId = 0;
    private SharedPreferences prefs = null;
    private AnimateHelper anim = new AnimateHelper();
    private SoundHelper sound = new SoundHelper();
    private WindowHelper w = new WindowHelper();
    private FabHelper fab = new FabHelper();
    private ToolbarHelper toolbar = new ToolbarHelper();
    private TutorialHelper tut = new TutorialHelper();
    private IntentHelper intent = new IntentHelper();
    private AdmobHelper ad = new AdmobHelper();
    private FileHelper file = new FileHelper();
    private boolean doubleBackToExitPressedOnce = false;
    private boolean isToolbarVisible = false;
    private boolean isSettingsFromMenu = false;
    private int circularRevealDuration = 400;
    private int fadeAnimDuration = 200;
//    private MaterialTapTargetPrompt promptToggle;   // 1
//    private MaterialTapTargetPrompt promptButton;   // 2
//    private MaterialTapTargetPrompt promptSwipe;    // 3
//    private MaterialTapTargetPrompt promptLoop;     // 4
//    private MaterialTapTargetPrompt promptPattern;  // 5
//    private MaterialTapTargetPrompt promptFab;      // 6
//    private MaterialTapTargetPrompt promptPreset;   // 7
//    private MaterialTapTargetPrompt promptInfo;     // 8
//    private MaterialTapTargetPrompt promptTutorial; // 9

    private Gson gson = new Gson();

    // TODO TAP launch
    //IabHelper mHelper;
    //IabBroadcastReceiver mBroadcastReceiver;

    public static void largeLog(String tag, String content) {
        if (content.length() > 4000) {
            Log.d(tag, content.substring(0, 4000));
            largeLog(tag, content.substring(4000));
        } else {
            Log.d(tag, content);
        }
    }

    public static void showSettingsFragment(AppCompatActivity a) {
        a.getSupportFragmentManager()
                .beginTransaction()
                .add(R.id.fragment_settings_container, new SettingsFragment())
                .commit();
        WindowHelper w = new WindowHelper();
        w.getView(R.id.fragment_settings_container, a).setVisibility(View.VISIBLE);
        setSettingVisible(true);
        w.setRecentColor(R.string.settings, 0, R.color.colorAccent, a);
    }

    public static void setSettingVisible(boolean isVisible) {
        isSettingVisible = isVisible;
        Log.d("SettingVisible", String.valueOf(isSettingVisible));
    }

    public static void setAboutVisible(boolean isVisible) {
        isAboutVisible = isVisible;
        Log.d("AboutVisible", String.valueOf(isAboutVisible));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        makeJson();

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

        // sharedPrefs
        Log.d(TAG, "Sharedprefs initialized");
        prefs = this.getSharedPreferences(APPLICATION_ID, MODE_PRIVATE);

        if (getSavedPreset() != null) {
            try {
                currentPreset = gson.fromJson(file.getStringFromFile(getCurrentPresetLocation() + "/about/json"), Preset.class);
            } catch (Exception e) {
                // corrupted preset
                e.printStackTrace();
                currentPreset = null;
            }
        }

        if (!file.isPresetAvailable(currentPreset)) {
            // preset corrupted or doesn't exist
            currentPreset = null;
        }

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
        setQuickstart(a);
        setFab();
        setToolbar();
        setPresetInfo();
        setToggleButton(R.color.colorAccent);
        enterAnim();
        loadPreset(400);
        setButtonLayout();

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
                intent.intent(a, "activity.HelpActivity");
                break;
        }

        return super.onOptionsItemSelected(item);
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
            w.setInvisible(tutorial[i], 10, a);
        }

        color = prefs.getInt("color", R.color.cyan_400);

        if (isPresetVisible) {
            if (!isAboutVisible && !isSettingVisible) {
                // preset store visible
                closePresetStore();
            }
            isPresetVisible = false;
        }


        if (isPresetChanged) {
            currentPreset = null;
            if (getSavedPreset() != null) {
                // preset loaded
                Log.d(TAG, "changed");
                currentPreset = gson.fromJson(file.getStringFromFile(getCurrentPresetLocation() + "/about/json"), Preset.class);
                loadPreset(0);
            } else {
                Log.d(TAG, "removed");
            }
            setPresetInfo();
            isPresetChanged = false;
        }

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
            w.setInvisible(tutorial[i], 10, a);
        }

        ad.resumeNativeAdView(R.id.adView_main, a);
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
        WindowHelper w = new WindowHelper();
        w.getView(R.id.fragment_about_container, a).setVisibility(View.VISIBLE);
        setAboutVisible(true);
        w.setRecentColor(R.string.about, 0, themeColor, a);
    }

    private void enterAnim() {
        anim.fadeIn(R.id.actionbar_layout, 0, 200, "background", a);
        anim.fadeIn(R.id.actionbar_image, 200, 200, "image", a);
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

    private void setQuickstart(final Activity activity) {
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
        fab.setFabIcon(R.drawable.ic_info_white, a);
        fab.showFab();
        fab.setFabOnClickListener(new Runnable() {
            @Override
            public void run() {
                if (isToolbarVisible == false) {
                    fab.hideFab(0, 200);
                    anim.fadeIn(R.id.toolbar, 200, 100, "toolbarIn", a);
//                    if (prefs.getInt(qs, 0) == 7) {
//                        Log.i("setQuickstart", "Quickstart started");
//                        if (promptInfo != null) {
//                            return;
//                        }
//                        promptInfo = new MaterialTapTargetPrompt.Builder(a)
//                                .setTarget(a.findViewById(R.id.toolbar_info))
//                                .setPrimaryText(R.string.dialog_tap_target_info_primary)
//                                .setSecondaryText(R.string.dialog_tap_target_info_secondary)
//                                .setAnimationInterpolator(new FastOutSlowInInterpolator())
//                                .setFocalColourFromRes(R.color.blue_500)
//                                .setAutoDismiss(false)
//                                .setAutoFinish(false)
//                                .setCaptureTouchEventOutsidePrompt(true)
//                                .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
//                                    @Override
//                                    public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
//                                        if (tappedTarget) {
//                                            promptInfo.finish();
//                                            promptInfo = null;
//                                            prefs.edit().putInt(qs, 8).apply();
//                                            Log.i("sharedPrefs", "quickstart edited to 8");
//                                        }
//                                    }
//
//                                    @Override
//                                    public void onHidePromptComplete() {
//                                    }
//                                })
//                                .show();
//                    }
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

                    intent.intent(a, "activity.PresetStoreActivity", circularRevealDuration);
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
                showTutorial();
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
        fab.showFab(100, 200);
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
                setPresetInfo();
                w.getView(R.id.fragment_about_container, a).setVisibility(View.GONE);
            }
        }, fadeAnimDuration);

        // Firstrun tutorial
//        if (prefs.getInt(qs, 0) == 8) {
//            promptPreset = new MaterialTapTargetPrompt.Builder(a)
//                    .setTarget(a.findViewById(R.id.toolbar_preset))
//                    .setPrimaryText(R.string.dialog_tap_target_preset_primary)
//                    .setSecondaryText(R.string.dialog_tap_target_preset_secondary)
//                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
//                    .setFocalColourFromRes(R.color.blue_500)
//                    .setAutoDismiss(false)
//                    .setAutoFinish(false)
//                    .setCaptureTouchEventOutsidePrompt(true)
//                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
//                        @Override
//                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
//                            if (tappedTarget) {
//                                promptPreset.finish();
//                                promptPreset = null;
//                                prefs.edit().putInt(qs, 9).apply();
//                                Log.i("sharedPrefs", "quickstart edited to 9");
//                            }
//                        }
//
//                        @Override
//                        public void onHidePromptComplete() {
//                            // idk why is this here
//                            //isTutorialVisible = false;
//                        }
//                    })
//                    .show();
//        }

        // reset the touch coords
        coord[0] = 0;
        coord[1] = 0;
    }

    private void showTutorial() {
        if (isTutorialVisible == false) {
            isTutorialVisible = true;
            if (currentPreset != null) {
                if (!isPresetLoading) {
                    String tutorialText = currentPreset.getAbout().getTutorialLink();
                    if (tutorialText == null || tutorialText.equals("null")) {
                        tutorialText = w.getStringFromId("dialog_tutorial_text_error", a);
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
            } else {
                // no preset
                Toast.makeText(a, R.string.tutorial_no_preset, Toast.LENGTH_LONG).show();
                isTutorialVisible = false;
            }
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
                    setPresetInfo();
                }
                w.getView(R.id.fragment_settings_container, a).setVisibility(View.GONE);
            }
        }, fadeAnimDuration);

        // reset the touch coords
        coord[2] = 0;
        coord[3] = 0;
    }

    private void closePresetStore() {
        setPresetInfo();

        if (coord[0] > 0 && coord[1] > 0) {
            anim.circularRevealInPx(R.id.placeholder,
                    coord[0], coord[1],
                    (int) Math.hypot(coord[0], coord[1]) + 200, 0, new AccelerateDecelerateInterpolator(),
                    circularRevealDuration, 200, a);
        }

//        if (prefs.getInt(qs, 0) == 7) {
//            promptTutorial = new MaterialTapTargetPrompt.Builder(a)
//                    .setTarget(a.findViewById(R.id.toolbar_tutorial))
//                    .setPrimaryText(R.string.dialog_tap_target_tutorial_primary)
//                    .setSecondaryText(R.string.dialog_tap_target_tutorial_secondary)
//                    .setAnimationInterpolator(new FastOutSlowInInterpolator())
//                    .setFocalColourFromRes(R.color.blue_500)
//                    .setAutoDismiss(false)
//                    .setAutoFinish(false)
//                    .setCaptureTouchEventOutsidePrompt(true)
//                    .setOnHidePromptListener(new MaterialTapTargetPrompt.OnHidePromptListener() {
//                        @Override
//                        public void onHidePrompt(MotionEvent event, boolean tappedTarget) {
//                            if (tappedTarget) {
//                                promptTutorial.finish();
//                                promptTutorial = null;
//                                prefs.edit().putInt(qs, -1).apply();
//                                Log.i("sharedPrefs", "quickstart edited to -1, completed");
//                            }
//                        }
//
//                        @Override
//                        public void onHidePromptComplete() {
//                        }
//                    })
//                    .show();
//        }
    }

    private void loadPreset(int delay) {
        if (currentPreset != null) {
            Handler preset = new Handler();
            preset.postDelayed(new Runnable() {
                @Override
                public void run() {
                    sound.loadSound(currentPreset, a);
                }
            }, delay);
        }
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

    private void setPresetInfo() {
        if (isSettingVisible == false && isAboutVisible == false && currentPreset != null) {
            themeColor = currentPreset.getAbout().getActionbarColor();
            toolbar.setActionBarTitle(0);
            toolbar.setActionBarColor(themeColor, a);
            toolbar.setActionBarPadding(a);
            toolbar.setActionBarImage(
                    PROJECT_LOCATION_PRESETS + "/" + currentPreset.getFirebaseLocation() + "/about/artist_icon",
                    this);
            w.setRecentColor(0, 0, themeColor, a);
            w.setVisible(R.id.base, 0, a);
            w.setGone(R.id.main_cardview_preset_store, 0, a);
        } else if (currentPreset == null || getSavedPreset() == null) {
            toolbar.setActionBarTitle(R.string.app_name);
            toolbar.setActionBarColor(R.color.colorPrimary, a);
            toolbar.setActionBarPadding(a);
            toolbar.setActionBarImage(0, this);
            w.setRecentColor(0, 0, R.color.colorPrimary, a);
            w.getView(R.id.main_cardview_preset_store, a).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.intent(a, "activity.PresetStoreActivity");
                }
            });
            w.getView(R.id.main_cardview_preset_store_download, a).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent.intent(a, "activity.PresetStoreActivity");
                }
            });
            w.setVisible(R.id.main_cardview_preset_store, 0, a);
            w.setGone(R.id.base, 0, a);
        }
    }

    public String getCurrentPresetLocation() {
        if (getSavedPreset() != null) {
            return PROJECT_LOCATION_PRESETS + "/" + getSavedPreset();
        } else {
            return null;
        }
    }

    public String getSavedPreset() {
        return prefs.getString(PRESET_KEY, null);
    }

    public void setSavedPreset(String savedPreset) {
        prefs.edit().putString(PRESET_KEY, savedPreset).apply();
    }

    private String getAvailableDownloadedPreset() {
        File directory = new File(PROJECT_LOCATION_PRESETS);
        File[] files = directory.listFiles();
        String presetName = null;
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    presetName = file.getName();
                    if (isPresetExists(presetName)) {
                        if (isPresetAvailable(presetName)) {
                            // available preset
                            break;
                        }
                    }
                }
            }
        }
        return presetName;
    }

    private boolean isPresetExists(String presetName) {
        // preset exist
        File folder = new File(PROJECT_LOCATION_PRESETS + "/" + presetName); // folder check
        return folder.isDirectory() && folder.exists();
    }

    private boolean isPresetAvailable(String presetName) {
        // preset available
        File folderSound = new File(PROJECT_LOCATION_PRESETS + "/" + presetName + "/sounds");
        File folderTiming = new File(PROJECT_LOCATION_PRESETS + "/" + presetName + "/timing");
        File folderAbout = new File(PROJECT_LOCATION_PRESETS + "/" + presetName + "/about");
        File fileJson = new File(PROJECT_LOCATION_PRESETS + "/" + presetName + "/about/json");
        return folderSound.isDirectory() && folderSound.exists() &&
                folderTiming.isDirectory() && folderTiming.exists() &&
                folderAbout.isDirectory() && folderAbout.exists() &&
                fileJson.exists();
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
                "alan_walker_faded_gesture",
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
                "alan_walker_faded_gesture",
                w.getStringFromId("preset_faded_tutorial_link", a),
                "Studio Berict",
                "#00D3BE",
                fadedBio, fadedDetails
        );

        Music fadedMusic = new Music(
                "preset_faded",
                "alan_walker_faded_gesture",
                true,
                246,
                90,
                null
        );

        Preset fadedPreset = new Preset("alan_walker_faded_gesture", fadedMusic, fadedAbout);

        largeLog("JSON", gson.toJson(fadedPreset));

        Preset[] presets = {
                fadedPreset
        };

        FirebaseMetadata firebaseMetadata = new FirebaseMetadata(presets, 15);
        largeLog("Metadata", gson.toJson(firebaseMetadata));

//        Bio tapadBio = new Bio(
//                w.getStringFromId("info_tapad_bio_title", a),
//                "about_bio_tapad",
//                w.getStringFromId("info_tapad_bio_name", a),
//                w.getStringFromId("info_tapad_bio_text", a),
//                w.getStringFromId("info_tapad_bio_source", a)
//        );
//
//        Item tapadInfo[] = {
//                new Item("info_tapad_info_check_update", w.getStringFromId("info_tapad_info_check_update_hint", a), "google_play", true),
//                new Item("info_tapad_info_tester", w.getStringFromId("info_tapad_info_tester_hint", a), "experiment", true),
//                new Item("info_tapad_info_legal", null, "info", false),
//                new Item("info_tapad_info_version", w.getStringFromId("info_tapad_info_version_hint", a), ""),
//                new Item("info_tapad_info_build_date", w.getStringFromId("info_tapad_info_build_date_hint", a), ""),
//                new Item("info_tapad_info_changelog", null, "changelog", false),
//                new Item("info_tapad_info_thanks", null, "thanks", false),
//                new Item("info_tapad_info_dev", w.getStringFromId("info_tapad_info_dev_hint", a), "developer", false)
//                // TODO ADD ITEMS
//        };
//
//        Item tapadOthers[] = {
//                new Item("info_tapad_others_song", w.getStringFromId("info_tapad_others_song_hint", a), "song", true),
//                new Item("info_tapad_others_feedback", w.getStringFromId("info_tapad_others_feedback_hint", a), "feedback", true),
//                new Item("info_tapad_others_report_bug", w.getStringFromId("info_tapad_others_report_bug_hint", a), "report_bug", true),
//                new Item("info_tapad_others_rate", w.getStringFromId("info_tapad_others_rate_hint", a), "rate", true),
//                new Item("info_tapad_others_translate", w.getStringFromId("info_tapad_others_translate_hint", a), "web", false),
//                new Item("info_tapad_others_recommend", w.getStringFromId("info_tapad_others_recommend_hint", a), "recommend", true)
//        };
//
//        Detail tapadDetails[] = {
//                new Detail(w.getStringFromId("info_tapad_info_title", a), tapadInfo),
//                new Detail(w.getStringFromId("info_tapad_others_title", a), tapadOthers)
//        };
//
//        About tapadAbout = new About(
//                w.getStringFromId("info_tapad_title", a),
//                "about_image_tapad",
//                "#9C27B0",
//                tapadBio, tapadDetails
//        );
//
//        largeLog("tapadAboutJSON", gson.toJson(tapadAbout));
//
//        Bio berictBio = new Bio(
//                w.getStringFromId("info_berict_bio_title", a),
//                null,
//                w.getStringFromId("info_berict_bio_name", a),
//                w.getStringFromId("info_berict_bio_text", a),
//                w.getStringFromId("info_berict_bio_source", a)
//        );
//
//        Item devItems[] = {
//                new Item("facebook", w.getStringFromId("info_berict_detail_facebook", a)),
//                new Item("twitter", w.getStringFromId("info_berict_detail_twitter", a)),
//                new Item("google_plus", w.getStringFromId("info_berict_detail_google_plus", a)),
//                new Item("youtube", w.getStringFromId("info_berict_detail_youtube", a)),
//                new Item("discord", w.getStringFromId("info_berict_detail_discord", a)),
//                new Item("web", w.getStringFromId("info_berict_detail_web", a))
//        };
//
//        Item devSupport[] = {
//                new Item("info_berict_action_report_bug", w.getStringFromId("info_berict_action_report_bug_hint", a), "report_bug", true),
//                new Item("info_berict_action_rate", w.getStringFromId("info_berict_action_rate_hint", a), "rate", true),
//                new Item("info_berict_action_translate", w.getStringFromId("info_berict_action_translate_hint", a), "translate", false),
//                new Item("info_berict_action_donate", w.getStringFromId("info_berict_action_donate_hint", a), "donate", false)
//        };
//
//        Detail berictDetails[] = {
//                new Detail(w.getStringFromId("info_berict_detail_title", a), devItems),
//                new Detail(w.getStringFromId("info_berict_action_title", a), devSupport)
//        };
//
//        About berictAbout = new About(
//                w.getStringFromId("info_berict_title", a),
//                "about_image_berict",
//                "#607D8B",
//                berictBio, berictDetails
//        );
//
//        largeLog("berictAboutJSON", gson.toJson(berictAbout));
    }
}