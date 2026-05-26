# test_endpoints.ps1

$baseUrl = "http://localhost:8080/api/v1/blueprints"

Write-Host "--- Testing POST /api/v1/blueprints ---"
$postBody = @{
    author = "testuser"
    name = "testbp"
    points = @(
        @{x=10; y=10},
        @{x=20; y=20},
        @{x=20; y=20},
        @{x=30; y=30}
    )
} | ConvertTo-Json -Depth 3

try {
    $response = Invoke-RestMethod -Method Post -Uri $baseUrl -Body $postBody -ContentType "application/json"
    Write-Host "POST Response Code: $($response.code)"
    Write-Host "POST Response Message: $($response.message)"
} catch {
    Write-Host "POST Failed: $_"
}

Write-Host "`n--- Testing GET /api/v1/blueprints/testuser/testbp ---"
try {
    $response = Invoke-RestMethod -Method Get -Uri "$baseUrl/testuser/testbp"
    Write-Host "GET Response Code: $($response.code)"
    Write-Host "GET Response Data: $($response.data | ConvertTo-Json -Depth 3)"
} catch {
    Write-Host "GET Failed: $_"
}

Write-Host "`n--- Testing PUT /api/v1/blueprints/testuser/testbp/points ---"
$putBody = @{ x=40; y=40 } | ConvertTo-Json
try {
    $response = Invoke-RestMethod -Method Put -Uri "$baseUrl/testuser/testbp/points" -Body $putBody -ContentType "application/json"
    Write-Host "PUT Response Code: $($response.code)"
} catch {
    Write-Host "PUT Failed: $_"
}

Write-Host "`n--- Testing GET ALL /api/v1/blueprints ---"
try {
    $response = Invoke-RestMethod -Method Get -Uri $baseUrl
    Write-Host "GET ALL Response Code: $($response.code)"
    Write-Host "GET ALL Count: $($response.data.Count)"
} catch {
    Write-Host "GET ALL Failed: $_"
}
