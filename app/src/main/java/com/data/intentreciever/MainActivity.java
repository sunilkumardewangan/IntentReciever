package com.data.intentreciever;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import android.os.Handler;
import android.os.ResultReceiver;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    Context mContext;
    public static final int PERMISSION_EXTERNAL_STORAGE = 101;
    ListView listView;
    ArrayList<Bitmap> bitmapArrayList = new ArrayList<>();
    ImageResultReceiver imageResultReceiver;

    Button button;
    EditText inputText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        mContext = this;
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_EXTERNAL_STORAGE
            );
        }

        button = findViewById(R.id.button);
        inputText = findViewById(R.id.inEnterUrl);
        inputText.setText("https://www.android.com/static/2016/img/share/andy-lg.png");
        listView = findViewById(R.id.listView);

        imageResultReceiver = new ImageResultReceiver(new Handler());

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent startIntent = new Intent(MainActivity.this,
                        MyIntentService.class);
                startIntent.putExtra("receiver", imageResultReceiver);
                startIntent.putExtra("url", inputText.getText().toString().trim());
                startService(startIntent);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_EXTERNAL_STORAGE:
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                } else {
                    Toast.makeText(getApplicationContext(), "We need the above permission to save images", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    private class ImageResultReceiver extends ResultReceiver {

        public ImageResultReceiver(Handler handler) {
            super(handler);
        }

        @Override
        protected void onReceiveResult(int resultCode, Bundle resultData) {
            switch (resultCode) {
                case MyIntentService.DOWNLOAD_ERROR:
                    Toast.makeText(getApplicationContext(), "Error in Downloading",
                            Toast.LENGTH_SHORT).show();
                    break;

                case MyIntentService.DOWNLOAD_SUCCESS:
                    String filePath = resultData.getString("filePath");
                    Bitmap bmp = BitmapFactory.decodeFile(filePath);

                    if (bmp != null) {
                        bitmapArrayList.add(bmp);
                        Log.d("API123", "bitmap " + bitmapArrayList.size());
                        listView.setAdapter(new ImagesAdapter(mContext, bitmapArrayList));
                        //imagesAdapter.setItems(bitmapArrayList);
                        Toast.makeText(getApplicationContext(),
                                "Image Download Successful",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getApplicationContext(),
                                "Error in Downloading",
                                Toast.LENGTH_SHORT).show();
                    }

                    break;
            }
            super.onReceiveResult(resultCode, resultData);
        }

    }
}
