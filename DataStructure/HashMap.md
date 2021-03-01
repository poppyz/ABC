## HashTable
实际上是默认长度为16宽度的数组，数组的元素是链表。链表的增加方式是头部追加(新加入的下次访问可能性更高(并不适合所有的情景))

```python

    /**
     * The default initial capacity - MUST be a power of two.
     */
    static final int DEFAULT_INITIAL_CAPACITY = 1 << 4; // aka 16

    /**
     * The maximum capacity, used if a higher value is implicitly specified
     * by either of the constructors with arguments.
     * MUST be a power of two <= 1<<30.
     */
    static final int MAXIMUM_CAPACITY = 1 << 30;

    /**
     * The load factor used when none specified in constructor.
     */
    static final float DEFAULT_LOAD_FACTOR = 0.75f;

    /**
     * The bin count threshold for using a tree rather than list for a
     * bin.  Bins are converted to trees when adding an element to a
     * bin with at least this many nodes. The value must be greater
     * than 2 and should be at least 8 to mesh with assumptions in
     * tree removal about conversion back to plain bins upon
     * shrinkage.
     */
    static final int TREEIFY_THRESHOLD = 8;

    /**
     * The bin count threshold for untreeifying a (split) bin during a
     * resize operation. Should be less than TREEIFY_THRESHOLD, and at
     * most 6 to mesh with shrinkage detection under removal.
     */
    static final int UNTREEIFY_THRESHOLD = 6;

    /**
     * The smallest table capacity for which bins may be treeified.
     * (Otherwise the table is resized if too many nodes in a bin.)
     * Should be at least 4 * TREEIFY_THRESHOLD to avoid conflicts
     * between resizing and treeification thresholds.
     */
    static final int MIN_TREEIFY_CAPACITY = 64;

```

- 树化阈值
添加元素时 桶内元素数量大于 8 自动转为 红黑树
- 链表阈值
删减元素时 桶内元素数量小于 6 自动转为链表
- static final int MIN_TREEIFY_CAPACITY = 64;

-------
HashMap 与 HashTable 的区别
| -           |HashMap                    |HashTable    |
| :---        | :----:                    | :----:      |
|继承的父类    |AbstractMap                |Dictionary   |
|null         |允许K==null \|\| V == null |Not Allow    |
|Thread soft  |N                          |Y            |
|性能         |较快（因为操作不加锁）        |较慢（synchronized）|
|初始容量     |16                         |11|
|扩容方式     |2的幂次方 最大pow(2,32)    |2n+1最大pow(2,32)|
|load factor  |0.75                     |0.75             |
