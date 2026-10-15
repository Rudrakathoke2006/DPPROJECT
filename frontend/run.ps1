param([int]$Port = 8080, [string]$RuntimeDirectory = 'runtime')
$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
if (!(Test-Path target/classes/campus/web/Launcher.class)) { & "$PSScriptRoot/build.ps1" }
$runtimePath = [IO.Path]::GetFullPath((Join-Path $PSScriptRoot $RuntimeDirectory))
New-Item -ItemType Directory -Force "$runtimePath/data" | Out-Null
Get-ChildItem ../data -Filter *.csv | ForEach-Object {
    $destination = Join-Path "$runtimePath/data" $_.Name
    if (!(Test-Path -LiteralPath $destination)) { Copy-Item -LiteralPath $_.FullName -Destination $destination }
}
Set-Location $runtimePath
& java '-Dfile.encoding=UTF-8' -cp "$PSScriptRoot/target/classes;$PSScriptRoot/lib/*" campus.web.Launcher "$PSScriptRoot/src/main/webapp" $Port
