10月16日代码修改内容如下
增加:
1.拦截器,防止用户未登录直接访问管理页面;
2.登陆后,员工管理页面展示员工数据:分页查询;
3.新增员工功能;
4.全局异常处理器,拦截异常:
    SQLIntegrityConstraintViolationException;
5.启用/禁用员工账号
    5.1.提供对象转换器JacksonObjectMapper，基于Jackson  进行Java对象到json数据的转换;
    5.2.在WebMvcConfig配置类中扩展Spring mvc的消息转换   器，在此消息转换器中使用提供的对象转换器进行Java对象到    json数据的转换;
6.编辑员工信息;
    6.1.根据id查询用户信息.