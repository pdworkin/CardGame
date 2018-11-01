package com.heyho.demo.cardgame;

import java.awt.EventQueue;

import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import javax.swing.JLabel;
import javax.swing.BoxLayout;
import com.jgoodies.forms.factories.DefaultComponentFactory;
import java.awt.Component;
import javax.swing.Box;
import javax.swing.SwingConstants;
import java.awt.Font;
import java.awt.Dimension;
import javax.swing.border.EtchedBorder;
import javax.swing.JButton;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.border.EmptyBorder;
import java.awt.Rectangle;
import javax.swing.JTextField;

public class GuiWindowBuilder {

	JFrame frame;
	JLabel score;
	JLabel played;
	JPanel hands;
	JLabel turn;
	JLabel round;
	JLabel game;
	JButton slow;
	JButton pause;
	JButton fast;
	JLabel message2;
	JButton quit;
	JTextField winningScore;
	JPanel panel;
	JTextField numPlayers;
	JTextField numCards;
	JLabel message;

	/**
	 * Launch the application.
	 */
	public static void main(String[] args) {
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					GuiWindowBuilder window = new GuiWindowBuilder();
					window.frame.setVisible(true);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}

	/**
	 * Create the application.
	 */
	public GuiWindowBuilder() {
		initialize(this);
	}

	/**
	 * Initialize the contents of the frame.
	 */
	private void initialize(GuiWindowBuilder window) {
		frame = new JFrame();
		frame.setBounds(new Rectangle(100, 100, 600, 420));
		frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		frame.getContentPane().setLayout(new BorderLayout(0, 0));
		
		panel = new JPanel();
		panel.setBorder(new EtchedBorder(EtchedBorder.LOWERED, null, null));
		frame.getContentPane().add(panel, BorderLayout.CENTER);
		panel.setLayout(new BoxLayout(panel, BoxLayout.Y_AXIS));
		
		Component rigidArea_7 = Box.createRigidArea(new Dimension(10, 10));
		panel.add(rigidArea_7);
		
		JPanel handsLabel = new JPanel();
		handsLabel.setAlignmentX(Component.LEFT_ALIGNMENT);
		panel.add(handsLabel);
		handsLabel.setLayout(new BoxLayout(handsLabel, BoxLayout.X_AXIS));
		
		JLabel lblHands = DefaultComponentFactory.getInstance().createTitle("Hands:");
		handsLabel.add(lblHands);
		lblHands.setHorizontalAlignment(SwingConstants.LEFT);
		lblHands.setFont(new Font("Lucida Grande", Font.BOLD, 18));
		
		Component horizontalGlueHands = Box.createHorizontalGlue();
		handsLabel.add(horizontalGlueHands);
		
		JLabel lblNewLabel_5 = new JLabel("Played/Tricks/Score");
		lblNewLabel_5.setFont(new Font("Lucida Grande", Font.PLAIN, 9));
		lblNewLabel_5.setAlignmentX(Component.RIGHT_ALIGNMENT);
		lblNewLabel_5.setHorizontalAlignment(SwingConstants.RIGHT);
		handsLabel.add(lblNewLabel_5);
		
		Component rigidArea_5 = Box.createRigidArea(new Dimension(5, 5));
		handsLabel.add(rigidArea_5);
		
		hands = new JPanel();
		panel.add(hands);
		hands.setLayout(new BoxLayout(hands, BoxLayout.Y_AXIS));
		
		Component rigidArea_1 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_1);
		

		JLabel lblPlayed = new JLabel("Played:");
		lblPlayed.setHorizontalAlignment(SwingConstants.LEFT);
		lblPlayed.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		panel.add(lblPlayed);
		
		played = new JLabel("");
		played.setHorizontalAlignment(SwingConstants.LEFT);
		played.setFont(new Font("Lucida Grande", Font.PLAIN, 13));
		panel.add(played);
		
		Component rigidArea_4 = Box.createRigidArea(new Dimension(20, 20));
		panel.add(rigidArea_4);
		
		message = new JLabel("");
		panel.add(message);
		
		message2 = new JLabel("");
		panel.add(message2);
		
		JPanel headerBox = new JPanel();
		headerBox.setBorder(new EmptyBorder(5, 0, 5, 0));
		frame.getContentPane().add(headerBox, BorderLayout.NORTH);
		headerBox.setLayout(new BoxLayout(headerBox, BoxLayout.Y_AXIS));
		
		JLabel lblNewLabel = new JLabel("Card Game");
		lblNewLabel.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel.setHorizontalAlignment(SwingConstants.CENTER);
		headerBox.add(lblNewLabel);
		
		JPanel rightBox = new JPanel();
		frame.getContentPane().add(rightBox, BorderLayout.EAST);
		rightBox.setLayout(new BoxLayout(rightBox, BoxLayout.Y_AXIS));
		
		JLabel lblHighest = new JLabel("Highest");
		lblHighest.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblHighest.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblHighest.setHorizontalAlignment(SwingConstants.CENTER);
		rightBox.add(lblHighest);
		
		JLabel lblNewLabel_1 = new JLabel("Score");
		lblNewLabel_1.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_1.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblNewLabel_1.setHorizontalAlignment(SwingConstants.CENTER);
		rightBox.add(lblNewLabel_1);
		
		score = new JLabel("0");
		score.setAlignmentX(Component.CENTER_ALIGNMENT);
		score.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		score.setHorizontalAlignment(SwingConstants.CENTER);
		rightBox.add(score);
		
		Component rigidArea = Box.createRigidArea(new Dimension(80, 20));
		rightBox.add(rigidArea);
		
		JLabel lblNewLabel_2 = new JLabel("To Win");
		lblNewLabel_2.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblNewLabel_2.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		rightBox.add(lblNewLabel_2);
		
		winningScore = new JTextField("0");
		winningScore.setMinimumSize(new Dimension(40, 40));
		winningScore.setPreferredSize(new Dimension(40, 40));
		winningScore.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		winningScore.setMaximumSize(new Dimension(40, 40));
		winningScore.setHorizontalAlignment(SwingConstants.CENTER);
		rightBox.add(winningScore);
		
		Component rigidArea_8 = Box.createRigidArea(new Dimension(80, 20));
		rightBox.add(rigidArea_8);
		
		JLabel lblPlayers = new JLabel("Players");
		lblPlayers.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblPlayers.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayers.setAlignmentX(Component.CENTER_ALIGNMENT);
		rightBox.add(lblPlayers);
		
		numPlayers = new JTextField("0");
		numPlayers.setPreferredSize(new Dimension(40, 40));
		numPlayers.setMinimumSize(new Dimension(40, 40));
		numPlayers.setMaximumSize(new Dimension(40, 40));
		numPlayers.setHorizontalAlignment(SwingConstants.CENTER);
		numPlayers.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		rightBox.add(numPlayers);
		
		Component rigidArea_9 = Box.createRigidArea(new Dimension(110, 20));
		rightBox.add(rigidArea_9);
		
		JLabel lblCardsDealt = new JLabel("Cards Dealt");
		lblCardsDealt.setHorizontalAlignment(SwingConstants.CENTER);
		lblCardsDealt.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblCardsDealt.setAlignmentX(0.5f);
		rightBox.add(lblCardsDealt);
		
		numCards = new JTextField("0");
		numCards.setPreferredSize(new Dimension(40, 40));
		numCards.setMinimumSize(new Dimension(40, 40));
		numCards.setMaximumSize(new Dimension(40, 40));
		numCards.setHorizontalAlignment(SwingConstants.CENTER);
		numCards.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		rightBox.add(numCards);
		
		JPanel leftBox = new JPanel();
		leftBox.setInheritsPopupMenu(true);
		frame.getContentPane().add(leftBox, BorderLayout.WEST);
		leftBox.setLayout(new BoxLayout(leftBox, BoxLayout.Y_AXIS));
		
		JLabel lblPlayer = new JLabel("Player");
		lblPlayer.setFont(new Font("Lucida Grande", Font.PLAIN, 15));
		lblPlayer.setHorizontalAlignment(SwingConstants.CENTER);
		lblPlayer.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftBox.add(lblPlayer);
		
		JLabel lblTurn = new JLabel("Turn");
		lblTurn.setAlignmentX(Component.CENTER_ALIGNMENT);
		lblTurn.setHorizontalAlignment(SwingConstants.CENTER);
		lblTurn.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		leftBox.add(lblTurn);
		
		turn = new JLabel("1");
		turn.setAlignmentX(Component.CENTER_ALIGNMENT);
		turn.setHorizontalAlignment(SwingConstants.CENTER);
		turn.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		leftBox.add(turn);
		
		Component rigidArea_2 = Box.createRigidArea(new Dimension(110, 20));
		leftBox.add(rigidArea_2);
		
		JLabel lblNewLabel_3 = new JLabel("Round");
		lblNewLabel_3.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblNewLabel_3.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftBox.add(lblNewLabel_3);
		
		round = new JLabel("1");
		round.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		round.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftBox.add(round);
		
		Component rigidArea_3 = Box.createRigidArea(new Dimension(80, 20));
		leftBox.add(rigidArea_3);
		
		JLabel lblNewLabel_4 = new JLabel("Game");
		lblNewLabel_4.setFont(new Font("Lucida Grande", Font.PLAIN, 18));
		lblNewLabel_4.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftBox.add(lblNewLabel_4);
		
		game = new JLabel("1");
		game.setFont(new Font("Lucida Grande", Font.PLAIN, 24));
		game.setAlignmentX(Component.CENTER_ALIGNMENT);
		leftBox.add(game);
		
		JPanel bottonBox = new JPanel();
		frame.getContentPane().add(bottonBox, BorderLayout.SOUTH);
		
		pause = new JButton(" Start ");
		pause.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!pause.getText().equals("  Pause  "))
					pause.setText("  Pause  ");
				else
					pause.setText("Resume");
			}
		});
		bottonBox.add(pause);
		
		slow = new JButton("Slow");
		slow.setSelected(true);
		slow.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!slow.isSelected()) {
					slow.setSelected(true);
					fast.setSelected(false);
				}
			}
		});
		bottonBox.add(slow);
		
		fast = new JButton("Fast");
		fast.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (!fast.isSelected()) {
					fast.setSelected(true);
					slow.setSelected(false);
				}
			}
		});
		bottonBox.add(fast);
		
		Component rigidArea_6 = Box.createRigidArea(new Dimension(80, 10));
		bottonBox.add(rigidArea_6);
		
		quit = new JButton("Quit");
		quit.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				quit.setSelected(true);
			}
		});
		bottonBox.add(quit);
	}
}
