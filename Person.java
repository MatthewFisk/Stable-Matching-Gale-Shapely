public class Person {
    String name;
    int[] crushIndexes = {0,0,0,0};
    int partner = -1;

    public Person(String n){
        name = n;
    }

    public void setCrush(int[] intArr){
        crushIndexes = intArr;
    }

    public void setPartner(int i){
        partner = i;
    }

    public boolean isBetterCrush(int i){
        if (findIndexOf(i) < findIndexOf(partner)) {
            return true;
        } else {
            return false;
        }
    }

    private int findIndexOf (int i){
        for (int j = 0; j < 4; j++) {
            if (crushIndexes[j] == i) {
                return j;
            }
        }
        return -1;
    }

    public void printName(){
        System.out.print(name);
    }
}
