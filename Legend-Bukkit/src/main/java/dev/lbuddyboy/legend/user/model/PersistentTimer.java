package dev.lbuddyboy.legend.user.model;

import lombok.Data;
import org.bson.Document;

/**
 * @author LBuddyBoy (dev.lbuddyboy)
 * @project LifeSteal
 * @file dev.lbuddyboy.legend.user
 * @since 1/2/2024
 */

@Data
public class PersistentTimer {

    private String id;
    private long duration, expiresAt, pausedAt;

    public PersistentTimer(String id, long duration) {
        this.id = id;
        this.duration = duration;
        this.expiresAt = System.currentTimeMillis();
        this.pausedAt = 0;
    }

    public PersistentTimer(Document document) {
        this.id = document.getString("id");
        this.duration = document.getLong("duration");
        this.expiresAt = document.getLong("expiresAt");
        this.pausedAt = document.getLong("pausedAt");
    }

    public Document toDocument() {
        return new Document()
                .append("id", this.id)
                .append("duration", this.duration)
                .append("expiresAt", this.expiresAt)
                .append("pausedAt", this.pausedAt)
                ;
    }

    public void unpause() {
        this.expiresAt = System.currentTimeMillis() + this.pausedAt;
        this.pausedAt = 0;
    }

    public void pause() {
        this.pausedAt = this.expiresAt - System.currentTimeMillis();
    }

    public void reset() {
        this.expiresAt = System.currentTimeMillis();
    }

    public boolean isExpired() {
        return getRemaining() <= 0;
    }

    public boolean isPaused() {
        return this.pausedAt != 0L;
    }

    public long getRemaining() {
        if (isPaused()) return this.duration + this.pausedAt;

        return (this.duration + this.expiresAt) - System.currentTimeMillis();
    }

}
