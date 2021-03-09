package core;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.mongodb.*;
import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoCursor;
import static com.mongodb.client.model.Projections.excludeId;

import org.bson.*;
import org.bson.conversions.Bson;
import org.json.JSONObject;
import org.reactivestreams.Subscription;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.web3j.crypto.*;
import org.web3j.model.Greeter;
import org.web3j.protocol.Web3j;
import org.web3j.protocol.core.DefaultBlockParameter;
import org.web3j.protocol.core.DefaultBlockParameterName;
import org.web3j.protocol.core.DefaultBlockParameterNumber;
import org.web3j.protocol.core.methods.response.EthBlock;
import org.web3j.protocol.core.methods.response.EthGetBalance;
import org.web3j.protocol.core.methods.response.Transaction;
import org.web3j.protocol.core.methods.response.TransactionReceipt;
import org.web3j.protocol.exceptions.TransactionException;
import org.web3j.protocol.http.HttpService;
import org.web3j.protocol.ipc.UnixIpcService;
import org.web3j.protocol.websocket.WebSocketService;
import org.web3j.tx.Transfer;
import org.web3j.tx.gas.ContractGasProvider;
import org.web3j.tx.gas.DefaultGasProvider;
import org.web3j.utils.Convert;
import org.web3j.utils.Numeric;

import java.io.*;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.ConnectException;
import java.security.InvalidAlgorithmParameterException;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


import com.mongodb.client.MongoDatabase;



class Base
{
    private static ArrayList<String> result;
    private Web3j               web3j;
    MongoClient                 mongoClient;
    MongoDatabase               mongoDatabase;
    MongoCollection<Document>   Transactions;
    MongoCollection<Document>   Prikeys;
    Credentials                 rich;
    ArrayList<Credentials>      credentials;

    private static final Logger log = LoggerFactory.getLogger(Base.class);
    public static void main(String[] args) throws Exception {
        WebSocketService ws = new WebSocketService("http://18.182.107.97:8546", false);
        Base test = new Base(ws,"mongodb://admin:7400ZXR.@18.182.107.97:27017/admin");
        test.credentials = test.get_wallet_from_db(5,"123456");
        System.out.println(test.get_balance(test.rich).getBalance());
        test.Give(test.credentials, 1000000);
        test.create_much_random_tx(test.credentials,1000);
    }
    public Base(WebSocketService ETH, String mongoose_ip) throws IOException, CipherException {
        ETH.connect();
        web3j = Web3j.build(ETH);
        MongoClientURI connStr = new MongoClientURI(mongoose_ip);
        mongoClient = new MongoClient(connStr);
        mongoDatabase = mongoClient.getDatabase("ETH");
        Prikeys = mongoDatabase.getCollection("Prikeys");
        Transactions = mongoDatabase.getCollection("Transactions");
        rich = WalletUtils.loadCredentials("123456", "F:/node0");
    }

    public Base(UnixIpcService ip, String mongoose_ip,int port) {
        ServerAddress adder = new ServerAddress(mongoose_ip,port);
        web3j = Web3j.build(ip);
        mongoClient = new MongoClient(adder);
    }

    public EthGetBalance get_balance(Credentials credentials) throws IOException {
        EthGetBalance ethGetBalance = web3j.ethGetBalance(credentials.getAddress(), DefaultBlockParameter.valueOf("latest")).send();
        return ethGetBalance;
    }
    public ArrayList<Credentials> get_wallet_from_db(int count,String pwd) throws JsonProcessingException, CipherException {
        ArrayList<Credentials> credentials = new ArrayList<>();
        FindIterable<Document> findIterable = Prikeys.find().projection(excludeId()) ;
        MongoCursor<Document> mongoCursor = findIterable.iterator();
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < count; i++) {
            if (mongoCursor.hasNext()) {
                Document doc = mongoCursor.next();
                WalletFile walletFile = objectMapper.readValue(doc.toJson(), WalletFile.class);
                credentials.add(Credentials.create(Wallet.decrypt(pwd, walletFile)));
            }
            else
            {
                break;
            }
        }
        return credentials;
    }




    public void import_db(ArrayList<WalletFile> walletFiles) throws IOException
    {
        ArrayList<Document> docs = new ArrayList<>();
        ObjectMapper objectMapper = new ObjectMapper();
        walletFiles.forEach((walletFile -> {
                String json = null;
                try {
                    json = objectMapper.writeValueAsString(walletFile);
                    Document doc =  Document.parse(json);
                    docs.add(doc);
                    if (docs.size() == 1000) {
                        Prikeys.insertMany(docs);
                        log.info("import_db: import "+docs.size()+" walletfile to mongodb");
                        docs.clear();
                    }
                }
                catch (Exception e)
                {
                    e.printStackTrace();
                }
        }));
        if (!docs.isEmpty())
        {
            Prikeys.insertMany(docs);
            log.info("import_db: import "+docs.size()+" walletfile to mongodb end ");
            docs.clear();
        }
    }

    public void takeTxintoDb(int start) {
        Subscription subscription = (Subscription) web3j.replayPastAndFutureBlocksFlowable(new DefaultBlockParameterNumber(start), false)
                .subscribe(block -> {
                    if (!block.getBlock().getTransactions().isEmpty()) {
                        List<EthBlock.TransactionResult> txs = web3j.ethGetBlockByNumber(DefaultBlockParameter.valueOf(block.getBlock().getNumber()), true).send().getBlock().getTransactions();
                        txs.forEach(tx ->
                        {
                            ObjectMapper objectMapper = new ObjectMapper();
                            try {
                                String json = objectMapper.writeValueAsString(((EthBlock.TransactionObject) tx.get()).get());
                                Document doc =  Document.parse(json);
                                Transactions.insertOne(doc);
                                System.out.println(json);
                            } catch (JsonProcessingException e) {
                                e.printStackTrace();
                            }
                        });
                    }
                });
    }



    public ArrayList<WalletFile> create_much_account (int count,String pwd) {
        ArrayList<WalletFile> walletFiles = new ArrayList<>();
        ECKeyPair ecKeyPair = null;
        WalletFile walletFile = null;
        for (int i = 0; i < count; i++) {
            try {
                ecKeyPair = Keys.createEcKeyPair();
                walletFile = Wallet.createLight(pwd, ecKeyPair);
                walletFiles.add(walletFile);
            } catch (Exception exception) {
                log.error(exception.toString());
            }
        }
        return walletFiles;
    }

    public void Give (ArrayList<Credentials> credentials,int count) throws Exception {

        credentials.forEach(credentials1 ->
        {
            TransactionReceipt transferReceipt = null;
            try {
                transferReceipt = Transfer.sendFunds(
                        web3j, rich,
                        credentials1.getAddress(),
                        BigDecimal.valueOf(count), Convert.Unit.WEI)
                        .send();
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            log.info("Transaction complete,"
                    + transferReceipt.getTransactionHash());
        });
    }


    public ArrayList<String> create_much_random_tx (ArrayList<Credentials> credentials,int count) throws Exception {
        Random rand = new Random();
        ArrayList<String> txHashes = new ArrayList<>();
        int randomWalletFrom = 0;
        int randomWalletTo = 0;
        ObjectMapper objectMapper = new ObjectMapper();
        for (int i = 0; i < count; i++)
        {
            randomWalletTo = rand.nextInt(credentials.size());
            randomWalletFrom = rand.nextInt(credentials.size());
            if (randomWalletFrom == randomWalletTo)
            {
                continue;
            }
            Credentials From = credentials.get(randomWalletFrom);
            Credentials To = credentials.get(randomWalletTo);
        log.info("Sending 1 Wei ("
                + Convert.fromWei("1", Convert.Unit.ETHER).toPlainString() + " Ether)");
        int cc = randomWalletFrom*10000;
        TransactionReceipt transferReceipt = Transfer.sendFunds(
                web3j, rich,
                To.getAddress(),
                BigDecimal.valueOf(cc), Convert.Unit.WEI)
                .send();
        log.info("Transaction complete,"
                + transferReceipt.getTransactionHash());
            txHashes.add(transferReceipt.getTransactionHash());
        }
        return txHashes;
    }

}
