import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.maxwen.consumption_data.DATA_STORE_FILE_NAME
import com.maxwen.consumption_data.createDataStore

fun createDataStore(): DataStore<Preferences> {
    return createDataStore {
        DATA_STORE_FILE_NAME
    }
}
