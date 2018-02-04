package blockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import utils.Printer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class Peer {
    private  Map<String,String> blockPool  = new ConcurrentHashMap<>();
    private ThreadLocal<String> currentBlock = new ThreadLocal<>();
    private String name;
    private ThreadLocal<String> currentProof = new ThreadLocal<>();

    Peer(String name){
        this.blockPool = JSON.parseObject(JSON.toJSONString(BlockChainNet.blockPool),new TypeReference<Map<String, String>>(){});
        BlockChainNet.peers.add(this);
        Block block = JSON.parseObject(BlockChainNet.currentBlock , new TypeReference<Block>(){});currentProof.set(block.getProof());
        this.name = name;
    }

    @Override
    protected void finalize() throws Throwable {
        BlockChainNet.peers.remove(this);
        super.finalize();
    }

    private long previous = System.currentTimeMillis();
    private void log(){
        if(System.currentTimeMillis() - previous >= 3000){
            Printer.println(this.name + " alive");
            previous = System.currentTimeMillis();
        }
    }

    public void account(){
        while(true){
            String  proof = String.valueOf(Math.random() * (1000000));
            log();
            String  hash = MD5Utils.getMD5(currentProof+proof);
            if(BlockChainNet.isHit(hash)){
                synchronized (this) {
                    if (BlockChainNet.isOk(this, proof)) {
                        Printer.println("Peer:"+this.name+".Proof:"+ proof +  ",hit hash:"+hash);
                    }
                }
            }
        }
    }

    public void appendBlock(Block block){
        currentBlock.set(block.getHash());
        currentProof.set(block.getProof());
        blockPool.put(block.getHash(),block.toString());
       }


    public boolean vote(String str) {
        String hash = MD5Utils.getMD5(BlockChainNet.currentProof+str);
        return BlockChainNet.isHit(hash);
    }




}
