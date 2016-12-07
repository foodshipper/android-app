package de.foodshippers.foodship.api.model;

import com.google.gson.annotations.SerializedName;

import java.io.Serializable;

/**
 * Created by hannes on 06.12.16.
 */
public class GroupInformations implements Serializable {
    @SerializedName("invited")
    int invitetd;
    @SerializedName("accepted")
    int accepted;
    @SerializedName("day")
    String day;

    public int getAccepted() {
        return accepted;
    }

    public int getInvitetd() {
        return invitetd;
    }

    public String getString() {
        return day;
    }
}
