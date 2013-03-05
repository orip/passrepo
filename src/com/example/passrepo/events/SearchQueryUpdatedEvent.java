package com.example.passrepo.events;

public class SearchQueryUpdatedEvent {
    public final String currentQuery;

    public SearchQueryUpdatedEvent(String currentQuery) {
        this.currentQuery = currentQuery;
    }
}
