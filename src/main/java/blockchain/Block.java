package blockchain;

import com.alibaba.fastjson.JSONObject;

public class Block {
    private String proof;
    private String hash;
    private String previous;
    private Trade trade;


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

    public Trade getTrade() {
        return trade;
    }

    public void setTrade(Trade trade) {
        this.trade = trade;
    }

    @Override
    public String toString() {
        return JSONObject.toJSONString(this);
    }
}
