package com.pnuema.barcode.lambda.models;

@SuppressWarnings("unused") //auto poplated
public class Request {
    private String data;
    private String symbology;
    private String width;
    private String height;
    private String format = "json";

    public Request() {}

    public Request(String data, String symbology, String width, String height) {
        this.data = data;
        this.symbology = symbology;
        this.width = width;
        this.height = height;
    }

    public String getData() {
        return data;
    }

    public String getSymbology() {
        return symbology;
    }

    public String getWidth() {
        return width;
    }

    public String getHeight() {
        return height;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setSymbology(String symbology) {
        this.symbology = symbology;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }

    @Override
    public String toString() {
        return "Request{" +
                "data='" + data + '\'' +
                ", symbology='" + symbology + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}
