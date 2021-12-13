package com.vg.raiddataparser.model;

import com.vg.raiddataparser.model.champion.Champion;


public class Skill {

    private int id;
    private Champion champion;
    private int revision;
    private String name;
    private String description;
    private int cooldown;
    private String multiplierFormula;
    private int visibility;
    private int group;

    public Skill() {}

    public Skill(Builder builder) {
        this.id = builder.id;
        this.revision = builder.revision;
        this.name = builder.name;
        this.description = builder.description;
        this.cooldown = builder.cooldown;
        this.multiplierFormula = builder.multiplierFormula;
        this.visibility = builder.visibility;
        this.group = builder.group;
        this.champion = builder.champion;
    }

    public int getId() { return id; }

    public int getRevision() { return revision; }

    public String getName() { return name; }

    public String getDescription() { return description; }

    public int getCooldown() { return cooldown; }

    public String getMultiplierFormula() { return multiplierFormula; }

    public int getVisibility() { return visibility; }

    public int getGroup() { return group; }

    public Champion getChampion() { return champion; }

    @Override
    public String toString() {
        return "Skill{" +
                "id=" + id +
                ", revision=" + revision +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", cooldown=" + cooldown +
                ", multiplierFormula='" + multiplierFormula + '\'' +
                ", visibility=" + visibility +
                ", group=" + group +
                '}';
    }

    public static class Builder {

        private int id;
        private int revision;
        private String name;
        private String description;
        private int cooldown;
        private String multiplierFormula;
        private int visibility;
        private int group;
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

        public Builder setVisibility(int visibility) {
            this.visibility = visibility;
            return this;
        }

        public Builder setGroup(int group) {
            this.group = group;
            return this;
        }

        public Builder setChampion(Champion champion) {
            this.champion = champion;
            return this;
        }

        public Skill build() {
            return new Skill(this);
        }
    }

}