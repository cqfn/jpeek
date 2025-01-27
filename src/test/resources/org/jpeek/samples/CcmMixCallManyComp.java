public class CcmMixCallManyComp {

    String name1;

    String name3;

    String name5;

    public void one() {
        name1 = "1";
    }

    public void two() {
        name1 = "2";
    }

    public void three() {
        name3 = "3";
    }

    public void four() {
        this.three();
    }

    public void five() {
        name5 = "5";
    }

}
