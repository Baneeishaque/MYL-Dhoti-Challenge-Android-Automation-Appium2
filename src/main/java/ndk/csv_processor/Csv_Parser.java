package ndk.csv_processor;

import com.opencsv.CSVReader;

import java.io.FileReader;

public class Csv_Parser {

    public static void main(String[] args) {

        CSVReader reader;
        try {
            reader = new CSVReader(new FileReader("Malappuram_Tanalur_Pandiyatt.csv"));
            String[] nextLine;
            reader.readNext();
            while ((nextLine = reader.readNext()) != null) {
                for (int i = 0; i < nextLine.length; i++) {
                    System.out.print(nextLine[i] + "\t");
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
