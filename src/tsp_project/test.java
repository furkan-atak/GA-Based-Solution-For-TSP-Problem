/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp_project;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.LinkedHashMap;
import java.util.Scanner;
import static tsp_project.Tsp_Project.readFile;

/**
 *
 * @author Pc
 */
public class test {
    public static void main(String[] args) {
        String thePath = "D:\\Downloads\\More Folders\\Cities Coordinates.txt";
        LinkedHashMap<String, String> cities = readFile(thePath);
        int[] bestList = {1, 2, 41, 22, 74, 73, 58, 47, 19, 62, 88, 31, 18, 83, 93, 100, 37, 92, 87, 21, 72, 55, 25,
 24, 29, 68, 80, 77, 3, 79, 81, 78, 34, 35, 20, 70, 27, 57, 15, 43, 42, 14, 44, 38, 86, 17,
 46, 36, 49, 64, 11, 63, 30, 76, 12, 26, 28, 53, 101, 89, 52, 10, 90, 32, 66, 65, 71, 9, 51,
 33, 54, 4, 75, 56, 23, 67, 39, 50, 69, 7, 48, 82, 8, 45, 84, 5, 60, 6, 94, 13, 97, 95, 59,
 98, 85, 91, 16, 61, 99, 96, 40};
        
        
    }
    
    public static LinkedHashMap<String, String> readFile(String path) {
        LinkedHashMap<String, String> cities = new LinkedHashMap<>();
        try {
            File myObj = new File(path);
            Scanner reader = new Scanner(myObj);
            while (reader.hasNextLine()) {
                String data = reader.nextLine();
                if (Character.isDigit(data.charAt(0))) {
                    cities.put(data.split(" ")[0], data.split(" ")[1] + " " + data.split(" ")[2]);
                }
                /*
                HashMap never preserves your Insertion Order. 
                It Internally Use a hashing Concept by which it generate a HashCode to the
                Corresponding key and add it to the HashMap.
                 */
            }
            reader.close();
        } catch (FileNotFoundException e) {
            System.out.println("An error occurred.");
            e.printStackTrace();
        }
        return cities;
    }
    
     public static double calculate(LinkedHashMap<String, String> cities) {
        double totalCost = 0.0;

        Object[] coordinates = cities.values().toArray();

        for (int i = 0; i < coordinates.length - 1; i++) {

            //city from 
            String range1 = (String) coordinates[i];
            //city to 
            String range2 = (String) coordinates[i + 1];

            int x1 = Integer.parseInt(range1.split(" ")[0]);
            int y1 = Integer.parseInt(range1.split(" ")[1]);
            int x2 = Integer.parseInt(range2.split(" ")[0]);
            int y2 = Integer.parseInt(range2.split(" ")[1]);
            /*
            To Check
            System.out.println("x1 = " + x1 + " y1 = " + y1 + " x2 = " + x2 + " y2 = " + y2);
             */
            double theDistance = Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
            totalCost += theDistance;
        }
        
        String end = (String) coordinates[coordinates.length-1];
        String start = (String) coordinates[0];
        int x1 = Integer.parseInt(end.split(" ")[0]);
        int y1 = Integer.parseInt(start.split(" ")[1]);
        int x2 = Integer.parseInt(start.split(" ")[0]);
        int y2 = Integer.parseInt(start.split(" ")[1]);
        double endToStart =  Math.sqrt(((x2 - x1) * (x2 - x1)) + ((y2 - y1) * (y2 - y1)));
        totalCost += endToStart;
        
        return totalCost;
    }
}
