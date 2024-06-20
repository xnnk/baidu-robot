### 前言

[千帆大模型文档](https://cloud.baidu.com/doc/WENXINWORKSHOP/index.html)

该项目使用了千帆大模型平台的ERNIE模型

其中还有一个简单的用户匹配模块

可以将此项目的对话模块进行自定义

### 初步启动项目需要注意的内容

1. 在`application.yml`文件中的`gpt.SK`和`gpt.AK`切换成自己在百度智能云中的SK和AK

2. 在`TokenCacheListener.java`文件中修改你自己的clientId和clientSecret

* 如何创建SK和AK, 详细内容请翻阅百度智能云API介绍文档[https://cloud.baidu.com/doc/WENXINWORKSHOP/s/flfmc9do2](https://cloud.baidu.com/doc/WENXINWORKSHOP/s/flfmc9do2#api-%E8%B0%83%E7%94%A8%E6%B5%81%E7%A8%8B%E7%AE%80%E4%BB%8B)

### 项目自定义

1. 该项目的`AuthStringUtil.java`是根据百度智能云的鉴权认证机制中的生成认证字符串文档编写的鉴权字符串生成工具, 大部分都不需要变动
[https://cloud.baidu.com/doc/WENXINWORKSHOP/s/flfmc9do2](https://cloud.baidu.com/doc/Reference/s/njwvz1yfu#%E7%BC%96%E7%A0%81%E7%94%9F%E6%88%90%E6%AD%A5%E9%AA%A4)

2. cache目录中的缓存形式可以复用, 创建Listener和Supplier即可复用创造新的缓存

### 最后
