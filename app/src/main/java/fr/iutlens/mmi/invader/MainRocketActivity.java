package fr.iutlens.mmi.invader;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.animation.AnimationUtils;


public class MainRocketActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rocket_main);

        findViewById(R.id.rocket).startAnimation(AnimationUtils.loadAnimation(this,R.anim.flotteur));

    }


    public void slidetop(View view) {
        findViewById(R.id.rocket).startAnimation(AnimationUtils.loadAnimation(this,R.anim.slidetop));
    }

}

