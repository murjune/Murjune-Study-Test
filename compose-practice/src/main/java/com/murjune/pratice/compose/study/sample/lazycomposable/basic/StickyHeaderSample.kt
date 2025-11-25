package com.murjune.pratice.compose.study.sample.lazycomposable.basic

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

/**
 * 기본 StickyHeader 예제
 * - 연락처 앱처럼 이름 첫 글자로 그룹핑
 * - 스크롤 시 해당 섹션의 헤더가 상단에 고정됨
 * - 다음 헤더가 올라오면 이전 헤더를 밀어내며 교체
 */
@Preview(showBackground = true)
@Composable
private fun BasicStickyHeaderSample() {
    val contacts by remember {
        mutableStateOf(Contact.fakeList(50))
    }

    // 이름 첫 글자로 그룹핑
    val groupedContacts = remember(contacts) { contacts.groupBy { it.name.first().uppercaseChar() } }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        groupedContacts.forEach { (initial, contactsForInitial) ->
            // stickyHeader: 스크롤 시 상단에 고정되는 헤더
            stickyHeader {
                Text(
                    text = initial.toString(),
                    style = MaterialTheme.typography.headlineMedium,
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color.LightGray)
                        .padding(vertical = 8.dp, horizontal = 16.dp),
                )
            }

            items(
                items = contactsForInitial,
                key = { it.id },
            ) { contact ->
                ContactRow(
                    modifier = Modifier.fillMaxWidth(),
                    contact = contact,
                )
            }
        }
    }
}

/**
 * 카테고리별 StickyHeader 예제
 * - 상품 리스트를 카테고리별로 그룹핑
 * - 각 카테고리 헤더가 스크롤 시 고정됨
 */
@Preview(showBackground = true)
@Composable
private fun CategoryStickyHeaderSample() {
    val products by remember {
        mutableStateOf(Product.fakeList(30))
    }

    val groupedProducts = products.groupBy { it.category }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        groupedProducts.forEach { (category, productsForCategory) ->
            stickyHeader {
                CategoryHeader(category = category)
            }

            items(
                items = productsForCategory,
                key = { it.id },
            ) { product ->
                ProductRow(
                    modifier = Modifier.fillMaxWidth(),
                    product = product,
                )
            }
        }
    }
}

@Composable
private fun ContactRow(
    modifier: Modifier,
    contact: Contact,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = contact.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = contact.phone,
                style = MaterialTheme.typography.bodyMedium,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun ProductRow(
    modifier: Modifier,
    product: Product,
) {
    Card(modifier = modifier) {
        Column(
            modifier = Modifier.padding(12.dp),
        ) {
            Text(
                text = product.name,
                style = MaterialTheme.typography.titleMedium,
            )
            Text(
                text = "카테고리: ${product.category}",
                style = MaterialTheme.typography.bodySmall,
                color = Color.Gray,
            )
        }
    }
}

@Composable
private fun CategoryHeader(category: String) {
    Text(
        text = category,
        style = MaterialTheme.typography.headlineSmall,
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFFFF3E0))
            .padding(vertical = 12.dp, horizontal = 16.dp),
    )
}

private data class Contact(
    val id: Int,
    val name: String,
    val phone: String,
) {
    companion object {
        fun fakeList(size: Int): List<Contact> {
            val firstNames = listOf(
                "Alice", "Bob", "Charlie", "David", "Emma",
                "Frank", "Grace", "Henry", "Iris", "Jack",
                "Kate", "Leo", "Mike", "Nina", "Oscar",
                "Paul", "Quinn", "Rachel", "Sam", "Tom",
            )
            val lastNames = listOf("Kim", "Lee", "Park", "Choi", "Jung")

            return List(size) { index ->
                val firstName = firstNames[index % firstNames.size]
                val lastName = lastNames[index % lastNames.size]
                Contact(
                    id = index,
                    name = "$firstName $lastName",
                    phone = "010-${(1000..9999).random()}-${(1000..9999).random()}",
                )
            }.sortedBy { it.name }
        }
    }
}

private data class Product(
    val id: Int,
    val name: String,
    val category: String,
) {
    companion object {
        fun fakeList(size: Int): List<Product> {
            val categories = listOf("과일", "채소", "육류", "수산물", "유제품")
            val productNames = mapOf(
                "과일" to listOf("사과", "배", "포도", "딸기", "바나나", "오렌지"),
                "채소" to listOf("양배추", "당근", "브로콜리", "시금치", "양파", "감자"),
                "육류" to listOf("소고기", "돼지고기", "닭고기", "양고기"),
                "수산물" to listOf("고등어", "연어", "참치", "새우", "오징어"),
                "유제품" to listOf("우유", "치즈", "요거트", "버터"),
            )

            return List(size) { index ->
                val category = categories[index % categories.size]
                val names = productNames[category] ?: emptyList()
                val productName = names[index % names.size]

                Product(
                    id = index,
                    name = "$productName ${index / categories.size + 1}",
                    category = category,
                )
            }.sortedBy { it.category }
        }
    }
}