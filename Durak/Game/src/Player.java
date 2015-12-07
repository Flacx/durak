import java.util.ArrayList;

public class Player {

    private ArrayList<Card> hand;
    public Player(ArrayList<Card> hand) {
        this.hand = hand;
    }

    public ArrayList getHand() {
        return hand;
    }

    public void removeCard(int index) {
        hand.set(index, null);
    }

    public void setCard(int index, Card c) {
        hand.set(index, c);
    }

    public int getCardCount() {
        int a = 0;
        shiftDownCards();
        for (Card c : hand) {
            if (c != null) {
                a++;
            }
        }
        return a;
    }

    public void shiftDownCards() {
        ArrayList<Card> buffer = new ArrayList<Card>();
        for (Card c : hand) {
            if (c != null) {
                buffer.add(c);
            }
        }
        hand = buffer;
    }
}
