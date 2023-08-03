package com.smytappstudio.musicoplayer;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionDeniedResponse;
import com.karumi.dexter.listener.PermissionGrantedResponse;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.single.PermissionListener;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    ListView listView;
    FirebaseFirestore firestore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView( R.layout.activity_main);
        listView = findViewById( R.id.listView );
        firestore = FirebaseFirestore.getInstance();
        Map<String, Object> user =  new HashMap<>();
        user.put("firstname", "easy");
        user.put("lastname", "Smit");
        user.put("Hey SMIT", "easy");
        firestore.collection( "users" ).add( user ).addOnSuccessListener( new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                Toast.makeText( MainActivity.this, "Sucsess", Toast.LENGTH_SHORT ).show();
            }
        } ).addOnFailureListener( new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText( MainActivity.this, "Failure", Toast.LENGTH_SHORT ).show();
            }
        } );

        Dexter.withContext(this)
                .withPermission( Manifest.permission.READ_EXTERNAL_STORAGE )
                .withListener( new PermissionListener() {
                    @Override
                    public void onPermissionGranted(PermissionGrantedResponse permissionGrantedResponse) {
//                        Toast.makeText( MainActivity.this, "Runtime permission given", Toast.LENGTH_SHORT ).show();
                        ArrayList<File> mySongs = fetchSongs( Environment.getExternalStorageDirectory());
                            String [] items = new String[mySongs.size()];
                            for(int i = 0; i<mySongs.size();i++){
                                items[i] = mySongs.get( i ).getName().replace( ".mp3", "" );
                                ArrayAdapter<String> adapter = new ArrayAdapter<String>( MainActivity.this, android.R.layout.simple_list_item_1, items );
                                listView.setAdapter( adapter );
                                listView.setOnItemClickListener( new AdapterView.OnItemClickListener() {
                                    @Override
                                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                                        Intent intent = new Intent(MainActivity.this, PlaySong.class);
                                        String currentsong = listView.getItemAtPosition( position).toString();
                                        intent.putExtra( "songList", mySongs );
                                        intent.putExtra( "currentSong", currentsong );
                                        intent.putExtra( "position", position );
                                        startActivity( intent );

                                    }
                                } );
                            }
                    }

                    @Override
                    public void onPermissionDenied(PermissionDeniedResponse permissionDeniedResponse) {

                    }

                    @Override
                    public void onPermissionRationaleShouldBeShown(PermissionRequest permissionRequest, PermissionToken permissionToken) {
                        permissionToken.continuePermissionRequest();
                    }
                } )
                .check();

    }
    public ArrayList<File> fetchSongs(File file){
        ArrayList<File> arrayList = new ArrayList<File>();
        File[] songs = file.listFiles();
        if (songs != null){
            for (File myFile: songs ){
                if(!myFile.isHidden() && myFile.isDirectory()){
                    arrayList.addAll( fetchSongs( myFile ) );
                }
                else{
                    if(myFile.getName().endsWith( ".mp3" ) && !myFile.getName().startsWith( "." )){
                        arrayList.add(myFile);
                    }
                }
            }
        }
        return arrayList;
    }
}