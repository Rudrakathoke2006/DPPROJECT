param([int]$Port = 8080, [string]$RuntimeDirectory = 'frontend/runtime', [switch]$Console)
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot/runtime-common.ps1"
$runtimePath = if ([IO.Path]::IsPathRooted($RuntimeDirectory)) { [IO.Path]::GetFullPath($RuntimeDirectory) } else { [IO.Path]::GetFullPath((Join-Path $PSScriptRoot $RuntimeDirectory)) }
$jdk = Find-CampusJdk
$runtimeLock = Lock-CampusRuntime $runtimePath
try {
    Initialize-CampusData $runtimePath
    & "$PSScriptRoot/frontend/build.ps1"
    Push-Location $runtimePath
    try {
        if ($Console) { & "$jdk/bin/java.exe" '-Dfile.encoding=UTF-8' -cp "$PSScriptRoot/frontend/target/classes" Main }
        else { & "$jdk/bin/java.exe" '-Dfile.encoding=UTF-8' -cp "$PSScriptRoot/frontend/target/classes;$PSScriptRoot/frontend/lib/*" campus.web.Launcher "$PSScriptRoot/frontend/src/main/webapp" $Port }
        if ($LASTEXITCODE -ne 0) { throw "Java exited with code $LASTEXITCODE" }
    } finally { Pop-Location }
} finally { $runtimeLock.Dispose() }
