package com.videonasocialmedia.videona.api;

import android.util.Log;

import com.videonasocialmedia.videona.api.Validator;

import junit.framework.TestCase;

/**
 * Created by jca on 23/1/15.
 */
public class TestValidator extends TestCase {
    public void testValidateUserName() {
        String inputs[] = {"AASSSDWF", "asmSsAde", "asdM1k3234", "asd4mea.w21.", "asdn",
                "", "a", "bcd", " ", "12345", "asdn kasdmeka", "asdk{m", "asdme}", "asdm?e",
                "asdm;jasj", "asdmeñk", "sadjnek", "kamsdkme_ksne", "asjnem@nasme.com", "asdnken@sakn, ",
                "asmSsAdé"
        };

        boolean expected[] = {true, true, true, true, true,
                false, false, false, false, true, false, false, false, false,
                false, true, true, true, false, false,
                true
        };

        for (int i = 0; i < inputs.length; i++) {
            Log.d("TEST", inputs[i]);
            Log.d("TEST", "" + expected[i]);
            Log.d("TEST", "" + i);

            assertEquals(expected[i], Validator.validateUserName(inputs[i]));
        }
    }

    public void testValidatePassword() {
        String inputs[] = {"AASSSDWF", "asmSsAde", "asdM1k3234", "asd4mea.w21.", "asdn",
                "", "a", "bcd", " ", "12345", "asdn kasdmeka", "asdk{m", "asdme}", "asdm?e",
                "asdm;jasj", "asdmeñk", "sadjnek", "kamsdkme_ksne"
        };
        //TODO llenar expected
        boolean expected[] = {
        };

        for (int i = 0; i < inputs.length; i++) {
            //assertEquals(expected[i], Validator.validatePassword(inputs[i]));
        }
    }

    public void testValidateEmail() {
        String inputs[] = {"1234@gmail.com", "kme121.34f4@abcd.com", "kme121@abcd.efg.es",
                "a_mel@llea.me", "AsdneAsU@mmma.fr", "smeeqeop@MAYUSCULAS.COM",
                "?9jasn@mail.com", "kasm}e@mail.com", "abcd@", "abcd@.com", "abcd@hie.", "@",
                "@emsd.com", "mkasme@asje@asnde.com", "mkasd-asdme@mail.com"};

        boolean expected[] = {true, true, true, true, true, true,
                false, false, false, false, false, false, false, false, true};
    }


}
