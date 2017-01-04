### 解决乱码
```
tag = new String(tag.getBytes("ISO-8859-1"), "UTF-8");
```
### 转换base64
```
function make_base_auth(user, password) {
  var tok = user + ':' + pass;
  var hash = Base64.encode(tok);
  return "Basic " + hash;
}
```

### 引用静态文件值
```
@PropertySource(value="classpath:missing.properties", ignoreResourceNotFound=true)

{
@Autowired
private Environment env;

String mongodbUrl = env.getProperty("mongodb.url");
String defaultDb = env.getProperty("mongodb.db");
}
{
@Value("${mongodb.url}")
private String mongodbUrl;
}
```

