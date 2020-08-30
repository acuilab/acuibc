gourd社区钱包桌面版v0.6.0已经发布（目前只支持Conflux钱包），项目地址：https://github.com/acuilab/acuibc

# 下载

**下载地址github**

| 链接 | 说明 |
| - | - |
| [gourd-v0.6.0.zip](https://github.com/acuilab/acuibc/releases/download/v0.6.0/gourd-v0.6.0.zip) | 未绑定jre，需单独安装jre8，支持windows、linux、mac，适合有经验的用户 |
| [gourd-with-jre-v0.6.0.zip](https://github.com/acuilab/acuibc/releases/download/v0.6.0/gourd-with-jre-v0.6.0.zip) | 绑定了windows版的jre8，只支持windows，适合小白用户 |

**下载地址百度网盘**

链接：https://pan.baidu.com/s/1a67nKOo7P7AVgOthcEwe5w
提取码：zlqn

# 安装

本软件为压缩版，直接解压到硬盘即可使用，注意由于开发平台的限制解压到硬盘的路径不能包含中文。
如果下载的是未绑定jre8的版本，需要单独安装Jre8并设置好环境变量。

如下图解压到d盘
<img src="https://b3logfile.com/file/2020/08/image-0cfd0755.png" width="50%" />

打开bin目录，windows下双击执行gourd64.exe，linux和mac执行gourd脚本文件
![image.png](https://b3logfile.com/file/2020/08/image-8830a9a7.png)

加载窗口显示
![image.png](https://b3logfile.com/file/2020/08/image-acc60fad.png)

稍等片刻，程序主窗口打开
![image.png](https://b3logfile.com/file/2020/08/image-cf64d9f0.png)

# 使用

程序是标准的桌面应用程序，包括菜单栏、工具栏，菜单栏和工具栏下面是工作区。工作区左侧是钱包列表，右侧是钱包信息，钱包信息包括基本信息、主网币和代币以及交易记录。

## 菜单栏

**文件-退出**：退出本程序
**钱包-创建钱包**：打开创建钱包向导，创建新钱包，目前只支持Conflux钱包
**钱包-导入钱包**：打开导入钱包向导，通过私钥或助记词导入钱包，目前只支持Conflux钱包
**钱包-迁入钱包**：将保存到硬盘的钱包数据恢复到本程序
**钱包-迁出钱包**：将本程序的钱包数据保存到硬盘
迁入和迁出功能可以实现钱包的备份和还原，也可以将钱包从一台电脑迁移到另一台电脑
**工具-选项**：设置应用程序的配置项
目前只有一个配置项“自动更新”，若选中，则程序启动后自动检查是否有更新，如果有更新则自动下载，程序重启后，自动更新为新版本；若未选中，则需手动执行菜单项“帮助-检查更新”来更新程序到新版本；默认选中自动更新。
**帮助-检查更新**：手动检查更新
**帮助-关于**：显示软件版本等信息

## 工具栏

![createwallet64.png](https://b3logfile.com/file/2020/08/createwallet64-482951d5.png)创建钱包：打开创建钱包向导，创建新钱包

![importwallet64.png](https://b3logfile.com/file/2020/08/importwallet64-aeb190c2.png)导入钱包：打开导入钱包向导，通过私钥或助记词导入钱包

## 创建钱包

1. 设置钱包名称并选择币种

![image.png](https://b3logfile.com/file/2020/08/image-58afc29e.png)

2. 设置钱包密码

![image.png](https://b3logfile.com/file/2020/08/image-683bcca6.png)

3. 生成助记词

![image.png](https://b3logfile.com/file/2020/08/image-90125913.png)

4. 确认助记词

![image.png](https://b3logfile.com/file/2020/08/image-1b71e724.png)
按顺序点击助记词，若顺序不一致，则点击上方对应的单词取消，重新选择。

5. 创建完成
   左侧钱包列表显示新创建的钱包。

## 导入钱包

1. 设置钱包名称并选择币种
   
   ![image.png](https://b3logfile.com/file/2020/08/image-33f6d8a8.png)
2. 设置钱包密码

![image.png](https://b3logfile.com/file/2020/08/image-c3c28e80.png)

3. 输入助记词或私钥

![image.png](https://b3logfile.com/file/2020/08/image-d68a5d72.png)

4. 导入完成
   左侧钱包列表显示新导入的钱包。

## 迁入钱包

1. 选择本地数据文件

![image.png](https://b3logfile.com/file/2020/08/image-556c2394.png)

2. 迁入完成，右下角提示重启应用以重新加载钱包

![image.png](https://b3logfile.com/file/2020/08/image-12f88754.png)

3. 重启后，钱包列表中加载新迁入的钱包
   注意，如果发现同名的钱包，新迁入的钱包会在钱包名称后加一个时间戳后缀，用户可通过“修改名称”操作重新命名。

## 迁出钱包

1. 选择要保存的本地文件

![image.png](https://b3logfile.com/file/2020/08/image-d2614598.png)

2. 迁入完成

## 钱包列表

左侧钱包列表窗口显示所有的钱包，每个钱包包括如下信息和操作
![image.png](https://b3logfile.com/file/2020/08/image-2491e552.png)

1. 钱包名称：显示钱包名称
2. 钱包地址：显示钱包地址
3. 转账操作：打开转账向导（主网币转账），参见下面的转账
4. 删除操作：删除当前钱包
5. 打开钱包信息：在工作区右侧显示钱包信息
6. 钱包余额：显示钱包余额
7. 刷新操作：刷新钱包余额

## 钱包信息

![image.png](https://b3logfile.com/file/2020/08/image-eccd0722.png)

钱包信息在工作区的右侧，如果打开多个钱包信息可通过上方的tab标签切换。钱包信息窗口上面是钱包的基本信息和操作

1. 钱包名称
2. 钱包地址
3. 最近一次交易哈希
4. 钱包地址二维码
5. 修改名称操作
6. 修改密码操作
7. 导出助记词操作
8. 导出私钥操作

钱包信息下面是主网币和代币信息、操作及交易记录列表。
主网币和代币以tab页标签的形式展示。目前支持cfx、fc、cpi三种。

1. 转账操作：打开转账向导进行转账
2. 显示余额
3. 刷新操作：刷新余额及交易记录
4. 交易记录下方可根据关键字和交易类型对交易记录进行筛选。

## 转账

主网币和代币的转账都是通过转账向导实现的。

1. 输入转账信息![image.png](https://b3logfile.com/file/2020/08/image-1a337537.png)
   注意：目前矿工费仅支持系统默认，不支持手动指定矿工费。
2. 输入钱包密码

![image.png](https://b3logfile.com/file/2020/08/image-35ead4f0.png)

3. 确认转账信息

![image.png](https://b3logfile.com/file/2020/08/image-f940b03c.png)

4. 转账完成
   通过刷新操作获得最新的余额和交易记录。


 
