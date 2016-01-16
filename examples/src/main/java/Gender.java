
public enum Gender {
    MALE("M"), FEMALE("F"), UNDEFINED(null);

    private String str;

    private Gender(String str){
        this.str = str;
    }
}
