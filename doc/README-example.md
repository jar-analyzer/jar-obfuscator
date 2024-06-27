## 实战

**示例一**

我有一个 `JAVAFX` 项目
- 主类是 `com.n1ar4.gui.Main`
- 使用的 `fxml` 绑定的是 `com.n1ar4.controller.DemoController`

由于 `fxml` 中的绑定类和方法无法修改，所以 `controller` 类暂不能混淆

```xml
<AnchorPane fx:controller="com.n1ar4.controller.DemoController"/>
```

如果我想完全混淆，应该给出这样的配置

```yaml
# 混淆包名称
obfuscatePackage: [ com.n1ar4 ]
# 混淆根包名
rootPackages: [ com.n1ar4 ]
# 不要混淆 fxml 绑定的 controller
classBlackList: [ com.n1ar4.controller.DemoController ]
# 注意 javafx 的启动类 start 方法不能改名
methodBlackList: [ start.* ]
```

如果只混淆核心包 `com.n1ar4.core` 这样配置

```yaml
# 混淆包名称
obfuscatePackage: [ com.n1ar4.core ]
# 混淆根包名
rootPackages: [ com.n1ar4 ]
# 不要混淆 fxml 绑定的 controller
classBlackList: [ com.n1ar4.controller.DemoController ]
# 这时候不用特殊处理 javafx 启动类的问题了
methodBlackList: [ ]
```

以上根包名的配置意义：只分析根包名下的类之间的引用关系

**示例二**

我项目使用了 `Apache Log4j2` 等组件如果我混淆类名和方法名，会报错无法启动

于是我使用不修改引用的混淆手段

```yaml
enableEncryptString: true
stringAesKey: Y4SuperSecretKey # 注意必须16位的KEY
enableAdvanceString: true
advanceStringName: GIiIiLA # 随意取名

enableDeleteCompileInfo: true
enableXOR: true

enableJunk: true
junkLevel: 5
maxJunkOneClass: 2000 # 防止混淆太大超出方法最大限制
```

如果你想体验进一步更强的加密混淆，可以开启超级配置

```yaml
enableSuperObfuscate: true
superObfuscateKey: 4ra1n4ra1n4ra1n1 # 注意必须16位的KEY
superObfuscatePackage: me.n1ar4 # 需要 JVMTI 加密的包名
```

启动 `JAR` 时使用以下的参数即可

```shell
java -agentpath:/path/to/libdecrypter.dll=PACKAGE_NAME=me.n1ar4,KEY=4ra1n4ra1n4ra1n1 -jar test.jar
```

该命令将会在 `JVM` 启动时使用 `libdecrypter.dll` 库解密（该库文件会自动导出）

你可以自行修改 `native` 目录的 `C/ASM` 代码自定义加密解密，是程序更安全

**示例三**

我混淆出来的为什么报错打不开

解决：
- 控制变量逐个尝试和搭配，逐个参数测试是否正常运行
- 关闭 `enableMethodName` 配置后再测试
- 注意 `rootPackages` 和 `obfuscatePackage` 配置
- 如果以上方案都不行，最终请使用**不修改引用**的几个配置（参考上文）