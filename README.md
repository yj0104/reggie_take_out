# Project _A

#### 介绍
练手项目:瑞吉点餐

#### 软件架构
软件架构说明


#### 安装教程

第一个算的上项目的代码,练手的

#### 使用说明

解压缩?IDEA运行?

#### 参与贡献

感谢爹感谢妈,感谢百度.

#### 特技

暂时不知,等我写出来再说哈哈
1.  使用 Readme\_XXX.md 来支持不同的语言，例如 Readme\_en.md, Readme\_zh.md
2.  Gitee 官方博客 [blog.gitee.com](https://blog.gitee.com)
3.  你可以 [https://gitee.com/explore](https://gitee.com/explore) 这个地址来了解 Gitee 上的优秀开源项目
4.  [GVP](https://gitee.com/gvp) 全称是 Gitee 最有价值开源项目，是综合评定出的优秀开源项目
5.  Gitee 官方提供的使用手册 [https://gitee.com/help](https://gitee.com/help)
6.  Gitee 封面人物是一档用来展示 Gitee 会员风采的栏目 [https://gitee.com/gitee-stars/](https://gitee.com/gitee-stars/)

#### 10月16日敲得代码

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
