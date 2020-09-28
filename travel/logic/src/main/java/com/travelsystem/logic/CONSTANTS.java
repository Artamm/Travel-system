package com.travelsystem.logic;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CONSTANTS {
    public static final int WIDTH = 344;
    public static final int HEIGHT = 340;
    public static final int THMB_WIDTH = 100;
    public static final int THMB_HEIGHT = 100;

    public static final String SUCCESSFUL_ADDITION = "You successfuly applied for a journey. The journey will be added in your journey list soon.";
    public static final String FAILED_ADDITION = "Sorry, but you can't apply for this journey, because the number of people reached it limit. Try to contact trip's organizator.";
public static final String SUCCESSFUL_SIGN_UP = "You have successfully registered. On you email will be sent mail";
    public static final String SUCCESSFUL_MODERATED_ADDITION = "You successfuly applied for a pre-moderated journey. The journey will be added in your journey list after organizator approves your candidature.";


}
