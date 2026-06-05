USE exam_system;

-- =====================================================
-- 知识图谱种子数据 - Java程序设计基础课程
-- =====================================================

-- 插入课程
INSERT INTO `course` (`name`, `description`, `status`) VALUES
('Java程序设计基础', '本课程涵盖Java编程语言的基础知识，包括面向对象编程、常用API、异常处理、集合框架、IO流和多线程等内容。适合Java初学者系统学习。', 'published');

SET @course_id = LAST_INSERT_ID();

-- =====================================================
-- Ch01 Java入门 (5个知识点，全L1)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, 'Java语言概述与开发环境搭建', 'java-basic-01', 'L1',
'## 核心概念
Java是一种面向对象的编程语言，由Sun Microsystems（现Oracle）于1995年发布。Java的核心特性包括：跨平台性（Write Once, Run Anywhere）、面向对象、自动内存管理（垃圾回收）、强类型检查。

## 知识点范围
- Java语言的历史和版本演进（Java SE、Java EE、Java ME）
- Java的核心特性：跨平台原理（JVM）、面向对象、自动垃圾回收
- JDK、JRE、JVM三者的关系和区别
- 开发环境搭建：JDK安装、环境变量配置（JAVA_HOME、PATH）
- 第一个Java程序的编写、编译和运行流程
- Java程序的基本结构：类、main方法、语句

## 前置知识
无（这是Java学习的第一课）

## 常见误区
- 混淆JDK、JRE、JVM的概念和包含关系
- 以为Java是解释型语言（实际上是编译+解释混合）
- 环境变量配置错误导致命令无法识别
- 不理解public class与文件名必须一致的规则',
'Java,JDK,JRE,JVM,跨平台,环境搭建', 1),

(@course_id, NULL, '第一个Java程序（HelloWorld）', 'java-basic-02', 'L1',
'## 核心概念
HelloWorld是学习任何编程语言的第一个程序，它展示了程序的基本结构和运行流程。通过这个简单的程序，可以理解Java程序的编译和执行过程。

## 知识点范围
- Java程序的基本结构：包声明、类定义、main方法
- main方法的标准写法：public static void main(String[] args)
- System.out.println()输出语句的使用
- Java程序的编译过程：javac命令
- Java程序的执行过程：java命令
- 常见的编译错误和运行时错误

## 前置知识
必须先完成Java开发环境搭建（java-basic-01）

## 常见误区
- main方法签名写错（缺少static、参数类型错误）
- 类名与文件名不一致
- 忘记加分号
- 中英文标点符号混淆',
'HelloWorld,main方法,编译,运行', 2),

(@course_id, NULL, '变量与数据类型', 'java-basic-03', 'L1',
'## 核心概念
变量是存储数据的容器，每个变量都有特定的数据类型。Java是强类型语言，变量必须先声明后使用。数据类型分为基本数据类型（8种）和引用数据类型。

## 知识点范围
- 变量的声明和初始化
- 8种基本数据类型：byte、short、int、long、float、double、char、boolean
- 基本数据类型的取值范围和默认值
- 类型转换：自动类型转换（隐式）和强制类型转换（显式）
- 字面量：整数字面量、浮点字面量、字符字面量、字符串字面量
- 变量的作用域和生命周期
- 常量的定义：final关键字

## 前置知识
必须先理解HelloWorld程序的基本结构（java-basic-02）

## 常见误区
- 整数溢出问题（int最大值约21亿）
- 浮点数精度问题（0.1 + 0.2 != 0.3）
- char类型占用2个字节（支持Unicode）
- String不是基本数据类型，是引用类型
- 强制类型转换可能导致数据丢失',
'变量,数据类型,基本类型,类型转换,常量', 3),

(@course_id, NULL, '运算符与表达式', 'java-basic-04', 'L1',
'## 核心概念
运算符用于对变量和值进行操作，表达式由运算符和操作数组成。Java提供了丰富的运算符类型，包括算术、关系、逻辑、赋值、位运算等。

## 知识点范围
- 算术运算符：+、-、*、/、%、++、--
- 关系运算符：==、!=、>、<、>=、<=
- 逻辑运算符：&&、||、!
- 赋值运算符：=、+=、-=、*=、/=
- 位运算符：&、|、^、~、<<、>>、>>>
- 三元运算符：条件 ? 值1 : 值2
- 运算符优先级和结合性
- 自动类型提升规则

## 前置知识
必须先理解变量和数据类型（java-basic-03）

## 常见误区
- ==和equals的区别（==比较地址，equals比较内容）
- ++i和i++的区别（前置先加后用，后置先用后加）
- 短路求值：&&和||的短路特性
- 整数除法结果仍是整数（5/2=2）
- 运算符优先级记忆错误',
'运算符,表达式,算术,逻辑,位运算', 4),

(@course_id, NULL, '流程控制（if/switch/for/while）', 'java-basic-05', 'L1',
'## 核心概念
流程控制语句用于控制程序的执行顺序。Java提供了三种基本的流程控制结构：顺序结构、选择结构（if/switch）、循环结构（for/while/do-while）。

## 知识点范围
- if语句：单分支、双分支、多分支
- if语句的嵌套使用
- switch语句：case穿透、break的作用、default
- for循环：标准for循环、增强for循环（for-each）
- while循环和do-while循环的区别
- break和continue的区别
- 循环的嵌套使用
- 无限循环和如何避免

## 前置知识
必须先理解变量、数据类型和运算符（java-basic-03、java-basic-04）

## 常见误区
- if条件后加分号导致逻辑错误
- switch忘记写break导致case穿透
- 循环变量的作用域问题
- 死循环的产生和避免
- break只跳出当前循环，不是跳出所有循环',
'流程控制,if,switch,for,while,break,continue', 5);

-- =====================================================
-- Ch02 面向对象基础 (7个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, '类与对象', 'java-oop-01', 'L1',
'## 核心概念
类是Java中对象的模板（蓝图），定义了对象的属性（成员变量）和行为（方法）。对象是类的具体实例，通过new关键字创建。理解类与对象的关系是面向对象编程的第一步。

## 知识点范围
- 类的定义语法（class关键字）
- 成员变量（属性）的声明
- 成员方法（行为）的定义
- 使用new关键字创建对象
- 通过对象引用访问属性和方法
- 栈内存与堆内存中对象的存储模型
- 对象引用的传递机制

## 前置知识
必须先掌握Java基础语法（java-basic-05）

## 常见误区
- 混淆类和对象：类是抽象的模板，对象是具体的实例
- 以为一个类只能创建一个对象
- 不理解对象引用变量和对象的区别
- 误以为方法必须写在main方法里
- 忘记使用new关键字创建对象',
'类,对象,实例化,成员变量,成员方法,new关键字', 6),

(@course_id, NULL, '构造方法', 'java-oop-02', 'L2',
'## 核心概念
构造方法是创建对象时自动调用的特殊方法。方法名必须与类名完全相同，没有返回值类型（连void都没有）。每个类至少有一个构造方法：如果没写，编译器自动添加无参构造方法；如果写了任何构造方法，默认构造方法消失。

## 知识点范围
- 构造方法的定义语法
- 默认构造方法与自定义构造方法的关系
- 构造方法的重载（多个构造方法，参数列表不同）
- 在构造方法中使用this()调用另一个构造方法
- 构造方法中的初始化代码
- 构造方法与普通方法的区别

## 前置知识
必须先理解"类与对象"的基本概念（java-oop-01）

## 常见误区
- 以为构造方法有返回值（它连void都不是）
- 不理解为什么写了有参构造方法后new 类名()会报错
- this()调用另一个构造方法时错误放在非首行
- 混淆构造方法和普通成员方法的调用时机',
'构造方法,默认构造方法,重载,this()', 7),

(@course_id, NULL, '封装与访问修饰符', 'java-oop-03', 'L2',
'## 核心概念
封装是面向对象的三大特性之一，它将数据（属性）和操作数据的方法捆绑在一起，并通过访问修饰符控制外部对内部数据的访问。封装的目的是隐藏实现细节，提供公共接口。

## 知识点范围
- 封装的概念和意义
- 四种访问修饰符：private、default（无修饰符）、protected、public
- 各种修饰符的访问范围（同类、同包、子类、其他）
- getter/setter方法的标准写法
- JavaBean规范
- this关键字在setter中的使用

## 前置知识
必须先理解类与对象（java-oop-01）和构造方法（java-oop-02）

## 常见误区
- 以为private就是完全不能访问（通过getter/setter可以）
- 不理解default修饰符（同包访问）
- 混淆protected和public的区别
- getter/setter方法命名不规范',
'封装,访问修饰符,private,public,getter,setter,JavaBean', 8),

(@course_id, NULL, 'this关键字', 'java-oop-04', 'L2',
'## 核心概念
this关键字代表当前对象的引用。它主要用于：区分成员变量和局部变量（当两者同名时）、在构造方法中调用另一个构造方法（this()）、作为参数传递当前对象。

## 知识点范围
- this的本质：当前对象的引用
- 使用this区分同名的成员变量和局部变量
- 使用this()调用其他构造方法（必须在第一行）
- this作为方法参数传递
- this作为返回值（链式调用）

## 前置知识
必须先理解构造方法（java-oop-02）

## 常见误区
- 在静态方法中使用this（编译错误）
- this()调用不在构造方法第一行
- 不理解this代表的是对象而不是类
- 滥用this，当没有命名冲突时不需要使用',
'this关键字,当前对象引用,构造方法调用', 9),

(@course_id, NULL, 'static关键字', 'java-oop-05', 'L2',
'## 核心概念
static关键字表示"静态的"，用于修饰成员变量、成员方法、代码块和内部类。静态成员属于类本身，而不是类的实例。可以通过类名直接访问，不需要创建对象。

## 知识点范围
- 静态变量（类变量）：所有对象共享同一份数据
- 静态方法（类方法）：不能访问非静态成员
- 静态代码块：类加载时执行，只执行一次
- 静态变量与实例变量的区别
- 静态方法与实例方法的区别
- static与this的关系（静态方法中不能使用this）

## 前置知识
必须先理解类与对象（java-oop-01）

## 常见误区
- 在静态方法中访问非静态成员（编译错误）
- 不理解静态变量的共享特性
- 静态代码块的执行时机理解错误
- 以为static修饰的变量是常量（final才是常量）',
'static,静态变量,静态方法,静态代码块,类变量', 10),

(@course_id, NULL, '继承 (extends)', 'java-oop-06', 'L2',
'## 核心概念
继承是子类获得父类属性和方法的机制，使用extends关键字实现。Java只支持单继承（一个类只能有一个直接父类），但可以多层继承。继承是代码复用的重要手段，也是多态的基础。

## 知识点范围
- 继承的语法：class 子类 extends 父类
- 子类可以访问父类的非private成员
- 方法重写（Override）：子类重新定义父类的方法
- super关键字：访问父类的构造方法、属性和方法
- 继承中的构造方法调用顺序
- Object类：所有类的根父类
- 继承的优缺点

## 前置知识
必须先理解构造方法（java-oop-02）和封装（java-oop-03）

## 常见误区
- 以为Java支持多继承（实际上只有单继承）
- 子类构造方法中忘记调用super()
- 方法重写时访问权限不能更严格
- 不理解super()和this()不能同时出现',
'继承,extends,方法重写,super,Object类', 11),

(@course_id, NULL, '多态 (polymorphism)', 'java-oop-07', 'L2',
'## 核心概念
多态是同一行为在不同对象上表现出不同形态。Java中多态的实现条件：有继承关系、子类重写父类方法、父类引用指向子类对象（向上转型）。多态是面向对象最强大的特性之一。

## 知识点范围
- 多态的概念和实现条件
- 向上转型：父类引用指向子类对象
- 向下转型：强制转换回子类类型
- instanceof关键字：判断对象的实际类型
- 多态的方法调用规则（编译看左边，运行看右边）
- 多态的好处：提高代码扩展性
- 多态的局限：无法访问子类特有方法

## 前置知识
必须先理解继承（java-oop-06）

## 常见误区
- 不理解"编译看左边，运行看右边"
- 向下转型时没有使用instanceof检查
- 以为多态就是方法重载（重载是编译时多态，重写是运行时多态）
- 不理解动态绑定机制',
'多态,向上转型,向下转型,instanceof,动态绑定', 12);

-- =====================================================
-- Ch03 面向对象进阶 (5个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, '抽象类 (abstract)', 'java-oop-08', 'L2',
'## 核心概念
抽象类是不能被实例化的类，用于定义一组相关类的共同模板。抽象类可以包含抽象方法（没有方法体）和具体方法。子类必须实现所有抽象方法，否则子类也必须声明为抽象类。

## 知识点范围
- 抽象类的定义：abstract class
- 抽象方法的定义：没有方法体
- 抽象类的特点：不能实例化、可以有构造方法
- 抽象类与普通类的区别
- 抽象方法的实现规则
- 抽象类作为类型多态

## 前置知识
必须先理解多态（java-oop-07）

## 常见误区
- 以为抽象类不能有构造方法
- 抽象方法没有方法体但忘记写分号
- 子类没有实现所有抽象方法却不是抽象类
- 以为抽象类完全不能实例化（匿名内部类可以）',
'抽象类,abstract,抽象方法,模板模式', 13),

(@course_id, NULL, '接口 (interface)', 'java-oop-09', 'L2',
'## 核心概念
接口是一种特殊的引用类型，是方法声明的集合。类通过implements关键字实现接口，必须实现接口中的所有抽象方法。Java支持一个类实现多个接口，这是实现多继承的方式。

## 知识点范围
- 接口的定义：interface
- 接口中的方法默认是public abstract
- 接口中的变量默认是public static final
- 类实现接口：implements
- 接口的多实现
- 接口的继承（接口可以继承多个接口）
- JDK8新特性：default方法和static方法
- 接口与抽象类的区别

## 前置知识
必须先理解抽象类（java-oop-08）

## 常见误区
- 以为接口中的方法可以有方法体（JDK8之前）
- 不理解接口变量的public static final特性
- 混淆implements和extends
- 以为接口不能有default方法（JDK8可以）',
'接口,interface,implements,多实现,default方法', 14),

(@course_id, NULL, '内部类', 'java-oop-10', 'L3',
'## 核心概念
内部类是定义在另一个类内部的类。Java支持四种内部类：成员内部类、静态内部类、局部内部类、匿名内部类。内部类可以访问外部类的私有成员，是实现回调和事件处理的重要机制。

## 知识点范围
- 成员内部类：可以访问外部类的所有成员
- 静态内部类：使用static修饰，只能访问外部类的静态成员
- 局部内部类：定义在方法中，只在方法内可见
- 匿名内部类：没有名字的内部类，常用于接口实现
- 内部类的使用场景和最佳实践
- Lambda表达式与函数式接口（JDK8）

## 前置知识
必须先理解接口（java-oop-09）

## 常见误区
- 不理解成员内部类持有外部类引用
- 匿名内部类访问局部变量必须是final或等效final
- 静态内部类不能访问外部类的非静态成员
- 滥用内部类导致代码可读性下降',
'内部类,匿名内部类,静态内部类,Lambda', 15),

(@course_id, NULL, '枚举 (enum)', 'java-oop-11', 'L2',
'## 核心概念
枚举是一种特殊的类，用于定义一组固定的常量。枚举类型是类型安全的，编译器会检查枚举值的合法性。枚举可以有构造方法、成员变量和方法。

## 知识点范围
- 枚举的定义：enum关键字
- 枚举常量的声明
- 枚举的构造方法（必须是private）
- 枚举的成员变量和方法
- 枚举的常用方法：values()、valueOf()、ordinal()
- 枚举在switch中的使用
- 枚举实现接口
- 枚举单例模式

## 前置知识
必须先理解类与对象（java-oop-01）

## 常见误区
- 以为枚举不能有构造方法
- 枚举构造方法不是private导致可以创建新实例
- 不理解枚举常量实际上是枚举类的实例
- 枚举比较使用==而不是equals',
'枚举,enum,常量,单例模式', 16),

(@course_id, NULL, '包装类', 'java-oop-12', 'L2',
'## 核心概念
包装类是将基本数据类型封装成对象的类。Java为8种基本数据类型提供了对应的包装类。包装类的主要用途：集合框架中存储基本类型、提供类型转换方法、自动装箱和拆箱。

## 知识点范围
- 8种包装类：Byte、Short、Integer、Long、Float、Double、Character、Boolean
- 自动装箱：基本类型 → 包装类
- 自动拆箱：包装类 → 基本类型
- 包装类的常用方法：parseInt()、valueOf()、toString()
- Integer缓存机制（-128到127）
- 包装类的比较问题

## 前置知识
必须先理解基本数据类型（java-basic-03）

## 常见误区
- Integer比较使用==时缓存范围内的结果可能相等
- 自动拆箱时可能抛出NullPointerException
- 不理解自动装箱和拆箱的时机
- 包装类的默认值是null而不是0',
'包装类,自动装箱,自动拆箱,Integer缓存', 17);

-- =====================================================
-- Ch04 常用API (6个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, 'String类', 'java-api-01', 'L1',
'## 核心概念
String类表示不可变的字符序列。一旦创建，String对象的内容不能修改。String是Java中最常用的类之一，理解它的不可变性和常量池机制对写出高效代码至关重要。

## 知识点范围
- String的创建方式：字面量和new
- 字符串常量池的概念和作用
- String的不可变性及其原因
- String的常用方法：length()、charAt()、substring()、indexOf()、trim()、equals()
- 字符串比较：==和equals的区别
- 字符串拼接：+运算符和StringBuilder
- String与byte[]、char[]的转换

## 前置知识
必须先理解基本数据类型和引用类型（java-basic-03）

## 常见误区
- 使用==比较字符串内容（应该用equals）
- 不理解字符串常量池的复用机制
- 频繁拼接字符串使用+而不是StringBuilder
- substring方法的索引越界问题',
'String,字符串,常量池,不可变,字符串比较', 18),

(@course_id, NULL, 'StringBuilder/StringBuffer', 'java-api-02', 'L2',
'## 核心概念
StringBuilder和StringBuffer是可变的字符序列，用于高效地进行字符串拼接操作。StringBuilder是非线程安全的，性能更好；StringBuffer是线程安全的，性能稍差。单线程环境下优先使用StringBuilder。

## 知识点范围
- StringBuilder的创建和使用
- StringBuilder的常用方法：append()、insert()、delete()、reverse()
- StringBuilder与String的性能对比
- StringBuffer与StringBuilder的区别
- 链式调用的实现原理（返回this）
- String、StringBuilder、StringBuffer的选择

## 前置知识
必须先理解String类（java-api-01）

## 常见误区
- 频繁拼接字符串使用String而不是StringBuilder
- 不理解StringBuilder的可变性
- 在多线程环境下使用StringBuilder（应该用StringBuffer）
- 以为StringBuilder和String可以直接互相赋值',
'StringBuilder,StringBuffer,字符串拼接,可变字符序列', 19),

(@course_id, NULL, 'Math类与Random类', 'java-api-03', 'L1',
'## 核心概念
Math类提供了数学运算的工具方法，包括绝对值、最大最小值、幂运算、三角函数等。Random类用于生成随机数。这两个类都是工具类，提供了大量静态方法供直接调用。

## 知识点范围
- Math类的常用方法：abs()、max()、min()、pow()、sqrt()、random()
- Math.PI和Math.E常量
- Random类的创建和使用
- 生成指定范围的随机数
- Math.random()与Random类的区别
- 随机数的种子概念

## 前置知识
必须先理解基本数据类型和运算符（java-basic-03、java-basic-04）

## 常见误区
- Math.random()返回的是double类型（0.0到1.0）
- Random类的nextInt(n)返回0到n-1的值
- 不理解随机数种子的作用
- 以为Math.random()是真正的随机数（伪随机）',
'Math,Random,随机数,数学运算', 20),

(@course_id, NULL, 'Date与Calendar', 'java-api-04', 'L2',
'## 核心概念
Date类表示特定的瞬间，精确到毫秒。Calendar类提供了日历字段之间的转换和操作。Java 8引入了新的日期时间API（LocalDate、LocalTime、LocalDateTime），推荐使用新API。

## 知识点范围
- Date类的创建和使用
- SimpleDateFormat的日期格式化
- Calendar类的常用方法
- Java 8新日期API：LocalDate、LocalTime、LocalDateTime
- 日期的格式化和解析
- 日期的计算和比较

## 前置知识
必须先理解类与对象（java-oop-01）

## 常见误区
- Date类的很多方法已废弃（应该用Calendar或新API）
- SimpleDateFormat不是线程安全的
- 月份从0开始计数（Calendar中）
- 时区问题导致日期计算错误',
'Date,Calendar,日期时间,SimpleDateFormat,LocalDate', 21),

(@course_id, NULL, 'Object类（toString/equals）', 'java-api-05', 'L2',
'## 核心概念
Object类是所有Java类的根父类，所有类都直接或间接继承自Object。toString()和equals()是Object类中最重要的两个方法，通常需要根据业务需求重写。

## 知识点范围
- Object类的地位和作用
- toString()方法：默认返回类名@哈希值，通常重写为返回对象信息
- equals()方法：默认比较地址，通常重写为比较内容
- hashCode()方法：与equals()的契约
- 重写equals()必须同时重写hashCode()
- getClass()方法
- clone()方法

## 前置知识
必须先理解继承（java-oop-06）

## 常见误区
- 重写equals()不重写hashCode()（导致集合类行为异常）
- 不理解==和equals的区别
- toString()返回的信息没有实际意义
- equals()方法没有处理null参数',
'Object,toString,equals,hashCode,根父类', 22),

(@course_id, NULL, 'Objects工具类', 'java-api-06', 'L2',
'## 核心概念
Objects类是Java 7引入的工具类，提供了操作对象的静态方法。Objects类的方法都是null安全的，可以避免NullPointerException。

## 知识点范围
- Objects.requireNonNull()：检查对象是否为null
- Objects.equals()：null安全的equals比较
- Objects.hashCode()：安全的hashCode计算
- Objects.toString()：安全的toString调用
- Objects.isNull()和Objects.nonNull()
- Objects.compare()：安全的比较

## 前置知识
必须先理解Object类（java-api-05）

## 常见误区
- 手动进行null检查而不是使用Objects工具类
- 不理解Objects方法的null安全特性
- 在需要null安全的场景使用普通方法',
'Objects,工具类,null安全,空指针', 23);

-- =====================================================
-- Ch05 异常处理 (4个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, '异常的概念与分类', 'java-exception-01', 'L2',
'## 核心概念
异常是程序运行中发生的非正常事件。Java异常体系结构：Throwable是所有异常的根类，分为Error（系统级错误，不可处理）和Exception（可处理异常）。Exception又分为受检异常（必须处理）和非受检异常（RuntimeException及其子类）。

## 知识点范围
- 异常的概念和作用
- Java异常体系结构：Throwable → Error + Exception
- Error与Exception的区别
- 受检异常（Checked Exception）与非受检异常（Unchecked Exception）
- 常见的运行时异常：NullPointerException、ArrayIndexOutOfBoundsException、ClassCastException
- 异常处理的意义：提高程序健壮性

## 前置知识
必须先理解继承（java-oop-06）

## 常见误区
- 以为Error可以捕获处理（通常不应该捕获）
- 不区分受检异常和非受检异常
- 异常处理过于宽泛（catch Exception）
- 忽略异常（空catch块）',
'异常,Error,Exception,RuntimeException,受检异常', 24),

(@course_id, NULL, 'try-catch-finally语法', 'java-exception-02', 'L2',
'## 核心概念
try-catch-finally是Java异常处理的标准语法。try块中放置可能抛出异常的代码，catch块捕获并处理异常，finally块中的代码无论是否发生异常都会执行（通常用于资源释放）。

## 知识点范围
- try-catch的基本语法
- 多个catch块的使用和顺序规则
- finally块的执行时机和作用
- try-with-resources语句（JDK7）
- 异常信息的获取：getMessage()、printStackTrace()
- 异常处理的最佳实践

## 前置知识
必须先理解异常的概念与分类（java-exception-01）

## 常见误区
- finally块中的return会覆盖try或catch中的return
- catch块的顺序问题（子类异常在前）
- 资源没有在finally中关闭
- try块后直接跟finally（没有catch）',
'try-catch-finally,异常处理,try-with-resources,资源释放', 25),

(@course_id, NULL, 'throw与throws', 'java-exception-03', 'L2',
'## 核心概念
throw用于在方法中手动抛出一个异常对象。throws用于方法声明，表示该方法可能抛出的异常类型。throw是语句，throws是方法修饰。

## 知识点范围
- throw的使用：throw new ExceptionType()
- throws的使用：在方法签名后声明
- throw与throws的区别
- 自定义异常的抛出
- 异常的传播机制
- 重写方法时的异常声明规则

## 前置知识
必须先理解try-catch-finally（java-exception-02）

## 常见误区
- 混淆throw和throws
- throw后直接写异常类名（需要new）
- 方法中抛出受检异常但没有用throws声明
- 重写方法时声明了比父类更宽泛的异常',
'throw,throws,异常抛出,异常声明', 26),

(@course_id, NULL, '自定义异常', 'java-exception-04', 'L3',
'## 核心概念
自定义异常是用户根据业务需求创建的异常类。通常继承Exception（受检异常）或RuntimeException（非受检异常）。自定义异常应该提供多个构造方法，并包含有意义的异常信息。

## 知识点范围
- 自定义异常的创建方式
- 继承Exception vs RuntimeException
- 提供多个构造方法
- 异常信息的设计规范
- 异常链（异常转译）
- 异常处理策略：抛出还是捕获

## 前置知识
必须先理解throw与throws（java-exception-03）

## 常见误区
- 自定义异常没有提供无参构造方法
- 异常信息不够详细
- 滥用异常控制业务流程
- 不使用异常链丢失原始异常信息',
'自定义异常,Exception,RuntimeException,异常链', 27);

-- =====================================================
-- Ch06 集合框架 (6个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, '集合框架概述', 'java-collection-01', 'L2',
'## 核心概念
集合框架是Java提供的一组用于存储和操作对象的类和接口。集合框架的主要接口：Collection（单列集合）和Map（双列集合）。Collection下有List（有序可重复）、Set（无序不重复）、Queue（队列）。

## 知识点范围
- 集合框架的体系结构
- Collection接口的常用方法
- List接口的特点：有序、可重复
- Set接口的特点：无序、不重复
- Queue接口的特点：先进先出
- Map接口的特点：键值对映射
- 迭代器（Iterator）模式
- 泛型在集合中的应用

## 前置知识
必须先理解继承和接口（java-oop-06、java-oop-09）

## 常见误区
- 混淆Collection和Collections（工具类）
- 不理解Set的去重原理（equals和hashCode）
- 集合中存储基本类型需要使用包装类
- 在遍历时修改集合导致ConcurrentModificationException',
'集合框架,Collection,List,Set,Map,Iterator', 28),

(@course_id, NULL, 'ArrayList', 'java-collection-02', 'L2',
'## 核心概念
ArrayList是基于动态数组实现的List接口。它支持随机访问（通过索引），查询效率高，但插入和删除效率较低（需要移动元素）。ArrayList是线程不安全的。

## 知识点范围
- ArrayList的底层实现：Object数组
- ArrayList的创建和初始化
- 常用方法：add()、get()、set()、remove()、size()
- ArrayList的扩容机制
- ArrayList与LinkedList的区别
- ArrayList的遍历方式
- ArrayList的性能特点

## 前置知识
必须先理解集合框架概述（java-collection-01）

## 常见误区
- 在ArrayList中间频繁插入元素（性能差）
- 删除元素时索引变化问题
- 不理解ArrayList的默认容量和扩容
- 使用ArrayList而不是LinkedList进行频繁插入删除',
'ArrayList,动态数组,随机访问,扩容机制', 29),

(@course_id, NULL, 'LinkedList', 'java-collection-03', 'L2',
'## 核心概念
LinkedList是基于双向链表实现的List接口。它支持高效的插入和删除操作，但随机访问效率较低（需要遍历）。LinkedList还实现了Deque接口，可以作为队列或栈使用。

## 知识点范围
- LinkedList的底层实现：双向链表
- LinkedList的创建和使用
- 作为List使用：add、get、remove
- 作为Deque使用：offer、poll、peek
- 作为Queue使用：先进先出
- ArrayList与LinkedList的选择
- LinkedList的性能特点

## 前置知识
必须先理解ArrayList（java-collection-02）

## 常见误区
- 使用LinkedList进行随机访问（性能差）
- 不理解LinkedList的内存结构
- 以为LinkedList比ArrayList在所有场景都慢
- 使用get(index)遍历LinkedList（应该用迭代器）',
'LinkedList,双向链表,Deque,队列', 30),

(@course_id, NULL, 'HashSet与HashMap', 'java-collection-04', 'L3',
'## 核心概念
HashSet是基于HashMap实现的Set接口，用于存储不重复的元素。HashMap是基于哈希表实现的Map接口，用于存储键值对。理解哈希表的原理对于正确使用这两个类至关重要。

## 知识点范围
- HashSet的底层实现：HashMap
- HashSet的去重原理：hashCode()和equals()
- HashMap的底层实现：数组+链表+红黑树（JDK8）
- HashMap的常用方法：put()、get()、remove()、containsKey()
- HashMap的扩容机制
- 哈希冲突的解决方法
- HashSet和HashMap的性能特点

## 前置知识
必须先理解集合框架概述（java-collection-01）

## 常见误区
- 自定义类作为键没有重写hashCode()和equals()
- 不理解HashMap的初始容量和负载因子
- 在多线程环境下使用HashMap（不安全）
- 以为HashSet是有序的',
'HashSet,HashMap,哈希表,hashCode,equals', 31),

(@course_id, NULL, 'TreeSet与TreeMap', 'java-collection-05', 'L3',
'## 核心概念
TreeSet是基于TreeMap实现的Set接口，元素按自然顺序或自定义顺序排序。TreeMap是基于红黑树实现的Map接口，键按自然顺序或自定义顺序排序。它们都是有序集合。

## 知识点范围
- TreeSet的底层实现：红黑树
- TreeSet的排序规则：自然排序（Comparable）和定制排序（Comparator）
- TreeMap的底层实现：红黑树
- TreeMap的常用方法
- Comparable接口的实现
- Comparator接口的使用
- TreeSet和TreeMap的性能特点

## 前置知识
必须先理解HashSet和HashMap（java-collection-04）

## 常见误区
- 元素没有实现Comparable接口导致ClassCastException
- 混淆Comparable和Comparator的使用场景
- 不理解红黑树的平衡特性
- 以为TreeSet和HashMap一样快（TreeSet是O(log n)）',
'TreeSet,TreeMap,红黑树,Comparable,Comparator', 32),

(@course_id, NULL, 'Collections工具类', 'java-collection-06', 'L2',
'## 核心概念
Collections是操作集合的工具类，提供了排序、查找、同步、不可修改等静态方法。它类似于Arrays工具类，但专门用于集合操作。

## 知识点范围
- 排序方法：sort()、reverse()、shuffle()
- 查找方法：binarySearch()、frequency()
- 同步方法：synchronizedList()、synchronizedMap()
- 不可修改方法：unmodifiableList()、unmodifiableMap()
- 其他常用方法：max()、min()、fill()、copy()
- 集合的线程安全包装

## 前置知识
必须先理解集合框架概述（java-collection-01）

## 常见误区
- 对未排序的集合使用binarySearch()
- 不理解同步包装的局限性
- 以为Collections是Collection的子类
- 使用Arrays.asList()返回的List不能修改',
'Collections,工具类,排序,查找,线程安全', 33);

-- =====================================================
-- Ch07 IO流 (4个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, 'File类', 'java-io-01', 'L2',
'## 核心概念
File类代表文件系统中的文件或目录路径。它可以用于创建、删除、重命名文件/目录，以及获取文件属性。File类不能用于读写文件内容（需要使用IO流）。

## 知识点范围
- File类的构造方法
- 文件属性方法：exists()、isFile()、isDirectory()、length()
- 文件操作方法：createNewFile()、delete()、mkdir()、renameTo()
- 目录操作方法：list()、listFiles()
- 路径分隔符：File.separator
- 绝对路径与相对路径
- 文件过滤器（FilenameFilter）

## 前置知识
必须先理解类与对象（java-oop-01）

## 常见误区
- 以为File类可以读写文件内容
- 路径分隔符在不同操作系统不同
- 创建文件前需要先创建目录
- delete()方法只能删除空目录',
'File,文件操作,目录操作,路径', 34),

(@course_id, NULL, '字节流 (InputStream/OutputStream)', 'java-io-02', 'L2',
'## 核心概念
字节流是按字节（8位）读写数据的IO流。InputStream是所有字节输入流的父类，OutputStream是所有字节输出流的父类。字节流可以处理任何类型的文件（文本、图片、视频等）。

## 知识点范围
- InputStream的常用方法：read()、available()、close()
- OutputStream的常用方法：write()、flush()、close()
- FileInputStream和FileOutputStream
- 缓冲字节流：BufferedInputStream和BufferedOutputStream
- 字节流的使用步骤：创建、读写、关闭
- try-with-resources自动关闭流

## 前置知识
必须先理解File类（java-io-01）

## 常见误区
- 使用字节流读取文本文件可能出现乱码
- 忘记关闭流导致资源泄漏
- 不使用缓冲流导致性能问题
- read()方法返回-1表示读取完毕',
'字节流,InputStream,OutputStream,FileInputStream,FileOutputStream', 35),

(@course_id, NULL, '字符流 (Reader/Writer)', 'java-io-03', 'L2',
'## 核心概念
字符流是按字符（16位）读写数据的IO流，专门用于处理文本文件。Reader是所有字符输入流的父类，Writer是所有字符输出流的父类。字符流内部使用了编码转换器。

## 知识点范围
- Reader的常用方法：read()、read(char[])
- Writer的常用方法：write()、flush()、close()
- InputStreamReader和OutputStreamWriter（转换流）
- FileReader和FileWriter
- 缓冲字符流：BufferedReader和BufferedWriter
- 编码问题：UTF-8、GBK
- PrintWriter：格式化输出

## 前置知识
必须先理解字节流（java-io-02）

## 常见误区
- 使用字节流处理文本文件（应该用字符流）
- 不指定编码导致乱码
- Writer没有flush导致数据没有写入
- BufferedReader.readLine()返回null表示读取完毕',
'字符流,Reader,Writer,BufferedReader,编码', 36),

(@course_id, NULL, '缓冲流与转换流', 'java-io-04', 'L3',
'## 核心概念
缓冲流为IO流增加了缓冲区功能，可以显著提高IO性能。转换流用于在字节流和字符流之间进行转换，可以指定编码格式。缓冲流和转换流是IO流的高级用法。

## 知识点范围
- 缓冲流的原理：减少IO次数
- BufferedInputStream/BufferedOutputStream
- BufferedReader/BufferedWriter
- 转换流的作用：字节→字符、字符→字节
- InputStreamReader/OutputStreamWriter
- 对象序列化：Serializable接口
- ObjectOutputStream/ObjectInputStream

## 前置知识
必须先理解字符流（java-io-03）

## 常见误区
- 以为缓冲流会自动刷新（需要手动flush或close）
- 不理解转换流的编码参数
- 序列化对象没有实现Serializable接口
- transient关键字的作用',
'缓冲流,转换流,序列化,Serializable', 37);

-- =====================================================
-- Ch08 多线程 (3个知识点)
-- =====================================================

INSERT INTO `knowledge_point` (`course_id`, `parent_id`, `name`, `code`, `difficulty`, `description`, `keywords`, `sort_order`) VALUES
(@course_id, NULL, '线程的概念与创建', 'java-thread-01', 'L3',
'## 核心概念
线程是程序执行的最小单位，一个进程可以包含多个线程。Java提供了两种创建线程的方式：继承Thread类和实现Runnable接口。线程的生命周期包括：新建、就绪、运行、阻塞、死亡。

## 知识点范围
- 进程与线程的区别
- 线程的创建方式一：继承Thread类
- 线程的创建方式二：实现Runnable接口
- 线程的创建方式三：实现Callable接口（有返回值）
- 线程的生命周期和状态
- 线程的启动：start()方法
- 线程的常用方法：sleep()、join()、yield()

## 前置知识
必须先理解接口（java-oop-09）和Object类（java-api-05）

## 常见误区
- 直接调用run()方法而不是start()
- 不理解线程的随机性
- 以为多线程一定比单线程快
- 线程安全问题的忽视',
'线程,Thread,Runnable,多线程,线程创建', 38),

(@course_id, NULL, '线程同步与锁', 'java-thread-02', 'L3',
'## 核心概念
线程同步是解决多线程安全问题的机制。当多个线程访问共享资源时，可能导致数据不一致。Java提供了synchronized关键字和Lock接口来实现线程同步。

## 知识点范围
- 线程安全问题的产生原因
- synchronized关键字：同步方法和同步代码块
- 对象锁和类锁
- wait()和notify()方法
- 死锁的概念和避免
- Lock接口和ReentrantLock
- volatile关键字的作用

## 前置知识
必须先理解线程的概念与创建（java-thread-01）

## 常见误区
- 不理解synchronized的锁对象
- 死锁的产生条件
- 使用sleep()而不是wait()进行线程等待
- 不释放锁导致其他线程阻塞',
'线程同步,synchronized,Lock,死锁,volatile', 39),

(@course_id, NULL, '线程池', 'java-thread-03', 'L3',
'## 核心概念
线程池是管理线程的容器，可以复用线程、控制并发数量、管理线程生命周期。使用线程池可以避免频繁创建和销毁线程的开销，提高系统性能。

## 知识点范围
- 线程池的概念和优势
- ThreadPoolExecutor类
- 线程池的核心参数：核心线程数、最大线程数、队列、拒绝策略
- 常见线程池类型：FixedThreadPool、CachedThreadPool、SingleThreadPool
- ExecutorService接口的使用
- submit()和execute()的区别
- 线程池的关闭：shutdown()和shutdownNow()

## 前置知识
必须先理解线程同步与锁（java-thread-02）

## 常见误区
- 不理解线程池的参数配置
- 使用无界队列导致内存溢出
- 不关闭线程池导致资源泄漏
- 以为线程池中的线程会自动回收',
'线程池,ThreadPoolExecutor,ExecutorService,并发', 40);

-- =====================================================
-- 插入知识点依赖关系
-- =====================================================

-- Ch01 Java入门的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-basic-01'), (SELECT id FROM knowledge_point WHERE code = 'java-basic-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-02'), (SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-basic-04'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-basic-05'), 'requires');

-- Ch02 面向对象基础的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-basic-05'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-02'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-04'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-05'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-02'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-06'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-03'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-06'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-06'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-07'), 'requires');

-- Ch03 面向对象进阶的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-oop-07'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-08'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-07'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-09'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-09'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-10'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-11'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-oop-12'), 'requires');

-- Ch04 常用API的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-api-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-api-01'), (SELECT id FROM knowledge_point WHERE code = 'java-api-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-api-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-basic-03'), (SELECT id FROM knowledge_point WHERE code = 'java-api-04'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-api-05'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-api-05'), (SELECT id FROM knowledge_point WHERE code = 'java-api-06'), 'requires');

-- Ch05 异常处理的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-oop-06'), (SELECT id FROM knowledge_point WHERE code = 'java-exception-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-exception-01'), (SELECT id FROM knowledge_point WHERE code = 'java-exception-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-exception-02'), (SELECT id FROM knowledge_point WHERE code = 'java-exception-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-exception-03'), (SELECT id FROM knowledge_point WHERE code = 'java-exception-04'), 'requires');

-- Ch06 集合框架的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-oop-07'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-collection-01'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-collection-02'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-collection-01'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-04'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-collection-04'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-05'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-collection-02'), (SELECT id FROM knowledge_point WHERE code = 'java-collection-06'), 'requires');

-- Ch07 IO流的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-oop-01'), (SELECT id FROM knowledge_point WHERE code = 'java-io-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-io-01'), (SELECT id FROM knowledge_point WHERE code = 'java-io-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-io-02'), (SELECT id FROM knowledge_point WHERE code = 'java-io-03'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-io-02'), (SELECT id FROM knowledge_point WHERE code = 'java-io-04'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-io-03'), (SELECT id FROM knowledge_point WHERE code = 'java-io-04'), 'requires');

-- Ch08 多线程的依赖关系
INSERT INTO `knowledge_edge` (`from_kp_id`, `to_kp_id`, `edge_type`) VALUES
((SELECT id FROM knowledge_point WHERE code = 'java-oop-07'), (SELECT id FROM knowledge_point WHERE code = 'java-thread-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-api-05'), (SELECT id FROM knowledge_point WHERE code = 'java-thread-01'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-thread-01'), (SELECT id FROM knowledge_point WHERE code = 'java-thread-02'), 'requires'),
((SELECT id FROM knowledge_point WHERE code = 'java-thread-02'), (SELECT id FROM knowledge_point WHERE code = 'java-thread-03'), 'requires');

-- =====================================================
-- 为questions表的现有数据添加知识点关联（示例）
-- =====================================================

-- 更新现有题目，关联到对应的知识点
UPDATE `questions` SET `knowledge_point_id` = (SELECT id FROM knowledge_point WHERE code = 'java-basic-03') WHERE `id` = 1;
UPDATE `questions` SET `knowledge_point_id` = (SELECT id FROM knowledge_point WHERE code = 'java-oop-01') WHERE `id` = 2;
UPDATE `questions` SET `knowledge_point_id` = (SELECT id FROM knowledge_point WHERE code = 'java-oop-09') WHERE `id` = 3;
UPDATE `questions` SET `knowledge_point_id` = (SELECT id FROM knowledge_point WHERE code = 'java-collection-01') WHERE `id` = 4;
