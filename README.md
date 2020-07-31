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

1. 利用auto-service注册注解生成器，使用write并编译时自动生成，实现了路由注册的类，并在类种添加注册ARouter的key，value的代码
2. 在application初始化的时候init，根据
   context->getPackageManager->dexFile->根据包名过虑class，找到后，判断是否是其实现类，再反射获取对象，调用实现方法
   
- 路由优化

1. 编译时需要遍历所有的class文件，并反射初始化，导致耗时？
```
解决办法1：

路由的实质就是一个map存储key,value的方式，实现跳转，可在编译的时候，直接生成一个key,value的json文件，类似：
{
  "test": [
    {
      "url": "hmiou://www.54jietiao.com/test/test1?title=*",
      "iclass": "Test1ViewController",
      "aclass": "com.hm.iou.router.demo.TestActivity1"
    }
  ],
  "main": [
    {
      "url": "hmiou://www.54jietiao.com/main/index?url=*",
      "iclass": "MainViewController",
      "aclass": "com.hm.iou.router.demo.MainActivity"
    }
  ]
}
在初始化的时候直接读取这个文件，实现路由界面的跳转，但是传递对象数据无法解决，可使用RxliveDataBus解决，专门维护一个数据表

解决办法2：

可使用自定义gradle的方式遍历到路由新生成的代码文件，，在注入路由类里面新增一个注入方法，然后在方法内通过asm插桩的方式，把遍历到的文件路径传递进去，类似：
private static void loadAppLike() {
  registerAppLike("com.xxx.xxx.a$$ModuleARoute");
  registerAppLike("com.xxx.xxx.b$$ModuleDRoute");
}
```

2. 路由传参，另一个页面注解获取参数值：
   传的参数存储在map中，在生成的另一个文件中获取赋值获取参数值的类，并在注册得时候调用，类似于:
  
```
A ---> :

@Extra
String msg;
    
B ---> : 
   
public class Module1MainActivity_Extra implements IExtra {
  @Override
  public void loadExtra(Object target) {
    Module1MainActivity t = (Module1MainActivity)target;
    t.msg = t.getIntent().getStringExtra("msg");
  }
}
```
