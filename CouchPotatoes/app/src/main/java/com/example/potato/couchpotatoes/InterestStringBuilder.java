package com.example.potato.couchpotatoes;

import com.google.firebase.database.DataSnapshot;

import static com.example.potato.couchpotatoes.StringUtilities.addStrAtPos;

public class InterestStringBuilder {

    public String getInterestString(DataSnapshot dataSnapshot) {
        String interests = "";

        // Format the user's interests
        for ( DataSnapshot child : dataSnapshot.getChildren() ) {
            String interest = child.getKey();
            interests += interest;
            interests += "\n\n";

            for ( DataSnapshot subchild : child.getChildren() ) {
                String subcategory = subchild.getKey();
                String preference = (String) subchild.getValue();


                int newLinePos = 22;
                //interests += "◇  ";
                interests += "    ";
                interests += addStrAtPos( subcategory, "\n     ", newLinePos );
                interests += "  -  ";
                interests += addStrAtPos( preference, "\n     ", newLinePos );
                interests += "\n";
            }
            interests += "\n";
        }

       return interests;
    }
}
