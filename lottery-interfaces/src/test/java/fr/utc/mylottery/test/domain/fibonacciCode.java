package fr.utc.mylottery.test.domain;

import org.junit.Test;

public class fibonacciCode {
    @Test
    public void test_idx() {
        int hashCode = 0;
        for (int i = 0; i < 16; i++) {
            hashCode = i * 0x61c88647 + 0x61c88647;
            int idx = hashCode & 15;
            System.out.println("斐波那契散列：" + idx + " 普通散列：" + (String.valueOf(i).hashCode() & 15));
        }
    }

}
