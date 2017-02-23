package com.example.andre.fitx.backend;

import com.google.api.server.spi.config.Api;
import com.google.api.server.spi.config.ApiNamespace;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.auth.FirebaseCredentials;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.jsoup.Connection;
import org.jsoup.Jsoup;

import java.io.IOException;
import java.util.logging.Logger;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;

/**
 * Created by andre on 1/31/2017.
 */
@Api(
        name = "newclassservlet",
        version = "v1",
        namespace = @ApiNamespace(
                ownerDomain = "backend.fitx.andre.example.com",
                ownerName = "backend.fitx.andre.example.com",
                packagePath = ""
        )
)
public final class NewClassServlet extends HttpServlet {

    private final Logger log = Logger.getLogger(NewClassServlet.class.getName());
    private final String FIREBASE_LOCATION_PERSONAL_CLASSES = "personalFitClasses";
    private final String FIREBASE_LOCATION_SERVER_DEVICE_ID_MAPPINGS = "server_device_id_mappings";

    @Override
    public void init() throws ServletException {
        super.init();

        try {

            final FirebaseOptions options = new FirebaseOptions.Builder()
                    .setCredential(FirebaseCredentials.applicationDefault())
                    .setDatabaseUrl("https://personalapp-ad97d.firebaseio.com")
                    .build();

            final FirebaseApp firebaseApp = FirebaseApp.initializeApp(options);

            final DatabaseReference databaseReference = FirebaseDatabase.getInstance(firebaseApp).getReference();
            databaseReference.child(this.FIREBASE_LOCATION_PERSONAL_CLASSES).limitToLast(1).addChildEventListener(new ChildEventListener() {
                public void onChildChanged( DataSnapshot dataSnapshot,  String p1) {
                    if(dataSnapshot != null && dataSnapshot.exists()) {
                        String personalKey = dataSnapshot.getKey();
                        PersonalFitClass personalClass = dataSnapshot.getChildren().iterator().next().getValue(PersonalFitClass.class);

                        getDatabaseTokenAndSendMessage(databaseReference, personalKey, personalClass);
                    }
                }

                public void onChildMoved(DataSnapshot p0, String p1) {

                }

                public void onChildAdded( DataSnapshot dataSnapshot, String previousChildName) {

                    if(dataSnapshot != null && dataSnapshot.exists()) {
                        String personalKey = dataSnapshot.getKey();
                        PersonalFitClass personalClass = dataSnapshot.getChildren().iterator().next().getValue(PersonalFitClass.class);

                        getDatabaseTokenAndSendMessage(databaseReference, personalKey, personalClass);
                    }

                }

                public void onChildRemoved( DataSnapshot p0) {

                }

                public void onCancelled( DatabaseError p0) {

                }
            });
        } catch (Exception error) {
            this.log.info("App already exists");
        }
    }


    private void getDatabaseTokenAndSendMessage(DatabaseReference databaseReference, String personalKey, final PersonalFitClass personalFitClass) {

        databaseReference.child(this.FIREBASE_LOCATION_SERVER_DEVICE_ID_MAPPINGS).child(personalKey).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onCancelled( DatabaseError p0) {
            }

            public void onDataChange( DataSnapshot dataSnapshot) {
                if(dataSnapshot != null && dataSnapshot.exists()) {
                    String databaseToken = (String) dataSnapshot.getValue();

                    try {
                        sendMessage(databaseToken, composeMessage(personalFitClass));
                    }catch (IOException ioe){
                        log.info("getDatabaseTokenMessage: " + ioe);
                    }
                }

            }
        });
    }

    private String composeMessage(PersonalFitClass personalFitClass) {
        return personalFitClass.getClientName() + " scheduled a class. Click to review.";
    }

    private void sendMessage(String databaseToken, String message) throws IOException {
        Connection connect = Jsoup.connect("https://fcm.googleapis.com/fcm/send").method(Connection.Method.POST);
        connect.data("registration_id", databaseToken);
        connect.data("collapse_key", "New PersonalClass");
        connect.data("data.title", "Congratulation! New Class!");
        connect.data("data.message", message);
        connect.execute();
    }
}
