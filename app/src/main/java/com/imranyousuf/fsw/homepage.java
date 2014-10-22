package com.imranyousuf.fsw;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;

import com.imranyousuf.fsw.drawings.DrawSpace;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.Constants;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.Card;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.ListCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.NotificationTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.card.SimpleTextCard;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.DeckOfCardsManager;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.DeckOfCardsEventListener;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCards;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteDeckOfCardsException;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteResourceStore;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.remote.RemoteToqNotification;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.CardImage;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.util.ParcelableUtil;
import java.util.HashMap;
import com.qualcomm.toq.smartwatch.api.v1.deckofcards.resource.DeckOfCardsLauncherIcon;
import java.io.InputStream;
import java.util.Iterator;

import android.app.Activity;

import java.util.Random;



public class homepage extends Activity {

    private final static String PREFS_FILE = "prefs_file";
    private final static String DECK_OF_CARDS_KEY = "deck_of_cards_key";
    private final static String DECK_OF_CARDS_VERSION_KEY = "deck_of_cards_version_key";

    private DeckOfCardsManager mDeckOfCardsManager;
    private RemoteDeckOfCards mRemoteDeckOfCards;
    private RemoteResourceStore mRemoteResourceStore;
    private Receiver toqReceiver;
    private DeckOfCardsEventListener deckOfCardsEventListener;
    private Bitmap fetchedBM;
    private boolean inZone = false;
    private CardImage[] mCardImages;
    private HashMap<String, String> people;
    private String[] peopleArray = { "Jack Weinberg", "Joan Baez", "Michael Rossman", "Art Goldberg", "Jackie Goldberg", "Jack Weinberg" };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.homepage);

        mDeckOfCardsManager = DeckOfCardsManager.getInstance(getApplicationContext());
        toqReceiver = new Receiver();

        init();
        setupUI();


        this.people = new HashMap();
        this.deckOfCardsEventListener = new DeckOfCardsEventListenerImpl();
        LocationManager localLocationManager = (LocationManager)getSystemService("location");
        LocationListener local1 = new LocationListener()
        {
            public void onLocationChanged(Location paramAnonymousLocation)
            {
                double d1 = paramAnonymousLocation.getLatitude();
                double d2 = paramAnonymousLocation.getLongitude();
                if ((homepage.this.measure(d1, d2, 37.86965D, -122.25914D)) && (homepage.this.inZone != true))
                {

                    System.out.println(homepage.this.measure(d1, d2, 37.86965D, -122.25914D));
                    homepage.this.sendNotification();
                }
                System.out.println(homepage.this.inZone);
            }

            public void onProviderDisabled(String paramAnonymousString)
            {
            }

            public void onProviderEnabled(String paramAnonymousString)
            {
            }

            public void onStatusChanged(String paramAnonymousString, int paramAnonymousInt, Bundle paramAnonymousBundle)
            {
            }
        };
        if (localLocationManager.isProviderEnabled("network"))
            localLocationManager.requestLocationUpdates("network", 10000L, 0.0F, local1);
        if (localLocationManager.isProviderEnabled("gps"))
            localLocationManager.requestLocationUpdates("gps", 10000L, 0.0F, local1);

        this.fetchedBM = ((Bitmap)getIntent().getParcelableExtra("fetchedImage"));
        if (this.fetchedBM != null)
            addRandomFlickrCard(this.fetchedBM);
    }


    public void onClick(View view) {
        switch (view.getId()) {
            case com.imranyousuf.fsw.R.id.drawBtn:
                Intent drawIntent = new Intent(this, DrawSpace.class);
                startActivity(drawIntent);
                break;
        }

    }

    /**
     * @see android.app.Activity#onStart()
     * This is called after onCreate(Bundle) or after onRestart() if the activity has been stopped
     */
    protected void onStart() {
        super.onStart();


        if( mDeckOfCardsManager.isConnected()){

        }

        Log.d(Constants.TAG, "ToqApiDemo.onStart");
        // If not connected, try to connect
        if (!mDeckOfCardsManager.isConnected()) {
            try {
                mDeckOfCardsManager.connect();

            } catch (RemoteDeckOfCardsException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.toq, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //Kossopmia
    private class DeckOfCardsEventListenerImpl
            implements DeckOfCardsEventListener
    {
        private DeckOfCardsEventListenerImpl()
        {
        }

        public void onCardClosed(String paramString)
        {
        }

        public void onCardInvisible(String paramString)
        {
        }

        public void onCardOpen(String paramString)
        {
            homepage.this.runOnUiThread(new Runnable()
            {
                public void run()
                {
                    Intent localIntent = new Intent(homepage.this, DrawSpace.class);
                    homepage.this.startActivity(localIntent);
                }
            });
        }

        public void onCardVisible(String paramString)
        {
        }

        public void onMenuOptionSelected(String paramString1, String paramString2)
        {
        }

        public void onMenuOptionSelected(String paramString1, String paramString2, String paramString3)
        {
        }
    }




    private void setupUI() {
        findViewById(R.id.send_notif_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendNotification();
            }
        });

        findViewById(R.id.install_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                install();
            }
        });

        findViewById(R.id.uninstall_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uninstall();
            }
        });

    }




    private void sendNotification()
    {
        String[] arrayOfString = new String[2];
        int i = new Random().nextInt(6);
        arrayOfString[0] = this.peopleArray[i];
        arrayOfString[1] = "Open app for instruction.";
        NotificationTextCard localNotificationTextCard = new NotificationTextCard(System.currentTimeMillis(), "Drawing Request!", arrayOfString);
        localNotificationTextCard.setShowDivider(true);
        localNotificationTextCard.setVibeAlert(true);
        RemoteToqNotification localRemoteToqNotification = new RemoteToqNotification(this, localNotificationTextCard);
        try
        {
            this.mDeckOfCardsManager.sendNotification(localRemoteToqNotification);
            Toast.makeText(this, "Sent Notification", Toast.LENGTH_SHORT).show();
            return;
        }
        catch (RemoteDeckOfCardsException localRemoteDeckOfCardsException)
        {
            localRemoteDeckOfCardsException.printStackTrace();
            Toast.makeText(this, "Failed to send Notification", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Installs applet to Toq watch if app is not yet installed
     */
    private void install() {
        boolean isInstalled = true;

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (!isInstalled) {
            try {
                mDeckOfCardsManager.installDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
            } catch (RemoteDeckOfCardsException e) {
                e.printStackTrace();
                Toast.makeText(this, "Error: Cannot install application", Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, "App is already installed!", Toast.LENGTH_SHORT).show();
        }

        try {
            storeDeckOfCards();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void uninstall() {
        boolean isInstalled = true;

        try {
            isInstalled = mDeckOfCardsManager.isInstalled();
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Error: Can't determine if app is installed", Toast.LENGTH_SHORT).show();
        }

        if (isInstalled) {
            try {
                mDeckOfCardsManager.uninstallDeckOfCards();
            } catch (RemoteDeckOfCardsException e) {
                Toast.makeText(this, getString(R.string.error_uninstalling_deck_of_cards), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.already_uninstalled), Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Adds a deck of cards to the applet
     */

    private void addSimpleTextCard0() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }
        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));


        simpleTextCard.setHeaderText("Jack Weinberg");
        simpleTextCard.setTitleText("Draw \"FSM\"");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[0]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        listCard.add(simpleTextCard);


        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSimpleTextCard1() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }


        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));
        simpleTextCard.setHeaderText("Art Goldberg");
        simpleTextCard.setTitleText("Draw \"Now\"");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[3]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        listCard.add(simpleTextCard);

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSimpleTextCard2() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }

        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));
        simpleTextCard.setHeaderText("Mario Savio");
        simpleTextCard.setTitleText("Express your own view of free speech in an image");
        mRemoteResourceStore.addResource(mCardImages[5]);
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[5]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        listCard.add(simpleTextCard);

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSimpleTextCard3() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }

        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));
        simpleTextCard.setHeaderText("Jakie Goldberg");
        simpleTextCard.setTitleText("Draw Slate");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[4]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        listCard.add(simpleTextCard);


        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }


    private void addSimpleTextCard4() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }

        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));
        simpleTextCard.setHeaderText("Joan Baez");
        simpleTextCard.setTitleText("Draw Image of: A Megaphone");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[1]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);

        listCard.add(simpleTextCard);


        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private void addSimpleTextCard() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        int currSize = listCard.size();

        // Create a SimpleTextCard with 1 + the current number of SimpleTextCards

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }

        SimpleTextCard simpleTextCard = new SimpleTextCard(Integer.toString(currSize + 1));
        simpleTextCard.setHeaderText("Mario Savio");
        simpleTextCard.setTitleText("Express your own view of free speech in an image");
        mRemoteResourceStore.addResource(mCardImages[5]);
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[5]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);
        listCard.add(simpleTextCard);


        simpleTextCard.setHeaderText("Jack Weinberg");
        simpleTextCard.setTitleText("Draw \"FSM\"");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[0]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);

        simpleTextCard.setHeaderText("Joan Baez");
        simpleTextCard.setTitleText("Draw Image of: A Megaphone");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[1]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);

        simpleTextCard.setHeaderText("Jakie Goldberg");
        simpleTextCard.setTitleText("Draw Slate");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[4]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);


        simpleTextCard.setHeaderText("Michael Rossmann");
        simpleTextCard.setTitleText("Draw Free Speech");
        simpleTextCard.setCardImage(mRemoteResourceStore, mCardImages[2]);
        simpleTextCard.setReceivingEvents(true);
        simpleTextCard.setShowDivider(true);


        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards, mRemoteResourceStore);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }


    protected void addRandomFlickrCard(Bitmap paramBitmap) {
        ListCard localListCard = this.mRemoteDeckOfCards.getListCard();
        SimpleTextCard localSimpleTextCard1 = (SimpleTextCard) localListCard.get("card7");
        System.out.println("HERE I AM " + localSimpleTextCard1);
        if (localSimpleTextCard1 != null) {
            localListCard.remove(localSimpleTextCard1);
            System.out.println("REMOVED");
        }
        SimpleTextCard localSimpleTextCard2 = new SimpleTextCard("card7");
        localSimpleTextCard2.setHeaderText("Art by others");
        CardImage localCardImage = new CardImage("card.image.7", paramBitmap);
        this.mRemoteResourceStore.addResource(localCardImage);
        localSimpleTextCard2.setCardImage(this.mRemoteResourceStore, localCardImage);
        localSimpleTextCard2.setReceivingEvents(false);
        localSimpleTextCard2.setShowDivider(true);
        localListCard.add(localSimpleTextCard2);
        try {
            this.mDeckOfCardsManager.updateDeckOfCards(this.mRemoteDeckOfCards, this.mRemoteResourceStore);
            startActivity(new Intent(getApplicationContext(), DrawSpace.class));
            return;
        } catch (RemoteDeckOfCardsException localRemoteDeckOfCardsException) {
            localRemoteDeckOfCardsException.printStackTrace();
            Toast.makeText(this, "Failed to Create SimpleTextCard", Toast.LENGTH_SHORT).show();
        }
    }

    private void removeDeckOfCards() {
        ListCard listCard = mRemoteDeckOfCards.getListCard();
        if (listCard.size() == 0) {
            return;
        }

        listCard.remove(0);

        try {
            mDeckOfCardsManager.updateDeckOfCards(mRemoteDeckOfCards);
        } catch (RemoteDeckOfCardsException e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to delete Card from ListCard", Toast.LENGTH_SHORT).show();
        }

    }



    // Initialise
    private void init() {

        // Create the resource store for icons and images
        mRemoteResourceStore = new RemoteResourceStore();

        DeckOfCardsLauncherIcon whiteIcon = null;
        DeckOfCardsLauncherIcon colorIcon = null;

        // Get the launcher icons
        try {
            whiteIcon = new DeckOfCardsLauncherIcon("white.launcher.icon", getBitmap("bw.png"), DeckOfCardsLauncherIcon.WHITE);
            colorIcon = new DeckOfCardsLauncherIcon("color.launcher.icon", getBitmap("color.png"), DeckOfCardsLauncherIcon.COLOR);
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get launcher icon");
            return;
        }

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
            return;
        }
        // Try to retrieve a stored deck of cards
        try {
            // If there is no stored deck of cards or it is unusable, then create new and store
            if ((mRemoteDeckOfCards = getStoredDeckOfCards()) == null) {
                mRemoteDeckOfCards = createDeckOfCards();
                storeDeckOfCards();
            }
        } catch (Throwable th) {
            th.printStackTrace();
            mRemoteDeckOfCards = null; // Reset to force recreate
        }

        // Make sure in usable state
        if (mRemoteDeckOfCards == null) {
            mRemoteDeckOfCards = createDeckOfCards();
        }

        // Set the custom launcher icons, adding them to the resource store
        mRemoteDeckOfCards.setLauncherIcons(mRemoteResourceStore, new DeckOfCardsLauncherIcon[]{whiteIcon, colorIcon});

        // Re-populate the resource store with any card images being used by any of the cards
        for (Iterator<Card> it = mRemoteDeckOfCards.getListCard().iterator(); it.hasNext(); ) {

            String cardImageId = ((SimpleTextCard) it.next()).getCardImageId();

            if ((cardImageId != null) && !mRemoteResourceStore.containsId(cardImageId)) {

                if (cardImageId.equals("card.image.1")) {
                    mRemoteResourceStore.addResource(mCardImages[0]);
                }

            }
        }
    }


    private RemoteDeckOfCards createDeckOfCards() {

        mCardImages = new CardImage[6];
        try {
            mCardImages[0] = new CardImage("card.image.1", getBitmap("jack.png"));
            mCardImages[1] = new CardImage("card.image.2", getBitmap("joan.png"));
            mCardImages[2] = new CardImage("card.image.3", getBitmap("rossman.png"));
            mCardImages[3] = new CardImage("card.image.4", getBitmap("art.png"));
            mCardImages[4] = new CardImage("card.image.5", getBitmap("goldberg.png"));
            mCardImages[5] = new CardImage("card.image.6", getBitmap("marios.png"));
        } catch (Exception e) {
            e.printStackTrace();
            System.out.println("Can't get picture icon");
        }

        ListCard localListCard = new ListCard();
        SimpleTextCard localSimpleTextCard1 = new SimpleTextCard("card1");
        localSimpleTextCard1.setHeaderText("Jack Weinberg");
        localSimpleTextCard1.setTitleText("Draw \"FSM\"");
        localSimpleTextCard1.setCardImage(this.mRemoteResourceStore,mCardImages[0]);
        localSimpleTextCard1.setReceivingEvents(true);
        localSimpleTextCard1.setShowDivider(true);
        localListCard.add(localSimpleTextCard1);
        SimpleTextCard localSimpleTextCard2 = new SimpleTextCard("card2");
        localSimpleTextCard2.setHeaderText("Joan Baez");
        localSimpleTextCard2.setTitleText("Draw a Megaphone");
        localSimpleTextCard2.setCardImage(this.mRemoteResourceStore,mCardImages[1]);
        localSimpleTextCard2.setReceivingEvents(true);
        localSimpleTextCard2.setShowDivider(true);
        localListCard.add(localSimpleTextCard2);
        SimpleTextCard localSimpleTextCard3 = new SimpleTextCard("card3");
        localSimpleTextCard3.setHeaderText("Michael Rossman");
        localSimpleTextCard3.setTitleText("Draw \"Free Speech\"");
        localSimpleTextCard3.setCardImage(this.mRemoteResourceStore,mCardImages[2]);
        localSimpleTextCard3.setReceivingEvents(true);
        localSimpleTextCard3.setShowDivider(true);
        localListCard.add(localSimpleTextCard3);
        SimpleTextCard localSimpleTextCard4 = new SimpleTextCard("card4");
        localSimpleTextCard4.setHeaderText("Art Goldberg");
        localSimpleTextCard4.setTitleText("Draw \"Now\"");
        localSimpleTextCard4.setCardImage(this.mRemoteResourceStore, mCardImages[3]);
        localSimpleTextCard4.setReceivingEvents(true);
        localSimpleTextCard4.setShowDivider(true);
        localListCard.add(localSimpleTextCard4);
        SimpleTextCard localSimpleTextCard5 = new SimpleTextCard("card5");
        localSimpleTextCard5.setHeaderText("Jackie Goldberg");
        localSimpleTextCard5.setTitleText("Draw \"SLATE\"");
        localSimpleTextCard5.setCardImage(this.mRemoteResourceStore, mCardImages[4]);
        localSimpleTextCard5.setReceivingEvents(true);
        localSimpleTextCard5.setShowDivider(true);
        localListCard.add(localSimpleTextCard5);
        SimpleTextCard localSimpleTextCard6 = new SimpleTextCard("card6");
        localSimpleTextCard6.setHeaderText("Mario Savio");
        localSimpleTextCard6.setTitleText("Express your own view of Free Speech in an drawing");
        localSimpleTextCard6.setCardImage(this.mRemoteResourceStore, mCardImages[5]);
        localSimpleTextCard6.setReceivingEvents(true);
        localSimpleTextCard6.setShowDivider(true);
        localListCard.add(localSimpleTextCard6);
        return new RemoteDeckOfCards(this, localListCard);
    }

    // Read an image from assets and return as a bitmap
    private Bitmap getBitmap(String fileName) throws Exception {

        try {
            InputStream is = getAssets().open(fileName);
            return BitmapFactory.decodeStream(is);
        } catch (Exception e) {
            throw new Exception("An error occurred getting the bitmap: " + fileName, e);
        }
    }

    private RemoteDeckOfCards getStoredDeckOfCards() throws Exception {

        if (!isValidDeckOfCards()) {
            Log.w(Constants.TAG, "Stored deck of cards not valid for this version of the demo, recreating...");
            return null;
        }

        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        String deckOfCardsStr = prefs.getString(DECK_OF_CARDS_KEY, null);

        if (deckOfCardsStr == null) {
            return null;
        } else {
            return ParcelableUtil.unmarshall(deckOfCardsStr, RemoteDeckOfCards.CREATOR);
        }

    }

    /**
     * Uses SharedPreferences to store the deck of cards
     * This is mainly used to
     */
    private void storeDeckOfCards() throws Exception {
        // Retrieve and hold the contents of PREFS_FILE, or create one when you retrieve an editor (SharedPreferences.edit())
        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Create new editor with preferences above
        SharedPreferences.Editor editor = prefs.edit();
        // Store an encoded string of the deck of cards with key DECK_OF_CARDS_KEY
        editor.putString(DECK_OF_CARDS_KEY, ParcelableUtil.marshall(mRemoteDeckOfCards));
        // Store the version code with key DECK_OF_CARDS_VERSION_KEY
        editor.putInt(DECK_OF_CARDS_VERSION_KEY, Constants.VERSION_CODE);
        // Commit these changes
        editor.commit();
    }

    private boolean measure(double paramDouble1, double paramDouble2, double paramDouble3, double paramDouble4)
    {
        double d1 = 3.141592653589793D * (paramDouble3 - paramDouble1) / 180.0D;
        double d2 = 3.141592653589793D * (paramDouble4 - paramDouble2) / 180.0D;
        double d3 = Math.sin(d1 / 2.0D) * Math.sin(d1 / 2.0D) + Math.cos(3.141592653589793D * paramDouble1 / 180.0D) * Math.cos(3.141592653589793D * paramDouble3 / 180.0D) * Math.sin(d2 / 2.0D) * Math.sin(d2 / 2.0D);
        double d4 = 6378.1369999999997D * (2.0D * Math.atan2(Math.sqrt(d3), Math.sqrt(1.0D - d3)));
        System.out.println(1000.0D * d4);
        if (1000.0D * d4 <= 50.0D)
            return true;
        return false;
    }
    // Check if the stored deck of cards is valid for this version of the demo
    private boolean isValidDeckOfCards() {

        SharedPreferences prefs = getSharedPreferences(PREFS_FILE, Context.MODE_PRIVATE);
        // Return 0 if DECK_OF_CARDS_VERSION_KEY isn't found
        int deckOfCardsVersion = prefs.getInt(DECK_OF_CARDS_VERSION_KEY, 0);

        return deckOfCardsVersion >= Constants.VERSION_CODE;
    }

    // Create some cards with example content


}
