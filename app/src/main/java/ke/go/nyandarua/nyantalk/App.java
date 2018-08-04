/*
 * Copyright (c) 2016. VibeCampo Social Network
 *
 * Website : http://www.vibecampo.com
 */

package ke.go.nyandarua.nyantalk;

import android.app.Application;
import android.support.multidex.MultiDexApplication;

import org.json.JSONObject;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.DefaultResponseDefinition;
import ke.co.toshngure.basecode.rest.ResponseDefinition;
import ke.co.toshngure.logging.BeeLog;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;

public class App extends MultiDexApplication {

    private static final String TAG = App.class.getSimpleName();

    private static App mInstance;

    public static App getInstance() {
        return mInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        BeeLog.init(BuildConfig.DEBUG, null);

        mInstance = this;

        configureClient();

    }

    private void configureClient() {
        Client.init(new Client.Config() {
            @Override
            protected Application getContext() {
                return App.this;
            }

            @Override
            protected String getBaseUrl() {
                return BackEnd.BASE_URL;
            }

            @Override
            protected String getToken() {
                User user = PrefUtils.getInstance().getUser();
                if (user != null){
                    return user.getToken();
                }
                return null;
            }

            @Override
            protected ResponseDefinition getResponseDefinition() {
                return new DefaultResponseDefinition();
            }

            @Override
            protected void onReportError(JSONObject response) {
                super.onReportError(response);
            }

            @Override
            public boolean enableRedirects() {
                return false;
            }

            @Override
            public boolean enableRelativeRedirects() {
                return false;
            }
        });
    }
}
