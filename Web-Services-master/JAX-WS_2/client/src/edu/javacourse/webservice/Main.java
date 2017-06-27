package edu.javacourse.webservice;

import java.util.List;

/**
 * Created by Georgy Gobozov on 10.02.2015.
 */
public class Main {


    public static void main(String[] args) {


        RegionServiceImplService regionServiceImpl = new RegionServiceImplService();
        RegionService regionService = regionServiceImpl.getRegionServiceImplPort();

        List<Region> allRegions = regionService.getAllRegions();

        for (Region region : allRegions) {
            System.out.println("region = " + region);
        }

    }

}
