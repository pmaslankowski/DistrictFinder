package hospitals;

import java.util.StringJoiner;

public class Hospital {
    public Hospital(String id, String name, String address, String phone) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phone = phone;
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

    @Override
    public String toString() {
        StringJoiner joiner = new StringJoiner(",\n", "{", "}");
        joiner.add("id : " + id);
        joiner.add("name : " + name);
        joiner.add("address : " + address);
        joiner.add("phone : " + phone);
        return joiner.toString();
    }

    private String id;
    private String name;
    private String address;
    private String phone;
}
