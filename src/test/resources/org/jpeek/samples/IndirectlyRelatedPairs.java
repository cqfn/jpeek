public final class IndirectlyRelatedPairs {
    public IndirectlyRelatedPairs(){
        methodOne(0);
    }
    public void methodOne(final int x) {
        methodTwo(1);
    }
    public void methodTwo(final int x) {
        methodThree(2);
    }
    public void methodThree(final int x) {
    }
}
