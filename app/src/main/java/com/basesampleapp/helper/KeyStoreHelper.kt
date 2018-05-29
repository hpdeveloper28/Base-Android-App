package com.basesampleapp.helper

import android.annotation.SuppressLint
import android.content.Context
import android.security.KeyPairGeneratorSpec
import android.util.Base64
import android.util.Log
import java.math.BigInteger
import java.nio.charset.Charset
import java.security.KeyPairGenerator
import java.security.KeyStore
import java.security.PrivateKey
import java.security.PublicKey
import java.security.spec.AlgorithmParameterSpec
import java.util.*
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import javax.security.auth.x500.X500Principal

/**
 * Created by Hiren
 */
class KeyStoreHelper(private val context: Context,
                     private val sharedPrefsHelper: SharedPrefsHelper) {


    private val parentKey = UUID.randomUUID().toString()

    companion object Constants {
        private const val ANDROID_KEY_STORE = "AndroidKeyStore"
        private const val KEY_ALGORITHM_RSA = "RSA"
        private const val KEY_ALGORITHM_AES = "AES"
        private const val TRANSFORMATION = "RSA/ECB/PKCS1Padding"
    }

    /**
     * Signs the data using the key pair stored in the Android Key Store.
     * Sends to SharedPref to store the signed data
     *
     * @param inputStr Input data
     * @param alias for KeyStore
     *
     */
    fun storeData(spKey: String, spValue: String) {

        val encryptedKey = getEncryptedKey(spKey)

        val entry = getKeyStoreEntry(true, encryptedKey)
        if (entry == null) {
            Log.e("KeyStore", "Keystore generation failed.")
            return
        }

        val key = entry as KeyStore.PrivateKeyEntry
        val encryptedData = encryptData(encryptedKey, spValue, key.certificate.publicKey)
        sharedPrefsHelper[encryptedKey] = encryptedData
    }

    fun retrieveData(spKey: String): String? {

        val encryptedKey = getEncryptedKey(spKey)

        val entry = getKeyStoreEntry(false, encryptedKey)
        if (entry == null) {
            Log.e("KeyStore", "Keystore generation failed.")
            return null
        }
        val key = entry as KeyStore.PrivateKeyEntry
        return decryptData(encryptedKey, key.privateKey)
    }

    private fun getEncryptedKey(spKey: String): String {
        val charset = Charsets.UTF_8
        val byteArray = spKey.toByteArray(charset)
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    private val keyStore: KeyStore by lazy {
        KeyStore.getInstance(ANDROID_KEY_STORE)
    }

    private fun generateKeyAes(alias: String): SecretKey {
        val generator = KeyGenerator.getInstance(KEY_ALGORITHM_AES)
        generator.init(128) // The AES key size in number of bits
        val secret = generator.generateKey()
        sharedPrefsHelper["aes!" + alias] = Base64.encodeToString(secret.encoded, Base64.DEFAULT)
        return secret
    }

    private fun generateKeyRsa(alias: String) {
        val keyPairGenerator = KeyPairGenerator.getInstance(KEY_ALGORITHM_RSA, ANDROID_KEY_STORE)
        keyPairGenerator.initialize(getParameterSpec(alias))
        keyPairGenerator.generateKeyPair()
    }

    private fun getParameterSpec(alias: String): AlgorithmParameterSpec {
        // Create a start and end time, for the validity range of the key pair
        // that's about to be
        // generated.
        val start = GregorianCalendar()
        val end = GregorianCalendar()
        end.add(Calendar.YEAR, 5)

        // The KeyPairGeneratorSpec object is how parameters for your key pair
        // are passed
        // to the KeyPairGenerator. For a fun home game, count how many classes
        // in this sample
        // start with the phrase "KeyPair".
        return KeyPairGeneratorSpec.Builder(context)
                // You'll use the alias later to retrieve the key. It's a key
                // for the key!
                .setAlias(alias)
                // The subject used for the self-signed certificate of the
                // generated pair
                .setSubject(X500Principal("CN=" + alias))
                // The serial number used for the self-signed certificate of the
                // generated pair.
                .setSerialNumber(BigInteger.valueOf(1338))
                // Date range of validity for the generated pair.
                .setStartDate(start.time)
                .setEndDate(end.time)
                .build()
    }

    private fun getKeyStoreEntry(shouldGenerateKey: Boolean, alias: String): KeyStore.Entry? {
        keyStore.load(null)

        // Load the key pair from the Android Key Store
        var entry = keyStore.getEntry(alias, null)

        // If the entry is null, keys were never stored under this alias.
        if (entry == null) {
            Log.w("KeyStore", "No key found under alias: " + alias)
            if (shouldGenerateKey) {
                Log.i("KeyStore", "generating key")
                generateKeyRsa(alias)
                entry = keyStore.getEntry(alias, null)
            }
        }
        return entry
    }

    /**
     * Encrypt Data with a KeyStore SecretKey
     *
     */
    private fun encryptData(alias: String, inputStr: String, publicKey: PublicKey): String {
        val data = inputStr.toByteArray(Charset.forName("UTF-8"))
        // AES Encryption
        val aesData = encryptAes(data, alias)
        // RSA Encryption with keystore
        val encryptedInput = encryptRsa(aesData, publicKey)
        // String encoding for easy storage
        return Base64.encodeToString(encryptedInput, Base64.DEFAULT)
    }

    @SuppressLint("GetInstance")
    private fun encryptAes(inputByteArray: ByteArray, alias: String): ByteArray {
        val cipherAes = Cipher.getInstance(KEY_ALGORITHM_AES)
        cipherAes.init(Cipher.ENCRYPT_MODE, generateKeyAes(alias))
        return cipherAes.doFinal(inputByteArray)
    }

    private fun encryptRsa(inputByteArray: ByteArray, publicKey: PublicKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.PUBLIC_KEY, publicKey)
        return cipher.doFinal(inputByteArray)
    }

    /**
     * Decrypt Data with a KeyStore SecretKey
     */
    private fun decryptData(alias: String, privateKey: PrivateKey): String? {
        val encryptedData = sharedPrefsHelper.get(alias, "")
        val secretData = sharedPrefsHelper.get("aes!" + alias, "")
        if (encryptedData == "" || secretData == "") {
            Log.w("Prefs", "No data found under alias: " + alias)
            return null
        }
        val decodedData = Base64.decode(encryptedData, Base64.DEFAULT)
        val decryptedData = decryptRsa(decodedData, privateKey)
        val decodedSecret = Base64.decode(secretData, Base64.DEFAULT)
        val secretKey = SecretKeySpec(decodedSecret, 0, decodedSecret.size, KEY_ALGORITHM_AES)
        val finalData = decryptAes(decryptedData, secretKey)
        return finalData.toString(Charset.forName("UTF-8"))
    }

    @SuppressLint("GetInstance")
    private fun decryptAes(decryptedKey: ByteArray, secret: SecretKey): ByteArray {
        val cipherAes = Cipher.getInstance(KEY_ALGORITHM_AES)
        cipherAes.init(Cipher.DECRYPT_MODE, secret)
        return cipherAes.doFinal(decryptedKey)
    }

    private fun decryptRsa(inputByteArray: ByteArray, secretKey: PrivateKey): ByteArray {
        val cipher = Cipher.getInstance(TRANSFORMATION)
        cipher.init(Cipher.PRIVATE_KEY, secretKey)
        return cipher.doFinal(inputByteArray)
    }
}