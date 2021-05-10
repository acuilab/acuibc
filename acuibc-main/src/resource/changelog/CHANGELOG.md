# Changelog

All notable changes to this project will be documented in this file.

The format is based on [Keep a Changelog](https://keepachangelog.com/en/1.0.0/),
and this project adheres to [Semantic Versioning](https://semver.org/spec/v2.0.0.html).

## [Unreleased]
### Added
- 有限支持Flux、Moondex、MoonSwap等应用（使用dapp浏览器实现）
- 支持cYFII、cMBTM代币
- 工具栏增加刷新、调试、前进、后退等按钮，用于操作dapp浏览器；
- 工具栏增加DappStore按钮方便访问

### Changed
- 更新记录窗口标题由"Changelog"改为"更新记录"
- Dapp浏览器窗口标题增加钱包地址后4位信息
- 钱包窗口标题增加钱包地址后4位信息
- cfx和其他erc20代币的交易记录默认返回条数调整为100
- CfxCoin和ERC20Coin的gasDefault方法返回值由常量1改为CfxUnit.DEFAULT_GAS_PRICE

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