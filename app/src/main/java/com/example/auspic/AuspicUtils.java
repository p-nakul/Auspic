package com.example.auspic;

import java.util.ArrayList;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AuspicUtils {

    private Map<String, List<AuspicHour>> auspicData = new HashMap<>();
    public AuspicUtils(){
        this.createMap();
    }

    public void createMap(){
        // Manually create the intervals for each day
        List<AuspicHour> sundayIntervals = new ArrayList<>();
        sundayIntervals.add(new AuspicHour(1, 90, "Udveg", 0));
        sundayIntervals.add(new AuspicHour(91, 180, "Char", 1));
        sundayIntervals.add(new AuspicHour(181, 270, "Labh", 1));
        sundayIntervals.add(new AuspicHour(271, 360, "Amrit", 2));
        sundayIntervals.add(new AuspicHour(361, 450, "Kaal", 0));
        sundayIntervals.add(new AuspicHour(451, 540, "Shubh", 3));
        sundayIntervals.add(new AuspicHour(541, 630, "Rog", 0));
        sundayIntervals.add(new AuspicHour(631, 720, "Udveg", 0));

        auspicData.put("Sunday", sundayIntervals);

        List<AuspicHour> mondayIntervals = new ArrayList<>();
        mondayIntervals.add(new AuspicHour(1, 90, "Amrit", 2));
        mondayIntervals.add(new AuspicHour(91, 180, "Kaal", 0));
        mondayIntervals.add(new AuspicHour(181, 270, "Shubh", 3));
        mondayIntervals.add(new AuspicHour(271, 360, "Rog", 0));
        mondayIntervals.add(new AuspicHour(361, 450, "Udveg", 0));
        mondayIntervals.add(new AuspicHour(451, 540, "Char", 1));
        mondayIntervals.add(new AuspicHour(541, 630, "Labh", 2));
        mondayIntervals.add(new AuspicHour(631, 720, "Amrit", 2));

        auspicData.put("Monday", mondayIntervals);

        List<AuspicHour> tuesdayIntervals = new ArrayList<>();
        tuesdayIntervals.add(new AuspicHour(1, 90, "Rog", 0));
        tuesdayIntervals.add(new AuspicHour(91, 180, "Udveg", 0));
        tuesdayIntervals.add(new AuspicHour(181, 270, "Char", 1));
        tuesdayIntervals.add(new AuspicHour(271, 360, "Labh", 1));
        tuesdayIntervals.add(new AuspicHour(361, 450, "Amrit", 2));
        tuesdayIntervals.add(new AuspicHour(451, 540, "Kaal", 0));
        tuesdayIntervals.add(new AuspicHour(541, 630, "Subh", 3));
        tuesdayIntervals.add(new AuspicHour(631, 720, "Amrit", 2));

        auspicData.put("Tuesday", tuesdayIntervals);

        List<AuspicHour> wednesdayIntervals = new ArrayList<>();
        wednesdayIntervals.add(new AuspicHour(1, 90, "Labh", 1));
        wednesdayIntervals.add(new AuspicHour(91, 180, "Amrit", 2));
        wednesdayIntervals.add(new AuspicHour(181, 270, "Kaal", 0));
        wednesdayIntervals.add(new AuspicHour(271, 360, "Subh", 3));
        wednesdayIntervals.add(new AuspicHour(361, 450, "Rog", 0));
        wednesdayIntervals.add(new AuspicHour(451, 540, "Udveg", 0));
        wednesdayIntervals.add(new AuspicHour(541, 630, "Char", 1));
        wednesdayIntervals.add(new AuspicHour(631, 720, "Labh", 1));

        auspicData.put("Wednesday", wednesdayIntervals);

        List<AuspicHour> thursdayIntervals = new ArrayList<>();
        thursdayIntervals.add(new AuspicHour(1, 90, "Subh", 3));
        thursdayIntervals.add(new AuspicHour(91, 180, "Rog", 0));
        thursdayIntervals.add(new AuspicHour(181, 270, "Udveg", 0));
        thursdayIntervals.add(new AuspicHour(271, 360, "Char", 1));
        thursdayIntervals.add(new AuspicHour(361, 450, "Labh", 1));
        thursdayIntervals.add(new AuspicHour(451, 540, "Amrit", 2));
        thursdayIntervals.add(new AuspicHour(541, 630, "Kaal", 0));
        thursdayIntervals.add(new AuspicHour(631, 720, "Shubh", 3));

        auspicData.put("Thursday", thursdayIntervals);

        List<AuspicHour> fridayIntervals = new ArrayList<>();
        fridayIntervals.add(new AuspicHour(1, 90, "Char", 1));
        fridayIntervals.add(new AuspicHour(91, 180, "Labh", 1));
        fridayIntervals.add(new AuspicHour(181, 270, "Amrit", 2));
        fridayIntervals.add(new AuspicHour(271, 360, "Kaal", 0));
        fridayIntervals.add(new AuspicHour(361, 450, "Shubh", 3));
        fridayIntervals.add(new AuspicHour(451, 540, "Rog", 0));
        fridayIntervals.add(new AuspicHour(541, 630, "Udveg", 0));
        fridayIntervals.add(new AuspicHour(631, 720, "Char", 1));

        auspicData.put("Friday", fridayIntervals);

        List<AuspicHour> saturdayIntervals = new ArrayList<>();
        saturdayIntervals.add(new AuspicHour(1, 90, "Kaal", 0));
        saturdayIntervals.add(new AuspicHour(91, 180, "Shubh", 3));
        saturdayIntervals.add(new AuspicHour(181, 270, "Rog", 0));
        saturdayIntervals.add(new AuspicHour(271, 360, "Udveg", 0));
        saturdayIntervals.add(new AuspicHour(361, 450, "Char", 1));
        saturdayIntervals.add(new AuspicHour(451, 540, "Labh", 1));
        saturdayIntervals.add(new AuspicHour(541, 630, "Amrit", 2));
        saturdayIntervals.add(new AuspicHour(631, 720, "Kaal", 0));

        auspicData.put("Saturday", saturdayIntervals);
    }


    public AuspicHour getCurrentInterval(String day, int minutesSinceSunrise) {
        List<AuspicHour> intervals = this.auspicData.get(day);
        if (intervals != null) {
            for (AuspicHour interval : intervals) {
                if (minutesSinceSunrise >= interval.startTime && minutesSinceSunrise <= interval.endTime) {
                    return interval; // Found the current interval
                }
            }
        }
        return null; // No matching interval
    }
}
