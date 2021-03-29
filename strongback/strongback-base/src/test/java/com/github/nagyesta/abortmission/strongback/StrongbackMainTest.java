package com.github.nagyesta.abortmission.strongback;

import org.junit.jupiter.api.Test;

class StrongbackMainTest {

    @Test
    void testErectorMainShouldResolvesBeansWhenCalled() {
        //given

        //when
        StrongbackErectorMain.main(new String[0]);

        //then no exception
    }


    @Test
    void testRetractorMainShouldResolvesBeansWhenCalled() {
        //given

        //when
        StrongbackRetractorMain.main(new String[0]);

        //then no exception
    }
}
