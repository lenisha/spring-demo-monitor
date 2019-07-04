<#
.SYNOPSIS
This Powershell Script authenticates users through the MSI endpoint, providing authentication token
.DESCRIPTION
This Powershell Script authenticates users through the MSI endpoint, providing authentication token
.EXAMPLE
.NOTES
.LINK
#>

Function Get-MRVAzureMSIToken
{
    param(
        [Parameter(Mandatory = $false)]
        [String]
        $apiVersion = "2017-09-01",
        [Parameter(Mandatory = $false)]
        [String]
        $resourceURI = "https://management.azure.com/",
        [Parameter(Mandatory = $false)]
        [String]
        $MSISecret,
        [Parameter(Mandatory = $false)]
        [String]
        $MSIEndpoint,
        [Parameter(Mandatory = $false)]
        [switch]
        $VMMSI
    )
    $result = @{Result = $false; Token = $null; Reason = 'Failed to get token'}
    If ($VMMSI)
    {
        Write-Output "Runining in Context of the VM"
        If (($MSIEndpoint -eq $null) -or ($MSIEndpoint -eq ""))
        {
            Write-Output "No MSI endpoint provided. Assuming default one for the VM"
            $MSIEndpoint = 'http://localhost:50342/oauth2/token'
        }
    }
    If (($MSIEndpoint -eq $null) -or ($MSIEndpoint -eq ""))
    {
        Write-Output "No MSI Endpont provided, checking in Environment Variables"
        $MSIEndpoint = $env:MSI_ENDPOINT
        if (($MSIEndpoint) -eq $null -or ($MSIEndpoint -eq ""))
        {
            Write-Error "Can't find MSI endpoint in System Variables"
            return $result
        }
    }
    If (($MSISecret -eq $null) -or ($MSISecret -eq ""))
    {
        Write-Output "No MSI Endpont provided, checking in Environment Variables"
        $MSISecret = $env:MSI_SECRET
        if (($MSIEndpoint) -eq $null -or ($MSIEndpoint -eq ""))
        {
            Write-Error "Can't find MSI endpoint in System Variables"
            return $result
        }
    }
    Write-Output "Endpoint: [$MSIEndpoint]"
    If ($VMMSI)
    {
        $response = Invoke-WebRequest -Uri $MSIEndpoint -Method GET -Body @{resource = $resourceURI} -Headers @{Metadata = "true"}
        $content = $response.Content | ConvertFrom-Json
        $accessToken = $content.access_token
    }
    else
    {
        $tokenAuthURI = $MSIEndpoint + "?resource=$resourceURI&api-version=$apiVersion"
        Write-Output "Invoking $tokenAuthURI with secret"
        $tokenResponse = Invoke-RestMethod -Method Get -Headers @{"Secret" = "$env:MSI_SECRET"} -Uri $tokenAuthURI
        $accessToken = $tokenResponse.access_token
        Out-File -FilePath token.json -InputObject $tokenResponse
    }


    if (($accessToken -eq $null) -or ($accessToken -eq ""))
    {
        Write-Error "Failed to get Token. It is empty [$accessToken]"
        return $result
    }
    else
    {
        $result = @{Result = $true; Token = $accessToken; Reason = 'Success'}
        return $result
    }
}


#Get-MRVAzureMSIToken -apiVersion "2017-09-01" -resourceURI "https://database.windows.net/"
Get-MRVAzureMSIToken -apiVersion "2017-09-01" -resourceURI "https://login.microsoftonline.com/common/v2.0/"