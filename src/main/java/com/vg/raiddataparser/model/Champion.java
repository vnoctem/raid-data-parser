package com.vg.raiddataparser.model;

import javax.persistence.Entity;
import javax.persistence.Id;

@Entity
public class Champion {

    @Id
    private int id;
    private String name;
    private int affinity;
    private int role;
    private int faction;
    private int rarity;

    public Champion() {
    }

    public Champion(int id, String name, int affinity, int role, int faction, int rarity) {
        this.id = id;
        this.name = name;
        this.affinity = affinity;
        this.role = role;
        this.faction = faction;
        this.rarity = rarity;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) { this.name = name; }

    public int getAffinity() {
        return affinity;
    }

    public void setAffinity(int affinity) {
        this.affinity = affinity;
    }

    public int getRole() {
        return role;
    }

    public void setRole(int role) {
        this.role = role;
    }

    public int getFaction() {
        return faction;
    }

    public void setFaction(int faction) {
        this.faction = faction;
    }

    public int getRarity() {
        return rarity;
    }

    public void setRarity(int rarity) {
        this.rarity = rarity;
    }

    @Override
    public String toString() {
        return "Champion{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", affinity=" + affinity +
                ", role=" + role +
                ", faction=" + faction +
                ", rarity=" + rarity +
                '}';
    }

}
