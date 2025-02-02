package org.example.first;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

@JsonPropertyOrder({"id", "name", "code", "sign"}) // Указываем порядок
public class Currency {
    private String id;
    private String name;
    private String code;
    private String sign;

    public Currency(String id, String name, String code, String sign) {
        this.id = id;
        this.name = name;
        this.code = code;
        this.sign = sign;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getSign() {
        return sign;
    }

    public void setSign(String sign) {
        this.sign = sign;
    }
}
