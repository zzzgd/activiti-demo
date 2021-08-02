# 尝试springboot整合activiti工作流的demo
## 一. 启动
不要轻易改里面的依赖版本, 因为一旦版本对不上会直接启动不了

启动application, 会自动在所连接的数据库中执行sql语句. 进行建表

## 二. 说明
resources下的processes文件夹是activiti读取加载的默认的文件夹位置

## 二. 坑
1. mysql连接必须加上`nullCatalogMeansCurrent=true`的参数,否则不会执行sql语句.具体原因如下:
    > 在使用mysql-connect 8.+以上版本的时候需要添加nullCatalogMeansCurrent=true参数，否则在使用mybatis-generator生成表对应的xml等时会扫描整个服务器里面的全部数据库中的表，而不是扫描对应数据库的表
2. Springboot2整合了Activiti7之后, 会报`ACT_RE_DEPLOYMENT`表中缺少`VERSION_`字段, 以及`PROJECT_RELEASE_VERSION`字段.
   需要手动创建.
   ```sql
    ALTER TABLE ACT_RE_DEPLOYMENT ADD VERSION_ varchar(255) NULL;
    ALTER TABLE ACT_RE_DEPLOYMENT ADD PROJECT_RELEASE_VERSION_ varchar(255) NULL;
    ```
3. 
