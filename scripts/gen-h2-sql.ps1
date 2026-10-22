# =====================================================
# 由 MySQL 脚本生成 H2（单机版）初始化脚本
# 用法：powershell -ExecutionPolicy Bypass -File scripts/gen-h2-sql.ps1
# 生成结果：src/main/resources/db/h2/schema-h2.sql、data-h2.sql
# =====================================================
$ErrorActionPreference = 'Stop'

$root = Split-Path -Parent $PSScriptRoot
$schemaSource = Join-Path $root 'sql/schema.sql'
$dataSource = Join-Path $root 'sql/data.sql'
$outputDir = Join-Path $root 'src/main/resources/db/h2'

if (-not (Test-Path $outputDir)) { New-Item -ItemType Directory -Path $outputDir | Out-Null }

$utf8 = New-Object System.Text.UTF8Encoding($false)

# ---------- 1. 处理 schema.sql ----------
$schema = [System.IO.File]::ReadAllText($schemaSource, [System.Text.Encoding]::UTF8)
$schema = [regex]::Replace($schema, '(?s)^.*?USE\s+`campus_express`;\s*', '')

$indexStatements = New-Object System.Collections.Generic.List[string]

$tablePattern = '(?s)CREATE TABLE\s+(`[^`]+`)\s*\((.*?)\)\s*ENGINE\s*=\s*InnoDB[^;]*;'
$schema = [regex]::Replace($schema, $tablePattern, {
        param($m)
        $table = $m.Groups[1].Value
        $body = $m.Groups[2].Value
        $kept = New-Object System.Collections.Generic.List[string]

        foreach ($rawLine in ($body -split "`r?`n")) {
            $line = $rawLine.Trim()
            if ($line -eq '') { continue }

            # 去掉列注释（H2 / MySQL 通用语法差异最大的一处）
            $line = [regex]::Replace($line, "\s*COMMENT\s+'(?:[^']|'')*'", '')
            # 去掉 ON UPDATE CURRENT_TIMESTAMP（H2 不支持）
            $line = [regex]::Replace($line, '\s+ON\s+UPDATE\s+CURRENT_TIMESTAMP', '')
            $line = $line.Trim().TrimEnd(',')

            # 唯一索引改为命名约束
            if ($line -match '^UNIQUE\s+KEY\s+(`[^`]+`)\s*\((.*?)\)$') {
                $kept.Add("    CONSTRAINT $($Matches[1]) UNIQUE ($($Matches[2]))")
                continue
            }

            # 普通索引移到 CREATE TABLE 之后
            if ($line -match '^KEY\s+(`[^`]+`)\s*\((.*?)\)$') {
                $indexStatements.Add("CREATE INDEX $($Matches[1]) ON $table ($($Matches[2]));")
                continue
            }

            $kept.Add("    $line")
        }

        $sb = New-Object System.Text.StringBuilder
        [void]$sb.AppendLine("CREATE TABLE $table (")
        [void]$sb.AppendLine(($kept -join ",`r`n"))
        [void]$sb.AppendLine(');')
        return $sb.ToString()
    })

$schemaOut = New-Object System.Text.StringBuilder
[void]$schemaOut.AppendLine('-- =====================================================')
[void]$schemaOut.AppendLine('-- 校园快递驿站管理系统 H2 建表脚本（由 scripts/gen-h2-sql.ps1 自动生成，请勿手工修改）')
[void]$schemaOut.AppendLine('-- 适用于单机版 / exe：MODE=MySQL')
[void]$schemaOut.AppendLine('-- =====================================================')
[void]$schemaOut.AppendLine()
[void]$schemaOut.Append($schema.Trim())
[void]$schemaOut.AppendLine()
[void]$schemaOut.AppendLine()
[void]$schemaOut.AppendLine('-- 索引（原 MySQL 脚本中的 KEY 定义）')
foreach ($idx in $indexStatements) { [void]$schemaOut.AppendLine($idx) }

[System.IO.File]::WriteAllText((Join-Path $outputDir 'schema-h2.sql'), $schemaOut.ToString(), $utf8)

# ---------- 2. 处理 data.sql ----------
$data = [System.IO.File]::ReadAllText($dataSource, [System.Text.Encoding]::UTF8)
$data = [regex]::Replace($data, '(?im)^\s*USE\s+`campus_express`;\s*$', '')
# MySQL 的 NOW() - INTERVAL n UNIT 改为 H2/MySQL 都支持的 DATEADD
$data = [regex]::Replace($data, "NOW\(\)\s*-\s*INTERVAL\s+(\d+)\s+(HOUR|MINUTE|DAY|SECOND)", {
        param($m)
        "DATEADD('$($m.Groups[2].Value)', -$($m.Groups[1].Value), NOW())"
    })

$dataOut = New-Object System.Text.StringBuilder
[void]$dataOut.AppendLine('-- =====================================================')
[void]$dataOut.AppendLine('-- 校园快递驿站管理系统 H2 初始化数据（由 scripts/gen-h2-sql.ps1 自动生成，请勿手工修改）')
[void]$dataOut.AppendLine('-- 默认密码均为 123456')
[void]$dataOut.AppendLine('-- =====================================================')
[void]$dataOut.AppendLine()
[void]$dataOut.Append($data.Trim())
[void]$dataOut.AppendLine()
[void]$dataOut.AppendLine()
[void]$dataOut.AppendLine('-- 数据中显式指定了主键，需要把自增序列推后，避免后续插入主键冲突')
$restartTables = @('sys_role', 'sys_user', 'express_company', 'shelf', 'student_user', 'send_order', 'deposit_order')
foreach ($t in $restartTables) {
    [void]$dataOut.AppendLine("ALTER TABLE ``$t`` ALTER COLUMN ``id`` RESTART WITH 1000;")
}

[System.IO.File]::WriteAllText((Join-Path $outputDir 'data-h2.sql'), $dataOut.ToString(), $utf8)

Write-Host "已生成："
Write-Host "  $outputDir\schema-h2.sql"
Write-Host "  $outputDir\data-h2.sql"
Write-Host "索引数量：$($indexStatements.Count)"
