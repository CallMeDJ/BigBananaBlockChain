package blockchain;


/**
 * @Author  大蕉
 * @Since   2018-02-04
 * @Desc    区块链中的交易
 */
public class Trade {

    /**
     * 从哪转出
     */
    private String from;
    /**
     * 转入到哪
     */
    private String to;

    /**
     * 交易了什么
     */
    private Long what;
    public Trade(){}

    public Trade(String from, String to, Long what) {
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

    public Long getWhat() {
        return what;
    }

    public void setWhat(Long what) {
        this.what = what;
    }
}
