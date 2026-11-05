$ErrorActionPreference = 'Stop'
function Initialize-CampusData([string]$RuntimePath) {
    New-Item -ItemType Directory -Force (Join-Path $RuntimePath 'data') | Out-Null
    $marker = Join-Path $RuntimePath '.initialized'
    $initialized = Test-Path -LiteralPath $marker
    foreach ($source in Get-ChildItem (Join-Path $PSScriptRoot 'data') -Filter *.csv) {
        $destination = Join-Path $RuntimePath ('data/' + $source.Name)
        if (!(Test-Path -LiteralPath $destination)) {
            if (!$initialized -or $source.Name -in @('users.csv','venues.csv')) {
                Copy-Item -LiteralPath $source.FullName -Destination $destination
            } else {
                $header = [IO.File]::ReadAllLines($source.FullName)[0]
                [IO.File]::WriteAllText($destination, $header + [Environment]::NewLine, [Text.UTF8Encoding]::new($false))
            }
        }
    }
    [IO.File]::WriteAllText($marker, 'Initialized')
}
function Lock-CampusRuntime([string]$RuntimePath) {
    New-Item -ItemType Directory -Force $RuntimePath | Out-Null
    try { return [IO.File]::Open((Join-Path $RuntimePath '.runtime.lock'), 'OpenOrCreate', 'ReadWrite', 'None') }
    catch { throw 'Runtime is in use. Stop Campus & Co. before restarting or resetting this directory.' }
}


function Find-CampusJdk {
    $candidates = @()
    if ($env:JAVA_HOME) { $candidates += $env:JAVA_HOME }
    foreach ($vendor in @('Java','Eclipse Adoptium','Microsoft')) {
        $folder = Join-Path $env:ProgramFiles $vendor
        if (Test-Path $folder) { $candidates += @(Get-ChildItem $folder -Directory | Sort-Object Name -Descending | Select-Object -ExpandProperty FullName) }
    }
    $compiler = Get-Command javac -ErrorAction SilentlyContinue
    if ($compiler) { $candidates += Split-Path (Split-Path $compiler.Source) }
    foreach ($candidate in $candidates) {
        if ((Test-Path (Join-Path $candidate 'bin/javac.exe')) -and (Test-Path (Join-Path $candidate 'bin/java.exe'))) { return $candidate }
    }
    throw 'Install JDK 17 or newer and set JAVA_HOME to its installation folder.'
}
