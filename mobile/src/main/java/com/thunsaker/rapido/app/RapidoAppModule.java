package com.thunsaker.rapido.app;

import android.content.Context;
import android.support.v4.app.NotificationManagerCompat;

import com.crashlytics.android.Crashlytics;
import com.google.common.eventbus.EventBus;
import com.thunsaker.android.common.annotations.ForApplication;
import com.thunsaker.android.common.dagger.AndroidApplicationModule;
import com.thunsaker.rapido.BuildConfig;
import com.thunsaker.rapido.RapidoPrefsManager;
import com.thunsaker.rapido.services.AuthHelper;
import com.thunsaker.rapido.services.BitlyPrefs;
import com.thunsaker.rapido.services.BitlyService;
import com.thunsaker.rapido.services.TwitterTasks;
import com.thunsaker.rapido.services.foursquare.FoursquarePrefs;
import com.thunsaker.rapido.services.foursquare.FoursquareService;
import com.thunsaker.rapido.services.foursquare.SwarmService;
import com.thunsaker.rapido.ui.BitlyAuthActivity;
import com.thunsaker.rapido.ui.GooglePlusAuthActivity;
import com.thunsaker.rapido.ui.LocationPicker;
import com.thunsaker.rapido.ui.MainActivity;
import com.thunsaker.rapido.ui.MainFragment;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import io.fabric.sdk.android.Fabric;
import retrofit.RequestInterceptor;
import retrofit.RestAdapter;
import twitter4j.TwitterFactory;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;

@Module(
        complete = true,
        library = true,
        addsTo = AndroidApplicationModule.class,
        injects = {
                RapidoApp.class,
                MainActivity.class,
                MainFragment.class,
                GooglePlusAuthActivity.class,
                TwitterTasks.class,
                NotificationFactory.class,
                BitlyAuthActivity.class,
                LocationPicker.class
        }
)

public class RapidoAppModule {
    public RapidoAppModule() {}

    @Provides
    @Singleton
    EventBus provideBus() {
        return new EventBus();
    }

    @Provides
    @Singleton
    RapidoPrefsManager providesRapidoPrefsManager(@ForApplication Context mContext) {
        return new RapidoPrefsManager(mContext.getSharedPreferences("rapido_prefs", Context.MODE_PRIVATE));
    }

    @Provides
    @Singleton
    Fabric providesFabric(@ForApplication Context mContext) {
        TwitterAuthConfig authConfig = new TwitterAuthConfig(AuthHelper.TWITTER_KEY, AuthHelper.TWITTER_SECRET);
        return Fabric.with(mContext, new Twitter(authConfig), new Crashlytics());
    }

    @Provides
    @Singleton
    TwitterTasks providesTwitterTasks(@ForApplication Context mContext) {
        return new TwitterTasks((RapidoApp)mContext);
    }

    @Provides
    @Singleton
    twitter4j.Twitter providesTwitter4j() {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        builder.setOAuthConsumerKey(AuthHelper.TWITTER_KEY);
        builder.setOAuthConsumerSecret(AuthHelper.TWITTER_SECRET);
        Configuration configuration = builder.build();
        TwitterFactory factory = new TwitterFactory(configuration);
        return factory.getInstance();
    }

    @Provides
    @Singleton
    NotificationManagerCompat providesNotificationManager(@ForApplication Context mContext) {
        return NotificationManagerCompat.from(mContext);
    }

    @Provides
    @Singleton
    BitlyService providesBitlyService() {
        final RequestInterceptor requestInterceptor = new RequestInterceptor() {

            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("format", "json");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(BitlyPrefs.BITLY_BASE_URL)
                .setRequestInterceptor(requestInterceptor)
                .setLogLevel(RestAdapter.LogLevel.FULL)
                .build();

        return restAdapter.create(BitlyService.class);
    }

    @Provides
    @Singleton
    FoursquareService providesFoursquareService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("v", FoursquarePrefs.CURRENT_API_DATE);
                request.addQueryParam("m", FoursquarePrefs.API_MODE_FOURSQUARE);
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(FoursquarePrefs.FOURSQUARE_BASE_URL)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(FoursquareService.class);
    }

    @Provides
    @Singleton
    SwarmService providesSwarmService() {
        RequestInterceptor requestInterceptor = new RequestInterceptor() {
            @Override
            public void intercept(RequestFacade request) {
                request.addQueryParam("v", FoursquarePrefs.CURRENT_API_DATE);
                request.addQueryParam("m", FoursquarePrefs.API_MODE_SWARM);
//                if(BuildConfig.DEBUG)
//                    request.addQueryParam("broadcast", "private");
            }
        };

        RestAdapter restAdapter = new RestAdapter.Builder()
                .setEndpoint(FoursquarePrefs.FOURSQUARE_BASE_URL)
                .setLogLevel(BuildConfig.DEBUG ? RestAdapter.LogLevel.FULL : RestAdapter.LogLevel.NONE)
                .setRequestInterceptor(requestInterceptor)
                .build();

        return restAdapter.create(SwarmService.class);
    }
}