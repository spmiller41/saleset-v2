package com.saleset.integration.zoho.enums;

public enum ZohoModuleApiName {

    DEALS("Deals"),
    LEADS("Leads");

    private final String apiName;

    ZohoModuleApiName(String apiName) {this.apiName = apiName; }

    @Override
    public String toString() { return apiName; }

}
