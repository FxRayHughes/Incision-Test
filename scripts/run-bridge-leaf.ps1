param(
    [string]$ServerDirectory = "E:\Minecraft-Server\incisionTest\run-26.2-leaf",
    [string]$Java = "D:\Java\zulu25.32.21-ca-jdk25.0.2-win_x64\bin\java.exe",
    [ValidateSet("jvmti", "instrumentation")]
    [string]$Backend = "jvmti",
    [int]$TimeoutMinutes = 8
)

$ErrorActionPreference = "Stop"

$projectRoot = Split-Path $PSScriptRoot -Parent
$mainJar = Join-Path $projectRoot "build\libs\Incision-Test-1.0.0.jar"
$peerJar = Join-Path $projectRoot "bridge-peer\build\libs\bridge-peer-1.0.0.jar"
$serverJar = Join-Path $ServerDirectory "leaf-26.2-42.jar"
$pluginDirectory = Join-Path $ServerDirectory "plugins"
$consoleLog = Join-Path $ServerDirectory "bridge-peer-console.log"
$latestLog = Join-Path $ServerDirectory "logs\latest.log"
$cacheRoot = Join-Path $ServerDirectory "cache\taboolib"
$localIncisionJar = Join-Path $env:USERPROFILE ".m2\repository\io\izzel\taboolib\incision\6.3.0-local-dev\incision-6.3.0-local-dev.jar"
$localIncisionSha1 = "$localIncisionJar.sha1"

foreach ($required in @($Java, $mainJar, $peerJar, $serverJar, $localIncisionJar)) {
    if (-not (Test-Path -LiteralPath $required)) {
        throw "Required file not found: $required"
    }
}

# publishToMavenLocal 不更新历史 checksum sidecar；保留错误 sha1 会让 PrimitiveLoader 下载成功后又判定损坏。
if (Test-Path -LiteralPath $localIncisionSha1) {
    $actualSha1 = (Get-FileHash -LiteralPath $localIncisionJar -Algorithm SHA1).Hash.ToLowerInvariant()
    $declaredSha1 = (Get-Content -LiteralPath $localIncisionSha1 -Raw).Trim().ToLowerInvariant()
    if ($actualSha1 -ne $declaredSha1) {
        Remove-Item -LiteralPath $localIncisionSha1 -Force
    }
}

# 两个 jar 必须同时复制，缺任意一个都会把双 ClassLoader 测试退化成普通单插件测试。
Copy-Item -LiteralPath $mainJar -Destination (Join-Path $pluginDirectory "Incision-Test-1.0.0.jar") -Force
Copy-Item -LiteralPath $peerJar -Destination (Join-Path $pluginDirectory "Incision-Bridge-Peer-1.0.0.jar") -Force

# local-dev 版本号固定，必须清除旧内容寻址副本；范围只限本测试服务端的 Incision 模块缓存。
if (Test-Path -LiteralPath $cacheRoot) {
    $resolvedServer = (Resolve-Path -LiteralPath $ServerDirectory).Path.TrimEnd('\')
    $resolvedCache = (Resolve-Path -LiteralPath $cacheRoot).Path
    if (-not $resolvedCache.StartsWith("$resolvedServer\", [StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to clean cache outside server directory: $resolvedCache"
    }
    Get-ChildItem -LiteralPath $resolvedCache -Recurse -Filter "incision-6.3.0-local-dev-*.jar" -File |
        Remove-Item -Force
}

$startInfo = [System.Diagnostics.ProcessStartInfo]::new()
$startInfo.FileName = $Java
$startInfo.WorkingDirectory = $ServerDirectory
$startInfo.UseShellExecute = $false
$startInfo.RedirectStandardInput = $true
$startInfo.RedirectStandardOutput = $true
$startInfo.RedirectStandardError = $true
$startInfo.CreateNoWindow = $true
$startInfo.ArgumentList.Add("-Xms512M")
$startInfo.ArgumentList.Add("-Xmx1536M")
$startInfo.ArgumentList.Add("-Dfile.encoding=UTF-8")
$startInfo.ArgumentList.Add("-Dtaboolib.incision.backend=$Backend")
$startInfo.ArgumentList.Add("-Djdk.attach.allowAttachSelf=true")
$startInfo.ArgumentList.Add("-XX:+EnableDynamicAgentLoading")
$startInfo.ArgumentList.Add("-jar")
$startInfo.ArgumentList.Add($serverJar)
$startInfo.ArgumentList.Add("nogui")

$process = [System.Diagnostics.Process]::new()
$process.StartInfo = $startInfo
Set-Content -LiteralPath $consoleLog -Value "" -Encoding UTF8
$started = $false
$stdoutTask = $null
$stderrTask = $null

try {
    if (-not $process.Start()) { throw "Failed to start Leaf" }
    $started = $true
    # 异步排空管道避免服务端日志填满缓冲区；完成判定读取 Bukkit 自己维护的 latest.log。
    $stdoutTask = $process.StandardOutput.ReadToEndAsync()
    $stderrTask = $process.StandardError.ReadToEndAsync()
    $deadline = [DateTime]::UtcNow.AddMinutes($TimeoutMinutes)
    $completed = $false
    while ([DateTime]::UtcNow -lt $deadline -and -not $process.HasExited) {
        Start-Sleep -Milliseconds 250
        $snapshot = if (Test-Path -LiteralPath $latestLog) { Get-Content -LiteralPath $latestLog -Raw } else { "" }
        if ($snapshot -match "bridge-peer-disable-isolation" -and $snapshot -match "结果: .* fail") {
            $completed = $true
            break
        }
    }
    if (-not $process.HasExited) {
        $process.StandardInput.WriteLine("stop")
        $process.StandardInput.Flush()
        if (-not $process.WaitForExit(30000)) {
            # 测试结论已落盘后只负责回收本脚本启动的进程，避免失效 stdin 让矩阵永久挂起。
            $process.Kill($true)
            $process.WaitForExit()
        }
    }
    $output = if (Test-Path -LiteralPath $latestLog) { Get-Content -LiteralPath $latestLog -Raw } else { "" }
    $consoleOutput = $stdoutTask.GetAwaiter().GetResult() + $stderrTask.GetAwaiter().GetResult()
    Set-Content -LiteralPath $consoleLog -Value $consoleOutput -Encoding UTF8
    if (-not $completed) { throw "Bridge test did not complete within timeout; see $consoleLog" }
    if ($output -match "FAIL bridge-peer-" -or $output -match "dispatch unavailable") {
        throw "Bridge test failed; see $consoleLog"
    }
    if ($output -notmatch "PASS bridge-peer-dual-dispatch" -or $output -notmatch "PASS bridge-peer-disable-isolation") {
        throw "Bridge PASS markers missing; see $consoleLog"
    }
    Write-Host "Bridge peer lifecycle PASS: $consoleLog"
} finally {
    if ($started -and -not $process.HasExited) {
        $process.Kill($true)
        $process.WaitForExit()
    }
    $process.Dispose()
}
