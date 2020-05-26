package com.danlogan.pegsandjokers.domain;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.ArrayList;

import org.junit.jupiter.api.Test;

public class GameUnitTests {
	
	@Test
	void aSimpleTestShouldWork()
	{
		boolean  aBool = true;
		assert(aBool);
	}
	
	@Test
	void testDefaultGameSetup() throws PlayerNotFoundException
	{
		Game game = Game.Builder.newInstance().build();
		
		assertThat(game.getPlayers().size()).isEqualTo(4);
		assertThat(game.getPlayers().get(0).getName()).isEqualTo("Player 1");
		assertThat(game.getPlayers().get(1).getName()).isEqualTo("Player 2");
		assertThat(game.getPlayers().get(2).getName()).isEqualTo("Player 3");
		assertThat(game.getPlayers().get(3).getName()).isEqualTo("Player 4");
		
		assertThat(game.getBoard().getPlayerSides().size()).isEqualTo(4);
		
		assertThat(game.getPlayerPositions(1).size()).isEqualTo(5);
		assertThat(game.getPlayerPositions(1).get(0).getPlayerBoardPosition().isStartPosition());
		assertThat(game.getPlayerPositions(1).get(1).getPlayerBoardPosition().isStartPosition());
		assertThat(game.getPlayerPositions(1).get(2).getPlayerBoardPosition().isStartPosition());
		assertThat(game.getPlayerPositions(1).get(3).getPlayerBoardPosition().isStartPosition());
		assertThat(game.getPlayerPositions(1).get(4).getPlayerBoardPosition().isStartPosition());

		assertThat(game.getCardsRemaining()).isEqualTo(108);
		
	}
	
	@Test
	void testBuildGameWithSpecificPlayerNames()
	{
		Game game = Game.Builder.newInstance()
				.withPlayerNamed("Player 1")
				.withPlayerNamed("Player 2")
				.build();

		assertThat(game.getPlayers().get(0).getName()).isEqualTo("Player 1");
		assertThat(game.getPlayers().get(0).getNumber()).isEqualTo(1);
		assertThat(game.getPlayers().get(1).getName()).isEqualTo("Player 2");
		assertThat(game.getPlayers().get(1).getNumber()).isEqualTo(2);

	}

	@Test
	void testBuildGameWithSpecificPlayerHands() throws PlayerNotFoundException
	{
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
												.withCard(new Card(CardRank.ACE, Suit.CLUBS))
												.build();

		Game game = Game.Builder.newInstance()
						.withPlayerHand(playerHand)
						.build();
		
		assertThat(game.getPlayerHand(1).getCards().size()).isEqualTo(1);
		assertThat(game.getPlayerHand(1).getCard("ACE of CLUBS")).isNotNull();
	}
	
	@Test
	void testDiscardTurn() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
									PlayerPositionNotFoundException
	{
		Game game = Game.Builder.newInstance().build();
		
		game.deal();
		int deckSizeBeforeTurn = game.getCardsRemaining();
		
		Card cardToDiscard = game.getPlayerHand(1).getCard(1);
	
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
								.withCardName(cardToDiscard.getName())
								.withMoveType(MoveType.DISCARD)
								.withPlayerNumber(1)
								.build();

		game.takeTurn(turn);
		
		assertThat(game.getPlayerHand(1).getCard(cardToDiscard.getName())).isNull();
		assertThat(deckSizeBeforeTurn - game.getCardsRemaining()).isEqualTo(1);
		
	}
	
	@Test
	public void testStartAPegTurn() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.ACE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.START_A_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.build();

		game.takeTurn(turn);
		
		assertThat(game.getPlayerPosition(1,1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
	}
	
	@Test
	public void testMovePegForwardTurn() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.THREE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(3)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-11");
		
		assertThat(game.getBoard().getBoardPositionById("RED-8").getHasPeg()).isFalse();
		

	}
	
	@Test
	public void testCannotMovePastYourOwnPeg() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.THREE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1, 2, "RED-9")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(3)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (CannotMoveToAPositionYouOccupyException e)
		{
			assertThat(e.getMessage()).contains("cannot move over a position with one of your own pegs in it");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		
	}

	@Test
	public void testCantLandOnOwnPeg() throws PlayerNotFoundException, InvalidMoveException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.ACE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.START_A_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(2)
				.build();
		
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (CannotMoveToAPositionYouOccupyException e)
		{
			assertThat(e.getMessage()).isNotBlank();
		}

	}
	
	@Test
	public void testCannotMoveBackward() throws PlayerNotFoundException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.THREE, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(-3)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("backward");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

	}

	@Test
	public void testCannotMoveForward() throws PlayerNotFoundException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.EIGHT, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(8)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("forward");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

	}

	@Test
	public void testMoveEightBackwards() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.EIGHT, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(-8)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("PINK-18");

	}

	@Test
	public void testCannotMoveWrongDistance() throws PlayerNotFoundException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.EIGHT, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(-4)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("invalid distance");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

	}
	
	@Test
	public void testSendOpponentBackToStart() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.QUEEN, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(2,1,"RED-8")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.START_A_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.build();
		
		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().isStartPosition()).isTrue();
		
	}

	@Test
	public void testSplitASeven() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.SEVEN, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1,2,"BLUE-1")
				.build();

		int[] splitMoveArray = {1,3,2,4};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-1");

		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-11");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-5");

	}

	@Test
	public void testSevenSplitMustAddToSeven() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.SEVEN, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1,2,"BLUE-1")
				.build();

		int[] splitMoveArray = {1,3,2,3};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-1");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("cannot split a SEVEN");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-1");

	}
	

	@Test
	public void testRollbackSplitMoveOnSecondPegError() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.SEVEN, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-9")
				.withPlayerPosition(1,2,"RED-8")
				.build();

		int[] splitMoveArray = {1,3,2,4};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-9");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (CannotMoveToAPositionYouOccupyException e)
		{
			assertThat(e.getMessage()).contains("cannot move to a position with one of your own pegs in it");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-9");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

	}

	@Test
	public void testCannotSplitAnEight() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.EIGHT, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1,2,"BLUE-1")
				.build();

		int[] splitMoveArray = {1,3,2,3};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-1");

		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("cannot split");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-1");

	}

	@Test
	public void testMustSplitANine() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.NINE, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.build();

	
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(9)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
	
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("9 is an invalid distance");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");

	}
	
	@Test
	public void testNineMustMoveOneForwardOneBackward() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.NINE, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1,2,"RED-9")
				.build();

		int[] splitMoveArray = {2,6,1,3};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-9");
	
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("cannot split");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-9");

	}

	@Test
	public void testSplitNineForwardOneBackward() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.NINE, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-8")
				.withPlayerPosition(1,2,"RED-9")
				.build();

		int[] splitMoveArray = {2,3,1,-6};
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.SPLIT_MOVE)
				.withPlayerNumber(1)
				.withSplitMoveArray(splitMoveArray)
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-8");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-9");
	
		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-2");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("RED-12");

	}

	@Test
	public void testUseJokerToReplaceOpponentPeg() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Joker();

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(2,1,"BLUE-9")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.USE_JOKER)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withTargetBoardPositionId("BLUE-9")
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");
	
		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");
		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().getId()).isEqualTo("BLUEStart-1");

	}

	@Test
	public void testCannotUseAnotherCardAsJoker() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Card(CardRank.NINE, Suit.CLUBS);

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(2,1,"BLUE-9")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.USE_JOKER)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withTargetBoardPositionId("BLUE-9")
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");
	
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("is not a joker");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
		assertThat(game.getPlayerPosition(2, 1).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");

	}

	@Test
	public void testCannotMoveJokerToEmptyPosition() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Joker();

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.USE_JOKER)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withTargetBoardPositionId("BLUE-9")
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
	
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("There is not a peg in position");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
	
	}

	@Test
	public void testCannotUseJokerToReplaceYourOwnPeg() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
		Card cardToPlay = new Joker();

		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,2,"BLUE-9")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.USE_JOKER)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withTargetBoardPositionId("BLUE-9")
				.build();

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");
	
		try {
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException e)
		{
			assertThat(e.getMessage()).contains("replace your own peg");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDStart-1");
		assertThat(game.getPlayerPosition(1, 2).getPlayerBoardPosition().getId()).isEqualTo("BLUE-9");

	}

	@Test
	public void testMovePlayerIntoHome() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.THREE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-3")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(3)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-3");
	
		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDHome-3");
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().isHomePosition()).isTrue();
		
	}

	
	@Test
	public void testMovePastHomeWhenMovingToFar() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.TEN, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-2")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(10)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-2");
	
		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-12");
		
	}

	@Test
	public void testMoveWithinHomeTrack() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.TWO, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"REDHome-2")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(2)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDHome-2");
	
		game.takeTurn(turn);

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDHome-4");
		
	}

	@Test
	public void testPegWithinHomeTrackCannotMovePastEnd() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.SIX, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"REDHome-2")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(6)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDHome-2");
	
		System.out.println("Testing cannot move past end of home when starting from within home");
		try
		{
			game.takeTurn(turn);
			assert(false);
		}
		catch (InvalidMoveException ex)
		{
			assertThat(ex.getMessage()).contains("past end of home");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("REDHome-2");
		
	}
	

	@Test
	public void testCannotPassYourselfInHome() throws PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, CannotMoveToAPositionYouOccupyException,
	PlayerPositionNotFoundException
	{
	Card cardToPlay = new Card(CardRank.THREE, Suit.CLUBS);
		
		PlayerHand playerHand = PlayerHand.Builder.newInstance(1)
				.withCard(cardToPlay)
				.build();

		Game game = Game.Builder.newInstance()
				.withPlayerHand(playerHand)
				.withPlayerPosition(1,1,"RED-3")
				.withPlayerPosition(1, 2, "REDHome-1")
				.build();

		PlayerTurn turn = PlayerTurn.Builder.newInstance()
				.withCardName(cardToPlay.getName())
				.withMoveType(MoveType.MOVE_PEG)
				.withPlayerNumber(1)
				.withPositionNumber(1)
				.withMoveDistance(3)
				.build();
		
		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-3");
	
		try
		{
			game.takeTurn(turn);
			assert(false);
		}
		catch (CannotMoveToAPositionYouOccupyException ex)
		{
			assert(ex.getMessage()).contains("cannot move over a position with one of your own");
		}

		assertThat(game.getPlayerPosition(1, 1).getPlayerBoardPosition().getId()).isEqualTo("RED-3");
			
	}


}
