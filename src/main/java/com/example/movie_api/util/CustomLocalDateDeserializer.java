package com.example.movie_api.util;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.example.movie_api.exception.InvalidDateFormatException;

import java.io.IOException;
import java.time.format.DateTimeParseException;
import java.time.LocalDate;

public class CustomLocalDateDeserializer extends JsonDeserializer<LocalDate> {

    @Override
    public LocalDate deserialize(JsonParser p, DeserializationContext ctxt)
            throws IOException, JsonProcessingException {
        String dateStr = p.getText();
        try {
            return LocalDate.parse(dateStr);
        } catch (DateTimeParseException ex) {
            String message = ex.getMessage();
            if (message.contains("could not be parsed")) {
                if (message.contains("Invalid value for Month")) {
                    throw new InvalidDateFormatException("Invalid month in birthDate: " + dateStr + " Valid month dates are (01-12)");
                } else if (message.contains("Invalid value for DayOfMonth") || message.contains("Invalid date")) {
                    throw new InvalidDateFormatException("Invalid day in birthDate: " + dateStr + " Valid day dates are (01-28/31)");
                } else {
                    throw new InvalidDateFormatException("Invalid birthDate format: " + dateStr + " Correct format is yyyy-MM-dd");
                }
            } else {
                throw new InvalidDateFormatException("Invalid birthDate format: " + dateStr + " Correct format is yyyy-MM-dd");
            }
        }
    }
} 