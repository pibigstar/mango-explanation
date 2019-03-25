package mango.pibigstar;

import mango.util.RequestIdGenerator;

/**
 * @author pibigstar
 * @create 2019-03-25 18:59
 * @desc
 **/
public class GenderatorTest {
    public static void main(String[] args){
        for (int i = 0; i < 10; i++) {
            System.out.println(RequestIdGenerator.getRequestId());
        }
    }
}

