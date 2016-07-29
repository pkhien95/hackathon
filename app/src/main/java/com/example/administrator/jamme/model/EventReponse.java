package com.example.administrator.jamme.model;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class EventReponse {

    @SerializedName("events")
    private List<Event> events;

    public EventReponse(List<Event> events) {
        this.events = events;
    }

    public List<Event> getEvents() {
        return events;
    }

    public void setEvents(List<Event> events) {
        this.events = events;
    }

}
