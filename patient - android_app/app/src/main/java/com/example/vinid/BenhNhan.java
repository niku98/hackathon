package com.example.vinid;

import org.json.JSONException;
import org.json.JSONObject;

public class BenhNhan{
    private String id;
    private String name;
    private String address;
    private String phone;

    public BenhNhan() {
        this.id = "";
        this.name = "";
        this.address = "";
        this.phone = "";
    }

    public BenhNhan(String id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
    }

    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getAddress() {
        return address;
    }

    public String getPhone() {
        return phone;
    }

    public void setId(String id) {
        this.id = id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

//    public String toJSON(){
//        JSONObject jsonObject= new JSONObject();
//        try {
//            jsonObject.put("id", getId());
//            jsonObject.put("name", getName());
//            jsonObject.put("address", getDiaChi());
//            jsonObject.put("phone", getSdt());
//            return jsonObject.toString();
//        } catch (JSONException e) {
//            return "";
//        }
//
//    }
}