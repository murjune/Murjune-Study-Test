# Document 와 Element, NodeList.. 은 Thread-Safe 하지 않다.

DOM API는 본질적으로 스레드로부터 안전하지 않으므로 여러 스레드에서 동시에 사용하면 문제가 발생한다..
- DocumentBuilder 은 도 thread-safe 하지 않음..
- org.w3c.dom.* 도 thread-safe 하지 않음..
```
Exception in thread "main" java.lang.IndexOutOfBoundsException: Index 4 out of bounds for length 0
	at java.base/jdk.internal.util.Preconditions.outOfBounds(Preconditions.java:64)
	at java.base/jdk.internal.util.Preconditions.outOfBoundsCheckIndex(Preconditions.java:70)
	at java.base/jdk.internal.util.Preconditions.checkIndex(Preconditions.java:266)
	at java.base/java.util.Objects.checkIndex(Objects.java:361)
	at java.base/java.util.ArrayList.get(ArrayList.java:427)
```

```kotlin
// RssService.kt
private suspend fun parseRSS(items: NodeList): List<BlogPost> = coroutineScope {
    val posts = mutableListOf<BlogPost>()
    repeat(items.length) { index ->
        val element = items.item(index) as Element
        val title = element.getElementsByTagName("title")
            .item(0).textContent
        val link = element.getElementsByTagName("link").item(0).textContent
        val pubDate = element.getElementsByTagName("pubDate").item(0).textContent.let {
            parseSystemDateTime(it)
        }
        val description = element.getElementsByTagName("description").item(0).textContent
        mutex.withLock { // mutex 를 사용하여 mutableList 에 레이스 컨디션을 방지하고자함
            posts.add(BlogPost(title, link, pubDate, description))
        }
    }
    posts
}
```

- reference
https://stackoverflow.com/questions/12455602/is-documentbuilder-thread-safe
https://itpfdoc.hitachi.co.jp/manuals/3020/30203Y2210e/EY220138.HTM
