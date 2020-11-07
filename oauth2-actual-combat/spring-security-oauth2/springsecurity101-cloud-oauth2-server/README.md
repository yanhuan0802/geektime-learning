# 授权服务器
* 如果授权服务器产生 Token 的话，受保护资源服务器必须要有一种办法来验证 Token，那如果这里的 Token 不是 JWT 的方式，怎么办呢？
    * 首先，Token 可以保存在数据库或 Redis 中，资源服务器和授权服务器共享底层的 TokenStore 来验证；
    * 然后，资源服务器可以使用 RemoteTokenServices，来从授权服务器的 /oauth/check_token 端点进行 Token 校验。