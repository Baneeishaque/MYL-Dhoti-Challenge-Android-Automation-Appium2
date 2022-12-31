package ndk.csv_processor;

import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.BreakIterator;
import com.opencsv.CSVReader;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.lang3.ArrayUtils;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import static ndk.Constants.*;

public class Csv_Parser {

    public static void main(String[] args) {

        CSVReader reader;

        File file = new File(UCharacter.toTitleCase(DISTRICT_NAME.toLowerCase(), BreakIterator.getWordInstance()) + "_" + UCharacter.toTitleCase(PANCHAYATH_NAME.toLowerCase(), BreakIterator.getWordInstance()) + "_" + UCharacter.toTitleCase(WARD_NAMES[0].toLowerCase(), BreakIterator.getWordInstance()) + "_filled.csv");

        String[] header = {"Sl. No.", "Name", "Transaction Number", "Ward Name", "Panchayath Name", "District Name", "Date", "Time", "Amount", "Count", "Paid", "Collection Agent", "Payer", "Remarks"};

        try {

            reader = new CSVReader(new FileReader("Malappuram_Tanalur_Pandiyatt.csv"));

            String[] nextLine;

            reader.readNext();

            int offset = 0;

            FileWriter outputFile = new FileWriter(file);

            CSVPrinter csvPrinter = new CSVPrinter(outputFile, CSVFormat.DEFAULT);
            csvPrinter.printRecord((Object[]) header);

            while ((nextLine = reader.readNext()) != null) {

                nextLine[0] = String.valueOf(offset + Integer.parseInt(nextLine[0]));
                int count = Integer.parseInt(nextLine[9]);
                if (count > 1) {
                    for (int i = 1; i <= count; i++) {
                        Csv_Reader.countProcessor(nextLine, i);
                        processNextLine(nextLine, csvPrinter);
                        nextLine[0] = String.valueOf(Integer.parseInt(nextLine[0]) + 1);
                    }
                    offset = offset + (count - 1);
                } else {
                    processNextLine(nextLine, csvPrinter);
                }
                System.out.println();
            }

            outputFile.close();

        } catch (Exception e) {
            System.out.println(e.getLocalizedMessage());
        }
    }

    private static void processNextLine(String[] nextLine, CSVPrinter csvPrinter) {
        for (String s : nextLine) {

            System.out.print(s + "\t");
        }
        System.out.print("Yes" + "\t" + "Mufeed" + "\t" + "Fasil" + "\t" + "");
        String[] concatResult = ArrayUtils.addAll(nextLine, "Yes", "Mufeed", "Fasil", "");
        try {
            csvPrinter.printRecord((Object[]) concatResult);
        } catch (IOException e) {
            System.out.println(e.getLocalizedMessage());
        }
    }
}
