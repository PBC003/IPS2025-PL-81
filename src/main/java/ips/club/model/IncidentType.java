package ips.club.model;

public class IncidentType {

    public enum FieldType {
        LOCATION("location"),
        DESCRIPTION("description");

        private final String dbValue;
        FieldType(String dbValue){ this.dbValue = dbValue; }
        public String dbValue(){ return dbValue; }
        public static FieldType fromDb(String value) {
            for (FieldType r : values()) {
                if (r.dbValue.equalsIgnoreCase(value)) {
                    return r;
            }
        }
        throw new IllegalArgumentException("Unknown FieldType: " + value);
    }
    }

    private Integer code;
    private String name;
    private FieldType FieldType;

    public IncidentType() {}
    public IncidentType(Integer code, String name, FieldType FieldType){
        this.code = code; this.name = name; this.FieldType = FieldType;
    }

    public Integer getCode(){ return code; }
    public void setCode(Integer code){ this.code = code; }

    public String getName(){ return name; }
    public void setName(String name){ this.name = name; }

    public FieldType getFieldType(){ return FieldType; }
    public void setFieldType(FieldType FieldType){ this.FieldType = FieldType; }

    @Override public String toString(){ return name; }
}
