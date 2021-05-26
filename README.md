# MissEvan 游戏 SDK 集成指南 V2.0.1

MissEvan 游戏 SDK 开发包（简称：SDK）主要用来向第三方应用程序提供便捷、安全以及可靠的登录、支付服务。本文主要描述 SDK 用户登录、支付接口的使用方法，供合作伙伴的开发者接入使用。

## 1. SDK 接入前准备

接入前期准备工作包括商户签约和密钥配置，已完成商户可略过。 需要获取的参数包括：

| 参数名称    | 参数说明                        |
| ----------- | ------------------------------- |
| server_id   | 分配给研发的区服 id             |
| merchant_id | 分配给研发的商户 id             |
| app_id      | 分配给研发的游戏 id             |
| app_key     | SDK 客户端与 SDK 服务器通信秘钥 |

这些参数请通过开放平台或与我方人员联系获取

- app_key 是客户端签名所使用的的 key，是 SDK 初始化时必填的参数

==重要提示：研发接入需根据不同 app_id 分配单独的服务器部署，基于平台游戏 app_id + 用户 uid 对应游戏内用户唯一 ID，以便区分用户。==

## 2. SDK 快速接入

### 2.1 基础环境

本教程适用于 AndroidStudio 开发工具。（请尽可能使用新版本的 AndroidStudio，Demo 使用的是 3.5）

### 2.2 SDK 导入

(1) 主 module 下新建 libs 文件夹，将 aar 文件放置到 libs 文件夹下
(2) 在主 module 的 build.gradle 配置文件中添加入下代码

```java
repositories {
    flatDir {
        dirs 'libs'
    }
}

dependencies {
    implementation fileTree(dir: 'libs', include: ['*.jar'])
    // 1. 导入 aar
    implementation fileTree(dir: 'libs', include: ['*.aar'])

    // 2. SDK 依赖了如下库
    implementation 'com.squareup.retrofit2:retrofit:2.5.0'
    implementation 'com.tencent.mm.opensdk:wechat-sdk-android-without-mta:5.4.0'
    implementation 'io.sentry:sentry-android:1.7.27'
    implementation 'org.slf4j:slf4j-nop:1.7.25'
}
```



### 2.3 修改 AndroidManifest.xml

#### 2.3.1 权限申请

**请将以下内容全部复制到 AndroidManifest.xml 的 manifest 标签下**

```xml
<uses-permission android:name="android.permission.INTERNET" />
<uses-permission android:name="android.permission.ACCESS_NETWORK_STATE" />
<uses-permission android:name="android.permission.ACCESS_WIFI_STATE" />
<uses-permission android:name="android.permission.READ_PHONE_STATE" />
<uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />
```

==**重要提示：Android 6.0 以后请对相应权限进行动态权限申请**==


#### 2.3.2 在 Application 中添加 Activity

**添加 Activity 前请注意以下内容**

1. 由于加入微信支付功能，接入时，需要在自己的包名下新建 wxapi 文件夹，然后在此文件夹下新建 WXPayEntryActivity，并且 WXPayEntryActivity 继承 SDK 中的 BaseWXEntryActivity，WXPayEntryActivity 不需要实现任何方法。最后在 AndroidManifest.xml 中声明此 Activity。（此部分可参照 Demo 项目）

**请将以下内容全部复制到 AndroidManifest.xml 的 application 标签下**

```xml
<activity
    android:name=".wxapi.WXPayEntryActivity"
    android:exported="true"
    android:launchMode="singleTop"/>
```

如果与 Demo 项目中存在差异，请以 Demo 项目中为准


## 3. SDK 初始化

### 3.1 SDK 初始化接口

初始化接口调用方式如下

```java
MissEvanSdk gameSdk = MissEvanSdk.initialize(boolean debug, Context c, String merchant_id, String app_id, String server_id, String app_key, InitCallbackListener initCallbackListener, ExitCallbackListener exitListener)
```

参数说明如下

| 参数名称             | 参数说明                                                     |
| -------------------- | ------------------------------------------------------------ |
| debug                | 是否打开 debug 模式，正式包必须关闭，值为 false              |
| c                    | Activity 实例                                                |
| merchant_id          | 商户 id，由平台提供                                          |
| app_id               | 每款应用在平台的唯一标识，由平台提供                         |
| server_id            | 我方分配的商户应用的区服 id，一般用来区分区服，如果游戏有多个区服，对应一个我方分配的区服 id，并在选择角色、区服后调用 notifyZone 接口传入我方的区服 id |
| app_key              | 商户应用的客户端密钥，请勿使用服务器端密钥                   |
| initCallbackListener | initCallbackListener 为 InitCallbackListener 的实例。InitCallbackListener 的回调方法有 onSuccess、onFailed。回调 onSuccess 方法表明初始化成功，回调 onFailed 方法表明初始化失败 |
| exitListener         | 当触发防沉迷功能时，会回调该接口，接入方需在该接口的 onExit 方法中释放资源，实现退出游戏的代码 |

- **注意：请保证收到初始化成功的回调后再调用登录接口以及其他接口**
- **注意：务必要在 exitListener 中实现游戏直接退出的代码**

### 3.2 接口调用方式

获取 MissEvanSdk 对象后，可以调用其中接口。每个接口方法的参数中都包含 CallbackListener 类型的监听器，用户需实现其中的3个方法：onSuccess、onFailed 和 onError。当调用执行完毕返回结果时，会根据返回状态的不同执行其中相应的方法。

**代码示例**

```java
MissEvanSdk.getInstance().login(new CallbackListener() {
    @Override
    public void onSuccess(Bundle success) {
        // 此处为操作成功时执行，返回值通过 Bundle 传回
        LogUtils.d("onSuccess");
        String uid = success.getString("uid");
    }
    @Override
    public void onFailed(MissEvanSdkError failed) {
        // 此处为操作失败时执行，返回值为 MissEvanSdkError 类型变量，其中包含 ErrorCode 和 ErrorMessage
        LogUtils.d("MainActivity onFailed\nErrorCode : " + failed.getErrorCode() + "\nErrorMessage : " + failed.getErrorMessage());
    }
    @Override
    public void onError(MissEvanSdkError error) {
        // 此处为操作异常时执行，返回值为 MissEvanSdkError 类型变量，其中包含 ErrorCode 和 ErrorMessage
        LogUtils.d("onError\nErrorCode : " + error.getErrorCode() + "\nErrorMessage : " + error.getErrorMessage());
    }
});
```

## 4. SDK 接口介绍

**接入前，建议运行 Demo apk，从而全面了解我方 SDK，接入的具体详情可以参考 Demo Project**

### 4.1 登录接口

```java
login(CallbackListener listener);
```

调用该方法，会打开猫耳游戏用户登录界面，引导用户输入用户名、密码完成登录过程，**需确保每次进入游戏前都调用**。

**注意：停服相关公告接口会在登录之前调用，停服维护期间不要调用 SDK 登录接口（若游戏有多个区服，停服维护的区服不可调用SDK接口，其他正常区服可调用）**

**参数：**

| 参数名称 | 参数说明                  |
| -------- | ------------------------- |
| listener | CallbackListener 回调监听 |

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key           | 含义         | 类型   | 样例                             |
| ------------- | ------------ | ------ | -------------------------------- |
| uid           | 用户 id      | String | 10001                            |
| username      | 用户名       | String | gameUserName                     |
| nickname      | 用户名昵称   | String | gameNickName                     |
| access_token  | 访问令牌     | String | fdae8922a3b3d06a4e40882ac9f37a7e |
| expire_times  | 会话过期时间 | long   | 1389262844                       |
| refresh_token | 刷新令牌     | String | 0d5ddfa364d51359e6243892bf0a965c |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

### 4.2 通知用户区服角色信息接口

```java
notifyZone(String server_id, String server_name, String role_id, String role_name, CallbackListener listener)
```

调用该方法来设置用户当前信息，用于支付校验

**参数：**

| 参数名称    | 参数说明                           |
| ----------- | ---------------------------------- |
| server_id   | 分配给研发的区服 id                |
| server_name | 分配给研发的区服名称               |
| role_id     | 用户在游戏内角色 id（游戏自己的）  |
| role_name   | 用户在游戏内角色名称（游戏自己的） |
| listener    | CallbackListener 回调监听          |

**返回结果：**
(1) 通知区服成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key  | 含义             | 类型   | 样例         |
| ---- | ---------------- | ------ | ------------ |
| tips | 通知区服提示内容 | String | 通知区服成功 |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

**注意：** 请在用户登录并选择角色以及服务器后调用，否则无法通过审核

### 4.3 用户创建角色接口

```java
createRole(String server_id, String server_name, String role_id, String role_name)
```

这个方法需要在用户创建角色成功时调用

**参数：**

| 参数名称    | 参数说明                           |
| ----------- | ---------------------------------- |
| server_id   | 分配给研发的区服 id                |
| server_name | 分配给研发的区服名称               |
| role_id     | 用户在游戏内角色 id（游戏自己的）  |
| role_name   | 用户在游戏内角色名称（游戏自己的） |

### 4.4 支付接口

```java
pay(Strin uid, int game_money, String subject, String body, String out_trade_no, int total_fee, long role_id, String role_name, String extension_info, String notify_url, String order_sign, OrderCallbackListener listener)
```

调用该方法，会打开平台支付页面，引导用户完成支付交易过程

支付之前请确认调用过 notifyZone 接口来设置当前区服信息，无需每次支付前都调用。**PS：该支付结果仅作为参考，真实结果请以服务器结果为准**

**参数：**

| 参数名称       | 参数说明                                                     |
| -------------- | ------------------------------------------------------------ |
| uid            | 猫耳平台用户的唯一标识                                       |
| game_money     | 游戏内货币，即本次交易购买的游戏内货币                       |
| subject        | 商品名称，如：金币（由于支付宝不支持特殊字符 % &，所以参数中不能包含 % &） |
| body           | 商品简单描述（参数中不能包含 % &）                           |
| out_trade_no   | 商户订单号，2~32 位字符，用于对账用                          |
| total_fee      | 本次交易金额，单位：分（注意，total_fee的值必须为整数，并且在1~100000之间) |
| role_id        | 用户在游戏内角色 id（游戏自己的）                            |
| role_name      | 用户在游戏内角色名称（游戏自己的）                           |
| extension_info | 支付接口的额外参数（请不要传入 emoji 等特殊字符），会在服务器异步回调中原样传回 |
| notify_url     | 异步通知地址，为空时，使用正式支付回调地址进行支付回调，否则使用该地址进行支付回调，本字段用于游戏线下测试时支付异步通知地址，游戏包上线前请将此字段设置为空（""） |
| order_sign     | 订单参数签名，请在服务端完成订单参数签名（参考服务端文档）   |
| listener       | CallbackListener 回调监听                                    |

**注意：为了保护游戏充值的安全性，此部分的签名操作一定要放在游戏服务端完成，否则暴露出游戏服务端secertkey会有很大风险。**

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 String 类型变量 out_trade_no，bs_trade_no（我方的订单号）

(2) 失败时执行 onFailed() 方法，返回 String 类型变量 out_trade_no 以及 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

- 错误码：7005。返回此异常的场景为，当 CP 进行支付时传入的 uid 与 SDK 本地存储的 uid 不同时 SDK 会返回 7005 的 errorCode
- 错误码：7004。返回此异常的场景为，新版微信支付过程中 SDK 通知发货失败，此时会返回 7004 的 error missevan sever 查单接口，也可以等待 missevan server 异步通知
- 错误码：-5。订单签名异常

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

### 4.5 账号失效监听接口

```java
gameSdk.setAccountListener(new AccountCallBackListener() {
    @Override
    public void onAccountInvalid() {
        // TODO 其他登出操作
        makeToast("账号已登出");
    }
});
```

此接口会在用户登录失效时调用，请在收到监听时进行相关登出操作，回到游戏登录界面

### 4.6 获取用户信息接口

```java
getUserInfo(CallbackListener listener)
```

调用该方法，如果用户已经登录且没有超时，则返回用户相关信息

**参数：**

| 参数名称 | 参数说明                  |
| -------- | ------------------------- |
| listener | CallbackListener 回调监听 |

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key           | 含义         | 类型   | 样例                             |
| ------------- | ------------ | ------ | -------------------------------- |
| uid           | 用户 id      | String | 10001                            |
| username      | 用户名       | String | gameUserName                     |
| nickname      | 用户名昵称   | String | gameNickName                     |
| access_token  | 访问令牌     | String | fdae8922a3b3d06a4e40882ac9f37a7e |
| expire_times  | 会话过期时间 | long   | 1389262844                       |
| refresh_token | 刷新令牌     | String | 0d5ddfa364d51359e6243892bf0a965c |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

### 4.7 判断用户是否登录接口

```java
isLogin(CallbackListener listener)
```

调用该方法，会返回用户是否登录

**参数：**

| 参数名称 | 参数说明                  |
| -------- | ------------------------- |
| listener | CallbackListener 回调监听 |

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key      | 含义     | 类型    | 样例  |
| -------- | -------- | ------- | ----- |
| is_login | 是否登录 | boolean | false |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

### 4.8 登出接口

```java
logout(CallbackListener listener)
```

调用该方法，会返回用户是否登出成功。**让玩家退出当前的账号信息**

**参数：**

| 参数名称 | 参数说明                  |
| -------- | ------------------------- |
| listener | CallbackListener 回调监听 |

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key  | 含义 | 类型   | 样例     |
| ---- | ---- | ------ | -------- |
| tips | 提示 | String | 注销成功 |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

### 4.9 退出接口

```java
exit(ExitCallbackListener exitListener)
```

调用该方法，会回调研发退出接口方法

**参数：**

| 参数名称     | 参数说明                      |
| ------------ | ----------------------------- |
| exitListener | ExitCallbackListener 回调监听 |

**exitListener：需在该接口的 onExit 方法中释放资源，关闭游戏**

**返回结果：**

调用该接口，弹出 SDK 退出框

### 4.10 判断当前用户是否实名认证接口

```java
isRealNameAuth(CallbackListener listener)
```

调用该方法，会返回用户是否实名认证

**参数：**

| 参数名称 | 参数说明                  |
| -------- | ------------------------- |
| listener | CallbackListener 回调监听 |

**返回结果：**
(1) 成功时执行 onSuccess() 方法，返回 Bundle 类型变量，其中包含键和值为：

| key              | 含义         | 类型    | 样例  |
| ---------------- | ------------ | ------- | ----- |
| is_realname_auth | 是否实名认证 | boolean | false |

(2) 失败时执行 onFailed() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段

(3) 错误时执行 onError() 方法，返回 MissEvanSdkError 类型变量，MissEvanSdkError 类中包含 errorCode 和 errorMessage 字段


## 5. SDK 返回值说明

### 5.1 返回值列表

| 参数名称      | 参数说明      | 类型    | 样例                             |
| ------------- | ------------- | ------- | -------------------------------- |
| result        | 结果状态      | int     | -1                               |
| uid           | 用户 id       | String  | 10001                            |
| username      | 用户名        | String  | gameUserName                     |
| nickname      | 用户名昵称    | String  | gameNickName                     |
| access_token  | 访问令牌      | String  | fdae8922a3b3d06a4e40882ac9f37a7e |
| expire_times  | 会话过期时间  | long    | 1389262844                       |
| refresh_token | 刷新令牌      | String  | 0d5ddfa364d51359e6243892bf0a965c |
| bs_trade_no   | 我方订单号    | String  | 20140101012348888                |
| out_trade_no  | CP 商户订单号 | String  | 20140101012345678                |
| is_login      | 登录状态      | boolean | false                            |

### 5.2 客户端状态代码

| 状态代码（result） | 参数说明 |
| ------------------ | -------- |
| 1                  | 操作成功 |
| -1                 | 操作失败 |

### 5.3 客户端错误代码

| 错误代码（errorCode） | 错误描述（errorMessage）                                    |
| --------------------- | ----------------------------------------------------------- |
| 1000                  | 支付失败                                                    |
| 1001                  | 用户取消交易                                                |
| 2001                  | 服务器返回数据异常                                          |
| 2002                  | 网络未连接                                                  |
| 3001                  | 注销失败                                                    |
| 3002                  | 未登录或者登录已过期                                        |
| 5701                  | 支付宝交易失败                                              |
| 5702                  | 微信支付失败                                                |
| 6001                  | 用户取消注册                                                |
| 6002                  | 用户取消登录                                                |
| 7003                  | 请检查是否安装新版微信客户端                                |
| 7004                  | 查单失败                                                    |
| 7005                  | uid 不统一,支付失败                                         |
| 8001                  | 支付前请先调用 nofiyZone 方法通知区服，并确保与支付参数相符 |
| 91001                 | 关闭登录                                                    |
| 200003                | 需要进行实名认证                                            |
| 200004                | 非法交易金额                                                |
| 500027                | 支付额度限制                                                |

## 6. 常见问题

### 6.1 Demo 相关

- 目前 demo 切换 API 环境需要先初始化 SDK 后再进行修改 BaseUrl，会出现多调用一次生产环境的接口的情况，可忽略，后续版本修复。
