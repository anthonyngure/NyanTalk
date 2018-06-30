package ke.go.nyandarua.nyantalk.fragment;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

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
import ke.co.toshngure.logging.BeeLog;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.BaseActivity;
import ke.go.nyandarua.nyantalk.model.SubCounty;
import ke.go.nyandarua.nyantalk.model.User;
import ke.go.nyandarua.nyantalk.model.Ward;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.PrefUtils;
import ke.go.nyandarua.nyantalk.utils.Utils;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileEditFragment extends BaseFragment {

    private static final String TAG = "SignUpFragment";

    @BindView(R.id.nameMET)
    MaterialEditText nameMET;
    @BindView(R.id.phoneMET)
    MaterialEditText phoneMET;
    @BindView(R.id.subCountyMS)
    MaterialSpinner subCountyMS;
    @BindView(R.id.wardMS)
    MaterialSpinner wardMS;
    @BindView(R.id.emailMET)
    MaterialEditText emailMET;
    @BindView(R.id.updateBtn)
    Button updateBtn;
    Unbinder unbinder;
    @BindView(R.id.smsNotificationsEnabledCB)
    CheckBox smsNotificationsEnabledCB;
    private List<SubCounty> mSubCounties;
    private SubCounty mSelectedSubCounty;
    private Ward mSelectedWard;

    public ProfileEditFragment() {
        // Required empty public constructor
    }

    public static ProfileEditFragment newInstance() {

        Bundle args = new Bundle();

        ProfileEditFragment fragment = new ProfileEditFragment();
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile_edit, container, false);
        unbinder = ButterKnife.bind(this, view);
        return view;
    }

    private void getData() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.SUB_COUNTIES);
        RequestParams params = new RequestParams();
        params.put(BackEnd.Params.INCLUDE, "wards");
        Client.getInstance().getClient().get(url, params, new ResponseHandler(new SubCountiesCallBack()));
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        getData();
        User user = PrefUtils.getInstance().getUser();
        nameMET.setText(user.getName());
        phoneMET.setText(Utils.removePhoneCode(user.getPhone()));
        emailMET.setText(user.getEmail());


        //BaseUtils.cacheInput(nameMET, R.string.pref_name, PrefUtils.getInstance());
        //BaseUtils.cacheInput(phoneMET, R.string.pref_phone, PrefUtils.getInstance());
        //BaseUtils.cacheInput(emailMET, R.string.pref_email, PrefUtils.getInstance());
        smsNotificationsEnabledCB.setChecked(user.isSmsNotifiable());
    }

    private class SubCountiesCallBack extends Callback<SubCounty> {

        private SubCountiesCallBack() {
            super((BaseAppActivity) getActivity(), SubCounty.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            getData();
        }

        @Override
        protected void onResponse(List<SubCounty> items) {
            super.onResponse(items);
            mSubCounties = items;
            initSpinners();
        }
    }

    private void initSpinners() {
        List<String> subCountyNames = new ArrayList<>();
        for (SubCounty subCounty : mSubCounties) {
            subCountyNames.add(subCounty.getName());
        }
        subCountyMS.setItems(subCountyNames);
        subCountyMS.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            for (SubCounty subCounty : mSubCounties) {
                String name = subCountyNames.get(position);
                if (subCounty.getName().equals(name)) {
                    mSelectedSubCounty = subCounty;
                    updateWardsMS();
                    break;
                }
            }
        });

        /*User user = PrefUtils.getInstance().getUser();
        if (user.getWard() != null && user.getWard().getSubCounty() != null){
            mSelectedSubCounty = user.getWard().getSubCounty();
            mSelectedSubCounty.
            updateWardsMS();
        }*/


    }

    private void updateWardsMS() {
        if (mSelectedSubCounty == null) {
            wardMS.setEnabled(false);
        } else {
            List<String> wardNames = new ArrayList<>();
            BeeLog.i(TAG, String.valueOf(mSelectedSubCounty));
            for (Ward ward : mSelectedSubCounty.wards) {
                wardNames.add(ward.getName());
            }
            wardMS.setItems(wardNames);
            wardMS.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
                for (Ward ward : mSelectedSubCounty.wards) {
                    String name = wardNames.get(position);
                    if (ward.getName().equals(name)) {
                        mSelectedWard = ward;
                        break;
                    }
                }
            });
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }

    @OnClick(R.id.updateBtn)
    public void onUpdateBtnClicked() {
        String name = nameMET.getText().toString();
        String phone = phoneMET.getText().toString();

        if (mSelectedSubCounty == null) {
            toast(R.string.error_sub_county);
        } else if (mSelectedWard == null) {
            toast(R.string.error_ward);
        } else if (TextUtils.isEmpty(name)) {
            toast(R.string.error_name);
        } else if (TextUtils.isEmpty(phone) || !TextUtils.isDigitsOnly(phone)) {
            toast(R.string.error_phone);
        } else {
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.SUB_COUNTY_ID, mSelectedSubCounty.getId());
            params.put(BackEnd.Params.WARD_ID, mSelectedWard.getId());
            params.put(BackEnd.Params.NAME, name);
            params.put(BackEnd.Params.PHONE, phone);
            params.put(BackEnd.Params.EMAIL, emailMET.getText().toString());
            params.put(BackEnd.Params.SMS_NOTIFICATIONS_ENABLED, smsNotificationsEnabledCB.isChecked() ? 1 : 2);

            String url = Client.absoluteUrl(BackEnd.EndPoints.AUTH);
            Client.getInstance().getClient().put(url, params, new ResponseHandler(new SignUpCallback()));
        }
    }


    private class SignUpCallback extends Callback<User> {

        private SignUpCallback() {
            super((BaseAppActivity) getActivity(), User.class);
        }

        @Override
        protected void onResponse(User item) {
            super.onResponse(item);
            User user = PrefUtils.getInstance().getUser();
            item.setToken(user.getToken());
            PrefUtils.getInstance().saveUser(item);
            Utils.startMainActivity(getActivity());
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onUpdateBtnClicked();
        }
    }
}
