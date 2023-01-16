import java.util.List;
import java.util.ArrayList;
import java.io.File;
import java.io.FileInputStream;
import java.util.Iterator;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;


/*
 * --GALE SHAPELY STABLE MATCHING--
 * Done using an excel spreadsheets file input, see FileSetup() for more info.
 * 
 * Sources I used online:
 * https://docs.oracle.com/javase/8/docs/api/java/util/ArrayList.html
 * https://poi.apache.org/components/spreadsheet/index.html
 * https://www.youtube.com/watch?v=xabbFBBn6T8&ab_channel=ChargeAhead
 */


public class Main {
    static List<Person> proposers = new ArrayList<Person>();
    static List<Person> responders = new ArrayList<Person>();
    static int n = 4; // Number of proposers or responders.
    
    public static void main(String[] args) throws Exception {
        FileSetup();
        FindStableMatches();
    }

    public static void FindStableMatches() {
        int proposerCounter = 0;
        int crushCounter = 0;
        int totalMatches = 0;

        while (totalMatches < 4) {
            int potentialPartner = proposers.get(proposerCounter).crushIndexes[crushCounter];
            if (proposers.get(proposerCounter).partner != -1) { // Proposer already has a partner
                proposerCounter++;
            } else if (responders.get(potentialPartner).partner == -1) { // Crush is free, so they get together
                responders.get(potentialPartner).setPartner(proposerCounter); // Sets responder's partner as proposer
                proposers.get(proposerCounter).setPartner(potentialPartner); // Sets proposer's partner as responder
                totalMatches++;
                proposerCounter++;
                crushCounter = 0;
            } else if (responders.get(potentialPartner).isBetterCrush(proposerCounter)) { // Responder likes new proposer more, so they dump their old partner for a new one
                int newPCounter = responders.get(potentialPartner).partner; // Since responder's old partner is about to be dumped, they will be the next to go through the algorithm
                proposers.get(proposerCounter).setPartner(potentialPartner); // New proposer's partner is set as responder
                proposers.get(responders.get(potentialPartner).partner).setPartner(-1); // Old proposer gets dumped
                responders.get(potentialPartner).setPartner(proposerCounter); // Responder's partner is set as new proposer
                proposerCounter = newPCounter;
                crushCounter = 0;
            } else { // Responder doesn't like new proposer, so proposer tries again with next crush
                crushCounter++;
            }
        }

        // Printing proposers.
        for (int i = 0; i < n; i++) {
            proposers.get(i).printName();
            System.out.print("'s partner is ");
            responders.get(proposers.get(i).partner).printName();
            System.out.println();
        }

        // Second print (of the responders) is solely to make sure there is no polyamory shenanigans going on.
        for (int i = 0; i < n; i++) {
            responders.get(i).printName();
            System.out.print("'s partner is ");
            proposers.get(responders.get(i).partner).printName();
            System.out.println();
        }
    }

    /*
     * I am reading the information from an excel spreadsheet. I am doing this using apache's API and have libraries.
     * https://poi.apache.org/components/spreadsheet/index.html
     * 
     * This video helped me A LOT as this was my first time reading from an excel spreadsheet, but it was fun! I could
     * have just done a .csv like I've done before but, it was so much more rewarding to be able to put the data in a
     * more readable way. Plus I learned something :).
     * https://www.youtube.com/watch?v=xabbFBBn6T8&ab_channel=ChargeAhead
     * 
     * The way I have it set up, is in excel there is a group of 4 proposers lined up and the crushes ordered by preference
     * from left to right. The crushes do not have their name entered, but their future index in the responders ArrayList.
     * Same thing for the responders, but their sectioned off below the proposers.
     * The code reads the name of the person from top to bottom and immediately adds them to their respective ArrayList
     * (responders or proposers). This means you can look at the excel sheet and their index from 0-4 is their order from
     * top to bottom.
     */
    public static void FileSetup() {
        File file = new File("C:\\Development\\Stable Matching\\preferences.xlsx");
        try {
            FileInputStream inputStream = new FileInputStream(file);
            XSSFWorkbook workbook = new XSSFWorkbook(inputStream);
            XSSFSheet sheet = workbook.getSheetAt(0);

            Iterator<Row> iterator = sheet.iterator();

            while (iterator.hasNext()) {
                Row row = iterator.next();
                Iterator<Cell> cellIterator = row.cellIterator();
                while (cellIterator.hasNext()) {
                    Cell cell = cellIterator.next();
                    Person p = new Person(cell.getStringCellValue()); // Setting the person's name.
                    int[] crushList = {0, 0, 0, 0};
                    for (int i = 0; i < n; i++) { // Iterating through the crushes, adding them to an int array, then setting that list of preferences to the person
                        cell = cellIterator.next();
                        crushList[i] = (int)cell.getNumericCellValue();
                    }
                    p.setCrush(crushList);
                    if (proposers.size() >= 4) {
                        responders.add(p);
                    } else {
                        proposers.add(p);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        System.out.println("\nPROPOSERS:");
        for (int i = 0; i < n; i++) {
            proposers.get(i).printName();
            System.out.print("'s preferences are ordered as: ");
            for (int j = 0; j < n; j++) {
                System.out.print(proposers.get(i).crushIndexes[j] + ", ");
            }
            System.out.println();
        }

        System.out.println("\nRESPONDERS:");
        for (int i = 0; i < n; i++) {
            responders.get(i).printName();
            System.out.print("'s preferences are ordered as: ");
            for (int j = 0; j < n; j++) {
                System.out.print(responders.get(i).crushIndexes[j] + ", ");
            }
            System.out.println();
        }
        System.out.println();
    }
}