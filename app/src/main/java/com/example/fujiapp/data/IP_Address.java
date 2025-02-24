package com.example.fujiapp.data;

import java.io.Serializable;

public class IP_Address implements Serializable {
    public static String FILENAME = "IPAsDRESS_FILE";

    private String ip_Adsress;

    public String getIp_Adsress() {
        return ip_Adsress;
    }

    public void setIp_Adsress(String ip_Adsress) {
        this.ip_Adsress = ip_Adsress;
    }
}
