$ErrorActionPreference = 'Stop'
Set-Location $PSScriptRoot
New-Item -ItemType Directory -Force lib, target/classes | Out-Null
$artifacts = @(
    @('org/apache/tomcat/embed','tomcat-embed-core','10.1.60'),
    @('org/apache/tomcat/embed','tomcat-embed-jasper','10.1.60'),
    @('org/apache/tomcat/embed','tomcat-embed-el','10.1.60'),
    @('org/apache/tomcat','tomcat-annotations-api','10.1.60'),
    @('org/eclipse/jdt','ecj','3.33.0')
)
foreach ($artifact in $artifacts) {
    $jar = "$($artifact[1])-$($artifact[2]).jar"
    if (!(Test-Path "lib/$jar")) {
        Write-Host "Downloading $jar"
        Invoke-WebRequest -UseBasicParsing "https://repo.maven.apache.org/maven2/$($artifact[0])/$($artifact[1])/$($artifact[2])/$jar" -OutFile "lib/$jar"
    }
}
$sources = Get-ChildItem ../src, src/main/java -Recurse -Filter *.java | ForEach-Object { '"' + $_.FullName.Replace('\','/') + '"' }
[IO.File]::WriteAllLines("$PSScriptRoot/target/sources.txt", $sources, [Text.UTF8Encoding]::new($false))
& javac -encoding UTF-8 --release 17 -cp 'lib/*' -d target/classes '@target/sources.txt'
if ($LASTEXITCODE -ne 0) { throw 'Java compilation failed.' }
Write-Host 'Build complete. Run .\run.ps1 to start Campus & Co.'
