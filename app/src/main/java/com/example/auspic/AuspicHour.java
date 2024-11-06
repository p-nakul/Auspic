package com.example.auspic;

// Handle the storage of Hour name and its weight
class AuspicHour {
    int startTime; // in minutes after sunrise
    int endTime;   // in minutes after sunrise
    int value;
    String weight;

    AuspicHour(int startTime, int endTime, String weight, int value) {
        this.startTime = startTime;
        this.endTime = endTime;
        this.value = value;
        this.weight = weight;
    }
}


