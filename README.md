# jfapps

Java Fx Apps Container

这是一个基于 JavaFX 的小工具框架，用户可以自己编写小工具，然后将构建出来的 jar 包放到框架的 apps 目录下，这样就能在运行时被框架读取和展示在界面上。

下面分别介绍项目的组成：

## jfapps-launcher

这个是启动项目，展示框架的主界面。在启动过程中将遍历 apps 目录下的小工具并初始化。

## jfapps-app-base

这是每个小工具都要依赖的一个公共库，定义了一些接口、枚举和注解，用于向框架提供一些信息。

## 如何开发小工具

小工具是独立的 JavaFX 项目，但需要：

1. 添加 jfapps-app-base 依赖；
2. 实现 com.hyd.jfapps.appbase.JfappsApp 的子类。实现这个类很简单，只需要实现 getRoot() 方法返回一个 Parent 对象即可。
3. 在 resources 目录下创建 jfapps.properties，内容参考示例项目。
4. （可选）通过在 reources 目录下添加 logo.png 来自定义小工具的图标。

## 小工具是如何加载的

框架会在 apps 目录下搜索 jar 文件，每个文件都是独立的小工具（打包的时候打一个大的 jar 包），必须基于 JavaFX。框架会为每个小工具创建独立的 ClassLoader，并将小工具提供的界面展示出来。

## 示例

jfapps-launcher/apps 目录下放了一个简单的例子，在运行 jfapps-launcher 时，框架便会加载并展示这个小工具。

![java_9BXgT662jm](https://user-images.githubusercontent.com/900606/60070187-5326c500-9748-11e9-8efd-172259d424db.png)

![java_FJiGn4baro](https://user-images.githubusercontent.com/900606/60070188-5326c500-9748-11e9-8ac4-be51d6e5ae5f.png)

![java_Q3wLbXALZ9](https://user-images.githubusercontent.com/900606/60070189-53bf5b80-9748-11e9-9b55-8c22883d7602.png)

## 尚未完成的工作

1. 根据名称搜索小工具；
2. 小工具分类展示。