package com.kau.minseop.pointshare.gas;

/**
 * Created by minseop on 2018-06-07.
 */
import java.math.BigInteger;

import org.web3j.tx.Contract;
import org.web3j.tx.ManagedTransaction;

public class DefaultGasProvider extends StaticGasProvider {
    public static final BigInteger GAS_LIMIT = Contract.GAS_LIMIT;
    public static final BigInteger GAS_PRICE = ManagedTransaction.GAS_PRICE;

    public DefaultGasProvider() {
        super(GAS_PRICE, GAS_LIMIT);
    }
}
