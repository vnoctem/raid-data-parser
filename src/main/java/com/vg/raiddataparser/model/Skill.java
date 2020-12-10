package com.vg.raiddataparser.model;

import com.vg.raiddataparser.model.champion.Champion;

import javax.persistence.*;

@Entity
public class Skill {

    @Id
    private int id;

    @ManyToOne
    @JoinColumn(name="champion_id")
    private Champion champion;

    private int revision;

    private String name;

    @Column(length = 1000000)
    private String description;

    private int cooldown;

    private String multiplierFormula;

    public Skill() {}

    public Skill(int id, int revision, String name, String description, int cooldown, String multiplierFormula, Champion champion) {
        this.id = id;
        this.revision = revision;
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
        this.multiplierFormula = multiplierFormula;
        this.champion = champion;
    }

    public int getId() { return id; }

    public void setId(int id) { this.id = id; }

    public int getRevision() { return revision; }

    public void setRevision(int revision) { this.revision = revision; }

    public String getName() { return name; }

    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }

    public void setDescription(String description) { this.description = description; }

    public int getCooldown() { return cooldown; }

    public void setCooldown(int cooldown) { this.cooldown = cooldown; }

    public String getMultiplierFormula() { return multiplierFormula; }

    public void setMultiplierFormula(String multiplierFormula) { this.multiplierFormula = multiplierFormula; }

    public Champion getChampion() { return champion; }

    public void setChampion(Champion champion) { this.champion = champion; }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", revision=" + revision +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cooldown=" + cooldown +
                ", multiplierFormula='" + multiplierFormula + '\'' +
                '}';
    }
}
