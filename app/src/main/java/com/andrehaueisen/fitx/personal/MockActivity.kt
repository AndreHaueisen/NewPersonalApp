package com.andrehaueisen.fitx.personal

/*class MockActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mock)

        val databaseRef = FirebaseDatabase.getInstance().reference

        val mockButton = findViewById(R.id.mock_button) as Button
        mockButton.setOnClickListener {
            view ->  databaseRef.child(Constants.FIREBASE_LOCATION_PERSONAL_CLASSES).limitToLast(1).addChildEventListener(object:ChildEventListener{
            override fun onChildChanged(p0: DataSnapshot?, p1: String?) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildMoved(p0: DataSnapshot?, p1: String?) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onChildAdded(dataSnapshot: DataSnapshot?, previousChildName: String?) {
                if (dataSnapshot != null && dataSnapshot.exists()){

                    val personalKey = dataSnapshot.key
                    val personalClass = dataSnapshot.children.first().getValue(PersonalFitClass::class.java)

                }
            }

            override fun onChildRemoved(p0: DataSnapshot?) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }

            override fun onCancelled(p0: DatabaseError?) {
                throw UnsupportedOperationException("not implemented") //To change body of created functions use File | Settings | File Templates.
            }
        })}
    }
}*/
