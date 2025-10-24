/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package services;

import java.util.List;

/**
 * Simple DTO holding report headers and rows.
 */
public class ReportData {
    private final String title;
    private final List<String> headers;
    private final List<List<Object>> rows;

    public ReportData(String title, List<String> headers, List<List<Object>> rows) {
        this.title = title;
        this.headers = headers;
        this.rows = rows;
    }

    public String getTitle() { return title; }
    public List<String> getHeaders() { return headers; }
    public List<List<Object>> getRows() { return rows; }
}
