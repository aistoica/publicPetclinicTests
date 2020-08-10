package com.endava.petclinic.models;

import java.util.Objects;

public class Visit {
    private Integer id;
    private String date;
    private String description;
    private Pet pet;

    public Visit(){};

    public Visit( String date, String description, Pet pet ){
        this.date = date;
        this.description = description;
        this.pet = pet;
    }

    public Integer getId(){
        return id;
    }

    public void setId( Integer id ){
        this.id = id;
    }

    public String getDate(){
        return date;
    }

    public void setDate( String date ){
        this.date = date;
    }

    public String getDescription(){
        return description;
    }

    public void setDescription( String description ){
        this.description = description;
    }

    public Pet getPet(){
        return pet;
    }

    public void setPet( Pet pet ){
        this.pet = pet;
    }

    @Override
    public boolean equals( Object o ){
        if ( this == o ) return true;
        if ( !( o instanceof Visit ) ) return false;
        Visit visit = ( Visit ) o;
        return Objects.equals( date, visit.date ) &&
                Objects.equals( description, visit.description ) &&
                Objects.equals( pet, visit.pet );
    }

    @Override
    public int hashCode(){
        return Objects.hash( date, description, pet );
    }

    @Override
    public String toString(){
        return "Visit{" +
                "id=" + id +
                ", date='" + date + '\'' +
                ", description='" + description + '\'' +
                ", pet=" + pet +
                '}';
    }
}
