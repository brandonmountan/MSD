import java.io.File;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Scanner; // Import the Scanner class to read text files
import java.io.FileNotFoundException;
import java.io.FileWriter;   // Import the FileWriter class
import java.io.IOException;  // Import the IOException class to handle errors

//TIP To <b>Run</b> code, press <shortcut actionId="Run"/> or
// click the <icon src="AllIcons.Actions.Execute"/> icon in the gutter.
public class RainData {
    private static final DecimalFormat decfor = new DecimalFormat("0.00");
    public static void main(String[] args) {

        ArrayList<String> test = new ArrayList<>();
        ArrayList<Double> inchesArr = new ArrayList<>();
        ArrayList<Double> january = new ArrayList<>();
        ArrayList<Double> february = new ArrayList<>();
        ArrayList<Double> march = new ArrayList<>();
        ArrayList<Double> april = new ArrayList<>();
        ArrayList<Double> may = new ArrayList<>();
        ArrayList<Double> june = new ArrayList<>();
        ArrayList<Double> july = new ArrayList<>();
        ArrayList<Double> august = new ArrayList<>();
        ArrayList<Double> september = new ArrayList<>();
        ArrayList<Double> october = new ArrayList<>();
        ArrayList<Double> november = new ArrayList<>();
        ArrayList<Double> december = new ArrayList<>();

        try {
            File myFile = new File("rainfall_data.txt");
            Scanner fileReader = new Scanner(myFile);
            fileReader.useDelimiter("\\s+");
            while (fileReader.hasNext()) {
                String data = fileReader.next();
                test.add(data);
            }
            fileReader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }

        // calculate the average rainfall per month and store it in a variable
        // start by isolating all the doubles in a
        for (int i = 3; i < test.size(); i+=3) {
            double d = Double.parseDouble(test.get(i));
            inchesArr.add(d);
        }

        double janSum = 0;
        double febSum = 0;
        double marchSum = 0;
        double apSum = 0;
        double maySum = 0;
        double juneSum = 0;
        double julySum = 0;
        double augSum = 0;
        double sepSum = 0;
        double octSum = 0;
        double novSum = 0;
        double decSum = 0;

        for (int i = 0; i < inchesArr.size(); i+=12) {
            january.add(inchesArr.get(i));
            janSum += inchesArr.get(i);
        }
        for (int i = 1; i < inchesArr.size(); i+=12) {
            february.add(inchesArr.get(i));
            febSum += inchesArr.get(i);
        }
        for (int i = 2; i < inchesArr.size(); i+=12) {
            march.add(inchesArr.get(i));
            marchSum += inchesArr.get(i);
        }
        for (int i = 3; i < inchesArr.size(); i+=12) {
            april.add(inchesArr.get(i));
            apSum += inchesArr.get(i);
        }
        for (int i = 4; i < inchesArr.size(); i+=12) {
            may.add(inchesArr.get(i));
            maySum += inchesArr.get(i);
        }
        for (int i = 5; i < inchesArr.size(); i+=12) {
            june.add(inchesArr.get(i));
            juneSum += inchesArr.get(i);
        }
        for (int i = 6; i < inchesArr.size(); i+=12) {
            july.add(inchesArr.get(i));
            julySum += inchesArr.get(i);
        }
        for (int i = 7; i < inchesArr.size(); i+=12) {
            august.add(inchesArr.get(i));
            augSum += inchesArr.get(i);
        }
        for (int i = 8; i < inchesArr.size(); i+=12) {
            september.add(inchesArr.get(i));
            sepSum += inchesArr.get(i);
        }
        for (int i = 9; i < inchesArr.size(); i+=12) {
            october.add(inchesArr.get(i));
            octSum += inchesArr.get(i);
        }
        for (int i = 10; i < inchesArr.size(); i+=12) {
            november.add(inchesArr.get(i));
            novSum += inchesArr.get(i);
        }
        for (int i = 11; i < inchesArr.size(); i+=12) {
            december.add(inchesArr.get(i));
            decSum += inchesArr.get(i);
        }

        double janAvg = janSum / january.size();
        double febAvg = febSum / february.size();
        double marchAvg = marchSum / march.size();
        double apAvg = apSum / april.size();
        double mayAvg = maySum / may.size();
        double juneAvg = juneSum / june.size();
        double julyAvg = julySum / july.size();
        double augAvg = augSum / august.size();
        double sepAvg = sepSum / september.size();
        double octAvg = octSum / october.size();
        double novAvg = novSum / november.size();
        double decAvg = decSum / december.size();

        decfor.format(janAvg);
        decfor.format(febAvg);
        decfor.format(marchAvg);
        decfor.format(apAvg);
        decfor.format(mayAvg);
        decfor.format(juneAvg);
        decfor.format(julyAvg);
        decfor.format(augAvg);
        decfor.format(sepAvg);
        decfor.format(octAvg);
        decfor.format(novAvg);
        decfor.format(decAvg);

        try {
            FileWriter myWriter = new FileWriter("rainfall_results.txt");
            myWriter.write("The average rainfall for January is " + decfor.format(janAvg) + " inches \n" +
                    "The average rainfall for February is " + decfor.format(febAvg) + " inches \n" +
                    "The average rainfall for March is " + decfor.format(marchAvg) + " inches \n" +
                    "The average rainfall for April is " + decfor.format(apAvg) + " inches \n" +
                    "The average rainfall for May is " + decfor.format(mayAvg) + " inches \n" +
                    "The average rainfall for June is " + decfor.format(juneAvg) + " inches \n" +
                    "The average rainfall for July is " + decfor.format(julyAvg) + " inches \n" +
                    "The average rainfall for August is " + decfor.format(augAvg) + " inches \n" +
                    "The average rainfall for September is " + decfor.format(sepAvg) + " inches \n" +
                    "The average rainfall for October is " + decfor.format(octAvg) + " inches \n" +
                    "The average rainfall for November is " + decfor.format(novAvg) + " inches \n" +
                    "The average rainfall for December is " + decfor.format(decAvg) + " inches \n");
            myWriter.close();
            System.out.println("Successfully wrote to the file.");
        } catch (IOException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
    }
}
