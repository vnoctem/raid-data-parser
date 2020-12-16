package com.vg.raiddataparser.model;

import com.vg.raiddataparser.model.champion.Champion;

import javax.persistence.*;

@Entity
public class Skill {

    @Id
    private int id;
    @ManyToOne
    @JoinColumn(name = "champion_id")
    private Champion champion;
    private int revision;
    private String name;
    @Column(length = 1000000)
    private String description;
    private int cooldown;
    private String multiplierFormula;

    public Skill() {}

    public Skill(int id,
            int revision,
            String name,
            String description,
            int cooldown,
            String multiplierFormula,
            Champion champion) {
        this.id = id;
        this.revision = revision;
        this.name = name;
        this.description = description;
        this.cooldown = cooldown;
        this.multiplierFormula = multiplierFormula;
        this.champion = champion;
    }

    public int getId() { return id; }

    public Champion getChampion() { return champion; }

    public int getRevision() { return revision; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getCooldown() { return cooldown; }

    public String getMultiplierFormula() { return multiplierFormula; }

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

    public static class Builder {

        private int id;
        private int revision;
        private String name;
        private String description;
        private int cooldown;
        private String multiplierFormula;
        private Champion champion;

        public Builder setId(int id) {
            this.id = id;
            return this;
        }

        public Builder setRevision(int revision) {
            this.revision = revision;
            return this;
        }

        public Builder setName(String name) {
            this.name = name;
            return this;
        }

        public Builder setDescription(String description) {
            this.description = description;
            return this;
        }

        public Builder setCooldown(int cooldown) {
            this.cooldown = cooldown;
            return this;
        }

        public Builder setMultiplierFormula(String multiplierFormula) {
            this.multiplierFormula = multiplierFormula;
            return this;
        }

        public Builder setChampion(Champion champion) {
            this.champion = champion;
            return this;
        }

        public Skill build() {
            return new Skill(id, revision, name, description, cooldown, multiplierFormula, champion);
        }
    }
}
