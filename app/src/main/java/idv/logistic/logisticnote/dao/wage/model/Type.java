package idv.logistic.logisticnote.dao.wage.model;

public enum Type {
    OnDuty("OnDuty",1),DayOff("DayOff",0);

    private String name;
    private int code;

    private Type(String name, int code){
        this.name=name;
        this.code=code;
    }



}
