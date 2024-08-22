package aueb.distributed.myapplication;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;

import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.MediaController;
import android.widget.TextView;
import android.widget.VideoView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;

import aueb.distributed.myapplication.activities.ChatActivity;

public class CustomAdapter extends RecyclerView.Adapter< ViewHolder> {

    private Context context;
    List<String> items;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder).
     */


    /**
     * Initialize the dataset of the Adapter.
     *
     * @param dataSet String[] containing the data to populate views to be used
     * by RecyclerView.
     */
    public CustomAdapter(Context context, List<String> dataSet) {
        this.context = context;
        items = dataSet;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.adapter_item, viewGroup, false);

        return new ViewHolder(view).linkAdapter(this);
    }


    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder, final int position) {
        String path = items.get(position);
        String fileType = path.substring(path.lastIndexOf('.') + 1);
        Log.d("Ti path", path);
        if(fileType.equals("jpeg") || fileType.equals("jpg"))
        {
            Bitmap bitmap = BitmapFactory.decodeFile(path);
            //String photoPath = Environment.getExternalStorageDirectory() + "/abc.jpg";
            // Get element from your dataset at this position and replace the
            // contents of the view with that element
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.GONE);
            viewHolder.videoChat.setVisibility(View.GONE);

            viewHolder.imageView.setImageBitmap(bitmap);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    //String messageType = view.getTag(R.id.TAG_MESSAGE_TYPE).toString();
                        Uri uri = getImageUri(context, bitmap);
                        Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                        intent.setDataAndType(uri, "image/jpg");
                        context.startActivity(intent);

                }
            });
        }
        else if (fileType.equals("mp4"))
        {
            viewHolder.imageView.setVisibility(View.VISIBLE);
            viewHolder.textView.setVisibility(View.GONE);
            viewHolder.videoChat.setVisibility(View.GONE);

            Uri uri = Uri.parse(path);

            viewHolder.imageView.setImageResource(R.drawable.ic_baseline_video_library_24);
            viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Intent intent = new Intent(Intent.ACTION_VIEW,uri);
                    intent.setDataAndType(uri, "video/mp4");
                    context.startActivity(intent);

                }
            });

//            Uri uri = Uri.parse(path);
//            viewHolder.videoChat.setVideoURI(uri);
//
//            MediaController mediaController = new MediaController(context);
//            viewHolder.videoChat.setMediaController(mediaController);
//            mediaController.setAnchorView(viewHolder.videoChat);


//            LocalVideoTrack localVideoTrack = localVideoTracks.get(position);
//
//            // Remove renderer from previous video track
//            if (viewHolderMap.containsKey(holder)) {
//                viewHolderMap.get(holder).removeRenderer(holder.frameCountProxyRendererListener);
//            }
//
//            // Update view holder
//            holder.trackIdTextView.setText(localVideoTrack.getTrackId());
//            holder.frameCountProxyRendererListener.videoView
//                    .setVideoScaleType(VideoScaleType.ASPECT_FILL);
//            localVideoTrack.addRenderer(holder.frameCountProxyRendererListener);
//            viewHolderMap.put(holder, localVideoTrack);


        }
         else if (fileType.equals("txt"))
        {

            // Declaring an empty string
            String str = "";

            // Try block to check for exceptions
            try {

                // Reading all bytes form file and
                // storing that in the string
                str = new String(
                        Files.readAllBytes(Paths.get(path)));
            }

            // Catch block to handle the exceptions
            catch (IOException e) {

                // Print the exception along with line number
                // using printStackTrace() method
                e.printStackTrace();
            }

            viewHolder.imageView.setVisibility(View.GONE);
            viewHolder.textView.setVisibility(View.VISIBLE);
            viewHolder.videoChat.setVisibility(View.GONE);

            viewHolder.textView.setText(str);

        }


    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return items.size();
    }

    public Uri getImageUri(Context inContext, Bitmap inImage) {
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "Title", null);
        return Uri.parse(path);
    }
}
class ViewHolder extends RecyclerView.ViewHolder {
    public final TextView textView;
    ImageView imageView;
    CustomAdapter adapter;
    VideoView videoChat;

    public ViewHolder(View view) {
        super(view);
        // Define click listener for the ViewHolder's View

        textView = (TextView) view.findViewById(R.id.textView);
        imageView = (ImageView) view.findViewById(R.id.imageChat);
        videoChat = (VideoView) view.findViewById(R.id.videoChat);

    }

    public TextView getTextView() {
        return textView;
    }

    public ViewHolder linkAdapter (CustomAdapter adapter){
        this.adapter = adapter;
        return this;
    }

}

