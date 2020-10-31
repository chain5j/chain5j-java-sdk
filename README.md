# Chain5j RPC API 文档

[TOC]

## 账户

### 账户信息

两种格式

1. 不包含域名: 长度3 ~ 32字节， 由字母、数字、下划线组成， 不区分大小写。以小写字母存储。
2. 包含域名; 格式为user@domain。
   + user格式同1
   + domain 长度3~ 64字节。由字母、数字、下划线组成， 不区分大小写。以小写字母存储。

区块链上账户的存储信息

```
type AccountStore struct {
	Nonce     uint64                          `json:"nonce"` //账户的nonce值
	Balance   *big.Int                        `json:"balance"` //账户余额
	Addresses map[types.Address]*AddressStore `json:"addresses"` // 账户地址

	CN     string `json:"cn"`     // 用户名称 common name
	Domain string `json:"domain"` // 所在域

	IsAdmin        bool         `json:"isAdmin"`               // 是否为管理员
	DeployContract bool         `json:"deployContract"`        // 是否允许部署合约
	Permissions    *Permissions `json:"permissions" rlp:"nil"` // 管理员权限

	IsFrozen bool `json:"isFrozen"` // 账户是否被冻结

	XXX map[string][]byte `json:"xxx"` // 扩展字段
}

// Permissions 用户权限
type Permissions struct {
	RegisterUser      bool `json:"registerUser"`      // 是否允许注册用户
	UpdateUser        bool `json:"updateUser"`        // 是否允许更新用户权限
	FrozenUser        bool `json:"frozenUser"`        // 是否允许冻结用户
	RegisterDomain    bool `json:"registerDomain"`    // 是否允许建立新的域名
	RegisterSubdomain bool `json:"registerSubDomain"` // 是否允许建立子域
}
```



### 查询账户信息

​	1、参数

- `String` - 账户名称， 如user1@chain5j.com
- `QUANTITY|TAG` - 整数块编号，或者字符串"latest", "earliest" 或 "pending"

2. 返回值

   账户信息

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data {"method":"accounts_accountInfo","params":["admin@chain5j.com", "latest"],"id":1}
响应：
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "nonce": 1,
        "balance": 340282366920938463463374607431768111456,
        "addresses": {
            "0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7": {}
        },
        "cn": "admin",
        "domain": "chain5j.com",
        "isAdmin": true,
        "deployContract": true,
        "permissions": {
            "registerUser": true,
            "updateUser": true,
            "frozenUser": true,
            "registerDomain": false,
            "registerSubDomain": true
        },
        "isFrozen": false,
        "xxx": {}
    }
}
```

## App

### 获取账户余额

​	1、参数

- `DATA` - 20字节，地址
- `QUANTITY|TAG` - 整数块编号，或者字符串"latest", "earliest" 或 "pending"

2. 返回值

   `QUANTITY` - 当前余额，单位：wei

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data '{"method":"apps_getBalance","params":["0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7", "latest"],"id":1}' 
响应：
{"jsonrpc":"2.0","id":1,"result":"0x2000000000000000"}
```

### 获取地址nonce

1、参数

- `DATA` - 20字节，地址
- `QUANTITY|TAG` - 整数块编号，或者字符串"latest", "earliest" 或 "pending"

2. 返回值

   `QUANTITY` - 从指定地址发出的交易数量，整数

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data '{"method":"apps_getTransactionCount","params":["0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7", "latest"],"id":1}' 
响应：
{"jsonrpc":"2.0","id":1,"result":"0x1"}
```

## 交易

### 构造交易

1. 查询交易数量 apps_getTransactionCount

2. 构造原始交易

   ```
   let rawTx = {
     from: 账户名称，
     to:  账户名称, 
     interpreter：解析器（）
     nonce: '0x30', // Replace by nonce for your account on bcos node
     gasLimit: '0x30000',
     gasPrice: '0x0', 
     value: '0x100000000',
     input: 0x0,
     deadline: 0, 交易有效时间
     signature: nil
     extra: nil
   };
   
   ```

   `Interpreter`字段可包含如下内容

   + "chain5j.base"
   + "chain5j.account"
   + "chain5j.lost"
   + "chain5j.evm"
   + "chain5j.ca"
   + "chain5j.poe"
   + "chain5j.ethereum"

3. 对rawTx进行rlp编码，

4. 用chain5j_sendRawTransaction发送数据

### 发送交易

发送签名交易。

1. 参数

   `Interger` - 交易类型。0，状态模型交易。 1， UTXO类型交易

   `DATA` - 签名的交易数据

2. 返回值

   ``DATA` - 32字节，交易哈希

   示例

   ```
   请求：
   curl -H "Content-Type:application/json" -X POST --data '{"method":"chain5j_sendRawTransaction","params":[0,"签名后的交易数据"],"id":1}'
   响应:
   {
     "id":1,
     "jsonrpc": "2.0",
     "result": "0xe670ec64341771606e55d6b4ca35a1a6b75ee3d5145a99d05921026d1527331"
   }
   ```

### 查询交易

1. 参数

   `DATA`, 32 字节 - 交易哈希

2. 返回值

   `Object` - 交易对象，如果没有找到匹配的交易则返回null。结构如下：

   - hash: DATA, 32字节 - 交易哈希
   - nonce: - 本次交易之前发送方已经生成的交易数量
   - blockHash: DATA, 32字节 - 交易所在块的哈希
   - blockNumber: QUANTITY - 交易所在块的编号
   - transactionIndex: QUANTITY - 交易在块中的索引位置
   - type: 交易类型
   - from: DATA, 20字节 - 交易发送方地址
   - to: DATA, 20字节 - 交易接收方地址，对于合约创建交易，该值为null
   - value: QUANTITY - 发送的以太数量，单位：wei
   - gasPrice: QUANTITY - 发送方提供的gas价格，单位：wei
   - gas: QUANTITY - 发送方提供的gas可用量
   - input: DATA - 交易发送的数据

3. 示例：

4. 

   ```
   请求：
   curl -H "Content-Type:application/json"  -X POST --data 
   '{"method":"chain5j_getTransaction","params":["0x0fc6fe77d78745532e35616fd692dc01a789d9e5596e53f18b59524cb8eeaf1a"],"id":1}'
   响应：
   {
       "jsonrpc": "2.0",
       "id": 1,
       "result": {
           "blockHash": "0x6f3c7aed873607f4c89c3a1ace8be13447e026f90c72adee89c3a8fed3d0692d",
           "blockNumber": "0x5f",
           "transactionIndex": "0x0",
           "type": 0,
           "transaction": {
               "from": "0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7",
               "to": "0xed9d02e382b34818e88b88a309c7fe71e65f419d",
               "nonce": 0,
               "gasLimit": 2000000,
               "gasPrice": 0,
               "value": "0x0",
               "input": "0x01",
               "deadline": 0,
               "signature": "0xe9e871ae8ff60ef74eb66c5e31a08ce2aa4cd6a34f04f589082bfa956ba28e9877fae653a3af6b4fe7f4642f68bdde462a392f44cea98f0370095e56f954a25701",
               "extra": "0x02",
               "hash": "0x0fc6fe77d78745532e35616fd692dc01a789d9e5596e53f18b59524cb8eeaf1a"
           }
       }
   }
   ```



### 用户操作（注册、更新、冻结）

通过发送交易注册用户。

From     string        `json:"from" `         // 域管理员。
To       string        `json:"to"  rlp:"nil"` // 增加或者删除的账户。
Interpreter string     `json:"interpreter"`   // "chain5j.account"

input 为如下结构的rlp编码。

```
type AccountTxData struct {
	Operation AccountOp //操作类型
	Data []byte 
}

const (
	RegisterAcountOp AccountOp = iota
	FrozenAccountOp
	UpdatePermissionOp

)
```

AccountTxData的Data封装

+ 注册。AccountStore的rlp编码。

```
type AccountStore struct {
	Nonce     uint64                          `json:"nonce"`
	Balance   *big.Int                        `json:"balance"`
	Addresses map[types.Address]*AddressStore `json:"addresses"`

	CN     string `json:"cn"`     // 用户名称 common name
	Domain string `json:"domain"` // 所在域

	IsAdmin        bool         `json:"isAdmin"`               // 是否为管理员
	DeployContract bool         `json:"deployContract"`        // 是否允许部署合约
	Permissions    *Permissions `json:"permissions" rlp:"nil"` // 管理员权限。只有管理员需要设置

	IsFrozen bool `json:"isFrozen"` // 账户是否被冻结

	XXX map[string][]byte `json:"xxx"` // 扩展字段
}
```

+ 冻结用户 FrozenAccountData的rlp编码

```
type FrozenAccountData struct {
	CN     string
	Domain string
	Frozen bool
}
```

+ 更新用户权限 UpdatePermissionData的rlp编码

```
type UpdatePermissionData struct {
	CN          string
	Domain      string
	Permissions Permissions
}
```



## 交易池

### 交易池状态

1. 参数

   无

2. 返回值

   ​	count:  交易数量

   ​	pending: 交易池中交易状态

3. 示例

```
请求：
curl -H "Content-Type:application/json"  -X POST --data 
'{"method":"chain5j_getTransaction","params":["0x0fc6fe77d78745532e35616fd692dc01a789d9e5596e53f18b59524cb8eeaf1a"],"id":1}'
响应:
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "count": 2,
        "pending": {
            "0x92C8Cae42A94045670cbb0Bfcf8f790D9F8097e7": {
                "pending": [
                    {
                        "nonce": 1,
                        "hash": "0x5bca783a0a6be240c2b62738c15b5ef51ac593e2e691202cebd81366131f3d34"
                    },
                    {
                        "nonce": 2,
                        "hash": "0xa0becad242257581106d36d2d808ab72950c7f3e8ee57bd788ea68c41c7555c3"
                    }
                ],
                "count": 2
            }
        }
    }
}
```



## 区块

### 查询区块高度

返回最新块的高度。

1. 参数

   无

2. 返回值

   `QUANTITY` - 节点当前块编号

3. 示例

   ```
   请求
   curl -H "Content-Type:application/json" -X POST --data '{"method":"chain5j_blockHeight","params":[],"id":1}'
   响应
   {"jsonrpc":"2.0","id":1,"result":"0x124"}
   ```

### 根据区块高度查询区块

返回指定编号的块。

1. 参数

   - `QUANTIT` - 整数块编号

2. 返回值

   `Object` - 匹配的块对象，如果未找到块则返回null，结构如下：

   + consensus: 共识数据
   + extraData: DATA - 块额外数据
   + gasLimit: QUANTITY - 本块允许的最大gas用量
   + gasUsed: QUANTITY - 本块中所有交易使用的总gas用量

   -  height: "0x1",
   - hash: DATA, 32 Bytes - 块哈希
   - parentHash: DATA, 32 Bytes - 父块的哈希
   - size: QUANTITY - 本块字节数
   - stateRoot: DATA, 32 Bytes - 块最终状态树的根节点
   - timestamp: QUANTITY - 块时间戳
   - transactions: Array - 交易对象数组，或32字节长的交易哈希数组
   - transactionsRoot: DATA, 32 Bytes - 块中的交易树根节点

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data '{"method":"chain5j_getBlockByNumber","params":[1],"id":1}' 
响应
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "consensus": "0xf88d80f886b841af58f1a799dba5df06a9272f193260624b0fd5c7698c76de3fc37663ee43316b6a871f4ba4fdb6ef5c8b91ad97916511319f4ff19daddf6de6a2360a1b9783ae01b84123648b377e30797659867c1ed439d5d26b954096edf00f77eadf7fed1143f80f10cde4cda41b6ba4696312c3151062284982d59a804e5bc2d0f8ecc5b2e86d7601c0c0c0c0",
        "extraData": "0xeaa356b01f539d17ae4541d95305dae7ad394e14fb53718d736dd56568b0789d255c5b33f805520ce53c4b35cf07a49a76e01f323e96a0075d4151b72a7d8e8501",
        "gasLimit": "0x47e7c4",
        "gasUsed": "0x0",
        "hash": "0x65611499a2e4c5be0193053051b90c11cfc29a8ffc5cf78efaf0b50c2088a538",
        "height": "0x1",
        "parentHash": "0xade0fb417eb8259fd625dfc585ba67c7b23de3ec9aae891a16e67726c852c2d3",
        "size": "0x14e",
        "stateRoot": "4+KAoNCd58MbQxQwy6NaUy2S3wCcsq1OTlThwBXD1WtX1fPG",
        "timestamp": "0x5e352d80",
        "transactions": [],
        "transactionsRoot": "0x56e81f171bcc55a6ff8345e692c0f86e5b48e01b996cadc001622fb5e363b421"
    }
}
```

### 根据区块hash查询区块

返回具有指定哈希的块。

1. 参数

- `DATA`, 32字节 - 块哈希

2. 返回值

   同chain5j_getBlockByNumber

3. 示例

   ```
   {"method":"chain5j_getBlockByHash","params":["0x4c6615c20a96969c4bb460bdaec6952595d271f526d430d79417477a68f5e5f7"],"id":1}
   ```

## 节点信息

### 节点信息

1. 参数

   无

2. 返回值

   id : 当前节点标识

    neturl: 节点p2p网络标识

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data '{"method":"node_info","params":[],"id":1}' 
响应：
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "id": "QmQWp6JpawMuXDZLqaiRoakAFHHeYRPTQDG6CmjbfbAXwH",
        "neturl": "/ip4/127.0.0.1/tcp/8888/p2p/QmQWp6JpawMuXDZLqaiRoakAFHHeYRPTQDG6CmjbfbAXwH"
    }
}
```

### 已连接节点信息

1. 参数

   无

2. 返回值

   p2p链接节点信息

   id : 当前节点标识

    neturl: 节点p2p网络标识

   connected: 是否已经连接

3. 示例

```
请求：
{"method":"node_peers","params":[],"id":1}' 
响应：
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "QmeGbkX3NGEaG9Xxadbt5WEy768T8xEjLw24f6vwVDLPKE": {
            "id": "QmeGbkX3NGEaG9Xxadbt5WEy768T8xEjLw24f6vwVDLPKE",
            "neturl": "/ip4/127.0.0.1/tcp/8889/p2p/QmeGbkX3NGEaG9Xxadbt5WEy768T8xEjLw24f6vwVDLPKE",
            "connected": true
        }
    }
}
```

## 节点管理

### 添加节点

1. 参数

   url:  需要添加的网络节点

2. 返回值

   错误消息

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data 
{"method":"admin_addPeer","params":["/ip4/127.0.0.1/tcp/8889/p2p/QmeGbkX3NGEaG9Xxadbt5WEy768T8xEjLw24f6vwVDLPKE"],"id":1}
响应：
{"jsonrpc":"2.0","id":1,"result":null}

```

### 删除节点

1. 参数

   url:  需要删除的网络节点标识

2. 返回值

   错误消息

3. 示例

```
请求：
curl -H "Content-Type:application/json" -X POST --data 
’{"method":"admin_dropPeer","params":["QmeGbkX3NGEaG9Xxadbt5WEy768T8xEjLw24f6vwVDLPKE"],"id":1}‘
响应：
{"jsonrpc":"2.0","id":1,"result":null}
```

## 共识

### 获取共识配置快照

1. 参数

   `QUANTIT` - 整数块编号

2. 返回快照信息

3. 示例

```
请求：
{"method":"pbft_getSnapshot","params":[1],"id":1}

响应：
{
    "jsonrpc": "2.0",
    "id": 1,
    "result": {
        "epoch": 30000,
        "number": 1,
        "hash": "0xa3acbd8f76d4bf304e46e9a2bc67d3ce024a36fa698b0dac37f53368798fbb56",
        "validators": [
            "QmQWp6JpawMuXDZLqaiRoakAFHHeYRPTQDG6CmjbfbAXwH"
        ],
        "managers": {
            "0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7": {}
        },
        "vvotes": [],
        "mvotes": [],
        "mtally": {},
        "vtally": {},
        "policy": 0
    }
}
```

### 添加/删除管理员提议

1. 参数

   + 地址： 投票增加/删除的管理员地址
   + bool类型， 增加/删除
   + 投票过期区块号
   + 签名

   ```
   签名数据格式
   enc, _ := rlp.EncodeToBytes([]interface{}{
   		"propose",
   		address,
   		auth,
   		deadline.ToInt(),
   	})
   ```

2. 返回值

   错误信息

3. 示例

   ```
   {"method":"pbft_proposeManager","params":["0x92c8cae42a94045670cbb0bfcf8f790d9f8097e7", true, "0x1000", "签名数据"],"id":1}
   
   ```

### 添加共识节点提议

1. 参数

   + id： 投票增加/删除的节点标识
   + bool类型， 增加/删除
   + 投票过期区块号
   + 签名

   ```
   签名数据格式
   enc, _ := rlp.EncodeToBytes([]interface{}{
   		"propose",
   		id, 
   		auth,
   		deadline.ToInt(),
   	})
   ```

2. 返回值

   错误信息

3. 示例

   ```
   {"method":"ProposeValidator","params":["QmQWp6JpawMuXDZLqaiRoakAFHHeYRPTQDG6CmjbfbAXwH", true, "0x1000", "签名数据"],"id":1}
   
   ```
