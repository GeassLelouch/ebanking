# run-service.ps1

# 1. 資料庫連線
$Env:POSTGRES_USER     = 'postgres'
$Env:POSTGRES_PASSWORD = 'postgres'
$Env:POSTGRES_HOST     = 'host.docker.internal'
$Env:POSTGRES_PORT     = '5432'

# 2. JWT 金鑰（讀檔、保留換行）
$Env:SPRING_SECURITY_JWT_PRIVATE_KEY = Get-Content .\certs\private.pem -Raw
$Env:SPRING_SECURITY_JWT_PUBLIC_KEY  = Get-Content .\certs\public.pem  -Raw

# 3. JWT 過期時間（毫秒）
$Env:SPRING_SECURITY_JWT_EXPIRATION_IN_MS = '3600000'

# 4. 啟動服務
copy .\target\transaction-service-1.0.0.jar .\target\ts-temp.jar
java -jar .\target\ts-temp.jar