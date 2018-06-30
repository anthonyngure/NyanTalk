package ke.go.nyandarua.nyantalk.utils;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.customtabs.CustomTabsIntent;
import android.text.TextUtils;

import com.synnapps.carouselview.CarouselView;

import java.util.Objects;

import ke.go.nyandarua.nyantalk.R;
import ke.go.nyandarua.nyantalk.activity.MainActivity;
import saschpe.android.customtabs.CustomTabsHelper;
import saschpe.android.customtabs.WebViewFallback;

public class Utils {

    public static String removePhoneCode(String phone) {
        if (!TextUtils.isEmpty(phone) && phone.length() > 10) {
            return '0' + phone.substring(phone.length() - 9);
        } else {
            return phone;
        }
    }

    public static void openWebPage(Context context, String url) {
        CustomTabsIntent customTabsIntent = new CustomTabsIntent.Builder()
                .addDefaultShareMenuItem()
                .setToolbarColor(context.getResources().getColor(R.color.colorPrimary))
                .setShowTitle(true)
                //.setCloseButtonIcon(backArrow)
                .build();

        // This is optional but recommended
        CustomTabsHelper.addKeepAliveExtra(context, customTabsIntent.intent);

        // This is where the magic happens...
        CustomTabsHelper.openCustomTab(context, customTabsIntent,
                Uri.parse(url),
                new WebViewFallback());
    }

    public static void initSlidersCarousel(CarouselView carouselView) {
        carouselView.setPageCount(11);
        carouselView.setImageListener((position, imageView) -> {
            switch (position) {
                case 0:
                    imageView.setImageResource(R.drawable.logo);
                    break;
                case 1:
                    imageView.setImageResource(R.drawable.slider_2);
                    break;
                case 2:
                    imageView.setImageResource(R.drawable.slider_3);
                    break;
                case 3:
                    imageView.setImageResource(R.drawable.slider_4);
                    break;
                case 4:
                    imageView.setImageResource(R.drawable.slider_5);
                    break;
                case 5:
                    imageView.setImageResource(R.drawable.slider_6);
                    break;
                case 6:
                    imageView.setImageResource(R.drawable.slider_7);
                    break;
                case 7:
                    imageView.setImageResource(R.drawable.slider_8);
                    break;
                case 8:
                    imageView.setImageResource(R.drawable.slider_9);
                    break;
                case 9:
                    imageView.setImageResource(R.drawable.slider_10);
                    break;
                case 10:
                    imageView.setImageResource(R.drawable.slider_1);
                    break;
                default:
                    imageView.setImageResource(R.drawable.slider_1);
            }
        });

    }

    public static void startMainActivity(Activity activity){
        Intent intent = new Intent(activity, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
        activity.startActivity(intent);
        Objects.requireNonNull(activity).finish();
    }
}
