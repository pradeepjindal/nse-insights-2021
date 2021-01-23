package org.pra.nse.csv.bean.in;

public class BpBean {
    private String clientType;
    private long futIdxLong;
    private long futIdxShort;
    private long futStkLong;
    private long futStkShort;
    private long optIdxCallLong;
    private long optIdxPutLong;
    private long optIdxCallShort;
    private long optIdxPutShort;
    private long optStkCallLong;
    private long optStkPutLong;
    private long optStkCallShort;
    private long optStkPutShort;
    private long totalLongContracts;
    private long totalShortContracts;

    @Override
    public String toString() {
        return "BpBean{" +
                "clientType='" + clientType + '\'' +
                ", futIdxLong=" + futIdxLong +
                ", futIdxShort=" + futIdxShort +
                ", futStkLong=" + futStkLong +
                ", futStkShort=" + futStkShort +
                ", optIdxCallLong=" + optIdxCallLong +
                ", optIdxPutLong=" + optIdxPutLong +
                ", optIdxCallShort=" + optIdxCallShort +
                ", optIdxPutShort=" + optIdxPutShort +
                ", optStkCallLong=" + optStkCallLong +
                ", optStkPutLong=" + optStkPutLong +
                ", optStkCallShort=" + optStkCallShort +
                ", optStkPutShort=" + optStkPutShort +
                ", totalLongContracts=" + totalLongContracts +
                ", totalShortContracts=" + totalShortContracts +
                '}';
    }


    public String getClientType() {
        return clientType;
    }

    public void setClientType(String clientType) {
        this.clientType = clientType;
    }

    public long getFutIdxLong() {
        return futIdxLong;
    }

    public void setFutIdxLong(long futIdxLong) {
        this.futIdxLong = futIdxLong;
    }

    public long getFutIdxShort() {
        return futIdxShort;
    }

    public void setFutIdxShort(long futIdxShort) {
        this.futIdxShort = futIdxShort;
    }

    public long getFutStkLong() {
        return futStkLong;
    }

    public void setFutStkLong(long futStkLong) {
        this.futStkLong = futStkLong;
    }

    public long getFutStkShort() {
        return futStkShort;
    }

    public void setFutStkShort(long futStkShort) {
        this.futStkShort = futStkShort;
    }

    public long getOptIdxCallLong() {
        return optIdxCallLong;
    }

    public void setOptIdxCallLong(long optIdxCallLong) {
        this.optIdxCallLong = optIdxCallLong;
    }

    public long getOptIdxPutLong() {
        return optIdxPutLong;
    }

    public void setOptIdxPutLong(long optIdxPutLong) {
        this.optIdxPutLong = optIdxPutLong;
    }

    public long getOptIdxCallShort() {
        return optIdxCallShort;
    }

    public void setOptIdxCallShort(long optIdxCallShort) {
        this.optIdxCallShort = optIdxCallShort;
    }

    public long getOptIdxPutShort() {
        return optIdxPutShort;
    }

    public void setOptIdxPutShort(long optIdxPutShort) {
        this.optIdxPutShort = optIdxPutShort;
    }

    public long getOptStkCallLong() {
        return optStkCallLong;
    }

    public void setOptStkCallLong(long optStkCallLong) {
        this.optStkCallLong = optStkCallLong;
    }

    public long getOptStkPutLong() {
        return optStkPutLong;
    }

    public void setOptStkPutLong(long optStkPutLong) {
        this.optStkPutLong = optStkPutLong;
    }

    public long getOptStkCallShort() {
        return optStkCallShort;
    }

    public void setOptStkCallShort(long optStkCallShort) {
        this.optStkCallShort = optStkCallShort;
    }

    public long getOptStkPutShort() {
        return optStkPutShort;
    }

    public void setOptStkPutShort(long optStkPutShort) {
        this.optStkPutShort = optStkPutShort;
    }

    public long getTotalLongContracts() {
        return totalLongContracts;
    }

    public void setTotalLongContracts(long totalLongContracts) {
        this.totalLongContracts = totalLongContracts;
    }

    public long getTotalShortContracts() {
        return totalShortContracts;
    }

    public void setTotalShortContracts(long totalShortContracts) {
        this.totalShortContracts = totalShortContracts;
    }
}
