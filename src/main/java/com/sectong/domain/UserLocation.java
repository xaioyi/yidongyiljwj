package com.sectong.domain;

import org.springframework.data.annotation.Id;

/**
 * Created by admin on 2016/11/10.
 */
public class UserLocation {

    @Id
    private String id;

    private String telephone;
    private String Locations;

    public UserLocation() {
    }

    public UserLocation( String telephone, String locations) {
        this.telephone = telephone;
        Locations = locations;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTelephone() {
        return telephone;
    }

    public void setTelephone(String telephone) {
        this.telephone = telephone;
    }

    public String getLocations() {
        return Locations;
    }

    public void setLocations(String locations) {
        Locations = locations;
    }
}
