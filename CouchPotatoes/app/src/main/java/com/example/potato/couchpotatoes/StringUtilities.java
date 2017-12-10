package com.example.potato.couchpotatoes;

/**
 * Utility methods for strings
 */
public final class StringUtilities {
    /**
     * Adds space between two strings
     *
     * @param title - first string to combine
     * @param value - second string to combine
     * @param desiredLength - intended length of combined string
     * @return combined string
     */
    public static String paddSpace( String title, String value, int desiredLength ) {
        String str = title;
        int numSpaces = desiredLength - title.length() - value.length();

        for ( int i = 0; i < numSpaces; i++ ) {
            str += "\t";
        }

        str += value;

        return str;
    }

    /**
     * Adds space between two strings and adds a newline to the end
     *
     * @param title - first string to combine
     * @param value - second string to combine
     * @param desiredLength - intended length of combined string
     * @return combined string with newline
     */
    public static String paddSpaceln( String title, String value, int desiredLength ) {
        return paddSpace( title, value + "\n", desiredLength );
    }

    /**
     * Adds space to the end of two strings
     *
     * @param title - first string to combine
     * @param value - second string to combine
     * @param desiredLength - intended length of combined string
     * @return combined string
     */
    public static String paddSpaceEnd( String title, String value, int desiredLength ) {
        String str = title + value;
        int numSpaces = desiredLength - title.length() - value.length();

        for ( int i = 0; i < numSpaces; i++ ) {
            str += "\t";
        }

        str += "|";

        return str;
    }

    /**
     * Adds space to the end of two strings and adds a newline
     *
     * @param title - first string to combine
     * @param value - second string to combine
     * @param desiredLength - intended length of combined string
     * @return combined string with newline
     */
    public static String paddSpaceEndln( String title, String value, int desiredLength ) {
        return paddSpaceEnd( title, value, desiredLength ) + "\n";
    }

    /**
     * Adds space inside a string
     *
     * @param str - string to insert another string into
     * @param addition - string to insert
     * @param position - position to insert string at
     * @return modified string
     */
    public static String addStrAtPos( String str, String addition, int position ) {
        String ret = "";

        for ( int i = 0; i < str.length(); i++ ) {
            if ( i == position ) {
                ret += addition;
            }
            ret += str.charAt( i );
        }

        return ret;
    }
}
