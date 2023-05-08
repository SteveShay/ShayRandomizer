package com.example.shayrandomizer;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;

import com.spotify.android.appremote.api.ConnectionParams;
import com.spotify.android.appremote.api.Connector;
import com.spotify.android.appremote.api.SpotifyAppRemote;

import com.spotify.protocol.client.Subscription;
import com.spotify.protocol.types.PlayerState;
import com.spotify.protocol.types.Track;



public class LoggedInActivity extends AppCompatActivity {

    GoogleSignInOptions gso;
    GoogleSignInClient gsc;
    TextView name, email;
    Button signoutBtn;
    private static final String CLIENT_ID = "f308e03cbc174502800b65915ebebcbe";
    private static final String REDIRECT_URI = "http://localhost:3000";
    private SpotifyAppRemote mSpotifyAppRemote;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logged_in);

        name = findViewById(R.id.name);
        email = findViewById(R.id.email);
        signoutBtn = findViewById(R.id.soutBtn);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).requestEmail().build();
        gsc = GoogleSignIn.getClient(this,gso);

        GoogleSignInAccount userAcct = GoogleSignIn.getLastSignedInAccount(this);
        if (userAcct != null){
            String userName = userAcct.getDisplayName();
            String userEmail = userAcct.getEmail();
            name.setText(userName);
            email.setText(userEmail);
        }

        signoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sOut();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        ConnectionParams connectionParams =
        new ConnectionParams.Builder(CLIENT_ID)
                .setRedirectUri(REDIRECT_URI)
                .showAuthView(true)
                .build();

        SpotifyAppRemote.connect(this, connectionParams, new Connector.ConnectionListener() {
            @Override
            public void onConnected(SpotifyAppRemote spotifyAppRemote) {
                mSpotifyAppRemote = spotifyAppRemote;
                Toast.makeText(getApplicationContext(), "Connection successful", Toast.LENGTH_SHORT).show();

                connected();
            }

            @Override
            public void onFailure(Throwable error) {
                Log.e("LoggedInActivity", error.getMessage(), error);
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        SpotifyAppRemote.disconnect(mSpotifyAppRemote);
    }

    private void connected(){
        mSpotifyAppRemote.getPlayerApi().play("spotify:playlist:2eH1vkRbss1OTm5bxqZvFi");

        mSpotifyAppRemote.getPlayerApi().subscribeToPlayerState().setEventCallback(playerstate -> {
            final Track track = playerstate.track;
            if (track != null){
                Toast.makeText(getApplicationContext(), track.name + " by " + track.artist.name, Toast.LENGTH_LONG).show();
            }
        });
    }

    void sOut() {
        gsc.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(Task<Void> task) {
                finish();
                startActivity(new Intent(LoggedInActivity.this,MainActivity.class));
            }
        });
    }
}