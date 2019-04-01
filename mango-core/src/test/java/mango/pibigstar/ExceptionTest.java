package mango.pibigstar;

/**
 * @author pibigstar
 * @create 2019-03-29 13:58
 * @desc
 **/
public class ExceptionTest {
    public static void main(String[] args){

            for(int i=0;i<3;i++){
                try {
                    test(i);
                } catch (Exception e){
                    System.out.println("+++++++");
                }
            }
        try {
            test(0);
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("=============");

    }
    public static void test(int i) throws Exception{
        try {
            int m = 5 / i;
            System.out.println(m);
        }catch (Exception e){
            throw new Exception(e);
        }
    }
}
