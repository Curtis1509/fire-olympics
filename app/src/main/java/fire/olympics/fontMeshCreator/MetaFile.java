package fire.olympics.fontMeshCreator;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Provides functionality for getting the values from a font file.
 * 
 * @author Karl
 *
 */
public class MetaFile {

    private static final String SPLITTER = " ";
    private static final String NUMBER_SEPARATOR = ",";

    private BufferedReader reader;
    private Map<String, String> values = new HashMap<String, String>();

    /**
     * Opens a font file in preparation for reading.
     * 
     * @param file
     *            - the font file.
     */
    public MetaFile(File file, double aspectRatio) {
        openFile(file);
    }

    /**
     * Read in the next line and store the variable values.
     * 
     * @return {@code true} if the end of the file hasn't been reached.
     */
    public boolean processNextLine() {
        values.clear();
        String line = null;
        try {
            line = reader.readLine();
        } catch (IOException e1) {
        }
        if (line == null) {
            return false;
        }
        for (String part : line.split(SPLITTER)) {
            String[] valuePairs = part.split("=");
            if (valuePairs.length == 2) {
                values.put(valuePairs[0], valuePairs[1]);
            }
        }
        return true;
    }

    /**
     * Gets the {@code int} value of the variable with a certain name on the
     * current line.
     * 
     * @param variable
     *            - the name of the variable.
     * @return The value of the variable.
     */
    public int getValueOfVariable(String variable) {
        return Integer.parseInt(values.get(variable));
    }

    /**
     * Gets the array of ints associated with a variable on the current line.
     * 
     * @param variable
     *            - the name of the variable.
     * @return The int array of values associated with the variable.
     */
    public int[] getValuesOfVariable(String variable) {
        String[] numbers = values.get(variable).split(NUMBER_SEPARATOR);
        int[] actualValues = new int[numbers.length];
        for (int i = 0; i < actualValues.length; i++) {
            actualValues[i] = Integer.parseInt(numbers[i]);
        }
        return actualValues;
    }

    /**
     * Closes the font file after finishing reading.
     */
    public void close() {
        try {
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Opens the font file, ready for reading.
     * 
     * @param file
     *            - the font file.
     */
    private void openFile(File file) {
        try {
            reader = new BufferedReader(new FileReader(file));
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Couldn't read font meta file!");
        }
    }
}
