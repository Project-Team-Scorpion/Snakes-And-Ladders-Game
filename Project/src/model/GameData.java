package model;

import java.io.File;
import java.io.IOException;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import control.HistoryControl;
import exceptions.HandleExceptions;
import exceptions.Questions_empty;
import exceptions.NoJsonFileFound;

/**
 * The GameData class manages the data related to the game, including players, questions, snakes, ladders, and special tiles.
 * It provides methods for initialization, resetting, and accessing game data, as well as handling game progression.
 * Write to Json and save the game history
 */

public class GameData {
	private int numberOfPlayers; // The number of players in the game
	private String difficulty; // The difficulty level of the game
	private HashMap<Integer, List<Question>> questions_Map; // Map containing questions categorized by difficulty level
	private LinkedList<Player> player_list; // List of players in the game
	private LinkedList<Snake> snake_list; // List of snakes on the game board
	private LinkedList<Ladder> ladders; // List of ladders on the game board
	private LinkedList<Tile> specialTiles_list; // List of special tiles on the game board
	private HistoryControl History; // Control object for managing game history
	private int playerTurn; // Index of the current player's turn
	private Board board; // Game board object
	private QuestionsFromJson questionData; // Object for reading questions from JSON
	private int playTime; // Total play time of the game
	private String winner; // Name of the winner of the game
	private Boolean soundFX = true; // Flag for enabling/disabling sound effects
	private Map<Integer, Set<Question>> askedQuestionsMap = new HashMap<>(); // Map to track asked questions

	// Singleton instance
	private static GameData instance = null;

	/**
	 * Private constructor that initializes the game data.
	 */
	private GameData() {
		init();
	}

	/**
	 * Initializes the game data by reading questions from JSON, setting up the game elements, and setting the default values.
	 */
	public void init() {
		try {
			//	        questionData = readQuestionFromJson("src\\Json\\Questions.txt");
			questionData = QuestionsFromJson.getInstance().readQuestionsFromJson();
		} catch (NoJsonFileFound e) {
			HandleExceptions.showException(e,this,null);
			return;
		}

		// Initialization code here
		//		this.questions = new HashMap<Integer, Question>();
		player_list = new LinkedList<Player>();
		snake_list = new LinkedList<Snake>();
		ladders = new LinkedList<Ladder>();
		specialTiles_list = new LinkedList<Tile>();
		questions_Map = questionData.init_QuestionMap();
		difficulty = "Easy";
		playerTurn = 0;
		numberOfPlayers = 1;
		//		this.board = new Board(player_list);

	}

	/**
	 * Resets the game data by clearing the game elements and setting the default values.
	 */
	public void reset() {
		player_list.clear();
		snake_list.clear();
		ladders.clear();
		specialTiles_list.clear();
		questions_Map.clear();
		difficulty = "Easy";
		playerTurn = 0;
		numberOfPlayers = 1;
	}

	/**
	 * Static method to get the singleton instance of GameData. If the instance is null, a new GameData is created.
	 *
	 * @return The singleton instance of GameData.
	 */
	// Static method to get the singleton instance
	public static GameData getInstance() {
		if (instance == null) {
			instance = new GameData();
		}
		return instance;
	}

	/**
	 * Advances to the next turn. If the current player is the last in the list, the turn goes back to the first player.
	 */
	public void next_turn() {
		if (playerTurn < player_list.size() - 1) {
			playerTurn++;
		} else {
			playerTurn = 0;
		}

	}

	//Initiate the board with the updated player list, used to init from the player_control to prepare the board.
	public void init_board() {
		board = new Board(player_list);
	}

	/**
	 * @return the difficulty
	 */
	public String getDifficulty() {
		return difficulty;
	}

	/**
	 * @return the playerTurn
	 */
	public int getPlayerTurn() {
		return playerTurn;
	}

	/**
	 * @param playerTurn the playerTurn to set
	 */
	public void setPlayerTurn(int p_Turn) {
		playerTurn = p_Turn;
	}

	/**
	 * @return the difficulty
	 */
	// NumOfTiles X NumOfTiles = size of Board
	public int getNumOfTiles() {
		switch (getDifficulty()) {
		case "Easy":
			return 7;
		case "Medium":
			return 10;
		case "Hard":
			return 13;
		default:
			return 0;
		}
	}

	/**
	 * Retrieves a question of a specified difficulty level.
	 * If all questions of the specified difficulty have been asked, it resets and starts from the beginning.
	 *
	 * @param diff The difficulty level of the question (7-easy, 8-medium, 9-hard).
	 * @return A question of the specified difficulty.
	 */
	public Question get_Question(int diff) {// 7-easy, 8- med, 9 - hard
		List<Question> q_list = questions_Map.get(diff);
		Set<Question> askedQuestions = askedQuestionsMap.getOrDefault(diff, new HashSet<>());
		if (askedQuestions.size() == q_list.size()) {
			// All questions have been asked, clear the set
			askedQuestions.clear();
		}
		Question question;
		do {
			Random rand = new Random();
			int generator = rand.nextInt(q_list.size());
			question = q_list.get(generator);
		} while (askedQuestions.contains(question));

		// Add the selected question to the set of asked questions
		askedQuestions.add(question);
		askedQuestionsMap.put(diff, askedQuestions);
		return question;
	}

	/**
	 * @param difficulty the difficulty to set
	 */
	public void setDifficulty(String diff) {
		difficulty = diff;
	}

	/**
	 * @return the numberOfPlayers
	 */
	public int getNumberOfPlayers() {
		return numberOfPlayers;
	}

	/**
	 * @param numberOfPlayers the numberOfPlayers to set
	 */
	public void setNumberOfPlayers(int num) {
		numberOfPlayers = num;
	}

	/**
	 * @return the questions
	 */
	public HashMap<Integer, List<Question>> getQuestions() {
		
		return questions_Map;
	}
	public boolean areListsEmpty() throws Questions_empty {
	    for (List<Question> list : questions_Map.values()) {
	        if (list.isEmpty()) {
	        	throw new Questions_empty();
	        }
	    }
	    return false;
	}

	/**
	 * @param questions the questions to set
	 */
	public void setQuestions(HashMap<Integer, List<Question>> questions) {
		questions_Map = questions;
	}

	/**
	 * @return the history
	 */
	public HistoryControl getHistory() {
		return History;
	}

	/**
	 * @param history the history to set
	 */
	public void setHistory(HistoryControl history) {
		History = history;
	}

	/**
	 * @return the player_list
	 */
	public LinkedList<Player> getplayer_list() {
		return player_list;
	}

	public Player get_Player(int index) {
		if (player_list.get(index) != null) {
			return player_list.get(index);
		}
		return null;
	}

	/**
	 * @param player_list the player_list to set
	 */
	public void setPlayer_list(LinkedList<Player> p) {
		player_list = p;
	}

	/**
	 * Adds a player to the game if the player is not null.
	 *
	 * @param player The player to be added.
	 * @return True if the player was added, false otherwise.
	 */
	public boolean addPlayer(Player player) {
		if (player != null) {
			player_list.add(player);
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Retrieves a player from the game.
	 *
	 * @param player The player to be retrieved.
	 * @return The player if found, null otherwise.
	 */
	public Player getPlayer(Player player) {
		for (Player p : player_list) {
			if (p.equals(player)) {
				return p;
			}
		}
		return null;
	}

	public Player getPlayer(int player) {

		return player_list.get(player);
	}

	/**
	 * @return the snake_list
	 */
	public LinkedList<Snake> getSnake_list() {
		return snake_list;
	}

	/**
	 * @param snake_list the snake_list to set
	 */
	public void setSnake_list(LinkedList<Snake> s) {
		snake_list = s;
	}

	public boolean addSnake(Snake s) {
		if (s != null) {
			snake_list.add(s);
			return true;
		}
		return false;
	}

	public Snake getPlayer(Snake snake) {
		for (Snake s : snake_list) {
			if (s.equals(snake)) {
				return s;
			}
		}
		return null;
	}

	/**
	 * @return the ladders
	 */
	public LinkedList<Ladder> getLadders() {
		return ladders;
	}

	/**
	 * @param ladders the ladders to set
	 */
	public void setLadders(LinkedList<Ladder> l) {
		ladders = l;
	}

	/**
	 * Adds a ladder to the game if the ladder is not null.
	 *
	 * @param l The ladder to be added.
	 * @return True if the ladder was added, false otherwise.
	 */
	public boolean addLadders(Ladder l) {
		if (l != null) {
			ladders.add(l);
			return true;
		}
		return false;
	}

	/**
	 * @return the specialTiles_list
	 */
	public LinkedList<Tile> getspecialTiles_list() {
		return specialTiles_list;
	}

	/**
	 * @param specialTiles_list the specialTiles_list to set
	 */
	public void setspecialTiles_list(LinkedList<Tile> st) {
		specialTiles_list = st;
	}

	public void addspecialTiles_list(Tile st) {
		specialTiles_list.add(st);
	}

	public boolean addSpecialtile(Tile t) {
		if (t != null) {
			return (specialTiles_list.add(t));
		}
		return false;
	}

	/**
	 * @return the soundFX
	 */
	public Boolean getSoundFX() {
		return soundFX;
	}

	/**
	 * @param soundFX the soundFX to set
	 */
	public void setSoundFX(Boolean soundFX) {
		this.soundFX = soundFX;
	}

	//	public void to_json() {
	//	}
	//
	//	public Question getRandQuestion(int diff) {
	//		return null;
	//
	//	}

	/**
	 * @return the board
	 */
	public Board getBoard() {
		return board;
	}

	/**
	 * @param board the board to set
	 */
	public void setBoard(Board board) {
		this.board = board;
	}

	@Override
	public String toString() {
		return "GameData [numberOfPlayers=" + numberOfPlayers + ", difficulty=" + difficulty + ", questions_Map="
				+ questions_Map + ", player_list=" + player_list + ", snake_list=" + snake_list + ", ladders=" + ladders
				+ ", specialTiles_list=" + specialTiles_list + ", History=" + History + ", playerTurn=" + playerTurn
				+ ", board=" + board + "]";
	}

	/**
	 * Appends the current game data to a JSON file.
	 * The game data includes the date, players, difficulty, play time, and winner.
	 */
	public void appendGameToJson() {
		ObjectMapper mapper = new ObjectMapper();
		File file = new File("Json/History.txt");
		ArrayNode gameDataArray;
		try {
			gameDataArray = (ArrayNode) mapper.readTree(file);
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}
		List<String> players = player_list.stream().map(player -> player.getName()).collect(Collectors.toList());
		// Create new game data
		ObjectNode game = mapper.createObjectNode();
		game.put("date", LocalDate.now().toString());
		game.putPOJO("players", players);
		game.put("difficulty", difficulty);
		game.put("playTime", playTime);
		game.put("winner", winner);

		// Append new game data to array
		gameDataArray.add(game);

		// Print game data to console
//		System.out.println("Appending game data to JSON: " + game.toString());

		// Write game data array back to file
		try {
			mapper.writerWithDefaultPrettyPrinter().writeValue(file, gameDataArray);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * @return the playTime
	 */
	public int getPlayTime() {
		return playTime;
	}

	/**
	 * @param playTime the playTime to set
	 */
	public void setPlayTime(int playTime) {
		this.playTime = playTime;
	}

	/**
	 * @return the winner
	 */
	public String getWinner() {
		return winner;
	}

	/**
	 * @param winner the winner to set
	 */
	public void setWinner(String winner) {
		this.winner = winner;
	}

}
