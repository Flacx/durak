import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

public class Game {
	//test
	private Player[] players;

	private char trumpColor;
	private Card trumpCard;
	// private Card[] staple;
	private Card[] attackingCards;
	private Card[] coverCards;
	private Stack<Card> staple;

	private Random random;
	private int currentPlayer;
	private boolean gameOver;
	
	public void main(String[] args) {
		new Game(5);
	}

	public Game(int playerCount) {
		gameOver = false;
		if (playerCount >= 2 && playerCount < 6) {
			random = new Random();
			random.setSeed(System.currentTimeMillis());
			players = new Player[playerCount];
			staple = new Stack<Card>();
			trumpCard = null;
			int stapleIndex = 0;
			attackingCards = new Card[5];
			coverCards = new Card[5];

			// Stapel wird gefuelllt
			for (int i = 0; i < 4; i++) {
				for (int j = 7; j < 14; j++) {
					if (i == 0) {
						staple.push(new Card('C', (char) j)); // C = Cross
					} else if (i == 1) {
						staple.push(new Card('P', (char) j));
					} else if (i == 2) {
						staple.push(new Card('H', (char) j));
					} else if (i == 3) {
						staple.push(new Card('K', (char) j));
					}
					stapleIndex++;
				}
				if (stapleIndex > 31) {
					break;
				}
			}

			// STapel wird gemischt
			Collections.shuffle(staple, random);

			// Spieler bekommen Karten
			int cardIndex = 0;
			Card[] startCards = new Card[5];
			for (Player p : players) {
				for (int i = 0; i < 5; i++) {
					startCards[i] = staple.pop();
				}

				ArrayList<Card> hand = new ArrayList<Card>();
				for (Card card : startCards) {
					hand.add(card);
				}

				p = new Player(hand);

			}

			// trumpcard wird von stack genommen und der ganze wird auf nen
			// anderen geshiftet.
			// trump drqauf und zurueck geschiftet
			trumpCard = staple.pop();
			Stack<Card> bufferStack = new Stack<Card>();

			while (staple.peek() != null) {
				bufferStack.push(staple.pop());
			}
			bufferStack.push(trumpCard);

			while (bufferStack.peek() != null) {
				staple.push(bufferStack.pop());
			}

			// Welcher Spieler anfaengt muss noch implementiert werden.
			startGame(0);
		} else {
			System.out.println("Too few or too many players. (2-5)");
		}

	}

	public void startGame(int startPlayer) {
		currentPlayer = startPlayer;
		while (!gameOver) {
			currentPlayer = executeAttack(currentPlayer, false);
		}
	}

	public void printCards(int player) {
		for (int i = 0; i < 5; i++) {
			System.out.println((i + 1) + ") " + players[player].getHand().get(i));
		}
	}

	public void flushTable() {
		for (Card c : attackingCards) {
			c = null;
		}
		for (Card d : coverCards) {
			d = null;
		}
	}

	public void printTable() {
		for (Card c : attackingCards) {
			if (c != null) {
				System.out.print(c.getColor() + c.getNumber() + " ");
			}
		}
		System.out.println();

		for (Card d : coverCards) {
			if (d != null) {
				System.out.print(d.getColor() + d.getNumber() + " ");
			}
		}
		System.out.println();
	}

	public int executeAttack(int attackingPlayer, boolean shifted) {
		int attackedPlayer = 0;
		if (attackingPlayer == players.length - 1) {
			attackedPlayer = 0;
		} else {
			attackedPlayer = attackingPlayer + 1;
		}
		int nextPlayer = attackedPlayer;
		boolean player1ok = false;
		boolean player2ok = false;

		System.out.println("Attacking Player: " + attackingPlayer + ". Your cards: ");
		printCards(attackingPlayer);
		System.out.print("> ");
		Scanner in = new Scanner(System.in);
		int chosenCard = -1;
		String choice = null;
		while (chosenCard < 1 || chosenCard > players[attackingPlayer].getCardCount()) {
			choice = in.nextLine();
			chosenCard = Integer.parseInt(choice);
		}

		attackingCards[0] = (Card) players[attackingPlayer].getHand().get(chosenCard - 1);
		int attackingIndex = 0;
		// Muss gedeckt werden, es kann dazugelegt werden
		// 3 phasen: p0 deckt. p1 wirft dazu. p2 wirft dazu - repeat
		coverphase: while (!player1ok && !player2ok) {
			printTable();
			// p0 deckt. Szenarien: deckt und niemand wirft dazu runde vorbei.
			// schiebt weiter -> runde vorbei. p1 und p2 legen dazu, p0 deckt /
			// schluckt. next play
			System.out.println("Attacked Player: " + attackedPlayer + ". Cover with your cards: ");
			printCards(attackedPlayer);
			int answerCard = -2;
			while (answerCard < 1 || answerCard > players[attackedPlayer].getCardCount() && answerCard != -1) {
				choice = in.nextLine();
				answerCard = Integer.parseInt(choice);
			}
			// Schlucken
			if (answerCard == -1) {
				for (Card c : attackingCards) {
					if (c != null) {
						players[attackedPlayer].getHand().add(c);
					}
				}
				// Springt aus der äußeren Schleife und beendet so den zug
				break coverphase;
			} else { // Decken
				// Nicht trump mit nicht trump decken
				// atkKarte Farbe == coverKarte Farbe
				// atkKarte Farbe != trumpffarbe
				// coverKarte Farbe != trumpffarbe
				// coverKarte Nummer > attackKarte Nummer
				Card answer = (Card) players[attackedPlayer].getHand().get(answerCard);
				if ((answer.getColor() == attackingCards[attackingIndex].getColor())
						&& attackingCards[attackingIndex].getColor() != trumpColor && answer.getColor() != trumpColor
						&& (int) answer.getNumber() > (int) attackingCards[attackingIndex].getNumber()) {
					coverCards[attackingIndex] = (Card) players[attackedPlayer].getHand().get(answerCard);
					players[attackedPlayer].getHand().remove(answerCard);
					players[attackedPlayer].shiftDownCards();

					// Nicht trump mit trumpf decken
					// atkKarte Farbe != trumpf
					// coverKarte Farbe == trumpf
				} else if (attackingCards[attackingIndex].getColor() != trumpColor && answer.getColor() == trumpColor) {
					coverCards[attackingIndex] = (Card) players[attackedPlayer].getHand().get(answerCard);
					players[attackedPlayer].getHand().remove(answerCard);
					players[attackedPlayer].shiftDownCards();

					// Trmpf mit trumpf decken
					// atkKarte Farbe == trumpffarbe
					// coverKarte Farbe == trumpffarbe
					// coverKarte Nummer >
				} else if (attackingCards[attackingIndex].getColor() == trumpColor && answer.getColor() == trumpColor
						&& (int) answer.getNumber() > (int) attackingCards[attackingIndex].getNumber()) {
					coverCards[attackingIndex] = (Card) players[attackedPlayer].getHand().get(answerCard);
					players[attackedPlayer].getHand().remove(answerCard);
					players[attackedPlayer].shiftDownCards();

					// SCHIEBEN
				} else if (answer.getNumber() == attackingCards[attackingIndex].getNumber()) {
					if (answer.getColor() == trumpColor) {
						System.out.print("Your Card is a trump. Show or lay down? (s/l)");
						String d = in.nextLine();
						if (d.equals("s")) {
							nextPlayer = executeAttack(attackedPlayer, true);
						} else if (d.equals("l")) {
							attackingCards[attackingIndex + 1] = (Card) players[attackingPlayer].getHand()
									.get(answerCard);
						} else {
							// Nur vorzeigen

						}
					}
				}

				// Schieben
			}

			// Player 1 legt dazu
		}

		// Karten ziehen FALLS shifted = false ist
		flushTable();
		return nextPlayer;
	}

}
