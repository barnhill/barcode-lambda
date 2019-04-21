package com.pnuema.barcode.lambda.models;

import com.pnuema.barcode.lambda.json.Json;

@SuppressWarnings("unused") //auto populated
public class Response implements JsonInterface {
    private String image;
    private String encodedValue;
    private String symbology;
    private String data;
    private String image_format;
    private Integer height;
    private Integer width;

    private String error;

    public Response() {}

    public Response(String data, String symbology, String encodedValue, String image, String imageFormat, Integer width, Integer height) {
        this.image = image;
        this.encodedValue = encodedValue;
        this.data = data;
        this.symbology = symbology;
        this.image_format = imageFormat;
        this.width = width;
        this.height = height;
    }

    public Response (String error) {
        this.error = error;
    }

    @Override
    public String toJson() {
        return Json.toJson(Response.class,this);
    }

    public String getImage() {
        return image;
    }

    public String getEncodedValue() {
        return encodedValue;
    }

    public String getError() {
        return error;
    }

    public String getSymbology() {
        return symbology;
    }

    public String getData() {
        return data;
    }

    public String getImageFormat() {
        return image_format;
    }

    public Integer getHeight() {
        return height;
    }

    public Integer getWidth() {
        return width;
    }
}
