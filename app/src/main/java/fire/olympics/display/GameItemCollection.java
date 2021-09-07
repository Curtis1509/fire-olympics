package fire.olympics.display;

import org.joml.Vector3f;

import java.util.ArrayList;

public class GameItemCollection {
    private ArrayList<GameItem> collection;

    public GameItemCollection(ArrayList<GameItem> gameItems) {
        collection = gameItems;
    }

    // returns the number of GameItems in the collection
    public int getCount() {return collection.size();}

    // gets all positions in an ArrayList
    public ArrayList<Vector3f> getPositions() {
        ArrayList<Vector3f> pos = new ArrayList<>();
        for (GameItem item : collection) {
            pos.add(item.getPosition());
        }
        return pos;
    }

    // gets the position of a single gameItem
    public Vector3f getPosition(int index) {
        return collection.get(index).getPosition();
    }

    // sets all GameItems to the same position
    // this should be fine to use normally, as their positions should be relative
    public void setPositions(Vector3f pos) {
        for (GameItem item : collection) {item.setPosition(pos);}
    }

    // sets all GameItems to positions in an array
    // again this shouldn't be necessary
    public void setPositions(ArrayList<Vector3f> pos) {
        for (int i = 0; i < collection.size(); i++) {
            if (i < pos.size()) collection.get(i).setPosition(pos.get(i));
        }
    }

    // sets the position of a single gameItem
    public void setPosition(Vector3f pos, int index) {
        collection.get(index).setPosition(pos);
    }



}
