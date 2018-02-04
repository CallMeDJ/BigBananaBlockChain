package blockchain;


import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import com.alibaba.fastjson.serializer.SerializerFeature;
import utils.Printer;


import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class BlockChainNet {
    public static Map<String,String> blockPool  = new ConcurrentHashMap<>();
    public static String currentBlock = "";
    public static Queue<Trade> currentTradePool = new LinkedList<>();

    public static String currentProof = "";
    public static Set<Peer> peers = new ConcurrentHashMap<>().newKeySet();
    public Block getBlock(String hash){
        return JSONObject.parseObject(blockPool.get(hash),Block.class);
    }

    public static  int hard = 12;
    public static  Lock lock = new ReentrantLock();
    public static boolean isOk(Peer peer , String proof){
        String hashed = MD5Utils.getMD5(currentProof+proof);
        boolean isHit =  isHit(hashed);
        if(isHit){

            //Printer.println("str:" + str +",HASH"+hashed +"isHit");
            if(vote(proof)) {

                lock.lock();

                List<Trade> currentTraddes = new ArrayList<>();
                while(!currentTradePool.isEmpty()){
                    currentTraddes.add(currentTradePool.poll());
                }

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
                lock.unlock();
                return true;
            }
        }


        return false;
    }


    private static void append(Block block){
        for(Peer peer : peers){
            peer.appendBlock(block);
        }
    }

    public static  double prob = 0.9;

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
    private static void log(){
        if(System.currentTimeMillis() - previous >= 3000){
            Printer.println("current bugs:");
            Printer.println(JSON.toJSONString(MoneyBags.bags,SerializerFeature.PrettyFormat));
            previous = System.currentTimeMillis();
        }
    }


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
