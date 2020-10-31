package com.github.chain5j.protocol.core.methods.response;

import java.io.IOException;
import java.math.BigInteger;
import java.util.List;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.ObjectReader;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import com.google.gson.Gson;
import com.github.chain5j.protocol.ObjectMapperFactory;
import com.github.chain5j.protocol.core.Response;
import com.github.chain5j.utils.Numeric;

public class BlockInfo extends Response<BlockInfo.Block> {

    @Override
    @JsonDeserialize(using = BlockInfo.ResponseDeserialiser.class)
    public void setResult(Block result) {
        super.setResult(result);
    }

    public Block getBlock() {
        return getResult();
    }

    public static class Block {
        private String hash;
        private String parentHash;
        private String height;
        private String stateRoot;
        private String transactionsRoot;
        private String timestamp;
        private String gasLimit;
        private String gasUsed;
        private String consensusName;
        private String consensus;
        private String extra;
        private List<TransactionObject> transactions;

        public Block() {
        }

        public String getParentHash() {
            return parentHash;
        }

        public void setParentHash(String parentHash) {
            this.parentHash = parentHash;
        }

        public BigInteger getHeight() {
            return Numeric.decodeQuantity(height);
        }

        public String getHeightRaw() {
            return height;
        }

        public void setHeight(String height) {
            this.height = height;
        }

        public String getStateRoot() {
            return stateRoot;
        }

        public void setStateRoot(String stateRoot) {
            this.stateRoot = stateRoot;
        }

        public String getTransactionsRoot() {
            return transactionsRoot;
        }

        public void setTransactionsRoot(String transactionsRoot) {
            this.transactionsRoot = transactionsRoot;
        }

        public BigInteger getTimestamp() {
            return Numeric.decodeQuantity(timestamp);
        }

        public String getTimestampRaw() {
            return timestamp;
        }

        public void setTimestamp(String timestamp) {
            this.timestamp = timestamp;
        }

        public String getGasLimit() {
            return gasLimit;
        }

        public void setGasLimit(String gasLimit) {
            this.gasLimit = gasLimit;
        }

        public String getGasUsed() {
            return gasUsed;
        }

        public void setGasUsed(String gasUsed) {
            this.gasUsed = gasUsed;
        }

        public String getConsensusName() {
            return consensusName;
        }

        public void setConsensusName(String consensusName) {
            this.consensusName = consensusName;
        }

        public String getConsensus() {
            return consensus;
        }

        public void setConsensus(String consensus) {
            this.consensus = consensus;
        }

        public String getExtra() {
            return extra;
        }

        public void setExtra(String extra) {
            this.extra = extra;
        }

        public String getHash() {
            return hash;
        }

        public void setHash(String hash) {
            this.hash = hash;
        }

        public List<TransactionObject> getTransactions() {
            return transactions;
        }

        public void setTransactions(List<TransactionObject> transactions) {
            this.transactions = transactions;
        }

        public String toJsonString() {
            Gson gson = new Gson();
            return gson.toJson(this);
        }

        @Override
        public String toString() {
            return toJsonString();
        }
    }



    public static class ResponseDeserialiser extends JsonDeserializer<Block> {

        private ObjectReader objectReader = ObjectMapperFactory.getObjectReader();

        @Override
        public Block deserialize(
                JsonParser jsonParser,
                DeserializationContext deserializationContext) throws IOException {
            if (jsonParser.getCurrentToken() != JsonToken.VALUE_NULL) {
                return objectReader.readValue(jsonParser, Block.class);
            } else {
                return null;  // null is wrapped by Optional in above getter
            }
        }
    }
}
