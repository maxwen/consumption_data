package com.maxwen.consumption_data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import androidx.datastore.preferences.core.Preferences
import androidx.security.crypto.EncryptedFile
import androidx.security.crypto.MasterKeys
import io.github.osipxd.security.crypto.createEncrypted

fun createDataStore(context: Context): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createEncrypted {
        EncryptedFile.Builder(
            context.filesDir.resolve(DATA_STORE_FILE_NAME),
            context,
            MasterKeys.getOrCreate(MasterKeys.AES256_GCM_SPEC),
            EncryptedFile.FileEncryptionScheme.AES256_GCM_HKDF_4KB
        ).build()
    }
}
