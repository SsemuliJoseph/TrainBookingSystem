/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package ui;

import javax.swing.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class DateLabelFormatter extends JFormattedTextField.AbstractFormatter {
    private final SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd");

    @Override
    public Object stringToValue(String text) throws ParseException {
        if (text == null || text.isEmpty()) return null;
        java.util.Date parsed = dateFormatter.parse(text);
        Calendar cal = Calendar.getInstance();
        cal.setTime(parsed);
        return cal;
    }

    @Override
    public String valueToString(Object value) {
        if (value == null) return "";
        Calendar cal;
        if (value instanceof Calendar) {
            cal = (Calendar) value;
        } else if (value instanceof Date) {
            cal = Calendar.getInstance();
            cal.setTime((Date) value);
        } else {
            return "";
        }
        return dateFormatter.format(cal.getTime());
    }
}
