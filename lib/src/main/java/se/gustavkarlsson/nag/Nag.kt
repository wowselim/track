package se.gustavkarlsson.nag

import android.content.Context
import se.gustavkarlsson.nag.sqlite.Helper
import se.gustavkarlsson.nag.sqlite.SqliteNag

interface Nag {
	fun getSingle(key: String): Record?
	fun setSingle(key: String, value: String = "")
	fun query(
		key: String,
		order: Order = Order.OldestFirst,
		filters: Filters.() -> Unit = {}
	): CloseableSequence<Record>

	fun add(key: String, value: String = "")
	fun remove(id: Long)
	fun remove(key: String, filters: Filters.() -> Unit = {})
	fun clearDatabase()

	companion object : Nag {
		private var initializedDelegate: Nag? = null
		private inline val delegate: Nag
			get() = requireNotNull(initializedDelegate) { "Nag is not yet initialized. Run initialize() first" }

		fun initialize(context: Context) {
			initializedDelegate = SqliteNag(Helper(context))
		}

		override fun getSingle(key: String) =
			delegate.getSingle(key)

		override fun setSingle(key: String, value: String) =
			delegate.setSingle(key, value)

		override fun query(key: String, order: Order, filters: Filters.() -> Unit) =
			delegate.query(key, order, filters)

		override fun add(key: String, value: String) =
			delegate.add(key, value)

		override fun remove(id: Long) =
			delegate.remove(id)

		override fun remove(key: String, filters: Filters.() -> Unit) =
			delegate.remove(key, filters)

		override fun clearDatabase() =
			delegate.clearDatabase()
	}
}
