package blockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import utils.Printer;

import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class Peer {
    private  Map<String,String> blockPool  = new ConcurrentHashMap<>();
    private ThreadLocal<String> currentBlock = new ThreadLocal<>();
    private String name;
    private ThreadLocal<String> currentProof = new ThreadLocal<>();
    private String address = "";

    public static  Map<String,String> addresses = new ConcurrentHashMap<>();
    static {
        addresses.put("peer4" , "a32ff571eccd55e2a3227140aa0f14bce7bda104e52aaabb41210db93c79aa71");
        addresses.put("peer1" , "5a022a58d8e2a0543494048ebc2d647f3fe7e8294f4192046f81daa02e083037");
        addresses.put("peer2" , "3f3724572bd15aee097b666bcf46bb77aa6c808bdd9ccca1a44089610d1388e6");
        addresses.put("peer7" , "8e531ea3e2f059c80c31ac36c6e08dbdd8f3db4dfb5da6921e42d09620b12b18");
        addresses.put("peer8" , "266decef04a06df1ba07e796e76c28090d718bf7b70d399b0d054ae013fb70d9");
        addresses.put("peer6" , "41542c332df4e108f24329847ee0c07fbc5b2f12b64e09571d229f933fba2b26");
        addresses.put("peer3" , "b17074b916dbd2c1bea57fbd8c1d496d6c06eda5d7f0aa7e2f8f46f37786e4e7");
        addresses.put("peer9" , "d42eda240b2723a56580bd0105d64e080ab2fecf1910d9d5fca7d86e9b5c61cc");
        addresses.put("peer0" , "cf8b7bbe793c684ba1251c9328f4c40889fd272b0882f627289ab3920f1edb5c");
        addresses.put("peer5" , "a64e3aef75e774a0380b3142143967a653a0c580b98c73ecabbc0e4ca159dc51");
    }



    Peer(String name){
        this.blockPool = JSON.parseObject(JSON.toJSONString(BlockChainNet.blockPool),new TypeReference<Map<String, String>>(){});
        BlockChainNet.peers.add(this);
        Block block = JSON.parseObject(BlockChainNet.currentBlock , new TypeReference<Block>(){});currentProof.set(block.getProof());
        this.name = name;
        this.address = addresses.get(name);
        //account.addAndGet(100L);
        Printer.println("Peer "+name+" 's address is: "+address);
    }

    @Override
    protected void finalize() throws Throwable {
        BlockChainNet.peers.remove(this);
        super.finalize();
    }

    public static boolean isDebug = false;
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
            if(isDebug) {
                log();
            }
            String  hash = MD5Utils.getMD5(currentProof+proof);
            if(BlockChainNet.isHit(hash)){
                synchronized (this) {
                    if (BlockChainNet.isOk(this, proof)) {
                        MoneyBags.bags.get(address).addAndGet(5L);
                        Printer.println("Peer:"+this.name+".Proof:"+ proof +  ",hit hash:"+hash);
                        Printer.println("");
                        Printer.println("");
                    }
                }
            }
        }
    }

    public void appendBlock(Block block){
        currentBlock.set(block.getHash());
        currentProof.set(block.getProof());
        blockPool.put(block.getHash(),JSON.toJSONString(block));
       }


    public boolean vote(String str) {
        String hash = MD5Utils.getMD5(BlockChainNet.currentProof+str);
        return BlockChainNet.isHit(hash);
    }




}
