package com.videonasocialmedia.videona.common.utils;

/*
 * Copyright (C) 2015 Videona Socialmedia SL
 * http://www.videona.com
 * info@videona.com
 * All rights reserved
 *
 * Authors:
 * Juan Javier Cabanas
 */

import com.videonasocialmedia.videona.utils.StringUtils;

import junit.framework.TestCase;

public class TestValidator extends TestCase {
    public void testValidateUserName() {
        String inputs[] = {"AASSSDWF", "asmSsAde", "asdM1k3234", "asd4mea.w21.", "asdn",
                "", "a", "bcd", " ", "12345", "asdn kasdmeka", "asdk{m", "asdme}", "asdm?e",
                "asdm;jasj", "asdmeñk", "sadjnek", "kamsdkme_ksne", "asjnem@nasme.com", "asdnken@sakn, ",
                "asmSsAdé", "asdkmneoamsldmasdñlmsaernlasdmpwefrpokasdf", "nasj-asdmk", "poeima_smoeo",
                "nas#asak", "a.bc.", "1msn.elfurpcm567.3", "anrlenñjenslepo", "<amdeje>"
        };

        boolean expected[] = {true, true, true, true, true,
                false, false, false, false, true, false, false, false, false,
                false, true, true, true, false, false,
                true, false, false, true,
                false, false, false, true, false,
        };

        for (int i = 0; i < inputs.length; i++) {
            assertEquals(inputs[i] + ": " + expected[i], expected[i], StringUtils.validateUserName(inputs[i]));
        }
    }

    public void testValidatePassword() {
//        String inputs[] = {"AASSSDWF", "asmSsAde", "asdM1k3234", "asd4mea.w21.", "asdn",
//                "", "a", "bcd", " ", "12345", "asdn kasdmeka", "asdk{m", "asdme}", "asdm?e",
//                "asdm;jasj", "asdmeñk", "sadjnek", "kamsdkme_ksne"
//        };

        String inputs[] = {"", " ", "vprueba", "123", "12345678", "Ab123456+", "Ab123456$", "Ab123456%",
                "Ab12#3456", "@Ab123456", "%Ab123456%", "<Ab123456$>", "%%%%%%%%", "$%#@12Ll",
                "123456789abcde$%#@", "12345678abcd$%#@", "12345678AbC$%#@", "12345ñabcde$%#@",
                "123_8AbC$%#@", "123 8AbC$%#@", " 1238AbC$%#@", "1238A.bC$%#@", "<Ab123456$>",
                "1238A.bC$%#?", "123,A.bC$%#?", "!1238A.b!$%#?", "123¿A.bC$%#?", "¡123!.bC$%#",
                "¡123!.abÇC$%#?¿p"};


        boolean expected[] = {

                false, false, false, false, false, false, true, true, true, true,
                true, false, false, true, false, false, true, false, false, false, false, true, false,
                true, true, true, true, true, true
        };

        for (int i = 0; i < inputs.length; i++) {
            //assertEquals(expected[i], Validator.validatePassword(inputs[i]));
        }
    }

    public void testValidateEmail() throws Exception {
        String inputs[] = {"1234@gmail.com", "kme121.34f4@abcd.com", "kme121@abcd.efg.es",
                "a_mel@llea.me", "AsdneAsU@mmma.fr", "smeeqeop@MAYUSCULAS.COM",
                "?9jasn@mail.com", "kasm}e@mail.com", "abcd@", "abcd@.com", "abcd@hie.", "@",
                "@emsd.com", "mkasme@asje@asnde.com", "mkasd-asdme@mail.com", "12312342@9439282.es",
                "mdke@mcdce.a", "msmepp@mmasd.kasdlemn", "kmasm@decse.abcd", "amk@192.178.0.1",
                "ms<eiak>@moeur.es"
        };

        boolean expected[] = {true, true, true,
                true, true, true,
                false, false, false, false, false, false,
                false, false, true, true,
                true, true, true, true,
                false,
        };

        for (int i = 0; i < inputs.length; i++) {
            assertEquals(inputs[i] + ": " + expected[i], expected[i], StringUtils.validateEmail(inputs[i]));
        }
    }


}

