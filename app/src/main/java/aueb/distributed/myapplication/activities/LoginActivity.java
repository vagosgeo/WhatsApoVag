package aueb.distributed.myapplication.activities;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import aueb.distributed.myapplication.R;
import com.google.android.material.textfield.TextInputEditText;

public class LoginActivity extends AppCompatActivity  {
    private TextInputEditText NameText, ChatRoom;
    private String Name, Chat;
    Button button;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        NameText = findViewById(R.id.NameText);
        ChatRoom = findViewById(R.id.ChatRoom);
        button = (Button) findViewById(R.id.btnLogin);

    }

    @Override
    protected void onStart (){
        super.onStart();

        //Pairnoume onoma xrhth kai topic
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Name = NameText.getText().toString().trim();
                Chat = ChatRoom.getText().toString().trim();

                if(Name.equals("")){
                    NameText.setError(getString(R.string.enter_name));
                }
                else if (Chat.equals("")){
                    ChatRoom.setError(getString(R.string.enter_chat));
                }

                Intent s = new Intent(view.getContext(),ChatActivity.class);
                s.putExtra("Name", Name );
                s.putExtra("Chat", Chat );

                startActivity(s);


            }
        });


    }


}