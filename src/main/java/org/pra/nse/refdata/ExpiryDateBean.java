package org.pra.nse.refdata;

import java.time.LocalDate;

public class ExpiryDateBean {

    private LocalDate ed;
    private LocalDate fed;

    public LocalDate getEd() {
        return ed;
    }

    public void setEd(LocalDate ed) {
        this.ed = ed;
    }

    public LocalDate getFed() {
        return fed;
    }

    public void setFed(LocalDate fed) {
        this.fed = fed;
    }

}
