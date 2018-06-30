package ke.go.nyandarua.nyantalk.fragment.password;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.stepstone.stepper.BlockingStep;
import com.stepstone.stepper.StepperLayout;
import com.stepstone.stepper.VerificationError;

import org.json.JSONObject;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.Unbinder;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.BaseFragment;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;


/**
 * A simple {@link Fragment} subclass.
 */
public class PasswordRecoveryFragment extends BaseFragment implements BlockingStep {


    @BindView(R.id.phoneET)
    EditText phoneET;
    Unbinder unbinder;

    public PasswordRecoveryFragment() {
        // Required empty public constructor
    }

    public static PasswordRecoveryFragment newInstance() {

        Bundle args = new Bundle();

        PasswordRecoveryFragment fragment = new PasswordRecoveryFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_password_recovery, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //BaseUtils.cacheInput(phoneET, R.string.pref_phone, PrefUtils.getInstance());

    }

    @Nullable
    @Override
    public VerificationError verifyStep() {
        String phone = phoneET.getText().toString();
        String errorMessage;

        if (TextUtils.isEmpty(phone) || !Patterns.PHONE.matcher(phone).matches() || String.valueOf(phone).length() != 10) {
            errorMessage = getString(R.string.error_phone);
        } else {
            errorMessage = null;
        }

        if (TextUtils.isEmpty(errorMessage)) {
            return null;
        } else {
            return new VerificationError(errorMessage);
        }
    }

    @Override
    public void onSelected() {

    }

    @Override
    public void onError(@NonNull VerificationError error) {
        Snackbar.make(phoneET, error.getErrorMessage(), Snackbar.LENGTH_INDEFINITE)
                .setAction(android.R.string.ok, v -> {

                }).show();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @Override
    public void onNextClicked(StepperLayout.OnNextClickedCallback callback) {
        if (verifyStep() != null){
            onError(verifyStep());
        } else {
            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH_RECOVER_PASSWORD);
            RequestParams params = new RequestParams(BackEnd.Params.PHONE, phoneET.getText().toString());
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new RecoverPasswordCallback(callback)));
        }
    }

    @Override
    public void onCompleteClicked(StepperLayout.OnCompleteClickedCallback callback) {

    }

    @Override
    public void onBackClicked(StepperLayout.OnBackClickedCallback callback) {

    }

    private class RecoverPasswordCallback extends Callback<User> {

        private StepperLayout.OnNextClickedCallback callback;

        RecoverPasswordCallback(StepperLayout.OnNextClickedCallback callback) {
            super((BaseAppActivity) getActivity(), User.class);
            this.callback = callback;
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onNextClicked(callback);
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            PrefUtils.getInstance().writeString(R.string.pref_phone, item.getPhone());
            callback.goToNextStep();
        }
    }
}
