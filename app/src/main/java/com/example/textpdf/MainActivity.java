package com.example.textpdf;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ActionMode;
import android.view.ContextMenu;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class MainActivity extends AppCompatActivity  {
    private static int textviewSize = 16;
    private static final int PERMISSION_REQUEST_CODE = 1;
    private ActivityResultLauncher<String> mGetContent;
    TextView mtextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PERMISSION_REQUEST_CODE);
        }

        mtextView=(TextView) findViewById(R.id.fileContentTextview);

        Button selectFileButton = findViewById(R.id.select_file_button);
        selectFileButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openFileSelectionDialog();
            }
        });

        mGetContent = registerForActivityResult(new ActivityResultContracts.GetContent(), uri -> {
            if (uri != null) {
                String fileContent = readFileContent(uri);
                mtextView.setText(fileContent);
                registerForContextMenu(mtextView);

                mtextView.setCustomSelectionActionModeCallback(new ActionMode.Callback() {
                    @Override
                    public boolean onCreateActionMode(ActionMode actionMode, Menu menu) {
                        MenuInflater inflater=actionMode.getMenuInflater();
                        inflater.inflate(R.menu.main_menu,menu);
                        return true;
                    }
                    @Override
                    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                        return false;
                    }
                    @Override
                    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                        if(item.getItemId()==R.id.own){
                            String selectedinText = mtextView.getText().toString().substring(mtextView.getSelectionStart(), mtextView.getSelectionEnd());

                            Intent intent = new Intent(MainActivity.this, OwnFeaturesActivity.class);
                            intent.putExtra("selectedtext", selectedinText);
                            startActivity(intent);
                            mode.finish();
                            return true;
                        }
                        else if(item.getItemId()==R.id.copy){
                            mode.finish();
                            return true;
                        }
                        else { return false; }
                    }
                    @Override
                    public void onDestroyActionMode(ActionMode mode) {}
                });
            }
        });
    }
    @Override
    public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
        super.onCreateContextMenu(menu, v, menuInfo);
        getMenuInflater().inflate(R.menu.main_menu,menu);
    }
    private void openFileSelectionDialog() {
        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("text/plain");
        mGetContent.launch(intent.getType());
    }
    private String readFileContent(Uri uri) {
        StringBuilder contentBuilder = new StringBuilder();
        try {
            InputStream inputStream = getContentResolver().openInputStream(uri);
            BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
            String line;
            while ((line = reader.readLine()) != null) {
                contentBuilder.append(line).append("\n");
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.d("result","content is  not read");
        }
        return contentBuilder.toString();
    }

    public void ZoomIn(View view){
        if(textviewSize>40) return;
        textviewSize+=2;
        mtextView.setTextSize(textviewSize);
    }
    public void ZoomOut(View view){
        if(textviewSize<24) return;
        textviewSize-=2;
        mtextView.setTextSize(textviewSize);
    }
}