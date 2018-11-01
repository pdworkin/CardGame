package com.heyho.demo.cardgame;

import java.awt.*;
import java.util.List;

import javax.swing.*;

class Gui {

	GuiWindowBuilder window;
	private int numPlayers;
	private int numCards;
	private int winningScore;
	
/**
	 * @wbp.parser.entryPoint
	 */
		Gui(int numPlayers, int numCards, int winningScore) {
			this.numPlayers = numPlayers;
			this.numCards = numCards;
			this.winningScore = winningScore;
		
//		EventQueue.invokeLater(new Runnable() {
//			public void run() {
				try {
					window = new GuiWindowBuilder();
//					CardGameGui2(window, numPlayers, numCards, winningScore);
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}

				setWinningScoreText(winningScore);
				setNumPlayersText(numPlayers);
				setNumCardsText(numCards);
//			}
//		});
	}
	
	void updatePlayers(int numPlayers, int numCards, int winningScore) {
		
		this.numPlayers = numPlayers;
		this.numCards = numCards;
		this.winningScore = winningScore;
		
		if (numPlayers == getHandsNum())
			return;
				
		window.hands.removeAll();
		
		for (int i=0; i<numPlayers; i++) {
//			getHandsContainer().add(new JLabel(""+i+": "));

			JPanel player = new JPanel();
			player.setAlignmentX(Component.LEFT_ALIGNMENT);
			window.hands.add(player);
			player.setLayout(new BoxLayout(player, BoxLayout.X_AXIS));
			
			
			JLabel cards = new JLabel("");
			cards.setAlignmentX(Component.LEFT_ALIGNMENT);
			cards.setHorizontalAlignment(SwingConstants.LEFT);
			player.add(cards);
	
			Component horizontalGlue = Box.createHorizontalGlue();
			player.add(horizontalGlue);
			
			Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 10));
			player.add(rigidArea_1);
			
			JLabel played = new JLabel(" ");
			played.setAlignmentX(Component.RIGHT_ALIGNMENT);
			played.setHorizontalAlignment(SwingConstants.RIGHT);
			player.add(played);
			
			Component rigidArea_2 = Box.createRigidArea(new Dimension(20, 10));
			player.add(rigidArea_2);
			
			JLabel tricks = new JLabel("0");
			tricks.setAlignmentX(Component.RIGHT_ALIGNMENT);
			tricks.setHorizontalAlignment(SwingConstants.RIGHT);
			player.add(tricks);
			
			Component rigidArea_3 = Box.createRigidArea(new Dimension(20, 10));
			player.add(rigidArea_3);
			
			JLabel score = new JLabel("0");
			score.setHorizontalAlignment(SwingConstants.RIGHT);
			score.setAlignmentX(Component.RIGHT_ALIGNMENT);
			player.add(score);
			
			Component rigidArea_4 = Box.createRigidArea(new Dimension(5, 5));
			player.add(rigidArea_4);

		}
		
		window.hands.revalidate();
		window.hands.repaint();
	}
		
	void setScoreLabel(int score) {
		((JLabel) window.score).setText(Integer.toString(score));
	}

	void setWinningScoreText(int winningScore) {
		((JTextField) window.winningScore).setText(Integer.toString(winningScore));
	}

	int getWinningScoreText() {		
		try {
			int tmp = Integer.parseInt(((JTextField) window.winningScore).getText());
			return tmp;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	void setWinningScoreEnabled(Boolean b) {
		((JTextField) window.winningScore).setEnabled(b);
	}

	void setNumPlayersText(int numPlayers) {
		((JTextField) window.numPlayers).setText(Integer.toString(numPlayers));
	}

	int getNumPlayersText() {		
		try {
			int tmp = Integer.parseInt(((JTextField) window.numPlayers).getText());
			return tmp;
		} catch (NumberFormatException e) {
			return -1;
		}
	}
	
	void setNumPlayersEnabled(Boolean b) {
		((JTextField) window.numPlayers).setEnabled(b);
	}

	void setNumCardsText(int numCards) {
		((JTextField) window.numCards).setText(Integer.toString(numCards));
	}

	int getNumCardsText() {		
		try {
			int tmp = Integer.parseInt(((JTextField) window.numCards).getText());
			return tmp;
		} catch (NumberFormatException e) {
			return -1;
		}
	}

	void setNumCardsEnabled(Boolean b) {
		((JTextField) window.numCards).setEnabled(b);
	}

	void setTurnLabel(int turn) {
		((JLabel) window.turn).setText(Integer.toString(turn+1));
	}

	void setRoundLabel(int round) {
		((JLabel) window.round).setText(Integer.toString(round+1));
	}

	void setGameLabel(int game) {
		((JLabel) window.game).setText(Integer.toString(game));
	}

	void setNumPlayersLabel(int numPlayers) {
		((JTextField) window.numPlayers).setText(Integer.toString(numPlayers));
	}

	void setNumCardsLabel(int numCards) {
		((JTextField) window.numCards).setText(Integer.toString(numCards));
	}

	Container getHandsContainer() {
		return window.hands;
	}
	
	int getHandsNum() {
		return window.hands.getComponentCount();
	}
	
	void setPlayerHandLabel(int player, String text) {
		Container playerContainer = (Container) window.hands.getComponent(player);
//		((JLabel) playerContainer.getComponent(0)).setVisible(false);
//		((JLabel) playerContainer.getComponent(0)).repaint();
		((JLabel) playerContainer.getComponent(0)).setText(text);
//		((JLabel) playerContainer.getComponent(0)).validate();
//		((JLabel) playerContainer.getComponent(0)).repaint();
//		((JLabel) playerContainer.getComponent(0)).setVisible(true);

//		setTextNoFlicker((JLabel) playerContainer.getComponent(0), (text));
	}
	
	// From https://stackoverflow.com/questions/16227877/how-to-update-a-jcomponent-with-html-without-flickering
	void setTextNoFlicker(JLabel label, String text) {
	label.setText(text);
	// revalidate, but do so synchronously.
	Container validateRoot = label;
	while (! validateRoot.isValidateRoot()) {
	    Container parent = validateRoot.getParent();
	    if (parent == null)
	        break;
	    validateRoot = parent;
	}
	// This first validate() call may be excluded if the width is already correct
//	validateRoot.validate();
	NoopGraphics g = new NoopGraphics(0, 0, label.getWidth(), label.getHeight(), label.getGraphicsConfiguration(), false, false);
	label.paint(g);
	validateRoot.validate();
	// Now you can use the measured bounds for e.g. scrollRectToVisible
	}
	
	void setPlayerHandLabel(int player, Hand hand) {
		setPlayerHandLabel(player, makeCardListStringShort(player, hand.getCards()));
	}

	void setPlayerPlayedLabel(int player, Card card) {
		String str = "";
		if (card != null)
			str = "<html><div align='right'>" + card.toStringShort() + "</div></html>";
		Container playerContainer = (Container) window.hands.getComponent(player);
		((JLabel) playerContainer.getComponent(3)).setText(str);
	}

	void setPlayerTrickLabel (int player, int score){
		Container playerContainer = (Container) window.hands.getComponent(player);
		((JLabel) playerContainer.getComponent(5)).setText(Integer.toString(score));
	}
	
	void setPlayerScoreLabel(int player, int score) {
		Container playerContainer = (Container) window.hands.getComponent(player);
		((JLabel) playerContainer.getComponent(7)).setText(Integer.toString(score));
	}

	void setPlayedLabel(String text) {
		((JLabel) window.played).setText(text);
	}
	
	void setPlayedLabel(List<Card> cards) {
		setPlayedLabel(makeCardListStringShort(-1, cards));
	}
	
	String makeCardListStringShort (int player, List<Card> cards) {
		String str = "<html><div align='left'>";
		if (player>=0)
			str += ""+(player+1)+":&nbsp;&nbsp;";
		for (Card card: cards)
			str += card.toStringShort() + "&nbsp;&nbsp;";
		return str + "</div></html>";
	}

	void setMessageLabel(String message) {
		((JLabel) window.message).setForeground(Color.BLACK);
		((JLabel) window.message).setText(message);
	}

	void setMessage2Label(String message) {
		((JLabel) window.message2).setText(message);
	}

	void setMessageLabelRed(String message) {
		((JLabel) window.message).setForeground(Color.RED);
		((JLabel) window.message).setText(message);
	}

	boolean isSlow() {
		return window.slow.isSelected();
	}
	
	boolean isPaused() {
		return window.pause.getText().equals("Resume");
	}

	void setIsPaused() {
		window.pause.setText("Resume");
	}
	
	boolean isQuit() {
		return window.quit.isSelected();
	}
	
	void quit() {
		window.frame.dispose();
	}
	
}
