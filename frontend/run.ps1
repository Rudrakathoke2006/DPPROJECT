param([int]$Port = 8080, [string]$RuntimeDirectory = 'runtime', [switch]$Console)
$runtimePath = if ([IO.Path]::IsPathRooted($RuntimeDirectory)) { $RuntimeDirectory } else { Join-Path $PSScriptRoot $RuntimeDirectory }
& "$PSScriptRoot/../run.ps1" -Port $Port -RuntimeDirectory $runtimePath -Console:$Console
