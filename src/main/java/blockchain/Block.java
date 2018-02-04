package blockchain;

import java.util.ArrayList;
import java.util.List;

public class Block {
    private String proof;
    private String hash;
    private String previous;
    private List<Trade> trade = new ArrayList<>();


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
