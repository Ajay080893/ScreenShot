package deepshikha.com.screenshot;

import android.app.Activity;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

/**
 * Created by deepshikha on 8/5/17.
 */

public class Screenshot extends Activity {
    ImageView iv_image;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screenshot);
        init();
    }

    private void init() {
        iv_image = (ImageView) findViewById(R.id.iv_image);
        String completePath = Environment.getExternalStorageDirectory() + "/" + "screenshotdemo.jpg";
        Glide.with(Screenshot.this).load(completePath).error(R.drawable.image1).into(iv_image);

    }


}
