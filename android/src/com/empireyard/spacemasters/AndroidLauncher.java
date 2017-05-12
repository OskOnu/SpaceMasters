package com.empireyard.spacemasters;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.WindowManager;
import android.widget.Toast;

import com.badlogic.gdx.Game;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.backends.android.AndroidApplication;
import com.badlogic.gdx.backends.android.AndroidApplicationConfiguration;
import com.badlogic.gdx.graphics.g2d.freetype.FreeType;
import com.empireyard.spacemasters.gameplay.GameStateManager;
import com.empireyard.spacemasters.tools.PlayServices;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.fitness.data.DataPoint;
import com.google.android.gms.games.Games;
import com.google.android.gms.games.GamesActivityResultCodes;
import com.google.android.gms.games.GamesStatusCodes;
import com.google.android.gms.games.multiplayer.Invitation;
import com.google.android.gms.games.multiplayer.Multiplayer;
import com.google.android.gms.games.multiplayer.OnInvitationReceivedListener;
import com.google.android.gms.games.multiplayer.Participant;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessage;
import com.google.android.gms.games.multiplayer.realtime.RealTimeMessageReceivedListener;
import com.google.android.gms.games.multiplayer.realtime.Room;
import com.google.android.gms.games.multiplayer.realtime.RoomConfig;
import com.google.android.gms.games.multiplayer.realtime.RoomStatusUpdateListener;
import com.google.android.gms.games.multiplayer.realtime.RoomUpdateListener;
import com.google.android.gms.games.snapshot.Snapshot;
import com.google.android.gms.games.snapshot.SnapshotMetadata;
import com.google.android.gms.games.snapshot.SnapshotMetadataChange;
import com.google.android.gms.games.snapshot.Snapshots;
import com.google.example.games.basegameutils.BaseGameUtils;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;

public class AndroidLauncher extends AndroidApplication implements PlayServices, GoogleApiClient.ConnectionCallbacks,
		GoogleApiClient.OnConnectionFailedListener, RealTimeMessageReceivedListener,
		RoomStatusUpdateListener, RoomUpdateListener, OnInvitationReceivedListener{
	private static final String logTag = AndroidLauncher.class.getSimpleName();

//	private GameHelper gameHelper;
//	private  final static int requestCode = 1;

	boolean mMultiplayer = false;

	private String mMyName = null;
	private String mMyId = null;

	private String mPeerName = null;
	private String mPeerId = null;
	private String mRoomId = null;
	private String mIncomingInvitationId = null;

	public String firstPlayerId = null;

	//Other's participants data
	Map<String, Integer> mParticipantsData = new HashMap<String, Integer>();
	//participants in the current screen
	ArrayList<Participant> mParticipants = null;
	//final data
	Set<String> mFinishedParticipantsData = new HashSet<String>();


	private static final int RC_SIGN_IN = 9001;
	private static final int RC_SAVED_GAMES = 9009;

	final static int RC_SELECT_PLAYERS = 10000;
	final static int RC_INVITATION_INBOX = 10001;
	final static int RC_WAITING_ROOM = 10002;
	final static int REQUEST_LEADERBOARD = 10003;
	final static int REQUEST_ACHIEVEMENTS = 10004;




	private GoogleApiClient mGoogleApiClient;

	//Is resolving a connection failure?
	private boolean mResolvingConnectionFailure = false;

	//Is the sign-in button clicked?
	private boolean mSignInClicked = false;

	// If true automatically starts the sign in flow when the Activity starts.
	// If false requires the user to click the button in order to sign in.
	private boolean mAutoStartSignInFlow = true;

	//byte[] mSendMessageBuffer = new byte[20];
	//private RealTimeData mDataReceived;
	byte[] mReceivedMessageBuffer = new byte[100];


	GameMode gameMode;

	//SavedGames
	private static final int MAX_SNAPSHOT_RESOLVE_RETRIES = 3;

	private String mCurrentSaveName = "snapshotTemp";
	PendingResult<Snapshots.CommitSnapshotResult> writeSavedGamesResult;
	Bitmap savedGamesCoverImage;
	Snapshot savedGamesSnapshot;
	private byte[] mSaveGameData;

	@Override
	protected void onCreate (Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		gameMode = GameMode.NONE;
//		gameHelper = new GameHelper(this, GameHelper.CLIENT_GAMES);
//		gameHelper.enableDebugLog(false);
//
//		GameHelper.GameHelperListener gameHelperListener = new GameHelper.GameHelperListener() {
//			@Override
//			public void onSignInFailed() {
//
//			}
//
//			@Override
//			public void onSignInSucceeded() {
//
//			}
//		};
//
//		gameHelper.setup(gameHelperListener);

		mGoogleApiClient = new GoogleApiClient.Builder(this).addConnectionCallbacks(this)
				.addOnConnectionFailedListener(this).addOnConnectionFailedListener(this)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.addApi(Drive.API).addScope(Drive.SCOPE_APPFOLDER)
				.addApi(Games.API).addScope(Games.SCOPE_GAMES)
				.build();

		AndroidApplicationConfiguration config = new AndroidApplicationConfiguration();
		initialize(new SpaceMasters(this), config);

		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

//	@Override
//	public boolean onKeyDown(int keyCode, KeyEvent keyEvent){
//		if(keyCode == KeyEvent.KEYCODE_BACK && currentScreen == R.id.multiplayer_gameplay){
//			spaceMastersGameplay.myGame.gameState.setState("END");
//			leaveRoom();
//			return true;
//		}else {
//			spaceMastersGameplay.myGame.gameState.setState("END");
//			Gdx.app.log(logTag, "!!Switch to sign in.??");
//		}
//		return super.onKeyDown(keyCode, keyEvent);
//	}

	@Override
	protected void onStart(){
		if(mGoogleApiClient == null){
			Gdx.app.log(logTag, "Switch to main menu screen.");
		}else if(!mGoogleApiClient.isConnected()){
			Gdx.app.log(logTag, "Connecting client.");
			Gdx.app.log(logTag, "Switch to waiting screen.");
			mGoogleApiClient.connect();
		} else {
			Gdx.app.log(logTag, "Client was already connected");
		}
		Gdx.app.log(logTag, "Object is beeing started.");
		super.onStart();
	}

	@Override
	protected void onResume(){
		super.onResume();
		Gdx.app.log(logTag, "Object is being resumed.");
	}

	@Override
	protected void onPause(){
		super.onPause();
		Gdx.app.log(logTag, "Object is being paused.");
	}

	@Override
	protected void onStop(){
		leaveRoom();

		//start keeping screen on
		if(mGoogleApiClient != null && mGoogleApiClient.isConnected()){
			Gdx.app.log(logTag, "Switch to main menu screen.");
		}else {
			Gdx.app.log(logTag, "Switch to sign in screen.");
		}
		Gdx.app.log(logTag, "Object is beeing sopped.");
		super.onStop();
	}

	@Override
	protected void onDestroy(){
		super.onDestroy();
		Gdx.app.log(logTag, "Object is beeing destroyed.");
	}

	@Override
	public void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
		if(requestCode == RC_SIGN_IN){
			mSignInClicked = false;
			mResolvingConnectionFailure = false;
//            if(!BaseGameUtils.verifySampleSetup(this, R.string.app_id)){
//                Gdx.app.log(logTag, "Setup problems detected. Sign in may not work!");
//            }
			if(resultCode == RESULT_OK){
				mGoogleApiClient.connect();
			}else {
				BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.sign_in_failure);
			}
		}if(requestCode == RC_SELECT_PLAYERS) {
			if (resultCode == RESULT_OK) {
				Gdx.app.log(logTag, "Selecting players succeed");
				//get invited people
				Bundle extras = data.getExtras();
				final ArrayList<String> invited = data.getStringArrayListExtra(Games.EXTRA_PLAYER_IDS);

				//get auto-match criteria
				Bundle autoMatchCriteria = null;
				int minAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MIN_AUTOMATCH_PLAYERS, 0);
				int maxAutoMatchPlayers = data.getIntExtra(Multiplayer.EXTRA_MAX_AUTOMATCH_PLAYERS, 0);

				if (minAutoMatchPlayers > 0 || maxAutoMatchPlayers > 0) {
					autoMatchCriteria = RoomConfig.createAutoMatchCriteria(minAutoMatchPlayers, maxAutoMatchPlayers, 0);
				} else {
					autoMatchCriteria = null;
				}

				//create the room
				RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
				roomConfigBuilder.addPlayersToInvite(invited);
				roomConfigBuilder.setMessageReceivedListener(this);
				roomConfigBuilder.setRoomStatusUpdateListener(this);

				//RoomConfig.Builder roomConfigBuilder = makeBasicRoomConfigBuilder();

				if (autoMatchCriteria != null) {
					roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
				}
				Gdx.app.log(logTag, "Switch to waiting screen.");
				//Start keeping screen on
				//reset game?
				RoomConfig roomConfig = roomConfigBuilder.build();
				Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);

				Gdx.app.log(logTag, "Room created, waiting for finish.");
			}else {
				Gdx.app.log(logTag, "Selecting players canceled. Response: " + resultCode);
				BaseGameUtils.showActivityResultError(this, requestCode, resultCode, R.string.select_players_failure);
				Gdx.app.log(logTag, "Switch to main screen.");
				// canceled
				return;
			}
		}else if(requestCode == RC_INVITATION_INBOX) {
			if (resultCode == Activity.RESULT_OK) {
				// get the selected invitation
				Bundle extras = data.getExtras();
				Invitation invitation = extras.getParcelable(Multiplayer.EXTRA_INVITATION);

				Gdx.app.log(logTag, "Accepting invitations.");
				// accepting invitations
				RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
				roomConfigBuilder.setInvitationIdToAccept(invitation.getInvitationId())
						.setMessageReceivedListener(this).setRoomStatusUpdateListener(this);
				Gdx.app.log(logTag, "Switch to waiting screen.");
				//start keeping screen on
				Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
			} else {
				Gdx.app.log(logTag, "Invitations inbox canceled. Response: " + resultCode);
				Gdx.app.log(logTag, "Switch to main screen.");
				// canceled
				return;
			}
		}else if(requestCode == RC_WAITING_ROOM){
			if (resultCode == Activity.RESULT_OK) {
				Gdx.app.log(logTag, "Starting game.");

				//SpaceMasters.setGameMode(SpaceMasters.GameMode.MULTI_PLAYER_READY);
				startGame(gameMode);


				//start game

			}else if (resultCode == GamesActivityResultCodes.RESULT_LEFT_ROOM) {
				leaveRoom();
			}else if (resultCode == Activity.RESULT_CANCELED) {
				leaveRoom();
			}
		}else if(requestCode == REQUEST_LEADERBOARD){
				Gdx.app.log(logTag, "REQUEST_LEADERBOARD.");
		}else if(requestCode == REQUEST_ACHIEVEMENTS){
				Gdx.app.log(logTag, "REQUEST_ACHIEVEMENTS.");

		}else if (data.hasExtra(Snapshots.EXTRA_SNAPSHOT_METADATA)) {
			// Load a snapshot.
			SnapshotMetadata snapshotMetadata = (SnapshotMetadata)
					data.getParcelableExtra(Snapshots.EXTRA_SNAPSHOT_METADATA);
			mCurrentSaveName = snapshotMetadata.getUniqueName();

			// Load the game data from the Snapshot
			// ...
		} else if (data.hasExtra(Snapshots.EXTRA_SNAPSHOT_NEW)) {
			// Create a new snapshot named with a unique string
			String unique = new BigInteger(281, new Random()).toString(13);
			mCurrentSaveName = "snapshotTemp-" + unique;

			// Create the new snapshot
			// ...
		}
	}

	@Override
	public void  startQuickGame(long role){
		if(role == ROLE_ENEMY){
			gameMode = GameMode.VERSUS;
		}else if(role == ROLE_ALLAY){
			gameMode = GameMode.COOP;
		}else{
			gameMode = GameMode.COOP;
		}
		//configure auto-match criteria
		Bundle autoMatchCriteria = RoomConfig.createAutoMatchCriteria(MIN_PLAYERS, MAX_PLAYERS, 0);

		//build room
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setMessageReceivedListener(this);
		roomConfigBuilder.setRoomStatusUpdateListener(this);
		roomConfigBuilder.setAutoMatchCriteria(autoMatchCriteria);
		Gdx.app.log(logTag, "Switch to waiting screen");

		//!!keep screen on

		RoomConfig roomConfig = roomConfigBuilder.build();

		//create room
		Games.RealTimeMultiplayer.create(mGoogleApiClient, roomConfig);
	}

	@Override
	public void startCustomGame() {
		Intent intent = Games.RealTimeMultiplayer.getSelectOpponentsIntent(mGoogleApiClient, 1, 3);
		Gdx.app.log(logTag, "Switch to waiting screen.");
		startActivityForResult(intent, RC_SELECT_PLAYERS);
	}

	@Override
	public void showInvitations() {
		Intent intent = Games.Invitations.getInvitationInboxIntent(mGoogleApiClient);
		Gdx.app.log(logTag, "Switch to waiting screen.");
		startActivityForResult(intent, RC_INVITATION_INBOX);
	}

	@Override
	public void showSavedGames() {
		int maxNumberOfSavedGamesToShow = 5;
		Intent savedGamesIntent = Games.Snapshots.getSelectSnapshotIntent(mGoogleApiClient,
				"See My Saves", true, true, maxNumberOfSavedGamesToShow);
		startActivityForResult(savedGamesIntent, RC_SAVED_GAMES);

	}

	@Override
	public void writeSavedGames(byte[] data, String desc) {

		// Set the data payload for the snapshot
		savedGamesSnapshot.getSnapshotContents().writeBytes(data);

		// Create the change operation
		SnapshotMetadataChange metadataChange = new SnapshotMetadataChange.Builder()
				.setCoverImage(savedGamesCoverImage)
				.setDescription(desc)
				.build();

		// Commit the operation
		writeSavedGamesResult = Games.Snapshots.commitAndClose(mGoogleApiClient, savedGamesSnapshot, metadataChange);

	}

	@Override
	public void loadSavedGames() {
		// Display a progress dialog
		// ...

		AsyncTask<Void, Void, Integer> task = new AsyncTask<Void, Void, Integer>() {
			@Override
			protected Integer doInBackground(Void... params) {
				// Open the saved game using its name.
				Snapshots.OpenSnapshotResult result = Games.Snapshots.open(mGoogleApiClient,
						mCurrentSaveName, true).await();

				// Check the result of the open operation
				if (result.getStatus().isSuccess()) {
					Snapshot snapshot = result.getSnapshot();
					// Read the byte content of the saved game.
					try {
						mSaveGameData = snapshot.getSnapshotContents().readFully();
					} catch (IOException e) {
						Log.d(logTag, "Error while reading Snapshot: " + e.getMessage().toString());
					}
				} else{
					Log.d(logTag, "Error while loading: " + result.getStatus().getStatusMessage().toString());
				}

				return result.getStatus().getStatusCode();
			}

			@Override
			protected void onPostExecute(Integer status) {
				// Dismiss progress dialog and reflect the changes in the UI.
				// ...
			}
		};

		task.execute();
	}

	//https://developers.google.com/games/services/android/savedgames
	Snapshot processSnapshotOpenResult(Snapshots.OpenSnapshotResult result, int retryCount) {
		Snapshot mResolvedSnapshot = null;
		retryCount++;

		int status = result.getStatus().getStatusCode();
		Log.d(logTag, "Save Result status: " + status);

		if (status == GamesStatusCodes.STATUS_OK) {
			return result.getSnapshot();
		} else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONTENTS_UNAVAILABLE) {
			return result.getSnapshot();
		} else if (status == GamesStatusCodes.STATUS_SNAPSHOT_CONFLICT) {
			Snapshot snapshot = result.getSnapshot();
			Snapshot conflictSnapshot = result.getConflictingSnapshot();

			// Resolve between conflicts by selecting the newest of the conflicting snapshots.
			mResolvedSnapshot = snapshot;

			if (snapshot.getMetadata().getLastModifiedTimestamp() <
					conflictSnapshot.getMetadata().getLastModifiedTimestamp()) {
				mResolvedSnapshot = conflictSnapshot;
			}

			Snapshots.OpenSnapshotResult resolveResult = Games.Snapshots.resolveConflict(
					mGoogleApiClient, result.getConflictId(), mResolvedSnapshot).await();

			if (retryCount < MAX_SNAPSHOT_RESOLVE_RETRIES) {
				// Recursively attempt again
				return processSnapshotOpenResult(resolveResult, retryCount);
			} else {
				// Failed, log error and show Toast to the user
				String message = "Could not resolve snapshot conflicts";
				Log.d(logTag, message);
				Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
			}

		}

		// Fail, return null.
		return null;
	}


	void acceptInviteToRoom(String invId) {
		// accept the invitation
		Gdx.app.log(logTag, "Accepting invitation");
		RoomConfig.Builder roomConfigBuilder = RoomConfig.builder(this);
		roomConfigBuilder.setInvitationIdToAccept(invId)
				.setMessageReceivedListener(this)
				.setRoomStatusUpdateListener(this);
		Gdx.app.log(logTag, "Switch to waiting screen");
		//!!start keeping screen on
		Games.RealTimeMultiplayer.join(mGoogleApiClient, roomConfigBuilder.build());
	}

	void updateRoom(Room room){
		if(room != null){
			mParticipants = room.getParticipants();
		}
		if(mParticipants != null){
			//!!updatePeerDataDisplay();
		}
	}

	void showWaitingRoom(Room room){
		final int MIN_PLAYERS = Integer.MAX_VALUE;
		Intent intent = Games.RealTimeMultiplayer.getWaitingRoomIntent(mGoogleApiClient, room, MIN_PLAYERS);

		startActivityForResult(intent, RC_WAITING_ROOM);
	}

	@Override
	public void signIn() {
		try{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//gameHelper.beginUserInitiatedSignIn();
				}
			});
		}catch (Exception e){
			Gdx.app.log("MainActivity", "Log in failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void signOut() {
		try{
			runOnUiThread(new Runnable() {
				@Override
				public void run() {
					//gameHelper.signOut();
				}
			});
		}catch (Exception e){
			Gdx.app.log("MainActivity", "Log out failed: " + e.getMessage() + ".");
		}
	}

	@Override
	public void rateGame() {
		String uriString = "My PlayStore Link"; //!!!!Remember to change!!!!
		startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(uriString)));
	}

	@Override
	public void displayVersusLeaderboard() {
		if(isSignedIn() == true) {
			//startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, R.id.), REQUEST_LEADERBOARD);
		}else {
			signIn();
		}
	}

	@Override
	public void displayCoOpLeaderboard() {
		if(isSignedIn() == true) {
			//startActivityForResult(Games.Leaderboards.getLeaderboardIntent(mGoogleApiClient, LEADERBOARD_ID), REQUEST_LEADERBOARD);
		}else {
			signIn();
		}
	}

	@Override
	public void submitVersusScore(int highScore) {
		if(isSignedIn() == true){
			//Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
		}else {
			signIn();
		}
	}

	@Override
	public void submitCoOpScore(int highScore) {
		if(isSignedIn() == true){
			//Games.Leaderboards.submitScore(mGoogleApiClient, LEADERBOARD_ID, score);
		}else {
			signIn();
		}
	}

	@Override
	public void unlockAchievement() {
		if(isSignedIn() == true){
			//Games.Achievements.unlock(mGoogleApiClient, "my_achievement_id");
		}else {
			signIn();
		}
	}

	@Override
	public void incrementAchievement() {
		if(isSignedIn() == true){
			//Games.Achievements.increment(mGoogleApiClient, "my_incremental_achievment_id", 1);
		}else {
			signIn();
		}
	}


	@Override
	public void displayAchievements() {
		if(isSignedIn() == true){
			//startActivityForResult(Games.Achievements.getAchievementsIntent(mGoogleApiClient), REQUEST_ACHIEVEMENTS);
		}else {
			signIn();
		}
	}

	@Override
	public boolean isSignedIn() {
		//return gameHelper.isSignedIn();
		return  mGoogleApiClient.isConnected();
	}

	@Override
	public void leaveRoom(){
		Gdx.app.log(logTag, "Leaving room.");
		//!!stop keeping screen on
		if(mRoomId != null){
			Games.RealTimeMultiplayer.leave(mGoogleApiClient, this, mRoomId);
			mRoomId = null;
			for(Participant mparticipant : mParticipants){
				mparticipant = null;
				mPeerId = null;
				mPeerName = null;
			}
			Gdx.app.log(logTag, "Switch to waiting screen");
		}else {
			Gdx.app.log(logTag, "Switch to main screen.");
		}
		SpaceMasters.gameStateManager.set(GameStateManager.GameState.QUIT);
	}

	@Override
	public byte[] getReceivedMessageBuffer() {
		//broadcastData();
		return mReceivedMessageBuffer;
	}

	@Override
	public String getMyName() {
		return mMyName;
	}

	@Override
	public String getMyId() {
		return mMyId;
	}

	@Override
	public String getPeerName() {
		return mPeerName;
	}

	@Override
	public GameMode getGameMode() {
		return gameMode;
	}

	@Override
	public void setGameMode(GameMode gameMode) {
		this.gameMode = gameMode;
	}

	@Override
	public String getPeerId() {
		return mPeerId;
	}


	@Override
	public void onRealTimeMessageReceived(RealTimeMessage realTimeMessage) {
		mReceivedMessageBuffer = realTimeMessage.getMessageData();
		mPeerId = realTimeMessage.getSenderParticipantId();

		//ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(mReceivedMessageBuffer);
		//Gdx.app.log(logTag, "mReceivedMessageBuffer: " + mReceivedMessageBuffer.toString());

	}

	@Override
	public void broadcastData(byte[] sendMessageBuffer, boolean reliable) {
		//Gdx.app.log(logTag, "mMultiplayer: " + mMultiplayer + "mRoomId: " + mRoomId  + "mRoomId.isEmpty(): " + mRoomId.isEmpty());
		if(!mMultiplayer || mRoomId == null || mRoomId.isEmpty()){
			return;
		}

//		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(sendMessageBuffer);
//		Gdx.app.log(logTag, "Data mMessageBuffer[1]: " + Integer.toBinaryString(sendMessageBuffer[2]&0xFF));
//		Gdx.app.log(logTag, "Data mMessageBuffer[2]: " + Integer.toBinaryString(sendMessageBuffer[3]&0x00FF));
//		Gdx.app.log(logTag, "Data mMessageBuffer[3]: " + Integer.toBinaryString(sendMessageBuffer[4]&0xFF));
//		Gdx.app.log(logTag, "Data mMessageBuffer[4]: " + Integer.toBinaryString(sendMessageBuffer[5]&0x00FF));

		for (Participant participant : mParticipants) {
			if (participant.getParticipantId().equals(mMyId)) {
				mMyName = participant.getDisplayName();
				continue;
			}
			if (participant.getStatus() != Participant.STATUS_JOINED) {
				continue;
			}

			try {
				mPeerName = participant.getDisplayName(); //!!only for 2players version
				if (reliable) {
					Gdx.app.log(logTag, "broadcastData: reliable");
					// final data notification must be sent via reliable message
					Games.RealTimeMultiplayer.sendReliableMessage(mGoogleApiClient, null, sendMessageBuffer,
							mRoomId, participant.getParticipantId());
				} else {
					Gdx.app.log(logTag, "broadcastData: unreliable");
					//Gdx.app.log(logTag, "mReceivedMessageBuffer: " + mReceivedMessageBuffer);
					// it's an interim score notification, so we can use unreliable
					Games.RealTimeMultiplayer.sendUnreliableMessage(mGoogleApiClient, sendMessageBuffer, mRoomId,
							participant.getParticipantId());
				}
			}catch (Exception e){
				Gdx.app.log(logTag, "broadcastData Exception : " + e.getMessage().toString() + ".");
			}
		}
	}

	@Override
	public void resetGame() {
		mParticipantsData.clear();
		mFinishedParticipantsData.clear();
	}

	@Override
	public void startGame(GameMode gameMode) {
		mMultiplayer = true;
		if(gameMode == GameMode.VERSUS) {
			keepScreenOn();
			//SpaceMasters.setGameMode(SpaceMasters.GameMode.QUICK_GAME_VERSUS_READY);
			SpaceMasters.gameStateManager.set(GameStateManager.GameState.QUICK_GAME_VERSUS_READY);
		}else if (gameMode == GameMode.COOP){
			keepScreenOn();
			SpaceMasters.gameStateManager.set(GameStateManager.GameState.QUICK_GAME_COOP_READY);
			//SpaceMasters.setGameMode(SpaceMasters.GameMode.QUICK_GAME_COOP_READY);
		}
		Gdx.app.log(logTag, "Switch to multiplayer game screen: " + gameMode.name().toString() );
	}

	@Override
	public void onRoomCreated(int statusCode, Room room) {
		Gdx.app.log(logTag, "onRoomCreated: " + statusCode + ", " + room + ".");
		if(statusCode != GamesStatusCodes.STATUS_OK){
			Gdx.app.log(logTag, "showGameError();");
			Gdx.app.log(logTag, "onRoomCreated: " + statusCode + ".");
			return;
		}

		mRoomId = room.getRoomId();

		showWaitingRoom(room);
	}

	@Override
	public void onJoinedRoom(int statusCode, Room room) {
		Gdx.app.log(logTag, "onJoinedRoom: " + statusCode + ", " + room + ".");
		if(statusCode != GamesStatusCodes.STATUS_OK){
			Gdx.app.log(logTag, "Error: " + statusCode + ".");
			Gdx.app.log(logTag, "showGameError();");
			return;
		}
		showWaitingRoom(room);
	}

	@Override
	public void onLeftRoom(int statusCode, String roomId) {
		Gdx.app.log(logTag, "onLeftRoom: " + statusCode);
		Gdx.app.log(logTag, "Switch to main menu screen.");
	}

	@Override
	public void onRoomConnected(int statusCode, Room room) {
		Gdx.app.log(logTag, "onRoomConnected: " + statusCode + ", " + room + ".");
		if(statusCode != GamesStatusCodes.STATUS_OK){
			Gdx.app.log(logTag, "Error: " + statusCode + ".");
			Gdx.app.log(logTag, "showGameError();");
			return;
		}
		updateRoom(room);
		// update players state

	}

	@Override
	public void onRoomConnecting(Room room) {
		Gdx.app.log(logTag, "onRoomConnecting");

		updateRoom(room);
	}

	@Override
	public void onRoomAutoMatching(Room room) {
		Gdx.app.log(logTag, "onRoomAutoMatching");

		updateRoom(room);
	}

	@Override
	public void onPeerInvitedToRoom(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeerInvitedToRoom");

		updateRoom(room);
	}

	@Override
	public void onPeerDeclined(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeerDeclined");

		updateRoom(room);
	}

	@Override
	public void onPeerJoined(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeerJoined");

		updateRoom(room);
	}

	@Override
	public void onPeerLeft(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeerLeft");
		SpaceMasters.gameStateManager.set(GameStateManager.GameState.PEER_LEFT);
		updateRoom(room);
	}

	@Override
	public void onConnectedToRoom(Room room) {

		mParticipants = room.getParticipants();
		mMyId = room.getParticipantId(Games.Players.getCurrentPlayerId(mGoogleApiClient));

		if(mRoomId == null){
			mRoomId = room.getRoomId();
		}
		Gdx.app.log(logTag, "onConnectedToRoom: " + "Room ID: " + mRoomId + " | \n" + "My ID: " + mMyId);
	}

	@Override
	public void onDisconnectedFromRoom(Room room) {
		Gdx.app.log(logTag, "onDisconnectedFromRoom");
		//SpaceMasters.setGameMode(SpaceMasters.GameMode.QUIT);
		mRoomId = null;
		Gdx.app.log(logTag, "showGameError();");
	}

	@Override
	public void onPeersConnected(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeersConnected");

		updateRoom(room);
	}

	@Override
	public void onPeersDisconnected(Room room, List<String> list) {
		Gdx.app.log(logTag, "onPeersDisconnected");
		//SpaceMasters.setGameMode(SpaceMasters.GameMode.QUIT);
		updateRoom(room);
	}

	@Override
	public void onP2PConnected(String s) {
		Gdx.app.log(logTag, "onP2PConnected");

	}

	@Override
	public void onP2PDisconnected(String s) {
		Gdx.app.log(logTag, "onP2PDisconnected");
		//SpaceMasters.setGameMode(SpaceMasters.GameMode.QUIT);
	}
	@Override
	public void onConnected(Bundle connectionHint) {
		Gdx.app.log(logTag, "onConnected: sign in successful!");

		Games.Invitations.registerInvitationListener(mGoogleApiClient, this);
		if (connectionHint != null) {
			Invitation invitation = connectionHint.getParcelable(Multiplayer.EXTRA_INVITATION);

			if (invitation != null) {
				Gdx.app.log(logTag, "onConnected: the hint has a rom invite.");
				acceptInviteToRoom(invitation.getInvitationId());
				return;
				// go to game screen
			}
		}
		Gdx.app.log(logTag, "Switch to main menu screen");
	}

	@Override
	public void onConnectionSuspended(int i) {
		Gdx.app.log(logTag, "onConnectionSuspended: trying to reconnect.");
		//try to reconnect
		mGoogleApiClient.connect();
	}

	@Override
	public void onConnectionFailed(ConnectionResult connectionResult) {
		Gdx.app.log(logTag, "onConnectionFailed: " + connectionResult);
		if(mResolvingConnectionFailure){
			Gdx.app.log(logTag, "ignoring onConnectionFailed: already resolving");
			//already resolving
			return;
		}

		//if sign-in is clicked or active launch sign in flow
		if(mSignInClicked || mAutoStartSignInFlow){
			mAutoStartSignInFlow = false;
			mSignInClicked = false;
			mResolvingConnectionFailure = true;

			if(!BaseGameUtils.resolveConnectionFailure(this, mGoogleApiClient, connectionResult,
					RC_SIGN_IN, getString(R.string.signin_other_error))){
				mResolvingConnectionFailure = false;
			}
		}
		Gdx.app.log(logTag, "Switch to sign in screen");
	}

	@Override
	public void onInvitationReceived(Invitation invitation) {
		//in game pop-out shown and stored for later
		mIncomingInvitationId = invitation.getInvitationId();
//!!!		((TextView) findViewById(R.id.invitation_popup_text)).setText(
//!!!				invitation.getInviter().getDisplayName() + " " + getString(R.string.is_inviting_you)
//!!!		);
		Gdx.app.log(logTag, "onInvitationReceived");
		Gdx.app.log(logTag, "Switch to current screen");
	}

	@Override
	public void onInvitationRemoved(String invitationId) {
		if((mIncomingInvitationId.equals(invitationId)) && (mIncomingInvitationId != null)){
			mIncomingInvitationId = null;
			Gdx.app.log(logTag, "Switch to current screen");
		}
	}


	public void keepScreenOn(){
		getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}

	public void stopKeepScreenOn(){
		getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
	}
}
