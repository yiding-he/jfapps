# jfapps

Java Fx Apps Container

这是一个基于 JavaFX 的小工具框架，用户可以自己编写小工具，然后将构建出来的 jar 包放到框架的 apps 目录下，这样就能在运行时被框架读取和展示在界面上。

下面分别介绍项目的组成：

## jfapps-launcher

这个是启动项目，展示框架的主界面。在启动过程中将遍历 apps 目录下的小工具并初始化。

## jfapps-app-base

这是每个小工具都要依赖的一个公共库，定义了一些接口、枚举和注解，用于向框架提供一些信息。

## 小工具是如何加载的

框架会在 apps 目录下搜索 jar 文件，每个文件都是独立的小工具（打包的时候打一个大的 jar 包），必须基于 JavaFX。框架会为每个小工具创建独立的 ClassLoader，并将小工具提供的界面展示出来。

## 示例

jfapps-launcher/apps 目录下放了一个简单的例子，在运行 jfapps-launcher 时，框架便会加载并展示这个小工具。

![idea64_FwD8XQR1dI](https://user-images.githubusercontent.com/900606/59966391-cb299b00-954e-11e9-82f7-c23f555b62fd.png)
