//    KriolOS POS
//    Copyright (c) 2019-2023 KriolOS
//
//    This program is free software: you can redistribute it and/or modify
//    it under the terms of the GNU General Public License as published by
//    the Free Software Foundation, either version 3 of the License, or
//    (at your option) any later version.
//
//    This program is distributed in the hope that it will be useful,
//    but WITHOUT ANY WARRANTY; without even the implied warranty of
//    MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
//    GNU General Public License for more details.
//
//    You should have received a copy of the GNU General Public License
//    along with this program.  If not, see <http://www.gnu.org/licenses/>.
package com.openbravo.pos.payment;


import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.NullAndEmptySource;
import org.junit.jupiter.params.provider.ValueSource;

/**
 *
 * @author poolborges
 */
public class CardNetworkIdentifierTest {
    
    @ParameterizedTest(name = "Test for cardNumber {0} should return {1}")
    @CsvSource({
        // VISA - Valid cases
        "4111-1111-1111-1111, VISA",      // 16-digit with dashes
        "4111111111111, VISA",            // 13-digit without dashes
        "4000 0000 0000 0000, VISA",      // 16-digit with spaces
        // MASTERCARD - Valid cases
        "5100-0000-0000-0000, MASTERCARD", // 16-digit with dashes (51-55 prefix)
        "5555555555555555, MASTERCARD",    // 16-digit no formatting
        "2221000000000000, MASTERCARD",    // 16-digit (2221-2720 prefix)
        // AMEX - Valid cases
        "3400-000000-00000, AMEX",       // 15-digit with dashes (34 prefix)
        "370000000000000, AMEX",         // 15-digit without formatting (37 prefix)
        // DISCOVER - Valid cases
        "6011-0000-0000-0000, DISCOVER",   // 16-digit (6011 prefix)
        "6500000000000000, DISCOVER",     // 16-digit (65 prefix)
        // JCB - Valid cases
        "3528-0000-0000-0000, JCB",       // 16-digit (3528-3589 prefix)
        "2131-0000-0000-0000, JCB",       // 15-digit (2131 prefix)
        // DINERS CLUB - Valid cases
        "3010-000000-0000, DINERS",       // 14-digit (300-305 prefix)
        "36000000000000, DINERS",         // 14-digit (36 prefix)
        "38000000000000, DINERS",         // 14-digit (38 prefix)
        // UNIONPAY - Valid cases (new)
        "6212-3456-7890-1234, UNIONPAY",    // 16-digit test
        "6212345678901234567, UNIONPAY",    // 19-digit test
        // Invalid cases
        "1234567890123456, UNKNOWN", // Incorrect prefix
        "41111, UNKNOWN",            // Incorrect length
        "not-a-number, INVALID",     // Non-numeric characters
        ", INVALID",                 // Empty string is parsed as null by default
        "' ', INVALID",              // Blank string must be in quotes
        "1234a56789012345, INVALID", // Contains non-digit
        "411111111111111, UNKNOWN" // 15-digit starts with 4, no match
    })
    @DisplayName("Test various card numbers for correct network recognition")
    void testGetCardNetwork(String cardNumber, String expectedNetwork) {
        assertEquals(expectedNetwork, CardNetworkIdentifier.getCardNetwork(cardNumber));
    }
    
    @ParameterizedTest(name = "Test for cardNumber {0} should return INVALID")
    @NullAndEmptySource // Provides null and ""
    @ValueSource(strings = " ") // Provides " "
    @DisplayName("Test invalid and blank card numbers")
    void testInvalidAndBlankCardNumbers(String cardNumber) {
        assertEquals("INVALID", CardNetworkIdentifier.getCardNetwork(cardNumber));
    }
    
    /**
     * TEST FROM 
     * https://developer.paypal.com/braintree/docs/guides/unionpay/testing/
     * 
     * @param cardNumber
     * @param expectedNetwork 
     */
    @ParameterizedTest(name = "Test UnionPay cardNumber {0} should return {1} . Card Description {2}")
    @CsvSource(textBlock = """
                           6212345678901265,UNIONPAY,Debit card
                           6212345678901232,UNIONPAY,Credit card
                           6212345678900028,UNIONPAY,Card that is not supported within Braintree.
                           6212345678900036,UNIONPAY,Card that is not activated for use online.
                           6212345678900085,UNIONPAY,Card that does not require SMS verification.
                           6212345678900093,UNIONPAY,Card that does not support separate Transaction: Submit For Settlement calls.
                           62123456789002,UNKNOWN,Card with a 14 digit number.
                           621234567890003,UNKNOWN,Card with a 15 digit number.
                           62123456789000003,UNIONPAY,Card with a 17 digit number.
                           621234567890000002,UNIONPAY,Card with a 18 digit number.
                           6212345678900000003,UNIONPAY,Card with a 19 digit number.
                           """)
    @DisplayName("Test UnionPay card numbers ")
    void testUnionPayCardNetwork(String cardNumber, String expectedNetwork) {
        assertEquals(expectedNetwork, CardNetworkIdentifier.getCardNetwork(cardNumber));
    }
}
