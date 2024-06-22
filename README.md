<!-- Improved compatibility of back to top link: See: https://github.com/othneildrew/Best-README-Template/pull/73 -->
<a name="readme-top"></a>
<!--
*** Thanks for checking out the Best-README-Template. If you have a suggestion
*** that would make this better, please fork the repo and create a pull request
*** or simply open an issue with the tag "enhancement".
*** Don't forget to give the project a star!
*** Thanks again! Now go create something AMAZING! :D
-->

<!-- PROJECT LOGO -->
[![State-of-the-art Shitcode](https://img.shields.io/static/v1?label=State-of-the-art&message=Shitcode&color=7B5804)](https://github.com/trekhleb/state-of-the-art-shitcode)
<br />
<div align="center">
  <a href="https://github.com/SakuraTao2007/Narrator">
    <img src="images/logo.png" alt="Logo" width="80" height="80">
  </a>

<h3 align="center">Narrator</h3>

  <p align="center">
    一个易用、高效的剧情部署工具
    <br />
    <a href="https://github.com/SakuraTao2007/Narrator"><strong>帮助文档 »</strong></a>
    <br />
    <br />
    <a href="https://github.com/SakuraTao2007/Narrator/issues/new?labels=bug&template=bug-report---.md">报告问题</a>
    ·
    <a href="https://github.com/SakuraTao2007/Narrator/issues/new?labels=enhancement&template=feature-request---.md">功能请求</a>
  </p>
</div>

<!-- ABOUT THE PROJECT -->
## 关于 Narrator

<!-- [![Product Name Screen Shot][product-screenshot]](https://example.com) -->

Narrator 是一个剧情部署工具，可以快速部署你的剧情。提供了多种语法，以及原版特效的实现最大化。利用好 Narrator 将可以在 Minecraft 中打造出令人叹为观止的沉浸式剧情体验。
<p align="right">(<a href="#readme-top">回到顶部</a>)</p>



### 构建环境

*  JDK 21
<p align="right">(<a href="#readme-top">回到顶部</a>)</p>



<!-- GETTING STARTED -->
## 快速开始

Narrator 的部署以及运行是非常简单的，但是需要一些基础条件。

### 运行环境

Narrator 基于 Java 17 进行开发并且在 Minecraft 1.19.4 上进行了测试。
要想保证 Narrator 能够正常运行，需要满足以下条件：
* Java 17
* Minecraft 1.19.4
> [!CAUTION]
> Narrator 尚未验证在低于 1.19.4 版本的服务端运行 以及 使用低于 1.19.4 版本的客户端执行 Narrator 内容的可行性。


### 安装

1. 构建
2. 放入 `plugins` 文件夹中
3. 启动服务器
4. 在 `plugins/Narrator/chapters` 目录下编辑生成的 `ChapterExample.yml` 文件
5. 执行 `narrator reload(nr)`进行重载

<p align="right">(<a href="#readme-top">回到顶部</a>)</p>


<!-- USAGE EXAMPLES -->
## 使用

要想使用好 Narrator，学习它的语法并熟练掌握则是最好的方法。  
_你可以在这里找到完整的语法帮助文档： [帮助文档](https://sakuratao.top/Narrator)_

<p align="right">(<a href="#readme-top">回到顶部</a>)</p>



<!-- ROADMAP -->
## 开发路线

- [ ] 数学计算式
- [ ] NPC 
    - [ ] 待机文本
    - [ ] 顶部文本显示
- [ ] Content 辅助生成器
    - [ ] 线函数
    - [ ] 位点计算
    - [ ] Delay 计算
    - [ ] 待续
- [ ] 生成
  - [ ] Entity
  - [ ] Effect(EntityEffect)
  - [ ] Lightning这些
- [ ] NBT (可能不要)
- [ ] AI 助手 (游戏功能)
- [ ] 剧情演绎
    - [ ] 自动/手动
    - [X] 剧情暂停开关
    - [ ] 上一条 Content 的查看
    - [ ] 一定程度的流速调节 (提供 GUI 调节)
    - [ ] 并行线 (用于特效方面)
    - [ ] 文本点击 (聊天框/浮空字等)
    - [ ] 背包内容识别 (用于剧情)
    - [ ] 利用背包回答问题
    - [ ] 聊天框打印文本
- [ ] 函数式套娃识别
- [X] 给 CONDITION 提供临时卡断
- [ ] 实时天气
- [ ] 实时时间
- [ ] 场景动态构建
- [X] IP 地址获取 (多语言识别)


在 [此处](https://github.com/SakuraTao2007/Narrator/issues) 查看预计的新功能和已知问题的完整列表.

<p align="right">(<a href="#readme-top">回到顶部</a>)</p>
