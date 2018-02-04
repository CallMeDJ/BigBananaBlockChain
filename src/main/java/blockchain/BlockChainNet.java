package blockchain;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import utils.Printer;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;


/**
 * @Author  大蕉
 * @Since   2018-02-04
 * @Desc    区块链网络中心
 */
public class BlockChainNet {

    /**
     * 区块链网络的区块链，其中 key 是hash值，value是JSON化的Block
     */
    public static Map<String,String> blockPool  = new ConcurrentHashMap<>();

    /**
     * 当前 JSON 化的区块
     */
    public static String currentBlock = "";

    /**
     * 交易池子，每次交易都会先写到池子队列中。区块记账的时候会清空当前池子里的交易。
     */
    public static Queue<Trade> currentTradePool = new LinkedList<>();

    /**
     * 当前工作量证明
     */
    public static String currentProof = "";


    /**
     * 节点们
     */
    public static Set<Peer> peers = new ConcurrentHashMap<>().newKeySet();
    public Block getBlock(String hash){
        return JSONObject.parseObject(blockPool.get(hash),Block.class);
    }

    /**
     * 挖矿难度，这里的 hard 表示，只要 hash 值中有 N 个 0 就可以代表挖矿成功。
     */
    public static  int hard = 12;
    public static  Lock lock = new ReentrantLock();

    /**
     *
     * @param peer
     * @param proof
     * @return
     * @desc  当有节点挖矿成功时，会请求区块网络进行确认。为了效率网络会自己先确认一遍，然后发起一轮投票，如果超过90%的节点认可，则认为本次计算有效。
     *        如果区块有效，那么直接把当前的交易池子清空，并生成一个新的区块。然后通知所有的节点更新自己的账本。
     */
    public static boolean isOk(Peer peer , String proof){
        String hashed = MD5Utils.getMD5(currentProof+proof);
        boolean isHit =  isHit(hashed);
        if(isHit){

            //Printer.println("str:" + str +",HASH"+hashed +"isHit");
            if(vote(proof)) {

                lock.lock();

                /**
                 * 清空当前交易池子
                 */
                List<Trade> currentTraddes = new ArrayList<>();
                while(!currentTradePool.isEmpty()){
                    currentTraddes.add(currentTradePool.poll());
                }


                /**
                 * 生成新区块
                 */
                Block block = new Block();
                block.setHash(hashed);

                Block previous = JSON.parseObject(currentBlock,new TypeReference<Block>(){});
                block.setPrevious(previous.getHash());
                block.setTrade(currentTraddes);
                block.setProof(proof);

                Printer.println("block found : "+JSON.toJSONString(block));
                Printer.println("");
                currentBlock = JSON.toJSONString(block);
                currentProof = proof;
                blockPool.put(hashed, JSON.toJSONString(block));


                /**
                 * 通知所有节点更新区块链
                 */
                append(block);
                lock.unlock();
                return true;
            }
        }


        return false;
    }

    /**
     * 通知所有节点更新区块链
     */
    private static void append(Block block){
        for(Peer peer : peers){
            peer.appendBlock(block);
        }
    }


    /**
     * 投票通过比例。这里的 prob 表示 百分之 prob 的节点投票通过则代表本次计算有效。
     */
    public static  double prob = 0.9;


    /**
     * 通知所有节点进行投票
     */
    public static boolean vote(String str){
        double voteCount = 0;
        int peerSize = peers.size();
        for(Peer peer : peers){
            if(peer.vote(str)){
                voteCount += 1;
            }

            if(voteCount / peerSize >= prob){
                return true;
            }

        }


        return false;

    }

    /**
     *
     * @param hash
     * @return
     * @desc  公共方法，检查当前的 hash 值是不是有效的 hash 值。
     */
    public static boolean isHit(String hash){

        int zeroCount = 0;
        int length = hash.length();
        for(int i = 0 ; i < length ; i++){
            if( hash.charAt(i) == '0'){
                zeroCount ++;
            }

            if(zeroCount == hard){
                return true;
            }
        }

        return false;

    }


    /**
     * 初始化创世块
     */
    public static void  INIT_THE_ONE_BLOCK(){
            Block block = new Block();

            String proof = "100";
            String hash = MD5Utils.getMD5(proof);
            block.setProof(proof);
            block.setHash(hash);

            currentBlock = JSON.toJSONString(block);
            currentProof = proof;
            blockPool.put(hash,JSON.toJSONString(block));

    }



    public static boolean isDebug = true;
    private static long previous = System.currentTimeMillis();

    /**
     *
     * 打印钱包当前的大蕉币数量情况。
     *
     */
    private static void log(){
        if(System.currentTimeMillis() - previous >= 3000){
            Printer.println("current bags:");
            Printer.println(JSON.toJSONString(MoneyBags.bags,SerializerFeature.PrettyFormat));
            previous = System.currentTimeMillis();
        }
    }

    /**
     *
     * main 方法。
     * 做了三件事。
     * 1、初始化创世块
     * 2、初始化十个节点。
     * 3、监控控制台的输入，以便添加交易。
     * 比如这样添加一笔交易。
     * from to what
     *
     * a32ff571eccd55e2a3227140aa0f14bce7bda104e52aaabb41210db93c79aa71 5a022a58d8e2a0543494048ebc2d647f3fe7e8294f4192046f81daa02e083037 1
     *
     */
    public static void main(String[] args){
        //初始化创世块
        INIT_THE_ONE_BLOCK();

        for(int i = 0 ; i <10 ; i++) {
            final int index = i;
            new Thread(() -> {
                Peer peer1 = new Peer("peer"+index);
                peer1.account();
            }).start();
        }

        Scanner scanner = new Scanner(System.in);

        while (scanner.hasNext()){

            String a = scanner.nextLine();

            String[] commans = a.split(" ");

            String from = commans[0];
            String to = commans[1];
            Long what = Long.valueOf(commans[2]);
            Trade trade = new Trade(from,to,what);
            MoneyBags.bags.get(from).addAndGet(-1 * what);
            MoneyBags.bags.get(to).addAndGet(what);
            currentTradePool.add(trade);

            log();

        }





    }
}
