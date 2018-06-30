package ke.go.nyandarua.nyantalk.activity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.synnapps.carouselview.CarouselView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import ke.co.toshngure.basecode.utils.BaseUtils;
import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.utils.Utils;

public class AboutActivity extends BaseActivity {

    @BindView(R.id.carouselView)
    CarouselView carouselView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        ButterKnife.bind(this);

        Utils.initSlidersCarousel(carouselView);
    }

    public static void start(Context context) {
        Intent starter = new Intent(context, AboutActivity.class);
        context.startActivity(starter);
    }

    @OnClick(R.id.shareBtn)
    public void onShareBtnClicked() {
        BaseUtils.shareText(this, getString(R.string.share_text));
    }

    @OnClick(R.id.visitWebsiteBtn)
    public void onVisitWebsiteBtnClicked() {
        Utils.openWebPage(this, getString(R.string.county_website_url));
    }

    @OnClick(R.id.poweredByBtn)
    public void onPoweredByBtnClicked() {
        Utils.openWebPage(this, getString(R.string.flex_website_url));
    }
}
