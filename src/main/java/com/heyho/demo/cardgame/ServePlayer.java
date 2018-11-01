package com.heyho.demo.cardgame;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

class ServePlayer extends Thread {
	Server game;
	ServerSocket serverSocket;
	int playerNum;
	int numPlayers;
	Socket socket;
	PrintWriter out;
	BufferedReader in;
	
	ServePlayer(Server game, ServerSocket serverSocket, int playerNum, int numPlayers) {
		this.game = game;
		this.serverSocket = serverSocket;
		this.playerNum = playerNum;
		this.numPlayers = numPlayers;
	}
	
	public void run() {
		try {
            socket = serverSocket.accept();
        } catch (IOException e) {
            System.out.println("I/O error: " + e);
            return;
        }
		
		try {
			out = new PrintWriter(socket.getOutputStream(), true);
			in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		} catch (IOException e) {
			e.printStackTrace();
			return;
		}

		System.out.println("Server thread #" + playerNum + ": Connection from " + socket.getRemoteSocketAddress());
		out.println(playerNum);
		out.println(numPlayers);

		game.playersAttached.getAndIncrement();
		while (game.playersAttached.get() > 0)
			try {
				Thread.sleep(100);
			} catch (InterruptedException e1) {
				e1.printStackTrace();
				return;
			}

		while (true) {
		try {
			boolean didWork = false;
			if (game.toClient.get(playerNum) != null) {
				if (game.toClient.get(playerNum).equals("QUIT")) {
					game.toClient.set(playerNum, null);
					break;
				}
				didWork = true;
				out.println(game.toClient.get(playerNum));
				game.toClient.set(playerNum, null);
			} else if (game.fromClient.get(playerNum) == null && in.ready()) {
				game.fromClient.set(playerNum, in.readLine());
				didWork = true;
			}
			if (!didWork)
				sleep(10);
		} catch (Exception e) {
			System.out.println("ERROR: Player #" + playerNum + " I/O failed: " + e);
			break;
		}
		}
		
		System.out.println("Player #" + playerNum + " quitting");
		try {
			in.close();
			out.close();
			socket.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}

