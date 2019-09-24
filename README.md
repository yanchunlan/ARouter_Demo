# ARouter_Demo

手写ARouter路由框架

- 包结构： 
```
 app                - app
 base               - androidLib
 lib_annotation     - javaLib
 lib_compiler       - javaLib
 lib_core           - androidLib
 module1            - androidLib or app
 module2            - androidLib or app
 ```
-  依赖关系

![](./pic/zujian.png)

-  原理

1. 利用auto-service编译时自动生成，实现了路由注册的类，并在类种添加注册ARouter的key，value的代码
2. 在application初始化的时候init，根据
   context->getPackageManager->dexFile->根据包名过虑class，找到后，判断是否是其实现类，再反射获取对象，调用实现方法


