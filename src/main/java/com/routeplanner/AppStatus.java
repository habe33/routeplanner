package com.routeplanner;

import com.routeplanner.utils.StatusName;
import lombok.Getter;
import lombok.Setter;

public class AppStatus {

    @Getter
    @Setter
    private static volatile String status = StatusName.STOPPED.name();

    private AppStatus() {
    }

}
