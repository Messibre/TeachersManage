

param(
    [string]$Password
)

function Get-PlainTextFromSecureString($ss) {
    $bstr = [System.Runtime.InteropServices.Marshal]::SecureStringToBSTR($ss)
    try {
        return [System.Runtime.InteropServices.Marshal]::PtrToStringAuto($bstr)
    } finally {
        [System.Runtime.InteropServices.Marshal]::ZeroFreeBSTR($bstr) | Out-Null
    }
}

if (-not $Password) {
    Write-Host "Enter password (input hidden):"
    $secure = Read-Host -AsSecureString
    $Password = Get-PlainTextFromSecureString $secure
}

$bytes = [System.Text.Encoding]::UTF8.GetBytes($Password)
$sha = [System.Security.Cryptography.SHA256]::Create()
$hashBytes = $sha.ComputeHash($bytes)
$hex = -join ($hashBytes | ForEach-Object { $_.ToString('x2') })

try {
    Set-Clipboard -Value $hex -ErrorAction SilentlyContinue
} catch {
}

Write-Host "SHA256:" $hex
