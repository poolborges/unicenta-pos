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

import java.util.regex.Pattern;

/**
 *
 * @author poolborges
 */
public class CardNetworkIdentifier {

    /**
     * Payment Card Networks and brands
     *
     * Visa: One of the two most widely accepted payment networks, with a strong
     * presence globally.
     *
     * Mastercard: The other of the two most widely accepted payment networks.
     *
     * American Express (AMEX): A major payment network and card issuer known
     * for strong travel rewards, though not as universally accepted as Visa or
     * Mastercard.
     *
     * Discover: A payment network and card issuer, which has a partnership with
     * JCB, allowing JCB cards to be used at many Discover merchants in the US.
     *
     * JCB: A Japanese credit card company that partners with Discover in the US
     * and American Express in some other countries, making its cards usable in
     * more places.
     *
     * Diners Club: A payment network that has partnered with other networks
     * like Discover and Mastercard, meaning Diners Club cards may have a
     * Mastercard or Discover logo and be accepted in those networks' locations
     *
     * UnionPay, or China UnionPay (CUP), is a Chinese state-owned financial
     * services corporation and one of the largest payment card networks in the
     * world While it is the dominant payment network in China, it has
     * significantly expanded its global reach and is accepted in 181 countries
     * and regions. Like Visa and Mastercard, UnionPay issues credit, debit, and
     * prepaid cards in partnership with thousands of financial institutions
     * worldwide.
     */
    
    
    /**
     * All Visa card numbers start with a 4. 
     * New cards have 16 digits. 
     * Old cards have 13.
     */
    private static final Pattern VISA_PATTERN = Pattern.compile("^4[0-9]{12}(?:[0-9]{3})?$");
    /**
     * MasterCard numbers either start with the numbers 51 through 55 or with the numbers 2221 through 2720. 
     * All have 16 digits.
     */
    private static final Pattern MASTERCARD_PATTERN = Pattern.compile("^(?:5[1-5][0-9]{2}|222[1-9]|22[3-9][0-9]|2[3-6][0-9]{2}|27[01][0-9]|2720)[0-9]{12}$");
    /**
     * American Express card numbers start with 34 or 37. 
     * All have 15 digits.
     */
    private static final Pattern AMEX_PATTERN = Pattern.compile("^3[47][0-9]{13}$");
    /**
     * Discover card numbers start with 6011 or 65. 
     * All have 16 digits.
     */
    private static final Pattern DISCOVER_PATTERN = Pattern.compile("^6(?:011|5[0-9]{2})[0-9]{12}$");
    /**
     * JCB card numbers start with 2131 or 1800 have 15 digits. 
     * JCB card numbers start with 35 have 16 digits.
     */
    private static final Pattern JCB_PATTERN = Pattern.compile("^(?:2131|1800|35\\d{3})\\d{11}$");
    /**
     * Diners Club card numbers start with 300 through 305, 36 or 38. 
     * All have 14 digits. 
     * There are Diners Club cards that begin with 5 and have 16 digits. 
     * These are a joint venture between Diners Club and MasterCard, and should be processed like a MasterCard.
     */
    private static final Pattern DINERS_PATTERN = Pattern.compile("^3(?:0[0-5]|[68][0-9])[0-9]{11}$");
    /**
     * UnionPay cart numbers starting with 62. 
     * Covering 16 to 19-digit 
     */
    private static final Pattern UNIONPAY_PATTERN = Pattern.compile("^62[0-9]{14,17}$");

    /**
     * Determines the card network (e.g., VISA, MASTERCARD) from a given card
     * number string.
     *
     * @param cardNumber The card number string, which can include spaces or
     * dashes.
     * @return The name of the card network, or "UNKNOWN" if the network is not
     * recognized.
     */
    public static String getCardNetwork(String cardNumber) {
        if (cardNumber == null || cardNumber.trim().isEmpty()) {
            return "INVALID";
        }

        // Remove any spaces or dashes from the number
        String cleanedNumber = cardNumber.replaceAll("[- ]", "");

        // Basic check for numeric characters
        if (!cleanedNumber.matches("\\d+")) {
            return "INVALID";
        }

        // Check patterns in an order that avoids prefix overlaps.
        // For instance, a 16-digit Diners Club (starting with 5) is a Master Card.
        // DINERS must be checked before JCB due to overlapping prefixes
        if (UNIONPAY_PATTERN.matcher(cleanedNumber).matches()) {
            return "UNIONPAY";
        } else if (VISA_PATTERN.matcher(cleanedNumber).matches()) {
            return "VISA";
        } else if (MASTERCARD_PATTERN.matcher(cleanedNumber).matches()) {
            return "MASTERCARD";
        } else if (AMEX_PATTERN.matcher(cleanedNumber).matches()) {
            return "AMEX";
        } else if (DISCOVER_PATTERN.matcher(cleanedNumber).matches()) {
            return "DISCOVER";
        } else if (DINERS_PATTERN.matcher(cleanedNumber).matches()) {
            return "DINERS";
        } else if (JCB_PATTERN.matcher(cleanedNumber).matches()) {
            return "JCB";
        }

        return "UNKNOWN";
    }
}
