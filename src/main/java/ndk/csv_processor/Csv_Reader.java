package ndk.csv_processor;

import com.opencsv.CSVReader;

import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class Csv_Reader {

    public static List<String> data = new ArrayList<>();

    public static void main(String[] args) {


        CSVReader reader;
        try {

            reader = new CSVReader(new FileReader("Malappuram_Tanalur_Pandiyatt_filled.csv"));

            String[] nextLine;

            reader.readNext();

            int offset = 0;

            while ((nextLine = reader.readNext()) != null) {

                nextLine[0] = String.valueOf(offset + Integer.parseInt(nextLine[0]));
                int count = Integer.parseInt(nextLine[9]);
                if (count > 1) {
                    for (int i = 1; i <= count; i++) {
                        countProcessor(nextLine, i);
                        processNextLine(nextLine);
                        nextLine[0] = String.valueOf(Integer.parseInt(nextLine[0]) + 1);
                    }
                    offset = offset + (count - 1);
                } else {
                    processNextLine(nextLine);
                }
//                System.out.println();
            }

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }

        int j = 1;
        for (int i = data.size() - 1; i >= 0; i--) {
            System.out.println((j++) + ". " + data.get(i));
        }
    }

    static void countProcessor(String[] nextLine, int index) {
        if (index != 1) {
//            System.out.println();
            nextLine[1] = nextLine[1].substring(0, nextLine[1].length() - 2) + " " + index;
        } else {
            nextLine[1] = nextLine[1] + " " + index;
        }
        nextLine[8] = String.valueOf(600);
        nextLine[9] = String.valueOf(1);
    }

    private static void processNextLine(String[] nextLine) {
//        String s = nextLine[0] + ". " + nextLine[1];
//        System.out.print(s);
        data.add(nextLine[1]);
    }
}
