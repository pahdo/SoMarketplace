package com.danielzou.somarketplace;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

/**
 * The details activity shows additional product information for a particular item listing.
 */
public class DetailsActivity extends AppCompatActivity {

    private String mComment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_details);

        Bundle extras = getIntent().getExtras();
        final InventoryItem inventoryItem = extras.getParcelable("item");

        FirebaseStorage storage = FirebaseStorage.getInstance();

        // Reference to an image file in Firebase Storage
        StorageReference storageReference = storage.getReferenceFromUrl("gs://somarketplace-f1028.appspot.com");
        try {
            StorageReference imageRef = storageReference.child(inventoryItem.getImageRef());

            // ImageView in your Activity
            ImageView imageView = (ImageView) findViewById(R.id.details_image_view);

            // Load the image using Glide
            Glide.with(getApplicationContext())
                    .using(new FirebaseImageLoader())
                    .load(imageRef)
                    .into(imageView);
        } catch (Exception e) {
            e.printStackTrace();
        }

        TextView itemName = (TextView) findViewById(R.id.details_item_name);
        itemName.setText(inventoryItem.getName());
        TextView itemPrice = (TextView) findViewById(R.id.details_item_price);
        itemPrice.setText(Integer.toString(inventoryItem.getPrice()));
        TextView itemDescription = (TextView) findViewById(R.id.details_item_description);
        itemDescription.setText(inventoryItem.getDescription());

        ImageButton addToCart = (ImageButton) findViewById(R.id.add_to_cart);
        addToCart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setTitle("Enter a comment.");

                // Set up the input
                final EditText input = new EditText(view.getContext());
                // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                input.setInputType(InputType.TYPE_CLASS_TEXT);
                builder.setView(input);

                // Set up the buttons
                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        mComment = input.getText().toString();
                        final CartItem cartItem = new CartItem(inventoryItem.getItemId(), mComment);

                        final FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference ref = database.getReference();

                        final String uid = FirebaseAuth.getInstance().getCurrentUser().getUid();

                        ref.child("cartItems").child(uid).push().setValue(cartItem, new DatabaseReference.CompletionListener() {
                            @Override
                            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                                if (databaseError != null) {
                                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                                } else {
                                    System.out.println("Data saved successfully.");
                                }
                            }
                        });
                        finish();
                    }
                });
                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });

                builder.show();
            }
        });
    }
}
