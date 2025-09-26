package fr.poc.hackaton.business.pojo;

import lombok.Data;

@Data
public class Balance {

    private int count;
    private double sum;
    private String nameCategory;
    private double average;
    private String date;

    private double averageSegment;
    private double countSegment;
    private double sumSegment;
}
