package aueb.distributed.myapplication.activities;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.VideoView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.UnknownHostException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;


import aueb.distributed.myapplication.CustomAdapter;
import aueb.distributed.myapplication.Publisher;
import aueb.distributed.myapplication.R;
import aueb.distributed.myapplication.UserNodeInfo;
import aueb.distributed.myapplication.UserNodeImpl;

public class ChatActivity extends AppCompatActivity {

    //TextView ChatName;
    String str1,str2;
    ImageView Send;
    ImageView uploadImage;
    ImageView uploadVideo;
    ImageView capture;
    //gia na deiksoume oti den epaize to video capture me ton kwdika tou android developers
//    ImageView captureVideo;
    VideoView videoView;
    Button HistoryBtn;
    EditText txtMessage;
    UserNodeInfo user22 = null;
    UserNodeImpl user2 = null;
    ArrayList<String> filesNotPrinted = new ArrayList<>();
    List<String> items = new LinkedList<>();
    CustomAdapter adapter  ;
    boolean userNodeFlag = true;
    String currentPhotoPath;
    int num = 0;

    //permissions
    private static int RESULT_LOAD_IMAGE = 102;
    private static int RESULT_LOAD_VIDEO = 103;
    private static int REQUEST_IMAGE_CAPTURE = 104;
    private static int REQUEST_VIDEO_CAPTURE = 105;


    private static final long serialVersionUID = 2683471646486152803L;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //get username and topic name
        Intent s = getIntent();
        str1 = s.getStringExtra("Name");
        str2 = s.getStringExtra("Chat");
        //ChatName.setText(str2);

        ChatActivity.UserNodeRun b = new ChatActivity.UserNodeRun();
        b.execute();

        setContentView(R.layout.activity_chat);

        //o recycler gia to chat
        RecyclerView recyclerView = findViewById(R.id.recycler_chat);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //o adapter gia to chat (CustomAdapter)
        adapter = new CustomAdapter(this, items);
        recyclerView.setAdapter(adapter);

        //button gia anevasma eikonas
        uploadImage = findViewById(R.id.imageUpload);
        uploadImage.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                fileIntent.setType("image/* ");
                startActivityForResult(fileIntent, RESULT_LOAD_IMAGE);
            }
        });

        //button gia anevasma video
        uploadVideo = findViewById(R.id.videoUpload);
        uploadVideo.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent fileIntent = new Intent(Intent.ACTION_PICK, MediaStore.Video.Media.INTERNAL_CONTENT_URI);
                fileIntent.setType("video/* ");
                startActivityForResult(fileIntent, RESULT_LOAD_VIDEO);
            }
        });

        //button gia capture eikonas
        capture = findViewById(R.id.capture);
        capture.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();

            }
        });

        //gia na deiksoume oti den epaize to video capture me ton kwdika tou android developers
//
//        captureVideo = findViewById(R.id.captureVideo);
//        captureVideo.setOnClickListener (new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Log.d("CAP","trying to capture");
//                dispatchTakeVideoIntent();
//
//            }
//        });

        //me to pathma tou koumpiou send pairnei to periexomeno tou text pediou, to kanei txt arxeio kai to stelnei ston publisher gia anevasma
        txtMessage = findViewById(R.id.Message);
        Send = findViewById(R.id.Send);
        Send.setOnClickListener (new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String str = txtMessage.getText().toString();

                File path = new File("/storage/self/primary/Download/text.txt");
                //try to create txt file
                try {
                    //passing file instance in filewriter
                    FileWriter wr = new FileWriter(path);

                    //calling writer.write() method with the string
                    wr.write(str);

                    //flushing the writer
                    wr.flush();

                    //closing the writer
                    wr.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                //if publisher exists, dwse txt file gia upload
                if (user2 !=null &&user2.getCons() !=null && user2.getCons().getPublisher()!=null)
                {
                    user2.getCons().getPublisher().setPath(path.getPath());
                    user2.getCons().getPublisher().setFlag(true);
                }
            }
        });

        //refresh button
        HistoryBtn = findViewById(R.id.historyBtn);
        HistoryBtn.setOnClickListener((new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                    //pare ta arxeia pou exoyn katevei kai den exoun emfanistei sthn othonh
                    if (user2 !=null && user2.getCons() !=null)
                    {
                        filesNotPrinted = user2.getCons().getFilesNotPrinted();

                    }
                    num += filesNotPrinted.size();

                    //send to adapter and empty ArrayList
                    if (filesNotPrinted.size() > 0) {
                        Log.d("REFRESH", "Files size: " + filesNotPrinted.size());
                        for (int k = 0; k < filesNotPrinted.size(); ) {
                            Log.d("Tag", "k " + k);
                            items.add(filesNotPrinted.get(0));
                            filesNotPrinted.remove(0);
                        }
                        adapter.notifyItemInserted(num);
                    }
            }
        }));

    }
    //create usernode and run
    private class UserNodeRun extends AsyncTask<Void, Void, String> {
        protected String doInBackground(Void... params) {

            if(userNodeFlag)
            {
                user22 = new UserNodeInfo("localhost", 44444, str1, str2);
                user2 = null;
                try {
                    user2 = new UserNodeImpl("10.0.2.2", 12311, user22);
                    Log.d("USERNODE","User created");
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                }
                user2.start();
                Log.d("USERNODE","Usernode started");

                userNodeFlag=false;
            }
            return "Executed";
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //upload selected image from collection
        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null != data) {

            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String picturePath = cursor.getString(columnIndex);
            Log.d("IMAGE_UPLOAD", picturePath );
            cursor.close();

            //send to publisher for publish
            if (user2 !=null &&user2.getCons() !=null && user2.getCons().getPublisher()!=null)
            {
                user2.getCons().getPublisher().setPath(picturePath);
                user2.getCons().getPublisher().setFlag(true);

            }
        }
        //upload selected video from collection
        else if (requestCode == RESULT_LOAD_VIDEO && resultCode == RESULT_OK && null != data) {
            Uri selectedImage = data.getData();
            String[] filePathColumn = { MediaStore.Images.Media.DATA };

            Cursor cursor = getContentResolver().query(selectedImage,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();
            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
            String videoPath = cursor.getString(columnIndex);
            Log.d("videoPath", videoPath );
            cursor.close();
            //send to publisher for publish
            if (user2 !=null &&user2.getCons() !=null && user2.getCons().getPublisher()!=null)
            {
                user2.getCons().getPublisher().setPath(videoPath);
                user2.getCons().getPublisher().setFlag(true);
            }
        }

        //gia na deiksoume oti den epaize to video capture me ton kwdika tou android developers
//
//        else if (requestCode == REQUEST_VIDEO_CAPTURE && resultCode == RESULT_OK) {
//            Log.d("CAP","URI");
//            Uri videoUri = data.getData();
//            videoView.setVideoURI(videoUri);
//        }

    }

    //intent gia capture eikonas apo camera
    private void dispatchTakePictureIntent(){

        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            // Create the File where the photo should go
            File photoFile = null;
            try {
                photoFile = createImageFile();
            } catch (IOException ex) {
                // Error occurred while creating the File
                ex.printStackTrace();

            }
            // Continue only if the File was successfully created
            if (photoFile != null) {
                Uri photoURI = FileProvider.getUriForFile(this,
                        "aueb.distributed.myapplication.fileprovider",
                        photoFile);
                takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
            }
        }
    }
    //create captured image
    private File createImageFile() throws IOException {
        // Create an image file name
        Log.d("Path", "createImageFile");
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "JPEG_" + timeStamp + "_";
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        //File storageDir = new File("/storage/emulated/0/DCIM/");
        File image = File.createTempFile(
                imageFileName,  /* prefix */
                ".jpg",         /* suffix */
                storageDir      /* directory */
        );

        // Save a file: path for use with ACTION_VIEW intents
        currentPhotoPath = image.getAbsolutePath();
        Log.d("Path", currentPhotoPath);
        return image;
    }

    //gia na deiksoume oti den epaize to video capture me ton kwdika tou android developers

//    private void dispatchTakeVideoIntent() {
//        Intent takeVideoIntent = new Intent(MediaStore.ACTION_VIDEO_CAPTURE);
//        if (takeVideoIntent.resolveActivity(getPackageManager()) != null) {
//            Log.d("CAP","den einai null");
//            startActivityForResult(takeVideoIntent, REQUEST_VIDEO_CAPTURE);
//        }
//    }

}


