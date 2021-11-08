# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [0.9.33] - 20211108
### Changed
- 适应近期Conflux主网环境
- 默认gasPrice设置从1改为60000，gasPrice范围从1-100改为1000-100000

## [0.9.32] - 20211029
### Added
- 支持熊猫NFT、小小乱斗英雄NFT（打包好的可战斗状态的）
- 支持Lumi
- 支持西游NFT
- 支持Dan

## [0.9.27] - 20210802
### Added
- 支持红楼、社区北京、松的秘密、Phantom加速等NTT
- 修复右下角状态栏增加代币折合美金价格显示
- 修复721的转账

## [0.9.26] - 20210625
### Added
- 深度优化古国Dapp,各种好用便捷
- 增加右下角状态栏增加代币折合美金价格显示
- 增加对art101meme的图片显示
- 增加多种NFT:时空区块、中本聪的礼物、拓扑三国、烤仔卡、烤仔卡合成等
- 增加论坛提申请的Points
- 增加古国秘密功能

## [0.9.24] - 2021-06-12
### Added
- 支持山海经NFT、古国众神NFT、古国创世NFT
- 增加古国序列(部分功能暂未实现)
- 在cfx代币页面增加FC挖矿操作，方便收取利息
- 增加YAO-CFX Pair代币

### Changed
- FluxDApp的url改为https://flux.01.finance/conflux/

## [0.9.22] - 2021-05-29
### Added
- 支持古国YAO token
- 交易记录表格选中时在状态栏显示数量合计

## [0.9.21] - 2021-05-15
### Added
- 增加工具菜单栏和工具栏，加入123cfx.com链接，使用默认浏览器打开
- 增加ANT代币的支持
- 增加PoolGo代币的支持

### Changed
- dappstore表格加入滚动条
- 修复钱包菜单栏图标比例偏大问题
- 批量转账功能从csv文件导入转账信息时，如果是旧地址，则自动转换为新地址格式

## [0.9.20] - 2021-05-13
### Added
- 有限支持Flux、Moondex、MoonSwap等应用（使用dapp浏览器实现）
- 支持cYFII、cMBTM代币
- 工具栏增加刷新、调试、前进、后退等按钮，用于操作dapp浏览器；
- 工具栏增加DappStore按钮方便访问
- 交易记录表格宽度固定

### Changed
- 更新记录窗口标题由"Changelog"改为"更新记录"
- Dapp浏览器窗口标题增加钱包地址后4位信息
- 钱包窗口标题增加钱包地址后4位信息
- cfx和其他erc20代币的交易记录默认返回条数调整为100
- CfxCoin和ERC20Coin的gasDefault方法返回值由常量1改为CfxUnit.DEFAULT_GAS_PRICE
- cfx和其他erc20代币的交易记录默认返回条数调整为100，最大返回条数为10000；超过100时会分多次请求
- eth转账gas相关(min,max,default)改为从ethgasstation.info动态获取

## [0.9.18] - 2021-04-21
### Added
- 支持ConHero英雄NFT
- 支持Trea 创世NFT1

## [0.9.17] - 2021-04-05
### Added
- 支持圣诞NFT
- 支持矿工NFT
- 支持棉花NFT
- 增加MetaData的Number属性，与id分开。用于tokenid和id不同的情况。同时在所有已有NFT代码里，都把tokenid和number分开为两个参数。

## [0.9.16] - 2021-04-01
### Added
- 支持TREA代币

### Changed
- 移除Auto Update UI模块
- 重写检查更新逻辑，以支持新增模块更新

## [0.9.15] - 2021-03-16
### Added
- 自动更新检查时间间隔设置为"每次启动时"
- 导出助记词面板增加打印操作

### Changed
- 菜单名称由"CHANGELOG"改为"更新日志"，位置调整到最下面

## [0.9.14] - 2021-03-15
### Added
- conflux批量转账刷新交易状态时写入日志窗口
- 增加CHANGELOG支持
- 增加flux nft支持
- 增加cflux token支持
- 增加wcfx cusdc cdai 的token支持

### Fixed
- conflux代币交易日志发送方和接收方转换为简化形式地址



[^_^]:http://jbt.github.io/markdown-editor/ 

[^_^]:## [Unreleased]