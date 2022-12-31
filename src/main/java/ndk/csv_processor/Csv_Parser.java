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
            int offset = 0;
            while ((nextLine = reader.readNext()) != null) {

                nextLine[0] = String.valueOf(offset + Integer.parseInt(nextLine[0]));
                int count = Integer.parseInt(nextLine[9]);
                if (count > 1) {
                    for (int i = 1; i <= count; i++) {
                        if (i != 1) {
                            System.out.println();
                            nextLine[1] = nextLine[1].substring(0, nextLine[1].length() - 2) + " " + i;
                        } else {
                            nextLine[1] = nextLine[1] + " " + i;
                        }
                        nextLine[8] = String.valueOf(600);
                        nextLine[9] = String.valueOf(1);
                        for (int j = 0; j < nextLine.length; j++) {

                            System.out.print(nextLine[j] + "\t");
                        }
                        nextLine[0] = String.valueOf(Integer.parseInt(nextLine[0]) + 1);
                    }
                    offset = offset + (count - 1);
                } else {
                    for (int j = 0; j < nextLine.length; j++) {

                        System.out.print(nextLine[j] + "\t");
                    }
                }
                System.out.println();
            }
        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
