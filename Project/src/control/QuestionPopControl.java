package control;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

import Intrefaces.GameEventObserver;
import Intrefaces.GameEventSubject;
import enums.GameEvent;
import javafx.animation.KeyFrame;
import javafx.animation.PauseTransition;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextFlow;
import javafx.stage.Stage;
import javafx.util.Duration;
import model.Player;
import model.Question;
import model.SoundManager;
import model.cpu_Player;

/**
 * Controller class for the question pop-up window.
 * This class manages the behavior and functionality of the pop-up window used to display questions during the game.
 */
public class QuestionPopControl implements GameEventSubject {
	@FXML
	private Label QuestionTitle;

	@FXML
	private RadioButton answerFour;

	@FXML
	private RadioButton answerOne;

	@FXML
	private TextFlow answerTextBox;

	@FXML
	private RadioButton answerThree;

	@FXML
	private RadioButton answerTwo;

	@FXML
	private Button checkAnswerButton;

	@FXML
	private Label chooseAnswerTitle;

	@FXML
	private Label check_Answer_label;
	@FXML
	private Pane leftPane;

	@FXML
	private Label questionTextBox;

	@FXML
	private ImageView p_image_viewr;

	@FXML
	private Label p_name_label;

	@FXML
	private Label qTimer_label;

	private Question question;

	private ToggleGroup toggleGroup;
	private String corr_answer_str;

	private Integer corr_answer;

	private BoardControl prev_control;

	private Player player;

	private int time = 60;

	private List<GameEventObserver> observers = new ArrayList<>(); //observer list

	private Timeline countdown;
	ArrayList<RadioButton> radioButtons;

	//OBSERVER METHODS
	@Override
	public void attach(GameEventObserver observer) {
		observers.add(observer);
		System.out.println("attached");

	}

	@Override
	public void detach(GameEventObserver observer) {
		observers.remove(observer);
	}

	@Override
	public void notifyObservers(GameEvent event) {
		for (GameEventObserver observer : observers) {
			observer.onEventTriggered(event);
		}}
	//END


	/**
	 * Initializes the controller.
	 * This method sets up the radio buttons, toggle group, and action listeners for the check answer button.
	 */
	public void initialize() {
		radioButtons = new ArrayList<RadioButton>();
		createTimer();
		radioButtons.add(answerOne);
		radioButtons.add(answerTwo);
		radioButtons.add(answerThree);
		radioButtons.add(answerFour);

		// Initialization logic here
		toggleGroup = new ToggleGroup();
		answerOne.setToggleGroup(toggleGroup);
		answerTwo.setToggleGroup(toggleGroup);
		answerThree.setToggleGroup(toggleGroup);
		answerFour.setToggleGroup(toggleGroup);
		checkAnswerButton.setDisable(true);

		// Set listener to the radio buttons, to allow to check answer only if something
		// is chosen
		toggleGroup.selectedToggleProperty().addListener((observable, oldValue, newValue) -> {
			if (newValue != null) {
				// Enable the checkAnswerButton
				checkAnswerButton.setDisable(false);
			}
		});
		// Set an action listener for the checkAnswerButton
		checkAnswerButton.setOnAction(event -> {
			RadioButton selectedRadioButton = (RadioButton) toggleGroup.getSelectedToggle();
			if (selectedRadioButton != null) {
				checkAnswerButton.setDisable(false);
				String selectedAnswer = selectedRadioButton.getText();
				// Save the selected answer (you can use it as needed)
				System.out.println("Selected answer: " + selectedAnswer);
				check_Answer(selectedAnswer);
			} else {
				System.out.println("No answer selected.");
			}
		});

	}

	/**
	 * Creates and starts the countdown timer.
	 */
	private void createTimer() {
		// Create a 30 seconds duration
		AtomicInteger duration = new AtomicInteger(time);

		// Create a new timeline
		countdown = new Timeline();
		qTimer_label.setFont(Font.font("JejuHallasan", 20));
		// Create a key frame that updates every second
		KeyFrame keyFrame = new KeyFrame(Duration.seconds(1), event -> {
			// Decrease the duration by one second
			int remaining = duration.decrementAndGet();
			// Calculate minutes and seconds
			long minutes = TimeUnit.SECONDS.toMinutes(remaining);
			long seconds = remaining - TimeUnit.MINUTES.toSeconds(minutes);
			// Update the label
			qTimer_label.setText(String.format("%02d:%02d", minutes, seconds));
			// If the duration is zero, stop the timeline
			if (remaining <= 0) {
				countdown.stop();
				check_Answer("Time up!");
			}
		});
		// Add the key frame to the timeline
		countdown.getKeyFrames().add(keyFrame);

		// Set the cycle count to indefinite, so the timeline repeats
		countdown.setCycleCount(Timeline.INDEFINITE);

		// Start the timeline
		countdown.play();
	}

	/**
	 * Checks the selected answer against the correct answer and handles the outcome.
	 * 
	 * @param selectedAnswer The answer selected by the player.
	 */
	private void check_Answer(String selectedAnswer) {
		// Create a sound manager instance
		SoundManager soundManager = new SoundManager();
		// Attach the sound manager to the question
		question.attach(soundManager);

		// Initialize steps variable
		int steps = 0;
		// Determine steps based on question difficulty
		if (question.getDifficulty() == 1) {
			steps = -1;
		} else if (question.getDifficulty() == 2) {
			steps = -2;
		} else if (question.getDifficulty() == 3) {
			steps = -3;
		}

		// Handle different cases based on selected answer
		if (selectedAnswer.equals("Time up!")) {
			// Notify observers about incorrect answer (time's up)
			question.notifyObservers(GameEvent.INCORRECT_ANSWER);
			// Update UI
			check_Answer_label.setText("Time up!");
			check_Answer_label.setStyle("-fx-background-color: yellow;"); // Set background to yellow
			// Move player according to difficulty
			prev_control.move_Player(steps, player);
		} else if (selectedAnswer.equals(corr_answer_str)) {
			// Notify observers about correct answer
			question.notifyObservers(GameEvent.CORRECT_ANSWER);
			// Update UI
			check_Answer_label.setText("Correct!");
			check_Answer_label.setStyle("-fx-background-color: green;"); // Set background to green
			// Move player according to difficulty
			if (question.getDifficulty() == 3) {
				prev_control.move_Player(1, player);
			} else {
				prev_control.move_Player(0, player);
			}
		} else {
			// Notify observers about incorrect answer
			question.notifyObservers(GameEvent.INCORRECT_ANSWER);
			// Update UI
			check_Answer_label.setText("You're Wrong!");
			check_Answer_label.setStyle("-fx-background-color: red;"); // Set background to red
			// Move player according to difficulty
			prev_control.move_Player(steps, player);
		}
		// Disable UI elements after answering
		answerOne.setDisable(true);
		answerTwo.setDisable(true);
		answerThree.setDisable(true);
		answerFour.setDisable(true);
		checkAnswerButton.setDisable(true);

		PauseTransition delay = new PauseTransition(Duration.seconds(2)); // 2 seconds delay
		delay.setOnFinished(event -> {
			countdown.stop();
			Stage stage = (Stage) checkAnswerButton.getScene().getWindow();
			stage.close();
		});
		delay.play();
	}


	/**
	 * Sets the question and updates the UI elements with question details.
	 * 
	 * @param q The question object to be set.
	 */
	public void set_question(Question q) {
		question = q;
		String theQ = question.getQuestion();
		corr_answer = Integer.valueOf(question.getCorrectAnswer());
		// Get the answers from the question
		List<String> answers = question.getAnswers();
		corr_answer_str = answers.get(corr_answer - 1);

		System.out.println("corr_answer " + corr_answer + " corr_answer_str " + corr_answer_str);
		if (q.getDifficulty() == 1) {
			QuestionTitle.setText("Easy Question");
		}
		if (q.getDifficulty() == 2) {
			QuestionTitle.setText("Medium Question");
		}
		if (q.getDifficulty() == 3) {
			QuestionTitle.setText("Hard Question");
		}

		// Populate the TextFields with the answers
		answerOne.setText(answers.get(0));
		answerTwo.setText(answers.get(1));
		answerThree.setText(answers.get(2));
		answerFour.setText(answers.get(3));
		questionTextBox.setText(theQ);
	}

	public void prev_window(BoardControl boardControl) {
		prev_control = boardControl;
	}

	/**
	 * Sets the player for answering the question and updates the UI accordingly.
	 * 
	 * @param p The player object to be set.
	 */
	public void set_player(Player p) {
		// TODO Auto-generated method stub
		player = p;
		if (player.getClass().getName() == "model.cpu_Player") {
			cpu_Player cpu_Player = ((cpu_Player) player);
			cpu_Player.set_question_controll(this);
			CommandInvoker invoker = new CommandInvoker();
			invoker.addCommand(new SelectAnswerCommand((cpu_Player) player));
			invoker.addCommand(new DelayCommand(2)); // Delay of 2 seconds
			invoker.addCommand(new PressButtonCommand((cpu_Player) player));
			invoker.executeCommands();
			checkAnswerButton.setVisible(false);
			p_name_label.setText("CPU:\n"+player.getName());
			p_name_label.setTextFill(Color.web(player.getColor()));
			for (RadioButton rb : radioButtons) {
				rb.setMouseTransparent(true);
			}

		} else {
			p_name_label.setText("Answering:\n" + player.getName());
			p_name_label.setTextFill(Color.web(player.getColor()));
		}
		Image img = new Image(player.getToken());
		p_image_viewr.setImage(img);
		prev_control.get_rollButton().setDisable(true);
	}

	/**
	 * @return the answerFour
	 */
	public ArrayList<RadioButton> getAnswerRadioButtons() {

		return radioButtons;
	}

	/**
	 * @return the checkAnswerButton
	 */
	public Button getCheckAnswerButton() {
		return checkAnswerButton;
	}

	/**
	 * @return the corr_answer
	 */
	public Integer getCorr_answer() {
		return corr_answer;
	}

	/**
	 * @param corr_answer the corr_answer to set
	 */
	public void setCorr_answer(Integer corr_answer) {
		this.corr_answer = corr_answer;
	}

}