package fire.olympics.display;

import java.util.Arrays;

public class GenerateModel {

    //Generates a 3D object using the dimensions supplied and coordinates supplied.
    public static float[] createPositions(float x, float y, float z, float length, float height, float width){
        float[] positions = new float[] {
                // V0
                x+-width/2, y+height/2, z,
                // V1
                x+-width/2, y-height/2, z,
                // V2
                x+width/2, y-height/2, z,
                // V3
                x+width/2, y+height/2, z,
                // V4
                x-width/2, y+height/2, z+length,
                // V5
                x+width/2, y+height/2, z+length,
                // V6
                x-width/2, y-width/2, z+length,
                // V7
                x+width/2, y-width/2, z+length,

                // For text coords in top face
                // V8: V4 repeated
                x-width/2, y+height/2, z+length,
                // V9: V5 repeated
                x+width/2, y+height/2, z+length,
                // V10: V0 repeated
                x+-width/2, y+height/2, z,
                // V11: V3 repeated
                x+width/2, y+height/2, z,

                // For text coords in right face
                // V12: V3 repeated
                x+width/2, y+height/2, z,
                // V13: V2 repeated
                x+width/2, y-height/2, z,

                // For text coords in left face
                // V14: V0 repeated
                x+-width/2, y+height/2, z,
                // V15: V1 repeated
                x+-width/2, y-height/2, z,

                // For text coords in bottom face
                // V16: V6 repeated
                x-width/2, y-width/2, z+length,
                // V17: V7 repeated
                x+width/2, y-width/2, z+length,
                // V18: V1 repeated
                x+-width/2, y-height/2, z,
                // V19: V2 repeated
                x+width/2, y-height/2, z,
        };

        System.out.println(Arrays.toString(positions));

        return positions;
    }
    public static float[] createColours() {
        return new float[]{
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
                0.5f, 0.0f, 0.0f,
                0.0f, 0.5f, 0.0f,
                0.0f, 0.0f, 0.5f,
                0.0f, 0.5f, 0.5f,
        };
    }
    public static int[] createIndicies() {
        return new int[]{
                // Front face
                0, 1, 3, 3, 1, 2,
                // Top Face
                4, 0, 3, 5, 4, 3,
                // Right face
                3, 2, 7, 5, 3, 7,
                // Left face
                6, 1, 0, 6, 0, 4,
                // Bottom face
                2, 1, 6, 2, 6, 7,
                // Back face
                7, 6, 4, 7, 4, 5,
        };
    }


}
