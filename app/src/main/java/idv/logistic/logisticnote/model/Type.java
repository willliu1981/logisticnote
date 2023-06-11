package idv.logistic.logisticnote.model;

public enum Type {

    LONG("過久", "O"), NORMAL("一年", "M"), NEW("新品", "N"), NONE("無資料", "-");

    private String name;
    private String code;

    private Type(String name, String code) {
        this.name = name;
        this.code = code;
    }

    public static Type getType(String code) {
        /*
        Optional<Type> find = Arrays.stream(Type.values()).filter(x -> x.code.equals(code)).findAny();
        if (find.isPresent()) {
            return find.get();
        } else {
            return Type.NONE;
        }

        // */

        for (Type value : Type.values()) {
            if (value.code.equalsIgnoreCase(code)) {
                return value;


            }
        }
        return Type.NONE;
    }


    public String getName() {
        return name;
    }

    public String getCode() {
        return code;
    }
}
