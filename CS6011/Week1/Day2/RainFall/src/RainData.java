import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.FileNotFoundException;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class RainData {
    //    String filename;
//    RainData(filename){
//
//    }
    public static void main(String[] args) {

//        ArrayList<String> month = new ArrayList<>();
//        ArrayList<Integer> year = new ArrayList<>();
//        ArrayList<Double> rainfall = new ArrayList<>();



        try {
            File myFile = new File("rainfall_data.txt");
            Scanner fileReader = new Scanner(myFile);
            while (fileReader.hasNextLine()) {
                String data = fileReader.nextLine();
                System.out.println(data);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // calculate the average rainfall per month and store it in a variable

//        try {
//            FileWriter myWriter = new FileWriter("rainfall_results.txt");
//            myWriter.write("Hello world");
//            myWriter.close();
//            System.out.println("Successfully wrote to the file.");
//        } catch (IOException e) {
//            System.out.println("An error occurred.");
//            e.printStackTrace();
//        }
//    }
//        PrintWriter pw = new PrintWriter(newFileOutputStream("rainfall_results.txt"));


    }
}
