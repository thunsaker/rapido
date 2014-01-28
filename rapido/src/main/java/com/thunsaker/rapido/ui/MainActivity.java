package com.thunsaker.rapido.ui;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.ActionBar;
import android.support.v7.app.ActionBarActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

import com.crashlytics.android.Crashlytics;
import com.facebook.AccessToken;
import com.facebook.Session;
import com.facebook.SessionState;
import com.facebook.Settings;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.plus.PlusClient;
import com.google.gson.Gson;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.classes.Draft;
import com.thunsaker.rapido.classes.PickedLocation;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.util.PreferencesHelper;
import com.thunsaker.rapido.util.Util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

public class MainActivity extends ActionBarActivity {
    private boolean useLogo = true;
    private boolean showHomeUp = false;

    private TextView mTextView;
    private EditText mEditText;
    private ToggleButton mToggleButtonFacebook;
    private ToggleButton mToggleButtonTwitter;
    private ToggleButton mToggleButtonFoursquare;
    private ToggleButton mToggleButtonGooglePlus;
    private ToggleButton mToggleButtonAppNet;
    //	private Button mButtonSend;
    private RelativeLayout mRelativeLayoutLocation;
    private ImageView mImageViewLocationIcon;
    private TextView mTextViewLocationName;
    private TextView mTextViewLocationDetails;

    private Boolean facebookEnabled = false;
    private Boolean facebookConnected = false;
    private Boolean twitterEnabled = false;
    private Boolean twitterConnected = false;
    private Boolean foursquareEnabled = false;
    private Boolean foursquareConnected = false;
    private Boolean locationEnabled = false;
    private Boolean googlePlusEnabled = false;
    private Boolean googlePlusConnected = false;
    private Boolean appDotNetEnabled = false;
    private Boolean appDotNetConnected = false;

    private Boolean submitOnEnter = false;

    private String FACEBOOK_KEY = "";
    private String TWITTER_TOKEN = "";
    private String TWITTER_TOKEN_SECRET = "";
    private String FOURSQUARE_TOKEN = "";
    private String APP_DOT_NET_TOKEN = "";

    public static String STORAGE_FILENAME = "RAPIDO_STORAGE";

    public static String REPOST_FACEBOOK = "RAPIDO_REPOST_FACEBOOK";
    public static String REPOST_TWITTER = "RAPIDO_REPOST_TWITTER";
    public static String REPOST_ALL = "RAPIDO_REPOST_ALL";
    public static String REPOST_FOURSQUARE = "RAPIDO_REPOST_FOURSQUARE";
    public static String REPOST_GOOGLE_PLUS = "RAPIDO_REPOST_GOOGLE_PLUS";
    public static String REPOST_APP_DOT_NET = "RAPIDO_REPOST_APP_DOT_NET";
    private String REPOST_START_TEXT = "RAPIDO_REPOST";

    public static String DRAFT_TWITTER = "RAPIDO_DRAFT_TWITTER";
    public static String DRAFT_FACEBOOK = "RAPIDO_DRAFT_FACEBOOK";
    public static String DRAFT_FOURSQUARE = "RAPIDO_DRAFT_FOURSQUARE";
    public static String DRAFT_GOOGLE_PLUS = "RAPIDO_DRAFT_FOURSQUARE";
    public static String DRAFT_GENERIC = "RAPIDO_DRAFT_GENERIC";

    static final int DIALOG_NO_INTERNETS_ID = 0;

    public static ProgressDialog loadingDialog;
    public static ProgressDialog sendingDialog;

    public static final int RAPIDO_NOTIFICATION = 0;
    public static final int FACEBOOK_NOTIFICATION = 1;
    public static final int TWITTER_NOTIFICATION = 2;
    public static final int BITLY_NOTIFICATION = 3;
    public static final int FOURSQUARE_NOTIFICATION = 4;
    public static final int GOOGLE_PLUS_NOTIFICATION = 5;
    public static final int RAPIDO_NOTIFICATION_PERSISTENT = 6;
    public static final int APP_DOT_NET_NOTIFICATION = 7;

    public static Intent genericIntent;
    public static PendingIntent genericPendingIntent;

    public static final int PICK_LOCATION_REQUEST = 0;
    public static PickedLocation CurrentPickedLocation;
    public static final int REQUEST_FOURSQUARE_AUTH = 1;
    public static final int REQUEST_FOURSQUARE_AUTH_TOKEN = 2;
    private static final int FACEBOOK_LOGIN_RESULT = 3;

    public static PlusClient myPlusClient = null;

    NotificationCompat.Builder mNotificationRapidoPersistent;
    NotificationManager mNotificationManager;

    public Session facebookSession = null;
    private Session.StatusCallback statusCallback = new SessionStatusCallback();
    public final int TWITTER_SHORT_URL_LENGTH = 23;
    public final int BITLY_SHORT_URL_LENGTH = 20;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Crashlytics.start(this);
        setContentView(R.layout.activity_main);
        final ActionBar ab = getSupportActionBar();
        ab.setDisplayHomeAsUpEnabled(showHomeUp);
        ab.setDisplayUseLogoEnabled(useLogo);

        // Get Prefs
        facebookEnabled = PreferencesHelper.getFacebookEnabled(getApplicationContext());
        twitterEnabled = PreferencesHelper.getTwitterEnabled(getApplicationContext());
        foursquareEnabled = PreferencesHelper.getFoursquareEnabled(getApplicationContext());
        googlePlusEnabled = PreferencesHelper.getGooglePlusEnabled(getApplicationContext());
        appDotNetEnabled = PreferencesHelper.getAppDotNetEnabled(getApplicationContext());

        facebookConnected = PreferencesHelper.getFacebookConnected(getApplicationContext());
        twitterConnected = PreferencesHelper.getTwitterConnected(getApplicationContext());
        foursquareConnected = PreferencesHelper.getFoursquareConnected(getApplicationContext());
        googlePlusConnected = PreferencesHelper.getGooglePlusConnected(getApplicationContext());
        appDotNetConnected = PreferencesHelper.getAppDotNetConnected(getApplicationContext());

        submitOnEnter = PreferencesHelper.getSendOnEnterEnabled(getApplicationContext());

        // Facebook Stuff
        Settings.publishInstallAsync(getApplicationContext(), getString(R.string.facebook_app_id));

        if(facebookConnected) {
            // Import Existing Token
            if(facebookSession == null) {
                facebookSession = new Session(getApplicationContext());
                // Get Keys
                FACEBOOK_KEY = PreferencesHelper.getFacebookKey(getApplicationContext());
                if (FACEBOOK_KEY != null && FACEBOOK_KEY != "") {
                    AccessToken facebookAccessToken =
                            AccessToken.createFromExistingAccessToken(FACEBOOK_KEY, null, null, null, null);
                    if(facebookAccessToken != null) {
                        PreferencesHelper.setFacebookConnected(getApplicationContext(), true);
                        PreferencesHelper.setFacebookKey(getApplicationContext(), facebookAccessToken.getToken());
                        PreferencesHelper.setFacebookEnabled(getApplicationContext(), true);
                    }
                    Session.setActiveSession(facebookSession);
                }
            }

            facebookSession = Session.getActiveSession();
            if (facebookSession == null) {
                if (savedInstanceState != null) {
                    facebookSession = Session.restoreSession(this, null, statusCallback, savedInstanceState);
                }
                if (facebookSession == null) {
                    facebookSession = new Session(this);
                }
                Session.setActiveSession(facebookSession);
                if (facebookSession.getState().equals(SessionState.CREATED_TOKEN_LOADED)) {
                    facebookSession.openForRead(new Session.OpenRequest(this).setCallback(statusCallback));
                }
            }
        }

        TWITTER_TOKEN = PreferencesHelper.getTwitterToken(getApplicationContext());
        TWITTER_TOKEN_SECRET = PreferencesHelper.getTwitterSecret(getApplicationContext());

        FOURSQUARE_TOKEN = PreferencesHelper.getFoursquareToken(getApplicationContext());
        APP_DOT_NET_TOKEN = PreferencesHelper.getAppDotNetToken(getApplicationContext());

        mTextView = (TextView) findViewById(R.id.TextViewCount);
        mEditText = (EditText) findViewById(R.id.EditTextUpdate);
        mEditText.addTextChangedListener(mTextEditorWatcher);

        SetupToggleButtons();

        mImageViewLocationIcon = (ImageView)findViewById(R.id.imageViewLocationIcon);
        mTextViewLocationName = (TextView)findViewById(R.id.textViewLocationName);
        mTextViewLocationDetails = (TextView)findViewById(R.id.textViewLocationDetails);
        mRelativeLayoutLocation = (RelativeLayout)findViewById(R.id.relativeLayoutLocationWrapper);
        mRelativeLayoutLocation.setOnClickListener(mRelativeLayoutLocationClickListener);
        mRelativeLayoutLocation.setOnLongClickListener(mRelativeLayoutLocationLongClickListener);

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            String action = intentReceived.getAction();
            if (Intent.ACTION_SEND.equals(action)) {
                handleRecievedText(intentReceived);
            }
        }

        genericIntent = new Intent(getApplicationContext(), MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.from(getApplicationContext());
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(genericIntent);
        genericPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        SetupPersistentNotification();

        AdView adView = (AdView)this.findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder()
	        .addTestDevice("D17425E1A2C3EB1DE2D8E56DEE780F50")
	        .build();
        adView.loadAd(adRequest);
    }

    private void SetupPersistentNotification() {
        mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        mNotificationRapidoPersistent =
                new NotificationCompat.Builder(getApplicationContext())
                        .setSmallIcon(R.drawable.ic_stat_rapido)
                        .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_launcher))
                        .setContentTitle(getString(R.string.alert_persistent_notification_title))
                        .setContentText(getString(R.string.alert_persistent_notification))
                        .setContentIntent(genericPendingIntent)
                        .setAutoCancel(true)
                        .setOngoing(true);

        if(PreferencesHelper.getPersistentNotificationEnabled(getApplicationContext())){
            mNotificationManager.notify(RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
        } else {
            mNotificationManager.cancel(RAPIDO_NOTIFICATION_PERSISTENT);
        }
    }

    private void handleRecievedText(Intent intent) {
        String sentText = intent.getStringExtra(Intent.EXTRA_TEXT);
        String textToPost = "";

        // Check to see if it is a repost, instead of just a received intent.
        if(sentText.startsWith(REPOST_START_TEXT)) {
            if(sentText.startsWith(REPOST_ALL)) {
                textToPost = sentText.replace(REPOST_ALL + " ", "");
                mToggleButtonFacebook.setChecked(true);
                mToggleButtonTwitter.setChecked(true);
                mToggleButtonFoursquare.setChecked(true);
                mToggleButtonGooglePlus.setChecked(true);
            } else if (sentText.startsWith(REPOST_FACEBOOK)) {
                textToPost = sentText.replace(REPOST_FACEBOOK + " ", "");
                mToggleButtonFacebook.setChecked(true);
                mToggleButtonTwitter.setChecked(false);
                mToggleButtonFoursquare.setChecked(false);
                mToggleButtonGooglePlus.setChecked(false);
                if(mToggleButtonAppNet != null)
                    mToggleButtonAppNet.setChecked(false);
            } else if (sentText.startsWith(REPOST_TWITTER)) {
                textToPost = sentText.replace(REPOST_TWITTER + " ", "");
                mToggleButtonFacebook.setChecked(false);
                mToggleButtonTwitter.setChecked(true);
                mToggleButtonFoursquare.setChecked(false);
                mToggleButtonGooglePlus.setChecked(false);
                if(mToggleButtonAppNet != null)
                    mToggleButtonAppNet.setChecked(false);
            } else if (sentText.startsWith(REPOST_FOURSQUARE)) {
                textToPost = sentText.replace(REPOST_FOURSQUARE + " ", "");
                mToggleButtonFacebook.setChecked(false);
                mToggleButtonTwitter.setChecked(false);
                mToggleButtonFoursquare.setChecked(true);
                mToggleButtonGooglePlus.setChecked(false);
                if(mToggleButtonAppNet != null)
                    mToggleButtonAppNet.setChecked(false);
            } else if (sentText.startsWith(REPOST_GOOGLE_PLUS)) {
                textToPost = sentText.replace(REPOST_GOOGLE_PLUS + " ", "");
                mToggleButtonFacebook.setChecked(false);
                mToggleButtonTwitter.setChecked(false);
                mToggleButtonFoursquare.setChecked(false);
                mToggleButtonGooglePlus.setChecked(true);
                if(mToggleButtonAppNet != null)
                    mToggleButtonAppNet.setChecked(false);
            } else if (sentText.startsWith(REPOST_APP_DOT_NET)) {
                textToPost = sentText.replace(REPOST_APP_DOT_NET + " ", "");
                mToggleButtonFacebook.setChecked(false);
                mToggleButtonTwitter.setChecked(false);
                mToggleButtonFoursquare.setChecked(false);
                mToggleButtonGooglePlus.setChecked(false);
                if(mToggleButtonAppNet != null)
                    mToggleButtonAppNet.setChecked(true);
            }
        } else {
            //String sentTextTitle = intent.getStringExtra(Intent.EXTRA_TITLE);
            String sentTextSubject =
                    intent.getStringExtra(Intent.EXTRA_SUBJECT) != null
                            ? String.format("%s - ", intent.getStringExtra(Intent.EXTRA_SUBJECT))
                            : "";
            textToPost = String.format("%s%s", sentTextSubject, sentText);
        }
        mEditText.setText(textToPost);
        updateCharacterCount(-1);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.activity_main, menu);

        final MenuItem menu_settings = (MenuItem) menu
                .findItem(R.id.menu_settings);
        menu_settings.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {

            @Override
            public boolean onMenuItemClick(
                    MenuItem item) {
                EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
                String myDraftMessage = myEditText.getText().toString();
                Draft myDraft = new Draft(myDraftMessage);
                MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
                Intent preferencesIntent = new Intent(getApplicationContext(), PreferencesActivity.class);
                startActivity(preferencesIntent);
                return false;
            }
        });

        final MenuItem menu_send = (MenuItem) menu.findItem(R.id.menu_send);
        menu_send.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                SendUpdate();
                return true;
            }
        });

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    protected void onResume() {
        super.onResume();

        SetupToggleButtons();

        Intent intentReceived = getIntent();
        if (intentReceived != null) {
            String action = intentReceived.getAction();
            String type = intentReceived.getType();

            if (Intent.ACTION_SEND.equals(action) && type != null) {
                handleRecievedText(intentReceived);
            } else if(intentReceived.getStringExtra(Intent.EXTRA_TEXT) != null) {
                if(intentReceived.getStringExtra(Intent.EXTRA_TEXT).startsWith(REPOST_START_TEXT)) {
                    handleRecievedText(intentReceived);
                }
            }
        }

        if(mNotificationManager != null)
            mNotificationManager.cancel(RAPIDO_NOTIFICATION_PERSISTENT);
    }

    protected void onStart() {
        SetupToggleButtons();
        if(facebookConnected) {
            Session.getActiveSession().addCallback(statusCallback);
        }
        super.onStart();
    }

    @Override
    public void onStop() {
        super.onStop();
        if(facebookConnected) {
            Session.getActiveSession().removeCallback(statusCallback);
        }
    }

    private final TextWatcher mTextEditorWatcher = new TextWatcher() {
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        public void onTextChanged(CharSequence s, int start, int before, int count) {
            updateCharacterCount(s.length());
        }

        public void afterTextChanged(Editable s) {
        }
    };

    // Twitter Toggle Button
    private final OnCheckedChangeListener mTwitterCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateCharacterCount(-1);
            EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
            String myDraftMessage = myEditText.getText() != null ? myEditText.getText().toString() : "";
            Draft myDraft = new Draft(myDraftMessage);
            MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
            if (isChecked) {
                if (TWITTER_TOKEN != null && PreferencesHelper.getTwitterConnected(getApplicationContext())) {
                    return;
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null) {
                        // Launch Twitter Activity
                        Intent twitterAuth = new Intent(getApplicationContext(), TwitterAuthorizationActivity.class);
                        startActivity(twitterAuth);
                    } else {
                        // Show Dialog
                        buttonView.setChecked(false);
                        showDialog(DIALOG_NO_INTERNETS_ID);
                    }
                }
            }
        }
    };

    // Facebook Toggle Button
    private final OnCheckedChangeListener mFacebookCheckedChangeListener =
            new OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    updateCharacterCount(-1);
                    EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
                    String myDraftMessage = myEditText.getText() != null ? myEditText.getText().toString() : "";
                    Draft myDraft = new Draft(myDraftMessage);
                    MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
                    if (isChecked) {
                        ConnectivityManager connectivityManager =
                                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                        if (activeNetworkInfo != null) {
                            facebookSession = Session.getActiveSession();
                            if (!facebookSession.isOpened() && !facebookSession.isClosed()) {
                                facebookSession.openForRead(new Session.OpenRequest(MainActivity.this).setCallback(statusCallback));
                            } else {
                                Session.openActiveSession(MainActivity.this, true, statusCallback);
                            }
                        } else {
                            // Show Dialog
                            buttonView.setChecked(false);
                            showDialog(DIALOG_NO_INTERNETS_ID);
                        }
                    }
                }
            };

    // Foursquare Toggle Button
    private final OnCheckedChangeListener
            mFoursquareCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
            String myDraftMessage = myEditText.getText() != null ? myEditText.getText().toString() : "";
            Draft myDraft = new Draft(myDraftMessage);
            MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
            if(isChecked) {
                if (FOURSQUARE_TOKEN != null && PreferencesHelper.getFoursquareConnected(getApplicationContext())) {
                    if(CurrentPickedLocation == null || (CurrentPickedLocation != null && !CurrentPickedLocation.getIsFoursquare()))
                        SelectLocation();
                    else
                        return;
                } else {
                    ConnectivityManager connectivityManager =
                            (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null) {
                        Intent intent =
                                FoursquareOAuth.getConnectIntent(
                                        getApplicationContext(),
                                        AuthHelper.FOURSQUARE_CLIENT_ID);
                        startActivityForResult(intent, REQUEST_FOURSQUARE_AUTH);
                    } else {
                        // Show Dialog
                        buttonView.setChecked(false);
                        showDialog(DIALOG_NO_INTERNETS_ID);
                    }
                }
            }
        }
    };

    // Google Plus Toggle Button
    private final OnCheckedChangeListener mGooglePlusCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
            String myDraftMessage = myEditText.getText() != null ? myEditText.getText().toString() : "";
            Draft myDraft = new Draft(myDraftMessage);
            MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
            if(isChecked) {
                Boolean isGooglePlusChecked = PreferencesHelper.getGooglePlusConnected(getApplicationContext());
                if(isGooglePlusChecked) {
                    return;
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null) {
                        startActivity(new Intent(getApplicationContext(), PlusAuthorizationActivity.class));
                    } else {
                        // Show Dialog
                        buttonView.setChecked(false);
                        showDialog(DIALOG_NO_INTERNETS_ID);
                    }
                }
            }
        }
    };

    // App.net Toggle Button
    private final OnCheckedChangeListener mAppNetCheckedChangeListener = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            updateCharacterCount(-1);
            EditText myEditText = (EditText) findViewById(R.id.EditTextUpdate);
            String myDraftMessage = myEditText.getText().toString();
            Draft myDraft = new Draft(myDraftMessage);
            MainActivity.saveDraft(MainActivity.DRAFT_GENERIC, myDraft, getApplicationContext());
            if (isChecked) {
                if (APP_DOT_NET_TOKEN != null && PreferencesHelper.getAppDotNetConnected(getApplicationContext())) {
                    return;
                } else {
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
                    if (activeNetworkInfo != null) {
                        // Launch AppNet Activity
                        Intent AppNetAuth = new Intent(getApplicationContext(), AppDotNetAuthorizationActivity.class);
                        startActivity(AppNetAuth);
                    } else {
                        // Show Dialog
                        buttonView.setChecked(false);
                        showDialog(DIALOG_NO_INTERNETS_ID);
                    }
                }
            }
        }
    };

    private final android.view.View.OnClickListener mRelativeLayoutLocationClickListener = new android.view.View.OnClickListener() {
        @Override
        public void onClick(View v) {
            SelectLocation();
        }
    };

    private final android.view.View.OnLongClickListener mRelativeLayoutLocationLongClickListener = new android.view.View.OnLongClickListener() {
        @Override
        public boolean onLongClick(View v) {
            ClearLocation();
            Toast.makeText(getApplicationContext(), getString(R.string.location_remove), Toast.LENGTH_SHORT).show();
            return true;
        }
    };

    private void SelectLocation() {
        ConnectivityManager connectivityManager =
                (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        if (activeNetworkInfo != null) {
            Intent locationIntent = new Intent(getApplicationContext(), LocationActivity.class);
            startActivityForResult(locationIntent, PICK_LOCATION_REQUEST);
        } else {
            // Show Dialog
            showDialog(DIALOG_NO_INTERNETS_ID);
        }
    }

    private void ClearLocation() {
        CurrentPickedLocation = null;
        mImageViewLocationIcon.setImageResource(R.drawable.location_icon_gray);
        mTextViewLocationName.setText(R.string.location_add);
        mTextViewLocationDetails.setText(R.string.location_add_summary);
        mToggleButtonFoursquare.setChecked(false);
        foursquareEnabled = false;
        locationEnabled = false;
    }

    private void updateCharacterCount(int length) {
        String text = mEditText != null ? mEditText.getText().toString() : "";

        int myLimit = getMessageLengthLimit();
        int myLength = getMessageCharacterCount(text, length, myLimit);

        if (myLength > myLimit) {
            mTextView.setTextColor(Color.RED);
        } else {
            mTextView.setTextColor(Color.BLACK);
        }
        mTextView.setText(String.valueOf(myLength - myLimit));
    }

    private int getMessageCharacterCount(String text, int length, int limit) {
        if (length == -1)
            length = text.length();

        if (length >= limit && getMessageAdjustForLinks()) {
            List<String> links = Util.GetLinksInText(text);

            if (links != null && links.size() > 0) {
                int linkCharacters = 0;
                for (String linkString : links) {
                    if(mToggleButtonTwitter.isChecked())
                        linkCharacters += (linkString.length() - TWITTER_SHORT_URL_LENGTH);
                    else
                        linkCharacters += (linkString.length() - BITLY_SHORT_URL_LENGTH);
                }

                length -= linkCharacters;
            }
        }

        return length;
    }

    private int getMessageLengthLimit() {
        int limit = Util.CHAR_LIMIT_FACEBOOK;

        if(mToggleButtonAppNet != null && mToggleButtonAppNet.isChecked())
            limit = Util.CHAR_LIMIT_APP_DOT_NET;

        if ((mToggleButtonTwitter != null && mToggleButtonTwitter.isChecked()) || (mToggleButtonFoursquare != null && mToggleButtonFoursquare.isChecked()))
            limit = Util.CHAR_LIMIT_TWITTER;

        return limit;
    }

    private boolean getMessageAdjustForLinks() {
        boolean adjustForLinks = true;

        if (mToggleButtonFoursquare != null && mToggleButtonFoursquare.isChecked() && !PreferencesHelper.getBitlyConnected(getApplicationContext()))
            adjustForLinks = false;

        return adjustForLinks;
    }

    public static void saveDraft(String draftKey, Draft myDraft, Context myContext) {
        try {
            if(myDraft != null) {
                FileOutputStream fos = myContext.openFileOutput(STORAGE_FILENAME, Context.MODE_PRIVATE);
                fos.write(myDraft.toString().getBytes());
                fos.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void clearDraft(String draftKey, Context myContext) {
        myContext.deleteFile(STORAGE_FILENAME);
    }

    public static Draft getDraft(String draftKey, Context myContext) {
        try {
            String readString = null;
            FileInputStream fis = myContext.openFileInput(STORAGE_FILENAME);
            fis.read(readString.getBytes());
            fis.close();
            return Draft.GetDraftFromJson(readString);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void SendUpdate() {
        try {
            NotificationCompat.Builder mNotificationFacebook =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_facebook_large_icon))
                            .setContentTitle(getString(R.string.alert_title))
                            .setContentText(getString(R.string.alert_posting_facebook))
                            .setContentIntent(genericPendingIntent);
            NotificationCompat.Builder mNotificationTwitter =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_twitter_large_icon))
                            .setContentTitle(getString(R.string.alert_title))
                            .setContentText(getString(R.string.alert_posting_twitter))
                            .setContentIntent(genericPendingIntent);
            NotificationCompat.Builder mNotificationFoursquare =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_foursquare_large_icon))
                            .setContentTitle(getString(R.string.alert_title))
                            .setContentText(getString(R.string.alert_posting_foursquare))
                            .setContentIntent(genericPendingIntent);
            NotificationCompat.Builder mNotificationGooglePlus =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_google_plus_large_icon))
                            .setContentTitle(getString(R.string.alert_title))
                            .setContentText(getString(R.string.alert_posting_google_plus))
                            .setContentIntent(genericPendingIntent);
            NotificationCompat.Builder mNotificationAppDotNet =
                    new NotificationCompat.Builder(getApplicationContext())
                            .setSmallIcon(R.drawable.ic_stat_rapido)
                            .setLargeIcon(BitmapFactory.decodeResource(getResources(), R.drawable.ic_stat_appnet_large_icon))
                            .setContentTitle(getString(R.string.alert_title))
                            .setContentText(getString(R.string.alert_posting_app_dot_net))
                            .setContentIntent(genericPendingIntent);

            ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
            if (activeNetworkInfo != null) {
                String update = mEditText.getText().toString();
                Boolean isTwitter = mToggleButtonTwitter.isChecked();
                Boolean isFacebook = mToggleButtonFacebook.isChecked();
                Boolean isBitlyEnabled = PreferencesHelper.getBitlyConnected(getApplicationContext());
                Boolean useBitly;
                Boolean isFoursquare = mToggleButtonFoursquare.isChecked();
                Boolean isGooglePlus = mToggleButtonGooglePlus.isChecked();
                Boolean isAppDotNet = false;
                if(Util.isPro() && appDotNetEnabled)
                    isAppDotNet = mToggleButtonAppNet.isChecked();

                int limit = getMessageLengthLimit();
                int updateLength = getMessageCharacterCount(update, -1, limit);

                if (isTwitter || isFacebook || isFoursquare || isGooglePlus || isAppDotNet) {
                    if (updateLength > 0) {
                        List<String> links = Util.GetLinksInText(update);
                        if (links != null && links.size() > 0) {
                            useBitly = isBitlyEnabled;
                        } else {
                            useBitly = false;
                        }

                        if (updateLength > limit) {
                            // If we are over the limit then check to see if we have links that can be shortened.
                            if ((isTwitter && (!isFoursquare || !isAppDotNet)) ||
                                    (isTwitter && (isFoursquare && getMessageAdjustForLinks())) ||
                                    (isTwitter && (isAppDotNet && getMessageAdjustForLinks()))) {
                                int linkCharacters = 0;
                                for (String linkString : links) {
                                    if(isTwitter)
                                        linkCharacters += (linkString.length() - TWITTER_SHORT_URL_LENGTH);
                                    else
                                        linkCharacters += (linkString.length() - BITLY_SHORT_URL_LENGTH);
                                }

                                updateLength -= linkCharacters;
                            }
                        }

                        if (updateLength <= limit) {
                            LatLng myLatLng = null;
                            if(locationEnabled) {
                                myLatLng = new LatLng(CurrentPickedLocation.getLatitude(), CurrentPickedLocation.getLongitude());
                                if(myLatLng.latitude == 0.00 || myLatLng.longitude == 0.00) {
                                    myLatLng = new LatLng(
                                            LocationActivity.currentLocation.latitude,
                                            LocationActivity.currentLocation.longitude);
                                }
                            }

                            String myVenueId = CurrentPickedLocation != null ? CurrentPickedLocation.getFoursquareVenueId() : "";

                            if(isFacebook) {
                                if(facebookSession == null) {
                                    facebookSession.openForPublish(new Session.OpenRequest(this));
                                }
                                mNotificationManager.notify(FACEBOOK_NOTIFICATION, mNotificationFacebook.getNotification());
                                new Util.UpdateStatus(getApplicationContext(),
                                        Util.FACEBOOK_UPDATE, update,
                                        useBitly, myLatLng, myVenueId).execute();
                            }

                            if(isTwitter) {
                                mNotificationManager.notify(TWITTER_NOTIFICATION, mNotificationTwitter.getNotification());
                                new Util.UpdateStatus(getApplicationContext(),
                                        Util.TWITTER_UPDATE, update, useBitly,
                                        myLatLng).execute();
                            }

                            if(isFoursquare && CurrentPickedLocation.getIsFoursquare()) {
                                mNotificationManager.notify(FOURSQUARE_NOTIFICATION, mNotificationFoursquare.getNotification());
                                new Util.UpdateStatus(getApplicationContext(),
                                        Util.FOURSQUARE_UPDATE, update,
                                        useBitly, myLatLng, myVenueId)
                                        .execute();
                            }
                            if (isGooglePlus) {
                                String myPrimaryLink = "";

                                if (links != null && links.size() > 0) {
                                    String firstLink = links.get(0) != null ? links.get(0) : "";
                                    if (firstLink.length() > 0) {
                                        myPrimaryLink = firstLink;
                                    }
                                }

                                new Util.UpdateStatus(getApplicationContext(),
                                        Util.GOOGLEPLUS_UPDATE, update,
                                        useBitly, myLatLng, myPrimaryLink,
                                        MainActivity.this, myPlusClient).execute();
                            }

                            if(isAppDotNet) {
                                mNotificationManager.notify(APP_DOT_NET_NOTIFICATION, mNotificationAppDotNet.getNotification());
                                new Util.UpdateStatus(getApplicationContext(),
                                        Util.APPDOTNET_POST, update, useBitly, myLatLng,
                                        myVenueId).execute();
                            }

                            CloseApp();
                        } else {
                            if(PreferencesHelper.getFoursquareEnabled(getApplicationContext()) && !getMessageAdjustForLinks())
                                Toast.makeText(
                                        getApplicationContext(),
                                        R.string.error_message_too_long_foursquare_links,
                                        Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(getApplicationContext(),
                                        R.string.error_message_too_long,
                                        Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), R.string.error_no_message, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(), R.string.error_no_services_selected, Toast.LENGTH_SHORT).show();
                }
            } else {
                showDialog(DIALOG_NO_INTERNETS_ID);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void SetupToggleButtons() {
        mToggleButtonFacebook = (ToggleButton) findViewById(R.id.ToggleButtonFacebook);
        mToggleButtonFacebook.setOnCheckedChangeListener(mFacebookCheckedChangeListener);
        if (facebookConnected)
            mToggleButtonFacebook.setChecked(facebookEnabled);

        mToggleButtonTwitter = (ToggleButton) findViewById(R.id.ToggleButtonTwitter);
        mToggleButtonTwitter.setOnCheckedChangeListener(mTwitterCheckedChangeListener);
        if (twitterConnected)
            mToggleButtonTwitter.setChecked(twitterEnabled);

        mToggleButtonFoursquare = (ToggleButton) findViewById(R.id.ToggleButtonFoursquare);
        mToggleButtonFoursquare.setOnCheckedChangeListener(mFoursquareCheckedChangeListener);
        if (foursquareConnected)
            mToggleButtonFoursquare.setChecked(foursquareEnabled);

        RelativeLayout mRelativeLayoutGooglePlusWrapper = (RelativeLayout)findViewById(R.id.RelativeLayoutGooglePlusWrapper);

        mRelativeLayoutGooglePlusWrapper.setVisibility(View.VISIBLE);
        mToggleButtonGooglePlus = (ToggleButton) findViewById(R.id.ToggleButtonGooglePlus);
        mToggleButtonGooglePlus.setOnCheckedChangeListener(mGooglePlusCheckedChangeListener);
        if (googlePlusConnected)
            mToggleButtonGooglePlus.setChecked(googlePlusEnabled);

        RelativeLayout mRelativeLayoutAppDotNetWrapper = (RelativeLayout) findViewById(R.id.RelativeLayoutAppNetWrapper);

        if(Util.isPro()) {
            mRelativeLayoutAppDotNetWrapper.setVisibility(View.VISIBLE);
            mToggleButtonAppNet = (ToggleButton) findViewById(R.id.ToggleButtonAppNet);
            mToggleButtonAppNet.setOnCheckedChangeListener(mAppNetCheckedChangeListener);
            if (appDotNetConnected)
                mToggleButtonAppNet.setChecked(appDotNetEnabled);
        } else {
            mRelativeLayoutAppDotNetWrapper.setVisibility(View.GONE);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PICK_LOCATION_REQUEST: {
                if(resultCode == Activity.RESULT_CANCELED) {
                    ClearLocation();
                } else {
                    if(data != null && data.hasExtra(LocationActivity.PICKED_LOCATION)) {
                        String result = data.getStringExtra(LocationActivity.PICKED_LOCATION);
                        Gson myGson = new Gson();
                        PickedLocation myPickedLocation = myGson.fromJson(result, PickedLocation.class);
                        if(myPickedLocation != null) {
                            CurrentPickedLocation = myPickedLocation;
                            mImageViewLocationIcon.setImageResource(R.drawable.location_icon);
                            locationEnabled = true;
                            if(myPickedLocation.getIsFoursquare()) {
                                // Foursquare Location
                                mTextViewLocationName.setText(myPickedLocation.getName() != null ? myPickedLocation.getName() : "");
                                mTextViewLocationDetails.setText(myPickedLocation.getAddressLine() != null ? myPickedLocation.getAddressLine() : "");
                                if(PreferencesHelper.getFoursquareConnected(getApplicationContext())) {
                                    foursquareEnabled = true;
                                    mToggleButtonFoursquare.setChecked(true);
                                }
                            } else {
                                // Just use the GPS
                                mTextViewLocationName.setText(getString(R.string.location_use_gps));
                                mTextViewLocationDetails.setText(String.format("%s, %s", myPickedLocation.getLatitude(), myPickedLocation.getLongitude()));
                                mToggleButtonFoursquare.setChecked(false);
                                foursquareEnabled = false;
                            }
                        } else {
                            ClearLocation();
                        }
                    } else {
                        ClearLocation();
                    }
                }
                break;
            }
            case GooglePlusAuthorizationActivity.REQUEST_CODE_RESOLVE_ERR: {
                if(resultCode == RESULT_OK) {
                    googlePlusConnected = true;
                    mToggleButtonGooglePlus.setChecked(true);
                } else if (resultCode == RESULT_CANCELED) {
                    Toast.makeText(getApplicationContext(), "Google Plus auth cancelled.", Toast.LENGTH_SHORT).show();
                    mToggleButtonGooglePlus.setChecked(false);
                } else {
                    Toast.makeText(getApplicationContext(), "Google Plus auth failed, try again.", Toast.LENGTH_SHORT).show();
                    mToggleButtonGooglePlus.setChecked(false);
                }
                break;
            }
            case REQUEST_FOURSQUARE_AUTH:
                AuthCodeResponse codeResponse =
                        FoursquareOAuth.getAuthCodeFromResult(resultCode, data);

                if(codeResponse != null) {
                    if(codeResponse.getCode() != null) {
                        String authCode = codeResponse.getCode();
                        Intent tokenIntent = FoursquareOAuth.getTokenExchangeIntent(
                                getApplicationContext(),
                                AuthHelper.FOURSQUARE_CLIENT_ID,
                                AuthHelper.FOURSQUARE_CLIENT_SECRET, authCode);
                        startActivityForResult(tokenIntent, REQUEST_FOURSQUARE_AUTH_TOKEN);
                    }
                }
                break;
            case REQUEST_FOURSQUARE_AUTH_TOKEN:
                AccessTokenResponse tokenResponse =
                        FoursquareOAuth.getTokenFromResult(resultCode, data);

                if(tokenResponse != null && tokenResponse.getAccessToken() != null) {
                    PreferencesHelper.setFoursquareToken(getApplicationContext(),
                            tokenResponse.getAccessToken());
                    PreferencesHelper.setFoursquareConnected(getApplicationContext(), true);
                    mToggleButtonFoursquare.setChecked(true);
                } else {
                    if(tokenResponse != null && tokenResponse.getException() != null) {
                        Toast.makeText(getApplicationContext(),
                                "Problem Authenticating: " + tokenResponse.getException().toString(),
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Problem Authenticating: An unknown error occurred",
                                Toast.LENGTH_SHORT).show();
                    }

                    mToggleButtonFoursquare.setChecked(false);
                    PreferencesHelper.setFoursquareToken(getApplicationContext(), "");
                    PreferencesHelper.setFoursquareConnected(getApplicationContext(), false);
                }
                break;
            case FACEBOOK_LOGIN_RESULT: {
                break;
            }
            default: {
                Session.getActiveSession().onActivityResult(this, requestCode, resultCode, data);
                break;
            }
        }
    }

    @Override
    @Deprecated
    protected Dialog onCreateDialog(int id) {
        AlertDialog alert;
        switch (id) {
            case DIALOG_NO_INTERNETS_ID:
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(this);
                dialogBuilder
                        .setMessage(getString(R.string.dialog_no_internets))
                        .setCancelable(true)
                        .setPositiveButton("ok",
                                new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog,
                                                        int which) {
                                        dialog.cancel();
                                    }
                                });

                alert = dialogBuilder.create();
                break;
            default:
                alert = null;
        }
        return alert;
    }

    public void CloseApp() {
        mEditText.setText("");
        ClearLocation();

        NotificationManager mNotificationManager =
                (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);

        if(PreferencesHelper.getPersistentNotificationEnabled(getApplicationContext()))
            mNotificationManager.notify(RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
        else
            mNotificationManager.cancel(RAPIDO_NOTIFICATION_PERSISTENT);

        finish();
    }

    @Override
    protected void onDestroy() {
        if(PreferencesHelper.getPersistentNotificationEnabled(getApplicationContext()))
            mNotificationManager.notify(RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
        else
            mNotificationManager.cancel(RAPIDO_NOTIFICATION_PERSISTENT);

        super.onDestroy();
    }

    @Override
    protected void onPause() {
        if(PreferencesHelper.getPersistentNotificationEnabled(getApplicationContext()))
            mNotificationManager.notify(RAPIDO_NOTIFICATION_PERSISTENT, mNotificationRapidoPersistent.getNotification());
        else
            mNotificationManager.cancel(RAPIDO_NOTIFICATION_PERSISTENT);
        super.onPause();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        Session session = Session.getActiveSession();
        Session.saveSession(session, outState);
    }

    private class SessionStatusCallback implements Session.StatusCallback {
        @Override
        public void call(Session session, SessionState state, Exception exception) {
            updateView();
        }
    }

    private void updateView() {
        Session session = Session.getActiveSession();
        if (session.isOpened()) {
            PreferencesHelper.setFacebookConnected(getApplicationContext(), true);
            PreferencesHelper.setFacebookKey(getApplicationContext(), session.getAccessToken());
            PreferencesHelper.setFacebookEnabled(getApplicationContext(), true);
            mToggleButtonFacebook.setChecked(true);
        }
//        } else {
//            PreferencesHelper.setFacebookConnected(getApplicationContext(), false);
//            PreferencesHelper.setFacebookKey(getApplicationContext(), "");
//            mToggleButtonFacebook.setChecked(false);
//        }
    }
}