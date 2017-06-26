package com.acedata.sqlsearch.cardPortrait;

/**
 * Created by Administrator on 2017/6/23.
 */
public class PortraitPO {
    private String name;
    private String accountName;
    private String requestTime;
    private String costTime;

    public String getCostTime() {
        return costTime;
    }

    public void setCostTime(String costTime) {
        this.costTime = costTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public String getRequestTime() {
        return requestTime;
    }

    public void setRequestTime(String requestTime) {
        this.requestTime = requestTime;
    }
}
