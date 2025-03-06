# Jar-Obfuscator V2

<img alt="gitleaks badge" src="https://img.shields.io/badge/protected%20by-gitleaks-blue">

![](https://github.com/jar-analyzer/jar-obfuscator/workflows/maven%20check/badge.svg)
![](https://github.com/jar-analyzer/jar-obfuscator/workflows/leak%20check/badge.svg)
![](https://img.shields.io/badge/build-Java%208-orange)
![](https://img.shields.io/github/downloads/jar-analyzer/jar-obfuscator/total)
![](https://img.shields.io/github/v/release/jar-analyzer/jar-obfuscator)

[CHANGE LOG](CHANGELOG.MD)

`Jar Obfuscator V2` 是一个 `JAR` 文件混淆工具

- 命令行模式，简单易用
- 仅单个 `JAR` 文件小于 `1 MB` 超轻量
- 简洁的配置文件快速上手
- 输入 `JAR` 直接输出混淆后的 `JAR`

注意：目前是 `v2` 版本，如果你需要 `v1` 版本可以从 `release` 页面下载

## 开始

[前往下载](https://github.com/jar-analyzer/jar-obfuscator/releases/latest)

简单命令即可启动（第一次启动将自动生成配置文件）

```shell
java -jar jar-obfuscator.jar --jar test.jar --config config.yaml
```

jar-obfuscator-pro 和 jar-obfuscator-ultra 版本正在开发中 暂时不会公开

| 功能                      | jar-obfuscator 开源版 | jar-obfuscator-pro | jar-obfuscator-ultra |
|-------------------------|--------------------|--------------------|----------------------|
| 类名混淆（包含引用修改）            | ✅                  | ✅                  | ✅                    |
| 包名混淆（包含引用修改）            | ✅                  | ✅                  | ✅                    |
| 方法名混淆（包含引用修改）           | ✅                  | ✅                  | ✅                    |
| 字段名混淆（包含引用修改）           | ✅                  | ✅                  | ✅                    |
| 整方法内参数名混淆（包含引用修改）       | ✅                  | ✅                  | ✅                    |
| 删除编译调试信息（删除行号信息）        | ✅                  | ✅                  | ✅                    |
| 字符串加密运行时解密（使用 `AES` 加密） | ✅                  | ✅                  | ✅                    |
| 字符串提取数组混淆（访问数组方式得到字符串）  | ✅                  | ✅                  | ✅                    |
| 整型常数异或混淆（多重异或的加密）       | ✅                  | ✅                  | ✅                    |
| 垃圾代码花指令混淆（可指定多级别的混淆）    | ✅                  | ✅                  | ✅                    |
| 使用某些技巧可以在反编译时隐藏方法       | ✅                  | ✅                  | ✅                    |
| 使用某些技巧可以在反编译时隐藏字段       | ✅                  | ✅                  | ✅                    |

## 配置

```yaml
# jar obfuscator v2 配置文件
# jar obfuscator v2 by jar-analyzer team (4ra1n)
# https://github.com/jar-analyzer/jar-obfuscator

# 日志级别
# debug info warn error
logLevel: info

# 混淆字符配置
# 类名方法名等信息会根据字符进行随机排列组合
obfuscateChars:
  - "i"
  - "l"
  - "L"
  - "1"
  - "I"
# 不对某些类做混淆（不混淆其中的所有内容）
# 通常情况必须加入 main 入口
classBlackList:
  - "com.test.Main"
# 不对指定正则的类进行混淆
# 注意这里的类名匹配是 java/lang/String 而不是 java.lang.String
# 该配置和 classBlackList 同时生效
classBlackRegexList:
  - "java/.*"
  - "com/intellij/.*"
# 不对某些 method 名做混淆 正则
# visit.* 忽略 JAVA ASM 的 visitCode visitMethod 等方法
# start.* 忽略 JAVAFX 因为启动基于 start 方法
# 以此类推某些方法和类是不能混淆的（类继承和接口实现等）
methodBlackList:
  - "visit.*"
  - "start.*"

# 开启类名混淆
enableClassName: true
# 开启包名混淆
enablePackageName: true
# 开启方法名混淆
enableMethodName: true
# 开启字段混淆
enableFieldName: true
# 开启参数名混淆
enableParamName: true
# 开启数字异或混淆
enableXOR: true

# 开启加密字符串
enableEncryptString: true
# 加密使用 AES KEY
# 注意长度必须是 16 且不包含中文
stringAesKey: Y4SuperSecretKey
# 开启进阶字符串混淆
enableAdvanceString: true
# 进阶字符串处理参数
advanceStringName: GIiIiLA
# 字符串解密类名
decryptClassName: org.apache.commons.collections.list.AbstractHashMap
# 字符串解密方法名
decryptMethodName: newMap
# 字符串 AES KEY 名字
decryptKeyName: LiLiLLLiiiLLiiLLi

# 是否隐藏方法
enableHideMethod: true
# 是否隐藏字段
enableHideField: true

# 开启删除编译信息选项
enableDeleteCompileInfo: true

# 开启花指令混淆
enableJunk: true
# 花指令级别
# 最低1 最高5
# 使用 3 以上会生成垃圾方法
junkLevel: 5
# 一个类中的花指令数量上限
maxJunkOneClass: 2000

# 是否打印所有主函数
showAllMainMethods: true

# 是否保留临时类文件
keepTempFile: false
```