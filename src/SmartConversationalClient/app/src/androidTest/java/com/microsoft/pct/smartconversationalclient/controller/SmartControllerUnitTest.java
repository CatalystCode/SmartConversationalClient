package com.microsoft.pct.smartconversationalclient.controller;

import android.content.Context;
import android.test.InstrumentationTestCase;

import com.microsoft.pct.smartconversationalclient.luis.LUISQueryResult;

/**
 * Created by abornst on 6/28/2016.
 */
public class SmartControllerUnitTest extends InstrumentationTestCase {

    private Context _context;
    private SmartConversationalController _controller;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        _context = getInstrumentation().getTargetContext();
        _controller = new SmartConversationalController(_context);
    }

    @Override
    protected void tearDown() throws Exception {
        super.tearDown();
        _controller.clearCache();
    }

    public void testQuery() throws Throwable {
        LUISQueryResult myResult = (LUISQueryResult) _controller.query("go to the kitchen");

        assertTrue(myResult.getIntents()[0].getIntent().equals("Go to"));
        assertNotNull(myResult.getEntities());

        myResult = (LUISQueryResult) _controller.query("go to the den");
        assertTrue(myResult.getIntents()[0].getIntent().equals("Go to"));
        assertNotNull(myResult.getEntities());
    }

    public void testClear() throws Exception {
        _controller.clearCache();
    }

}