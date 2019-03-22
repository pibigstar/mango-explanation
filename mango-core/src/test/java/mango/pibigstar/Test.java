package mango.pibigstar;

/**
 * @author pibigstar
 * @create 2019-03-21 16:34
 * @desc
 **/
public class Test {

    public static void main(String[] args){
        testThrow();
    }

    public static void testThrow(){
        for (int i = 0; i < 3; i++) {
            if (i==1){
                throw new RuntimeException("i:"+i);
            }
            System.out.println(i);
        }
    }
}
