package com.sixfold.routeplanner;

import com.sixfold.routeplanner.utils.StatusName;
import lombok.Getter;
import lombok.Setter;

public class AppStatus {

    @Getter
    @Setter
    private static String status = StatusName.STOPPED.name();

    private AppStatus() {
    }

}
