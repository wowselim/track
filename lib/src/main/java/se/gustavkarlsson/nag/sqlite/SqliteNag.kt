package se.gustavkarlsson.nag.sqlite

import android.database.Cursor
import se.gustavkarlsson.nag.*

internal class SqliteNag(
	private val helper: Helper,
	private val appVersion: Long,
	private val getTimestamp: () -> Long = System::currentTimeMillis,
	private val tryGetRecordAndClose: Cursor.() -> Record? =
		{ tryGetRecordAndClose(Cursor::readExistingRecord) },
	private val toSelection: Filter.() -> Selection = Filter::toSelection
) : Nag {
	override fun getSingle(key: String): Record? {
		val selections =
			createKeySelection(key) + Selection(Table.COLUMN_SINGLETON, Operator.Equals, true)
		val cursor = helper.query(selections, limit = 1)
		return cursor.tryGetRecordAndClose()
	}

	override fun setSingle(key: String, value: String) {
		val selections =
			createKeySelection(key) + Selection(Table.COLUMN_SINGLETON, Operator.Equals, true)
		helper.upsert(selections, createRow(key, value, true))
	}

	override fun query(
		key: String,
		order: Order,
		filtersConfigBlock: FiltersConfig.() -> Unit
	): CloseableSequence<Record> {
		val selections = createKeySelection(key) + filtersConfigBlock.toSelections()
		val orderBy = when (order) {
			Order.OldestFirst -> OrderBy.Ascending(Table.COLUMN_TIMESTAMP)
			Order.NewestFirst -> OrderBy.Descending(Table.COLUMN_TIMESTAMP)
		}
		val cursor = helper.query(selections, orderBy)
		return CloseableRecordCursorSequence(cursor)
	}

	override fun add(key: String, value: String) {
		helper.insert(createRow(key, value, false))
	}

	private fun createRow(key: String, value: String, singleton: Boolean): Map<String, Any> =
		mapOf(
			Table.COLUMN_SINGLETON to singleton,
			Table.COLUMN_KEY to key,
			Table.COLUMN_TIMESTAMP to getTimestamp(),
			Table.COLUMN_APP_VERSION to appVersion,
			Table.COLUMN_VALUE to value
		)

	override fun remove(id: Long) {
		helper.delete(listOf(Selection(Table.COLUMN_ID, Operator.Equals, id)))
	}

	override fun remove(key: String, filtersConfigBlock: FiltersConfig.() -> Unit) {
		val selections = createKeySelection(key) + filtersConfigBlock.toSelections()
		helper.delete(selections)
	}

	override fun deleteDatabase() = helper.deleteDatabase()

	private fun createKeySelection(key: String) =
		listOf(Selection(Table.COLUMN_KEY, Operator.Equals, key))

	private fun (FiltersConfig.() -> Unit).toSelections(): List<Selection> =
		FiltersConfig().apply(this).filters.map(toSelection)
}
