package ips.club.model;

public class IncidentType {

    private Integer code;
    private String name;

    public IncidentType(Integer code, String name){
        this.code = code; this.name = name;
    }

    public Integer getCode(){ return code; }
    public void setCode(Integer code){ this.code = code; }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    @Override public String toString(){ return name; }
}
