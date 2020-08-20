package com.beauney.butterinjector;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.beauney.injector.annotation.BindView;
import com.beauney.injector.library.ButterInjector;

public class MainActivity extends AppCompatActivity {
    @BindView(R.id.text_view)
    TextView mTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterInjector.bind(this);

        Toast.makeText(this, "mTextView----->" + mTextView, Toast.LENGTH_SHORT).show();
    }
}
