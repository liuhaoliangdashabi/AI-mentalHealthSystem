# DTO 编写指南

> 面向 ai-springboot 项目。以 knowledge 模块为例，讲清楚 Command / Query / Response 三类 DTO 怎么推导、怎么写、哪里容易踩坑。
>
> 整理日期：2026-10-01

---

## 目录

- [一、本质：DTO 到底解决什么问题](#一本质dto-到底解决什么问题)
- [二、三种 DTO 和一条判据](#二三种-dto-和一条判据)
- [三、方法：三步推导，别凭空想](#三方法三步推导别凭空想)
- [四、实战：knowledge 模块的 4 个 DTO](#四实战knowledge-模块的-4-个-dto)
- [五、注意事项](#五注意事项)
- [六、附录：校验注解速查](#六附录校验注解速查)
- [七、写完自查清单](#七写完自查清单)

---

## 一、本质：DTO 到底解决什么问题

项目里数据要经过三个"容器"：

```
浏览器 ──JSON──► Controller ──CommandDTO──► Service ──Entity──► Mapper ──SQL──► DB
                    ▲                          │
                    └──────ResponseDTO─────────┘
```

| 容器 | 是什么 | 一句话 |
|---|---|---|
| **Entity** | 数据库的**镜子**，一个字段对一列 | **绝不外传** |
| **CommandDTO** | 前端**传进来**的数据 | 带校验注解 |
| **ResponseDTO** | 传给**前端**的数据 | 可以比 Entity 多 / 少字段 |

### 为什么不能直接返回 Entity

以 `knowledge_article` 为例，前端列表要这些字段：

```
id, title, categoryId, authorName, readCount, publishedAt, status
```

而表里只有：

```
id, title, category_id, author_id, read_count, published_at, status
                            ↑
                        存的是 ID
```

**`authorName` 表里根本没有** —— 它在 `user` 表里，要 join 才有。

三个理由，按重要性排：

1. **多字段** —— `authorName` / `categoryName` 是靠 join 拼出来的"视图字段"，Entity 里不该有
2. **少字段** —— `author_id`、`created_at`、`updated_at` 前端不需要，别浪费带宽
3. **防泄露** —— `User` 实体里有 `password`，直接返回 Entity 就漏了

---

## 二、三种 DTO 和一条判据

```
进来的 → Command    （前端 → 后端）
出去的 → Response   （后端 → 前端）
```

### 一条判据，永不出错

> **这个类是从 `@RequestBody` 里出来的，还是从 `return` 里出去的？**

```java
// 从 @RequestBody 出来 → command 包
public Result<?> create(@Valid @RequestBody ArticleCommandDTO cmd)

// 从 return 出去 → response 包
public Result<ArticleResponseDTO> detail(@PathVariable String id)
```

### 第三种，别漏

GET 请求没有 body，查询参数由 Spring 从 query string 绑定：

```java
// 没有 @RequestBody！但方向是"进来的" → 也放 command 包
public Result<Page<ArticleResponseDTO>> page(ArticlePageQueryDTO query)
```

名字叫 `Query` 只是提醒你"这是查询条件，不是提交内容"。

### 汇总

| 类 | 包 | 方向 | 数据从哪来 |
|---|---|---|---|
| `ArticleCommandDTO` | `DTO/command/` | 进来 | `@RequestBody` |
| `ArticlePageQueryDTO` | `DTO/command/` | 进来 | query string |
| `ArticleResponseDTO` | `DTO/response/` | 出去 | `return` |
| `CategoryResponseDTO` | `DTO/response/` | 出去 | `return` |

### 命名规矩

跟项目现有代码保持一致：

```
DTO/command/    XxxCommandDTO.java     UserRegisterCommandDTO（已有）
DTO/command/    XxxQueryDTO.java
DTO/response/   XxxResponseDTO.java    UserLoginResponseDTO（已有）
```

---

## 三、方法：三步推导，别凭空想

DTO 的字段**不是想出来的，是查出来的**。两个信息来源，做一次加减法。

### Step 1 · 读前端 —— 它要什么

前端的字段名是**写死的**，这是最硬的约束。看 `ai-vue/src/views/knowledge.vue` 和 `components/ArticleDialog.vue`：

```js
// 列表要（knowledge.vue:10-36）
scope.row.title
scope.row.categoryId          // 用来查 categoryMap 显示分类名
scope.row.authorName
scope.row.readCount
scope.row.publishedAt
scope.row.status              // 控制按钮显隐：0/2 显示"发布"，1 显示"下线"

// 详情要（ArticleDialog.vue:104-113，会被摊进表单）
Object.assign(formData, newVal)
// → title, content, coverImage, categoryId, summary, tags, id

// 提交要（ArticleDialog.vue:206-208）
submitData = { title, content, coverImage, categoryId, summary, tags, id }
```

### Step 2 · 读数据库 —— 有什么

```sql
-- knowledge_article
id           varchar(36)   文章ID(UUID)
category_id  bigint        分类ID
title        varchar(200)  文章标题
summary      text          文章摘要
content      longtext      文章内容
cover_image  varchar(500)  封面图片
tags         varchar(500)  标签
author_id    bigint        作者ID
read_count   int           阅读次数
status       tinyint       状态
published_at datetime      发布时间
created_at   datetime
updated_at   datetime
```

### Step 3 · 做加减法

```
表里有、前端不要    →  删掉                    author_id, created_at, updated_at
前端要、表里没有    →  加上，Convert 里填        authorName, categoryName
前端传了、表里没有  →  忽略                    id（新增时）, tagArray
```

**这张对照表做出来，DTO 就自己浮出来了。**

---

## 四、实战：knowledge 模块的 4 个 DTO

### ① `DTO/command/ArticleCommandDTO.java`

新增 / 修改共用。

```java
package org.example.aispringboot.DTO.command;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

@Data
public class ArticleCommandDTO {

    @NotBlank(message = "文章标题不能为空")
    @Size(max = 200, message = "文章标题不能超过200个字符")
    private String title;

    @NotNull(message = "分类不能为空")
    private Long categoryId;

    @NotBlank(message = "文章内容不能为空")
    @Size(max = 5000, message = "文章内容不能超过5000个字符")
    private String content;

    @Size(max = 1000, message = "文章摘要不能超过1000个字符")
    private String summary;

    @Size(max = 500, message = "封面路径不能超过500个字符")
    private String coverImage;

    @Size(max = 500, message = "标签不能超过500个字符")
    private String tags;
}
```

**注意三个"没有"：**

| 没有 | 为什么 |
|---|---|
| **没有 `@Builder`** | Command 是 Jackson 反序列化 new 出来的，不走 builder。加了没用，还容易被字段初始值坑（见 [五、注意事项 3](#五注意事项)） |
| **没有 `id`** | 新增时前端传的 UUID 要忽略；修改时 id 在 URL 路径上（`@PathVariable`） |
| **没有 `authorId`** | 作者从 token 拿（`@AuthenticationPrincipal`），**绝不能让前端传**，否则谁都能冒充别人发文章 |

### ② `DTO/command/ArticlePageQueryDTO.java`

分页查询条件。

```java
package org.example.aispringboot.DTO.command;

import lombok.Data;

@Data
public class ArticlePageQueryDTO {
    private Integer currentPage = 1;
    private Integer size = 5;
    private String  title;
    private Long    categoryId;
    private Integer status;
}
```

字段名必须和前端传的对上 —— `knowledge.vue:164` 传的就是 `{currentPage, size, ...formData}`。

> 这个类**不加 `@Builder`**，所以 `= 1` / `= 5` 的默认值正常生效。

### ③ `DTO/response/ArticleResponseDTO.java`

列表 + 详情共用。

```java
package org.example.aispringboot.DTO.response;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArticleResponseDTO {
    private String  id;
    private String  title;
    private String  content;
    private String  coverImage;
    private Long    categoryId;
    private Integer status;
    private Integer readCount;
    private String  summary;
    private String  tags;
    private LocalDateTime publishedAt;

    // 表里没有 —— 得 join 出来，在 Convert 里填
    private String categoryName;
    private String authorName;
}
```

**最后这两个字段，就是"为什么要有 Response DTO"的全部答案。**

### ④ `DTO/response/CategoryResponseDTO.java`

分类树。

```java
package org.example.aispringboot.DTO.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class CategoryResponseDTO {
    private Long   id;
    private String categoryName;
}
```

`knowledge.vue:144` 只用了 `item.id` 和 `item.categoryName`，给这两个就够，别多送。

> 注意：前端是 `data.map(item => ...)` 直接调的，**没有递归**。接口名叫 `tree`，但前端当**平铺列表**用 —— 你要么返回平铺的，要么得改前端。

---

## 五、注意事项

### 1. `@NotBlank` 只能用在 String 上

```java
// ❌ 错：Long 上用 @NotBlank
@NotBlank(message = "分类Id不能为空")
private Long categoryId;
```

`@NotBlank` 的校验器签名是 `ConstraintValidator<NotBlank, CharSequence>` —— **只认字符串**。用在 `Long` / `Integer` 上，Hibernate Validator 找不到校验器，直接抛：

```
jakarta.validation.UnexpectedTypeException: HV000030:
No validator could be found for constraint 'NotBlank' validating type 'java.lang.Long'
```

**这是崩溃，不是"不生效"。**

```java
// ✅ 对
@NotNull(message = "分类不能为空")
private Long categoryId;
```

### 2. `@Size` 的上限抄数据库列宽，别自己拍

```
title       varchar(200)  →  @Size(max = 200)
summary     text          →  @Size(max = 1000)   ← 前端 maxlength=1000，跟前端对齐
cover_image varchar(500)  →  @Size(max = 500)
tags        varchar(500)  →  @Size(max = 500)
```

- 拍**小**了：前端让用户填 200 字，后端 30 字就拒收，用户白填
- 拍**大**了：MySQL 报 `Data too long for column`，用户看到 500 错误

### 3. `@Builder` + 字段初始值 = 初始值失效

```java
@Builder
public class XxxDTO {
    private Integer size = 5;
}
```

```java
XxxDTO.builder().build().getSize();   // → null，不是 5！
```

`@Builder` 生成的 builder 完全绕过字段初始化。

**两种修法：**
- 加 `@Builder.Default`
- **或者干脆别加 `@Builder`**（Command / Query 推荐这条）

> 项目里现在就有一个：`UserRegisterCommandDTO:46` 的 `private Integer userType = 1;` 缺少 `@Builder.Default`，编译会打警告。目前没出问题只是因为 `UserService:86` 有一行 `commandDTO.setUserType(...)` 把它覆盖了 —— **是靠运气，不是靠设计**。

### 4. 前端传什么都不信

```java
private String id;        // 前端会传（封面上传的 UUID）→ 忽略
private Long   authorId;  // 绝对不能有
private Long   userId;    // 绝对不能有
```

身份类字段一律从 `@PathVariable` 或 token（`@AuthenticationPrincipal`）取。前端传了也当没看见。

### 5. GET 参数不加 `@RequestBody`

```java
// ❌ 400 Bad Request
public Result<?> page(@RequestBody ArticlePageQueryDTO query)

// ✅
public Result<?> page(ArticlePageQueryDTO query)
```

### 6. Response 的字段名跟前端对齐，不是跟数据库对齐

前端读 `categoryName`，你就得叫 `categoryName` —— 哪怕数据库列叫 `category_name`。

**这层翻译就是 Convert 干的活：**

```java
// ArticleResponseDTO 里叫 categoryName
.categoryName(categoryName)     // ← Convert 里把 join 出来的值填进去
```

---

## 六、附录：校验注解速查

| 注解 | 适用类型 | 作用 | 例子 |
|---|---|---|---|
| `@NotNull` | **任意类型** | 不能为 null | `@NotNull private Long categoryId;` |
| `@NotBlank` | **只能 String** | 不能为 null / 空串 / 纯空白 | `@NotBlank private String title;` |
| `@NotEmpty` | String / 集合 / 数组 | 不能为 null 且不能为空 | `@NotEmpty private List<String> tags;` |
| `@Size(min,max)` | String / 集合 / 数组 | 长度范围 | `@Size(max=200) private String title;` |
| `@Min(v)` / `@Max(v)` | 数字类型 | 数值范围 | `@Min(1) @Max(10) private Integer moodScore;` |
| `@Positive` | 数字类型 | 必须 > 0（ID 类字段常用） | `@Positive private Long authorId;` |
| `@Pattern(regexp)` | String | 正则**全匹配** | `@Pattern(regexp="AUTO\|MANUAL") private String taskType;` |
| `@Past` | 日期时间 | 必须是过去 | |
| `@PastOrPresent` | 日期时间 | 过去**或现在**（≤ 今天），**不含将来** | `@PastOrPresent private LocalDate diaryDate;` |
| `@Future` / `@FutureOrPresent` | 日期时间 | 将来 / 将来或现在 | |

**两个容易记混的：**

- `@NotBlank` **只能用在 String 上**，其他类型用 `@NotNull`
- `@PastOrPresent` 是 "Past **or** Present" —— **过去或现在**，名字里没有 Future

---

## 七、写完自查清单

对着这份清单逐条过：

- [ ] 包放对了吗？`@RequestBody` → command，`return` → response
- [ ] `Long` / `Integer` 上是不是误用了 `@NotBlank`？
- [ ] `@Size` 的上限和数据库列宽对上了吗？
- [ ] 加了 `@Builder` 的类里，有没有带初始值的字段？（有就加 `@Builder.Default`，或删掉 `@Builder`）
- [ ] Command 里有没有混进 `id` / `authorId` / `userId` 这类身份字段？
- [ ] Response 的字段名和前端读的名字**逐字**对上了吗？
- [ ] GET 的查询参数是不是误加了 `@RequestBody`？
- [ ] 日期字段用的是 `LocalDate` / `LocalDateTime`，不是 `java.util.Date`？

---

## 附：文件位置

```
ai-springboot/src/main/java/org/example/aispringboot/
├── DTO/
│   ├── command/
│   │   ├── ArticleCommandDTO.java
│   │   └── ArticlePageQueryDTO.java
│   └── response/
│       ├── ArticleResponseDTO.java
│       └── CategoryResponseDTO.java
```

**写完对着 `ai-vue/src/views/knowledge.vue` 和 `components/ArticleDialog.vue` 逐个字段核对一遍** —— 这一步比编译通过重要得多。
