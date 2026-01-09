# 说明
自动获取 edge 浏览器微软积分，完成浏览器和手机端的搜索任务

# 系统要求

- JDK17+
- 模拟用的 selenium 需要 JDK11+
- spring boot 3 需要 JDK17
- spring ai 需要 spring boot 3.4+
- 暂时无法做到降版本

# 使用说明

## Edge 账号配置

- `van.project.rewardAgent.edge-user-dir`为 Edge 浏览器用户数据目录，默认为`C:\Users\{user-name}\AppData\Local\Microsoft\Edge\User Data`。
- `van.project.rewardAgent.edge-user-profile`为 Edge 浏览器当前用户配置目录，默认为`Default`，也可能是类似`profile1`的命名方式
- 可以通过在 edge 浏览器地址栏输入 `edge://version/` 进行查看当前用户数据目录和配置位置，其中的“用户配置路径” 为对应的配置信息。

> <b style="color:#ff3333">注意：在浏览器的安全机制下，不允许浏览器登录账号的同时，edgeDriver 登录同样的账号，因此启动时需要关闭浏览器再启动，或者浏览器不登录对应账号</b>

## 获取搜索关键词方式

`van.project.rewardAgent.keywords-mode` 配置获取搜索关键词的方式，有 `local` 和 `deepseek` 两种。
- local: 通过预设的关键词进行搜索，目前尝试下来，隔天搜索同样内容也能获得积分
- deepseek: 通过deepseek获得关键词，需要同步配置API key `spring.ai.deepseek.api-key`。通过deepseek得到的关键词会缓存下来，记录在 `./keywords-from-ai-cache.json` 中，这份关键词有3天有效期，3天后重新获取 

# 待解决问题

1. 由于浏览器的安全机制不允许同时在2个客户端登录同样的账号，也就无法在浏览器打开并登录账号的情况下运行程序，如果在已打开多个标签页并且不想关闭的情况下，无法运行程序
   1. 尝试过复制用户数据文件夹的方法，没有效果