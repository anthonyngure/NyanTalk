package ke.go.nyandarua.nyantalk.activity;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.jaredrummler.materialspinner.MaterialSpinner;
import com.loopj.android.http.RequestParams;
import com.rengwuxian.materialedittext.MaterialEditText;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.toshngure.basecode.rest.Callback;
import ke.co.toshngure.basecode.rest.Client;
import ke.co.toshngure.basecode.rest.ResponseHandler;
import ke.co.toshngure.camera.ImagePicker;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.fragment.TicketsFragment;
import ke.go.nyandarua.nyantalk.model.CreateTicketData;
import ke.go.nyandarua.nyantalk.model.Department;
import ke.go.nyandarua.nyantalk.model.SubCounty;
import ke.go.nyandarua.nyantalk.model.Ticket;
import ke.go.nyandarua.nyantalk.model.Ward;
import ke.go.nyandarua.nyantalk.network.BackEnd;
import ke.go.nyandarua.nyantalk.utils.Extras;

public class EditTicketActivity extends BaseActivity {

    private static final int IMAGE_REQUEST_CODE = 100;
    @BindView(R.id.subjectMET)
    MaterialEditText subjectMET;
    @BindView(R.id.detailsMET)
    MaterialEditText detailsMET;
    @BindView(R.id.subCountyMS)
    MaterialSpinner subCountyMS;
    @BindView(R.id.wardMS)
    MaterialSpinner wardMS;
    @BindView(R.id.departmentMS)
    MaterialSpinner departmentMS;
    @BindView(R.id.imageIP)
    ImagePicker imageIP;
    @BindView(R.id.toolbar)
    Toolbar toolbar;
    @BindView(R.id.submitBtn)
    Button submitBtn;
    @BindView(R.id.permissionTV)
    TextView permissionTV;
    private CreateTicketData mCreateTicketData;
    private SubCounty mSelectedSubCounty;
    private Ward mSelectedWard;
    private Department mSelectedDepartment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_ticket);
        ButterKnife.bind(this);
        getData();
        updatePermissionsUI();

    }

    private void getData() {
        String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS_CREATE);
        Client.getInstance().getClient().get(url, new ResponseHandler(new CreateTicketDataCallback()));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        imageIP.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.submitBtn)
    public void onSubmitBtnClicked() {
        String subject = subjectMET.getText().toString();
        String details = detailsMET.getText().toString();
        if (TextUtils.isEmpty(subject)) {
            toast(R.string.error_subject);
        } else if (TextUtils.isEmpty(details)) {
            toast(R.string.error_details);
        } else if (mSelectedSubCounty == null) {
            toast(R.string.error_sub_county);
        } else if (mSelectedWard == null) {
            toast(R.string.error_ward);
        } else if (mSelectedDepartment == null) {
            toast(R.string.error_department);
        } else {
            RequestParams params = new RequestParams();
            params.put(BackEnd.Params.SUBJECT, subject);
            params.put(BackEnd.Params.DETAILS, details);
            params.put(BackEnd.Params.SUB_COUNTY_ID, mSelectedSubCounty.getId());
            params.put(BackEnd.Params.WARD_ID, mSelectedWard.getId());
            params.put(BackEnd.Params.DEPARTMENT_ID, mSelectedDepartment.getId());

            if (imageIP.getFile() != null) {
                try {
                    params.put(BackEnd.Params.IMAGE, imageIP.getFile());
                } catch (FileNotFoundException e) {
                    e.printStackTrace();
                }
            }

            String url = Client.absoluteUrl(BackEnd.EndPoints.TICKETS);
            url = url + "&include=rating,department,ward.sub-county,official";
            Client.getInstance().getClient().post(url, params, new ResponseHandler(new SubmitCallback()));
        }
    }


    @OnClick(R.id.permissionTV)
    public void onPermissionTVClicked() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(getMissingPermissions().toArray(new String[getMissingPermissions().size()]), 0);
        }
    }

    private List<String> getMissingPermissions() {
        ArrayList<String> permissions = new ArrayList<>();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.CAMERA);
            } else if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
            } else if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                permissions.add(Manifest.permission.READ_EXTERNAL_STORAGE);
            }
        }
        return permissions;
    }

    private class SubmitCallback extends Callback<Ticket> {

        SubmitCallback() {
            super(EditTicketActivity.this, Ticket.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            onSubmitBtnClicked();
        }

        @Override
        protected void onResponse(Ticket item) {
            super.onResponse(item);
            Intent intent = new Intent();
            intent.putExtra(Extras.TICKET, item);
            intent.setAction(TicketsFragment.ACTION_NEW_TICKET);
            LocalBroadcastManager.getInstance(EditTicketActivity.this).sendBroadcast(intent);
            EditTicketActivity.this.finish();
        }
    }

    private class CreateTicketDataCallback extends Callback<CreateTicketData> {

        private CreateTicketDataCallback() {
            super(EditTicketActivity.this, CreateTicketData.class);
        }

        @Override
        protected void onRetry() {
            super.onRetry();
            getData();
        }

        @Override
        protected void onResponse(CreateTicketData item) {
            super.onResponse(item);
            mCreateTicketData = item;
            initSpinners();
        }
    }

    private void initSpinners() {
        List<String> subCountyNames = new ArrayList<>();
        for (SubCounty subCounty : mCreateTicketData.subCounties) {
            subCountyNames.add(subCounty.getName());
        }
        subCountyMS.setItems(subCountyNames);
        subCountyMS.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            for (SubCounty subCounty : mCreateTicketData.subCounties) {
                String name = subCountyNames.get(position);
                if (subCounty.getName().equals(name)) {
                    mSelectedSubCounty = subCounty;
                    break;
                }
            }
            updateWardsMS();
        });

        List<String> departmentNames = new ArrayList<>();
        for (Department department : mCreateTicketData.departments) {
            departmentNames.add(department.getName());
        }
        departmentMS.setItems(departmentNames);
        departmentMS.setOnItemSelectedListener((MaterialSpinner.OnItemSelectedListener<String>) (view, position, id, item) -> {
            for (Department department : mCreateTicketData.departments) {
                String name = departmentNames.get(position);
                if (department.getName().equals(name)) {
                    mSelectedDepartment = department;
                    break;
                }
            }
        });


    }


    private void updateWardsMS() {
        if (mSelectedSubCounty == null) {
            wardMS.setEnabled(false);
        } else {
            List<String> wardNames = new ArrayList<>();
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        updatePermissionsUI();
    }

    @SuppressLint("SetTextI18n")
    private void updatePermissionsUI() {
        if (getMissingPermissions().size() > 0) {
            permissionTV.setText("Tap here or got to your device settings to allow use of CAMERA and STORAGE to be able to attach an optional image");
            imageIP.setVisibility(View.GONE);
            permissionTV.setVisibility(View.VISIBLE);
        } else {
            imageIP.setActivity(this, IMAGE_REQUEST_CODE, false);
            imageIP.setVisibility(View.VISIBLE);
            permissionTV.setVisibility(View.GONE);
        }
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, EditTicketActivity.class);
        context.startActivity(starter);
    }

}
