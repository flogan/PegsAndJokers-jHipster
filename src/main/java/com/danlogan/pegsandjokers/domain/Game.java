package com.danlogan.pegsandjokers.domain;
import java.util.ArrayList;
import java.util.UUID;
import java.util.concurrent.ArrayBlockingQueue;

public class Game {
	public static final String NOT_STARTED = "NOT_STARTED";
	public static final String STARTED = "STARTED";
	
	private final UUID id = java.util.UUID.randomUUID();
	private String status = Game.NOT_STARTED;	
	private ArrayList<Player> players;
	private DeckOfCards drawPile;
	private ArrayList<Card> discardPile;
	private ArrayBlockingQueue<Player> playerQueue;
	private ArrayList<PlayerHand> playerHands;
	private ArrayList<ArrayList<PlayerPosition>> playerPositions;
	private Board board;

	
	public static class Builder{
	
		private ArrayList<Player> players = new ArrayList<Player>();
		private DeckOfCards drawPile;
		private ArrayList<Card> discardPile = new ArrayList<Card>();
		private ArrayBlockingQueue<Player> playerQueue;
		private ArrayList<PlayerHand> playerHands = new ArrayList<PlayerHand>();
		private ArrayList<ArrayList<PlayerPosition>> playerPositions = new ArrayList<ArrayList<PlayerPosition>>();
		private Board board;
		private int defaultNumberOfPlayers=4;
		
		private ArrayList<ArrayList<String>> initialPlayerPositionIds = new ArrayList<ArrayList<String>>();
		
		public static Builder newInstance() {
			
			Builder builder = new Builder();
			//Intialize initialPlayerPostions to an 8 player by 5 position two-dimensional array list
			
			 for (int player=0;player<8;player++)
			 {
				 builder.initialPlayerPositionIds.add(new ArrayList<String>());
				 
				 for (int pos=0;pos<5;pos++)
				 {
					builder.initialPlayerPositionIds.get(player).add(null); 
				 }
			 }
			
			
			return builder;
		}
		
		private Builder() {}
		
		//setter methods to configure builder
		public Builder withPlayerNamed(String playerName)
		{
			int playerNumber = players.size() + 1;
			Player player = Player.Builder.newInstance()
					.withName(playerName)
					.withNumber(playerNumber)
					.build();
			
			players.add(player);
			
			return this;
		}
		
		public Builder withNumberOfPlayers(int numberOfPlayers)
		{
			for (int i=1;i<=numberOfPlayers;i++)
			{
				int playerNumber = players.size()+1;
				Player player = Player.Builder.newInstance()
						.withName("Player " + playerNumber)
						.withNumber(playerNumber)
						.build();
				
				players.add(player);
						
			}
			
			return this;
		}
		
		public Builder withPlayerHand(PlayerHand hand)
		{
			this.playerHands.add(hand);
			return this;
		}
		
		public Builder withPlayerPosition(int playerNumber, int positionNumber, String boardPositionId)
		{
			this.initialPlayerPositionIds.get(playerNumber-1).add(positionNumber-1,boardPositionId);
			return this;
		}

		
		//build method to return a new instance from Builder
		public Game build() {
			
			//if less than 3 players have been provided to the builder use default players
			if (this.players.size() < this.defaultNumberOfPlayers) {
				for(Player defaultPlayer : getDefaultPlayers())
				{
					this.players.add(defaultPlayer);
				}
			}
			
			//Build a queue for players to take turns
			this.playerQueue = new ArrayBlockingQueue<Player>(this.players.size());
			
	
			//Create the game board
			this.board = Board.Builder.newInstance().withNumberOfPlayers(players.size()).build();
			
			//for each player add to player queue, create any missing PlayerHands or PlayerPositions as defaults
			int playerNumber = 0;

			for(Player p : players) {
				this.playerQueue.add(p);

				if (this.playerHands.size()==playerNumber)
				{
					this.playerHands.add(PlayerHand.Builder.newInstance(playerNumber).build());
				}
				
				ArrayList<BoardPosition> playerStartPositions = this.board.getPlayerSides().get(playerNumber).getStartPositions();

				ArrayList<PlayerPosition> playerInitialPositions = new ArrayList<PlayerPosition>();
				
				for (BoardPosition bp : playerStartPositions)
				{
					playerInitialPositions.add(new PlayerPosition(playerNumber+1,bp));
				}
				
				this.playerPositions.add(playerInitialPositions);
				
				//for any initialized player positions... move update the default start positions
				ArrayList<String> thisPlayersInitialPositionIds = null;
				
				if (this.initialPlayerPositionIds.size() > playerNumber)
				{
					thisPlayersInitialPositionIds = this.initialPlayerPositionIds.get(playerNumber);
				}

				int initialPositionNumber = 0;
				for(String positionId : thisPlayersInitialPositionIds)
				{
					if (positionId != null)
					{
						PlayerPosition positionToInitialize = this.playerPositions.get(playerNumber).get(initialPositionNumber);

						try {
							positionToInitialize.moveTo(this.board.getBoardPositionById(positionId));
						}catch(CannotMoveToAPositionYouOccupyException ex)
						{
							throw new RuntimeException("Cannot intialize with board with given initial postions. Duplicate Position found.");
						}
					}

					initialPositionNumber++;
				}
				
				playerNumber++;
			}
			
			
			//set up draw pile with two decks of cards
			this.drawPile = new DeckOfCards();
			this.drawPile.combineDecks(new DeckOfCards());
			
			Game game = new Game(this);
			
					
			return game;
		}
		
		//default to 3 players
		private ArrayList<Player> getDefaultPlayers() {
			
			ArrayList<Player> defaultPlayers = new ArrayList<Player>();
			
			for(int i=players.size()+1;i<=this.defaultNumberOfPlayers;i++) {
				
				Player player = Player.Builder.newInstance()
									.withName("Player " + i)
									.withNumber(i)
									.build();
				
				defaultPlayers.add(player);
				
			}
			
			return defaultPlayers;
			
		}
	}

	public UUID getId() {
		return id;
	}

	//Game Class Methods
	public Game(Builder builder)
	{
		//set all properties from the builder
		players = builder.players;
		drawPile = builder.drawPile;
		playerQueue = builder.playerQueue;
		playerHands = builder.playerHands;
		board = builder.board;
		playerPositions = builder.playerPositions;
		this.discardPile = builder.discardPile;
	}

	public String getStatus() {
		return status;
	}
	
	public String start( ) throws CannotStartGameWithoutPlayersException {
		
		if (players.size() > 2) {
			this.status = Game.STARTED;
		} else {
			throw new CannotStartGameWithoutPlayersException("Game requires at least 3 players. Add players before starting game");
		}
		
		//after game is started shuffle the draw pile and deal the cards
		this.drawPile.shuffle();
		this.deal();
				
		return this.status;
	}

	public ArrayList<Player> getPlayers() {
		return players;
	}

	public int getCardsRemaining() {
		return drawPile.cardsRemaining();
	}
	
	public void takeTurn(PlayerTurn turn) throws PlayerNotFoundException, InvalidGameStateException, InvalidMoveException, PlayerPositionNotFoundException, CannotMoveToAPositionYouOccupyException {

		//Get the PlayerHand for the player taking a turn
		PlayerHand playerHand = this.getPlayerHand(turn.getPlayerNumber());
		
		//Make sure player taking turn is the current player
		int currentPlayerNumber = this.getCurrentPlayer().getNumber();
		
		if (currentPlayerNumber != turn.getPlayerNumber())
		{
			throw new InvalidGameStateException(String.format("It's not your turn.  It is player %d's turn.", currentPlayerNumber));
		}
			
		//Make sure the current player has the card being played in their hand
		if (!playerHand.hasCard(turn.getCardName()))
		{
			throw new InvalidGameStateException("You cannot play a card that is not in your hand.");
		}
		
		//Make the requested move
		switch(turn.getMoveType())
		{
			case START_A_PEG: 
				
				this.handleStartAPegRequest(turn, playerHand);
				break;

			case DISCARD:
				
				this.handleDiscardRequest(turn, playerHand);
				break;
				
			case MOVE_PEG:
				this.handleMovePegRequest(turn, playerHand);
				break;
				
			case SPLIT_MOVE:
				this.handleSplitMoveRequest(turn, playerHand);
				break;
				
			case USE_JOKER:
				this.handleUseJokerRequest(turn, playerHand);
				break;
					
		}
		
		
		//At end of turn, discard the card played, draw a new card, and move player to back of the queue
		playerHand.discardCard(this.discardPile, turn.getCardName());
		playerHand.drawCard(this.drawPile);
		
		Player tempPlayer = playerQueue.remove();
		playerQueue.add(tempPlayer);
	}
	
	private void validatePosition(int playerPositionNumber) throws PlayerPositionNotFoundException
	{
		//Make sure they are requesting a valid position number
		if (playerPositionNumber <1 || playerPositionNumber > 5)
		{
			throw new PlayerPositionNotFoundException(String.format("Position number %d is not a valid position.", playerPositionNumber));
		}
		
	}

	private void handleUseJokerRequest(PlayerTurn turn, PlayerHand playerHand) throws InvalidGameStateException, CannotMoveToAPositionYouOccupyException, PlayerPositionNotFoundException, InvalidMoveException
	{
		//Make sure a valid position is referenced in the request
		this.validatePosition(turn.getPlayerPositionNumber());
		
		//Validate that a Joker is being played
		Card cardBeingPlayed = playerHand.getCard(turn.getCardName());
		
		if(!cardBeingPlayed.isJoker())
		{
			throw new InvalidMoveException(String.format("%s is not a joker.", turn.getCardName()));
		}
		
		//verify that targetBoardPosition is legitimate
		BoardPosition targetBoardPosition = this.board.getBoardPositionById(turn.getTargetBoardPositionId());
		if (targetBoardPosition == null)
		{
			throw new InvalidMoveException(String.format("%s is an invalid targetBoardPositionID", turn.getTargetBoardPositionId()));
		}
		
		//Verify that position is occupied by opponent
		if (!targetBoardPosition.getHasPeg())
		{
			throw new InvalidMoveException(String.format("There is not a peg in position %s", turn.getTargetBoardPositionId()));
		}
		else
		{
			Color pegColor = targetBoardPosition.getPegColor();
			if (pegColor.ordinal() == turn.getPlayerNumber() -1 )
			{
				throw new InvalidMoveException("Cannot use joker to replace your own peg");
			}
		}
		
		PlayerPosition fromPlayerPosition = this.getPlayerPositions(turn.getPlayerNumber()).get(turn.getPlayerPositionNumber()-1);
		this.movePeg(fromPlayerPosition, targetBoardPosition);

	}
	
	private void handleStartAPegRequest(PlayerTurn turn, PlayerHand playerHand) throws InvalidGameStateException, CannotMoveToAPositionYouOccupyException, PlayerPositionNotFoundException

	{
		//Make sure a valid position is referenced in the request
		this.validatePosition(turn.getPlayerPositionNumber());

		//First verify that the card being used for the turn can be used to start a peg
		Card cardBeingPlayed = playerHand.getCard(turn.getCardName());
		
		if(!cardBeingPlayed.canBeUsedToStart())
		{
			throw new InvalidGameStateException(String.format("Cannot use a %s to start a peg.", turn.getCardName()));
		}
		
		//Then verify that the peg being requested to move is in a start position
		int playerPositionNumber = turn.getPlayerPositionNumber();
		PlayerPosition playerPosition = this.getPlayerPositions(turn.getPlayerNumber()).get(playerPositionNumber-1);
		
		if (!playerPosition.getPlayerBoardPosition().isStartPosition())
		{
			throw new InvalidGameStateException("Can only start pegs that are in the start position.");
		}
		
		//Lastly make sure there is actually a peg in that start position
		if (!playerPosition.getPlayerBoardPosition().getHasPeg())
		{
			throw new InvalidGameStateException(String.format("Player Postion %d does not have a peg in it.", playerPositionNumber));
		}
		
		//Then update the PlayerPosition to be in the come out position
		BoardPosition comeOutPosition = this.board.getPlayerSides().get(turn.getPlayerNumber()-1).getComeOutPosition();
		
		this.movePeg(playerPosition, comeOutPosition);
		
		return;
	}
	
	private void movePeg(PlayerPosition fromPlayerPosition, BoardPosition toBoardPosition) throws CannotMoveToAPositionYouOccupyException
	{
		//If to position being moved to is occupied by another player, that other players peg will be sent back to start
		if(toBoardPosition.getHasPeg() && !toBoardPosition.getPegColor().equals(fromPlayerPosition.getPegColor()))
		{
			
			//get the player position that corresponds to the target position
			PlayerPosition otherPlayerPosition = this.getPlayerPositionForBoardPosition(toBoardPosition);
			
			//find an available start position for the other player
			BoardPosition availableStartPosition = null;
			ArrayList<BoardPosition> otherPlayerStartPositions = board.getStartPositionsForPlayerNumber(otherPlayerPosition.getPlayerNumber());

			for (BoardPosition boardPosition : otherPlayerStartPositions)
			{
					if (!boardPosition.getHasPeg())
					{
						availableStartPosition = boardPosition;
					}
				
			}

			otherPlayerPosition.moveTo(availableStartPosition);
			
		}
		
		//Make the requested move once the other player has been moved
		fromPlayerPosition.moveTo(toBoardPosition);
	}
	
	private void handleDiscardRequest(PlayerTurn turn, PlayerHand playerHand)
	{
		
		playerHand.discardCard(this.discardPile, turn.getCardName());
		
	}
	
	private void handleMovePegRequest(PlayerTurn turn, PlayerHand playerHand) throws PlayerPositionNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException
	{
		int playerPositionNumber = turn.getPlayerPositionNumber();
		int moveDistance = turn.getmoveDistance();
		
		handleMoveASinglePeg(turn.getPlayerNumber(), playerPositionNumber, moveDistance, turn.getCardName(), playerHand);
	}
	
	private void handleMoveASinglePeg(int playerNumber, int playerPositionNumber, int spacesToMove, String cardName, PlayerHand playerHand) throws PlayerPositionNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException
	{
		//Make sure a valid position is requested
		this.validatePosition(playerPositionNumber);

		//Verify that the position referenced in the turn is not a start position
		PlayerPosition playerPosition = this.getPlayerPositions(playerNumber).get(playerPositionNumber-1);

		if (playerPosition.getPlayerBoardPosition().isStartPosition())
		{
			throw new InvalidGameStateException("Pegs in start position cannot move forward.");
		}

		//Determine how many spaces to move  (positive distance is forward, negative distance is backwards)
		int stepDistance;

		//Verify that the card being used for the turn can be used to move forward when distance is positive number
		Card cardBeingPlayed = playerHand.getCard(cardName);

		if (spacesToMove > 0)
		{
			stepDistance = 1;
			if(!cardBeingPlayed.canBeUsedToMoveForward())
			{
				throw new InvalidMoveException(String.format("Cannot use a %s to move forward.", cardName));
			}
		}
		else if (spacesToMove < 0) //Ensure that the card being played can move backwards when distance is negative number
		{
			stepDistance = -1;
			if(!cardBeingPlayed.canBeUsedToMoveBackward())
			{
				throw new InvalidMoveException(String.format("Cannot use a %s to move backward.", cardName));
			}
		}
		else
		{
			throw new InvalidMoveException("Cannot move 0 spaces.");
		}

		//Verify that the card can move the requested distance
		if (!cardBeingPlayed.canMoveDistanceOf(spacesToMove))
		{
			throw new InvalidMoveException(String.format("%d is an invalid distance for a %s to move.", spacesToMove, cardName));
		}

		//Verify that the player does not pass one of his/her own pegs along the way
		BoardPosition playerBoardPosition = playerPosition.getPlayerBoardPosition();
		int startStep = 0 + stepDistance;

		//Keep track if player passes over the readyToGoHomePosition which step is it
		int readyToGoHomeStep = -1;
		int stepsToEndOfHome = -1;
		int currentHomePositionNumber = 0;
		String playersReadyToGoHomePositionId = this.board.getReadyToGoHomePositionIdForPlayerNumber(playerNumber);

		if (playersReadyToGoHomePositionId.equals(playerBoardPosition.getId()))
		{
			//player is on the ready to go home spot so set step number to 0 and stepsToEndOfHome = 5
			readyToGoHomeStep = 0;
			stepsToEndOfHome = 5;
		}

		if (playerBoardPosition.isHomePosition())
		{
			//player is already in home track so set step number to 0 and calculated stepsToEndOfHome
			//	readyToGoHomeStep=0 - playerBoardPosition.getHomePositionNumber();
			readyToGoHomeStep=0;
			currentHomePositionNumber = playerBoardPosition.getHomePositionNumber();
			stepsToEndOfHome = 5 - currentHomePositionNumber;
		}

		for(int step=startStep;step != spacesToMove;step=step+stepDistance)
		{
			System.out.println(String.format("Taking step %d of %d with %d stepsToEndOfHome", step, spacesToMove, stepsToEndOfHome));
			BoardPosition stepPosition = null;

			if (readyToGoHomeStep < 0)
			{//get the next main track position
				stepPosition = board.getBoardPositionWithOffset(playerBoardPosition, step);
			}
			else
			{//get the next home position unless it is going past the end of home
				if (step  <= stepsToEndOfHome)
				{
					stepPosition = board.getPlayerSides().get(playerNumber-1).getHomePositionByNumber(currentHomePositionNumber+1);
//					stepsToEndOfHome--;
					System.out.println(String.format("Stepping forward into %s ", stepPosition.getId()));
					currentHomePositionNumber++;
				}		
				else
				{
					//if go past the end of home, then continue on the main track
					if (playerBoardPosition.isMainTrackPosition())
					{
						stepPosition = board.getBoardPositionWithOffset(playerBoardPosition, step);
					}
					else
					{
						//trying to move past the end of home is not allowed
						throw new InvalidMoveException("Cannot move past end of home track");
					}
				}
			}

			//if moving forward and stepping on the readyToGoHome position, keep track of step number
			if (stepDistance > 0 && stepPosition.getId().equals(playersReadyToGoHomePositionId) )
			{
				readyToGoHomeStep = step;
				stepsToEndOfHome = 5;
			}

			//check the BoardPosition at this step against other PlayerPositions
			for (PlayerPosition otherPosition : this.getPlayerPositions(playerNumber))
			{
				System.out.println(String.format("Comparing %s against %s", otherPosition.getPlayerBoardPosition().getId(), stepPosition.getId()));
				if (playerPosition.equals(otherPosition))
				{
					//skip over the current player position
				}
				else
				{
					if (otherPosition.getPlayerBoardPosition().getPegColor().equals(stepPosition.getPegColor()))
					{
						System.out.println(String.format("This is the step it blows up on %s", stepPosition.getId()));
						throw new CannotMoveToAPositionYouOccupyException("You cannot move over a position with one of your own pegs in it.");
					}
				}
			}

		}

		//If on main track move forward accounting for 18 spaces per side

		if (playerBoardPosition.isMainTrackPosition())
		{
			//if passing over readyToGoHome spot move into the home track
			if (readyToGoHomeStep > -1 && spacesToMove-readyToGoHomeStep <=5)
			{
				int homeSpace = spacesToMove - readyToGoHomeStep;
				BoardPosition newBoardPosition = board.getPlayerSides().get(playerNumber-1).getHomePositionByNumber(homeSpace);
				this.movePeg(playerPosition, newBoardPosition);
			}
			else
			{  //not moving past home so keep going
				Side boardPositionSide = board.getBoardPositionSide(playerBoardPosition);
				int boardSideIndex = board.getSideIndex(boardPositionSide);
				int sidePositionIndex = board.getBoardPositionSideIndex(boardPositionSide, playerBoardPosition);

				int numberOfSides = board.getPlayerSides().size();
				Side nextSide = boardPositionSide;

				//Change Board position side if move wraps around to a different side
				if (sidePositionIndex+spacesToMove > 17 || sidePositionIndex+spacesToMove < 0)
				{
					nextSide = board.getPlayerSides().get((numberOfSides + stepDistance + boardSideIndex)%numberOfSides);
				}

				BoardPosition newBoardPosition = nextSide.getPosition((18+spacesToMove+sidePositionIndex)%18);

				this.movePeg(playerPosition, newBoardPosition);
			}
		}
		else
		{//player is moving in home track
			BoardPosition newBoardPosition = board.getPlayerSides().get(playerNumber-1).getHomePositionByNumber(playerPosition.getPlayerBoardPosition().getHomePositionNumber() + spacesToMove);			
			this.movePeg(playerPosition, newBoardPosition);
		}

	}
	
	public void handleSplitMoveRequest(PlayerTurn turn, PlayerHand playerHand) throws PlayerPositionNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException
	{
		//Make sure card can be split
		if (!playerHand.getCard(turn.getCardName()).canBeSplit())
		{
			throw new InvalidMoveException(String.format("You cannot split a %s", turn.getCardName())); 
		}
				
		int movePositionNumber1 = turn.getSplitMovePosition1();
		int movePositionNumber2 = turn.getSplitMovePosition2();
		int moveDistance1 = turn.getSplitMoveDistance1();
		int moveDistance2 = turn.getSplitMoveDistance2();
		
		//Make sure the split distances are valid
		if (!playerHand.getCard(turn.getCardName()).isValidSplit(moveDistance1, moveDistance2))
		{
			throw new InvalidMoveException(String.format("You cannot split a %s into moves distances of: %d and %d", turn.getCardName(), moveDistance1, moveDistance2));
		}
		
		PlayerPosition originalPosition1 = this.getPlayerPosition(turn.getPlayerNumber(),movePositionNumber1);
		String originalPosition1ID = originalPosition1.getPlayerBoardPosition().getId();
		
		//handle moving the first peg
		this.handleMoveASinglePeg(turn.getPlayerNumber(), movePositionNumber1, moveDistance1, turn.getCardName(), playerHand); 
		
		//handle moving the second peg
		try 
		{
			this.handleMoveASinglePeg(turn.getPlayerNumber(), movePositionNumber2, moveDistance2, turn.getCardName(), playerHand);
		}
		catch (Throwable t)
		{
			//move the first peg back where it came from
			originalPosition1.moveTo(this.board.getBoardPositionById(originalPosition1ID));
			throw t;
		}
		
		//TO DO... if something goes wrong moving the second peg.. put the first peg back to original position
	}
	
	
	public void deal( )
	{
		//Deal 5 cards to each player
		for(int i=1;i<6;i++)
		{
			for (PlayerHand hand : this.playerHands) {
				hand.drawCard(this.drawPile);
			}
		}
		
	}
	
	public Player getCurrentPlayer() 
	{
			Player currentPlayer=playerQueue.peek();

			return currentPlayer;
	}
	
	public PlayerHand getPlayerHand(int playerNumber) throws PlayerNotFoundException {
		
		int numberPlaying = this.playerHands.size();
		
		if( (playerNumber<1) || (playerNumber>numberPlaying) )
		{
			throw new PlayerNotFoundException(String.format("Player number %d does not exist. There are %d players in this game.", playerNumber, numberPlaying));
		}

		return this.playerHands.get(playerNumber-1);
	}
	
	public Board getBoard()
	{
		return this.board;
	}
	
	public PlayerView getPlayerView(int playerNumber) throws PlayerNotFoundException
	{
		return new PlayerView(this, playerNumber);
	}
	
	public ArrayList<PlayerPosition> getPlayerPositions(int playerNumber)
	{
		return this.playerPositions.get(playerNumber-1);

	}
	
	public PlayerPosition getPlayerPosition(int playerNumber, int positionNumber)
	{
		return this.playerPositions.get(playerNumber - 1).get(positionNumber -1);
	}
	
	public PlayerPosition getPlayerPositionForBoardPosition(BoardPosition boardPosition)
	{
		PlayerPosition  positionToReturn = null;
		
		for(int p = 0; p<this.playerPositions.size();p++)
		{
			for (PlayerPosition playerPosition : this.playerPositions.get(p))
			{
				if (playerPosition.getPlayerBoardPosition().equals(boardPosition))
				{
					return playerPosition;
				}
			}
		}
		
		return positionToReturn;
	}
	
	public ArrayList<Move> getAllowedMoves() 
	{ 
		ArrayList<Move> allowedMoves = new ArrayList<Move>();

		//for each card in the current player's hand, look at each of the player's positions to 
		//determine which moves would be allowed

/*		int currentPlayerNumber = this.getCurrentPlayer().getNumber();

		for (Card card : this.playerHands.get(currentPlayerNumber-1).getCards()) 
			{ 
				for (PlayerPosition currentPosition : this.getPlayerPositions(currentPlayerNumber)) 
				{ 
					//determine the moves this card will allow the player's peg at this position to do
					addMovesBasedOnCardAndPosition(allowedMoves,card,currentPosition);
				} 
			}
*/
		return allowedMoves; 
	}

	


}
