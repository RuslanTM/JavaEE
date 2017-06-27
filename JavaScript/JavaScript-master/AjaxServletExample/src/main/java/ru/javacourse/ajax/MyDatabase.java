package ru.javacourse.ajax;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Georgy Gobozov on 17.03.2015.
 */
public class MyDatabase {

    static List<City> cities = new ArrayList<City>();

    static {
        cities.add(new City("Russia", "Spb"));
        cities.add(new City("Russia", "Moscow"));
        cities.add(new City("Russia", "Pskov"));

        cities.add(new City("USA", "NY"));
        cities.add(new City("USA", "LA"));
        cities.add(new City("USA", "Boston"));

        cities.add(new City("Spain", "Barcelona"));
        cities.add(new City("Spain", "Madrid"));
        cities.add(new City("Spain", "Valencia"));
    }



    public static List<City> getCitiesByCountry(String country){
        List<City> c = new ArrayList<City>();
        for (City city : cities) {
            if (country.equals(city.getCountry()))
                c.add(city);
        }
        return c;
    }





    public static class City{

        String name;
        String country;

        public City(String country, String name) {
            this.name = name;
            this.country = country;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getCountry() {
            return country;
        }

        public void setCountry(String country) {
            this.country = country;
        }
    }


}
