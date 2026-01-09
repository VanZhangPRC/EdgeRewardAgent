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

# 附录
## Windows 环境下切换JDK环境方式

### Jabba

使用第三方工具 [Jabba](https://github.com/shyiko/jabba)

### Batch 脚本

自己编写Batch脚本进行管理

```batch
@echo off
setlocal EnableExtensions EnableDelayedExpansion

:: ==================================================
:: JDK 映射表（在这里维护即可）
:: ==================================================
:: 格式：
::   set JDK_<name>=<absolute_path>
::
:: 示例：
set JDK_jdk8=C:\ProgramFiles\Java\jdk1.8.0_211
set JDK_jdk11=C:\ProgramFiles\Java\openjdk-11.0.0.1_windows-x64_bin\jdk-11.0.0.1
set JDK_jdk17=C:\ProgramFiles\Java\openjdk-17+35_windows-x64_bin\jdk-17
set JDK_jdk24=C:\ProgramFiles\Java\openjdk-24_windows-x64_bin\jdk-24

:: ==================================================
:: 参数校验
:: ==================================================
if "%~1"=="" (
    echo Usage: switchjdk jdk8 ^| jdk11 ^| jdk17 ^| jdk24
    exit /b 1
)

set "JDK_KEY=JDK_%~1"

:: ==================================================
:: 从映射表取路径
:: ==================================================
for %%A in (!JDK_KEY!) do set "JDK_PATH=!%%A!"

if not defined JDK_PATH (
    echo [ERROR] Unknown JDK key: %~1
    echo.
    echo Available options:
    echo   jdk8
    echo   jdk11
    echo   jdk17
    echo   jdk24
    exit /b 1
)

:: ==================================================
:: 校验 JDK 是否有效
:: ==================================================
if not exist "%JDK_PATH%\bin\java.exe" (
    echo [ERROR] Invalid JDK path:
    echo   %JDK_PATH%
    exit /b 1
)

:: ==================================================
:: 设置系统环境变量
:: ==================================================
setx JAVA_HOME "%JDK_PATH%" /M >nul

:: ==================================================
:: 输出结果
:: ==================================================
echo ----------------------------------------
echo JAVA_HOME switched to:
echo   %JDK_PATH%
echo ----------------------------------------
echo.
echo NOTE:
echo   1. Effective in NEW terminals only
echo   2. Run CMD as Administrator
echo ----------------------------------------

endlocal
```

将脚本命名为 `switchjdk`，在管理员模式下的 cmd 中运行 `switchjdk jdk17` 可以设置`JAVA_HOME`环境变量，
从而达到切换JDK环境的目的。**需要注意，环境变量设置后，需要新打开命令行窗口才能生效**
