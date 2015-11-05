package com.thunsaker.rapido.ui;

import android.Manifest;
import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Notification;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.location.Location;
import android.os.Build;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.app.TaskStackBuilder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.ViewGroup;
import android.view.animation.AlphaAnimation;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginResult;
import com.foursquare.android.nativeoauth.FoursquareOAuth;
import com.foursquare.android.nativeoauth.model.AccessTokenResponse;
import com.foursquare.android.nativeoauth.model.AuthCodeResponse;
import com.github.mrengineer13.snackbar.SnackBar;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.PlusShare;
import com.google.gson.Gson;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewTextChangeEvent;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.rapido.BuildConfig;
import com.thunsaker.rapido.R;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.TwitterTextUtils;
import com.thunsaker.rapido.app.BaseRapidoFragment;
import com.thunsaker.rapido.app.NotificationFactory;
import com.thunsaker.rapido.data.PendingUpdate;
import com.thunsaker.rapido.data.PickedLocation;
import com.thunsaker.rapido.data.api.ShortenResponse;
import com.thunsaker.rapido.data.api.model.PostUserCheckinResponse;
import com.thunsaker.rapido.data.events.BitlyAuthEvent;
import com.thunsaker.rapido.data.events.ShortenedUrlEvent;
import com.thunsaker.rapido.data.events.UpdateEvent;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.services.BitlyPrefs;
import com.thunsaker.rapido.services.BitlyService;
import com.thunsaker.rapido.services.ServiceNotifications;
import com.thunsaker.rapido.services.TwitterTasks;
import com.thunsaker.rapido.services.UpdateService;
import com.thunsaker.rapido.services.UpdateServiceResult;
import com.thunsaker.rapido.services.foursquare.SwarmService;
import com.thunsaker.rapido.ui.services.ServiceUtils;
import com.thunsaker.rapido.ui.widget.CheckableImageView;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthException;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;
import com.twitter.sdk.android.core.identity.TwitterLoginButton;
import com.wangjie.wavecompat.WaveCompat;
import com.wangjie.wavecompat.WaveDrawable;
import com.wangjie.wavecompat.WaveTouchHelper;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.OnLongClick;
import de.greenrobot.event.EventBus;
import io.fabric.sdk.android.Fabric;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.schedulers.Schedulers;
import uk.co.deanwild.materialshowcaseview.IShowcaseListener;
import uk.co.deanwild.materialshowcaseview.MaterialShowcaseView;

import static com.thunsaker.rapido.services.UpdateService.SERVICE_FACEBOOK;
import static com.thunsaker.rapido.services.UpdateService.SERVICE_FOURSQUARE;
import static com.thunsaker.rapido.services.UpdateService.SERVICE_TWITTER;

public class MainFragment extends BaseRapidoFragment
    implements WaveTouchHelper.OnWaveTouchHelperListener {

    private static final String SHOWCASE_ID = "SHOWCASE_SOCIAL_ICONS";
    @Inject @ForApplication
    Context mContext;

    @Inject
    EventBus mBus;

    @Inject
    RapidoPrefsManager mPreferences;

    @Inject
    NotificationManagerCompat mNotificationManager;

    @Inject
    Fabric mFabric;

    @Inject
    TwitterTasks mTwitterTasks;

    @Inject
    BitlyService mBitlyService;

    @Inject
    SwarmService mSwarmService;

    @Bind(R.id.compose_send_button) Button mButtonSend;

    @Bind(R.id.compose_add_location) ImageButton mButtonLocationAdd;
    @Bind(R.id.compose_location_wrapper) RelativeLayout mWrapperLocation;
    @Bind(R.id.compose_remove_location) ImageButton mButtonLocationRemove;
    @Bind(R.id.compose_location_text) TextView mTextLocation;
//    @Bind(R.id.compose_map_wrapper) FrameLayout mWrapperMap;

    @Bind(R.id.compose_to_chips_wrapper) LinearLayout mComposeToChipsWrapper;
    @Bind(R.id.compose_to_chip_facebook) CheckableImageView mChipFacebook;
    @Bind(R.id.compose_to_chip_twitter) CheckableImageView mChipTwitter;
    @Bind(R.id.compose_to_chip_foursquare) CheckableImageView mChipFoursquare;
    @Bind(R.id.compose_to_chip_plus) CheckableImageView mChipPlus;
    @Bind(R.id.compose_to_chip_bitly) CheckableImageView mChipBitly;

    @Bind(R.id.twitter_login_button) TwitterLoginButton mTwitterLogin;
    @Bind(R.id.facebook_login_button) com.facebook.login.widget.LoginButton mFacebookLogin;

    @Bind(R.id.compose_edit_text) EditText mComposeText;
    @Bind(R.id.compose_text_count) TextView mTextCount;

    public LayoutInflater mInflater;

    public static GoogleApiClient mGoogleClient;
    private static final int REQUEST_CODE_FOURSQUARE_SIGN_IN = 4000;
    private static final int REQUEST_CODE_FOURSQUARE_AUTH_TOKEN = 4001;

    public static final int REQUEST_CODE_LOCATION_PICKER = 4002;
    public static PickedLocation CurrentPickedLocation;

    private static int REQUEST_CODE_PERMISSION_GET_ACCOUNT = 1;
    private static int REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION = 2;

    public final int TWITTER_SHORT_URL_LENGTH = 23;
    public final int BITLY_SHORT_URL_LENGTH = 20;

    public final int CHAR_LIMIT_FACEBOOK = 1000;
    public final int CHAR_LIMIT_TWITTER = 140;

    CallbackManager facebookCallbackManager;
    private Intent genericIntent;
    private PendingIntent genericPendingIntent;

    private List<String> mLinksInText;
    private String mUpdateText;
    private PendingUpdate mPendingUpdate;
    private static String PENDING_UPDATE_KEY = "PENDING_UPDATE_KEY";

    private static final String ARG_RECEIVED_TEXT = "ARG_RECEIVED_TEXT";

    private UpdateEvent mUpdateEventData;
    private int PLACE_SEARCH_RADIUS = 500;
    private int PLACE_SEARCH_COUNT = 2;
    private String DIALOG_PERM_ACCOUNT_EXPLAINER = "PERM_ACCOUNT_EXPLAINER";
    private String DIALOG_PERM_LOCATION_EXPLAINER = "PERM_LOCATION_EXPLAINER";
//    private boolean mLocationEnabled = false;

    private enum PendingAction {
        NONE,
        POST_PHOTO,
        POST_STATUS_UPDATE
    }

    private String LOG_TAG = "MainFragment";

    public static MainFragment newInstance() {
        MainFragment fragment = new MainFragment();
        fragment.setArguments(new Bundle());
        return fragment;
    }

    public static MainFragment newInstance(String receivedText) {
        MainFragment fragment = new MainFragment();
        Bundle args = new Bundle();
        args.putString(ARG_RECEIVED_TEXT, receivedText);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        if(mBus != null && !mBus.isRegistered(this))
            mBus.register(this);

        FacebookSdk.sdkInitialize(mContext);

        genericIntent = new Intent(mContext, MainActivity.class);
        TaskStackBuilder stackBuilder = TaskStackBuilder.from(mContext);
        stackBuilder.addParentStack(MainActivity.class);
            stackBuilder.addNextIntent(genericIntent);
        genericPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT
                );

        mPendingUpdate = new PendingUpdate();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                Bundle savedInstanceState) {
        View rootView;
        rootView = inflater.inflate(R.layout.fragment_compose, container, false);
        mInflater = inflater;
        ButterKnife.bind(this, rootView);

        SetupAccountList();

        SetupEditText();

        SetupTwitterLogin();
        SetupFacebookLogin();

        SetupShowcaseView();

        Bundle args = getArguments();
        if(args != null) {
            String receivedText = args.getString(ARG_RECEIVED_TEXT);
            if(receivedText != null)
                mComposeText.setText(receivedText);
        }

        if(savedInstanceState != null) {
            String pendingUpdateJson = savedInstanceState.getString(PENDING_UPDATE_KEY);
            if(pendingUpdateJson != null)
                mPendingUpdate = PendingUpdate.GetPendingUpdateFromJson(pendingUpdateJson);
            if(mPendingUpdate != null)
                handlePendingUpdate();
        }

        WaveTouchHelper.bindWaveTouchHelper(mButtonLocationAdd, this);

        return rootView;
    }

    private void SetupShowcaseView() {
//        if(BuildConfig.DEBUG)
//            MaterialShowcaseView.resetSingleUse(mContext, SHOWCASE_ID);

        new MaterialShowcaseView.Builder(getActivity())
                .setTarget(mComposeToChipsWrapper)
                .setDelay(1000)
                .setDismissText(getString(R.string.showcase_dismiss_text).toUpperCase(Locale.getDefault()))
//                .setDismissTextBackgroundColor(R.color.accent)
                .setContentText(R.string.showcase_text_accounts)
                .setMaskColour(R.color.accent)
                .singleUse(SHOWCASE_ID)
                .setListener(new IShowcaseListener() {
                    @Override
                    public void onShowcaseDisplayed(MaterialShowcaseView materialShowcaseView) {
                        mChipBitly.setVisibility(View.VISIBLE);
                        mChipBitly.setChecked(true);
                        mChipTwitter.setChecked(true);
                        mChipFacebook.setChecked(true);
                        mChipPlus.setChecked(true);
                        mChipFoursquare.setChecked(true);
                        hideKeyboard();
                    }

                    @Override
                    public void onShowcaseDismissed(MaterialShowcaseView materialShowcaseView) {
                        SetupAccountList();
                        showKeyboard();
                        // Reset the status bar color until the MaterialShowcaseView bug is fixed
                        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
                            getActivity().getWindow().setStatusBarColor(mContext.getResources().getColor(R.color.gray_light));
                    }
                })
                .show();
    }

    private void handlePendingUpdate() {
        mComposeText.setText(mPendingUpdate.text);
        for (UpdateService service : mPendingUpdate.services) {
            switch (service) {
                case SERVICE_TWITTER:
                    mChipTwitter.setEnabled(true);
                    break;
                case SERVICE_FACEBOOK:
                    mChipFacebook.setEnabled(true);
                    break;
                case SERVICE_FOURSQUARE:
                    mChipFoursquare.setEnabled(true);
                    break;
                case SERVICE_GOOGLE_PLUS:
                    mChipPlus.setEnabled(true);
                    break;
                case SERVICE_BITLY:
                    mChipBitly.setEnabled(true);
                    break;
            }
        }

        if(mPendingUpdate.location != null) {
            CurrentPickedLocation = mPendingUpdate.location;
            ShowPickedLocationInfo();
        }
    }

    private void SetupEditText() {
        RxTextView.textChangeEvents(mComposeText)
                .subscribe(new Action1<TextViewTextChangeEvent>() {
                    @Override
                    public void call(TextViewTextChangeEvent onTextViewTextChangeEvent) {
                        updateComposeTextCharacterCount(onTextViewTextChangeEvent.text().length());
                    }
                });
    }

    private void updateComposeTextCharacterCount(int characterLength) {
        String text = mComposeText != null ? mComposeText.getText().toString() : "";
        mUpdateText = text;

        int limit = getMessageLengthLimit();
        int length = getMessageCharacterCount(text, characterLength);

        if(length > limit) {
            mTextCount.setTextColor(getResources().getColor(R.color.character_count_limit));
        } else if(length > (limit - 20)) {
            mTextCount.setTextColor(getResources().getColor(R.color.character_count_warn));
        } else {
            mTextCount.setTextColor(getResources().getColor(R.color.character_count_default));
        }

        mTextCount.setText(String.valueOf(limit - length));
    }

    private void UpdateCharCountFromChip() {
        updateComposeTextCharacterCount(mComposeText.getText().toString().length());
    }

    private void UpdatePendingUpdateServices(CheckableImageView chip, UpdateService service) {
        if(chip.isChecked())
            if(!mPendingUpdate.services.contains(service))
                mPendingUpdate.services.add(service);
            else
            if(mPendingUpdate.services.contains(service))
                mPendingUpdate.services.remove(service);
    }

    private int getMessageCharacterCount(String text, int length) {
        if (length == -1)
            length = text.length();

        mLinksInText = null;

        if (getMessageAdjustForLinks()) {
            List<String> links = TwitterTextUtils.GetLinksInText(text);

            if (links != null && links.size() > 0) {
                mLinksInText = links;
                mChipBitly.setVisibility(View.VISIBLE);
                mChipBitly.startAnimation(new AlphaAnimation(0, 1));
                if (MainActivity.mBitlyEnabled)
                    mChipBitly.setChecked(true);

                int linkCharacters = 0;
                for (String linkString : links) {
                    if (mChipTwitter.isChecked())
                        linkCharacters += (linkString.length() - TWITTER_SHORT_URL_LENGTH);
                    else
                        linkCharacters += (linkString.length() - BITLY_SHORT_URL_LENGTH);
                }

                length -= linkCharacters;
            } else {
                mLinksInText = null;
                mChipBitly.startAnimation(new AlphaAnimation(1, 0));
                mChipBitly.setVisibility(View.GONE);
                mChipBitly.setChecked(false);
            }
        }

        return length;
    }

    private int getMessageLengthLimit() {
        int limit = CHAR_LIMIT_FACEBOOK;
        if(mChipTwitter.isChecked() || mChipFoursquare.isChecked())
            limit = CHAR_LIMIT_TWITTER;

        return limit;
    }

    private boolean getMessageAdjustForLinks() {
        boolean adjustForLinks = true;

        if (mChipFoursquare.isChecked() && !mPreferences.bitlyEnabled().getOr(false))
            adjustForLinks = false;

        return adjustForLinks;
    }

    private void SetupTwitterLogin() {
        mTwitterLogin.setCallback(new Callback<TwitterSession>() {
            @Override
            public void success(Result<TwitterSession> twitterSessionResult) {
                try {
                    mChipTwitter.setChecked(true);

                    TwitterSession session = Twitter.getSessionManager().getActiveSession();
                    String twitterUsername = session.getUserName();

                    String token = session.getAuthToken().token;
                    String secret = session.getAuthToken().secret;
                    mPreferences
                            .twitterUsername().put(twitterUsername)
                            .twitterAuthToken().put(token)
                            .twitterAuthSecret().put(secret)
                            .twitterEnabled().put(true)
                            .apply();
                    MainActivity.mTwitterEnabled = mPreferences.twitterEnabled().getOr(false);

                    PopSnackBar(String.format(getString(R.string.account_connected), twitterUsername), SERVICE_TWITTER);
                } catch (Exception e) {
                    e.printStackTrace();
                    PopSnackBar(getResources().getString(R.string.error_generic), SERVICE_TWITTER);
                }
            }

            @Override
            public void failure(TwitterException e) {
                mChipTwitter.setChecked(false);
            }
        });
    }

    private void SetupFacebookLogin() {
        facebookCallbackManager = CallbackManager.Factory.create();

        mFacebookLogin.setPublishPermissions("publish_actions");
        mFacebookLogin.setFragment(this);
        mFacebookLogin.registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        mPreferences
                                .facebookEnabled().put(true)
                                .facebookUsername().put(loginResult.getAccessToken().getUserId())
                                .apply();

                        MainActivity.mFacebookEnabled = mPreferences.facebookEnabled().getOr(false);
                        mChipFacebook.setChecked(true);

                        if (getActivity() != null && getActivity().getWindow() != null
                                && getActivity().getWindow().isActive())
                            PopSnackBar(String.format(
                                            getString(R.string.account_connected),
                                            getString(R.string.accounts_facebook)),
                                    UpdateService.SERVICE_FACEBOOK);
                    }

                    @Override
                    public void onCancel() {
                        mPreferences
                                .facebookEnabled().put(true)
                                .facebookUsername().put("")
                                .apply();

                        MainActivity.mFacebookEnabled = false;

                        if (getActivity() != null && getActivity().getWindow() != null
                                && getActivity().getWindow().isActive())
                            PopSnackBar(String.format(
                                            getString(R.string.account_not_connected),
                                            getString(R.string.accounts_facebook)),
                                    UpdateService.SERVICE_FACEBOOK);
                    }

                    @Override
                    public void onError(FacebookException e) {
                        mPreferences
                                .facebookEnabled().put(true)
                                .facebookUsername().put("")
                                .apply();

                        MainActivity.mFacebookEnabled = false;

                        if (getActivity() != null && getActivity().getWindow() != null
                                && getActivity().getWindow().isActive())
                            PopSnackBar(String.format(
                                            getString(R.string.account_authentication_error),
                                            getString(R.string.accounts_facebook)),
                                    UpdateService.SERVICE_FACEBOOK);
                    }
                });

        // Listen for the facebook button to change text
        RxTextView.textChangeEvents(mFacebookLogin)
                .subscribe(new Action1<TextViewTextChangeEvent>() {
                    @Override
                    public void call(TextViewTextChangeEvent onTextViewTextChangeEvent) {
                        if (mFacebookLogin.getText().toString().contains("Log in") && mPreferences.facebookEnabled().getOr(false)) {
                            FacebookSignOut();
                        }
                    }
                });
    }

    public void SetupAccountList() {
        mChipFacebook.setChecked(MainActivity.mFacebookEnabled);

        mChipTwitter.setChecked(MainActivity.mTwitterEnabled);

        SetupFoursquareChip();

        mChipPlus.setChecked(MainActivity.mPlusEnabled);

        mChipBitly.setChecked(MainActivity.mBitlyEnabled);
        if(mComposeText.getText().length() == 0)
            mChipBitly.setVisibility(View.GONE);
    }

    private void SetupFoursquareChip() {
//        if(mLocationEnabled)
            if(MainActivity.mFoursquareEnabled)
                if (CurrentPickedLocation != null)
                    mChipFoursquare.setChecked(true);
                else
                    mChipFoursquare.setChecked(false);
            else
                mChipFoursquare.setChecked(false);
//        else
//            mChipFoursquare.setVisibility(View.GONE);
    }

    @OnClick(R.id.compose_to_chip_facebook)
    public void FacebookChipClick() {
        if(!MainActivity.mFacebookEnabled) {
            mChipFacebook.setChecked(false);
            mFacebookLogin.performClick();
        } else {
            UpdateCharCountFromChip();
            UpdatePendingUpdateServices(mChipFacebook, UpdateService.SERVICE_FACEBOOK);
        }
    }

    @OnLongClick(R.id.compose_to_chip_facebook)
    public boolean FacebookChipLongClick() {
        if(MainActivity.mFacebookEnabled) {
            mFacebookLogin.performClick();
            return true;
        }

        return false;
    }

    private void FacebookSignOut() {
        MainActivity.mFacebookEnabled = false;
        mPreferences
                .facebookEnabled().put(false)
                .facebookUsername().put("")
                .apply();

        mChipFacebook.setChecked(false);
        mFacebookLogin.clearPermissions();
        UpdatePendingUpdateServices(mChipFacebook, UpdateService.SERVICE_FACEBOOK);

        PopSnackBar(
                String.format(getString(R.string.account_disconnected), getString(R.string.accounts_facebook)),
                UpdateService.SERVICE_FACEBOOK);
    }

    @OnClick(R.id.compose_to_chip_twitter)
    public void TwitterChipClick() {
        if(!MainActivity.mTwitterEnabled) {
            mChipTwitter.setChecked(false);
            mTwitterLogin.performClick();
        } else {
            UpdateCharCountFromChip();
            UpdatePendingUpdateServices(mChipTwitter, UpdateService.SERVICE_TWITTER);
        }
    }

    @OnLongClick(R.id.compose_to_chip_twitter)
    public boolean TwitterChipLongClick() {
        if(MainActivity.mTwitterEnabled) {
            TwitterSignOut();
            return true;
        }
        return false;
    }

    private void TwitterSignOut() {
        if(Twitter.getInstance() != null)
            Twitter.logOut();

        MainActivity.mTwitterEnabled = false;

        mChipTwitter.setChecked(false);
        UpdatePendingUpdateServices(mChipTwitter, UpdateService.SERVICE_TWITTER);

        mPreferences
                .twitterEnabled().put(false)
                .twitterUsername().put("")
                .apply();

        PopSnackBar(String.format(getString(R.string.account_disconnected), getString(R.string.accounts_twitter)), SERVICE_TWITTER);
    }

    @OnClick(R.id.compose_to_chip_foursquare)
    public void FoursquareChipClick() {
        if(!MainActivity.mFoursquareEnabled) {
            mChipFoursquare.setChecked(false);
            Intent foursquareAuth =
                    FoursquareOAuth.getConnectIntent(mContext,
                            AuthHelper.FOURSQUARE_CLIENT_ID);
            if (FoursquareOAuth.isPlayStoreIntent(foursquareAuth)) {
                // TODO: Make this into a dialog with options to authenticate with browser or install Foursquare.
                Toast.makeText(mContext, getString(R.string.accounts_foursquare_not_installed), Toast.LENGTH_SHORT).show();
                if(GooglePlayServicesUtil.isGooglePlayServicesAvailable(mContext) == ConnectionResult.SUCCESS) {
                    this.getActivity().startActivity(foursquareAuth);
                } else {
                    //TODO: Foursquare Web Auth
//                    Intent foursquareWebAuth =
//                    this.getActivity().startActivity(foursquareWebAuth);
                }
            } else {
                startActivityForResult(foursquareAuth, REQUEST_CODE_FOURSQUARE_SIGN_IN);
            }
        } else {
            UpdateCharCountFromChip();
            UpdatePendingUpdateServices(mChipFoursquare, UpdateService.SERVICE_FOURSQUARE);
            if(CurrentPickedLocation == null)
                ShowLocationPicker();
        }
    }

    @OnLongClick(R.id.compose_to_chip_foursquare)
    public boolean FoursquareChipLongClick() {
        if(MainActivity.mFoursquareEnabled) {
            FoursquareSignOut();
            return true;
        }
        return false;
    }

    private void FoursquareSignOut() {
        MainActivity.mFoursquareEnabled = false;

        mChipFoursquare.setChecked(false);
        UpdatePendingUpdateServices(mChipFoursquare, UpdateService.SERVICE_FOURSQUARE);

        mPreferences
                .foursquareEnabled().put(false)
                .foursquareToken().put("")
                .foursquareUsername().put("")
                .apply();

        PopSnackBar(String.format(getString(R.string.account_disconnected), getString(R.string.accounts_foursquare)), UpdateService.SERVICE_FOURSQUARE);
    }

    @OnClick(R.id.compose_to_chip_plus)
    public void PlusChipClick() {
        if(!MainActivity.mPlusEnabled) {
            mChipPlus.setChecked(false);

            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                    ActivityCompat.checkSelfPermission(
                        getActivity(),
                        Manifest.permission.GET_ACCOUNTS)
                            != PackageManager.PERMISSION_GRANTED) {

                if(ActivityCompat.shouldShowRequestPermissionRationale(
                        getActivity(),
                        Manifest.permission.GET_ACCOUNTS)) {
                    PopSnackBar(getString(R.string.perm_get_accounts_rationale),
                            UpdateService.SERVICE_GOOGLE_PLUS, R.string.perm_enable,
                            new SnackBar.OnMessageClickListener() {
                                @Override
                                public void onMessageClick(Parcelable token) {
                                    ActivityCompat.requestPermissions(
                                            getActivity(),
                                            new String[]{Manifest.permission.GET_ACCOUNTS},
                                            REQUEST_CODE_PERMISSION_GET_ACCOUNT);
                                }
                            });
                } else {
                    new GooglePlusPermissionExplainerDialog()
                            .show(getFragmentManager(), DIALOG_PERM_ACCOUNT_EXPLAINER);
                }
            } else {
                startActivityForResult(new Intent(mContext, GooglePlusAuthActivity.class), GooglePlusAuthActivity.REQUEST_CODE_GOOGLE_SIGN_IN);
            }
        } else {
            UpdateCharCountFromChip();
            UpdatePendingUpdateServices(mChipPlus, UpdateService.SERVICE_GOOGLE_PLUS);
        }
    }

    public void startActivityForResult(Intent intent, int requestCode) {
        this.getActivity().startActivityForResult(
                intent,
                requestCode);
    }

    @OnLongClick(R.id.compose_to_chip_plus)
    public boolean PlusChipLongClick() {
        if(MainActivity.mPlusEnabled) {
            PlusSignOut();
            return true;
        }
        return false;
    }

    private void PlusSignOut() {
        if (mGoogleClient != null && mGoogleClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(mGoogleClient);
            mGoogleClient.disconnect();
        }

        MainActivity.mPlusEnabled = false;
        mChipPlus.setChecked(false);
        UpdatePendingUpdateServices(mChipPlus, UpdateService.SERVICE_GOOGLE_PLUS);
        mPreferences
                .googleEnabled().put(false)
                .googleUsername().put("")
                .apply();

        PopSnackBar(
                String.format(getString(R.string.account_disconnected), getString(R.string.accounts_google_plus)),
                UpdateService.SERVICE_GOOGLE_PLUS);
    }

    @OnClick(R.id.compose_to_chip_bitly)
    public void BitlyChipClick() {
        if(!MainActivity.mBitlyEnabled) {
            mChipBitly.setChecked(false);
            startActivityForResult(
                    new Intent(mContext, BitlyAuthActivity.class),
                    BitlyAuthActivity.REQUEST_CODE_BITLY_SIGN_IN);
        } else {
            UpdateCharCountFromChip();
            UpdatePendingUpdateServices(mChipBitly, UpdateService.SERVICE_BITLY);
        }
    }

    @OnLongClick(R.id.compose_to_chip_bitly)
    public boolean BitlyChipLongClick() {
        if(MainActivity.mBitlyEnabled) {
            BitlySignOut();
            return true;
        }
        return false;
    }

    private void BitlySignOut() {
        MainActivity.mBitlyEnabled = false;

        mChipBitly.setChecked(false);
        UpdatePendingUpdateServices(mChipBitly, UpdateService.SERVICE_BITLY);

        mPreferences
                .bitlyEnabled().put(false)
                .bitlyToken().put("")
                .bitlyApiKey().put("")
                .bitlyUsername().put("")
                .apply();

        PopSnackBar(String.format(getString(R.string.account_disconnected), getString(R.string.accounts_bitly)), UpdateService.SERVICE_BITLY);
    }

    private void PopSnackBar(String message) {
        PopSnackBar(message, null);
    }

    private void PopSnackBar(String message, UpdateService service) {
        PopSnackBar(message, service, 0, null);
    }

    private void PopSnackBar(String message, UpdateService service, int actionId,
                             SnackBar.OnMessageClickListener clickListener) {
        SnackBar.Builder builder =
                new SnackBar.Builder(this.getActivity()).withMessage(message);
        if(service != null) {
            int textColor = ServiceUtils.GetServiceTextColor(service);
            builder.withTextColorId(textColor);

            if(service != UpdateService.NONE) {
                int backgroundColor = ServiceUtils.GetServiceColor(service);
                builder.withBackgroundColorId(backgroundColor);
            }
        }

        if(actionId > 0 && clickListener != null) {
            builder.withActionMessageId(actionId)
                    .withOnClickListener(clickListener);
        }

        builder.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_FOURSQUARE_SIGN_IN:
                AuthCodeResponse codeResponse =
                        FoursquareOAuth.getAuthCodeFromResult(resultCode, data);

                if (codeResponse != null && codeResponse.getCode() != null) {
                    String authCode = codeResponse.getCode();
                    Intent tokenIntent =
                            FoursquareOAuth.getTokenExchangeIntent(
                                    mContext,
                                    AuthHelper.FOURSQUARE_CLIENT_ID,
                                    AuthHelper.FOURSQUARE_CLIENT_SECRET,
                                    authCode);
                    startActivityForResult(tokenIntent, REQUEST_CODE_FOURSQUARE_AUTH_TOKEN);
                }
                break;
            case REQUEST_CODE_FOURSQUARE_AUTH_TOKEN:
                AccessTokenResponse tokenResponse =
                        FoursquareOAuth.getTokenFromResult(resultCode, data);

                if (tokenResponse != null && tokenResponse.getAccessToken() != null) {
                    mPreferences
                            .foursquareToken().put(tokenResponse.getAccessToken())
                            .foursquareEnabled().put(true)
                            .apply();

                    // TODO: Create background task/event to populate username
                    mChipFoursquare.setChecked(true);
                    MainActivity.mFoursquareEnabled = mPreferences.foursquareEnabled().getOr(false);

                    ShowLocationPicker();

                    PopSnackBar(
                            String.format(
                                    getString(R.string.account_connected),
                                    getString(R.string.accounts_foursquare)),
                            UpdateService.SERVICE_FOURSQUARE);
                } else {
                    PopSnackBar(
                            String.format(
                                    getString(R.string.account_authentication_error),
                                    getString(R.string.accounts_foursquare)),
                            UpdateService.SERVICE_FOURSQUARE);
                    if (tokenResponse != null && tokenResponse.getException() != null) {
                        Log.e(LOG_TAG, "Problem Authenticating Foursquare: " + tokenResponse.getException().toString());
                    } else {
                        Log.e(LOG_TAG, "Problem Authenticating Foursquare: An unknown error occurred");
                    }
                }
                break;
            case GooglePlusAuthActivity.REQUEST_CODE_GOOGLE_SIGN_IN:
                if (resultCode == Activity.RESULT_OK && MainActivity.mPlusEnabled) {
                    PopSnackBar(
                            String.format(getString(R.string.account_connected), getString(R.string.accounts_google_plus)),
                            UpdateService.SERVICE_GOOGLE_PLUS);
                    MainActivity.mPlusEnabled = mPreferences.googleEnabled().getOr(false);
                    mChipPlus.setChecked(true);
                } else {
                    Log.e(LOG_TAG, "Problem Authenticating Google Plus: An unknown error occurred");
                    PopSnackBar(
                            String.format(
                                    getString(R.string.account_authentication_error),
                                    getString(R.string.accounts_google_plus)),
                            UpdateService.SERVICE_GOOGLE_PLUS);
                    mChipPlus.setChecked(false);
                }
                break;
            case BitlyAuthActivity.REQUEST_CODE_BITLY_SIGN_IN:
                if (resultCode == Activity.RESULT_OK) {
                    PopSnackBar(
                            String.format(
                                    getString(R.string.account_finishing_up),
                                    getString(R.string.accounts_bitly)), UpdateService.SERVICE_BITLY);
                    mChipBitly.setChecked(true);
                } else {
                    Log.e(LOG_TAG, "Problem Authenticating Bitly: An unknown error occurred");
                    PopSnackBar(
                            String.format(
                                    getString(R.string.account_authentication_error),
                                    getString(R.string.accounts_bitly)),
                            UpdateService.SERVICE_BITLY);
                    mChipBitly.setChecked(false);
                }
                break;
            case REQUEST_CODE_LOCATION_PICKER:
                if (resultCode == Activity.RESULT_OK) {
                    if (data != null && data.hasExtra(LocationPicker.PICKED_LOCATION)) {
                        String result = data.getStringExtra(LocationPicker.PICKED_LOCATION);
                        Gson myGson = new Gson();
                        PickedLocation picked = myGson.fromJson(result, PickedLocation.class);
                        if (picked != null) {
                            CurrentPickedLocation = picked;
                            mPendingUpdate.location = picked;
                            ShowPickedLocationInfo();
                        }
                    }
                } else {
                    PopSnackBar(getString(R.string.error_location_picking));
                    mChipFoursquare.setChecked(false);
                }
                break;
            default:
                try {
                    mTwitterLogin.onActivityResult(requestCode, resultCode, data);
                } catch (TwitterAuthException authex) {
                    PopSnackBar(authex.getMessage(), SERVICE_TWITTER);
                } catch (Exception ex) {
                    Log.e(LOG_TAG, ex.getMessage());
                }
                Log.e(LOG_TAG, "Facebook callback here?");
                facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    private void ShowLocationPicker() {
        ShowLocationPicker(false, null);
    }

    private void ShowLocationPicker(boolean showWave, Point touchPoint) {
        Log.i(LOG_TAG, "Inside location picker");
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M &&
                ActivityCompat.checkSelfPermission(
                        getActivity(),
                        Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED) {

            if(ActivityCompat.shouldShowRequestPermissionRationale(
                    getActivity(),
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                PopSnackBar(getString(R.string.perm_location_rationale),
                        UpdateService.NONE, R.string.perm_enable,
                        new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable token) {
                                ActivityCompat.requestPermissions(
                                        getActivity(),
                                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                        REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION);
                            }
                        });
            } else {
                new LocationPermissionExplainerDialog()
                        .show(getFragmentManager(), DIALOG_PERM_ACCOUNT_EXPLAINER);
            }
        } else {
            if (showWave) {
                int color = getResources().getColor(R.color.accent);
                WaveCompat.startWaveFilterForResult(this.getActivity(),
                        new WaveDrawable()
                                .setColor(color)
                                .setTouchPoint(touchPoint),
                        addWaveColorToIntent(
                                new Intent(mContext, LocationPicker.class),
                                color),
                        REQUEST_CODE_LOCATION_PICKER);
            } else {
                startActivityForResult(new Intent(
                                mContext,
                                LocationPicker.class),
                        REQUEST_CODE_LOCATION_PICKER);
            }
        }
    }

    private void ShowPickedLocationInfo() {
        if(CurrentPickedLocation != null) {
            mButtonLocationAdd.startAnimation(new AlphaAnimation(1.0f, 0.0f));
            mButtonLocationAdd.setVisibility(View.GONE);

            mWrapperLocation.startAnimation(new AlphaAnimation(0.0f, 1.0f));
            mWrapperLocation.setVisibility(View.VISIBLE);

            mButtonLocationRemove.startAnimation(new AlphaAnimation(0.0f, 1.0f));
            mButtonLocationRemove.setVisibility(View.VISIBLE);

            String locationInfo = "";
            if(CurrentPickedLocation.getName() != null) {
                if(BuildConfig.DEBUG)
                    PopSnackBar(CurrentPickedLocation.getName());

                locationInfo = String.format("%s\n", CurrentPickedLocation.getName());
                if (CurrentPickedLocation.getAddressLine() != null) {
                    locationInfo += CurrentPickedLocation.getAddressLine();
                }
            }

            if(locationInfo != null && locationInfo.length() > 0) {
                mTextLocation.setText(locationInfo);
                mTextLocation.startAnimation(new AlphaAnimation(0.0f, 1.0f));
                mTextLocation.setVisibility(View.VISIBLE);
            }

            SetupFoursquareChip();
        } else {
            PopSnackBar("Error Selecting Location :(");
            mChipFoursquare.setChecked(false);
        }
    }

    private void HidePickedLocation() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                int cx = (mWrapperLocation.getLeft() + mWrapperLocation.getRight()) / 2;
                int cy = (mWrapperLocation.getTop() + mWrapperLocation.getBottom()) / 2;

                int initialRadius = mWrapperLocation.getWidth();

                Animator anim =
                        ViewAnimationUtils.createCircularReveal(
                                mWrapperLocation, cx, cy, initialRadius, 0);

                anim.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        super.onAnimationEnd(animation);
                        mWrapperLocation.setVisibility(View.GONE);

                        mButtonLocationAdd.startAnimation(new AlphaAnimation(0.0f, 1.0f));
                        mButtonLocationAdd.setVisibility(View.VISIBLE);
                    }
                });

                anim.start();
            } else {
                //TODO: Do a honeycomb compatible animation
                mWrapperLocation.startAnimation(new AlphaAnimation(1.0f, 0.0f));
                mWrapperLocation.setVisibility(View.GONE);

                mButtonLocationAdd.startAnimation(new AlphaAnimation(0.0f, 1.0f));
                mButtonLocationAdd.setVisibility(View.VISIBLE);
            }
        } else {
            mWrapperLocation.startAnimation(new AlphaAnimation(1.0f, 0.0f));
            mWrapperLocation.setVisibility(View.GONE);

            mButtonLocationAdd.startAnimation(new AlphaAnimation(0.0f, 1.0f));
            mButtonLocationAdd.setVisibility(View.VISIBLE);
        }
        ClearPickedLocation();
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        if (mPendingUpdate != null)
            outState.putString(PENDING_UPDATE_KEY, mPendingUpdate.toString());
    }

    @Override
    public void onDestroy() {
            super.onDestroy();
    }

    @OnClick(R.id.compose_send_button)
    public void ComposeSendClick() {
        hideKeyboard();
        final String updateText = mComposeText.getText().toString();
        if(updateText.trim().length() > 0) {
            int limit = getMessageLengthLimit();
            int updateLength = getMessageCharacterCount(updateText, -1);
            if (updateLength <= limit) {
                // saveDraft();
                mUpdateText = updateText;
                if(mChipBitly.isChecked() && mLinksInText.size() > 0) {
                    String url = mLinksInText.get(0);
                    String accessToken = mPreferences.bitlyToken().getOr("");
                    if (accessToken != null && accessToken.length() > 0) {
                        if (!url.startsWith("https://") && !url.startsWith("http://"))
                            url = "http://" + url;

                        mBitlyService
                            .createBitmark(accessToken, url, BitlyPrefs.BITLY_DOMAIN_DEFAULT)
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(new Func1<Throwable, ShortenResponse>() {
                                @Override
                                public ShortenResponse call(Throwable throwable) {
                                    PopSnackBar(getString(R.string.error_generic),
                                            UpdateService.SERVICE_BITLY);

                                    ShowRepostNotification(updateText,
                                            MainActivity.REPOST_ALL,
                                            UpdateService.SERVICE_BITLY,
                                            ServiceNotifications.BITLY_NOTIFICATION);

                                    return null;
                                }
                            })
                            .subscribe(new Action1<ShortenResponse>() {
                                @Override
                                public void call(ShortenResponse response) {
                                    assert response != null;
                                    assert response.data != null;
                                    String replacedText =
                                            mUpdateText.replace(
                                                    response.data.getLong_url(),
                                                    response.data.getUrl());
                                    AttemptUpdateAll(replacedText);
                                }
                            });
                    } else {
                        PopSnackBar(
                                getString(R.string.error_shortening_authentication),
                                UpdateService.SERVICE_BITLY,
                                R.string.auth_retry,
                                new SnackBar.OnMessageClickListener() {
                            @Override
                            public void onMessageClick(Parcelable token) {
                                startActivityForResult(
                                        new Intent(mContext, BitlyAuthActivity.class),
                                        BitlyAuthActivity.REQUEST_CODE_BITLY_SIGN_IN);
                            }
                        });
                    }
                } else {
                    AttemptUpdateAll();
                }
            } else {
                PopSnackBar(getString(R.string.alert_update_too_long));
            }
        } else {
            PopSnackBar(getString(R.string.alert_update_too_short));
        }
    }

    private void AttemptUpdateAll() {
        AttemptUpdateAll(null);
    }

    private void AttemptUpdateAll(String update) {
        if(update == null || update.length() == 0) {
            update = mUpdateText;
        }

        UpdateTwitter(update);
        UpdateFacebook(update);
        UpdateFoursquare(update);
        UpdateGooglePlus(update);

        mComposeText.setText("");
        HidePickedLocation();
//        this.getActivity().finish(); // TODO: Consider addressing this with a background service
    }

    private void UpdateFacebook(final String updateText) {
        if(mChipFacebook.isChecked() && MainActivity.mFacebookEnabled) {
            NotificationCompat.Builder mNotificationFacebookBuilder
                    = new NotificationFactory(mContext)
                    .withService(UpdateService.SERVICE_FACEBOOK)
                    .withTitle(getString(R.string.alert_title_posting))
                    .withMessage(String.format(getString(R.string.account_posting), getString(R.string.accounts_facebook)))
                    .withContentIntent(genericPendingIntent)
                    .makeNotification();

            mNotificationManager.notify(
                    ServiceNotifications.FACEBOOK_NOTIFICATION,
                    mNotificationFacebookBuilder.build());

            final JSONObject params = new JSONObject();
            try {
                params.put("message", updateText);
                params.put("description", "Posting from Rápido for Android");
                if(mLinksInText != null && mLinksInText.size() > 0)
                    params.put("link", mLinksInText.get(0));

//                if(mLocationEnabled && CurrentPickedLocation != null) {
                if(CurrentPickedLocation != null) {
                    String placeId = "";
                    Location venueLocation = new Location(MainActivity.currentLocation);
                    venueLocation.setLatitude(CurrentPickedLocation.getLatitude());
                    venueLocation.setLongitude(CurrentPickedLocation.getLongitude());
                    GraphRequest.newPlacesSearchRequest(AccessToken.getCurrentAccessToken(),
                            venueLocation, PLACE_SEARCH_RADIUS, PLACE_SEARCH_COUNT,
                            CurrentPickedLocation.getName(), new GraphRequest.GraphJSONArrayCallback() {
                                @Override
                                public void onCompleted(JSONArray jsonArray, GraphResponse graphResponse) {
                                    if (graphResponse.getError() != null) {
                                        sendFacebookPostRequest(updateText, params);
                                        // TODO: Can't find location...so should we post anyway?
//                                        mBus.post(
//                                                new UpdateEvent(
//                                                        false, graphResponse.getError().getErrorMessage(),
//                                                        UpdateServiceResult.RESULT_FAILURE,
//                                                        updateText,
//                                                        Collections.singletonList(UpdateService.SERVICE_FACEBOOK)));
                                    } else {
                                        try {
                                            JSONObject blah = jsonArray.getJSONObject(0);
                                            params.put("place", blah.getString("id"));
                                            sendFacebookPostRequest(updateText, params);
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }
                                    }
                                }
                            }).executeAsync();
                } else {
                    sendFacebookPostRequest(updateText, params);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    private void UpdateTwitter(String updateText) {
        if(mChipTwitter.isChecked() && MainActivity.mTwitterEnabled) {
            NotificationCompat.Builder mNotificationTwitterBuilder
                = new NotificationFactory(mContext)
                    .withService(SERVICE_TWITTER)
                    .withTitle(getString(R.string.alert_title_posting))
                    .withMessage(String.format(getString(R.string.account_posting), getString(R.string.accounts_twitter)))
                    .withContentIntent(genericPendingIntent)
                    .makeNotification();

            mNotificationManager.notify(
                    ServiceNotifications.TWITTER_NOTIFICATION,
                    mNotificationTwitterBuilder.build());

//            if(mLocationEnabled && CurrentPickedLocation != null) {
            if(CurrentPickedLocation != null) {
                mTwitterTasks.new SearchTwitterPlaces(updateText, CurrentPickedLocation).execute();
            } else {
                mTwitterTasks.new PostStatusUpdate(updateText).execute();
            }
        }
    }

    private void UpdateGooglePlus(String updateText) {
        if(mChipPlus.isChecked() && MainActivity.mPlusEnabled) {
            Intent shareIntent = new PlusShare.Builder(mContext)
                    .setType("text/plain")
                    .setText(updateText)
                    .getIntent();

            startActivityForResult(shareIntent, 0);
        }
    }

    private void UpdateFoursquare(String updateText) {
        if(mChipFoursquare.isChecked() && MainActivity.mFoursquareEnabled) {
            final NotificationCompat.Builder mNotificationFoursquareBuilder
                    = new NotificationFactory(mContext)
                    .withService(UpdateService.SERVICE_FOURSQUARE)
                    .withTitle(getString(R.string.alert_title_posting))
                    .withMessage(String.format(getString(R.string.account_posting), getString(R.string.accounts_foursquare)))
                    .withContentIntent(genericPendingIntent)
                    .makeNotification();

            if(CurrentPickedLocation != null) {
                mNotificationManager.notify(
                        ServiceNotifications.FOURSQUARE_NOTIFICATION,
                        mNotificationFoursquareBuilder.build());
                String accessToken = mPreferences.foursquareToken().getOr("");
                if(accessToken.length() > 0) {
                    final String finalUpdateText = updateText;
                    mSwarmService
                            .postUserCheckinWithShout(
                                    "Non est corpus.",
                                    accessToken,
                                    CurrentPickedLocation.getFoursquareVenueId(),
                                    finalUpdateText,
                                    "",
                                    String.format("%s,%s",
                                            CurrentPickedLocation.getLatitude(),
                                            CurrentPickedLocation.getLongitude()))
                            .subscribeOn(Schedulers.io())
                            .observeOn(AndroidSchedulers.mainThread())
                            .onErrorReturn(new Func1<Throwable, PostUserCheckinResponse>() {
                                @Override
                                public PostUserCheckinResponse call(Throwable throwable) {
                                    PopSnackBar(getString(R.string.error_generic),
                                            UpdateService.SERVICE_FOURSQUARE);

                                    ShowRepostNotification(finalUpdateText,
                                            MainActivity.REPOST_FOURSQUARE,
                                            UpdateService.SERVICE_FOURSQUARE,
                                            ServiceNotifications.FOURSQUARE_NOTIFICATION);

                                    return null;
                                }
                            })
                            .subscribe(new Action1<PostUserCheckinResponse>() {
                                @Override
                                public void call(PostUserCheckinResponse response) {
                                    assert response != null;
                                    if (response.meta.code == 200) {
                                        PopSnackBar(String.format(
                                                        getResources().getString(R.string.alert_update_posted),
                                                        getResources().getString(R.string.accounts_foursquare)),
                                                UpdateService.SERVICE_FOURSQUARE);

                                        String updateSuccessMessage =
                                                String.format(getResources().getString(R.string.alert_update_posted), getResources().getString(R.string.accounts_foursquare));

                                        Notification foursquareNotification =
                                                new NotificationFactory(mContext)
                                                        .withService(SERVICE_FOURSQUARE)
                                                        .withMessage(updateSuccessMessage)
                                                        .withLowPriority()
                                                        .buildNotification();

                                        mNotificationManager.notify(
                                                ServiceNotifications.FOURSQUARE_NOTIFICATION,
                                                foursquareNotification);
                                        mNotificationManager.cancel(ServiceNotifications.FOURSQUARE_NOTIFICATION);
                                    } else {
                                        PopSnackBar(
                                                String.format(
                                                        getString(R.string.error_template),
                                                        getString(R.string.error_checkin) + "Response Code:" + response.meta.code),
                                                UpdateService.SERVICE_FOURSQUARE);

                                        ShowRepostNotification(finalUpdateText,
                                                MainActivity.REPOST_FOURSQUARE,
                                                UpdateService.SERVICE_FOURSQUARE,
                                                ServiceNotifications.FOURSQUARE_NOTIFICATION);
                                    }
                                }
                            });
                }
            }
        }
    }

    private void sendFacebookPostRequest(final String updateText, JSONObject params) {
        GraphRequest.newPostRequest(AccessToken.getCurrentAccessToken(),
                "me/feed", params, new GraphRequest.Callback() {
                    @Override
                    public void onCompleted(GraphResponse graphResponse) {
                        if (graphResponse.getError() != null) {
                            mBus.post(
                                    new UpdateEvent(
                                            false, graphResponse.getError().getErrorMessage(),
                                            UpdateServiceResult.RESULT_FAILURE,
                                            updateText,
                                            Collections.singletonList(UpdateService.SERVICE_FACEBOOK)));
                        } else {
                            mBus.post(
                                    new UpdateEvent(
                                            true, "",
                                            UpdateServiceResult.RESULT_SUCCESS,
                                            updateText,
                                            Collections.singletonList(UpdateService.SERVICE_FACEBOOK)));
                        }
                    }
                }).executeAsync();
    }

    private void ShowRepostNotification(String updateText,
                                        String updateServiceRepost,
                                        UpdateService updateService,
                                        int updateServiceNotification) {
        Intent repost = new Intent(mContext, MainActivity.class);
        repost.putExtra(
                Intent.EXTRA_TEXT,
                String.format("%s %s",
                        updateServiceRepost,
                        updateText));
        TaskStackBuilder stackBuilder =
                TaskStackBuilder.create(mContext);
        stackBuilder.addParentStack(MainActivity.class);
        stackBuilder.addNextIntent(repost);
        PendingIntent repostPendingIntent =
                stackBuilder.getPendingIntent(
                        0,
                        PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder mNotificationError =
                new NotificationFactory(mContext)
                        .withService(updateService)
                        .withMessage(updateText)
                        .makeNotification()
                        .setAutoCancel(true);

        mNotificationError.addAction(
                R.drawable.ic_stat_refresh,
                getString(R.string.post_retry),
                repostPendingIntent);

        mNotificationManager.notify(
                updateServiceNotification,
                mNotificationError.build());
    }

    public void onEvent(BitlyAuthEvent event) {
        if(event.result) {
            String username = mPreferences.bitlyUsername().getOr("bitly");
            PopSnackBar(String.format(getString(R.string.account_connected), username), UpdateService.SERVICE_BITLY);
        } else {
            PopSnackBar(
                    String.format(
                            getString(R.string.account_authentication_error),
                            getString(R.string.accounts_bitly)),
                    UpdateService.SERVICE_BITLY);
        }
    }

    public void onEvent(ShortenedUrlEvent event) {
        assert event != null;
        if(event.result) {
            String replacedText =
                    mUpdateText.replace(event.longUrl, event.bitmark.getUrl());
            UpdateTwitter(replacedText);
            UpdateFacebook(replacedText);
            UpdateGooglePlus(replacedText);

            // TODO: Handle multiple urls...do I only shorten one?
        }
    }

    public void onEvent(UpdateEvent event) {
        // TODO: Handle Updates being finished?
        if(event.updateServices != null) {
            for (UpdateService service : event.updateServices) {
                switch(service) {
                    case SERVICE_TWITTER:
                        if(event.result) {
                            String updateSuccessMessage = String.format(getResources().getString(R.string.alert_update_posted), getResources().getString(R.string.accounts_twitter));

                            if(this.getActivity().getWindow().isActive())
                                PopSnackBar(updateSuccessMessage, service);

                            Notification twitterNotification =
                                    new NotificationFactory(mContext)
                                    .withService(SERVICE_TWITTER)
                                    .withMessage(updateSuccessMessage)
                                    .withLowPriority()
                                    .buildNotification();

                            mNotificationManager.notify(ServiceNotifications.TWITTER_NOTIFICATION, twitterNotification);
                            mNotificationManager.cancel(ServiceNotifications.TWITTER_NOTIFICATION);
                        } else {
                            String snackBarMessage = getString(R.string.error_posting_update);
                            switch (event.updateServiceResult) {
                                case RESULT_DUPLICATE:
                                    snackBarMessage = getString(R.string.error_duplicate_message);

                                    NotificationCompat.Builder mTwitterErrorDuplcate =
                                            new NotificationFactory(mContext)
                                                    .withService(SERVICE_TWITTER)
                                                    .withMessage(snackBarMessage)
                                                    .makeNotification()
                                                    .setAutoCancel(true);

                                    mNotificationManager.notify(
                                            ServiceNotifications.TWITTER_NOTIFICATION,
                                            mTwitterErrorDuplcate.build());
                                    break;
                                default:
                                    ShowRepostNotification(event.updateText,
                                            MainActivity.REPOST_TWITTER,
                                            UpdateService.SERVICE_TWITTER,
                                            ServiceNotifications.TWITTER_NOTIFICATION);
                                    break;
                            }

                            if(this.getActivity().getWindow().isActive())
                                PopSnackBar(snackBarMessage, service);
                        }
                        break;

                    case SERVICE_FACEBOOK:
                        if(event.result) {
                            String updateSuccessMessage =
                                    String.format(
                                            getResources().getString(R.string.alert_update_posted),
                                            getResources().getString(R.string.accounts_facebook));

                            if(this.getActivity().getWindow().isActive())
                                PopSnackBar(updateSuccessMessage, service);

                            Notification facebookNotification =
                                    new NotificationFactory(mContext)
                                    .withService(UpdateService.SERVICE_FACEBOOK)
                                    .withMessage(updateSuccessMessage)
                                    .withLowPriority()
                                    .buildNotification();

                            mNotificationManager.notify(
                                    ServiceNotifications.FACEBOOK_NOTIFICATION,
                                    facebookNotification);
                            mNotificationManager.cancel(ServiceNotifications.FACEBOOK_NOTIFICATION);
                        } else {
                            String snackBarMessage = getString(R.string.error_posting_update);
                            switch (event.updateServiceResult) {
                                case RESULT_DUPLICATE:
                                    snackBarMessage = getString(R.string.error_duplicate_message);

                                    NotificationCompat.Builder mFacebookErrorDuplicate =
                                            new NotificationFactory(mContext)
                                                    .withService(SERVICE_FACEBOOK)
                                                    .withMessage(snackBarMessage)
                                                    .makeNotification()
                                                    .setAutoCancel(true);

                                    mNotificationManager.notify(
                                            ServiceNotifications.FACEBOOK_NOTIFICATION,
                                            mFacebookErrorDuplicate.build());

                                    break;
                                default:
                                    ShowRepostNotification(event.updateText,
                                            MainActivity.REPOST_FACEBOOK,
                                            UpdateService.SERVICE_FACEBOOK,
                                            ServiceNotifications.FACEBOOK_NOTIFICATION);
                                    break;
                            }

                            if(this.getActivity().getWindow().isActive())
                                PopSnackBar(snackBarMessage, service);
                        }
                        break;
                    default:
                        PopSnackBar("Update failed? :)");
                }
            }
        }
    }

    private void hideKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.hideSoftInputFromWindow(mComposeText.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
    }

    private void showKeyboard() {
        InputMethodManager inputMethodManager =
                (InputMethodManager) mContext.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethodManager.showSoftInput(mComposeText, InputMethodManager.SHOW_IMPLICIT);
    }

//    @OnClick(R.id.compose_to_text)
//    public void ClickTo() {
//
//    }

//    @OnLongClick(R.id.compose_to_text)
//    public boolean ToLongClick() {
//        EnableLocation();
//        return true;
//    }
//
//    private void EnableLocation() {
//        if(mLocationEnabled) {
//            ClearPickedLocation();
//            mButtonLocationAdd.setVisibility(View.GONE);
//            mChipFoursquare.setVisibility(View.GONE);
//            mLocationEnabled = false;
//        } else {
//            mButtonLocationAdd.setVisibility(View.VISIBLE);
//            mChipFoursquare.setVisibility(View.VISIBLE);
//            mLocationEnabled = true;
//        }
//    }

    @OnClick(R.id.compose_remove_location)
    public void ClickRemoveLocation() {
        HidePickedLocation();
    }

    public void ClearPickedLocation() {
        CurrentPickedLocation = null;
        mPendingUpdate.location = null;
        SetupFoursquareChip();
    }

    @Override
    public void onWaveTouchUp(View view, Point locationInView, Point locationInScreen) {
        Log.i(LOG_TAG, "Inside onWaveTouchUp");
        ShowLocationPicker(true, locationInScreen);
    }

    private Intent addWaveColorToIntent(Intent intent, int color) {
        intent.putExtra(WaveCompat.IntentKey.BACKGROUND_COLOR, color);
        return intent;
    }

    public void setData(UpdateEvent data) {
        this.mUpdateEventData = data;
    }

    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions,
                                           int[] grantResults) {
        if(requestCode == REQUEST_CODE_PERMISSION_GET_ACCOUNT) {
            Log.i(LOG_TAG, "Inside GET_ACCOUNT Permission response");
            if(grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startActivityForResult(
                        new Intent(mContext, GooglePlusAuthActivity.class),
                        GooglePlusAuthActivity.REQUEST_CODE_GOOGLE_SIGN_IN);
            } else {
                PopSnackBar(
                        String.format(
                                getString(R.string.perm_denied),
                                getString(R.string.perm_get_accounts)));
            }
        } else if(requestCode == REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION) {
            Log.i(LOG_TAG, "Inside ACCESS_FINE_LOCATION Permission response");
            if (grantResults.length == 1 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.i(LOG_TAG, "Permission Granted, launch LocationPicker");
                mButtonLocationAdd.performClick();
            } else {
                Log.i(LOG_TAG, "Else permission denied...");
                PopSnackBar(
                        String.format(
                                getString(R.string.perm_denied),
                                getString(R.string.perm_get_accounts)));
            }
        }
    }

    public static class GooglePlusPermissionExplainerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            return
                    new AlertDialog.Builder(this.getActivity())
                            .setView(inflater.inflate(R.layout.dialog_permissions_account, null))
                            .setPositiveButton(android.R.string.ok,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialogInterface, int i) {
                                            ActivityCompat.requestPermissions(
                                                    getActivity(),
                                                    new String[]{Manifest.permission.GET_ACCOUNTS},
                                                    REQUEST_CODE_PERMISSION_GET_ACCOUNT);
                                        }
                                    })
                            .create();
        }
    }

    public static class LocationPermissionExplainerDialog extends DialogFragment {
        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            LayoutInflater inflater = getActivity().getLayoutInflater();
            return
                    new AlertDialog.Builder(this.getActivity())
                            .setView(inflater.inflate(R.layout.dialog_permissions_location, null))
                            .setPositiveButton(R.string.perm_enable,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(
                                                DialogInterface dialogInterface, int i) {
                                            ActivityCompat.requestPermissions(
                                                    getActivity(),
                                                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                                    REQUEST_CODE_PERMISSION_ACCESS_FINE_LOCATION);
                                        }
                                    })
                            .setNegativeButton(android.R.string.cancel,
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialogInterface, int i) {
                                            // Do Nothing
                                        }
                                    })
                            .create();
        }
    }
}