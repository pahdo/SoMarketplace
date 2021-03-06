package com.danielzou.somarketplace;

import android.support.test.runner.AndroidJUnit4;

import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.Assert.*;

/**
 * A suite of instrumentation tests designed to test Firebase realtime database functions
 * as well as populate inventoryItems.
 */
@RunWith(AndroidJUnit4.class)
public class DatabaseTests {

    /**
     * This test demonstrates add to cart functionality with the Firebase realtime database.
     * @throws Exception
     */
    @Test
    public void addToCart() throws Exception {
        final String user = "user1";
        final String item = "item1";
        final String comment = "This is a comment.";
        final CartItem cartItem = new CartItem(item, comment);

        final CountDownLatch writeSignal = new CountDownLatch(1);

        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("cartItems");

        ref.child(user).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                assertEquals(cartItem, dataSnapshot.getValue(CartItem.class));
                writeSignal.countDown();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        ref.child(user).push().setValue(cartItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });
    }

    /**
     * This test demonstrates the purchase cart functionality with the Firebase realtime database.
     * @throws Exception
     */
    @Test
    public void purchaseCart() throws Exception {
        final String user = "user2";
        final String item = "item1";
        final String comment = "This is great!";
        final PurchasedItem purchasedItem = new PurchasedItem(item, comment, user);

        final CountDownLatch writeSignal = new CountDownLatch(1);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("purchasedItems");

        ref.child(user).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                assertEquals(purchasedItem, dataSnapshot.getValue(PurchasedItem.class));
                writeSignal.countDown();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);

        ref.child(user).push().setValue(purchasedItem);
    }

    /**
     * This test demonstrates the add inventory item functionality of the Firebase realtime database.
     * @throws Exception
     */
    @Test
    public void addInventoryItem() throws Exception {
        final String name = "Big Coat";
        final String imageRef = "-";
        final int price = 100;
        final String description = "This is a fluffy coat.";
        final InventoryItem inventoryItem = new InventoryItem("item0", name, imageRef, price, description);

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("inventoryItems");

        final CountDownLatch writeSignal = new CountDownLatch(2);

        ref.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                System.out.println(inventoryItem.toString());
                System.out.println(dataSnapshot.getValue(InventoryItem.class).toString());
                assertEquals(inventoryItem, dataSnapshot.getValue(InventoryItem.class));
                writeSignal.countDown();
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        ref.push().setValue(inventoryItem, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);
    }

    /**
     * Run this test to populate the inventoryItems of the Firebase realtime database.
     * @throws Exception
     */
    @Test
    public void populateListings() throws Exception {
        final String imageRef = "-";
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference ref = database.getReference("inventoryItems");

        Map<String, InventoryItem> listings = new HashMap<>();
        listings.put("item1", new InventoryItem("item1","Big Coat", "big_coat.jpg", 100, "This is a fluffy coat"));
        listings.put("item2", new InventoryItem("item2", "Small Coat", "small_coat.jpg", 20, "This is a light coat"));
        listings.put("item3", new InventoryItem("item3", "Skinny Cargo Pants", "skinny_cargo_pants.jpg", 40, "This is a pair of skinny pants with big pockets"));
        listings.put("item4", new InventoryItem("item4", "Light Blue Jeans", "light_blue_jeans.jpg", 40, "This is a nice pair of jeans"));
        listings.put("item5", new InventoryItem("item5", "Chelsea Boots", "chelsea_boots.jpg", 50, "A comfortable pair of boots"));
        listings.put("item6", new InventoryItem("item6", "Gray Beanie", "gray_beanie.jpg", 15, "A warm, good-looking hat"));
        listings.put("item7", new InventoryItem("item7", "Maroon Scarf", "maroon_scarf.jpg", 15, "A knit scarf"));

        final CountDownLatch writeSignal = new CountDownLatch(1);
        ref.setValue(listings, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    System.out.println("Data could not be saved. " + databaseError.getMessage());
                } else {
                    System.out.println("Data saved successfully.");
                }
                writeSignal.countDown();
            }
        });

        writeSignal.await(10, TimeUnit.SECONDS);

    }
}
