package com.adibta.channel_chat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.Arrays;

import butterknife.BindDimen;
import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @BindView(R.id.linear_layout_community) LinearLayout linearLayoutCommunity;
    @BindView(R.id.recycler_view_community) RecyclerView rvCommunity;
    @BindView(R.id.text_view_empty_community) TextView tvEmptyCommunity;

    public static final int RC_SIGN_IN = 1;
    public static final String DEFAULT_COMMUNITY_ID = "community-default";
    public static final String DEFAULT_CHANNEL_ID = "channel-default";
    public static final String MESSAGE_NODE = "messages";

    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference communityDatabaseReference;
    private DatabaseReference channelDatabaseReference;
    private DatabaseReference messageDatabaseReference;
    private ChildEventListener childEventListener;

    private FirebaseAuth firebaseAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    private FirebaseStorage firebaseStorage;
    private StorageReference storageReference;

    private FirebaseFirestore firebaseFirestore;
    private DocumentReference communityDocumentReference;
    private DocumentReference channelDocumentReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        hideView();
        setAuthStateListener();
        firebaseInitialize();
    }

    @Override
    protected void onResume() {
        super.onResume();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (authStateListener != null){
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN){
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Sign in Success", Toast.LENGTH_SHORT).show();
            } else if (resultCode == RESULT_CANCELED) {
                Toast.makeText(this, "Sign in cancelled", Toast.LENGTH_LONG).show();
                finish();
            }
        }
    }

    private void firebaseInitialize(){
        firebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        //TODO get data from net / shared pref
        communityDatabaseReference = firebaseDatabase.getReference().child(DEFAULT_COMMUNITY_ID);
        channelDatabaseReference = communityDatabaseReference.child(DEFAULT_CHANNEL_ID);
        messageDatabaseReference = channelDatabaseReference.child(MESSAGE_NODE);

        firebaseStorage = FirebaseStorage.getInstance();
        firebaseFirestore = FirebaseFirestore.getInstance();
    }

    private void attachDatabaseReadListener(){
        if (childEventListener == null){
            childEventListener = new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildChanged(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onChildRemoved(DataSnapshot dataSnapshot) {}
                @Override
                public void onChildMoved(DataSnapshot dataSnapshot, String s) {}
                @Override
                public void onCancelled(DatabaseError databaseError) {}
            };
        }
        messageDatabaseReference.addChildEventListener(childEventListener);
    }

    private void detachDatabaseReadListener(){
        if(childEventListener != null){
            messageDatabaseReference.removeEventListener(childEventListener);
        }
    }

    private void onSignedInInitialize(FirebaseUser user){
        //TODO : Get user Information

        attachDatabaseReadListener();
    }

    private void onSignedOutCleanUp(){
        //TODO : Clear user information
        //TODO : Clear any rv adapter
        detachDatabaseReadListener();
    }

    private void setAuthStateListener(){
        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null){
                    //userSignIn
                    onSignedInInitialize(user);
                } else {
                    //UserSignOut
                    onSignedOutCleanUp();
                    startActivityForResult(
                            AuthUI.getInstance()
                                    .createSignInIntentBuilder()
                                    .setAvailableProviders(
                                            Arrays.asList(
                                                    new AuthUI.IdpConfig.Builder(
                                                            AuthUI.PHONE_VERIFICATION_PROVIDER
                                                    ).build()
                                            ))
                                    .build(),
                            RC_SIGN_IN);
                }
            }
        };
    }
    private void hideView(){
        linearLayoutCommunity.setVisibility(View.GONE);
        tvEmptyCommunity.setVisibility(View.GONE);
    }
}
