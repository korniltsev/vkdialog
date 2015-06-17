package ru.kurganec.vk.messenger.model.actions.events;

/**
 * User: anatoly
 * Date: 17.08.12
 * Time: 1:16
 */
public class SearchUserEvent extends BaseEvent {
    private String query;
    public SearchUserEvent( String query) {
        super(null);
        this.query = query;
    }

    public String getQuery() {
        return query;
    }
}
