package blockchain;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import utils.Printer;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;



/**
 * @Author  大蕉
 * @Since   2018-02-04
 * @Desc    挖矿节点
 */
public class Peer {

    /**
     * Peer自己持有的区块链，其中 key 是hash值，value是JSON化的Block
     */
    private  Map<String,String> blockPool  = new ConcurrentHashMap<>();

    /**
     * 区块链的当前块
     */
    private ThreadLocal<String> currentBlock = new ThreadLocal<>();

    /**
     * 节点名字
     */
    private String name;

    /**
     * 当前的工作量证明，用于进行 Hash。
     */
    private ThreadLocal<String> currentProof = new ThreadLocal<>();

    /**
     * 节点拥有的钱包地址
     */
    private String address = "";

    public static  Map<String,String> addresses = new ConcurrentHashMap<>();


    /**
     * 初始化一下各个节点的钱包位置，实际区块链应该由节点自己配置。
     */
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


    /**
     * 初始化一个节点，首先从区块网络上下载最新的完整区块链，然后把自己注册到网络中，并初始化当前块和当前工作量证明等。
     */
    Peer(String name){
        this.blockPool = JSON.parseObject(JSON.toJSONString(BlockChainNet.blockPool),new TypeReference<Map<String, String>>(){});
        BlockChainNet.peers.add(this);
        Block block = JSON.parseObject(BlockChainNet.currentBlock , new TypeReference<Block>(){});
        currentProof.set(block.getProof());
        currentBlock.set(BlockChainNet.currentBlock);
        this.name = name;
        this.address = addresses.get(name);

        //account.addAndGet(100L);
        Printer.println("Peer "+name+" 's address is: "+address);
    }


    /**
     * 若节点退出，将自己从区块链中注销
     */
    @Override
    protected void finalize() throws Throwable {
        BlockChainNet.peers.remove(this);
        super.finalize();
    }


    public static boolean isDebug = false;
    private long previous = System.currentTimeMillis();

    /**
     * 每三秒打印一下自己的存活状态，debug用的
     */
    private void log(){
        if(System.currentTimeMillis() - previous >= 3000){
            Printer.println(this.name + " alive");
            previous = System.currentTimeMillis();
        }
    }


    /**
     * 核心程序，记账。（挖矿）
     * 在自己的字典中产生随机数，然后跟上一个 proof 合起来作为下一次 Hash 的值，进行 Hash。（工作量证明）
     * 自己检查这个 Hash 值是不是符合当前难度的。如果不符合继续计算。
     * 如果符合，请求区块链网络进行检查。（共识算法）
     * 检查通过往自己的钱包里加5个 大蕉币。（挖矿奖励）
     *
     */
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

    /**
     *
     * @param block
     * @desc 若其他节点计算出区块了，那么区块网路会通知各个节点进行补充。
     *       接收到请求就直接往自己的链上追加就行了。
     */

    public void appendBlock(Block block){
        currentBlock.set(block.getHash());
        currentProof.set(block.getProof());
        blockPool.put(block.getHash(),JSON.toJSONString(block));
       }


    /**
     *
     * @param str
     * @desc 当有节点计算出区块，区块链网络会发起投票。投票内容就是根据智能合约的内容进行检验，以证明当前的工作量证明是不是有效的。
     * @return boolean
     */
    public boolean vote(String str) {
        String hash = MD5Utils.getMD5(BlockChainNet.currentProof+str);
        return BlockChainNet.isHit(hash);
    }




}
