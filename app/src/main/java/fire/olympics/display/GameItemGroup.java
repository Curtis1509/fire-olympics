package fire.olympics.display;

import org.joml.Vector3f;

import java.util.ArrayList;

/** Class purpose:
 *  Manage entities such as the arrow, stadium etc.
 *  that are made of multiple gameItems with different meshes and textures,
 *  so we can treat them as a single thing without breaking any lower-level code.
 *
 *  Setting a single position or rotation for all gameItems works;
 *  that is how the original rotating arrow was done.
 *  Named a "group" not a "collection", because individual elements shouldn't be separated.
 */

public class GameItemGroup {
    private ArrayList<GameItem> group;

    // GameItems should be constructed separately, then "grouped"
    public GameItemGroup(ArrayList<GameItem> gameItems) { group = gameItems; }

    // returns the number of GameItems in the collection
    public int getCount() { return group.size(); }

    // returns the GameItem at a given index
    // this *should* only need to be used in debugging
    public GameItem get(int index) { return group.get(index); }

    // gets the position of the GameItemGroup from the first GameItem
    // All GameItems in the group should have the same position
    public Vector3f getPosition() {
        return group.get(0).getPosition();
    }

    // sets the position of the GameItemGroup
    public void setPosition(Vector3f position) {
        for (GameItem item : group)
            item.setPosition(position);
    }

    // sets the position of the GameItemGroup
    public void setPositions(float x, float y, float z) {
        for (GameItem item : group)
            item.setPosition(x,y,z);
    }

    // gets the rotation of the GameItemGroup from the first GameItem
    // All GameItems in the group should have the same rotation
    public Vector3f getRotation() {
        return group.get(0).getRotation();
    }

    // rotates the GameItemGroup
    public void setRotation(Vector3f rotation) {
        for (GameItem item : group)
            item.setRotation(rotation);
    }

    // rotates the GameItemGroup
    public void setRotation(float x, float y, float z) {
        for (GameItem item : group)
            item.setRotation(x,y,z);
    }

    // gets the scale of the GameItemGroup from the first GameItem
    // All GameItems in the group should have the same scale
    public float getScale() {
        return group.get(0).getScale();
    }

    // scales the GameItemGroup
    public void setScale(float scale) {
        for (GameItem item : group)
            item.setScale(scale);
    }

}