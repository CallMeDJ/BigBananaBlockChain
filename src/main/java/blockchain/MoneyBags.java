package blockchain;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

public class MoneyBags {
    public static Map<String,AtomicLong> bags = new ConcurrentHashMap<>();

    static{
        bags.put("8e531ea3e2f059c80c31ac36c6e08dbdd8f3db4dfb5da6921e42d09620b12b18",new AtomicLong());
        bags.put("3f3724572bd15aee097b666bcf46bb77aa6c808bdd9ccca1a44089610d1388e6",new AtomicLong());
        bags.put("d42eda240b2723a56580bd0105d64e080ab2fecf1910d9d5fca7d86e9b5c61cc",new AtomicLong());
        bags.put("41542c332df4e108f24329847ee0c07fbc5b2f12b64e09571d229f933fba2b26",new AtomicLong());
        bags.put("a64e3aef75e774a0380b3142143967a653a0c580b98c73ecabbc0e4ca159dc51",new AtomicLong());
        bags.put("266decef04a06df1ba07e796e76c28090d718bf7b70d399b0d054ae013fb70d9",new AtomicLong());
        bags.put("5a022a58d8e2a0543494048ebc2d647f3fe7e8294f4192046f81daa02e083037",new AtomicLong());
        bags.put("b17074b916dbd2c1bea57fbd8c1d496d6c06eda5d7f0aa7e2f8f46f37786e4e7",new AtomicLong());
        bags.put("cf8b7bbe793c684ba1251c9328f4c40889fd272b0882f627289ab3920f1edb5c",new AtomicLong());
        bags.put("a32ff571eccd55e2a3227140aa0f14bce7bda104e52aaabb41210db93c79aa71",new AtomicLong());
}
}
