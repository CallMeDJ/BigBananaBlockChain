package blockchain;

import java.util.ArrayList;
import java.util.List;

public class Block {
    /**
     * 工作量证明
     */
    private String proof;
    /**
     * 当前块的Hash值
     */
    private String hash;

    /**
     * 上一个块的Hash值
     */
    private String previous;

    /**
     * 这个块包含的交易
     */
    private List<Trade> trade = new ArrayList<>();


    /**
     * 区块的位置
     */
    private Long index;


    public Long getIndex() {
        return index;
    }

    public void setIndex(Long index) {
        this.index = index;
    }

    public String getProof() {
        return proof;
    }

    public void setProof(String proof) {
        this.proof = proof;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public String getPrevious() {
        return previous;
    }

    public void setPrevious(String previous) {
        this.previous = previous;
    }

    public List<Trade> getTrade() {
        return trade;
    }

    public void setTrade(List<Trade> trade) {
        this.trade = trade;
    }

}
