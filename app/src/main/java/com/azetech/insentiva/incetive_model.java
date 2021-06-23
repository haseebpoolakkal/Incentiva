package com.azetech.insentiva;

public class incetive_model {
    String name, bill_number, incentive, inct_type, date, month;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBill_number() {
        return bill_number;
    }

    public void setBill_number(String bill_number) {
        this.bill_number = bill_number;
    }

    public String getIncentive() {
        return incentive;
    }

    public void setIncentive(String incentive) {
        this.incentive = incentive;
    }

    public String getInct_type() {
        return inct_type;
    }

    public void setInct_type(String inct_type) {
        this.inct_type = inct_type;
    }

    public String getDate() {
        return date;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public incetive_model(String name, String bill_number, String incentive, String inct_type, String date, String month) {
        this.name = name;
        this.bill_number = bill_number;
        this.incentive = incentive;
        this.inct_type = inct_type;
        this.date = date;
        this.month = month;
    }

    public incetive_model() {
    }
}
