package ke.go.nyandarua.nyantalk.fragment.password;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.telecom.Call;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.loopj.android.http.RequestParams;
import com.stepstone.stepper.VerificationError;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;
import ke.co.toshngure.basecode.app.BaseAppActivity;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.views.ToshButton;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.BaseFragment;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.network.BackEnd;


/**
 * A simple {@link Fragment} subclass.
 */
public class ChangePasswordFragment extends BaseFragment {


    @BindView(R.id.submitBtn)
    ToshButton submitBtn;
    @BindView(R.id.currentPasswordET)
    EditText currentPasswordET;
    @BindView(R.id.newPasswordET)
    EditText newPasswordET;
    @BindView(R.id.confirmNewPasswordET)
    EditText confirmNewPasswordET;
    Unbinder unbinder;

    public ChangePasswordFragment() {
        // Required empty public constructor
    }

    public static ChangePasswordFragment newInstance() {

        Bundle args = new Bundle();

        ChangePasswordFragment fragment = new ChangePasswordFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_change_password, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.submitBtn)
    public void onSubmitBtnClicked() {

        String currentPassword = currentPasswordET.getText().toString();
        String newPassword = newPasswordET.getText().toString();
        String confirmedPassword = confirmNewPasswordET.getText().toString();

        String errorMessage;
        if (TextUtils.isEmpty(currentPassword)) {
            errorMessage = getString(R.string.error_current_password);
        } else if (TextUtils.isEmpty(confirmedPassword)) {
            errorMessage = getString(R.string.error_confirm_password);
        } else if (!newPassword.equals(confirmedPassword)) {
            errorMessage = getString(R.string.error_password_match);
        } else {
            errorMessage = null;
        }


        if (!TextUtils.isEmpty(errorMessage)) {
            Snackbar.make(newPasswordET, errorMessage, Snackbar.LENGTH_INDEFINITE)
            .setAction(android.R.string.ok, v -> {

            }).show();
        } else {
            String url =Client.absoluteUrl(BackEnd.EndPoints.AUTH_CHANGE_PASSWORD);
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.CURRENT_PASSWORD, currentPasswordET.getText().toString());
            params.put(BackEnd.Params.NEW_PASSWORD, newPasswordET.getText().toString());
            params.put(BackEnd.Params.NEW_PASSWORD_CONFIRMATION, confirmNewPasswordET.getText().toString());
            Client.getInstance().getClient().post(url,params, new ResponseHandler(new ChangePasswordCallback()));
        }
    }


    private class ChangePasswordCallback extends Callback<User> {

        private ChangePasswordCallback() {
            super((BaseAppActivity) getActivity(), User.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onSubmitBtnClicked();
        }


        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            toast(R.string.password_changed_successfully);
            getActivity().finish();
        }
    }

}
