/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package tsp_project;

import com.sun.xml.internal.fastinfoset.algorithm.IntegerEncodingAlgorithm;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Stack;
import java.util.stream.Collectors;
import java.util.concurrent.ThreadLocalRandom;

/**
 *
 * @author Pc
 */
public class Tsp_Project {

    public static void main(String[] args) {
        String thePath = "D:\\Downloads\\More Folders\\Cities Coordinates.txt";
        LinkedHashMap<String, String> cities = readFile(thePath);
        double max1 = Double.MAX_VALUE;
        double max2 = Double.MAX_VALUE;
        int populationSize = 300;
        int mutationCount = 3;
        int parentNumber = 8;
        int generation = 5000;
        ArrayList<LinkedHashMap<String, String>> routes = initialize1(cities, populationSize);
        ArrayList<LinkedHashMap<String, String>> steps = new ArrayList<>();
        
        
        int[] bests = getBestRoutes(routes, parentNumber);
         for (int i : bests) {
            double bestOne = calculate(routes.get(i));
            if(bestOne < max1) max1 = bestOne;
        }
         
        while (generation-- > 0) {
            ArrayList<ArrayList<String>> crossOveredIds = crossOver(routes, parentNumber);
            ArrayList<ArrayList<String>> mutatedIds = mutation(crossOveredIds, mutationCount);
            steps = deleteWorstAddChildren(routes, mutatedIds);
            routes = steps;
        }
        
        int[] bests1 = getBestRoutes(routes, parentNumber);
        LinkedHashMap<String,String> best = new LinkedHashMap<>();
        System.out.println("At first best path cost = "+ max1);
        System.out.println("SECOND");
        for (int i : bests1) {
            double bestOne = calculate(routes.get(i));
            if(bestOne < max2) {
                max2 = bestOne;
                best = routes.get(i);
            }
        }
        System.out.println("After GA = " + max2);
        System.out.println(best);
        System.out.println(best.keySet());
        System.out.println(calculate(best));
    }

    // Function to read the file for the specific problem
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

    //Calculates given path of cities as euclidian distance
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
    /*
    Dynamic Starting Point
    public static ArrayList<LinkedHashMap<String, String>> initialize(HashMap<String, String> cities) {

        ArrayList<LinkedHashMap<String, String>> routes = new ArrayList<>();
        //LinkedHashMap to save order of cities
        LinkedHashMap<String, String> citiesLinked = new LinkedHashMap<>();
        //Casting HashMap to set to shuffle started path
        List<HashMap.Entry<String, String>> shuffle = new ArrayList<>(cities.entrySet());

        //Copying HashMap to Linked because our list is going to be typeof LinkedHashMap
        shuffle.forEach(t -> citiesLinked.put(t.getKey(), t.getValue()));

        //init element adding
        routes.add(citiesLinked);
        //Looping over to shuffle and create the population
        for (int i = 1; i < 100; i++) {
            LinkedHashMap<String, String> aRoute = new LinkedHashMap<>();
            //shuffling
            Collections.shuffle(shuffle);
            //shuffled set to LinkedHashMap
            shuffle.forEach(t -> aRoute.put(t.getKey(), t.getValue()));
            //adding to population
            routes.add(aRoute);
        }
        return routes;
    }*/
    //Static Starting Point
    public static ArrayList<LinkedHashMap<String, String>> initialize1(LinkedHashMap<String, String> cities, int size) {

        ArrayList<LinkedHashMap<String, String>> routes = new ArrayList<>();

        //Casting HashMap to set to shuffle started path
        List<HashMap.Entry<String, String>> shuffle = new ArrayList<>(cities.entrySet());

        //init element adding
        routes.add(cities);
        //Looping over to shuffle and create the population
        for (int i = 1; i < size; i++) {
            LinkedHashMap<String, String> aRoute = new LinkedHashMap<>();
            //shuffling
            Collections.shuffle(shuffle.subList(1, 101));
            //shuffled set to LinkedHashMap
            shuffle.forEach(t -> aRoute.put(t.getKey(), t.getValue()));
            //adding to population
            routes.add(aRoute);
        }
        return routes;
    }

    //getWorstRoutes to replace with crossovered and mutated children
    public static int[] getWorstRoutes(ArrayList<LinkedHashMap<String, String>> routes, int parentNum) {
        //size of population
        int routesSize = routes.size();
        //to save their lengths with in order
        Double[] routeLengths = new Double[routes.size()];
        //to save worsts
        int[] worsts = new int[parentNum];
        //looping over routesSize/2 to loop less adding length from start and end
        for (int i = 0; i < routesSize / 2; i++) {
            double pathLength1 = calculate(routes.get(i));
            double pathLength2 = calculate(routes.get(routesSize - i - 1));

            routeLengths[i] = pathLength1;
            routeLengths[routesSize - i - 1] = pathLength2;
        }

        List<Double> routeList = new ArrayList<>(Arrays.asList(routeLengths));

        for (int i = 0; i < parentNum; i++) {
            Double theMax = Collections.max(routeList);
            Double theMin = Collections.min(routeList);
            int index = routeList.indexOf(theMax);
            worsts[i] = index;
            routeList.set(index, theMin);
        }

        return worsts;
    }

    public static int[] getBestRoutes(ArrayList<LinkedHashMap<String, String>> routes, int parentNum) {
        int routesSize = routes.size();
        Double[] routeLengths = new Double[routes.size()];
        //Arrays.fill(routeLengths, 0.0);
        int[] worsts = new int[parentNum];
        for (int i = 0; i < routesSize; i++) {
            double pathLength1 = calculate(routes.get(i));
            //   double pathLength2 = calculate(routes.get(routesSize - i - 1));
            routeLengths[i] = pathLength1;
            //   routeLengths[routesSize - i - 1] = pathLength2;
        }

        List<Double> routeList = new ArrayList<>(Arrays.asList(routeLengths));

        for (int i = 0; i < parentNum; i++) {
            Double theMin = Collections.min(routeList);
            Double theMax = Collections.max(routeList);
            int index = routeList.indexOf(theMin);
            worsts[i] = index;
            routeList.set(index, theMax);
        }

        return worsts;
    }

    public static ArrayList<ArrayList<String>> crossOver(ArrayList<LinkedHashMap<String, String>> routes, int parentNum) {
        ArrayList<ArrayList<String>> crossOvered = new ArrayList<>();
        int[] bestsAsParent = getBestRoutes(routes, parentNum);
        int random = ThreadLocalRandom.current().nextInt(2, 101);
        int randomRangeStart = random > 85 ? random - 15 : random;
        int randomRangeEnd = randomRangeStart + 15;
        for (int i = 0; i < parentNum - 1; i += 2) {
            LinkedHashMap<String, String> firstCrossOveredParent = routes.get(bestsAsParent[i]);
            LinkedHashMap<String, String> secondCrossOveredParent = routes.get(bestsAsParent[i + 1]);
            //Arrays.asList returns a static list [] thats why we have to cast it to ArrayList
            List<String> firstParentIds = new ArrayList<>(Arrays.asList((firstCrossOveredParent.keySet().toArray(new String[firstCrossOveredParent.size()]))));
            List<String> secondParentIds = new ArrayList<>(Arrays.asList((secondCrossOveredParent.keySet().toArray(new String[secondCrossOveredParent.size()]))));
            //    System.out.println("BEFORE CO first: " + firstParentIds);
            //    System.out.println("BEFORE CO second: "+ secondParentIds);
            for (int j = randomRangeStart; j < randomRangeEnd; j++) {
                String id = firstParentIds.get(j);
                firstParentIds.set(j, secondParentIds.get(j));
                secondParentIds.set(j, id);
            }
            //    System.out.println("AFTER first: " + firstParentIds);
            //    System.out.println("AFTER second: "+ secondParentIds);
            firstParentIds = makeDistinct((ArrayList<String>) firstParentIds, randomRangeStart, randomRangeEnd);
            secondParentIds = makeDistinct((ArrayList<String>) secondParentIds, randomRangeStart, randomRangeEnd);

            // System.out.println("DISTINCT first: " + firstParentIds);
            // System.out.println("DISTINCT second: " + secondParentIds);
            crossOvered.add((ArrayList<String>) firstParentIds);
            crossOvered.add((ArrayList<String>) secondParentIds);
        }
        return crossOvered;
    }

    public static ArrayList<ArrayList<String>> mutation(ArrayList<ArrayList<String>> crossOveredIds, int mutationCount) {
        ArrayList<ArrayList<String>> mutated = new ArrayList<>();
        for (int i = 0; i < crossOveredIds.size(); i++) {
            ArrayList<String> child1 = crossOveredIds.get(i);
            for (int j = 0; j < mutationCount / 2; j++) {
                int rand1 = ThreadLocalRandom.current().nextInt(2, 101);
                int rand2 = ThreadLocalRandom.current().nextInt(2, 101);
                String id = child1.get(rand1);
                child1.set(rand1, child1.get(rand2));
                child1.set(rand2, id);
            }
            mutated.add(child1);
        }

        return mutated;
    }

    public static ArrayList<String> makeDistinct(ArrayList<String> idList, int rangeStart, int rangeEnd) {
        List<String> subList = idList.subList(rangeStart, rangeEnd);
        ArrayList<String> distinctList = new ArrayList<>();
        for (int i = 0; i < idList.size(); i++) {
            if (subList.contains(idList.get(i)) && !(i >= rangeStart && i < rangeEnd)) {
                distinctList.add("0");
            } else {
                distinctList.add(idList.get(i));
            }
        }

        ArrayList<String> originalList = new ArrayList<>();
        for (int i = 1; i < 102; i++) {
            originalList.add(String.valueOf(i));
        }
        
        originalList.removeAll(idList);
        while (distinctList.contains("0")) {
            distinctList.set(distinctList.indexOf("0"), originalList.get(0));
            originalList.remove(0);
        }

        return distinctList;
    }

    public static ArrayList<LinkedHashMap<String, String>> deleteWorstAddChildren(ArrayList<LinkedHashMap<String, String>> routes,
            ArrayList<ArrayList<String>> childrenIds) {
        LinkedHashMap<String, String> getValues = routes.get(0);

        int[] worstsToReplace = getWorstRoutes(routes, childrenIds.size());
        for (int i = 0; i < worstsToReplace.length; i++) {
            LinkedHashMap<String, String> childKeyValuePaired = new LinkedHashMap<>();
            for (int j = 0; j < childrenIds.get(i).size(); j++) {
                
                String Id = childrenIds.get(i).get(j);
                childKeyValuePaired.put(Id, getValues.get(Id));
            }
            routes.set(worstsToReplace[i], childKeyValuePaired);
        }

        return routes;
    }

}
