package org.jtalks.jcommune.model.entity;

/**
 *
 * @author Temdegon
 */
public abstract class Persistent {
    private long id;
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }
}