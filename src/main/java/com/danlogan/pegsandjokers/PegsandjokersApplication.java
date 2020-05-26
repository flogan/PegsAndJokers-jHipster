package com.danlogan.pegsandjokers;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import com.danlogan.pegsandjokers.domain.Board;
import com.danlogan.pegsandjokers.domain.CannotMoveToAPositionYouOccupyException;
import com.danlogan.pegsandjokers.domain.CannotStartGameWithoutPlayersException;
import com.danlogan.pegsandjokers.domain.Game;
import com.danlogan.pegsandjokers.domain.InvalidGameStateException;
import com.danlogan.pegsandjokers.domain.PlayerHand;
import com.danlogan.pegsandjokers.domain.PlayerTurn;
import com.danlogan.pegsandjokers.domain.PlayerNotFoundException;
import com.danlogan.pegsandjokers.domain.PlayerPositionNotFoundException;
import com.danlogan.pegsandjokers.domain.InvalidMoveException;
import com.danlogan.pegsandjokers.domain.MoveType;
import com.danlogan.pegsandjokers.domain.PlayerView;

import java.net.URI;
import java.util.ArrayList;
import com.danlogan.pegsandjokers.infrastructure.GameNotFoundException;
import com.danlogan.pegsandjokers.infrastructure.GameRepository;


@SpringBootApplication
@Controller
public class PegsandjokersApplication {

	private final String allowedCrossOrigin = "http://localhost:4200";

	private static GameRepository gameRepository = new GameRepository();
//	private ArrayList<Game> games = new ArrayList<Game>();
	
	public static void main(String[] args) {
		
		SpringApplication.run(PegsandjokersApplication.class, args);
	}

	@Bean
   	public WebMvcConfigurer corsConfigurer() {
      return new WebMvcConfigurer() {
        	@Override
         	public void addCorsMappings(CorsRegistry registry) {
				registry.addMapping("/**").allowedOrigins(allowedCrossOrigin);
         }
      };
   }

	@GetMapping("/")
	@ResponseBody
	public String root() {
		return String.format("Welcome to Pegs and Jokers! There are 0 Games.");//,gameRepository.getNumberOfGames());
	}

	//Return all Games
	@GetMapping("/games")
	@ResponseBody
	public ResponseEntity<ArrayList<Game>> games()
	{
		ArrayList<Game>games = gameRepository.getAllGames();
		
		return new ResponseEntity<ArrayList<Game>>(games, HttpStatus.OK);
	}


	//New Game API
	@PostMapping("/games")
	@ResponseBody
	public ResponseEntity<Game> newGame(UriComponentsBuilder ucb) throws CannotStartGameWithoutPlayersException
	{
		
		Game game = Game.Builder.newInstance().build();
		gameRepository.addGame(game);
		game.start();
		
		URI locationURI = ucb.path("/game/")
								.path(String.valueOf(game.getId()))
								.build()
								.toUri();
							
		HttpHeaders headers = new HttpHeaders();
		
		headers.setLocation(locationURI);
		
		return new ResponseEntity<Game>(game, headers, HttpStatus.CREATED);
		
	}

	//Get a game by it's id
	@GetMapping(value = "/game/{id}")
	@ResponseBody
	public ResponseEntity<Game> getGameById(@PathVariable String id) throws GameNotFoundException
	{
		Game game = gameRepository.findGameById(id);
			
		return new ResponseEntity<Game>(game, HttpStatus.OK);
	}
	
	//Post a new turn to a game -  this is how players take turns
	@PostMapping("/game/{id}/turns")
	@ResponseBody
	public ResponseEntity<Game> takeTurn(@PathVariable String id, @RequestBody PlayerTurn turn) 
			throws GameNotFoundException, PlayerNotFoundException, InvalidMoveException, InvalidGameStateException, PlayerPositionNotFoundException,
				CannotMoveToAPositionYouOccupyException
	{
		Game game = gameRepository.findGameById(id);
	
		game.takeTurn(turn);
						
		return  new ResponseEntity<Game>(game, HttpStatus.OK);
	}
	
	//Get Player Hands for a specific player number
	@GetMapping(value="/game/{id}/playerhand/{playerNumber}")
	@ResponseBody
	public ResponseEntity<PlayerHand> getPlayerHand(@PathVariable String id, @PathVariable int playerNumber) throws GameNotFoundException, PlayerNotFoundException
	{
		  Game game = gameRepository.findGameById(id); 
		  
		  PlayerHand hand = game.getPlayerHand(playerNumber);
		  
		  return new ResponseEntity<PlayerHand>(hand,HttpStatus.OK);
		 	 		
	}
	
	//Get current board layout
	@GetMapping(value="/game/{id}/board")
	@ResponseBody
	public ResponseEntity<Board> getBoard(@PathVariable String id) throws GameNotFoundException
	{
		Game game = gameRepository.findGameById(id);
		
		Board board = game.getBoard();
		
		return new ResponseEntity<Board>(board, HttpStatus.OK);
		
	}
	
	//Get view of the game for a specific player
	@GetMapping(value="/game/{id}/playerView/{playerNumber}")
	@ResponseBody
	public ResponseEntity<PlayerView> getPlayerView(@PathVariable String id, @PathVariable int playerNumber) throws GameNotFoundException, PlayerNotFoundException
	{
		Game game = gameRepository.findGameById(id);
		
		PlayerView playerView = game.getPlayerView(playerNumber);
		
		return new ResponseEntity<PlayerView>(playerView, HttpStatus.OK);
	}
	
	
	//probably should deprecate this action concept as it is not really RESTful
	@GetMapping(value = "/game/{id}", params = "action")
	@ResponseBody
	public ResponseEntity<Game> gameAction(@PathVariable String id, @RequestParam String action) throws GameNotFoundException, CannotStartGameWithoutPlayersException {
		
		Game game = gameRepository.findGameById(id);
		
		if (game == null) {
			return new ResponseEntity<Game>(HttpStatus.NOT_FOUND);
		}

		if (action.equals("start")) {
			
				game.start();	
		}
		
		return new ResponseEntity<Game>(game, HttpStatus.OK);
		
		
	}

	@RequestMapping("/mvc/games")
	public String getGames(Model model)
	{
		model.addAttribute("games", gameRepository.getAllGames());
		
		return "mvc/games";
	}
	
	@RequestMapping("/mvc/game/{id}")
	public String getGameById(String id, Model model)
	{
		
		return "mvc/game";
	}
	
	@RequestMapping("/mvc/game/{id}/playerView/{playerNumber}")
	public String getPlayerViewByGameAndPlayerNumber(@PathVariable String id, @PathVariable int playerNumber, Model model) throws GameNotFoundException, PlayerNotFoundException 
	{
		Game game = gameRepository.findGameById(id);
		
		PlayerView playerView = game.getPlayerView(playerNumber);
		model.addAttribute("playerView",playerView);
		
		TurnRequest turnRequest = new TurnRequest();
		turnRequest.setGameId(id);
		turnRequest.setPlayerNumber(playerNumber);
		turnRequest.setCardName("please choose a card");
		turnRequest.setMoveDistance(0);
		turnRequest.setPlayerPositionNumber(1);
		model.addAttribute("turnRequest",turnRequest);

		System.out.println("in mvc player view request");
		return "mvc/playerView";
	}

	@PostMapping("/mvc/taketurn")
	public String takeTurn(@ModelAttribute("turnRequest") TurnRequest turnRequest) throws GameNotFoundException, PlayerNotFoundException, InvalidGameStateException, InvalidMoveException, PlayerPositionNotFoundException, CannotMoveToAPositionYouOccupyException
	{
		System.out.println("got turn request: " + turnRequest.toString());
		
		String gameId = turnRequest.getGameId();
		
		Game game = gameRepository.findGameById(gameId);
		
		PlayerTurn turn = PlayerTurn.Builder.newInstance()
			.withCardName(turnRequest.getCardName())
			.withMoveType(turnRequest.getMoveType())
			.withMoveDistance(turnRequest.getMoveDistance())
			.withPlayerNumber(turnRequest.getPlayerNumber())
			.withPositionNumber(turnRequest.getPlayerPositionNumber())
			.build();
		
		game.takeTurn(turn);
		
		return "redirect:/mvc/game/"+gameId+"/playerView/"+turnRequest.getPlayerNumber();
	}

	//Data Transfer Objects
	public class TurnRequest
	{
		private int playerNumber;
		private String cardName;
		private MoveType moveType;
		private int moveDistance;
		private String gameId;
		private int playerPositionNumber;
	
	
		public int getPlayerPositionNumber() {
			return playerPositionNumber;
		}
		public void setPlayerPositionNumber(int playerPositionNumber) {
			this.playerPositionNumber = playerPositionNumber;
		}
		public String getGameId() {
			return gameId;
		}
		public void setGameId(String gameId) {
			this.gameId = gameId;
		}
		public MoveType getMoveType() {
			return moveType;
		}
		public void setMoveType(MoveType moveType) {
			this.moveType = moveType;
		}
		public int getMoveDistance() {
			return moveDistance;
		}
		public void setMoveDistance(int moveDistance) {
			this.moveDistance = moveDistance;
		}
		public TurnRequest()
		{
			
		}
		public int getPlayerNumber() {
			return playerNumber;
		}
		@Override
		public String toString() {
			return "TurnRequest [playerNumber=" + playerNumber + ", cardName=" + cardName + ", moveType=" + moveType
					+ ", moveDistance=" + moveDistance + ", gameId=" + gameId + ", playerPositionNumber="
					+ playerPositionNumber + "]";
		}
		public void setPlayerNumber(int playerNumber) {
			this.playerNumber = playerNumber;
		}
		public String getCardName() {
			return cardName;
		}
		public void setCardName(String cardName) {
			this.cardName = cardName;
		}
	}
}
