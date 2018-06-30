package ke.go.nyandarua.nyantalk.fragment;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.app.ReusableFragmentActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.Response;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.activity.EditTicketActivity;
import ke.go.nyandarua.nyantalk.activity.MainActivity;
import ke.go.nyandarua.nyantalk.activity.RecoverPasswordActivity;
import ke.go.nyandarua.nyantalk.activity.WelcomeActivity;
import ke.go.nyandarua.nyantalk.model.CreateTicketData;
import ke.go.nyandarua.nyantalk.model.Department;
import ke.go.nyandarua.nyantalk.model.SubCounty;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.model.Ward;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class SignInFragment extends BaseFragment {


    @BindView(R.id.signInIdMET)
    MaterialEditText signInIdMET;
    @BindView(R.id.passwordMET)
    MaterialEditText passwordMET;
    Unbinder unbinder;

    public SignInFragment() {
        // Required empty public constructor
    }

    public static SignInFragment newInstance() {

        Bundle args = new Bundle();

        SignInFragment fragment = new SignInFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_sign_in, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        BaseUtils.cacheInput(signInIdMET, R.string.pref_phone, PrefUtils.getInstance());
        BaseUtils.cacheInput(passwordMET, R.string.pref_password, PrefUtils.getInstance());
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.signInBtn)
    public void onSignInBtnClicked() {
        String signInId = signInIdMET.getText().toString();
        String password = passwordMET.getText().toString();
        if (TextUtils.isEmpty(signInId)) {
            toast(R.string.error_sign_in_id);
        } else if (TextUtils.isEmpty(password)) {
            toast(R.string.error_password);
        } else {
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.SIGN_IN_ID, signInId);
            params.put(BackEnd.Params.PASSWORD, password);
            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_SIGN_IN);
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new SignInCallback()));
        }
    }

    private class SignInCallback extends Callback<User> {

        private SignInCallback() {
            super((BaseAppActivity) SignInFragment.this.getActivity(), User.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onSignInBtnClicked();
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(getActivity());
        }


        @Override
        protected void onFailure(int statusCode, JSONObject response) {
            if (response != null && statusCode == 400){
                try {
                    if (response.has(Response.META)){
                        JSONObject meta = response.getJSONObject(Response.META);
                        if (meta.has(Response.CODE)){
                            String code = meta.getString(Response.CODE);
                            if (code.equals(BackEnd.Errors.PHONE_NOT_VERIFIED)){
                                String phone = response.getJSONObject(Response.DATA).getString(BackEnd.Params.PHONE);
                                PrefUtils.getInstance().writeString(R.string.pref_phone, phone);
                                PrefUtils.getInstance().writeBoolean(R.string.pref_pending_phone_verification, true);
                                toast(meta.getString(Response.MESSAGE));
                                ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                                        CodeVerificationFragment.newInstance(), getString(R.string.verify_phone_number));
                                getActivity().finish();
                            } else {
                                super.onFailure(statusCode, response);
                            }
                        } else {
                            super.onFailure(statusCode, response);
                        }
                    } else {
                        super.onFailure(statusCode, response);
                    }
                } catch (JSONException e){
                    e.printStackTrace();
                    super.onFailure(statusCode, response);
                }
            } else {
                super.onFailure(statusCode, response);
            }

        }
    }

    @OnClick(R.id.signUpBtn)
    public void onSignUpBtnClicked() {
        ReusableFragmentActivity.start((AppCompatActivity) Objects.requireNonNull(getActivity()),
                SignUpFragment.newInstance(), getString(R.string.sign_up));
    }

    @OnClick(R.id.forgotPasswordBtn)
    public void onForgotPasswordBtn() {
        RecoverPasswordActivity.start(getActivity());
    }
}
