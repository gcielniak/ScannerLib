package com.gcielniak.scannerlib;

/**
 * A single reading including id, timestamp (microseconds) and signal strength
 */
public class Reading {
    public DeviceType device_type;
    public String name;
    public long timestamp;//microseconds
    public UUID uuid;
    public double value;
    public double[] rotation;
    public double[] translation;
    String mac_address;
    long mac_address_int;

    public enum DeviceType {
        BT_BEACON,
        WIFI_AP,
        NO_DEVICE
    }

    public Reading() {
        translation = new double[3];
        rotation = new double[4];
        uuid = new UUID(new byte[20]);
    }

    @Override
    public boolean equals(Object o) {
        return o instanceof Reading && this.mac_address_int == ((Reading) o).mac_address_int;
    }

    public String getMacAddress() {
        return mac_address;
    }

    public void setMacAddress(String mac_address) {
        this.mac_address = mac_address;
        mac_address.replace(":","");
        mac_address_int = Long.parseLong(mac_address, 16);
    }

    @Override
    public String toString() {

        String _name;
        if (name != null)
            _name = name;
        else
            _name = "";

        if (device_type == DeviceType.BT_BEACON) {
            return "BT: t=" + timestamp + " n=\"" + _name + "\" a=" + mac_address + " v=" + value +
                    " p=" + translation[0] + " " + translation[1] + " " + translation[2] +
                    " r=" + rotation[0] + " " + rotation[1] + " " + rotation[2] + " " + rotation[3] + " u=" + uuid;

        }
        else if (device_type == DeviceType.WIFI_AP) {
            return "WF: t=" + timestamp + " n=\"" + _name + "\" a=" + mac_address + " v=" + value +
                    " p=" + translation[0] + " " + translation[1] + " " + translation[2] +
                    " r=" + rotation[0] + " " + rotation[1] + " " + rotation[2] + " " + rotation[3];

        }
        else {
            return "ND: t=" + timestamp + " p=" + translation[0] + " " + translation[1] + " " + translation[2] +
                    " r=" + rotation[0] + " " + rotation[1] + " " + rotation[2] + " " + rotation[3];
        }
    }
}