package fire.olympics;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class FileReader {

    public String read(String filename){

        String processString = "";
        Scanner scanner = null;
        try {
            scanner = new Scanner(new File(filename));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        while (scanner.hasNextLine()) {
            while (scanner.hasNext()) {
                processString += " " + scanner.next();
            }
        }

        return processString;
    }

}
