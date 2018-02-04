package blockchain;

public class Trade {
    private String from;
    private String to;
    private Integer what;


    public Trade(String from, String to, Integer what) {
        this.from = from;
        this.to = to;
        this.what = what;
    }

    public String getFrom() {
        return from;
    }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getTo() {
        return to;
    }

    public void setTo(String to) {
        this.to = to;
    }

    public Integer getWhat() {
        return what;
    }

    public void setWhat(Integer what) {
        this.what = what;
    }
}
