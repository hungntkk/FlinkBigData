package Entities;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.Serializable;

public class User implements Serializable {
    @JsonProperty("id")
    int id;

    @JsonProperty("username")
    String userName;

    @JsonProperty("email")
    String emailAddress;

    @JsonProperty("phone")
    String phoneNumber;

    @JsonProperty("coin")
    Double coin;

    @JsonProperty("isdeleted")
    int isDeleted;

    @JsonProperty("isonline")
    int isOnline;

    public void setCoin(Double coin) {
        this.coin = coin;
    }

    public void addjustCoin(Double addjustValue) {
        this.coin += addjustValue;
    }

    public Double getCoin() {
        return coin;
    }

    public User(int id, String userName, String emailAddress, String phoneNumber, int isDeleted, int isOnline, Double coin) {
        this.id = id;
        this.userName = userName;
        this.emailAddress = emailAddress;
        this.coin = coin;
        this.phoneNumber = phoneNumber;
        this.isDeleted = isDeleted;
        this.isOnline = isOnline;
    }

    public User(ObjectNode node) {
        this.id = node.has("id") ? Integer.parseInt(node.get("id").asText()) : null;
        this.userName = node.has("username") ? node.get("username").asText() : null;
        this.emailAddress = node.has("email") ? node.get("email").asText() : null;
        this.phoneNumber = node.has("phone") ? node.get("phone").asText() : null;
        this.coin = node.has("coin") ? Double.parseDouble(node.get("coin").asText()) : null;
        this.isDeleted = node.has("isdeleted") ? Integer.parseInt(node.get("isdeleted").asText()) : null;
        this.isOnline = node.has("isonline") ? Integer.parseInt(node.get("isonline").asText()) : null;
    }

    public ObjectNode getUserNode (){
        ObjectNode node = JsonNodeFactory.instance.objectNode();
        node.put("id", this.id);
        node.put("username", this.userName);
        node.put("email", this.emailAddress);
        node.put("phone", this.phoneNumber);
        node.put("isdeleted", this.isDeleted);
        node.put("isonline", this.isOnline);
        return node;
    }

    public int getId() {
        return id;
    }

    public String getUserName() {
        return userName;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public int getIsDeleted() {
        return isDeleted;
    }

    public int getIsOnline() {
        return isOnline;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void setIsDeleted(int isDeleted) {
        this.isDeleted = isDeleted;
    }

    public void setIsOnline(int isOnline) {
        this.isOnline = isOnline;
    }
}
