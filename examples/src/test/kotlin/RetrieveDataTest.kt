
import com.mongodb.client.model.Accumulators
import com.mongodb.client.model.Aggregates
import com.mongodb.client.model.Filters
import com.mongodb.client.model.Sorts
import com.mongodb.kotlin.client.coroutine.MongoClient
import io.github.cdimascio.dotenv.dotenv
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.runBlocking
import org.bson.codecs.pojo.annotations.BsonId
import org.junit.jupiter.api.AfterAll
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeAll
import org.junit.jupiter.api.TestInstance
import java.util.*
import kotlin.test.*

// :snippet-start: retrieve-data-model
data class PaintOrder(
    @BsonId val id: Int,
    val qty: Int,
    val color: String
)
// :snippet-end:

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
internal class RetrieveDataTest {

    companion object {
        private val dotenv = dotenv()
        private val client = MongoClient.create(dotenv["MONGODB_CONNECTION_URI"])
        private val database = client.getDatabase("paint_store")
        val collection = database.getCollection<PaintOrder>("paint_order")

        @BeforeAll
        @JvmStatic
        fun beforeAll() {
            runBlocking {
                val paintOrders = listOf(
                    PaintOrder(1, 10, "purple"),
                    PaintOrder(2, 8, "green"),
                    PaintOrder(3, 4, "purple"),
                    PaintOrder(4, 11, "green")
                )
                collection.insertMany(paintOrders)
            }
        }

        @AfterAll
        @JvmStatic
        fun afterAll() {
            runBlocking {
                collection.drop()
                client.close()
            }
        }
    }

    @Test
    fun basicFindTest() = runBlocking {
        // :snippet-start: basic-find
        val filter = Filters.and(Filters.gt("qty", 3), Filters.lt("qty", 9))
        val resultsFlow = collection.find(filter)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        val expected = listOf(
            PaintOrder(2, 8, "green"),
            PaintOrder(3, 4, "purple"),
        )
        assertEquals(expected, resultsFlow.toList())
    }

    @Test
    fun aggregationFindTest() = runBlocking {
        // :snippet-start: aggregation-find
        data class AggregationResult(@BsonId val id: String, val qty: Int)

        val filter = Filters.empty()
        val pipeline = listOf(
            Aggregates.match(filter),
            Aggregates.group(
                "\$color",
                Accumulators.sum("qty", "\$qty")
            ),
            Aggregates.sort(Sorts.descending("qty"))
        )
        val resultsFlow = collection.aggregate<AggregationResult>(pipeline)
        resultsFlow.collect { println(it) }
        // :snippet-end:
        val expected = listOf(
            AggregationResult("green", 19),
            AggregationResult("purple", 14)
        )
        assertEquals(expected,  resultsFlow.toList())
    }
}
