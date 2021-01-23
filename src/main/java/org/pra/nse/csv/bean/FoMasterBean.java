package org.pra.nse.csv.bean;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

public class FoMasterBean {
    private final Set<LocalDate> foExpiryDates;

    public FoMasterBean() {
        this.foExpiryDates = new HashSet<>();
    }

    public Set<LocalDate> getFoExpiryDates() {
        return foExpiryDates;
    }
}
