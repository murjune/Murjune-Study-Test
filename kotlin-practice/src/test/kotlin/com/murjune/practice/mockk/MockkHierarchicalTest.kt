package com.murjune.practice.mockk

import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.junit5.MockKExtension
import io.mockk.mockk
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith

@ExtendWith(MockKExtension::class)
class MockkHierarchicalTest {
    data class AddressBook(
        val contacts: List<Contact>,
    )

    data class Contact(
        val name: String,
        val telephone: String,
        val address: Address,
    )

    data class Address(
        val city: String,
        val zip: String,
    )

    val addressBook: AddressBook =
        mockk<AddressBook> {
            every { contacts } returns
                listOf(
                    mockk {
                        every { name } returns "John"
                        every { telephone } returns "123-456-789"
                        every { address.city } returns "New-York"
                        every { address.zip } returns "123-45"
                    },
                    mockk {
                        every { name } returns "Alex"
                        every { telephone } returns "789-456-123"
                        every { address } returns
                            mockk {
                                every { city } returns "Wroclaw"
                                every { zip } returns "543-21"
                            }
                    },
                )
        }

    @Test
    fun `test`() {
        addressBook.contacts[0].address.city shouldBe "New-York"
        addressBook.contacts[0].telephone shouldBe "123-456-789"
        addressBook.contacts[1].address.city shouldBe "Wroclaw"
        addressBook.contacts[1].address.zip shouldBe "543-21"
    }
}
