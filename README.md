# 瑞吉点餐

### 介绍
练手项目:瑞吉点餐

### 软件架构
三层架构

### 10月16日敲得代码
####增加:
1. 拦截器,防止用户未登录直接访问管理页面
2. 登陆后,员工管理页面展示员工数据:分页查询
3. 新增员工功能
4. 全局异常处理器,拦截全局异常
   1. SQLIntegrityConstraintViolationException
      1. 处理添加重复用户的报错
5. 启用/禁用员工账号
   1. 提供对象转换器JacksonObjectMapper，基于Jackson进行Java对象到json数据的转换;
   2. 在WebMvcConfig配置类中扩展Spring mvc的消息转换器，在此消息转换器中使用提供的对象转换器进行Java对象到json数据的转换;
6. 编辑员工信息
   1. 根据id查询用户信息


###10月17日敲得代码
####优化
1. 公共字段自动填充
   - 实现步骤
   1. 在实体类的属性上加入@TableField注解，指定自动填充的策略。
   2. 按照框架要求编写元数据对象处理器，在此类中统一为公共字段赋值，此类需要实现MetaObjectHandler接口。

3. ThreadLocal为每个线程提供单独一份存储空间，具有线程隔离的效果，只有在线程内才能获取到对应的值，线程外则不能访问当前线程对应的值。
   - 实现步骤
   1. 编写BaseContext工具类，基于ThreadLocal封装的工具类
   2. 在LoginCheckFilter的doFilter方法中调用BaseContext来设置当前登录用户的id
   3. 在MyMetaObjectHandler的方法中调用BaseContext获取登录用户的id
   
####新增
1. 分类管理页面的'新增菜品分类'和'新增套餐分类'
   1. 实体类Category
   2. Mapper接口CategoryMapper
   3. 业务层接口CategoryService 
   4. 业务层实现类CategoryServiceImpl 
   5. 控制层CategoryController
2. 分类信息分页查询
   1. 在CategoryController中增加分页查询的方法，在方法中传递分页条件进行查询，并且需要对查询到的结果，安排设置的套餐顺序字段sort进行排序。
3. 删除分类
   1. 准备实体类菜品(Dish)及套餐(Setmeal)
      - 所属包: com.itheima.reggie.entity
   2. 准备Mapper接口DishMapper和SetmealMapper
      - 所属包: com.itheima.reggie.mapper
   3. 准备Service接口DishService和SetmealService
      - 所属包: com.itheima.reggie.service
   4. 准备Service实现类DishServiceImpl和SetmealServiceImpl
   5. 创建自定义异常
      1. 在业务逻辑操作过程中,如果遇到一些业务参数、操作异常的情况下，我们直接抛出此异常
      2. 所在包: com.itheima.reggie.common
   6. 在CategoryService中扩展delete方法
   7. 在CategoryServiceImpl中实现delete方法
   8. 在GlobalExceptionHandler中处理自定义异常
4. 修改分类
   1. 在CategoryController中增加修改分类的方法
5. 图片上传
   - 通过MultipartFile类型的参数即可接收上传的文件, 方法形参的名称需要与页面的file域的name属性一致
   - 所在包: com.itheima.reggie.controller
   - 逻辑
   1. 获取文件的原始文件名, 通过原始文件名获取文件后缀
   2. 通过UUID重新声明文件名, 文件名称重复造成文件覆盖
   3. 创建文件存放路径(在application.yml中定义文件存储路径)
   4. 将上传的临时文件转存至指定目录
6. 图片下载
   - 逻辑
   1. 定义输入流,通过输入流读取文件
   2. 通过response对象,获取输出流
   3. 通过response对象设置响应格式
   4. 通过输入流读取文件,然后通过输出流将数据写回浏览器