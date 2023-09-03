package org.iiidev.pinda.utils;

import com.google.common.collect.Lists;
import org.iiidev.pinda.constant.MatchType;
import org.junit.jupiter.api.Test;

import java.util.List;

/**
 * CollectionHelperTest
 *
 * @Author IIIDelay
 * @Date 2023/9/1 22:53
 **/
public class CollectionHelperTest {

    @Test
    public void matchAny() {
        List<Integer> list = Lists.newArrayList(111,222,336,1,23);
        boolean c = CollectionHelper.matchAny(list, 3, MatchType.SUBFIX);
        System.out.println("c = " + c);
    }
}