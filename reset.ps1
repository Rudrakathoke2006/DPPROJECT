param([string]$RuntimeDirectory = 'frontend/runtime')
$ErrorActionPreference = 'Stop'
. "$PSScriptRoot/runtime-common.ps1"
$runtimePath = if ([IO.Path]::IsPathRooted($RuntimeDirectory)) { [IO.Path]::GetFullPath($RuntimeDirectory) } else { [IO.Path]::GetFullPath((Join-Path $PSScriptRoot $RuntimeDirectory)) }
$runtimeLock = Lock-CampusRuntime $runtimePath
try {
    Initialize-CampusData $runtimePath
    $dataPath = Join-Path $runtimePath 'data'
    $backup = Join-Path $runtimePath ('backups/' + (Get-Date -Format 'yyyyMMdd-HHmmss-ffff'))
    New-Item -ItemType Directory -Force $backup | Out-Null
    Copy-Item -LiteralPath $dataPath -Destination (Join-Path $backup 'data') -Recurse
    foreach ($file in Get-ChildItem -LiteralPath $dataPath -Filter *.csv) {
        if ($file.Name -in @('users.csv','venues.csv')) { continue }
        $header = [IO.File]::ReadAllLines($file.FullName)[0]
        if ([string]::IsNullOrWhiteSpace($header)) { throw "Missing CSV header: $($file.Name). Backup: $backup" }
        [IO.File]::WriteAllText($file.FullName, $header.TrimStart([char]0xFEFF) + [Environment]::NewLine, [Text.UTF8Encoding]::new($false))
    }
    Write-Host "Reset complete: $dataPath"
    Write-Host "Accounts and venues preserved. Backup: $backup"
} finally { $runtimeLock.Dispose() }

