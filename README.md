# Global Administrative Areas

这个项目致力于降低管理行政区域，选择行政区域之类的重复劳动投入问题。

可以在此基础上衍生「地址管理」
## 地址管理
从设计场景出发，需求点大致集中在
### 地址选择
应当提供可靠的JS链接，并且以约定的input作为入口；
用户完成选择之后将形成最终的「地址码」并保存入约定的input；
还应当尽可能的提供视觉定制化空间。
### 地址渲染
这应当属于地址选择功能的一部分
### 「地址码」本地解析
应当提供「地址码」的解析API，这对于需要进行数据筛选的业务非常必要；
诸如
#### 属于某行政区域
#### 临近于某行政区域
这些API不仅仅是普通的程序API，还应当可以作为主流数据库的自定义函数；因为「地址码」通常会被数据库储存。
### 高级需求
#### 地址过滤
属于「地址选择」的扩展；应当允许客户机系统介入地址展示的过程，并可决定「不显示」，「不允许选择」，「备注信息」或者「备注信息并且不允许选择」某一行政区域。