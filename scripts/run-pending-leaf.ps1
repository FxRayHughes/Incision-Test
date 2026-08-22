param(
    [string]$ServerDirectory = "E:\Minecraft-Server\incisionTest\run-26.2-leaf",
    [string]$Java = "D:\Java\zulu25.32.21-ca-jdk25.0.2-win_x64\bin\java.exe",
    [int]$TimeoutMinutes = 5
)

$ErrorActionPreference = "Stop"
$projectRoot = Split-Path $PSScriptRoot -Parent
$pluginDirectory = Join-Path $ServerDirectory "plugins"
$mainJar = Join-Path $projectRoot "build\libs\Incision-Test-1.0.0.jar"
$serverJar = Join-Path $ServerDirectory "leaf-26.2-42.jar"
$peerJar = Join-Path $pluginDirectory "Incision-Bridge-Peer-1.0.0.jar"
$peerBackup = "$peerJar.pending-test-disabled"
$latestLog = Join-Path $ServerDirectory "logs\latest.log"
$consoleLog = Join-Path $ServerDirectory "pending-load-console.log"
$cacheRoot = Join-Path $ServerDirectory "cache\taboolib"

foreach ($required in @($Java, $mainJar, $serverJar)) {
    if (-not (Test-Path -LiteralPath $required)) { throw "Required file not found: $required" }
}

Copy-Item -LiteralPath $mainJar -Destination (Join-Path $pluginDirectory "Incision-Test-1.0.0.jar") -Force
if (Test-Path -LiteralPath $peerJar) { Move-Item -LiteralPath $peerJar -Destination $peerBackup -Force }

if (Test-Path -LiteralPath $cacheRoot) {
    $resolvedServer = (Resolve-Path -LiteralPath $ServerDirectory).Path.TrimEnd('\')
    $resolvedCache = (Resolve-Path -LiteralPath $cacheRoot).Path
    if (-not $resolvedCache.StartsWith("$resolvedServer\", [StringComparison]::OrdinalIgnoreCase)) {
        throw "Refusing to clean cache outside server directory: $resolvedCache"
    }
    Get-ChildItem -LiteralPath $resolvedCache -Recurse -Filter "incision-6.3.0-local-dev-*.jar" -File | Remove-Item -Force
}

$startInfo = [System.Diagnostics.ProcessStartInfo]::new()
$startInfo.FileName = $Java
$startInfo.WorkingDirectory = $ServerDirectory
$startInfo.UseShellExecute = $false
$startInfo.RedirectStandardInput = $true
$startInfo.RedirectStandardOutput = $true
$startInfo.RedirectStandardError = $true
$startInfo.CreateNoWindow = $true
foreach ($argument in @("-Xms512M", "-Xmx1536M", "-Dfile.encoding=UTF-8", "-Dtaboolib.incision.backend=jvmti", "-jar", $serverJar, "nogui")) {
    $startInfo.ArgumentList.Add($argument)
}

$process = [System.Diagnostics.Process]::new()
$process.StartInfo = $startInfo
try {
    if (-not $process.Start()) { throw "Failed to start Leaf" }
    $stdout = $process.StandardOutput.ReadToEndAsync()
    $stderr = $process.StandardError.ReadToEndAsync()
    $deadline = [DateTime]::UtcNow.AddMinutes($TimeoutMinutes)
    $completed = $false
    while ([DateTime]::UtcNow -lt $deadline -and -not $process.HasExited) {
        Start-Sleep -Milliseconds 250
        $snapshot = if (Test-Path -LiteralPath $latestLog) { Get-Content -LiteralPath $latestLog -Raw } else { "" }
        if ($snapshot -match "PASS backend-jvmti-pending-nms" -and $snapshot -match "PASS backend-jvmti-pending-fixture") {
            $completed = $true
            break
        }
    }
    if (-not $process.HasExited) {
        $process.StandardInput.WriteLine("stop")
        $process.StandardInput.Flush()
        if (-not $process.WaitForExit(30000)) { $process.Kill($true); $process.WaitForExit() }
    }
    Set-Content -LiteralPath $consoleLog -Value ($stdout.GetAwaiter().GetResult() + $stderr.GetAwaiter().GetResult()) -Encoding UTF8
    if (-not $completed) { throw "Pending-load tests did not pass; see $consoleLog" }
    Write-Host "Leaf 26.2 JVMTI pending-load PASS: $consoleLog"
} finally {
    if (-not $process.HasExited) { $process.Kill($true); $process.WaitForExit() }
    $process.Dispose()
    if (Test-Path -LiteralPath $peerBackup) { Move-Item -LiteralPath $peerBackup -Destination $peerJar -Force }
}
