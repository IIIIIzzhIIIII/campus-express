# =====================================================
# 打包「零依赖单机版」Windows 可执行程序（内置 JRE + H2 数据库）
#
# 前置条件：
#   * 已安装 JDK 17 及以上（需包含 jpackage / jlink，Oracle JDK、Temurin、Microsoft OpenJDK 均可）
#   * 首次执行会下载 Maven 依赖，需联网
#
# 用法：
#   powershell -ExecutionPolicy Bypass -File scripts\build-exe.ps1
#   powershell -ExecutionPolicy Bypass -File scripts\build-exe.ps1 -JdkHome "C:\Program Files\Java\jdk-17" -Version 1.0.1
#
# 产物：
#   dist\CampusExpress\CampusExpress.exe        ← 双击即用，无需安装 JDK / MySQL
#   dist\CampusExpress-<版本>-win-x64.zip       ← 可直接上传 GitHub Releases
# =====================================================
param(
    [string]$JdkHome = $env:JAVA_HOME,
    [string]$Version = '1.0.0',
    [string]$IconPath = ''
)

$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
Push-Location $root
try {
    if (-not $JdkHome) {
        throw '未找到 JDK：请设置 JAVA_HOME，或使用 -JdkHome 指定 JDK 17+ 的安装目录。'
    }
    $jpackage = Join-Path $JdkHome 'bin\jpackage.exe'
    if (-not (Test-Path $jpackage)) {
        throw "当前 JDK 不包含 jpackage：$jpackage（请使用 JDK 17+ 完整版）"
    }
    $env:JAVA_HOME = $JdkHome
    $env:PATH = "$(Join-Path $JdkHome 'bin');$env:PATH"
    Write-Host "使用 JDK：$JdkHome" -ForegroundColor Cyan

    Write-Host '==> 1/4 编译并打包 Spring Boot jar' -ForegroundColor Cyan
    & (Join-Path $root 'mvnw.cmd') -B -DskipTests clean package
    if ($LASTEXITCODE -ne 0) { throw 'Maven 构建失败' }

    $dist = Join-Path $root 'dist'
    $stage = Join-Path $dist 'app'
    if (Test-Path $dist) { Remove-Item -Recurse -Force $dist }
    New-Item -ItemType Directory -Path $stage -Force | Out-Null
    Copy-Item (Join-Path $root 'target\campus-express.jar') (Join-Path $stage 'campus-express.jar')

    Write-Host '==> 2/4 使用 jpackage 生成内置 JRE 的 app-image' -ForegroundColor Cyan
    $jpackageArgs = @(
        '--type', 'app-image',
        '--name', 'CampusExpress',
        '--app-version', $Version,
        '--input', $stage,
        '--main-jar', 'campus-express.jar',
        '--main-class', 'org.springframework.boot.loader.launch.JarLauncher',
        '--dest', $dist,
        '--java-options', '-Dfile.encoding=UTF-8',
        '--java-options', '-Dspring.profiles.active=standalone',
        '--java-options', '-Djava.awt.headless=false',
        '--add-modules', 'java.base,java.desktop,java.sql,java.naming,java.management,java.instrument,java.security.jgss,java.xml,java.net.http,jdk.unsupported,jdk.crypto.ec,jdk.zipfs',
        '--vendor', 'Campus Express',
        '--description', '校园快递驿站管理系统（单机版，内置 H2 数据库）'
    )
    if ($IconPath) {
        if (-not (Test-Path $IconPath)) { throw "图标文件不存在：$IconPath" }
        $jpackageArgs += @('--icon', (Resolve-Path $IconPath).Path)
    }
    & $jpackage @jpackageArgs
    if ($LASTEXITCODE -ne 0) { throw 'jpackage 打包失败' }

    Write-Host '==> 3/4 复制使用说明' -ForegroundColor Cyan
    $doc = Join-Path $root 'docs\05-单机版与exe运行说明.md'
    if (Test-Path $doc) {
        Copy-Item $doc (Join-Path $dist 'CampusExpress\使用说明.md')
    }

    Write-Host '==> 4/4 生成 zip 压缩包' -ForegroundColor Cyan
    $zip = Join-Path $dist "CampusExpress-$Version-win-x64.zip"
    Compress-Archive -Path (Join-Path $dist 'CampusExpress\*') -DestinationPath $zip -Force

    Write-Host ''
    Write-Host '打包完成：' -ForegroundColor Green
    Write-Host "  程序：$(Join-Path $dist 'CampusExpress\CampusExpress.exe')"
    Write-Host "  压缩包：$zip"
    Write-Host ''
    Write-Host '提示：双击 CampusExpress.exe 即可运行，数据保存在 %USERPROFILE%\.campus-express\data' -ForegroundColor Yellow
}
finally {
    Pop-Location
}
