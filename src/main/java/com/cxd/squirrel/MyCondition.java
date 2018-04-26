package com.cxd.squirrel;

import com.cxd.exception.BizException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.squirrelframework.foundation.fsm.Condition;

/**
 * desc
 *
 * @author childe
 * @date 2018/4/25 15:24
 **/
public class MyCondition implements Condition<MyContext> {

    private static final Logger LOGGER = LoggerFactory.getLogger(MyCondition.class);

    @Override
    public boolean isSatisfied(MyContext context) {
        LOGGER.warn(context.toString());

        if (!"yes".equals(context.getNo())) {
            throw new BizException("context param error");
        }

        return true;
    }

    @Override
    public String name() {
        return this.getClass().getName();
    }
}
