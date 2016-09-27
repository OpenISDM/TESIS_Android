package sinica.earth.tesis;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    /** Duration of wait **/
    private final int SPLASH_DISPLAY_LENGTH = 500;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);

        /* New Handler to start the HomeActivity
         * and close this MainActivity(Splash-Screen) after some seconds.*/
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                /* Create an Intent that will start the HomeActivity. */
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                MainActivity.this.startActivity(intent);
                MainActivity.this.finish();
            }
        }, SPLASH_DISPLAY_LENGTH);

    }

}
